<dsp:page>
	<div class="cartItemsTotalWrapper marTop_20">
		<ul class="clearfix noMar">
			<li class="grid_4 prefix_4 alpha omega">
				<dl class="clearfix summaryTotal">					
                    	  <dsp:getvalueof var="orderHeaderInfo" param="orderHeaderInfo"/>                    	  
                    	  <dsp:getvalueof var="totalSavedAmount" param="totalDisAmt"/>
                    	  <dsp:getvalueof var="totalSurcharge" param="orderHeaderInfo.shipSurchargeTotalAmt"/>
                    	   <dsp:getvalueof var="totalShippingAmount" param="orderHeaderInfo.shipAmt"/>						 	
						  <dsp:getvalueof var="totalTax" param="orderHeaderInfo.taxAmt"/>
                    	  <dsp:getvalueof var="subTotalAmount" param="orderHeaderInfo.subTotalAmt"/>
                    	  <dsp:getvalueof var="disPercentage" param="disPercentage"/>
                    	  <dsp:getvalueof var="totalQty" param="totalQty"/>
                    	  <dsp:getvalueof var="totalAmount" param="orderHeaderInfo.totalAmt"/>
                    		<dt>
								<p class="noMar">
									<c:if test="${totalQty gt 0}">
										<span class="totalItems bold"><bbbl:label key="lbl_order_subtotal" language="<c:out param='${language}'/>"/> 
										</span>
										<span class="summaryCount"><dsp:valueof param="totalQty"/>
										<bbbl:label key="lbl_preview_items" language="<c:out param='${language}'/>"/></span>
									</c:if>
								</p>
								
							</dt>
							<c:if test="${subTotalAmount gt 0.0}">
								<dd class="bold itemsPrice"><dsp:valueof value="${subTotalAmount}" converter="currency"/></dd>
							</c:if>
							<c:choose>
								<c:when test="${totalShippingAmount gt 0.0}">
									<dt><span class="bold"><bbbl:label key="lbl_cartdetail_estimatedshipping" language="<c:out param='${language}'/>"/>:</span> <a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a></dt> 
									<dd class="bold"><dsp:valueof param="orderHeaderInfo.shipAmt" converter="currency"/></dd>
								</c:when>
								<c:otherwise>
									<dt><span class="bold"><bbbl:label key="lbl_cartdetail_estimatedshipping" language="<c:out param='${language}'/>"/>:</span> <a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a></dt>
									<dd class="bold"><bbbl:label key="lbl_spc_shipping_free" language="${pageContext.request.locale.language}"/></dd>
								</c:otherwise>
							</c:choose>
							<c:if test="${totalSurcharge gt 0.0}">
								<dt><span class="bold"><bbbl:label key="lbl_parcel_surcharge" language="<c:out param='${language}'/>"/>:</span> <a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a></dt>
								<dd class="bold"><dsp:valueof value="${totalSurcharge}" converter="currency"/></dd>
							</c:if>
							
							<%-- TODO: Need to be revisited once tax implementation is done --%>	
							<c:if test="${totalTax gt 0.0}">
								<dt class="bold"><bbbl:label key="lbl_preview_tax" language="<c:out param='${language}'/>"/></dt>
								<dd class="bold"><dsp:valueof value="${totalTax}" converter="currency"/></dd>
							</c:if>							
							<c:if test="${totalAmount gt 0.0}">
								<dt class="total">
								<div class="totalAllign">
								<strong><bbbl:label key="lbl_preview_total" language="<c:out param='${language}'/>"/>:</strong>
								<span class="bold"><dsp:valueof value="${totalAmount}" converter="currency"/></span>
								</div></dt>
							</c:if>		
							
							<c:if test="${totalSavedAmount gt 0.0}">
								<dt class="fr highlight bold totalSaving"><div class="fr"><bbbl:label key="lbl_preview_totalsavings" language="<c:out param='${language}'/>"/>:
								<span class="highlight bold"><dsp:valueof value="${totalSavedAmount}" converter="currency"/></span></div></dt>	
							</c:if>
													
				</dl>
			</li>
		</ul>
	</div>
</dsp:page>
