package com.shizzy.customer;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(
        origins = "http://shizzy-api-env.eba-anuuvjnu.eu-west-2.elasticbeanstalk.com:80/",
        maxAge = 3600,
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
@RestController
@RequestMapping("api/v1/customers")

public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @GetMapping
    public List<Customer> getCustomers(){
        return customerService.getAllCustomers();
    }

    @GetMapping("{id}")
    public Customer getCustomer(@PathVariable("id")Integer id) {
        return customerService.getCustomer(id);
    }

    @PostMapping
    public void registerCustomer(
            @RequestBody CustomerRegistrationRequest request){
        customerService.addCustomer(request);
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
}
