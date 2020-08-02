package com.batch.config;

import java.io.File;
import java.time.LocalDateTime;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.integration.launch.JobLaunchingGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileNameGenerator;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.sftp.dsl.Sftp;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;

import com.jcraft.jsch.ChannelSftp.LsEntry;

@Configuration
@ComponentScan("com.batch")
@IntegrationComponentScan
@EnableIntegration
@EnableBatchIntegration
@PropertySource("classpath:application.properties")
public class InboundConfigs {

	@Value("${sftp.host}")
	private String sftpHost;

	@Value("${sftp.port}")
	private int sftpPort;

	@Value("${sftp.username}")
	private String sftpUserName;

	@Value("${sftp.password}")
	private String sftpPassword;

	@Value("${sftp.source.directory}")
	private String sftpSourceDirectory;

	@Value("${sftp.local.directory}")
	private String sftpLocalDirectory;

	@Value("${sftp.maxFetchsize:1}")
	private int maxFetchSize;

	@Autowired
	private Job job;

	@Value("${sftp.polling.interval}")
	private int fixedDelay;

	@Autowired
	private JobRepository jobRepository;

	@Bean
	public SessionFactory<LsEntry> sftpSessionFactory() {
		DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
		factory.setHost(sftpHost);
		factory.setPort(sftpPort);
		factory.setUser(sftpUserName);
		factory.setPassword(sftpPassword);
		factory.setAllowUnknownKeys(true);
		// factory.setTestSession(true);
		return new CachingSessionFactory<LsEntry>(factory);
	}

	@Bean
	public SftpInboundFileSynchronizer sftpInboundFileSynchronizer() {
		SftpInboundFileSynchronizer fileSynchronizer = new SftpInboundFileSynchronizer(sftpSessionFactory());
		fileSynchronizer.setDeleteRemoteFiles(false);
		fileSynchronizer.setRemoteDirectory(sftpSourceDirectory);
		// fileSynchronizer.setFilter(new SftpSimplePatternFileListFilter("*.xml"));
		return fileSynchronizer;
	}

	@Bean
	public JobLaunchingGateway jobLaunchingGateway() {
		SimpleJobLauncher simpleJobLauncher = new SimpleJobLauncher();
		simpleJobLauncher.setJobRepository(jobRepository);
		simpleJobLauncher.setTaskExecutor(new SyncTaskExecutor());
		JobLaunchingGateway jobLaunchingGateway = new JobLaunchingGateway(simpleJobLauncher);
		jobLaunchingGateway.onError(new RuntimeException("*********************** ############# Error occurred"));

		return jobLaunchingGateway;
	}

	@Bean
	public FileMessageToJobRequest fileMessageToJobRequest() {
		FileMessageToJobRequest fileMessageToJobRequest = new FileMessageToJobRequest();
		fileMessageToJobRequest.setFileParameterName("input.file.name");
		fileMessageToJobRequest.setJob(job);
		return fileMessageToJobRequest;
	}

	@Bean
	public MessageChannel onSuccessChannel() {
		return MessageChannels.direct().get();
	}

	@Bean
	public IntegrationFlow integrationFlow(JobLaunchingGateway jobLaunchingGateway) {

		return IntegrationFlows
				.from(Sftp.inboundAdapter(sftpSessionFactory()).remoteDirectory(sftpSourceDirectory)
						.localDirectory(new File(sftpLocalDirectory)).autoCreateLocalDirectory(true)
						.maxFetchSize(maxFetchSize).deleteRemoteFiles(false),
						// filter(new SimplePatternFileListFilter("*.csv")),
						c -> c.poller(Pollers.fixedRate(fixedDelay).maxMessagesPerPoll(1)))
				.transform(fileMessageToJobRequest()).handle(jobLaunchingGateway)
				.log(LoggingHandler.Level.WARN, "headers.id + ': ' + payload").channel(onSuccessChannel()).get();
	}

	@Bean
	public IntegrationFlow onSuccessFlow() {

		// Sftp.outboundAdapter(sftpSessionFactory()).remoteDirectory("/processed/");

		return IntegrationFlows.from(onSuccessChannel()).transform(new GenericTransformer<JobExecution, File>() {
			public File transform(JobExecution source) {
				if(source.getStatus()==BatchStatus.FAILED) {
					throw new RuntimeException("Batch operation failed");
				}
				return new File(source.getJobParameters().getString("input.file.name"));

			};
		}).handle(Sftp.outboundAdapter(sftpSessionFactory()).remoteDirectory("/processed")
				.fileNameGenerator(new FileNameGenerator() {

					@Override
					public String generateFileName(Message<?> message) {
						return ((File) message.getPayload()).getName() + "-processed-" + LocalDateTime.now().toString().replace(":", "") + ".csv";
					}
				})).get();
	}

	@Bean
	public IntegrationFlow onErrorFlow() {

		// Sftp.outboundAdapter(sftpSessionFactory()).remoteDirectory("/processed/");

		return IntegrationFlows.from("errorChannel")
				.transform(new GenericTransformer<MessagingException, File>() {
					@Override
					public File transform(MessagingException payload) {
						System.out.println(
								"**************************** Error " + payload.getFailedMessage().getPayload());
						String fileName = ((JobExecution) payload.getFailedMessage().getPayload())
								.getJobParameters().getString("input.file.name");
						return new File(fileName);
					}
				}).handle(Sftp.outboundAdapter(sftpSessionFactory()).remoteDirectory("/processed")
						.fileNameGenerator(new FileNameGenerator() {

							@Override
							public String generateFileName(Message<?> message) {
								return ((File) message.getPayload()).getName() + "-errored-" + LocalDateTime.now().toString().replace(":", "")
										+ ".csv";
							}
						}))
				.get();

	}

}
