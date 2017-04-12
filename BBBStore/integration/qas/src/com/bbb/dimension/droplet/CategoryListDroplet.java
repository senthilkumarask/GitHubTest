package com.bbb.dimension.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.repositorywrapper.RepositoryItemWrapper;
import com.bbb.repositorywrapper.RepositoryWrapperImpl;

import atg.core.util.StringUtils;
import atg.multisite.SiteContext;
import atg.nucleus.Nucleus;
import atg.repository.MutableRepository;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class CategoryListDroplet extends DynamoServlet {
	private Repository catalogRepository;

	public void service (DynamoHttpServletRequest request,
	                       DynamoHttpServletResponse response) throws ServletException, IOException {
		
		String categoryId = request.getParameter("categoryId");
		String siteId = request.getParameter("siteId");
		boolean isactive = false;
		RepositoryItemWrapper categoryRepWrapperItem = null;
		if(!StringUtils.isEmpty(categoryId)) {

				RepositoryItem[] repositoryItems = null;
				RepositoryWrapperImpl repository = new RepositoryWrapperImpl(this.getCatalogRepository());
				categoryRepWrapperItem = repository.getItem(categoryId, "category");
				RepositoryItem repItem = categoryRepWrapperItem.getOriginalItem();
				BBBCatalogTools catalogtools = (BBBCatalogTools) Nucleus.getGlobalNucleus().resolveName("/com/bbb/commerce/catalog/BBBCatalogTools");
				isactive = catalogtools.isCategoryActive(repItem);
		}
		if(categoryRepWrapperItem != null && isactive) {
			request.setParameter ("element", categoryRepWrapperItem.getOriginalItem());
			request.serviceParameter ("output", request, response);
		}
	}

	/** @return the catalogRepository */
    public final Repository getCatalogRepository() {
        return this.catalogRepository;
    }

    /** @param catalogRepository the catalogRepository to set */
    public final void setCatalogRepository(final Repository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }
}
