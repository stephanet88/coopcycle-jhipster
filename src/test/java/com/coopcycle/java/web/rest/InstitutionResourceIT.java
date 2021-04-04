package com.coopcycle.java.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.coopcycle.java.IntegrationTest;
import com.coopcycle.java.domain.Institution;
import com.coopcycle.java.repository.InstitutionRepository;
import com.coopcycle.java.service.EntityManager;
import com.coopcycle.java.service.InstitutionService;
import com.coopcycle.java.service.dto.InstitutionDTO;
import com.coopcycle.java.service.mapper.InstitutionMapper;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link InstitutionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient
@WithMockUser
class InstitutionResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/institutions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InstitutionRepository institutionRepository;

    @Mock
    private InstitutionRepository institutionRepositoryMock;

    @Autowired
    private InstitutionMapper institutionMapper;

    @Mock
    private InstitutionService institutionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Institution institution;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Institution createEntity(EntityManager em) {
        Institution institution = new Institution().name(DEFAULT_NAME).type(DEFAULT_TYPE);
        return institution;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Institution createUpdatedEntity(EntityManager em) {
        Institution institution = new Institution().name(UPDATED_NAME).type(UPDATED_TYPE);
        return institution;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_institution__cart").block();
            em.deleteAll(Institution.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        institution = createEntity(em);
    }

    @Test
    void createInstitution() throws Exception {
        int databaseSizeBeforeCreate = institutionRepository.findAll().collectList().block().size();
        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(institutionDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Institution in the database
        List<Institution> institutionList = institutionRepository.findAll().collectList().block();
        assertThat(institutionList).hasSize(databaseSizeBeforeCreate + 1);
        Institution testInstitution = institutionList.get(institutionList.size() - 1);
        assertThat(testInstitution.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testInstitution.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    void createInstitutionWithExistingId() throws Exception {
        // Create the Institution with an existing ID
        institution.setId(1L);
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        int databaseSizeBeforeCreate = institutionRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(institutionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Institution in the database
        List<Institution> institutionList = institutionRepository.findAll().collectList().block();
        assertThat(institutionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = institutionRepository.findAll().collectList().block().size();
        // set the field null
        institution.setName(null);

        // Create the Institution, which fails.
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(institutionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Institution> institutionList = institutionRepository.findAll().collectList().block();
        assertThat(institutionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllInstitutionsAsStream() {
        // Initialize the database
        institutionRepository.save(institution).block();

        List<Institution> institutionList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(InstitutionDTO.class)
            .getResponseBody()
            .map(institutionMapper::toEntity)
            .filter(institution::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(institutionList).isNotNull();
        assertThat(institutionList).hasSize(1);
        Institution testInstitution = institutionList.get(0);
        assertThat(testInstitution.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testInstitution.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    void getAllInstitutions() {
        // Initialize the database
        institutionRepository.save(institution).block();

        // Get all the institutionList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(institution.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInstitutionsWithEagerRelationshipsIsEnabled() {
        when(institutionServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(institutionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInstitutionsWithEagerRelationshipsIsNotEnabled() {
        when(institutionServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(institutionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getInstitution() {
        // Initialize the database
        institutionRepository.save(institution).block();

        // Get the institution
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, institution.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(institution.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.type")
            .value(is(DEFAULT_TYPE));
    }

    @Test
    void getNonExistingInstitution() {
        // Get the institution
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewInstitution() throws Exception {
        // Initialize the database
        institutionRepository.save(institution).block();

        int databaseSizeBeforeUpdate = institutionRepository.findAll().collectList().block().size();

        // Update the institution
        Institution updatedInstitution = institutionRepository.findById(institution.getId()).block();
        updatedInstitution.name(UPDATED_NAME).type(UPDATED_TYPE);
        InstitutionDTO institutionDTO = institutionMapper.toDto(updatedInstitution);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, institutionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(institutionDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Institution in the database
        List<Institution> institutionList = institutionRepository.findAll().collectList().block();
        assertThat(institutionList).hasSize(databaseSizeBeforeUpdate);
        Institution testInstitution = institutionList.get(institutionList.size() - 1);
        assertThat(testInstitution.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testInstitution.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    void putNonExistingInstitution() throws Exception {
        int databaseSizeBeforeUpdate = institutionRepository.findAll().collectList().block().size();
        institution.setId(count.incrementAndGet());

        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, institutionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(institutionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Institution in the database
        List<Institution> institutionList = institutionRepository.findAll().collectList().block();
        assertThat(institutionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchInstitution() throws Exception {
        int databaseSizeBeforeUpdate = institutionRepository.findAll().collectList().block().size();
        institution.setId(count.incrementAndGet());

        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(institutionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Institution in the database
        List<Institution> institutionList = institutionRepository.findAll().collectList().block();
        assertThat(institutionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamInstitution() throws Exception {
        int databaseSizeBeforeUpdate = institutionRepository.findAll().collectList().block().size();
        institution.setId(count.incrementAndGet());

        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(institutionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Institution in the database
        List<Institution> institutionList = institutionRepository.findAll().collectList().block();
        assertThat(institutionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateInstitutionWithPatch() throws Exception {
        // Initialize the database
        institutionRepository.save(institution).block();

        int databaseSizeBeforeUpdate = institutionRepository.findAll().collectList().block().size();

        // Update the institution using partial update
        Institution partialUpdatedInstitution = new Institution();
        partialUpdatedInstitution.setId(institution.getId());

        partialUpdatedInstitution.type(UPDATED_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInstitution.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedInstitution))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Institution in the database
        List<Institution> institutionList = institutionRepository.findAll().collectList().block();
        assertThat(institutionList).hasSize(databaseSizeBeforeUpdate);
        Institution testInstitution = institutionList.get(institutionList.size() - 1);
        assertThat(testInstitution.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testInstitution.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    void fullUpdateInstitutionWithPatch() throws Exception {
        // Initialize the database
        institutionRepository.save(institution).block();

        int databaseSizeBeforeUpdate = institutionRepository.findAll().collectList().block().size();

        // Update the institution using partial update
        Institution partialUpdatedInstitution = new Institution();
        partialUpdatedInstitution.setId(institution.getId());

        partialUpdatedInstitution.name(UPDATED_NAME).type(UPDATED_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInstitution.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedInstitution))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Institution in the database
        List<Institution> institutionList = institutionRepository.findAll().collectList().block();
        assertThat(institutionList).hasSize(databaseSizeBeforeUpdate);
        Institution testInstitution = institutionList.get(institutionList.size() - 1);
        assertThat(testInstitution.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testInstitution.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    void patchNonExistingInstitution() throws Exception {
        int databaseSizeBeforeUpdate = institutionRepository.findAll().collectList().block().size();
        institution.setId(count.incrementAndGet());

        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, institutionDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(institutionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Institution in the database
        List<Institution> institutionList = institutionRepository.findAll().collectList().block();
        assertThat(institutionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchInstitution() throws Exception {
        int databaseSizeBeforeUpdate = institutionRepository.findAll().collectList().block().size();
        institution.setId(count.incrementAndGet());

        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(institutionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Institution in the database
        List<Institution> institutionList = institutionRepository.findAll().collectList().block();
        assertThat(institutionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamInstitution() throws Exception {
        int databaseSizeBeforeUpdate = institutionRepository.findAll().collectList().block().size();
        institution.setId(count.incrementAndGet());

        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(institutionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Institution in the database
        List<Institution> institutionList = institutionRepository.findAll().collectList().block();
        assertThat(institutionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteInstitution() {
        // Initialize the database
        institutionRepository.save(institution).block();

        int databaseSizeBeforeDelete = institutionRepository.findAll().collectList().block().size();

        // Delete the institution
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, institution.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Institution> institutionList = institutionRepository.findAll().collectList().block();
        assertThat(institutionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
