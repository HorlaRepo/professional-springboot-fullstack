package com.shizzy.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;

    @Mock
    private  CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
        //System.out.println(underTest.selectAllCustomers().size());
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        System.out.println(underTest.selectAllCustomers().size());

        verify(customerRepository)
                .findAll();
    }

    @Test
    void selectCustomerById() {

        //Given
        int id  = 1;

        //When
        underTest.selectCustomerById(id);

        //Then
        verify(customerRepository)
                .findById(id);
    }

    @Test
    void insertCustomer() {
        Customer customer = new Customer(
                1,"Francis","francis@mail.com",27,
                Gender.MALE);

        underTest.insertCustomer(customer);

        verify(customerRepository).save(customer);
    }

    @Test
    void existsPersonWithEmail() {
        String email = "fran64biz@gmail.com";

        underTest.existsPersonWithEmail(email);

        verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void deleteCustomerById() {
        int id = 1;

        underTest.deleteCustomerById(id);

        verify(customerRepository).deleteById(id);
    }

    @Test
    void existsPersonWithId() {
        int id = 1;

        underTest.existsPersonWithId(id);

        verify(customerRepository).existsById(id);
    }

    @Test
    void updateCustomer() {
        Customer customer = new Customer(
                1,"Francis","francis@mail.com",27,
                Gender.MALE);

        underTest.updateCustomer(customer);

        verify(customerRepository).save(customer);

    }
}