/**
 * 
 */
package atg.commerce.pricing;

import java.util.Map;

import com.bbb.ecommerce.order.BBBStoreShippingGroup;

public class BBBShippingClosenessQualifierEvaluator extends
		ShippingClosenessQualifierEvaluator {

	@Override
	public void evaluateClosenessQualifier(PricingContext pPricingContext,
			Map pExtraParameters) throws PricingException {

		if (!(pPricingContext.getShippingGroup() instanceof BBBStoreShippingGroup)) {
			if (isLoggingDebug()) {
				logDebug("Calling super.evaluateClosenessQualifier(");
			}
			super.evaluateClosenessQualifier(pPricingContext, pExtraParameters);
		} 

	}
}
