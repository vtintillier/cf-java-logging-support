package com.sap.hcp.cf.logging.servlet.filter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.rules.ExternalResource;

public class SystemOutRule extends ExternalResource {

	private PrintStream originalOut;
	private OutputStream output = new ByteArrayOutputStream();

	@Override
	protected void before() throws Throwable {
		this.originalOut = System.out;
		System.setOut(new PrintStream(output));
	}

	@Override
	protected void after() {
		System.setOut(originalOut);
		System.out.append(output.toString());
	};

	@Override
	public String toString() {
		return output.toString();
	}
}
