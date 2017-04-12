<dsp:page>	
	<dsp:importbean	bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler" />
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>

	<c:choose>
		<c:when test="${param.check ne null}">
			<c:set var="xx">
				{"addItemResults":[{"skuId":"${param.skuId}","prodId":"${param.productId}","qty":"${param.itemQuantity}","price":"","bts":"${param.storeId}","storeId":"${param.storeId}","registryId":"${param.registryId}","count":"${param.count}"}]}
			</c:set>
			<dsp:setvalue bean="CartModifierFormHandler.jsonResultString" value="${xx}" valueishtml="false"/>
			<dsp:input bean="CartModifierFormHandler.queryParam"
												type="hidden"
												value="${param.pageURL}" /> 
							           
			<%-- <dsp:setvalue bean="CartModifierFormHandler.addItemToOrderSuccessURL" value="${param.pageURL}" />
			<dsp:setvalue bean="CartModifierFormHandler.addItemToOrderErrorURL" value="${param.pageURL}" /> --%>
			<dsp:setvalue bean="CartModifierFormHandler.fromWishlist"  value="${true}"/>
			<dsp:setvalue bean="CartModifierFormHandler.wishlistItemId"  value="${param.wishlistItemId}"/>
			<dsp:setvalue bean="CartModifierFormHandler.registryId"  value="${param.registryId}"/>
			<dsp:setvalue bean="CartModifierFormHandler.fromCart"  value="${true}"/>
			<dsp:setvalue bean="CartModifierFormHandler.addItemToOrder" />
		</c:when>
		<c:otherwise>
	<dsp:setvalue bean="BBBShippingGroupFormhandler.commerceItemId"	value="${param.commerceItemId}" />
	<dsp:setvalue bean="BBBShippingGroupFormhandler.oldShippingId"	value="${param.oldShippingId}" />
	<dsp:setvalue bean="BBBShippingGroupFormhandler.newQuantity"	value="${param.newQuantity}" />
	<dsp:setvalue bean="BBBShippingGroupFormhandler.storeId" value="${param.storeId}" />	
	<c:if test="${param.pageURL ne null}">
		<dsp:input bean="BBBShippingGroupFormhandler.queryParam"
												type="hidden"
												value="${param.pageURL}" />
		<%-- <dsp:setvalue bean="BBBShippingGroupFormhandler.successURL" value="${param.pageURL}" />
		<dsp:setvalue bean="BBBShippingGroupFormhandler.errorURL" value="${param.pageURL}"/> --%>
		<%-- <dsp:setvalue bean="BBBShippingGroupFormhandler.systemErrorURL" value="${param.pageURL}"/> --%>
	</c:if>
	<dsp:setvalue bean="BBBShippingGroupFormhandler.changeToStorePickup" />
		</c:otherwise>
	</c:choose>
	
</dsp:page>
