<dsp:page>
	<dsp:importbean	bean="/com/bbb/tag/droplet/ReferralInfoDroplet" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	
	<dsp:getvalueof var="email" bean="Profile.email"/>
	<dsp:getvalueof var="billingAddress" bean="ShoppingCart.last.billingAddress"/>
	<dsp:getvalueof var="shippingAddress" bean="ShoppingCart.last.shippingAddress"/>
	<dsp:getvalueof var="lastOrder" bean="ShoppingCart.last"/>
	<dsp:getvalueof var="onlineOrderNumber" bean="ShoppingCart.last.onlineOrderNumber"/>
	<dsp:getvalueof var="bopusOrderNumber" bean="ShoppingCart.last.bopusOrderNumber"/>
	<c:choose>
	<c:when test="${empty onlineOrderNumber}">
				<c:set var="genOrderCode" value="${bopusOrderNumber}" />
				</c:when>
				<c:otherwise>
				<c:set var="genOrderCode" value="${onlineOrderNumber}" />
				</c:otherwise>
				</c:choose>
	
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI"/>
	<dsp:droplet name="ReferralInfoDroplet">
		<dsp:param name="currentPage" value="${requestURI}"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="cjBaseUrl" param="cjSaleUrl" />
			<dsp:getvalueof var="wcBaseUrl" param="wcSaleUrl" />
		</dsp:oparam>
	</dsp:droplet>
	
	
	
	<c:if test="${(currentSiteId eq TBS_BedBathUSSite)}">
	   <c:set var="cat"><bbbc:config key="cat_confirm_bedBathUS" configName="RKGKeys" /></c:set>
	   <c:set var="src"><bbbc:config key="src_bedBathUS" configName="RKGKeys" /></c:set>
	   <c:set var="type"><bbbc:config key="type_3_bedBathUS" configName="RKGKeys" /></c:set>
	   <c:set var="cj_cid"><bbbc:config key="cj_cid_us" configName="ReferralControls" /></c:set>
	   <c:set var="cj_type"><bbbc:config key="cj_type_order_confirmation_us" configName="ReferralControls" /></c:set>
     </c:if>
	 <c:if test="${(currentSiteId eq TBS_BuyBuyBabySite)}">
	   <c:set var="cat"><bbbc:config key="cat_confirm_baby" configName="RKGKeys" /></c:set>
	   <c:set var="src"><bbbc:config key="src_baby" configName="RKGKeys" /></c:set>
	   <c:set var="type"><bbbc:config key="type_3_baby" configName="RKGKeys" /></c:set>
	   <c:set var="cj_cid"><bbbc:config key="cj_cid_baby" configName="ReferralControls" /></c:set>
	   <c:set var="cj_type"><bbbc:config key="cj_type_order_confirmation_baby" configName="ReferralControls" /></c:set>
     </c:if>
     <c:if test="${currentSiteId eq TBS_BedBathCanadaSite}">
		<c:set var="cj_cid"><bbbc:config key="cj_cid_ca" configName="ReferralControls" /></c:set>
		<c:set var="cj_type"><bbbc:config key="cj_type_order_confirmation_ca" configName="ReferralControls" /></c:set>
	 </c:if>
     <c:set var="rkgBaseURL"><bbbc:config key="rkg_referral_sale_url" configName="ThirdPartyURLs" /></c:set>

	<%-- Begin TagMan --%>
	<script type="text/javascript">	
		// client configurable parameters 
		window.tmParam.action_type = 'tx';
		window.tmParam.page_type = 'CheckoutConfirmation';
		window.tmParam.customer_bill_name = '${billingAddress.firstName} ${billingAddress.middleName} ${billingAddress.lastName}';		
		window.tmParam.customer_bill_addr = '${billingAddress.address1} ${billingAddress.address2} ${billingAddress.address3}';
		window.tmParam.customer_bill_city = '${billingAddress.city}';
		window.tmParam.customer_bill_state = '${billingAddress.state}';
		window.tmParam.customer_bill_postalcode = '${billingAddress.postalCode}';
		window.tmParam.customer_bill_country = '${billingAddress.country}';
		
		
		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
	        <dsp:param name="array" bean="ShoppingCart.last.shippingGroups" />
	        <dsp:param name="elementName" value="shippingGroup" />
	        <dsp:oparam name="output">
	        	<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
	            <dsp:droplet name="/atg/dynamo/droplet/Compare">
		            <dsp:param name="obj1" param="shippingGroup.repositoryItem.type"/>
		            <dsp:param name="obj2" value="hardgoodShippingGroup"/>
						<dsp:oparam name="equal">
		                   	<dsp:getvalueof var="commerceItemRelationshipCount" param="shippingGroup.commerceItemRelationshipCount"/>
		                       <c:if test="${commerceItemRelationshipCount gt 0}">
									window.tmParam.customer_ship_name = '${shippingAddress.firstName} ${shippingAddress.middleName} ${shippingAddress.lastName}';
									window.tmParam.customer_ship_addr = '${shippingAddress.address1} ${shippingAddress.address2} ${shippingAddress.address3}';
									window.tmParam.customer_ship_city = '${shippingAddress.city}';
									window.tmParam.customer_ship_state = '${shippingAddress.state}';
									window.tmParam.customer_ship_postalcode = '${shippingAddress.postalCode}';
									window.tmParam.customer_ship_country = '${shippingAddress.country}';
								</c:if>
						</dsp:oparam>
		         </dsp:droplet>       
		      </dsp:oparam>
	      </dsp:droplet>
		
		window.tmParam.levordref = '${genOrderCode}';
		window.tmParam.orderId = '${genOrderCode}';
		window.tmParam.sale_date = '${lastOrder.submittedDate}';
		window.tmParam.currency_code = '${lastOrder.priceInfo.currencyCode}';
		
		window.tmParam.order_total = '${lastOrder.priceInfo.total}';
		window.tmParam.net_total_exc_ship_tax = '${lastOrder.priceInfo.total - (lastOrder.priceInfo.tax + lastOrder.priceInfo.shipping)}';
		window.tmParam.net_total_exc_ship = '${lastOrder.priceInfo.amount + lastOrder.priceInfo.tax}';
		window.tmParam.net_total_exc_tax = '${lastOrder.priceInfo.amount + lastOrder.priceInfo.shipping}';
		window.tmParam.gross_total_inc_ship = '${lastOrder.priceInfo.total}';
		
		window.tmParam.rkg_src = '${src}';
		window.tmParam.rkg_type = '${type}';
		window.tmParam.rkg_cat = '${cat}';
		window.tmParam.rkg_base_url = '${rkgBaseURL}';
		window.tmParam.rkg_merch_id = '1518705';
		window.tmParam.rkg_cid = '';
		
		window.tmParam.cj_base_url = '${cjBaseUrl}';
		window.tmParam.cj_cid = '${cj_cid}';
		window.tmParam.cj_type = '${cj_type}';
		window.tmParam.cj_method = 'IMG';
		window.tmParam.cj_height = '1';
		window.tmParam.cj_width = '1';
		
		window.tmParam.wc_base_url = '${wcBaseUrl}';
		window.tmParam.retailer_uid = '10001';
	</script>
	<%-- End TagMan --%>
</dsp:page>