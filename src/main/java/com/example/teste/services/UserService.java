package com.example.teste.services;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.example.teste.dto.LoginDTO;
import com.example.teste.dto.UserDTO;
import com.example.teste.entities.Order;
import com.example.teste.entities.User;
import com.example.teste.repository.OrderRepository;
import com.example.teste.repository.UserRepository;
import com.example.teste.services.exceptions.DatabaseException;
import com.example.teste.services.exceptions.IllegalArgumentException;
import com.example.teste.services.exceptions.ResourceNotFoundException;
import com.example.teste.utils.MD5Util;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

@Service
public class UserService {

	@Autowired
	private UserRepository repository; 
	
	@Autowired
	private OrderRepository orderRepository; 
	
	public List<UserDTO> findAll() {
		List<User> user = repository.findAll();
		return user.stream().map(UserDTO::new).collect(Collectors.toList());
	}
	
	public User findById(Long id) {
		Optional<User> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ResourceNotFoundException(id));
	}
	
	private boolean isEmailValid(@Email String email) {
		boolean isValid = repository.findByEmail(email).isEmpty();
	    if (!isValid) {
	        throw new IllegalArgumentException("O email inserido já está cadastrado, tente outro.");
	    }
	    return true;
	}

	private void validatePassword(String password) {
		Optional<User> validatePassword = repository.findByPassword(password);
		  if (validatePassword == null || password.length()< 8) {
		        throw new IllegalArgumentException("A senha deve ter 8 ou mais caracteres.");
		  }
	}
	
	private void validateUser(User user) {
		if (user.getName() == null || user.getName().trim().isEmpty()) {
	        throw new IllegalArgumentException("O nome não pode estar em branco.");
	    }
	    if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
	        throw new IllegalArgumentException("O e-mail não pode estar em branco.");
	    }
	    if (user.getPhone() == null || user.getPhone().trim().isEmpty()) {
	        throw new IllegalArgumentException("O telefone não pode estar em branco.");
	    }
	    if (user.getPassword() == null || user.getPassword().length() < 8) {
	        throw new IllegalArgumentException("A senha deve ter 8 ou mais caracteres.");
	    }
	}
	
	@Email
	private void checkAndUpdateData(User entity, User user) {
		entity.setName((Objects.nonNull(user.getName()) && !user.getName().isBlank()) ? user.getName() : entity.getName());
		entity.setEmail((Objects.nonNull(user.getEmail()) &&  isEmailValid(user.getEmail()) && !user.getEmail().isBlank())? user.getEmail() : entity.getEmail());
	    entity.setPhone((Objects.nonNull(user.getPhone()) && !user.getPhone().isBlank()) ? user.getPhone() : entity.getPhone());
	    entity.setPassword((Objects.nonNull(user.getPassword()) && !user.getPassword().isBlank()) ? user.getPassword() : entity.getPassword());

	    	    
	}

	public UserDTO insert(@Valid User obj) {
		validateUser(obj);
		isEmailValid(obj.email);
		validatePassword(obj.getPassword());
		obj.setPassword(MD5Util.encrypt(obj.getPassword()));
		User user = repository.save(obj);
		
		UserDTO responseDTO = new UserDTO();
	    responseDTO.setId(user.getId());
	    responseDTO.setName(user.getName());
	    responseDTO.setEmail(user.getEmail());
	    responseDTO.setPhone(user.getPhone());
	    
	    return responseDTO;
	}
	
	
	public UserDTO login(@Valid LoginDTO loginDTO) {
		Optional<User> emailLogin = repository.findByEmail(loginDTO.getEmail());
		
	    if (emailLogin.isEmpty()) {
	        throw new IllegalArgumentException("E-mail não cadastrado.");
	    }

	    User user = emailLogin.get(); 
	    String hashedPassword = MD5Util.encrypt(loginDTO.getPassword());

	    if (!hashedPassword.equals(user.getPassword())) {
	        throw new IllegalArgumentException("Senha incorreta.");
	    }

	    UserDTO responseDTO = new UserDTO();
	    responseDTO.setId(user.getId());
	    responseDTO.setName(user.getName());
	    responseDTO.setEmail(user.getEmail());
	    responseDTO.setPhone(user.getPhone());
	    
	    return responseDTO;
	}
	
	public void delete(Long id) {
		repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuário com ID " + id + " não encontrado."));

	    try {
	        List<Order> orders = orderRepository.findByClientId(id);
	        if (!orders.isEmpty()) {
	            throw new DatabaseException("Não é possível excluir um usuário com pedidos associados.");
	        }
	        
	        repository.deleteById(id);
	    } catch (EmptyResultDataAccessException e) {
	        throw new ResourceNotFoundException(id);
	    } catch (DataIntegrityViolationException e) {
	        throw new DatabaseException(e.getMessage());
	    }
	}

	
	public UserDTO update(Long id, @Valid User obj) {
		try {
			User entity = repository.getReferenceById(id);
			
			checkAndUpdateData(entity, obj);
			
			validatePassword(entity.getPassword());
			entity.setPassword(MD5Util.encrypt(entity.getPassword()));
			obj.setPassword(obj.getPassword());
			
			User user = repository.save(entity);
			
			UserDTO responseDTO = new UserDTO();
		    responseDTO.setId(user.getId());
		    responseDTO.setName(user.getName());
		    responseDTO.setEmail(user.getEmail());
		    responseDTO.setPhone(user.getPhone());
		    
		    return responseDTO;
		    
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(id);
		} 
    }
	
    public User updatePartial(Long id, Map<String, Object> updates) {
        try {
            User entity = repository.getReferenceById(id);
           			
            updates.forEach((key, value) -> {
                switch (key) {
                    case "name" -> entity.setName((String) value);
                    case "email" -> entity.setEmail((String) value);
                    case "phone" -> entity.setPhone((String) value);
                    case "password" -> entity.setPassword((String) value);
                }
            });

            return repository.save(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id); 
        }
    }


}