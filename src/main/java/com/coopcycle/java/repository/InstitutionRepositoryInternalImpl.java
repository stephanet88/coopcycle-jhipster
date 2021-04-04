package com.coopcycle.java.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.coopcycle.java.domain.Cart;
import com.coopcycle.java.domain.Institution;
import com.coopcycle.java.repository.rowmapper.CooperativeRowMapper;
import com.coopcycle.java.repository.rowmapper.InstitutionRowMapper;
import com.coopcycle.java.repository.rowmapper.UserAccountRowMapper;
import com.coopcycle.java.service.EntityManager;
import com.coopcycle.java.service.EntityManager.LinkTable;
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
 * Spring Data SQL reactive custom repository implementation for the Institution entity.
 */
@SuppressWarnings("unused")
class InstitutionRepositoryInternalImpl implements InstitutionRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserAccountRowMapper useraccountMapper;
    private final CooperativeRowMapper cooperativeMapper;
    private final InstitutionRowMapper institutionMapper;

    private static final Table entityTable = Table.aliased("institution", EntityManager.ENTITY_ALIAS);
    private static final Table userAccountTable = Table.aliased("user_account", "userAccount");
    private static final Table cooperativeTable = Table.aliased("cooperative", "cooperative");

    private static final EntityManager.LinkTable cartLink = new LinkTable("rel_institution__cart", "institution_id", "cart_id");

    public InstitutionRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserAccountRowMapper useraccountMapper,
        CooperativeRowMapper cooperativeMapper,
        InstitutionRowMapper institutionMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.useraccountMapper = useraccountMapper;
        this.cooperativeMapper = cooperativeMapper;
        this.institutionMapper = institutionMapper;
    }

    @Override
    public Flux<Institution> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Institution> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Institution> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = InstitutionSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserAccountSqlHelper.getColumns(userAccountTable, "userAccount"));
        columns.addAll(CooperativeSqlHelper.getColumns(cooperativeTable, "cooperative"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userAccountTable)
            .on(Column.create("user_account_id", entityTable))
            .equals(Column.create("id", userAccountTable))
            .leftOuterJoin(cooperativeTable)
            .on(Column.create("cooperative_id", entityTable))
            .equals(Column.create("id", cooperativeTable));

        String select = entityManager.createSelect(selectFrom, Institution.class, pageable, criteria);
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
    public Flux<Institution> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Institution> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    @Override
    public Mono<Institution> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Institution> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Institution> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Institution process(Row row, RowMetadata metadata) {
        Institution entity = institutionMapper.apply(row, "e");
        entity.setUserAccount(useraccountMapper.apply(row, "userAccount"));
        entity.setCooperative(cooperativeMapper.apply(row, "cooperative"));
        return entity;
    }

    @Override
    public <S extends Institution> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Institution> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity).flatMap(savedEntity -> updateRelations(savedEntity));
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update Institution with id = " + entity.getId());
                        }
                        return entity;
                    }
                )
                .then(updateRelations(entity));
        }
    }

    @Override
    public Mono<Integer> update(Institution entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId)
            .then(r2dbcEntityTemplate.delete(Institution.class).matching(query(where("id").is(entityId))).all().then());
    }

    protected <S extends Institution> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager.updateLinkTable(cartLink, entity.getId(), entity.getCarts().stream().map(Cart::getId)).then();
        return result.thenReturn(entity);
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(cartLink, entityId);
    }
}

class InstitutionSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("name", table, columnPrefix + "_name"));
        columns.add(Column.aliased("type", table, columnPrefix + "_type"));

        columns.add(Column.aliased("user_account_id", table, columnPrefix + "_user_account_id"));
        columns.add(Column.aliased("cooperative_id", table, columnPrefix + "_cooperative_id"));
        return columns;
    }
}
