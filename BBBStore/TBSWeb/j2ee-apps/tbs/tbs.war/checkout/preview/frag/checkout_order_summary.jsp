<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet"/>
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/atg/dynamo/droplet/CurrencyFormatter"/>
	<%-- Variables --%>
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<c:set var="bedBathCanadaSiteCode"><bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" /></c:set>
	<dsp:getvalueof var="isCheckout" param="isCheckout" />
	<dsp:getvalueof var="isConfirmation" param="isConfirmation" />
	<dsp:getvalueof var="count" param="count" />
	<dsp:getvalueof param="isMultiShip" var="isMultiShip"/>

	<dsp:droplet name="BBBPriceDisplayDroplet">
		<dsp:param name="priceObject" param="order" />
		<dsp:oparam name="output">

			<dsp:getvalueof var="adjustmentsList" param="priceInfoVO.adjustmentsList"/>
			<dsp:getvalueof var="totalSavedAmount" param="priceInfoVO.totalSavedAmount"/>
			<dsp:getvalueof var="totalSurcharge" param="priceInfoVO.totalSurcharge"/>
			<dsp:getvalueof var="giftWrapTotal" param="priceInfoVO.giftWrapTotal"/>
<%-- 			<dsp:getvalueof var="shippingLevelTax" param="priceInfoVO.shippingLevelTax"/> --%>
			<dsp:getvalueof var="countyLevelTax" param="priceInfoVO.shippingcountyLevelTax"/>
			<dsp:getvalueof var="stateLevelTax" param="priceInfoVO.shippingStateLevelTax"/>
			<dsp:getvalueof var="totalAmount" param="priceInfoVO.totalAmount"/>
<%--			<dsp:getvalueof var="shippingGroupItemsTotal" param="priceInfoVO.shipOnlineOrderPreTaxAmout"/>--%>
			<dsp:getvalueof var="itemCount" param="priceInfoVO.itemCount"/>
			<dsp:getvalueof var="totalEcoFeeAmount" param="priceInfoVO.ecoFeeTotal"/>
			<dsp:getvalueof var="shippingSavings" param="priceInfoVO.shippingSavings"/>
			<dsp:getvalueof var="surchargeSavings" param="priceInfoVO.surchargeSavings"/>
			<dsp:getvalueof var="totalDeliverySurcharge" param="priceInfoVO.totalDeliverySurcharge"/>
			<dsp:getvalueof var="deliverySurchargeSaving" param="priceInfoVO.deliverySurchargeSaving"/>
			<dsp:getvalueof var="assemblyFee" param="priceInfoVO.assemblyFee"/>

			<dsp:getvalueof var="hardgoodShippingGroupItemCount" param="priceInfoVO.hardgoodShippingGroupItemCount"/>
			<dsp:getvalueof var="totalShippingAmount" param="priceInfoVO.totalShippingAmount"/>
<%-- 			<dsp:getvalueof var="totalTax" param="priceInfoVO.totalTax"/> --%>
			<dsp:getvalueof var="storePickupShippingGroupItemCount" param="priceInfoVO.storePickupShippingGroupItemCount"/>
			<dsp:getvalueof var="onlineEcoFeeTotal" param="priceInfoVO.onlineEcoFeeTotal"/>
			<dsp:getvalueof var="storeEcoFeeTotal" param="priceInfoVO.storeEcoFeeTotal"/>
			<dsp:getvalueof var="storeAmount" param="priceInfoVO.storeAmount"/>
			<dsp:getvalueof var="onlinePurchaseTotal" param="priceInfoVO.onlinePurchaseTotal"/>
			<dsp:getvalueof var="rawShippingTotal" param="priceInfoVO.rawShippingTotal"/>
			<dsp:getvalueof var="freeShipping" param="priceInfoVO.freeShipping"/>
			<dsp:getvalueof var="orderPreTaxAmout" param="priceInfoVO.orderPreTaxAmout"/>
			<dsp:getvalueof var="onlineTotal" param="priceInfoVO.onlineTotal"/>
			<dsp:getvalueof var="totalAmount" param="priceInfoVO.totalAmount" scope="request"/>
			<dsp:getvalueof var="maxDeliverySurchargeReached" param="priceInfoVO.maxDeliverySurchargeReached"/>
			<dsp:getvalueof var="totalAssemblyFee" param="priceInfoVO.totalAssemblyFee"/>
			<dsp:getvalueof var="maxDeliverySurcharge" param="priceInfoVO.maxDeliverySurcharge"/>
			<dsp:getvalueof var="orderHasLtl" param="orderHasLtl"/>
			<dsp:getvalueof var="orderContainsEmptySG" param="orderContainsEmptySG"/>

			<dsp:getvalueof var="shippingLevelTax" param="priceInfoVO.totalTax"/>
			
			<dl class="totals">
				<c:if test="${hardgoodShippingGroupItemCount gt 0}">
					<div class="dl-wrap">
						<dt class="small-12 large-8 columns print-left no-padding-right">
							<h3>
								<%-- <bbbl:label key="lbl_preview_delivery" language="<c:out param='${language}'/>"/> --%>
								<c:out value="${hardgoodShippingGroupItemCount}" /> <bbbl:label key="lbl_preview_items" language="<c:out param='${language}'/>"/>
							</h3>
						</dt>
						<dd class="small-12 large-4 columns no-margin print-5 print-right">
							<h3>
								<dsp:valueof value="${onlinePurchaseTotal}" converter="currency"/>
							</h3>
						</dd>
					</div>
				</c:if>
				<dsp:getvalueof var="rawShippingTotal" param="priceInfoVO.rawShippingTotal"/>
				
				<c:if test="${onlineEcoFeeTotal gt 0.0}">
					<div class="dl-wrap">
						<dt class="small-12 large-6 columns print-left">
							<bbbl:label key="lbl_eco_fees" language="<c:out param='${language}'/>"/>
						</dt>
						<dd class="small-12 large-4 columns no-margin print-5 print-right">
							<dsp:valueof value="${onlineEcoFeeTotal}" converter="currency"/>
						</dd>
					</div>
				</c:if>
				
				<c:if test="${storeEcoFeeTotal gt 0.0}">
					<div class="dl-wrap">
						<dt class="small-12 large-6 columns print-left">
							<bbbl:label key="lbl_eco_fees" language="<c:out param='${language}'/>"/>
						</dt>
						<dd class="small-12 large-4 columns no-margin print-5 print-right">
							<dsp:valueof value="${storeEcoFeeTotal}" converter="currency"/>
						</dd>
					</div>
				</c:if>

				<c:if test="${giftWrapTotal gt 0.0}">
					<div class="dl-wrap">
						<dt class="small-12 large-6 columns print-left">
							<bbbl:label key="lbl_preview_giftpackaging" language="<c:out param='${language}'/>"/>
						</dt>
						<dd class="small-12 large-4 columns no-margin print-5 print-right">
							<dsp:valueof value="${giftWrapTotal}" converter="currency"/>
						</dd>
					</div>
				</c:if>

				<c:choose>
					<c:when test="${rawShippingTotal gt 0.0}">
						<div class="dl-wrap">
							<dt class="small-12 large-6 columns print-left">
								<bbbl:label key="lbl_preview_shipping" language="<c:out param='${language}'/>"/>
								<a href="${contextPath}/static/shippingexclusionsinclusions" class="popupShipping hide-for-print">
                                	(<bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/>)
                            	</a>
							</dt>
							<dd class="small-12 large-4 columns no-margin print-5 print-right">
								<c:set var="shippingTotal"><dsp:valueof value="${rawShippingTotal}" converter="currency" number="0.00"/></c:set>
								<c:choose>
									<c:when test="${isCheckout and not isConfirmation and empty isMultiShip}">
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
									<dd class="small-12 large-4 columns no-margin print-5 print-right savings">(<dsp:valueof value="${shippingSavings}" converter="currency" number="0.00"/>)</dd>
									</div>
								</c:when>
							</c:choose>
					</c:when>
					<c:otherwise>
						<c:if test="${hardgoodShippingGroupItemCount gt 0}">
							<div class="dl-wrap">
								<dt class="small-12 large-6 columns print-left savings">
									<bbbl:label key="lbl_cartdetail_freeshipping" language="<c:out param='${language}'/>"/>
								</dt>
								<dd></dd>
							</div>
						</c:if>
					</c:otherwise>
				</c:choose>
				
				<dsp:getvalueof var="orderAutoWaiveFlag" param="order.autoWaiveFlag"/>
				<dsp:getvalueof var="orderLevelMessage" param="order.autoWaiveClassification"/>
				<c:if test="${orderAutoWaiveFlag && not empty orderLevelMessage}">
                    <div class="dl-wrap">
                    	<dt class="savings columns">${orderLevelMessage}</dt>
						<dd class="savings columns no-margin small-4">(<dsp:valueof value="${shippingSavings}" converter="currency" number="0.00"/>)</dd>
					</div>
				</c:if>

				<c:if test="${totalSurcharge gt 0.0}">
					<div class="dl-wrap">
						<dt class="small-12 large-6 columns print-left surcharge"><bbbl:label key="lbl_preview_surcharge" language="<c:out param='${language}'/>"/></dt>
						<dd class="small-12 large-4 columns no-margin print-5 print-right ">
<%-- 							<c:choose>
								<c:when test="${isCheckout and not isConfirmation}">
									<a class="surcharge" href="#" data-reveal-id="assemblyFeeOverrideModal">
										<dsp:valueof value="${totalSurcharge}" converter="currency"/>
									</a>
								</c:when>
								<c:otherwise>
								</c:otherwise>
							</c:choose>
 --%>						<dsp:valueof value="${totalSurcharge}" converter="currency"/>
						</dd>
					</div>
				</c:if>

				<c:if test="${surchargeSavings gt 0.0}">
					<div class="dl-wrap">
						<dt class="small-12 large-6 columns print-left savings"><bbbl:label key="lbl_surchage_savings" language="<c:out param='${language}'/>"/></dt>
						<dd class="small-12 large-4 columns no-margin print-5 print-right savings">(<dsp:valueof value="${surchargeSavings}" converter="currency"/>)</dd>
					</div>
				</c:if>

				<!-- Additional info for LTL items summary (if totalDeliverySurcharge gt 0 then render LTL details)-->
				<c:if  test ="${totalDeliverySurcharge gt 0.0}">
					<div class="dl-wrap">
						<dt class="small-12 large-6 columns print-left surcharge">
							<bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/>
							<a href="${contextPath}/static/shippingexclusionsinclusions" class="popupShipping hide-for-print">
								(<bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/>)
							</a>
						</dt>
						<dd class="small-12 large-4 columns no-margin print-5 print-right surcharge">
							<%-- <c:choose>
								<c:when test="${isCheckout and not isConfirmation} ">
									<a class="surcharge" href="#" data-reveal-id="deliveryChargeOverrideModal">
										<dsp:valueof value="${totalDeliverySurcharge}" number="0.00" converter="currency"/>
									</a>
								</c:when>
								<c:otherwise>
								</c:otherwise>
							</c:choose> --%>
							<dsp:valueof value="${totalDeliverySurcharge}" number="0.00" converter="currency"/>
						</dd>
					</div>
					
					<c:if test="${deliverySurchargeSaving gt 0.0}">
						<div class="dl-wrap">
							<dt class="small-12 large-4 columns no-margin print-5 print-right savings">
								<bbbl:label key="lbl_cart_delivery_surcharge_saving" language="<c:out param='${language}'/>"/><br>
							</dt>
							<dd class="savings">(<dsp:valueof value="${deliverySurchargeSaving}" number="0.00" converter="currency"/>)</dd>
						</div>
					</c:if>
					
					<c:if test ="${maxDeliverySurchargeReached}">
	                	<div class="dl-wrap">
	                    	<dt class="small-12 large-6 columns print-left">
	                        	<bbbl:label key="lbl_cart_max_surcharge_reached" language="${language}"/> <br>
	                       		<a class="ltlsurcharge hide-on-print" href="/tbs/cart/static/max_surcharges_info.jsp" data-reveal-id="infoModal" data-reveal-ajax="true"><bbbl:label key="lbl_what_this_mean" language="${pageContext.request.locale.language}" /></a>
	                       	</dt>
	                      	<dd class="small-12 large-4 columns no-margin print-5 print-right savings">(-<dsp:valueof value="${totalDeliverySurcharge - maxDeliverySurcharge}" number="0.00" converter="currency"/>)</dd>
	                     </div>
	             	</c:if>
	             	
	             	<c:if  test ="${totalAssemblyFee gt 0.0}">
	                	<div class="dl-wrap">
	                    	<dt class="small-12 large-6 columns print-left"><h3><bbbl:label key="lbl_cart_assembly_fee" language="${language}"/></h3></dt>
	                      	<dd class="small-12 large-4 columns no-margin print-5 print-right"><h3><dsp:valueof value="${totalAssemblyFee}" number="0.00" converter="currency"/></h3></dd>
	                 	</div>
	            	</c:if>
				</c:if>
				<!-- Additional info for LTL items summary -->

				<c:if test="${shippingLevelTax ge 0.0}">
					<c:choose>
						<c:when test="${currentSiteId ne bedBathCanadaSiteCode}">
							<div class="dl-wrap">
								<dt class="small-12 large-6 columns print-left"><bbbl:label key="lbl_preview_tax" language="<c:out param='${language}'/>"/></dt>
								<dd class="small-12 large-4 columns no-margin print-5 print-right ">
									<c:set var="taxTotal"><dsp:valueof value="${shippingLevelTax}" converter="currency" number="0.00"/></c:set>
									<c:choose>
										<c:when test="${isCheckout and not isConfirmation and empty isMultiShip}">
											<a href="#" class="currentTaxTotal" id="currentTaxTotal" data-reveal-id="taxPriceOverrideModal" data-tax-total="${taxTotal}" data-shipping-group-id="<dsp:valueof param='shippingGroup.id'/>">
												<dsp:droplet name="CurrencyFormatter"><dsp:param name="currency" value="${shippingLevelTax}"/>
														    <dsp:param name="locale" bean="/OriginatingRequest.requestLocale.locale"/>
														    <dsp:oparam name="output">
															    <dsp:valueof param="formattedCurrency"/>
														    </dsp:oparam>
											</dsp:droplet>
											</a>
										</c:when>
										<c:otherwise>
											<dsp:droplet name="CurrencyFormatter"><dsp:param name="currency" value="${shippingLevelTax}"/>
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
							<%-- we need to figure out if we need to support tax overrides in canada as well --%>
							<%-- <c:choose>
								<c:when test="${stateLevelTax eq 0.0 && countyLevelTax ne 0.0}">
									<c:set var="totalStateTax" value="${totalStateTax+countyLevelTax}" scope="request" />
									<c:set var="totalCountyTax" value="${totalCountyTax+stateLevelTax}" scope="request" />
								</c:when>
								<c:otherwise>
									<c:set var="totalStateTax" value="${totalStateTax+stateLevelTax}" scope="request" />
									<c:set var="totalCountyTax" value="${totalCountyTax+countyLevelTax}" scope="request" />
								</c:otherwise>
							</c:choose> --%>
							
							<div class="dl-wrap">
										<dt class="small-12 large-6 columns print-left"><bbbl:label key="lbl_preview_state_tax" language="<c:out param='${language}'/>"/> </dt>
										<dd class="small-12 large-4 columns no-margin print-5 print-right"><dsp:valueof value="${totalStateTax}" converter="currency"/></dd>
									</div>
									<div class="dl-wrap">
										<dt class="small-12 large-6 columns print-left"><bbbl:label key="lbl_preview_county_tax" language="<c:out param='${language}'/>"/> </dt>
										<dd class="small-12 large-4 columns no-margin print-5 print-right"><dsp:valueof value="${totalCountyTax}" converter="currency"/></dd>
									</div>
						</c:otherwise>
					</c:choose>
				</c:if>
				<div class="print-gray-panel">
				<c:if test="${totalAmount gt 0.0}">
					<div class="dl-wrap">
						<dt class="small-12 large-6 columns print-left">
							<h3>
								<bbbl:label key="lbl_preview_total" language="<c:out param='${language}'/>"/>
							</h3>
						</dt>
						<dd class="small-12 large-4 columns no-margin print-5 print-right "><h3><dsp:valueof value="${totalAmount}" converter="currency"/></h3></dd>
					</div>
				</c:if>
				<c:if test="${totalSavedAmount gt 0.0}">
                   <div class="dl-wrap">
                      <dt class="small-12 large-6 columns print-left savings"><bbbl:label key="lbl_cartdetail_totalsavings" language="${language}"/></dt>
                      <dd class="small-12 large-4 columns no-margin print-5 print-right savings"><dsp:valueof value="${totalSavedAmount}" converter="currency"/></dd>
                   </div>
               </c:if>
              </div>
               <c:if test="${storePickupShippingGroupItemCount gt 0}">
                    <h3 class="subHeading"><bbbl:label key="lbl_shipping_store_pickup" language="<c:out param='${language}'/>"/> <span class="smallText"><bbbl:label key="lbl_store_amountdue" language="<c:out param='${language}'/>"/></span></h3>
                    <div class="dl-wrap">
                      <dt class="small-12 large-6 columns print-left"><h3>Order Subtotal:</h3></dt>
                      <dd class="small-12 large-4 columns no-margin print-5 print-right "><h3><dsp:valueof value="${storeAmount }" converter="defaultCurrency"/></h3></dd>
                    <c:if test="${storeEcoFeeTotal gt 0.0}">
                        <dt class="small-12 large-6 columns print-left"><h3><bbbl:label key="lbl_preview_ecofee" language="<c:out param='${language}'/>"/></h3></dt>
                        <dd class="small-12 large-4 columns no-margin print-5 print-right "><h3><dsp:valueof value="${storeEcoFeeTotal}" converter="defaultCurrency"/></h3></dd>
                    </c:if>
                    </div>                   
                </c:if>
			</dl>
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
