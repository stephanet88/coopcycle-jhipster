package com.coopcycle.java.service;

import com.coopcycle.java.domain.UserAccount;
import com.coopcycle.java.repository.UserAccountRepository;
import com.coopcycle.java.service.dto.UserAccountDTO;
import com.coopcycle.java.service.mapper.UserAccountMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link UserAccount}.
 */
@Service
@Transactional
public class UserAccountService {

    private final Logger log = LoggerFactory.getLogger(UserAccountService.class);

    private final UserAccountRepository userAccountRepository;

    private final UserAccountMapper userAccountMapper;

    public UserAccountService(UserAccountRepository userAccountRepository, UserAccountMapper userAccountMapper) {
        this.userAccountRepository = userAccountRepository;
        this.userAccountMapper = userAccountMapper;
    }

    /**
     * Save a userAccount.
     *
     * @param userAccountDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<UserAccountDTO> save(UserAccountDTO userAccountDTO) {
        log.debug("Request to save UserAccount : {}", userAccountDTO);
        return userAccountRepository.save(userAccountMapper.toEntity(userAccountDTO)).map(userAccountMapper::toDto);
    }

    /**
     * Partially update a userAccount.
     *
     * @param userAccountDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<UserAccountDTO> partialUpdate(UserAccountDTO userAccountDTO) {
        log.debug("Request to partially update UserAccount : {}", userAccountDTO);

        return userAccountRepository
            .findById(userAccountDTO.getId())
            .map(
                existingUserAccount -> {
                    userAccountMapper.partialUpdate(existingUserAccount, userAccountDTO);
                    return existingUserAccount;
                }
            )
            .flatMap(userAccountRepository::save)
            .map(userAccountMapper::toDto);
    }

    /**
     * Get all the userAccounts.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<UserAccountDTO> findAll() {
        log.debug("Request to get all UserAccounts");
        return userAccountRepository.findAll().map(userAccountMapper::toDto);
    }

    /**
     *  Get all the userAccounts where Institution is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<UserAccountDTO> findAllWhereInstitutionIsNull() {
        log.debug("Request to get all userAccounts where Institution is null");
        return userAccountRepository.findAllWhereInstitutionIsNull().map(userAccountMapper::toDto);
    }

    /**
     *  Get all the userAccounts where Cooperative is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<UserAccountDTO> findAllWhereCooperativeIsNull() {
        log.debug("Request to get all userAccounts where Cooperative is null");
        return userAccountRepository.findAllWhereCooperativeIsNull().map(userAccountMapper::toDto);
    }

    /**
     * Returns the number of userAccounts available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return userAccountRepository.count();
    }

    /**
     * Get one userAccount by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<UserAccountDTO> findOne(Long id) {
        log.debug("Request to get UserAccount : {}", id);
        return userAccountRepository.findById(id).map(userAccountMapper::toDto);
    }

    /**
     * Delete the userAccount by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete UserAccount : {}", id);
        return userAccountRepository.deleteById(id);
    }
}
