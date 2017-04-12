<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:getvalueof var="priceInfoVO" param="priceInfoVO"/>
	<dsp:getvalueof var="commerceItemRelationship" param="commerceItemRelationship"/>
	<dsp:getvalueof var="unitSavedAmount" param="priceInfoVO.unitSavedAmount"/>
    <dsp:getvalueof var="unitListPrice" param="priceInfoVO.unitListPrice"/>
    <dsp:getvalueof var="unitSalePrice" param="priceInfoVO.unitSalePrice"/>	
    <dsp:getvalueof var="totalSavedAmount" param="priceInfoVO.totalSavedAmount"/>
    <dsp:getvalueof var="totalSavedPercentage" param="priceInfoVO.totalSavedPercentage"/>
	<dsp:getvalueof var="deliverySurcharge" param="priceInfoVO.deliverySurcharge"/>
    <dsp:getvalueof var="deliverySurchargeSaving" param="priceInfoVO.deliverySurchargeSaving"/>
    <dsp:getvalueof var="assemblyFee" param="priceInfoVO.assemblyFee"/>
    <dsp:getvalueof var="isSkuLtl" param="isSkuLtl"/>
				<dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
			         <dsp:param name="priceObject" param="commerceItemRelationship" />
			         <dsp:oparam name="output">
			         <dsp:getvalueof var="undiscountedItemsCount" param="priceInfoVO.undiscountedItemsCount"/>
					 	
					 <li class="grid_1 textCenter">
							<dsp:droplet name="/atg/dynamo/droplet/NotZero">
							  <dsp:param name="Price" param="commerceItemRelationship.commerceItem.priceInfo.salePrice"/>
							  <dsp:oparam name="empty"><strong tabindex="0" aria-label="Our Price:<dsp:valueof param="commerceItemRelationship.commerceItem.priceInfo.listPrice" converter="defaultCurrency"/>"><dsp:valueof param="commerceItemRelationship.commerceItem.priceInfo.listPrice" converter="currency"/></strong></dsp:oparam>
							  <dsp:oparam name="output">
							  <strong class="highlight" tabindex="0" aria-label="Our Price:<dsp:valueof param="commerceItemRelationship.commerceItem.priceInfo.salePrice" converter="currency"/>">
									<dsp:valueof param="commerceItemRelationship.commerceItem.priceInfo.salePrice" converter="currency"/>
								</strong>
							  </dsp:oparam>
							</dsp:droplet>
							<c:if test="${unitSavedAmount gt 0.0}">
                        <span class="smallText marTop_5" style="display: block;">
                                (<bbbl:label key="lbl_spc_preview_reg" language="<c:out param='${language}'/>"/> <dsp:valueof value="${unitListPrice}" converter="currency"/>)
                         </span>
                     </c:if>
					</li>
					

                     
                    <li class="grid_3 textRight yourPrice">
					 <c:choose>
							<c:when test="${undiscountedItemsCount gt 0}">
                                
                                     <ul class="productPriceContainer">
                                       
        							
        								 
        								<c:choose>
        				                    <c:when test="${totalSavedAmount gt 0.0}">
											 <li class="highlight">
												 <strong tabindex="0" aria-label="Your Price:<dsp:valueof value="${undiscountedItemsCount}" /> <bbbl:label key="lbl_spc_cart_multiplier" language="${language}"/><dsp:valueof value="${unitSalePrice}" converter="currency"/>"><dsp:valueof value="${undiscountedItemsCount}" /> <bbbl:label key="lbl_spc_cart_multiplier" language="${language}"/>
        				                         <dsp:valueof value="${unitSalePrice}" converter="currency"/>
											  </strong></li>
        				                     </c:when>
        				                     <c:otherwise>
												 <li><strong tabindex="0" aria-label="Your Price:<dsp:valueof value="${undiscountedItemsCount}" /> <bbbl:label key="lbl_spc_cart_multiplier" language="${language}"/>
        				                         <dsp:valueof value="${unitListPrice}" converter="currency"/>"><dsp:valueof value="${undiscountedItemsCount}" /> <bbbl:label key="lbl_spc_cart_multiplier" language="${language}"/>
        				                         <dsp:valueof value="${unitListPrice}" converter="currency"/>
        				                     </strong></li></c:otherwise>
        								</c:choose> 
        							
                                  
                                    <c:if test="${totalSavedAmount gt 0.0}">
                                        <li class="smallText"><bbbl:label key="lbl_spc_cartdetail_yousave" language="<c:out param='${language}'/>"/> 
                                            <dsp:valueof value="${totalSavedAmount}" converter="currency"/>
                                        </li>
                                    </c:if>
                                 </ul>							
							</c:when>
		                </c:choose> 
                        <ul class="productPriceContainer">    
                        <dsp:getvalueof var="couponApplied" value=""/>
                        	<dsp:droplet name="ForEach">
    							<dsp:param name="array" param="priceInfoVO.priceBeans" />
    							<dsp:param name="elementName" value="unitPriceBean" />																						
    							<dsp:oparam name="output">
    								<dsp:droplet name="IsEmpty">
    								<dsp:param name="value" param="unitPriceBean.pricingModels" />																													
    									<dsp:oparam name="false">
    										<dsp:getvalueof var="couponApplied" value="true"/>
    										<li class="highlight">
        										<strong tabindex="0" aria-label="price:<dsp:valueof param="unitPriceBean.quantity"/> <bbbl:label key="lbl_spc_cart_multiplier" language="${language}"/> 
        											<dsp:valueof param="unitPriceBean.unitPrice" converter="currency"/>">
        											<dsp:valueof param="unitPriceBean.quantity"/> <bbbl:label key="lbl_spc_cart_multiplier" language="${language}"/> 
        											<dsp:valueof param="unitPriceBean.unitPrice" converter="currency"/>
        										</strong>
    										</li>
    										
    											<li class="couponInfo">
        											<dsp:droplet name="ForEach">
            											<dsp:param name="array" param="unitPriceBean.pricingModels" />
            											<dsp:param name="elementName" value="pricingModel" />
        												<dsp:oparam name="output">																																					
        													<p class="smallText noMar"><dsp:valueof param="pricingModel.displayName"/>
															&#32;<a href="${contextPath}/static/shippingexclusionsinclusions" class="popup"><bbbl:label key="lbl_spc_exclusion_inclusion_details" language="<c:out param='${language}'/>"/></a>
															</p>
        												</dsp:oparam>
        											</dsp:droplet>							
    											</li>
    									
    									</dsp:oparam>
    								</dsp:droplet>																							
    							</dsp:oparam>
    						</dsp:droplet>
    						<li>
								<dsp:include page="promo_exclusion_msg.jsp" flush="true">
									<dsp:param name="promoExclusionMap" param="promoExclusionMap"/>
									<dsp:param name="commerceItem" param="commerceItemRelationship.commerceItem" />
								</dsp:include>    									
    						</li>
    						<dsp:droplet
    							name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
    							<dsp:param name="priceObject" param="shippingGroup" />
    							<dsp:param name="orderObject" param="order" />
    							<dsp:param name="commerceIdForEcoFee" param="commerceItemRelationship.commerceItem.id" />
    							<dsp:oparam name="output">
									<dsp:getvalueof var="ecoFeeAmountTemp" param="ecoFeeAmount" />
									 <c:if test="${ecoFeeAmountTemp gt '0.0'}">
									
										  <li class="smallText youPay"> <bbbl:label key="lbl_spc_eco_fee" language="${language}"/>: 
											<dsp:valueof param="ecoFeeAmount" converter="currency"/></li>
									
									</c:if>
    							</dsp:oparam>
    						</dsp:droplet>		
                        </ul>				
				    </li>
					
					<li class="grid_2 alpha omega textRight">
						<ul class="productPriceContainer">
							<c:choose>
		                    <c:when test="${priceInfoVO.undiscountedItemsCount ne commerceItemRelationship.quantity}">
		                         <li class="highlight">
									<strong tabindex="0" aria-label="Total:<dsp:valueof param="priceInfoVO.shippingGroupItemTotal" converter="currency"/>"><dsp:valueof param="priceInfoVO.shippingGroupItemTotal" converter="currency"/></strong>					
								</li>
		                     </c:when>
		                     <c:otherwise>
		                         <li><strong tabindex="0" aria-label="Total:<dsp:valueof param="priceInfoVO.shippingGroupItemTotal" converter="defaultCurrency"/>"><dsp:valueof param="priceInfoVO.shippingGroupItemTotal" converter="currency"/></strong></li>
		                     </c:otherwise>
							</c:choose> 
							<c:if test="${totalSavedAmount gt 0.0 && couponApplied eq 'true'}">
								<li class="smallText">
									<bbbl:label key="lbl_spc_cartdetail_yousave" language="<c:out param='${language}'/>"/>
								 	<dsp:valueof value="${totalSavedAmount}" converter="currency"/>
								 	(<dsp:valueof value="${totalSavedPercentage}" number="0.00"/> %)
								 </li>
							</c:if>
						</ul>
						
						<%-- Additional info for LTL items --%>
						<c:if test="${isSkuLtl}">
							<dl class="LTLDetails clearfix">
								<dt>+ <bbbl:label key="lbl_spc_cart_delivery_surcharge" language="<c:out param='${language}'/>"/>:</dt>
								<dd><dsp:valueof value="${deliverySurcharge}" number="0.00" converter="currency"/></dd>
								<c:if  test ="${deliverySurchargeSaving gt 0.0}">
									<dt class="highlightRed">- <bbbl:label key="lbl_spc_cart_delivery_surcharge_saving" language="<c:out param='${language}'/>"/></dt>
									<dd class="highlightRed">(<dsp:valueof value="${deliverySurchargeSaving}" number="0.00" converter="currency"/>)</dd>
								</c:if>
								<c:if  test ="${assemblyFee gt 0.0}">
									<dt>+ <bbbl:label key="lbl_spc_cart_assembly_fee" language="<c:out param='${language}'/>"/>:</dt>
									<dd><dsp:valueof value="${assemblyFee}" number="0.00" converter="currency"/></dd>
								</c:if>
							</dl>
						</c:if>
						<%-- Additional info for LTL items --%>
					</li>
				</dsp:oparam>
			</dsp:droplet>
</dsp:page>