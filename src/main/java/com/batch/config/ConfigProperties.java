package com.batch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties()
public class ConfigProperties {

	private String sftpHost;

	private int sftpPort;

	private String sftpUserName;

	private String sftpPassword;

	private String sftpSourceDirectory;

	private String sftpLocalDirectory;

	private int maxFetchSize;

	private String sftpPollingInterval;

	public String getSftpHost() {
		return sftpHost;
	}

	public void setSftpHost(String sftpHost) {
		this.sftpHost = sftpHost;
	}

	public int getSftpPort() {
		return sftpPort;
	}

	public void setSftpPort(int sftpPort) {
		this.sftpPort = sftpPort;
	}

	public String getSftpUserName() {
		return sftpUserName;
	}

	public void setSftpUserName(String sftpUserName) {
		this.sftpUserName = sftpUserName;
	}

	public String getSftpPassword() {
		return sftpPassword;
	}

	public void setSftpPassword(String sftpPassword) {
		this.sftpPassword = sftpPassword;
	}

	public String getSftpSourceDirectory() {
		return sftpSourceDirectory;
	}

	public void setSftpSourceDirectory(String sftpSourceDirectory) {
		this.sftpSourceDirectory = sftpSourceDirectory;
	}

	public String getSftpLocalDirectory() {
		return sftpLocalDirectory;
	}

	public void setSftpLocalDirectory(String sftpLocalDirectory) {
		this.sftpLocalDirectory = sftpLocalDirectory;
	}

	public int getMaxFetchSize() {
		return maxFetchSize;
	}

	public void setMaxFetchSize(int maxFetchSize) {
		this.maxFetchSize = maxFetchSize;
	}

	public String getSftpPollingInterval() {
		return sftpPollingInterval;
	}

	public void setSftpPollingInterval(String sftpPollingInterval) {
		this.sftpPollingInterval = sftpPollingInterval;
	}

}
