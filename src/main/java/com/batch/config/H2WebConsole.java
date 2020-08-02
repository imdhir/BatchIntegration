package com.batch.config;

import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//@Component
public class H2WebConsole {

	@Value("${h2DatabaseWebConsolePort:9094}")
	private String h2DatabaseWebConsolePort;
	private Server server = null;
	
	@PostConstruct
	public void createH2WebConsole() throws SQLException {
		this.server  = org.h2.tools.Server.createWebServer("-web","-webAllowOthers","-webPort",h2DatabaseWebConsolePort);
	}
	
	@PreDestroy
	public void stopWebConsole() {
		if(server != null) {
			server.stop();
		}
	}
}
