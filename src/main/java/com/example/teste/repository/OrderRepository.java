package com.example.teste.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.teste.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByClientId(Long clientId);
}
