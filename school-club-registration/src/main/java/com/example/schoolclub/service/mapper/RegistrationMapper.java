package com.example.schoolclub.service.mapper;

import com.example.schoolclub.domain.Club;
import com.example.schoolclub.domain.Registration;
import com.example.schoolclub.domain.User;
import com.example.schoolclub.service.dto.ClubDTO;
import com.example.schoolclub.service.dto.RegistrationDTO;
import com.example.schoolclub.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Registration} and its DTO {@link RegistrationDTO}.
 */
@Mapper(componentModel = "spring")
public interface RegistrationMapper extends EntityMapper<RegistrationDTO, Registration> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "club", source = "club", qualifiedByName = "clubId")
    RegistrationDTO toDto(Registration s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("clubId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ClubDTO toDtoClubId(Club club);
}
