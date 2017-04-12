package com.bbb.utils;

import java.util.HashMap;
import java.util.Map;

public class BBBJobContextManager {

	// Mapping between thread and Job ID
	private static Map<String, String> jobInfo;

	// Context for each job Id
	private static Map<String, Map<String, String>> jobContext;

	private BBBJobContextManager() {

	}

	private static BBBJobContextManager jobContextInstance;

	public static BBBJobContextManager getInstance() {
		if (jobContextInstance == null) {
			synchronized (BBBJobContextManager.class) {
				if (jobContextInstance == null) {
					jobContextInstance = new BBBJobContextManager();
					jobInfo = new HashMap<String, String>();
					jobContext = new HashMap<String, Map<String, String>>();
				}
			}
		}
		return jobContextInstance;
	}

	public static Map<String, String> getJobInfo() {
		return jobInfo;
	}

	public static void setJobInfo(Map<String, String> jobInfo) {
		BBBJobContextManager.jobInfo = jobInfo;
	}

	public static Map<String, Map<String, String>> getJobContext() {
		return jobContext;
	}

	public static void setJobContext(Map<String, Map<String, String>> jobContext) {
		BBBJobContextManager.jobContext = jobContext;
	}

}
