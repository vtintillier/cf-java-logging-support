package com.sap.hcp.cf.logging.servlet.filter;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

public class WrappedOutputStream extends ServletOutputStream {

	private long contentLength = -1;
	private final OutputStream wrappedStream;

	public WrappedOutputStream(OutputStream out) {
		wrappedStream = out;
	}

	public long getContentLength() {
		return contentLength;
	}

	@Override
	public void write(int b) throws IOException {
		wrappedStream.write(b);
		incrContentLength(1);
	}

	private void incrContentLength(int i) {
		if (contentLength == -1) {
			contentLength = i;
		} else {
			contentLength += i;
		}
	}

	@Override
	public void write(byte[] b) throws IOException {
		wrappedStream.write(b);
		incrContentLength(b.length);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		wrappedStream.write(b, off, len);
		incrContentLength(len);
	}

	@Override
	public void close() throws IOException {
		wrappedStream.close();
	}

	@Override
	public void flush() throws IOException {
		wrappedStream.flush();
	}
}
