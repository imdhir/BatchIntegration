package com.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.batch.db.RecordRepository;
import com.batch.tasklet.LinesProcessor;
import com.batch.tasklet.LinesReader;
import com.batch.tasklet.LinesWriter;

@Configuration
@EnableBatchProcessing
@EnableBatchIntegration
@ComponentScan("com.batch")
public class BatchConfig {

	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;

	@Autowired
	private RecordRepository recordRepository;
	
	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private JobLauncher jobLauncher;

	/*
	 * @Bean public JobLauncherTestUtils jobLauncherTestUtils() { return new
	 * JobLauncherTestUtils(); }
	 */

	/*@Bean
	public JobRepository jobRepository() throws Exception {
		MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean();
		factory.setTransactionManager(transactionManager());
		return (JobRepository) factory.getObject();
	}*/

	/*@Bean
	public PlatformTransactionManager transactionManager() {
		return new ResourcelessTransactionManager();
	}*/

	/*@Bean
	public JobLauncher jobLauncher() throws Exception {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		jobLauncher.setJobRepository(jobRepository);
		return jobLauncher;
	}*/

	@Bean
	public LinesReader linesReader() {
		return new LinesReader();
	}

	@Bean
	public LinesProcessor linesProcessor() {
		return new LinesProcessor(recordRepository);
	}

	@Bean
	public LinesWriter linesWriter() {
		return new LinesWriter();
	}

	@Bean
	protected Step readLines() {

		return steps.get("readLines").tasklet(linesReader()).build();
	}

	@Bean
	protected Step processLines() {
		return steps.get("processLines").tasklet(linesProcessor()).build();
	}

	@Bean
	protected Step writeLines() {
		return steps.get("writeLines").tasklet(linesWriter()).build();
	}

	@Bean
	public Job job() {
		return jobs.get("taskletsJob").start(readLines()).next(processLines()).build();
				//.next(writeLines()).build();
	}

}
