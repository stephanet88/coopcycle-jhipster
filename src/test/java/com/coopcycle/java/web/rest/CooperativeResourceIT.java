package com.coopcycle.java.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.coopcycle.java.IntegrationTest;
import com.coopcycle.java.domain.Cooperative;
import com.coopcycle.java.repository.CooperativeRepository;
import com.coopcycle.java.service.EntityManager;
import com.coopcycle.java.service.dto.CooperativeDTO;
import com.coopcycle.java.service.mapper.CooperativeMapper;
import java.time.Duration;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link CooperativeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class CooperativeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/cooperatives";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CooperativeRepository cooperativeRepository;

    @Autowired
    private CooperativeMapper cooperativeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Cooperative cooperative;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cooperative createEntity(EntityManager em) {
        Cooperative cooperative = new Cooperative().name(DEFAULT_NAME).location(DEFAULT_LOCATION);
        return cooperative;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cooperative createUpdatedEntity(EntityManager em) {
        Cooperative cooperative = new Cooperative().name(UPDATED_NAME).location(UPDATED_LOCATION);
        return cooperative;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Cooperative.class).block();
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
        cooperative = createEntity(em);
    }

    @Test
    void createCooperative() throws Exception {
        int databaseSizeBeforeCreate = cooperativeRepository.findAll().collectList().block().size();
        // Create the Cooperative
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeCreate + 1);
        Cooperative testCooperative = cooperativeList.get(cooperativeList.size() - 1);
        assertThat(testCooperative.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCooperative.getLocation()).isEqualTo(DEFAULT_LOCATION);
    }

    @Test
    void createCooperativeWithExistingId() throws Exception {
        // Create the Cooperative with an existing ID
        cooperative.setId(1L);
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);

        int databaseSizeBeforeCreate = cooperativeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = cooperativeRepository.findAll().collectList().block().size();
        // set the field null
        cooperative.setName(null);

        // Create the Cooperative, which fails.
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLocationIsRequired() throws Exception {
        int databaseSizeBeforeTest = cooperativeRepository.findAll().collectList().block().size();
        // set the field null
        cooperative.setLocation(null);

        // Create the Cooperative, which fails.
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCooperativesAsStream() {
        // Initialize the database
        cooperativeRepository.save(cooperative).block();

        List<Cooperative> cooperativeList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(CooperativeDTO.class)
            .getResponseBody()
            .map(cooperativeMapper::toEntity)
            .filter(cooperative::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(cooperativeList).isNotNull();
        assertThat(cooperativeList).hasSize(1);
        Cooperative testCooperative = cooperativeList.get(0);
        assertThat(testCooperative.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCooperative.getLocation()).isEqualTo(DEFAULT_LOCATION);
    }

    @Test
    void getAllCooperatives() {
        // Initialize the database
        cooperativeRepository.save(cooperative).block();

        // Get all the cooperativeList
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
            .value(hasItem(cooperative.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].location")
            .value(hasItem(DEFAULT_LOCATION));
    }

    @Test
    void getCooperative() {
        // Initialize the database
        cooperativeRepository.save(cooperative).block();

        // Get the cooperative
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, cooperative.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(cooperative.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.location")
            .value(is(DEFAULT_LOCATION));
    }

    @Test
    void getNonExistingCooperative() {
        // Get the cooperative
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCooperative() throws Exception {
        // Initialize the database
        cooperativeRepository.save(cooperative).block();

        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().collectList().block().size();

        // Update the cooperative
        Cooperative updatedCooperative = cooperativeRepository.findById(cooperative.getId()).block();
        updatedCooperative.name(UPDATED_NAME).location(UPDATED_LOCATION);
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(updatedCooperative);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, cooperativeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
        Cooperative testCooperative = cooperativeList.get(cooperativeList.size() - 1);
        assertThat(testCooperative.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCooperative.getLocation()).isEqualTo(UPDATED_LOCATION);
    }

    @Test
    void putNonExistingCooperative() throws Exception {
        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().collectList().block().size();
        cooperative.setId(count.incrementAndGet());

        // Create the Cooperative
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, cooperativeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCooperative() throws Exception {
        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().collectList().block().size();
        cooperative.setId(count.incrementAndGet());

        // Create the Cooperative
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCooperative() throws Exception {
        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().collectList().block().size();
        cooperative.setId(count.incrementAndGet());

        // Create the Cooperative
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCooperativeWithPatch() throws Exception {
        // Initialize the database
        cooperativeRepository.save(cooperative).block();

        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().collectList().block().size();

        // Update the cooperative using partial update
        Cooperative partialUpdatedCooperative = new Cooperative();
        partialUpdatedCooperative.setId(cooperative.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCooperative.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCooperative))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
        Cooperative testCooperative = cooperativeList.get(cooperativeList.size() - 1);
        assertThat(testCooperative.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCooperative.getLocation()).isEqualTo(DEFAULT_LOCATION);
    }

    @Test
    void fullUpdateCooperativeWithPatch() throws Exception {
        // Initialize the database
        cooperativeRepository.save(cooperative).block();

        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().collectList().block().size();

        // Update the cooperative using partial update
        Cooperative partialUpdatedCooperative = new Cooperative();
        partialUpdatedCooperative.setId(cooperative.getId());

        partialUpdatedCooperative.name(UPDATED_NAME).location(UPDATED_LOCATION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCooperative.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCooperative))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
        Cooperative testCooperative = cooperativeList.get(cooperativeList.size() - 1);
        assertThat(testCooperative.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCooperative.getLocation()).isEqualTo(UPDATED_LOCATION);
    }

    @Test
    void patchNonExistingCooperative() throws Exception {
        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().collectList().block().size();
        cooperative.setId(count.incrementAndGet());

        // Create the Cooperative
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, cooperativeDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCooperative() throws Exception {
        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().collectList().block().size();
        cooperative.setId(count.incrementAndGet());

        // Create the Cooperative
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCooperative() throws Exception {
        int databaseSizeBeforeUpdate = cooperativeRepository.findAll().collectList().block().size();
        cooperative.setId(count.incrementAndGet());

        // Create the Cooperative
        CooperativeDTO cooperativeDTO = cooperativeMapper.toDto(cooperative);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cooperativeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Cooperative in the database
        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCooperative() {
        // Initialize the database
        cooperativeRepository.save(cooperative).block();

        int databaseSizeBeforeDelete = cooperativeRepository.findAll().collectList().block().size();

        // Delete the cooperative
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, cooperative.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Cooperative> cooperativeList = cooperativeRepository.findAll().collectList().block();
        assertThat(cooperativeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
