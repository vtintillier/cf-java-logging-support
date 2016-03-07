# Logging Sample Application



A simple CF sample application that we cloned from [cloudfoundry-sticky-session](https://github.com/jbayer/cloudfoundry-sticky-session) and tweaked to provide a sample for the logging and instrumentation features.

Currently configured to use `logback` as the logging implementation.

## Running it locally

You can use the following Maven target to run the application locally 

```sh
mvn tomcat7:run
```

Once the tomcat container has started, you can access the application via [http://localhost:8080/logging-sample-app](http://localhost:8080/logging-sample-app)

Here's a table of "interesting" request URLs you may want to load:

| URL                      | Comment                                  |
| ------------------------ | ---------------------------------------- |
| `/`                      | Handled by standard servlet. Prints all environment variables, HTTP request headers and a random "greeting" message. |
| `/<n>`                   | Like the one above, but also computes the `<n>`th Fibonacci number. Computation happens recursively, so you can use higher values for `n` to get long response times or even crash the application |
| `/stacktrace`            | Handled by standard servlet. Generates a `500` response and prints a stack trace |
| `/jersey?greeting=<msg>` | Handled by Jersey container. Will print the line `Hello from Jersey: <msg>` |
| `/jersey/forward`        | Handled by Jersey container. Will use a Jersey client to "forward" to default servlet. |
| `/jersey/forward?q=<n>`  | Again handled by Jersey container and forwarded to default servlet as `/<n>`. |