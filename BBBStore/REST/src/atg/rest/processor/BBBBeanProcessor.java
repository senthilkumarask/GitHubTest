package atg.rest.processor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import atg.core.util.StringUtils;
import atg.droplet.DropletFormHandler;
import atg.droplet.ObjectFormHandler;
import atg.droplet.RestFormSubmissionHandler;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.rest.Messages;
import atg.rest.RestException;
import atg.rest.RestFormExceptions;
import atg.rest.util.BeanURI;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class BBBBeanProcessor extends BeanProcessor {

	TransactionManager transactionManager;


	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void doRESTPost(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws RestException,
			IOException {
		boolean returnFormHandlerProps = false;
		boolean returnFormExceptions = false;
		RestFormExceptions formErrors = new RestFormExceptions();
		boolean ret=false;
		Object outputValue = null;
		if (isLoggingDebug()) {
			logDebug("Received POST request for " + pRequest.getPathInfo());
		}
		validateResource(pRequest, pResponse);

		BeanURI parsedURI = (BeanURI) pRequest.getAttribute("_PARSED_URI");

		Object resContainer = getComponentResolver().findComponent(
				parsedURI.getResourceContainer(), new Object[] { pRequest });

		if (isLoggingDebug()) {
			logDebug("Found resource container for POST request: "
					+ pRequest.getPathInfo());
		}
		TransactionManager tm = getTransactionManager();
		TransactionDemarcation td = new TransactionDemarcation();

		if (!(StringUtils.isBlank(parsedURI.getResource()))) {
			if ((resContainer instanceof DropletFormHandler)
					|| (resContainer instanceof ObjectFormHandler)
					|| (isConfiguredFormHandlerClass(resContainer.getClass()))) {
				if (isLoggingDebug()) {
					logDebug("Calling method on form handler for POST request: "
							+ pRequest.getPathInfo());
				}

				/*
				 * Transaction start ATG REST code has defect where in it
				 * returns the response before committing the transaction. This
				 * code is to workaround that defect.If not done then
				 * Mobile/other REST users can face intermittent issues where
				 * new request comes before commit of old request.
				 */
				try {
					if (tm != null) {
						td.begin(tm, TransactionDemarcation.REQUIRES_NEW);
					}
					RestFormSubmissionHandler submitHandler = new RestFormSubmissionHandler(
							resContainer, parsedURI, pRequest, pResponse);

					ret = submitHandler.execute(formErrors);

					returnFormHandlerProps = getBooleanControlParam(
							"atg-rest-return-form-handler-properties",
							isReturnFormHandlerPropertiesByDefault());

					returnFormExceptions = getBooleanControlParam(
							"atg-rest-return-form-handler-exceptions",
							isReturnFormHandlerExceptionsByDefault());
				} catch (TransactionDemarcationException e) {
					try {
						throw new ServletException(e);
					} catch (ServletException e1) {
						logError(e);
					}
				} finally {
					try {
						if (tm != null)
							td.end();
						if (isLoggingDebug()) {
							logDebug("Transaction End::request for: " + pRequest.getPathInfo());
						}
					} catch (TransactionDemarcationException e) {
						this.logError(e.getMessage(), e);
					}
				}
				// Transaction ended
				if ((returnFormExceptions) && (returnFormHandlerProps)) {
					FormHandlerPropertiesAndExceptions fho = new FormHandlerPropertiesAndExceptions();
					fho.setComponent(resContainer);
					fho.setExceptions(formErrors);
					fho.setResult(ret);
					getOutputCustomizer().outputBean(parsedURI, fho, -1,
							pRequest, pResponse);
				} else if (returnFormHandlerProps) {
					FormHandlerProperties fho = new FormHandlerProperties();
					fho.setComponent(resContainer);
					fho.setResult(ret);
					getOutputCustomizer().outputBean(parsedURI, fho, -1,
							pRequest, pResponse);
				} else if (returnFormExceptions) {
					FormHandlerExceptions fho = new FormHandlerExceptions();
					fho.setExceptions(formErrors);
					fho.setResult(ret);
					getOutputCustomizer().outputBean(parsedURI, fho, -1,
							pRequest, pResponse);
				} else {
					outputValue = Boolean.valueOf(ret);
					getOutputCustomizer().outputBeanValue(parsedURI, null,
							outputValue, 0, pRequest, pResponse);
				}
			} else {
				if (isLoggingDebug())
					logDebug("Calling method for POST request: "
							+ pRequest.getPathInfo());
				Method m = findMethod(parsedURI, resContainer, pRequest,
						pResponse);
				if (m != null) {
					invokeMethod(m, parsedURI, resContainer, pRequest,
							pResponse);
				} else {
					doRESTPut(pRequest, pResponse);
				}
			}
		} else
			throw new RestException(
					Messages.getString("BeanProcessor.ResourceNotSpecified"),
					pResponse, 400);
	}
	
	void invokeMethod(Method pMethod, BeanURI pParsedURI, Object pResContainer,
			DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws RestException,
			IOException {
		if (isLoggingDebug())
			logDebug("Executing method: " + pRequest.getPathInfo());
		try {
			Object[] args = getMethodArguments(pMethod, pRequest, pResponse);

			if (isLoggingDebug()) {
				logDebug("Invoking method now for POST request: "
						+ pRequest.getPathInfo());
			}
			// Transaction added
			Object ret = null;
			TransactionManager tm = getTransactionManager();
			TransactionDemarcation td = new TransactionDemarcation();
			try {
				if (tm != null) {
					td.begin(tm, TransactionDemarcation.REQUIRES_NEW);
				}

				if ((args == null) || (args.length == 0))
					ret = pMethod.invoke(pResContainer, new Object[0]);
				else {
					ret = pMethod.invoke(pResContainer, args);
				}

				if ((pMethod.getReturnType().isAssignableFrom(Void.TYPE))
						&& (ret == null)) {
					ret = "void";
				}
			} catch (TransactionDemarcationException e) {
				try {
					throw new ServletException(e);
				} catch (ServletException e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
					this.logError(e1.getMessage(), e1);
				}
			} finally {
				try {
					if (tm != null)
						td.end();
				} catch (TransactionDemarcationException e) {
					
					this.logError(e.getMessage(), e);
				}
			}
			// transcation ended

			if ((ret == null) || (ret instanceof String)
					|| (ret instanceof Number) || (ret instanceof Boolean)
					|| (ret instanceof Collection) || (ret instanceof Map)
					|| (ret.getClass().isArray()) || (ret instanceof Class)
					|| (ret instanceof Byte) || (ret instanceof Character)
					|| (ret instanceof Enum)) {
				getOutputCustomizer().outputBeanValue(pParsedURI, null, ret, 0,
						pRequest, pResponse);
			} else
				getOutputCustomizer().outputBean(pParsedURI, ret, 0, pRequest,
						pResponse);
		} catch (IOException e) {
			throw new RestException(e, pResponse, 500);
		} catch (IllegalAccessException e) {
			throw new RestException(e, pResponse, 401);
		} catch (IllegalArgumentException e) {
			throw new RestException(e, pResponse, 400);
		} catch (InvocationTargetException e) {
			if (e.getCause() != null) {
				throw new RestException(e.getCause(), pResponse, 500);
			}
			throw new RestException(e, pResponse, 500);
		}
	}

}