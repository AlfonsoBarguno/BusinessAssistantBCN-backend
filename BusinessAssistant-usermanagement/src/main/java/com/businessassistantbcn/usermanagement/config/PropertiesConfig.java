package com.businessassistantbcn.usermanagement.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
@Getter
public class PropertiesConfig {

	//Security datasource properties
	@Value("${security.datasource.secret}")
	private String secret;
	@Value("${security.datasource.headerString}")
	private String headerString;
	@Value("${security.datasource.authoritiesClaim}")
	private String authoritiesClaim;
	@Value("${security.datasource.err}")
	private String err;

	//limitDb properties
	@Value("${limit-db.enabled}")
	private Boolean enabled;
	@Value("${limit-db.max-users}")
	private Integer maxUsers;
	@Value("${limit-db.err}")
	private String error;
}
