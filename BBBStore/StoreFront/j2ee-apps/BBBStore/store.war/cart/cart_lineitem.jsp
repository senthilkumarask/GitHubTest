<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

	<%-- need this for single page checkout ajax load --%>
	<c:set var="scene7Path">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
    <dsp:getvalueof var="commItem" param="commItem"/>
    <dsp:getvalueof var="displayName" param="commItem.auxiliaryData.productRef.displayName"/>
    <dsp:getvalueof var="image" param="commItem.auxiliaryData.catalogRef.thumbnailImage"/>
    <div class="grid_2 omega">
        <c:choose>
			<c:when test="${empty image}">
				<img class="fl" src="${imagePath}/_assets/global/images/no_image_available.jpg" width="63" height="63" alt="${displayName}" title="${displayName}" />
			</c:when>
			<c:otherwise>
				<img class="fl noImageFound" src="${scene7Path}/${image}" width="63" height="63" alt="${displayName}" title="${displayName}" />
			</c:otherwise>
		</c:choose>
        <ul class="productInfoContainer">
            <li class="productTitle" tabindex="0"><c:out value="${displayName}" escapeXml="false"/></li>
        </ul>
    </div>
    
    <div class="grid_1 textRight" tabindex="0" aria-label="Quantity:<dsp:valueof param="commItem.quantity" />">
        <span><dsp:valueof param="commItem.quantity" /></span>
    </div>
    <dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
        <dsp:param name="priceObject" param="commItem" />
         <dsp:param name="profile" bean="Profile"/>
        <dsp:param name="elementName" value="priceInfoVO" />
        <dsp:oparam name="output">
            <dsp:getvalueof var="unitSavedAmount" param="priceInfoVO.unitSavedAmount"/>
            <dsp:getvalueof var="unitListPrice" param="priceInfoVO.unitListPrice"/>
            <dsp:getvalueof var="unitSalePrice" param="priceInfoVO.unitSalePrice"/>
            <dsp:getvalueof var="totalSavedAmount" param="priceInfoVO.totalSavedAmount"/>
            <dsp:getvalueof var="adjustmentsList" param="priceInfoVO.adjustmentsList"/>
            <dsp:getvalueof var="undiscountedItemsCount" param="priceInfoVO.undiscountedItemsCount"/>
            <div class="grid_2 textRight">
                <ul class="productPriceContainer">
					<li class="disPrice" tabindex="0" aria-label="our price:<dsp:valueof value="${unitListPrice}" converter="currency"/>">
						<c:choose>
							 <c:when test="${unitSavedAmount gt 0.0}">
								 <dsp:valueof value="${unitSalePrice}" converter="currency"/>
							 </c:when>
							 <c:otherwise>
								 <dsp:valueof value="${unitListPrice}" converter="currency"/>
							 </c:otherwise>
						 </c:choose>
					</li>
					
				</ul>
            </div>
			
									<div class="grid_2 alpha textRight">
										<ul class="productPriceContainer">

													<c:choose>
															<c:when test="${undiscountedItemsCount gt 0}">
																
																		<c:choose>
																			<c:when test="${unitSavedAmount gt 0.0}">
																			<li class="highlight" tabindex="0" aria-label="Your price:<dsp:valueof value="${undiscountedItemsCount}" /> <bbbl:label key="lbl_cart_multiplier" language="${language}"/> <dsp:valueof value="${unitListPrice}" converter="currency" />"><span class="quantity">
																				<dsp:valueof value="${undiscountedItemsCount}" /> <bbbl:label key="lbl_cart_multiplier" language="${language}"/></span> <span class="price">
																				<dsp:valueof value="${unitSalePrice}" converter="currency" /></span>
																			</li>
																			</c:when>
																			<c:otherwise>
																			<li tabindex="0" aria-label="Your Price:<dsp:valueof value="${undiscountedItemsCount}" /> <bbbl:label key="lbl_cart_multiplier" language="${language}"/> <dsp:valueof value="${unitListPrice}" converter="currency" />"><span class="quantity">
																				<dsp:valueof value="${undiscountedItemsCount}" /> <bbbl:label key="lbl_cart_multiplier" language="${language}"/></span> 
																				<span class="price">
																				<dsp:valueof value="${unitListPrice}" converter="currency" /></span>
																			</li>
																			</c:otherwise>
																		</c:choose> 
																
															</c:when>
														</c:choose>
											
														<dsp:droplet name="ForEach">
															<dsp:param name="array" param="priceInfoVO.priceBeans" />
															<dsp:param name="elementName" value="unitPriceBean" />																						
															<dsp:oparam name="output">
																<dsp:droplet name="IsEmpty">
																<dsp:param name="value" param="unitPriceBean.pricingModels" />																													
																	<dsp:oparam name="false">
																		<li class="highlight" tabindex="0" aria-label="Your price:<dsp:valueof param="unitPriceBean.quantity"/> <bbbl:label key="lbl_cart_multiplier" language="${language}"/><dsp:valueof param="unitPriceBean.unitPrice" converter="currency"/>">
																			<dsp:valueof param="unitPriceBean.quantity"/> <bbbl:label key="lbl_cart_multiplier" language="${language}"/> 
																		   <span class="price"><dsp:valueof param="unitPriceBean.unitPrice" converter="currency"/></span>																			                        
																		   <ul class="prodDeliveryInfo couponInfoWrapper marTop_5">
																			 <li class="couponInfo">
																			  <dsp:droplet name="ForEach">
																				  <dsp:param name="array" param="unitPriceBean.pricingModels" />
																				  <dsp:param name="elementName" value="pricingModel" />
																				  <dsp:oparam name="output">																										   
																					<strong><dsp:valueof param="pricingModel.displayName"/></strong>
																				  </dsp:oparam>
																			  </dsp:droplet>
																			 </li>
																		   </ul>
																	   </li>
																	</dsp:oparam>
																</dsp:droplet>																							
															</dsp:oparam>
														</dsp:droplet>
										</ul>
									</div>
            
            <div class="grid_1 omega textRight">
                <ul class="productPriceContainer">
                    <li class="disPrice" tabindex="0" aria-label="Total:<dsp:valueof param="priceInfoVO.totalAmount" converter="currency"/>"><dsp:valueof param="priceInfoVO.totalAmount" converter="currency"/></li>
                    <c:if test="${totalSavedAmount gt 0.0}">
                        <li class="regPrice"><bbbl:label key="lbl_cartdetail_yousave" language="${language}"/> <dsp:valueof value="${totalSavedAmount}" converter="currency"/></li>
                    </c:if>
                </ul>
            </div>
        </dsp:oparam>
    </dsp:droplet>
</dsp:page>