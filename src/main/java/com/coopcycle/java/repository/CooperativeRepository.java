package com.coopcycle.java.repository;

import com.coopcycle.java.domain.Cooperative;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Cooperative entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CooperativeRepository extends R2dbcRepository<Cooperative, Long>, CooperativeRepositoryInternal {
    @Query("SELECT * FROM cooperative entity WHERE entity.user_account_id = :id")
    Flux<Cooperative> findByUserAccount(Long id);

    @Query("SELECT * FROM cooperative entity WHERE entity.user_account_id IS NULL")
    Flux<Cooperative> findAllWhereUserAccountIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Cooperative> findAll();

    @Override
    Mono<Cooperative> findById(Long id);

    @Override
    <S extends Cooperative> Mono<S> save(S entity);
}

interface CooperativeRepositoryInternal {
    <S extends Cooperative> Mono<S> insert(S entity);
    <S extends Cooperative> Mono<S> save(S entity);
    Mono<Integer> update(Cooperative entity);

    Flux<Cooperative> findAll();
    Mono<Cooperative> findById(Long id);
    Flux<Cooperative> findAllBy(Pageable pageable);
    Flux<Cooperative> findAllBy(Pageable pageable, Criteria criteria);
}
