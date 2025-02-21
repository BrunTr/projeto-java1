package com.example.teste.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.teste.entities.User;

import jakarta.persistence.criteria.Predicate;

public class UserSpecification {
	 public static Specification<User> filterBy(String name, String email, String phone) {
	        return (root, query, criteriaBuilder) -> {
	            List<Predicate> predicates = new ArrayList<>();

	            if (name != null && !name.isEmpty()) {
	                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
	            }
	            if (email != null && !email.isEmpty()) {
	                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
	            }
	            if (phone != null && !phone.isEmpty()) {
	                predicates.add(criteriaBuilder.like(root.get("phone"), "%" + phone + "%"));
	            }

	            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
	        };
	    }
}
