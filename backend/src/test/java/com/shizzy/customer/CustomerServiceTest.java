package com.shizzy.customer;

import com.shizzy.exception.DuplicateResourceException;
import com.shizzy.exception.RequestValidationException;
import com.shizzy.exception.ResourceNotFoundException;
import com.shizzy.s3.S3Buckets;
import com.shizzy.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private S3Service s3Service;
    @Mock
    private S3Buckets buckets;
    private CustomerService underTest;
    private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();



    @BeforeEach
    void setUp() {
        AutoCloseable closeable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerService(
                customerDao,
                passwordEncoder,
                customerDTOMapper,
                s3Service,
                buckets
        );
    }

    @Test
    void getAllCustomers() {
        //When
        underTest.getAllCustomers();

        //Then
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        //Given
        int id = 10;
        Customer customer = new Customer(id,"Francis","francis@gmail.com", "password", 20, Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerDTO expected = customerDTOMapper.apply(customer);
        //When
        CustomerDTO actual = underTest.getCustomer(id);

        //Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void willThrowWhenGetCustomerReturnsEmptyOptional() {
        //Given
        int id = 10;

        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        //When
        //Then
        assertThatThrownBy(()-> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));

    }

    @Test
    void addCustomer() {
        //Given
        String email = "francis@gmail.com";
        when(customerDao.existsPersonWithEmail(email)).thenReturn(false);

        final int age = 19;

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex",email, "password", age, Gender.MALE
        );

        String passwordHash = "65!!!@#ml;::as";

        when(passwordEncoder.encode(request.password())).thenReturn(passwordHash);

        //When
        underTest.addCustomer(request);

        //Then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );

        verify(customerDao).insertCustomer(argumentCaptor.capture());

        Customer capturedCustomer = argumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
        assertThat(capturedCustomer.getPassword()).isEqualTo(passwordHash);

    }

    @Test
    void willThrowWhenEmailExistsWhileAddingCustomer() {
        //Given
        String email = "francis@gmail.com";
        when(customerDao.existsPersonWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex",email, "password", 19, Gender.MALE
        );

        //When
        assertThatThrownBy(()->  underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");


        //Then

        verify(customerDao, never()).insertCustomer(any());


    }

    @Test
    void deleteCustomerById() {
        //Given
        int id = 10;

        when(customerDao.existsPersonWithId(id)).thenReturn(true);

        //When
        underTest.deleteCustomerById(id);

        //Then
        verify(customerDao).deleteCustomerById(id);
    }


    @Test
    void willThrowDeleteCustomerByIdNotExists() {
        //Given
        int id = 10;

        when(customerDao.existsPersonWithId(id)).thenReturn(false);

        //When
        assertThatThrownBy(()-> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessage("customer with id [%s] not found ".formatted(id));

        //Then
        verify(customerDao, never()).deleteCustomerById(id);
    }

    @Test
    void canUpdateAllCustomerProperties() {
        //Given
        int id = 10;
        Customer customer = new Customer(id,"Francis","francis@gmail.com", "password", 20, Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        final String newEmail = "frank@yahoo.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "Frank", newEmail,23
        );

        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);
        //When
        underTest.updateCustomer(id,updateRequest);

        //Then
        ArgumentCaptor<Customer> argumentCaptor =
                ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(argumentCaptor.capture());
        final Customer capturedCustomer = argumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());

    }

    @Test
    void canUpdateOnlyCustomerName() {
        //Given
        int id = 10;
        Customer customer = new Customer(id,"Francis","francis@gmail.com", "password", 20, Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "Frank", null,null
        );

        //When
        underTest.updateCustomer(id,updateRequest);

        //Then
        ArgumentCaptor<Customer> argumentCaptor =
                ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(argumentCaptor.capture());
        final Customer capturedCustomer = argumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());

    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        //Given
        int id = 10;
        Customer customer = new Customer(id,"Francis","francis@gmail.com", "password", 20, Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        final String newEmail = "frank@yahoo.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, newEmail,null
        );

        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);


        //When
        underTest.updateCustomer(id,updateRequest);

        //Then
        ArgumentCaptor<Customer> argumentCaptor =
                ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(argumentCaptor.capture());
        final Customer capturedCustomer = argumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());

    }

    @Test
    void canUpdateOnlyCustomerAge() {
        //Given
        int id = 10;
        Customer customer = new Customer(id,"Francis","francis@gmail.com", "password", 20, Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, null,45
        );

        //When
        underTest.updateCustomer(id,updateRequest);

        //Then
        ArgumentCaptor<Customer> argumentCaptor =
                ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(argumentCaptor.capture());
        final Customer capturedCustomer = argumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());

    }

    @Test
    void willThrowWhenTryingToUpdateCustomerEmailWhenAlreadyTaken() {
        //Given
        int id = 10;
        Customer customer = new Customer(id,"Francis","francis@gmail.com", "password", 20, Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        final String newEmail = "frank@yahoo.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null, newEmail,null
        );

        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(true);


        //When
        assertThatThrownBy(()->  underTest.updateCustomer(id,updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        //Then
        verify(customerDao, never()).updateCustomer(any());

    }

    @Test
    void willThrowWhenCustomerUpdateHasNoChanges() {
        //Given
        int id = 10;
        Customer customer = new Customer(id,"Francis","francis@gmail.com", "password", 20, Gender.MALE);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                customer.getName(), customer.getEmail(),customer.getAge()
        );


        //When
        assertThatThrownBy(()-> underTest.updateCustomer(id,updateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changes found");


        //Then

        verify(customerDao, never()).updateCustomer(any());

    }

    @Test
    void canUploadProfileImage() {
        //Given
        int customerId = 10;

        when(customerDao.existsPersonWithId(customerId)).thenReturn(true);

        final byte[] bytes = "Hello World".getBytes();
        MultipartFile multipartFile = new MockMultipartFile("file", bytes);
        final String bucket = "customer-bucket";
        when(buckets.getCustomer()).thenReturn(bucket);

        //When
        underTest.uploadCustomerProfileImage(customerId, multipartFile);

        //Then
        ArgumentCaptor<String> profileImageIdArgumentCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(customerDao).updateCustomerProfileImageId(profileImageIdArgumentCaptor.capture(), eq(customerId));

        verify(s3Service).putObject(
                bucket,
                "profile-images/%s/%s".formatted(customerId, profileImageIdArgumentCaptor.getValue()),
                bytes
        );

    }

    @Test
    void cannotUploadProfileImageWhenCustomerDoesNotExist() {
        //Given
        int customerId = 10;

        when(customerDao.existsPersonWithId(customerId)).thenReturn(false);

        //When
        assertThatThrownBy(() -> {underTest.uploadCustomerProfileImage(customerId, mock(MultipartFile.class));})
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id ["+customerId+"] not found ");

        //Then
        verify(customerDao).existsPersonWithId(customerId);
        verifyNoMoreInteractions(customerDao);
        verifyNoInteractions(buckets);
        verifyNoInteractions(s3Service);

    }

    @Test
    void cannotUploadProfileImageWhenExceptionIsThrown() throws IOException {
        //Given
        int customerId = 10;


        when(customerDao.existsPersonWithId(customerId)).thenReturn(true);

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getBytes()).thenThrow(IOException.class);

        final String bucket = "customer-bucket";

        when(buckets.getCustomer()).thenReturn(bucket);

        //When
        assertThatThrownBy(()-> {
            underTest.uploadCustomerProfileImage(customerId, multipartFile);
        }).isInstanceOf(RuntimeException.class)
                .hasMessage("failed tp upload profile image")
                .hasRootCauseInstanceOf(IOException.class);
        //Then

        verify(customerDao, never()).updateCustomerProfileImageId(any(), any());


    }

    @Test
    void canDownloadCustomerImage() {
        //Given
        String profileImageId = "2222";
        int customerId = 10;
        Customer customer = new Customer(
                customerId,
                "Francis",
                "francis@gmail.com",
                "password",
                20,
                Gender.MALE,
                profileImageId

        );
        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        final String bucket = "customer-bucket";
        when(buckets.getCustomer()).thenReturn(bucket);

        final byte[] expectedImage = "image".getBytes();
        when(s3Service.getObject(bucket, "profile-images/%s/%s".formatted(customerId, profileImageId )))
                .thenReturn(expectedImage);

        //When
       byte [] actualImage = underTest.getCustomerProfileImage(customerId);

        //Then
        assertThat(actualImage).isEqualTo(expectedImage);

    }

    @Test
    void cannotDownloadWhenNoProfileImageId() {
        //Given
        int customerId = 10;
        Customer customer = new Customer(
                customerId,
                "Francis",
                "francis@gmail.com",
                "password",
                20,
                Gender.MALE
        );

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.of(customer));

        //When
        //Then
        assertThatThrownBy(()-> underTest.getCustomerProfileImage(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(customerId));

        verifyNoInteractions(buckets);
        verifyNoInteractions(s3Service);

    }

    @Test
    void cannotDownloadCustomerImageWhenCustomerDoesNotExists() {
        //Given
        int customerId = 10;

        when(customerDao.selectCustomerById(customerId)).thenReturn(Optional.empty());

        //When
        //Then
        assertThatThrownBy(()-> underTest.getCustomerProfileImage(customerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] profile image not found".formatted(customerId));

        verifyNoInteractions(buckets);
        verifyNoInteractions(s3Service);

    }
}