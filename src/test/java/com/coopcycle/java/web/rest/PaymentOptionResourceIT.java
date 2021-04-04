package com.coopcycle.java.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.coopcycle.java.IntegrationTest;
import com.coopcycle.java.domain.PaymentOption;
import com.coopcycle.java.repository.PaymentOptionRepository;
import com.coopcycle.java.service.EntityManager;
import com.coopcycle.java.service.dto.PaymentOptionDTO;
import com.coopcycle.java.service.mapper.PaymentOptionMapper;
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
 * Integration tests for the {@link PaymentOptionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class PaymentOptionResourceIT {

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/payment-options";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PaymentOptionRepository paymentOptionRepository;

    @Autowired
    private PaymentOptionMapper paymentOptionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private PaymentOption paymentOption;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentOption createEntity(EntityManager em) {
        PaymentOption paymentOption = new PaymentOption().type(DEFAULT_TYPE);
        return paymentOption;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaymentOption createUpdatedEntity(EntityManager em) {
        PaymentOption paymentOption = new PaymentOption().type(UPDATED_TYPE);
        return paymentOption;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(PaymentOption.class).block();
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
        paymentOption = createEntity(em);
    }

    @Test
    void createPaymentOption() throws Exception {
        int databaseSizeBeforeCreate = paymentOptionRepository.findAll().collectList().block().size();
        // Create the PaymentOption
        PaymentOptionDTO paymentOptionDTO = paymentOptionMapper.toDto(paymentOption);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentOptionDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the PaymentOption in the database
        List<PaymentOption> paymentOptionList = paymentOptionRepository.findAll().collectList().block();
        assertThat(paymentOptionList).hasSize(databaseSizeBeforeCreate + 1);
        PaymentOption testPaymentOption = paymentOptionList.get(paymentOptionList.size() - 1);
        assertThat(testPaymentOption.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    void createPaymentOptionWithExistingId() throws Exception {
        // Create the PaymentOption with an existing ID
        paymentOption.setId(1L);
        PaymentOptionDTO paymentOptionDTO = paymentOptionMapper.toDto(paymentOption);

        int databaseSizeBeforeCreate = paymentOptionRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentOptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PaymentOption in the database
        List<PaymentOption> paymentOptionList = paymentOptionRepository.findAll().collectList().block();
        assertThat(paymentOptionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPaymentOptionsAsStream() {
        // Initialize the database
        paymentOptionRepository.save(paymentOption).block();

        List<PaymentOption> paymentOptionList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(PaymentOptionDTO.class)
            .getResponseBody()
            .map(paymentOptionMapper::toEntity)
            .filter(paymentOption::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(paymentOptionList).isNotNull();
        assertThat(paymentOptionList).hasSize(1);
        PaymentOption testPaymentOption = paymentOptionList.get(0);
        assertThat(testPaymentOption.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    void getAllPaymentOptions() {
        // Initialize the database
        paymentOptionRepository.save(paymentOption).block();

        // Get all the paymentOptionList
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
            .value(hasItem(paymentOption.getId().intValue()))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE));
    }

    @Test
    void getPaymentOption() {
        // Initialize the database
        paymentOptionRepository.save(paymentOption).block();

        // Get the paymentOption
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, paymentOption.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(paymentOption.getId().intValue()))
            .jsonPath("$.type")
            .value(is(DEFAULT_TYPE));
    }

    @Test
    void getNonExistingPaymentOption() {
        // Get the paymentOption
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPaymentOption() throws Exception {
        // Initialize the database
        paymentOptionRepository.save(paymentOption).block();

        int databaseSizeBeforeUpdate = paymentOptionRepository.findAll().collectList().block().size();

        // Update the paymentOption
        PaymentOption updatedPaymentOption = paymentOptionRepository.findById(paymentOption.getId()).block();
        updatedPaymentOption.type(UPDATED_TYPE);
        PaymentOptionDTO paymentOptionDTO = paymentOptionMapper.toDto(updatedPaymentOption);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, paymentOptionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentOptionDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PaymentOption in the database
        List<PaymentOption> paymentOptionList = paymentOptionRepository.findAll().collectList().block();
        assertThat(paymentOptionList).hasSize(databaseSizeBeforeUpdate);
        PaymentOption testPaymentOption = paymentOptionList.get(paymentOptionList.size() - 1);
        assertThat(testPaymentOption.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    void putNonExistingPaymentOption() throws Exception {
        int databaseSizeBeforeUpdate = paymentOptionRepository.findAll().collectList().block().size();
        paymentOption.setId(count.incrementAndGet());

        // Create the PaymentOption
        PaymentOptionDTO paymentOptionDTO = paymentOptionMapper.toDto(paymentOption);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, paymentOptionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentOptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PaymentOption in the database
        List<PaymentOption> paymentOptionList = paymentOptionRepository.findAll().collectList().block();
        assertThat(paymentOptionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPaymentOption() throws Exception {
        int databaseSizeBeforeUpdate = paymentOptionRepository.findAll().collectList().block().size();
        paymentOption.setId(count.incrementAndGet());

        // Create the PaymentOption
        PaymentOptionDTO paymentOptionDTO = paymentOptionMapper.toDto(paymentOption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentOptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PaymentOption in the database
        List<PaymentOption> paymentOptionList = paymentOptionRepository.findAll().collectList().block();
        assertThat(paymentOptionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPaymentOption() throws Exception {
        int databaseSizeBeforeUpdate = paymentOptionRepository.findAll().collectList().block().size();
        paymentOption.setId(count.incrementAndGet());

        // Create the PaymentOption
        PaymentOptionDTO paymentOptionDTO = paymentOptionMapper.toDto(paymentOption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentOptionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PaymentOption in the database
        List<PaymentOption> paymentOptionList = paymentOptionRepository.findAll().collectList().block();
        assertThat(paymentOptionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePaymentOptionWithPatch() throws Exception {
        // Initialize the database
        paymentOptionRepository.save(paymentOption).block();

        int databaseSizeBeforeUpdate = paymentOptionRepository.findAll().collectList().block().size();

        // Update the paymentOption using partial update
        PaymentOption partialUpdatedPaymentOption = new PaymentOption();
        partialUpdatedPaymentOption.setId(paymentOption.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPaymentOption.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPaymentOption))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PaymentOption in the database
        List<PaymentOption> paymentOptionList = paymentOptionRepository.findAll().collectList().block();
        assertThat(paymentOptionList).hasSize(databaseSizeBeforeUpdate);
        PaymentOption testPaymentOption = paymentOptionList.get(paymentOptionList.size() - 1);
        assertThat(testPaymentOption.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    void fullUpdatePaymentOptionWithPatch() throws Exception {
        // Initialize the database
        paymentOptionRepository.save(paymentOption).block();

        int databaseSizeBeforeUpdate = paymentOptionRepository.findAll().collectList().block().size();

        // Update the paymentOption using partial update
        PaymentOption partialUpdatedPaymentOption = new PaymentOption();
        partialUpdatedPaymentOption.setId(paymentOption.getId());

        partialUpdatedPaymentOption.type(UPDATED_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPaymentOption.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPaymentOption))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PaymentOption in the database
        List<PaymentOption> paymentOptionList = paymentOptionRepository.findAll().collectList().block();
        assertThat(paymentOptionList).hasSize(databaseSizeBeforeUpdate);
        PaymentOption testPaymentOption = paymentOptionList.get(paymentOptionList.size() - 1);
        assertThat(testPaymentOption.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    void patchNonExistingPaymentOption() throws Exception {
        int databaseSizeBeforeUpdate = paymentOptionRepository.findAll().collectList().block().size();
        paymentOption.setId(count.incrementAndGet());

        // Create the PaymentOption
        PaymentOptionDTO paymentOptionDTO = paymentOptionMapper.toDto(paymentOption);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, paymentOptionDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentOptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PaymentOption in the database
        List<PaymentOption> paymentOptionList = paymentOptionRepository.findAll().collectList().block();
        assertThat(paymentOptionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPaymentOption() throws Exception {
        int databaseSizeBeforeUpdate = paymentOptionRepository.findAll().collectList().block().size();
        paymentOption.setId(count.incrementAndGet());

        // Create the PaymentOption
        PaymentOptionDTO paymentOptionDTO = paymentOptionMapper.toDto(paymentOption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentOptionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PaymentOption in the database
        List<PaymentOption> paymentOptionList = paymentOptionRepository.findAll().collectList().block();
        assertThat(paymentOptionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPaymentOption() throws Exception {
        int databaseSizeBeforeUpdate = paymentOptionRepository.findAll().collectList().block().size();
        paymentOption.setId(count.incrementAndGet());

        // Create the PaymentOption
        PaymentOptionDTO paymentOptionDTO = paymentOptionMapper.toDto(paymentOption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(paymentOptionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PaymentOption in the database
        List<PaymentOption> paymentOptionList = paymentOptionRepository.findAll().collectList().block();
        assertThat(paymentOptionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePaymentOption() {
        // Initialize the database
        paymentOptionRepository.save(paymentOption).block();

        int databaseSizeBeforeDelete = paymentOptionRepository.findAll().collectList().block().size();

        // Delete the paymentOption
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, paymentOption.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<PaymentOption> paymentOptionList = paymentOptionRepository.findAll().collectList().block();
        assertThat(paymentOptionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
