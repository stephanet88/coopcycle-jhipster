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
 * A UserAccount.
 */
@Table("user_account")
public class UserAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @Min(value = 2)
    @Column("age")
    private Integer age;

    @NotNull(message = "must not be null")
    @Column("type")
    private String type;

    @Transient
    @JsonIgnoreProperties(value = { "order", "paymentOption", "userAccount", "institutions" }, allowSetters = true)
    private Set<Cart> carts = new HashSet<>();

    @Transient
    private Institution institution;

    @Transient
    private Cooperative cooperative;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserAccount id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public UserAccount name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return this.age;
    }

    public UserAccount age(Integer age) {
        this.age = age;
        return this;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getType() {
        return this.type;
    }

    public UserAccount type(String type) {
        this.type = type;
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<Cart> getCarts() {
        return this.carts;
    }

    public UserAccount carts(Set<Cart> carts) {
        this.setCarts(carts);
        return this;
    }

    public UserAccount addCart(Cart cart) {
        this.carts.add(cart);
        cart.setUserAccount(this);
        return this;
    }

    public UserAccount removeCart(Cart cart) {
        this.carts.remove(cart);
        cart.setUserAccount(null);
        return this;
    }

    public void setCarts(Set<Cart> carts) {
        if (this.carts != null) {
            this.carts.forEach(i -> i.setUserAccount(null));
        }
        if (carts != null) {
            carts.forEach(i -> i.setUserAccount(this));
        }
        this.carts = carts;
    }

    public Institution getInstitution() {
        return this.institution;
    }

    public UserAccount institution(Institution institution) {
        this.setInstitution(institution);
        return this;
    }

    public void setInstitution(Institution institution) {
        if (this.institution != null) {
            this.institution.setUserAccount(null);
        }
        if (institution != null) {
            institution.setUserAccount(this);
        }
        this.institution = institution;
    }

    public Cooperative getCooperative() {
        return this.cooperative;
    }

    public UserAccount cooperative(Cooperative cooperative) {
        this.setCooperative(cooperative);
        return this;
    }

    public void setCooperative(Cooperative cooperative) {
        if (this.cooperative != null) {
            this.cooperative.setUserAccount(null);
        }
        if (cooperative != null) {
            cooperative.setUserAccount(this);
        }
        this.cooperative = cooperative;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserAccount)) {
            return false;
        }
        return id != null && id.equals(((UserAccount) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserAccount{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", age=" + getAge() +
            ", type='" + getType() + "'" +
            "}";
    }
}
