package com.example.blogcodedelegate.file.amazons3.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AmazonS3BasePrivateProperties implements AmazonS3PrivateProperties {

	@Value("${env.profile}")
	private String stagePath;

	@Value("${cloud.aws.bucket.private}")
	private String bucketName;
}

