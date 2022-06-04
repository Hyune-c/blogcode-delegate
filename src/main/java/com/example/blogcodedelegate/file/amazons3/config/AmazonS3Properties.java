package com.example.blogcodedelegate.file.amazons3.config;

public interface AmazonS3Properties {

	String getStagePath();

	String getBucketName();

	/**
	 * s3에 연결되는 공개된 도메인이 없는 경우 호출 실패가 발생합니다.
	 * ex) private bucket
	 */
	default String getProvendorContentUrl() {
		throw new UnsupportedOperationException("잘못된 호출 입니다.");
	}
}
