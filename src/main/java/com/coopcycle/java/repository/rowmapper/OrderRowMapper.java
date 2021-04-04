package com.coopcycle.java.repository.rowmapper;

import com.coopcycle.java.domain.Order;
import com.coopcycle.java.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Order}, with proper type conversions.
 */
@Service
public class OrderRowMapper implements BiFunction<Row, String, Order> {

    private final ColumnConverter converter;

    public OrderRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Order} stored in the database.
     */
    @Override
    public Order apply(Row row, String prefix) {
        Order entity = new Order();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", String.class));
        entity.setOrderTime(converter.fromRow(row, prefix + "_order_time", ZonedDateTime.class));
        entity.setEstimatedDeliveryTime(converter.fromRow(row, prefix + "_estimated_delivery_time", ZonedDateTime.class));
        entity.setRealDeliveryTime(converter.fromRow(row, prefix + "_real_delivery_time", ZonedDateTime.class));
        entity.setCartId(converter.fromRow(row, prefix + "_cart_id", Long.class));
        return entity;
    }
}
