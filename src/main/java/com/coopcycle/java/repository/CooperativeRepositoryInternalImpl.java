package com.coopcycle.java.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.coopcycle.java.domain.Cooperative;
import com.coopcycle.java.repository.rowmapper.CooperativeRowMapper;
import com.coopcycle.java.repository.rowmapper.UserAccountRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the Cooperative entity.
 */
@SuppressWarnings("unused")
class CooperativeRepositoryInternalImpl implements CooperativeRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserAccountRowMapper useraccountMapper;
    private final CooperativeRowMapper cooperativeMapper;

    private static final Table entityTable = Table.aliased("cooperative", EntityManager.ENTITY_ALIAS);
    private static final Table userAccountTable = Table.aliased("user_account", "userAccount");

    public CooperativeRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserAccountRowMapper useraccountMapper,
        CooperativeRowMapper cooperativeMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.useraccountMapper = useraccountMapper;
        this.cooperativeMapper = cooperativeMapper;
    }

    @Override
    public Flux<Cooperative> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Cooperative> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Cooperative> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = CooperativeSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserAccountSqlHelper.getColumns(userAccountTable, "userAccount"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userAccountTable)
            .on(Column.create("user_account_id", entityTable))
            .equals(Column.create("id", userAccountTable));

        String select = entityManager.createSelect(selectFrom, Cooperative.class, pageable, criteria);
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
    public Flux<Cooperative> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Cooperative> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Cooperative process(Row row, RowMetadata metadata) {
        Cooperative entity = cooperativeMapper.apply(row, "e");
        entity.setUserAccount(useraccountMapper.apply(row, "userAccount"));
        return entity;
    }

    @Override
    public <S extends Cooperative> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Cooperative> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update Cooperative with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(Cooperative entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class CooperativeSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("location", table, columnPrefix + "_location"));

        columns.add(Column.aliased("user_account_id", table, columnPrefix + "_user_account_id"));
        return columns;
    }
}
