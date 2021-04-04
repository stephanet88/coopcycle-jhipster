package com.coopcycle.java.repository;

import com.coopcycle.java.domain.Institution;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Institution entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InstitutionRepository extends R2dbcRepository<Institution, Long>, InstitutionRepositoryInternal {
    @Override
    Mono<Institution> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Institution> findAllWithEagerRelationships();

    @Override
    Flux<Institution> findAllWithEagerRelationships(Pageable page);

    @Override
    Mono<Void> deleteById(Long id);

    @Query("SELECT * FROM institution entity WHERE entity.user_account_id = :id")
    Flux<Institution> findByUserAccount(Long id);

    @Query("SELECT * FROM institution entity WHERE entity.user_account_id IS NULL")
    Flux<Institution> findAllWhereUserAccountIsNull();

    @Query(
        "SELECT entity.* FROM institution entity JOIN rel_institution__cart joinTable ON entity.id = joinTable.institution_id WHERE joinTable.cart_id = :id"
    )
    Flux<Institution> findByCart(Long id);

    @Query("SELECT * FROM institution entity WHERE entity.cooperative_id = :id")
    Flux<Institution> findByCooperative(Long id);

    @Query("SELECT * FROM institution entity WHERE entity.cooperative_id IS NULL")
    Flux<Institution> findAllWhereCooperativeIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Institution> findAll();

    @Override
    Mono<Institution> findById(Long id);

    @Override
    <S extends Institution> Mono<S> save(S entity);
}

interface InstitutionRepositoryInternal {
    <S extends Institution> Mono<S> insert(S entity);
    <S extends Institution> Mono<S> save(S entity);
    Mono<Integer> update(Institution entity);

    Flux<Institution> findAll();
    Mono<Institution> findById(Long id);
    Flux<Institution> findAllBy(Pageable pageable);
    Flux<Institution> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Institution> findOneWithEagerRelationships(Long id);

    Flux<Institution> findAllWithEagerRelationships();

    Flux<Institution> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
