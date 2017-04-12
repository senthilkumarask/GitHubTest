
package com.bbb.framework.performance.pipeline;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.pipeline.InsertableServletImpl;


/**
 * @author gbisht
 * 
 */
public class SessionLoggingServlet extends InsertableServletImpl {

	
	private static final String NEW_SESSION_LOG_MSG = "New session created with id=";
	private static final String LOG_DELIM = "; ";
	private static final String REQ_URI_LOG_MSG = "Request URI was: ";
	private static final String SESS_DETAIL_LOG = "Session Creation Time: ";

	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {

		HttpSession session = pRequest.getSession();

		if (session.isNew()) {
			logInfo(NEW_SESSION_LOG_MSG + session.getId() + LOG_DELIM
					+ REQ_URI_LOG_MSG + pRequest.getRequestURIWithQueryString()
					+ LOG_DELIM + SESS_DETAIL_LOG + session.getCreationTime());
		}
		
		passRequest(pRequest, pResponse);

	}

}
