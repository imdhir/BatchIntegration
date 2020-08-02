package com.batch.config;

import java.io.File;

import org.springframework.batch.core.Job;
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
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.sftp.dsl.Sftp;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;

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

	/*
	 * @Bean
	 * 
	 * @InboundChannelAdapter(channel = "sftpChannel", poller = @Poller(fixedDelay =
	 * "${sftp.polling.interval}")) public MessageSource<File> sftpMessageSource() {
	 * SftpInboundFileSynchronizingMessageSource source = new
	 * SftpInboundFileSynchronizingMessageSource(sftpInboundFileSynchronizer());
	 * source.setLocalDirectory(new File(sftpLocalDirectory));
	 * source.setAutoCreateLocalDirectory(true); source.setLocalFilter(new
	 * AcceptOnceFileListFilter<File>()); source.setMaxFetchSize(maxFetchSize);
	 * return source; }
	 */

	/*
	 * @Bean
	 * 
	 * @ServiceActivator(inputChannel = "sftpChannel") public MessageHandler
	 * handler() { return new MessageHandler() {
	 * 
	 * @Override public void handleMessage(Message<?> message) throws
	 * MessagingException { System.out.println("*******************" +
	 * message.getPayload()); }
	 * 
	 * }; }
	 */

	@Bean
	public JobLaunchingGateway jobLaunchingGateway() {
		SimpleJobLauncher simpleJobLauncher = new SimpleJobLauncher();
		simpleJobLauncher.setJobRepository(jobRepository);
		simpleJobLauncher.setTaskExecutor(new SyncTaskExecutor());
		JobLaunchingGateway jobLaunchingGateway = new JobLaunchingGateway(simpleJobLauncher);

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
	public IntegrationFlow integrationFlow(JobLaunchingGateway jobLaunchingGateway) {

		return IntegrationFlows
				.from(Sftp.inboundAdapter(sftpSessionFactory()).remoteDirectory(sftpSourceDirectory)
						.localDirectory(new File(sftpLocalDirectory)).autoCreateLocalDirectory(true)
						.maxFetchSize(maxFetchSize).deleteRemoteFiles(false),
						// filter(new SimplePatternFileListFilter("*.csv")),
						c -> c.poller(Pollers.fixedRate(fixedDelay).maxMessagesPerPoll(1)))
				.transform(fileMessageToJobRequest()).handle(jobLaunchingGateway)
				.log(LoggingHandler.Level.WARN, "headers.id + ': ' + payload").get();
	}

	/*
	 * @Bean
	 * 
	 * @Transformer(inputChannel = "sftpChannel") public JobLaunchRequest handler(
	 * MessageSource<File> message) { JobParametersBuilder jobParametersBuilder =
	 * new JobParametersBuilder();
	 * 
	 * jobParametersBuilder.addString("fileName",
	 * message.receive().getPayload().getAbsolutePath());
	 * ///jobParametersBuilder.toJobParameters() return new JobLaunchRequest(job,
	 * null); }
	 */

}
