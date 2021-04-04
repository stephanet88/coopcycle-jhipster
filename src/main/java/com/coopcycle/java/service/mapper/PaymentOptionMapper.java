package com.coopcycle.java.service.mapper;

import com.coopcycle.java.domain.*;
import com.coopcycle.java.service.dto.PaymentOptionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PaymentOption} and its DTO {@link PaymentOptionDTO}.
 */
@Mapper(componentModel = "spring", uses = { CartMapper.class })
public interface PaymentOptionMapper extends EntityMapper<PaymentOptionDTO, PaymentOption> {
    @Mapping(target = "cart", source = "cart", qualifiedByName = "id")
    PaymentOptionDTO toDto(PaymentOption s);
}
