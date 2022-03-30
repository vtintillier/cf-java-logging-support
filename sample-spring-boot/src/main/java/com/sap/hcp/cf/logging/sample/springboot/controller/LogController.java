package com.sap.hcp.cf.logging.sample.springboot.controller;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller provides an endpoint to generate logs. This can be used to
 * test logging.
 */
@RestController
public class LogController {

	private static final String DEFAULT_LOG_MESSAGE = "This is the default log message!";

	@PostConstruct
	public void logFramework() {
		Logger logger = LoggerFactory.getLogger(LogController.class);
		logger.info("Logger implementation class: {}", logger.getClass());
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
			@RequestParam(name = "m", required = false, defaultValue = DEFAULT_LOG_MESSAGE) String message,
			@RequestParam(name = "n", required = false, defaultValue = "1") int number) {
		Logger logger = LoggerFactory.getLogger(loggerName);
		switch (logLevel.toLowerCase()) {
		case "error":
			for (int i = 0; i < number; i++) {
				logger.error(message);
			}
			return ResponseEntity.ok().body("Generated error log with message: \"" + message + "\".");
		case "warn":
		case "warning":
			for (int i = 0; i < number; i++) {
				logger.warn(message);
			}
			return ResponseEntity.ok().body("Generated warn log with message: \"" + message + "\".");
		case "info":
		case "informational":
			for (int i = 0; i < number; i++) {
				logger.info(message);
			}
			return ResponseEntity.ok().body("Generated info log with message: \"" + message + "\".");
		case "debug":
			for (int i = 0; i < number; i++) {
				logger.debug(message);
			}
			return ResponseEntity.ok().body("Generated debug log with message: \"" + message + "\".");
		case "trace":
			for (int i = 0; i < number; i++) {
				logger.trace(message);
			}
			return ResponseEntity.ok().body("Generated trace log with message: \"" + message + "\".");
		}
		return ResponseEntity.badRequest().body("Unknows log level \"" + logLevel + "\".");
	}
}
