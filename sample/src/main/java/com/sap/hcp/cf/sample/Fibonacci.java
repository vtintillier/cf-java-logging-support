package com.sap.hcp.cf.sample;

import java.math.BigInteger;

public class Fibonacci {

	public BigInteger compute(BigInteger in) {
		if (in.equals(BigInteger.ZERO) || in.equals(BigInteger.ONE)) {
			return in;
		}
		else {
			return compute(in.subtract(BigInteger.ONE)).add(compute(in.subtract(BigInteger.ONE).subtract(BigInteger.ONE)));
		}
	}
}
