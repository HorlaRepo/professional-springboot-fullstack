package com.shizzy.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao{

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        var sql = """
                SELECT id, name, email, age
                FROM customer
                """;
        return jdbcTemplate.query(sql,customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        Optional<Customer> customer;
            var sql = """
                SELECT id, name, email, age
                FROM customer
                WHERE id = ?
                """;
            List<Customer> customers = jdbcTemplate.query(sql,customerRowMapper,id);
            if(customers.isEmpty()){
                return Optional.empty();
            }
            return Optional.ofNullable(customers.get(0));
//            try {
//                customer = Optional.of((Customer) Objects.requireNonNull(jdbcTemplate.queryForObject(sql, customerRowMapper, id)));
//            } catch (Exception e){
//                return null;
//                //throw new ResourceNotFoundException("Customer not found");
//            }
//                return customer;

    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = """
                INSERT INTO customer(name, email, age)
                VALUES (?, ?, ?)
                """;
        jdbcTemplate.update(
                sql,
                customer.getName(),
                customer.getEmail(),
                customer.getAge()
        );

    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        var sql = """
                SELECT count(id)
                FROM customer
                WHERE email = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count >0;
    }

    @Override
    public void deleteCustomerById(Integer id) {
        String sql = """
                DELETE FROM customer
                WHERE id = ?
                """;
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existsPersonWithId(Integer id) {
        var sql = """
                SELECT count(id)
                FROM customer
                WHERE id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count >0;
    }

    @Override
    public void updateCustomer(Customer update) {
        if(update.getName() != null){
            String sql = "UPDATE customer SET name = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    update.getName(),
                    update.getId()
            );
        }
        if(update.getAge() != null){
            String sql = "UPDATE customer SET age = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    update.getAge(),
                    update.getId()
            );
        }
        if(update.getEmail() != null){
            String sql = "UPDATE customer SET email = ? WHERE id = ?";
            int result = jdbcTemplate.update(
                    sql,
                    update.getEmail(),
                    update.getId()
            );
        }



    }

}
