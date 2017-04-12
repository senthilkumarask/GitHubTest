<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
 <c:set var="bedBathCanadaSiteCode"><bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" /></c:set>
	<div class="cartItemsTotalWrapper marTop_20">
		<ul class="clearfix noMar">
			<li class="grid_4 prefix_6 alpha omega 123">
				<dl class="clearfix summaryTotal">
					<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
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
						  <dsp:getvalueof var="finalShippingCharge" param="priceInfoVO.finalShippingCharge"/>
						  
                    	  <dt>
						  		<p class="noMar">
									<c:if test="${itemCount gt 0}">
										<span class="totalItems bold"><bbbl:label key="lbl_spc_order_subtotal" language="<c:out param='${language}'/>"/> 
										</span>
										<span class="summaryCount"><dsp:valueof param="priceInfoVO.itemCount"/>
										<bbbl:label key="lbl_spc_preview_items" language="<c:out param='${language}'/>"/></span>
									</c:if>
								</p>
							</dt>
							<c:if test="${shippingGroupItemsTotal gt 0.0}">
								<dd class="bold itemsPrice"><dsp:valueof value="${shippingGroupItemsTotal}" converter="currency"/></dd>
							</c:if>
							
							<dsp:getvalueof var="rawShippingTotal" param="priceInfoVO.rawShippingTotal"/>
							
							<c:if test="${totalEcoFeeAmount gt 0.0}">
								<dt class="bold"><bbbl:label key="lbl_spc_eco_fees" language="<c:out param='${language}'/>"/></dt>
								<dd class="bold"><dsp:valueof value="${totalEcoFeeAmount}" converter="currency"/></dd>
							</c:if>
							<c:if test="${giftWrapTotal gt 0.0}">
								<dt class="bold"><bbbl:label key="lbl_spc_preview_giftpackaging" language="<c:out param='${language}'/>"/></dt>
								<dd class="bold"><dsp:valueof value="${giftWrapTotal}" converter="currency"/></dd>
							</c:if>								
							
							<c:choose>
								<c:when test="${rawShippingTotal gt 0.0}">
									<dt><span class="bold"><bbbl:label key="lbl_spc_cartdetail_estimatedshipping" language="<c:out param='${language}'/>"/>: </span>
									<c:choose>
										 <c:when test="${finalShippingCharge eq 0.0}">
										 <a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_spc_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a></dt>
											<dd class="bold"><bbbl:label key="lbl_spc_shipping_free" language="<c:out param='${language}'/>"/></dd>
										</c:when>
										 <c:otherwise>
										 </dt>
										 	<dd class="bold"><dsp:valueof value="${finalShippingCharge}" converter="currency" number="0.00"/></dd>
										 </c:otherwise>
									</c:choose>

								</c:when>
								<c:otherwise>
									<dt class=""><span class="bold"><bbbl:label key="lbl_spc_cartdetail_estimatedshipping" language="<c:out param='${language}'/>"/>: </span>
									<c:choose>
										 <c:when test="${finalShippingCharge eq 0.0}">
										 <a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_spc_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a></dt>
										 	<dd class="bold"><bbbl:label key="lbl_spc_shipping_free" language="<c:out param='${language}'/>"/></dd>
										 </c:when>
										 <c:otherwise>
										 </dt>
											<dd class="fl bold"><dsp:valueof value="${finalShippingCharge}" converter="currency" number="0.00"/></dd>
										 </c:otherwise>
									 </c:choose>
								</c:otherwise>
							</c:choose>
							<c:if test="${totalSurcharge gt 0.0}">
								<dt><span class="bold"><bbbl:label key="lbl_spc_parcel_surcharge" language="<c:out param='${language}'/>"/> </span> <a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_spc_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a></dt>
								<dd class="bold"><dsp:valueof value="${totalSurcharge}" converter="currency"/></dd>
							</c:if>
							<c:if test="${surchargeSavings gt 0.0}">
								<dt class="bold"><bbbl:label key="lbl_spc_surchage_savings" language="<c:out param='${language}'/>"/></dt>
								<dd class="highlight bold">(<dsp:valueof value="${surchargeSavings}" converter="currency"/>)</dd>
							</c:if>
							
							<%-- Additional info for LTL items summary (if totalDeliverySurcharge gt 0 then render LTL details)--%>
							<c:if  test ="${totalDeliverySurcharge gt 0.0}">
							<dt class="hidden">LTL items summary</dt>
							
								<%-- <dl class="LTLDetails clearfix"> --%>
									
									<dt class=""> <span class="bold"><bbbl:label key="lbl_spc_cart_delivery_surcharge" language="<c:out param='${language}'/>"/>: </span><a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_spc_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a></dt>
									<dd class="bold"><dsp:valueof value="${totalDeliverySurcharge}" number="0.00" converter="currency"/></dd>
	
									<c:if  test ="${deliverySurchargeSaving gt 0.0}">
										<dt class="bold">
											 <bbbl:label key="lbl_spc_cart_delivery_surcharge_saving" language="<c:out param='${language}'/>"/><br>
										</dt>
										<dd class="parentheses highlightRed bold">(<dsp:valueof value="${deliverySurchargeSaving}" number="0.00" converter="currency"/>)</dd>
									</c:if>
									<c:if  test ="${assemblyFee gt 0.0}">
										<dt class="bold"><bbbl:label key="lbl_spc_cart_assembly_fee" language="<c:out param='${language}'/>"/>:</dt>
										<dd class="bold"><dsp:valueof value="${assemblyFee}" number="0.00" converter="currency"/></dd>
									</c:if>
								<%-- </dl> --%>
							
							</c:if>
							<%-- Additional info for LTL items summary --%>
												
							 <c:if test="${shippingLevelTax gt 0.0}">
							 <c:choose>
							 	<c:when test="${currentSiteId ne bedBathCanadaSiteCode}">
							 	<dt class="bold"><bbbl:label key="lbl_spc_preview_tax" language="<c:out param='${language}'/>"/></dt>
								<dd class="bold"><dsp:valueof value="${shippingLevelTax}" converter="currency"/></dd>	
								</c:when>
								<c:otherwise>
								<c:choose>
										<c:when test="${stateLevelTax eq 0.0 && countyLevelTax ne 0.0}">
											<dt class="bold"><bbbl:label key="lbl_spc_preview_state_tax" language="<c:out param='${language}'/>"/> </dt>
											<dd class="bold"><dsp:valueof value="${countyLevelTax}" converter="currency"/></dd>
											<dt class="bold"><bbbl:label key="lbl_spc_preview_county_tax" language="<c:out param='${language}'/>"/> </dt>
											<dd class="bold"><dsp:valueof value="${stateLevelTax}" converter="currency"/></dd>
											<c:set var="totalStateTax" value="${totalStateTax+countyLevelTax}" scope="request" />
											<c:set var="totalCountyTax" value="${totalCountyTax+stateLevelTax}" scope="request" />
										</c:when>
										<c:otherwise>
											<dt class="bold"><bbbl:label key="lbl_spc_preview_state_tax" language="<c:out param='${language}'/>"/> </dt>
											<dd class="bold"><dsp:valueof value="${stateLevelTax}" converter="currency"/></dd>
											<dt class="bold"><bbbl:label key="lbl_spc_preview_county_tax" language="<c:out param='${language}'/>"/> </dt>
											<dd class="bold"><dsp:valueof value="${countyLevelTax}" converter="currency"/></dd>
											<c:set var="totalStateTax" value="${totalStateTax+stateLevelTax}" scope="request" />
											<c:set var="totalCountyTax" value="${totalCountyTax+countyLevelTax}" scope="request" />											
										</c:otherwise>
								</c:choose>
								</c:otherwise>
								</c:choose>
							</c:if>
							<c:if test="${totalAmount gt 0.0}">
								<dt class="total">
								<div class="totalAllign">
								<strong><bbbl:label key="lbl_spc_preview_total" language="<c:out param='${language}'/>"/>:</strong>
								<span class="bold"><dsp:valueof value="${totalAmount}" converter="currency"/></span>
								</div></dt>
							</c:if>
							<c:if test="${(totalSavedAmount + deliverySurchargeSaving) gt 0}">
								<dt class="fr highlight bold totalSaving"><div class="fr"><bbbl:label key="lbl_spc_preview_totalsavings" language="<c:out param='${language}'/>"/>:
								<span class="highlight bold"><dsp:valueof value="${totalSavedAmount+deliverySurchargeSaving}" converter="currency"/></span></div></dt>
							</c:if>
						</dsp:oparam>
					</dsp:droplet>	
				</dl>
			</li>
		</ul>
	</div>
</dsp:page>