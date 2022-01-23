package com.sap.hcp.cf.log4j2.benchmark;

import static com.sap.hcp.cf.logging.common.request.RequestRecordBuilder.requestRecord;

import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.util.NullOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.sap.hcp.cf.logging.common.Fields;
import com.sap.hcp.cf.logging.common.Markers;
import com.sap.hcp.cf.logging.common.customfields.CustomField;
import com.sap.hcp.cf.logging.common.request.RequestRecord;

@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
public class EncodingBenchmarks {

    public static Logger LOG = LoggerFactory.getLogger(EncodingBenchmarks.class);

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        private PrintStream out;

        public String simpleLogMessage = "Simple message benchmark";
        public CustomField customField = CustomField.customField("custom of ", CustomField.class.getSimpleName());
        public String componentId = EncodingBenchmarks.class.getSimpleName();
        public String requestLayer = "benchmark";

        @Setup
        public void substituteSystemOut() {
            out = System.out;
            System.setOut(new PrintStream(new NullOutputStream()));
        }

        @TearDown
        public void resetSystemOut() {
            System.setOut(out);
        }
    }

    @Benchmark
    public void simpleLog(BenchmarkState state) {
        LOG.info(state.simpleLogMessage);
    }

    @Benchmark
    public void singleCustomFieldFromArgument(BenchmarkState state) {
        LOG.info(state.simpleLogMessage, state.customField);
    }

    @Benchmark
    public void singleCommonFieldFromMdc(BenchmarkState state) {
        MDC.put(Fields.COMPONENT_ID, state.componentId);
        LOG.info(state.simpleLogMessage);
        MDC.clear();
    }

    @Benchmark
    public void minimalRequestRecord(BenchmarkState state) {
        RequestRecord requestRecord = requestRecord(state.requestLayer).addContextTag(Fields.COMPONENT_ID,
                                                                                      state.componentId).build();
        requestRecord.start();
        requestRecord.stop();
        LOG.info(Markers.REQUEST_MARKER, "{}", requestRecord);
    }

}
