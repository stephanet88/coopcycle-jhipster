package com.coopcycle.java.repository;

import com.coopcycle.java.domain.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Order entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderRepository extends R2dbcRepository<Order, Long>, OrderRepositoryInternal {
    @Query("SELECT * FROM jhi_order entity WHERE entity.cart_id = :id")
    Flux<Order> findByCart(Long id);

    @Query("SELECT * FROM jhi_order entity WHERE entity.cart_id IS NULL")
    Flux<Order> findAllWhereCartIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Order> findAll();

    @Override
    Mono<Order> findById(Long id);

    @Override
    <S extends Order> Mono<S> save(S entity);
}

interface OrderRepositoryInternal {
    <S extends Order> Mono<S> insert(S entity);
    <S extends Order> Mono<S> save(S entity);
    Mono<Integer> update(Order entity);

    Flux<Order> findAll();
    Mono<Order> findById(Long id);
    Flux<Order> findAllBy(Pageable pageable);
    Flux<Order> findAllBy(Pageable pageable, Criteria criteria);
}
