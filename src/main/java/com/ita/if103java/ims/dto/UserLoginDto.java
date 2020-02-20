package com.ita.if103java.ims.dto;

import com.ita.if103java.ims.dto.transfer.ExistData;
import com.ita.if103java.ims.dto.transfer.NewData;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

public class UserLoginDto {

    @NotBlank(message = "This field mustn't be empty. Please, enter your username")
    @Email(groups = {NewData.class, ExistData.class},
        message = "Please, enter a correct email")
    private String username;

    @NotBlank(message = "This field mustn't be empty. Please, enter your password")
    @Size(min = 8, max = 32)
    private String password;

    public UserLoginDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserLoginDto{" +
            "username='" + username + '\'' +
            ", password='" + password + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserLoginDto that = (UserLoginDto) o;
        return Objects.equals(username, that.username) &&
            Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }
}
