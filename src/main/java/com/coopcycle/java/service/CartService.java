package com.coopcycle.java.service;

import com.coopcycle.java.domain.Cart;
import com.coopcycle.java.repository.CartRepository;
import com.coopcycle.java.service.dto.CartDTO;
import com.coopcycle.java.service.mapper.CartMapper;
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
 * Service Implementation for managing {@link Cart}.
 */
@Service
@Transactional
public class CartService {

    private final Logger log = LoggerFactory.getLogger(CartService.class);

    private final CartRepository cartRepository;

    private final CartMapper cartMapper;

    public CartService(CartRepository cartRepository, CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
    }

    /**
     * Save a cart.
     *
     * @param cartDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CartDTO> save(CartDTO cartDTO) {
        log.debug("Request to save Cart : {}", cartDTO);
        return cartRepository.save(cartMapper.toEntity(cartDTO)).map(cartMapper::toDto);
    }

    /**
     * Partially update a cart.
     *
     * @param cartDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CartDTO> partialUpdate(CartDTO cartDTO) {
        log.debug("Request to partially update Cart : {}", cartDTO);

        return cartRepository
            .findById(cartDTO.getId())
            .map(
                existingCart -> {
                    cartMapper.partialUpdate(existingCart, cartDTO);
                    return existingCart;
                }
            )
            .flatMap(cartRepository::save)
            .map(cartMapper::toDto);
    }

    /**
     * Get all the carts.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CartDTO> findAll() {
        log.debug("Request to get all Carts");
        return cartRepository.findAll().map(cartMapper::toDto);
    }

    /**
     *  Get all the carts where Order is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CartDTO> findAllWhereOrderIsNull() {
        log.debug("Request to get all carts where Order is null");
        return cartRepository.findAllWhereOrderIsNull().map(cartMapper::toDto);
    }

    /**
     *  Get all the carts where PaymentOption is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CartDTO> findAllWherePaymentOptionIsNull() {
        log.debug("Request to get all carts where PaymentOption is null");
        return cartRepository.findAllWherePaymentOptionIsNull().map(cartMapper::toDto);
    }

    /**
     * Returns the number of carts available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return cartRepository.count();
    }

    /**
     * Get one cart by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<CartDTO> findOne(Long id) {
        log.debug("Request to get Cart : {}", id);
        return cartRepository.findById(id).map(cartMapper::toDto);
    }

    /**
     * Delete the cart by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Cart : {}", id);
        return cartRepository.deleteById(id);
    }
}
