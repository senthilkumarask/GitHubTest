package com.bbb.commerce.cart.droplet;

import java.io.IOException;
import javax.servlet.ServletException;
import atg.multisite.SiteContextManager;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;

/**
 * This servlet will get the upsell items for the given cart
 * @author pbhoomul
 *
 */
public class CartAnalyzerDroplet extends BBBDynamoServlet {
    
	public static final ParameterName EMPTY_OPARAM = ParameterName.getParameterName("empty");
	public static final ParameterName OPARAM = ParameterName.getParameterName("output");
	public static final String RECOMMEDNED_SKUS_VO = "recommSkuVO";	
	private BBBCatalogTools catalogTools = null;
    
    /**
     * 
     */
    public void service(DynamoHttpServletRequest req,
            DynamoHttpServletResponse res) throws ServletException, IOException {
        
        BBBOrderImpl order = (BBBOrderImpl)req.getObjectParameter(BBBCoreConstants.ORDER);
		String siteId = SiteContextManager.getCurrentSiteId();
        req.setParameter(RECOMMEDNED_SKUS_VO, getCatalogTools().getRecommendedSKU(siteId, order)); 
	    req.serviceLocalParameter(OPARAM, req, res);
    }
    
	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools ;
	}

	/**
	 * @param catalogTools
	 *          the catalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
}
