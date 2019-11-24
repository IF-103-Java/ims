package com.ita.if103java.ims.entity;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime updatedDate;
    private boolean active;
    private String emailUUID;
    private Long accountId;

    public User() {
    }

    public User(Long id, String firstName, String lastName, String email, String password, Role role, LocalDateTime createdDate,
                LocalDateTime updatedDate, boolean active, String emailUUID, Long accountId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.active = active;
        this.emailUUID = email;
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
        return "User{" +
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
        User user = (User) o;
        return active == user.active &&
            id.equals(user.id) &&
            Objects.equals(firstName, user.firstName) &&
            Objects.equals(lastName, user.lastName) &&
            email.equals(user.email) &&
            Objects.equals(password, user.password) &&
            role == user.role &&
            createdDate.equals(user.createdDate) &&
            updatedDate.equals(user.updatedDate) &&
            emailUUID.equals(user.emailUUID) &&
            Objects.equals(accountId, user.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, password, role, createdDate, updatedDate, active, emailUUID, accountId);
    }
}
