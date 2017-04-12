package atg.tbsqas;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;

public class QasAddressVerificationSearch extends BBBDynamoServlet {
	protected String onDemandUrl;
	protected String onDemandUsername;
	protected String onDemandPassword;
	
	private QasAddressVerification qasaddressverification;

	public QasAddressVerification getQasaddressverification() {
		return qasaddressverification;
	}

	public void setQasaddressverification(
			QasAddressVerification qasaddressverification) {
		this.qasaddressverification = qasaddressverification;
	}

	public QasAddressVerificationSearch() {
		this.onDemandUrl = "";
		this.onDemandUsername = "";
		this.onDemandPassword = "";
	}

	public String getOnDemandUrl() {
		return this.onDemandUrl;
	}

	public void setOnDemandUrl(String s) {
		this.onDemandUrl = s;
	}

	public String getOnDemandUsername() {
		return this.onDemandUsername;
	}

	public void setOnDemandUsername(String s) {
		this.onDemandUsername = s;
	}

	public String getOnDemandPassword() {
		return this.onDemandPassword;
	}

	public void setOnDemandPassword(String s) {
		this.onDemandPassword = s;
	}

	public void service(DynamoHttpServletRequest dynamohttpservletrequest,
			DynamoHttpServletResponse dynamohttpservletresponse)
			throws IOException, ServletException {
		try {
			logDebug("I am inside The droplet");
			String s = URLDecoder.decode(
					dynamohttpservletrequest.getParameter("country"), "UTF-8");
			String s1 = URLDecoder.decode(
					dynamohttpservletrequest.getParameter("searchstring"),
					"UTF-8");
			String s2 = URLDecoder
					.decode(dynamohttpservletrequest.getParameter("addlayout"),
							"UTF-8");
			QasAddressVerification qasaddressverification = new QasAddressVerification();
			qasaddressverification.setOnDemandUsername(getOnDemandUsername());
			qasaddressverification.setOnDemandPassword(getOnDemandPassword());
			qasaddressverification.setOnDemandUrl(getOnDemandUrl());
			qasaddressverification.setOnDemandLayout(s2);
			qasaddressverification.setCountry(s);
			qasaddressverification.setSearchString(s1);
			qasaddressverification.doSearch(dynamohttpservletrequest,
					dynamohttpservletresponse);
			dynamohttpservletrequest.setParameter("addresslines",
					qasaddressverification.getAddressLines());
			dynamohttpservletrequest.setParameter("verificationlevel",
					qasaddressverification.getVerificationLevel());
			dynamohttpservletrequest.setParameter("dpvstatus",
					qasaddressverification.getDpvStatus());
			dynamohttpservletrequest.setParameter("picklistitems",
					qasaddressverification.getPicklistItems());
			dynamohttpservletrequest.setParameter("fullmoniker",
					qasaddressverification.getFullMoniker());
		} catch (Exception exception) {
			dynamohttpservletrequest.setParameter("qaserror",
					exception.getMessage());
		}
		dynamohttpservletrequest.serviceLocalParameter("output",
				dynamohttpservletrequest, dynamohttpservletresponse);
	}
}