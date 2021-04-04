package com.coopcycle.java.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.coopcycle.java.IntegrationTest;
import com.coopcycle.java.domain.UserAccount;
import com.coopcycle.java.repository.UserAccountRepository;
import com.coopcycle.java.service.EntityManager;
import com.coopcycle.java.service.dto.UserAccountDTO;
import com.coopcycle.java.service.mapper.UserAccountMapper;
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
 * Integration tests for the {@link UserAccountResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class UserAccountResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_AGE = 2;
    private static final Integer UPDATED_AGE = 3;

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/user-accounts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private UserAccount userAccount;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserAccount createEntity(EntityManager em) {
        UserAccount userAccount = new UserAccount().name(DEFAULT_NAME).age(DEFAULT_AGE).type(DEFAULT_TYPE);
        return userAccount;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserAccount createUpdatedEntity(EntityManager em) {
        UserAccount userAccount = new UserAccount().name(UPDATED_NAME).age(UPDATED_AGE).type(UPDATED_TYPE);
        return userAccount;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(UserAccount.class).block();
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
        userAccount = createEntity(em);
    }

    @Test
    void createUserAccount() throws Exception {
        int databaseSizeBeforeCreate = userAccountRepository.findAll().collectList().block().size();
        // Create the UserAccount
        UserAccountDTO userAccountDTO = userAccountMapper.toDto(userAccount);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userAccountDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the UserAccount in the database
        List<UserAccount> userAccountList = userAccountRepository.findAll().collectList().block();
        assertThat(userAccountList).hasSize(databaseSizeBeforeCreate + 1);
        UserAccount testUserAccount = userAccountList.get(userAccountList.size() - 1);
        assertThat(testUserAccount.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testUserAccount.getAge()).isEqualTo(DEFAULT_AGE);
        assertThat(testUserAccount.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    void createUserAccountWithExistingId() throws Exception {
        // Create the UserAccount with an existing ID
        userAccount.setId(1L);
        UserAccountDTO userAccountDTO = userAccountMapper.toDto(userAccount);

        int databaseSizeBeforeCreate = userAccountRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userAccountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserAccount in the database
        List<UserAccount> userAccountList = userAccountRepository.findAll().collectList().block();
        assertThat(userAccountList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = userAccountRepository.findAll().collectList().block().size();
        // set the field null
        userAccount.setName(null);

        // Create the UserAccount, which fails.
        UserAccountDTO userAccountDTO = userAccountMapper.toDto(userAccount);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userAccountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<UserAccount> userAccountList = userAccountRepository.findAll().collectList().block();
        assertThat(userAccountList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = userAccountRepository.findAll().collectList().block().size();
        // set the field null
        userAccount.setType(null);

        // Create the UserAccount, which fails.
        UserAccountDTO userAccountDTO = userAccountMapper.toDto(userAccount);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userAccountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<UserAccount> userAccountList = userAccountRepository.findAll().collectList().block();
        assertThat(userAccountList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllUserAccountsAsStream() {
        // Initialize the database
        userAccountRepository.save(userAccount).block();

        List<UserAccount> userAccountList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(UserAccountDTO.class)
            .getResponseBody()
            .map(userAccountMapper::toEntity)
            .filter(userAccount::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(userAccountList).isNotNull();
        assertThat(userAccountList).hasSize(1);
        UserAccount testUserAccount = userAccountList.get(0);
        assertThat(testUserAccount.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testUserAccount.getAge()).isEqualTo(DEFAULT_AGE);
        assertThat(testUserAccount.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    void getAllUserAccounts() {
        // Initialize the database
        userAccountRepository.save(userAccount).block();

        // Get all the userAccountList
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
            .value(hasItem(userAccount.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].age")
            .value(hasItem(DEFAULT_AGE))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE));
    }

    @Test
    void getUserAccount() {
        // Initialize the database
        userAccountRepository.save(userAccount).block();

        // Get the userAccount
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, userAccount.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(userAccount.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.age")
            .value(is(DEFAULT_AGE))
            .jsonPath("$.type")
            .value(is(DEFAULT_TYPE));
    }

    @Test
    void getNonExistingUserAccount() {
        // Get the userAccount
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewUserAccount() throws Exception {
        // Initialize the database
        userAccountRepository.save(userAccount).block();

        int databaseSizeBeforeUpdate = userAccountRepository.findAll().collectList().block().size();

        // Update the userAccount
        UserAccount updatedUserAccount = userAccountRepository.findById(userAccount.getId()).block();
        updatedUserAccount.name(UPDATED_NAME).age(UPDATED_AGE).type(UPDATED_TYPE);
        UserAccountDTO userAccountDTO = userAccountMapper.toDto(updatedUserAccount);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, userAccountDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userAccountDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserAccount in the database
        List<UserAccount> userAccountList = userAccountRepository.findAll().collectList().block();
        assertThat(userAccountList).hasSize(databaseSizeBeforeUpdate);
        UserAccount testUserAccount = userAccountList.get(userAccountList.size() - 1);
        assertThat(testUserAccount.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUserAccount.getAge()).isEqualTo(UPDATED_AGE);
        assertThat(testUserAccount.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    void putNonExistingUserAccount() throws Exception {
        int databaseSizeBeforeUpdate = userAccountRepository.findAll().collectList().block().size();
        userAccount.setId(count.incrementAndGet());

        // Create the UserAccount
        UserAccountDTO userAccountDTO = userAccountMapper.toDto(userAccount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, userAccountDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userAccountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserAccount in the database
        List<UserAccount> userAccountList = userAccountRepository.findAll().collectList().block();
        assertThat(userAccountList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchUserAccount() throws Exception {
        int databaseSizeBeforeUpdate = userAccountRepository.findAll().collectList().block().size();
        userAccount.setId(count.incrementAndGet());

        // Create the UserAccount
        UserAccountDTO userAccountDTO = userAccountMapper.toDto(userAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userAccountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserAccount in the database
        List<UserAccount> userAccountList = userAccountRepository.findAll().collectList().block();
        assertThat(userAccountList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamUserAccount() throws Exception {
        int databaseSizeBeforeUpdate = userAccountRepository.findAll().collectList().block().size();
        userAccount.setId(count.incrementAndGet());

        // Create the UserAccount
        UserAccountDTO userAccountDTO = userAccountMapper.toDto(userAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userAccountDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserAccount in the database
        List<UserAccount> userAccountList = userAccountRepository.findAll().collectList().block();
        assertThat(userAccountList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateUserAccountWithPatch() throws Exception {
        // Initialize the database
        userAccountRepository.save(userAccount).block();

        int databaseSizeBeforeUpdate = userAccountRepository.findAll().collectList().block().size();

        // Update the userAccount using partial update
        UserAccount partialUpdatedUserAccount = new UserAccount();
        partialUpdatedUserAccount.setId(userAccount.getId());

        partialUpdatedUserAccount.name(UPDATED_NAME).age(UPDATED_AGE).type(UPDATED_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserAccount.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUserAccount))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserAccount in the database
        List<UserAccount> userAccountList = userAccountRepository.findAll().collectList().block();
        assertThat(userAccountList).hasSize(databaseSizeBeforeUpdate);
        UserAccount testUserAccount = userAccountList.get(userAccountList.size() - 1);
        assertThat(testUserAccount.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUserAccount.getAge()).isEqualTo(UPDATED_AGE);
        assertThat(testUserAccount.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    void fullUpdateUserAccountWithPatch() throws Exception {
        // Initialize the database
        userAccountRepository.save(userAccount).block();

        int databaseSizeBeforeUpdate = userAccountRepository.findAll().collectList().block().size();

        // Update the userAccount using partial update
        UserAccount partialUpdatedUserAccount = new UserAccount();
        partialUpdatedUserAccount.setId(userAccount.getId());

        partialUpdatedUserAccount.name(UPDATED_NAME).age(UPDATED_AGE).type(UPDATED_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserAccount.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUserAccount))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserAccount in the database
        List<UserAccount> userAccountList = userAccountRepository.findAll().collectList().block();
        assertThat(userAccountList).hasSize(databaseSizeBeforeUpdate);
        UserAccount testUserAccount = userAccountList.get(userAccountList.size() - 1);
        assertThat(testUserAccount.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUserAccount.getAge()).isEqualTo(UPDATED_AGE);
        assertThat(testUserAccount.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    void patchNonExistingUserAccount() throws Exception {
        int databaseSizeBeforeUpdate = userAccountRepository.findAll().collectList().block().size();
        userAccount.setId(count.incrementAndGet());

        // Create the UserAccount
        UserAccountDTO userAccountDTO = userAccountMapper.toDto(userAccount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, userAccountDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(userAccountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserAccount in the database
        List<UserAccount> userAccountList = userAccountRepository.findAll().collectList().block();
        assertThat(userAccountList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchUserAccount() throws Exception {
        int databaseSizeBeforeUpdate = userAccountRepository.findAll().collectList().block().size();
        userAccount.setId(count.incrementAndGet());

        // Create the UserAccount
        UserAccountDTO userAccountDTO = userAccountMapper.toDto(userAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(userAccountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserAccount in the database
        List<UserAccount> userAccountList = userAccountRepository.findAll().collectList().block();
        assertThat(userAccountList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamUserAccount() throws Exception {
        int databaseSizeBeforeUpdate = userAccountRepository.findAll().collectList().block().size();
        userAccount.setId(count.incrementAndGet());

        // Create the UserAccount
        UserAccountDTO userAccountDTO = userAccountMapper.toDto(userAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(userAccountDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserAccount in the database
        List<UserAccount> userAccountList = userAccountRepository.findAll().collectList().block();
        assertThat(userAccountList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteUserAccount() {
        // Initialize the database
        userAccountRepository.save(userAccount).block();

        int databaseSizeBeforeDelete = userAccountRepository.findAll().collectList().block().size();

        // Delete the userAccount
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, userAccount.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<UserAccount> userAccountList = userAccountRepository.findAll().collectList().block();
        assertThat(userAccountList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
