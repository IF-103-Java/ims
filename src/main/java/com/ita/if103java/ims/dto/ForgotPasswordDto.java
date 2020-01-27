package com.ita.if103java.ims.dto;

import com.ita.if103java.ims.dto.transfer.NewData;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class ForgotPasswordDto implements Serializable {
    @NotBlank(groups = {NewData.class},
        message = "Please, enter these data")
    private String email;

    public ForgotPasswordDto() {
    }

    public ForgotPasswordDto(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "ForgotPasswordDto{" +
            "email='" + email + '\'' +
            '}';
    }
}
