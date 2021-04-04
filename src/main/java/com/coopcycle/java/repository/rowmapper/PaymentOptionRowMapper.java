package com.coopcycle.java.repository.rowmapper;

import com.coopcycle.java.domain.PaymentOption;
import com.coopcycle.java.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link PaymentOption}, with proper type conversions.
 */
@Service
public class PaymentOptionRowMapper implements BiFunction<Row, String, PaymentOption> {

    private final ColumnConverter converter;

    public PaymentOptionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link PaymentOption} stored in the database.
     */
    @Override
    public PaymentOption apply(Row row, String prefix) {
        PaymentOption entity = new PaymentOption();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setType(converter.fromRow(row, prefix + "_type", String.class));
        entity.setCartId(converter.fromRow(row, prefix + "_cart_id", Long.class));
        return entity;
    }
}
