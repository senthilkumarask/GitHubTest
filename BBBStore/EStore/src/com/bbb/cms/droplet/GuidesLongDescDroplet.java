package com.bbb.cms.droplet;

import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;

import com.bbb.cms.GuidesTemplateVO;
import com.bbb.cms.manager.GuidesTemplateManager;


public class GuidesLongDescDroplet extends BBBDynamoServlet {
  private GuidesTemplateManager mGuidesTemplateManager = null;


  /**
   * @return the guidesTemplateManager
   */
  public GuidesTemplateManager getGuidesTemplateManager() {
    return mGuidesTemplateManager;
  }

  /**
   * @param pGuidesTemplateManager the guidesTemplateManager to set
   */
  public void setGuidesTemplateManager(GuidesTemplateManager pGuidesTemplateManager) {
    mGuidesTemplateManager = pGuidesTemplateManager;
  }


  /**
   * This method gets  long description and interact with manager class.
   */
 
  public void service(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response)
      throws javax.servlet.ServletException, java.io.IOException {

   
      logDebug("starting method GuidesLongDescDroplet");

      
    
    final String GUIDE_ID = "guideId";
    final String EMPTY = "empty";
    final String GUIDE_LONG_DESC = "guidesLongDesc";
    final String OUTPUT = "output";
     
    
    String guideId = null; 


    if(request.getLocalParameter(GUIDE_ID) !=null){
      guideId = (String)request.getLocalParameter(GUIDE_ID);
    }


    if(StringUtils.isEmpty(guideId)) {
      request.serviceParameter(EMPTY, request, response);
      response.setStatus(404);
    }
    else{

      GuidesTemplateVO guidesLongDesc = getGuidesTemplateManager().getGuidesLongDescription(guideId);
      if(guidesLongDesc !=null){
        request.setParameter(GUIDE_LONG_DESC, guidesLongDesc);
        request.serviceParameter(OUTPUT, request, response);
      }
      else{
        request.serviceParameter(EMPTY, request, response);
        response.setStatus(404);
      }

    }
   
      logDebug("starting method GuidesLongDescDroplet");

     
  }

  
}
