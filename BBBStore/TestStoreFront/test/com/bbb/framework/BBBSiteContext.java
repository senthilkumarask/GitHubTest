package com.bbb.framework;

import java.util.Collection;

import atg.multisite.Site;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextImpl;
import atg.multisite.SiteContextManager;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItemDescriptor;



public class BBBSiteContext
{

	public static SiteContext getBBBSiteContext (final String pSiteId){
		
		SiteContextManager contextManager =  new SiteContextManager();
    	
    	Site site = new Site() {
			
			@Override
			public String getItemDisplayName() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean isTransient() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public String getRepositoryId() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Repository getRepository() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object getPropertyValue(String s) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public RepositoryItemDescriptor getItemDescriptor()
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Collection<String> getContextMemberships()
					throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getId() {
				// TODO Auto-generated method stub
				return pSiteId;
			}
		};  

		SiteContextImpl context = new SiteContextImpl(contextManager, site);
		return context;
	}
}
