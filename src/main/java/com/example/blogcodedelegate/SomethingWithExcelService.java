package com.example.blogcodedelegate;

import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.blogcodedelegate.file.amazons3.AbstractAmazonS3Service;
import com.example.blogcodedelegate.file.amazons3.AbstractAmazonS3UploadRequestProvider;
import com.example.blogcodedelegate.file.amazons3.AmazonS3CommonPrivateUploadRequestProvider;
import com.example.blogcodedelegate.file.amazons3.AmazonS3PrivateService;
import java.io.File;
import org.springframework.stereotype.Service;

@Service
public class SomethingWithExcelService {

	private final AbstractAmazonS3UploadRequestProvider amazonS3DelegateProvider;
	private final AbstractAmazonS3Service amazonS3Delegator;

	public SomethingWithExcelService(final AmazonS3CommonPrivateUploadRequestProvider amazonS3DelegateProvider, final AmazonS3PrivateService amazonS3Delegator) {
		this.amazonS3DelegateProvider = amazonS3DelegateProvider;
		this.amazonS3Delegator = amazonS3Delegator;
	}

	public void doSomething(final File file) {
		// 전처리 ...

		final PutObjectRequest request = amazonS3DelegateProvider.createRequest(file);
		amazonS3Delegator.putObject(request);

		// 후처리 ...
	}
}
