package com.shizzy.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDao{

    private static final List<Customer> customers;

    static{
        customers = new ArrayList<>();
        Customer alex = new Customer(
                "Alex",
                "alex@gmail.com",
                "password",
                21,
                Gender.MALE

        );

        Customer tobi = new Customer(
                "Tobi",
                "tobi@gmail.com",
                "password",
                23,
                Gender.MALE
        );
        customers.add(alex);
        customers.add(tobi);
    }
    @Override
    public List<Customer> selectAllCustomers() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        return customers.stream()
                .filter(customer -> customer.getId().equals(id))
                .findFirst();

    }

    @Override
    public void insertCustomer(Customer customer) {

    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        return customers.stream()
                .anyMatch(c -> c.getEmail().equals(email));
    }

    @Override
    public void deleteCustomerById(Integer id) {
        customers.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .ifPresent(customers::remove);
    }

    @Override
    public boolean existsPersonWithId(Integer id) {
        return customers.stream()
                .anyMatch(c -> c.getId().equals(id));
    }

    @Override
    public void updateCustomer(Customer update) {
        customers.add(update);
    }

    @Override
    public Optional<Customer> selectUserByEmail(String email) {
        return customers.stream()
                .filter(customer -> customer.getUsername().equals(email))
                .findFirst();
    }


}
