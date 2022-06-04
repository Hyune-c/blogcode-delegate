package com.example.blogcodedelegate.file.amazons3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.example.blogcodedelegate.file.amazons3.config.AmazonS3Properties;
import java.io.File;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractAmazonS3Service {

	private final AmazonS3 amazonS3;
	private final String bucketName;

	protected AbstractAmazonS3Service(final AmazonS3Properties properties, final AmazonS3 amazonS3) {
		this.amazonS3 = amazonS3;
		this.bucketName = properties.getBucketName();
	}

	/**
	 * aws s3에 object를 업로드 합니다.
	 *
	 * @param request provider를 통해 생성할 수 있습니다.
	 */
	public PutObjectResult putObject(final PutObjectRequest request) {
		try {
			return amazonS3.putObject(request);
		} catch (final SdkClientException e) {
			log.error(e.getClass().getName(), e);
			throw new IllegalStateException();
		}
	}

	/**
	 * @param objectKey object 식별자
	 * @return bucketName이 포함된 path
	 */
	public String getObjectFullPath(final String objectKey) {
		return amazonS3.getUrl(bucketName, objectKey).getPath();
	}

	/**
	 * @param objectKey object 식별자
	 * @param targetFile 가져오는 object가 들어갈 파일
	 */
	public void getObject(final String objectKey, final File targetFile) {
		amazonS3.getObject(new GetObjectRequest(bucketName, objectKey), targetFile);
	}
}
