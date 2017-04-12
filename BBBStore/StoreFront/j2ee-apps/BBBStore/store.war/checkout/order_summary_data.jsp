<dsp:page>
    <dsp:importbean bean="/atg/commerce/ShoppingCart" />
    <dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
    <c:set var="bedBathCanadaSiteCode"><bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" /></c:set>
    <c:set var="sddSignatureThreshold"><bbbc:config key="sddSignatureThreshold" configName="SameDayDeliveryKeys" /></c:set>
    <c:set var="displayTax" scope="request">
        <dsp:valueof param="displayTax"/>
    </c:set>
    <dsp:getvalueof var="order" param="order" scope="request"/>
	<dsp:getvalueof var="placeHolderMap" param="placeHolderMap"/>
    <c:if test="${empty order}">
        <dsp:getvalueof var="order" bean="ShoppingCart.current" scope="request"/>    
    </c:if>
    <dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
        <dsp:param name="priceObject" value="${order}"  />
        <dsp:param name="profile" bean="Profile"/>
            <dsp:oparam name="output">
            <dsp:getvalueof var="hardgoodShippingGroupItemCount" param="priceInfoVO.hardgoodShippingGroupItemCount"/>
            <dsp:getvalueof var="totalShippingAmount" param="priceInfoVO.totalShippingAmount" scope="request" />
            <dsp:getvalueof var="totalSurcharge" param="priceInfoVO.totalSurcharge"/>
            <dsp:getvalueof var="totalTax" param="priceInfoVO.totalTax" scope="request"/>
            <dsp:getvalueof var="storePickupShippingGroupItemCount" param="priceInfoVO.storePickupShippingGroupItemCount"/>
            <dsp:getvalueof var="onlineEcoFeeTotal" param="priceInfoVO.onlineEcoFeeTotal"/>
            <dsp:getvalueof var="storeEcoFeeTotal" param="priceInfoVO.storeEcoFeeTotal"/>
            <dsp:getvalueof var="giftWrapTotal" param="priceInfoVO.giftWrapTotal"/>
            <dsp:getvalueof var="storeAmount" param="priceInfoVO.storeAmount"/>
            <dsp:getvalueof var="countyLevelTax" param="priceInfoVO.shippingCountyLevelTax"/>
            <dsp:getvalueof var="stateLevelTax" param="priceInfoVO.shippingStateLevelTax"/>
            <dsp:getvalueof var="onlinePurchaseTotal" param="priceInfoVO.onlinePurchaseTotal"/>
            <dsp:getvalueof var="onlineRawTotal" param="priceInfoVO.onlineRawTotal"/>
            <dsp:getvalueof var="promoDiscountAmount" param="priceInfoVO.promoDiscountAmount"/>
            <dsp:getvalueof var="rawShippingTotal" param="priceInfoVO.rawShippingTotal"/>
            <dsp:getvalueof var="totalSavedAmount" param="priceInfoVO.totalSavedAmount"/>
            <dsp:getvalueof var="freeShipping" param="priceInfoVO.freeShipping"/>
            <dsp:getvalueof var="orderPreTaxAmout" param="priceInfoVO.orderPreTaxAmout"/>
            <dsp:getvalueof var="onlineTotal" param="priceInfoVO.onlineTotal"/>
            <dsp:getvalueof var="totalAmount" param="priceInfoVO.totalAmount" scope="request"/>                            
            <dsp:getvalueof var="shippingSavings" param="priceInfoVO.shippingSavings"/>
            <dsp:getvalueof var="surchargeSavings" param="priceInfoVO.surchargeSavings"/>
			<dsp:getvalueof var="finalShippingCharge" param="priceInfoVO.finalShippingCharge"/>
			
			<dsp:getvalueof var="totalDeliverySurcharge" param="priceInfoVO.totalDeliverySurcharge"/>
			<dsp:getvalueof var="maxDeliverySurchargeReached" param="priceInfoVO.maxDeliverySurchargeReached"/>
			<dsp:getvalueof var="totalAssemblyFee" param="priceInfoVO.totalAssemblyFee"/>
			<dsp:getvalueof var="maxDeliverySurcharge" param="priceInfoVO.maxDeliverySurcharge"/>
			<dsp:getvalueof var="orderHasLtl" param="orderHasLtl"/>
			<dsp:getvalueof var="orderContainsEmptySG" param="orderContainsEmptySG"/>	
			<c:if test="${onlineTotal gt sddSignatureThreshold}">
				<input type="hidden" id="displaySDDSignatureMsg" value="true" />
			</c:if>					
			<h3 class="subHeading"><bbbl:label key="lbl_preview_delivery" language="<c:out param='${language}'/>"/></h3>
            <dl class="clearfix <c:if test='${storePickupShippingGroupItemCount gt 0}'>inStorePickup</c:if> noBorder summaryNew">
            	<c:choose>
                	<c:when test="${totalSavedAmount gt 0.0}">
	            	<dt class="bold"><bbbl:label key="lbl_item_subtotal" language="<c:out param='${language}'/>"/> <span class="summaryCount">${hardgoodShippingGroupItemCount} <bbbl:label key="lbl_preview_items" language="<c:out param='${language}'/>"/></span></dt>
	            	<dd class="bold itemsPrice"><dsp:valueof value="${onlineRawTotal}" converter="defaultCurrency"/></dd>
	            	
	            	<dt class="highlightRed bold"><bbbl:label key="lbl_promo_offers_applied" language="<c:out param='${language}'/>"/> </dt>
	            	<dd class="highlightRed bold itemsPrice"> -<dsp:valueof value="${promoDiscountAmount}" converter="defaultCurrency"/></dd>

	            	<dt class="bold"><bbbl:label key="lbl_net_order_subtotal" language="<c:out param='${language}'/>"/> </dt>
		            </c:when>
		            <c:otherwise>
	                <dt class="bold"><bbbl:label key="lbl_order_subtotal" language="<c:out param='${language}'/>"/> <span class="summaryCount">${hardgoodShippingGroupItemCount} <bbbl:label key="lbl_preview_items" language="<c:out param='${language}'/>"/></span></dt>
	                </c:otherwise>
                </c:choose>
                <dd class="bold itemsPrice"><dsp:valueof value="${onlinePurchaseTotal }" converter="defaultCurrency"/></dd>
                <c:if test="${hardgoodShippingGroupItemCount gt 0.0}">
                    <c:set value="0" var="count" scope="request"/>
                    
                    <c:if test="${onlineEcoFeeTotal gt 0.0}">
                        <dt class="bold"><bbbl:label key="lbl_preview_ecofee" language="<c:out param='${language}'/>"/>
                            <c:set var="ecofeeFootNote" scope="request">
                                <bbbl:label key="lbl_footnote_ecofee" language="<c:out param='${language}'/>"/>
                            </c:set>
                            <c:if test="${not empty ecofeeFootNote}">
                                <sup>${placeHolderMap.ecofeeFootNoteCount}</sup>
                            </c:if>
                        </dt>
                        <dd class="bold">
                            <dsp:valueof value="${onlineEcoFeeTotal}" converter="defaultCurrency"/>                
                        </dd>
                    </c:if>
                    <c:if test="${giftWrapTotal gt 0.0}">
                        <dt class="bold"><bbbl:label key="lbl_preview_giftpackaging" language="<c:out param='${language}'/>"/>
                            <c:set var="giftWrapFootNote" scope="request">
                                <bbbl:label key="lbl_footnote_giftWrap" language="<c:out param='${language}'/>"/>
                            </c:set>
                            <c:if test="${not empty giftWrapFootNote}">
                                <sup>${placeHolderMap.giftWrapFootNoteCount}</sup>
                            </c:if>
                        </dt>
                        <dd class="bold"><dsp:valueof value="${giftWrapTotal}" converter="defaultCurrency"/></dd>
                    </c:if>
                                            
                    <c:choose>
                        <c:when test="${freeShipping ne true}">
                        
                        		 <c:choose>
										<c:when  test ="${orderHasLtl and orderContainsEmptySG}"> 
										<dt><span class="bold"><bbbl:label key="lbl_cartdetail_estimatedshipping" language="<c:out param='${language}'/>"/>:
											<c:set var="shippingFootNote" scope="request">
												<bbbl:label key="lbl_footnote_shipping" language="<c:out param='${language}'/>"/>
											</c:set>
											 <c:if test="${not empty shippingFootNote}">
					                               
					                                    <sup>${placeHolderMap.shippingFootNoteCount}</sup>
					                                </c:if>
										 </span>	 
										 <a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a>
											    
										 <dd class="bold">TBD</dd>
										</c:when>
										<c:otherwise>
										   <dt><span class="bold"><bbbl:label key="lbl_cartdetail_estimatedshipping" language="<c:out param='${language}'/>"/>:
					                                <c:set var="shippingFootNote" scope="request">
					                                    <bbbl:label key="lbl_footnote_shipping" language="<c:out param='${language}'/>"/>
					                                </c:set>
					                                <c:if test="${not empty shippingFootNote}">
					                                    <sup>${placeHolderMap.shippingFootNoteCount}</sup>
					                                </c:if>
											   </span>	 											  
											<c:choose>
												<c:when test="${finalShippingCharge eq 0.0}">
												 <a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a>
				                            	</dt>
													<dd class="bold"><bbbl:label key="lbl_shipping_free" language="<c:out param='${language}'/>"/></dd>
												</c:when>
												<c:otherwise>
												</dt>
													<dd class="bold"><dsp:valueof value="${finalShippingCharge}" converter="defaultCurrency" number="0.00"/></dd>
												</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>

                        </c:when>
                        <c:otherwise>
                            <dt class="fl"><span class="bold"><bbbl:label key="lbl_cartdetail_estimatedshipping" language="<c:out param='${language}'/>"/>:</span> 
							 <c:choose>
							 	<c:when test="${orderHasLtl eq true && rawShippingTotal eq 0.0}" >
							 	</dt>
							 		<dd class="fl bold">  TBD </dd>
							 	</c:when>
							 	<c:otherwise>
								 	<c:choose>
										<c:when test="${finalShippingCharge eq 0.0}">
										<a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a></dt>
											<dd class="fl bold"><bbbl:label key="lbl_shipping_free" language="<c:out param='${language}'/>"/></dd>
										</c:when>
										<c:otherwise>
										</dt>
											<dd class="fl bold"><dsp:valueof value="${finalShippingCharge}" converter="defaultCurrency" number="0.00"/></dd>
										</c:otherwise>
									</c:choose>
								</c:otherwise>
								
								
							</c:choose>
                        </c:otherwise>
                   </c:choose>
                    <c:if test="${totalSurcharge gt 0.0}">
						<dt><span class="bold"><bbbl:label key="lbl_parcel_surcharge" language="<c:out param='${language}'/>"/> </span> <a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a></dt>
						<dd class="bold"><dsp:valueof value="${totalSurcharge}" converter="defaultCurrency"/></dd>
					</c:if>
					<c:if test="${surchargeSavings gt 0.0}">
						<dt class="bold"><bbbl:label key="lbl_surchage_savings" language="<c:out param='${language}'/>"/></dt>
						<dd class="bold">(<dsp:valueof value="${surchargeSavings}" converter="defaultCurrency"/>)</dd>
					</c:if>
					
					<%-- Additional info for LTL items summary --%>
					<c:if  test ="${orderHasLtl}">
						<dt class="hidden">LTL items summary</dt>
							<c:if  test ="${orderHasLtl and orderContainsEmptySG}">
							 <dt class="fl"> 
								
							<span class="bold"><bbbl:label key="ltl_delivery_surcharge_may_apply" language="<c:out param='${language}'/>"/></span></dt>
							<dd class="fl bold">TBD</dd>
								</c:if>
						<c:if  test ="${totalDeliverySurcharge gt 0.0 and !orderContainsEmptySG and orderHasLtl}">
							<dt><span class="bold"><bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/>: </span><a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a></dt>
							<dd class="bold"><dsp:valueof value="${totalDeliverySurcharge}" number="0.00" converter="defaultCurrency"/></dd>
								</c:if>
						
						<c:if  test ="${maxDeliverySurchargeReached}"> 
									<c:choose>
										<c:when test="${orderHasLtl and orderContainsEmptySG}">
									 <dt>
			                            <span class="bold"><bbbl:label key="lbl_cart_max_surcharge_reached" language="<c:out param='${language}'/>"/>:</span> <br>
					                            <a href="${contextPath}/static/whatthismean" class="maxSurcharges popup"><bbbl:label key="lbl_what_this_mean" language="<c:out param='${language}'/>"/></a>
					                        </dt>
			                        <dd class="parentheses highlightRed bold">TBD</dd>
										</c:when>
										<c:otherwise>
									 <dt>
			                           <span class="bold"> <bbbl:label key="lbl_cart_max_surcharge_reached" language="<c:out param='${language}'/>"/>:</span> <br>
					                            <a href="${contextPath}/static/whatthismean" class="maxSurcharges popup"><bbbl:label key="lbl_what_this_mean" language="<c:out param='${language}'/>"/></a>
					                        </dt>
	                        		<dd class="parentheses highlightRed bold">(-<dsp:valueof value="${totalDeliverySurcharge - maxDeliverySurcharge}" number="0.00" converter="defaultCurrency"/>)</dd>
										</c:otherwise>
									</c:choose>
								</c:if>

						<c:if  test ="${totalAssemblyFee gt 0.0}"> 
							<dt class="bold"><bbbl:label key="lbl_cart_assembly_fee" language="<c:out param='${language}'/>"/>:</dt>
	                        <dd class="bold"><dsp:valueof value="${totalAssemblyFee}" number="0.00" converter="defaultCurrency"/></dd>
								</c:if>
						
					</c:if>
					<%-- Additional info for LTL items summary --%>
					
	                <c:if test="${not empty order.specialInstructions['CYBERSOURCE_TAX_FAILURE']}">
				    	<c:set var="taxFailureFootNote" scope="request">
				            <bbbl:label key="lbl_preview_tax_unavailable" language="<c:out param='${language}'/>"/>
				        </c:set>
	                </c:if>       
                    <c:choose>
                        <c:when test="${displayTax}">
							<c:choose>
								<c:when test="${not empty taxFailureFootNote}">
								<dt class="bold"><bbbl:label key="lbl_preview_tax" language="<c:out param='${language}'/>"/>:</dt>
									<dd class="smallText">*</dd>
								</c:when>
								<c:when test="${currentSiteId eq bedBathCanadaSiteCode}">
								
										<dt class="bold"><bbbl:label key="lbl_preview_state_tax" language="<c:out param='${language}'/>"/> </dt>
										<dd class="bold"><dsp:valueof value="${totalStateTax}" converter="defaultCurrency"/></dd>
										<dt class="bold"><bbbl:label key="lbl_preview_county_tax" language="<c:out param='${language}'/>"/> </dt>
										<dd class="bold"><dsp:valueof value="${totalCountyTax}" converter="defaultCurrency"/></dd>
									<%-- <c:choose>
										<c:when test="${stateLevelTax eq 0.0 && countyLevelTax ne 0.0}">
											<dt class="bold"><bbbl:label key="lbl_preview_state_tax" language="<c:out param='${language}'/>"/> </dt>
											<dd class="bold"><dsp:valueof value="${countyLevelTax}" converter="defaultCurrency"/></dd>
											<dt class="bold"><bbbl:label key="lbl_preview_county_tax" language="<c:out param='${language}'/>"/> </dt>
											<dd class="bold"><dsp:valueof value="${stateLevelTax}" converter="defaultCurrency"/></dd>
										</c:when>
										<c:otherwise>
											<dt class="bold"><bbbl:label key="lbl_preview_state_tax" language="<c:out param='${language}'/>"/> </dt>
											<dd class="bold"><dsp:valueof value="${stateLevelTax}" converter="defaultCurrency"/></dd>
											<dt class="bold"><bbbl:label key="lbl_preview_county_tax" language="<c:out param='${language}'/>"/> </dt>
											<dd class="bold"><dsp:valueof value="${countyLevelTax}" converter="defaultCurrency"/></dd>
										</c:otherwise>
								</c:choose> --%>
								</c:when>
								<c:otherwise>
								<dt class="bold"><bbbl:label key="lbl_preview_tax" language="<c:out param='${language}'/>"/>:</dt>
									<dd class="bold"><dsp:valueof value="${totalTax}" converter="defaultCurrency"/></dd>
								</c:otherwise> 
							</c:choose>
							<dt class="total bold"><div class="totalAllign"><strong><bbbl:label key="lbl_preview_total" language="<c:out param='${language}'/>"/>:</strong>
								<span><dsp:valueof value="${onlineTotal}" converter="defaultCurrency"/> </span></div>								
							</dt>
							<c:if test="${totalSavedAmount gt 0.0}">
								<dt class="fr highlight summarySavingNew bold">
									<bbbl:label key="lbl_cartdetail_totalsavings" language="<c:out param='${language}'/>" />
										: <span class="bold"><dsp:valueof value="${totalSavedAmount}" converter="defaultCurrency" /></span>
								</dt>
							</c:if>
								<%-- <c:choose>
										<c:when
											test="${displayDeliverySurMayApply && !shipmethodAvlForAllLtlItem}">
											<dt class="fr highlight summarySaving bold">
											<bbbl:label key="lbl_cartdetail_totalsavings"
													language="<c:out param='${language}'/>" />
												: <span class="bold">TBD</span>
											</dt>																		
										</c:when>
										<c:otherwise>
											<dt class="fr highlight summarySaving bold">
											<bbbl:label key="lbl_cartdetail_totalsavings"
													language="<c:out param='${language}'/>" />
												: <span class="bold"><dsp:valueof
														value="${totalSavedAmount}" converter="defaultCurrency" /></span>
											</dt>
										</c:otherwise>
									</c:choose> 
								 </dt> --%>
                        </c:when>
                        <c:otherwise>          
                            <c:set var="totalAmount" value="${totalAmount - totalTax}"/>       
                            <dt class="total bold">
	                            <c:choose>
							   		<c:when test ="${orderHasLtl and orderContainsEmptySG}">
							   			<div class="totalAllign"><strong><bbbl:label key="lbl_cartdetail_cart_total" language="<c:out param='${language}'/>"/>:</strong>
										<span><dsp:valueof value="${storeAmount + onlinePurchaseTotal + totalSurcharge + totalAssemblyFee}" converter="defaultCurrency"/></span></div>
							   		</c:when>
							   		<c:otherwise>
							   			<div class="totalAllign"><strong><bbbl:label key="lbl_preview_pretaxtotal" language="<c:out param='${language}'/>"/>:</strong>
										<span><dsp:valueof value="${orderPreTaxAmout}" converter="defaultCurrency"/></span></div>
							   		</c:otherwise>
							   	</c:choose>
							   	<c:if test="${totalSavedAmount gt 0.0}">
									<dt class="fr highlight summarySavingNew bold">
										<bbbl:label key="lbl_cartdetail_totalsavings" language="<c:out param='${language}'/>" />
											: <span class="bold"><dsp:valueof value="${totalSavedAmount}" converter="defaultCurrency" /></span>
									</dt>
								</c:if>
								<%-- <c:choose>
									<c:when
										test="${orderHasLtl and orderContainsEmptySG  }">
										<dt class="fr highlight summarySaving bold">
										<bbbl:label key="lbl_cartdetail_totalsavings"
												language="<c:out param='${language}'/>" />
											: <span class="bold">TBD</span>
										</dt>																		
									</c:when>
									<c:otherwise>
									<dt class="fr highlight summarySaving bold">
										<bbbl:label key="lbl_cartdetail_totalsavings"
												language="<c:out param='${language}'/>" />
											: <span class="bold"><dsp:valueof
													value="${totalSavedAmount}" converter="defaultCurrency" /></span>
										</dt>
									</c:otherwise>
								</c:choose> 
							</dt> --%>
                        </c:otherwise>
                    </c:choose>
                </c:if>
				
            </dl>

			<c:if test="${storePickupShippingGroupItemCount gt 0}">
                <h3 class="subHeading"><bbbl:label key="lbl_shipping_store_pickup" language="<c:out param='${language}'/>"/> <span class="smallText"><bbbl:label key="lbl_store_amountdue" language="<c:out param='${language}'/>"/></span></h3>
				
                <dl class="clearfix summaryNew">
                    <dt class="bold">Order Subtotal:</dt>
                    <dd class="bold"><dsp:valueof value="${storeAmount }" converter="defaultCurrency"/></dd>   
                    <c:if test="${storeEcoFeeTotal gt 0.0}">
                        <dt class="bold"><bbbl:label key="lbl_preview_ecofee" language="<c:out param='${language}'/>"/></dt>
                        <dd class="bold"><dsp:valueof value="${storeEcoFeeTotal}" converter="defaultCurrency"/></dd>
                    </c:if>                        
                </dl>
            </c:if>
        </dsp:oparam>
    </dsp:droplet>

</dsp:page>