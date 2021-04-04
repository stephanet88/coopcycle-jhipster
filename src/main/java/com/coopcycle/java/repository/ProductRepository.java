package com.coopcycle.java.repository;

import com.coopcycle.java.domain.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Product entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductRepository extends R2dbcRepository<Product, Long>, ProductRepositoryInternal {
    @Query("SELECT * FROM product entity WHERE entity.institution_id = :id")
    Flux<Product> findByInstitution(Long id);

    @Query("SELECT * FROM product entity WHERE entity.institution_id IS NULL")
    Flux<Product> findAllWhereInstitutionIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Product> findAll();

    @Override
    Mono<Product> findById(Long id);

    @Override
    <S extends Product> Mono<S> save(S entity);
}

interface ProductRepositoryInternal {
    <S extends Product> Mono<S> insert(S entity);
    <S extends Product> Mono<S> save(S entity);
    Mono<Integer> update(Product entity);

    Flux<Product> findAll();
    Mono<Product> findById(Long id);
    Flux<Product> findAllBy(Pageable pageable);
    Flux<Product> findAllBy(Pageable pageable, Criteria criteria);
}
