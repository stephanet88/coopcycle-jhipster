package com.coopcycle.java.repository;

import com.coopcycle.java.domain.UserAccount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the UserAccount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserAccountRepository extends R2dbcRepository<UserAccount, Long>, UserAccountRepositoryInternal {
    @Query("SELECT * FROM user_account entity WHERE entity.id not in (select user_account_id from institution)")
    Flux<UserAccount> findAllWhereInstitutionIsNull();

    @Query("SELECT * FROM user_account entity WHERE entity.id not in (select user_account_id from cooperative)")
    Flux<UserAccount> findAllWhereCooperativeIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<UserAccount> findAll();

    @Override
    Mono<UserAccount> findById(Long id);

    @Override
    <S extends UserAccount> Mono<S> save(S entity);
}

interface UserAccountRepositoryInternal {
    <S extends UserAccount> Mono<S> insert(S entity);
    <S extends UserAccount> Mono<S> save(S entity);
    Mono<Integer> update(UserAccount entity);

    Flux<UserAccount> findAll();
    Mono<UserAccount> findById(Long id);
    Flux<UserAccount> findAllBy(Pageable pageable);
    Flux<UserAccount> findAllBy(Pageable pageable, Criteria criteria);
}
