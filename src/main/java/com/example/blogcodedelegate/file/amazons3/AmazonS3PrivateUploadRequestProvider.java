package com.example.blogcodedelegate.file.amazons3;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.blogcodedelegate.file.amazons3.config.AmazonS3PrivateProperties;
import java.io.File;
import org.springframework.stereotype.Component;

@Component
public class AmazonS3PrivateUploadRequestProvider extends AbstractAmazonS3UploadRequestProvider {

	private static final CannedAccessControlList CANNED_ACL_HEADER = CannedAccessControlList.Private;

	public AmazonS3PrivateUploadRequestProvider(final AmazonS3PrivateProperties properties) {
		super(properties);
	}

	@Override
	public PutObjectRequest createRequest(final String sourceName, final File file) {
		final String objectKey = generateObjectKey(sourceName);
		return createRequest(objectKey, file, CANNED_ACL_HEADER);
	}
}
