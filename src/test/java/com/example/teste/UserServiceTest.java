package com.example.teste;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

import com.example.teste.dto.LoginDTO;
import com.example.teste.dto.UserDTO;
import com.example.teste.entities.Order;
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
    void testFindUser() {
        User user1 = new User(1L, "Maria Brown", "maria@gmail.com", "988888888", "12345678");
        User user2 = new User(2L, "Alex Green", "alex@gmail.com", "977777777", "87654321");

        when(userRepository.searchUser("Ma")).thenReturn(List.of(user1));
        when(userRepository.searchUser("A")).thenReturn(List.of(user1, user2));

        List<UserDTO> result1 = userService.findUser("Ma");
        List<UserDTO> result2 = userService.findUser("A");

        assertEquals(1, result1.size());
        assertEquals("Maria Brown", result1.get(0).getName());

        assertEquals(2, result2.size());
        assertEquals("Maria Brown", result2.get(0).getName());
        assertEquals("Alex Green", result2.get(1).getName());

        verify(userRepository, times(1)).searchUser("Ma");
        verify(userRepository, times(1)).searchUser("A");
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
        when(userRepository.existsById(1L)).thenReturn(true);
        when(orderRepository.findByClientId(1L)).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> userService.delete(1L));
    }
    
    @Test
    void delete_UserHasOrders_ShouldThrowDatabaseException() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(orderRepository.findByClientId(1L)).thenReturn(List.of(new Order()));

        assertThrows(DatabaseException.class, () -> userService.delete(1L));
    }
    
    @Test
    void delete_UserNotFound_ShouldThrowException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.delete(1L));
    }

    
    @Test
    void delete_UserHasOrders_ShouldThrowException() {
    	   Long userId = 1L;

    	   when(userRepository.existsById(userId)).thenReturn(true); // Simulando que o usuário existe
    	   when(orderRepository.findByClientId(userId)).thenReturn(List.of(new Order())); // Simulando que o usuário tem pedidos

    	   assertThrows(DatabaseException.class, () -> userService.delete(userId));
    	}
       
    @Test
    void update_UserExists_ShouldUpdateUser() {
        Long userId = 1L;

        User updatedUser = new User();
        updatedUser.setName("Novo Nome");
        updatedUser.setPassword("novaSenha");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Simula salvar o próprio objeto

        UserDTO updatedDTO = userService.update(userId, updatedUser);

        assertEquals("Novo Nome", updatedDTO.getName());
        assertEquals(MD5Util.encrypt("novaSenha"), user.getPassword()); // Senha criptografada corretamente
    }

    @Test
    void update_UserNotFound_ShouldThrowException() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.update(userId, user));
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
