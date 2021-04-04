package com.coopcycle.java.service.mapper;

import com.coopcycle.java.domain.*;
import com.coopcycle.java.service.dto.CooperativeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Cooperative} and its DTO {@link CooperativeDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserAccountMapper.class })
public interface CooperativeMapper extends EntityMapper<CooperativeDTO, Cooperative> {
    @Mapping(target = "userAccount", source = "userAccount", qualifiedByName = "id")
    CooperativeDTO toDto(Cooperative s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CooperativeDTO toDtoId(Cooperative cooperative);
}
