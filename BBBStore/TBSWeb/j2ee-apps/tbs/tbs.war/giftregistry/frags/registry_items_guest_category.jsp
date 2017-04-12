<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFetchProductIdDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/IsProductSKUShippingDroplet"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean"/>
	<dsp:importbean bean="/com/bbb/commerce/droplet/TBSItemExclusionDroplet"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<c:set var="TBS_BuyBuyBabySite"><bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" /></c:set>
	<c:set var="othersCat"><bbbc:config key="DefaultCategoryForRegistry" configName="ContentCatalogKeys" /></c:set>
	<%-- Droplet Placeholder --%>
	<dsp:getvalueof var="registryId" param="registryId" />
	<dsp:getvalueof var="skuId" value="" />
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="sortSeq" param="sorting"/>
	<dsp:getvalueof var="view" param="view"/>
	<dsp:getvalueof var="eventType" param="eventType" />
	<dsp:getvalueof var="eventTypeCode" param="eventTypeCode" />
	<dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
	<dsp:getvalueof var="isGiftGiver" param="isGiftGiver" />
	<c:set var="button_active" value="button_cart button_active"/>
	<c:set var="scene7Path">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
	<c:if test="${eventType eq 'Baby' && currentSiteId ne TBS_BuyBuyBabySite}">
		<c:set var="button_active" value=""/>
	</c:if>
	<c:set var="customizeCTACodes">
		<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
	</c:set>
	<c:set var="TBS_BedBathCanadaSite"><bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" /></c:set>
	<c:set var="TBS_BedBathUSSite">
			<bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:choose>
			<c:when test="${currentSiteId eq TBS_BedBathUSSite || currentSiteId eq TBS_BuyBuyBabySite}">
				<c:set var="incartPriceList"><bbbc:config key="BedBathUSInCartPriceList" configName="ContentCatalogKeys" /></c:set>
			</c:when>
			<c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
				<c:set var="incartPriceList"><bbbc:config key="BedBathCanadaInCartPriceList" configName="ContentCatalogKeys" /></c:set>
			</c:when>
	</c:choose>
 <dl class="accordion" data-accordion="">
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="inStockCategoryBuckets" />
		<dsp:oparam name="output">
		<dsp:getvalueof var="countIE" param="count"/>
		<dsp:getvalueof var="sizeIE" param="size"/>
			<%-- <c:choose>
                <c:when test="${countIE eq 1}">
                    <dd class="accordion-navigation accordionReg accordionReg1 active">
                </c:when>
                <c:when test="${countIE eq 5}">
                    </dd>
                    <div class="ajaxLoadWrapper load2 loadMore hidden">
                        <bbbt:textArea key="txt_viewregistry_imageloader" language ="${pageContext.request.locale.language}"/>
                    </div>
                    <dd class="accordion-navigation accordionReg accordionReg2 active">
                </c:when>
                <c:when test="${countIE eq 12}">
                    </dd>
                    <div class="ajaxLoadWrapper load3 loadMore hidden">
                        <bbbt:textArea key="txt_viewregistry_imageloader" language ="${pageContext.request.locale.language}"/>
                    </div>
                    <dd class="accordion-navigation accordionReg accordionReg3 active">
                </c:when>
            </c:choose> --%>

			<dsp:getvalueof var="bucketName" param="key" idtype="java.lang.String" />
			<dsp:getvalueof var="bucket" param="inStockCategoryBuckets.${bucketName}" idtype="java.util.List" />
            <dsp:getvalueof var="bucketNameNew" value="${fn:replace(bucketName, '$','')}"/>
                        <dsp:getvalueof var="bucketNameNew" value="${fn:replace(bucketNameNew, '-','')}"/>
			<c:if test="${bucketName ne othersCat}">
				<dsp:getvalueof var="count" value="${fn:length(bucket)}"/>
				<c:if test="${count ne 0}">
                    <dd class="accordion-navigation accordionReg accordionReg active">
					<a href="#accordionDiv${fn:replace(bucketNameNew, ' ','')}${count}"><dsp:valueof param="key" />(${count})</a>
				</c:if>
				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="inStockCategoryBuckets.${bucketName}" />
					<dsp:param name="elementName" value="regItem" />
                    <dsp:oparam name="outputStart">
                        <div id="accordionDiv${fn:replace(bucketNameNew, ' ','')}${count}" class="accordionDiv content active">
                    </dsp:oparam>
					<dsp:oparam name="output">
						<c:if test="${count ne 0}">
							<dsp:getvalueof var="skuID" vartype="java.lang.String"  param="regItem.sku" />
							<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
								<dsp:param name="skuId" value="${skuID}" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="productId" vartype="java.lang.String" param="productId" />
									<dsp:getvalueof var="inStoreSku" param="inStore" />
									<c:set var="productId1" value="${productId1}${productId},"/>
								</dsp:oparam>
							</dsp:droplet>
							<dsp:getvalueof var="bucketName" param="key" idtype="java.lang.String" />
							<dsp:getvalueof var="bucket" param="inStockCategoryBuckets.${bucketName}" idtype="java.util.List" />
							
							<dsp:getvalueof var="ltlShipMethod" param="regItem.ltlDeliveryServices" />
							<dsp:getvalueof var="ltlShipMethodDesc" param="regItem.ltlShipMethodDesc"/>
							<dsp:getvalueof var="shipMethodUnsupported" param="regItem.shipMethodUnsupported"/>
										
									<div class="clearfix productRow btngiftViewTopMargin giftViewProductChangeStoreWrapper addToCartGiftRegWrapper">
										<input type="hidden" name="bts" value="false" class="addToCartSubmitData bts" data-change-store-submit="bts" />
						                 <input type="hidden" name="storeId" value="" data-change-store-storeid="storeId" class="addToCartSubmitData" />
										<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
											<dsp:param name="id" value="${productId}"/>
											<dsp:param name="itemDescriptorName" value="product" />
											<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
											</dsp:oparam>
										</dsp:droplet>
										<dsp:getvalueof var="disable" param="regItem.sKUDetailVO.disableFlag" />
										<dsp:getvalueof var="webOffered" param="regItem.sKUDetailVO.webOfferedFlag" />
										<dsp:getvalueof var="active" param="regItem.sKUDetailVO.activeFlag" />
										<dsp:getvalueof var="personalizedImageUrls" param="regItem.personalizedImageUrls"/>
										<dsp:getvalueof var="personalizedImageUrlThumbs" param="regItem.personalizedImageUrlThumbs"/>
										<dsp:getvalueof var="personalisedCode" param="regItem.personalisedCode"/>
										<dsp:getvalueof var="personalizationOptionsDisplay" param="regItem.personalizationOptionsDisplay"/>
										<dsp:getvalueof var="customizedPrice" vartype="java.lang.Double" param="regItem.customizedDoublePrice"/>
										<dsp:getvalueof var="personlisedPrice" vartype="java.lang.Double" param="regItem.personlisedPrice"/>
										<c:set var="customizedPrice1"><dsp:valueof value="${customizedPrice}" converter="currency"/></c:set>
	                                    <c:set var="personlisedPrice1"><dsp:valueof value="${personlisedPrice}" converter="currency"/></c:set>
										<dsp:getvalueof var="customizationDetails" param="regItem.customizationDetails"/>
										<dsp:getvalueof var="refNum" param="regItem.refNum"/>
										<dsp:getvalueof var="personalizationType" param="regItem.sKUDetailVO.personalizationType" />
									
										<c:if test="${not empty personalisedCode}">
											<c:set var="cusDet">${eximCustomizationCodesMap[personalisedCode]}</c:set>									
											<input type="hidden" value="${cusDet}" id="customizationDetails"/> 
										</c:if>
										<c:choose>
											<c:when test="${not empty personalisedCode && fn:contains(customizeCTACodes, personalisedCode)}">
												<c:set var="customizeTxt" value="true"/>
											</c:when>
											<c:otherwise>
												<c:set var="customizeTxt" value="false"/>
											</c:otherwise>
										</c:choose>
										<dsp:getvalueof var="ltlItemFlag" param="regItem.SKUDetailVO.ltlItem" />
										<c:set var="productName">
											<dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/>
										</c:set>
										<div class="productImage small-4 medium-2 columns print-2">
										
											<c:choose>
												<c:when test="${ltlItemFlag}">
												<span <c:if test="${not empty refNum}"> " oncontextmenu="return false;" </c:if> class="lnkQVPopup" data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}&registryView=guest"
												 title="${productName}">
													<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.mediumImage" />
													<c:choose>
														<c:when test="${empty imageURL}">
															<img src="${imagePath}/_assets/global/images/no_image_available.jpg"  alt="${productName}" class="prodImage" />
														</c:when>
														<c:otherwise>
															<img src="${scene7Path}/${imageURL}" data-original="${scene7Path}/${imageURL}" alt="${productName}" class="prodImage stretch" />
														</c:otherwise>
													</c:choose>
												</span>
												</c:when>
												<c:otherwise>
												
												<c:if test="${active}">
												<a <c:if test="${not empty refNum}"> " oncontextmenu="return false;" </c:if> href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}"
												 class="lnkQVPopup prodTitle" data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}&registryView=guest"
												 title="${productName}" data-skuId="${skuID}" data-disableItemDetails="true" data-registryId="${registryId}" data-sopt="${ltlShipMethod}" data-productId="${productId}" data-refNum="${refNum}" <c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
															data-customizedPrice="${customizedPrice1}" data-customizationDetails="${customizationDetails}" data-color="${color}" data-size="${size}" data-personalizedImageUrls="${personalizedImageUrls}"
															data-personlisedPrice="${personlisedPrice1}" data-personalizedImageUrlThumbs="${personalizedImageUrlThumbs}" data-personalizationType= "${personalizationType}"</c:if>>  </c:if>
													<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.mediumImage" />
												
													<c:choose>
													    <c:when test="${not empty refNum && not empty personalizedImageUrlThumbs}">
                                             						<img src="${personalizedImageUrlThumbs}" alt="<c:out value='${productName}'/>" class="prodImage stretch" >
                                             			</c:when>
														<c:when test="${empty imageURL}">
															<img src="${imagePath}/_assets/global/images/no_image_available.jpg"  alt="${productName}" class="prodImage" />
														</c:when>
														<c:otherwise>
															<img src="${scene7Path}/${imageURL}" data-original="${scene7Path}/${imageURL}" alt="${productName}" class="prodImage stretch" />
														</c:otherwise>
													</c:choose>
												<c:if test="${active}">
												 </a>
												</c:if>
												</c:otherwise>
											</c:choose>
											
										</div>
										<div class="productContainer small-8 medium-10 columns no-padding-right print-10">
											<div class="productTab productHeading clearfix">
												<div class="productName small-12 medium-3 columns print-3">
													<dsp:getvalueof var="upc" param="regItem.sKUDetailVO.upc" />
													<dsp:getvalueof var="color" param="regItem.sKUDetailVO.color" />
													<dsp:getvalueof var="size" param="regItem.sKUDetailVO.size" />
													<dsp:getvalueof var="skuInCartFlag" param="regItem.sKUDetailVO.inCartFlag" />
														<c:if test="${upc ne null}">
															<div class="fl upc"><span class="bold"> <bbbl:label key='lbl_mng_regitem_upc' language="${pageContext.request.locale.language}" /> </span>
															<span>${upc}</span></div>
														</c:if>
												</div>
												<div class="small-1 medium-2 columns price print-2 hide-for-small-only"><bbbl:label key='lbl_mng_regitem_price' language="${pageContext.request.locale.language}" /></div>
												<div class="small-2 medium-1 columns requested print-2 hide-for-small-only no-padding"><bbbl:label key='lbl_mng_regitem_requested' language="${pageContext.request.locale.language}" /></div>
												<div class="small-2 medium-1 columns purchase print-2 hide-for-small-only no-padding"><bbbl:label key='lbl_mng_regitem_purchased' language="${pageContext.request.locale.language}" /></div>
												<div class="small-2 columns quantity print-2 hide-for-small-only"><bbbl:label key='lbl_mng_regitem_quantity' language="${pageContext.request.locale.language}" /></div>
												<div class="small-1 columns productLastColumn print-1 hide-for-small-only"> </div>
											</div>
											<div class="row productTab productContent clearfix">
												<div class="productName small-12 medium-3 columns print-3">
													<c:choose>
														<c:when test="${active}">
														<span class="blueName">
														<c:choose>
															<c:when test="${ltlItemFlag}">
																<span <c:if test="${not empty refNum}"> " oncontextmenu="return false;" </c:if> class="lnkQVPopup prodTitle" data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}&registryView=guest"
																title="${productName}">
																	<dsp:getvalueof var="displayName" param="regItem.sKUDetailVO.displayName" />
																	${productName}
																</span>
															</c:when>
															<c:otherwise>
															<a <c:if test="${not empty refNum}"> " oncontextmenu="return false;" </c:if> href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}"
															class="lnkQVPopup prodTitle" data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}&registryView=guest"
															title="${productName}" data-skuId="${skuID}" data-disableItemDetails="true" data-registryId="${registryId}" data-sopt="${ltlShipMethod}" data-productId="${productId}" data-refNum="${refNum}" <c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
															data-customizedPrice="${customizedPrice1}" data-customizationDetails="${customizationDetails}" data-color="${color}" data-size="${size}" data-personalizedImageUrls="${personalizedImageUrls}"
															data-personlisedPrice="${personlisedPrice1}" data-personalizedImageUrlThumbs="${personalizedImageUrlThumbs}" data-personalizationType= "${personalizationType}"</c:if>>
																<dsp:getvalueof var="displayName" param="regItem.sKUDetailVO.displayName" />
																${productName}
															</a>
															</c:otherwise>
														</c:choose>
														</span><br/>
														</c:when>
														<c:otherwise>
															<span class="blueName prodTitle">
																<dsp:getvalueof var="displayName" param="regItem.sKUDetailVO.displayName" />
																<dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/>
															</span><br/>
														</c:otherwise>
													</c:choose>
                                                    <span class="prodColor noPad"><c:if test='${not empty color}'><bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/>&nbsp;:&nbsp;<dsp:valueof value="${color}" valueishtml="true" /></c:if><c:if test='${not empty size}'><c:if test='${not empty color}'><br/></c:if><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/>&nbsp;:&nbsp;<dsp:valueof value="${size}" valueishtml="true" /></c:if></span>
                                                    <div class="personalizationAttributes"> 
                                                       <c:if test="${not empty personalisedCode}">
                                                            <span class="exim-code">${eximCustomizationCodesMap[personalisedCode]}:</span>
				        									<span class="exim-detail">${customizationDetails}</span>
                                                       </c:if>
                                                 	</div>
	                                                <div class="personalizationAttributes">
                                                       <div class="pricePersonalization">
                                                           <c:choose>
	                                                           <c:when test="${personalizationType == 'PB'}">
	                                                           		<span class="fee-detail">
	                                                                    <bbbl:label key='lbl_PB_Fee_detail' language="${pageContext.request.locale.language}" />
	                                                                </span>                                                          
	                                                           </c:when>
	                                                           <c:when test="${personalizationType == 'PY'}">
                                                                     <span class="cust-price">
                                                                     	<dsp:valueof value="${customizedPrice1}"/>
                                                                     </span>	
                                                                     <span class="fee-detail">
                                                                     	<bbbl:label key='lbl_exim_added_price' language="${pageContext.request.
                                                                     	locale.language}" />
                                                                     </span>
	                                                           </c:when>
	                                                           <c:when test="${personalizationType == 'CR'}">
	                                                               		<span class="cust-price">
			                                                               <dsp:include page="/global/gadgets/formattedPrice.jsp">  
			                                                               			<dsp:param name="price" value="${customizedPrice}"/>
			                                                               </dsp:include>
	                                                               		</span>
	                                                               <span class="fee-detail">
	                                                               	    <c:choose>
																	 		<c:when test="${customizeTxt eq true}">
																	 			<bbbl:label key="lbl_exim_cr_added_price_customize"
																								language="${pageContext.request.locale.language}"/>
																	 		</c:when>
																	 		<c:otherwise>
																	 			<bbbl:label key="lbl_exim_cr_added_price"
																								language="${pageContext.request.locale.language}"/>
																	 		</c:otherwise>
																	 	</c:choose>
	                                                               </span>
	                                              			   </c:when>
                                                           </c:choose>
                                                       </div>
	                                                </div>

												</div>
												 <dsp:getvalueof var="ltlItemFlag" param="regItem.SKUDetailVO.ltlItem" />
											<c:choose>
													<c:when test="${ltlItemFlag}">
													<dsp:getvalueof var="ltlShipMethodDesc" param="regItem.ltlShipMethodDesc"/>
													<dsp:getvalueof var="deliverySurcharge" param="regItem.deliverySurcharge"/> 
													<dsp:getvalueof var="assemblyFee" param="regItem.assemblyFees"/>
													<dsp:getvalueof var="shipMethodUnsupported" param="regItem.shipMethodUnsupported"/>

												       <div class="price bold small-12 medium-2 columns print-2">
															  <dsp:droplet name="PriceDroplet">
																<dsp:param name="product" value="${productId}" />
																<dsp:param name="sku" value="${skuID}" />
																<dsp:oparam name="output">
																	<dsp:setvalue param="theListPrice" paramvalue="price" />
																	<c:if test="${skuInCartFlag}">
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
																					<dsp:getvalueof var="price" vartype="java.lang.Double"
																						param="theListPrice.listPrice" />
																					<c:choose>
																						<c:when test="${skuInCartFlag}">
																							<c:set var="listPrice" value="${inCartPrice}" />
																						</c:when>
																						<c:otherwise>
																							<dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
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
																						<c:when test="${skuInCartFlag}">
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
																				<c:when test="${skuInCartFlag}">
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
															</dsp:droplet>
															<li class="price grid_2 omega ltlPrice">
															<ul>
															<c:choose>
																<c:when test="${ltlShipMethod == null or ltlShipMethod == '' or shipMethodUnsupported}">
																	<li class="deliveryLtl"><bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/>:</li>
																	<li class="deliverypriceLtl">
																		<span class="deliverypriceLtlClass">TBD</span>
																		<img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" />
																	</li>
																 </c:when>
																<c:otherwise>
																	 <c:choose>
																		 <c:when test="${ltlShipMethod== 'LWA' }">
																		   <li class="deliveryLtl">  Incl White Glove <span>With Assembly:</span></li>
																		   <li class="deliverypriceLtl"><span class="deliverypriceLtlClass"><dsp:valueof value="${deliverySurcharge+assemblyFee}" number="0.00" converter="currency"/></span>
																		   <img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" /></li>
																		</c:when>
																		<c:otherwise>
																		<li class="deliveryLtl">
																				 Incl ${ltlShipMethodDesc}:
																					 </li>     <li class="deliverypriceLtl"><span class="deliverypriceLtlClass">       <c:if test="${deliverySurcharge eq 0.0}"> FREE</c:if>
																											 <c:if test="${deliverySurcharge gt 0.0}"><dsp:valueof value="${deliverySurcharge}" number="0.00" converter="currency"/></c:if>
																			</span>  <img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" /></li>
																			</c:otherwise>
																	</c:choose>
																</c:otherwise>
															 </c:choose>
															<c:if test="${ltlShipMethod != null && ltlShipMethod != '' && !shipMethodUnsupported}">		 
															  <li class="itemLtl">Item Price
																<span class="itempriceLtl">
																	<dsp:include page="registry_items_guest_category_frag.jsp">
																		<dsp:param name="productId" value="${productId}"/>
																		<dsp:param name="skuID" value="${skuID}"/>
																		<dsp:param name="skuInCartFlag" value="${skuInCartFlag}"/>
																		<dsp:param name="incartPriceList" value="${incartPriceList}"/>				
																	</dsp:include>	
																</span>
															  </li> 
															  </c:if>
															  </ul>
															  </li>
															</div>  
												</c:when>
												<c:otherwise>
												<div class="price bold small-2 columns">
													<c:choose>
															<c:when test="${not empty personalisedCode}">
																<div><dsp:include page="/global/gadgets/formattedPrice.jsp">												
													               <span><dsp:param name="price" value="${personlisedPrice}"/>
													        	</dsp:include></span>															
																</div>
																<input type="hidden" value="${personlisedPrice}" class="itemPrice"/>
															</c:when>
															<c:otherwise>
																<span class="columnHeader rlpPrice">
																<dsp:include page="registry_items_guest_category_frag.jsp">
																	<dsp:param name="productId" value="${productId}"/>
																	<dsp:param name="skuID" value="${skuID}"/>
																	<dsp:param name="skuInCartFlag" value="${skuInCartFlag}"/>
																	<dsp:param name="incartPriceList" value="${incartPriceList}"/>
																</dsp:include>
																</span>
										            </c:otherwise>
											             	</c:choose>
										          </div>
												</c:otherwise>
												</c:choose>

												<div class="requested bold small-12 medium-1 columns print-2"><dsp:valueof param="regItem.qtyRequested" /></div>
												<div class="purchase bold small-12 medium-1 columns print-2"><dsp:valueof param="regItem.qtyPurchased" /></div>
												<div class="quantity bold  small-12 medium-2 columns print-2">
												<c:choose>
													<c:when test="${((active || inStoreSku) || (currentSiteId ne TBS_BedBathCanadaSite))}">
													<div id="shopCollection" class="input alpha">
														<div class="qty-spinner">
															<a href="#" class="button minus secondary" title="<bbbl:label key='lbl_decrease_quantity' language="${pageContext.request.locale.language}" />"><span/></a>
															<input name="qty" type="text" value="1" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="input_tiny_giftView quantity-input qty itemQuantity" data-change-store-submit="qty" data-change-store-errors="required digits nonZero" role="textbox" aria-required="false" maxlength="2" />
															<a href="#" class="button plus secondary" title="<bbbl:label key='lbl_increase_quantity' language="${pageContext.request.locale.language}" />"><span/></a>
														</div>
													</div>
													</c:when>
													<c:otherwise>1</c:otherwise>
												</c:choose>
												</div>
												<div class="productLastColumn small-12 medium-2 columns print-2">
												<dsp:getvalueof var="skuID" param="regItem.sku" />
												<input type="hidden" name="skuId" value="${skuID}" class="skuId changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" />
												<input type="hidden" name="registryId" value="${registryId}" class="registryId" data-change-store-submit="registryId" data-change-store-errors="required"  />
													<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
														<dsp:param name="skuId" value="${skuID}" />
														<dsp:oparam name="output">
															<dsp:getvalueof var="productId" param="productId" />
															<input type="hidden" name="prodId" value="${productId}" class="productId" data-change-store-submit="prodId"/>
														</dsp:oparam>
														<dsp:oparam name="error">
															<dsp:getvalueof param="errorMsg" var="errorMsg" />
															<div class="error">
																<bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}" />
															</div>
														</dsp:oparam>
													</dsp:droplet>
                                                    
													<dsp:getvalueof param="regItem.qtyRequested" var="requestedQuantity" />
													<dsp:getvalueof param="regItem.qtyPurchased" var="purchasedQuantity" />

													<c:choose>
														 <c:when test="${requestedQuantity > purchasedQuantity}">
															<dsp:droplet name="TBSItemExclusionDroplet">
																<dsp:param name="skuId" value="${skuID}" />
																<dsp:param name="siteId" value="${currentSiteId}" />
																<dsp:oparam name="output">
																<dsp:getvalueof var="isItemExcluded" param="validItemExcludedSku"/>
																<dsp:getvalueof var="caDisabled" param="caDisabled"/>
																<dsp:getvalueof var="reasonCode" param="reasonCode"/>
																<c:choose>
																	<c:when test="${isItemExcluded || caDisabled || (enableKatoriFlag eq false && not empty refNum)}">
																		<div class="button_disabled addToCart hidden  marBottom_5">
																			<input class="tiny button" type="button" disabled="disabled" name="btnAddToCart" title="Add to cart" value="Add to Cart" role="button" aria-pressed="false" />
																		</div>
																		<div style="color:red;font-weight:bold;">
                                    								<dsp:include page="/browse/frag/reasonCodeMessage.jsp" flush="true" >
																		<dsp:param name="reasonCode" value="${reasonCode}"/>
																	</dsp:include>
                                									</div>
																	</c:when>
																	<c:otherwise>
																		<div class="${button_active} addToCart marBottom_5">
																			<input class="tiny button" type="button" name="btnAddToCart" title="Add to cart" value="Add to Cart" role="button" aria-pressed="false" />
																		</div>
																	</c:otherwise>
																</c:choose>
																</dsp:oparam>
															</dsp:droplet>
														 </c:when>
														 <c:otherwise>
															<div class="purchasedConfirmation">
															  <h2><bbbl:label key='lbl_mng_regitem_purchased_confirm' language="${pageContext.request.locale.language}" /></h2>
															</div>																	
														</c:otherwise>
													</c:choose>															
													<dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>															
													<dsp:getvalueof var="userRegCount" value="${fn:length(sessionMapValues.userRegistriesList)}"/>
													<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO.registryId" var="sessionRegId"/>
													 <dsp:getvalueof var="ltlItemFlag" param="regItem.sKUDetailVO.ltlItem" />
													 <input type="hidden" value="${ltlItemFlag}" name="isLtlItem" />
													 <input type="hidden" name="ltlShipMethod" value="${ltlShipMethod}" />
													<c:choose>
															<c:when test="${!isTransient }">																			
																		<c:if test="${userRegCount ne 0}">																					
																				<!-- LTL-328 Hide Registry Button for LTL item for Logged In User -->
																				<%--COPY REGISTRY--%>
																				 <c:choose>
																					<c:when test="${registryId!=sessionRegId}">                                                 	
																				 		<div class="button_active copyRegButton <c:if test='${not empty refNum}'>disabled</c:if>" >
																							<input type="button" class="btnAddToRegistry tiny button secondary"<c:if test="${not empty refNum}"> disabled="disabled"</c:if> name="" title="Add to Registry" value="Add to Registry" role="button" aria-pressed="false" />			
																						</div>
																					</c:when>
																				 </c:choose>	
																				<%-- END COPY REGISTRY--%>																	
																																				
																	</c:if>																		
															</c:when>
													</c:choose>
												<%-- <dsp:droplet name="IsProductSKUShippingDroplet">
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
												</div>
											</div>

											<div class="productFooter small-12 columns">
											<ul class="noMar clearfix freeShipContainer prodAttribWrapper">
												<dsp:getvalueof var="skuDetailVO" param="regItem.sKUDetailVO"/>

									            <c:if test="${active}">

													<dsp:droplet name="/atg/dynamo/droplet/ForEach">
														<dsp:param name="array" param="regItem.sKUDetailVO.skuAttributes" />
														<dsp:param name="elementName" value="attributeVOList"/>
														<dsp:oparam name="output">
															<dsp:getvalueof var="pageName" param="key" />

															<c:if test="${pageName eq 'RLP'}">
																<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																<dsp:param param="attributeVOList" name="array" />
																<dsp:param name="elementName" value="attributeVO"/>
																<dsp:param name="sortProperties" value="+priority"/>
																	<dsp:oparam name="output">
																		 <c:set var="currentCount"><dsp:valueof param="count" /></c:set>
																		 <c:set var="actionURL"><dsp:valueof param="attributeVO.actionURL" /></c:set>
																         <c:choose>
																			<c:when test="${not empty actionURL}">
																				<li><a href="${actionURL}" class="popup"><span><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></span></a></li>
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
																				<c:set var="nextStyle">prodPrimaryAttribute</c:set>
																			</c:otherwise>
														            	</c:choose>
																	</dsp:oparam>
																</dsp:droplet>
															</c:if>
														</dsp:oparam>
													</dsp:droplet>
													<%-- <c:if test="${skuDetailVO.shippingSurcharge gt 0.0}">
														<span class="${nextStyle}"><bbbl:label key="lbl_mng_surcharge" language="<c:out param='${language}'/>"/>${skuDetailVO.shippingSurcharge}</span>
													</c:if>
													<c:if test="${skuDetailVO.vdcSku}">
														<span class="prodSecondaryAttribute">${skuDetailVO.vdcSKUShipMesage}</span>
													</c:if> --%>
												</c:if>
											</ul>
											<div class="right">
												<dsp:getvalueof var="bopusAllowed" param="regItem.sKUDetailVO.bopusAllowed"/>
												<c:choose>
												 <c:when test="${not empty refNum}">
												<%-- <a class="pickupStore changeStore" title="<bbbl:label key='lbl_mng_regitem_pickupinstore' language='${pageContext.request.locale.language}' />" href="#"><bbbl:label key='lbl_mng_regitem_pickupinstore' language="${pageContext.request.locale.language}" /></a> --%>
												<dsp:a iclass="nearbyStores nearby-stores in-store disabled" href="/tbs/selfservice/find_tbs_store.jsp">
													<dsp:param name="id" value="${productId}" />
													<dsp:param name="siteId" value="${currentSiteId}" />
													<dsp:param name="registryId" value="${registryId}" />
													<dsp:param name="skuid" value="${skuID}"/>
													<dsp:param name="itemQuantity" value="1"/>
													<bbbl:label key='lbl_mng_regitem_pickupintbs' language="${pageContext.request.locale.language}" />
												</dsp:a>
							                </c:when>
							                <c:otherwise>
												<c:if test="${not bopusAllowed}">
													<c:if test="${requestedQuantity > purchasedQuantity}">
														<%-- <a class="pickupStore changeStore" title="<bbbl:label key='lbl_mng_regitem_pickupinstore' language='${pageContext.request.locale.language}' />" href="#"><bbbl:label key='lbl_mng_regitem_pickupinstore' language="${pageContext.request.locale.language}" /></a> --%>
														<dsp:a iclass="nearbyStores nearby-stores in-store" href="/tbs/selfservice/find_tbs_store.jsp">
															<dsp:param name="id" value="${productId}" />
															<dsp:param name="siteId" value="${currentSiteId}" />
															<dsp:param name="registryId" value="${registryId}" />
															<dsp:param name="skuid" value="${skuID}"/>
															<dsp:param name="itemQuantity" value="1"/>
															<bbbl:label key='lbl_mng_regitem_pickupintbs' language="${pageContext.request.locale.language}" />
														</dsp:a>
														
													</c:if>
												</c:if>
											</c:otherwise>
											</c:choose>
											</div>
											</div>
											</div>
										</div><hr/>
						</c:if>
					</dsp:oparam>
                    <dsp:oparam name="outputEnd">
							<%-- <li  class="productRow textRight returnTopRow">
									<a class="returnToTop" href="#backToTop"><bbbl:label key='lbl_mng_regitem_back_to_top' language="${pageContext.request.locale.language}" /></a>
									<div class="clear"></div>
							</li> --%>
					</div></dd>
                    </dsp:oparam>
				</dsp:droplet>
			</c:if>

			<%-- <c:if test="${countIE eq sizeIE}">
				</dd>
			</c:if> --%>

		</dsp:oparam>
		<dsp:oparam name="outputEnd">



			<dsp:droplet name="ForEach">


				<dsp:param name="array" param="inStockCategoryBuckets" />
				<dsp:oparam name="output">

				<dsp:getvalueof var="sizeIE" param="size"/>
				<dsp:getvalueof var="countIE" param="count"/>
				<%-- <c:choose>
                <c:when test="${countIE eq 1}">
                    <dd class="accordion-navigation accordionReg accordionReg4 active">
                </c:when>
                <c:when test="${countIE eq 5}">
                    </dd>
                    <div class="ajaxLoadWrapper load2 loadMore hidden">
                        <bbbt:textArea key="txt_viewregistry_imageloader" language ="${pageContext.request.locale.language}"/>
                    </div>
                    <dd class="accordion-navigation accordionReg accordionReg5 active">
                </c:when>
                <c:when test="${countIE eq 12}">
                    </dd>
                    <div class="ajaxLoadWrapper load3 loadMore hidden">
                        <bbbt:textArea key="txt_viewregistry_imageloader" language ="${pageContext.request.locale.language}"/>
                    </div>
                    <dd class="accordion-navigation accordionReg accordionReg6 active">
                </c:when>
            </c:choose> --%>

					<dsp:getvalueof var="bucketName" param="key" idtype="java.lang.String" />
					<dsp:getvalueof var="bucket" param="inStockCategoryBuckets.${bucketName}" idtype="java.util.List" />
                    <dsp:getvalueof var="bucketNameNew" value="${fn:replace(bucketName, '$','')}"/>
                        <dsp:getvalueof var="bucketNameNew" value="${fn:replace(bucketNameNew, '-','')}"/>
					<c:if test="${bucketName eq othersCat}">

						<dsp:getvalueof var="count" value="${fn:length(bucket)}"/>
						<c:if test="${count ne 0}">
                            <dd class="accordion-navigation accordionReg accordionReg active">
							<a href="#accordionDiv${fn:replace(bucketNameNew, ' ','')}${count}"><dsp:valueof param="key" />(${count})</a>
						</c:if>
						<dsp:droplet name="ForEach">
							<dsp:param name="array" param="inStockCategoryBuckets.${bucketName}" />
							<dsp:param name="elementName" value="regItem" />
                            <dsp:oparam name="outputStart">
                                <div id="accordionDiv${fn:replace(bucketNameNew, ' ','')}${count}" class="accordionDiv content active">
                            </dsp:oparam>
							<dsp:oparam name="output">
								<c:if test="${count ne 0}">
									<dsp:getvalueof var="skuID" vartype="java.lang.String"  param="regItem.sku" />
									<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
										<dsp:param name="skuId" value="${skuID}" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="productId" vartype="java.lang.String" param="productId" />
											<dsp:getvalueof var="inStoreSku" param="inStore" />
											<c:set var="productId2" value="${productId2}${productId},"/>
										</dsp:oparam>
									</dsp:droplet>
									<dsp:getvalueof var="bucketName" param="key" idtype="java.lang.String" />
									<dsp:getvalueof var="bucket" param="inStockCategoryBuckets.${bucketName}" idtype="java.util.List" />
									<div class="clearfix productRow btngiftViewTopMargin giftViewProductChangeStoreWrapper addToCartGiftRegWrapper">
										<input type="hidden" name="bts" value="false" class="addToCartSubmitData bts" data-change-store-submit="bts" />
						                 <input type="hidden" name="storeId" value="" data-change-store-storeid="storeId" class="addToCartSubmitData" />
										<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
											<dsp:param name="id" value="${productId}"/>
											<dsp:param name="itemDescriptorName" value="product" />
											<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
											</dsp:oparam>
										</dsp:droplet>
										<dsp:getvalueof var="disable" param="regItem.sKUDetailVO.disableFlag" />
										<dsp:getvalueof var="webOffered" param="regItem.sKUDetailVO.webOfferedFlag" />
										<dsp:getvalueof var="active" param="regItem.sKUDetailVO.activeFlag" />
										<dsp:getvalueof var="personalizedImageUrls" param="regItem.personalizedImageUrls"/>
										<dsp:getvalueof var="personalizedImageUrlThumbs" param="regItem.personalizedImageUrlThumbs"/>
										<dsp:getvalueof var="personalisedCode" param="regItem.personalisedCode"/>
										<dsp:getvalueof var="personalizationOptionsDisplay" param="regItem.personalizationOptionsDisplay"/>
										<dsp:getvalueof var="customizedPrice" vartype="java.lang.Double" param="regItem.customizedDoublePrice"/>
										<dsp:getvalueof var="personlisedPrice" vartype="java.lang.Double" param="regItem.personlisedPrice"/>
										<c:set var="customizedPrice1"><dsp:valueof value="${customizedPrice}" converter="currency"/></c:set>
	                                    <c:set var="personlisedPrice1"><dsp:valueof value="${personlisedPrice}" converter="currency"/></c:set>
										<dsp:getvalueof var="customizationDetails" param="regItem.customizationDetails"/>
										<dsp:getvalueof var="refNum" param="regItem.refNum"/>
										<dsp:getvalueof var="personalizationType" param="regItem.sKUDetailVO.personalizationType" />
										
										<c:if test="${not empty personalisedCode}">
											<c:set var="cusDet">${eximCustomizationCodesMap[personalisedCode]}</c:set>									
											<input type="hidden" value="${cusDet}" id="customizationDetails"/> 
										</c:if>
																				
										<dsp:getvalueof var="ltlItemFlag" param="regItem.SKUDetailVO.ltlItem" />
										<c:set var="productName">
											<dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/>
										</c:set>
										<div class="productImage small-4 medium-2 columns print-2">
										
											<c:choose>
												<c:when test="${ltlItemFlag}">
												<span <c:if test="${not empty refNum}"> " oncontextmenu="return false;" </c:if> class="lnkQVPopup" data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}&registryView=guest"
												 title="${productName}">
													<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.mediumImage" />
													<c:choose>
														<c:when test="${empty imageURL}">
															<img src="${imagePath}/_assets/global/images/no_image_available.jpg"  alt="${productName}" class="prodImage" />
														</c:when>
														<c:otherwise>
															<img src="${scene7Path}/${imageURL}" data-original="${scene7Path}/${imageURL}" alt="${productName}" class="prodImage stretch" />
														</c:otherwise>
													</c:choose>
												</span>
												</c:when>
												<c:otherwise>
												<c:if test="${active}">
												<a <c:if test="${not empty refNum}"> " oncontextmenu="return false;" </c:if> href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}"
												 class="lnkQVPopup prodTitle" data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}&registryView=guest"
												 title="${productName}" data-skuId="${skuID}" data-disableItemDetails="true" data-registryId="${registryId}" data-sopt="${ltlShipMethod}" data-productId="${productId}" data-refNum="${refNum}" <c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
															data-customizedPrice="${customizedPrice1}" data-customizationDetails="${customizationDetails}" data-color="${color}" data-size="${size}" data-personalizedImageUrls="${personalizedImageUrls}"
															data-personlisedPrice="${personlisedPrice1}" data-personalizedImageUrlThumbs="${personalizedImageUrlThumbs}" data-personalizationType= "${personalizationType}"</c:if>>
												</c:if>

													<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.mediumImage" />
													<c:choose>
													    <c:when test="${not empty refNum && not empty personalizedImageUrlThumbs}">
                                             						<img src="${personalizedImageUrlThumbs}" alt="<c:out value='${productName}'/>" class="prodImage stretch" >
                                             			</c:when>
														<c:when test="${empty imageURL}">
															<img src="${imagePath}/_assets/global/images/no_image_available.jpg"  alt="${productName}" class="prodImage" />
														</c:when>
														<c:otherwise>
															<img src="${scene7Path}/${imageURL}" data-original="${scene7Path}/${imageURL}" alt="${productName}" class="prodImage stretch" />
														</c:otherwise>
													</c:choose>
												<c:if test="${active}">
												</a>
												</c:if>
												</c:otherwise>
											</c:choose>
											
										</div>
										<div class="productContainer small-8 medium-10 columns no-padding-right print-10">
											<div class="productTab productHeading clearfix">
												<div class="productName small-12 medium-3 columns print-3">
													<dsp:getvalueof var="upc" param="regItem.sKUDetailVO.upc" />
													<dsp:getvalueof var="color" param="regItem.sKUDetailVO.color" />
													<dsp:getvalueof var="size" param="regItem.sKUDetailVO.size" />
													<dsp:getvalueof var="skuInCartFlag" param="regItem.sKUDetailVO.inCartFlag" />
														<c:if test="${upc ne null}">
															<div class="fl upc"><span class="bold"> <bbbl:label key='lbl_mng_regitem_upc' language="${pageContext.request.locale.language}" /> </span>
															<span>${upc}</span></div>
														</c:if>
												</div>
												<div class="small-1 medium-2 columns price print-2 hide-for-small-only"><bbbl:label key='lbl_mng_regitem_price' language="${pageContext.request.locale.language}" /></div>
												<div class="small-2 medium-1 columns requested print-2 hide-for-small-only no-padding"><bbbl:label key='lbl_mng_regitem_requested' language="${pageContext.request.locale.language}" /></div>
												<div class="small-2 medium-1 columns purchase print-2 hide-for-small-only no-padding"><bbbl:label key='lbl_mng_regitem_purchased' language="${pageContext.request.locale.language}" /></div>
												<div class="small-2 columns quantity print-2 hide-for-small-only"><bbbl:label key='lbl_mng_regitem_quantity' language="${pageContext.request.locale.language}" /></div>
												<div class="small-1 columns productLastColumn print-1 hide-for-small-only"> </div>
											</div>
											<div class="productTab productContent clearfix">
												<div class="productName small-12 medium-3 columns print-3">
													<c:choose>
														<c:when test="${active}">
														<span class="blueName">
														<c:choose>
															<c:when test="${ltlItemFlag}">
																<span <c:if test="${not empty refNum}"> " oncontextmenu="return false;" </c:if> class="lnkQVPopup prodTitle" data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}&registryView=guest"
																title="${productName}">
																	<dsp:getvalueof var="displayName" param="regItem.sKUDetailVO.displayName" />
																	${productName}
																</span>
															</c:when>
															<c:otherwise>
															<a <c:if test="${not empty refNum}"> " oncontextmenu="return false;" </c:if> href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}"
															class="lnkQVPopup prodTitle" data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}&registryView=guest"
															title="${productName}" data-skuId="${skuID}" data-disableItemDetails="true" data-registryId="${registryId}" data-sopt="${ltlShipMethod}" data-productId="${productId}" data-refNum="${refNum}" <c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
															data-customizedPrice="${customizedPrice1}" data-customizationDetails="${customizationDetails}" data-color="${color}" data-size="${size}" data-personalizedImageUrls="${personalizedImageUrls}"
															data-personlisedPrice="${personlisedPrice1}" data-personalizedImageUrlThumbs="${personalizedImageUrlThumbs}" data-personalizationType= "${personalizationType}"</c:if>>
																<dsp:getvalueof var="displayName" param="regItem.sKUDetailVO.displayName" />
																${productName}
															</a>
															</c:otherwise>
														</c:choose>
															
														</span><br/>
														</c:when>
														<c:otherwise>
															<span class="blueName prodTitle">
																<dsp:getvalueof var="displayName" param="regItem.sKUDetailVO.displayName" />
																<dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/>
															</span><br/>
														</c:otherwise>
													</c:choose>
                                                    <span class="prodColor noPad"><c:if test='${not empty color}'><bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/>&nbsp;:&nbsp;<dsp:valueof value="${color}" valueishtml="true" /></c:if><c:if test='${not empty size}'><c:if test='${not empty color}'><br/></c:if><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/>&nbsp;:&nbsp;<dsp:valueof value="${size}" valueishtml="true" /></c:if></span>
                                                    <div class="personalizationAttributes"> 
                                                       <c:if test="${not empty personalisedCode}">
                                                            <span class="exim-code">${eximCustomizationCodesMap[personalisedCode]}:</span>
				        									<span class="exim-detail">${customizationDetails}</span>
                                                       </c:if>
                                                 	</div>
	                                                <div class="personalizationAttributes">
                                                       <div class="pricePersonalization">
                                                           <c:choose>
	                                                           <c:when test="${personalizationType == 'PB'}">
	                                                           <span class="fee-detail">
	                                                           		<bbbl:label key='lbl_PB_Fee_detail' language="${pageContext.request.locale.language}" />                                                               
	                                                           </span>                                     
	                                                           </c:when>
	                                                           <c:when test="${personalizationType == 'PY'}">
                                                                     <span class="cust-price">
                                                                     	<dsp:valueof value="${customizedPrice1}"/>
                                                                     </span>
                                                                     <span class="fee-detail">
                                                                     	<bbbl:label key='lbl_exim_added_price' language="${pageContext.request.locale.language}" />	
                                                                     </span>                                                                     
	                                                           </c:when>
	                                                           <c:when test="${personalizationType == 'CR'}">
	                                                               		 <span class="cust-price">
	                                                               			<dsp:include page="/global/gadgets/formattedPrice.jsp">
	                                                               		 	<dsp:param name="price" value="${customizedPrice}"/>
	                                                               		 </span>
	                                                               </dsp:include>
	                                                               <span class="fee-detail">
	                                                               		<c:choose>
																	 		<c:when test="${customizeTxt eq true}">
																	 			<bbbl:label key="lbl_exim_cr_added_price_customize"
																								language="${pageContext.request.locale.language}"/>
																	 		</c:when>
																	 		<c:otherwise>
																	 			<bbbl:label key="lbl_exim_cr_added_price"
																								language="${pageContext.request.locale.language}"/>
																	 		</c:otherwise>
																	 	</c:choose>
	                                                               	</span>
	                                              			   </c:when>
                                                           </c:choose>
                                                       </div>
	                                                </div>
												</div>
												
												<dsp:getvalueof var="ltlShipMethodDesc" param="regItem.ltlShipMethodDesc"/>
												<dsp:getvalueof var="deliverySurcharge" param="regItem.deliverySurcharge"/> 
												<dsp:getvalueof var="assemblyFee" param="regItem.assemblyFees"/>
												
												<dsp:getvalueof var="ltlShipMethod" param="regItem.ltlDeliveryServices" />										
												<dsp:getvalueof var="shipMethodUnsupported" param="regItem.shipMethodUnsupported"/>
												<dsp:getvalueof var="ltlItemFlag" param="regItem.SKUDetailVO.ltlItem" />
												<div class="price bold small-12 medium-2 columns print-2">
												
													<c:choose>
															<c:when test="${not empty personalisedCode}">
															<%--  BBBH-5862 : TBS | For CR type of personalised SKU, actual price is getting displayed in guest view. Katori price is not getting displayed --%>
																<div><dsp:include page="/global/gadgets/formattedPrice.jsp">												
													               <span><dsp:param name="price" value="${personlisedPrice}"/>
													        	</dsp:include></span>															
																</div>
																<input type="hidden" value="${personlisedPrice}" class="itemPrice"/>
															</c:when>
															<c:otherwise>
																<dsp:droplet name="PriceDroplet">
									                <dsp:param name="product" value="${productId}" />
									                <dsp:param name="sku" value="${skuID}"/>
									                <dsp:oparam name="output">
									                  	<dsp:setvalue param="theListPrice" paramvalue="price"/>
														<c:if test="${skuInCartFlag}">
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
														 
									                  	<dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
										                  <c:choose>
										                  	<c:when test="${not empty profileSalePriceList}">
										                      	<dsp:droplet name="PriceDroplet">
											                        <dsp:param name="priceList" bean="Profile.salePriceList"/>
											                        <dsp:oparam name="output">
											                            <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
											                            <c:choose>
																			<c:when test="${skuInCartFlag}">
																				<c:set var="listPrice" value="${inCartPrice}" />
																			</c:when>
																			<c:otherwise>
																				<dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
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


												                        	<%-- (<bbbl:label key='lbl_mng_regitem_reg' language="${pageContext.request.locale.language}" />&nbsp; 	<dsp:include page="/global/gadgets/formattedPrice.jsp">
														          								<dsp:param name="price" value="${price }"/>
														              				</dsp:include>)

																		<bbbl:label key='lbl_mng_regitem_you_save' language="${pageContext.request.locale.language}" />&nbsp;
															      				<dsp:include page="/global/gadgets/formattedPrice.jsp">
												                                	<dsp:param name="price" value="${price-listPrice}"/>
												                              	</dsp:include>(<fmt:formatNumber type="number" maxFractionDigits="2" value="${(listPrice/100)*price}" />%)
												                         --%>
													                </dsp:oparam>
											                        <dsp:oparam name="empty">
												                    	<c:choose>
																			<c:when test="${skuInCartFlag}">
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
											                	</dsp:droplet><%-- End price droplet on sale price --%>
										                    </c:when>
										                    <c:otherwise>
										                      	<c:choose>
																	<c:when test="${skuInCartFlag}">
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
									                </dsp:oparam>
									            </dsp:droplet>
									            <c:if test="${ltlItemFlag}">
									            <c:choose>
																<c:when test="${ltlShipMethod == null or ltlShipMethod == '' or shipMethodUnsupported}">
																	<li class="deliveryLtl"><bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/>:</li>
																	<li class="deliverypriceLtl">
																		<span class="deliverypriceLtlClass">TBD</span>
																		<img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" />
																	</li>
																 </c:when>
																<c:otherwise>
																	 <c:choose>
																		 <c:when test="${ltlShipMethod== 'LWA' }">
																		   <li class="deliveryLtl">  Incl White Glove <span>With Assembly:</span></li>
																		   <li class="deliverypriceLtl"><span class="deliverypriceLtlClass"><dsp:valueof value="${deliverySurcharge+assemblyFee}" number="0.00" converter="currency"/></span>
																		   <img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" /></li>
																		</c:when>
																		<c:otherwise>
																		<li class="deliveryLtl">
																				 Incl ${ltlShipMethodDesc}:
																					 </li>     <li class="deliverypriceLtl"><span class="deliverypriceLtlClass">       <c:if test="${deliverySurcharge eq 0.0}"> FREE</c:if>
																											 <c:if test="${deliverySurcharge gt 0.0}"><dsp:valueof value="${deliverySurcharge}" number="0.00" converter="currency"/></c:if>
																			</span> <img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" /></li>
																			</c:otherwise>
																	</c:choose>
																</c:otherwise>
															 </c:choose>
											 	</c:if>
																<c:if test="${ltlShipMethod != null && ltlShipMethod != '' && !shipMethodUnsupported}">	
																<li class="itemLtl">Item Price
																	<span class="itempriceLtl">
																		<dsp:include page="registry_items_guest_category_frag.jsp">
																			<dsp:param name="productId" value="${productId}"/>
																			<dsp:param name="skuID" value="${skuID}"/>
																			<dsp:param name="skuInCartFlag" value="${skuInCartFlag}"/>
																			<dsp:param name="incartPriceList" value="${incartPriceList}"/>
																		</dsp:include>
																	</span>
																</li>
																</c:if>
										            </c:otherwise>
											             	</c:choose>
												
												</div>
												<div class="requested bold small-12 medium-1 columns print-2"><dsp:valueof param="regItem.qtyRequested" /></div>
												<div class="purchase bold small-12 medium-1 columns print-2"><dsp:valueof param="regItem.qtyPurchased" /></div>
												<div class="quantity bold small-12 medium-2 columns print-2">
												<c:choose>
													<c:when test="${((active || inStoreSku) || (currentSiteId ne TBS_BedBathCanadaSite))}">
													<div id="shopCollection" class="input alpha">
														<div class="qty-spinner fl">
															<a href="#" class="button minus secondary" title="<bbbl:label key='lbl_decrease_quantity' language="${pageContext.request.locale.language}" />"><span/></a>
															<input name="qty" type="text" value="1" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="input_tiny_giftView quantity-input qty itemQuantity" data-change-store-submit="qty" data-change-store-errors="required digits nonZero" role="textbox" aria-required="false" maxlength="2" />
															<a href="#" class="button plus secondary" title="<bbbl:label key='lbl_increase_quantity' language="${pageContext.request.locale.language}" />"><span/></a>
														</div>
													</div>
													</c:when>
													<c:otherwise>1</c:otherwise>
												</c:choose>
												</div>
												<div class="productLastColumn small-12 medium-2 columns print-2">
												<dsp:getvalueof var="skuID" param="regItem.sku" />
												<input type="hidden" name="skuId" value="${skuID}" class="skuId changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" />
												<input type="hidden" name="registryId" value="${registryId}" class="registryId" data-change-store-submit="registryId" data-change-store-errors="required"  />
													<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
														<dsp:param name="skuId" value="${skuID}" />
														<dsp:oparam name="output">
															<dsp:getvalueof var="productId" param="productId" />
															<input type="hidden" name="prodId" value="${productId}" class="productId" data-change-store-submit="prodId" />
														</dsp:oparam>
														<dsp:oparam name="error">
															<dsp:getvalueof param="errorMsg" var="errorMsg" />
															<div class="error">
																<bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}" />
															</div>
														</dsp:oparam>
													</dsp:droplet>
													<dsp:getvalueof param="regItem.qtyRequested" var="requestedQuantity" />
													<dsp:getvalueof param="regItem.qtyPurchased" var="purchasedQuantity" />
													<c:choose>
									                     <c:when test="${requestedQuantity > purchasedQuantity}">
															<dsp:droplet name="TBSItemExclusionDroplet">
																<dsp:param name="skuId" value="${skuID}" />
																<dsp:param name="siteId" value="${currentSiteId}" />
																<dsp:oparam name="output">
																<dsp:getvalueof var="isItemExcluded" param="validItemExcludedSku"/>
																<dsp:getvalueof var="caDisabled" param="caDisabled"/>
																<dsp:getvalueof var="reasonCode" param="reasonCode"/>

																<c:choose>
																	<c:when test="${isItemExcluded || caDisabled || (enableKatoriFlag eq false && not empty refNum)}">

																		<div class="button_disabled addToCart marBottom_5">
																			<input class="button tiny" type="button" disabled="disabled" name="btnAddToCart" title="Add to cart" value="Add to Cart" role="button" aria-pressed="false" />

																		</div>
																		<div style="color:red;font-weight:bold;">
																	
							 										<dsp:include page="/browse/frag/reasonCodeMessage.jsp" flush="true" >
																		<dsp:param name="reasonCode" value="${reasonCode}"/>
																	</dsp:include>
							    									</div>  
																	</c:when>
																	<c:otherwise>
																		<div class="${button_active} addToCart marBottom_5">
																			<input class="button tiny" type="button" name="btnAddToCart" title="Add to cart" value="Add to Cart" role="button" aria-pressed="false" />
																		</div>
																	</c:otherwise>
																</c:choose>
																</dsp:oparam>
															</dsp:droplet>
														</c:when>
													    <c:otherwise>
															<div class="purchasedConfirmation">
															  <h2><bbbl:label key='lbl_mng_regitem_purchased_confirm' language="${pageContext.request.locale.language}" /></h2>
															</div>
														</c:otherwise>
													</c:choose>
													<dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>															
													<dsp:getvalueof var="userRegCount" value="${fn:length(sessionMapValues.userRegistriesList)}"/>
													<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO.registryId" var="sessionRegId"/>
													 <dsp:getvalueof var="ltlItemFlag" param="regItem.sKUDetailVO.ltlItem" />
		                                               <input type="hidden" value="${ltlItemFlag}" name="isLtlItem" />
		                                               <input type="hidden" name="ltlShipMethod" value="${ltlShipMethod}" />
													<c:choose>
															<c:when test="${!isTransient }">																			
																		<c:if test="${userRegCount ne 0}">																																								
																		<!-- LTL-328 Hide Registry Button for LTL item for Logged In User -->
																				<%--COPY REGISTRY--%>     
																				<c:choose>
																					<c:when test="${registryId!=sessionRegId}">                                              	
																						<div class="button_active copyRegButton <c:if test='${not empty refNum}'>disabled</c:if>" >
																							<input type="button" class="btnAddToRegistry tiny button secondary" <c:if test="${not empty refNum}">disabled="disabled"</c:if>
																							name="" title="Add to Registry" value="Add to Registry" role="button" aria-pressed="false" />
																						</div>
																					</c:when>
																				</c:choose>	
																				<%-- END COPY REGISTRY--%>																	
																              																			
																	</c:if>																		
															</c:when>
													</c:choose>
													
												<%-- 	<dsp:droplet name="IsProductSKUShippingDroplet">
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
												</div>
											</div>

											<div class="productFooter small-12 columns">

													 <dsp:getvalueof var="skuDetailVO" param="regItem.sKUDetailVO"/>

										            <c:if test="${active}">
										            	<ul class="noMar clearfix freeShipContainer prodAttribWrapper">
														<dsp:droplet name="/atg/dynamo/droplet/ForEach">
															<dsp:param name="array" param="regItem.sKUDetailVO.skuAttributes" />
															<dsp:param name="elementName" value="attributeVOList"/>
															<dsp:oparam name="output">
																<dsp:getvalueof var="pageName" param="key" />

																<c:if test="${pageName eq 'RLP'}">
																	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	<dsp:param param="attributeVOList" name="array" />
																	<dsp:param name="elementName" value="attributeVO"/>
																	<dsp:param name="sortProperties" value="+priority"/>
																		<dsp:oparam name="output">
																			 <c:set var="currentCount"><dsp:valueof param="count" /></c:set>
																			 <c:set var="actionURL"><dsp:valueof param="attributeVO.actionURL" /></c:set>
																	         <c:choose>
																				<c:when test="${not empty actionURL}">
																					<li><a href="${actionURL}" class="popup"><span><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></span></a></li>
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
																					<c:set var="nextStyle">prodPrimaryAttribute</c:set>
																				</c:otherwise>
															            	</c:choose>
																		</dsp:oparam>
																	</dsp:droplet>
																</c:if>
															</dsp:oparam>
														</dsp:droplet>
														</ul>
														<%-- <c:if test="${skuDetailVO.shippingSurcharge gt 0.0}">
															<span class="${nextStyle}"><bbbl:label key="lbl_mng_surcharge" language="<c:out param='${language}'/>"/>${skuDetailVO.shippingSurcharge}</span>
														</c:if>
														<c:if test="${skuDetailVO.vdcSku}">
															<span class="prodSecondaryAttribute">${skuDetailVO.vdcSKUShipMesage}</span>
														</c:if>	 --%>
													</c:if>
													<div class="right">
													<dsp:getvalueof var="bopusAllowed" param="regItem.sKUDetailVO.bopusAllowed"/>
													<c:choose>
													 <c:when test="${not empty refNum}">
														<%-- <a class="pickupStore changeStore" title="<bbbl:label key='lbl_mng_regitem_pickupinstore' language='${pageContext.request.locale.language}' />" href="#"><bbbl:label key='lbl_mng_regitem_pickupinstore' language="${pageContext.request.locale.language}" /></a> --%>
														<dsp:a iclass="nearbyStores nearby-stores in-store disabled" href="/tbs/selfservice/find_tbs_store.jsp">
															<dsp:param name="id" value="${productId}" />
															<dsp:param name="siteId" value="${currentSiteId}" />
															<dsp:param name="registryId" value="${registryId}" />
															<dsp:param name="skuid" value="${skuID}"/>
															<dsp:param name="itemQuantity" value="1"/>
															<bbbl:label key='lbl_mng_regitem_pickupintbs' language="${pageContext.request.locale.language}" />
														</dsp:a>
									                </c:when>
									                <c:otherwise>
													<c:if test="${not bopusAllowed}">
														<c:if test="${requestedQuantity > purchasedQuantity}">
															<%-- <a class="pickupStore changeStore" title="<bbbl:label key='lbl_mng_regitem_pickupinstore' language='${pageContext.request.locale.language}' />" href="#"><bbbl:label key='lbl_mng_regitem_pickupinstore' language="${pageContext.request.locale.language}" /></a> --%>
															<dsp:a iclass="nearbyStores nearby-stores in-store" href="/tbs/selfservice/find_tbs_store.jsp">
																<dsp:param name="id" value="${productId}" />
																<dsp:param name="siteId" value="${currentSiteId}" />
																<dsp:param name="registryId" value="${registryId}" />
																<dsp:param name="skuid" value="${skuID}"/>
																<dsp:param name="itemQuantity" value="1"/>
																<bbbl:label key='lbl_mng_regitem_pickupintbs' language="${pageContext.request.locale.language}" />
															</dsp:a>
															
														</c:if>
													</c:if>
													</c:otherwise>
													</c:choose>
													</div>
											</div>

										</div>
									</div><hr/>
								</c:if>
							</dsp:oparam>
                                <dsp:oparam name="outputEnd">
                                				<%-- <li  class="productRow textRight returnTopRow">
														<a class="returnToTop" href="#backToTop"><bbbl:label key='lbl_mng_regitem_back_to_top' language="${pageContext.request.locale.language}" /></a>
												</li> --%>
                                        </div></dd>
                                </dsp:oparam>
						</dsp:droplet>
					</c:if>
				<%-- <c:if test="${countIE eq sizeIE}">
					</dd>
				</c:if> --%>
				</dsp:oparam>

			</dsp:droplet>


		</dsp:oparam>
	</dsp:droplet>









	<dsp:getvalueof var="emptyOutOfStockListFlag" param="emptyOutOfStockListFlag"/>
	<%-- <c:if test="${eventTypeCode ne 'BA1' }">
		<p>
			<div class="grid_2 suffix_1 alpha">
				<img alt="BUY BUY BABY" src="${imagePath}/_assets/bbregistry/images/buy-buy-baby-store.jpg">
			</div>
			<div class="grid_9 alpha">
			<bbbt:textArea key="txt_mng_regitem_chkfirst" language="${pageContext.request.locale.language}" />
			</div>
			<a href="${contextPath}/selfservice/store/find_store.jsp" title='<bbbl:label key="lbl_mng_regitem_clickhere" language ="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_mng_regitem_clickhere" language="${pageContext.request.locale.language}" /></a>&nbsp;
			<c:choose>
				<c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
					<bbbt:textArea key="txt_mng_regitem_callbbby" language="${pageContext.request.locale.language}" />
				</c:when>
				<c:otherwise>
					<bbbt:textArea key="txt_mng_regitem_callbbb" language="${pageContext.request.locale.language}" />
				</c:otherwise>
		    </c:choose>
		</p>
	</c:if> --%>
	<c:if test="${emptyOutOfStockListFlag ne 'true' }">
		<p class="alpha space"><bbbt:textArea key="txt_mng_regitem_items_unavail" language="${pageContext.request.locale.language}" /></p>

		<dsp:droplet name="ForEach">
			<dsp:param name="array" param="notInStockCategoryBuckets" />
			<dsp:oparam name="output">

			<dsp:getvalueof var="sizeIE" param="size"/>
			<dsp:getvalueof var="countIE" param="count"/>
			<%-- <c:choose>
                <c:when test="${countIE eq 1}">
                    <dd class="accordion-navigation accordionReg accordionReg7 active">
                </c:when>
                <c:when test="${countIE eq 5}">
                    </dd>
                    <div class="ajaxLoadWrapper load2 loadMore hidden">
                        <bbbt:textArea key="txt_viewregistry_imageloader" language ="${pageContext.request.locale.language}"/>
                    </div>
                    <dd class="accordion-navigation accordionReg accordionReg8 active">
                </c:when>
                <c:when test="${countIE eq 12}">
                    </dd>
                    <div class="ajaxLoadWrapper load3 loadMore hidden">
                        <bbbt:textArea key="txt_viewregistry_imageloader" language ="${pageContext.request.locale.language}"/>
                    </div>
                    <dd class="accordion-navigation accordionReg accordionReg9 active">
                </c:when>
            </c:choose> --%>
				<dsp:getvalueof var="bucketName" param="key" idtype="java.lang.String" />
				<dsp:getvalueof var="bucket" param="notInStockCategoryBuckets.${bucketName}" idtype="java.util.List" />
                <dsp:getvalueof var="bucketNameNew" value="${fn:replace(bucketName, '$','')}"/>
                        <dsp:getvalueof var="bucketNameNew" value="${fn:replace(bucketNameNew, '-','')}"/>
				<c:if test="${bucketName ne othersCat}">
					<dsp:getvalueof var="count" value="${fn:length(bucket)}"/>
					<c:if test="${count ne 0}">
						<dd class="accordion-navigation accordionReg accordionReg active">
                        <a href="#accordionDiv${fn:replace(bucketNameNew, ' ','')}${count}"><dsp:valueof param="key" />(${count})</a>
					</c:if>
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="notInStockCategoryBuckets.${bucketName}" />
						<dsp:param name="elementName" value="regItem" />
                        <dsp:oparam name="outputStart">
                            <div id="accordionDiv${fn:replace(bucketNameNew, ' ','')}${count}" class="accordionDiv content active">
                        </dsp:oparam>
						<dsp:oparam name="output">
							<c:if test="${count ne 0}">
							<dsp:getvalueof var="skuID" vartype="java.lang.String"  param="regItem.sku" />
							<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
								<dsp:param name="skuId" value="${skuID}" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="productId" vartype="java.lang.String" param="productId" />
									<dsp:getvalueof var="inStoreSku" param="inStore" />
									<c:set var="productId3" value="${productId3}${productId},"/>
								</dsp:oparam>
							</dsp:droplet>
									<div class="clearfix productRow btngiftViewTopMargin giftViewProductChangeStoreWrapper addToCartGiftRegWrapper">
										<input type="hidden" name="bts" value="false" class="addToCartSubmitData bts" data-change-store-submit="bts" />
						                 <input type="hidden" name="storeId" value="" data-change-store-storeid="storeId" class="addToCartSubmitData" />
										<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
											<dsp:param name="id" value="${productId}"/>
											<dsp:param name="itemDescriptorName" value="product" />
											<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
											</dsp:oparam>
										</dsp:droplet>
										<dsp:getvalueof var="disable" param="regItem.sKUDetailVO.disableFlag" />
										<dsp:getvalueof var="webOffered" param="regItem.sKUDetailVO.webOfferedFlag" />
										<dsp:getvalueof var="active" param="regItem.sKUDetailVO.activeFlag" />
										<dsp:getvalueof var="personalizedImageUrls" param="regItem.personalizedImageUrls"/>
										<dsp:getvalueof var="personalizedImageUrlThumbs" param="regItem.personalizedImageUrlThumbs"/>
										<dsp:getvalueof var="personalisedCode" param="regItem.personalisedCode"/>
										<dsp:getvalueof var="personalizationOptionsDisplay" param="regItem.personalizationOptionsDisplay"/>
										<dsp:getvalueof var="customizedPrice" vartype="java.lang.Double" param="regItem.customizedDoublePrice"/>
										<dsp:getvalueof var="personlisedPrice" vartype="java.lang.Double" param="regItem.personlisedPrice"/>
										<c:set var="customizedPrice1"><dsp:valueof value="${customizedPrice}" converter="currency"/></c:set>
	                                    <c:set var="personlisedPrice1"><dsp:valueof value="${personlisedPrice}" converter="currency"/></c:set>
										<dsp:getvalueof var="customizationDetails" param="regItem.customizationDetails"/>
										<dsp:getvalueof var="refNum" param="regItem.refNum"/>
										<dsp:getvalueof var="personalizationType" param="regItem.sKUDetailVO.personalizationType" />
										
										<c:if test="${not empty personalisedCode}">
											<c:set var="cusDet">${eximCustomizationCodesMap[personalisedCode]}</c:set>									
											<input type="hidden" value="${cusDet}" id="customizationDetails"/> 
										</c:if>
										<dsp:getvalueof var="ltlItemFlag" param="regItem.SKUDetailVO.ltlItem" />
										<c:set var="productName">
											<dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/>
										</c:set>
										<div class="productImage small-4 medium-2 columns print-2">
										
											<c:choose>
												<c:when test="${ltlItemFlag}">
												<span <c:if test="${not empty refNum}"> " oncontextmenu="return false;" </c:if> class="lnkQVPopup" data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}&registryView=guest"
												 title="${productName}">
													<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.mediumImage" />
													<c:choose>
														<c:when test="${empty imageURL}">
															<img src="${imagePath}/_assets/global/images/no_image_available.jpg"  alt="${productName}" class="prodImage" />
														</c:when>
														<c:otherwise>
															<img src="${scene7Path}/${imageURL}" data-original="${scene7Path}/${imageURL}" alt="${productName}" class="prodImage stretch" />
														</c:otherwise>
													</c:choose>
												</span>
												</c:when>
												<c:otherwise>
												<c:if test="${active}">
												<a <c:if test="${not empty refNum}"> " oncontextmenu="return false;" </c:if> href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}"
												 class="lnkQVPopup prodTitle" data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}&registryView=guest"
												 title="${productName}" data-skuId="${skuID}" data-disableItemDetails="true" data-registryId="${registryId}" data-sopt="${ltlShipMethod}" data-productId="${productId}" data-refNum="${refNum}" <c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
															data-customizedPrice="${customizedPrice1}" data-customizationDetails="${customizationDetails}" data-color="${color}" data-size="${size}" data-personalizedImageUrls="${personalizedImageUrls}"
															data-personlisedPrice="${personlisedPrice1}" data-personalizedImageUrlThumbs="${personalizedImageUrlThumbs}" data-personalizationType= "${personalizationType}"</c:if>>
												</c:if>	
												 <dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.mediumImage" />
													<c:choose>
													    <c:when test="${not empty refNum && not empty personalizedImageUrlThumbs}">
                                             						<img src="${personalizedImageUrlThumbs}" alt="<c:out value='${productName}'/>" class="prodImage stretch" >
                                             			</c:when>
														<c:when test="${empty imageURL}">
															<img src="${imagePath}/_assets/global/images/no_image_available.jpg"  alt="${productName}" class="prodImage" />
														</c:when>
														<c:otherwise>
															<img src="${scene7Path}/${imageURL}" data-original="${scene7Path}/${imageURL}" alt="${productName}" class="prodImage stretch" />
														</c:otherwise>
													</c:choose>
											<c:if test="${active}">
												</a>
											</c:if>
												</c:otherwise>
											</c:choose>
											
										</div>
										<div class="productContainer small-8 medium-10 columns no-padding-right print-10">
											<div class="productTab productHeading clearfix">
												<div class="productName small-12 medium-3 columns print-3">
													<dsp:getvalueof var="upc" param="regItem.sKUDetailVO.upc" />
													<dsp:getvalueof var="color" param="regItem.sKUDetailVO.color" />
													<dsp:getvalueof var="size" param="regItem.sKUDetailVO.size" />
													<dsp:getvalueof var="skuInCartFlag" param="regItem.sKUDetailVO.inCartFlag" />
														<c:if test="${upc ne null}">
															<div class="fl upc"><span class="bold"> <bbbl:label key='lbl_mng_regitem_upc' language="${pageContext.request.locale.language}" /> </span>
															<span>${upc}</span></div>
														</c:if>
												</div>
												<div class="price small-1 medium-2 columns print-2 hide-for-small-only"><bbbl:label key='lbl_mng_regitem_price' language="${pageContext.request.locale.language}" /></div>
												<div class="requested small-2 medium-1 columns print-2 hide-for-small-only no-padding"><bbbl:label key='lbl_mng_regitem_requested' language="${pageContext.request.locale.language}" /></div>
												<div class="purchase small-2 medium-1 columns print-2 hide-for-small-only no-padding"><bbbl:label key='lbl_mng_regitem_purchased' language="${pageContext.request.locale.language}" /></div>
												<div class="quantity small-2 columns print-2 hide-for-small-only"><bbbl:label key='lbl_mng_regitem_quantity' language="${pageContext.request.locale.language}" /></div>
												<div class="productLastColumn small-2 columns print-2 hide-for-small-only"> </div>
											</div>
											<div class="productTab productContent clearfix">
												<div class="productName small-12 medium-3 columns print-3">
													<c:choose>
														<c:when test="${active && !(isGiftGiver && ltlItemFlag)}">
														<span class="blueName">
															<a <c:if test="${not empty refNum}"> " oncontextmenu="return false;" </c:if>  href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}"
															class="lnkQVPopup prodTitle" data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}&registryView=guest"
															title="${productName}" data-skuId="${skuID}" data-disableItemDetails="true" data-registryId="${registryId}" data-sopt="${ltlShipMethod}" data-productId="${productId}" data-refNum="${refNum}" <c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
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
                                                    <span class="prodColor noPad"><c:if test='${not empty color}'><bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/>&nbsp;:&nbsp;<dsp:valueof value="${color}" valueishtml="true" /></c:if><c:if test='${not empty size}'><c:if test='${not empty color}'><br/></c:if><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/>&nbsp;:&nbsp;<dsp:valueof value="${size}" valueishtml="true" /></c:if></span>
                                                    <div class="personalizationAttributes"> 
                                                       <c:if test="${not empty personalisedCode}">
                                                            <span class="exim-code">${eximCustomizationCodesMap[personalisedCode]}:</span>
				        									<span class="exim-detail">${customizationDetails}</span>
                                                       </c:if>
                                                 	</div>
	                                                <div class="personalizationAttributes">
                                                       <div class="pricePersonalization">
                                                           <c:choose>
	                                                           <c:when test="${personalizationType == 'PB'}">
	                                                                <span class="fee-detail">
	                                                                    <bbbl:label key='lbl_PB_Fee_detail' language="${pageContext.request.locale.language}" />                                                     
	                                                                </span>                                                                      
	                                                           </c:when>
	                                                           <c:when test="${personalizationType == 'PY'}">
                                                                     <span class="cust-price">
                                                                     	<dsp:valueof value="${customizedPrice1}"/>
                                                                     </span>
                                                                     <span class="fee-detail">
                                                                        <bbbl:label key='lbl_exim_added_price' language="${pageContext.request.locale.language}" />
                                                                     </span>
	                                                           </c:when>
	                                                           <c:when test="${personalizationType == 'CR'}">
	                                                               		<span class="cust-price">
			                                                               <dsp:include page="/global/gadgets/formattedPrice.jsp">                       
			                                                               			<dsp:param name="price" value="${customizedPrice}"/>
			                                                               </dsp:include>
	                                                               		</span>
	                                                               <span class="fee-detail">
	                                                               		<c:choose>
																	 		<c:when test="${customizeTxt eq true}">
																	 			<bbbl:label key="lbl_exim_cr_added_price_customize"
																								language="${pageContext.request.locale.language}"/>
																	 		</c:when>
																	 		<c:otherwise>
																	 			<bbbl:label key="lbl_exim_cr_added_price"
																								language="${pageContext.request.locale.language}"/>
																	 		</c:otherwise>
																	 	</c:choose>
	                                                               </span>
	                                              			   </c:when>
                                                           </c:choose>
                                                       </div>
	                                                </div>
												</div>
												
												<dsp:getvalueof var="ltlShipMethodDesc" param="regItem.ltlShipMethodDesc"/>
												<dsp:getvalueof var="deliverySurcharge" param="regItem.deliverySurcharge"/> 
												<dsp:getvalueof var="assemblyFee" param="regItem.assemblyFees"/>
												
												<dsp:getvalueof var="ltlShipMethod" param="regItem.ltlDeliveryServices" />										
												<dsp:getvalueof var="shipMethodUnsupported" param="regItem.shipMethodUnsupported"/>
												<dsp:getvalueof var="ltlItemFlag" param="regItem.SKUDetailVO.ltlItem" />										

												<div class="price bold small-12 medium-2 columns print-2">
												<c:choose>
												<c:when test="${not empty personalisedCode}">
												<%--  BBBH-5862 : TBS | For CR type of personalised SKU, actual price is getting displayed in guest view. Katori price is not getting displayed --%>
																<div><dsp:include page="/global/gadgets/formattedPrice.jsp">												
													               <span><dsp:param name="price" value="${personlisedPrice}"/>
													        	</dsp:include></span>															
																</div>
																<input type="hidden" value="${personlisedPrice}" class="itemPrice"/>
															</c:when>
												
												<c:otherwise>
												<dsp:droplet name="PriceDroplet">
									                <dsp:param name="product" value="${productId}" />
									                <dsp:param name="sku" value="${skuID}"/>
									                <dsp:oparam name="output">
									                  	<dsp:setvalue param="theListPrice" paramvalue="price"/>
														
														<c:if test="${skuInCartFlag}">
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
														

									                  	<dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
										                  <c:choose>
										                  	<c:when test="${not empty profileSalePriceList}">
										                      	<dsp:droplet name="PriceDroplet">
											                        <dsp:param name="priceList" bean="Profile.salePriceList"/>
											                        <dsp:oparam name="output">
											                            <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
																		
																		<c:choose>
																			<c:when test="${skuInCartFlag}">
																				<c:set var="listPrice" value="${inCartPrice}" />
																			</c:when>
																			<c:otherwise>
																				<dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
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


												                        	<%-- (<bbbl:label key='lbl_mng_regitem_reg' language="${pageContext.request.locale.language}" />&nbsp; 	<dsp:include page="/global/gadgets/formattedPrice.jsp">
														          								<dsp:param name="price" value="${price }"/>
														              				</dsp:include>)

																		<bbbl:label key='lbl_mng_regitem_you_save' language="${pageContext.request.locale.language}" />&nbsp;
															      				<dsp:include page="/global/gadgets/formattedPrice.jsp">
												                                	<dsp:param name="price" value="${price-listPrice}"/>
												                              	</dsp:include>(<fmt:formatNumber type="number" maxFractionDigits="2" value="${(listPrice/100)*price}" />%)
												                         --%>
													                </dsp:oparam>
											                        <dsp:oparam name="empty">
																		<c:choose>
																			<c:when test="${skuInCartFlag}">
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
											                	</dsp:droplet><%-- End price droplet on sale price --%>
										                    </c:when>
										                    <c:otherwise>
																<c:choose>
																	<c:when test="${skuInCartFlag}">
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
									                </dsp:oparam>
									            </dsp:droplet>
									            <c:if test="${ltlItemFlag}">
									            <c:choose>
																<c:when test="${ltlShipMethod == null or ltlShipMethod == '' or shipMethodUnsupported}">
																	<li class="deliveryLtl"><bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/>:</li>
																	<li class="deliverypriceLtl">
																		<span class="deliverypriceLtlClass">TBD</span>
																		<img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" />
																	</li>
																</c:when>
																<c:otherwise>
																	 <c:choose>
																		 <c:when test="${ltlShipMethod== 'LWA' }">
																		   <li class="deliveryLtl">  Incl White Glove <span>With Assembly:</span></li>
																		   <li class="deliverypriceLtl"><span class="deliverypriceLtlClass"><dsp:valueof value="${deliverySurcharge+assemblyFee}" number="0.00" converter="currency"/></span>
																		   <img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" /></li>
																		</c:when>
																		<c:otherwise>
																		<li class="deliveryLtl">
																				 Incl ${ltlShipMethodDesc}:
																					 </li>     <li class="deliverypriceLtl"><span class="deliverypriceLtlClass">       <c:if test="${deliverySurcharge eq 0.0}"> FREE</c:if>
																											 <c:if test="${deliverySurcharge gt 0.0}"><dsp:valueof value="${deliverySurcharge}" number="0.00" converter="currency"/></c:if>
																			</span>  <img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" /></li>
																			</c:otherwise>
																	</c:choose>
																</c:otherwise>
															 </c:choose>
												 </c:if>
															<c:if test="${ltlShipMethod != null && ltlShipMethod != '' && !shipMethodUnsupported}">
															  <li class="itemLtl">Item Price
																<span class="itempriceLtl">
																	<dsp:include page="registry_items_guest_category_frag.jsp">
																		<dsp:param name="productId" value="${productId}"/>
																		<dsp:param name="skuID" value="${skuID}"/>
																		<dsp:param name="skuInCartFlag" value="${skuInCartFlag}"/>
																		<dsp:param name="incartPriceList" value="${incartPriceList}"/>
																	</dsp:include>
																</span>
															  </li>
												 </c:if>
												
												</c:otherwise>
												
												</c:choose>
															  
												</div>
												<div class="requested bold small-12 medium-1 columns print-2"><dsp:valueof param="regItem.qtyRequested" /></div>
												<div class="purchase bold small-12 medium-1 columns print-2"><dsp:valueof param="regItem.qtyPurchased" /></div>
												<div class="quantity bold small-12 medium-2 columns print-2">
												<c:choose>
													<c:when test="${((active || inStoreSku) || (currentSiteId ne TBS_BedBathCanadaSite))}">
													<div class="input alpha">
														<div class="qty-spinner fl">
															<a href="#" class="button minus secondary" title="<bbbl:label key='lbl_decrease_quantity' language="${pageContext.request.locale.language}" />"><span/></a>
															<input name="qty" type="text" value="1" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="input_tiny_giftView quantity-input qty itemQuantity" data-change-store-submit="qty" data-change-store-errors="required digits nonZero" role="textbox" aria-required="false" maxlength="2" />
															<a href="#" class="button plus secondary" title="<bbbl:label key='lbl_increase_quantity' language="${pageContext.request.locale.language}" />"><span/></a>
														</div>
													</div>
													</c:when>
													<c:otherwise>1</c:otherwise>
												</c:choose>
												</div>

												<div class="productLastColumn small-12 medium-2 columns print-2">
												<dsp:getvalueof var="skuID" param="regItem.sku" />
												<input type="hidden" name="skuId" value="${skuID}" class="skuId changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" />
												<input type="hidden" name="registryId" value="${registryId}" class="registryId" data-change-store-submit="registryId" data-change-store-errors="required"  />
													<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
														<dsp:param name="skuId" value="${skuID}" />
														<dsp:oparam name="output">
															<dsp:getvalueof var="productId" param="productId" />
															<input type="hidden" name="prodId" value="${productId}" class="productId" data-change-store-submit="prodId" data-change-store-errors="required"  />
														</dsp:oparam>
														<dsp:oparam name="error">
															<dsp:getvalueof param="errorMsg" var="errorMsg" />
															<div class="error">
																<bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}" />
															</div>
														</dsp:oparam>
													</dsp:droplet>
													
													<dsp:getvalueof param="regItem.qtyRequested" var="requestedQuantity" />
													<dsp:getvalueof param="regItem.qtyPurchased" var="purchasedQuantity" />
								                    <c:if test="${purchasedQuantity ge requestedQuantity}">
														<div class="purchasedConfirmation">
														  <h2><bbbl:label key='lbl_mng_regitem_purchased_confirm' language="${pageContext.request.locale.language}" /></h2>
														</div>
														
													</c:if>
													
													<dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>															
													<dsp:getvalueof var="userRegCount" value="${fn:length(sessionMapValues.userRegistriesList)}"/>
													<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO.registryId" var="sessionRegId"/>
													 <dsp:getvalueof var="ltlItemFlag" param="regItem.sKUDetailVO.ltlItem" />
		                                               <input type="hidden" value="${ltlItemFlag}" name="isLtlItem" />
		                                               <input type="hidden" name="ltlShipMethod" value="${ltlShipMethod}" />
														<c:choose>
															<c:when test="${!isTransient }">																			
																		<c:if test="${userRegCount ne 0}">																					
																		<!-- LTL-328 Hide Registry Button for LTL item for Logged In User -->
																				<%--COPY REGISTRY--%>
																				<c:choose>
																					<c:when test="${registryId!=sessionRegId}">                                                  	
																						<div class="button_active copyRegButton <c:if test='${not empty refNum}'>disabled</c:if>" >
																							<input type="button" class="btnAddToRegistry s1 tiny button secondary"  
																									<c:if test="${not empty refNum}"> disabled="disabled"</c:if> name="" title="Add to Registry" value="Add to Registry" role="button" aria-pressed="false" />			
																						</div>
																					</c:when>
																				</c:choose>
																				<%-- END COPY REGISTRY--%>																	
																              
																	</c:if>																		
															</c:when>
														</c:choose>

												</div>
											</div>

											<div class="productFooter small-12 columns">
													 <dsp:getvalueof var="skuDetailVO" param="regItem.sKUDetailVO"/>

										            <c:if test="${active}">
										            	<ul class="noMar clearfix freeShipContainer prodAttribWrapper">
														<dsp:droplet name="/atg/dynamo/droplet/ForEach">
															<dsp:param name="array" param="regItem.sKUDetailVO.skuAttributes" />
															<dsp:param name="elementName" value="attributeVOList"/>
															<dsp:oparam name="output">
																<dsp:getvalueof var="pageName" param="key" />

																<c:if test="${pageName eq 'RLP'}">
																	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	<dsp:param param="attributeVOList" name="array" />
																	<dsp:param name="elementName" value="attributeVO"/>
																	<dsp:param name="sortProperties" value="+priority"/>
																		<dsp:oparam name="output">
																			 <c:set var="currentCount"><dsp:valueof param="count" /></c:set>
																			 <c:set var="actionURL"><dsp:valueof param="attributeVO.actionURL" /></c:set>
																	         <c:choose>
																				<c:when test="${not empty actionURL}">
																					<li><a href="${actionURL}" class="popup"><span><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></span></a></li>
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
																					<c:set var="nextStyle">prodPrimaryAttribute</c:set>
																				</c:otherwise>
															            	</c:choose>
																		</dsp:oparam>
																	</dsp:droplet>
																</c:if>
															</dsp:oparam>
														</dsp:droplet>
														</ul>
														<%-- <c:if test="${skuDetailVO.shippingSurcharge gt 0.0}">
															<span class="${nextStyle}"><bbbl:label key="lbl_mng_surcharge" language="<c:out param='${language}'/>"/>${skuDetailVO.shippingSurcharge}</span>
														</c:if>
														<c:if test="${skuDetailVO.vdcSku}">
															<span class="prodSecondaryAttribute">${skuDetailVO.vdcSKUShipMesage}</span>
														</c:if>	 --%>
													</c:if>
													<dsp:getvalueof param="regItem.qtyRequested" var="requestedQuantity" />
													<dsp:getvalueof param="regItem.qtyPurchased" var="purchasedQuantity" />
													<div class="right">
														<c:if test="${requestedQuantity > purchasedQuantity}">

														<%--  For OOS item , this button is made hidden so that find in store functionality can work --%>
															<dsp:droplet name="TBSItemExclusionDroplet">
																<dsp:param name="skuId" value="${skuID}" />
																<dsp:param name="siteId" value="${currentSiteId}" />
																<dsp:oparam name="output">
																<dsp:getvalueof var="isItemExcluded" param="validItemExcludedSku"/>
																<dsp:getvalueof var="caDisabled" param="caDisabled"/>
																<dsp:getvalueof var="reasonCode" param="reasonCode"/>
																<c:choose>
																	<c:when test="${isItemExcluded || caDisabled || (enableKatoriFlag eq false && not empty refNum)}"> 
																		<div class="button_disabled hidden addToCart marBottom_5">
																			<input class="tiny button" type="button" disabled="disabled" name="btnAddToCart" title="Add to cart" value="Add to Cart" role="button" aria-pressed="false" />
																		</div>
																		<div style="color:red;font-weight:bold;">
																		
							 												<dsp:include page="/browse/frag/reasonCodeMessage.jsp" flush="true" >
																				<dsp:param name="reasonCode" value="${reasonCode}"/>
																			</dsp:include>
							    										</div> 
																	</c:when>
																	<c:otherwise>
																		<div class="hidden ${button_active} addToCart marBottom_5">
																			<input class="tiny button" type="button" name="btnAddToCart" title="Add to cart" value="Add to Cart" role="button" aria-pressed="false" />
																		</div>
																	</c:otherwise>
																</c:choose>
																</dsp:oparam>
															</dsp:droplet>
															
															<dsp:getvalueof var="bopusAllowed" param="regItem.sKUDetailVO.bopusAllowed"/>
															<!-- LTL-328 Hide Pick Up in Store for LTL item -->
															<dsp:getvalueof var="ltlItemFlag" param="regItem.SKUDetailVO.ltlItem" />
			                                               <c:choose>
												                <c:when test="${ltlItemFlag}">
																	<%-- <a class="pickupStore changeStore" title="<bbbl:label key='lbl_mng_regitem_pickupinstore' language='${pageContext.request.locale.language}' />" href="#"><bbbl:label key='lbl_mng_regitem_pickupinstore' language="${pageContext.request.locale.language}" /></a> --%>
																	<dsp:a iclass="nearbyStores nearby-stores in-store hidden" href="/tbs/selfservice/find_tbs_store.jsp">
																		<dsp:param name="id" value="${productId}" />
																		<dsp:param name="siteId" value="${currentSiteId}" />
																		<dsp:param name="registryId" value="${registryId}" />
																		<dsp:param name="skuid" value="${skuID}"/>
																		<dsp:param name="itemQuantity" value="1"/>
																		<bbbl:label key='lbl_mng_regitem_pickupintbs' language="${pageContext.request.locale.language}" />
																	</dsp:a>
												                </c:when>
												                <c:when test="${not empty refNum}">
																<%-- <a class="pickupStore changeStore" title="<bbbl:label key='lbl_mng_regitem_pickupinstore' language='${pageContext.request.locale.language}' />" href="#"><bbbl:label key='lbl_mng_regitem_pickupinstore' language="${pageContext.request.locale.language}" /></a> --%>
																<dsp:a iclass="nearbyStores nearby-stores in-store disabled" href="/tbs/selfservice/find_tbs_store.jsp">
																	<dsp:param name="id" value="${productId}" />
																	<dsp:param name="siteId" value="${currentSiteId}" />
																	<dsp:param name="registryId" value="${registryId}" />
																	<dsp:param name="skuid" value="${skuID}"/>
																	<dsp:param name="itemQuantity" value="1"/>
																	<bbbl:label key='lbl_mng_regitem_pickupintbs' language="${pageContext.request.locale.language}" />
																</dsp:a>
											                </c:when>
												                <c:otherwise>
															<c:if test="${not bopusAllowed}">
															<%-- <a class="pickupStore changeStore" title="<bbbl:label key='lbl_mng_regitem_pickupinstore' language='${pageContext.request.locale.language}' />" href="#"><bbbl:label key='lbl_mng_regitem_pickupinstore' language="${pageContext.request.locale.language}" /></a> --%>
															<dsp:a iclass="nearbyStores nearby-stores in-store" href="/tbs/selfservice/find_tbs_store.jsp">
																<dsp:param name="id" value="${productId}" />
																<dsp:param name="siteId" value="${currentSiteId}" />
																<dsp:param name="registryId" value="${registryId}" />
																<dsp:param name="skuid" value="${skuID}"/>
																<dsp:param name="itemQuantity" value="1"/>
																<bbbl:label key='lbl_mng_regitem_pickupintbs' language="${pageContext.request.locale.language}" />
															</dsp:a>
															
															</c:if>
												                </c:otherwise>
												            </c:choose>
														</c:if>
													</div>

											</div>

										</div>
									</div><hr/>
							</c:if>
						</dsp:oparam>
                        <dsp:oparam name="outputEnd">
                        				<%-- <li  class="productRow textRight returnTopRow">
												<a class="returnToTop" href="#backToTop"><bbbl:label key='lbl_mng_regitem_back_to_top' language="${pageContext.request.locale.language}" /></a>
										</li>
										</div> --%>
                                </div></dd>
                        </dsp:oparam>
					</dsp:droplet>
				</c:if>

			<%-- <c:if test="${countIE eq sizeIE}">
				</dd>
			</c:if> --%>

			</dsp:oparam>
			<dsp:oparam name="outputEnd">

				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="notInStockCategoryBuckets" />
					<dsp:oparam name="output">

					<dsp:getvalueof var="sizeIE" param="size"/>
					<dsp:getvalueof var="countIE" param="count"/>
					<%-- <c:choose>
                        <c:when test="${countIE eq 1}">
                            <dd class="accordion-navigation accordionReg accordionReg10 active">
                        </c:when>
                        <c:when test="${countIE eq 5}">
                            </dd>
                            <div class="ajaxLoadWrapper load2 loadMore hidden">
                                <bbbt:textArea key="txt_viewregistry_imageloader" language ="${pageContext.request.locale.language}"/>
                            </div>
                            <dd class="accordion-navigation accordionReg accordionReg11 active">
                        </c:when>
                        <c:when test="${countIE eq 12}">
                            </dd>
                            <div class="ajaxLoadWrapper load3 loadMore hidden">
                                <bbbt:textArea key="txt_viewregistry_imageloader" language ="${pageContext.request.locale.language}"/>
                            </div>
                            <dd class="accordion-navigation accordionReg accordionReg12 active">
                        </c:when>
                    </c:choose> --%>
						<dsp:getvalueof var="bucketName" param="key" idtype="java.lang.String" />
						<dsp:getvalueof var="bucket" param="notInStockCategoryBuckets.${bucketName}" idtype="java.util.List" />
                        <dsp:getvalueof var="bucketNameNew" value="${fn:replace(bucketName, '$','')}"/>
                        <dsp:getvalueof var="bucketNameNew" value="${fn:replace(bucketNameNew, '-','')}"/>
						<c:if test="${bucketName eq othersCat}">
							<dsp:getvalueof var="count" value="${fn:length(bucket)}"/>
							<c:if test="${count ne 0}">
                                <dd class="accordion-navigation accordionReg accordionReg active">
								<a href="#accordionDiv${fn:replace(bucketNameNew, ' ','')}${count}"><dsp:valueof param="key" />(${count})</a>
							</c:if>
							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="notInStockCategoryBuckets.${bucketName}" />
								<dsp:param name="elementName" value="regItem" />
                                <dsp:oparam name="outputStart">
                                    <div id="accordionDiv${fn:replace(bucketNameNew, ' ','')}${count}" class="accordionDiv content active">
                                </dsp:oparam>
								<dsp:oparam name="output">
									<c:if test="${count ne 0}">
										<dsp:getvalueof var="skuID" vartype="java.lang.String"  param="regItem.sku" />
										<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
											<dsp:param name="skuId" value="${skuID}" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="productId" vartype="java.lang.String" param="productId" />
												<dsp:getvalueof var="inStoreSku" param="inStore" />
												<c:set var="productId4" value="${productId4}${productId},"/>
											</dsp:oparam>
										</dsp:droplet>
									<div class="clearfix productRow btngiftViewTopMargin giftViewProductChangeStoreWrapper addToCartGiftRegWrapper">
										<input type="hidden" name="bts" value="false" class="addToCartSubmitData bts" data-change-store-submit="bts" />
						                 <input type="hidden" name="storeId" value="" data-change-store-storeid="storeId" class="addToCartSubmitData" />
										<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
											<dsp:param name="id" value="${productId}"/>
											<dsp:param name="itemDescriptorName" value="product" />
											<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
											</dsp:oparam>
										</dsp:droplet>
										<dsp:getvalueof var="disable" param="regItem.sKUDetailVO.disableFlag" />
										<dsp:getvalueof var="webOffered" param="regItem.sKUDetailVO.webOfferedFlag" />
										<dsp:getvalueof var="active" param="regItem.sKUDetailVO.activeFlag" />
										<dsp:getvalueof var="personalizedImageUrls" param="regItem.personalizedImageUrls"/>
										<dsp:getvalueof var="personalizedImageUrlThumbs" param="regItem.personalizedImageUrlThumbs"/>
										<dsp:getvalueof var="personalisedCode" param="regItem.personalisedCode"/>
										<dsp:getvalueof var="personalizationOptionsDisplay" param="regItem.personalizationOptionsDisplay"/>
										<dsp:getvalueof var="customizedPrice" vartype="java.lang.Double" param="regItem.customizedDoublePrice"/>
										<dsp:getvalueof var="personlisedPrice" vartype="java.lang.Double" param="regItem.personlisedPrice"/>
										<c:set var="customizedPrice1"><dsp:valueof value="${customizedPrice}" converter="currency"/></c:set>
	                                    <c:set var="personlisedPrice1"><dsp:valueof value="${personlisedPrice}" converter="currency"/></c:set>
										<dsp:getvalueof var="customizationDetails" param="regItem.customizationDetails"/>
										<dsp:getvalueof var="refNum" param="regItem.refNum"/>
										<dsp:getvalueof var="personalizationType" param="regItem.sKUDetailVO.personalizationType" />
										
										<c:if test="${not empty personalisedCode}">
											<c:set var="cusDet">${eximCustomizationCodesMap[personalisedCode]}</c:set>									
											<input type="hidden" value="${cusDet}" id="customizationDetails"/> 
										</c:if>
										<dsp:getvalueof var="ltlItemFlag" param="regItem.SKUDetailVO.ltlItem" />
										<c:set var="productName">
											<dsp:valueof param="regItem.sKUDetailVO.displayName" valueishtml="true"/>
										</c:set>
										<div class="productImage small-4 medium-2 columns print-2">
										
											<c:choose>
												<c:when test="${ltlItemFlag}">
												<span <c:if test="${not empty refNum}"> " oncontextmenu="return false;" </c:if> class="lnkQVPopup" data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}&registryView=guest"
												 title="${productName}">
													<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.mediumImage" />
													<c:choose>
														<c:when test="${empty imageURL}">
															<img src="${imagePath}/_assets/global/images/no_image_available.jpg"  alt="${productName}" class="prodImage" />
														</c:when>
														<c:otherwise>
															<img src="${scene7Path}/${imageURL}" data-original="${scene7Path}/${imageURL}" alt="${productName}" class="prodImage stretch" />
														</c:otherwise>
													</c:choose>
												</span>
												</c:when>
												<c:otherwise>
												<c:if test="${active}">
												<a <c:if test="${not empty refNum}"> " oncontextmenu="return false;" </c:if> href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}"
												 class="lnkQVPopup prodTitle" data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}&registryView=guest"
												 title="${productName}" data-skuId="${skuID}" data-disableItemDetails="true" data-registryId="${registryId}" data-sopt="${ltlShipMethod}" data-productId="${productId}" data-refNum="${refNum}" <c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
															data-customizedPrice="${customizedPrice1}" data-customizationDetails="${customizationDetails}" data-color="${color}" data-size="${size}" data-personalizedImageUrls="${personalizedImageUrls}"
															data-personlisedPrice="${personlisedPrice1}" data-personalizedImageUrlThumbs="${personalizedImageUrlThumbs}" data-personalizationType= "${personalizationType}"</c:if>>
												</c:if>	
												 <dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.mediumImage" />
													<c:choose>
													    <c:when test="${not empty refNum && not empty personalizedImageUrlThumbs}">
                                             						<img src="${personalizedImageUrlThumbs}" alt="<c:out value='${productName}'/>" class="prodImage stretch" >
                                             			</c:when>
														<c:when test="${empty imageURL}">
															<img src="${imagePath}/_assets/global/images/no_image_available.jpg"  alt="${productName}" class="prodImage" />
														</c:when>
														<c:otherwise>
															<img src="${scene7Path}/${imageURL}" data-original="${scene7Path}/${imageURL}" alt="${productName}" class="prodImage stretch" />
														</c:otherwise>
													</c:choose>
											  <c:if test="${active}">
												</a>
											  </c:if>
												</c:otherwise>
											</c:choose>
											
										</div>
										<div class="productContainer small-8 medium-10 columns no-padding-right print-10">
											<div class="productTab productHeading clearfix">
												<div class="productName small-12 medium-3 columns print-3">
													<dsp:getvalueof var="upc" param="regItem.sKUDetailVO.upc" />
													<dsp:getvalueof var="color" param="regItem.sKUDetailVO.color" />
													<dsp:getvalueof var="size" param="regItem.sKUDetailVO.size" />
													<dsp:getvalueof var="skuInCartFlag" param="regItem.sKUDetailVO.inCartFlag" />
														<c:if test="${upc ne null}">
															<div class="fl upc"><span class="bold"> <bbbl:label key='lbl_mng_regitem_upc' language="${pageContext.request.locale.language}" /> </span>
															<span>${upc}</span></div>
														</c:if>
												</div>
												<div class="price small-1 medium-2 columns print-2 hide-for-small-only"><bbbl:label key='lbl_mng_regitem_price' language="${pageContext.request.locale.language}" /></div>
												<div class="requested small-2 medium-1 columns print-2 hide-for-small-only no-padding"><bbbl:label key='lbl_mng_regitem_requested' language="${pageContext.request.locale.language}" /></div>
												<div class="purchase small-2 medium-1 columns print-2 hide-for-small-only no-padding"><bbbl:label key='lbl_mng_regitem_purchased' language="${pageContext.request.locale.language}" /></div>
												<div class="quantity small-2 columns print-2 hide-for-small-only"><bbbl:label key='lbl_mng_regitem_quantity' language="${pageContext.request.locale.language}" /></div>
												<div class="productLastColumn small-2 columns print-2 hide-for-small-only"> </div>
											</div>
											<div class="row productTab productContent clearfix">
												<div class="productName small-12 medium-3 columns print-3">
													<c:choose>
														<c:when test="${active && !(isGiftGiver && ltlItemFlag)}">
														<span class="blueName">
															<a <c:if test="${not empty refNum}"> " oncontextmenu="return false;" </c:if> href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}"
															class="lnkQVPopup prodTitle" data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}&registryView=guest"
															title="${productName}" data-skuId="${skuID}" data-disableItemDetails="true" data-registryId="${registryId}" data-sopt="${ltlShipMethod}" data-productId="${productId}" data-refNum="${refNum}" <c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
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
                                                    <span class="prodColor noPad"><c:if test='${not empty color}'><bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/>&nbsp;:&nbsp;<dsp:valueof value="${color}" valueishtml="true" /></c:if><c:if test='${not empty size}'><c:if test='${not empty color}'><br/></c:if><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/>&nbsp;:&nbsp;<dsp:valueof value="${size}" valueishtml="true" /></c:if></span>
                                                    <div class="personalizationAttributes"> 
                                                       <c:if test="${not empty personalisedCode}">
                                                            <span class="exim-code">${eximCustomizationCodesMap[personalisedCode]}:</span>
				        									<span class="exim-detail">${customizationDetails}</span>
                                                       </c:if>
                                                 	</div>
	                                                <div class="personalizationAttributes">
                                                       <div class="pricePersonalization">
                                                           <c:choose>
	                                                           <c:when test="${personalizationType == 'PB'}">
	                                                           <span class="fee-detail">
	                                                                 <bbbl:label key='lbl_PB_Fee_detail' language="${pageContext.request.locale.language}" />      
	                                                           </span>                                        
	                                                           </c:when>
	                                                           <c:when test="${personalizationType == 'PY'}">
                                                                     <span class="fee-detail">
                                                                     	<dsp:valueof value="${customizedPrice1}"/>
                                                                     </span>
                                                                     <span class="cust-price">
                                                                     	<bbbl:label key='lbl_exim_added_price' language="${pageContext.request.locale.language}" />
                                                                     </span>
	                                                           </c:when>
	                                                           <c:when test="${personalizationType == 'CR'}">
	                                                               		<span class="fee-detail">
			                                                               <dsp:include page="/global/gadgets/formattedPrice.jsp">
			                                                               			<dsp:param name="price" value="${customizedPrice}"/>
			                                                               </dsp:include>
	                                                               		</span>
	                                                               <span class="cust-price">
	                                                               	 <c:choose>
																 		<c:when test="${customizeTxt eq true}">
																 			<bbbl:label key="lbl_exim_cr_added_price_customize"
																							language="${pageContext.request.locale.language}"/>
																 		</c:when>
																 		<c:otherwise>
																 			<bbbl:label key="lbl_exim_cr_added_price"
																							language="${pageContext.request.locale.language}"/>
																 		</c:otherwise>
																 	</c:choose>
	                                                               </span>
	                                              			   </c:when>
                                                           </c:choose>
                                                       </div>
	                                                </div>
												</div>
												
												<dsp:getvalueof var="ltlShipMethodDesc" param="regItem.ltlShipMethodDesc"/>
												<dsp:getvalueof var="deliverySurcharge" param="regItem.deliverySurcharge"/> 
												<dsp:getvalueof var="assemblyFee" param="regItem.assemblyFees"/>
												
												<dsp:getvalueof var="ltlShipMethod" param="regItem.ltlDeliveryServices" />										
												<dsp:getvalueof var="shipMethodUnsupported" param="regItem.shipMethodUnsupported"/>
												
												<div class="price bold small-12 medium-2 columns print-2">
												<c:choose>
												<c:when test="${not empty personalisedCode}">
												<%--  BBBH-5862 : TBS | For CR type of personalised SKU, actual price is getting displayed in guest view. Katori price is not getting displayed --%>
																<div><dsp:include page="/global/gadgets/formattedPrice.jsp">												
													               <span><dsp:param name="price" value="${personlisedPrice}"/>
													        	</dsp:include></span>															
																</div>
																<input type="hidden" value="${personlisedPrice}" class="itemPrice"/>
															</c:when>
												<c:otherwise>
												<dsp:droplet name="PriceDroplet">
									                <dsp:param name="product" value="${productId}" />
									                <dsp:param name="sku" value="${skuID}"/>
									                <dsp:oparam name="output">
									                  	<dsp:setvalue param="theListPrice" paramvalue="price"/>
														<c:if test="${skuInCartFlag}">
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
														 
									                  	<dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
										                  <c:choose>
										                  	<c:when test="${not empty profileSalePriceList}">
										                      	<dsp:droplet name="PriceDroplet">
											                        <dsp:param name="priceList" bean="Profile.salePriceList"/>
											                        <dsp:oparam name="output">
											                            <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
											                            <c:choose>
																			<c:when test="${skuInCartFlag}">
																				<c:set var="listPrice" value="${inCartPrice}" />
																			</c:when>
																			<c:otherwise>
																				<dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
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


												                        	<%-- (<bbbl:label key='lbl_mng_regitem_reg' language="${pageContext.request.locale.language}" />&nbsp; 	<dsp:include page="/global/gadgets/formattedPrice.jsp">
														          								<dsp:param name="price" value="${price }"/>
														              				</dsp:include>)

																		<bbbl:label key='lbl_mng_regitem_you_save' language="${pageContext.request.locale.language}" />&nbsp;
															      				<dsp:include page="/global/gadgets/formattedPrice.jsp">
												                                	<dsp:param name="price" value="${price-listPrice}"/>
												                              	</dsp:include>(<fmt:formatNumber type="number" maxFractionDigits="2" value="${(listPrice/100)*price}" />%)
												                         --%>
													                </dsp:oparam>
											                        <dsp:oparam name="empty">
												                    	<c:choose>
																			<c:when test="${skuInCartFlag}">
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
											                	</dsp:droplet><%-- End price droplet on sale price --%>
										                    </c:when>
										                    <c:otherwise>
										                      	<c:choose>
																	<c:when test="${skuInCartFlag}">
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
									                </dsp:oparam>
									            </dsp:droplet>
									            <c:if test="${ltlItemFlag}">
									            <c:choose>
																<c:when test="${ltlShipMethod == null or ltlShipMethod == '' or shipMethodUnsupported}">
																	<li class="deliveryLtl"><bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/>:</li>
																	<li class="deliverypriceLtl">
																		<span class="deliverypriceLtlClass">TBD</span>
																		<img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" />
																	</li>
																</c:when>
																<c:otherwise>
																	 <c:choose>
																		 <c:when test="${ltlShipMethod== 'LWA' }">
																		   <li class="deliveryLtl">  Incl White Glove <span>With Assembly:</span></li>
																		   <li class="deliverypriceLtl"><span class="deliverypriceLtlClass"><dsp:valueof value="${deliverySurcharge+assemblyFee}" number="0.00" converter="currency"/></span>
																		   <img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" /></li>
																		</c:when>
																		<c:otherwise>
																		<li class="deliveryLtl">
																				 Incl ${ltlShipMethodDesc}:
																					 </li>     <li class="deliverypriceLtl"><span class="deliverypriceLtlClass">       <c:if test="${deliverySurcharge eq 0.0}"> FREE</c:if>
																											 <c:if test="${deliverySurcharge gt 0.0}"><dsp:valueof value="${deliverySurcharge}" number="0.00" converter="currency"/></c:if>
																			</span>  <img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" /></li>
																			</c:otherwise>
																	</c:choose>
																</c:otherwise>
															 </c:choose>
												 </c:if>
															<c:if test="${ltlShipMethod != null && ltlShipMethod != '' && !shipMethodUnsupported}">
															  <li class="itemLtl">Item Price
																<span class="itempriceLtl">
																	<dsp:include page="registry_items_guest_category_frag.jsp">
																		<dsp:param name="productId" value="${productId}"/>
																		<dsp:param name="skuID" value="${skuID}"/>
																		<dsp:param name="skuInCartFlag" value="${skuInCartFlag}"/>
																		<dsp:param name="incartPriceList" value="${incartPriceList}"/>
																	</dsp:include>
																</span>
															  </li>
												 </c:if>
												</c:otherwise>
												</c:choose>
												
															  
												</div>
												<div class="requested bold small-12 medium-1 columns print-2"><dsp:valueof param="regItem.qtyRequested" /></div>
												<div class="purchase bold small-12 medium-1 columns print-2"><dsp:valueof param="regItem.qtyPurchased" /></div>
												<div class="quantity bold small-12 medium-2 columns print-2">
												<c:choose>
													<c:when test="${((active || inStoreSku) || (currentSiteId ne TBS_BedBathCanadaSite))}">
													<div class="input alpha">
														<div class="qty-spinner fl">
															<a href="#" class="button minus secondary" title="<bbbl:label key='lbl_decrease_quantity' language="${pageContext.request.locale.language}" />"><span/></a>
															<input name="qty" type="text" value="1" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" class="input_tiny_giftView quantity-input qty itemQuantity" data-change-store-submit="qty" data-change-store-errors="required digits nonZero" role="textbox" aria-required="false" maxlength="2" />
															<a href="#" class="button plus secondary" title="<bbbl:label key='lbl_increase_quantity' language="${pageContext.request.locale.language}" />"><span/></a>
														</div>
													</div>
													</c:when>
													<c:otherwise>1</c:otherwise>
												</c:choose>
												</div>
												<div class="productLastColumn small-12 medium-2 columns print-2">
												<dsp:getvalueof var="skuID" param="regItem.sku" />
												<input type="hidden" name="skuId" value="${skuID}" class="skuId changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" />
												<input type="hidden" name="registryId" value="${registryId}" class="registryId" data-change-store-submit="registryId" data-change-store-errors="required"  />
													<dsp:droplet name="GiftRegistryFetchProductIdDroplet">
														<dsp:param name="skuId" value="${skuID}" />
														<dsp:oparam name="output">
															<dsp:getvalueof var="productId" param="productId" />
															<input type="hidden" name="prodId" value="${productId}" class="productId" data-change-store-submit="prodId" data-change-store-errors="required"  />
														</dsp:oparam>
														<dsp:oparam name="error">
															<dsp:getvalueof param="errorMsg" var="errorMsg" />
															<div class="error">
																<bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}" />
															</div>
														</dsp:oparam>
													</dsp:droplet>
													
													<dsp:getvalueof param="regItem.qtyRequested" var="requestedQuantity" />
													<dsp:getvalueof param="regItem.qtyPurchased" var="purchasedQuantity" />
								                    <c:if test="${purchasedQuantity ge requestedQuantity}">
														<div class="purchasedConfirmation">
														  <h2><bbbl:label key='lbl_mng_regitem_purchased_confirm' language="${pageContext.request.locale.language}" /></h2>
														</div>
													</c:if>

													<dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>															
													<dsp:getvalueof var="userRegCount" value="${fn:length(sessionMapValues.userRegistriesList)}"/>
													<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO.registryId" var="sessionRegId"/>
													 <dsp:getvalueof var="ltlItemFlag" param="regItem.sKUDetailVO.ltlItem" />
		                                               <input type="hidden" value="${ltlItemFlag}" name="isLtlItem" />
		                                               <input type="hidden" name="ltlShipMethod" value="${ltlShipMethod}" />
													<c:choose>
														<c:when test="${!isTransient }">																			
															<c:if test="${userRegCount ne 0}">																					
																<!-- LTL-328 Hide Registry Button for LTL item for Logged In User -->
																<%--COPY REGISTRY--%>
																<c:choose>
																	<c:when test="${registryId!=sessionRegId}">                                                	
																		<div class=" button_active copyRegButton <c:if test='${not empty refNum}'>disabled</c:if>" >
																			<input type="button" class="btnAddToRegistry s2 tiny button secondary"  
																					<c:if test="${not empty refNum}">disabled="disabled"</c:if> name="" title="Add to Registry" value="Add to Registry" role="button" aria-pressed="false" />			
																		</div>
																	</c:when>
																</c:choose>	
																<%-- END COPY REGISTRY--%>																	
															                																				
															</c:if>																		
														</c:when>
													</c:choose>

												</div>
											</div>

											<div class="productFooter small-12 columns">
												<div class="fl">
													 <dsp:getvalueof var="skuDetailVO" param="regItem.sKUDetailVO"/>

										            <c:if test="${active}">
										            	<ul class="noMar clearfix freeShipContainer prodAttribWrapper">
														<dsp:droplet name="/atg/dynamo/droplet/ForEach">
															<dsp:param name="array" param="regItem.sKUDetailVO.skuAttributes" />
															<dsp:param name="elementName" value="attributeVOList"/>
															<dsp:oparam name="output">
																<dsp:getvalueof var="pageName" param="key" />

																<c:if test="${pageName eq 'RLP'}">
																	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	<dsp:param param="attributeVOList" name="array" />
																	<dsp:param name="elementName" value="attributeVO"/>
																	<dsp:param name="sortProperties" value="+priority"/>
																		<dsp:oparam name="output">
																			 <c:set var="currentCount"><dsp:valueof param="count" /></c:set>
																			 <c:set var="actionURL"><dsp:valueof param="attributeVO.actionURL" /></c:set>
																	         <c:choose>
																				<c:when test="${not empty actionURL}">
																					<li><a href="${actionURL}" class="popup"><span><dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true"/></span></a></li>
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
																					<c:set var="nextStyle">prodPrimaryAttribute</c:set>
																				</c:otherwise>
															            	</c:choose>
																		</dsp:oparam>
																	</dsp:droplet>
																</c:if>
															</dsp:oparam>
														</dsp:droplet>
														</ul>
														<%-- <c:if test="${skuDetailVO.shippingSurcharge gt 0.0}">
															<span class="${nextStyle}"><bbbl:label key="lbl_mng_surcharge" language="<c:out param='${language}'/>"/>${skuDetailVO.shippingSurcharge}</span>
														</c:if>
														<c:if test="${skuDetailVO.vdcSku}">
															<span class="prodSecondaryAttribute">${skuDetailVO.vdcSKUShipMesage}</span>
														</c:if>	 --%>
													</c:if>
													</div>
												<dsp:getvalueof param="regItem.qtyRequested" var="requestedQuantity" />
												<dsp:getvalueof param="regItem.qtyPurchased" var="purchasedQuantity" />
													<c:if test="${requestedQuantity > purchasedQuantity}">
														<div class="right">
															<%--  For OOS item , this button is made hidden so that find in store functionality can work --%>
															<dsp:droplet name="TBSItemExclusionDroplet">
																<dsp:param name="skuId" value="${skuID}" />
																<dsp:param name="siteId" value="${currentSiteId}" />
																<dsp:oparam name="output">
																<dsp:getvalueof var="isItemExcluded" param="validItemExcludedSku"/>
																<dsp:getvalueof var="caDisabled" param="caDisabled"/>
																<c:choose>
																	<c:when test="${isItemExcluded || caDisabled}">
																		<div class="button_disabled hidden addToCart marBottom_5">
																			<input class="tiny button" type="button" disabled="disabled" name="btnAddToCart" title="Add to cart" value="Add to Cart" role="button" aria-pressed="false" />
																		</div>
																	</c:when>
																	<c:when test="${not bopusAllowed}">
																		<div class="button button_disabled addToCart hidden marBottom_5">
																			<input type="button" disabled="disabled" name="btnAddToCart" title="Add to cart" value="Add to Cart" role="button" aria-pressed="false" />
																		</div>
																	</c:when>
																	<c:otherwise>
																		<div class="hidden ${button_active} addToCart marBottom_5">
																			<input class="tiny button" type="button" name="btnAddToCart" title="Add to cart" value="Add to Cart" role="button" aria-pressed="false" />
																		</div>
																	</c:otherwise>
																</c:choose>
																</dsp:oparam>
															</dsp:droplet>
															<dsp:getvalueof var="bopusAllowed" param="regItem.sKUDetailVO.bopusAllowed"/>
															<!-- LTL-328 Hide Pick Up in Store for LTL item -->
															 <dsp:getvalueof var="ltlItemFlag" param="regItem.SKUDetailVO.ltlItem" />
				                                               <c:choose>
													                <c:when test="${ltlItemFlag}">
																		<dsp:a iclass="nearbyStores nearby-stores in-store hidden" href="/tbs/selfservice/find_tbs_store.jsp">
																		<dsp:param name="id" value="${productId}" />
																		<dsp:param name="siteId" value="${currentSiteId}" />
																		<dsp:param name="registryId" value="${registryId}" />
																		<dsp:param name="skuid" value="${skuID}"/>
																		<dsp:param name="itemQuantity" value="1"/>
																		<bbbl:label key='lbl_mng_regitem_pickupintbs' language="${pageContext.request.locale.language}" />
																	</dsp:a>
													                </c:when>
													                <c:when test="${not empty refNum}">
																	<%-- <a class="pickupStore changeStore" title="<bbbl:label key='lbl_mng_regitem_pickupinstore' language='${pageContext.request.locale.language}' />" href="#"><bbbl:label key='lbl_mng_regitem_pickupinstore' language="${pageContext.request.locale.language}" /></a> --%>
																	<dsp:a iclass="nearbyStores nearby-stores in-store disabled" href="/tbs/selfservice/find_tbs_store.jsp">
																		<dsp:param name="id" value="${productId}" />
																		<dsp:param name="siteId" value="${currentSiteId}" />
																		<dsp:param name="registryId" value="${registryId}" />
																		<dsp:param name="skuid" value="${skuID}"/>
																		<dsp:param name="itemQuantity" value="1"/>
																		<bbbl:label key='lbl_mng_regitem_pickupintbs' language="${pageContext.request.locale.language}" />
																	</dsp:a>
												                </c:when>
													                <c:otherwise>
															<c:if test="${not bopusAllowed}">
															<%-- <a class="pickupStore changeStore" title="<bbbl:label key='lbl_mng_regitem_pickupinstore' language='${pageContext.request.locale.language}' />" href="#"><bbbl:label key='lbl_mng_regitem_pickupinstore' language="${pageContext.request.locale.language}" /></a> --%>
																<dsp:a iclass="nearbyStores nearby-stores in-store" href="/tbs/selfservice/find_tbs_store.jsp">
																	<dsp:param name="id" value="${productId}" />
																	<dsp:param name="siteId" value="${currentSiteId}" />
																	<dsp:param name="registryId" value="" />
																	<dsp:param name="skuid" value="${skuID}"/>
																	<dsp:param name="itemQuantity" value="1"/>
																	<bbbl:label key='lbl_mng_regitem_pickupintbs' language="${pageContext.request.locale.language}" />
																</dsp:a>
																
															</c:if>
													                </c:otherwise>
													            </c:choose>
														</div>
													</c:if>
											</div>
										</div>
									</div><hr/>
									</c:if>
								</dsp:oparam>
                                <dsp:oparam name="outputEnd">
                                			<%-- <li  class="productRow textRight returnTopRow">
													<a class="returnToTop" href="#backToTop"><bbbl:label key='lbl_mng_regitem_back_to_top' language="${pageContext.request.locale.language}" /></a>
											</li>
											</div> --%>
                                        </div></dd>
                                </dsp:oparam>
							</dsp:droplet>
						</c:if>

					<%-- <c:if test="${countIE eq sizeIE}">
						</dd>
					</c:if> --%>
					</dsp:oparam>
				</dsp:droplet>
			</dsp:oparam>
		</dsp:droplet>
	</c:if>
 </dl>
	<div id="nearbyStore" class="reveal-modal" data-reveal>	</div>
	<script type="text/javascript">
		$(document).ready(function(){
			$(".nearbyStores").attr("data-reveal-id","nearbyStore");
			$(".nearbyStores").attr("data-reveal-ajax","true");
			$(document).foundation('reflow');
		});
	</script>
	<c:set var="link" value="${productId1}${productId2}${productId3}${productId4}" scope="request"/>
</dsp:page>