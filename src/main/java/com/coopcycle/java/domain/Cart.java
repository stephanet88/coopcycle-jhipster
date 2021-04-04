package com.coopcycle.java.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Cart.
 */
@Table("cart")
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Min(value = 1)
    @Column("number_of_products")
    private Integer numberOfProducts;

    @Transient
    private Order order;

    @Transient
    private PaymentOption paymentOption;

    @JsonIgnoreProperties(value = { "carts", "institution", "cooperative" }, allowSetters = true)
    @Transient
    private UserAccount userAccount;

    @Column("user_account_id")
    private Long userAccountId;

    @JsonIgnoreProperties(value = { "userAccount", "products", "carts", "cooperative" }, allowSetters = true)
    @Transient
    private Set<Institution> institutions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cart id(Long id) {
        this.id = id;
        return this;
    }

    public Integer getNumberOfProducts() {
        return this.numberOfProducts;
    }

    public Cart numberOfProducts(Integer numberOfProducts) {
        this.numberOfProducts = numberOfProducts;
        return this;
    }

    public void setNumberOfProducts(Integer numberOfProducts) {
        this.numberOfProducts = numberOfProducts;
    }

    public Order getOrder() {
        return this.order;
    }

    public Cart order(Order order) {
        this.setOrder(order);
        return this;
    }

    public void setOrder(Order order) {
        if (this.order != null) {
            this.order.setCart(null);
        }
        if (order != null) {
            order.setCart(this);
        }
        this.order = order;
    }

    public PaymentOption getPaymentOption() {
        return this.paymentOption;
    }

    public Cart paymentOption(PaymentOption paymentOption) {
        this.setPaymentOption(paymentOption);
        return this;
    }

    public void setPaymentOption(PaymentOption paymentOption) {
        if (this.paymentOption != null) {
            this.paymentOption.setCart(null);
        }
        if (paymentOption != null) {
            paymentOption.setCart(this);
        }
        this.paymentOption = paymentOption;
    }

    public UserAccount getUserAccount() {
        return this.userAccount;
    }

    public Cart userAccount(UserAccount userAccount) {
        this.setUserAccount(userAccount);
        this.userAccountId = userAccount != null ? userAccount.getId() : null;
        return this;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
        this.userAccountId = userAccount != null ? userAccount.getId() : null;
    }

    public Long getUserAccountId() {
        return this.userAccountId;
    }

    public void setUserAccountId(Long userAccount) {
        this.userAccountId = userAccount;
    }

    public Set<Institution> getInstitutions() {
        return this.institutions;
    }

    public Cart institutions(Set<Institution> institutions) {
        this.setInstitutions(institutions);
        return this;
    }

    public Cart addInstitution(Institution institution) {
        this.institutions.add(institution);
        institution.getCarts().add(this);
        return this;
    }

    public Cart removeInstitution(Institution institution) {
        this.institutions.remove(institution);
        institution.getCarts().remove(this);
        return this;
    }

    public void setInstitutions(Set<Institution> institutions) {
        if (this.institutions != null) {
            this.institutions.forEach(i -> i.removeCart(this));
        }
        if (institutions != null) {
            institutions.forEach(i -> i.addCart(this));
        }
        this.institutions = institutions;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cart)) {
            return false;
        }
        return id != null && id.equals(((Cart) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Cart{" +
            "id=" + getId() +
            ", numberOfProducts=" + getNumberOfProducts() +
            "}";
    }
}
