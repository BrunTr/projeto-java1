package com.example.teste.dto;

import java.time.Instant;

import com.example.teste.entities.Order;
import com.example.teste.entities.enums.OrderStatus;

public class OrderDTO {

	private Long id;
	private Instant moment;
	private Double totalPrice;
	private String clientName;
	private OrderStatus orderStatus;
		
	public OrderDTO(Order order) {
		this.id = order.getId();
        this.moment = order.getMoment();
        this.totalPrice = order.getTotal(); 
        this.clientName = order.getClient().getName(); 
        this.orderStatus = order.getOrderStatus(); 
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


	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

}
