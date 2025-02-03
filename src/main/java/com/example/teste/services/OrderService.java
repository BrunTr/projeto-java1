package com.example.teste.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.teste.dto.OrderDTO;
import com.example.teste.entities.Order;
import com.example.teste.repository.OrderRepository;
import com.example.teste.services.exceptions.ResourceNotFoundException;

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
	
	public OrderDTO findById(Long id ) {
		 Order order = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
	        return new OrderDTO(order);
	}
	
	
	
}
