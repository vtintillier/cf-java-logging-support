# Beat Specifications for Application Logs and Request Statistics

The two subfolders contain the YAML specifications for

* application log, and
* request (performance) statistics

["beats"](https://github.com/elastic/beats).

These specifications capture which fields should ultimately show up in ElasticSearch once they've travelled through our processing pipeline. Ideally, the entity emitting the application or request log will already put things into a proper shape, but most likely the parser component (aka Logstash) will use rule sets to do a proper transformation.

Note that we also use the specifications to generate the Java _field names_ for our [Java logging support library](https://github.wdf.sap.corp/perfx/java-logging-support).
