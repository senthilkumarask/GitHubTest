<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/SKUDetailDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFetchProductIdDroplet" />	
	<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
    <dsp:importbean bean="/com/bbb/commerce/checkout/droplet/IsProductSKUShippingDroplet"/>
    <dsp:importbean bean="/com/bbb/commerce/droplet/TBSItemExclusionDroplet"/>
    <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	
	<c:set var="TBS_BedBathUSSite">
		<bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BuyBuyBabySite">
		<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BedBathCanadaSite">
		<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="customizeCTACodes">
		<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
	</c:set>				
		
	<c:choose>
		<c:when test="${currentSiteId eq TBS_BedBathUSSite || currentSiteId eq TBS_BuyBuyBabySite}">
			<c:set var="incartPriceList"><bbbc:config key="BedBathUSInCartPriceList" configName="ContentCatalogKeys" /></c:set>
		</c:when>
		<c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
			<c:set var="incartPriceList"><bbbc:config key="BedBathCanadaInCartPriceList" configName="ContentCatalogKeys" /></c:set>
		</c:when>
   </c:choose>
	   
	<%-- Droplet Placeholder --%>
	<dsp:getvalueof var="registryId" param="registryId" />
	<dsp:getvalueof var="skuId" value="" />
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="sortSeq" param="sorting"/>
	<dsp:getvalueof var="view" param="view"/>
	<dsp:getvalueof var="eventType" param="eventType" />
	<c:set var="scene7Path">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
	<c:set var="enableLTLReg"><bbbc:config key="enableLTLRegForSite" configName="FlagDrivenFunctions" /></c:set>
	<c:set var="enableKatoriFlag"><bbbc:config key="enableKatori" configName="EXIMKeys" /></c:set>
	<c:set var="valueIndex">0</c:set>
	<dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
	<dl class="accordion" data-accordion="">
	<dsp:include page="registry_modified_item_form.jsp">
	</dsp:include>
	<dsp:form id="formID1" method="post">
	
	<dsp:input type="hidden" bean="GiftRegistryFormHandler.size" paramvalue="totEntries" />
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="priceRangeList" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="sizeIE" param="size"/>
			<dsp:getvalueof var="countIE" param="count"/>
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
			<dsp:getvalueof var="idxPrice" param="index"/>
			<dsp:getvalueof var="priceRange" param="element"/>
			<dsp:droplet name="ForEach">
				<dsp:param name="array" param="categoryBuckets" />
				<%--<dsp:param name="sortProperties" value="+_key" /> --%>
				<dsp:oparam name="output">
					<dsp:getvalueof var="idxCat" param="index"/>
					<dsp:getvalueof var="bucketName" param="key" idtype="java.lang.String" />
					<dsp:getvalueof var="bucket" param="categoryBuckets.${bucketName}" idtype="java.util.List" />
                    <dsp:getvalueof var="bucketNameNew" value="${fn:replace(bucketName, '$','')}"/>
                    <dsp:getvalueof var="bucketNameNew" value="${fn:replace(bucketNameNew, '-','')}"/>
                    <dsp:getvalueof var="count" value="${fn:length(bucket)}"/>
					<c:if test="${priceRange eq bucketName}">
						  <dd class="accordion-navigation accordionReg accordionReg active">
							<a href="#accordionDiv${fn:replace(bucketNameNew, ' ','')}${count}"><dsp:valueof param="key" />(${count}) </a>
							<%-- ForEach for List<RegistryItemVO> listRegistryItemVO --%>
							<div id="accordionDiv${fn:replace(bucketNameNew, ' ','')}${count}" class="accordionDiv content active">
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
									<c:when test="${not empty itemList}">
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
									<dsp:getvalueof var="skuInCartFlag" param="regItem.sKUDetailVO.inCartFlag" />
									<dsp:getvalueof var="idx" param="index"/>
								<div class="qtyWrapper productRow btngiftViewTopMargin giftViewProductChangeStoreWrapper addToCartGiftRegWrapper clearfix">
									
									<input type="hidden" name="bts" value="false" class="addToCartSubmitData" />
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
										<dsp:getvalueof var="imageURL" param="regItem.sKUDetailVO.skuImages.mediumImage" />
									<div class="productImage small-4 medium-2 columns">
									
                                     <c:if test="${active}">
                                     <a class="prodImg lnkQVPopup" href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}" 
                                      data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}"  data-skuId="${skuID}" data-registryId="${registryId}" data-productId="${productId}" data-sopt="${ltlShipMethod}" data-refNum="${refNum}"  title="<c:out value='${productName}'/>" <c:if test="${not empty refNum}">data-personalisedCode="${personalisedCode}"
													data-customizedPrice="${customizedPrice1}" data-customizationDetails="${customizationDetails}" data-color="${color}" data-size="${size}" data-personalizedImageUrls="${personalizedImageUrls}"
													data-personlisedPrice="${personlisedPrice1}" data-personalizedImageUrlThumbs="${personalizedImageUrlThumbs}" data-personalizationType= "${personalizationType}"</c:if>>
                                    </c:if>
                           				<c:choose>
                           					<c:when test="${not empty refNum && not empty personalizedImageUrlThumbs}">
                           						<img src="${personalizedImageUrlThumbs}" data-original="${personalizedImageUrlThumbs}" alt="<c:out value='${productName}'/>" class="prodImage stretch" />
                           					</c:when>
                           					<c:when test="${empty imageURL}">
                                                 <img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="96" width="96" alt="${productName}" class="prodImage" />
                                             </c:when>
                                             <c:otherwise>
                                                 <img src="${scene7Path}/${imageURL}" data-original="${scene7Path}/${imageURL}" alt="${productName}" class="prodImage stretch" />
                                             </c:otherwise>
                                         </c:choose>
                                    <c:if test="${active}">
                           			 </a>
                           			</c:if>
                           			
									</div>
									<dsp:getvalueof var="color" param="regItem.sKUDetailVO.color" />
									<dsp:getvalueof var="upc" param="regItem.sKUDetailVO.upc" />
									<dsp:getvalueof var="size" param="regItem.sKUDetailVO.size" />
									<dsp:getvalueof var="ltlItemFlag" param="regItem.sKUDetailVO.ltlItem" />
									<dsp:getvalueof var="deliverySurcharge" param="regItem.deliverySurcharge"/> 
									<dsp:getvalueof var="assemblyFee" param="regItem.assemblyFees"/>
									<c:if test="${ltlItemFlag}">
									<dsp:getvalueof var="shipMethodUnsupported" param="regItem.shipMethodUnsupported"/>
									</c:if>
									<div class="productContainer small-8 medium-10 columns no-padding-right">
											<div class="productTab productHeading clearfix">
												<div class="productName small-12 medium-3 columns">
													<c:if test="${upc ne null}">
														<div class="fl upc"><span class="bold"> <bbbl:label key='lbl_mng_regitem_upc' language="${pageContext.request.locale.language}" /> </span> 
														<span>${upc}</span></div>
													</c:if>
												</div>
												<div class="small-2 columns price hide-for-small-only">Price</div>
												<div class="small-2 columns requested hide-for-small-only"><bbbl:label key='lbl_mng_regitem_purchased' language="${pageContext.request.locale.language}" /></div>
												<div class="small-2 columns purchase hide-for-small-only"><bbbl:label key='lbl_mng_regitem_requested' language="${pageContext.request.locale.language}" /></div>
												<div class="small-1 columns productLastColumn hide-for-small-only"> </div>
											</div>
									<div class="productTab productContent clearfix">
										<dsp:getvalueof var="displayName" param="regItem.sKUDetailVO.displayName" />
										<div class="productName small-12 medium-3 columns">
											<span class="blueName">
												<c:choose>
													<c:when test="${active}">
														<a href="${contextPath}${finalUrl}?skuId=${skuID}&registryId=${registryId}" 
													class="lnkQVPopup prodTitle" data-pdp-qv-url="pdp_quick_view.jsp?skuId=${skuID}&registryId=${registryId}&productId=${productId}"
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
											</span><br />
											 <span><c:if test='${not empty color}'><bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}"/>&nbsp;:&nbsp;<dsp:valueof value="${color}" valueishtml="true" /></c:if><c:if test='${not empty size}'><c:if test='${not empty color}'><br/></c:if><bbbl:label key="lbl_item_size" language ="${pageContext.request.locale.language}"/>&nbsp;:&nbsp;<dsp:valueof value="${size}" valueishtml="true" /></c:if>
                                         	</span>
                                         	<div class="personalizationAttributes"> 
                                                 <c:if test="${not empty personalisedCode}">
	        											<span class="exim-code">${eximCustomizationCodesMap[personalisedCode]}:</span>
				        								<span class="exim-detail">${customizationDetails}</span>
     											</c:if>
     										</div>
     										<c:choose>
												<c:when test="${not empty personalisedCode && fn:contains(customizeCTACodes, personalisedCode)}">
													<c:set var="customizeTxt" value="true"/>
												</c:when>
												<c:otherwise>
													<c:set var="customizeTxt" value="false"/>
												</c:otherwise>
											</c:choose>
     									 <c:if test="${not empty personalisedCode}">
     										<div class="personalizationAttributes">
	        										<div class="pricePersonalization">
	        											<c:choose>
	        											<c:when test="${personalizationType == 'PB'}">
	        												<span class="fee-detail">
	        													<bbbl:label key='lbl_PB_Fee_detail' language="${pageContext.request.locale.language}" />	
	        												</span>        												
	        											</c:when>
	        											<c:when test="${personalizationType == 'PY'}">
		        											<span class="cust-price"><dsp:valueof value="${customizedPrice1}"/></span>
		        											<span class="fee-detail"><bbbl:label key='lbl_exim_added_price' language="${pageContext.request.locale.language}" /></span>
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
 										</c:if>
										    
                                            </div>

		<div class="price bold small-12 medium-2 columns">
			<span class="toalpriceLtl">
			<dsp:droplet name="PriceDroplet">
					<dsp:param name="product" value="${productId}" />
					<dsp:param name="sku" value="${skuID}" />
					<dsp:oparam name="output">
						<dsp:setvalue param="theListPrice" paramvalue="price" />
						
						<%-- BBBH-2890 --%>
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
										<%-- BBBH-2890 --%>
										<c:choose>		
											<c:when test="${skuInCartFlag}">
												<c:set var="listPrice" value="${inCartPrice}" />
											</c:when>
											<c:otherwise>
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
		</span>
				
								<c:choose>
		<c:when test="${ltlItemFlag}">
		<dsp:getvalueof var="ltlShipMethodDesc" param="regItem.ltlShipMethodDesc"/>
		<dsp:getvalueof var="deliverySurcharge" param="regItem.deliverySurcharge"/>
		<dsp:getvalueof var="assemblyFee" param="regItem.assemblyFees"/>
		<dsp:getvalueof var="shipMethodUnsupported" param="regItem.shipMethodUnsupported"/>
		<dsp:getvalueof var="ltlShipMethod" param="regItem.ltlDeliveryServices"/>
		<dsp:getvalueof var="isDSLUpdateable" param="regItem.dslUpdateable"/>		
		
			<li class="price grid_2 omega ltlPrice">
				<ul>
					
				<c:choose>
						<c:when test="${empty ltlShipMethod or shipMethodUnsupported}">
                          <c:choose>
							 <c:when test="${isDSLUpdateable}">
							 	<li class="small-11">
							 		<a class="updateDsl"><bbbl:label key='lbl_update_service_level' language="${pageContext.request.locale.language}" /></a>							 		
							 	</li>
							 	<li class="deliveryLtl"></li>
	                        	<li class="deliverypriceLtl"><span class="deliverypriceLtlClass"></span><img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo hidden" /> </li>
							 </c:when>
	                         <c:otherwise>
								  <li class="deliveryLtl"><bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/>:</li>
	                              <li class="deliverypriceLtl">
	                              	 <span class="deliverypriceLtlClass">TBD</span>
	                              	 <img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" />
	                              </li>
	                         </c:otherwise>
	                       </c:choose>
	                      </c:when>
						<c:otherwise>
							 <c:choose>
								 <c:when test="${ltlShipMethod== 'LWA' }">
								   <li class="deliveryLtl"> Incl White Glove <span>With Assembly:</span></li>
								   <li class="deliverypriceLtl">
								   		<span class="deliverypriceLtlClass"><dsp:valueof value="${deliverySurcharge+assemblyFee}" number="0.00" converter="currency"/></span>
								   		<img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" />
								   </li>
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
				 
				 <li class="itemLtl"><c:if test="${ltlShipMethod != null && ltlShipMethod != '' && !shipMethodUnsupported}">Item Price</c:if>
				<span class="itempriceLtl">
				<dsp:droplet name="PriceDroplet">
					<dsp:param name="product" value="${productId}" />
					<dsp:param name="sku" value="${skuID}" />
					<dsp:oparam name="output">
						<dsp:setvalue param="theListPrice" paramvalue="price" />
						<%-- BBBH-2890 --%>
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
											<c:when test="${skuInCartFlag}">
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
								<dsp:getvalueof var="price" vartype="java.lang.Double"
									param="theListPrice.listPrice" />
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
			</li>
			</ul>
			</li> 
		</c:when>
		</c:choose>
				<li class="price grid_2 omega ltlPrice">
		<c:choose>
			<c:when test="${not empty personalisedCode}">
				<div>
					<dsp:include page="/global/gadgets/formattedPrice.jsp">
						<dsp:param name="price" value="${personlisedPrice}" />
					</dsp:include>
					
				</div>
				<input type="hidden" value="${personlisedPrice}" class="itemPrice" />
			</c:when>
		
		</c:choose>
		</div>
										<div class="requested bold small-12 medium-2 columns">
										<dsp:valueof param="regItem.qtyPurchased"/>
										</div> 
							
										<div class="purchase quantity small-12 medium-3 columns">
                                                                                       
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
                                            
                                            <dsp:getvalueof var="itemType" param="regItem.itemType"/>
                                            <dsp:getvalueof var="ltlDeliveryPrices" param="regItem.ltlDeliveryPrices"/>
                                            
                                            <dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].itemTypes" type="hidden" value="${itemType}"/>
											<input type="hidden" name="itemTypes" value="${itemType}" class="skuId frmAjaxSubmitData" />
											<input type="hidden" name="updateDslFromModal" value="" class="frmAjaxSubmitData" />
											<input type="hidden" name="alternateNum" value="" class="frmAjaxSubmitData" />											 
											<c:choose>
												<c:when test="${not empty ltlShipMethod}">
												<input type="hidden" name="ltlDeliveryServices" value="${ltlShipMethod}" class="frmAjaxSubmitData" />
												</c:when>
												<c:otherwise>
												<input type="hidden" name="ltlDeliveryServices" value="" class="frmAjaxSubmitData" />
												</c:otherwise>
											</c:choose>
											
											<dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].ltlDeliveryPrices" type="hidden" value="${ltlDeliveryPrices}"/>
											<input type="hidden" name="ltlDeliveryPrices" value="${itemType}" class="frmAjaxSubmitData" />
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
                                           <%--  <dsp:getvalueof var="ownerUrl" value="${contextPath}/giftregistry/view_registry_owner.jsp?registryId=${registryId}&eventType=${eventType}&sorting=2"/>
                                            <dsp:input bean="GiftRegistryFormHandler.successURL"  type="hidden" value="${ownerUrl}" />
                                            <dsp:input bean="GiftRegistryFormHandler.errorURL"	type="hidden" value="${ownerUrl}" />	 --%>
                                             <dsp:input bean="GiftRegistryFormHandler.queryParam"  type="hidden" value="registryId=${registryId}&eventType=${eventType}&sorting=2" />
                                                     <dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden" value="registryItemsOwnerPrice" />
                                            <dsp:getvalueof var="qtyRequested" param="regItem.qtyRequested"/>
											<div class="input qty-spinner">
                                                    <%-- 	<dsp:input name="qty" type="text" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" iclass="input_tiny_giftView fl itemQuantity" />  --%>
														<%-- <dsp:input bean="GiftRegistryFormHandler.value.purchasedQuantity" type="text" title="<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />" value="${qtyRequested}" name="qty" iclass="valQtyGiftReg input_tiny_giftView fl itemQuantity">
                                                        <dsp:tagAttribute name="data-change-store-errors" value="required digits nonZero"/>
                                                        <dsp:tagAttribute name="data-change-store-submit" value="qty"/>
                                                        </dsp:input> --%>
                                                        <c:set var="titleqt">
                                                        	<bbbl:label key='lbl_quantity_input_box' language="${pageContext.request.locale.language}" />
                                                        </c:set>
                                                        <a href='#' class="button minus secondary"><span></span></a>
                                                        <dsp:input bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].quantity" type="text" paramvalue="regItem.qtyRequested" id="quantityRequested" title="${titleqt}" iclass="valQtyGiftReg input_tiny_giftView quantity-input qty itemQuantity" maxlength="2">
                                                            <dsp:tagAttribute name="data-change-store-errors" value="required digits nonZero"/>
                                                            <dsp:tagAttribute name="data-change-store-submit" value="qty"/>
                                                            <dsp:tagAttribute name="aria-required" value="false"/>
                                                            <dsp:tagAttribute name="aria-labelledby" value="quantityRequested"/>
                                                        </dsp:input>
														<dsp:input type="hidden" bean="GiftRegistryFormHandler.viewBeans[${valueIndex}].regItemOldQty"
															paramvalue="regItem.qtyRequested" id="oldquantityd" />

														<a href='#' class="button plus secondary"><span></span></a>
												</div>	
										</div>
										<div class="productLastColumn small-12 medium-2 columns"> 
											<dsp:getvalueof var="regItemOldQty" param="regItem.qtyRequested" /> 
											<input type="hidden" name="regItemOldQty" value="${regItemOldQty}" class="frmAjaxSubmitData" />
											<dsp:input bean="GiftRegistryFormHandler.updateItemToGiftRegistry" 
																	id="update${idxPrice}${idxCat}${idx}" 	type="submit" style="display:none" value="Update"	/>
											<dsp:input bean="GiftRegistryFormHandler.removeItemFromGiftRegistry" 
																	id="remove${idxPrice}${idxCat}${idx}" 	type="submit" style="display:none"  value="Remove" />
													
											<c:set var="valueIndex" value="${valueIndex + 1}"/>				
											<div class="button_active button_active_orange">
												<a href="#" data-trigger-button="update${idxPrice}${idxCat}${idx}" class="validateQuantity tiny button service" >		
												<bbbl:label key='lbl_mng_regitem_update' language="${pageContext.request.locale.language}" /></a>			
											</div>
											<a href="#" data-submit-button="remove${idxPrice}${idxCat}${idx}" class="btnAjaxSubmitRegistry buttonTextLink tiny button secondary">
											<bbbl:label key='lbl_mng_regitem_remove' language="${pageContext.request.locale.language}" /></a>
											<div class="clear"></div>
											<dsp:getvalueof param="regItem.qtyRequested" var="requestedQuantity" />
											<dsp:getvalueof param="regItem.qtyPurchased" var="purchasedQuantity" />
											<!-- LTL-328 Hide add to cart button and show not availability message for LTL item -->
											<dsp:getvalueof var="ltlItemFlag" param="regItem.sKUDetailVO.ltlItem" />
											<c:choose>
												<c:when test="${(((isBelowLineItem eq 'false') || inStoreSku) && (not ltlItemFlag || enableLTLReg))}">
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
																		<div class="addToCart button_disabled">
																			<c:set var="lblAddTOCart"><bbbl:label key='lbl_mng_regitem_additemcart' language="${pageContext.request.locale.language}" /></c:set>
																			<input class="tiny button transactional" type="button" disabled="disabled" name="btnAddToCart" value="${lblAddTOCart}" role="button" aria-pressed="false" />	
																		</div>
																		<div style="color:red;font-weight:bold;">

																			<dsp:include page="/browse/frag/reasonCodeMessage.jsp" flush="true" >
																				<dsp:param name="reasonCode" value="${reasonCode}"/>
																			</dsp:include>
							    										</div> 
																	</c:when>
																	<c:otherwise>
																		<div class="addToCart">
																			<c:set var="lblAddTOCart"><bbbl:label key='lbl_mng_regitem_additemcart' language="${pageContext.request.locale.language}" /></c:set>
																			<input class="tiny button transactional" type="button" name="btnAddToCart" value="${lblAddTOCart}" role="button" aria-pressed="false" />	
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
											<c:set var="writeReviewOn" scope="request"><bbbc:config key="writeReviewOn" configName="FlagDrivenFunctions" /></c:set>
											<c:if test="${writeReviewOn}">
												<dsp:droplet name="/com/bbb/commerce/browse/droplet/ProductStatusDroplet">
													<dsp:param name="productId" value="${productId}" />
													<dsp:oparam name="output">
														<dsp:getvalueof var="isProductActive" param="isProductActive" />
													</dsp:oparam>
												</dsp:droplet>
												<c:if test="${isProductActive && purchasedQuantity>0}">
													<a  href="#" data-BVProductId="${productId}" class="buttonTextLink triggerBVsubmitReview">
														<bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" />
													</a>
												</c:if>
											</c:if>
										</div>
									</div>
									
									<div class="productFooter columns clearfix">
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
																			<c:set var="nextStyle">prodSecondaryAttribute</c:set>
																		</c:otherwise>
													            	</c:choose>
																</dsp:oparam>
															</dsp:droplet>
														</c:if>
													</dsp:oparam>
												</dsp:droplet>
												<%-- <c:if test="${skuDetailVO.shippingSurcharge gt 0.0}">
													<li class="${nextStyle}"><bbbl:label key="lbl_mng_surcharge" language="<c:out param='${language}'/>"/>${skuDetailVO.shippingSurcharge}</li>
												</c:if> --%>
											</ul>
							</div>
								</div>
									</div>
								</dsp:oparam>
							</dsp:droplet>
						</div>
					</c:if>
				</dsp:oparam>
			</dsp:droplet>
			<c:if test="${countIE eq sizeIE}">
				</dd>
			</c:if>
		</dsp:oparam>
	</dsp:droplet>
	</dl>
	</dsp:form>
</dsp:page>