<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFetchProductIdDroplet" />
	<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
    <dsp:importbean bean="/com/bbb/commerce/checkout/droplet/IsProductSKUShippingDroplet"/>
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/RegistryInfoDisplayDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/POBoxValidateDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
    <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<c:set var="enableLTLReg"><bbbc:config key="enableLTLRegForSite" configName="FlagDrivenFunctions" /></c:set>
	<c:set var="enableKatoriFlag"><bbbc:config key="enableKatori" configName="EXIMKeys" /></c:set>
	<c:set var="customizeCTACodes">
		<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
	</c:set>
	<jsp:useBean id="placeHolderMapServiceLevel" class="java.util.HashMap" scope="request"/>
	<%-- Droplet Placeholder --%>
	<dsp:getvalueof var="registryId" param="registryId" />
	<dsp:getvalueof var="regAddress" value="${fn:escapeXml(param.regAddress)}" />
	<dsp:getvalueof var="skuId" value="" />
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="sortSeq" param="sorting"/>
	<dsp:getvalueof var="view" param="view"/>
	<dsp:getvalueof var="eventType" param="eventType" />
	<dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
	<c:set var="myItemsFlag">
			<bbbc:config key="My_Items_Checklist_Flag" configName="FlagDrivenFunctions" />
	</c:set>
	<dsp:getvalueof var="isChecklistDisabled" param="isChecklistDisabled"/>
	<c:choose>
		<c:when test="${isChecklistDisabled or null eq isChecklistDisabled or empty isChecklistDisabled or !myItemsFlag}">
			<c:set var="isMyItemsCheckList">false</c:set>
		</c:when>
		<c:otherwise>
			<c:set var="isMyItemsCheckList">true</c:set>
		</c:otherwise>
	</c:choose>
<c:set var="lblAboutThe"><bbbl:label key="lbl_accessibility_about_the" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="writeReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblProductQuickView"><bbbl:label key='lbl_mng_regitem_quick_view' language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblForThe"><bbbl:label key="lbl_accessibility_for_the" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblOfThe"><bbbl:label key="lbl_accessibility_of_the" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="scene7Path">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
	<c:set var="vdcAttributesList">
		<bbbc:config key="vdcAttributesList" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="valueIndex">0</c:set>
<dsp:getvalueof id="defaultCountry" bean="/atg/multisite/Site.defaultCountry" />

<c:choose>
	<c:when test="${defaultCountry eq 'MX'}">
		<c:set var="incartPriceList"><bbbc:config key="MexInCartPriceList" configName="ContentCatalogKeys" /></c:set>
	</c:when>
	<c:when test="${currentSiteId eq 'BedBathUS' || currentSiteId eq 'BuyBuyBaby'}">
		<c:set var="incartPriceList"><bbbc:config key="BedBathUSInCartPriceList" configName="ContentCatalogKeys" /></c:set>
	</c:when>
	<c:when test="${currentSiteId eq 'BedBathCanada'}">
		<c:set var="incartPriceList"><bbbc:config key="BedBathCanadaInCartPriceList" configName="ContentCatalogKeys" /></c:set>
	</c:when>
</c:choose>

	<dsp:droplet name="POBoxValidateDroplet">
		 <dsp:param value="${regAddress}" name="address" />
		 <dsp:oparam name="output">
		 <dsp:getvalueof var="isPOBoxAddress" param="isValid"/>
		 </dsp:oparam>
	</dsp:droplet>

	<%-- if the registry is private and no shipping address available, product links bring up edit modal--%>
	<c:choose>
		<c:when test="${empty regAddress}">
			<c:set var="productLinkClass" value="editRegInfo completeShipping" />
		</c:when>
		<c:otherwise >
			<c:set var="productLinkClass" value="lnkQVPopup" />
		</c:otherwise>
	</c:choose>
	
	<dsp:include page="registry_modified_item_form.jsp">
	</dsp:include>
	<dsp:form id="formID1" method="post">

	<dsp:input type="hidden" bean="GiftRegistryFormHandler.size" paramvalue="totEntries" />
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="priceRangeList" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="sizeIE" param="size"/>
			<dsp:getvalueof var="countIE" param="count"/>
			<c:choose>
				<c:when test="${countIE eq 1}">
					<div class="accordionReg1 accordionReg accordion ui-accordion ui-widget ui-helper-reset ui-accordion-icons" id="accordionReg1">
				</c:when>
				<c:when test="${countIE eq 5}">
					</div>
					
					<div class="accordionReg2 accordionReg  accordion ui-accordion ui-widget ui-helper-reset ui-accordion-icons" id="accordionReg2">
				</c:when>
				<c:when test="${countIE eq 12}">
					</div>
					
					<div class="accordionReg3 accordionReg  accordion ui-accordion ui-widget ui-helper-reset ui-accordion-icons" id="accordionReg3">
				</c:when>
			</c:choose>
			<dsp:getvalueof var="idxPrice" param="index"/>
			<dsp:getvalueof var="priceRange" param="element"/>
			<dsp:droplet name="ForEach">
				<dsp:param name="array" param="categoryBuckets" />
				<%--<dsp:param name="sortProperties" value="+_key" /> --%>
				<dsp:oparam name="output">
					<dsp:getvalueof var="idxCat" param="index"/>
					<dsp:getvalueof var="bucketName" param="key" idtype="java.lang.String" />
					<dsp:getvalueof var="bucket" param="categoryBuckets.${bucketName}" idtype="java.util.List" />
					<dsp:droplet name="IsEmpty">
					<dsp:param name="value" value="${bucket}"/>
					<dsp:oparam name="false">
						<c:set var="bucketEmpty" value="false"/> 
					</dsp:oparam>
					<dsp:oparam name="true">
						<c:set var="bucketEmpty" value="true"/>
					</dsp:oparam>
					</dsp:droplet>
					<c:choose>
						<c:when test="${isMyItemsCheckList && bucketEmpty}">
							<c:set var="showPriceRange" value="false"/>
						</c:when>
						<c:otherwise>
							<c:set var="showPriceRange" value="true"/>
						</c:otherwise>
					</c:choose>
					<c:if test="${priceRange eq bucketName && showPriceRange eq 'true'}">

							<h2 class="ui-accordion-header ui-helper-reset ui-state-default ui-corner-all"><span class="ui-icon ui-icon-triangle-1-e"></span><span class="accordionTitle block clearfix priceTitle">
									<dsp:getvalueof var="count" value="${fn:length(bucket)}"/>
									<c:choose>
										<c:when test="${count eq 0}">
											<a href="#" class="fl accordionLink"><dsp:valueof converter="currency" param="key" />&nbsp;(${count})</a>
										</c:when>
										<c:otherwise>
											<a href="#" class="fl accordionLink"><dsp:valueof converter="currency" param="key" />&nbsp;(${count}) </a>
										</c:otherwise>
									</c:choose>
							</span></h2>
							<%-- ForEach for List<RegistryItemVO> listRegistryItemVO --%>
							<div class="accordionDiv ui-accordion-content ui-helper-reset ui-widget-content ui-corner-bottom">
							<ul class="productDetailList giftViewProduct">
							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="categoryBuckets.${bucketName}" />
								<dsp:param name="elementName" value="regItem" />

								<dsp:oparam name="output">
									<dsp:getvalueof var="skuID" vartype="java.lang.String"  param="regItem.sku" />
									<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
										<dsp:param name="skuId" value="${skuID}" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="productId" vartype="java.lang.String" param="productId" />
											<dsp:getvalueof var="inStoreSku" param="inStore" />
										</dsp:oparam>
									</dsp:droplet>
									<c:choose>
									<c:when test="${itemList ne ''}">
										<c:set var="itemList" scope="request">${itemList};${productId}</c:set>
									</c:when>
									<c:otherwise>
										<c:set var="itemList" scope="request">${productId}</c:set>
									</c:otherwise>
									</c:choose>
									<dsp:getvalueof var="isBelowLineItem" param="regItem.isBelowLineItem" />
									<dsp:getvalueof var="disable" param="regItem.sKUDetailVO.disableFlag" />
									<dsp:getvalueof var="webOffered" param="regItem.sKUDetailVO.webOfferedFlag" />
									<dsp:getvalueof var="active" param="regItem.sKUDetailVO.activeFlag" />
									<dsp:getvalueof var="ltlShipMethod" param="regItem.ltlDeliveryServices" />
									<dsp:getvalueof var="idx" param="index"/>
									<li class="grid_12 alpha omega qtyWrapper productRow btngiftViewTopMargin giftViewProductChangeStoreWrapper addToCartGiftRegWrapper clearfix">

									<input type="hidden" name="bts" value="false" class="addToCartSubmitData" />
									<dsp:getvalueof var="ltlItemFlag" param="regItem.sKUDetailVO.ltlItem" />
									<dsp:getvalueof var="ltlShipMethod" param="regItem.ltlDeliveryServices" />
									<dsp:getvalueof var="ltlShipMethodDesc" param="regItem.ltlShipMethodDesc"/>
									<dsp:getvalueof var="deliverySurcharge" param="regItem.deliverySurcharge"/>
									<dsp:getvalueof var="ltlAssemblySelected" param="regItem.assemblySelected"/>
									<dsp:getvalueof var="assemblyFee" param="regItem.assemblyFees"/>
									<dsp:getvalueof var="shipMethodUnsupported" param="regItem.shipMethodUnsupported"/>	
									<dsp:getvalueof var="isDSLUpdateable" param="regItem.dslUpdateable"/>
									<dsp:getvalueof var="inCartFlagSKU" param="regItem.sKUDetailVO.inCartFlag" />
									<dsp:getvalueof var="regItemOldQty" param="regItem.qtyRequested" />
												<dsp:getvalueof var="qtyPurchased" param="regItem.qtyPurchased"/>
												<dsp:getvalueof var="rowID" param="regItem.rowID"/>
												<dsp:getvalueof param="regItem.sKUDetailVO.intlRestricted" var="isIntlRestricted" />
												<dsp:getvalueof var="color" param="regItem.sKUDetailVO.color" />
									<dsp:getvalueof var="upc" param="regItem.sKUDetailVO.upc" />
									<dsp:getvalueof var="size" param="regItem.sKUDetailVO.size" />	
									
										
										<c:if test="${ltlItemFlag && isPOBoxAddress}">
											<div class="ltlMessage displayPOBox"><bbbt:textArea key="txt_ltl_shipping_not_available" language ="${pageContext.request.locale.language}"/></div>
										</c:if>
										<dsp:getvalueof var="notifyRegistrantMsgTyp" param="regItem.displayNotifyRegistrantMsg"/>										
										<c:if test="${not empty notifyRegistrantMsgTyp}">
											<div class="notifyRegistrantMessage"> <bbbl:label key="notifyRegMsg_Status${notifyRegistrantMsgTyp}" language="${pageContext.request.locale.language}" /> </div>
										</c:if>
										<c:if test="${ltlItemFlag && (ltlShipMethod eq null || ltlShipMethod eq '')}">
											<div class="ltlMessage"><bbbl:label key='lbl_rlp_ltl_registry_message' language="${pageContext.request.locale.language}" />
											<c:if test="${isDSLUpdateable}">
											<a class="updateDsl"><bbbl:label key='lbl_update_service_level' language="${pageContext.request.locale.language}" /></a>
											</c:if></div>
										</c:if>										
										<c:if test="${ltlItemFlag && not empty ltlShipMethod && shipMethodUnsupported}">
											
										<c:set target="${placeHolderMapServiceLevel}" property="ShippingMethodDesc" value="${ltlShipMethodDesc}"/>
										<div class="clear"></div>
										<div class="ltlMessage marBottom_10 marTop_10" >
										<bbbt:textArea key="txt_ship_method_not_supported_rlp" placeHolderMap="${placeHolderMapServiceLevel}" language ="${pageContext.request.locale.language}"/>
										<c:if test="${isDSLUpdateable}"><a class="updateDsl"><bbbl:label key='lbl_update_service_level' language="${pageContext.request.locale.language}" /></a></c:if>
										</div>
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
									<c:set var="customizedPrice1"><dsp:valueof value="${customizedPrice}" converter="currency"/></c:set>
	                                    <c:set var="personlisedPrice1"><dsp:valueof value="${personlisedPrice}" converter="currency"/></c:set>
									<dsp:getvalueof var="itemType" param="regItem.itemType"/>
									<dsp:getvalueof var="refNum" param="regItem.refNum"/>
									<dsp:getvalueof var="customizationDetails" param="regItem.customizationDetails"/>
									<dsp:getvalueof var="personalizationType" param="regItem.sKUDetailVO.personalizationType" />
									<c:if test="${not empty personalisedCode}">
										<c:set var="cusDet">${eximCustomizationCodesMap[personalisedCode]}</c:set>									
										<input type="hidden" value="${cusDet}" id="customizationDetails"/> 
									</c:if>
									<div class="productImage  grid_2 alpha">
										<c:choose>
											<c:when test="${active}">
												<a class="prodImg ${productLinkClass}" href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}${ltlOpt}"
												data-upc="${upc}" data-color="${color}" data-size="${size}" data-qtyPurchased="${qtyPurchased}" data-qtyRequested="${regItemOldQty}" data-deliverySurcharge="${deliverySurcharge}" 
														data-rowID="${rowID}" data-itemType="${itemType}"  data-eventType="${eventType}" data-isBelowLineItem="${isBelowLineItem}" data-isIntlRestricted="${isIntlRestricted}" data-isInternationalCustomer="${isInternationalCustomer}" data-enableLTLReg="${enableLTLReg}" data-regAddress="${regAddress}"
												 data-pdp-qv-url="pdp_quick_view.jsp" data-skuId="${skuID}" data-registryId="${registryId}" data-productId="${productId}" data-refNum="${refNum}" data-sopt="${ltlShipMethod}" title="<c:out value='${productName}'/>" <c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
															data-customizedPrice="${customizedPrice1}" data-customizationDetails="${customizationDetails}" data-color="${color}" data-size="${size}" data-personalizedImageUrls="${personalizedImageUrls}"
															data-personlisedPrice="${personlisedPrice1}" data-personalizedImageUrlThumbs="${personalizedImageUrlThumbs}" data-personalizationType= "${personalizationType}"</c:if>>
													<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.mediumImage" />
													<c:choose>
                                             			<c:when test="${not empty refNum}">
                                             				<c:choose>
                                             					<c:when test="${not empty personalizedImageUrlThumbs}">
                                             						<img src="${imagePath}/_assets/global/images/blank.gif" data-original="${personalizedImageUrlThumbs}" alt="<c:out value='${productName}'/>" height="96" width="96" class="prodImage noImageFound loadingGIF" onerror="this.src = '${imagePath}/_assets/global/images/no_image_available.jpg';" />
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
                                            </c:when>
                                            <c:otherwise>
                                                <dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.mediumImage" />
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
                                        </div>

									
									
									
									<div class="productContainer grid_10 omega">

										<%--
										<ul class="productTab productHeading clearfix">
											<li class="productName">
												<c:if test="${upc ne null}">
													<div class="fl upc"><span class="bold"> <bbbl:label key='lbl_mng_regitem_upc' language="${pageContext.request.locale.language}" /> </span>
													<span>${upc}</span></div>
												</c:if>
											</li>
											<li class="price">Price</li>
											<li class="requested"><bbbl:label key='lbl_mng_regitem_purchased' language="${pageContext.request.locale.language}" /></li>
											<li class="purchase"><bbbl:label key='lbl_mng_regitem_requested' language="${pageContext.request.locale.language}" /></li>
											<li class="productLastColumn"> </li>
										</ul>
										--%>
										<div class="productTab productContent clearfix">
											<dsp:getvalueof var="displayName" param="regItem.sKUDetailVO.displayName" />
											<div class="productName grid_2">
												<span class="blueName">
													<c:choose>
														<c:when test="${active}">
															<a href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}${ltlOpt}"
														class="${productLinkClass} prodTitle" data-pdp-qv-url="pdp_quick_view.jsp" 
														data-upc="${upc}" data-color="${color}" data-size="${size}" data-qtyPurchased="${qtyPurchased}" data-qtyRequested="${regItemOldQty}" data-deliverySurcharge="${deliverySurcharge}" 
														data-rowID="${rowID}" data-itemType="${itemType}"  data-eventType="${eventType}" data-isBelowLineItem="${isBelowLineItem}" data-isIntlRestricted="${isIntlRestricted}" data-isInternationalCustomer="${isInternationalCustomer}" data-enableLTLReg="${enableLTLReg}" data-regAddress="${regAddress}"
														data-skuId="${skuID}" data-registryId="${registryId}" data-productId="${productId}" data-sopt="${ltlShipMethod}" data-refNum="${refNum}"  title="<c:out value='${productName}'/>" <c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
															data-customizedPrice="${customizedPrice1}" data-customizationDetails="${customizationDetails}" data-color="${color}" data-size="${size}" data-personalizedImageUrls="${personalizedImageUrls}"
															data-personlisedPrice="${personlisedPrice1}" data-personalizedImageUrlThumbs="${personalizedImageUrlThumbs}" data-personalizationType= "${personalizationType}"</c:if>>
																${productName}
															</a>
														</c:when>
														<c:otherwise>
																${productName}
														</c:otherwise>
													</c:choose>
												</span>

	                                            <dl class="productAttributes">
													<c:if test="${upc ne null}">
														<dt><bbbl:label key='lbl_mng_regitem_upc' language="${pageContext.request.locale.language}" /></dt>
														<dd>${upc}</dd>
													</c:if> 
													<c:if test='${not empty color}'>
														<dt><bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/>:</dt>
														<dd><dsp:valueof value="${color}" valueishtml="true" /></dd>
													</c:if>

													<c:if test='${not empty size}'>
														<dt><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/>:</dt>
														<dd><dsp:valueof value="${size}" valueishtml="true" /></dd>
													</c:if>

													
													<c:if test="${not empty personalisedCode}">
															<dt>${eximCustomizationCodesMap[personalisedCode]}</dt>
															<dd>: ${customizationDetails}</dd>
															<span class="personalizationAttr katoriPrice clearfix">
															<%-- BBBSL-8154 --%>
															<%-- <span  aria-hidden="true">${personalizationOptionsDisplay}</span> --%>
															<div class="eximRegMsg">
															<c:choose>
															<c:when test="${personalizationType == 'PB'}">
															  <bbbl:label key='lbl_PB_Fee_detail' language="${pageContext.request.locale.language}" />
															</c:when>
															<c:when test="${personalizationType == 'PY'}">
															 	<dsp:valueof value="${customizedPrice1}"/>&nbsp;<bbbl:label key='lbl_exim_added_price' language="${pageContext.request.locale.language}" />
															</c:when>
															<c:when test="${personalizationType == 'CR'}">
															<c:choose>
																<c:when test="${not empty personalisedCode && fn:contains(customizeCTACodes, personalisedCode)}">
																	<c:set var="showMsg" value="lbl_exim_cr_added_price_customize"/>
																</c:when>
																<c:otherwise>
																	<c:set var="showMsg" value="lbl_exim_cr_added_price"/>
																</c:otherwise>
															</c:choose>
															 <dsp:include page="/global/gadgets/formattedPrice.jsp">												
												               <dsp:param name="price" value="${customizedPrice}"/>
												               <dsp:param name="showMsg" value="${showMsg}"/>
												        	</dsp:include>
															</c:when>
															</c:choose>
															</div>
															</span>
													</c:if>
												</dl>

												<c:if test="${active}">
													<span class="quickViewLink">
	                                                    			<a href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}"
															class="lnkQVPopup prodTitle" role="link" aria-label="${lblProductQuickView} ${lblOfThe} ${productName}" data-upc="${upc}" data-color="${color}" data-size="${size}" data-qtyPurchased="${qtyPurchased}" data-qtyRequested="${regItemOldQty}" data-deliverySurcharge="${deliverySurcharge}" 
															data-pdp-qv-url="pdp_quick_view.jsp" data-skuId="${skuID}" data-registryId="${registryId}" data-productId="${productId}" data-refNum="${refNum}" 
															data-rowID="${rowID}" data-itemType="${itemType}"  data-eventType="${eventType}" data-isBelowLineItem="${isBelowLineItem}" data-isIntlRestricted="${isIntlRestricted}" data-isInternationalCustomer="${isInternationalCustomer}" data-enableLTLReg="${enableLTLReg}" data-regAddress="${regAddress}"
															 title="<c:out value='${productName}'/>" <c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
															data-customizedPrice="${customizedPrice1}" data-customizationDetails="${customizationDetails}" data-color="${color}" data-size="${size}" data-personalizedImageUrls="${personalizedImageUrls}"
															data-personlisedPrice="${personlisedPrice1}" data-personalizedImageUrlThumbs="${personalizedImageUrlThumbs}" data-personalizationType= "${personalizationType}"</c:if>>
															<span class="icon-fallback-text">
																<span class="icon-add" aria-hidden="true"></span>
																<span class="icon-text" aria-hidden="true"><bbbl:label key='lbl_mng_regitem_quick_view' language="${pageContext.request.locale.language}" /></span>
															</span>
															<bbbl:label key='lbl_mng_regitem_quick_view' language="${pageContext.request.locale.language}" />
														</a>
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
				
					<div class="toalpriceLtl"><dsp:droplet name="PriceDroplet">
					<dsp:param name="product" value="${productId}" />
					<dsp:param name="sku" value="${skuID}" />
					<dsp:oparam name="output">
						<dsp:setvalue param="theListPrice" paramvalue="price" />
						<c:if test="${inCartFlagSKU}">
						<dsp:droplet name="PriceDroplet">
								<dsp:param name="product" param="product" />
									<dsp:param name="sku" param="sku" />
									<dsp:param name="priceList" value="${incartPriceList}" />
									<dsp:oparam name="output">
									<dsp:getvalueof var="inCartPrice" vartype="java.lang.Double"
											param="price.listPrice" />
								</dsp:oparam>
							</dsp:droplet>
						</c:if>
						<dsp:getvalueof var="profileSalePriceList"
							bean="Profile.salePriceList" />
						<c:choose>
							<c:when test="${not empty profileSalePriceList}">
								<dsp:droplet name="PriceDroplet">
									<dsp:param name="priceList" bean="Profile.salePriceList" />
									<dsp:oparam name="output">
									<c:choose>
										<c:when test="${inCartFlagSKU}">
										<c:set var="listPrice" value="${inCartPrice}" />
										</c:when>
										<c:otherwise>
											<dsp:getvalueof var="price" vartype="java.lang.Double"
												param="theListPrice.listPrice" />
											<dsp:getvalueof var="listPrice" vartype="java.lang.Double"
												param="price.listPrice" />
										</c:otherwise>
									</c:choose>
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
									<c:choose>
											<c:when test="${inCartFlagSKU}">
											 <c:set var="price" value="${inCartPrice}" />
											</c:when>
											<c:otherwise>
												<dsp:getvalueof var="price" vartype="java.lang.Double"
														param="theListPrice.listPrice" />
											</c:otherwise>
										</c:choose>
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
							<c:choose>
								<c:when test="${inCartFlagSKU}">
									<c:set var="price" value="${inCartPrice}" />
								</c:when>
								<c:otherwise>
									<dsp:getvalueof var="price" vartype="java.lang.Double"
										param="theListPrice.listPrice" />
								</c:otherwise>
							</c:choose>
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
						<c:when test="${empty ltlShipMethod or shipMethodUnsupported}">
						<input name="shipMethodUnsupported" type="hidden" value="${shipMethodUnsupported}"/>
                          <c:choose>
							 <c:when test="${isDSLUpdateable}">
								 <div><a class="updateDsl"><bbbl:label key='lbl_update_service_level' language="${pageContext.request.locale.language}" />
										 <img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" />
										</a>
								</div> 
							 	 <div class="deliveryLtl"></div>
	                        	  <div class="deliverypriceLtl"><span class="deliverypriceLtlClass"></span><img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo hidden" /> </div>
							 </c:when>
	                          <c:otherwise>
								  <div class="deliveryLtl"><bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/>:</div>
	                              <div class="deliverypriceLtl"><span class="deliverypriceLtlClass">TBD</span><img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" /> </div>
	                         </c:otherwise>
	                       </c:choose>
	                      </c:when>
						<c:otherwise>
							 <c:choose>
								 <c:when test="${ltlShipMethod== 'LWA' }">
								   <div class="deliveryLtl"> Incl White Glove <span>With Assembly:</span></div>
								   <div class="deliverypriceLtl">
								   		<span class="deliverypriceLtlClass"><dsp:valueof value="${deliverySurcharge+assemblyFee}" number="0.00" converter="currency"/></span>
								   		<img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" />
								   </div>
								 </c:when>
								<c:otherwise>
								<div class="deliveryLtl">
										 Incl ${ltlShipMethodDesc}:
	                                         </div>     <div class="deliverypriceLtl"><span class="deliverypriceLtlClass">       <c:if test="${deliverySurcharge eq 0.0}"> FREE</c:if>
	                                                                 <c:if test="${deliverySurcharge gt 0.0}"><dsp:valueof value="${deliverySurcharge}" number="0.00" converter="currency"/></c:if>
									</span>  <img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" /></div>
									</c:otherwise>
							</c:choose>
						</c:otherwise>
					 </c:choose>
				 
				 <div class="itemLtl"><c:if test="${ltlShipMethod != null && ltlShipMethod != '' && !shipMethodUnsupported}">Item Price</c:if>
				<span class="itempriceLtl">
				<dsp:droplet name="PriceDroplet">
					<dsp:param name="product" value="${productId}" />
					<dsp:param name="sku" value="${skuID}" />
					<dsp:oparam name="output">
						<dsp:setvalue param="theListPrice" paramvalue="price" />
							<c:if test="${inCartFlagSKU}">
								<dsp:droplet name="PriceDroplet">
										<dsp:param name="product" param="product" />
											<dsp:param name="sku" param="sku" />
											<dsp:param name="priceList" value="${incartPriceList}" />
											<dsp:oparam name="output">
											<dsp:getvalueof var="inCartPrice" vartype="java.lang.Double"
													param="price.listPrice" />
										</dsp:oparam>
									</dsp:droplet>
							</c:if>
						<dsp:getvalueof var="profileSalePriceList"
							bean="Profile.salePriceList" />
						<c:choose>
							<c:when test="${not empty profileSalePriceList}">
								<dsp:droplet name="PriceDroplet">
									<dsp:param name="priceList" bean="Profile.salePriceList" />
									<dsp:oparam name="output">
									<c:choose>
										<c:when test="${inCartFlagSKU}">
										<c:set var="listPrice" value="${inCartPrice}" />
										</c:when>
										<c:otherwise>
											<dsp:getvalueof var="price" vartype="java.lang.Double"
												param="theListPrice.listPrice" />
											<dsp:getvalueof var="listPrice" vartype="java.lang.Double"
												param="price.listPrice" />
										</c:otherwise>
									</c:choose>
										<c:if test="${listPrice gt 0.10}">
										<input type="hidden" name="itemPrice" value="${listPrice}" class="frmAjaxSubmitData" />
											<c:if test="${ltlShipMethod != null && ltlShipMethod != '' && !shipMethodUnsupported}">	
											<dsp:include page="/global/gadgets/formattedPrice.jsp">
												<dsp:param name="price" value="${listPrice }" />
											</dsp:include>
										</c:if>										
										</c:if>										
									</dsp:oparam>
									<dsp:oparam name="empty">
										<c:choose>
											<c:when test="${inCartFlagSKU}">
											 <c:set var="price" value="${inCartPrice}" />
											</c:when>
											<c:otherwise>
												<dsp:getvalueof var="price" vartype="java.lang.Double"
														param="theListPrice.listPrice" />
											</c:otherwise>
										</c:choose>
										<input type="hidden" name="itemPrice" value="${price}" class="frmAjaxSubmitData" />
										<c:if test="${ltlShipMethod != null && ltlShipMethod != '' && !shipMethodUnsupported}">	
										<c:if test="${price gt 0.10}">
											<dsp:include page="/global/gadgets/formattedPrice.jsp">
												<dsp:param name="price" value="${price }" />
											</dsp:include>
										</c:if>
										</c:if>
									</dsp:oparam>
								</dsp:droplet>
								<%-- End price droplet on sale price --%>
							</c:when>
							<c:otherwise>
								<c:choose>
								<c:when test="${inCartFlagSKU}">
									<c:set var="price" value="${inCartPrice}" />
								</c:when>
								<c:otherwise>
									<dsp:getvalueof var="price" vartype="java.lang.Double"
										param="theListPrice.listPrice" />
								</c:otherwise>
							</c:choose>
								<c:if test="${price gt 0.10}">
								<input type="hidden" name="itemPrice" value="${price}" class="frmAjaxSubmitData" />
								<c:if test="${ltlShipMethod != null && ltlShipMethod != '' && !shipMethodUnsupported}">	
									<dsp:include page="/global/gadgets/formattedPrice.jsp">
										<dsp:param name="price" value="${price }" />
									</dsp:include>
								</c:if>
								</c:if>
							</c:otherwise>
						</c:choose>
						<c:if test="${topFragment}">
							<c:if test="${not empty listPrice}">
								<c:set value="${listPrice}" var="price" />
							</c:if>
							<input type="hidden" value="${price}" class="itemPrice" />
							<input type="hidden" name="itemPrice" value="${price}" class="frmAjaxSubmitData" />
						</c:if>
					</dsp:oparam>
				</dsp:droplet>
				</span>
			</div>
			
			</div> 
		</c:when>
		<c:otherwise>
				<div class="price grid_2 omega ltlPrice"><span class="columnHeader">Price</span>
		<c:choose>
			<c:when test="${not empty personalisedCode}">
				<div>
					<dsp:include page="/global/gadgets/formattedPrice.jsp">
						<dsp:param name="price" value="${personlisedPrice}" />
					</dsp:include>
					<span aria-hidden="true" class="fontAreial alignVeritcal">*</span>
					<div class="priceSubject fontAreial">
						<span>*</span> <span class="priceSubString"><bbbl:label
								key='lbl_price_subject_to_change'
								language="${pageContext.request.locale.language}" /></span>
					</div>
				</div>
				<input type="hidden" value="${personlisedPrice}" class="itemPrice" />
			</c:when>
			<c:otherwise>
				<span class="columnHeader rlpPrice">
				<dsp:droplet name="PriceDroplet">
					<dsp:param name="product" value="${productId}" />
					<dsp:param name="sku" value="${skuID}" />
					<dsp:oparam name="output">
						<dsp:setvalue param="theListPrice" paramvalue="price" />
						<c:if test="${inCartFlagSKU}">
							<dsp:droplet name="PriceDroplet">
									<dsp:param name="product" param="product" />
										<dsp:param name="sku" param="sku" />
										<dsp:param name="priceList" value="${incartPriceList}" />
										<dsp:oparam name="output">
										<dsp:getvalueof var="inCartPrice" vartype="java.lang.Double"
												param="price.listPrice" />
									</dsp:oparam>
								</dsp:droplet>
						</c:if>
						<dsp:getvalueof var="profileSalePriceList"
							bean="Profile.salePriceList" />
						<c:choose>
							<c:when test="${not empty profileSalePriceList}">
								<dsp:droplet name="PriceDroplet">
									<dsp:param name="priceList" bean="Profile.salePriceList" />
									<dsp:oparam name="output">
											<c:choose>
										<c:when test="${inCartFlagSKU}">
										<c:set var="listPrice" value="${inCartPrice}" />
										</c:when>
										<c:otherwise>
											<dsp:getvalueof var="price" vartype="java.lang.Double"
												param="theListPrice.listPrice" />
											<dsp:getvalueof var="listPrice" vartype="java.lang.Double"
												param="price.listPrice" />
										</c:otherwise>
									</c:choose>
										<c:if test="${listPrice gt 0.10}">
											<dsp:include page="/global/gadgets/formattedPrice.jsp">
												<dsp:param name="price" value="${listPrice }" />
											</dsp:include>
										</c:if>										
									</dsp:oparam>
									<dsp:oparam name="empty">
										<c:choose>
											<c:when test="${inCartFlagSKU}">
											 <c:set var="price" value="${inCartPrice}" />
											</c:when>
											<c:otherwise>
												<dsp:getvalueof var="price" vartype="java.lang.Double"
														param="theListPrice.listPrice" />
											</c:otherwise>
										</c:choose>
										<c:if test="${price gt 0.10}">
											<dsp:include page="/global/gadgets/formattedPrice.jsp">
												<dsp:param name="price" value="${price }" />
											</dsp:include>
										</c:if>
									</dsp:oparam>
								</dsp:droplet>
								<%-- End price droplet on sale price --%>
							</c:when>
							<c:otherwise>
								<c:choose>
								<c:when test="${inCartFlagSKU}">
									<c:set var="price" value="${inCartPrice}" />
								</c:when>
								<c:otherwise>
									<dsp:getvalueof var="price" vartype="java.lang.Double"
										param="theListPrice.listPrice" />
								</c:otherwise>
							</c:choose>
								<c:if test="${price gt 0.10}">
									<dsp:include page="/global/gadgets/formattedPrice.jsp">
										<dsp:param name="price" value="${price }" />
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
				</dsp:droplet>
				</span>
			</c:otherwise>
		</c:choose>
		</div>
		</c:otherwise>
		</c:choose>

											<div class="requested grid_2 omega">
	                                            <span class="columnHeader"><bbbl:label key='lbl_mng_regitem_requested' language="${pageContext.request.locale.language}" /></span>
	                                            <dsp:input type="hidden" bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].purchasedQuantity" paramvalue="regItem.qtyPurchased" />
	                                            <dsp:getvalueof var="regItemOldQty" param="regItem.qtyRequested" />

	                                            <dsp:input type="hidden" bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].regItemOldQty"
	                                                                            paramvalue="regItem.qtyRequested" id="oldquantityd"/>

	                                            <dsp:input bean="GiftRegistryFormHandler.value.registryId" name="registryId" iclass="registryId frmAjaxSubmitData"
	                                                                        type="hidden" paramvalue="registryId" />
	                                            <dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].rowId"
	                                                type="hidden" paramvalue="regItem.rowID"/>
	                                            <dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].sku"
	                                                type="hidden" paramvalue="regItem.sku"/>

	                                            <dsp:getvalueof var="skuId" param="regItem.sku" />
	                                            <dsp:getvalueof var="rowId" param="regItem.rowID" />
	                                            <input type="hidden" name="skuId" value="${skuId}" class="skuId frmAjaxSubmitData" />
	                                            <input type="hidden" name="rowId" value="${rowId}" class="frmAjaxSubmitData" />
	                                            <input type="hidden" name="itemTypes" value="${itemType}" class="frmAjaxSubmitData" />
	                                            <c:choose>
                                                    	<c:when test="${not empty ltlShipMethod}">
                                                    	<input type="hidden" name="ltlDeliveryServices" value="${ltlShipMethod}" class="frmAjaxSubmitData" />
                                                    	</c:when>
                                                    	<c:otherwise>
                                                    	<input type="hidden" name="ltlDeliveryServices" value="" class="frmAjaxSubmitData" />
                                                    	</c:otherwise>
                                                </c:choose>
	                                             <input type="hidden" name="ltlDeliveryPrices" value="${deliverySurcharge}" class="frmAjaxSubmitData" />
	                                            <input type="hidden" name="refNum" value="${refNum}" class="frmAjaxSubmitData" />
	                                            <input type="hidden" name="personalizationCode" value="${personalizationType}" class="frmAjaxSubmitData" />
                                                    <dsp:droplet name="GiftRegistryFetchProductIdDroplet">
	                                                <dsp:param name="skuId" value="${skuId}" />
	                                                <dsp:oparam name="output">
	                                                    <dsp:getvalueof var="productId" param="productId" />
	                                                    <input type="hidden" name="prodId" value="${productId}" class="frmAjaxSubmitData"/>
	                                                    <dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].productId" type="hidden" paramvalue="productId"/>
	                                                </dsp:oparam>
	                                                <dsp:oparam name="error">
	                                                    <dsp:getvalueof param="errorMsg"  var="errorMsg"/>
	                                                    <div class="error"><bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}"/></div>
	                                                </dsp:oparam>
	                                            </dsp:droplet>
	                                            		  <dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden" value="updateItemRegistry" />
	                                             <dsp:input bean="GiftRegistryFormHandler.queryParam"  type="hidden" value="registryId=${registryId}&eventType=${eventType}" />
	                                            <%-- Client DOM XSRF | Part -1
	                                            <dsp:getvalueof var="ownerUrl" value="${contextPath}/giftregistry/view_registry_owner.jsp?registryId=${registryId}&eventType=${eventType}&sorting=2"/>
	                                            <dsp:input bean="GiftRegistryFormHandler.successURL"  type="hidden" value="${ownerUrl}" />
	                                            <dsp:input bean="GiftRegistryFormHandler.errorURL"	type="hidden" value="${ownerUrl}" /> --%>
	                                            <dsp:getvalueof var="qtyRequested" param="regItem.qtyRequested"/>
												<div class="input alpha marTop_5 clearfix spinner">
														<div class="text clearfix">
	                                                    <%-- 	<dsp:input name="qty" type="text" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" iclass="input_tiny_giftView fl itemQuantity" />  --%>
															<%-- <dsp:input bean="GiftRegistryFormHandler.value.purchasedQuantity" type="text" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" value="${qtyRequested}" name="qty" iclass="valQtyGiftReg input_tiny_giftView fl itemQuantity">
	                                                        <dsp:tagAttribute name="data-change-store-errors" value="required digits nonZero"/>
	                                                        <dsp:tagAttribute name="data-change-store-submit" value="qty"/>
	                                                        </dsp:input> --%>
	                                                        <c:set var="titleqt">
	                                                        	<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />
	                                                        </c:set>

	                                                        <a href="#" class="down button-Med btnSecondary" title="<bbbl:label key='lbl_decrease_quantity' language='${pageContext.request.locale.language}' />">
																<span class="icon-fallback-text">
																	<span class="icon-minus" aria-hidden="true"></span>
																	<span class="icon-text txtOffScreen"><bbbl:label key="lbl_decrease_quantity" language="${pageContext.request.locale.language}" /></span>
																</span>
															</a>
															<dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].quantity" type="text" paramvalue="regItem.qtyRequested" title="${titleqt}" iclass="valQtyGiftReg input_tiny_giftView fl itemQuantity" id="quantityPurchased">
                                                                   <dsp:tagAttribute name="data-change-store-errors" value="required digits nonZero"/>
                                                                   <dsp:tagAttribute name="data-change-store-submit" value="qty"/>
                                                                   <dsp:tagAttribute name="aria-required" value="false"/>
                                                                   <dsp:tagAttribute name="aria-labelledby" value="quantityPurchased"/>
                                                               </dsp:input>
															<a href="#" class="up button-Med btnSecondary" title="<bbbl:label key='lbl_increase_quantity' language='${pageContext.request.locale.language}' />">
																<span class="icon-fallback-text">
																	<span class="icon-plus" aria-hidden="true"></span>
																	<span class="icon-text txtOffScreen"><bbbl:label key="lbl_increase_quantity" language="${pageContext.request.locale.language}" /></span>
																</span>
															</a>

	                                                   		<dsp:input type="hidden" bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].regItemOldQty"
																paramvalue="regItem.qtyRequested" id="oldquantityd" />

															</div>
													</div>
											</div>
											<div class="purchase grid_1">
												<span class="columnHeader"><bbbl:label key='lbl_mng_regitem_purchased' language="${pageContext.request.locale.language}" /></span>
												<dsp:valueof param="regItem.qtyPurchased"/>
											</div>
											<div class="productLastColumn grid_2 omega">
												<dsp:getvalueof var="regItemOldQty" param="regItem.qtyRequested" />
												<input type="hidden" name="regItemOldQty" id="regItemOnLoadQty" value="${regItemOldQty}" class="frmAjaxSubmitData" />
												  <dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden" value="updateItemRegistry" />
												<dsp:input bean="GiftRegistryFormHandler.updateItemToGiftRegistry"
																		id="update${idxPrice}${idxCat}${idx}" 	type="submit" style="display:none" value="Update" onclick="omniRegistryUpdateItem('${productId}','${eventType}','${registryId}', '${skuId}');"/>
												<dsp:input bean="GiftRegistryFormHandler.removeItemFromGiftRegistry"
																		id="remove${idxPrice}${idxCat}${idx}" 	type="submit" style="display:none"  value="Remove" onclick="omniRegistryRemoveItem('${productId}','${eventType}','${registryId}', '${skuId}', '${price}', '${qtyPurchased}');"/>

												<c:set var="valueIndex" value="${valueIndex + 1}"/>
												<div class="">
													<a href="#" data-trigger-button="update${idxPrice}${idxCat}${idx}" class="validateQuantity button-Med btnPrimary" onclick="omniRegistryUpdateItem('${productId}','${eventType}','${registryId}', '${skuId}');">
													<bbbl:label key='lbl_mng_regitem_update' language="${pageContext.request.locale.language}" /></a>
												</div>


												<div class="clear"></div>
												<dsp:getvalueof param="regItem.qtyRequested" var="requestedQuantity" />
												<dsp:getvalueof param="regItem.qtyPurchased" var="purchasedQuantity" />
												<dsp:getvalueof param="regItem.sKUDetailVO.intlRestricted" var="isIntlRestricted" />
												<%-- LTL-328 Hide add to cart button and show not availability message for LTL item --%>
												<dsp:getvalueof var="ltlItemFlag" param="regItem.sKUDetailVO.ltlItem" />
												<c:choose>
													<c:when test="${(isBelowLineItem eq 'false')}">
														<c:choose>
										                    <c:when test="${(requestedQuantity > purchasedQuantity) || (requestedQuantity == 0 && purchasedQuantity ==0)}">
																<div class="addToCart marTop_10 <c:if test="${isInternationalCustomer && (isIntlRestricted || not empty refNum)}">disabled</c:if> <c:if test="${enableKatoriFlag eq false && not empty refNum}">disabled</c:if>">
																	<c:set var="lblAddTOCart"><bbbl:label key='lbl_mng_regitem_additemcart' language="${pageContext.request.locale.language}" /></c:set>																	
																	<input type="button" class="button-Med btnSecondary <c:if test="${empty regAddress}">editRegInfo completeShipping</c:if>" name="<c:if test="${not empty regAddress}">btnAddToCart</c:if>" value="${lblAddTOCart}" role="button" aria-pressed="false" <c:if test="${(isInternationalCustomer && isIntlRestricted) || (isInternationalCustomer && not empty refNum)}">disabled="disabled"</c:if> <c:if test="${enableKatoriFlag eq false && not empty refNum}">disabled</c:if> />
																</div>
																<c:if test="${isInternationalCustomer && isIntlRestricted}">
									                               <div class="notAvailableIntShipMsg regIntShipMsg cb clearfix"><bbbl:label key='lbl_reg_intl_restricted_message' language="${pageContext.request.locale.language}" /></div>
								                                </c:if>
															</c:when>
														    <c:otherwise>
																<div class="purchasedConfirmation">
																	<span class="icon-fallback-text">
																		<span class="icon-checkmark" aria-hidden="true"></span>
																		<span class="icon-text"><bbbl:label key='lbl_mng_regitem_purchased_confirm' language="${pageContext.request.locale.language}" /></span>
																	</span>
																  	<bbbl:label key='lbl_mng_regitem_purchased_confirm' language="${pageContext.request.locale.language}" />
																</div>
															</c:otherwise>
														</c:choose>
														<%--  <dsp:droplet name="IsProductSKUShippingDroplet">
																			<dsp:param name="siteId" value="${currentSiteId}"/>

																			<dsp:param name="skuId" value="${skuID}"/>
																				<dsp:oparam name="true">
																				<div class="fl clearfix cb padTop_10">

																				<div class="clearfix">

																                	<dsp:getvalueof var="restrictedAttributes" param="restrictedAttributes"/>
																					<c:forEach var="item" items="${restrictedAttributes}">

																					<c:choose>
																					<c:when test="${null ne item.actionURL}">
																					 <a href="${item.actionURL}" class="popup"><span>${item.attributeDescrip}</span></a>
																					</c:when>
																					<c:otherwise>
																					${item.attributeDescrip}
																					</c:otherwise>
																					</c:choose>
																					</c:forEach>
														              			</div>
														              			</div>
																			</dsp:oparam>
																			<dsp:oparam name="false">
																			</dsp:oparam>
																			</dsp:droplet> --%>
													</c:when>
													<c:otherwise>

															<span class="itemNotAvailable block padTop_10"><bbbl:label key='lbl_mng_item_belowline' language="${pageContext.request.locale.language}" /></span>
													</c:otherwise>
												</c:choose>
													<c:choose>
														<c:when test="${not empty refNum && refNum ne null}">
															<c:choose>
																<c:when test="${purchasedQuantity gt 0}">
																	<c:set var="hideRemoveButtonCustomProd" value="true" scope="request"/>
																</c:when>
																<c:otherwise>
																	<c:set var="hideRemoveButtonCustomProd" value="false" scope="request"/>
																</c:otherwise>
															</c:choose>
														</c:when>
														<c:otherwise>
															<c:set var="hideRemoveButtonCustomProd" value="false" scope="request"/>
												       </c:otherwise>
												    </c:choose>
												<a href="#" title="Remove registry" role="link" aria-label="Remove quantity ${lblForThe} ${productName}" data-submit-button="remove${idxPrice}${idxCat}${idx}" class="btnAjaxSubmitRegistry <c:if test="${hideRemoveButtonCustomProd}"> hidden </c:if> buttonTextLink" onclick="omniRegistryRemoveItem('${productId}','${eventType}','${registryId}', '${skuId}', '${price}', '${qtyPurchased}');">
													<span class="icon-fallback-text">
														<span class="icon-times" aria-hidden="true"></span>
														<span class="icon-text"><bbbl:label key='lbl_mng_regitem_remove' language="${pageContext.request.locale.language}" /></span>
													</span>
													<bbbl:label key='lbl_mng_regitem_remove' language="${pageContext.request.locale.language}" />
												</a>

												<c:set var="writeReviewOn" scope="request"><bbbc:config key="writeReviewOn" configName="FlagDrivenFunctions" /></c:set>
												<c:if test="${writeReviewOn}">
													<dsp:droplet name="/com/bbb/commerce/browse/droplet/ProductStatusDroplet">
														<dsp:param name="productId" value="${productId}" />
														<dsp:oparam name="output">
															<dsp:getvalueof var="isProductActive" param="isProductActive" />
														</dsp:oparam>
													</dsp:droplet>
													<c:if test="${isProductActive && purchasedQuantity>0}">
														<a  href="#" title="write a review" role="link" aria-label="${writeReviewLink} ${lblAboutThe} ${productName}" data-BVProductId="${productId}" class="buttonTextLink triggerBVsubmitReview">
															<bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" />
														</a>
													</c:if>
												</c:if>
											</div>
										</div>

										<div class="productFooter clearfix">
											<div>
												<div class="noMar clearfix freeShipContainer prodAttribWrapper">
													<dsp:droplet name="/atg/dynamo/droplet/ForEach">
														<dsp:param name="array" param="regItem.sKUDetailVO.skuAttributes" />
														<dsp:param name="elementName" value="attributeVOList"/>
														<dsp:oparam name="output">
															<dsp:getvalueof var="pageName" param="key" />
															<c:if test="${pageName eq 'RLP'}">
																	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	<dsp:param param="attributeVOList" name="array" />
																	<dsp:param name="elementName" value="attributeVO"/>
																	<%-- <dsp:param name="sortProperties" value="+priority"/> --%>
																		<dsp:oparam name="output">
																            <c:set var="currentCount"><dsp:valueof param="count" /></c:set>
																            <c:set var="actionURL"><dsp:valueof param="attributeVO.actionURL" /></c:set>
																            <c:set var="attributeName"><dsp:valueof param="attributeVO.attributeName" /></c:set>
																				<c:if test="${not empty actionURL}">
																           			<c:choose>
																						<c:when test="${not empty skuID && null ne attributeName && fn:contains(attributeName,vdcAttributesList)}">
																						  	<div <c:if test="${fn:contains(actionURL,'ltlDeliveryInfo') }">class="ltlTruck"</c:if>><a href="/store/browse/dynamicStaticContent.jsp?pageName=ItemShippedDirectlyFromVendor&isVdcSku=true&skuId=${skuID}" class="popup"><span><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></span></a></div>
																						</c:when>
																						<c:otherwise>
																							<div <c:if test="${fn:contains(actionURL,'ltlDeliveryInfo') }">class="ltlTruck"</c:if>><a href="${actionURL}?skuID=${skuID}" class="popup"><span><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></span></a></div>	
																						</c:otherwise>																					
																					</c:choose>
																					<c:choose>
											                          				<c:when test="${currentCount%2 == 0}">
															            				<c:set var="nextStyle">prodSecondaryAttribute</c:set>
															            			</c:when>
															            			<c:otherwise>
																						<c:set var="nextStyle">prodSecondaryAttribute</c:set>
																					</c:otherwise>
															            			</c:choose>
																				</c:if>
																		</dsp:oparam>
																	</dsp:droplet>
																	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	<dsp:param param="attributeVOList" name="array" />
																	<dsp:param name="elementName" value="attributeVO"/>
																	<dsp:param name="sortProperties" value="+priority"/>
																		<dsp:oparam name="output">
																            <c:set var="currentCount"><dsp:valueof param="count" /></c:set>
																            <c:set var="actionURL"><dsp:valueof param="attributeVO.actionURL" /></c:set>
																            <c:set var="attributeName"><dsp:valueof param="attributeVO.attributeName" /></c:set>
																			<c:if test="${empty actionURL}">
																				<div><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></div>
																				<c:choose>
											                          			<c:when test="${currentCount%2 == 0}">
															            			<c:set var="nextStyle">prodSecondaryAttribute</c:set>
															            		</c:when>
															            		<c:otherwise>
																					<c:set var="nextStyle">prodSecondaryAttribute</c:set>
																				</c:otherwise>
															            		</c:choose>
																			</c:if>
																		</dsp:oparam>
																	</dsp:droplet>
																<%-- <dsp:droplet name="/atg/dynamo/droplet/ForEach">
																<dsp:param param="attributeVOList" name="array" />
																<dsp:param name="elementName" value="attributeVO"/>
																<dsp:param name="sortProperties" value="+priority"/>
																	<dsp:oparam name="output">
															            <c:set var="currentCount"><dsp:valueof param="count" /></c:set>
															            <c:set var="actionURL"><dsp:valueof param="attributeVO.actionURL" /></c:set>
															            <c:set var="attributeName"><dsp:valueof param="attributeVO.attributeName" /></c:set>
																	    <c:choose>
																			<c:when test="${not empty actionURL}">
																				<c:choose>
																					<c:when test="${not empty skuID && null ne attributeName && fn:contains(attributeName,vdcAttributesList)}">
																					  	<li><a href="/store/browse/dynamicStaticContent.jsp?pageName=ItemShippedDirectlyFromVendor&isVdcSku=true&skuId=${skuID}" class="popup"><span><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></span></a></li>
																					</c:when>
																					<c:otherwise>
																						<li  <c:if test="${fn:contains(actionURL,'ltlDeliveryInfo') }">class="ltlTruck"</c:if>><a href="${actionURL}?skuID=${skuID}" class="popup"><span><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></span></a></li>	
																					</c:otherwise>																					
																				</c:choose>
																			</c:when>
																			<c:otherwise>
																				<li><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></li>
																			</c:otherwise>
																		</c:choose>
																		<c:choose>
										                          			<c:when test="${currentCount%2 == 0}">
														            			<c:set var="nextStyle">prodSecondaryAttribute</c:set>
														            		</c:when>
														            		<c:otherwise>
																				<c:set var="nextStyle">prodSecondaryAttribute</c:set>
																			</c:otherwise>
														            	</c:choose>

																	</dsp:oparam>
																</dsp:droplet> --%>
															</c:if>
														</dsp:oparam>
													</dsp:droplet>
													<%-- <c:if test="${skuDetailVO.shippingSurcharge gt 0.0}">
														<li class="${nextStyle}"><bbbl:label key="lbl_mng_surcharge" language="<c:out param='${language}'/>"/>${skuDetailVO.shippingSurcharge}</li>
													</c:if> --%>
												</div>
											</div>

										</div>
									</div>


									</li>
								</dsp:oparam>
							</dsp:droplet>
							<%-- <c:if test="${count ne 0 }">
								<li class="noBorder"><a href="#backToTop"><bbbl:label key='lbl_mng_regitem_back_to_top' language="${pageContext.request.locale.language}" /></a></li>
							</c:if> --%>
							</ul>
						</div>
					</c:if>
				</dsp:oparam>
			</dsp:droplet>
			<c:if test="${countIE eq sizeIE}">
				</div>
			</c:if>
		</dsp:oparam>
	</dsp:droplet>
	<dsp:getvalueof var="totEntries" param="totEntries"/>
	<c:if test="${totEntries eq 0 && isMyItemsCheckList ne 'false'}">
		<ul class="emptyExplArea">
			<li class="emptyExplMsg"><bbbl:label key='lbl_ic_mkt_msg'
					language="${pageContext.request.locale.language}" /></li>
					<li class="emptyExplBtn">
				<c:choose>
					<c:when test="${view == 3 && giftsRegistered gt 0}">
						<a href="#" class="button-Med btnSecondary checkListShare"><bbbl:label
								key='lbl_share_reg_friends'
								language="${pageContext.request.locale.language}" /></a>
					</c:when>
					<c:when test="${view == 2 && giftsRegistered gt 0}">
						<li class="emptyExplBtn">
						<a href="#" class="button-Med btnSecondary" id="checklistbrowseAddGifts">
							<bbbl:label key="lbl_registry_browse_add_gifts_tab" language="${pageContext.request.locale.language}" />
						</a>
						</li>
						<li class="emptyExplBtn">
						<dsp:a href="../view_registry_owner.jsp" id="seeAllItems" bean="GiftRegistryFormHandler.viewEditRegistry" value=""  iclass="seeAllItemLink" requiresSessionConfirmation="false">
							<dsp:param name="registryId" value="${registryId}" />
							<dsp:param name="eventType" value="${eventType}" />
							<bbbl:label key='lbl_ic_see_all_items' language="${pageContext.request.locale.language}" />
						</dsp:a>
						</li>
					</c:when>
					<c:otherwise>
						<a href="#" class="button-Med btnSecondary"><bbbl:label
								key='lbl_explore_checklist'
								language="${pageContext.request.locale.language}" /></a>
					</c:otherwise>
				</c:choose>
			</li>
		</ul>
	</c:if>
	</dsp:form>
</dsp:page>
