package com.coopcycle.java.repository.rowmapper;

import com.coopcycle.java.domain.UserAccount;
import com.coopcycle.java.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link UserAccount}, with proper type conversions.
 */
@Service
public class UserAccountRowMapper implements BiFunction<Row, String, UserAccount> {

    private final ColumnConverter converter;

    public UserAccountRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link UserAccount} stored in the database.
     */
    @Override
    public UserAccount apply(Row row, String prefix) {
        UserAccount entity = new UserAccount();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setAge(converter.fromRow(row, prefix + "_age", Integer.class));
        entity.setType(converter.fromRow(row, prefix + "_type", String.class));
        return entity;
    }
}
