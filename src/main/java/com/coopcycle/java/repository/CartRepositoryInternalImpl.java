package com.coopcycle.java.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.coopcycle.java.domain.Cart;
import com.coopcycle.java.repository.rowmapper.CartRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the Cart entity.
 */
@SuppressWarnings("unused")
class CartRepositoryInternalImpl implements CartRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserAccountRowMapper useraccountMapper;
    private final CartRowMapper cartMapper;

    private static final Table entityTable = Table.aliased("cart", EntityManager.ENTITY_ALIAS);
    private static final Table userAccountTable = Table.aliased("user_account", "userAccount");

    public CartRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserAccountRowMapper useraccountMapper,
        CartRowMapper cartMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.useraccountMapper = useraccountMapper;
        this.cartMapper = cartMapper;
    }

    @Override
    public Flux<Cart> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Cart> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Cart> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = CartSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserAccountSqlHelper.getColumns(userAccountTable, "userAccount"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userAccountTable)
            .on(Column.create("user_account_id", entityTable))
            .equals(Column.create("id", userAccountTable));

        String select = entityManager.createSelect(selectFrom, Cart.class, pageable, criteria);
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
    public Flux<Cart> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Cart> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Cart process(Row row, RowMetadata metadata) {
        Cart entity = cartMapper.apply(row, "e");
        entity.setUserAccount(useraccountMapper.apply(row, "userAccount"));
        return entity;
    }

    @Override
    public <S extends Cart> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Cart> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update Cart with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(Cart entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class CartSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("number_of_products", table, columnPrefix + "_number_of_products"));

        columns.add(Column.aliased("user_account_id", table, columnPrefix + "_user_account_id"));
        return columns;
    }
}
