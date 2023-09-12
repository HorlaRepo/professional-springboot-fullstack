package com.shizzy;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.shizzy.customer.Customer;
import com.shizzy.customer.CustomerRepository;
import com.shizzy.customer.Gender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerRepository repository, PasswordEncoder encoder){
        return args -> {
            generateFakeData(repository, encoder);
        };

    }

    void generateFakeData(
            CustomerRepository repository,
            PasswordEncoder encoder){
        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Faker faker = new Faker();
            Random random = new Random();
            Name name = faker.name();
            String firstName = name.firstName();
            String lastName = name.lastName();
            final int age = random.nextInt(16, 99);
            Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;
            Customer customer = new Customer(
                    firstName+" "+lastName,
                    firstName.toLowerCase()+"."+lastName.toLowerCase()+"@froshtech.org",
                    encoder.encode(UUID.randomUUID().toString()),
                    age,
                    gender);
            customers.add(customer);
        }
        repository.saveAll(customers);
    }


}