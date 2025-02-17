package com.example.teste;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.example.teste.dto.LoginDTO;
import com.example.teste.dto.UserDTO;
import com.example.teste.entities.User;
import com.example.teste.repository.OrderRepository;
import com.example.teste.repository.UserRepository;
import com.example.teste.services.UserService;
import com.example.teste.services.exceptions.DatabaseException;
import com.example.teste.services.exceptions.IllegalArgumentException;
import com.example.teste.services.exceptions.ResourceNotFoundException;
import com.example.teste.services.exceptions.UnauthorizedException;
import com.example.teste.utils.MD5Util;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
	@Spy
    @InjectMocks
    private UserService userService;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private OrderRepository orderRepository;
    
    private User user;
    
    private LoginDTO loginDTO;
    
    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Teste");
        user.setEmail("teste@example.com");
        user.setPhone("123456789");
        user.setPassword(MD5Util.encrypt("password123"));
        
        User updatedUser = new User();
        updatedUser.setName("New Name");
        updatedUser.setEmail("new.email@example.com");
        updatedUser.setPhone("987654321");
        updatedUser.setPassword("newPassword");
        
        loginDTO = new LoginDTO("teste@example.com", "password123");
    }
    
    @Test
    void findAll_ShouldReturnUserList() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<UserDTO> users = userService.findAll();
        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
    }
    
    @Test
    void findById_UserExists_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User foundUser = userService.findById(1L);
        assertEquals(user.getId(), foundUser.getId());
    }
    
    @Test
    void findById_UserNotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.findById(1L));
    }
    
    @Test
    void validateEmail_EmailAlreadyExists_ShouldThrowException() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        assertThrows(DatabaseException.class, () -> userService.validateEmail(user.getEmail()));
    }
    
    @Test
    void validatePassword_InvalidPassword_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> userService.validatePassword("123"));
    }
       
    @Test
    void insert_ValidUser_ShouldSaveUser() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        UserDTO savedUser = userService.insert(user);
        assertEquals(user.getEmail(), savedUser.getEmail());
    }
    
    @Test
    void validateUser_NameIsNull_ShouldThrowException() {
        User user = new User(null, null, "email@example.com", "123456789", "password123");
        assertThrows(IllegalArgumentException.class, () -> userService.validateUser(user));
    }
    
    @Test
    void validateUser_NameIsEmpty_ShouldThrowException() {
        User user = new User(null, "   ", "email@example.com", "123456789", "password123");
        assertThrows(IllegalArgumentException.class, () -> userService.validateUser(user));
    }

    @Test
    void validateUser_EmailIsNull_ShouldThrowException() {
        User user = new User(null, "Nome", null, "123456789", "password123");
        assertThrows(IllegalArgumentException.class, () -> userService.validateUser(user));
    }
    
    @Test
    void validateUser_EmailIsEmpty_ShouldThrowException() {
        User user = new User(null, "Nome", "   ", "123456789", "password123");
        assertThrows(IllegalArgumentException.class, () -> userService.validateUser(user));
    }
    
    @Test
    void validateUser_PhoneIsNull_ShouldThrowException() {
        User user = new User(null, "Nome", "email@example.com", null, "password123");
        assertThrows(IllegalArgumentException.class, () -> userService.validateUser(user));
    }

    @Test
    void validateUser_PhoneIsEmpty_ShouldThrowException() {
        User user = new User(null, "Nome", "email@example.com", "   ", "password123");
        assertThrows(IllegalArgumentException.class, () -> userService.validateUser(user));
    }

    @Test
    void validateUser_PasswordIsNull_ShouldThrowException() {
        User user = new User(null, "Nome", "email@example.com", "123456789", null);
        assertThrows(IllegalArgumentException.class, () -> userService.validateUser(user));
    }

    @Test
    void validateUser_PasswordIsTooShort_ShouldThrowException() {
        User user = new User(null, "Nome", "email@example.com", "123456789", "1234567");
        assertThrows(IllegalArgumentException.class, () -> userService.validateUser(user));
    }

    @Test
    void checkAndUpdateData_AllFieldsValid_ShouldUpdateEntity() {
        User entity = new User();
        entity.setName("Old Name");
        entity.setEmail("old.email@example.com");
        entity.setPhone("123456789");
        entity.setPassword("oldPassword");

        User updatedUser = new User();
        updatedUser.setName("New Name");
        updatedUser.setEmail("new.email@example.com");
        updatedUser.setPhone("987654321");
        updatedUser.setPassword("newPassword");

        when(userService.validateEmail("new.email@example.com")).thenReturn(true);

        userService.checkAndUpdateData(entity, updatedUser);

        assertEquals("New Name", entity.getName());
        assertEquals("new.email@example.com", entity.getEmail());
        assertEquals("987654321", entity.getPhone());
        assertEquals("newPassword", entity.getPassword());
    }

    @Test
    void checkAndUpdateData_SomeFieldsNullOrBlank_ShouldNotUpdateThoseFields() {
        User entity = new User();
        entity.setName("Old Name");
        entity.setEmail("old.email@example.com");
        entity.setPhone("123456789");
        entity.setPassword("oldPassword");

        User updatedUser = new User();
        updatedUser.setName("");
        updatedUser.setEmail(null);
        updatedUser.setPhone("987654321");
        updatedUser.setPassword("");

        userService.checkAndUpdateData(entity, updatedUser);

        assertEquals("Old Name", entity.getName()); 
        assertEquals("old.email@example.com", entity.getEmail()); 
        assertEquals("987654321", entity.getPhone());
        assertEquals("oldPassword", entity.getPassword());
    }

    @Test
    void checkAndUpdateData_InvalidEmail_ShouldNotUpdateEmail() {
    	 User entity = new User();
    	    entity.setEmail("old.email@example.com");

    	    User updatedUser = new User();
    	    updatedUser.setEmail("invalid-email");

    	    doReturn(false).when(userService).validateEmail("invalid-email");

    	    userService.checkAndUpdateData(entity, updatedUser);

    	    assertEquals("old.email@example.com", entity.getEmail());
    }

    @Test
    void checkAndUpdateData_AllFieldsNull_ShouldNotUpdateAnything() {
        User entity = new User();
        entity.setName("Old Name");
        entity.setEmail("old.email@example.com");
        entity.setPhone("123456789");
        entity.setPassword("oldPassword");

        User updatedUser = new User();

        userService.checkAndUpdateData(entity, updatedUser);

        assertEquals("Old Name", entity.getName());
        assertEquals("old.email@example.com", entity.getEmail());
        assertEquals("123456789", entity.getPhone());
        assertEquals("oldPassword", entity.getPassword());
    }
    
    @Test
    void insert_UserWithExistingEmail_ShouldThrowException() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        assertThrows(DatabaseException.class, () -> userService.insert(user));
    }
    
    @Test
    void login_ValidCredentials_ShouldReturnUserDTO() {
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));
        UserDTO loggedUser = userService.login(loginDTO);
        assertEquals(user.getEmail(), loggedUser.getEmail());
    }
    
    @Test
    void login_InvalidCredentials_ShouldThrowException() {
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.empty());
        assertThrows(UnauthorizedException.class, () -> userService.login(loginDTO));
    }
    
    @Test
    void login_PasswordIsIncorrect_ShouldThrowException() {
        when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.of(user));
       
        LoginDTO loginDTO = new LoginDTO("email@example.com", "wrongPassword");
        assertThrows(UnauthorizedException.class, () -> userService.login(loginDTO));
    }
        
    @Test
    void delete_UserExists_ShouldDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(orderRepository.findByClientId(1L)).thenReturn(Collections.emptyList());
        assertDoesNotThrow(() -> userService.delete(1L));
    }
    
    @Test
    void delete_UserExists_ShouldThrowException() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        doThrow(EmptyResultDataAccessException.class).when(userRepository).deleteById(userId);
        assertThrows(ResourceNotFoundException.class, () -> userService.delete(userId));
    }
    
    @Test
    void delete_NonExistentUser_ShouldThrowException() {
        Long nonExistentUserId = 999L;
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.delete(nonExistentUserId));
    }
    
    @Test
    void delete_UserHasOrders_ShouldThrowException() {
    	 Long userId = 1L;
         when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
         when(orderRepository.findByClientId(userId)).thenReturn(Collections.singletonList(mock(com.example.teste.entities.Order.class)));
         assertThrows(DatabaseException.class, () -> userService.delete(userId));
     }
    
    @Test
    void delete_UserNoOrders_ShouldThrowException() {
        Long userId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(orderRepository.findByClientId(userId)).thenReturn(Collections.emptyList());

        doThrow(DataIntegrityViolationException.class).when(userRepository).deleteById(userId);

        assertThrows(DatabaseException.class, () -> userService.delete(userId));    
    }
    
    @Test
    void update_UserExists_ShouldUpdateUser() {
        User updatedUser = new User();
        updatedUser.setName("Novo Nome");
        
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        UserDTO updatedDTO = userService.update(1L, updatedUser);
        assertEquals("Novo Nome", updatedDTO.getName());
    }
    
    @Test
    void update_UserNotFound_ShouldThrowException() {
        when(userRepository.getReferenceById(1L)).thenThrow(new EntityNotFoundException());
        assertThrows(ResourceNotFoundException.class, () -> userService.update(1L, user));
    }
    
    @Test
    void updatePartial_UserExists_ShouldUpdateFields() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Novo Nome");
        updates.put("email", "novo@example.com");
        
        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        User updatedUser = userService.updatePartial(1L, updates);
        assertEquals("Novo Nome", updatedUser.getName());
        assertEquals("novo@example.com", updatedUser.getEmail());
    }
    
    @Test
    void updatePartial_UserNotFound_ShouldThrowException() {
        when(userRepository.getReferenceById(1L)).thenThrow(new EntityNotFoundException());
        assertThrows(ResourceNotFoundException.class, () -> userService.updatePartial(1L, Map.of("name", "Novo Nome")));
    }
}
