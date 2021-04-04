package com.coopcycle.java.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A PaymentOption.
 */
@Table("payment_option")
public class PaymentOption implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("type")
    private String type;

    private Long cartId;

    @Transient
    private Cart cart;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PaymentOption id(Long id) {
        this.id = id;
        return this;
    }

    public String getType() {
        return this.type;
    }

    public PaymentOption type(String type) {
        this.type = type;
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Cart getCart() {
        return this.cart;
    }

    public PaymentOption cart(Cart cart) {
        this.setCart(cart);
        this.cartId = cart != null ? cart.getId() : null;
        return this;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
        this.cartId = cart != null ? cart.getId() : null;
    }

    public Long getCartId() {
        return this.cartId;
    }

    public void setCartId(Long cart) {
        this.cartId = cart;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentOption)) {
            return false;
        }
        return id != null && id.equals(((PaymentOption) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentOption{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            "}";
    }
}
