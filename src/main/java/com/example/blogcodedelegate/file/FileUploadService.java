package com.example.blogcodedelegate.file;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FileUploadService {

	public static Regions clientRegion = Regions.AP_NORTHEAST_2;

	public final String ROOT_PATH = "storage";

	public static final int RANDOM_CODE_SIZE = 6;

	@Value("${env.profile}")
	public String stagePath;

	@Value("${cloud.aws.credentials.accessKey}")
	private String accessKey;

	@Value("${cloud.aws.credentials.secretKey}")
	private String secretKey;

	@Value("${cloud.aws.provendor.content}")
	private String provendorContentUrl;

	@Value("${cloud.aws.bucketName}")
	public String bucketName;


	public void getAwsCredentialsForAlarm(String fileObjKeyName, InputStream inputStream, ObjectMetadata objectMetadata) {

		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(clientRegion)
				.build();

		s3Upload(fileObjKeyName, s3Client, inputStream, objectMetadata);
	}

	public String getAwsCredentialsForExcel(String fileObjKeyName, File file, MenuType menuType) {
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(clientRegion)
				.build();
		return s3UploadReturnUploadFilePath(getForderPath(menuType) + fileObjKeyName, s3Client, file);
	}

	public ObjectMetadata getObjectMetadataHtml() {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType("text/html; charset=UTF-8");
		return objectMetadata;
	}

	public InputStream getStringToInputStream(String htmlContent) {
		return new ByteArrayInputStream(htmlContent.getBytes());
	}

	private void s3Upload(String fileObjKeyName, AmazonS3 s3Client, InputStream inputStream, ObjectMetadata objectMetadata) {
		// Upload a text string as a new object.
		// s3Client.putObject(bucketName, stringObjKeyName, "Uploaded String Object");
		try {
			// Upload a file as a new object with ContentType and title specified.
			PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, inputStream, objectMetadata);
			s3Client.putObject(request);
		} catch (AmazonServiceException e) {
			// The call was transmitted successfully, but Amazon S3 couldn't process
			// it, so it returned an error response.
			log.error("AmazonServiceException", e);
			throw new IllegalStateException();
		} catch (SdkClientException e) {
			// Amazon S3 couldn't be contacted for a response, or the client
			// couldn't parse the response from Amazon S3.
			log.error("SdkClientException", e);
			throw new IllegalStateException();
		}
	}

	private String s3UploadReturnUploadFilePath(String fileObjKeyName, AmazonS3 s3Client, File file) {
		String s3UploadFilePath = null;
		try {

			PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, file);
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType("application/vnd.ms-excel; charset=UTF-8");
			request.setMetadata(metadata);
			request.withCannedAcl(CannedAccessControlList.PublicRead);
			s3Client.putObject(request);

			s3UploadFilePath = getS3UploadFileUrl(s3Client, fileObjKeyName);
		} catch (AmazonServiceException e) {
			log.error("AmazonServiceException", e);
			throw new IllegalStateException();
		} catch (SdkClientException e) {
			log.error("SdkClientException", e);
			throw new IllegalStateException();
		}
		return s3UploadFilePath;
	}

	private String getForderPath(MenuType menuType) {
		LocalDate localDate = LocalDate.now();
		LocalTime localTime = LocalTime.now();
		String randomCode = RandomStringUtils.randomNumeric(RANDOM_CODE_SIZE);

		return ROOT_PATH + "/" + stagePath + "/" + menuType.getName() +
				"/" + localDate.getYear() +
				"/" + localDate.getMonth().getValue() +
				"/" + localDate.getDayOfMonth() +
				"/" + localTime.getHour() +
				"-" + localTime.getMinute() +
				"-" + localTime.getSecond() +
				"-" + randomCode + "/";
	}

	private String getS3UploadFileUrl(AmazonS3 s3Client, String fileObjKeyName) {
		String path = s3Client.getUrl(bucketName, fileObjKeyName).getPath();
		return provendorContentUrl + path;
	}
}
