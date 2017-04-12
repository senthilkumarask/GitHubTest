/**
 * @author akhaju
 * @version Id: //com.bbb.commerce.order/TestBBBFavStore.java.testFavStore $$
 * @updated $DateTime: Jan 27, 2012 12:46:29 PM
 */
package com.bbb.account;

import atg.repository.RepositoryException;

import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

/**
 * @author akhaju
 *
 */
public class TestBBBFavStore extends BaseTestCase{
	
	public void testFavStore() throws Exception {
		BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) getObject("bbbProfileFormHandler");
		bbbProfileFormHandler.setExpireSessionOnLogout(false);
		bbbProfileFormHandler.getErrorMap().clear();
		bbbProfileFormHandler.getFormExceptions().clear();
		bbbProfileFormHandler.setFormErrorVal(false);
		if (!bbbProfileFormHandler.getProfile().isTransient()) {
			bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
		}
		System.out.println("TestBBBFavStore.testFavStore.getFormExceptions()"
				+ bbbProfileFormHandler.getFormExceptions());
		assertTrue(bbbProfileFormHandler.getProfile().isTransient());
		assertTrue(bbbProfileFormHandler.getFormExceptions().size() == 0);

		getRequest().setParameter("BBBProfileFormHandler",
				bbbProfileFormHandler);

		BBBProfileTools bbbProfileTools = (BBBProfileTools) getObject("bbbProfileTools");
		assertNotNull(bbbProfileTools);
		getRequest().setParameter("BBBProfileFormHandler",
				bbbProfileFormHandler);

		String pSiteId = (String) getObject("siteId");
		bbbProfileFormHandler.setSiteContext(BBBSiteContext
				.getBBBSiteContext(pSiteId));

		String email = (String) getObject("email");
		String password = (String) getObject("password");
		bbbProfileFormHandler.getValue().put("login", email);
		bbbProfileFormHandler.getValue().put("password", password);
		atg.servlet.ServletUtil.setCurrentRequest(getRequest());

		boolean isLogin = bbbProfileFormHandler.handleLogin(getRequest(),
				getResponse());
		assertTrue(isLogin);

		if (!bbbProfileFormHandler.getProfile().isTransient()) {
			BBBProfileManager profileManager = (BBBProfileManager) getRequest()
					.resolveName("/com/bbb/account/BBBProfileManager");
			getRequest().resolveName("/com/bbb/account/BBBProfileManager");
			String favStoreId = (String) getObject("favouriteStoreId");
			String favStoreProperty = (String) getObject("favouriteStoreProperty");

			testSetFavStoreId(profileManager, bbbProfileFormHandler, pSiteId,
					favStoreId, favStoreProperty);
			// removing favorite store
			testModifyFavStoreId(profileManager, bbbProfileFormHandler,
					pSiteId, favStoreProperty);
			bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
			bbbProfileFormHandler.getErrorMap().clear();
			bbbProfileFormHandler.getFormExceptions().clear();
			bbbProfileFormHandler.setFormErrorVal(false);
		}
	}
	
	public void testSetFavStoreId(final BBBProfileManager profileManager,
			final BBBProfileFormHandler bbbProfileFormHandler,
			final String pSiteId, final String favStoreId,
			final String favStoreProperty) throws RepositoryException {
		assertTrue(profileManager.updateSiteItem(
				bbbProfileFormHandler.getProfile(), pSiteId, favStoreId,
				favStoreProperty));
	}

	public void testModifyFavStoreId(final BBBProfileManager profileManager,
			final BBBProfileFormHandler bbbProfileFormHandler,
			final String pSiteId, final String favStoreProperty)
			throws RepositoryException {
		assertTrue(profileManager.updateSiteItem(
				bbbProfileFormHandler.getProfile(), pSiteId, null,
				favStoreProperty));
	}
}
