package com.coopcycle.java.service.mapper;

import com.coopcycle.java.domain.*;
import com.coopcycle.java.service.dto.CartDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Cart} and its DTO {@link CartDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserAccountMapper.class })
public interface CartMapper extends EntityMapper<CartDTO, Cart> {
    @Mapping(target = "userAccount", source = "userAccount", qualifiedByName = "id")
    CartDTO toDto(Cart s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CartDTO toDtoId(Cart cart);

    @Named("idSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    Set<CartDTO> toDtoIdSet(Set<Cart> cart);
}
