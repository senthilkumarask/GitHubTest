package com.bbb.commerce.catalog.custom;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import atg.adapter.gsa.GSARepository;
import atg.commerce.catalog.custom.AncestorGeneratorService;
import atg.dtm.TransactionDemarcationException;
import atg.repository.RepositoryException;

public class BBBAncestorGeneratorService extends AncestorGeneratorService {

	public static final String BBB_AGS_GENPROPERTIES_FOR_PRODUCT = "BBB_AGS_GENPROPERTIES_FOR_PRODUCT";
	public static final String BBB_AGS_GENPROPERTIES_FOR_SKU = "BBB_AGS_GENPROPERTIES_FOR_SKU";
	private String updatePriceStringForProductSQL;
	private String updatePriceFlagForSKUSQL;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atg.commerce.catalog.custom.AncestorGeneratorService#updateProducts().
	 * Overridden the OOTB function to run custom functions for dynamic
	 * pricing.This function would call a store procedure which will update the
	 * BBB_PROD_PRICE_STRINGS if the function in the list of availableFunctions
	 * contains BBB_AGS_GENPROPERTIES_FOR_PRODUCT else returns.
	 */
	@Override
	protected void updateProducts() throws RepositoryException,
			TransactionDemarcationException {

		// If the custom service functions is not selected then return
		if ((getServiceFunctions().contains(BBB_AGS_GENPROPERTIES_FOR_PRODUCT))) {
			if (isLoggingDebug()) {
				logDebug("BBB_AGS_GENPROPERTIES_FOR_PRODUCT in service function of AncestorGeneratorService . Excecuting");
			}
		
		long startTime = System.currentTimeMillis();
		if (isLoggingDebug()) {
			logDebug(new StringBuilder(
					" Starting : updateProducts() for BBBAncestorGeneratorService | Start time is : ")
					.append(new Date(startTime)).toString());
		}
		Connection dbConnection = null;
		CallableStatement stmt = null;
		try {
			dbConnection = ((GSARepository) getRepository()).getConnection();

			boolean autoCommit = dbConnection.getAutoCommit();
			dbConnection.setAutoCommit(false);
			stmt = dbConnection
					.prepareCall(getUpdatePriceStringForProductSQL());
			if (isLoggingInfo()) {
				logInfo("Started update product for catalogs.");
			}
			stmt.executeUpdate();
			if (isLoggingInfo()) {
				logInfo("Completed update product for catalogs.");
			}
			long endTime = System.currentTimeMillis();
			long seconds = (endTime - startTime) / 1000;
			if (isLoggingDebug()) {
				logDebug(new StringBuilder(
						" End : store proc call of product for BBBAncestorGeneratorService | End time is : ")
						.append(new Date(endTime))
						.append(" | time taken in seconds: ")
						.append(Long.valueOf(seconds)).toString());
			}
			stmt.close();

			dbConnection.commit();
			dbConnection.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			if (isLoggingError()) {
				logError("SQLException occured while updating product in AncestorGeneratorService");
			}
			throw new RepositoryException(e);
		} finally {
			// close resources
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					if (isLoggingError()) {
						logError("SQLException occured while trying to close connection in BBBAncestorGeneratorService");
					}
				}
			}

			if (dbConnection != null) {
				((GSARepository) getRepository()).close(dbConnection);
			}
		}
		}
		super.updateProducts();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atg.commerce.catalog.custom.AncestorGeneratorService#updateSkus()
	 * Overridden the OOTB function to run custom functions for dynamic
	 * pricing.This function would call a store procedure which will update the
	 * BBB_SKU_PRICE_FLAGS if the function in the list of availableFunctions
	 * contains BBB_AGS_GENPROPERTIES_FOR_SKU else returns.
	 */
	@Override
	protected void updateSkus() throws RepositoryException,
			TransactionDemarcationException {

		// If the custom service functions is not selected then return
		if ((getServiceFunctions().contains(BBB_AGS_GENPROPERTIES_FOR_SKU))) {
			if (isLoggingDebug()) {
				logDebug("BBB_AGS_GENPROPERTIES_FOR_SKU in service function of AncestorGeneratorService . Executing");
			}
			
		long startTime = System.currentTimeMillis();
		if (isLoggingDebug()) {
			logDebug(new StringBuilder(
					" Starting : updateSkus() for BBBAncestorGeneratorService | Start time is : ")
					.append(new Date(startTime)).toString());
		}
		Connection dbConnection = null;
		CallableStatement stmt = null;
		try {
			dbConnection = ((GSARepository) getRepository()).getConnection();

			boolean autoCommit = dbConnection.getAutoCommit();
			dbConnection.setAutoCommit(false);
			stmt = dbConnection.prepareCall(getUpdatePriceFlagForSKUSQL());

			if (isLoggingInfo()) {
				logInfo("Started update SKU for catalogs.");
			}
			stmt.executeUpdate();
			if (isLoggingInfo()) {
				logInfo("Completed update SKU for catalogs.");
			}

			long endTime = System.currentTimeMillis();
			long seconds = (endTime - startTime) / 1000;
			if (isLoggingDebug()) {
				logDebug(new StringBuilder(
						" End : store proc call of sku for BBBAncestorGeneratorService | End time is : ")
						.append(new Date(endTime))
						.append(" | time taken in seconds: ")
						.append(Long.valueOf(seconds)).toString());
			}
			stmt.close();

			dbConnection.commit();
			dbConnection.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			if (isLoggingError()) {
				logError("SQLException occured while updating sku in BBBAncestorGeneratorService");
			}
			throw new RepositoryException(e);
		} finally {
			// close resources
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					if (isLoggingError()) {
						logError("SQLException occured while trying to close connection in BBBAncestorGeneratorService");
					}
				}
			}

			if (dbConnection != null) {
				((GSARepository) getRepository()).close(dbConnection);
			}
		}
		}
		super.updateSkus();
	}

	public String getUpdatePriceStringForProductSQL() {
		return updatePriceStringForProductSQL;
	}

	public void setUpdatePriceStringForProductSQL(
			String updatePriceStringForProductSQL) {
		this.updatePriceStringForProductSQL = updatePriceStringForProductSQL;
	}

	public String getUpdatePriceFlagForSKUSQL() {
		return updatePriceFlagForSKUSQL;
	}

	public void setUpdatePriceFlagForSKUSQL(String updatePriceFlagForSKUSQL) {
		this.updatePriceFlagForSKUSQL = updatePriceFlagForSKUSQL;
	}

}
