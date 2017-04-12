package com.bbb.repository;

import java.util.Collection;
import java.util.HashMap;

import atg.beans.DynamicBeanDescriptor;
import atg.beans.DynamicBeanInfo;
import atg.beans.DynamicPropertyDescriptor;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryPropertyDescriptor;
import atg.repository.RepositoryView;


public class RepositoryItemMock implements RepositoryItem {
	private String id;
	private Repository repository;
	private HashMap properties = new HashMap();              

	@Override
	public String getItemDisplayName() {
		
		return "mock display name";
	}

	@Override
	public Collection<String> getContextMemberships()
			throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RepositoryItemDescriptor getItemDescriptor()
			throws RepositoryException {
		// TODO Auto-generated method stub
		return new RepositoryItemDescriptor() {
			
			@Override
			public boolean isInstance(Object arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean hasProperty(String arg0) {
				// TODO Auto-generated method stub
				return true;
			}
			
			@Override
			public String[] getPropertyNames() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public DynamicPropertyDescriptor[] getPropertyDescriptors() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public DynamicPropertyDescriptor getPropertyDescriptor(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public DynamicBeanDescriptor getBeanDescriptor() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean areInstances(DynamicBeanInfo arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void updatePropertyDescriptor(RepositoryPropertyDescriptor arg0)
					throws RepositoryException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void removePropertyDescriptor(String arg0)
					throws RepositoryException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isContextMembershipEnabled() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean hasCompositeKey() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public RepositoryView getRepositoryView() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Repository getRepository() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getItemDescriptorName() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public RepositoryPropertyDescriptor getDisplayNameProperty() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public RepositoryPropertyDescriptor getContextMembershipProperty() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String encodeCompositeKey(String[] arg0) throws RepositoryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void addPropertyDescriptor(RepositoryPropertyDescriptor arg0)
					throws RepositoryException {
				// TODO Auto-generated method stub
				
			}
		};
	}

	@Override
	public Object getPropertyValue(String arg0) {
		// TODO Auto-generated method stub
		return properties.get(arg0);
	}

	@Override
	public Repository getRepository() {
		// TODO Auto-generated method stub
		return repository;
	}

	public void setRepositoryId(String pId) {
		// TODO Auto-generated method stub
		id = pId;
	}
	
	@Override
	public String getRepositoryId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public boolean isTransient() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}
	
	public void setProperties(HashMap map) {
		properties.putAll(map);
	}

}
