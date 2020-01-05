package com.ita.if103java.ims.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ita.if103java.ims.dto.transfer.ExistData;
import com.ita.if103java.ims.dto.transfer.NewData;
import com.ita.if103java.ims.dto.transfer.NewDataAdmin;
import com.ita.if103java.ims.dto.transfer.NewDataWorker;
import com.ita.if103java.ims.entity.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class UserDto implements Serializable {

    @Null(groups = {NewData.class},
        message = "This field must be filled with the auto-generator during registration")
    private Long id;

    @NotBlank(groups = {NewData.class, ExistData.class},
        message = "Please, enter these data")
    private String firstName;

    @NotBlank(groups = {NewData.class, ExistData.class},
        message = "Please, enter these data")
    private String lastName;

    @NotBlank(groups = {NewData.class, ExistData.class},
        message = "Please, enter an email")
    @Email(groups = {NewData.class, ExistData.class},
        message = "Please, enter a valid email")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(groups = {NewData.class, ExistData.class},
        message = "Please, enter these data")
    @Size(min = 8, max = 32)
    private String password;

    private Role role;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private ZonedDateTime createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private ZonedDateTime updatedDate;

    private boolean active;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Null(groups = {NewData.class, ExistData.class})
    private String emailUUID;

    @Null(groups = {NewData.class, ExistData.class},
        message = "This field must be filled with the auto-generator during the creation of organization")
    private Long accountId;

    @NotBlank(groups = {NewData.class, ExistData.class},
        message = "Please, enter an account name")
    private String accountName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(ZonedDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getEmailUUID() {
        return emailUUID;
    }

    public void setEmailUUID(String emailUUID) {
        this.emailUUID = emailUUID;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public String toString() {
        return "UserDto{" +
            "id=" + id +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", password='" + password + '\'' +
            ", role=" + role +
            ", createdDate=" + createdDate +
            ", updatedDate=" + updatedDate +
            ", active=" + active +
            ", emailUUID='" + emailUUID + '\'' +
            ", accountId=" + accountId +
            ", accountName='" + accountName + '\'' +
            '}';
    }
}
