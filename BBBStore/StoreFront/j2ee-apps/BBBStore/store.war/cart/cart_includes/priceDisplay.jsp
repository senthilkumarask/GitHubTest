<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/> 
<dsp:importbean bean="/atg/userprofiling/Profile" var="Profile" />
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>

<dsp:getvalueof var="commItem" param="commItem"></dsp:getvalueof>
<dsp:getvalueof var="enableKatoriFlag" param="enableKatoriFlag"></dsp:getvalueof>

<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
<dsp:param name="priceObject" value="${commItem.BBBCommerceItem}" />
<dsp:param name="profile" bean="Profile"/>
<dsp:oparam name="output">
    <dsp:getvalueof var="unitSavedAmount" param="priceInfoVO.unitSavedAmount"/>
    <dsp:getvalueof var="unitListPrice" param="priceInfoVO.unitListPrice"/>
    <dsp:getvalueof var="unitSalePrice" param="priceInfoVO.unitSalePrice"/>
    <dsp:getvalueof var="totalSavedAmount" param="priceInfoVO.totalSavedAmount"/>
    <dsp:getvalueof var="adjustmentsList" param="priceInfoVO.adjustmentsList"/>
    <dsp:getvalueof var="undiscountedItemsCount" param="priceInfoVO.undiscountedItemsCount"/>
   	<dsp:getvalueof var="deliverySurcharge" param="priceInfoVO.deliverySurcharge"/>
    <dsp:getvalueof var="deliverySurchargeSaving" param="priceInfoVO.deliverySurchargeSaving"/>
    <dsp:getvalueof var="assemblyFee" param="priceInfoVO.assemblyFee"/>
     <dsp:getvalueof var="shippingMethodAvl" param="shippingMethodAvl"/>
    <dsp:getvalueof var="shippingMethodDescription" param="shippingMethodDescription"/>				
			
    
    <dsp:getvalueof var="undiscountedItemsCount" param="priceInfoVO.undiscountedItemsCount"/>
    <div class="cartTotalDetails clearfix itemCol">
        <dl class="clearfix">
            <dt class="fl total"><bbbl:label key="lbl_cartdetail_totalprice" language="<c:out param='${language}'/>"/></dt>
             <dsp:getvalueof var="omniTotalPrice" param="priceInfoVO.totalAmount" vartype="java.lang.Double"/>
          <c:set var="omniTotalPrice"><dsp:valueof value="${omniTotalPrice}" converter="unformattedCurrency" /></c:set>
                   <input name="omniTotalPrice" type="hidden" value="${omniTotalPrice}" class="omniTotalPriceHidden"/>
                    <dd class="fl total omniTotalPrice">
                <c:choose>
                	<c:when test="${not empty commItem.BBBCommerceItem.referenceNumber && (commItem.BBBCommerceItem.eximErrorExists || !enableKatoriFlag)}">
                        TBD
                    </c:when>
                    <c:otherwise>
                    	<c:choose>
		                    <c:when test="${undiscountedItemsCount eq commItem.BBBCommerceItem.quantity}">
		                        <dsp:valueof param="priceInfoVO.totalAmount" converter="currency"/>
		                    </c:when>
		                    <c:otherwise>
		                        <dsp:valueof param="priceInfoVO.totalAmount" converter="currency"/>
		                    </c:otherwise>
		                    </c:choose>
                    </c:otherwise>
                </c:choose>
            </dd>
            <dt class="fl"><bbbl:label key="lbl_cartdetail_yourprice" language="<c:out param='${language}'/>"/></dt>
            <dd class="fl">
                <c:choose>
                	<c:when test="${not empty commItem.BBBCommerceItem.referenceNumber && (commItem.BBBCommerceItem.eximErrorExists || !enableKatoriFlag)}">
                        TBD
                    </c:when>
                    <c:otherwise>
                    	<c:choose>
		                    <c:when test="${undiscountedItemsCount gt 0}">
		                        <c:choose>
		                            <c:when test="${unitSavedAmount gt 0.0}">
		                                <dsp:valueof value="${undiscountedItemsCount}" /> <bbbl:label key="lbl_cart_multiplier" language="${language}"/> <span class="highlight"><dsp:valueof value="${unitSalePrice}" converter="currency" /></span><br/>
		                            </c:when>
		                            <c:otherwise>
		                                <dsp:valueof value="${undiscountedItemsCount}" /> <bbbl:label key="lbl_cart_multiplier" language="${language}"/> <span class="highlight"><dsp:valueof value="${unitListPrice}" converter="currency" /></span><br/>
		                            </c:otherwise>
		                        </c:choose> 
		                    </c:when>
		                </c:choose>
		                <dsp:getvalueof var="couponApplied" value=""/>
		                <dsp:droplet name="ForEach">
		                    <dsp:param name="array" param="priceInfoVO.priceBeans" />
		                    <dsp:param name="elementName" value="unitPriceBean" />
		                    <dsp:oparam name="output">
		                        <dsp:droplet name="IsEmpty">
		                            <dsp:param name="value" param="unitPriceBean.pricingModels" />
		                            <dsp:oparam name="false">
		                                <dsp:valueof param="unitPriceBean.quantity"/> <bbbl:label key="lbl_cart_multiplier" language="${language}"/> <span class="highlight"><dsp:valueof param="unitPriceBean.unitPrice" converter="currency"/></span><br/>
		                                <dsp:getvalueof var="couponApplied" value="true"/>
		                                <dsp:getvalueof var="disQty" param="unitPriceBean.quantity"/>
		                                <dsp:getvalueof var="unitDisAmt" param="unitPriceBean.unitPrice"/>
		                                <c:set var="couponDiscountAmount"><dsp:valueof value="${couponDiscountAmount + (disQty * (unitListPrice - unitDisAmt))}"/></c:set>
		                                
		                            </dsp:oparam>
		                        </dsp:droplet>
		                    </dsp:oparam>
		                </dsp:droplet>
                 </c:otherwise>
                 </c:choose>
            </dd>
            <dt class="fl"><bbbl:label key="lbl_cart_our_price" language="${language}"/></dt>
            <c:choose>
	            <c:when test="${not empty commItem.BBBCommerceItem.referenceNumber && (commItem.BBBCommerceItem.eximErrorExists || !enableKatoriFlag)}"><dd class="fl">TBD</dd>
	            </c:when>
	            <c:otherwise>
	            	<dd class="fl"><dsp:valueof value="${unitListPrice}" converter="currency"/></dd>
	            </c:otherwise>
            </c:choose>
            
            <c:if test="${empty commItem.BBBCommerceItem.referenceNumber or !commItem.BBBCommerceItem.eximErrorExists}">
	            <dt class="fl">	
	            	<c:if test="${unitSavedAmount gt 0.0 || totalSavedAmount gt 0.0 || couponApplied eq 'true'}">
	            		<bbbl:label key="lbl_cartdetail_yousave" language="<c:out param='${language}'/>"/>
	            	</c:if>		
	            </dt>
	            <dd class="fl">
	            	<c:choose>
	            		<c:when test="${couponApplied ne 'true'}">
			            	<c:if test="${totalSavedAmount gt 0.0}">
			                    <span class="highlight"><dsp:valueof value="${totalSavedAmount}" number="0.00" converter="currency"/> (<dsp:valueof param="priceInfoVO.totalSavedPercentage" number="0.00"/>%)</span>
			                </c:if>
			           </c:when>
		                <c:otherwise>
		                	<c:choose>
			                	<c:when test="${unitSavedAmount gt 0.0 && totalSavedAmount gt 0.0 && totalSavedAmount ne unitSavedAmount}">
					            	<span class="highlight"><dsp:valueof value="${totalSavedAmount}" number="0.00" converter="currency"/></span>
					            </c:when>
					            <c:otherwise>
			                		<span class="highlight"><dsp:valueof value="${couponDiscountAmount + totalSavedAmount}" number="0.00" converter="currency"/></span>
			                	</c:otherwise>
		                	</c:choose>
		                </c:otherwise>
	                </c:choose>
	            </dd>
				
				
				<c:if test="${deliverySurcharge eq 0.0 && (shippingMethodAvl eq false || commItem.BBBCommerceItem.shipMethodUnsupported) && commItem.skuDetailVO.ltlItem }">
					<dt class="fl noPadBot">+ <bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/>:</dt>
					<dd class="fl noPadBot">TBD</dd>
				</c:if>
				
				<%-- Additional info for LTL items --%>
				<c:if test="${shippingMethodAvl && commItem.skuDetailVO.ltlItem}">
				 <dt class="fl noPadBot">+ ${shippingMethodDescription}</dt>
					<dd class="fl noPadBot">
					  <c:if test="${deliverySurcharge eq 0.0}"> FREE</c:if>
					  <c:if test="${deliverySurcharge gt 0.0}"><dsp:valueof value="${deliverySurcharge}" number="0.00" converter="currency"/></c:if>
					</dd>
				</c:if>
				<c:if test="${deliverySurchargeSaving gt 0.0}">
					<dt class="fl noPadBot highlightRed">- <bbbl:label key="lbl_cart_delivery_surcharge_saving" language="<c:out param='${language}'/>"/>:</dt>
					<dd class="fl noPadBot highlightRed">(<dsp:valueof value="${deliverySurchargeSaving}" number="0.00" converter="currency"/>)</dd>
				</c:if>			
				<c:if test="${assemblyFee gt 0.0}">
					<dt class="fl noPadBot">+ <bbbl:label key="lbl_cart_assembly_fee" language="<c:out param='${language}'/>"/>:</dt>
					<dd class="fl noPadBot"><dsp:valueof value="${assemblyFee}" number="0.00" converter="currency"/> </dd>
				</c:if>
				<%-- Additional info for LTL items --%>
	
				<dt class="clear">&nbsp;</dt>
		
	            <dsp:droplet name="ForEach">
					<dsp:param name="array" param="priceInfoVO.promotionDetails" />
					<dsp:param name="elementName" value="promoDisplayName" />
					<dsp:oparam name="output">
						<dsp:droplet name="IsEmpty">
							<dsp:param name="value" param="promoDisplayName" />
							<dsp:oparam name="false">
								<ul class="prodDeliveryInfo pricingModels noMar textRight">
									<li class="pricingModel">
										<span><dsp:valueof param="promoDisplayName"/>&#32;<a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a></span>
									</li>
								</ul>
							</dsp:oparam>
						</dsp:droplet>
					</dsp:oparam>
				</dsp:droplet>
		        <dt class="clear">&nbsp;</dt><dd class="clear">&nbsp;</dd>
            </c:if>
        </dl>
        <div class="clear"></div>
    </div>
</dsp:oparam>
</dsp:droplet>
</dsp:page>