package io.humourmind.rsocketserver.config;

import org.springframework.boot.rsocket.server.ServerRSocketFactoryProcessor;
import org.springframework.stereotype.Component;

import io.rsocket.RSocketFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ResumeConfig implements ServerRSocketFactoryProcessor {

	@Override
	public RSocketFactory.ServerRSocketFactory process(
			RSocketFactory.ServerRSocketFactory factory) {
		log.info("Supporting server resumption feature");
		return factory.resume();
	}
}
