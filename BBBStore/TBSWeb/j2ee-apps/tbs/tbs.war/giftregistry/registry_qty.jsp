<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/TBSCommerceItemLookupDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/SKUDetailDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryVODroplet"/>
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />

	<%-- Page Variables --%>
	<dsp:getvalueof param="commerceId" var="cId"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

	<dsp:droplet name="TBSCommerceItemLookupDroplet">
		<dsp:param name="id" param="commerceId"/>
		<dsp:param name="order" bean="ShoppingCart.current"/>
		<dsp:param name="elementName" value="cItem"/>
		<dsp:oparam name="output">
			<dsp:getvalueof param="cItem.quantity" var="qty" scope="request"/>
			<dsp:getvalueof param="cItem.catalogRefId" var="skuId" scope="request"/>
			<dsp:getvalueof param="cItem.auxiliaryData.productId" var="productId" scope="request"/>
		</dsp:oparam>
	</dsp:droplet>
	<dsp:droplet name="SKUDetailDroplet">
		<dsp:param name="skuId" value="${skuId}"/>
		<dsp:oparam name="output">
			<dsp:getvalueof param="pSKUDetailVO.displayName" var="displayName" scope="request"/>
		</dsp:oparam>
	</dsp:droplet>

	<dsp:droplet name="GetRegistryVODroplet">
		<dsp:param name="registryId" param="registryId"/>
		<dsp:param name="siteId" bean="Site.id" />
		<dsp:oparam name="output">
			<dsp:getvalueof param="registryVO.primaryRegistrant.firstName" var="regFirstName"/>
			<dsp:getvalueof param="registryVO.primaryRegistrant.lastName" var="regLastName"/>
		</dsp:oparam>
	</dsp:droplet>
	<dsp:setvalue param="cItemName" value="${displayName}"/>

	<c:if test="${empty regFirstName}">
		<c:set var="regFirstName" value="the" scope="request" />
		<c:set var="regLastName" value="registrant" scope="request" />
	</c:if>

	<dsp:form action="" id="qtyForRegistrantForm" method="post">
		<div class="row qty-for-registrant">
			<div class="small-12 columns no-padding">
				<h1><bbbl:label key="lbl_qty_for_registrant" language="<c:out param='${language}'/>"/></h1>
				<p class="p-secondary">
					<bbbl:label key="lbl_you_have" language="<c:out param='${language}'/>"/> <c:out value="${qty}"/> <dsp:valueof param="cItemName" valueishtml="true"/>
					<bbbl:label key="lbl_in_your_cart" language="<c:out param='${language}'/>"/> <bbbl:label key="lbl_how_many_gifts" language="<c:out param='${language}'/>"/> <c:out value="${regFirstName}"/> <c:out value="${regLastName}"/>?
				</p>
			</div>
		</div>
		<div class="row">
			<div class="small-12 large-6 columns quantity">
				<h3>Quantity</h3>
				<div class="qty-spinner">
					<a href='#' class="button minus secondary"><span></span></a>
					<dsp:input bean="CartModifierFormHandler.quantity" iclass="quantity-input" type="text" maxlength="2" value="${qty}"/>
					<a href='#' class="button plus secondary"><span></span></a>
				</div>
			</div>
			<div class="small-12 large-6 columns">
				<a class="button small service expand qty-for-reg-submit">apply</a>

				<dsp:input bean="CartModifierFormHandler.commerceItemId" type="hidden" paramvalue="commerceId"/>
				<dsp:input bean="CartModifierFormHandler.registryId" type="hidden" paramvalue="registryId"/>
				<dsp:input bean="CartModifierFormHandler.productId" type="hidden" value="${productId}" />
				<dsp:input bean="CartModifierFormHandler.catalogRefIds"  type="hidden" value="${skuId}"/>
				<dsp:input bean="CartModifierFormHandler.allItemsForRegistry"  type="hidden" beanvalue="CartModifierFormHandler.allItemsForRegistry" />
				<dsp:input bean="CartModifierFormHandler.splitAndAssignRegistry" id="splitAndAssignRegistry" value="Assign" type="submit" iclass="hidden"/>
				<dsp:input bean="CartModifierFormHandler.fromPage" type="hidden"
												value="registryQty" />
				<%-- <dsp:input bean="CartModifierFormHandler.splitAndAssignRegistrySuccessUrl" value="${contextPath}/cart/cart.jsp" type="hidden"/>
				<dsp:input bean="CartModifierFormHandler.splitAndAssignRegistryErrorUrl" value="${contextPath}/cart/cart.jsp" type="hidden"/> --%>
			</div>
		</div>
	</dsp:form>
	<a class="close-reveal-modal">&times;</a>

</dsp:page>
