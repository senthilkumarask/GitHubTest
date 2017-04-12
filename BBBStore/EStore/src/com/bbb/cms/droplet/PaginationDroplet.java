package com.bbb.cms.droplet;


import java.util.ArrayList;
import java.util.List;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;

import com.bbb.cms.GuidesTemplateVO;



public class PaginationDroplet extends BBBDynamoServlet {

	private static final String OUTPUT = "output";
	private static final String GUIDE_RETURN_LIST = "guideReturnList";
	private static final String GUIDE_LIST = "guideList";
	private static final String PER_PAGE = "perPage";
	private static final String PAGE_NO = "pageNo";
	private static final String SEE_ALL = "seeAll";
	private static final String PAGE_COUNT = "pageCount";
	private static final String EMPTY = "empty";


	@SuppressWarnings("unchecked")
	public void service(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response)
	throws javax.servlet.ServletException, java.io.IOException {

		
		logDebug("starting method PaginationDroplet");
		

		Integer pageNo=1;
		Integer perPage=2;
		boolean seeAll = false;
		List<GuidesTemplateVO> guideList;
		List<GuidesTemplateVO> guideReturnList = new ArrayList<GuidesTemplateVO>();
		try {
			if(request.getLocalParameter(PAGE_NO) !=null){
				pageNo = Integer.parseInt((String) request.getLocalParameter(PAGE_NO));
			}

			if(request.getLocalParameter(PER_PAGE) !=null){
				perPage = Integer.parseInt((String) request.getLocalParameter(PER_PAGE));
			}

			if(request.getLocalParameter(SEE_ALL) != null){
				seeAll = ((String)request.getLocalParameter(SEE_ALL)).equals("true");  
			}
		} catch (NumberFormatException e) {
			
				logError("Issue with request parameters page No"+pageNo +"and per page"+perPage);
			
		}

		guideList = (List<GuidesTemplateVO>)request.getLocalParameter(GUIDE_LIST);
		if(guideList != null && guideList.size() > 0){
			int forStart = 1;
			int forEnd = 1;

			if(!seeAll){
				forStart=((pageNo-1)*perPage);
				forEnd=((pageNo*perPage)-1);
			} else {
				forStart = 0;
				forEnd = guideList.size();
			}

			for(int i=forStart;i<=forEnd;i++){

				if(!(i>=guideList.size())){
					guideReturnList.add(guideList.get(i));
				}

			}

			long pageCount = (long)Math.ceil((double)guideList.size()/perPage);
			
				logDebug("GuideList : "+guideList.size());
				logDebug("perPage : "+perPage);
				logDebug("Page Count : "+pageCount);
			
			request.setParameter(PAGE_COUNT, pageCount);
			request.setParameter(GUIDE_RETURN_LIST, guideReturnList);
			request.serviceParameter(OUTPUT, request, response);
		} else {
			request.serviceParameter(EMPTY, request, response);
		}

	}
	
	 

}










