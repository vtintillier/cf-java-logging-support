package com.sap.hcp.cf.logging.sample.springboot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * This controller provides an endpoint to generate logs. This can be used to
 * test logging.
 */
@RestController
public class LogController {

	private static final String DEFAULT_LOG_MESSAGE = "This is the default log message!";
	private Counter logsCount;
	private Counter logsChars;

	public LogController(@Autowired MeterRegistry meterRegistry) {
		logsCount = Counter.builder("logs.generated.count").description("The number of generated log messages")
				.register(meterRegistry);
		logsChars = Counter.builder("logs.generated.chars").description("The number of generated characters")
				.register(meterRegistry);
	}

	/**
	 * Generate a log event with the given parameters.
	 * 
	 * @param loggerName the name of the logger to use
	 * @param logLevel   the level to use, allowed values: error, warn, info, debug,
	 *                   trace
	 * @param message    the message to log, defaults to
	 *                   {@value #DEFAULT_LOG_MESSAGE}
	 * @return a response containing the emitted message
	 */
	@PostMapping("/log/{logger}/{logLevel}")
	public ResponseEntity<String> generateLog(@PathVariable("logger") String loggerName,
			@PathVariable("logLevel") String logLevel,
			@RequestParam(name = "m", required = false, defaultValue = DEFAULT_LOG_MESSAGE) String message) {
		Logger logger = LoggerFactory.getLogger(loggerName);
		switch (logLevel.toLowerCase()) {
		case "error":
			logger.error(message);
			logsCount.increment();
			logsChars.increment(message.length());
			return ResponseEntity.ok().body("Generated error log with message: \"" + message + "\".");
		case "warn":
		case "warning":
			logger.warn(message);
			logsCount.increment();
			logsChars.increment(message.length());
			return ResponseEntity.ok().body("Generated warn log with message: \"" + message + "\".");
		case "info":
		case "informational":
			logger.info(message);
			logsCount.increment();
			logsChars.increment(message.length());
			return ResponseEntity.ok().body("Generated info log with message: \"" + message + "\".");
		case "debug":
			logger.debug(message);
			logsCount.increment();
			logsChars.increment(message.length());
			return ResponseEntity.ok().body("Generated debug log with message: \"" + message + "\".");
		case "trace":
			logger.trace(message);
			logsCount.increment();
			logsChars.increment(message.length());
			return ResponseEntity.ok().body("Generated trace log with message: \"" + message + "\".");
		}
		return ResponseEntity.badRequest().body("Unknows log level \"" + logLevel + "\".");
	}
}
