# Dynamic Log Levels

## Summary

Dynamic Log Levels enable application developers to set a specific log level for
just one thread executed by their application. This feature allows to understand
misbehavior of a live-system in detail for a specific request, without flooding
the system with an enormous number of DEBUG level log messages emitted during 
the processing of all other requests. This approach has the advantage that no
unnecessary log messages need to be processed that might lead to quota
exceedance. To avoid abuse of this feature, the dynamic log level has to be 
provided within an RSA-signed JWT token that also contains an expiry date.

## App-Configuration

This feature is easy to use and requires only little extra configuration of the
application. Depending on the logging framework in use, few additions have to be
made to the logback.xml or the log4j2.xml file as described below. Regardless of
the framework, the public RSA key required to verify the JWT token's signature
must be provided in the application's environment variables. Furthermore an
individually chosen name for the HTTPS header field can also be specified here.

### Environment Variables

- `DYN_LOG_HEADER`: a specific header name for the log level token can be 
  defined here. If not specified, the default value (SAP-LOG-LEVEL) is used.

- `DYN_LOG_LEVEL_KEY`: a public key which can be used to verify the JWT 
  tokens that contain the dynamic log level.

### Logback Specific Configuration

In the logback.xml file, a turbofilter has to be defined by adding the following
line to the configuration element:

```xml
<turboFilter class="com.sap.hcp.cf.logback.filter.CustomLoggingTurboFilter" />
```

### Log4j2 Specific Configuration

In the log4j2.xml file, add the following to the configuration element:

```xml
<DynamicThresholdFilter key="dynamic_log_level"
    defaultThreshold="ERROR" onMatch="ACCEPT" onMismatch="DENY">
    <KeyValuePair key="TRACE" value="TRACE" />
    <KeyValuePair key="DEBUG" value="DEBUG" />
    <KeyValuePair key="INFO" value="INFO" />
    <KeyValuePair key="WARN" value="WARN" />
    <KeyValuePair key="ERROR" value="ERROR" />
</DynamicThresholdFilter>
```

## Usage

### What should a valid token look like

A valid JWT token should be signed with RS256. Its payload should contain the
email of the issuer, the desired log-level, a timestamp for the time at which
the token has been generated and a timestamp that represents the expiry date.
The Java class [TokenCreator] can be used to create valid tokens.

[TokenCreator]: ./cf-java-logging-support-servlet/src/main/java/com/sap/hcp/cf/logging/servlet/dynlog/TokenCreator.java

#### Header

```json
{
  "alg": "RS256",
  "typ": "JWT"
}
```

#### Payload

```json
{
  "issuer": "firstname.lastname@sap.com",
  "level": "TRACE",
  "iat": 1506016127,
  "exp": 1506188927
}
```
