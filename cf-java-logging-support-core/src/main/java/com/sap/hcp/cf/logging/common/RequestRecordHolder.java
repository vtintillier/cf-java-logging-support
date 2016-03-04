package com.sap.hcp.cf.logging.common;

import java.util.HashSet;
import java.util.Set;

/**
 * As we need to keep track of nested calls we use thread local storage to
 * manage a set of RequestRecord instances that are "in flight" for individual
 * thread.
 * 
 * Upon removal of a record, we check whether that one is the last one so that
 * we can clear context information.
 *
 */
public class RequestRecordHolder {

	private static final ThreadLocal<Set<RequestRecord>> RECORD_SETS = new ThreadLocal<Set<RequestRecord>>();
	
	public static void add(RequestRecord rr) {
		if (rr == null) {
			return;
		}
		Set<RequestRecord> recSet = RECORD_SETS.get();
		if (recSet == null) {
			recSet = new HashSet<RequestRecord>();
			RECORD_SETS.set(recSet);
		}
		recSet.add(rr);
	}
	
	public static void remove(RequestRecord rr) {
		if (rr == null) {
			return;
		}
		Set<RequestRecord> recSet = RECORD_SETS.get();
		if (recSet.remove(rr)) {
			/*
			 * -- time to clean up if this was the last 
			 */
			if (recSet.isEmpty()) {
				rr.resetContext();
			}
		}
	}
}
