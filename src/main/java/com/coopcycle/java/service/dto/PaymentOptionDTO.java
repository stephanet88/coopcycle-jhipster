package com.coopcycle.java.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.coopcycle.java.domain.PaymentOption} entity.
 */
public class PaymentOptionDTO implements Serializable {

    private Long id;

    private String type;

    private CartDTO cart;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CartDTO getCart() {
        return cart;
    }

    public void setCart(CartDTO cart) {
        this.cart = cart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentOptionDTO)) {
            return false;
        }

        PaymentOptionDTO paymentOptionDTO = (PaymentOptionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, paymentOptionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaymentOptionDTO{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", cart=" + getCart() +
            "}";
    }
}
