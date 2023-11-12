package com.shizzy.customer;

import com.shizzy.exception.DuplicateResourceException;
import com.shizzy.exception.RequestValidationException;
import com.shizzy.exception.ResourceNotFoundException;
import com.shizzy.s3.S3Buckets;
import com.shizzy.s3.S3Service;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerDao customerDao;
    private final PasswordEncoder passwordEncoder;
    private final CustomerDTOMapper customerDTOMapper;
    private final S3Service s3Service;
    private final S3Buckets buckets;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao, PasswordEncoder passwordEncoder, CustomerDTOMapper customerDTOMapper, S3Service s3Service, S3Buckets buckets){
        this.customerDao = customerDao;
        this.passwordEncoder = passwordEncoder;
        this.customerDTOMapper = customerDTOMapper;
        this.s3Service = s3Service;
        this.buckets = buckets;
    }

    public List<CustomerDTO> getAllCustomers(){
        return customerDao.selectAllCustomers()
                .stream().map(customerDTOMapper)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomer(Integer id){
        return customerDao.selectCustomerById(id)
                .map(customerDTOMapper)
                .orElseThrow(
                        ()-> new ResourceNotFoundException(
                                "customer with id [%s] not found".formatted(id)
                        )
                );
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest){
        //check if email exists before adding customer
        String email = customerRegistrationRequest.email();
        if(customerDao.existsPersonWithEmail(customerRegistrationRequest.email())){
            throw new DuplicateResourceException("email already taken");
        }

        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                passwordEncoder.encode(customerRegistrationRequest.password()),
                customerRegistrationRequest.age(),
                customerRegistrationRequest.gender());
        customerDao.insertCustomer(customer);
    }


    public void deleteCustomerById(Integer id){
        checkIfCustomerExistsOrThrow(id);
        customerDao.deleteCustomerById(id);
    }

    private void checkIfCustomerExistsOrThrow(Integer id) {
        if(!customerDao.existsPersonWithId(id)){
            throw new ResourceNotFoundException(
                    "customer with id [%s] not found ".formatted(id));
        }
    }

    public void updateCustomer(Integer id, CustomerUpdateRequest updateRequest) {
        Customer customer = customerDao.selectCustomerById(id)
                .orElseThrow(
                        ()-> new ResourceNotFoundException(
                                "customer with id [%s] not found".formatted(id)
                        )
                );

        boolean changes = false;

        if(updateRequest.name() != null && !updateRequest.name().equals(customer.getName())){
            customer.setName(updateRequest.name());
            changes = true;
        }

        if(updateRequest.age() != null && !updateRequest.age().equals(customer.getAge())){
            customer.setAge(updateRequest.age());
            changes = true;
        }

        if(updateRequest.email() != null && !updateRequest.email().equals(customer.getEmail())){
            if(customerDao.existsPersonWithEmail(updateRequest.email())){
                throw new DuplicateResourceException(
                        "email already taken"
                );
            }
            customer.setEmail(updateRequest.email());
            changes = true;
        }

        if(!changes){
            throw new RequestValidationException("no data changes found");
        }
        customerDao.updateCustomer(customer);
    }

    public void uploadCustomerProfileImage(Integer customerId, MultipartFile file) {
        checkIfCustomerExistsOrThrow(customerId);
        final String profileImageId = UUID.randomUUID().toString();
        try {

            s3Service.putObject(
                    buckets.getCustomer(),
                    "profile-images/%s/%s".formatted(customerId, profileImageId),
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new RuntimeException("failed tp upload profile image", e);
        }

        customerDao.updateCustomerProfileImageId(profileImageId, customerId);
    }

    public byte[] getCustomerProfileImage(Integer customerId) {
        var customer = customerDao.selectCustomerById(customerId)
                .map(customerDTOMapper)
                .orElseThrow(
                        ()-> new ResourceNotFoundException(
                                "customer with id [%s] profile image not found".formatted(customerId)
                        )
                );
        //TODO: Check if profileImageId is empty or null
        if(StringUtils.isBlank(customer.profileImageId())){
            throw new ResourceNotFoundException(
                    "customer with id [%s] not found".formatted(customerId)
            );
        }

        final byte[] profileImage = s3Service.getObject(
                buckets.getCustomer(),
                "profile-images/%s/%s".formatted(customerId, customer.profileImageId())
        );
        return profileImage;
    }
}
