package com.shizzy.customer;

import java.util.List;

public record CustomerDTO (
        Integer id,
        String name,
        String email,
        Gender gender,
        Integer age,
        List<String> roles,
        String username

){
}
