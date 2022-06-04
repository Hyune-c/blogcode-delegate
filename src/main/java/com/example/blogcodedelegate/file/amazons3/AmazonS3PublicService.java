package com.example.blogcodedelegate.file.amazons3;

import com.amazonaws.services.s3.AmazonS3;
import com.example.blogcodedelegate.file.amazons3.config.AmazonS3PublicProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AmazonS3PublicService extends AbstractAmazonS3Service {

	public AmazonS3PublicService(final AmazonS3PublicProperties properties, final AmazonS3 amazonS3) {
		super(properties, amazonS3);
	}
}
