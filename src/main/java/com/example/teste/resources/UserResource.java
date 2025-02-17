package com.example.teste.resources;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.teste.dto.LoginDTO;
import com.example.teste.dto.UserDTO;
import com.example.teste.entities.User;
import com.example.teste.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/users")
public class UserResource {

	@Autowired
	private UserService userService;
	
		
	@GetMapping
	public ResponseEntity<List<UserDTO>> findAll() {
		List<UserDTO> list = userService.findAll();
		return ResponseEntity.ok().body(list);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		User obj = userService.findById(id);
		return ResponseEntity.ok().body(obj);
	}
	
    @GetMapping("/search")
    public List<UserDTO> findUser(@RequestParam String termo) {
        return userService.findUser(termo);
//		List<UserDTO> list = userService.findUser();
//		return ResponseEntity.ok().body(termo);
    }
    
	@PostMapping
	public ResponseEntity<UserDTO> insert(@RequestBody @Valid User obj) {
	    UserDTO userDTO = userService.insert(obj); 
	    URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
	            .path("/{id}")
	            .buildAndExpand(userDTO.getId()) 
	            .toUri();

	    return ResponseEntity.created(uri).body(userDTO);
	}
	
	@PostMapping(value = "/login")
	public ResponseEntity<UserDTO> login(@RequestBody LoginDTO loginDTO){
		UserDTO userDTO = userService.login(loginDTO);
		
		return ResponseEntity.ok(userDTO);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody @Valid User obj) {
		UserDTO userDTO = userService.update(id, obj);
		
	    return ResponseEntity.ok().body(userDTO);
	}
	
	
	@PatchMapping("/{id}")
	public ResponseEntity<User> updatePartial(@PathVariable Long id, @RequestBody Map<String, Object> updates) {

	    User updatedUser = userService.updatePartial(id, updates);
	    return ResponseEntity.ok().body(updatedUser);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		userService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
