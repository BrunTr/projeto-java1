package com.example.teste.dto;

import com.example.teste.entities.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public class UserDTO {
	
		private Long id;
		private String name;
	  	@Email
	    private String email;
		private String phone;
	   
	    
		
	    public UserDTO() {
	    }
	    
	    public UserDTO(User user) {
	    	 this.id = user.getId();
			 this.name = user.getName();   
			 this.email = user.getEmail();
		     this.phone = user.getPhone();
		     
		}

	    public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
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

		
		
	   
}
