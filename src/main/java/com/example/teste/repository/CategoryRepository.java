package com.example.teste.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.teste.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
