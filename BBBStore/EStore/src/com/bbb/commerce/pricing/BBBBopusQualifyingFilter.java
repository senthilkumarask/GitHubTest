package com.bbb.commerce.pricing;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.bbb.order.bean.BBBCommerceItem;

import atg.commerce.pricing.FilteredCommerceItem;
import atg.commerce.pricing.PricingContext;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PromotionQualifyingFilter;
import atg.core.util.StringUtils;

public class BBBBopusQualifyingFilter implements PromotionQualifyingFilter{

    @SuppressWarnings("rawtypes")
    @Override
    public void filterItems(int i, PricingContext pricingcontext, Map map,
            Map map1, Map map2, List list) throws PricingException {
        if(list != null) {
            for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                FilteredCommerceItem item = (FilteredCommerceItem) iterator.next();
                if(item.getWrappedItem() instanceof BBBCommerceItem &&
                        !StringUtils.isEmpty(((BBBCommerceItem) item.getWrappedItem()).getStoreId())) {
                    iterator.remove();
                }
            }
        }
        
    }

}
