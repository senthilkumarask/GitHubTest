<%@ page contentType="text/json" %>
<dsp:importbean bean="/atg/commerce/ShoppingCart" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile" var="Profile" />

<dsp:page>
<json:object> 
<c:if test="${not empty sessionScope.priceOverrideStatusVO}">
<json:property name="success" value="${sessionScope.priceOverrideStatusVO.success}"/>
<json:property name="commerceItemId" value="${sessionScope.priceOverrideStatusVO.commerceItemId}"/>
<c:if test="${sessionScope.priceOverrideStatusVO.success == 'true'}">
<dsp:droplet name="/atg/commerce/order/droplet/BBBCartDisplayDroplet">
<dsp:param name="order" bean="ShoppingCart.current" />
<dsp:param name="fromCart" value="true" />
<dsp:oparam name="output">
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="commerceItemList" />
		<dsp:param name="elementName" value="commerceItem" />
		<dsp:oparam name="output">
		   <dsp:getvalueof id="commerceItemId" param="commerceItem.BBBCommerceItem.id"/>
		   <c:if test="${commerceItemId == sessionScope.priceOverrideStatusVO.commerceItemId}">
			    <json:property name="overrideQty" value="${sessionScope.priceOverrideStatusVO.overrideQty}" />
			    <json:property name="overridePrice" value="${sessionScope.priceOverrideStatusVO.overridePrice}" />
			    <dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
					<dsp:param name="priceObject" param="commerceItem.BBBCommerceItem" />
					<dsp:param name="profile" bean="Profile"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="quantity" param="commerceItem.BBBCommerceItem.quantity" />
						<dsp:getvalueof var="unitSavedAmount" param="priceInfoVO.unitSavedAmount"/>
						<dsp:getvalueof var="unitListPrice" param="priceInfoVO.unitListPrice"/>
						<dsp:getvalueof var="unitSalePrice" param="priceInfoVO.unitSalePrice"/>
						<dsp:getvalueof var="totalSavedAmount" param="priceInfoVO.totalSavedAmount"/>
						<dsp:getvalueof var="adjustmentsList" param="priceInfoVO.adjustmentsList"/>
						<dsp:getvalueof var="undiscountedItemsCount" param="priceInfoVO.undiscountedItemsCount"/>
						<dsp:getvalueof var="deliverySurcharge" param="priceInfoVO.deliverySurcharge"/>
						<dsp:getvalueof var="deliverySurchargeSaving" param="priceInfoVO.deliverySurchargeSaving"/>
						<dsp:getvalueof var="assemblyFee" param="priceInfoVO.assemblyFee"/>
						<dsp:getvalueof var="undiscountedItemsCount" param="priceInfoVO.undiscountedItemsCount"/>
						<dsp:getvalueof var="totalAmount" param="priceInfoVO.totalAmount"/>
						<c:choose>
							<c:when test="${undiscountedItemsCount gt 0}">
								<c:choose>
									<c:when test="${unitSavedAmount gt 0.0}">
										<json:property name="newItemTotals" value="${unitSalePrice}" />
									</c:when>
									<c:otherwise>
										<json:property name="newItemTotals" value="${unitListPrice}" />
									</c:otherwise>
								</c:choose>
							</c:when>
						</c:choose>
						<c:choose>
							<c:when test="${undiscountedItemsCount eq quantity}">								
								<json:property name="newCartTotals" value="${totalAmount}" />
							</c:when>
							<c:otherwise>
								<json:property name="newCartTotals" value="${totalAmount}" />
							</c:otherwise>
						</c:choose>
						
					</dsp:oparam>
				</dsp:droplet>		   
		   </c:if>
		</dsp:oparam>
	</dsp:droplet>
</dsp:oparam>
</dsp:droplet>
</c:if>

<c:if test="${not empty sessionScope.priceOverrideStatusVO.errorMessages}">
 <json:array name="errorMessages">
	<c:forEach items="${sessionScope.priceOverrideStatusVO.errorMessages}" var="errorMessage">	  
		 <json:object> 
		    <json:property name="fieldId" value="${errorMessage.fieldId}"/>
		    <json:property name="message" value="${errorMessage.message}"/>
		 </json:object>
	</c:forEach>
 </json:array>
</c:if>
</c:if>
</json:object> 
</dsp:page>
