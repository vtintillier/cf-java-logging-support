package com.sap.hcp.cf.logging.sample.springboot.controller;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
public class StreamController {

	@RequestMapping("/stream")
	public ResponseEntity<StreamingResponseBody> handleRequest() {

		StreamingResponseBody responseBody = new StreamingResponseBody() {
			@Override
			public void writeTo(OutputStream out) throws IOException {
				for (int i = 0; i < 1000; i++) {
					out.write((Integer.toString(i) + " - ").getBytes());
					out.flush();
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		};

		return new ResponseEntity<>(responseBody, HttpStatus.OK);
	}
}
