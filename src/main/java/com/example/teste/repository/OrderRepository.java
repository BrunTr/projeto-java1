package com.example.teste.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.teste.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
