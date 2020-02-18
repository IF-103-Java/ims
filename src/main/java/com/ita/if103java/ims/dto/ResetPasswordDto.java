package com.ita.if103java.ims.dto;

import com.ita.if103java.ims.dto.transfer.ExistData;

import javax.validation.constraints.NotBlank;

public class ResetPasswordDto {
    @NotBlank(groups = {ExistData.class},
        message = "Please, enter these data")
    private String currentPassword;

    @NotBlank(groups = {ExistData.class},
        message = "Please, enter these data")
    private String newPassword;

    public ResetPasswordDto() {
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
