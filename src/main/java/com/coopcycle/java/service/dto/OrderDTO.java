package com.coopcycle.java.service.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.coopcycle.java.domain.Order} entity.
 */
public class OrderDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private String status;

    private ZonedDateTime orderTime;

    @NotNull(message = "must not be null")
    private ZonedDateTime estimatedDeliveryTime;

    private ZonedDateTime realDeliveryTime;

    private CartDTO cart;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ZonedDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(ZonedDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public ZonedDateTime getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(ZonedDateTime estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public ZonedDateTime getRealDeliveryTime() {
        return realDeliveryTime;
    }

    public void setRealDeliveryTime(ZonedDateTime realDeliveryTime) {
        this.realDeliveryTime = realDeliveryTime;
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
        if (!(o instanceof OrderDTO)) {
            return false;
        }

        OrderDTO orderDTO = (OrderDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orderDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", orderTime='" + getOrderTime() + "'" +
            ", estimatedDeliveryTime='" + getEstimatedDeliveryTime() + "'" +
            ", realDeliveryTime='" + getRealDeliveryTime() + "'" +
            ", cart=" + getCart() +
            "}";
    }
}
