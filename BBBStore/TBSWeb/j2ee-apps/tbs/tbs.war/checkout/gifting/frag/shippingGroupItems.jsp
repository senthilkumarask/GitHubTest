
<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>

	<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>
	<dsp:getvalueof var="giftWrapMap" param="giftWrapMap" />
	<dsp:getvalueof var="commItemList" param="commItemList" />
	<c:set var="customizeCTACodes">
		<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
	</c:set>
	<dsp:droplet name="ForEach">
	<dsp:param name="array" param="commItemList" />
	<dsp:param name="elementName" value="commerceItem"/>
	<dsp:oparam name="output">

		<dsp:droplet name="ProductDetailDroplet">
		<dsp:param name="id" param="commerceItem.auxiliaryData.productId" />
		<dsp:param name="skuId" param="commerceItem.catalogRefId" />
		<dsp:param name="siteId" param="commerceItem.auxiliaryData.siteId"/>
		<dsp:param name="isDefaultSku" value="true"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="skuName" param="pSKUDetailVO.displayName"/>
			<dsp:getvalueof var="unitPrice" param="commerceItem.priceInfo.listPrice"/>
			<dsp:getvalueof var="sourceImg" param="commerceItem.auxiliaryData.catalogRef.thumbnailImage"/>
			<dsp:getvalueof var="skuColor" param="pSKUDetailVO.color"/>
			<dsp:getvalueof var="skuSize" param="pSKUDetailVO.size"/>
			<dsp:getvalueof var="personalizationType" param="pSKUDetailVO.personalizationType"/>
			<dsp:getvalueof var="referenceNumber" param="commerceItem.referenceNumber" />
			<dsp:getvalueof var="personalizationOptions" param="commerceItem.personalizationOptions" />
			<dsp:getvalueof var="personalizationOptionsDisplay" param="commerceItem.personalizationOptionsDisplay" />
			<dsp:getvalueof var="personalizePrice" param="commerceItem.personalizePrice" />
			<dsp:getvalueof var="personalizationDetails" param="commerceItem.personalizationDetails" />
			<dsp:getvalueof var="thumbnailImagePath" param="commerceItem.thumbnailImagePath" />
            

        	<dsp:droplet name="/com/bbb/common/droplet/EximCustomizationDroplet">
        		<dsp:param name="personalizationOptions" value="${personalizationOptions}"/>	
        		<dsp:oparam name="output">
        			 <dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
        		</dsp:oparam>
        	 </dsp:droplet>
        	 
			<dsp:droplet name="ForEach">
			<dsp:param param="productVO.rollupAttributes" name="array" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="menu" param="key"/>
				<c:if test="${menu eq 'FINISH'}">
					<c:set var="rollupAttributesFinish" value="true" />
				</c:if>
			</dsp:oparam>
			</dsp:droplet>

			<div class="productContent">
				<c:choose>
				    <c:when test="${not empty referenceNumber}">
				        <img title="${skuName} ${unitPrice}" alt="${skuName} ${unitPrice}" src="${thumbnailImagePath}" class="productImage noImageFound" height="83" width="83"/>
			        </c:when>
					<c:when test="${empty sourceImg}">
						<img title="${skuName} ${unitPrice}" alt="${skuName} ${unitPrice}" src="${imagePath}/_assets/global/images/no_image_available.jpg" class="productImage" />
					</c:when>
					<c:otherwise>
						<img title="${skuName} ${unitPrice}" alt="${skuName} ${unitPrice}" src="${scene7Path}/${sourceImg}" class="productImage noImageFound" />
					</c:otherwise>
				</c:choose>

				<%-- capitalize facet labels --%>
				<c:set var="labelColor"><bbbl:label key='lbl_item_color' language='${pageContext.request.locale.language}'/></c:set>
				<c:set var="labelSize"><bbbl:label key='lbl_item_size' language='${pageContext.request.locale.language}'/></c:set>
				<c:set var="labelFinish"><bbbl:label key="lbl_item_finish" language="${pageContext.request.locale.language}"/></c:set>
				<c:set var="labelQuantity"><bbbl:label key="lbl_shipping_qty" language="${pageContext.request.locale.language}"/></c:set>
				<c:set var="labelColorCapitalize" value="${fn:toUpperCase(fn:substring(labelColor, 0, 1))}${fn:toLowerCase(fn:substring(labelColor, 1,fn:length(labelColor)))}" />
				<c:set var="labelSizeCapitalize" value="${fn:toUpperCase(fn:substring(labelSize, 0, 1))}${fn:toLowerCase(fn:substring(labelSize, 1,fn:length(labelSize)))}" />
				<c:set var="labelFinishCapitalize" value="${fn:toUpperCase(fn:substring(labelFinish, 0, 1))}${fn:toLowerCase(fn:substring(labelFinish, 1,fn:length(labelFinish)))}" />
				<c:set var="labelQuantityCapitalize" value="${fn:toUpperCase(fn:substring(labelQuantity, 0, 1))}${fn:toLowerCase(fn:substring(labelQuantity, 1,fn:length(labelQuantity)))}" />

				<ul class="prodInfo">
					<li class="prodName"><dsp:valueof param="productVO.name" valueishtml="true"/></li>
					<li class="prodSubInfo">
						<c:if test='${not empty skuColor}'>
							<c:choose>
								<c:when test="${rollupAttributesFinish == 'true'}">
									<c:out value="${labelFinishCapitalize}" />:
								</c:when>
								<c:otherwise>
									<c:out value="${labelColorCapitalize}" />:
								</c:otherwise>
							</c:choose>
							<dsp:valueof value="${fn:toLowerCase(skuColor)}" valueishtml="true" />
						</c:if>
						<c:choose>
							<c:when test="${not empty personalizationOptions && fn:contains(customizeCTACodes, personalizationOptions)}">
								<c:set var="customizeTxt" value="true"/>
							</c:when>
							<c:otherwise>
								<c:set var="customizeTxt" value="false"/>
							</c:otherwise>
						</c:choose>
						<c:if test='${not empty skuSize}'>
							<c:if test='${not empty skuColor}'><br/></c:if>
							<c:out value="${labelSizeCapitalize}" />: <dsp:valueof value="${fn:toLowerCase(skuSize)}" valueishtml="true" />
						</c:if>
						<c:if test='${not empty referenceNumber && not empty personalizationOptions}'>
						  <div class="personalizationAttributes">
						      ${eximCustomizationCodesMap[personalizationOptions]} :  ${personalizationDetails}
					     <div class="pricePersonalization"> 
						   <c:choose>
				                <c:when test='${not empty personalizePrice && not empty personalizationType && personalizationType == "PY"}'>
									<dsp:valueof value="${personalizePrice}" converter="currency"/> <bbbl:label key="lbl_exim_added_price" language ="${pageContext.request.locale.language}"/>
								</c:when>
								<c:when test='${not empty personalizePrice && not empty personalizationType && personalizationType == "CR"}'>
									<dsp:valueof value="${personalizePrice}" converter="currency"/> <c:choose>
																								 		<c:when test="${customizeTxt eq true}">
																								 			<bbbl:label key="lbl_exim_cr_added_price_customize"
																															language="${pageContext.request.locale.language}"/>
																								 		</c:when>
																								 		<c:otherwise>
																								 			<bbbl:label key="lbl_exim_cr_added_price"
																															language="${pageContext.request.locale.language}"/>
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

					<li class="prodSubInfo">
						<c:out value="${labelQuantityCapitalize}" />:
						<dsp:getvalueof var="tempCatalogId" param="commerceItem.id" />
						<c:out value="${giftWrapMap[tempCatalogId]}"/>
					</li>

					<dsp:getvalueof var="salePrice"  param="commerceItem.priceInfo.salePrice"/>
					<dsp:getvalueof var="listPrice" param="commerceItem.priceInfo.listPrice" />
					<c:choose>
						<c:when test="${salePrice gt 0.0}">
							<li class="prodPrice"><dsp:valueof value="${salePrice}" converter="currency" number="000.00" /></li>
						</c:when>
						<c:otherwise>
							<li class="prodPrice"><dsp:valueof value="${listPrice}" converter="currency" number="000.00" /></li>
						</c:otherwise>
					</c:choose>

					<dsp:getvalueof var="giftWrapEligible" param="pSKUDetailVO.giftWrapEligible"/>

					<c:if test="${giftWrapEligible}">
						<li class="smallText">
							<bbbl:label key="lbl_shipping_eligible_gift_pack" language="<c:out param='${language}'/>"/>
						</li>
					</c:if>
				</ul>
			</div>
		</dsp:oparam>
		</dsp:droplet>
	</dsp:oparam>
	</dsp:droplet>

</dsp:page>
