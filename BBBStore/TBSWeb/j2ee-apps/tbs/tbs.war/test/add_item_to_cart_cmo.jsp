<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	 <dsp:form action="/tbs/cart/cart.jsp" method="post" name="TBSForm" formid="TBSForm" id="TBSForm">
        <dsp:input bean="CartModifierFormHandler.createCMOOrder" type="submit" value="Add TBS Item To Cart" />
     </dsp:form>
</dsp:page>
