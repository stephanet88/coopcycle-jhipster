package com.coopcycle.java.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.coopcycle.java.domain.Institution} entity.
 */
public class InstitutionDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String name;

    private String type;

    private UserAccountDTO userAccount;

    private Set<CartDTO> carts = new HashSet<>();

    private CooperativeDTO cooperative;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserAccountDTO getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccountDTO userAccount) {
        this.userAccount = userAccount;
    }

    public Set<CartDTO> getCarts() {
        return carts;
    }

    public void setCarts(Set<CartDTO> carts) {
        this.carts = carts;
    }

    public CooperativeDTO getCooperative() {
        return cooperative;
    }

    public void setCooperative(CooperativeDTO cooperative) {
        this.cooperative = cooperative;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InstitutionDTO)) {
            return false;
        }

        InstitutionDTO institutionDTO = (InstitutionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, institutionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InstitutionDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", type='" + getType() + "'" +
            ", userAccount=" + getUserAccount() +
            ", carts=" + getCarts() +
            ", cooperative=" + getCooperative() +
            "}";
    }
}
