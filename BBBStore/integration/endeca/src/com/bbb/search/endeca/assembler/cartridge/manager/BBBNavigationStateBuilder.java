/**
 * 
 */
package com.bbb.search.endeca.assembler.cartridge.manager;

import java.util.ArrayList;
import java.util.List;

import com.endeca.infront.navigation.NavigationException;
import com.endeca.infront.navigation.NavigationState;
import com.endeca.infront.navigation.model.SearchFilter;

import atg.endeca.assembler.navigation.ExtendedNavigationStateBuilder;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;

public class BBBNavigationStateBuilder extends GenericService {
	
	private NavigationState mNavigationState;
	private NavigationState mSwsContentNavigationState;

	private ExtendedNavigationStateBuilder mNavBuilder;
	//private HttpServletRequest mHttpRequest;

//	public NavigationState getNavigationState(){
//		return mNavigationState;
//	}
//	
	public void setNavigationState(NavigationState pNavState){
		mNavigationState = pNavState;
	}

//	public NavigationState getSwsPrimaryContentNavigationState(){
//		return mSwsContentNavigationState;
//	}
//	
	public NavigationState getReferenceNavigationState() {
		if(mSwsContentNavigationState != null){
			return mSwsContentNavigationState;
		}
		return mNavigationState;
	}

	@Override
	public void doStartService() throws ServiceException {
		super.doStartService();
		try{
			//if(mNavBuilder == null || mHttpRequest == null){
			if(mNavBuilder == null){
				throw new ServiceException("BBBNavigationStateBuilder: Invalid Configuration file.");
			}
			//initializeNavStates();
			mSwsContentNavigationState = createSwsContentNavState(mNavigationState);
		}catch(Exception ex){
			logError(ex);
			throw new ServiceException(ex.getMessage());
		}
	}
	
	@Override
	public void doStopService() throws ServiceException {
		clearNavStates();
		super.doStopService();
	}
	
//	protected void initializeNavStates() throws NavigationException{
//		//create nav states if they are not already created.
//		if(mPrimaryNavigationState == null){
//			mPrimaryNavigationState = createPrimaryNavState();
//			if(mPrimaryNavigationState == null){
//				throw new NavigationException("initializeNavStates. Null primary nav state.");
//			}
//			mSwsContentNavigationState = createSwsContentNavState(mPrimaryNavigationState);
//		}
//	}
	
//	protected NavigationState createPrimaryNavState() throws NavigationException
//	{
//		return getNavBuilder().parseNavigationState(getHttpRequest());
//	}
	
	protected NavigationState createSwsContentNavState(NavigationState pPrimaryNavState) throws NavigationException
	{
		NavigationState newNavState = null;
		//if more than one searches
		if(null != pPrimaryNavState && null != pPrimaryNavState.getFilterState()) {
			List<SearchFilter> searchFilters = pPrimaryNavState.getFilterState().getSearchFilters();
			//if more than one searches
			if(searchFilters != null && searchFilters.size() > 1) {
				List<SearchFilter> updatedSearchFilters = new ArrayList<SearchFilter>();
				updatedSearchFilters.add(searchFilters.get(0));
				
				newNavState = pPrimaryNavState.clearSearchFilters();
				newNavState = newNavState.updateSearchFilters(updatedSearchFilters);
			} 
		}
		return newNavState;
	}
	
//	public void reInitializeNavStates() throws NavigationException{
//		clearNavStates();
//		initializeNavStates();
//	}
	
	protected void clearNavStates(){
		//mPrimaryNavigationState = null;
		mSwsContentNavigationState = null;
	}
	
	public ExtendedNavigationStateBuilder getNavBuilder() {
		return mNavBuilder;
	}

	public void setNavBuilder(ExtendedNavigationStateBuilder pNavBuilder) {
		this.mNavBuilder = pNavBuilder;
	}

//	public HttpServletRequest getHttpRequest() {
//		return mHttpRequest;
//	}
//
//	public void setHttpRequest(HttpServletRequest pHttpRequest) {
//		this.mHttpRequest = pHttpRequest;
//	}

}
