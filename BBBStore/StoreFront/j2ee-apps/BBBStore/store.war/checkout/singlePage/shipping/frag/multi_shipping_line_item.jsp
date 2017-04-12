<dsp:page>
<dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupDroplet"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupContainerService"/>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/order/droplet/CommerceItemCheckDroplet"/>
 <dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
 <dsp:importbean bean="/atg/commerce/ShoppingCart" />

<dsp:getvalueof var="clearContainer" bean="BBBShippingGroupFormhandler.clearContainer"/>
<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
<c:set var="isStockAvailability" value="yes" scope="page"/>
<c:set var="BedBathUSSite">
		<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
</c:set>
<c:set var="BuyBuyBabySite">
	<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
</c:set>
<c:set var="BedBathCanadaSite">
	<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
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

				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param name="array" value="${cisiObjs}" />
			  		<dsp:oparam name="output">		    		
						<dsp:param name="cisi" param="element" />
						<dsp:getvalueof id="cisi" param="element"></dsp:getvalueof>
								<dsp:getvalueof var="index" param="index" />
								<dsp:getvalueof var="registryId" param="cisi.commerceItem.registryId"/>	
								
								<c:choose>
									<c:when test="${registryId ne null}">
										<li class="gritItemWithHeader last changeStoreItemWrap addNewAddressItemWrap">
											<div class="shippingItemHeader clearfix">
												<div class="fl marRight_20 marTop_5">
													<dsp:getvalueof bean="ShoppingCart.current.registryMap.${registryId}" var="registratantVO"/>
															<span><bbbl:label key="lbl_spc_cart_registry_from_text" language="<c:out param='${language}'/>"/></span>
															<span>${registratantVO.primaryRegistrantFirstName}<c:if test="${not empty  registratantVO.coRegistrantFirstName}">&nbsp;&amp;&nbsp;${registratantVO.coRegistrantFirstName}</c:if><bbbl:label key="lbl_spc_cart_registry_name_suffix" language="<c:out param='${language}'/>"/>
															</span>
													<dsp:droplet name="/atg/commerce/order/droplet/BBBRegistryDetailsDroplet">
														<dsp:param name="registryId" value="${registryId}" />
														<dsp:param name="order" bean="ShoppingCart.current" />
														<dsp:oparam name="regOutput">
															<dsp:getvalueof var="registrantEmail" param="registrantEmail"/>
															
															<span><dsp:valueof param="registryType"/></span>
															<span><bbbl:label key="lbl_spc_cart_registry_text" language="<c:out param='${language}'/>"/></span>													
														</dsp:oparam>
													</dsp:droplet>
													
												</div>
												
												<%-- splitShippingGroupName property of CommerceItemShippingInfo is used for sendShippingConfEmail --%>
												
												<%-- DO NOT DELETE ...... the shipping confirmation is commented since DS is not ready to send confirmation emails --%>
												
												<%-- <div class="fl marLeft_20">
													<div class="checkboxItem input clearfix noPad noBorder">
														<div class="checkbox noMar">
															<dsp:input type="checkbox" name="sendShippingConfEmail_${index}" id="sendShippingConfEmail_${index}" 
																bean="BBBShippingGroupFormhandler.cisiItems[${index}].splitShippingGroupName" 
																value="${registratantVO.registrantEmail}" />
														</div>
														<div class="label">
															<label for="sendShippingConfEmail_${index}">
																<bbbl:label key="lbl_spc_shipping_shipping_conf_email" language="<c:out param='${language}'/>"/>
															</label>															
														</div>
													</div>
												</div> --%>
												<%-- DO NOT DELETE .... closed --%>
                                               
											</div>
									</c:when>
									<c:otherwise>
										<li class="changeStoreItemWrap addNewAddressItemWrap">
									</c:otherwise>
								</c:choose>
								
								<dsp:droplet name="/com/bbb/commerce/checkout/droplet/CheckoutProductDetailDroplet">
									<dsp:param name="productId" param="cisi.commerceItem.auxiliaryData.productId" />
									<dsp:param name="skuId" param="cisi.commerceItem.catalogRefId" />
									<dsp:param name="siteId" param="cisi.commerceItem.auxiliaryData.siteId"/>
									<dsp:param name="commerceItemId" param="cisi.commerceItem.id"/>
									<dsp:param name="order" bean="ShoppingCart.current" />
									<dsp:getvalueof var="oldShippingId" param="cisi.shippingGroupName"/>
									
									<dsp:oparam name="output">							
										<ul class="clearfix noMarTop">	
											<dsp:getvalueof var="commItemId2" param="cisi.commerceItem.id"/>
											<dsp:include page="multi_shipping_item_details.jsp">
												<dsp:param name="commItem" param="cisi.commerceItem"/>
												<dsp:param name="productVO" param="productVO"/>
												<dsp:param name="SKUDetailVO" param="pSKUDetailVO"/>
												<dsp:param name="cisiIndex" value="${index}" />
												<dsp:param name="cisiShipGroupName" value="${oldShippingId}" />
												<dsp:param name="quantity" param="cisi.quantity" />
											</dsp:include>
											<li class="grid_3">
											<ul class="noMarTop">
											
											<%--<dsp:input id="storeId_${commItemId2}" name="newStoreId" type="hidden" bean="BBBShippingGroupFormhandler.cisiItems[${index}].commerceItem.storeId" />--%>
												<dsp:getvalueof var="storeId" param="cisi.commerceItem.storeId" />
												<c:if test="${empty storeId}">
													<c:set var="containsShipOnline" value="true" scope="request"/> 
													<dsp:getvalueof var="ltlItemFlag" param="pSKUDetailVO.ltlItem"/>
													<dsp:include page="multi_shipping_address_selection.jsp">
														<dsp:param name="cisiIndex" value="${index}" />											
														<dsp:param name="ltlItemFlag" value="${ltlItemFlag}" />										
													</dsp:include>
												</c:if>
                                                <c:if test="${not empty storeId}">
                                                    <dsp:input type="hidden" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingMethod" />
													<dsp:include page="multi_shipping_store_details.jsp">
														<dsp:param name="storeId" value="${storeId}" />	
														<dsp:param name="cisiIndex" value="${index}" />										
													</dsp:include>											
												</c:if>
											</ul>
											<dsp:getvalueof var="freeShipMethods" param="pSKUDetailVO.freeShipMethods"/>
											<dsp:getvalueof var="VDCSku" param="pSKUDetailVO.VDCSku"/>
											<dsp:getvalueof var="stockAvailability" param="pSKUDetailVO.skuInStock"/>
											<dsp:getvalueof var="vdcSKUShipMesage" param="pSKUDetailVO.vdcSKUShipMesage"/>
											<dsp:getvalueof var="commaSepNonShipableStates" param="pSKUDetailVO.commaSepNonShipableStates"/>
											<dsp:getvalueof var="productId" param="cisi.commerceItem.catalogRefId"/>
											<dsp:getvalueof var="skuId" param="cisi.commerceItem.catalogRefId"/>
											<dsp:getvalueof var="commItemId" param="cisi.commerceItem.id"/>
											<dsp:getvalueof var="oldShippingId" param="cisi.shippingGroupName"/>
											<dsp:getvalueof var="newQuantity" param="cisi.quantity"/>
											<dsp:getvalueof var="itemQuantity" param="cisi.quantity"/>
												<c:if test="${empty storeId}">
											
											
												<ul class="squareBulattedList smallText">	
													<c:if test="${not empty freeShipMethods}">										
														<dsp:droplet name="ForEach">
																<dsp:param name="array" value="${freeShipMethods}" />				
																<dsp:oparam name="outputStart">	
																	<li class="highlight">
																		<bbbl:label key="lbl_spc_cartdetail_free" language="<c:out	param='${language}'/>"/>																								
																</dsp:oparam>
																<dsp:oparam name="output">																	
																		<dsp:getvalueof var="indexPos" param="index" />																		
																		<dsp:getvalueof var="size" param="size" />
																		<bbbl:label key="lbl_spc_free_shipping_${freeShipMethods[indexPos].shipMethodDescription}" language="<c:out  param='${language}'/>"/>	
																		<c:if test="${indexPos lt size-1}" >
																				,
													 					</c:if>																			 
																	
																</dsp:oparam>
																<dsp:oparam name="outputEnd">	
																		<bbbl:label key="lbl_spc_cartdetail_shipping" language="<c:out	param='${language}'/>"/>																								
																	</li>
																</dsp:oparam>
														</dsp:droplet>														
													</c:if>
													<c:choose>
                                                        <c:when test="${stockAvailability eq 'false'}">
															<c:set var="isStockAvailability" value="no" scope="page"/>
                                                            <li><bbbl:label key="lbl_spc_cartdetail_outofstock" language="<c:out param='${language}'/>"/></li>
                                                        </c:when>
                                                        <c:otherwise>
                                                             <li><bbbl:label key="lbl_spc_cartdetail_instockandreadytouse." language="<c:out param='${language}'/>"/></li>
                                                        </c:otherwise>
                                                    </c:choose>
													<c:if test="${VDCSku and not ltlItemFlag}">
														<li>${vdcSKUShipMesage}<bbbl:label key="lbl_spc_checkout_period" language="<c:out	param='${language}'/>"/> 
														<bbbl:label key="lbl_spc_vdc_learn_more" language="<c:out	param='${language}'/>"/></li>
													</c:if>
													<c:if test="${not empty commaSepNonShipableStates}">
														<li>
														<a class="shippingRestrictionsApplied smallText" href="/store/cart/static/shipping_restrictions_applied.jsp" data-nonShipping="${commaSepNonShipableStates}" data-cantShipto="<bbbl:label key="lbl_spc_cartdetail_cantshipto" language="<c:out param='${language}'/>"/>" title="<bbbl:label key="lbl_spc_shipping_restrictions_applied" language="<c:out param='${language}'/>"/>">
														<bbbl:label key="lbl_spc_shipping_restrictions_applied" language="<c:out param='${language}'/>"/>
														</a>
														</li>
													</c:if>
													<dsp:droplet name="/com/bbb/commerce/order/droplet/EcoFeeApplicabilityCheckDroplet">
												      <dsp:param name="skuId" value="${skuId}" />						      
							                          <dsp:oparam name="true">
														<li><bbbl:label key="lbl_spc_cartdetail_elegibleforecofee" language="<c:out param='${language}'/>"/></li>
							                          </dsp:oparam>
						                          </dsp:droplet>										
												</ul>
											</c:if>
											</li>									
											<li class="grid_3 omega">
											 <dsp:getvalueof var="bopusAllowed" param="pSKUDetailVO.bopusAllowed"/>
												<c:if test="${empty storeId}"> 
												<dsp:getvalueof var="ltlItem" param="pSKUDetailVO.ltlItem"/>
												<c:choose>
													<c:when test="${ltlItem eq 'true'}">
														<c:if test="${currentSiteId ne BedBathCanadaSite}">
															<jsp:useBean id="placeHolderMapContext" class="java.util.HashMap" scope="request"/>
															<c:set target="${placeHolderMapContext}" property="contextPath" value="${contextPath}"/>
															<div class="padBottom_10">
																<span class="bold"><bbbl:label key="ltl_truck_delivery_options" language="<c:out param='${language}'/>"/></span>
																<a href="<bbbt:textArea key="txt_spc_static_deliveryinfo" placeHolderMap="${placeHolderMapContext}" language ="${pageContext.request.locale.language}"/>" class="popupShipping">
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
														<dsp:param name="cisiIndex" value="${index}" />														
													</dsp:include>
													</c:otherwise>
												</c:choose>													
													<c:if test="${MapQuestOn && not bopusAllowed and not ltlItem}">
													<a href="#" class="upperCase changeStore">
														<strong><bbbl:label key="lbl_spc_cartdetail_pickupinstore" language="<c:out param='${language}'/>"/></strong>
														</a>
													</c:if>		
																								
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
														<a href="javascript:void(0);" class="upperCase" onclick="changeToShipOnline('${commItemId}','${oldShippingId}','${newQuantity}');">
																<strong><bbbl:label key="lbl_spc_cartdetail_shipthisitem" language="<c:out param='${language}'/>"/></strong>
														</a>	
														<dsp:input id="commItemId_${commItemId}" type="hidden" bean="BBBShippingGroupFormhandler.cisiItems[${index}].shippingMethod"
													   				value="storeShippingGroup" />
													</c:if>
												</c:if>
											</li>
												</ul><%-- These hidden inputs are used for change store and Pickup in Store  --%>
											<input type="hidden" name="productId" class="productId" value="${productId}" data-change-store-submit="prodId" />
											<input type="hidden" name="skuId" value="${skuId}" class="changeStoreSkuId" data-change-store-submit="skuId"/>
											<input type="hidden" name="commerceItemId" value="${commItemId}" data-change-store-submit="commerceItemId" />
											<input type="hidden" name="oldShippingId" value="${oldShippingId}" data-change-store-submit="oldShippingId" />
											<input type="hidden" name="newQuantity" value="${newQuantity}" data-change-store-submit="newQuantity" />
											<input type="hidden" name="itemQuantity" class="itemQuantity" value="${itemQuantity}" data-change-store-submit="itemQuantity"/>
											<input type="hidden" name="pageURL" value="${contextPath}/checkout/shipping/shipping.jsp" data-change-store-submit="pageURL"/>
									
						    </li>
							
							</dsp:oparam>
						</dsp:droplet>												
					</dsp:oparam>
				</dsp:droplet>		
		
		<c:if test="${isStockAvailability eq 'no'}"> 
			<dsp:setvalue bean="BBBShippingGroupFormhandler.multiShipOutOfStock" value="yes"/> 
		</c:if>

</dsp:page>