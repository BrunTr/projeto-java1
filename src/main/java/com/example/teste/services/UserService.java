package com.example.teste.services;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.teste.dto.LoginDTO;
import com.example.teste.dto.UserDTO;
import com.example.teste.entities.User;
import com.example.teste.repository.OrderRepository;
import com.example.teste.repository.UserRepository;
import com.example.teste.services.exceptions.DatabaseException;
import com.example.teste.services.exceptions.IllegalArgumentException;
import com.example.teste.services.exceptions.ResourceNotFoundException;
import com.example.teste.services.exceptions.UnauthorizedException;
import com.example.teste.specification.UserSpecification;
import com.example.teste.utils.MD5Util;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository; 
	
	@Autowired
	public OrderRepository orderRepository; 
	
	public List<UserDTO> findAll() {
		List<User> user = userRepository.findAll();
		return user.stream().map(UserDTO::new).collect(Collectors.toList());
	}
	
	public User findById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado. Id: " + id));
	}
	
	public Page<UserDTO> specUser(String name, String email, String phone, Pageable pageable) {
	    Specification<User> spec = UserSpecification.filterBy(name, email, phone);
	    Page<User> users = userRepository.findAll(spec, pageable);
	    return users.map(UserDTO::new);
    }
   
	public boolean validateEmail(@Email String email) {
		boolean isValid = userRepository.findByEmail(email).isEmpty();
	    if (!isValid) {
	        throw new DatabaseException("O email inserido já está cadastrado, tente outro.");
	    }
	    return true;
	}
	
	public void validatePassword(String password) {
		  if (password.length()< 8) {
		        throw new IllegalArgumentException("A senha deve ter 8 ou mais caracteres.");
		  }
	}
	
	public void validateUser(User user) {
		if (Objects.isNull(user.getName()) || user.getName().trim().isEmpty()) {
	        throw new IllegalArgumentException("O nome não pode estar em branco.");
	    }
	    if (Objects.isNull(user.getEmail()) || user.getEmail().trim().isEmpty()) {
	        throw new IllegalArgumentException("O e-mail não pode estar em branco.");
	    }
	    if (Objects.isNull (user.getPhone()) || user.getPhone().trim().isEmpty()) {
	        throw new IllegalArgumentException("O telefone não pode estar em branco.");
	    }
	    if (Objects.isNull(user.getPassword()) || user.getPassword().length() < 8) {
	        throw new IllegalArgumentException("A senha deve ter 8 ou mais caracteres.");
	    }
	}
	
	@Email
	public void checkAndUpdateData(User entity, User user) {
		entity.setName((Objects.nonNull(user.getName()) && !user.getName().isBlank()) ? user.getName() : entity.getName());
		entity.setEmail((Objects.nonNull(user.getEmail()) &&  validateEmail(user.getEmail()) && !user.getEmail().isBlank())? user.getEmail() : entity.getEmail());
	    entity.setPhone((Objects.nonNull(user.getPhone()) && !user.getPhone().isBlank()) ? user.getPhone() : entity.getPhone());
	    entity.setPassword((Objects.nonNull(user.getPassword()) && !user.getPassword().isBlank()) ? user.getPassword() : entity.getPassword());	    
	}
	
	public UserDTO insert(@Valid User obj) {
		
		validateUser(obj);
		validateEmail(obj.email);
		validatePassword(obj.getPassword());
		
		obj.setPassword(MD5Util.encrypt(obj.getPassword()));
		
		User savedUser = userRepository.save(obj);
		
		return new UserDTO(savedUser);
	}
	
	
	public UserDTO login(@Valid LoginDTO loginDTO) {
	    User user = userRepository.findByEmail(loginDTO.getEmail()).orElseThrow(() -> new UnauthorizedException("Email ou senha estão incorretos."));
	    
	    String hashedPassword = MD5Util.encrypt(loginDTO.getPassword());

	     if (!hashedPassword.equals(user.getPassword())) {
	        throw new UnauthorizedException("Email ou senha estão incorretos.");
	    }

	    return new UserDTO(user);
	}
	
	public void delete(Long id) {
		if (!userRepository.existsById(id)) {
		    throw new ResourceNotFoundException("Usuário com ID " + id + " não encontrado.");
		}
		if (!orderRepository.findByClientId(id).isEmpty()) {
		        throw new DatabaseException("Não é possível excluir um usuário com pedidos associados.");
		    }
	    
		userRepository.deleteById(id);
	}

	
	public UserDTO update(Long id, @Valid User obj) {
		User entity = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuário com ID " + id + " não encontrado."));

		checkAndUpdateData(entity, obj);
			
		entity.setPassword(MD5Util.encrypt(entity.getPassword()));
			
		User user = userRepository.save(entity);
			
		return new UserDTO(user);
		    
    }
	
    public User updatePartial(Long id, Map<String, Object> updates) {
        try {
            User entity = userRepository.getReferenceById(id);
           			
            updates.forEach((key, value) -> {
                switch (key) {
                    case "name" -> entity.setName((String) value);
                    case "email" -> entity.setEmail((String) value);
                    case "phone" -> entity.setPhone((String) value);
                    case "password" -> entity.setPassword((String) value);
                }
            });

            return userRepository.save(entity);
            
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Usuário não encontrado. Id: " + id); 
        }
    }


}