package com.bbb.tbs.test;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.tbs.selfservice.manager.TBSSearchStoreManager;

import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by acer on 9/3/2014.
 */
public class SearchStoreTestDroplet extends BBBDynamoServlet {

    private TBSSearchStoreManager mSearchStoreManager;

    @Override
    public void service(DynamoHttpServletRequest req, DynamoHttpServletResponse res) throws ServletException, IOException {

        String lSiteId = req.getParameter("siteId");
        String lStoreId = req.getParameter("storeId");

        if(lStoreId == null){

            req.serviceParameter("empty", req, res);
            req.setParameter("msg", "Please mention the store id parameter");
            return;
        }
        try {
            RepositoryItem[] lItems = getSearchStoreManager().searchNearByStores(lStoreId);
            if(lItems != null) {
                req.setParameter("items", lItems);
                req.serviceParameter("output", req, res);
            } else {
                req.serviceParameter("empty", req, res);
            }

        } catch (RepositoryException e) {
            logError(e);
            req.serviceParameter("empty", req, res);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public TBSSearchStoreManager getSearchStoreManager() {
        return mSearchStoreManager;
    }

    public void setSearchStoreManager(TBSSearchStoreManager pSearchStoreManager) {
        mSearchStoreManager = pSearchStoreManager;
    }


}
