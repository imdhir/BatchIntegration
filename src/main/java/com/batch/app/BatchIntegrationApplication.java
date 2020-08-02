package com.batch.app;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.integration.config.EnableIntegration;

@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@EnableIntegration
@ComponentScan(basePackages={"com.batch"}) 
public class BatchIntegrationApplication {

	public static void main(String[] args) {
//		 new SpringApplicationBuilder(BatchIntegrationApplication.class)
//         .web(WebApplicationType.NONE)
//         .run(args);
		 AbstractApplicationContext context 
	      = new AnnotationConfigApplicationContext(BatchIntegrationApplication.class);
	    context.registerShutdownHook();
	}

}
