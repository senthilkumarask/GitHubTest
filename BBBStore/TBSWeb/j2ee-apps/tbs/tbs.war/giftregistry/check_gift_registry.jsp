<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/TBSCommerceItemLookupDroplet"/>

	<%-- Page Variables --%>
	<dsp:getvalueof param="orderObject" var="order"/>
	<dsp:getvalueof param="commItem.BBBCommerceItem.id" var="commerceId"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
	<dsp:getvalueof param="commItem" var="commItem"/>
	<dsp:droplet name="TBSCommerceItemLookupDroplet">
		<dsp:param name="id" value="${commerceId}"/>
		<dsp:param name="order" param="order"/>
		<dsp:param name="elementName" value="cItem"/>
		<dsp:oparam name="output">
			<dsp:getvalueof param="cItem.registryId" var="registryId"/>
			<dsp:getvalueof param="cItem.quantity" var="qty"/>
		</dsp:oparam>
	</dsp:droplet>
	
	<c:if test="${commItem.BBBCommerceItem.commerceItemClassType eq 'tbsCommerceItem'}">
		<dsp:getvalueof var="cmo" value="${commItem.BBBCommerceItem.CMO}"/>
	</c:if>
	<%--Added for RM #33059 Start --%>
	<c:if test="${commItem.BBBCommerceItem.commerceItemClassType eq 'tbsCommerceItem'}">
		<dsp:getvalueof var="kirsch" value="${commItem.BBBCommerceItem.kirsch}"/>
	</c:if>
	<%--Added for RM #33059 End --%>
	<c:if test="${commItem.skuDetailVO.ltlItem}"> 
		<dsp:getvalueof var="ltlItem" value="${commItem.skuDetailVO.ltlItem}"/>
	</c:if>
    <%--Modified for RM #33059 Start --%>   
	<label class="inline-rc checkbox <c:if test="${kirsch ||cmo}">disabled</c:if>" for="giftForReg_${commerceId}${registryId}">
		<input type="checkbox" id="giftForReg_${commerceId}${registryId}" data-cid="${commerceId}" class="gift-for-reg <c:if test="${kirsch || cmo}">disabled</c:if>" <c:if test="${kirsch || cmo}">disabled="disabled"</c:if> <c:if test="${not empty registryId and not cmo and not kirsch}">checked</c:if>>
		<span></span>
		This is a gift for a Registrant
	</label>
	<%--Modified for RM #33059 End --%> 
	
	<div class="dropdown-container <c:if test="${empty registryId}">hidden</c:if>">
		<a href="#" data-dropdown="gift_${commerceId}${registryId}" class="small secondary radius button dropdown expand">Select A Registry<span></span></a>
		<ul id="gift_${commerceId}${registryId}" data-dropdown-content class="f-dropdown">
			<li><a href="#">SELECT A REGISTRY</a></li>
			<dsp:droplet name="ForEach">
				<dsp:param name="array" bean="ShoppingCart.current.registryMap"/>
				<dsp:param name="elementName" value="registrymap"/>
				<dsp:oparam name="output">
					<dsp:getvalueof param="registrymap" var="registratantVO"/>
					<dsp:getvalueof param="key" var="currentRegistryId"/>
					<li<c:if test="${registryId eq currentRegistryId}"> class="selected"</c:if>>
						<a href="#" data-registry-id="${currentRegistryId}" data-cid="${commerceId}" data-qty="${qty}" class="registrant-select">
							<c:out value="${registratantVO.primaryRegistrantFirstName}" /> <c:out value="${registratantVO.primaryRegistrantLastName}" />
						</a>
					</li>
				</dsp:oparam>
			</dsp:droplet>
			<li><a class="search-for-reg" data-cid="${commerceId}">SEARCH FOR A REGISTRY</a></li>
		</ul>
	</div>

	<c:if test="${empty order.registryMap}">
		<c:set var="regMapEmpty" value="true" scope="request" />
	</c:if>

</dsp:page>
