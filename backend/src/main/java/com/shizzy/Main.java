package com.shizzy;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.shizzy.customer.Customer;
import com.shizzy.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerRepository repository){
        return args -> {

            //generateFakeData(repository);

        };

    }

    void generateFakeData(CustomerRepository repository){
        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Faker faker = new Faker();
            Random random = new Random();
            Name name = faker.name();
            String firstName = name.firstName();
            String lastName = name.lastName();
            Customer customer = new Customer(
                    firstName+" "+lastName,
                    firstName.toLowerCase()+"."+lastName.toLowerCase()+"@froshtech.org",
                    random.nextInt(16,99)
            );
            customers.add(customer);
        }
        repository.saveAll(customers);
    }


}