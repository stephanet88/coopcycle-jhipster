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
 * A Cooperative.
 */
@Table("cooperative")
public class Cooperative implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Column("location")
    private String location;

    private Long userAccountId;

    @Transient
    private UserAccount userAccount;

    @Transient
    @JsonIgnoreProperties(value = { "userAccount", "products", "carts", "cooperative" }, allowSetters = true)
    private Set<Institution> institutions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cooperative id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Cooperative name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return this.location;
    }

    public Cooperative location(String location) {
        this.location = location;
        return this;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public UserAccount getUserAccount() {
        return this.userAccount;
    }

    public Cooperative userAccount(UserAccount userAccount) {
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

    public Cooperative institutions(Set<Institution> institutions) {
        this.setInstitutions(institutions);
        return this;
    }

    public Cooperative addInstitution(Institution institution) {
        this.institutions.add(institution);
        institution.setCooperative(this);
        return this;
    }

    public Cooperative removeInstitution(Institution institution) {
        this.institutions.remove(institution);
        institution.setCooperative(null);
        return this;
    }

    public void setInstitutions(Set<Institution> institutions) {
        if (this.institutions != null) {
            this.institutions.forEach(i -> i.setCooperative(null));
        }
        if (institutions != null) {
            institutions.forEach(i -> i.setCooperative(this));
        }
        this.institutions = institutions;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cooperative)) {
            return false;
        }
        return id != null && id.equals(((Cooperative) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Cooperative{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", location='" + getLocation() + "'" +
            "}";
    }
}
