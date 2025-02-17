package com.example.teste.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.teste.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	Optional<User> findByPassword(String password);
  
	 @Query("SELECT u FROM User u " +
	           "WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :termo, '%')) " +
	           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :termo, '%')) " +
	           "OR u.phone LIKE CONCAT('%', :termo, '%')")
	 List<User> searchUser(String termo);
}
