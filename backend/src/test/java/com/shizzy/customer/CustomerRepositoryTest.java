package com.shizzy.customer;

import com.shizzy.AbstractTestContainers;
import com.shizzy.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestConfig.class})
class CustomerRepositoryTest extends AbstractTestContainers {

    @Autowired
    private CustomerRepository underTest;

    @Autowired
    private ApplicationContext context;

    @BeforeEach
    void setUp() {
        System.out.println(context.getBeanDefinitionCount());
    }

    @Test
    void existsCustomerByEmail() {
        final String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                "password", 20,
                Gender.MALE);

        underTest.save(customer);

        var actual = underTest.existsCustomerByEmail(email);

        assertThat(actual).isTrue();
    }
 
    @Test
    void existsCustomerById() {
        final String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                "password", 20,
                Gender.MALE);

        underTest.save(customer);

        Integer id = underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

       var actual = underTest.existsCustomerById(id);

        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByEmailWhenEmailNotPresent() {
        final String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();

        var actual = underTest.existsCustomerByEmail(email);

        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerByIdFailsWhenIdNotPresent() {

        Integer id = -1;

        var actual = underTest.existsCustomerById(id);

        assertThat(actual).isFalse();
    }

    @Test
    void canUpdateProfileImageId() {
        //Given
        final String email = "email@mail.com";
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                "password", 20,
                Gender.MALE
        );
        underTest.save(customer);

        Integer id = underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //When
        underTest.updateProfileImageId("222", id);

        //Then
        final Optional<Customer> customerOptional = underTest.findById(id);
        assertThat(customerOptional).isPresent().hasValueSatisfying(c -> assertThat(c.getProfileImageId()).isEqualTo("222"));
    }
}