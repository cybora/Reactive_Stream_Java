package com.bora;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.endsWith;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import com.bora.model.Employee;
import com.bora.model.Image;
import com.bora.repository.EmployeeRepository;

import reactor.core.publisher.Flux;

@Component
public class InitDatabase {
	
	@Autowired
	private EmployeeRepository repo;
	
	@Bean
	CommandLineRunner init(MongoOperations mongoOperations) {
		return args -> {
			mongoOperations.dropCollection(Employee.class);
			
			Employee e1 = new Employee();
			e1.setId(UUID.randomUUID().toString());
			e1.setFirstName("George");
			e1.setLastName("Adams");
			e1.setRole("burglar");
			
			mongoOperations.insert(e1);
			
			Employee e2 = new Employee();
			e2.setId(UUID.randomUUID().toString());
			e2.setFirstName("Micheal");
			e2.setLastName("Adams");
			e2.setRole("security");
			
			mongoOperations.insert(e2);
			
			Employee e3 = new Employee();
			e3.setId(UUID.randomUUID().toString());
			e3.setFirstName("Xenia");
			e3.setLastName("Lutf");
			e3.setRole("dasdsadsa");
			
			mongoOperations.insert(e3);
			
			Employee e = new Employee();
			e.setLastName("tf");
			
			ExampleMatcher matcher = ExampleMatcher.matching()
					.withIgnoreCase()
					.withMatcher("lastName", endsWith())
					.withIncludeNullValues();
			
			Example<Employee> example = Example.of(e, matcher);
			
			Flux<Employee> employees = repo.findAll(example);
			
			employees.subscribe(System.out::println);
		};
	}

}
