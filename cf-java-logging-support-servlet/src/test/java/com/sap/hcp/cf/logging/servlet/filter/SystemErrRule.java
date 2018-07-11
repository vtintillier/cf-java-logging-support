package com.sap.hcp.cf.logging.servlet.filter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.rules.ExternalResource;

public class SystemErrRule extends ExternalResource {

	private PrintStream originalErr;
	private OutputStream output = new ByteArrayOutputStream();

	@Override
	protected void before() throws Throwable {
		this.originalErr = System.err;
		System.setErr(new PrintStream(output));
	}

	@Override
	protected void after() {
		System.setOut(originalErr);
		System.out.append(output.toString());
	};

	@Override
	public String toString() {
		return output.toString();
	}
}
