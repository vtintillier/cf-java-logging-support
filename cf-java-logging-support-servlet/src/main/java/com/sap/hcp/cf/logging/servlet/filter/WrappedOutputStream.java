package com.sap.hcp.cf.logging.servlet.filter;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

public class WrappedOutputStream extends ServletOutputStream {

    private long contentLength = -1;
    private final ServletOutputStream wrappedStream;

    public WrappedOutputStream(ServletOutputStream out) {
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
}
