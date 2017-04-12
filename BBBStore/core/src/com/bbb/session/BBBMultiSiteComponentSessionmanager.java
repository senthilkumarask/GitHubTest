/** This class overrides the default class for GenericSessionManager and provides the ability to disable its session save feature.
 * It also prints the thread stack trace which invoke the saveSessionData method and which set the backingSessionEnabled property.
 * For debugging purposes the enableSessionSave and enableThreadTraceLogging flags will be set to true, but these should be made as false 
 * for production before go-live
 */

package com.bbb.session;

import javax.servlet.http.HttpSession;

import atg.multisite.session.MultisiteComponentSessionManager;
import atg.servlet.DynamoHttpServletRequest;

/**
 *
 * 
 */
public class BBBMultiSiteComponentSessionmanager extends
		MultisiteComponentSessionManager {

	boolean mEnableSessionSave;

	boolean mEnableThreadTraceLogging;

	public boolean isEnableThreadTraceLogging() {
		return mEnableThreadTraceLogging;
	}

	public void setEnableThreadTraceLogging(boolean mEnableThreadTraceLogging) {
		this.mEnableThreadTraceLogging = mEnableThreadTraceLogging;
	}

	public boolean isEnableSessionSave() {
		return mEnableSessionSave;
	}

	public void setEnableSessionSave(boolean mEnableSessionSave) {
		this.mEnableSessionSave = mEnableSessionSave;
	}

	@Override
	public void saveSessionData(DynamoHttpServletRequest pRequest) {

		// Adding logger to indicate the sessions which are attempted to be
		// saved
		logInfo("Invoked the saveSessionData(DynamoHttpServletRequest pRequest) method for"
				+ "session id: "
				+ pRequest.getSession().getId()
				+ "\n"
				+ "; lastAccessedTime: "
				+ pRequest.getSession().getLastAccessedTime()
				+ "\n"
				+ "; CreationTime: "
				+ pRequest.getSession().getCreationTime()
				+ "\n"
				+ ": BackingSessionProp value: "
				+ isBackingUpSessions()
				+ "\n" + "Total session count: " + getSessionCount());

		// Invoke the session save functionality only when the enableSessionSave
		// flag is true
		if (isEnableSessionSave()) {
			super.saveSessionData(pRequest);
		}
		printThreadStackTrace("saveSessionData(pRequest)");
	}

	// @Override
	// protected void saveSessionData(DynamoHttpServletRequest pRequest,
	// HttpSession pSession, SavedComponentResolver pSavedComponentResolver) {
	//
	// // Adding logger to indicate the sessions which are attempted to be
	// // saved
	// logInfo("Invoked the saveSessionData(DynamoHttpServletRequest pRequest,"
	// +
	// "HttpSession pSession, SavedComponentResolver pSavedComponentResolver) method for"
	// + "session id: "
	// + pRequest.getSession().getId()
	// + "; lastAccessedTime: "
	// + pRequest.getSession().getLastAccessedTime()
	// + "; CreationTime"
	// + pRequest.getSession().getCreationTime()
	// + ": BackingSessionProp value: " + isBackingUpSessions());
	//
	// // Invoke the session save functionality only when the enableSessionSave
	// // flag is true
	// if (isEnableSessionSave()) {
	// super.saveSessionData(pRequest, pSession, pSavedComponentResolver);
	// }
	// printThreadStackTrace("saveSessionData(pRequest, pSession, pSavedComponentResolver)");
	//
	// }

	@Override
	public void setBackingUpSessions(boolean pBackingUpSessions) {
		// Adding logger and thread trace when the backing session property is
		// set to true
		super.setBackingUpSessions(pBackingUpSessions);
		if (pBackingUpSessions) {
			logInfo("Some thread is setting backing session property to true");
			printThreadStackTrace("setBackingUpSessions() with value true");
		}

	}

	private void printThreadStackTrace(String operationName) {

		if (isEnableThreadTraceLogging()) {
			StackTraceElement[] stArray = Thread.currentThread()
					.getStackTrace();
			StackTraceElement element;
			StringBuffer sbuffer = new StringBuffer();
			for (int count = 0; count < stArray.length; count++) {
				element = stArray[count];
				sbuffer.append(element.toString());
				sbuffer.append("\n");
			}
			logInfo("Printing thread trace which invoked " + operationName
					+ "\n" + sbuffer.toString());
		}

	}

	@Override
	public void putSession(String pName, Object pSession) {

		// String url = "NA";
		// if (ServletUtil.getCurrentRequest()!=null &&
		// ServletUtil.getCurrentRequest().getRequestURIWithQueryString()!=null)
		// url = ServletUtil.getCurrentRequest().getRequestURIWithQueryString();

		super.putSession(pName, pSession);
		String id = "unknown";
		if (pSession instanceof HttpSession) {
			HttpSession sess = (HttpSession) pSession;
			id = sess.getId();
		}

		logInfo("Putting a new session in generic session manager component: id="
				+ id + "; session count=" + getSessionCount());

		printThreadStackTrace("putSession with session id=" + id);

	}

	@Override
	public void removeSession(String pId) {

		// String url = "NA";
		// if (ServletUtil.getCurrentRequest()!=null &&
		// ServletUtil.getCurrentRequest().getRequestURIWithQueryString()!=null)
		// url = ServletUtil.getCurrentRequest().getRequestURIWithQueryString();
		logInfo("Removing session from generic session manager component: id="
				+ pId + "; session count=" + getSessionCount());

		super.removeSession(pId);
		logInfo("Removed session from generic session manager component: id="
				+ pId + "; URL=" + "; session count=" + getSessionCount());
		// Printing stack trace of the thread which remove the sessions from the
		// generic session manager
		printThreadStackTrace("removeSession with session id=" + pId);
	}

}
