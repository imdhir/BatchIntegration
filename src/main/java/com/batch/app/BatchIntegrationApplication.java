package com.batch.app;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAutoConfiguration()
//@EnableTransactionManagement
@EnableIntegration
@ComponentScan(basePackages={"com.batch"}) 
@EntityScan(basePackages={"com.batch"}) 
//@EnableJpaRepositories(basePackages = { "com.batch.db" })
public class BatchIntegrationApplication {

	public static void main(String[] args) {
		 new SpringApplicationBuilder(BatchIntegrationApplication.class)
       
		 //.web(WebApplicationType.)
         .run(args);
		 /*AbstractApplicationContext context 
	      = new AnnotationConfigApplicationContext(BatchIntegrationApplication.class);
	    context.registerShutdownHook();*/
	}

}
