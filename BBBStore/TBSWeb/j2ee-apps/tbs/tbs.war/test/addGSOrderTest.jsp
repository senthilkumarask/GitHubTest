<dsp:page>

 <bbb:pageContainer>
<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>


<dsp:form action="addGSOrderTest.jsp" method="post">

<dsp:input bean="CartModifierFormHandler.gsOrderId" value="" type="text"/>
<dsp:input bean="CartModifierFormHandler.createGSOrderSuccessURL" type="hidden" value="../cart/cart.jsp"/>
<dsp:input bean="CartModifierFormHandler.createGSOrderErrorURL" type="hidden" value="addGSOrderTest.jsp"/>
<dsp:input bean="CartModifierFormHandler.createGSOrder" type="submit" value="Add GS Order To Cart"/>
</dsp:form>



</bbb:pageContainer>
</dsp:page>