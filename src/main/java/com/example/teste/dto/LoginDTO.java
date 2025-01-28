package com.example.teste.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginDTO {
	
	@Email(message = "O e-mail deve ser válido")
    @NotBlank(message = "O campo do e-mail não pode estar vazio")
	private String email;
	@NotBlank(message = "O campo da senha não pode estar vazio")
	private String password;

    public LoginDTO() {
    }

    public LoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
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
}