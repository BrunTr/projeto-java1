package com.example.teste.dto;

import java.util.Objects;

import com.example.teste.entities.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public class UserDTO {
	
		@NotBlank
		private String name;
	    @Email(message = "O e-mail deve ser válido")
	    @NotBlank(message = "O campo do e-mail não pode estar vazio")
	    private String email;

	    @NotBlank
	    @NotBlank(message = "O campo do telefone não pode estar vazio")
	    private String phone;
	   
	    @NotBlank(message = "O campo da senha não pode estar vazio")
	    private String password;
	    
		
	    public UserDTO() {
	    }
	    
	    public UserDTO(User user) {
			 this.name = user.getName();   
			 this.email = user.getEmail();
		     this.phone = user.getPhone();
		     this.password = user.getPassword();
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	    
	    public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(email);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			UserDTO other = (UserDTO) obj;
			return Objects.equals(email, other.email);
		}

		

	

	   
}
