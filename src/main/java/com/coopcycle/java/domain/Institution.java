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
 * A Institution.
 */
@Table("institution")
public class Institution implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @Column("type")
    private String type;

    private Long userAccountId;

    @Transient
    private UserAccount userAccount;

    @Transient
    @JsonIgnoreProperties(value = { "institution" }, allowSetters = true)
    private Set<Product> products = new HashSet<>();

    @JsonIgnoreProperties(value = { "order", "paymentOption", "userAccount", "institutions" }, allowSetters = true)
    @Transient
    private Set<Cart> carts = new HashSet<>();

    @JsonIgnoreProperties(value = { "userAccount", "institutions" }, allowSetters = true)
    @Transient
    private Cooperative cooperative;

    @Column("cooperative_id")
    private Long cooperativeId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Institution id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Institution name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public Institution type(String type) {
        this.type = type;
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserAccount getUserAccount() {
        return this.userAccount;
    }

    public Institution userAccount(UserAccount userAccount) {
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

    public Set<Product> getProducts() {
        return this.products;
    }

    public Institution products(Set<Product> products) {
        this.setProducts(products);
        return this;
    }

    public Institution addProduct(Product product) {
        this.products.add(product);
        product.setInstitution(this);
        return this;
    }

    public Institution removeProduct(Product product) {
        this.products.remove(product);
        product.setInstitution(null);
        return this;
    }

    public void setProducts(Set<Product> products) {
        if (this.products != null) {
            this.products.forEach(i -> i.setInstitution(null));
        }
        if (products != null) {
            products.forEach(i -> i.setInstitution(this));
        }
        this.products = products;
    }

    public Set<Cart> getCarts() {
        return this.carts;
    }

    public Institution carts(Set<Cart> carts) {
        this.setCarts(carts);
        return this;
    }

    public Institution addCart(Cart cart) {
        this.carts.add(cart);
        cart.getInstitutions().add(this);
        return this;
    }

    public Institution removeCart(Cart cart) {
        this.carts.remove(cart);
        cart.getInstitutions().remove(this);
        return this;
    }

    public void setCarts(Set<Cart> carts) {
        this.carts = carts;
    }

    public Cooperative getCooperative() {
        return this.cooperative;
    }

    public Institution cooperative(Cooperative cooperative) {
        this.setCooperative(cooperative);
        this.cooperativeId = cooperative != null ? cooperative.getId() : null;
        return this;
    }

    public void setCooperative(Cooperative cooperative) {
        this.cooperative = cooperative;
        this.cooperativeId = cooperative != null ? cooperative.getId() : null;
    }

    public Long getCooperativeId() {
        return this.cooperativeId;
    }

    public void setCooperativeId(Long cooperative) {
        this.cooperativeId = cooperative;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Institution)) {
            return false;
        }
        return id != null && id.equals(((Institution) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Institution{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
