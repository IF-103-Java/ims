package com.ita.if103java.ims.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ita.if103java.ims.dto.transfer.ExistData;
import com.ita.if103java.ims.dto.transfer.NewData;
import com.ita.if103java.ims.dto.transfer.NewDataAdmin;
import com.ita.if103java.ims.dto.transfer.NewDataWorker;
import com.ita.if103java.ims.entity.Role;
import org.springframework.stereotype.Component;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class UserDto implements Serializable {

    @Null(groups = {NewDataAdmin.class},
        message = "This field must be filled with the auto-generator during registration")
    @NotNull(groups = {ExistData.class, NewDataWorker.class},
        message = "This field must not be empty")
    private Long id;

    @Null(groups = {NewDataWorker.class},
        message = "This field must be empty until confirmation via email and a real registration of Worker")
    @NotBlank(groups = {NewDataAdmin.class, ExistData.class},
        message = "Please, enter these data")
    private String firstName;

    @Null(groups = {NewDataWorker.class},
        message = "This field must be empty until confirmation via email and a real registration of Worker")
    @NotBlank(groups = {NewDataAdmin.class, ExistData.class},
        message = "Please, enter these data")
    private String lastName;

    @NotBlank(groups = {NewData.class, ExistData.class},
        message = "Please, enter an email")
    @Email(groups = {NewData.class, ExistData.class},
        message = "Please, enter a valid email")
    private String email;

    @Null(groups = {NewDataWorker.class},
        message = "This field must be empty until confirmation via email and a real registration of Worker")
    @NotBlank(groups = {NewDataAdmin.class, ExistData.class},
        message = "Please, enter these data")
    @Size(min = 8, max = 32)
    private String password;

    @NotNull(groups = {NewData.class, ExistData.class})
    private Role role;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime updatedDate;

    @NotNull(groups = {NewData.class, ExistData.class})
    private boolean active;

    @NotBlank(groups = {NewData.class, ExistData.class})
    private String emailUUID;

    @Null(groups = {NewDataAdmin.class},
        message = "This field must be filled with the auto-generator during the creation of organization")
    @NotNull(groups = {ExistData.class, NewDataWorker.class},
        message = "This field must not be empty")
    private Long accountId;

    public UserDto() {
    }

    public UserDto(Long id, String firstName, String lastName, String email, String password, Role role, LocalDateTime createdDate, LocalDateTime updatedDate, boolean active, String emailUUID, Long accountId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.active = active;
        this.emailUUID = emailUUID;
        this.accountId = accountId;
    }

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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
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
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return active == userDto.active &&
            id.equals(userDto.id) &&
            Objects.equals(firstName, userDto.firstName) &&
            Objects.equals(lastName, userDto.lastName) &&
            email.equals(userDto.email) &&
            Objects.equals(password, userDto.password) &&
            role == userDto.role &&
            createdDate.equals(userDto.createdDate) &&
            updatedDate.equals(userDto.updatedDate) &&
            emailUUID.equals(userDto.emailUUID) &&
            Objects.equals(accountId, userDto.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, password, role, createdDate, updatedDate, active, emailUUID, accountId);
    }
}
