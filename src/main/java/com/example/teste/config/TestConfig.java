package com.example.teste.config;

import java.time.Instant;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.example.teste.entities.Category;
import com.example.teste.entities.Order;
import com.example.teste.entities.OrderItem;
import com.example.teste.entities.Payment;
import com.example.teste.entities.Product;
import com.example.teste.entities.User;
import com.example.teste.entities.enums.OrderStatus;
import com.example.teste.repository.CategoryRepository;
import com.example.teste.repository.OrderItemRepository;
import com.example.teste.repository.OrderRepository;
import com.example.teste.repository.ProductRepository;
import com.example.teste.repository.UserRepository;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private OrderItemRepository orderItemRepository;
	
	@Override
	public void run(String... args) throws Exception {
		
		Category cat1 = new Category("Electronics");
		Category cat2 = new Category("Books");
		Category cat3 = new Category("Computers"); 
		
		Product p1 = new Product("The Lord of the Rings", "Lorem ipsum dolor sit amet, consectetur.", 90.5, "");
		Product p2 = new Product("Smart TV", "Nulla eu imperdiet purus. Maecenas ante.", 2190.0, "");
		Product p3 = new Product("Macbook Pro", "Nam eleifend maximus tortor, at mollis.", 1250.0, "");
		Product p4 = new Product("PC Gamer", "Donec aliquet odio ac rhoncus cursus.", 1200.0, "");
		Product p5 = new Product("Rails for Dummies", "Cras fringilla convallis sem vel faucibus.", 100.99, ""); 

		
		categoryRepository.saveAll (Arrays.asList(cat1, cat2, cat3));
		productRepository.saveAll (Arrays.asList(p1, p2, p3, p4, p5));
		
		p1.getCategories().add(cat2);
		p2.getCategories().add(cat3);
		p2.getCategories().add(cat1);
		p3.getCategories().add(cat3);
		p4.getCategories().add(cat3);
		p5.getCategories().add(cat2);
		
		productRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5));

		User u1 = new User(null, "Maria Brown", "maria@gmail.com", "988888888", "123456789");
		User u2 = new User(null, "Alex Green", "alex@gmail.com", "977777777", "12345678");
		User u3 = new User(null, "João Silva", "joao@gmail.com", "966666666", "senha123");
		User u4 = new User(null, "Ana Souza", "ana@gmail.com", "955555555", "segura456");
		User u5 = new User(null, "Carlos Mendes", "carlos@gmail.com", "944444444", "pass7890");
		User u6 = new User(null, "Beatriz Lima", "beatriz@gmail.com", "933333333", "minhaSenha");
		User u7 = new User(null, "Fernanda Rocha", "fernanda@gmail.com", "922222222", "teste2024");
		User u8 = new User(null, "Pedro Almeida", "pedro@gmail.com", "911111111", "teste2025");
		User u9 = new User(null, "Maria Almeida", "mei@gmail.com", "999999999", "testando1");

		
		Order o1 = new Order(null, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);
		Order o2 = new Order(null, Instant.parse("2019-07-21T03:42:10Z"), OrderStatus.WAITING_PAYMENT, u2);
		Order o3 = new Order(null, Instant.parse("2019-07-22T15:21:22Z"), OrderStatus.SHIPPED, u1);
		
		userRepository.saveAll (Arrays.asList(u1, u2, u3, u4, u5, u6, u7, u8, u9));
		orderRepository.saveAll(Arrays.asList(o1, o2, o3));
		
		OrderItem oi1 = new OrderItem(o1, p1, 2, p1.getPrice());
		OrderItem oi2 = new OrderItem(o1, p3, 1, p3.getPrice());
		OrderItem oi3 = new OrderItem(o2, p3, 2, p3.getPrice());
		OrderItem oi4 = new OrderItem(o3, p5, 2, p5.getPrice()); 
		
		orderItemRepository.saveAll(Arrays.asList(oi1, oi2, oi3, oi4));
		
		Payment pay1 = new Payment(null, Instant.parse("2019-06-20T21:53:07Z"), o1);
		o1.setPayment(pay1);

		orderRepository.save(o1);
	}
}
