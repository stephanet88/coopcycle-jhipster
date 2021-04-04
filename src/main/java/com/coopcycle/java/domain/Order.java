package com.coopcycle.java.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Order.
 */
@Table("jhi_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull(message = "must not be null")
    @Column("status")
    private String status;

    @Column("order_time")
    private ZonedDateTime orderTime;

    @NotNull(message = "must not be null")
    @Column("estimated_delivery_time")
    private ZonedDateTime estimatedDeliveryTime;

    @Column("real_delivery_time")
    private ZonedDateTime realDeliveryTime;

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

    public Order id(Long id) {
        this.id = id;
        return this;
    }

    public String getStatus() {
        return this.status;
    }

    public Order status(String status) {
        this.status = status;
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ZonedDateTime getOrderTime() {
        return this.orderTime;
    }

    public Order orderTime(ZonedDateTime orderTime) {
        this.orderTime = orderTime;
        return this;
    }

    public void setOrderTime(ZonedDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public ZonedDateTime getEstimatedDeliveryTime() {
        return this.estimatedDeliveryTime;
    }

    public Order estimatedDeliveryTime(ZonedDateTime estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
        return this;
    }

    public void setEstimatedDeliveryTime(ZonedDateTime estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public ZonedDateTime getRealDeliveryTime() {
        return this.realDeliveryTime;
    }

    public Order realDeliveryTime(ZonedDateTime realDeliveryTime) {
        this.realDeliveryTime = realDeliveryTime;
        return this;
    }

    public void setRealDeliveryTime(ZonedDateTime realDeliveryTime) {
        this.realDeliveryTime = realDeliveryTime;
    }

    public Cart getCart() {
        return this.cart;
    }

    public Order cart(Cart cart) {
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
        if (!(o instanceof Order)) {
            return false;
        }
        return id != null && id.equals(((Order) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Order{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", orderTime='" + getOrderTime() + "'" +
            ", estimatedDeliveryTime='" + getEstimatedDeliveryTime() + "'" +
            ", realDeliveryTime='" + getRealDeliveryTime() + "'" +
            "}";
    }
}
