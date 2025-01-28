package com.example.teste.dto;

import java.time.Instant;

import com.example.teste.entities.Order;

public class OrderDTO {

	private Long id;
	private Instant moment;
	private Double totalPrice;
	private String clientName;
	
	public OrderDTO(Order order) {
		this.id = order.getId();
        this.moment = order.getMoment();
        this.totalPrice = order.getTotal(); 
        this.clientName = order.getClient().getName(); 
	}
	
	public Long getId() { 
		return id; 
	}
    public Instant getMoment() {
    	return moment; 
    }
    public Double getTotalPrice() { 
    	return totalPrice; 
    }
    public String getClientName() { 
    	return clientName; 
    }
}
