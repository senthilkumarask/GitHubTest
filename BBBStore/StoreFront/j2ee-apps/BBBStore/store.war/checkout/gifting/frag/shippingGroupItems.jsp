
<dsp:page>

<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>	
<c:set var="customizeCTACodes">
	<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
</c:set>
<dsp:getvalueof var="giftWrapMap" param="giftWrapMap" />
<dsp:getvalueof var="commItemList" param="commItemList" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>
      <dsp:droplet name="/com/bbb/common/droplet/EximCustomizationDroplet">
		    <dsp:oparam name="output">
			    <dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
		    </dsp:oparam>
      </dsp:droplet>
		<dsp:droplet name="ForEach">
			<dsp:param name="array" param="commItemList" />
			<dsp:param name="elementName" value="commerceItem"/>
			
			<dsp:oparam name="output">

			<dsp:droplet name="ProductDetailDroplet">
							<dsp:param name="id" param="commerceItem.auxiliaryData.productId" />
							<dsp:param name="skuId" param="commerceItem.catalogRefId" />
							<dsp:param name="siteId" param="commerceItem.auxiliaryData.siteId"/>
							<dsp:param name="isMainProduct" value="true" />
							<dsp:oparam name="output">	
									<dsp:getvalueof var="skuName" param="pSKUDetailVO.displayName"/>
									<dsp:getvalueof var="unitPrice" param="commerceItem.priceInfo.listPrice"/>
									<dsp:getvalueof var="sourceImg" param="commerceItem.auxiliaryData.catalogRef.thumbnailImage"/>
									<dsp:getvalueof var="skuColor" param="pSKUDetailVO.color"/>
									<dsp:getvalueof var="skuSize" param="pSKUDetailVO.size"/>
									<dsp:getvalueof var="personalizationType" param="pSKUDetailVO.personalizationType"/>
									<dsp:getvalueof var="customizableCodes" param="pSKUDetailVO.customizableCodes"/>
									<dsp:getvalueof var="referenceNumber" param="commerceItem.referenceNumber" />
									<dsp:getvalueof var="personalizationOptions" param="commerceItem.personalizationOptions" />
									<dsp:getvalueof var="personalizationOptionsDisplay" param="commerceItem.personalizationOptionsDisplay" />
									<dsp:getvalueof var="personalizePrice" param="commerceItem.personalizePrice" />
									<dsp:getvalueof var="personalizationDetails" param="commerceItem.personalizationDetails" />
									<dsp:getvalueof var="fullImagePath" param="commerceItem.fullImagePath" />
									
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param param="productVO.rollupAttributes" name="array" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="menu" param="key"/>
											<c:if test="${menu eq 'FINISH'}">																			
												<c:set var="rollupAttributesFinish" value="true" />
											</c:if>
										</dsp:oparam>
									</dsp:droplet>

									<li class="grid_2 product alpha">
									<div class="productShadow"></div>
									<div class="productContent">
										
										<c:choose>
											<c:when test="${empty sourceImg}">
												<img width="63" height="63" title="${skuName} ${unitPrice}" alt="${skuName} ${unitPrice}" src="${imagePath}/_assets/global/images/no_image_available.jpg" class="productImage" />
											</c:when>
											<c:when test="${not empty referenceNumber}">
												<img width="63" height="63" title="${skuName} ${unitPrice}" alt="${skuName} ${unitPrice}" src="${fullImagePath}" class="productImage noImageFound" />
											</c:when>
											<c:otherwise>
												<img width="63" height="63" title="${skuName} ${unitPrice}" alt="${skuName} ${unitPrice}" src="${scene7Path}/${sourceImg}" class="productImage noImageFound" />
											</c:otherwise>
										</c:choose>
										<ul class="prodInfo">
											<li class="prodName"><dsp:valueof param="productVO.name" valueishtml="true"/></li>
												<dsp:getvalueof var="salePrice"  param="commerceItem.priceInfo.salePrice"/>
												<dsp:getvalueof var="listPrice" param="commerceItem.priceInfo.listPrice" />
													<c:choose>
														<c:when test="${salePrice gt 0.0}">
															<li class="prodPrice"><dsp:valueof value="${salePrice}"
																	converter="currency" number="000.00" />
															</li>
														</c:when>
														<c:otherwise>
															<li class="prodPrice"><dsp:valueof value="${listPrice}"
																	converter="currency" number="000.00" />
															</li>
														</c:otherwise>
													</c:choose>
											<li>
												<c:if test='${not empty skuColor}'>
													 <c:choose>
														<c:when test="${rollupAttributesFinish == 'true'}">
															<bbbl:label key="lbl_item_finish" language="${pageContext.request.locale.language}"/> : 
														</c:when>
														<c:otherwise>
															<bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/> : 
														</c:otherwise>
													</c:choose><dsp:valueof value="${skuColor}" valueishtml="true" />
												</c:if>
												<c:if test='${not empty skuSize}'>
													 <c:if test='${not empty skuColor}'><br/></c:if><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/> : <dsp:valueof value="${skuSize}" valueishtml="true" />
												</c:if>
												<c:if test='${not empty personalizationOptions}'>
													<br>${eximCustomizationCodesMap[personalizationOptions]} :  ${personalizationDetails}
												</c:if>
												<c:if test='${not empty referenceNumber}'>
													<div class="personalizationAttr katoriPrice">
													<%--BBBSL-8154 --%>
										              <%--  <span  aria-hidden="true">${personalizationOptionsDisplay}</span> --%>
													   <div class="priceAddText">
													    <c:choose>
											                 <c:when test='${not empty personalizePrice && not empty personalizationType && personalizationType == "PY"}'>
																<dsp:valueof value="${personalizePrice}" converter="currency"/> <bbbl:label key="lbl_exim_added_price" language ="${pageContext.request.locale.language}"/>
															</c:when>
															<c:when test='${not empty personalizePrice && not empty personalizationType && personalizationType == "CR"}'>
																<dsp:valueof value="${personalizePrice}" converter="currency"/> 
																<c:choose>
																	<c:when test="${not empty customizableCodes && fn:contains(customizeCTACodes, customizableCodes)}">
																		<bbbl:label key="lbl_exim_cr_added_price_customize" language ="${pageContext.request.locale.language}"/>
																	</c:when>
																	<c:otherwise>
																		<bbbl:label key="lbl_exim_cr_added_price" language ="${pageContext.request.locale.language}"/>
																	</c:otherwise>
																</c:choose>
															</c:when>
															<c:when test='${not empty personalizationType && personalizationType == "PB"}'>
																<bbbl:label key="lbl_PB_Fee_detail" language ="${pageContext.request.locale.language}"/>
															</c:when>
														</c:choose>
														</div>
									              	</div>
												</c:if>
												<c:set var="rollupAttributesFinish" value="false" />
											</li>											
											<li class="prodSubInfo clearfix">
												<ul>
													<li><span class="smallText">
														<bbbl:label key="lbl_shipping_qty" language="<c:out param='${language}'/>"/>:
													</span> 
														<dsp:getvalueof var="tempCatalogId" param="commerceItem.id" />
														<c:out value="${giftWrapMap[tempCatalogId]}"/>
														</li>
												</ul>
											</li>
											<dsp:getvalueof var="giftWrapEligible" param="pSKUDetailVO.giftWrapEligible"/>

											<c:if test="${giftWrapEligible}">
												<li class="smallText">
													<bbbl:label key="lbl_shipping_eligible_gift_pack" language="<c:out param='${language}'/>"/>
												</li>
											</c:if>
															
										</ul>
									</div>
								</li>
							</dsp:oparam>
			</dsp:droplet>
			</dsp:oparam>
		</dsp:droplet>
	</dsp:page>