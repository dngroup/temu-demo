package com.jbq.db.mddash.dockermgt.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;


@Component
public class DockerConfig{
	
	@Bean
	public DockerClient dockerClient(){
		return DockerClientBuilder.getInstance(
				"http://localhost:2375").build();
		
	}

}