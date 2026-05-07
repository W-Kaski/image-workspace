package com.eric.imageworkspace.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "s3.client")
@Data
public class S3ClientConfig {

    private String accountId;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String publicUrl;

    @Bean
    public AmazonS3 s3Client() {
        // Construct R2 endpoint
        String endpoint = String.format("https://%s.r2.cloudflarestorage.com", accountId);
        
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTPS);

        return AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, "auto"))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withClientConfiguration(clientConfig)
                .withPathStyleAccessEnabled(true) // R2 works better with path style
                .build();
    }
}
