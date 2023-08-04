package com.shizzy.customer;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age
) {
}
