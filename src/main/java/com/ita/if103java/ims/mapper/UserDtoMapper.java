package com.ita.if103java.ims.mapper;

import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper extends AbstractEntityDtoMapper<User, UserDto> {

    @Override
    public User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        } else {
            User user = new User();
            user.setId(dto.getId());
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setEmail(dto.getEmail());
            user.setPassword(dto.getPassword());
            user.setRole(dto.getRole());
            user.setCreatedDate(dto.getCreatedDate());
            user.setUpdatedDate(dto.getUpdatedDate());
            user.setActive(dto.isActive());
            user.setEmailUUID(dto.getEmailUUID());
            user.setAccountId(dto.getAccountId());
            return user;
        }
    }

    @Override
    public UserDto toDto(User entity) {
        if (entity == null) {
            return null;
        } else {
            UserDto userDto = new UserDto();
            userDto.setId(entity.getId());
            userDto.setFirstName(entity.getFirstName());
            userDto.setLastName(entity.getLastName());
            userDto.setEmail(entity.getEmail());
            userDto.setRole(entity.getRole());
            userDto.setCreatedDate(entity.getCreatedDate());
            userDto.setUpdatedDate(entity.getUpdatedDate());
            userDto.setActive(entity.isActive());
            userDto.setAccountId(entity.getAccountId());
            return userDto;
        }
    }
}
