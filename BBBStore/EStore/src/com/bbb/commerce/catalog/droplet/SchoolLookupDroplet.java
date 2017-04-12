package com.bbb.commerce.catalog.droplet;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.vo.SchoolVO;

/**
 * This class calls the catalog API to get school details.
 * @author ajosh8
 *
 */
public class SchoolLookupDroplet extends BBBDynamoServlet{

  private BBBCatalogTools catalogTools = null;



  /**
   * Return CatalogTools
   * @return
   */
  public BBBCatalogTools getCatalogTools() {
    return catalogTools;
  }

  /**
   * Setting CatalogTools
   * @param catalogTools
   */
  public void setCatalogTools(BBBCatalogTools catalogTools) {
    this.catalogTools = catalogTools;
  }
/**
 * This method will take schoolID as as input and calls the catalog api for school details.
 */
  public void service(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response)
      throws javax.servlet.ServletException, java.io.IOException {
    logDebug("starting method SchoolLookupDroplet.service");
    String schoolId =null;

    if(request.getLocalParameter(BBBCoreConstants.SCHOOL_ID) !=null){
       schoolId = (String)request.getLocalParameter(BBBCoreConstants.SCHOOL_ID);
       logDebug("Input Paramter SchoolId :"+schoolId);
    }else{
     logDebug("School Id is Null");
      request.serviceParameter(BBBCoreConstants.EMPTY, request, response);

    }
    try {
      SchoolVO schoolVO=getCatalogTools().getSchoolDetailsById(schoolId);
      request.setParameter(BBBCoreConstants.SCHOOL_VO, schoolVO);
      request.serviceParameter(BBBCoreConstants.OUTPUT, request, response);

    } catch (BBBSystemException e) {
     	  logError(LogMessageFormatter.formatMessage(request, "SchoolLookupDroplet.service() | RepositoryException ","catalog_1068"), e);
    } catch (BBBBusinessException e) {
    	logError(LogMessageFormatter.formatMessage(request, "SchoolLookupDroplet.service() | RepositoryException ","catalog_1069"), e);
      
    }

      logDebug("Ending method SchoolLookupDroplet.service");
  }
}












