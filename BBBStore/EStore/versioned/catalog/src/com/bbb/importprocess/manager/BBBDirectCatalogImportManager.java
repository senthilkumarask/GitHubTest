package com.bbb.importprocess.manager;

import java.sql.Connection;
import java.util.List;

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBStringUtils;

/**
 * The is the catalog import manager used for directly importing PIM data into
 * the Staging schema by bypassing BCC
 * 
 * @author logixal
 * 
 */

public class BBBDirectCatalogImportManager extends BBBCatalogImportManager {

	/** 
	 * Overridden Method used for directly importing PIM data into the Staging schema by bypassing BCC
	 */
	public void executeImport(final String pFeedType, final List<String> feedIdList, final String pWorkflowName,
	        final String pProjectName, final Connection pConnection) throws BBBSystemException {
		try {
			if (isLoggingDebug()) {
				logDebug("Before importData,  date=" + formatDate(getDate()));
			}
			importData(pFeedType, feedIdList, pConnection, false);
			if (isLoggingDebug()) {
				logDebug("After importData,  date=" + formatDate(getDate()));
				logDebug("Import Data Completed");
			}
		} catch (BBBSystemException bse) {
			if (isLoggingError()) {
				logError("BBB System Exception : " + BBBStringUtils.stack2string(bse), bse);
			}
			throw bse;
		} catch (Exception e) {
			if (isLoggingError()) {
				logError(BBBStringUtils.stack2string(e), e);
			}
			throw new BBBSystemException(BBBCoreErrorConstants.VERS_MGR_GENERIC_ERROR, "Genric Exception=", e);
		}
	}
}
