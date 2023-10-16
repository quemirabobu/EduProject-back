package com.bit.eduventure.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Configuration {

    @Value("${cloud.ncp.access.key}") // application.properties에서 aws.accessKey 설정
    private String cloudAccessKey;

    @Value("${cloud.ncp.secret.key}") // application.properties에서 aws.secretKey 설정
    private String cloudSecretKey;

    @Value("${cloud.aws.region.static}") // application.properties에서 aws.s3.region 설정
    private String cloudRegion;

    @Value("${cloud.aws.s3.endpoint}") // application.properties에서 aws.s3.endpointUrl 설정
    private String cloudEndPoint;

    @Bean
    public AmazonS3 amazonS3Client() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(cloudAccessKey, cloudSecretKey);
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(cloudEndPoint, cloudRegion))
                .build();
    }
}