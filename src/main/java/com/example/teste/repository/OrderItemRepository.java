package com.example.teste.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.teste.entities.OrderItem;
import com.example.teste.entities.pk.OrderItemPK;

public interface OrderItemRepository extends JpaRepository <OrderItem, OrderItemPK> {

}
