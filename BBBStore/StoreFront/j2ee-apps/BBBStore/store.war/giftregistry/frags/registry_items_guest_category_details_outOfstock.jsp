<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/GuestRegistryItemsDisplayDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/IsProductSKUShippingDroplet"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/POBoxValidateDroplet" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<dsp:importbean bean="/com/bbb/certona/CertonaConfig" />
	<c:set var="BedBathUSSite" scope="request">
		<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BuyBuyBabySite" scope="request">
		<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BedBathCanadaSite" scope="request">
		<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:choose>
		<c:when test="${currentSiteId eq BedBathUSSite}">
			<c:set var="CertonaOn" scope="request"><tpsw:switch tagName="CertonaTag_us"/></c:set>
		</c:when>
		<c:when test="${currentSiteId eq BuyBuyBabySite}">
			<c:set var="CertonaOn" scope="request"><tpsw:switch tagName="CertonaTag_baby"/></c:set>
		</c:when>
		<c:otherwise>
			<c:set var="CertonaOn" scope="request"><tpsw:switch tagName="CertonaTag_ca"/></c:set>
		</c:otherwise>
	</c:choose>	
	<c:set var="shippingAttributesList">
		<bbbc:config key="shippingAttributesList" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="customizeCTACodes">
		<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
	</c:set>
	<c:set var="BuyBuyBabySite"><bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" /></c:set>
	<c:set var="enableKatoriFlag"><bbbc:config key="enableKatori" configName="EXIMKeys" /></c:set>
	<c:set var="lblProductQuickView"><bbbl:label key='lbl_mng_regitem_quick_view' language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="lblOfThe"><bbbl:label key="lbl_accessibility_of_the" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="BedBathCanadaSite">
			<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
		</c:set>
	<dsp:droplet name="GuestRegistryItemsDisplayDroplet">
			<dsp:param name="isThirdAjaxCall" value="true"/>
			<dsp:param name="registryId" param="registryId"/>
			<dsp:param name="inventoryCallEnabled" param="inventoryCallEnabled"/>
			<dsp:oparam name="output">
					<dsp:getvalueof var="categoryBuckets" param="categoryBuckets" />
					<dsp:getvalueof var="emptyOutOfStockListFlag" param="emptyOutOfStockListFlag" />
					<dsp:getvalueof var="omnitureList" param="omnitureList" />
					<dsp:getvalueof var="itemList" param="itemList" />
			</dsp:oparam>
		</dsp:droplet>
	<jsp:useBean id="placeHolderMapServiceLevel" class="java.util.HashMap" scope="request"/>
	<%-- Droplet Placeholder --%>
	<dsp:getvalueof var="registryId" param="registryId" />
	<dsp:getvalueof var="view" param="view"/>
	<dsp:getvalueof var="eventType" param="eventType" />
	<dsp:getvalueof var="eventTypeCode" param="eventTypeCode" />
	<dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
	<dsp:getvalueof var="isGiftGiver" param="isGiftGiver" />
	<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO.registryType.registryTypeDesc" var="sessionRegistryType"/>
	<c:if test="${currentSiteId eq 'BedBathUS'}">
	<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_us"/></c:set>
	</c:if>
	<c:if test="${currentSiteId eq 'BuyBuyBaby'}">
	  <c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_baby"/></c:set>
	</c:if>
	<c:if test="${currentSiteId eq 'BedBathCanada'}">
	<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_ca"/></c:set>
	</c:if>
	<c:set var="button_active" value="button_cart button_active"/>
	<c:set var="scene7Path">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
	<c:if test="${eventType eq 'Baby' && currentSiteId ne BuyBuyBabySite}">
		<c:set var="button_active" value=""/>
	</c:if>
	<c:set var="BedBathCanadaSite"><bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" /></c:set>
	<c:set var="enableLTLRegForSite">
				<bbbc:config key="enableLTLRegForSite" configName="FlagDrivenFunctions" />
			</c:set>

	<dsp:getvalueof bean="SessionBean.ownerRegAddPOBOX" var="isPOBoxAddress"/>

<script>
			var omniString = '${omnitureList}';
            var registryId = '${registryId}';
			var eventType = '${eventType}';
			</script>
			
	<c:if test="${!emptyOutOfStockListFlag}">
		<p class="alpha space inStoreOnly" >
			<span class="icon-fallback-text">
				<span class="icon-store-icon" aria-hidden="true"></span>
				<span class="icon-text"><bbbl:label key='lbl_mng_regitem_pickupinstore' language='${pageContext.request.locale.language}' /></span>
			</span>
			<bbbt:textArea key="txt_mng_regitem_items_unavail" language="${pageContext.request.locale.language}" />
		</p>
		<dsp:droplet name="ForEach">
			<dsp:param name="array" value="${categoryBuckets}" />
			<dsp:param name="elementName" value="notInStockCategoryVO"/>
			<dsp:oparam name="output">
			<dsp:getvalueof var="sizeIE" param="size"/>
			<dsp:getvalueof var="countIE" param="count"/>
			<div class="accordionReg7 accordionReg accordion ui-accordion ui-widget ui-helper-reset ui-accordion-icons" id="accordionReg7">
				<dsp:getvalueof var="bucketName" param="notInStockCategoryVO.displayName" idtype="java.lang.String" />
				<dsp:getvalueof var="registryItemList" param="notInStockCategoryVO.registryItemList"/>
				<c:set var="showC1" value="true"/>
				<c:if test="${showC1}">
					<h2 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-all"><span class="ui-icon ui-icon-triangle-1-e"></span><span class="accordionTitle block clearfix giftTitle"><a href="#" class="fl accordionLink">${fn:toLowerCase(bucketName)}&nbsp;(${fn:length(registryItemList)})</a></span></h2>
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="notInStockCategoryVO.registryItemList" />
						<dsp:param name="elementName" value="regItem" />
                        <dsp:oparam name="outputStart">
                            <div class="accordionDiv ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom">
                                <ul class="clearfix productDetailList giftViewProduct">
                        </dsp:oparam>
						<dsp:oparam name="output">
							<dsp:getvalueof var="skuID" vartype="java.lang.String"  param="regItem.sku" />
							 <dsp:getvalueof var="productId" param="regItem.sKUDetailVO.parentProdId"/>
							
									<li class="grid_12 alpha clearfix productRow btngiftViewTopMargin giftViewProductChangeStoreWrapper addToCartGiftRegWrapper<c:if test="${inCartFlagSKU}"> inCartContainer</c:if>">
									
										<input type="hidden" name="bts" value="false" class="addToCartSubmitData bts" data-change-store-submit="bts" />
						                 <input type="hidden" name="storeId" value="" data-change-store-storeid="storeId" class="addToCartSubmitData" />
						                 <dsp:getvalueof var="prdDisplayShipMsg" param="regItem.sKUDetailVO.displayShipMsg"/>
										 <dsp:getvalueof var="ltlItemFlag" param="regItem.sKUDetailVO.ltlItem" />
										 <dsp:getvalueof var="ltlShipMethod" param="regItem.ltlDeliveryServices" />
										 <dsp:getvalueof var="ltlShipMethodDesc" param="regItem.ltlShipMethodDesc"/>
										<dsp:getvalueof var="ltlAssemblySelected" param="regItem.assemblySelected"/>
										<dsp:getvalueof var="shipMethodUnsupported" param="regItem.shipMethodUnsupported"/>
										<c:if test="${ltlItemFlag && isPOBoxAddress}">
												<div class="ltlMessage"><bbbt:textArea key="txt_ltl_shipping_not_available" language ="${pageContext.request.locale.language}"/></div>
										</c:if>
										<dsp:getvalueof var="notifyRegistrantMsgTyp" param="regItem.displayNotifyRegistrantMsg"/>										
										<c:if test="${not empty notifyRegistrantMsgTyp}">
											<div class="notifyRegistrantMessage"> <bbbl:label key="notifyRegMsg_Status${notifyRegistrantMsgTyp}" language="${pageContext.request.locale.language}" /> </div>
										</c:if>
										<c:if test="${ltlItemFlag && (ltlShipMethod eq null || ltlShipMethod eq '' || shipMethodUnsupported)}">
													<div class="ltlMessage"><bbbl:label key='lbl_rlp_ltl_registry_message' language="${pageContext.request.locale.language}" /></div>
										</c:if>
										
										<c:if test="${ltlItemFlag && (ltlShipMethod ne null && ltlShipMethod ne '')}">
											<c:set var="ltlOpt" value="&sopt=${ltlShipMethod}"></c:set>
										</c:if>
										
										<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
											<dsp:param name="id" value="${productId}"/>
											<dsp:param name="itemDescriptorName" value="product" />
											<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
											</dsp:oparam>
										</dsp:droplet>
											
										<c:set var="productName">
											<dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/>
										</c:set>
										<dsp:getvalueof var="personalizedImageUrls" param="regItem.personalizedImageUrls"/>
										<dsp:getvalueof var="personalizedImageUrlThumbs" param="regItem.personalizedImageUrlThumbs"/>
										<dsp:getvalueof var="personalisedCode" param="regItem.personalisedCode"/>
										<dsp:getvalueof var="personalizationOptionsDisplay" param="regItem.personalizationOptionsDisplay"/>
										<dsp:getvalueof var="customizedPrice" vartype="java.lang.Double" param="regItem.customizedDoublePrice"/>
										<dsp:getvalueof var="personlisedPrice" vartype="java.lang.Double" param="regItem.personlisedPrice"/>
										<dsp:getvalueof var="refNum" param="regItem.refNum"/>
										<dsp:getvalueof var="customizationDetails" param="regItem.customizationDetails"/>
										<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.mediumImage"/>
										<c:set var="customizedPrice1"><dsp:valueof value="${customizedPrice}" converter="currency"/></c:set>
	                                    <c:set var="personlisedPrice1"><dsp:valueof value="${personlisedPrice}" converter="currency"/></c:set>
										<dsp:getvalueof var="skuDetailVO" param="regItem.sKUDetailVO"/>
										<dsp:getvalueof var="color" param="regItem.sKUDetailVO.color"/>
										<dsp:getvalueof var="size" param="regItem.sKUDetailVO.size"/>
										<dsp:getvalueof var="personalizationType" param="regItem.sKUDetailVO.personalizationType" />
										<dsp:getvalueof var="upc" param="regItem.sKUDetailVO.upc"/>
										<dsp:getvalueof var="active" param="regItem.sKUDetailVO.activeFlag"/>
										<dsp:getvalueof var="inCartFlagSKU" param="regItem.sKUDetailVO.inCartFlag"/>
										<dsp:getvalueof param="regItem.qtyRequested" var="requestedQuantity" />
										<dsp:getvalueof param="regItem.qtyPurchased" var="purchasedQuantity" />
										<dsp:getvalueof param="regItem.sKUDetailVO.intlRestricted" var="isIntlRestricted" />
										<dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>
										<dsp:getvalueof var="userRegCount" value="${fn:length(sessionMapValues.userRegistriesList)}"/>
										<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO.registryId" var="sessionRegId"/>
										<dsp:getvalueof var="bopusAllowed" param="regItem.sKUDetailVO.bopusAllowed"/>
										
										<div class="productImage  grid_2 alpha">
										<c:choose>
											<c:when test="${active}">
											<c:choose>
												<c:when test="${ltlItemFlag}">
												<span <c:if test="${not empty refNum}"> " oncontextmenu="return false;" </c:if> class="lnkQVPopup" data-pdp-qv-url="pdp_quick_view.jsp" data-skuId="${skuID}" data-disableItemDetails="true" data-registryId="${registryId}" data-sopt="${ltlShipMethod}" data-productId="${productId}" 
												data-upc="${upc}" data-color="${color}" data-size="${size}" data-qtyPurchased="${purchasedQuantity}" data-qtyRequested="${requestedQuantity}" data-bopusAllowed="${bopusAllowed}" data-isTransient="${isTransient}" data-emptyOutOfStockListFlag="${emptyOutOfStockListFlag}"
														data-userRegCount="${userRegCount}"  data-eventType="${eventType}" data-sessionRegId="${sessionRegId}" data-isIntlRestricted="${isIntlRestricted}" data-isInternationalCustomer="${isInternationalCustomer}" data-enableLTLReg="${enableLTLReg}" data-regAddress="${regAddress}"
														  data-refNum="${refNum}"  title="<c:out value='${productName}'/>" <c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
												data-customizedPrice="${customizedPrice1}" data-customizationDetails="${customizationDetails}" data-color="${color}" data-size="${size}" data-personalizedImageUrls="${personalizedImageUrls}"
												data-personlisedPrice="${personlisedPrice1}" data-personalizedImageUrlThumbs="${personalizedImageUrlThumbs}" data-personalizationType= "${personalizationType}"</c:if>>
													<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.mediumImage" />
													<c:choose>
                                             			<c:when test="${not empty refNum}">
                                             				<c:choose>
                                             					<c:when test="${not empty personalizedImageUrlThumbs}">
                                             						<img src="${personalizedImageUrlThumbs}" height="96" width="96" alt="<c:out value='${productName}'/>" class="prodImage" onerror="this.src = '${imagePath}/_assets/global/images/no_image_available.jpg';">
                                             					</c:when>
                                             					<c:when test="${not empty imageURL}">                                                       			 
                                                            		<img src="${imagePath}/_assets/global/images/blank.gif" data-original="${scene7Path}/${imageURL}" alt="<c:out value='${productName}'/>" height="96" width="96" class="prodImage noImageFound loadingGIF" />
                                                        		</c:when>
                                             					<c:otherwise>
                                             						<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="96" width="96" alt="<c:out value='${productName}'/>" class="prodImage" />                                             						
                                             					</c:otherwise>
                                             				</c:choose>
                                             				
                                             				
                                             			</c:when>
                                             			<c:otherwise>
                                             				<c:choose>
                                                       			 <c:when test="${empty imageURL}">                                                       			 
                                                            		<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="96" width="96" alt="<c:out value='${productName}'/>" class="prodImage" />
                                                        		</c:when>
                                                        		<c:otherwise>
                                                            		<img src="${imagePath}/_assets/global/images/blank.gif" data-original="${scene7Path}/${imageURL}" alt="<c:out value='${productName}'/>" height="96" width="96" class="prodImage noImageFound loadingGIF" />
                                                        		</c:otherwise>
                                                 			</c:choose>
                                             			</c:otherwise>
                                             		
                                             		</c:choose>
												</span>
												</c:when>
												<c:otherwise>
												<a <c:if test="${not empty refNum}"> " oncontextmenu="return false;" </c:if> href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}${ltlOpt}"
												class="lnkQVPopup" data-pdp-qv-url="pdp_quick_view.jsp" data-skuId="${skuID}" data-disableItemDetails="true" data-registryId="${registryId}" data-sopt="${ltlShipMethod}" data-productId="${productId}" data-refNum="${refNum}" 
												data-upc="${upc}" data-color="${color}" data-size="${size}" data-qtyPurchased="${purchasedQuantity}" data-qtyRequested="${requestedQuantity}" data-bopusAllowed="${bopusAllowed}" data-isTransient="${isTransient}" data-emptyOutOfStockListFlag="${emptyOutOfStockListFlag}"
														data-userRegCount="${userRegCount}"  data-eventType="${eventType}" data-sessionRegId="${sessionRegId}" data-isIntlRestricted="${isIntlRestricted}" data-isInternationalCustomer="${isInternationalCustomer}" data-enableLTLReg="${enableLTLReg}" data-regAddress="${regAddress}"
														   title="<c:out value='${productName}'/>" <c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
															data-customizedPrice="${customizedPrice1}" data-customizationDetails="${customizationDetails}" data-color="${color}" data-size="${size}" data-personalizedImageUrls="${personalizedImageUrls}"
															data-personlisedPrice="${personlisedPrice1}" data-personalizedImageUrlThumbs="${personalizedImageUrlThumbs}" data-personalizationType= "${personalizationType}"</c:if>>
													<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.mediumImage" />
													<c:choose>
                                             			<c:when test="${not empty refNum}">
                                             				<c:choose>
                                             					<c:when test="${not empty personalizedImageUrlThumbs}">
                                             						<img src="${personalizedImageUrlThumbs}" height="96" width="96" alt="<c:out value='${productName}'/>" class="prodImage" onerror="this.src = '${imagePath}/_assets/global/images/no_image_available.jpg';">
                                             					</c:when>
                                             					<c:when test="${not empty imageURL}">                                                       			 
                                                            		<img src="${imagePath}/_assets/global/images/blank.gif" data-original="${scene7Path}/${imageURL}" alt="<c:out value='${productName}'/>" height="96" width="96" class="prodImage noImageFound loadingGIF" />
                                                        		</c:when>
                                             					<c:otherwise>
                                             						<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="96" width="96" alt="<c:out value='${productName}'/>" class="prodImage" />                                             						
                                             					</c:otherwise>
                                             				</c:choose>
                                             				
                                             				
                                             			</c:when>
                                             			<c:otherwise>
                                             				<c:choose>
                                                       			 <c:when test="${empty imageURL}">                                                       			 
                                                            		<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="96" width="96" alt="<c:out value='${productName}'/>" class="prodImage" />
                                                        		</c:when>
                                                        		<c:otherwise>
                                                            		<img src="${imagePath}/_assets/global/images/blank.gif" data-original="${scene7Path}/${imageURL}" alt="<c:out value='${productName}'/>" height="96" width="96" class="prodImage noImageFound loadingGIF" />
                                                        		</c:otherwise>
                                                 			</c:choose>
                                             			</c:otherwise>
                                             		
                                             		</c:choose>
												</a>
												</c:otherwise>
											</c:choose>
											</c:when>
											<c:otherwise>
												<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.mediumImage" />
													<c:choose>
														<c:when test="${empty imageURL}">
															<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="116" width="116" alt="<c:out value='${productName}'/>" class="prodImage" />
														</c:when>
														<c:otherwise>
															<img src="${imagePath}/_assets/global/images/blank.gif" data-original="${scene7Path}/${imageURL}" alt="<c:out value='${productName}'/>" height="116" width="116" class="prodImage noImageFound loadingGIF" />
														</c:otherwise>
													</c:choose>
											</c:otherwise>
										</c:choose>
										</div>
										<div class="productContainer grid_10 omega">
											<div class="productTab productContent clearfix">
												<div class="productName grid_2">
													<c:choose>
														<c:when test="${active && !(isGiftGiver && ltlItemFlag)}">
														<span class="blueName">
															<a <c:if test="${not empty refNum}"> " oncontextmenu="return false;" </c:if> href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}${ltlOpt}"
															class="lnkQVPopup prodTitle" data-pdp-qv-url="pdp_quick_view.jsp" data-skuId="${skuID}" data-disableItemDetails="true" data-registryId="${registryId}" data-sopt="${ltlShipMethod}" data-productId="${productId}" data-refNum="${refNum}" 
															data-upc="${upc}" data-color="${color}" data-size="${size}" data-qtyPurchased="${purchasedQuantity}" data-qtyRequested="${requestedQuantity}" data-bopusAllowed="${bopusAllowed}" data-isTransient="${isTransient}" data-emptyOutOfStockListFlag="${emptyOutOfStockListFlag}"
														data-userRegCount="${userRegCount}"  data-eventType="${eventType}" data-sessionRegId="${sessionRegId}" data-isIntlRestricted="${isIntlRestricted}" data-isInternationalCustomer="${isInternationalCustomer}" data-enableLTLReg="${enableLTLReg}" data-regAddress="${regAddress}"
														   title="<c:out value='${productName}'/>" <c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
															data-customizedPrice="${customizedPrice1}" data-customizationDetails="${customizationDetails}" data-color="${color}" data-size="${size}" data-personalizedImageUrls="${personalizedImageUrls}"
															data-personlisedPrice="${personlisedPrice1}" data-personalizedImageUrlThumbs="${personalizedImageUrlThumbs}" data-personalizationType= "${personalizationType}"</c:if>>
																<dsp:getvalueof var="displayName" param="regItem.sKUDetailVO.displayName" />
																${productName}
															</a>
														</span><br/>
														</c:when>
														<c:otherwise>
															<span class="blueName prodTitle">
																<dsp:getvalueof var="displayName" param="regItem.sKUDetailVO.displayName" />
																<dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/>
															</span><br/>
														</c:otherwise>
													</c:choose>

                                                    			<dl class="productAttributes">
														<c:if test='${not empty color}'>
															<dt><bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/>:</dt>
															<dd><dsp:valueof value="${color}" valueishtml="true" /></dd>
														</c:if>

														<c:if test='${not empty size}'>
															<dt><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/>:</dt>
															<dd><dsp:valueof value="${size}" valueishtml="true" /></dd>
														</c:if>

														<c:if test="${upc ne null}">
															<dt><bbbl:label key='lbl_mng_regitem_upc' language="${pageContext.request.locale.language}" /></dt>
															<dd>${upc}</dd>
														</c:if>
														<c:if test="${not empty personalisedCode}">
															<dt>${eximCustomizationCodesMap[personalisedCode]}</dt>
															<dd>: ${customizationDetails}</dd>
															<span class="personalizationAttr katoriPrice">
															<div class="eximRegMsg">
															<c:choose>
															<c:when test="${personalizationType == 'PB'}">
															<bbbl:label key='lbl_PB_Fee_detail' language="${pageContext.request.locale.language}" />
															</c:when>
															<c:when test="${personalizationType == 'PY'}">
															 <dsp:valueof value="${customizedPrice1}"/>&nbsp;<bbbl:label key='lbl_exim_added_price' language="${pageContext.request.locale.language}" />
															</c:when>
															<c:when test="${personalizationType == 'CR'}">
															 <dsp:include page="/global/gadgets/formattedPrice.jsp">												
												               <dsp:param name="price" value="${customizedPrice}"/>
												        	</dsp:include>
												        	<c:choose>
																<c:when test="${not empty personalisedCode && fn:contains(customizeCTACodes, personalisedCode)}">
																	<bbbl:label key="lbl_exim_cr_added_price_customize" language ="${pageContext.request.locale.language}"/>
																</c:when>
																<c:otherwise>
																	<bbbl:label key="lbl_exim_cr_added_price" language ="${pageContext.request.locale.language}"/>
																</c:otherwise>
															</c:choose>
															</c:when>
														</c:choose>
														</div></span>
														</c:if>
													</dl>

                                               				<c:if test="${active}">
	                                               				<span class="quickViewLink">
	                                               				<c:choose>
																	<c:when test="${ltlItemFlag}">
																	<span <c:if test="${not empty refNum}"> " oncontextmenu="return false;" </c:if> class="lnkQVPopup prodTitle" role="link" aria-label="${lblProductQuickView} ${lblOfThe} ${productName}"
																		data-pdp-qv-url="pdp_quick_view.jsp" data-skuId="${skuID}" data-disableItemDetails="true" data-registryId="${registryId}" data-productId="${productId}" data-sopt="${ltlShipMethod}" 
																		data-upc="${upc}" data-color="${color}" data-size="${size}" data-qtyPurchased="${purchasedQuantity}" data-qtyRequested="${requestedQuantity}" data-bopusAllowed="${bopusAllowed}" data-isTransient="${isTransient}" data-emptyOutOfStockListFlag="${emptyOutOfStockListFlag}"
														data-userRegCount="${userRegCount}"  data-eventType="${eventType}" data-sessionRegId="${sessionRegId}" data-isIntlRestricted="${isIntlRestricted}" data-isInternationalCustomer="${isInternationalCustomer}" data-enableLTLReg="${enableLTLReg}" data-regAddress="${regAddress}"
														  data-refNum="${refNum}"  title="<c:out value='${productName}'/>" <c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
																		data-customizedPrice="${customizedPrice1}" data-customizationDetails="${customizationDetails}" data-color="${color}" data-size="${size}" data-personalizedImageUrls="${personalizedImageUrls}"
																		data-personlisedPrice="${personlisedPrice1}" data-personalizedImageUrlThumbs="${personalizedImageUrlThumbs}" data-personalizationType= "${personalizationType}"</c:if>>
																		<span class="icon-fallback-text">
																			<span class="icon-add" aria-hidden="true"></span>
																			<span class="icon-text" aria-hidden="true"><bbbl:label key='lbl_mng_regitem_quick_view' language="${pageContext.request.locale.language}" /></span>
																		</span>
																		<bbbl:label key='lbl_mng_regitem_quick_view' language="${pageContext.request.locale.language}" />
																	</span>
																	</c:when>
																	<c:otherwise>
				                                                    			<a <c:if test="${not empty refNum}"> " oncontextmenu="return false;" </c:if> href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}"
																		class="lnkQVPopup prodTitle" role="link" aria-label="${lblProductQuickView} ${lblOfThe} ${productName}"
																		data-pdp-qv-url="pdp_quick_view.jsp" data-skuId="${skuID}" data-disableItemDetails="true" data-registryId="${registryId}" data-productId="${productId}" data-sopt="${ltlShipMethod}" data-refNum="${refNum}" 
																		data-upc="${upc}" data-color="${color}" data-size="${size}" data-qtyPurchased="${purchasedQuantity}" data-qtyRequested="${requestedQuantity}" data-bopusAllowed="${bopusAllowed}" data-isTransient="${isTransient}" data-emptyOutOfStockListFlag="${emptyOutOfStockListFlag}"
														data-userRegCount="${userRegCount}"  data-eventType="${eventType}" data-sessionRegId="${sessionRegId}" data-isIntlRestricted="${isIntlRestricted}" data-isInternationalCustomer="${isInternationalCustomer}" data-enableLTLReg="${enableLTLReg}" data-regAddress="${regAddress}"
														   title="<c:out value='${productName}'/>" <c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
																	data-customizedPrice="${customizedPrice1}" data-customizationDetails="${customizationDetails}" data-color="${color}" data-size="${size}" data-personalizedImageUrls="${personalizedImageUrls}"
																	data-personlisedPrice="${personlisedPrice1}" data-personalizedImageUrlThumbs="${personalizedImageUrlThumbs}" data-personalizationType= "${personalizationType}"</c:if>>
																		<span class="icon-fallback-text">
																			<span class="icon-add" aria-hidden="true"></span>
																			<span class="icon-text" aria-hidden="true"><bbbl:label key='lbl_mng_regitem_quick_view' language="${pageContext.request.locale.language}" /></span>
																		</span>
																		<bbbl:label key='lbl_mng_regitem_quick_view' language="${pageContext.request.locale.language}" />
																	</a>
																	</c:otherwise>
																</c:choose>
														</span>
													</c:if>
												</div>
												<c:choose>
													<c:when test="${ltlItemFlag}">
													<dsp:getvalueof var="ltlShipMethodDesc" param="regItem.ltlShipMethodDesc"/>
													<dsp:getvalueof var="deliverySurcharge" param="regItem.deliverySurcharge"/> 
													<dsp:getvalueof var="assemblyFee" param="regItem.assemblyFees"/>
													<dsp:getvalueof var="shipMethodUnsupported" param="regItem.shipMethodUnsupported"/>
														<div class="price grid_2 omega ltlPrice">
														 
														  <span class="columnHeader"> <bbbl:label key="lbl_mng_regitem_sortprice" language="${pageContext.request.locale.language}" /></span>	
														
														  <div>
														  <c:if test="${inCartFlagSKU}">
															<div class="disPriceString"> <bbbl:label key="lbl_discounted_incart_text" language="${pageContext.request.locale.language}" /> </div>
															 </c:if>
															<!--li class="totalLtl"> Total</div-->
															  <div class="toalpriceLtl">
															  <dsp:droplet name="PriceDroplet">
																<dsp:param name="product" value="${productId}" />
																<dsp:param name="sku" value="${skuID}" />
																<dsp:oparam name="output">
																	<dsp:setvalue param="theListPrice" paramvalue="price" />
																	<dsp:getvalueof var="profileSalePriceList"
																		bean="Profile.salePriceList" />
																	<c:choose>
																		<c:when test="${not empty profileSalePriceList}">
																			<dsp:droplet name="PriceDroplet">
																				<dsp:param name="priceList" bean="Profile.salePriceList" />
																				<dsp:oparam name="output">
																					<dsp:getvalueof var="price" vartype="java.lang.Double"
																						param="theListPrice.listPrice" />
																					<dsp:getvalueof var="listPrice" vartype="java.lang.Double"
																						param="price.listPrice" />
																					<c:if test="${listPrice gt 0.10}">
																						<dsp:include page="/global/gadgets/formattedPrice.jsp">
																							<c:choose>	
																						 	 <c:when test="${shipMethodUnsupported}">
																						 	 	<dsp:param name="price" value="${listPrice}" />
																							 </c:when>
																							 <c:otherwise>
																							 	<dsp:param name="price" value="${listPrice+deliverySurcharge+assemblyFee }" />
																							 </c:otherwise>
																							</c:choose>
																						</dsp:include>
																					</c:if>										
																				</dsp:oparam>
																				<dsp:oparam name="empty">
																					<dsp:getvalueof var="price" vartype="java.lang.Double"
																						param="theListPrice.listPrice" />
																					<c:if test="${price gt 0.10}">
																						<dsp:include page="/global/gadgets/formattedPrice.jsp">
																							<c:choose>	
																							 	 <c:when test="${shipMethodUnsupported}">
																							 	 	<dsp:param name="price" value="${price}" />
																								 </c:when>
																								 <c:otherwise>
																								 	<dsp:param name="price" value="${price+deliverySurcharge+assemblyFee }" />
																								 </c:otherwise>
																							</c:choose>
																						</dsp:include>
																					</c:if>
																				</dsp:oparam>
																			</dsp:droplet>
																			<%-- End price droplet on sale price --%>
																		</c:when>
																		<c:otherwise>
																			<dsp:getvalueof var="price" vartype="java.lang.Double"
																				param="theListPrice.listPrice" />
																			<c:if test="${price gt 0.10}">
																				<dsp:include page="/global/gadgets/formattedPrice.jsp">
																					<c:choose>	
																						<c:when test="${shipMethodUnsupported}">
																						 	<dsp:param name="price" value="${price}" />
																						</c:when>
																						<c:otherwise>
																						 	<dsp:param name="price" value="${price+deliverySurcharge+assemblyFee }" />
																						</c:otherwise>
																					</c:choose>
																				</dsp:include>
																			</c:if>
																		</c:otherwise>
																	</c:choose>
																	<c:if test="${topFragment}">
																		<c:if test="${not empty listPrice}">
																			<c:set value="${listPrice}" var="price" />
																		</c:if>
																		<input type="hidden" value="${price}" class="itemPrice" />
																	</c:if>
																</dsp:oparam>
															</dsp:droplet></div>
															
															<c:choose>
																<c:when test="${ltlShipMethod == null or ltlShipMethod == ''  or shipMethodUnsupported}">
																				<div class="deliveryLtl"><bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/>:</div>
																				<div class="deliverypriceLtl"><span class="deliverypriceLtlClass"><bbbl:label key="lbl_cart_tbd" language="${pageContext.request.locale.language}"/></span><img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" /></div>
																 </c:when>
																<c:otherwise>
																	 <c:choose>
																		 <c:when test="${ltlShipMethod== 'LWA' }">
																		   <div class="deliveryLtl">   <bbbl:label key="lbl_Incl_White_Glove" language="${pageContext.request.locale.language}"/> <span><bbbl:label key="lbl_With_Assembly" language="${pageContext.request.locale.language}"/></span></div>
																		   <div class="deliverypriceLtl"><span class="deliverypriceLtlClass"><dsp:valueof value="${deliverySurcharge+assemblyFee}" number="0.00" converter="currency"/></span>
																		  <img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" /></div>
																		</c:when>
																		<c:otherwise>
																		<div class="deliveryLtl">
																				 Incl ${ltlShipMethodDesc}:
																					 </div>     <div class="deliverypriceLtl"><span class="deliverypriceLtlClass">       <c:if test="${deliverySurcharge eq 0.0}"> <bbbl:label key="lbl_shipping_free" language="${pageContext.request.locale.language}"/></c:if>
																											 <c:if test="${deliverySurcharge gt 0.0}"><dsp:valueof value="${deliverySurcharge}" number="0.00" converter="currency"/></c:if>
																			</span>  <img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" /></div>
																			</c:otherwise>
																	</c:choose>
																</c:otherwise>
															 </c:choose>
															<c:if test="${ltlShipMethod != null && ltlShipMethod != '' && !shipMethodUnsupported}">	 
															  <div class="itemLtl"><bbbl:label key="lbl_Item_Price_dsk" language="${pageContext.request.locale.language}"/>
																<span class="itempriceLtl">
																	<dsp:include page="registry_items_guest_category_frag.jsp">
																		<dsp:param name="productId" value="${productId}"/>
																		<dsp:param name="skuID" value="${skuID}"/>
																		</dsp:include>	
																</span>
															</div>
															</c:if>
																 </div>
																 </div> 
												</c:when>
												<c:otherwise>
													<div class="price grid_2 omega ltlPrice" tabindex="0">
													<span class="columnHeader">Price</span>
													<c:choose>
															<c:when test="${not empty personalisedCode}">
																<div><dsp:include page="/global/gadgets/formattedPrice.jsp">												
													               <span><dsp:param name="price" value="${personlisedPrice}"/>
													        	</dsp:include></span>
															<span aria-hidden="true" class="fontAreial alignVeritcal">*</span>
																<div class ="priceSubject fontAreial">
																	<span>*</span>
																	<span class="priceSubString"><bbbl:label key='lbl_price_subject_to_change' language="${pageContext.request.locale.language}" /></span>
																</div>
																</div>
																<input type="hidden" value="${personlisedPrice}" class="itemPrice"/>
															</c:when>
															<c:otherwise>
																<span class="columnHeader rlpPrice">
																<dsp:include page="registry_items_guest_category_frag.jsp">
																	<dsp:param name="productId" value="${productId}"/>
																	<dsp:param name="skuID" value="${skuID}"/>
																	<dsp:param name="inCartFlagSKU" value="${inCartFlagSKU}" />												
																</dsp:include>	
																</span>														
										            </c:otherwise>
											             	</c:choose>
										          
													</div>
												</c:otherwise>
												</c:choose>
												<div class="requested">
													<span class="columnHeader"><bbbl:label key='lbl_mng_regitem_requested' language="${pageContext.request.locale.language}" /></span><span class="columnHead" tabindex="0" aria-label="Requested:<dsp:valueof param="regItem.qtyRequested" />" style="font-family: 'ITCNewBaskervilleRoman';color: #666;font-size: 18px;">
													<dsp:valueof param="regItem.qtyRequested" /></span>
												</div>
												<div class="purchase">
													<span class="columnHeader"><bbbl:label key='lbl_mng_regitem_purchased' language="${pageContext.request.locale.language}" /></span><span class="columnHead" tabindex="0" aria-label="Purchased:<dsp:valueof param="regItem.qtyPurchased" />"style="font-family: 'ITCNewBaskervilleRoman';color: #666;font-size: 18px;">
													<dsp:valueof param="regItem.qtyPurchased" /></span>
												</div>
												<div class="quantity">
													<span class="columnHeader" tabindex="0"><bbbl:label key='lbl_mng_regitem_quantity' language="${pageContext.request.locale.language}" /></span>
													<div class="input alpha">
														<div class="spinner fl">


															<a href="#" class="down button-Med btnSecondary" aria-label="<bbbl:label key='lbl_decrease_quantity' language='${pageContext.request.locale.language}' />">
																<span class="icon-fallback-text">
																	<span class="icon-minus" aria-hidden="true"></span>

																</span>
															</a>
															<span class="incDec visuallyhidden" aria-live="assertive" aria-atomic="true"></span>
															<input name="qty" type="text" value="1" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="input_tiny_giftView fl itemQuantity" data-change-store-submit="qty" data-change-store-errors="required digits nonZero" role="textbox" aria-required="false" />
															<a href="#" class="up button-Med btnSecondary" aria-label="<bbbl:label key='lbl_increase_quantity' language='${pageContext.request.locale.language}' />">
																<span class="icon-fallback-text">
																	<span class="icon-plus" aria-hidden="true"></span>

																</span>
															</a>



														</div>
													</div>
												</div>

												<div class="productLastColumn fr grid_2">
												<dsp:getvalueof var="skuID" param="regItem.sku" />
												<input type="hidden" name="skuId" value="${skuID}" class="skuId changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" />
												<input type="hidden" name="registryId" value="${registryId}" class="registryId" data-change-store-submit="registryId" data-change-store-errors="required"  />
												<input type="hidden" name="refNum" value="${refNum}" class="frmAjaxSubmitData" />
												<input type="hidden" name="prodId" value="${productId}" class="productId" data-change-store-submit="prodId"/>
												<dsp:getvalueof param="regItem.qtyRequested" var="requestedQuantity" />
													<dsp:getvalueof param="regItem.qtyPurchased" var="purchasedQuantity" />

								                    <c:if test="${(purchasedQuantity ge requestedQuantity) && !(requestedQuantity == 0 && purchasedQuantity ==0)}">
														<div class="purchasedConfirmation">
														  	<span class="icon-fallback-text">
																<span class="icon-checkmark" aria-hidden="true"></span>
																<span class="icon-text"><bbbl:label key='lbl_mng_regitem_purchased_confirm' language="${pageContext.request.locale.language}" /></span>
															</span>
															<bbbl:label key='lbl_mng_regitem_purchased_confirm' language="${pageContext.request.locale.language}" />
														</div>

													</c:if>

													<dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>
													<dsp:getvalueof var="userRegCount" value="${fn:length(sessionMapValues.userRegistriesList)}"/>
													<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO.registryId" var="sessionRegId"/>
														<c:choose>
															<c:when test="${!isTransient }">
																		<c:if test="${userRegCount ne 0}">
																		<%-- LTL-328 Hide Registry Button for LTL item for Logged In User --%>
																		 <dsp:getvalueof var="ltlItemFlag" param="regItem.sKUDetailVO.ltlItem" />
																		 <input type="hidden" name="isLtlItem" value="${ltlItemFlag}" />
																		 <c:if test="${!shipMethodUnsupported}">
																		 <input type="hidden" name="ltlShipMethod" value="${ltlShipMethod}" />
																		 <input type="hidden" name="ltlDeliveryServices" value="${ltlShipMethod}" />
																	</c:if>
																		 <input type="hidden" name="ltlShipMethodRegistry" value="" />
																						<c:if test="${registryId!=sessionRegId}">
																					 		<div class="copyRegButton <c:if test='${isInternationalCustomer or not empty refNum}'>disabled</c:if>" >
																								<input type="button" class="btnAddToRegistry button-Med btnSecondary" 
																								<c:if test="${isInternationalCustomer or not empty refNum or (ltlItemFlag && fn:containsIgnoreCase(enableLTLRegForSite,'false'))}">disabled="disabled"</c:if> name="" title="Add to Registry" value="Add to Registry" role="button" aria-pressed="false" data-notify-reg="true" />
																							</div>
																						</c:if>
																	</c:if>
															</c:when>
														</c:choose>

													<dsp:getvalueof param="regItem.qtyRequested" var="requestedQuantity" />
													<dsp:getvalueof param="regItem.qtyPurchased" var="purchasedQuantity" />
													<%-- <c:if test="${currentSiteId ne BedBathCanadaSite}"> --%>
														<c:if test="${requestedQuantity > purchasedQuantity}">

														<%--  For OOS item , this button is made hidden so that find in store functionality can work --%>
															<div class="hidden button ${button_active} addToCart marBottom_5">
																<input type="button" name="btnAddToCart" title="Add to cart" value="add TO CART" role="button" aria-pressed="false" />
															</div>

															<dsp:getvalueof var="bopusAllowed" param="regItem.sKUDetailVO.bopusAllowed"/>
															<%-- LTL-328 Hide Pick Up in Store for LTL item --%>
															<dsp:getvalueof var="ltlItemFlag" param="regItem.SKUDetailVO.ltlItem" />
															<c:choose>
																<c:when test="${not empty refNum and not empty personalisedCode }">
																	<c:set var="isPersonalized" value="true"  />
																</c:when>
																<c:otherwise>
																	<c:set var="isPersonalized" value="false" />
																</c:otherwise>
															</c:choose>
															<c:if test="${MapQuestOn}">
			                                               	<c:choose>
												                <c:when test="${ltlItemFlag}">
																		<a class="pickupStore changeStore hidden " title="<bbbl:label key='lbl_mng_regitem_pickupinstore' language='${pageContext.request.locale.language}' />" href="#"><bbbl:label key='lbl_mng_regitem_pickupinstore' language="${pageContext.request.locale.language}" /></a>
												                </c:when>
												                <c:otherwise>
																	<c:if test="${not bopusAllowed}">
																		<a class="pickupStore changeStore <c:if test='${isInternationalCustomer or isPersonalized}'>disabled</c:if> " title="<bbbl:label key='lbl_mng_regitem_pickupinstore' language='${pageContext.request.locale.language}' />" href="#">
																			<span class="icon-fallback-text">
																				<span class="icon-store-icon" aria-hidden="true"></span>
																				<span class="icon-text" aria-label="Find ${productName} in a store near you"></span>
																			</span>
																			<bbbl:label key='lbl_mng_regitem_pickupinstore' language="${pageContext.request.locale.language}" />
																		</a>
																	</c:if>
												                </c:otherwise>
												            </c:choose>
												            </c:if>
														</c:if>
													<%-- </c:if> --%>


												</div>
											</div>

											<div class="productFooter clearfix">
													<div>
													 <dsp:getvalueof var="skuDetailVO" param="regItem.sKUDetailVO"/>
													 <dsp:getvalueof var="inCartFlagSKU" value="${skuDetailVO.inCartFlag}" />
										            <c:if test="${active}">
										           <div class="noMar clearfix freeShipContainer prodAttribWrapper">
										            	<c:set var="showShipCustomMsg" value="true"/>
														<dsp:droplet name="/atg/dynamo/droplet/ForEach">
															<dsp:param name="array" param="regItem.sKUDetailVO.skuAttributes" />
															<dsp:param name="elementName" value="attributeVOList"/>
															<dsp:oparam name="output">
																<dsp:getvalueof var="pageName" param="key" />
																	<c:if test="${pageName eq 'RLP'}">
																	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	<dsp:param param="attributeVOList" name="array" />
																	<dsp:param name="elementName" value="attributeVO"/>
																	<dsp:oparam name="output">
																	 <c:set var="currentCount"><dsp:valueof param="count" /></c:set>
																	 <c:set var="actionURL"><dsp:valueof param="attributeVO.actionURL" /></c:set>
																		<c:if test="${not empty actionURL}">
																			<div <c:if test="${fn:contains(actionURL,'ltlDeliveryInfo') }">class="ltlTruck"</c:if>><a href="${actionURL}" class="popup"><span><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></span></a></div>
																		</c:if>
																	<c:choose>
									                          			<c:when test="${currentCount%2 == 0}">
													            			<c:set var="nextStyle">prodSecondaryAttribute</c:set>
													            		</c:when>
													            		<c:otherwise>
																			<c:set var="nextStyle">prodPrimaryAttribute</c:set>
																		</c:otherwise>
													            	</c:choose>
																	</dsp:oparam>
																	</dsp:droplet>
																	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	<dsp:param param="attributeVOList" name="array" />
																	<dsp:param name="elementName" value="attributeVO"/>
																	<dsp:param name="sortProperties" value="+priority"/>
																		<dsp:oparam name="output">
																		<dsp:getvalueof var="attrId" param="attributeVO.attributeName" />
																		<c:if test="${fn:contains(shippingAttributesList,attrId)}">
																			<c:set var="showShipCustomMsg" value="false"/>
																		</c:if>
                                                                      <c:set var="currentCount"><dsp:valueof param="count" /></c:set>
																	 <c:set var="actionURL"><dsp:valueof param="attributeVO.actionURL" /></c:set>
																		<c:if test="${empty actionURL}">
																			<div><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></div>
																		</c:if>
																	<c:choose>
									                          			<c:when test="${currentCount%2 == 0}">
													            			<c:set var="nextStyle">prodSecondaryAttribute</c:set>
													            		</c:when>
													            		<c:otherwise>
																			<c:set var="nextStyle">prodPrimaryAttribute</c:set>
																		</c:otherwise>
													            	</c:choose>
																	</dsp:oparam>
																	</dsp:droplet>
																	
																</c:if>
															</dsp:oparam>
														</dsp:droplet>
														
														<c:if test="${showShipCustomMsg}">
														<div class="freeShipBadge">
														 ${prdDisplayShipMsg}
														 </div>
														</c:if>
														
														</div>
													</c:if>
													</div>


											</div>

										</div>
										<div class="clear"></div>
									</li>
						</dsp:oparam>
                        <dsp:oparam name="outputEnd">
                        				
										<div class="clear"></div>
                                    </ul>
                                </div>
                        </dsp:oparam>
					</dsp:droplet>
			</c:if>
			</div>

			</dsp:oparam>
		</dsp:droplet>

		</c:if>
			
		<%--BBBSL-6813 | Adding certona tagging on guest registry view --%>
		<c:if test="${CertonaOn}">
			<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${currentSiteId}" />
			<script type="text/javascript">
			var links = "${itemList}";
			var resx = new Object();
			resx.appid ="${appIdCertona}";
			resx.links = links;
			resx.pageid = "${pageIdCertona}";
			 if (typeof certonaResx === 'object') { 
					certonaResx.run();  
		   };
		   function callCertonaResxRun(certonaString) {
		       resx = new Object();
		        resx.appid = "${appIdCertona}";
				resx.event = "addtocart_op";
		          resx.itemid = certonaString;
				resx.pageid = "";
		        resx.links = "";
		        if (typeof certonaResx === 'object') { 
		        	certonaResx.run();
		        };	
		   }			   
    </script>
    </c:if>
</dsp:page>
