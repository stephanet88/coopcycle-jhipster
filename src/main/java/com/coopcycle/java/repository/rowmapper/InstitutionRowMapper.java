package com.coopcycle.java.repository.rowmapper;

import com.coopcycle.java.domain.Institution;
import com.coopcycle.java.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Institution}, with proper type conversions.
 */
@Service
public class InstitutionRowMapper implements BiFunction<Row, String, Institution> {

    private final ColumnConverter converter;

    public InstitutionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Institution} stored in the database.
     */
    @Override
    public Institution apply(Row row, String prefix) {
        Institution entity = new Institution();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setType(converter.fromRow(row, prefix + "_type", String.class));
        entity.setUserAccountId(converter.fromRow(row, prefix + "_user_account_id", Long.class));
        entity.setCooperativeId(converter.fromRow(row, prefix + "_cooperative_id", Long.class));
        return entity;
    }
}
