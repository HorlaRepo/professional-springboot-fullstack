package com.shizzy.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    private S3Service underTest;

    @BeforeEach
    void setUp() {
        underTest = new S3Service(s3Client);
    }

    @Test
    void canPutObject() throws IOException {
        //Given
        final String bucket = "customer";
        final String key = "foo";
        final byte[] data = "Hello world".getBytes();

        //When
        underTest.putObject(bucket, key, data);

        //Then
        ArgumentCaptor<PutObjectRequest> putObjectRequestArgumentCaptor =
                ArgumentCaptor.forClass(PutObjectRequest.class);
        ArgumentCaptor<RequestBody> requestBodyArgumentCaptor =
                ArgumentCaptor.forClass(RequestBody.class);
        verify(s3Client).putObject(
                putObjectRequestArgumentCaptor.capture(),
                requestBodyArgumentCaptor.capture()
        );

        PutObjectRequest putObjectRequest = putObjectRequestArgumentCaptor.getValue();

        assertThat(putObjectRequest.bucket()).isEqualTo(bucket);
        assertThat(putObjectRequest.key()).isEqualTo(key);

        final RequestBody argumentCaptorValue = requestBodyArgumentCaptor.getValue();

        assertThat(argumentCaptorValue.contentStreamProvider().newStream().readAllBytes()).isEqualTo(RequestBody.fromBytes(data).contentStreamProvider().newStream().readAllBytes());

    }

    @Test
    void getObject() throws IOException {
        //Given
        final String bucket = "customer";
        final String key = "foo";
        final byte[] data = "Hello world".getBytes();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                        .bucket(bucket)
                                .key(key)
                                        .build();

        final ResponseInputStream<GetObjectResponse> res = mock(ResponseInputStream.class);
        when(res.readAllBytes()).thenReturn(data);


        when(s3Client.getObject(eq(getObjectRequest))).thenReturn(res);

        //When
        final byte[] bytes = underTest.getObject(bucket, key);

        //Then
        assertThat(bytes).isEqualTo(data);
    }

    @Test
    void WillThrowWhenGetObject() throws IOException {
        //Given
        final String bucket = "customer";
        final String key = "foo";
        final byte[] data = "Hello world".getBytes();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        final ResponseInputStream<GetObjectResponse> res = mock(ResponseInputStream.class);
        when(res.readAllBytes()).thenThrow(new IOException("Cannot read bytes"));


        when(s3Client.getObject(eq(getObjectRequest))).thenReturn(res);

        //When
        //Then
        assertThatThrownBy(() -> underTest.getObject(bucket, key))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cannot read bytes")
                .hasRootCauseInstanceOf(IOException.class);


    }
}