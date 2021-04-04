package com.coopcycle.java.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.coopcycle.java.IntegrationTest;
import com.coopcycle.java.domain.Cart;
import com.coopcycle.java.repository.CartRepository;
import com.coopcycle.java.service.EntityManager;
import com.coopcycle.java.service.dto.CartDTO;
import com.coopcycle.java.service.mapper.CartMapper;
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
 * Integration tests for the {@link CartResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class CartResourceIT {

    private static final Integer DEFAULT_NUMBER_OF_PRODUCTS = 1;
    private static final Integer UPDATED_NUMBER_OF_PRODUCTS = 2;

    private static final String ENTITY_API_URL = "/api/carts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Cart cart;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cart createEntity(EntityManager em) {
        Cart cart = new Cart().numberOfProducts(DEFAULT_NUMBER_OF_PRODUCTS);
        return cart;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cart createUpdatedEntity(EntityManager em) {
        Cart cart = new Cart().numberOfProducts(UPDATED_NUMBER_OF_PRODUCTS);
        return cart;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Cart.class).block();
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
        cart = createEntity(em);
    }

    @Test
    void createCart() throws Exception {
        int databaseSizeBeforeCreate = cartRepository.findAll().collectList().block().size();
        // Create the Cart
        CartDTO cartDTO = cartMapper.toDto(cart);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeCreate + 1);
        Cart testCart = cartList.get(cartList.size() - 1);
        assertThat(testCart.getNumberOfProducts()).isEqualTo(DEFAULT_NUMBER_OF_PRODUCTS);
    }

    @Test
    void createCartWithExistingId() throws Exception {
        // Create the Cart with an existing ID
        cart.setId(1L);
        CartDTO cartDTO = cartMapper.toDto(cart);

        int databaseSizeBeforeCreate = cartRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCartsAsStream() {
        // Initialize the database
        cartRepository.save(cart).block();

        List<Cart> cartList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(CartDTO.class)
            .getResponseBody()
            .map(cartMapper::toEntity)
            .filter(cart::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(cartList).isNotNull();
        assertThat(cartList).hasSize(1);
        Cart testCart = cartList.get(0);
        assertThat(testCart.getNumberOfProducts()).isEqualTo(DEFAULT_NUMBER_OF_PRODUCTS);
    }

    @Test
    void getAllCarts() {
        // Initialize the database
        cartRepository.save(cart).block();

        // Get all the cartList
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
            .value(hasItem(cart.getId().intValue()))
            .jsonPath("$.[*].numberOfProducts")
            .value(hasItem(DEFAULT_NUMBER_OF_PRODUCTS));
    }

    @Test
    void getCart() {
        // Initialize the database
        cartRepository.save(cart).block();

        // Get the cart
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, cart.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(cart.getId().intValue()))
            .jsonPath("$.numberOfProducts")
            .value(is(DEFAULT_NUMBER_OF_PRODUCTS));
    }

    @Test
    void getNonExistingCart() {
        // Get the cart
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCart() throws Exception {
        // Initialize the database
        cartRepository.save(cart).block();

        int databaseSizeBeforeUpdate = cartRepository.findAll().collectList().block().size();

        // Update the cart
        Cart updatedCart = cartRepository.findById(cart.getId()).block();
        updatedCart.numberOfProducts(UPDATED_NUMBER_OF_PRODUCTS);
        CartDTO cartDTO = cartMapper.toDto(updatedCart);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, cartDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeUpdate);
        Cart testCart = cartList.get(cartList.size() - 1);
        assertThat(testCart.getNumberOfProducts()).isEqualTo(UPDATED_NUMBER_OF_PRODUCTS);
    }

    @Test
    void putNonExistingCart() throws Exception {
        int databaseSizeBeforeUpdate = cartRepository.findAll().collectList().block().size();
        cart.setId(count.incrementAndGet());

        // Create the Cart
        CartDTO cartDTO = cartMapper.toDto(cart);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, cartDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCart() throws Exception {
        int databaseSizeBeforeUpdate = cartRepository.findAll().collectList().block().size();
        cart.setId(count.incrementAndGet());

        // Create the Cart
        CartDTO cartDTO = cartMapper.toDto(cart);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCart() throws Exception {
        int databaseSizeBeforeUpdate = cartRepository.findAll().collectList().block().size();
        cart.setId(count.incrementAndGet());

        // Create the Cart
        CartDTO cartDTO = cartMapper.toDto(cart);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCartWithPatch() throws Exception {
        // Initialize the database
        cartRepository.save(cart).block();

        int databaseSizeBeforeUpdate = cartRepository.findAll().collectList().block().size();

        // Update the cart using partial update
        Cart partialUpdatedCart = new Cart();
        partialUpdatedCart.setId(cart.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCart.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCart))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeUpdate);
        Cart testCart = cartList.get(cartList.size() - 1);
        assertThat(testCart.getNumberOfProducts()).isEqualTo(DEFAULT_NUMBER_OF_PRODUCTS);
    }

    @Test
    void fullUpdateCartWithPatch() throws Exception {
        // Initialize the database
        cartRepository.save(cart).block();

        int databaseSizeBeforeUpdate = cartRepository.findAll().collectList().block().size();

        // Update the cart using partial update
        Cart partialUpdatedCart = new Cart();
        partialUpdatedCart.setId(cart.getId());

        partialUpdatedCart.numberOfProducts(UPDATED_NUMBER_OF_PRODUCTS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCart.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCart))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeUpdate);
        Cart testCart = cartList.get(cartList.size() - 1);
        assertThat(testCart.getNumberOfProducts()).isEqualTo(UPDATED_NUMBER_OF_PRODUCTS);
    }

    @Test
    void patchNonExistingCart() throws Exception {
        int databaseSizeBeforeUpdate = cartRepository.findAll().collectList().block().size();
        cart.setId(count.incrementAndGet());

        // Create the Cart
        CartDTO cartDTO = cartMapper.toDto(cart);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, cartDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCart() throws Exception {
        int databaseSizeBeforeUpdate = cartRepository.findAll().collectList().block().size();
        cart.setId(count.incrementAndGet());

        // Create the Cart
        CartDTO cartDTO = cartMapper.toDto(cart);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCart() throws Exception {
        int databaseSizeBeforeUpdate = cartRepository.findAll().collectList().block().size();
        cart.setId(count.incrementAndGet());

        // Create the Cart
        CartDTO cartDTO = cartMapper.toDto(cart);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCart() {
        // Initialize the database
        cartRepository.save(cart).block();

        int databaseSizeBeforeDelete = cartRepository.findAll().collectList().block().size();

        // Delete the cart
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, cart.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
