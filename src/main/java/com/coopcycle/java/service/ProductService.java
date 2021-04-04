package com.coopcycle.java.service;

import com.coopcycle.java.domain.Product;
import com.coopcycle.java.repository.ProductRepository;
import com.coopcycle.java.service.dto.ProductDTO;
import com.coopcycle.java.service.mapper.ProductMapper;
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
 * Service Implementation for managing {@link Product}.
 */
@Service
@Transactional
public class ProductService {

    private final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    /**
     * Save a product.
     *
     * @param productDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ProductDTO> save(ProductDTO productDTO) {
        log.debug("Request to save Product : {}", productDTO);
        return productRepository.save(productMapper.toEntity(productDTO)).map(productMapper::toDto);
    }

    /**
     * Partially update a product.
     *
     * @param productDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ProductDTO> partialUpdate(ProductDTO productDTO) {
        log.debug("Request to partially update Product : {}", productDTO);

        return productRepository
            .findById(productDTO.getId())
            .map(
                existingProduct -> {
                    productMapper.partialUpdate(existingProduct, productDTO);
                    return existingProduct;
                }
            )
            .flatMap(productRepository::save)
            .map(productMapper::toDto);
    }

    /**
     * Get all the products.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ProductDTO> findAll() {
        log.debug("Request to get all Products");
        return productRepository.findAll().map(productMapper::toDto);
    }

    /**
     * Returns the number of products available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return productRepository.count();
    }

    /**
     * Get one product by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ProductDTO> findOne(Long id) {
        log.debug("Request to get Product : {}", id);
        return productRepository.findById(id).map(productMapper::toDto);
    }

    /**
     * Delete the product by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Product : {}", id);
        return productRepository.deleteById(id);
    }
}
