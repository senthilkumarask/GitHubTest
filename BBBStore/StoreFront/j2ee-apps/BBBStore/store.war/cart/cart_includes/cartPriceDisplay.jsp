<dsp:importbean bean="/atg/userprofiling/Profile" var="Profile" />
<dsp:importbean bean="/atg/commerce/ShoppingCart" />
<dsp:getvalueof id="displayDeliverySurMayApply" param="displayDeliverySurMayApply"/>
<dsp:getvalueof id="shipmethodAvlForAllLtlItem" param="shipmethodAvlForAllLtlItem"/>
<dsp:getvalueof id="orderHasLTLItem" param="orderHasLTLItem"/>

<dsp:droplet name="/com/bbb/commerce/order/purchase/OrderHasLTLItemDroplet">
			 <dsp:param name="order" bean="ShoppingCart.current"/>
			 <dsp:oparam name="output">
				<dsp:getvalueof var="orderHasLTLItem" param="orderHasLTLItem" />
			 </dsp:oparam>
</dsp:droplet>

<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
	 <dsp:param name="profile" bean="Profile"/>
	 <dsp:param name="priceObject" bean="ShoppingCart.current" />
	 <dsp:oparam name="output">
		 <dsp:getvalueof var="storeAmount" param="priceInfoVO.storeAmount"/>
		 <dsp:getvalueof var="onlinePurchaseTotal" param="priceInfoVO.onlinePurchaseTotal"/>
		 <dsp:getvalueof var="storeEcoFeeTotal" param="priceInfoVO.storeEcoFeeTotal"/>
		 <dsp:getvalueof var="onlineEcoFeeTotal" param="priceInfoVO.onlineEcoFeeTotal"/>
		 <dsp:getvalueof var="giftWrapTotal" param="priceInfoVO.giftWrapTotal"/>
		 <dsp:getvalueof var="rawShippingTotal" param="priceInfoVO.rawShippingTotal"/>
		 <dsp:getvalueof var="totalSurcharge" param="priceInfoVO.totalSurcharge"/>
		 <dsp:getvalueof var="orderPreTaxAmout" param="priceInfoVO.orderPreTaxAmout"/>
		 <dsp:getvalueof var="totalSavedAmount" param="priceInfoVO.totalSavedAmount"/>
		 <dsp:getvalueof var="freeShipping" param="priceInfoVO.freeShipping"/>
		 <dsp:getvalueof var="shippingSavings" param="priceInfoVO.shippingSavings"/>
		 <dsp:getvalueof var="surchargeSavings" param="priceInfoVO.surchargeSavings"/>
		 <dsp:getvalueof var="finalShippingCharge" param="priceInfoVO.finalShippingCharge"/>

		<dsp:getvalueof var="totalDeliverySurcharge" param="priceInfoVO.totalDeliverySurcharge"/>
		<dsp:getvalueof var="maxDeliverySurchargeReached" param="priceInfoVO.maxDeliverySurchargeReached"/>
	    <dsp:getvalueof var="totalAssemblyFee" param="priceInfoVO.totalAssemblyFee"/>
		<dsp:getvalueof var="maxDeliverySurcharge" param="priceInfoVO.maxDeliverySurcharge"/>
		
		<dd class="fl bold <c:if test="${isInternationalCustomer}">noPadBot internationalOrderPrice</c:if>"><dsp:valueof value="${storeAmount + onlinePurchaseTotal }" converter="currency"/></dd>
                                       <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
         <c:if test="${not isInternationalCustomer}">
		 <c:choose>
			 <c:when test="${freeShipping ne true}">
				<c:choose>
					<c:when  test ="${displayDeliverySurMayApply && !shipmethodAvlForAllLtlItem}"> 
						  <dt class="fl"><span class="bold"><bbbl:label key="lbl_cartdetail_estimatedshipping" language="<c:out param='${language}'/>"/>:</span> <a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a></dt>
						  <dd class="fl bold">TBD</dd>
					</c:when>
					<c:otherwise>
						 <dt class="fl"><span class="bold"><bbbl:label key="lbl_cartdetail_estimatedshipping" language="<c:out param='${language}'/>"/>:</span> 
						  <c:choose>
							 <c:when test="${finalShippingCharge eq 0.0}">
							 <a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a></dt>
								<dd class="fl bold"><bbbl:label key="lbl_shipping_free" language="<c:out param='${language}'/>"/></dd>
							</c:when>
							<c:otherwise>
							</dt>
								<dd class="fl bold"><dsp:valueof value="${finalShippingCharge}" converter="currency" number="0.00"/>*</dd>
							</c:otherwise>
						 </c:choose>
					</c:otherwise>
				</c:choose>
			 </c:when>			 
			 <c:otherwise>
				 <%-- LTL changes --%>
				 <c:choose>
					 <c:when test="${orderHasLTLItem eq true && rawShippingTotal eq 0.0}" >
						 <dt class="fl"><span class="bold"><bbbl:label key="lbl_cartdetail_estimatedshipping" language="<c:out param='${language}'/>"/>:</span> <a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a></dt>
						 <dd class="fl bold">  TBD </dd>
					 </c:when>
					 <c:otherwise>
					 <dt class="fl"><span class="bold"><bbbl:label key="lbl_cartdetail_estimatedshipping" language="<c:out param='${language}'/>"/>:</span> 
						  <c:choose>
							 <c:when test="${finalShippingCharge eq 0.0}">
							 <a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a></dt>
									<dd class="fl bold"><bbbl:label key="lbl_shipping_free" language="<c:out param='${language}'/>"/></dd>
							 </c:when>
							 <c:otherwise>
							 </dt>
									<dd class="fl bold"><dsp:valueof value="${finalShippingCharge}" converter="currency" number="0.00"/>*</dd>
							 </c:otherwise>
						 </c:choose>
						 <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
					 </c:otherwise>
				 </c:choose>
			 </c:otherwise>
		 </c:choose>
		 <c:if test="${totalSurcharge gt 0.0}">
			 <dt class="fl"><span class="bold"><bbbl:label key="lbl_parcel_surcharge" language="<c:out param='${language}'/>"/> </span><a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a></dt>
			 <dd class="fl bold"><dsp:valueof value="${totalSurcharge}" converter="currency"/></dd>
                                           <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
		 </c:if>
		 <c:if test="${surchargeSavings gt 0.0}">
			 <dt class="fl bold"><bbbl:label key="lbl_surchage_savings" language="<c:out param='${language}'/>"/></dt>
			 <dd class="fl bold">(<dsp:valueof value="${surchargeSavings}" converter="currency"/>)</dd>
                                           <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
		 </c:if>
		 <dsp:getvalueof var="ecoFeeTotal" value="${storeEcoFeeTotal + onlineEcoFeeTotal }"/>
		 <c:if test="${ecoFeeTotal gt 0.0}">
			 <dt class="fl bold"><bbbl:label key="lbl_preview_ecofee" language="<c:out param='${language}'/>"/></dt>
			 <dd class="fl bold"><dsp:valueof value="${ecoFeeTotal}" converter="currency"/></dd>
                                           <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
		 </c:if>
		<%-- Additional info for LTL items summary --%>
		<c:if  test ="${displayDeliverySurMayApply && !shipmethodAvlForAllLtlItem}"> 	
                                      <dt class="fl bold  padTop_5"><bbbl:label key="ltl_delivery_surcharge_may_apply" language="<c:out param='${language}'/>"/></dt>
                                      <dd class="fl bold padTop_5">TBD</dd>
		</c:if>
		<c:if  test ="${totalDeliverySurcharge gt 0.0 && shipmethodAvlForAllLtlItem}"> 
                                       <dt class="fl padTop_5"><span class="bold"><bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/>:</span> <a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a></dt>
                                       <dd class="fl bold padTop_5"><dsp:valueof value="${totalDeliverySurcharge}" number="0.00" converter="currency"/></dd>
		</c:if>
                                      <c:if  test ="${maxDeliverySurchargeReached}"> 
                                        <c:choose>
				<c:when test ="${displayDeliverySurMayApply && !shipmethodAvlForAllLtlItem}">
					  <dt class="fl padTop_5"><span class="bold"><bbbl:label key="lbl_cart_max_surcharge_reached" language="<c:out param='${language}'/>"/>:</span> <br>
					  <a href="${contextPath}/static/whatthismean" class="maxSurcharges popup"><bbbl:label key="lbl_what_this_mean" language="<c:out param='${language}'/>"/></a>
					  </dt>
					  <dd class="fl bold highlightRed padTop_5">(TBD)</dd>
				</c:when>
				<c:otherwise>
					 <dt class="fl padTop_5"><span class="bold">
					<bbbl:label key="lbl_cart_max_surcharge_reached" language="<c:out param='${language}'/>"/>:</span> <br>
					<a href="${contextPath}/static/whatthismean" class="maxSurcharges popup"><bbbl:label key="lbl_what_this_mean" language="<c:out param='${language}'/>"/></a>
					</dt>
					<dd class="fl bold highlightRed padTop_5">(-<dsp:valueof value="${totalDeliverySurcharge - maxDeliverySurcharge}" number="0.00" converter="currency"/>)</dd>
				</c:otherwise>
		   </c:choose>
		</c:if>
                                      <c:if  test ="${totalAssemblyFee gt 0.0}"> 
			<dt class="fl bold padTop_5"><bbbl:label key="lbl_cart_assembly_fee" language="<c:out param='${language}'/>"/>:</dt>
                                       <dd class="fl bold padTop_5"><dsp:valueof value="${totalAssemblyFee}" number="0.00" converter="currency"/></dd>
		</c:if>
		<%-- Additional info for LTL items summary --%>
		 
		 <dsp:getvalueof var="preTaxAmout" value="${orderPreTaxAmout + storeAmount + storeEcoFeeTotal }"/>
		 <dt class="fr total">
		   
		    <c:choose>
		   		<c:when test ="${displayDeliverySurMayApply && !shipmethodAvlForAllLtlItem}">
		   			<strong><bbbl:label key="lbl_cartdetail_cart_total" language="<c:out param='${language}'/>"/>:</strong>&nbsp;<span>
		   			<dsp:valueof value="${storeAmount + onlinePurchaseTotal + totalSurcharge + totalAssemblyFee}" converter="currency"/></span>
		   		</c:when>
		   		<c:otherwise>
		   			<strong><bbbl:label key="lbl_cartdetail_pretaxtotal" language="<c:out param='${language}'/>"/>:</strong>&nbsp;<span>
		   			<dsp:valueof value="${preTaxAmout}" converter="currency"/></span>
		   		</c:otherwise>
		   	</c:choose>
		 </dt>
		 <%--<dd class="fl total"></dd>--%>
		 
                                       <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
		 <c:if test="${totalSavedAmount gt 0.0}">
			    <c:choose>
				<c:when test ="${displayDeliverySurMayApply && !shipmethodAvlForAllLtlItem}">
					 <dt class="fr highlight summarySaving bold">
					     <bbbl:label key="lbl_cartdetail_totalsavings" language="<c:out param='${language}'/>"/>:
					     <span class="bold">TBD</span>
					  </dt>
					<%--<dd class="fl bold"></dd>--%>
					 <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
				</c:when>
				<c:otherwise>
					 <dt class="fr highlight summarySaving bold">
					     <bbbl:label key="lbl_cartdetail_totalsavings" language="<c:out param='${language}'/>"/>:
					     <span class="bold"><dsp:valueof value="${totalSavedAmount}" converter="defaultCurrency"/></span>
					 </dt>
					<%-- <dd class="fl bold"></dd>--%>
					 <dt class="clear noPad noMar">&nbsp;</dt><dd class="clear noPad noMar">&nbsp;</dd>
				</c:otherwise>
		  	    </c:choose>
		 </c:if>
		 </c:if>
	 </dsp:oparam>
 </dsp:droplet>