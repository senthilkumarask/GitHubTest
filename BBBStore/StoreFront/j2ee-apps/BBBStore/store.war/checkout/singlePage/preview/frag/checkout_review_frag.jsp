 <dsp:page>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/commerce/order/purchase/CommitOrderFormHandler"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/BBBSkuPropDetailsDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/PromotionExclusionMsgDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
	<dsp:importbean bean="/com/bbb/account/TrackingInfoDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/BBBPackNHoldDroplet"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:getvalueof var="sessionBabyCA" bean="SessionBean.babyCA" />
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<dsp:getvalueof var="isFromPreviewPage" param="isFromPreviewPage"/>
	<c:set var="sddSignatureThreshold"><bbbc:config key="sddSignatureThreshold" configName="SameDayDeliveryKeys" /></c:set>
	<c:set var="totalStateTax" value="0" scope="request" />
	<c:set var="totalCountyTax" value="0" scope="request" />
	<c:set var="internationalCCFlag" value="${sessionScope.internationalCreditCard}"/>
	<dsp:droplet name="BBBPackNHoldDroplet">
            <dsp:param name="order" param="order"/>
            <dsp:oparam name="output">
               <dsp:getvalueof param="isPackHold" var="isPackHold"/>
			   <dsp:getvalueof param="isPackAndHoldFlag" var="isPackAndHoldFlag"/>
           </dsp:oparam>
     </dsp:droplet>
     <dsp:getvalueof var="order" param="order"/>
        <dsp:droplet name="/com/bbb/commerce/checkout/droplet/SuperScriptDroplet">
	        <dsp:param name="order" param="order"/>
	        <dsp:oparam name="output">
	           <dsp:tomap  var="placeHolderMap" param="SuperScriptMap"/>
	        </dsp:oparam>
        </dsp:droplet>
	<c:if test="${isPackHold eq true}"> 
		<dsp:droplet name="/com/bbb/commerce/checkout/droplet/BBBBeddingKitsAddrDroplet">
	 	  	<dsp:param name="order" param="order"/>
	 	  	<dsp:param name="shippingGroup" param="order.shippingGroups"/>
	 	   	<dsp:param name="isPackHold" value="${true}"/>
	 	   	<dsp:oparam name="beddingKit">
           		 <dsp:getvalueof var="beddingShipAddrVO" param="beddingShipAddrVO" />
           		<c:set var="beddingKit" value="true"/>
           	</dsp:oparam>
		   	<dsp:oparam name="weblinkOrder">
           		 <dsp:getvalueof var="beddingShipAddrVO" param="beddingShipAddrVO" />
           		<c:set var="weblinkOrder" value="true"/>
           	</dsp:oparam>
           	<dsp:oparam name="notBeddingKit">
           		<c:set var="beddingKit" value="false"/>
           	</dsp:oparam>
	</dsp:droplet>
	</c:if>
	<c:if test="${beddingShipAddrVO != null && isPackHold eq true }">
		<dsp:getvalueof var="collegeName" value="${beddingShipAddrVO.collegeName}"/>
	</c:if>
	<c:set var="isPayPal" value="${false}"/>
	<dsp:importbean bean="/com/bbb/commerce/droplet/PaypalDroplet"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:getvalueof var="isShippingMethodChanged" param="isShippingMethodChanged" />
	<dsp:getvalueof var="orderPriceInfo" vartype="java.lang.Object" bean="ShoppingCart.current.priceInfo"/>
	<dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
	<c:if test="${payPalOrder || orderPriceInfo == null}">
		<dsp:droplet name="/atg/commerce/order/purchase/RepriceOrderDroplet">
			<dsp:param value="OP_REPRICE_TAX" name="pricingOp"/>
		</dsp:droplet>
	</c:if>
	<dsp:getvalueof var="sessionBeanNull" bean = "PayPalSessionBean.sessionBeanNull"/>
	<dsp:setvalue value="false" bean = "PayPalSessionBean.fromPayPalPreview"/>
	<dsp:droplet name="PromotionExclusionMsgDroplet">
	<dsp:param name="order" bean="ShoppingCart.current"/>
	<dsp:oparam name="output">
		<dsp:getvalueof var="promoExclusionMap" vartype=" java.util.Map" param="promoExclusionMap" scope="request"/>
	</dsp:oparam>
	</dsp:droplet>

	<dsp:getvalueof var="isFromOrderDetail" param="isFromOrderDetail"/>	
	<dsp:getvalueof var="orderDate" param="orderDate"/>	
	
	<c:if test="${isFromOrderDetail}">
		<dsp:droplet name="/com/bbb/commerce/cart/droplet/CartRegistryInfoDroplet">
			  <dsp:param name="order" param="order"/>
		</dsp:droplet>
    </c:if>

	<c:set var="showLinks" scope="request">
		<dsp:valueof param="showLinks"/>
	</c:set>
	   
   
	<dsp:getvalueof var="order" param="order"/>
	<dsp:getvalueof var="onlineOrderNumber" param="order.onlineOrderNumber"/>
	<dsp:getvalueof var="bopusOrderNumber" param="order.bopusOrderNumber"/>
	<dsp:getvalueof var="orderSubStatus" param="order.subStatus"/>
	<dsp:getvalueof var="hideOrderNumber" param="hideOrderNumber"/>	
	
	
	<dsp:getvalueof var ="porchServiceErrorMsg" bean="PayPalSessionBean.porchServiceErrorMsg" />
	 <c:if test="${not empty porchServiceErrorMsg}">
	 <dsp:setvalue value="" bean="PayPalSessionBean.porchServiceErrorMsg" />
		<p class="error noMarTop padBottom_10 marLeft_10 grid_6">
			${porchServiceErrorMsg} 
		</p>
	</c:if>
	
	<div class="grid_8 clearfix">
			<c:if test="${(not empty onlineOrderNumber && not empty bopusOrderNumber) || not isFromOrderDetail}">
				<h1 class="SPCSectionHeading noBorderBot">Preview
					<c:if test="${empty hideOrderNumber || (not empty hideOrderNumber && hideOrderNumber =='false') }">
					<span class="bold upperCase">${onlineOrderNumber}</span>
					</c:if>
					<c:if test="${orderSubStatus=='DUMMY_RESTORE_INVENTORY'|| orderSubStatus=='DUMMY_IGNORE_INVENTORY' }">
						<p><font color="red" class="noMar bold"><bbbl:label key="lbl_test_order" language="${pageContext.request.locale.language}"/></font></p>
					</c:if>
				</h1>
		    </c:if>
	</div>
	<div class="grid_8" id="leftCol">
		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
			<dsp:param name="array" param="order.shippingGroups" />
			<dsp:param name="elementName" value="shippingGroup" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
					<dsp:droplet name="/atg/dynamo/droplet/Compare">
						<dsp:param name="obj1" param="shippingGroup.repositoryItem.type"/>
						<dsp:param name="obj2" value="hardgoodShippingGroup"/>
						<dsp:oparam name="equal">
							<dsp:getvalueof var="commerceItemRelationshipCount" param="shippingGroup.commerceItemRelationshipCount"/>
							
							<%-- Start get count of bbb commerceItem --%>
							<c:if test="${commerceItemRelationshipCount gt 0}">
								<c:set var="bbbItemcount" value="0" scope="page" />
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param name="array" param="shippingGroup.commerceItemRelationships" />
										<dsp:param name="elementName" value="commerceItemRelationship" />
										<dsp:oparam name="output">
											<dsp:droplet name="/atg/dynamo/droplet/Compare">
											  <dsp:param name="obj1" param="commerceItemRelationship.commerceItem.repositoryItem.type"/>
											  <dsp:param name="obj2" value="bbbCommerceItem"/>
												<dsp:oparam name="equal">
														<dsp:getvalueof var="quantity" param="commerceItemRelationship.quantity"/>
														<c:set var="bbbItemcount" value="${bbbItemcount + quantity}" scope="page" /> 
												</dsp:oparam>
											</dsp:droplet>
										</dsp:oparam>
								</dsp:droplet>
							</c:if>
							<%-- End get count of bbb commerceItem --%>
							
							<%-- Start trackingNumber --%>
							<c:if test="${empty showLinks}">
								<c:if test="${not empty onlineOrderNumber}">
									<c:if test="${not empty onlineOrderNumber && not empty bopusOrderNumber}">
										<c:if test="${isFromOrderDetail}">
											<p><bbbl:label key="lbl_spc_checkoutconfirmation_split_order_message" language="${language}"/><p>
										</c:if>
									</c:if>									
									<dsp:getvalueof var="billingAddress" param="order.billingAddress"/>	
									<c:if test="${isFromOrderDetail}">
										<h3 class="sectionHeading">${bbbItemcount} <bbbl:label key="lbl_spc_item_shipping_to" language="${language}"/> <span>
										${shippingGroup.shippingAddress.firstName} ${shippingGroup.shippingAddress.lastName}</span></h3>
									</c:if>
									<c:choose>
									<c:when test="${isFromOrderDetail == true}">
										<dsp:droplet name="TrackingInfoDroplet">
										<dsp:oparam name="orderOutput">
											<dsp:getvalueof var="carrierURL" param="carrierURL"/>
										</dsp:oparam>
										</dsp:droplet>
								
										<ul class="shippingStatusDetails">
											<li class="clearfix">
												<div class="grid_5 alpha">
													<p><strong><bbbl:label key="lbl_spc_order_detail_shipping_status" language="${language}"/></strong></p>
													<c:choose>
														<c:when test="${empty shippingGroup.stateDetail}">
															<p class="marTop_5">${order.stateAsString}</p>
															<c:set var="orderStatus" value="${fn:toLowerCase(order.stateAsString)}"/>
														</c:when>
														<c:otherwise>
															<p class="marTop_5">${shippingGroup.stateDetail}</p>
															<c:set var="orderStatus" value="${fn:toLowerCase(shippingGroup.stateDetail)}"/>
														</c:otherwise>
													</c:choose>	
												</div>
												<div class="grid_2">
													<p><strong><bbbl:label key='lbl_trackorder_tracking' language='${pageContext.request.locale.language}' /></strong></p>
												<c:choose>
												<c:when test="${orderStatus eq 'cancelled' || orderStatus eq 'canceled'}">
												</c:when>
												<c:otherwise>
													<ul>
													<dsp:droplet name="/atg/dynamo/droplet/ForEach">
													<dsp:param name="array" value="${shippingGroup.trackingInfos}" />
													<dsp:param name="elementName" value="TrackingInfo" />
													<dsp:oparam name="output">
														<dsp:getvalueof var="trackingNumber" param="TrackingInfo.trackingNumber" />
														<dsp:getvalueof var="carrierName" param="TrackingInfo.carrierCode" />
														<dsp:getvalueof var="imageKey" value="${carrierName}_Image"/>
														<dsp:getvalueof var="trackKey" value="${carrierName}_Track"/>
														<dsp:getvalueof var="carrierImageURL" value="${carrierURL[imageKey]}"/>
														<dsp:getvalueof var="carrierTrackUrl" value="${carrierURL[trackKey]}"/>
														<dsp:getvalueof var="trackURL" value="${carrierTrackUrl}${trackingNumber}"/>

														   <c:choose>
                       										<c:when test="${carrierTrackUrl  ne null && not empty carrierTrackUrl}">
																<!-- BBBH-3534 STORY 28 | Changes for SDD tracking  start -->
                       											<li class="marBottom_10 trackingIcon"><a href="${trackURL}" target="_blank"><img alt="${carrierName}" src="${imagePath}${carrierImageURL}" align="left"/>${trackingNumber}</a></li>
																<!-- BBBH-3534 STORY 28 | Changes for SDD tracking  end -->
                       										</c:when>
                       										<c:otherwise>
                       											<li class="marBottom_10">${trackingNumber}</li>
                       										</c:otherwise>
                       									</c:choose>

													</dsp:oparam>
													</dsp:droplet>
													</ul>
												</c:otherwise>
												</c:choose>
												</div>
											</li>
										</ul>
									</c:when>
									</c:choose>	
								</c:if>
							</c:if>
							<%-- End tracking Number --%>
								<c:if test="${not empty isShippingMethodChanged}">
									<div class="error grid_8">
										<bbbe:error key="${isShippingMethodChanged}" language="${language}"/>
									</div>
								</c:if>
								
							<%-- Shipping Items details Start here --%>
							<div class="sectionHeadingBorderTop grid_8">
							<c:if test="${shippingGroup.shippingMethod eq 'SDD' && isFromPreviewPage}">
								<jsp:useBean id="placeHolderSignature" class="java.util.HashMap" scope="request"/>
								<c:set target="${placeHolderSignature}" property="signatureThreshold">${sddSignatureThreshold}</c:set>
								<bbbt:textArea key="txt_sdd_signature_msg_preview" placeHolderMap="${placeHolderSignature}" language="${pageContext.request.locale.language}"/>
							</c:if>
							<c:if test="${commerceItemRelationshipCount gt 0}">																
									<ul class="SPCshippingItemAddDetails marTop_10">
										<li class="clearfix">
											<dsp:include page="/checkout/singlePage/preview/frag/shipping_group_order_details.jsp" flush="true">
												<dsp:param name="shippingGroup" value="${shippingGroup}"/>
												<dsp:param name="beddingKit" value = "${beddingKit}"/>
												<dsp:param name="collegeName" value = "${collegeName}"/>
												<dsp:param name="weblinkOrder" value = "${weblinkOrder}"/>
												<dsp:param name="isPackAndHoldFlag" value="${isPackAndHoldFlag}"/>
											</dsp:include>
											<dsp:include page="/checkout/singlePage/preview/frag/gift_wrap.jsp" flush="true">
												<dsp:param name="shippingGroup" value="${shippingGroup}"/>
												<dsp:param name="order" param="order"/>
											</dsp:include>
										</li>
									</ul>
		<dsp:getvalueof var="isFromOrderHistory" param="isFromOrderHistory"/>	
		<c:choose>
			<c:when test="${isFromOrderHistory == true}">
			</c:when>
			<c:otherwise>
		<dsp:form name="commit_form" id="chekoutReview" method="post" >
		<%-- Billing information Starts here --%>
								<c:if test="${not isPayPal}">
	
								<dsp:getvalueof var="billingAddress" param="order.billingAddress"/>	
								<div class="SPCbordertop">
								<ul>
								<c:if test="${not empty billingAddress.city}">
								<span class="grid_3 SPCpreviewText">
									<h3 class="subHeading"><bbbl:label key="lbl_spc_preview_billingaddress" language="<c:out param='${language}'/>"/></h3></span>
									<span class="marTop_5"><dsp:valueof param="order.billingAddress.firstName" valueishtml="false"/> <dsp:valueof param="order.billingAddress.lastName" valueishtml="false"/>,</span>
									<c:if test="${order.billingAddress.companyName != ''}">
									 	<span><dsp:valueof param="order.billingAddress.companyName" valueishtml="false"/></span>
									</c:if>									
									<span>
										<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
											<dsp:param name="value" param="order.billingAddress.address2"/>
											<dsp:oparam name="true">
												<dsp:valueof param="order.billingAddress.address1" valueishtml="false"/>,
											</dsp:oparam>
											<dsp:oparam name="false">
												<dsp:valueof param="order.billingAddress.address1" valueishtml="false"/>, <dsp:valueof param="order.billingAddress.address2" valueishtml="false"/>,
										</dsp:oparam>
								</dsp:droplet>
									</span>
									<span><dsp:valueof param="order.billingAddress.city" valueishtml="false"/>, <dsp:valueof param="order.billingAddress.state" /> <dsp:valueof param="order.billingAddress.postalCode" valueishtml="false"/></span>
									<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
										<dsp:param name="value" param="order.billingAddress.countryName"/>
										<dsp:oparam name="false">
											<c:choose>	
                                      			<c:when test="${internationalCCFlag}">
                                      				<span class="countryName internationalCountry"><dsp:valueof param="order.billingAddress.countryName" valueishtml="false"/></span>
												</c:when>
												<c:otherwise>
                                      				<p class="countryName hidden"><dsp:valueof param="order.billingAddress.countryName" valueishtml="false"/></p>
												</c:otherwise>
											</c:choose>	
                                      		<span class="country hidden"><dsp:valueof param="order.billingAddress.country" /></span>
                                      	</dsp:oparam>
									</dsp:droplet>									
									<c:if test="${not empty showLinks}">
										<span class="marTop_10"><a class="capitalize" href="${pageContext.request.contextPath}/checkout/checkout_single.jsp?isFromPreview=true#spcBilling" title="<bbbl:label key="lbl_spc_preview_edit" language="<c:out param='${language}'/>"/>"><strong><bbbl:label key="lbl_spc_preview_edit" language="<c:out param='${language}'/>"/></strong></a></span>
										</c:if>
								</c:if>
								</ul>
								</div>
							</c:if>
						<div class="SPCbordertop">
			<dsp:getvalueof var="cartEmpty" param="ShoppingCart.empty"/>
			<c:if test="${not cartEmpty}">
                <%-- TODO: Make bbblabel for this text --%>
                <p class="error errorSummaryMsg hidden"><bbbl:label key="lbl_one_or_more_errors" language="${pageContext.request.locale.language}"/></p>
						<ul class="SPCPayment">
						
							<span class="grid_3 SPCpreviewText"><h3 class="subHeading"><bbbl:label key="lbl_spc_preview_billingmethod" language="<c:out param='${language}'/>"/><c:if test="${currentSiteId eq 'BedBathCanada'}"><sup>${placeHolderMap.creditCardDisclaimer}</sup></c:if></h3></span>
							<span class="paymentMethod grid_4 noMarLeft marBottom_20">
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param name="array" param="order.paymentGroups"/>
								<dsp:param name="elementName" value="paymentGroup" />
								<dsp:oparam name="output">
									<dsp:droplet name="Switch">
			                			<dsp:param name="value" param="paymentGroup.paymentMethod"/>
			                			<dsp:oparam name="paypal">
										<c:set var="isPayPal" value="${true}"/>
										<li class="grid_4 noMarLeft marBottom_10">
			                				<span class="grid_1 noMar"><bbbl:label key="lbl_spc_paypal_title" language="<c:out param='${language}'/>"/></span>
											<span class="grid_3 marBottom_10"><dsp:valueof param="paymentGroup.payerEmail" /><br>
											<span class="marTop_10 editPreviewLink"><a class="capitalize" href="${pageContext.request.contextPath}/checkout/checkout_single.jsp?isFromPreview=true#spcPaypal" title="<bbbl:label key="lbl_spc_preview_edit" language="<c:out param='${language}'/>"/>"><strong><bbbl:label key="lbl_spc_preview_edit" language="<c:out param='${language}'/>"/></strong></a></span>
											</span></li>
			                			</dsp:oparam>
			                			<dsp:oparam name="creditCard">
			                			<li class="grid_4 noMarLeft marBottom_10">
									  		<span class="grid_1 noMar">
									  		<dsp:getvalueof var="creditCardType" param="paymentGroup.creditCardType"/>
									  		<c:set var="ImageURL" value="/_assets/global/images/${fn:toLowerCase(creditCardType)}-card-icon.jpg"/>
											
											<c:choose>
												<c:when test="${creditCardType eq 'AmericanExpress'}">
													<c:set var="cvvMaxLength" value="4"/>
												</c:when>
												<c:otherwise>
													<c:set var="cvvMaxLength" value="3"/>
												</c:otherwise>
											</c:choose>
									  		<img class="creditCardIcon" src="${ImageURL}" width="41" height="26" alt="${creditCardType}">
											</span>
											<span class="grid_3 noMarLeft marBottom_10">
											${creditCardType}
											<dsp:droplet name="/com/bbb/commerce/checkout/droplet/BBBCreditCardDisplayDroplet">
											
												<dsp:param name="creditCardNo" param="paymentGroup.creditCardNumber"/>
												<dsp:oparam name="output">	
												<dsp:valueof param="maskedCreditCardNo"/>		
												</dsp:oparam>
											</dsp:droplet>
											<br>
											<span class="marTop_10 editPreviewLink"><a class="capitalize" href="${pageContext.request.contextPath}/checkout/checkout_single.jsp?isFromPreview=true#spcCreditCard" title="<bbbl:label key="lbl_spc_preview_edit" language="<c:out param='${language}'/>"/>"><strong><bbbl:label key="lbl_spc_preview_edit" language="<c:out param='${language}'/>"/></strong></a></span>
											</span>
											<span>
									  			<dsp:getvalueof var="cardVerificationNumber" param="paymentGroup.cardVerificationNumber"/>						  			
												<dsp:getvalueof var="cardVerNumber" bean="CommitOrderFormHandler.cardVerNumber"/>	
												<dsp:getvalueof var="isExpress" bean="ShoppingCart.current.expressCheckOut"/>
						
									  			<c:if test="${not empty showLinks}">
													<c:if test="${isExpress}">
														<div class="input alpha omega noMar">
															<div class="label">
																<label id="lblsecurityCode" for="securityCode"> <bbbl:label key="lbl_spc_payment_securityCode" language="<c:out param='${language}'/>"/> 
																<span class="required"><bbbl:label key="lbl_spc_mandatory" language="<c:out param='${language}'/>"/></span> </label>
															</div>
															<div class="text grid_1">
																<dsp:input type="text" id="securityCode" name="securityCode" bean="CommitOrderFormHandler.cardVerNumber" autocomplete="off" iclass="textRight" maxlength="${cvvMaxLength}">
                                                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                                                    <dsp:tagAttribute name="aria-labelledby" value="lblsecurityCode errorsecurityCode"/>
                                                                    <dsp:tagAttribute name="role" value="textbox"/>
                                                                </dsp:input>
															</div>
															<div class="whatsThis grid_3 omega marTop_5"><a class="info"> <b><bbbl:label key="lbl_spc_payment_txt" language="<c:out param='${language}'/>"/></b> 
															<span><bbbl:textArea key="txt_payment_dynamicText" language="${language}"/></span> </a></div>
															<div class="cb label">
																<label id="errorsecurityCode" for="securityCode" class="error" generated="true"></label>
															</div>
														</div>
													</c:if>
		
												</c:if>
											</span><span>&nbsp;</span>	
										 </span>
											</li>
										</dsp:oparam>
			                		</dsp:droplet>
			          			</dsp:oparam> 
								</dsp:droplet>
								<dsp:include page="/checkout/singlePage/preview/frag/giftcard_balance_frag.jsp" flush="true">
									<dsp:param name="order" param="order"/>
								</dsp:include>
                           <dsp:droplet name="BBBSkuPropDetailsDroplet">
		                   <dsp:param name="order" value="${order}"/>
 	 
	                    	<dsp:oparam name="output">
	                    	   <dsp:getvalueof var="skuList" param="skuProdStatus" scope="request"/>
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param name="array" value="${skuList}" />
								<dsp:oparam name="output">
								<dsp:getvalueof var="size" param="size" />
								<c:set var="size" value="${size}"/>
								</dsp:oparam>
								</dsp:droplet> 
	                    	</dsp:oparam>
	                    	</dsp:droplet> 
	                    	   <c:if test="${size eq '1' }">
                    	     		<div class="marTop_10 highlight"><bbbl:label key="lbl_spc_skuprod_statename" language="<c:out param='${language}'/>"/></div>
	                               <a class="popup" href="${contextPath}/_includes/modals/prop65.jsp">
									 <strong><bbbl:label key="lbl_spc_skuprod_link" language="<c:out param='${language}'/>"/></strong>
									</a>
	                    	   </c:if>
								 
 					   
				</ul>
		
				<div class="alpha omega SPCbordertop grid_8">
										<dsp:include page="/checkout/singlePage/preview/frag/checkout_order_items.jsp" flush="true">
											<dsp:param name="shippingGroup" value="${shippingGroup}"/>
                                <dsp:param name="order" param="order"/>
											<dsp:param name="promoExclusionMap" value="${promoExclusionMap}"/>
											<dsp:param name="isFromPreviewPage" value="${isFromPreviewPage}"/>
                            </dsp:include>
				</div>
				<div class="clearfix marTop_20">
					<c:if test="${not empty showLinks}">
						<div class="placeOrderInformation grid_12 omega marLeft_25">
							<bbbl:label key="lbl_spc_preview_placeordercaption" language="<c:out param='${language}'/>"/>
						</div>
						<div class="alpha omega">					
								<%-- Start: R2.2 Scope 258 Verified by Visa change to call handleVerifiedByVisaLookup instead of hanldeCommitOrder --%>				
                                <dsp:input id="shippingPlaceOrder1" bean="CommitOrderFormHandler.verifiedByVisaLookup" type="submit" value="Place Order Now" iclass="button-Med btnAddtocart">
                                    <dsp:tagAttribute name="aria-pressed" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="shippingPlaceOrder1"/>
                                    <dsp:tagAttribute name="role" value="button"/>
                                </dsp:input>
                                <%-- End: R2.2 Scope 258 Verified by Visa change to call handleVerifiedByVisaLookup instead of hanldeCommitOrder --%>
						</div>
					</c:if>
					<div class="grid_8 footNotes marLeft_25">
						<ol class="noMarTop">
									
			                <c:if test="${not empty taxFailureFootNote}">
			                    <li class="smallText">* . ${taxFailureFootNote}</li>
			                </c:if>
							<c:if test="${not empty ecofeeFootNote}">
								<li class="smallText"><bbbl:label key="lbl_spc_footnote_ecofee" language="<c:out param='${language}'/>" placeHolderMap="${placeHolderMap}"/></li>
							</c:if>
							<c:if test="${not empty giftWrapFootNote}">	
								<li class="smallText"><bbbl:label key="lbl_spc_footnote_giftWrap" language="<c:out param='${language}'/>" placeHolderMap="${placeHolderMap}"/></li>
							</c:if>
							<c:if test="${not empty shippingFootNote}">
								<li class="smallText"><bbbl:label key="lbl_spc_footnote_shipping" language="<c:out param='${language}'/>" placeHolderMap="${placeHolderMap}"/></li>
							</c:if>							
							<c:if test="${not empty totalFootNote}">
								<li class="smallText"><bbbl:label key="lbl_spc_footnote_total" language="<c:out param='${language}'/>" placeHolderMap="${placeHolderMap}"/></li>
							</c:if>
							<c:if test="${currentSiteId eq 'BedBathCanada'}">
							     <li class="smallText"><bbbl:textArea key="txt_billing_cc_disclaimer" language="<c:out param='${language}'/>" placeHolderMap="${placeHolderMap}"/></li>
							</c:if>
						</ol> 
					</div>
				</div>
				</c:if>	
			</div>
		</dsp:form>	
	</c:otherwise>
	</c:choose>	
					
								</c:if></div>
							<%-- Shipping Items details Ends here --%>
						</dsp:oparam>
					</dsp:droplet>	
				</dsp:oparam>
			</dsp:droplet>			
			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param name="array" param="order.shippingGroups" />
				<dsp:param name="elementName" value="shippingGroup" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>		
					<dsp:droplet name="/atg/dynamo/droplet/Compare">
						<dsp:param name="obj1" param="shippingGroup.repositoryItem.type"/>
						<dsp:param name="obj2" value="storePickupShippingGroup"/>
						<dsp:oparam name="equal">
						<dsp:getvalueof var="commerceItemRelationshipCount" param="shippingGroup.commerceItemRelationshipCount"/>
						
							<%-- Start get count of bbb commerceItem for StorePickup Shipping group --%>
							<c:if test="${commerceItemRelationshipCount gt 0}">
								<c:set var="bbbStoreItemcount" value="0" scope="page" />
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param name="array" param="shippingGroup.commerceItemRelationships" />
										<dsp:param name="elementName" value="commerceItemRelationship" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="quantityStoreItem" param="commerceItemRelationship.quantity"/>
											<c:set var="bbbStoreItemcount" value="${bbbStoreItemcount + quantityStoreItem}" scope="page" />
										</dsp:oparam>
								</dsp:droplet>
							</c:if>
							<%-- End get count of bbb commerceItem for StorePickup Shipping group  --%>
						
							<c:if test="${empty showLinks}">
								<c:if test="${not empty bopusOrderNumber}">
									<div class="marBottom_10 grid_8 alpha">
										<c:if test="${not empty onlineOrderNumber && not empty bopusOrderNumber || not isFromOrderDetail}">
											<h1 class="SPCSectionHeading noBorderBot"><bbbl:label key="lbl_spc_checkoutconfirmation_instorepickup_order" language="${language}"/> <span class="bold upperCase">${bopusOrderNumber}</span></h1>
										</c:if>
										<p><bbbl:textArea key="txt_checkoutconfirmation_instorepickup_message" language="${language}"/></p>
										<c:if test="${isFromOrderDetail}">
											<h3 class="sectionHeading">${bbbStoreItemcount} <bbbl:label key='lbl_item_to_be_picked_store' language='${pageContext.request.locale.language}' /></h3>
											<p><bbbl:label key='lbl_trackorder_status' language='${pageContext.request.locale.language}' /> : 
											<c:choose>	
												<c:when test="${empty shippingGroup.stateDetail}">
													<p class="marTop_5">${order.stateAsString}</p>
												</c:when>
												<c:otherwise>
													<p class="marTop_5">${shippingGroup.stateDetail}</p>
												</c:otherwise>
											</c:choose>	
										</c:if>
					</div>
								</c:if>
							</c:if>
							
							<%-- Items pick from store details Starts here --%>
							<c:if test="${commerceItemRelationshipCount gt 0}">								
								<div class="grid_8 alpha omega marBottom_20">									
									<dsp:include page="/checkout/singlePage/preview/frag/order_store_pickup_info.jsp" flush="true">
										<dsp:param name="shippingGroup" value="${shippingGroup}"/>
									</dsp:include>
									<dsp:include page="/checkout/singlePage/preview/frag/checkout_order_items.jsp" flush="true">
										<dsp:param name="shippingGroup" value="${shippingGroup}"/>
										<dsp:param name="promoExclusionMap" value="${promoExclusionMap}"/>
										<dsp:param name="isFromPreviewPage" value="${isFromPreviewPage}"/>
									</dsp:include>
									<dsp:include page="/checkout/singlePage/preview/frag/store_pickup_shipping_group_order_summary.jsp" flush="true">
										<dsp:param name="shippingGroup" value="${shippingGroup}"/>
									</dsp:include>
				</div>
							</c:if>
							<%-- Items pick from store details Ends here --%>
						</dsp:oparam>
					</dsp:droplet>
			</dsp:oparam>
		</dsp:droplet>

			</div>	
<%-- Billing information Ends here --%>
</dsp:page>