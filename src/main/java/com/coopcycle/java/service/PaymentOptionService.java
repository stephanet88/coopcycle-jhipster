package com.coopcycle.java.service;

import com.coopcycle.java.domain.PaymentOption;
import com.coopcycle.java.repository.PaymentOptionRepository;
import com.coopcycle.java.service.dto.PaymentOptionDTO;
import com.coopcycle.java.service.mapper.PaymentOptionMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link PaymentOption}.
 */
@Service
@Transactional
public class PaymentOptionService {

    private final Logger log = LoggerFactory.getLogger(PaymentOptionService.class);

    private final PaymentOptionRepository paymentOptionRepository;

    private final PaymentOptionMapper paymentOptionMapper;

    public PaymentOptionService(PaymentOptionRepository paymentOptionRepository, PaymentOptionMapper paymentOptionMapper) {
        this.paymentOptionRepository = paymentOptionRepository;
        this.paymentOptionMapper = paymentOptionMapper;
    }

    /**
     * Save a paymentOption.
     *
     * @param paymentOptionDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PaymentOptionDTO> save(PaymentOptionDTO paymentOptionDTO) {
        log.debug("Request to save PaymentOption : {}", paymentOptionDTO);
        return paymentOptionRepository.save(paymentOptionMapper.toEntity(paymentOptionDTO)).map(paymentOptionMapper::toDto);
    }

    /**
     * Partially update a paymentOption.
     *
     * @param paymentOptionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<PaymentOptionDTO> partialUpdate(PaymentOptionDTO paymentOptionDTO) {
        log.debug("Request to partially update PaymentOption : {}", paymentOptionDTO);

        return paymentOptionRepository
            .findById(paymentOptionDTO.getId())
            .map(
                existingPaymentOption -> {
                    paymentOptionMapper.partialUpdate(existingPaymentOption, paymentOptionDTO);
                    return existingPaymentOption;
                }
            )
            .flatMap(paymentOptionRepository::save)
            .map(paymentOptionMapper::toDto);
    }

    /**
     * Get all the paymentOptions.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<PaymentOptionDTO> findAll() {
        log.debug("Request to get all PaymentOptions");
        return paymentOptionRepository.findAll().map(paymentOptionMapper::toDto);
    }

    /**
     * Returns the number of paymentOptions available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return paymentOptionRepository.count();
    }

    /**
     * Get one paymentOption by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<PaymentOptionDTO> findOne(Long id) {
        log.debug("Request to get PaymentOption : {}", id);
        return paymentOptionRepository.findById(id).map(paymentOptionMapper::toDto);
    }

    /**
     * Delete the paymentOption by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete PaymentOption : {}", id);
        return paymentOptionRepository.deleteById(id);
    }
}
