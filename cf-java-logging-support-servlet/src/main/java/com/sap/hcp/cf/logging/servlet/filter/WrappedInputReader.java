package com.sap.hcp.cf.logging.servlet.filter;

import java.io.BufferedReader;
import java.io.IOException;

public class WrappedInputReader extends BufferedReader {

  private final BufferedReader wrappedReader;
  private int contentLength = -1;

  protected WrappedInputReader(BufferedReader in) {
    super(in, 1);  // unused
    this.wrappedReader = in;
  }

  public int getContentLength() {
    return contentLength;
  }

  @Override
  public int read() throws IOException {
    int c = wrappedReader.read();
    if (c != -1) {
      incrContentLength(1);
    }
    return c;
  }

  @Override
  public int read(char[] cbuf, int off, int len) throws IOException {
    int c = wrappedReader.read(cbuf, off, len);
    if (c != -1) {
      incrContentLength(c);
    }
    return c;
  }

  @Override
  public String readLine() throws IOException {
    String s = wrappedReader.readLine();
    if (s != null) {
      incrContentLength(s.length());
    }
    return s;
  }

  private void incrContentLength(int i) {
    if (contentLength == -1) {
      contentLength = i;
    }
    else {
      contentLength += i;
    }
  }
}
