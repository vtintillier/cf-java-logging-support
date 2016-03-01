package com.sap.hcp.cf.logging.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Version {

  public static final String UNKNOWN = "???";
  private static final String POM_PROPS_PATH = "META-INF/maven/com.sap.hcp.perfx.logging/java-logging-support-core/pom.properties";
  private static final String KEY_VERSION = "version";

  public static final Version INSTANCE = new Version();

  private final String versionNumber;

  private Version() {
    versionNumber = readVersionNumber();
  }

  private String readVersionNumber() {
    Properties props = new Properties();
    InputStream in = ClassLoader.getSystemResourceAsStream(POM_PROPS_PATH);
    try {
      props.load(in);
      return props.getProperty(KEY_VERSION, UNKNOWN);
    } catch (Exception e) {
      return UNKNOWN;
    }
  }

  public String getVersionNumber() {
    return versionNumber;
  }

  public static void main(String [] args) {
    System.out.println("Version is " + INSTANCE.getVersionNumber());
  }
}
