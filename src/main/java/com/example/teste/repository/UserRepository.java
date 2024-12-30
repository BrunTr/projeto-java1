package com.example.teste.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.teste.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
