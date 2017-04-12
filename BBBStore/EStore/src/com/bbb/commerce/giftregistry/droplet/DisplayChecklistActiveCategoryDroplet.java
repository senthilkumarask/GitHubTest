package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.checklist.manager.CheckListManager;
import com.bbb.commerce.checklist.vo.CategoryVO;
import com.bbb.commerce.checklist.vo.CheckListPrevNextCategoriesVO;
import com.bbb.commerce.checklist.vo.CheckListVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

/**
 * The Class DisplayChecklistActiveCategoryDroplet.
 * This droplet is used to populate C3
 */
public class DisplayChecklistActiveCategoryDroplet extends BBBPresentationDroplet {
	
	/** The session bean. */
	private BBBSessionBean sessionBean;
	
	/** The check list manager. */
	private CheckListManager checkListManager;
	
	/* (non-Javadoc)
	 * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	@Override
	public void service(DynamoHttpServletRequest req,
			DynamoHttpServletResponse res) throws ServletException, IOException {/*
		
		final String registryId= req.getParameter(BBBCoreConstants.REGISTRY_ID);
		final String registryType= req.getParameter(BBBCoreConstants.REGISTRY_TYPE);
		final String fromRegistryActivity= req.getParameter(BBBCoreConstants.FROM_REGISTRY_ACTIVITY);
		
		String cat1Id=req.getParameter(BBBCoreConstants.CAT1_ID);
		String cat2Id=req.getParameter(BBBCoreConstants.CAT2_ID);
		String cat3Id=req.getParameter(BBBCoreConstants.CAT3_ID);
		String catId=req.getParameter(BBBCoreConstants.CAT_ID);

		
		try {
			CheckListPrevNextCategoriesVO checkListPrevNextCategoriesVO = new CheckListPrevNextCategoriesVO();
			
			CheckListVO checkListVO = getCheckListManager().getRegistryCheckList(registryId, registryType);
			
			if (null != checkListVO && null != checkListVO.getCategoryListVO() && checkListVO.getCategoryListVO().size() >0 ){
				
				if (getSessionBean().getCheckListPrevNextCategoriesVO() == null) {
					
					List<CategoryVO> c1Categories = checkListVO.getCategoryListVO();
					if(c1Categories!=null && !c1Categories.isEmpty() && BBBUtility.isEmpty(cat1Id) && !BBBUtility.isListEmpty(checkListVO.getCategoryListVO())) {
						cat1Id = checkListVO.getCategoryListVO().get(0).getCategoryId();
					}
					Map<String,CategoryVO> c1ChildMap = checkListVO.getCategoryListVO().get(0).getChildCategoryVO();
					if(c1ChildMap!=null && !c1ChildMap.isEmpty() && BBBUtility.isEmpty(cat2Id)){
						cat2Id = c1ChildMap.entrySet().iterator().next().getValue().getCategoryId();
					}
					Map<String,CategoryVO> c2ChildMap = c1ChildMap.get(c1ChildMap.keySet().toArray()[0]).getChildCategoryVO();
					if(c2ChildMap!=null && !c2ChildMap.isEmpty() && BBBUtility.isEmpty(cat3Id)){
						cat3Id = c2ChildMap.entrySet().iterator().next().getValue().getCategoryId();
					}
					
					checkListPrevNextCategoriesVO = getCheckListManager().getSurroundingCategoriesOnClickingC3(cat1Id, cat2Id, cat3Id, registryType);
					getCheckListManager().populatePrevNextVOQuantityAdded(checkListVO, cat1Id, cat2Id, cat3Id,checkListPrevNextCategoriesVO);
					getSessionBean().setCheckListPrevNextCategoriesVO(checkListPrevNextCategoriesVO);
				} else if(BBBCoreConstants.TRUE.equals(fromRegistryActivity)){
					cat1Id = getSessionBean().getCheckListPrevNextCategoriesVO().getCat1Id();
					cat2Id = getSessionBean().getCheckListPrevNextCategoriesVO().getCat2Id();
					cat3Id = getSessionBean().getCheckListPrevNextCategoriesVO().getCat3Id();
					checkListPrevNextCategoriesVO = getSessionBean().getCheckListPrevNextCategoriesVO();
					if(!BBBUtility.isEmpty(cat1Id)&& !BBBUtility.isEmpty(cat2Id) && !BBBUtility.isEmpty(cat3Id)){
						getCheckListManager().populatePrevNextVOQuantityAdded(checkListVO, cat1Id, cat2Id, cat3Id,checkListPrevNextCategoriesVO);
					}
					getSessionBean().setCheckListPrevNextCategoriesVO(checkListPrevNextCategoriesVO);
				} else if(!BBBUtility.isEmpty(cat1Id)&& !BBBUtility.isEmpty(cat2Id)){
					checkListPrevNextCategoriesVO = getCheckListManager().getSurroundingCategoriesOnClickingC3(cat1Id, cat2Id, cat3Id, registryType);
					getCheckListManager().populatePrevNextVOQuantityAdded(checkListVO, cat1Id, cat2Id, cat3Id,checkListPrevNextCategoriesVO);
					getSessionBean().setCheckListPrevNextCategoriesVO(checkListPrevNextCategoriesVO);
				} else if(!BBBUtility.isEmpty(catId)){
					cat1Id = getSessionBean().getCheckListPrevNextCategoriesVO().getCat1Id();
					cat2Id = getSessionBean().getCheckListPrevNextCategoriesVO().getCat2Id();
					cat3Id = getSessionBean().getCheckListPrevNextCategoriesVO().getCat3Id();
					if(BBBUtility.isEmpty(cat3Id)){
						checkListPrevNextCategoriesVO = getCheckListManager().getSurroundingCategoriesOnClickingC3(cat1Id, catId, null, registryType);
						getCheckListManager().populatePrevNextVOQuantityAdded(checkListVO, cat1Id, catId, null,checkListPrevNextCategoriesVO);
					} else {
						checkListPrevNextCategoriesVO = getCheckListManager().getSurroundingCategoriesOnClickingC3(cat1Id, cat2Id, catId, registryType);
						getCheckListManager().populatePrevNextVOQuantityAdded(checkListVO, cat1Id, cat2Id, catId,checkListPrevNextCategoriesVO);
					}
					getSessionBean().setCheckListPrevNextCategoriesVO(checkListPrevNextCategoriesVO);
				}	
			}
		} catch (BBBBusinessException e) {
			logError("BBBBusinessException in service method of DisplayChecklistActiveCategoryDroplet :: ",e);			
		} catch (BBBSystemException sE) {
			logError("BBBSystemException in service method of DisplayChecklistActiveCategoryDroplet :: ",sE);
		}
		
*/	}

	
	/**
	 * Gets the session bean.
	 *
	 * @return the session bean
	 */
	public BBBSessionBean getSessionBean() {
		return sessionBean;
	}

	/**
	 * Gets the check list manager.
	 *
	 * @return the check list manager
	 */
	public CheckListManager getCheckListManager() {
		return checkListManager;
	}

	/**
	 * Sets the check list manager.
	 *
	 * @param checkListManager the new check list manager
	 */
	public void setCheckListManager(CheckListManager checkListManager) {
		this.checkListManager = checkListManager;
	}

	/**
	 * Sets the session bean.
	 *
	 * @param sessionBean the new session bean
	 */
	public void setSessionBean(BBBSessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

}