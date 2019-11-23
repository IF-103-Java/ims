package com.ita.if103java.ims.mapper;

import com.ita.if103java.ims.dto.UserDto;
import com.ita.if103java.ims.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
        if (userDtoList == null)
            return new ArrayList<>();
        else {
            List<User> list = new ArrayList<>();
            userDtoList.forEach(item -> list.add(convertUserDtoToUser(item)));
            return list;
        }
    }

    public List<UserDto> convertToUserDtoList(List<User> userList) {
        if (userList == null)
            return new ArrayList<>();
        else {
            List<UserDto> list = new ArrayList<>();
            userList.forEach(item -> list.add(convertUserToUserDto(item)));
            return list;
        }
    }


}
