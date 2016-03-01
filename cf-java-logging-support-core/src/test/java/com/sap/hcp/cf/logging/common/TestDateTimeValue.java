package com.sap.hcp.cf.logging.common;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import com.sap.hcp.cf.logging.common.DateTimeValue;

public class TestDateTimeValue {

	@Test
	public void test() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		long ts = System.currentTimeMillis();
		DateTimeValue dtv = new DateTimeValue(ts);
		try {
			assertTrue(dtv.getValue().equals(df.parse(df.format(new Date(ts)))));
		}
		catch (ParseException pex) {
			fail(pex.getMessage());
		}		
	}

}
