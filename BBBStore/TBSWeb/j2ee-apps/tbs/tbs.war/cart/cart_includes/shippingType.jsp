<%-- TODO: need to get all the symbols and messages possible in this file --%>

<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/IsProductSKUShippingDroplet"/>
	<dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/EcoFeeApplicabilityCheckDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/com/bbb/selfservice/SearchStoreDroplet"/>
	
	<dsp:getvalueof var="shiptime" param="shipTime"/>
	<%-- Page Variables --%>
	<dsp:getvalueof id="applicationId" bean="Site.id" />
	<dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
	<c:set var="internationalShippingOn" scope="request"><bbbc:config key="international_shipping_switch" configName="International_Shipping"/></c:set>
	
	<c:set var="poBoxEnabled" scope="request"><bbbc:config key="shipTo_POBoxOn" configName="FlagDrivenFunctions"/></c:set>
	
	<c:set var="deliveryDetailsUL">

		<dsp:getvalueof var="commItem" param="commItem"></dsp:getvalueof>
		<c:if test="${empty commItem.BBBCommerceItem.storeId}">
			<c:if test="${not empty commItem.skuDetailVO.freeShipMethods}">
				<dsp:droplet name="ForEach">
					<dsp:param name="array" value="${commItem.skuDetailVO.freeShipMethods}" />
					<dsp:oparam name="outputStart">
						<li><bbbl:label key="lbl_cartdetail_free" language="<c:out  param='${language}'/>"/>
					</dsp:oparam>
					<dsp:oparam name="output">
						<dsp:getvalueof var="index" param="index" />
						<dsp:getvalueof var="count" param="count" />
						<dsp:getvalueof var="size" param="size" />
						<bbbl:label key="lbl_free_shipping_${commItem.skuDetailVO.freeShipMethods[index].shipMethodDescription}" language="<c:out  param='${language}'/>"/>
						<c:if test="${count lt size}" >,</c:if>
					</dsp:oparam>
					<dsp:oparam name="outputEnd">
						<bbbl:label key="lbl_cartdetail_shipping" language="<c:out  param='${language}'/>"/></li>
					</dsp:oparam>
				</dsp:droplet>
			</c:if>

			<c:if test="${commItem.skuDetailVO.shippingSurcharge gt 0.0}">
				<li><bbbl:label key="lbl_cartdetail_surchargeapplies" language="<c:out param='${language}'/>"/></li>
			</c:if>

			<c:choose>
				<c:when test="${commItem.stockAvailability eq 0}">
					<li><bbbl:label key="lbl_cartdetail_instockandreadytouse." language="<c:out param='${language}'/>"/></li>
				</c:when>
				<c:otherwise>
					<c:set var="isStockAvailability" value="no" scope="request"/>
					<li><bbbl:label key="lbl_cartdetail_outofstock" language="<c:out param='${language}'/>"/></li>
				</c:otherwise>
			</c:choose>

			<dsp:droplet name="IsProductSKUShippingDroplet">
				<dsp:param name="siteId" value="${applicationId}"/>
				<dsp:param name="skuId" value="${commItem.BBBCommerceItem.catalogRefId}"/>
				<dsp:param name="prodId" param="commerceItem.BBBCommerceItem.repositoryItem.productId"/>
				<dsp:oparam name="true">
					<dsp:getvalueof var="restrictedAttributes" param="restrictedAttributes"/>
					<c:forEach var="item" items="${restrictedAttributes}">
						<li>
							<c:choose>
								<c:when test="${null ne item.actionURL}">
									<a href="${item.actionURL}" class="popup"><span>${item.attributeDescrip}</span></a>
								</c:when>
								<c:otherwise>
									${item.attributeDescrip}
								</c:otherwise>
							</c:choose>
						</li>
					</c:forEach>
				</dsp:oparam>
				<dsp:oparam name="false"></dsp:oparam>
			</dsp:droplet>

			<c:if test="${currentSiteId == TBS_BedBathCanadaSite}">
				<dsp:droplet name="EcoFeeApplicabilityCheckDroplet">
					<dsp:param name="skuId" value="${commItem.BBBCommerceItem.catalogRefId}" />
					<dsp:oparam name="true">
						<li><bbbl:label key="lbl_cartdetail_elegibleforecofee" language="<c:out param='${language}'/>"/></li>
					</dsp:oparam>
				</dsp:droplet>
			</c:if>

			<c:if test="${commItem.skuDetailVO.vdcSku}">
			
					<dsp:droplet name="/com/bbb/commerce/browse/VDCShippingMessagingDroplet">
						<dsp:param name="skuId" value="${commItem.BBBCommerceItem.catalogRefId}"/>
						<dsp:param name="siteId" value="${currentSiteId}"/>
						<dsp:oparam name="output">
							<dsp:getvalueof var="vdcDelTime" param="vdcShipMsg"/>
							<dsp:getvalueof var="offsetDateVDC" param="offsetDateVDC"/>
							 <c:set var="vdcOffsetFlag">
								<bbbc:config key="vdcOffsetFlag" configName="FlagDrivenFunctions" />
							</c:set>
						</dsp:oparam>
					</dsp:droplet>
					<jsp:useBean id="placeHolderMapVdcLearnMore" class="java.util.HashMap" scope="request" />
					<c:set target="${placeHolderMapVdcLearnMore}" property="shipMethod" value="" />
					<c:set target="${placeHolderMapVdcLearnMore}" property="skuId" value="${commItem.BBBCommerceItem.catalogRefId}" />
					<c:set target="${placeHolderMapVdcLearnMore}" property="vdcDelTime" value="${vdcDelTime}" />
					<jsp:useBean id="placeHolderMapVdcCarts" class="java.util.HashMap" scope="request" />
					<c:set target="${placeHolderMapVdcCarts}" property="vdcDelTime" value="${vdcDelTime}" />
					<c:if test="${vdcOffsetFlag && not empty offsetDateVDC}">
						<c:if test="${ !commItem.skuDetailVO.ltlItem}">
						<jsp:useBean id="placeHolderMapServiceLevel" class="java.util.HashMap" scope="request" />
						<c:set target="${placeHolderMapServiceLevel}" property="actualOffSetDate" value="${offsetDateVDC}" />
							<li>
								<span class="highlightRed vdcOffsetMsg bold">
								<bbbt:textArea key="txt_vdc_offset_msg" placeHolderMap="${placeHolderMapServiceLevel}" language="${pageContext.request.locale.language}" />
								<c:set var="lbl_offset_learn_more_link" scope="request"><bbbl:label key="lbl_offset_learn_more" placeHolderMap="${placeHolderMapVdcLearnMore}" language="<c:out   param='${language}'/>"/></c:set>
								<c:set var="lbl_offset_learn_moreTBS" value="${fn:replace(lbl_offset_learn_more_link, '/store/includes','/tbs/includes')}" />
								<dsp:valueof value="${lbl_offset_learn_moreTBS}" valueishtml="true"/>
								</span>
							</li>
						</c:if>
					</c:if>
					<li>
						<bbbt:label key="lbl_vdc_del_time_cart_msg_tbs" placeHolderMap="${placeHolderMapVdcLearnMore}"	language="${pageContext.request.locale.language}" />
						<c:set var="learn_more_link" scope="request"><bbbl:label key="lbl_vdc_learn_more" placeHolderMap="${placeHolderMapVdcLearnMore}" language="<c:out   param='${language}'/>"/></c:set>
						<c:set var="learn_more_linkTBS" value="${fn:replace(learn_more_link, '/store/includes','/tbs/includes')}" />
						<dsp:valueof value="${learn_more_linkTBS}" valueishtml="true"/>
					</li>
				
			
			
			</c:if>

			<c:if test="${commItem.BBBCommerceItem.bts}">
				<li><bbbl:label key="lbl_cartdetail_elegibleforpackandhold" language="<c:out param='${language}'/>"/></li>
			</c:if>

		</c:if>
	</c:set>

	<c:choose>
		<c:when test="${defaultCountry eq 'US' || defaultCountry eq 'Canada'}">
			<div class="deliveryBy">
				<c:if test="${commItem.BBBCommerceItem.commerceItemClassType eq 'tbsCommerceItem'}">
            		<dsp:getvalueof var="cmo" value="${commItem.BBBCommerceItem.CMO}"/>
                </c:if>
				<dsp:droplet name="IsEmpty">
					<dsp:param name="value" value="${commItem.BBBCommerceItem.storeId}"/>
					<dsp:oparam name="true">
						<c:set var="commItemStoreId" value="" />
						<c:if test="${MapQuestOn}">
							<c:if test="${not commItem.skuDetailVO.ltlItem && not commItem.skuDetailVO.vdcSku}">
								<c:choose>
									<c:when test="${(not commItem.skuDetailVO.bopusAllowed) and (not commItem.skuDetailVO.bopusExcludedForMinimalSku)}">
										<label class="inline-rc radio" id="lblshippingMethods1${commItem.BBBCommerceItem.id}" for="shippingMethods1${commItem.BBBCommerceItem.id}">
											<input type="radio" value="1" name="shippingMethods${commItem.BBBCommerceItem.id}" id="shippingMethods1${commItem.BBBCommerceItem.id}" checked='<c:if test="${(shiptime eq '0004') or (cmo)}">checked</c:if>' aria-checked="false" aria-labelledby="lblshippingMethods1${commItem.BBBCommerceItem.id}" />
											<span></span>
											Pick Up In Store
											<c:if test="${!cmo}">
													<br/><dsp:a iclass="nearbyStores nearby-stores pdp-sprite in-store" href="/tbs/selfservice/find_tbs_store.jsp">
													<dsp:param name="id" value="${commItem.BBBCommerceItem.auxiliaryData.productId}" />
													<dsp:param name="siteId" value="${currentSiteId}" />
													<dsp:param name="registryId" param="registryId" />
													<dsp:param name="skuid" value="${commItem.BBBCommerceItem.catalogRefId}"/>
													<dsp:param name="itemQuantity" value="${commItem.BBBCommerceItem.quantity}"/>
													<dsp:param name="commerceId" value="${commItem.BBBCommerceItem.id}"/>
													Nearby Stores
												</dsp:a>
											</c:if>
										</label>
										<c:if test="${internationalShippingOn}">
											<c:if test="${commItem.skuDetailVO.restrictedForBopusAllowed}">
												<div class="error">
													<bbbl:error key="ERR_INT_SHIP_SKU_RESTRICT_BOPUS_ITEM" language ="${pageContext.request.locale.language}"/>
												</div>
											</c:if>
										</c:if>
									</c:when>
									<c:otherwise>
											<c:choose>
												<c:when test="${shiptime eq '0004'}">
													<label class="inline-rc radio" id="lblshippingMethods1${commItem.BBBCommerceItem.id}" for="shippingMethods1${commItem.BBBCommerceItem.id}">
														<input type="radio" value="1" name="shippingMethods${commItem.BBBCommerceItem.id}" id="shippingMethods1${commItem.BBBCommerceItem.id}" checked="checked" aria-checked="false" aria-labelledby="lblshippingMethods1${commItem.BBBCommerceItem.id}" />
														<span></span>
														Pick Up In Store
														<c:if test="${!cmo}">
															<br/><dsp:a iclass="nearbyStores nearby-stores pdp-sprite in-store" href="/tbs/selfservice/find_tbs_store.jsp">
																<dsp:param name="id" value="${commItem.BBBCommerceItem.auxiliaryData.productId}" />
																<dsp:param name="siteId" value="${currentSiteId}" />
																<dsp:param name="registryId" param="registryId" />
																<dsp:param name="skuid" value="${commItem.BBBCommerceItem.catalogRefId}"/>
																<dsp:param name="itemQuantity" value="${commItem.BBBCommerceItem.quantity}"/>
																Nearby Stores
															</dsp:a>
														</c:if>
													</label>
													<c:if test="${internationalShippingOn}">
														<c:if test="${commItem.skuDetailVO.restrictedForBopusAllowed}">
															<div class="error">
																<bbbl:error key="ERR_INT_SHIP_SKU_RESTRICT_BOPUS_ITEM" language ="${pageContext.request.locale.language}"/>
															</div>
														</c:if>
													</c:if>
												</c:when>
												<c:otherwise>
													<label class="inline-rc radio disabled" id="lblshippingMethods1${commItem.BBBCommerceItem.id}" for="shippingMethods1${commItem.BBBCommerceItem.id}">
														<input type="radio" value="1" name="shippingMethods${commItem.BBBCommerceItem.id}" id="shippingMethods1${commItem.BBBCommerceItem.id}" disabled="disabled" aria-checked="false" aria-labelledby="lblshippingMethods1${commItem.BBBCommerceItem.id}" />
														<span></span>
														<strong><bbbl:label key="lbl_cartdetail_pickupinstore" language="<c:out param='${language}'/>"/></strong>
														<bbbl:label key="lbl_check_store_avail" language="<c:out param='${language}'/>"/>
														<%--<a href="#" class="pdp-sprite nearby-stores">Nearby Stores</a><br>--%> <%-- add .no-stock for no stores --%>
													</label>
												</c:otherwise>
											</c:choose>
									</c:otherwise>
								</c:choose>
							</c:if>
						</c:if>
						<dsp:droplet name="SKULookup">
							<dsp:param name="id" param="commItem.BBBCommerceItem.catalogRefId"/>
							<dsp:param name="elementName" value="skuItem"/>
							<dsp:oparam name="output">
								<dsp:getvalueof param="skuItem.giftCert" var="giftCert"/>
							</dsp:oparam>
						</dsp:droplet>
							 <c:choose>
								<c:when test="${shiptime eq '0004' and not commItem.skuDetailVO.ltlItem and not giftCert and not commItem.skuDetailVO.vdcSku or cmo}">
                                    <label class="inline-rc radio disabled" id="lblshippingMethods2${commItem.BBBCommerceItem.id}" for="shippingMethods2${commItem.BBBCommerceItem.id}">
										<input type="radio" value="2" name="shippingMethods${commItem.BBBCommerceItem.id}" id="shippingMethods2${commItem.BBBCommerceItem.id}" class="curShippingMethod" aria-checked="false" disabled="disabled" aria-labelledby="lblshippingMethods2${commItem.BBBCommerceItem.id}" />
	                                    <span></span>
	                                    <bbbl:label key="lbl_cartdetail_shipthisitem" language="<c:out param='${language}'/>"/>
	                                    <c:out value="${deliveryDetailsUL}" escapeXml="false" />
                                    </label>
								</c:when>
								 <c:otherwise>
                                    <label class="inline-rc radio" id="lblshippingMethods2${commItem.BBBCommerceItem.id}" for="shippingMethods2${commItem.BBBCommerceItem.id}">
										<input type="radio" value="2" name="shippingMethods${commItem.BBBCommerceItem.id}" id="shippingMethods2${commItem.BBBCommerceItem.id}" checked="checked" class="curShippingMethod" aria-checked="false" aria-labelledby="lblshippingMethods2${commItem.BBBCommerceItem.id}" />
                                    	<span></span>
                                    	<bbbl:label key="lbl_cartdetail_shipthisitem" language="<c:out param='${language}'/>"/>
                                    	<c:out value="${deliveryDetailsUL}" escapeXml="false" />
	                                    <c:if test="${not empty commItem.skuDetailVO.commaSepNonShipableStates}">
											<%-- <li><bbbl:label key="lbl_cartdetail_cantshipto" language="<c:out param='${language}'/>"/> ${commItem.skuDetailVO.commaSepNonShipableStates}</li> --%>
											
											
											<li><a data-reveal-id="openModal_${commItem.BBBCommerceItem.catalogRefId}" class="shippingRestrictionsApplied" href="#">
												<bbbl:label key="lbl_shipping_restrictions_applied" language="<c:out param='${language}'/>"/>
											</a></li>
											
											
											
									        <div id="openModal_${commItem.BBBCommerceItem.catalogRefId}" class="reveal-modal tiny" data-reveal>
									          <h3><bbbl:label key="lbl_shipping_restrictions_applied" language="<c:out param='${language}'/>" /></h3>
									          <div class="bulletsReset"><ul><li>
									          <bbbl:label key="lbl_cartdetail_cantshipto" language="<c:out param='${language}'/>"/>${commItem.skuDetailVO.commaSepNonShipableStates}</span>
									          </div></li></ul>
									          <a class="close-reveal-modal">&#215;</a>
									        </div>
										</c:if>
									</label>
								</c:otherwise> 
							</c:choose> 
						
						<c:if test="${internationalShippingOn}">
							<c:if test="${commItem.skuDetailVO.restrictedForIntShip}">
								<div class="error">
									<bbbl:error key="ERR_INT_SHIP_SKU_RESTRICT_GENERIC_ERROR" language ="${pageContext.request.locale.language}"/>
								</div>
							</c:if>
						</c:if>

					</dsp:oparam>
					<dsp:oparam name="false">

						<c:set var="commItemStoreId" value="${commItem.BBBCommerceItem.storeId}" />

						
							<dsp:droplet name="SearchStoreDroplet">
								<dsp:param name="storeId" value="${commItem.BBBCommerceItem.storeId}" />
								<dsp:oparam name="output">
										<c:choose>
											<c:when test="${commItem.stockAvailability eq 0}">
												<div class="savings"><bbbl:label key="lbl_cartdetail_availableforpickupinstore." language="<c:out param='${language}'/>"/></div>
											</c:when>
											<c:otherwise>
												<c:set var="isStockAvailability" value="no" scope="request"/>
												<div class="savings"><bbbl:label key="lbl_cartdetail_outofstock" language="<c:out param='${language}'/>"/></div>
											</c:otherwise>
										</c:choose>
                                        <label class="inline-rc radio" id="lblshippingMethods1${commItem.BBBCommerceItem.id}" for="shippingMethods1${commItem.BBBCommerceItem.id}">
                                                          <input type="radio" value="1" checked="checked" class="curShippingMethod" name="shippingMethods${commItem.BBBCommerceItem.id}" id="shippingMethods1${commItem.BBBCommerceItem.id}" aria-checked="false" aria-labelledby="lblshippingMethods1${commItem.BBBCommerceItem.id}" />
                                                          <span></span>
                                                          <strong><bbbl:label key="lbl_cartdetail_pickupinstore" language="<c:out param='${language}'/>"/></strong>
										<%-- <a href="#" class="changeStore"><bbbl:label key="lbl_cartdetail_change" language="<c:out param='${language}'/>"/></a> --%>
									    <%--  <dsp:a iclass="nearbyStores nearby-stores in-store" href="/tbs/selfservice/find_tbs_store.jsp">
											<dsp:param name="id" value="${commItem.BBBCommerceItem.auxiliaryData.productId}" />
											<dsp:param name="siteId" value="${currentSiteId}" />
											<dsp:param name="registryId" param="registryId" />
											<dsp:param name="skuid" value="${commItem.BBBCommerceItem.catalogRefId}"/>
											<dsp:param name="itemQuantity" value="${commItem.BBBCommerceItem.quantity}"/>
										 	<bbbl:label key="lbl_cartdetail_change" language="<c:out param='${language}'/>"/>
										</dsp:a> --%>
										<label class="nearbystores" id="lblshippingMethodStorePickup" for="shippingMethod_1${commItem.BBBCommerceItem.id}StorePickup" data-qty="${commItem.BBBCommerceItem.quantity}" data-shipid="${shippingGroupName}" data-commerceid="${commItem.BBBCommerceItem.id}" data-storeid="${commItem.BBBCommerceItem.storeId}" data-skuid="${commItem.BBBCommerceItem.catalogRefId}">
											<dsp:valueof param="StoreDetails.storeName"/>
											<input type="radio" class="hidden" name="shippingMethods_ProdName_1${commItem.BBBCommerceItem.catalogRefId}" id="shippingMethod_${index}${commItem.BBBCommerceItem.catalogRefId}StorePickup" value="storepickup">			
											<strong><a><bbbl:label key="lbl_cartdetail_change" language="<c:out param='${language}'/>"/></a></strong>
										</label>
										<div>
											<dsp:valueof param="StoreDetails.address"/>
											<dsp:valueof param="StoreDetails.city"/>,&nbsp;<dsp:valueof param="StoreDetails.state"/>&nbsp;<dsp:valueof param="StoreDetails.postalCode"/>
											<dsp:valueof param="StoreDetails.storePhone"/>
										</div>
                                        </label>
								</dsp:oparam>
							</dsp:droplet>
						
						<c:if test="${internationalShippingOn}">
							<c:if test="${commItem.skuDetailVO.restrictedForBopusAllowed}">
								<div class="error">
									<bbbl:error key="ERR_INT_SHIP_SKU_RESTRICT_BOPUS_ITEM" language ="${pageContext.request.locale.language}"/>
								</div>
							</c:if>
						</c:if>
						<c:choose>
								<c:when test="${shiptime eq '0004'}">
								<label class="inline-rc radio disabled" id="lblshippingMethods2${commItem.BBBCommerceItem.id}" for="shippingMethods2${commItem.BBBCommerceItem.id}">
									<input type="radio" value="2" name="shippingMethods${commItem.BBBCommerceItem.id}" id="shippingMethods2${commItem.BBBCommerceItem.id}" disabled="true" aria-checked="false" aria-labelledby="lblshippingMethods2${commItem.BBBCommerceItem.id}" />
										<span></span>
										<a class="lnkShippingMethodChange upperCase noline btnAjaxSubmitCart" data-ajax-frmID="frmCartShipOnline" href="#">
											<bbbl:label key="lbl_cartdetail_shipthisitem" language="<c:out param='${language}'/>"/>
										</a>
									</label>
								</c:when>
								<c:otherwise>
									<label class="inline-rc radio" id="lblshippingMethods2${commItem.BBBCommerceItem.id}" for="shippingMethods2${commItem.BBBCommerceItem.id}">
									<input type="radio" value="2" name="shippingMethods${commItem.BBBCommerceItem.id}" id="shippingMethods2${commItem.BBBCommerceItem.id}" aria-checked="false" aria-labelledby="lblshippingMethods2${commItem.BBBCommerceItem.id}" />
										<span></span>
										<a class="lnkShippingMethodChange upperCase noline btnAjaxSubmitCart" data-ajax-frmID="frmCartShipOnline" data-commerceid="${commItem.BBBCommerceItem.id}" data-qty="${commItem.BBBCommerceItem.quantity}" href="#">
											<bbbl:label key="lbl_cartdetail_shipthisitem" language="<c:out param='${language}'/>"/>
										</a>
									</label>
								</c:otherwise>
						</c:choose>
						<c:if test="${internationalShippingOn}">
							<c:if test="${commItem.skuDetailVO.restrictedForIntShip}">   
								<div class="error">
									<bbbl:error key="ERR_INT_SHIP_SKU_RESTRICT_GENERIC_ERROR" language ="${pageContext.request.locale.language}"/>
								</div>
							</c:if>
						</c:if>
					</dsp:oparam>
				</dsp:droplet>
			</div>
		</c:when>
		<c:otherwise>
			<c:out value="${deliveryDetailsUL}" escapeXml="false" />
			<c:set var="commItemStoreId" value="" />
		</c:otherwise>
	</c:choose>

	<input type="submit" name="btnPickUpInStore" id="btnPickUpInStore${commItem.BBBCommerceItem.id}" class="hidden" value="PickUpInStore" aria-pressed="false" aria-labelledby="btnPickUpInStore${commItem.BBBCommerceItem.id}" role="button" />
</dsp:page>
