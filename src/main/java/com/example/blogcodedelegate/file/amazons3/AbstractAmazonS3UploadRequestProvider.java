package com.example.blogcodedelegate.file.amazons3;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.blogcodedelegate.file.amazons3.config.AmazonS3Properties;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Locale;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.StringUtils;

@Slf4j
public abstract class AbstractAmazonS3UploadRequestProvider {

	private final String stagePath;
	private final String bucketName;

	protected AbstractAmazonS3UploadRequestProvider(final AmazonS3Properties properties) {
		this.stagePath = properties.getStagePath();
		this.bucketName = properties.getBucketName();
	}

	/**
	 * service를 사용하기 위한 request 생성 메서드 - recommend
	 *
	 * @param file 실 upload 파일. objectKey에 fileName이 들어갑니다.
	 * @return upload 기능을 사용하기 위한 request
	 */
	public PutObjectRequest createRequest(final File file) {
		return createRequest(file.getName(), file);
	}

	/**
	 * service를 사용하기 위한 request 생성 메서드
	 *
	 * @param sourceName objectKey에 파일 이름 대신 들어갈 값
	 * @param file 실 upload 파일
	 * @return upload 기능을 사용하기 위한 request
	 */
	public abstract PutObjectRequest createRequest(final String sourceName, final File file);

	/**
	 * 각 서비스에서 public createRequest를 구현할 때 활용됩니다.
	 *
	 * @param objectKey object 고유 식별자
	 * @param cannedAclHeader 업로드된 s3 파일 공개 범위
	 */
	protected PutObjectRequest createRequest(final String objectKey, final File file, final CannedAccessControlList cannedAclHeader) {
		final PutObjectRequest request = new PutObjectRequest(bucketName, objectKey, file);
		request.setMetadata(ObjectMetadataType.getMetadata(file));
		request.withCannedAcl(cannedAclHeader);

		return request;
	}

	/**
	 * @return sourceName에 환경 정보와 hash를 포함해서 objectKey를 생성합니다.
	 */
	protected String generateObjectKey(final String sourceName) {
		final LocalDateTime now = LocalDateTime.now();
		final String hash = LocalDateTimeUtils.toIsoFormat(now) + "_" + DigestUtils.md5Hex(String.valueOf(now.getNano()));
		return stagePath + "/" + hash + "/" + sourceName;
	}

	@Getter
	@RequiredArgsConstructor
	public enum ObjectMetadataType {

		CSV("text/csv; charset=UTF-8"),
		XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=UTF-8"),
		XLS("application/vnd.ms-excel; charset=UTF-8"),
		HTML("text/html; charset=UTF-8");

		private final String contentType;

		public static ObjectMetadataType find(final String filenameExtension) {
			return Arrays.stream(ObjectMetadataType.values())
					.filter(type -> type.name().toLowerCase(Locale.ROOT).equals(filenameExtension))
					.findFirst()
					.orElseThrow(() -> new IllegalArgumentException("정의되지 않은 파일 확장자"));
		}

		public static ObjectMetadataType find(final File file) {
			return find(StringUtils.getFilenameExtension(file.getName()));
		}

		public static ObjectMetadata getMetadata(final File file) {
			final ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(find(file).getContentType());
			return metadata;
		}
	}
}
