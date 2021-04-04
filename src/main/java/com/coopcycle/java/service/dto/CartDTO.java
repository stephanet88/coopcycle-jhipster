package com.coopcycle.java.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.coopcycle.java.domain.Cart} entity.
 */
public class CartDTO implements Serializable {

    private Long id;

    @Min(value = 1)
    private Integer numberOfProducts;

    private UserAccountDTO userAccount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumberOfProducts() {
        return numberOfProducts;
    }

    public void setNumberOfProducts(Integer numberOfProducts) {
        this.numberOfProducts = numberOfProducts;
    }

    public UserAccountDTO getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccountDTO userAccount) {
        this.userAccount = userAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CartDTO)) {
            return false;
        }

        CartDTO cartDTO = (CartDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cartDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CartDTO{" +
            "id=" + getId() +
            ", numberOfProducts=" + getNumberOfProducts() +
            ", userAccount=" + getUserAccount() +
            "}";
    }
}
