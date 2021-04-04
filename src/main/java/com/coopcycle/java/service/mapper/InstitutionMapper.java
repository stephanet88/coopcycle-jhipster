package com.coopcycle.java.service.mapper;

import com.coopcycle.java.domain.*;
import com.coopcycle.java.service.dto.InstitutionDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Institution} and its DTO {@link InstitutionDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserAccountMapper.class, CartMapper.class, CooperativeMapper.class })
public interface InstitutionMapper extends EntityMapper<InstitutionDTO, Institution> {
    @Mapping(target = "userAccount", source = "userAccount", qualifiedByName = "id")
    @Mapping(target = "carts", source = "carts", qualifiedByName = "idSet")
    @Mapping(target = "cooperative", source = "cooperative", qualifiedByName = "id")
    InstitutionDTO toDto(Institution s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    InstitutionDTO toDtoId(Institution institution);

    @Mapping(target = "removeCart", ignore = true)
    Institution toEntity(InstitutionDTO institutionDTO);
}
