package com.coopcycle.java.service.mapper;

import com.coopcycle.java.domain.*;
import com.coopcycle.java.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring", uses = { InstitutionMapper.class })
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {
    @Mapping(target = "institution", source = "institution", qualifiedByName = "id")
    ProductDTO toDto(Product s);
}
