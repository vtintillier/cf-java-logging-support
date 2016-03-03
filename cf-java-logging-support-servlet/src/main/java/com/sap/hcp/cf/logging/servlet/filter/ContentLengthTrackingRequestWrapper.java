package com.sap.hcp.cf.logging.servlet.filter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;

/**
 *
 */
public class ContentLengthTrackingRequestWrapper extends HttpServletRequestWrapper {

  private final HttpServletRequest request;
  private WrappedInputReader wrappedReader = null;
  private WrappedInputStream wrappedStream = null;

  /**
   * Constructs a request object wrapping the given request.
   *
   * @param request
   * @throws IllegalArgumentException if the request is null
   */
  public ContentLengthTrackingRequestWrapper(HttpServletRequest request) {
    super(request);
    this.request = request;
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    this.wrappedStream = new WrappedInputStream(super.getInputStream());
    return this.wrappedStream;
  }

  @Override
  public BufferedReader getReader() throws IOException {
    this.wrappedReader = new WrappedInputReader(super.getReader());
    return this.wrappedReader;
  }

  @Override
  public int getContentLength() {
    if (this.wrappedReader != null) {
      return this.wrappedReader.getContentLength();
    }
    else {
      if (this.wrappedStream != null) {
        return this.wrappedStream.getContentLength();
      } else {
        return -1;
      }
    }
  }
}
