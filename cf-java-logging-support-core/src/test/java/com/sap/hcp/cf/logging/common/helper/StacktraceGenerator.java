package com.sap.hcp.cf.logging.common.helper;

public class StacktraceGenerator {
    private int f1RecursionDepth;
    private int f2RecursionDepth;
    private int f3RecursionDepth;

    public StacktraceGenerator(int f1_RecursionDepth, int f2_RecursionDepth, int f3_RecursionDepth) {
        this.f1RecursionDepth = f1_RecursionDepth;
        this.f2RecursionDepth = f2_RecursionDepth;
        this.f3RecursionDepth = f3_RecursionDepth;
    }

    public IllegalArgumentException generateException() {
        try {
            f1IsASimpleFunctionWithAnExceptionallyLongName(0);
        } catch (IllegalArgumentException ex) {
            return ex;
        }
        return null;
    }

    private void f1IsASimpleFunctionWithAnExceptionallyLongName(int i) {
        if (i < f1RecursionDepth) {
            f1IsASimpleFunctionWithAnExceptionallyLongName(i + 1);
        } else {
            f2IsASimpleFunctionWithAnExceptionallyLongName(0);
        }
    }

    private void f2IsASimpleFunctionWithAnExceptionallyLongName(int i) {
        if (i < f2RecursionDepth) {
            f2IsASimpleFunctionWithAnExceptionallyLongName(i + 1);
        } else {
            f3IsASimpleFunctionWithAnExceptionallyLongName(0);
        }
    }

    private void f3IsASimpleFunctionWithAnExceptionallyLongName(int i) {
        if (i < f3RecursionDepth) {
            f3IsASimpleFunctionWithAnExceptionallyLongName(i + 1);
        } else {
            throw new IllegalArgumentException("too long");
        }
    }

    public int getF1RecursionDepth() {
        return f1RecursionDepth;
    }

    public int getF2RecursionDepth() {
        return f2RecursionDepth;
    }

    public int getF3RecursionDepth() {
        return f3RecursionDepth;
    }

}
