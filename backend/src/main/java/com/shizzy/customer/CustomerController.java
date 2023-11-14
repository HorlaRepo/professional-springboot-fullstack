package com.shizzy.customer;

import com.shizzy.jwt.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("api/v1/customers")
@Slf4j
public class CustomerController {

    private final CustomerService customerService;
    private final JWTUtil jwtUtil;

    public CustomerController(CustomerService customerService, JWTUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }


    @GetMapping
    public List<CustomerDTO> getCustomers(){
        log.info("Calling customer api");
        return customerService.getAllCustomers();
    }

    @GetMapping("{id}")
    public CustomerDTO getCustomer(@PathVariable("id")Integer id) {
        return customerService.getCustomer(id);
    }

    @PostMapping
    public ResponseEntity<?> registerCustomer(
            @RequestBody CustomerRegistrationRequest request){
        customerService.addCustomer(request);
        final String jwtToken = jwtUtil.issueToken(request.email(), "ROLE_USER");
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .build();
    }

    @DeleteMapping("{id}")
    public void deleteCustomer(@PathVariable("id") Integer id){
        customerService.deleteCustomerById(id);
    }

    @PutMapping("{id}")
    public void updateCustomer(
            @PathVariable("id") Integer id,
            @RequestBody CustomerUpdateRequest updateRequest){
        customerService.updateCustomer(id,updateRequest);
    }

    @PostMapping(value = "{customerId}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadCustomerProfilePicture(
            @PathVariable("customerId") Integer customerId,
            @RequestParam("file")MultipartFile file) {

        customerService.uploadCustomerProfileImage(customerId, file);

    }

    @GetMapping(value = "{customerId}/profile-image", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getCustomerProfilePicture(
            @PathVariable("customerId") Integer customerId) {

        return customerService.getCustomerProfileImage(customerId);

    }
}
