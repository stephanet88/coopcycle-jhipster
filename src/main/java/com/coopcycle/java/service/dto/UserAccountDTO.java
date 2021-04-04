package com.coopcycle.java.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.coopcycle.java.domain.UserAccount} entity.
 */
public class UserAccountDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String name;

    @Min(value = 2)
    private Integer age;

    @NotNull(message = "must not be null")
    private String type;

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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserAccountDTO)) {
            return false;
        }

        UserAccountDTO userAccountDTO = (UserAccountDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userAccountDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserAccountDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", age=" + getAge() +
            ", type='" + getType() + "'" +
            "}";
    }
}
