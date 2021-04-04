package com.coopcycle.java.web.rest;

import com.coopcycle.java.repository.PaymentOptionRepository;
import com.coopcycle.java.service.PaymentOptionService;
import com.coopcycle.java.service.dto.PaymentOptionDTO;
import com.coopcycle.java.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
 * REST controller for managing {@link com.coopcycle.java.domain.PaymentOption}.
 */
@RestController
@RequestMapping("/api")
public class PaymentOptionResource {

    private final Logger log = LoggerFactory.getLogger(PaymentOptionResource.class);

    private static final String ENTITY_NAME = "paymentOption";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PaymentOptionService paymentOptionService;

    private final PaymentOptionRepository paymentOptionRepository;

    public PaymentOptionResource(PaymentOptionService paymentOptionService, PaymentOptionRepository paymentOptionRepository) {
        this.paymentOptionService = paymentOptionService;
        this.paymentOptionRepository = paymentOptionRepository;
    }

    /**
     * {@code POST  /payment-options} : Create a new paymentOption.
     *
     * @param paymentOptionDTO the paymentOptionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new paymentOptionDTO, or with status {@code 400 (Bad Request)} if the paymentOption has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/payment-options")
    public Mono<ResponseEntity<PaymentOptionDTO>> createPaymentOption(@RequestBody PaymentOptionDTO paymentOptionDTO)
        throws URISyntaxException {
        log.debug("REST request to save PaymentOption : {}", paymentOptionDTO);
        if (paymentOptionDTO.getId() != null) {
            throw new BadRequestAlertException("A new paymentOption cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return paymentOptionService
            .save(paymentOptionDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/payment-options/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /payment-options/:id} : Updates an existing paymentOption.
     *
     * @param id the id of the paymentOptionDTO to save.
     * @param paymentOptionDTO the paymentOptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paymentOptionDTO,
     * or with status {@code 400 (Bad Request)} if the paymentOptionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the paymentOptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/payment-options/{id}")
    public Mono<ResponseEntity<PaymentOptionDTO>> updatePaymentOption(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PaymentOptionDTO paymentOptionDTO
    ) throws URISyntaxException {
        log.debug("REST request to update PaymentOption : {}, {}", id, paymentOptionDTO);
        if (paymentOptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paymentOptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return paymentOptionRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return paymentOptionService
                        .save(paymentOptionDTO)
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
     * {@code PATCH  /payment-options/:id} : Partial updates given fields of an existing paymentOption, field will ignore if it is null
     *
     * @param id the id of the paymentOptionDTO to save.
     * @param paymentOptionDTO the paymentOptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paymentOptionDTO,
     * or with status {@code 400 (Bad Request)} if the paymentOptionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the paymentOptionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the paymentOptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/payment-options/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<PaymentOptionDTO>> partialUpdatePaymentOption(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PaymentOptionDTO paymentOptionDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update PaymentOption partially : {}, {}", id, paymentOptionDTO);
        if (paymentOptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paymentOptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return paymentOptionRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<PaymentOptionDTO> result = paymentOptionService.partialUpdate(paymentOptionDTO);

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
     * {@code GET  /payment-options} : get all the paymentOptions.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of paymentOptions in body.
     */
    @GetMapping("/payment-options")
    public Mono<List<PaymentOptionDTO>> getAllPaymentOptions() {
        log.debug("REST request to get all PaymentOptions");
        return paymentOptionService.findAll().collectList();
    }

    /**
     * {@code GET  /payment-options} : get all the paymentOptions as a stream.
     * @return the {@link Flux} of paymentOptions.
     */
    @GetMapping(value = "/payment-options", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<PaymentOptionDTO> getAllPaymentOptionsAsStream() {
        log.debug("REST request to get all PaymentOptions as a stream");
        return paymentOptionService.findAll();
    }

    /**
     * {@code GET  /payment-options/:id} : get the "id" paymentOption.
     *
     * @param id the id of the paymentOptionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the paymentOptionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/payment-options/{id}")
    public Mono<ResponseEntity<PaymentOptionDTO>> getPaymentOption(@PathVariable Long id) {
        log.debug("REST request to get PaymentOption : {}", id);
        Mono<PaymentOptionDTO> paymentOptionDTO = paymentOptionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(paymentOptionDTO);
    }

    /**
     * {@code DELETE  /payment-options/:id} : delete the "id" paymentOption.
     *
     * @param id the id of the paymentOptionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/payment-options/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePaymentOption(@PathVariable Long id) {
        log.debug("REST request to delete PaymentOption : {}", id);
        return paymentOptionService
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
