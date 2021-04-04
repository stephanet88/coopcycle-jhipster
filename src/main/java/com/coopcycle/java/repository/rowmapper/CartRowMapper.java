package com.coopcycle.java.repository.rowmapper;

import com.coopcycle.java.domain.Cart;
import com.coopcycle.java.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Cart}, with proper type conversions.
 */
@Service
public class CartRowMapper implements BiFunction<Row, String, Cart> {

    private final ColumnConverter converter;

    public CartRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Cart} stored in the database.
     */
    @Override
    public Cart apply(Row row, String prefix) {
        Cart entity = new Cart();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNumberOfProducts(converter.fromRow(row, prefix + "_number_of_products", Integer.class));
        entity.setUserAccountId(converter.fromRow(row, prefix + "_user_account_id", Long.class));
        return entity;
    }
}
