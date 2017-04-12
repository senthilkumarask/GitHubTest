<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:importbean bean="/atg/commerce/order/droplet/BBBCartRegistryDisplayDroplet"/>
	<dsp:importbean bean="/atg/repository/seo/CanonicalItemLink"/>
	<dsp:importbean bean="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet"/>

	<%-- Variables --%>
	<dsp:getvalueof var="skuId" param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.id"/>
	<dsp:getvalueof var="image" param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.mediumImage"/>
	<dsp:getvalueof var="skuUpc" param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.upc" />
	<dsp:getvalueof var="skuColor" param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.color" />
	<dsp:getvalueof var="skuSize"  param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.size" />
	<dsp:getvalueof var="skuName" param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.displayName" />
	<dsp:getvalueof var="storeSKU" param="commerceItemRelationship.commerceItem.auxiliaryData.catalogRef.storeSKU" />
	<dsp:getvalueof var="quantity" param="commerceItemRelationship.quantity" />
	<c:set var="customizeCTACodes">
		<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
	</c:set>
	<dsp:getvalueof var="isFromOrderDetail" param="isFromOrderDetail"/>
	<dsp:getvalueof var="isConfirmation" param="isConfirmation"/>
	<dsp:getvalueof var="commItem" param="commerceItemRelationship.commerceItem" />
	<dsp:getvalueof var="skuDetailVO" param="skuDetailVO" />
	<dsp:getvalueof var="shippingMethod" param="shipMethod" />
	<dsp:getvalueof var="shippingState" param="shippingState" />
	<dsp:getvalueof var="personalizationType" param="skuDetailVO.personalizationType" />
<%-- 	<dsp:getvalueof var="cartCommerceItem" param="cartCommerceItem" /> --%>
		<dsp:getvalueof var="referenceNumber" param="commerceItemRelationship.commerceItem.referenceNumber" />
		<dsp:getvalueof var="personalizationOptions" param="commerceItemRelationship.commerceItem.personalizationOptions" />
		<dsp:getvalueof var="personalizationOptionsDisplay" param="commerceItemRelationship.commerceItem.personalizationOptionsDisplay" />
		<dsp:getvalueof var="personalizePrice" param="commerceItemRelationship.commerceItem.personalizePrice" />
		<dsp:getvalueof var="personalizationDetails" param="commerceItemRelationship.commerceItem.personalizationDetails" />
		<dsp:getvalueof var="thumbnailImagePath" param="commerceItemRelationship.commerceItem.thumbnailImagePath" />
		<dsp:getvalueof var="fullImagePath" param="commerceItemRelationship.commerceItem.fullImagePath" />
		<dsp:droplet name="/com/bbb/common/droplet/EximCustomizationDroplet">
			<dsp:oparam name="output">
				<dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
			</dsp:oparam>
		</dsp:droplet>

	<dsp:droplet name="CanonicalItemLink">
		<dsp:param name="id" param="commerceItemRelationship.commerceItem.repositoryItem.productId" />
		<dsp:param name="itemDescriptorName" value="product" />
		<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
		</dsp:oparam>
	</dsp:droplet>
	<c:if test="${storeSKU || commItem.storeSKU}">
		<dsp:getvalueof var="finalUrl" vartype="java.lang.String" value="" />
	</c:if>

	<dsp:droplet name="BBBPriceDisplayDroplet">
		<dsp:param name="priceObject" value="${commItem}" />
		<dsp:param name="profile" bean="Profile"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="unitListPrice" param="priceInfoVO.unitListPrice"/>
		</dsp:oparam>
	</dsp:droplet>

	<%-- item --%>
	<div class="small-12 large-6 print-4 columns">
		<div class="small-12 columns no-padding-left">
			<c:if test="${not empty commItem.registryId}">
				<span>From
					<dsp:getvalueof param="order.registryMap.${commItem.registryId}" var="registratantVO"/>
					<c:if test="${commItem.registryInfo ne null}">
						${registratantVO.primaryRegistrantFirstName}
						<c:if test="${not empty registratantVO.coRegistrantFirstName}"> &amp; ${registratantVO.coRegistrantFirstName}</c:if><bbbl:label key="lbl_cart_registry_name_suffix" language="<c:out param='${language}'/>"/>
					</c:if>
				</span>
				<dsp:droplet name="BBBCartRegistryDisplayDroplet">
					<dsp:param name="registryId" value="${commItem.registryId}" />
					<dsp:param name="order" param="order" />
					<dsp:oparam name="regOutput">
						<dsp:getvalueof var="registrantEmail" param="registrantEmail"/>
						<span><dsp:valueof param="registryType"/></span>
						<span><bbbl:label key="lbl_cart_registry_text" language="<c:out param='${language}'/>"/></span>
					</dsp:oparam>
				</dsp:droplet>
			</c:if>
		</div>
		<div class="row">
			<%-- image --%>
			<div class="small-4 large-6 columns left hide-for-print">
				<div class="category-prod-img">
					<c:choose>
						<c:when test="${itemFlagoff or disableLink}">
							<c:choose>
							    <c:when test="${not empty referenceNumber && not empty thumbnailImagePath}">
              		                <img  src="${thumbnailImagePath}" alt="${skuName}&nbsp;$${unitListPrice}" title="${skuName}&nbsp;$${unitListPrice}" />
              		                    <div class="zoomin-link-wrapper">
											 <a href="#" alt="<bbbl:label key="lbl_click_larger_image" language ="${pageContext.request.locale.language}"/>" class="zoomin-link" data-reveal-id="previewImageModalPopup_${commItem.id}">
	 									       <span class="zoomin-icon"></span>
											   <span class="zoomin-label"><bbbl:label key="lbl_view_large" language ="${pageContext.request.locale.language}"/></span>
										    </a>
							        	 </div>
							            <div id="previewImageModalPopup_${commItem.id}" class="reveal-modal small previewImageModalPopup" data-options="close_on_background_click:true;close_on_esc:true;" data-reveal="">
                                              <img src="${fullImagePath}" alt="${skuName}" title="${skuName}&nbsp;$${unitListPrice}"/>
                                                 <a class="close-reveal-modal" aria-label="Close">&#215;</a>
                                        </div>
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
							<dsp:a iclass="prodImg" page="${finalUrl}?skuId=${skuId}" title="${skuName}&nbsp;$${unitListPrice}">
								<c:choose>
								    <c:when test="${not empty referenceNumber && not empty thumbnailImagePath}">
          		                        <img  src="${thumbnailImagePath}" alt="${skuName}&nbsp;$${unitListPrice}" title="${skuName}&nbsp;$${unitListPrice}" />
          		                          <div class="zoomin-link-wrapper">
												 <a href="#" alt="<bbbl:label key="lbl_click_larger_image" language ="${pageContext.request.locale.language}"/>" class="zoomin-link" data-reveal-id="previewImageModalPopup_${commItem.id}">
	   										      <span class="zoomin-icon"></span>
												  <span class="zoomin-label"><bbbl:label key="lbl_view_large" language ="${pageContext.request.locale.language}"/></span>
											  	</a>
									          </div>
								           <div id="previewImageModalPopup_${commItem.id}" class="reveal-modal small previewImageModalPopup" data-options="close_on_background_click:true;close_on_esc:true;" data-reveal="">
                                                <img src="${fullImagePath}" alt="${skuName}" title="${skuName}&nbsp;$${unitListPrice}"/>
                                                   <a class="close-reveal-modal" aria-label="Close">&#215;</a>
                                          </div>
          		                   </c:when>
									<c:when test="${empty image || 'null' == image}">
										<img class="productImage" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${skuName}&nbsp;$${unitListPrice}" title="${skuName}&nbsp;$${unitListPrice}"/>
									</c:when>
									<c:otherwise>
										<img class="productImage noImageFound" src="${scene7Path}/${image}" alt="${skuName}&nbsp;$${unitListPrice}" title="${skuName}&nbsp;$${unitListPrice}"/>
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
			<c:set var="labelColorCapitalize" value="${fn:toUpperCase(fn:substring(labelColor, 0, 1))}${fn:toLowerCase(fn:substring(labelColor, 1,fn:length(labelColor)))}" />
			<c:set var="labelSizeCapitalize" value="${fn:toUpperCase(fn:substring(labelSize, 0, 1))}${fn:toLowerCase(fn:substring(labelSize, 1,fn:length(labelSize)))}" />

			<%-- description --%>
			<div class="small-8 large-6 columns print-12 left">
				<c:choose>
					<c:when test="${itemFlagoff or disableLink}">
						<div class="product-name"><c:out value="${skuName}" escapeXml="false" /></div>
						<c:if test="${not empty skuId}">
							<div class="facet hide show-for-print">
								<strong>SKU: <dsp:valueof value="${skuId}" valueishtml="true" /></strong>
							</div>
						</c:if>
						<c:if test='${not empty skuColor}'>
							<div class="facet">
								<c:out value="${labelColorCapitalize}" />: <dsp:valueof value="${fn:toLowerCase(skuColor)}" valueishtml="true" />
							</div>
						</c:if>
						<c:if test='${not empty skuSize}'>
							<div class="facet">
								<c:out value="${labelSizeCapitalize}" />: <dsp:valueof value="${fn:toLowerCase(skuSize)}" valueishtml="true" />
							</div>
						</c:if>
					</c:when>
					<c:otherwise>
						<div class="product-name">
							<dsp:a page="${finalUrl}?skuId=${skuId}" title="${skuName}&nbsp;$${unitListPrice}">
								<c:out value="${skuName}" escapeXml="false" />
							</dsp:a>
						</div>
						<c:if test="${not empty skuId}">
							<div class="facet hide show-for-print">
								<strong>SKU: <dsp:valueof value="${skuId}" valueishtml="true" /></strong>
							</div>
						</c:if>
						<c:if test='${not empty skuColor}'>
							<div class="facet">
								<c:out value="${labelColorCapitalize}" />: <dsp:valueof value="${fn:toLowerCase(skuColor)}" valueishtml="true" />
							</div>
						</c:if>
						<c:if test='${not empty skuSize}'>
							<div class="facet">
								<c:out value="${labelSizeCapitalize}" />: <dsp:valueof value="${fn:toLowerCase(skuSize)}" valueishtml="true" />
							</div>
						</c:if>
					</c:otherwise>
				</c:choose>
				<dsp:getvalueof var="isVdcSku" value="${skuDetailVO.vdcSku}"/>
						<dsp:droplet name="/com/bbb/commerce/browse/VDCShippingMessagingDroplet">
									<dsp:param name="skuId" value="${skuDetailVO.skuId}"/>
									<dsp:param name="siteId" value="${currentSiteId}"/>
									<dsp:param name="shippingMethodCode" value="${shippingMethod}"/>
									<dsp:param name="requireMsgInDates" value="true"/>
									<dsp:param name="isVdcSku" value="${isVdcSku}"/>
									<dsp:param name="shippingState" value="${shippingState}"/>
									<dsp:param name="isFromOrderDetail" value="${isFromOrderDetail}"/>
									<dsp:param name="order" param="order" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="vdcDelTime" param="vdcShipMsg"/>
										<dsp:getvalueof var="offsetDateVDC" param="offsetDateVDC"/>
										 <c:set var="vdcOffsetFlag">
											<bbbc:config key="vdcOffsetFlag" configName="FlagDrivenFunctions" />
										</c:set>
									</dsp:oparam>
									<dsp:oparam name="error">
									</dsp:oparam>
								</dsp:droplet>
								<c:if test="${not empty vdcDelTime }">
									<div class="facet">Expected delivery <br>${vdcDelTime}</div>
								</c:if>
								<c:if test="${vdcOffsetFlag && not empty offsetDateVDC && !isConfirmation}">
									<c:if test="${ !skuDetailVO.ltlItem && skuDetailVO.vdcSku}">
										<jsp:useBean id="placeHolderMapServiceLevel" class="java.util.HashMap" scope="request" />
										<jsp:useBean id="placeHolderMapVdcLearnMore" class="java.util.HashMap" scope="request" />
										<c:set target="${placeHolderMapVdcLearnMore}" property="shipMethod" value="${shippingMethod }" />
										<c:set target="${placeHolderMapVdcLearnMore}" property="skuId" value="${skuId}" />
										<c:set target="${placeHolderMapServiceLevel}" property="actualOffSetDate" value="${offsetDateVDC}" />
											<div class="facet highlightRed vdcOffsetMsg bold">
												<br>
												<bbbt:textArea key="txt_vdc_offset_msg" placeHolderMap="${placeHolderMapServiceLevel}" language="${pageContext.request.locale.language}" />
												<c:set var="lbl_offset_learn_more_link" scope="request"><bbbl:label key="lbl_offset_learn_more" placeHolderMap="${placeHolderMapVdcLearnMore}" language="<c:out   param='${language}'/>"/></c:set>
												<c:set var="lbl_offset_learn_moreTBS" value="${fn:replace(lbl_offset_learn_more_link, '/store/includes','/tbs/includes')}" />
												<dsp:valueof value="${lbl_offset_learn_moreTBS}" valueishtml="true"/>
											</div>
									</c:if>
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
				<dsp:getvalueof var="orderSiteId" param="order.siteId" />
				<div class="facet">
				<c:if test="${(fn:contains(orderSiteId,'TBS')) && not empty commItem.autoWaiveClassification}">
					<bbbl:label key="lbl_autowaive_item_code" language="<c:out param='${language}'/>"/>${commItem.autoWaiveClassification}
				</c:if>
				</div>
			</div>
		</div>
	</div>

</dsp:page>
