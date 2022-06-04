package com.example.blogcodedelegate.file.amazons3.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AmazonS3PublicProperties implements AmazonS3Properties {

	@Value("${env.profile}")
	private String stagePath;

	@Value("${cloud.aws.provendor.content}")
	private String provendorContentUrl;

	@Value("${cloud.aws.bucket.public}")
	private String bucketName;
}
