<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/commerce/order/droplet/BBBCartRegistryDisplayDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/CheckoutProductDetailDroplet"/>

	<%-- Variables --%>
	<dsp:getvalueof var="clearContainer" bean="BBBShippingGroupFormhandler.clearContainer"/>
	<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
	<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
	<c:set var="isStockAvailability" value="yes" scope="page"/>
	<c:set var="TBS_BedBathUSSite">
		<bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BuyBuyBabySite">
		<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BedBathCanadaSite">
		<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<dsp:getvalueof var="isFromPreview" param="isFromPreview" />
	<dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
	<c:if test="${clearContainer eq true and isFromPreview ne 'true'|| payPalOrder eq true and isFromPreview eq 'true'}">
		<dsp:droplet name="ShippingGroupDroplet">
			<dsp:param name="clear" value="true" />
			<dsp:param name="createOneInfoPerUnit" value="false" />
			<dsp:param name="shippingGroupTypes" value="hardgoodShippingGroup,storeShippingGroup" />
			<dsp:param name="initBasedOnOrder" value="true" />
		</dsp:droplet>
	</c:if>
	<dsp:getvalueof var="cisiObjs" bean="BBBShippingGroupFormhandler.cisiItems" />

	<dsp:droplet name="ForEach">
		<dsp:param name="array" value="${cisiObjs}" />
		<dsp:oparam name="output">
			<dsp:param name="cisi" param="element" />
			<dsp:getvalueof id="cisi" param="element"></dsp:getvalueof>
			<dsp:getvalueof var="index" param="index" />
			<dsp:getvalueof var="registryId" param="cisi.commerceItem.registryId"/>

			<c:if test="${registryId ne null}">
				<div class="small-12 columns no-padding-left">
					<h3 class="subheader">
						<dsp:getvalueof bean="ShoppingCart.current.registryMap.${registryId}" var="registratantVO"/>
						<span><bbbl:label key="lbl_cart_registry_from_text" language="<c:out param='${language}'/>"/></span>
						<span>${registratantVO.primaryRegistrantFirstName}<c:if test="${not empty  registratantVO.coRegistrantFirstName}">&nbsp;&amp;&nbsp;${registratantVO.coRegistrantFirstName}</c:if><bbbl:label key="lbl_cart_registry_name_suffix" language="<c:out param='${language}'/>"/></span>

						<dsp:droplet name="BBBCartRegistryDisplayDroplet">
						<dsp:param name="registryId" value="${registryId}" />
						<dsp:param name="order" bean="ShoppingCart.current" />
						<dsp:oparam name="regOutput">
							<dsp:getvalueof var="registrantEmail" param="registrantEmail"/>
							<span><dsp:valueof param="registryType"/></span>
							<span><bbbl:label key="lbl_cart_registry_text" language="<c:out param='${language}'/>"/></span>
						</dsp:oparam>
						</dsp:droplet>
					</h3>
				</div>

				<%-- splitShippingGroupName property of CommerceItemShippingInfo is used for sendShippingConfEmail --%>
				<!-- DO NOT DELETE ...... the shipping confirmation is commented since DS is not ready to send confirmation emails -->
				<%-- <div class="fl marLeft_20">
					<div class="checkboxItem input clearfix noPad noBorder">
						<div class="checkbox noMar">
							<dsp:input type="checkbox" name="sendShippingConfEmail_${index}" id="sendShippingConfEmail_${index}"
							bean="BBBShippingGroupFormhandler.cisiItems[${index}].splitShippingGroupName" value="${registratantVO.registrantEmail}" />
						</div>
						<div class="label">
							<label for="sendShippingConfEmail_${index}">
								<bbbl:label key="lbl_shipping_shipping_conf_email" language="<c:out param='${language}'/>"/>
							</label>
						</div>
					</div>
				</div> --%>
				<!-- DO NOT DELETE .... closed -->
			</c:if>

			<dsp:droplet name="CheckoutProductDetailDroplet">
				<dsp:param name="productId" param="cisi.commerceItem.auxiliaryData.productId" />
				<dsp:param name="skuId" param="cisi.commerceItem.catalogRefId" />
				<dsp:param name="siteId" param="cisi.commerceItem.auxiliaryData.siteId"/>
				<dsp:param name="commerceItemId" param="cisi.commerceItem.id"/>
				<dsp:param name="order" bean="ShoppingCart.current" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="oldShippingId" param="cisi.shippingGroupName"/>
					<dsp:getvalueof var="commaSepNonShipableStates" param="pSKUDetailVO.commaSepNonShipableStates"/>
					<ul class="small-block-grid-1 medium-block-grid-3 order-item changeStoreItemWrap addNewAddressItemWrap">
						<li>
							<dsp:getvalueof var="commItemId2" param="cisi.commerceItem.id"/>
							<dsp:getvalueof var="commerceQty" param="cisi.commerceItem.quantity"/>
							<dsp:getvalueof var="relationqty" param="cisi.quantity"/>
							<dsp:include page="multi_shipping_item_details.jsp">
								<dsp:param name="commItem" param="cisi.commerceItem"/>
								<dsp:param name="productVO" param="productVO"/>
								<dsp:param name="SKUDetailVO" param="pSKUDetailVO"/>
								<dsp:param name="cisiIndex" value="${index}" />
								<dsp:param name="cisiShipGroupName" value="${oldShippingId}" />
								<dsp:param name="quantity" param="cisi.quantity" />
							</dsp:include>
						</li>
						<li>
							<%-- Ship To Address Selection --%>
							<div class="small-12 columns">
								<dsp:getvalueof var="storeId" param="cisi.commerceItem.storeId" />
								<c:choose>
									<c:when test="${empty storeId}">
										<c:set var="containsShipOnline" value="true" scope="request"/>
										<dsp:getvalueof var="ltlItemFlag" param="pSKUDetailVO.ltlItem"/>
										<dsp:getvalueof var="vdcSku" param="pSKUDetailVO.vdcSku" scope="request"/>
										<dsp:include page="multi_shipping_address_selection.jsp">
											<dsp:param name="cisiIndex" value="${index}" />
											<dsp:param name="ltlItemFlag" value="${ltlItemFlag}" />
										</dsp:include>
									</c:when>
									<c:otherwise>
										<dsp:input type="hidden" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingMethod" />
										<dsp:include page="multi_shipping_store_details.jsp">
											<dsp:param name="storeId" value="${storeId}" />
											<dsp:param name="commItem" param="cisi.commerceItem"/>
											<dsp:param name="cisiIndex" value="${index}" />
											<dsp:param name="sku" param="pSKUDetailVO.skuId"/>
											<dsp:param name="productId" param="cisi.commerceItem.auxiliaryData.productId" />
											<dsp:param name="registryId" value="${registryId}" />
											<dsp:param name="quantity" param="cisi.quantity" />
											<dsp:param name="shippingMethod" param="cisi.shippingMethod" />
											<dsp:param name="shippingGroupName" param="cisi.shippingGroupName"/>
											<dsp:param name="siteId" param="cisi.commerceItem.auxiliaryData.siteId"/>
											
										</dsp:include>
									</c:otherwise>
								</c:choose>
								
								<dsp:getvalueof var="freeShipMethods" param="pSKUDetailVO.freeShipMethods" />
								<c:if test="${not empty freeShipMethods}">
									<dsp:droplet name="ForEach">
										<dsp:param name="array" value="${freeShipMethods}" />
										<dsp:oparam name="outputStart">
											<div class="highlightRed"><bbbl:label key="lbl_cartdetail_free" language="<c:out  param='${language}'/>"/>
										</dsp:oparam>
										<dsp:oparam name="output">
											<dsp:getvalueof var="freeShipMethodIndex" param="index" />
											<dsp:getvalueof var="count" param="count" />
											<dsp:getvalueof var="size" param="size" />
											<bbbl:label key="lbl_free_shipping_${freeShipMethods[freeShipMethodIndex].shipMethodDescription}" language="<c:out  param='${language}'/>"/>
											<c:if test="${count lt size}" >,</c:if>
										</dsp:oparam>
										<dsp:oparam name="outputEnd">
											<bbbl:label key="lbl_cartdetail_shipping" language="<c:out  param='${language}'/>"/></div>
										</dsp:oparam>
									</dsp:droplet>
								</c:if>
								<dsp:getvalueof var="quantity" param="cisi.quantity" />
								<c:if test="${quantity > 1}">
									<a href="#" class="lnkShipToMultiple" data-submitButtonID="shipToMultiplePeople" data-hiddenFieldNameValuePairs="{shipToMultiplePeople_cisiIndex:'${index}',cisiShipGroupName:'${oldShippingId}', shipToMultiplePeople_shippingGr:'true'}">
										<bbbl:label key="lbl_shipping_ship_to_multi_ppl" language="<c:out param='${language}'/>"/>
									</a><br/>
								</c:if>
								<c:if test="${ltlItemFlag eq 'true'}">
									<c:choose>
		                                <c:when test="${stockAvailability eq 'false'}">
											<c:set var="isStockAvailability" value="no" scope="page"/>
		                                	<div class="small-12"><bbbl:label key="lbl_cartdetail_outofstock" language="<c:out param='${language}'/>"/></div>
		                                </c:when>
		                                <c:otherwise>
		                                	<div class="small-12"><bbbl:label key="lbl_cartdetail_instockandreadytouse." language="<c:out param='${language}'/>"/></div>
		                                </c:otherwise>
	                            	</c:choose>
								</c:if>
								<c:if test="${not empty commaSepNonShipableStates}">
								<c:set var="shippingRestrictionsTxt"><bbbl:label key="lbl_shipping_restrictions_applied" language="<c:out param='${language}'/>"/></c:set>
									<a data-reveal-id="openModal" class="shippingRestrictionsApplied" href="#" title="${shippingRestrictionsTxt}">
									<bbbl:label key="lbl_shipping_restrictions_applied" language="<c:out param='${language}'/>"/>
									</a>
                                    <div id="openModal" class="reveal-modal tiny" data-reveal>
                                        <h3><bbbl:label key="lbl_shipping_restrictions_applied" language="<c:out param='${language}'/>" /></h3>
                                        <span><bbbl:label key="lbl_cartdetail_cantshipto" language="<c:out param='${language}'/>"/>
                                        ${commaSepNonShipableStates}</span>
                                        <a class="close-reveal-modal">&#215;</a>
                                     </div>
								</c:if>
							</div> 
						</li>
						<li>
							<%-- shipping method --%>
							<div class="small-12 columns">
								<dsp:getvalueof var="bopusAllowed" param="pSKUDetailVO.bopusAllowed"/>
								<h3 class="checkout-title no-margin-top">Shipping Method</h3>
								<c:if test="${empty storeId}"> 
								<c:choose>
									<c:when test="${ltlItemFlag eq 'true'}">
										<c:if test="${currentSiteId ne BedBathCanadaSite}">
											<div class="padBottom_10">
												<span class="bold"><bbbl:label key="ltl_truck_delivery_options" language="<c:out param='${language}'/>"/></span>
												<a href="${contextPath}/static/ltlShippingInfo" class="popupShipping">
											 	<img  width="11" height="11" src="/_assets/global/images/icons/icon_info.png" alt="shipping info"/></a>
										 	</div>
									    </c:if>
										<dsp:include page="multi_ltl_shipping_method_selection.jsp">
											<dsp:param name="sku" param="pSKUDetailVO.skuId"/>
											<dsp:param name="shippingMethod" param="cisi.shippingMethod" />	
											<dsp:param name="cisiIndex" value="${index}" />
											<dsp:param name="skuAssemblyOffered" param="pSKUDetailVO.assemblyOffered"/>
											<dsp:param name="commItem" param="cisi.commerceItem"/>
											<dsp:param name="siteId" param="cisi.commerceItem.auxiliaryData.siteId"/>	
											<dsp:param name="shippingGroupName" param="cisi.shippingGroupName"/>										
										</dsp:include>
										<c:if test="${currentSiteId ne BedBathCanadaSite}">
											<div class="ltlVendorMessage">
												<bbbl:label key="ltl_vendor_delivery_message" language="<c:out param='${language}'/>"/>
											</div>
										</c:if> 
									</c:when>
									<c:otherwise>
										<dsp:include page="multi_shipping_method_selection.jsp">
											<dsp:param name="sku" param="pSKUDetailVO.skuId"/>
											<dsp:param name="shippingMethod" param="cisi.shippingMethod" />
											<dsp:param name="shippingGroupName" param="cisi.shippingGroupName"/>
											<dsp:param name="cisiIndex" value="${index}" />
											<dsp:param name="commItem" param="cisi.commerceItem"/>
										</dsp:include>
									</c:otherwise>
								</c:choose>		
								</c:if>	
							<c:if test="${not empty storeId}">
								<c:set var="isSkuActive">${true}</c:set>
								<c:if test="${empty registryId}">
									<c:set var="isSkuActive"><dsp:valueof param="pSKUDetailVO.activeFlag"></dsp:valueof></c:set>
								</c:if>
								<dsp:getvalueof var="skuBelowLineItem" param="pSKUDetailVO.skuBelowLine"/>
								<c:set var="isSkuInStock"><dsp:valueof param="pSKUDetailVO.skuInStock"></dsp:valueof>	</c:set>
								<c:set var="isBelowLineItem" value="${false}"/>
							   	<c:if test="${skuBelowLineItem and not empty registryId}">
								 	<c:set var="isBelowLineItem" value="${true}"/>
							    </c:if>
								<c:if test="${isSkuInStock eq 'true' and isSkuActive eq 'true' && isBelowLineItem != 'true'}">
									<a href="javascript:void(0);" class="upperCase" data-commerceid="${commItemId2}" data-qty="${commerceQty}" data-shipid="${oldShippingId}" onclick="changeToShipOnline('${commItemId2}','${oldShippingId}','${relationqty}');">
											<strong><bbbl:label key="lbl_cartdetail_shipthisitem" language="<c:out param='${language}'/>"/></strong>
									</a>	
									<dsp:input id="commItemId_${commItemId}" type="hidden" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingMethod"
								   				value="storeShippingGroup" />
								</c:if>
							</c:if>
							</div>
                            <div class="small-12 columns">
                                * <bbbl:label key="lbl_shipping_option_disclaimer" language="${pageContext.request.locale.language}" /> 
                                <%-- <bbbl:label key="lbl_cartdetail_viewshippingcostslink" language="${pageContext.request.locale.language}" /></div> --%>
                            </div>
						</li>

						<%-- These hidden inputs are used for change store and Pickup in Store  --%>
						<input type="hidden" name="productId" class="productId" value="${productId}" data-change-store-submit="prodId" />
						<input type="hidden" name="skuId" value="${skuId}" class="changeStoreSkuId" data-change-store-submit="skuId"/>
						<input type="hidden" name="commerceItemId" value="${commItemId}" data-change-store-submit="commerceItemId" />
						<input type="hidden" name="oldShippingId" value="${oldShippingId}" data-change-store-submit="oldShippingId" />
						<input type="hidden" name="newQuantity" value="${newQuantity}" data-change-store-submit="newQuantity" />
						<input type="hidden" name="itemQuantity" class="itemQuantity" value="${itemQuantity}" data-change-store-submit="itemQuantity"/>
						<input type="hidden" name="pageURL" value="${contextPath}/checkout/shipping/shipping.jsp" data-change-store-submit="pageURL"/>
					</ul>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>

	<c:if test="${isStockAvailability eq 'no'}">
		<dsp:setvalue bean="BBBShippingGroupFormhandler.multiShipOutOfStock" value="yes"/>
	</c:if>
	<div id="nearbyStore" class="reveal-modal" data-reveal></div>
</dsp:page>
