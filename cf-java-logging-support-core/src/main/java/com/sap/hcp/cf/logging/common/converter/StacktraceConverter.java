package com.sap.hcp.cf.logging.common.converter;

public abstract class StacktraceConverter {
    public abstract void convert(Throwable t, StringBuilder appendTo);

    public final static StacktraceConverter CONVERTER = new DefaultStacktraceConverter();
}
