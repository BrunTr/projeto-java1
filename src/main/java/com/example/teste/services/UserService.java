package com.example.teste.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.example.teste.dto.UserDTO;
import com.example.teste.entities.User;
import com.example.teste.repository.UserRepository;
import com.example.teste.services.exceptions.DatabaseException;
import com.example.teste.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@Service
public class UserService {

	@Autowired
	private UserRepository repository; 
	
	public List<User> findAll() {
		return repository.findAll();
	}
	
	public User findById(Long id) {
		Optional<User> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ResourceNotFoundException(id));
	}
	
	public void isEmailValid(String email) {
		Optional<User> isValid = repository.findByEmail(email);
		  if (isValid.isPresent()) {
		        throw new IllegalArgumentException("O email inserido já existe, tente outro.");
		    }
	}

	public User insert(@Valid User obj) {
		isEmailValid(obj.email);
		return repository.save(obj);
	}
	
	
	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException(id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}
	}
	
	public User update(Long id, @Valid UserDTO dto) {
		try {
			User entity = repository.getReferenceById(id);
			updateData(entity, dto);
			return repository.save(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(id);
		}
    }

    private void updateData(User entity, @Valid UserDTO dto) {
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setPassword(dto.getPassword());
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
