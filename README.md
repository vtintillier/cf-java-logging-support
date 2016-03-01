# cf-java-logging-support

#### For the impatient:

Version 2.x is a major rewrite of what we had before. While the usage as such should be similar, the most notable change is that you need to

* make up your mind which parts you actually need, 
* adjust your Maven dependencies accordingly, 
* pick your favorite implementation, and
* adjust your logging configuration accordingly.

While V1 provided the one-stop super jar with baked-in, implementation specific log configuration, this is now torn apart into pieces. While we still have dependencies to other stuff, we now tag almost everything as *provided*, i.e. it's your duty to get the dependency management right in your application POM.

##### Features and dependencies

Say, you want to make use the *servlet filter* feature, then you need to add the following dependency to your POM:

``` xml
<dependency>
  <groupId>com.sap.hcp.cf.logging</groupId>
  <artifactId>cf-java-logging-support-servlet</artifactId>
  <version>2.0.7</version>
</dependency>
```

This feature only depends on the servlet API which you have included in your POM anyhow.

##### Implementation variants and logging configurations

The *core* feature (on which all other features rely) is just using the `org.slf4j` API, but to actually get logs written, you need to pick an implementation feature. We now have two implementations

* `cf-java-logging-support-logback` based on [logback](http://logback.qos.ch/), and 
* `cf-java-logging-support-log4j2` based on [log4j2](http://logging.apache.org/log4j/2.x/).

Again, we don't include dependencies to those implementation backends ourselves, so you need to provide the corresponding dependencies in your POM file:

*Using logback:*

``` xml
<dependency>
	<groupId>com.sap.hcp.cf.logging</groupId>
  	<artifactId>cf-java-logging-support-logback</artifactId>
  	<version>2.0.7</version>
</dependency>

<dependency>
  	<groupId>ch.qos.logback</groupId>
   	<artifactId>logback-classic</artifactId>
   	<version>1.1.3</version>
 </dependency>
```

*Using log4j2:*

``` xml
<dependency>
	<groupId>com.sap.hcp.cf.logging</groupId>
  	<artifactId>java-logging-support-log4j2</artifactId>
  	<version>2.0.7</version>
</dependency>
<dependency>
	<groupId>org.apache.logging.log4j</groupId>
	<artifactId>log4j-slf4j-impl</artifactId>
<version>2.4.1</version>
</dependency>
	<dependency>
	<groupId>org.apache.logging.log4j</groupId>
	<artifactId>log4j-core</artifactId>
	<version>2.4.1</version>
</dependency>
```

As they have slightly different ways to do configuration, you again need to do that yourself. But we hope that we've found an easy way to accomplish that. The one thing you have to do is pick our *encoder* in your `logback.xml` if you're using `logback` or our `layout` in your `log4j2.xml`if you're using `log4j2`.

Here are sort of the minimal configurations you'd need:

*logback.xml*:

``` xml
<configuration debug="false" scan="false">
	<appender name="STDOUT-JSON" class="ch.qos.logback.core.ConsoleAppender">
       <encoder class="com.sap.hcp.cf.logback.encoder.JsonEncoder"/>
    </appender>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%date %-5level [%thread] - [%logger]- %msg%n</pattern>
        </encoder>
    </appender>
    <root level="${LOG_ROOT_LEVEL:-WARN}">
       <!-- Use 'STDOUT' instead for human-readable output -->
       <appender-ref ref="STDOUT-JSON" />
    </root>
    <logger name="com.sap.hcp.cf" level="${LOG_PERFX_LEVEL:-INFO}" />	
</configuration>
```

*log4j2.xml:*

``` xml
<Configuration 
   status="warn" strict="true"
   packages="com.sap.hcp.cf.log4j2.converter,com.sap.hcp.cf.log4j2.layout">
	<Appenders>
        <Console name="STDOUT-JSON" target="SYSTEM_OUT" follow="true">
            <JsonPatternLayout charset="utf-8"/>
        </Console>
        <Console name="STDOUT" target="SYSTEM_OUT" follow="true">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </Console>
	</Appenders>
  <Loggers>
     <Root level="${LOG_ROOT_LEVEL:-WARN}">
        <!-- Use 'STDOUT' instead for human-readable output -->
        <AppenderRef ref="STDOUT-JSON" />
     </Root>
     <Logger name="com.sap.hcp.cf" level="${LOG_PERFX_LEVEL:-INFO}"/>
  </Loggers>
</Configuration>      
```