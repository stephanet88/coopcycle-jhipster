package com.coopcycle.java.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.coopcycle.java.domain.Product;
import com.coopcycle.java.repository.rowmapper.InstitutionRowMapper;
import com.coopcycle.java.repository.rowmapper.ProductRowMapper;
import com.coopcycle.java.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Product entity.
 */
@SuppressWarnings("unused")
class ProductRepositoryInternalImpl implements ProductRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final InstitutionRowMapper institutionMapper;
    private final ProductRowMapper productMapper;

    private static final Table entityTable = Table.aliased("product", EntityManager.ENTITY_ALIAS);
    private static final Table institutionTable = Table.aliased("institution", "institution");

    public ProductRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        InstitutionRowMapper institutionMapper,
        ProductRowMapper productMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.institutionMapper = institutionMapper;
        this.productMapper = productMapper;
    }

    @Override
    public Flux<Product> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Product> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Product> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = ProductSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(InstitutionSqlHelper.getColumns(institutionTable, "institution"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(institutionTable)
            .on(Column.create("institution_id", entityTable))
            .equals(Column.create("id", institutionTable));

        String select = entityManager.createSelect(selectFrom, Product.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(
                crit ->
                    new StringBuilder(select)
                        .append(" ")
                        .append("WHERE")
                        .append(" ")
                        .append(alias)
                        .append(".")
                        .append(crit.toString())
                        .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<Product> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Product> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Product process(Row row, RowMetadata metadata) {
        Product entity = productMapper.apply(row, "e");
        entity.setInstitution(institutionMapper.apply(row, "institution"));
        return entity;
    }

    @Override
    public <S extends Product> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Product> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update Product with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(Product entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class ProductSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("price", table, columnPrefix + "_price"));
        columns.add(Column.aliased("currency", table, columnPrefix + "_currency"));
        columns.add(Column.aliased("image", table, columnPrefix + "_image"));
        columns.add(Column.aliased("image_content_type", table, columnPrefix + "_image_content_type"));

        columns.add(Column.aliased("institution_id", table, columnPrefix + "_institution_id"));
        return columns;
    }
}
