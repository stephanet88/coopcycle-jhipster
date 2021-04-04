package com.coopcycle.java.web.rest;

import com.coopcycle.java.repository.UserAccountRepository;
import com.coopcycle.java.service.UserAccountService;
import com.coopcycle.java.service.dto.UserAccountDTO;
import com.coopcycle.java.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.coopcycle.java.domain.UserAccount}.
 */
@RestController
@RequestMapping("/api")
public class UserAccountResource {

    private final Logger log = LoggerFactory.getLogger(UserAccountResource.class);

    private static final String ENTITY_NAME = "userAccount";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserAccountService userAccountService;

    private final UserAccountRepository userAccountRepository;

    public UserAccountResource(UserAccountService userAccountService, UserAccountRepository userAccountRepository) {
        this.userAccountService = userAccountService;
        this.userAccountRepository = userAccountRepository;
    }

    /**
     * {@code POST  /user-accounts} : Create a new userAccount.
     *
     * @param userAccountDTO the userAccountDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userAccountDTO, or with status {@code 400 (Bad Request)} if the userAccount has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-accounts")
    public Mono<ResponseEntity<UserAccountDTO>> createUserAccount(@Valid @RequestBody UserAccountDTO userAccountDTO)
        throws URISyntaxException {
        log.debug("REST request to save UserAccount : {}", userAccountDTO);
        if (userAccountDTO.getId() != null) {
            throw new BadRequestAlertException("A new userAccount cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return userAccountService
            .save(userAccountDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/user-accounts/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /user-accounts/:id} : Updates an existing userAccount.
     *
     * @param id the id of the userAccountDTO to save.
     * @param userAccountDTO the userAccountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userAccountDTO,
     * or with status {@code 400 (Bad Request)} if the userAccountDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userAccountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-accounts/{id}")
    public Mono<ResponseEntity<UserAccountDTO>> updateUserAccount(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserAccountDTO userAccountDTO
    ) throws URISyntaxException {
        log.debug("REST request to update UserAccount : {}, {}", id, userAccountDTO);
        if (userAccountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userAccountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return userAccountRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return userAccountService
                        .save(userAccountDTO)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            result ->
                                ResponseEntity
                                    .ok()
                                    .headers(
                                        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString())
                                    )
                                    .body(result)
                        );
                }
            );
    }

    /**
     * {@code PATCH  /user-accounts/:id} : Partial updates given fields of an existing userAccount, field will ignore if it is null
     *
     * @param id the id of the userAccountDTO to save.
     * @param userAccountDTO the userAccountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userAccountDTO,
     * or with status {@code 400 (Bad Request)} if the userAccountDTO is not valid,
     * or with status {@code 404 (Not Found)} if the userAccountDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the userAccountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-accounts/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<UserAccountDTO>> partialUpdateUserAccount(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserAccountDTO userAccountDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserAccount partially : {}, {}", id, userAccountDTO);
        if (userAccountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userAccountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return userAccountRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<UserAccountDTO> result = userAccountService.partialUpdate(userAccountDTO);

                    return result
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            res ->
                                ResponseEntity
                                    .ok()
                                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                                    .body(res)
                        );
                }
            );
    }

    /**
     * {@code GET  /user-accounts} : get all the userAccounts.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userAccounts in body.
     */
    @GetMapping("/user-accounts")
    public Mono<List<UserAccountDTO>> getAllUserAccounts(@RequestParam(required = false) String filter) {
        if ("institution-is-null".equals(filter)) {
            log.debug("REST request to get all UserAccounts where institution is null");
            return userAccountService.findAllWhereInstitutionIsNull().collectList();
        }
        if ("cooperative-is-null".equals(filter)) {
            log.debug("REST request to get all UserAccounts where cooperative is null");
            return userAccountService.findAllWhereCooperativeIsNull().collectList();
        }
        log.debug("REST request to get all UserAccounts");
        return userAccountService.findAll().collectList();
    }

    /**
     * {@code GET  /user-accounts} : get all the userAccounts as a stream.
     * @return the {@link Flux} of userAccounts.
     */
    @GetMapping(value = "/user-accounts", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<UserAccountDTO> getAllUserAccountsAsStream() {
        log.debug("REST request to get all UserAccounts as a stream");
        return userAccountService.findAll();
    }

    /**
     * {@code GET  /user-accounts/:id} : get the "id" userAccount.
     *
     * @param id the id of the userAccountDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userAccountDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-accounts/{id}")
    public Mono<ResponseEntity<UserAccountDTO>> getUserAccount(@PathVariable Long id) {
        log.debug("REST request to get UserAccount : {}", id);
        Mono<UserAccountDTO> userAccountDTO = userAccountService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userAccountDTO);
    }

    /**
     * {@code DELETE  /user-accounts/:id} : delete the "id" userAccount.
     *
     * @param id the id of the userAccountDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-accounts/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteUserAccount(@PathVariable Long id) {
        log.debug("REST request to delete UserAccount : {}", id);
        return userAccountService
            .delete(id)
            .map(
                result ->
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
            );
    }
}
