package com.example.teste.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.teste.dto.OrderDTO;
import com.example.teste.entities.Order;
import com.example.teste.repository.OrderRepository;


@Service
public class OrderService {

	@Autowired
	private OrderRepository repository; 
	
	public List<OrderDTO> findAll() {
		 List<Order> orders = repository.findAll();
	        return orders.stream().map(OrderDTO::new).collect(Collectors.toList());
	}
	
	public List<Order> findAll1() {
		 return repository.findAll(); 
	}
	
	public Order findById(Long id ) {
		Optional<Order> obj = repository.findById(id);
		return obj.get();
	}
	
	
	
}
