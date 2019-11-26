package com.ita.if103java.ims.mapper;

import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserDtoMapper {

    public User convertUserDtoToUser(UserDto userDto) {
        if (userDto == null) {
            return null;
        } else {
            User user = new User();
            user.setId(userDto.getId());
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
            user.setEmail(userDto.getEmail());
            user.setRole(userDto.getRole());
            user.setCreatedDate(userDto.getCreatedDate());
            user.setUpdatedDate(userDto.getUpdatedDate());
            user.setActive(userDto.isActive());
            user.setAccountId(userDto.getAccountId());
            return user;
        }
    }

    public UserDto convertUserToUserDto(User user) {
        if (user == null) {
            return null;
        } else {
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setFirstName(user.getFirstName());
            userDto.setLastName(user.getLastName());
            userDto.setEmail(user.getEmail());
            userDto.setPassword(user.getPassword());
            userDto.setRole(user.getRole());
            userDto.setCreatedDate(user.getCreatedDate());
            userDto.setUpdatedDate(user.getUpdatedDate());
            userDto.setActive(user.isActive());
            userDto.setEmailUUID(user.getEmailUUID());
            userDto.setAccountId(user.getAccountId());
            return userDto;
        }

    }

    public List<User> convertToUserList(List<UserDto> userDtoList) {
        return Optional.ofNullable(userDtoList)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::convertUserDtoToUser)
            .collect(Collectors.toList());
    }

    public List<UserDto> convertToUserDtoList(List<User> userList) {
        return Optional.ofNullable(userList)
            .orElse(Collections.emptyList())
            .stream()
            .map(this::convertUserToUserDto)
            .collect(Collectors.toList());
    }


}
