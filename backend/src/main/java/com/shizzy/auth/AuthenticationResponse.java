package com.shizzy.auth;

import com.shizzy.customer.CustomerDTO;

public record AuthenticationResponse (String token, CustomerDTO customerDTO){
}
