package com.bbb.pipeline;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.pipeline.InsertableServletImpl;

public class PrintThreadDumpServlet extends InsertableServletImpl {

	private boolean enabled;
	private long dumpInterval;
	private int noOfDumps;
	private long waitInterval;
	private List<String> checkURL;
	private boolean suspended;
	private int poolSize;
	private ExecutorService es;
	
	public ExecutorService getEs() {
		if(es == null) {
			es = Executors.newFixedThreadPool(getPoolSize());
		}
		return es;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public long getDumpInterval() {
		return dumpInterval;
	}

	public void setDumpInterval(long dumpInterval) {
		this.dumpInterval = dumpInterval;
	}

	public int getNoOfDumps() {
		return noOfDumps;
	}

	public void setNoOfDumps(int noOfDumps) {
		this.noOfDumps = noOfDumps;
	}

	public long getWaitInterval() {
		return waitInterval;
	}

	public void setWaitInterval(long waitInterval) {
		this.waitInterval = waitInterval;
	}

	public List<String> getCheckURL() {
		return checkURL;
	}

	public void setCheckURL(List<String> checkURL) {
		this.checkURL = checkURL;
	}

	public boolean isSuspended() {
		return suspended;
	}

	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}

	public void service(DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
			throws IOException, ServletException {
		
		if (isEnabled()  && !isSuspended() && checkURL.contains(pRequest.getRequestURI())  ) {
			StackTraceThread stactTraceThread = new StackTraceThread(Thread.currentThread());
			 
			Future<?> futureThread = getEs().submit(stactTraceThread);
			
			passRequest(pRequest, pResponse);
			
			futureThread.cancel(true);
		} else {
			passRequest(pRequest, pResponse);
		}
	}

	public int getPoolSize() {
		return poolSize;
	}
	
	public void setPoolSize(int size) {
		poolSize = size;
	}
	

	synchronized void startDumps(long dumpInterval, int noOfDumps, Thread mainThread) throws InterruptedException {
		
		for (int dumpCount = 0; dumpCount < noOfDumps; dumpCount++) {
			if(mainThread.getStackTrace() != null ) {
				for (int stackElement = 0; stackElement < mainThread.getStackTrace().length; stackElement++) {
					logTrace(mainThread.getStackTrace()[stackElement].toString());
				}
			}
			putThreadToWait(dumpInterval);
		}
	}

	private void putThreadToWait(long dumpInterval) throws InterruptedException {
		Thread.sleep(dumpInterval);
	}

	private class StackTraceThread implements Runnable {

		Thread mainThread;

		public StackTraceThread(Thread mainThread) {
			this.mainThread = mainThread;
		}
@Override
		public void run() {

			try {
				putThreadToWait(getWaitInterval());
				setSuspended(true);
				startDumps(getDumpInterval(), getNoOfDumps(), mainThread);
			} catch (Exception e) {
				logDebug(e.getMessage(), e);
			} finally{
				setSuspended(false);
			}
		}
	}
}
