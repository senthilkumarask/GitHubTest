/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  CertonaProfileFeedScheduler.java
 *
 *  DESCRIPTION: Sapunit for Profile Feed Marshaller This will generate xml file.
 * 
 *  @author rsain4
 *  
 *  HISTORY:
 *  
 *  05/02/12 Initial version
 *
 */
package com.bbb.certona.marshaller;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import com.sapient.common.tests.BaseTestCase;

public class TestCertonaProfileFeedMarshaller extends BaseTestCase {

	CertonaProfileFeedMarshaller profileMarshaller;
	private boolean isFullDataFeed = false;
	private boolean isLastModDateFrmRepo = false;
	private Timestamp schedulerStartDate = null;
	private File feedFile = null;

	protected void setUp() throws Exception {
		super.setUp();
		profileMarshaller = (CertonaProfileFeedMarshaller) getObject("profileMarshaller");
		profileMarshaller.setLoggingDebug(true);
		isFullDataFeed = (Boolean) getObject("full");
		isLastModDateFrmRepo = (Boolean) getObject("lastModeDate");
	}

	public void testFullFeedMarshall() throws Exception {
		try {
			schedulerStartDate = new Timestamp(new Date().getTime());
			profileMarshaller.setProfileLastModDate(null);
			profileMarshaller.setProfileLastModDateFrmRepo(isLastModDateFrmRepo);
			profileMarshaller.marshall(isFullDataFeed, schedulerStartDate);
			feedFile = new File(profileMarshaller.getFilePathFrmConfig() + "" + profileMarshaller.getFullFileName(0));
			
			assertTrue(feedFile.exists());
			updateUserProfile();

		} catch (Exception e) {
			fail("Exception is : " + e.getMessage());
		}
	}

	public void testIncrementalFeedMarshall() throws Exception {
		try {

			schedulerStartDate = new Timestamp(new Date().getTime());
			profileMarshaller.setProfileLastModDate(null);
			profileMarshaller.setProfileLastModDateFrmRepo(isLastModDateFrmRepo);
			profileMarshaller.marshall(isFullDataFeed, schedulerStartDate);
			feedFile = new File(profileMarshaller.getFilePathFrmConfig() + "" + profileMarshaller.getFullFileName(0));
			
			assertTrue(feedFile.exists());
			updateUserProfile();
		} catch (Exception e) {
			fail("Exception is : " + e.getMessage());
		}
	}

	// Modified User Profile to update Lastmodified Late
	public void updateUserProfile() throws Exception {
		try {

			final MutableRepository profileRepository = (MutableRepository) resolveName("/atg/userprofiling/ProfileAdapterRepository");
			final RepositoryView profileView = profileRepository.getView("user");
			final QueryBuilder queryBuilder = profileView.getQueryBuilder();
			final Query getAllItemsQuery = queryBuilder.createUnconstrainedQuery();
			final RepositoryItem[] profileItems = profileView.executeQuery(getAllItemsQuery);
			final MutableRepositoryItem item = (MutableRepositoryItem) profileItems[0];
			item.setPropertyValue("phoneNumber", profileItems[0].getPropertyValue("phoneNumber"));
			profileRepository.updateItem(item);
		} catch (Exception e) {
			fail("Exception is : " + e.getMessage());
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
