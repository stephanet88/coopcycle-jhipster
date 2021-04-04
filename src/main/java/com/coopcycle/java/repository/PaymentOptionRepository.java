package com.coopcycle.java.repository;

import com.coopcycle.java.domain.PaymentOption;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the PaymentOption entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaymentOptionRepository extends R2dbcRepository<PaymentOption, Long>, PaymentOptionRepositoryInternal {
    @Query("SELECT * FROM payment_option entity WHERE entity.cart_id = :id")
    Flux<PaymentOption> findByCart(Long id);

    @Query("SELECT * FROM payment_option entity WHERE entity.cart_id IS NULL")
    Flux<PaymentOption> findAllWhereCartIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<PaymentOption> findAll();

    @Override
    Mono<PaymentOption> findById(Long id);

    @Override
    <S extends PaymentOption> Mono<S> save(S entity);
}

interface PaymentOptionRepositoryInternal {
    <S extends PaymentOption> Mono<S> insert(S entity);
    <S extends PaymentOption> Mono<S> save(S entity);
    Mono<Integer> update(PaymentOption entity);

    Flux<PaymentOption> findAll();
    Mono<PaymentOption> findById(Long id);
    Flux<PaymentOption> findAllBy(Pageable pageable);
    Flux<PaymentOption> findAllBy(Pageable pageable, Criteria criteria);
}
