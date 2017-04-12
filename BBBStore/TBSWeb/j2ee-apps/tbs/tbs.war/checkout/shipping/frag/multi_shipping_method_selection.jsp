<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/GetApplicableShippingMethodsDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>

	<%-- Variables --%>
	<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
	<dsp:getvalueof var="index" param="cisiIndex" />
	<dsp:getvalueof var="sku" param="sku" />
	<dsp:getvalueof var="shippingMethod" param="shippingMethod" />
	<dsp:getvalueof var="shippingGroupName" param="shippingGroupName" />
	<dsp:getvalueof param="commItem.quantity" var="qty"/>
	<dsp:getvalueof param="commItem.id" var="commerceid"/>
	<dsp:getvalueof var="skuid" param="commItem.auxiliaryData.catalogRef.id" />
	
	<dsp:getvalueof var="listPrice" param="commItem.priceInfo.listPrice" />
	<dsp:getvalueof var="salePrice" param="commItem.priceInfo.salePrice" />
	<c:set var="lineAmt" value="0.0"/>
	<c:choose>
       <c:when test="${salePrice gt 0.0}">
       		<c:set var="lineAmt" value="${salePrice}"/>
       </c:when>
       <c:otherwise>
       		<c:set var="lineAmt" value="${listPrice}"/>
       </c:otherwise>
   </c:choose>

	<dsp:droplet name="GetApplicableShippingMethodsDroplet">
		<dsp:param name="operation" value="perSku" />
		<dsp:param name="order" bean="ShoppingCart.current" />
		<dsp:param name="isMulti" value="${true}"/>
		<dsp:param name="lineAmt" value="${lineAmt}"/>
		<dsp:param name="commItem" param="commItem"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="mapShip" param="skuMethodsMap" />
			<div class="shipping-methods" id="shippingMethod_${index}${sku}">
				<dsp:droplet name="ForEach">
					<dsp:param name="array" value="${mapShip[sku]}" />
					<dsp:param name="sortProperties" value="shippingCharge,shipMethodId"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="countIndex" param="count" />
						<dsp:getvalueof var="shipMethodDescription" param="element.shipMethodDescription" />
						<dsp:getvalueof var="shipMethodId" param="element.shipMethodId" />
						<dsp:getvalueof var="cisiCount" param="cisiCount" />
						<label class="inline-rc radio gray-panel" id="lblshippingMethod${shipMethodDescription}" for="shippingMethod_${index}${sku}${shipMethodDescription}">
							<dsp:input type="radio" name="shippingMethods_ProdName_${index}${sku}" id="shippingMethod_${index}${sku}${shipMethodDescription}" value="${shipMethodId}" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingMethod">
								<dsp:tagAttribute name="aria-checked" value="true"/>
								<dsp:tagAttribute name="aria-labelledby" value="lblshippingMethod${shipMethodDescription} errorshippingMethod"/>
							</dsp:input>
							<span></span>
							<c:choose>
								<c:when test="${HolidayMessagingOn}">
									<dsp:include src="/tbs/common/holidayMessaging.jsp">								 		
							 			<dsp:param name="timeframe" value="${timeframe}"/>
							 			<dsp:param name="shipMethodDesc" value="${shipMethodDescription}"/>
										<dsp:param name="shippingCharge" param="element.shippingCharge"/>
										<dsp:param name="shipMethod" value="${shipMethodId}"/>
							 		</dsp:include>
								</c:when>
								<c:otherwise>
									<c:out value="${shipMethodDescription}" /> (<dsp:valueof param="element.shippingCharge" converter="currency" number="000.00"/>)
								</c:otherwise>
							</c:choose>
							<c:if test="${shipMethodId eq '3g'}">
								<dsp:droplet name="ForEach">
									<dsp:param name="array" bean="ShoppingCart.current.shippingGroups" />
									<dsp:param name="elementName" value="shipGroup"/>
									<dsp:oparam name="output">
										<dsp:getvalueof param="shipGroup.shippingMethod" var="shipme"/>
										<dsp:getvalueof param="shipGroup.autoWaiveFlag" var="autoWaiveFlag"/>
										<c:if test="${shipme eq '3g' and autoWaiveFlag}">
											(<bbbl:label key="lbl_autowaive_message" language="${pageContext.request.locale.language}" />)
										</c:if>
									</dsp:oparam>
								</dsp:droplet>
							</c:if>
						</label>
					</dsp:oparam>
				</dsp:droplet>
			</div>
		</dsp:oparam>
	</dsp:droplet>
	
	<c:if test="${MapQuestOn && not bopusAllowed and not ltlItem and not vdcSku}">
		<label class="inline-rc radio gray-panel" id="lblshippingMethodStorePickup" for="shippingMethod_${index}${sku}StorePickup" data-qty="${qty}" data-shipid="${shippingGroupName}" data-commerceid="${commerceid}" data-skuid="${skuid}">
			<input type="radio" name="shippingMethods_ProdName_${index}${sku}" id="shippingMethod_${index}${sku}StorePickup" value="storepickup">
			<span></span>
			Store Pick Up
		</label>
	</c:if>	
</dsp:page>
