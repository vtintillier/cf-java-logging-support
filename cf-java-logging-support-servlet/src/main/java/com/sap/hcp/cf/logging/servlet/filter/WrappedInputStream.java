package com.sap.hcp.cf.logging.servlet.filter;

import java.io.IOException;

import javax.servlet.ServletInputStream;

/**
 *
 */
public class WrappedInputStream extends ServletInputStream {

    private int contentLength = -1;
    private final ServletInputStream wrappedStream;

    public int getContentLength() {
        return contentLength;
    }

    protected WrappedInputStream(ServletInputStream out) {
        wrappedStream = out;
    }

    @Override
    public int read() throws IOException {
        int c = wrappedStream.read();
        if (c != -1) {
            incrContentLength(1);
        }
        return c;
    }

    @Override
    public int readLine(byte[] b, int off, int len) throws IOException {
        int retLen = wrappedStream.readLine(b, off, len);
        if (retLen != -1) {
            incrContentLength(retLen);
        }
        return retLen;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int len = wrappedStream.read(b);
        if (len != -1) {
            incrContentLength(len);
        }
        return len;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return wrappedStream.read(b, off, len);
    }

    private void incrContentLength(int i) {
        if (contentLength == -1) {
            contentLength = i;
        } else {
            contentLength += i;
        }
    }
}
