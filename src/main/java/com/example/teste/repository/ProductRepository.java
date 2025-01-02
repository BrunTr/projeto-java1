package com.example.teste.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.teste.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
