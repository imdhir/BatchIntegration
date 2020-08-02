package com.batch.config;

import java.io.File;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;

public class SuccessTransformer {
	

	@ServiceActivator
	public void toRequest(Message<File> message) {
	
		Message message1 = message;
	
	}

}