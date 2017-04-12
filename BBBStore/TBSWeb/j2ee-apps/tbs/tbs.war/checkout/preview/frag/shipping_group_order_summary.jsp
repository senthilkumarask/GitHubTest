<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/TBSOverrideReasonDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/TBSCommerceItemLookupDroplet"/>
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/CurrencyFormatter"/>

	<%-- Variables --%>
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<c:set var="bedBathCanadaSiteCode"><bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" /></c:set>
	<dsp:getvalueof var="isCheckout" param="isCheckout" />
	<dsp:getvalueof var="isConfirmation" param="isConfirmation" />
	<dsp:getvalueof var="index" param="count" />
	<dsp:getvalueof var="isMultiShip" param="isMultiShip"/>
	<dsp:getvalueof var="shippingGroupId" param="shippingGroup.id"/>

	<c:if test="${isMultiShip eq 'true'}">
		<dsp:droplet name="ForEach">
			<dsp:param name="array" param="order.relationships"/>
			<dsp:param name="elementName" value="relations"/>
			<dsp:oparam name="output">
				<dsp:getvalueof param="relations.relationshipClassType" var="relationClass"/>
				<c:if test="${relationClass eq 'shippingGroupCommerceItem' }">
					<dsp:getvalueof param="relations.commerceItem" var="tbsItem"/>
					<dsp:getvalueof param="relations.shippingGroup.id" var="loclShipGroupId"/>
					<c:if test="${not empty tbsItem && tbsItem.commerceItemClassType eq 'default' && loclShipGroupId eq shippingGroupId}">
						<dsp:getvalueof var="surchargeItemId" param="relations.commerceItem.deliveryItemId"/>
						<dsp:getvalueof var="assemblyItemId" param="relations.commerceItem.assemblyItemId"/>
						<c:if test="${not empty surchargeItemId}">
							<input type="hidden" id="ltl" value="true"/>
							<dsp:droplet name="TBSCommerceItemLookupDroplet">
								<dsp:param name="id" value="${surchargeItemId}"/>
								<dsp:param name="order" param="order"/>
								<dsp:param name="elementName" value="surchargeItem"/>
									<dsp:oparam name="output">
										<dsp:param param="surchargeItem" name="surchargeItem"/>
										<dsp:getvalueof param="surchargeItem.quantity" var="surchargeQty"/>
										<dsp:getvalueof param="listPrice" var="surchargeListPrice"/>
										<dsp:getvalueof param="displayName" var="SurchargeSkuName"/>
									</dsp:oparam>
							</dsp:droplet>
						</c:if>
						<c:if test="${not empty assemblyItemId}">
							<input type="hidden" id="ltl" value="true"/>
							<dsp:droplet name="TBSCommerceItemLookupDroplet">
								<dsp:param name="id" value="${assemblyItemId}"/>
								<dsp:param name="order" param="order"/>
								<dsp:param name="elementName" value="assemblyItem"/>
									<dsp:oparam name="output">
										<dsp:param param="assemblyItem" name="assemblyItem"/>
										<dsp:getvalueof param="assemblyItem.quantity" var="assemblyQty"/>
										<dsp:getvalueof param="listPrice" var="assemblyListPrice"/>
										<dsp:getvalueof param="displayName" var="assemblySkuName"/>
									</dsp:oparam>
							</dsp:droplet>
						</c:if>
					</c:if>
				</c:if>
			</dsp:oparam>
		</dsp:droplet>
	</c:if>

	<dsp:droplet name="BBBPriceDisplayDroplet">
		<dsp:param name="priceObject" param="shippingGroup" />
		<dsp:param name="orderObject" param="order" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="adjustmentsList" param="priceInfoVO.adjustmentsList"/>
			<dsp:getvalueof var="totalSavedAmount" param="priceInfoVO.totalSavedAmount"/>
			<dsp:getvalueof var="totalSurcharge" param="priceInfoVO.totalSurcharge"/>
			<dsp:getvalueof var="giftWrapTotal" param="priceInfoVO.giftWrapTotal"/>
			<dsp:getvalueof var="shippingLevelTax" param="priceInfoVO.shippingLevelTax"/>
			<dsp:getvalueof var="countyLevelTax" param="priceInfoVO.shippingcountyLevelTax"/>
			<dsp:getvalueof var="stateLevelTax" param="priceInfoVO.shippingStateLevelTax"/>
			<dsp:getvalueof var="totalAmount" param="priceInfoVO.totalAmount"/>
			<dsp:getvalueof var="shippingGroupItemsTotal" param="priceInfoVO.shippingGroupItemsTotal"/>
			<dsp:getvalueof var="itemCount" param="priceInfoVO.itemCount"/>
			<dsp:getvalueof var="totalEcoFeeAmount" param="priceInfoVO.ecoFeeTotal"/>
			<dsp:getvalueof var="shippingSavings" param="priceInfoVO.shippingSavings"/>
			<dsp:getvalueof var="surchargeSavings" param="priceInfoVO.surchargeSavings"/>
			<dsp:getvalueof var="totalDeliverySurcharge" param="priceInfoVO.totalDeliverySurcharge"/>
			<dsp:getvalueof var="deliverySurchargeSaving" param="priceInfoVO.deliverySurchargeSaving"/>
			<dsp:getvalueof var="assemblyFee" param="priceInfoVO.assemblyFee"/>
			<dsp:getvalueof var="assemblyFeeSaving" param="priceInfoVO.assemblyFeeSaving"/>
			<dsp:getvalueof var="shipMethod" param="shippingMethodDescription"/>

			<dl class="totals">

				<div class="dl-wrap">
					<dt class="small-8 large-6 columns print-left">
						<h3>
							<c:if test="${itemCount gt 0}">
								<dsp:valueof param="priceInfoVO.itemCount"/> <bbbl:label key="lbl_preview_items" language="<c:out param='${language}'/>"/>
							</c:if>
						</h3>
					</dt>
					<dd class="small-4 large-4 columns no-margin print-5 print-right ">
						<h3>
							<dsp:valueof value="${shippingGroupItemsTotal}" converter="currency"/>
						</h3>
					</dd>
				</div>

				<dsp:getvalueof var="rawShippingTotal" param="priceInfoVO.rawShippingTotal"/>
				<c:if test="${totalEcoFeeAmount gt 0.0}">
					<div class="dl-wrap">
						<dt class="small-8 large-6 columns print-left">
							<bbbl:label key="lbl_eco_fees" language="<c:out param='${language}'/>"/>
						</dt>
						<dd class="small-4 large-4 columns no-margin print-5 print-right ">
							<dsp:valueof value="${totalEcoFeeAmount}" converter="currency"/>
						</dd>
					</div>
				</c:if>
				<c:choose>
					<c:when test="${giftWrapTotal gt 0.0}">
						<div class="dl-wrap">
							<dt class="small-8 large-6 columns">
								<bbbl:label key="lbl_preview_giftpackaging" language="<c:out param='${language}'/>"/>
							</dt>
							<dd class="small-4 large-4 columns no-margin">
								<c:choose>
									<c:when test="${not isConfirmation}">
										<input type="hidden" class="giftWrapTotals" value="" />
                                   		<input type="hidden" class="shippingGroupId" value="" />
										<a href="#" class="currentGiftWrapTotal" id="currentGiftWrapTotal" data-reveal-id="giftWrapPriceOverrideModal" data-giftwrap-total="<dsp:valueof value="${giftWrapTotal}" converter="currency" number="0.00"/>" data-shipping-group-id="<dsp:valueof param='shippingGroup.id'/>">
											<%-- <dsp:valueof value="${giftWrapTotal}" converter="currency" number="0.00"/> --%>
											<dsp:droplet name="CurrencyFormatter"><dsp:param name="currency"  value="${giftWrapTotal}"/>
														    <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
														    <dsp:oparam name="output">
															    <dsp:valueof param="formattedCurrency"/>
														    </dsp:oparam>
											</dsp:droplet>
										</a>
									</c:when>
									<c:otherwise>
										<dsp:droplet name="CurrencyFormatter"><dsp:param name="currency" value="${giftWrapTotal}"/>
														    <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
														    <dsp:oparam name="output">
															    <dsp:valueof param="formattedCurrency"/>
														    </dsp:oparam>
											</dsp:droplet>	
									</c:otherwise>
								</c:choose>								
							</dd>
						</div>
					</c:when>
					<c:otherwise>
						<div class="dl-wrap">
							<dt class="small-8 large-6 columns">
								<bbbl:label key="lbl_preview_giftpackaging" language="<c:out param='${language}'/>"/>
							</dt>
							<dd class="small-4 large-4 columns no-margin">
									<dsp:droplet name="CurrencyFormatter"><dsp:param name="currency" value="${giftWrapTotal}"/>
														    <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
														    <dsp:oparam name="output">
															    <dsp:valueof param="formattedCurrency"/>
														    </dsp:oparam>
											</dsp:droplet>		
							</dd>
						</div>
					</c:otherwise>
				</c:choose>
				<c:choose>
					<c:when test="${cmo}">
						<div class="dl-wrap">
							<dt class="small-8 large-6 columns print-left">
								<bbbl:label key='lbl_shipping_charges' language='${pageContext.request.locale.language}'/>
							</dt>
							<dd class="small-4 large-4 columns no-margin print-5 print-right ">
								<fmt:formatNumber value="0"  type="currency"/>
							</dd>
							
						</div>
					</c:when>
					<c:when test="${rawShippingTotal gt 0.0}">
						<div class="dl-wrap">
							<dt class="small-8 large-6 columns print-left">
								<bbbl:label key="lbl_preview_shipping" language="<c:out param='${language}'/>"/> &nbsp;
								(<c:out value="${shipMethod}"></c:out>)
								<a href="${contextPath}/static/shippingexclusionsinclusions" class="popupShipping hide-for-print">
                                	(<bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/>)
                            	</a>
							</dt>
							<dd class="small-4 large-4 columns no-margin print-5 print-right ">
								<c:set var="shippingTotal"><dsp:valueof value="${rawShippingTotal}" converter="currency" number="0.00"/></c:set>
								<c:choose>
									<c:when test="${not isConfirmation}">
                                        <input type="hidden" class="shippingTotals" value="" />
                                        <input type="hidden" class="shippingGroupId" value="" />
										<a href="#" class="currentShippingTotal" id="currentShippingTotal" data-reveal-id="shippingPriceOverrideModal" data-shipping-total="${shippingTotal}" data-shipping-group-id="<dsp:valueof param='shippingGroup.id'/>">
											<dsp:valueof value="${rawShippingTotal}" converter="currency" number="0.00"/>
										</a>
									</c:when>
									<c:otherwise>
										<dsp:valueof value="${rawShippingTotal}" converter="currency" number="0.00"/>
									</c:otherwise>
								</c:choose>
							</dd>
						</div>
						
							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="priceInfoVO.shippingAdjustments" />
								<dsp:param name="elementName" value="shippingPromoDiscount" />
								<dsp:oparam name="outputStart">
									<div class="dl-wrap">
										<dt class="small-12 large-6 columns print-left savings">
								</dsp:oparam>
								<dsp:oparam name="outputEnd">
										</dt>
								</dsp:oparam>
								<dsp:oparam name="output">
									<c:set var="shipPromo" value="true" />
									<dsp:getvalueof var="count" param="count" />
									<dsp:getvalueof var="size" param="size" />
									<c:choose>
										<c:when test="${count lt size}" >
											<dsp:valueof param="key.displayName"/>,
										</c:when>
										<c:otherwise>
											<dsp:valueof param="key.displayName"/>
										</c:otherwise>
									</c:choose>
								</dsp:oparam>
							</dsp:droplet>
							<c:choose>
								<c:when test="${not empty shipPromo}">
									<dd class="small-4 large-4 columns no-margin print-5 print-right savings">(<dsp:valueof value="${shippingSavings}" converter="currency" number="0.00"/>)</dd>
									</div>
								</c:when>
							</c:choose>
					</c:when>
					<c:otherwise>
						<div class="dl-wrap">
							<dt class="small-8 large-6 columns print-left savings">
								<%-- <bbbl:label key="lbl_cartdetail_freeshipping" language="<c:out param='${language}'/>"/> --%>
								<bbbl:label key="lbl_preview_shipping" language="<c:out param='${language}'/>"/> &nbsp;
								<c:if test="${not empty shipMethod}">
									(<c:out value="${shipMethod}"></c:out>)
								</c:if>
								<a href="${contextPath}/static/shippingexclusionsinclusions" class="popupShipping hide-for-print">
                                	(<bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/>)
                            	</a>
							</dt>
							<dd class="small-4 large-4 columns no-margin print-5 print-right savings">
								<dsp:valueof value="${shippingSavings}" converter="currency" number="0.00"/>
							</dd>
						</div>
					</c:otherwise>
				</c:choose>
				
				<dsp:getvalueof var="orderAutoWaiveFlag" param="order.autoWaiveFlag"/>
				<dsp:getvalueof var="orderLevelMessage" param="order.autoWaiveClassification"/>
				<c:if test="${not empty orderAutoWaiveFlag and orderAutoWaiveFlag and not empty orderLevelMessage}">
                    <div class="dl-wrap">
                    	<dt class="savings columns">${orderLevelMessage}</dt>
						<dd class="savings columns no-margin small-4">(<dsp:valueof value="${shippingSavings}" converter="currency" number="0.00"/>)</dd>
					</div>
				</c:if>

				<c:if test="${totalSurcharge gt 0.0}">
					<div class="dl-wrap">
						<dt class="small-8 large-6 columns print-left surcharge"><bbbl:label key="lbl_preview_surcharge" language="<c:out param='${language}'/>"/></dt>
						<dd class="small-4 large-4 columns no-margin print-5 print-right surcharge">
							<input type="hidden" class=surchargeTotals value="" />
                            <input type="hidden" class="shippingGroupId" value="" />
							<a href="#" class="shipSurchargeOverride" data-reveal-id="shipSurchargeOverrideModal_1" data-surcharge-total="${totalSurcharge}" data-shipping-group-id="<dsp:valueof param='shippingGroup.id'/>">
                                 <dsp:valueof value="${totalSurcharge}" converter="currency"/>
                            </a>
						</dd>
					</div>
				</c:if>
				<c:if test="${assemblyFeeSaving gt 0.0}">
					<div class="dl-wrap">
						<dt class="small-8 large-6 columns print-left savings"><bbbl:label key="lbl_surchage_savings" language="<c:out param='${language}'/>"/></dt>
						<dd class="small-4 large-4 columns no-margin print-5 print-right savings">(<dsp:valueof value="${assemblyFeeSaving}" converter="currency"/>)</dd>
					</div>
				</c:if>

				<c:if test="${surchargeSavings gt 0.0}">
					<div class="dl-wrap">
						<dt class="small-8 large-6 columns print-left savings"><bbbl:label key="lbl_surchage_savings" language="<c:out param='${language}'/>"/></dt>
						<dd class="small-4 large-4 columns no-margin print-5 print-right savings">(<dsp:valueof value="${surchargeSavings}" converter="currency"/>)</dd>
					</div>
				</c:if>

				<!-- Additional info for LTL items summary (if totalDeliverySurcharge gt 0 then render LTL details)-->
				<c:if  test ="${totalDeliverySurcharge gt 0.0}">
					<div class="dl-wrap">
						<dt class="small-8 large-6 columns print-left"><h3 class="surcharge"><bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/></h3>
                            <a href="${contextPath}/static/shippingexclusionsinclusions" class="popupShipping hide-for-print">
                                (<bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/>)
                            </a></dt>
						<dd class="small-4 large-4 columns no-margin print-5 print-right">
								<h3 class="surcharge"><dsp:valueof value="${totalDeliverySurcharge}" number="0.00" converter="currency"/></h3>
						</dd>
					</div>
					<c:if test="${deliverySurchargeSaving gt 0.0}">
						<div class="dl-wrap">
							<dt class="savings small-12 large-6 columns print-left">
								<bbbl:label key="lbl_cart_delivery_surcharge_saving" language="<c:out param='${language}'/>"/><br>
							</dt>
							<dd class="savings small-12 large-4 columns no-margin print-5 print-right ">(<dsp:valueof value="${deliverySurchargeSaving}" number="0.00" converter="currency"/>)</dd>
						</div>
					</c:if>
					<c:if test ="${assemblyFee gt 0.0}">
						<div class="dl-wrap">
							<dt class="small-8 large-6 columns print-left"><h3><bbbl:label key="lbl_cart_assembly_fee" language="<c:out param='${language}'/>"/></h3></dt>
							<dd class="small-4 large-4 columns no-margin print-5 print-right ">
								<h3><dsp:valueof value="${assemblyFee}" number="0.00" converter="currency"/></h3>
							</dd>
						</div>
					</c:if>
				</c:if>
				<!-- Additional info for LTL items summary -->

				<c:choose>
				<c:when test="${shippingLevelTax gt 0.0}">
					<c:choose>
						<c:when test="${currentSiteId ne bedBathCanadaSiteCode}">
							<div class="dl-wrap">
								<dt class="small-8 large-6 columns print-left"><bbbl:label key="lbl_preview_tax" language="<c:out param='${language}'/>"/></dt>
								<dd class="small-4 large-4 columns no-margin print-5 print-right ">								
									<c:set var="taxTotal"><dsp:droplet name="CurrencyFormatter"><dsp:param name="currency" value="${shippingLevelTax}"/>
														    <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
														    <dsp:oparam name="output">
															    <dsp:valueof param="formattedCurrency"/>
														    </dsp:oparam>
											</dsp:droplet></c:set>
									<c:choose>
										<c:when test="${not isConfirmation}">
                                            <input type="hidden" class="taxTotals" value="" />
                                            <input type="hidden" class="shippingGroupId" value="" />
											<a href="#" class="currentTaxTotal" id="currentTaxTotal" data-reveal-id="taxPriceOverrideModal" data-tax-total="${taxTotal}" data-shipping-group-id="<dsp:valueof param='shippingGroup.id'/>">
												${taxTotal}
											</a>
										</c:when>
										<c:otherwise>
											${taxTotal}
										</c:otherwise>
									</c:choose>
								</dd>
							</div>
						</c:when>
						<c:otherwise>
							<%-- we need to figure out if we need to support tax overrides in canada as well --%>
							<fmt:formatNumber var="totalCanadaTax" value="${countyLevelTax+stateLevelTax}" type="currency"/>
							<input type="hidden" class="taxTotals" value="" />
							<input type="hidden" class="shippingGroupId" value="" />
							<c:choose>
								<c:when test="${stateLevelTax eq 0.0 && countyLevelTax ne 0.0}">
									<div class="dl-wrap">
										<dt class="small-8 large-6 columns print-left"><bbbl:label key="lbl_preview_state_tax" language="<c:out param='${language}'/>"/> </dt>
										<c:choose>
										<c:when test="${countyLevelTax gt 0}">
										<dd class="small-4 large-4 columns no-margin print-5 print-right "><a id="currentTaxTotal" class="currentTaxTotal" href="#" data-reveal-id="taxPriceOverrideModal" data-tax-total="${totalCanadaTax}" data-shipping-group-id="<dsp:valueof param='shippingGroup.id'/>"><dsp:valueof value="${countyLevelTax}" converter="currency"/></a></dd>
										</c:when>
										<c:otherwise>
										<dd class="small-4 large-4 columns no-margin print-5 print-right "><dsp:valueof value="${countyLevelTax}" converter="currency"/></dd>
										</c:otherwise>
										</c:choose>
									</div>
									<div class="dl-wrap">
										<dt class="small-8 large-6 columns print-left"><bbbl:label key="lbl_preview_county_tax" language="<c:out param='${language}'/>"/> </dt>
										<dd class="small-4 large-4 columns no-margin print-5 print-right "><dsp:valueof value="${stateLevelTax}" converter="currency"/></dd>
									</div>
									<c:set var="totalStateTax" value="${totalStateTax+countyLevelTax}" scope="request" />
									<c:set var="totalCountyTax" value="${totalCountyTax+stateLevelTax}" scope="request" />
								</c:when>
								<c:otherwise>
									<div class="dl-wrap">
										<dt class="small-8 large-6 columns print-left"><bbbl:label key="lbl_preview_state_tax" language="<c:out param='${language}'/>"/> </dt>
										<c:choose>
										<c:when test="${stateLevelTax gt 0}">
										<dd class="small-4 large-4 columns no-margin print-5 print-right "><a id="currentTaxTotal" class="currentTaxTotal" href="#" data-reveal-id="taxPriceOverrideModal" data-tax-total="${totalCanadaTax}" data-shipping-group-id="<dsp:valueof param='shippingGroup.id'/>"><dsp:valueof value="${stateLevelTax}" converter="currency"/></a></dd>
										</c:when>
										<c:otherwise>
										<dd class="small-4 large-4 columns no-margin print-5 print-right "><dsp:valueof value="${stateLevelTax}" converter="currency"/></dd>
										</c:otherwise>
										</c:choose>
									</div>
									<div class="dl-wrap">
										<dt class="small-8 large-6 columns print-left"><bbbl:label key="lbl_preview_county_tax" language="<c:out param='${language}'/>"/> </dt>
										<c:choose>
										<c:when test="${countyLevelTax gt 0}">
										<dd class="small-4 large-4 columns no-margin print-5 print-right "><a id="currentTaxTotal" class="currentTaxTotal" href="#" data-reveal-id="taxPriceOverrideModal" data-tax-total="${totalCanadaTax}" data-shipping-group-id="<dsp:valueof param='shippingGroup.id'/>"><dsp:valueof value="${countyLevelTax}" converter="currency"/></a></dd>
										</c:when>
										<c:otherwise>
										<dd class="small-4 large-4 columns no-margin print-5 print-right "><dsp:valueof value="${countyLevelTax}" converter="currency"/></dd>
										</c:otherwise>
										</c:choose>
									</div>
									<c:set var="totalStateTax" value="${totalStateTax+stateLevelTax}" scope="request" />
									<c:set var="totalCountyTax" value="${totalCountyTax+countyLevelTax}" scope="request" />
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${currentSiteId ne bedBathCanadaSiteCode}">
							<div class="dl-wrap">
								<dt class="small-8 large-6 columns print-left"><bbbl:label key="lbl_preview_tax" language="<c:out param='${language}'/>"/></dt>
								<dd class="small-4 large-4 columns no-margin print-5 print-right">
									<%-- <dsp:valueof value="${shippingLevelTax}" converter="currency"/> --%>
									<dsp:droplet name="CurrencyFormatter"><dsp:param name="currency" value="${shippingLevelTax}"/>
														    <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
														    <dsp:oparam name="output">
															    <dsp:valueof param="formattedCurrency"/>
														    </dsp:oparam>
											</dsp:droplet>
								</dd>
							</div>
						</c:when>
						<c:otherwise>
							<div class="dl-wrap">
								<dt class="small-8 large-6 columns print-left"><bbbl:label key="lbl_preview_state_tax" language="<c:out param='${language}'/>"/> </dt>
								<dd class="small-4 large-4 columns no-margin print-5 print-right "><dsp:valueof value="${stateLevelTax}" converter="currency"/></dd>
							</div>
							<div class="dl-wrap">
								<dt class="small-8 large-6 columns print-left"><bbbl:label key="lbl_preview_county_tax" language="<c:out param='${language}'/>"/> </dt>
								<dd class="small-4 large-4 columns no-margin print-5 print-right "><dsp:valueof value="${countyLevelTax}" converter="currency"/></dd>
							</div>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
				</c:choose>
				<div class="dl-wrap print-gray-panel">
					<c:if test="${totalAmount gt 0.0}">
						<div class="dl-wrap">
							<dt class="small-8 large-6 columns print-left">
								<h3>
									<c:if test="${index gt 0 and isMultiship}">Shipment ${count}</c:if> <bbbl:label key="lbl_preview_total" language="<c:out param='${language}'/>"/>
								</h3>
							</dt>
							<dd class="small-4 large-4 columns no-margin print-5 print-right"><h3><dsp:valueof value="${totalAmount}" converter="currency"/></h3></dd>
						</div>
					</c:if>
					<c:if test="${totalSavedAmount gt 0.0}">
	                   <div class="dl-wrap">
	                      <dt class="small-8 large-6 columns print-left savings"><bbbl:label key="lbl_cartdetail_totalsavings" language="${language}"/></dt>
	                      <dd class="small-4 large-4 columns no-margin print-5 print-right savings"><dsp:valueof value="${totalSavedAmount}" converter="currency"/></dd>
	                   </div>
	               </c:if>
			</div>
			</dl>
			
			<div id="surchargeOverrideModal_${surchargeItemId}" class="reveal-modal medium surcharge-override" data-reveal="">
				<div class="row">
					<div class="small-12 columns no-padding">
						<h1>Surcharge Override</h1>
						<p class="note">* required field</p>
					</div>
				</div>
				<div class="row">
					<div class="small-12 large-4 columns">
						<label class="right inline">Item Description</label>
					</div>
					<div class="small-12 large-8 columns">
						<input type="text" value="<c:out value='${SurchargeSkuName}' escapeXml='false' />" disabled />
					</div>
				</div>
				<div class="row">
					<div class="small-12 large-4 columns">
						<label class="right inline">Quantity</label>
					</div>
					<div class="small-12 large-8 columns surchargeQty">
						 <input type="text" value="<c:out value='${surchargeQty}' escapeXml='false' />" disabled />
					</div>
				</div>
				<div class="row">
					<div class="small-12 large-4 columns">
						<label class="right inline">Price per Unit</label>
					</div>
					<div class="small-12 large-8 columns price">
						<input type="text" value="<dsp:valueof value='${surchargeListPrice}' converter='currency' />" disabled />
					</div>
				</div>
				<div class="row">
					<div class="small-12 large-4 columns">
						<label class="right inline">Quantity to Override</label>
					</div>
					<div class="small-12 large-8 columns quantity">
						<div class="qty-spinner">
							<a class="button minus secondary"><span></span></a>
								<input id="qtyToOverride_${surchargeItemId}" class="quantity-input" type="text" data-max-value="99" maxlength="2" value="${surchargeQty}" data-max-value="${surchargeQty}">
							<a class="button plus secondary"><span></span></a>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="small-12 large-4 columns">
						<label for="reasonList_${surchargeItemId}" class="right inline">Reason *</label>
					</div>
					<div class="small-12 large-8 columns">
						<select name="reasonList_${surchargeItemId}" id="reasonList_${surchargeItemId}" class="surchargeReasonList">
							<dsp:droplet name="TBSOverrideReasonDroplet">
								<dsp:param name="OverrideType" value="surcharge" />
								<dsp:oparam name="output">
									<option value="">Select Reason</option>
									<dsp:droplet name="ForEach">
										<dsp:param name="array" param="reasons" />
										<dsp:param name="elementName" value="elementVal" />
										<dsp:oparam name="output">
										<option value="<dsp:valueof param='key'/>">
                                        <dsp:valueof param="elementVal" />
                                      </option>
                                   		</dsp:oparam>
									</dsp:droplet>
								</dsp:oparam>
							</dsp:droplet>
						</select>
					</div>
				</div>
				<div class="row">
					<div class="small-12 large-4 columns">
						<label for="competitorList_${surchargeItemId}" class="right inline">Competitor</label>
					</div>
					<div class="small-12 large-8 columns">
						<select name="competitorList_${surchargeItemId}" id="competitorList_${surchargeItemId}" class="surchargeCompetitorList">
							<dsp:droplet name="TBSOverrideReasonDroplet">
								<dsp:param name="OverrideType" value="competitors" />
								<dsp:oparam name="output">
									<option value="">Select Competitor</option>
									<dsp:droplet name="ForEach">
										<dsp:param name="array" param="reasons" />
										<dsp:param name="elementName" value="elementVal" />
										<dsp:oparam name="output">
										<option value="<dsp:valueof param='key'/>">
                                           <dsp:valueof param="elementVal" />
                                        </option>
                                   		</dsp:oparam>
									</dsp:droplet>
								</dsp:oparam>
							</dsp:droplet>
						</select>
					</div>
				</div>
				<div class="row">
					<div class="small-12 large-4 columns">
						<label for="newPrice_${surchargeItemId}" class="right inline">New Price *</label>
					</div>
					<div class="small-12 large-8 columns">
						<span class="dollarSignOverride">$</span>
						<input type="tel" value="0.00" id="newPrice_${surchargeItemId}" name="newPrice_${surchargeItemId}" class="priceOverride newSurchargePrice" maxlength="7" value=""/>
					</div>
				</div>
				<div class="row">
					<div class="small-12 large-offset-4 large-4 columns">
						<a href='javascript:void(0);' class="button small service expand submit-surcharge-override" data-parent="#priceOverrideModal_${surchargeItemId}" data-cid="${surchargeItemId}">Override</a>
					</div>
					<div class="small-12 large-4 columns">
						<a href='javascript:void(0);' class="close-modal button secondary">Cancel</a>
					</div>
				</div>
				<a class="close-reveal-modal">&times;</a>
			</div>
			<div id="assemblyFeeOverrideModal_${assemblyItemId}" class="reveal-modal medium assemblyFee-override" data-reveal="">
				<div class="row">
					<div class="small-12 columns no-padding">
						<h1>Assembly Fee Override</h1>
						<p class="note">* required field</p>
					</div>
				</div>
				<div class="row">
					<div class="small-12 large-4 columns">
						<label class="right inline">Item Description</label>
					</div>
					<div class="small-12 large-8 columns">
						<input type="text" value="<c:out value='${assemblySkuName}' escapeXml='false' />" disabled />
					</div>
				</div>
				<div class="row">
					<div class="small-12 large-4 columns">
						<label class="right inline">Quantity</label>
					</div>
					<div class="small-12 large-8 columns">
						 <input type="text" value="<c:out value='${assemblyQty}' escapeXml='false' />" disabled />
					</div>
				</div>
				<div class="row">
					<div class="small-12 large-4 columns">
						<label class="right inline">Price per Unit</label>
					</div>
					<div class="small-12 large-8 columns">
						<input type="text" value="<dsp:valueof value='${assemblyListPrice}' converter='currency' />" disabled />
					</div>
				</div>
				<div class="row">
					<div class="small-12 large-4 columns">
						<label class="right inline">Quantity to Override</label>
					</div>
					<div class="small-12 large-8 columns quantity">
						<div class="qty-spinner">
							<a class="button minus secondary"><span></span></a>
								<input id="qtyToOverride_${assemblyItemId}" class="quantity-input" type="text" data-max-value="99" maxlength="2" value="${assemblyQty}" data-max-value="${assemblyQty}">
							<a class="button plus secondary"><span></span></a>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="small-12 large-4 columns">
						<label for="reasonList_${assemblyItemId}" class="right inline">Reason *</label>
					</div>
					<div class="small-12 large-8 columns">
						<select name="reasonList_${assemblyItemId}" id="reasonList_${assemblyItemId}" class="assemblyReasonList">
							<dsp:droplet name="TBSOverrideReasonDroplet">
								<dsp:param name="OverrideType" value="surcharge" />
								<dsp:oparam name="output">
									<option value="">Select Reason</option>
									<dsp:droplet name="ForEach">
										<dsp:param name="array" param="reasons" />
										<dsp:param name="elementName" value="elementVal" />
										<dsp:oparam name="output">
										<option value="<dsp:valueof param='key'/>">
                                        <dsp:valueof param="elementVal" />
                                      </option>
                                   		</dsp:oparam>
									</dsp:droplet>
								</dsp:oparam>
							</dsp:droplet>
						</select>
					</div>
				</div>
				<div class="row">
					<div class="small-12 large-4 columns">
						<label for="competitorList_${assemblyItemId}" class="right inline">Competitor</label>
					</div>
					<div class="small-12 large-8 columns">
						<select name="competitorList_${assemblyItemId}" id="competitorList_${assemblyItemId}" class="assemblyCompetitorList">
							<dsp:droplet name="TBSOverrideReasonDroplet">
								<dsp:param name="OverrideType" value="competitors" />
								<dsp:oparam name="output">
									<option value="">Select Competitor</option>
									<dsp:droplet name="ForEach">
										<dsp:param name="array" param="reasons" />
										<dsp:param name="elementName" value="elementVal" />
										<dsp:oparam name="output">
										<option value="<dsp:valueof param='key'/>">
                                        <dsp:valueof param="elementVal" />
                                      </option>
                                   		</dsp:oparam>
									</dsp:droplet>
								</dsp:oparam>
							</dsp:droplet>
						</select>
					</div>
				</div>
				<div class="row">
					<div class="small-12 large-4 columns">
						<label for="newPrice_${assemblyItemId}" class="right inline">New Price *</label>
					</div>
					<div class="small-12 large-8 columns">
						<span class="dollarSignOverride">$</span>
						<input type="tel" value="0.00" id="newPrice_${assemblyItemId}" name="newPrice_${assemblyItemId}" class="priceOverride assemblyNewPrice" maxlength="7" />
					</div>
				</div>
				<div class="row">
					<div class="small-12 large-offset-4 large-4 columns">
						<a href='javascript:void(0);' class="button small service expand submit-assemblyFee-override" data-parent="#priceOverrideModal_${assemblyItemId}" data-cid="${assemblyItemId}">Override</a>
					</div>
					<div class="small-12 large-4 columns">
						<a href='javascript:void(0);' class="close-modal">Cancel</a>
					</div>
				</div>
				<a class="close-reveal-modal">&times;</a>
			</div>
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
