package com.kpi.composer.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kpi.composer.validate.annotation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Username must not be blank.")
    @Size(max = 50, message = "Max username size is 50.")
    private String username;

    @NotBlank(message = "Email must not be blank.")
    @Email(message = "Email is not correct.")
    private String email;

    @Password
    private String password;
}
