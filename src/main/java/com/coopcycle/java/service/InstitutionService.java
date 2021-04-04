package com.coopcycle.java.service;

import com.coopcycle.java.domain.Institution;
import com.coopcycle.java.repository.InstitutionRepository;
import com.coopcycle.java.service.dto.InstitutionDTO;
import com.coopcycle.java.service.mapper.InstitutionMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Institution}.
 */
@Service
@Transactional
public class InstitutionService {

    private final Logger log = LoggerFactory.getLogger(InstitutionService.class);

    private final InstitutionRepository institutionRepository;

    private final InstitutionMapper institutionMapper;

    public InstitutionService(InstitutionRepository institutionRepository, InstitutionMapper institutionMapper) {
        this.institutionRepository = institutionRepository;
        this.institutionMapper = institutionMapper;
    }

    /**
     * Save a institution.
     *
     * @param institutionDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<InstitutionDTO> save(InstitutionDTO institutionDTO) {
        log.debug("Request to save Institution : {}", institutionDTO);
        return institutionRepository.save(institutionMapper.toEntity(institutionDTO)).map(institutionMapper::toDto);
    }

    /**
     * Partially update a institution.
     *
     * @param institutionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<InstitutionDTO> partialUpdate(InstitutionDTO institutionDTO) {
        log.debug("Request to partially update Institution : {}", institutionDTO);

        return institutionRepository
            .findById(institutionDTO.getId())
            .map(
                existingInstitution -> {
                    institutionMapper.partialUpdate(existingInstitution, institutionDTO);
                    return existingInstitution;
                }
            )
            .flatMap(institutionRepository::save)
            .map(institutionMapper::toDto);
    }

    /**
     * Get all the institutions.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<InstitutionDTO> findAll() {
        log.debug("Request to get all Institutions");
        return institutionRepository.findAllWithEagerRelationships().map(institutionMapper::toDto);
    }

    /**
     * Get all the institutions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Flux<InstitutionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return institutionRepository.findAllWithEagerRelationships(pageable).map(institutionMapper::toDto);
    }

    /**
     * Returns the number of institutions available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return institutionRepository.count();
    }

    /**
     * Get one institution by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<InstitutionDTO> findOne(Long id) {
        log.debug("Request to get Institution : {}", id);
        return institutionRepository.findOneWithEagerRelationships(id).map(institutionMapper::toDto);
    }

    /**
     * Delete the institution by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Institution : {}", id);
        return institutionRepository.deleteById(id);
    }
}
