# Beat Specifications for Application Logs and Request Metrics

The two subfolders contain the YAML specifications for

* application logs, and
* request (performance) metrics

["beats"](https://github.com/elastic/beats).

Although we can't use the corresponding software libraries (as we're talking about logging in Java, not in Go), having specifications written in that format still are of help if you consider ingesting such messages in ElasticSearch, as we do. Actually, we do automatically generate the Java field names that we use to store the data from those specification files.

These specifications capture which fields should ultimately show up in ElasticSearch once they've travelled through a typical ELK processing pipeline. Ideally, the entity emitting the application log or request metrics will already put things into a proper shape, but most likely the parser component ( Logstash) will use rule sets to perform the necessary additions and/or transformations.



