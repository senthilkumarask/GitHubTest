<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet"/>
	<dsp:importbean bean="/atg/repository/seo/CanonicalItemLink"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" var="Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
 
	<c:set var="language" value="${pageContext.request.locale.language}" scope="page"/>	
	<dsp:getvalueof var="commItem" param="commItem"/>
	<dsp:getvalueof var="skuId" param="commItem.catalogRefId"/>
	<dsp:getvalueof var="image" param="SKUDetailVO.skuImages.mediumImage"/>
	<dsp:getvalueof var="giftWrapEligible" param="SKUDetailVO.giftWrapEligible"/>
	<dsp:getvalueof var="skuName" param="SKUDetailVO.displayName"/>
	<dsp:getvalueof var="index" param="cisiIndex" />
	<dsp:getvalueof var="quantity" param="quantity" />
	<dsp:getvalueof var="listPrice" param="commItem.priceInfo.listPrice" />
	<dsp:getvalueof var="salePrice" param="commItem.priceInfo.salePrice" />
	<dsp:getvalueof var="unitPrice" param="commItem.priceInfo.listPrice"/>	
	<dsp:getvalueof var="cisiShipGroupName" param="cisiShipGroupName" />
	<dsp:getvalueof var="skuColor" param="SKUDetailVO.color" />
	<dsp:getvalueof var="skuSize"  param="SKUDetailVO.size" />
	<dsp:getvalueof var="productVO"  param="productVO" />
	<dsp:getvalueof var="vdcSku"  param="SKUDetailVO.vdcSku" />
	<dsp:getvalueof var="ltlItem"  param="SKUDetailVO.ltlItem" />
	
	<dsp:getvalueof var="personalizationType" param="SKUDetailVO.personalizationType" />

	<dsp:getvalueof var="referenceNumber" param="commItem.referenceNumber" />
	<dsp:getvalueof var="personalizationOptions" param="commItem.personalizationOptions" />
	<dsp:getvalueof var="personalizationOptionsDisplay" param="commItem.personalizationOptionsDisplay" />
	<dsp:getvalueof var="personalizePrice" param="commItem.personalizePrice" />
	<dsp:getvalueof var="personalizationDetails" param="commItem.personalizationDetails" />
	<dsp:getvalueof var="fullImagePath" param="commItem.fullImagePath" />
	<dsp:droplet name="/com/bbb/common/droplet/EximCustomizationDroplet">
		<dsp:oparam name="output">
			<dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
		</dsp:oparam>
	</dsp:droplet>
	<c:set var="customizeCTACodes">
		<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
	</c:set>
	<c:set var="itemFlagoff" value="${false}"/>
	<c:set var="disableLink" value="${false}"/>
	
	<dsp:droplet name="ForEach">
	<dsp:param param="productVO.rollupAttributes" name="array" />
	<dsp:oparam name="output">
		<dsp:getvalueof var="menu" param="key"/>${menu}
		<c:if test="${menu eq 'FINISH'}">
			<c:set var="rollupAttributesFinish" value="true" />
		</c:if>
	</dsp:oparam>
	</dsp:droplet>

	<dsp:droplet name="BBBPriceDisplayDroplet">
	<dsp:param name="priceObject" param="commItem" />
	<dsp:param name="profile" bean="Profile"/>
	<dsp:oparam name="output">
		<dsp:getvalueof var="unitListPrice" param="priceInfoVO.unitListPrice"/>
	 </dsp:oparam>
	</dsp:droplet>
	
	<dsp:getvalueof id="priceMessageVO" param="commItem.priceMessageVO" />

	<c:if test="${not empty priceMessageVO}">
		<c:set var="itemFlagoff" value="${priceMessageVO.flagOff}"/>
		<c:set var="disableLink" value="${priceMessageVO.disableLink}"/>
	</c:if>
														
	<dsp:droplet name="CanonicalItemLink">
	<dsp:param name="id" param="commItem.auxiliaryData.productId" />
	<dsp:param name="itemDescriptorName" value="product" />
	<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
	<dsp:oparam name="output">
		<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
		<c:if test="${itemFlagOff or disableLink}">
			<c:set var="finalUrl" value="#"/>
		</c:if>
		
		<div class="row">
			<div class="small-4 large-6 columns">
				<%-- image --%>
				<div class="category-prod-img">
					<c:choose>
						<c:when test="${itemFlagoff or disableLink or (ltlItem and vdcSku)}">
							<c:choose>
							    <c:when test="${not empty referenceNumber && not empty fullImagePath}">
                      		        <img  src="${fullImagePath}" alt="${skuName}&nbsp;$${unitListPrice}" title="${skuName}&nbsp;$${unitListPrice}" />
                 		        </c:when>
								<c:when test="${empty image || 'null' == image}">
									<img src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${skuName}&nbsp;$${unitListPrice}" title="${skuName}&nbsp;$${unitListPrice}" />
								</c:when>
								<c:otherwise>
									<img src="${scene7Path}/${image}" alt="${skuName}&nbsp;$${unitListPrice}" title="${skuName}&nbsp;$${unitListPrice}" />
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<dsp:a iclass="prodImg padLeft_10 block fl" page="${finalUrl}?skuId=${skuId}" title="${skuName}&nbsp;$${unitListPrice}">
								<c:choose>
								    <c:when test="${not empty referenceNumber && not empty fullImagePath}">
                   		                <img  src="${fullImagePath}" alt="${skuName}&nbsp;$${unitListPrice}" title="${skuName}&nbsp;$${unitListPrice}" />
              		                </c:when>
									<c:when test="${empty image || 'null' == image}">
										<img class="fl productImage" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${skuName}&nbsp;$${unitListPrice}" title="${skuName}&nbsp;$${unitListPrice}" height="146" width="146" />
									</c:when>
									<c:otherwise>
										<img class="fl productImage noImageFound" src="${scene7Path}/${image}" alt="${skuName}&nbsp;$${unitListPrice}" title="${skuName}&nbsp;$${unitListPrice}" height="146" width="146" />
									</c:otherwise>
								</c:choose>
							</dsp:a>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			
			<%-- capitalize facet labels --%>
			<c:set var="labelColor"><bbbl:label key='lbl_item_color' language='${pageContext.request.locale.language}'/></c:set>
			<c:set var="labelSize"><bbbl:label key='lbl_item_size' language='${pageContext.request.locale.language}'/></c:set>
			<c:set var="labelFinish"><bbbl:label key="lbl_item_finish" language="${pageContext.request.locale.language}"/></c:set>
			<c:set var="labelColorCapitalize" value="${fn:toUpperCase(fn:substring(labelColor, 0, 1))}${fn:toLowerCase(fn:substring(labelColor, 1,fn:length(labelColor)))}" />
			<c:set var="labelSizeCapitalize" value="${fn:toUpperCase(fn:substring(labelSize, 0, 1))}${fn:toLowerCase(fn:substring(labelSize, 1,fn:length(labelSize)))}" />
			<c:set var="labelFinishCapitalize" value="${fn:toUpperCase(fn:substring(labelFinish, 0, 1))}${fn:toLowerCase(fn:substring(labelFinish, 1,fn:length(labelFinish)))}" />
				
			<%-- description --%>
			<div class="small-8 large-6 columns">
				<c:choose>
					<c:when test="${itemFlagoff or disableLink or (ltlItem and vdcSku)}">
						<div class="product-name"><c:out value="${skuName}" escapeXml="false" /></div>
						
					</c:when>
					<c:otherwise>
						<div class="product-name">
							<dsp:a page="${finalUrl}?skuId=${skuId}" title="${skuName}&nbsp;$${unitListPrice}">
								<c:out value="${skuName}" escapeXml="false" />
							</dsp:a>
						</div>
					</c:otherwise>
				</c:choose>
				
				<c:if test='${not empty skuColor}'>
					<c:choose>
						<c:when test="${rollupAttributesFinish == 'true'}">
							<div class="facet">
								<c:out value="${labelFinishCapitalize}" />: <dsp:valueof value="${fn:toLowerCase(skuColor)}" valueishtml="true" />
							</div>
						</c:when>
						<c:otherwise>
							<div class="facet">
								<c:out value="${labelColorCapitalize}" />: <dsp:valueof value="${fn:toLowerCase(skuColor)}" valueishtml="true" />
							</div>
						</c:otherwise>
					</c:choose>
				</c:if>
				<c:if test='${not empty skuSize}'>
					<div class="facet">
						<c:out value="${labelSizeCapitalize}" />: <dsp:valueof value="${fn:toLowerCase(skuSize)}" valueishtml="true" />
					</div>
				</c:if>
				<c:choose>
					<c:when test="${not empty personalizationOptions && fn:contains(customizeCTACodes, personalizationOptions)}">
						<c:set var="customizeTxt" value="true"/>
					</c:when>
					<c:otherwise>
						<c:set var="customizeTxt" value="false"/>
					</c:otherwise>
				</c:choose>
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
			
				<div class="quantity">
					<bbbl:label key="lbl_orderitems_quantity" language="${pageContext.request.locale.language}" /> ${quantity}
				</div>
				
				<div class="quantity">
					<c:choose>
	                   <c:when test="${salePrice gt 0.0}">
	                       <dsp:valueof value="${salePrice}" converter="currency"/>
	                   </c:when>
	                   <c:otherwise>
	                       <dsp:valueof value="${listPrice}" converter="currency"/>
	                   </c:otherwise>
	               </c:choose>
	      		</div>
	      		<div class="facet">
	      		<c:if test="${(commItem.commerceItemClassType eq 'tbsCommerceItem' || commItem.commerceItemClassType eq 'default')
								&& commItem.autoWaiveClassification ne null}">
					<bbbl:label key="lbl_autowaive_item_code" language="<c:out param='${language}'/>"/>${commItem.autoWaiveClassification}
				</c:if>
				</div>
			</div>
		</div>
	</dsp:oparam>
	</dsp:droplet>


</dsp:page>