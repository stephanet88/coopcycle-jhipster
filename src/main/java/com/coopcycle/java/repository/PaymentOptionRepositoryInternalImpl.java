package com.coopcycle.java.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.coopcycle.java.domain.PaymentOption;
import com.coopcycle.java.repository.rowmapper.CartRowMapper;
import com.coopcycle.java.repository.rowmapper.PaymentOptionRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the PaymentOption entity.
 */
@SuppressWarnings("unused")
class PaymentOptionRepositoryInternalImpl implements PaymentOptionRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CartRowMapper cartMapper;
    private final PaymentOptionRowMapper paymentoptionMapper;

    private static final Table entityTable = Table.aliased("payment_option", EntityManager.ENTITY_ALIAS);
    private static final Table cartTable = Table.aliased("cart", "cart");

    public PaymentOptionRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CartRowMapper cartMapper,
        PaymentOptionRowMapper paymentoptionMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.cartMapper = cartMapper;
        this.paymentoptionMapper = paymentoptionMapper;
    }

    @Override
    public Flux<PaymentOption> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<PaymentOption> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<PaymentOption> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = PaymentOptionSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CartSqlHelper.getColumns(cartTable, "cart"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(cartTable)
            .on(Column.create("cart_id", entityTable))
            .equals(Column.create("id", cartTable));

        String select = entityManager.createSelect(selectFrom, PaymentOption.class, pageable, criteria);
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
    public Flux<PaymentOption> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<PaymentOption> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private PaymentOption process(Row row, RowMetadata metadata) {
        PaymentOption entity = paymentoptionMapper.apply(row, "e");
        entity.setCart(cartMapper.apply(row, "cart"));
        return entity;
    }

    @Override
    public <S extends PaymentOption> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends PaymentOption> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update PaymentOption with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(PaymentOption entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class PaymentOptionSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("type", table, columnPrefix + "_type"));

        columns.add(Column.aliased("cart_id", table, columnPrefix + "_cart_id"));
        return columns;
    }
}
