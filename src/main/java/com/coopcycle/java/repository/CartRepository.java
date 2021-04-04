package com.coopcycle.java.repository;

import com.coopcycle.java.domain.Cart;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Cart entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CartRepository extends R2dbcRepository<Cart, Long>, CartRepositoryInternal {
    @Query("SELECT * FROM cart entity WHERE entity.id not in (select cart_id from jhi_order)")
    Flux<Cart> findAllWhereOrderIsNull();

    @Query("SELECT * FROM cart entity WHERE entity.id not in (select cart_id from payment_option)")
    Flux<Cart> findAllWherePaymentOptionIsNull();

    @Query("SELECT * FROM cart entity WHERE entity.user_account_id = :id")
    Flux<Cart> findByUserAccount(Long id);

    @Query("SELECT * FROM cart entity WHERE entity.user_account_id IS NULL")
    Flux<Cart> findAllWhereUserAccountIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Cart> findAll();

    @Override
    Mono<Cart> findById(Long id);

    @Override
    <S extends Cart> Mono<S> save(S entity);
}

interface CartRepositoryInternal {
    <S extends Cart> Mono<S> insert(S entity);
    <S extends Cart> Mono<S> save(S entity);
    Mono<Integer> update(Cart entity);

    Flux<Cart> findAll();
    Mono<Cart> findById(Long id);
    Flux<Cart> findAllBy(Pageable pageable);
    Flux<Cart> findAllBy(Pageable pageable, Criteria criteria);
}
