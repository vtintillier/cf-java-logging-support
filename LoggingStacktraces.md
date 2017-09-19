# Logging Stack Traces

This library provides the possibility to log stack traces within one log
message. This facilitates debugging significantly, because the complete stack
trace can be read in the correct order and does not need to be retrieved from
numerous single and unordered log messages. This library does also consider
limitations imposed by the logging pipeline. These limitations would prevent
very large stack traces from being written to Elasticsearch. If a stack trace
exceeds a total size of 55kB, it will therefore not be logged entirely. Since
the interesting lines of a stack trace are usually situated in its first and in
its last part, we will remove as few lines as necessary from its middle part
whereby the last remaining part will have twice the size of the first part.
