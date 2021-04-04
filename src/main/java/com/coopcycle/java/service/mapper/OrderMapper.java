package com.coopcycle.java.service.mapper;

import com.coopcycle.java.domain.*;
import com.coopcycle.java.service.dto.OrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Order} and its DTO {@link OrderDTO}.
 */
@Mapper(componentModel = "spring", uses = { CartMapper.class })
public interface OrderMapper extends EntityMapper<OrderDTO, Order> {
    @Mapping(target = "cart", source = "cart", qualifiedByName = "id")
    OrderDTO toDto(Order s);
}
