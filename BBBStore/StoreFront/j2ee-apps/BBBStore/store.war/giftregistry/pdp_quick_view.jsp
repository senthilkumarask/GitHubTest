<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>
	<dsp:importbean bean="/atg/multisite/Site"/>   
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<c:set var="lblReviewsCount"><bbbl:label key="lbl_reviews_count" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblReviewCount"><bbbl:label key="lbl_review_count" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblForThe"><bbbl:label key="lbl_accessibility_for_the" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblAboutThe"><bbbl:label key="lbl_accessibility_about_the" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="writeReviewLink"><bbbl:label key='lbl_grid_write_review_link' language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="omniProp5" value="Product modal"/>
		<c:set var="productNotfound" value="false"/>
		<c:set var="productOOS" value="false"/>
	<c:set var="customizeCTACodes">
		<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
	</c:set>	
	<c:set var="shippingAttributesList">
		<bbbc:config key="shippingAttributesList" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="sddAttributeList">
		<bbbc:config key="sameDayDelAttributeKeyList" configName="SameDayDeliveryKeys" />
	</c:set>
	<c:set var="vdcAttributesList">
		<bbbc:config key="vdcAttributesList" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="AttributePDPTOP">
		<bbbl:label key='lbl_pdp_attributes_top' language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="AttributePDPPrice">
		<bbbl:label key='lbl_pdp_attributes_price' language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="scene7Path" scope="request">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
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
			<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_us"/></c:set>
		</c:when>
		<c:when test="${currentSiteId eq BuyBuyBabySite}">
			<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_baby"/></c:set>
		</c:when>
		<c:otherwise>
			<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_ca"/></c:set>
		</c:otherwise>
	</c:choose>
	<dsp:getvalueof var="personalisedCode" param="personalisedCode"/>
	<dsp:getvalueof var="customizedPrice" param="customizedPrice"/>
	<dsp:getvalueof var="customizationDetails" param="customizationDetails"/>
	<dsp:getvalueof var="color" param="color"/>
	<dsp:getvalueof var="size" param="size"/>
	<dsp:getvalueof var="skuID" param="skuId"/>
	<dsp:getvalueof var="personalizedImageUrls" param="personalizedImageUrls"/>
	<dsp:getvalueof var="personlisedPrice" param="personlisedPrice"/>
	<dsp:getvalueof var="personalizedImageUrlThumbs" param="personalizedImageUrlThumbs"/>
    <dsp:getvalueof var="refNum" param="refNum"/>
	<dsp:getvalueof var="disableItemDetails" param="disableItemDetails"/>
	<dsp:getvalueof var="personalizationType" param="personalizationType" />
	<dsp:getvalueof var="registryView" param="registryView" />
	<dsp:droplet name="/com/bbb/common/droplet/EximCustomizationDroplet">
	            <dsp:param name="personalizationOptions" param="personalisedCode" />
				<dsp:oparam name="output">
					 <dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
					 <dsp:getvalueof var="personalizationOptionsDisplay" param="personalizationOptionsDisplay" />
				</dsp:oparam>
	</dsp:droplet>
<dsp:droplet name="ProductDetailDroplet">
	<dsp:param name="id" param="productId" />
	<dsp:param name="siteId" value="${appid}"/>
	<dsp:param name="skuId" param="skuId"/>
	<dsp:param name="registryId" param="registryId" />
	<dsp:param name="isMainProduct" value="true" />
	<c:if test="${!registryView eq 'owner'}">
		<dsp:param name="showIncartPrice" value="true" />
	</c:if>
	<dsp:getvalueof var="collection" param="productVO.collection"/>
	<dsp:oparam name="output">
		<dsp:getvalueof var="skuVO" param="pSKUDetailVO" />
        <dsp:getvalueof var="skuinStock" param="inStock" />
        <dsp:getvalueof var="priceLabelCodeSKU" param="pSKUDetailVO.pricingLabelCode"/>
        <dsp:getvalueof var="inCartFlagSKU" param="pSKUDetailVO.inCartFlag"/>
        <dsp:getvalueof var="ltlItemFlag" param="pSKUDetailVO.ltlItem"/>
		<c:choose>
				<c:when test="${not empty skuVO}">
					<dsp:getvalueof var="attribs" param="pSKUDetailVO.skuAttributes"/>
				</c:when>
				<c:otherwise>
					<dsp:getvalueof var="attribs" param="productVO.attributesList"/>
				</c:otherwise>
		</c:choose>
		<c:choose>
				<c:when test="${not empty skuVO}">
					<dsp:getvalueof var="displayShipMsg" param="pSKUDetailVO.displayShipMsg"/>
				</c:when>
				<c:otherwise>
					<dsp:getvalueof var="displayShipMsg" param="productVO.displayShipMsg"/>
				</c:otherwise>
		</c:choose>
		<c:choose>
					<c:when test="${skuinStock eq null}">
						<c:set var="inStock" value="true" />
					</c:when>
					<c:otherwise>
						<dsp:getvalueof var="inStock" param="inStock" />
					</c:otherwise>
		</c:choose>
		<c:if test="${inStock==false}">
                    <c:set var="productOOS" value="true"/>
        </c:if>
		<dsp:getvalueof var="productName" param="productVO.name"/>
		<div title="<c:out value='${productName}'/>">
		    <div class="width_10 clearfix">
		    <c:if test="${not empty refNum}">
		    <div class= "personalizedPDPContainer">
			<h3 class= "savePersonalization">
			<c:choose>
				<c:when test="${not empty personalisedCode && fn:contains(customizeCTACodes, personalisedCode)}">
					<bbbl:label key='lbl_quick_view_item_ready_to_purchase_custom' language="${pageContext.request.locale.language}" />
				</c:when>
				<c:otherwise>
					<bbbl:label key='lbl_quick_view_item_ready_to_purchase' language="${pageContext.request.locale.language}" />
				</c:otherwise>
			</c:choose>
			</h3>
		    </div>
		    </c:if>
		        <div class="width_5 fl marRight_10">
		     
		            <dsp:getvalueof var="largeImage" param="productVO.productImages.largeImage"/>
                    <dsp:getvalueof var="smallImage" param="productVO.productImages.smallImage"/>
                    <dsp:getvalueof var="thumbnailImage" param="productVO.productImages.thumbnailImage" />
                    <dsp:getvalueof var="productName" param="productVO.name" scope="request"/>
                    <dsp:getvalueof var="refinedNameProduct" param="productVO.refinedName" scope="request"/>
                    <dsp:getvalueof var="zoomFlag" param="productVO.zoomAvailable"/>
		     
		            <c:choose>
					<c:when test="${not empty refNum}">
					<img id="mainProductImg" src="${personalizedImageUrls}" class="prodImage" height="380" width="380" alt="<c:out value='${productName}'/>" onerror="this.src = '${imagePath}/_assets/global/images/no_image_available.jpg';"/>
					</c:when>
					<c:otherwise>
					<img id="mainProductImg" data-original-url="${scene7Path}/${largeImage}" src="/_assets/global/images/blank.gif" class="loadingGIF mainProductImage noImageFound" height="380" width="380" alt="<c:out value='${productName}'/>" />
					</c:otherwise>
					</c:choose>
		            
		        </div>
					<div class="width_5 fl">

						<div id="prodAttribs" class="prodAttribs prodAttribWrapper">
						<c:set var="showShipCustomMsg" value="true"/>
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param name="array" value="${attribs}" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="placeHolderTop" param="key" />
									<c:if test="${(not empty placeHolderTop) && (placeHolderTop eq AttributePDPTOP)}">
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param param="element" name="array" />
											<dsp:param name="sortProperties" value="+priority" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="placeHolderTop" param="element.placeHolder" />
												<dsp:getvalueof var="attributeDescripTop" param="element.attributeDescrip" />
												<dsp:getvalueof var="imageURLTop" param="element.imageURL" />
												<dsp:getvalueof var="actionURLTop" param="element.actionURL" />
												<dsp:getvalueof var="attrId" param="element.attributeName" />
												<c:if test="${!fn:contains(sddAttributeList,attrId)}">
												<c:if test="${fn:contains(shippingAttributesList,attrId)}">
														<c:set var="showShipCustomMsg" value="false"/>
													</c:if>
												<div class="attribs">
													<c:choose>
														<c:when test="${null ne attributeDescripTop}">
															<c:choose>
																<c:when test="${null ne imageURLTop}">
																	<a href="${actionURLTop}" class="qvAttribPopup"><img src="${imageURLTop}" alt="<dsp:valueof param="element.attributeDescrip" valueishtml="true"/>" /></a>
																</c:when>
																<c:otherwise>
																	<c:choose>
																		<c:when test="${null ne actionURLTop}">
																			<a href="${actionURLTop}" class="qvAttribPopup"><span class="prod-attrib"><dsp:valueof param="element.attributeDescrip" valueishtml="true" /></span></a>
																		</c:when>
																		<c:otherwise>
																			<span class="prod-attrib"><dsp:valueof param="element.attributeDescrip" valueishtml="true" /></span>
																		</c:otherwise>
																	</c:choose>
																</c:otherwise>
															</c:choose>
														</c:when>
														<c:otherwise>
															<c:if test="${null ne imageURLTop}">
																<c:choose>
																	<c:when test="${null ne actionURLTop}">
																		<a href="${actionURLTop}" class="qvAttribPopup"><img src="${imageURLTop}" alt="" /></a>
																	</c:when>
																	<c:otherwise>
																		<img src="${imageURLTop}" alt="" />
																	</c:otherwise>
																</c:choose>
															</c:if>
														</c:otherwise>
													</c:choose>
												</div>
											  </c:if>
											</dsp:oparam>
										</dsp:droplet>
									</c:if>
								</dsp:oparam>
							</dsp:droplet>
						</div><%-- prodAttribs --%>


						<dsp:getvalueof var="productId" param="productId" />
						<dsp:getvalueof var="registryId" param="registryId" />
						<dsp:getvalueof var="sopt" param="sopt" />
						<dsp:getvalueof var="skuId" param="skuId" />
						<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
							<dsp:param name="id" value="${productId}" />
							<dsp:param name="itemDescriptorName" value="product" />
							<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="pdpURL" vartype="java.lang.String"	param="url" />
							</dsp:oparam>
						</dsp:droplet>
						<c:set var="pdpURL"	value="${pdpURL}?skuId=${skuId}&registryId=${registryId}&sopt=${sopt}" />
						<div class="clear"></div>
						<h2 id="productTitle" class="productDetails">
						<%--BBBSL-11683 | Adding refnum check to disable click of personalized items on guest view --%>
						<c:choose>
							<c:when test="${(ltlItemFlag || not empty refNum) && registryView eq 'guest'}">
								<dsp:valueof param="productVO.name" valueishtml="true" />
							</c:when>    
							<c:otherwise>
								<c:choose>
									<c:when test="${!(not empty refNum && disableItemDetails eq 'true')}">
										<a href="${contextPath}${pdpURL}"><dsp:valueof param="productVO.name" valueishtml="true" /></a>
									</c:when>
									<c:otherwise>
										<a href="javascript:void(0);" " oncontextmenu="return false;" class="cursorDefault"><dsp:valueof param="productVO.name" valueishtml="true" /></a></h2>
									</c:otherwise>
								</c:choose>	
							</c:otherwise>
						</c:choose>
						
						</h2>
						<div class="clear"></div>
						<div id="productDesc" class="bulletsReset productDesc"><dsp:valueof param="productVO.shortDescription"	valueishtml="true" /></div>
						<div class="clear"></div>

						<c:if test="${!(registryView eq 'guest' && ltlItemFlag)}">
						<c:if test="${BazaarVoiceOn}">
							<div id="prodRatings">
								<dsp:getvalueof var="ratingAvailable" param="productVO.bvReviews.ratingAvailable"></dsp:getvalueof>
								<dsp:getvalueof param="productVO.bvReviews.averageOverallRating" var="averageOverallRating"/>
								<dsp:getvalueof param="element.bvReviews.totalReviewCount" var="totalReviewCount"/>
								<dsp:getvalueof param="productVO.bvReviews.ratingsTitle" var="ratingsTitle"/>
								<c:choose>
									<c:when test="${ratingAvailable == true}">
										<dsp:getvalueof var="fltValue" param="productVO.bvReviews.averageOverallRating" />
										<dsp:getvalueof param="productVO.bvReviews.totalReviewCount" var="totalReviewCount" />
										<li class="clearfix metaFeedback"><span title="${fltValue}" class="ratingReg ratingTxt prodReview prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel">${ratingsTitle}</span></span>
										<span>
											
												<c:if test="${!(not empty refNum && disableItemDetails eq 'true')}">
													<a href="${contextPath}${pdpURL}&showRatings=true">
													<c:choose>
														<c:when test="${totalReviewCount == 1}">
															(<dsp:valueof param="productVO.bvReviews.totalReviewCount" /><bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" />)
														</c:when>
														<c:otherwise>
															(<dsp:valueof param="productVO.bvReviews.totalReviewCount" /><bbbl:label key='lbl_reviews_count' language="${pageContext.request.locale.language}" />)
														</c:otherwise>
													</c:choose>
													</a>
												</c:if>
											</span>
										</li>
									</c:when>
									<c:otherwise>
										<c:set var="writeReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
										<div class="prodReview clearfix">
										  <%--BBBSL-11683 | Adding refnum check to disable click of personalized items on guest view --%>
											<c:choose>
												<c:when test="${(not empty refNum && registryView eq 'guest')}">
													<bbbl:label key='lbl_grid_write_review_link' language="${pageContext.request.locale.language}" />
												</c:when>
												<c:otherwise>
													<c:if test="${!(not empty refNum && disableItemDetails eq 'true')}">
														<a href="${contextPath}${pdpURL}&writeReview=true">
															<bbbl:label key='lbl_grid_write_review_link' language="${pageContext.request.locale.language}" />
														</a>
													</c:if>
												</c:otherwise>
											</c:choose>
										</div>
									</c:otherwise>
								</c:choose>
							</div>
						</c:if>
						</c:if>
						<c:if test="${not empty refNum}">
							
		                <div class="personalizedPDPWrapper">
			            <div class="productContent clearfix">
				           <img src="${personalizedImageUrlThumbs}" class="personalizedImg" alt="<c:out value='${productName}'/>" height="100px" width="100px" onerror="this.src = '${imagePath}/_assets/global/images/no_image_available.jpg';"/> 
				           <ul class="prodInfo grid_3 padLeft_5">
				           <%--Displaying actual Sku Color and size --%>
					       <li class="personalisedProdDetails propColor">
						   <c:if test='${not empty color}'>
								<bbbl:label key="lbl_item_color" language="${pageContext.request.locale.language}" />
											:
                                 <dsp:valueof value="${color}" valueishtml="true" />
                           </c:if>
		
					</li>
					<li class="personalisedProdDetails propSize ">
						<c:if test='${not empty size}'>
						
											<bbbl:label key="lbl_item_size"
												language="${pageContext.request.locale.language}" />
											:
											<dsp:valueof value="${size}" valueishtml="true" />
						
									</c:if>
					</li>
					<li class="personalisedProdDetails">
						<span class="personalizationType">${eximCustomizationCodesMap[personalisedCode]}:</span> <span class="prodNameDrop">${customizationDetails}</span>
					</li>
					
					<div class="priceAdditionText">
						<span class="personalizationAttr">
						<%--BBBSL-8154 --%>
							<%-- <span class="eximIcon"  aria-hidden="true">${personalizationOptionsDisplay}</span> --%>
							</span>
						<c:choose>
						
														<c:when test="${personalizationType == 'PB'}">
															<bbbl:label key='lbl_PB_Fee_detail' language="${pageContext.request.locale.language}" />
															</c:when>
															<c:when test="${personalizationType == 'PY'}">
				      <span class="addedPrice">${customizedPrice}</span>
						<span class='addPriceText'><bbbl:label key="lbl_exim_added_price"
												language="${pageContext.request.locale.language}"/></span>
												</c:when>
												<c:when test="${personalizationType == 'CR'}">
				      <span class="addedPrice">${customizedPrice}</span>
						<span class='addPriceText'>
						<c:choose>
							<c:when test="${not empty personalisedCode && fn:contains(customizeCTACodes, personalisedCode)}">
								<bbbl:label key="lbl_exim_cr_added_price_customize" language="${pageContext.request.locale.language}"/>
							</c:when>
							<c:otherwise>
								<bbbl:label key="lbl_exim_cr_added_price" language="${pageContext.request.locale.language}"/>
							</c:otherwise>
						</c:choose>
						</span>
												</c:when>
														</c:choose>
						
					</div>
					
				</ul>
			</div>
		</div>
		
                     </c:if>
					<div class="clear"></div>
		        			

					<%-- Rebate display--%>
					<c:set var="rebateDivCreated" value="${false}" />
									
					<dsp:getvalueof var="hasRebate" param="pSKUDetailVO.hasRebate"/>
					<c:set var="rebatesOn" value="${false}" />
					<c:if test="${not empty hasRebate && hasRebate}">
						<dsp:getvalueof var="chkEligibleRebates" param="pSKUDetailVO.eligibleRebates"/>
						<c:if test="${(null != chkEligibleRebates) && (fn:length(chkEligibleRebates) == 1 )}"> 
							<c:set var="rebatesOn" value="${true}" />
						</c:if>
					</c:if>
					<%-- attribs droplet --%>
					<c:set var="showShipCustomMsg" value="true"/>
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param value="${attribs}" name="array" />
						<dsp:oparam name="output">
						<dsp:getvalueof var="placeHolderPrice" param="key"/>
							<c:if test="${(not empty placeHolderPrice) && (placeHolderPrice eq AttributePDPPrice)}">
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param param="element" name="array" />
									<dsp:param name="sortProperties" value="+priority"/>
									<dsp:oparam name="output">
										<dsp:getvalueof var="chkCount" param="count"/>
										<dsp:getvalueof var="chkSize" param="size"/>
										<c:set var="sep" value="seperator" />
										<c:if test="${chkCount == chkSize}">
											<c:choose>
												<c:when test="${rebatesOn}">
													<c:set var="sep" value="seperator" />
												</c:when>
												<c:otherwise>
													<c:set var="sep" value="" />
												</c:otherwise>
											</c:choose>
										</c:if>
										<dsp:getvalueof var="placeHolderPrice" param="element.placeHolder"/>
										<dsp:getvalueof var="attributeDescripPrice" param="element.attributeDescrip"/>
										<dsp:getvalueof var="imageURLPrice" param="element.imageURL"/>
										<dsp:getvalueof var="actionURLPrice" param="element.actionURL"/>
										<dsp:getvalueof var="attributeName" param="element.attributeName"/>
										<dsp:getvalueof var="attrId" param="element.attributeName" />
										<c:if test="${!fn:contains(sddAttributeList,attrId)}">
												<c:if test="${fn:contains(shippingAttributesList,attrId)}">
														<c:set var="showShipCustomMsg" value="false"/>
													</c:if>
										<c:choose>
											 <c:when test="${null ne attributeDescripPrice}">
                                                    <c:if test="${rebateDivCreated == false}">
                                                        <div id="rebateContainer" class="rebateContainer prodAttribWrapper">
                                                        <c:set var="rebateDivCreated" value="${true}" />
                                                    </c:if>
													<c:choose>
														   <c:when test="${null ne imageURLPrice}">
																 <div class="attribs ${sep}"><a href="${actionURLPrice}" class="qvAttribPopup"><img src="${imageURLPrice}" alt="<dsp:valueof param="element.attributeDescrip" valueishtml="true"/>"/></a></div>
														   </c:when>
														   <c:otherwise>
																 <c:choose>
																		 <c:when test="${null ne actionURLPrice}">
																			<c:choose>
																				<c:when test="${not empty skuID && null ne attributeName && fn:contains(attributeName,vdcAttributesList)}">
																					<div class="attribs ${sep}"><a href="/store/browse/dynamicStaticContent.jsp?pageName=ItemShippedDirectlyFromVendor&isVdcSku=true&skuId=${skuID}" class="qvAttribPopup"><span class="prod-attrib"><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a></div>																					
																				</c:when>
																				<c:otherwise>
																					<div class="attribs ${sep}"><a href="${actionURLPrice}?skuID=${skuID}" class="qvAttribPopup"><span class="prod-attrib"><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a></div>
																				</c:otherwise>																					
																			</c:choose>
																		 </c:when>
																		 <c:otherwise>
																			   <div class="attribs ${sep}"><span class="prod-attrib"><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></div>
																		 </c:otherwise>
																  </c:choose>
														   </c:otherwise>
													 </c:choose>
											 </c:when>
											 <c:otherwise>
												   <c:if test="${null ne imageURLPrice}">
                                                           <c:if test="${rebateDivCreated == false}">
                                                               <div id="rebateContainer" class="rebateContainer prodAttribWrapper">
                                                               <c:set var="rebateDivCreated" value="${true}" />
                                                           </c:if>
														   <c:choose>
																  <c:when test="${null ne actionURLPrice}">
																		<div class="attribs ${sep}"><a href="${actionURLPrice}" class="qvAttribPopup"><img src="${imageURLPrice}" alt=""/></a></div>
																  </c:when>
																  <c:otherwise>
																		<div class="attribs ${sep}"><img src="${imageURLPrice}" alt="" /></div>
																  </c:otherwise>
														   </c:choose>
													</c:if>
											 </c:otherwise>
										</c:choose>
										</c:if>
										</dsp:oparam>
								</dsp:droplet>
							</c:if>
						</dsp:oparam>
					</dsp:droplet>
					<%-- attribs droplet --%>			

					<c:if test="${not empty hasRebate && hasRebate}">
						<dsp:getvalueof var="eligibleRebates" param="pSKUDetailVO.eligibleRebates"/>
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" value="${eligibleRebates}"/>
							<dsp:oparam name="output">
                                <c:if test="${rebateDivCreated == false}">
                                    <div id="rebateContainer" class="rebateContainer prodAttribWrapper">
                                    <c:set var="rebateDivCreated" value="${true}" />
                                </c:if>
								<dsp:getvalueof var="chkCount1" param="count"/>
								<dsp:getvalueof var="chkSize1" param="size"/>
								<c:set var="sep1" value="seperator" />
								<c:if test="${chkCount1 == chkSize1}">
									<c:set var="sep1" value="" />
								</c:if>
								<dsp:getvalueof var="rebate" param="element"/>						
								<div class="attribs ${sep1}" ><a href="${rebate.rebateURL}" class="links" target="_blank" title="Rebate"><c:out value="${rebate.rebateDescription}" escapeXml="false"/></a></div>
							</dsp:oparam>
						</dsp:droplet>
					</c:if>
					
					<c:if test="${rebateDivCreated == true}">
                             	<div class="clear"></div>
                             </div> <%-- close rebate div --%>
                     </c:if>

		        	<%-- End rebate --%>
		        	
			        <div class="clear"></div>
		            <div class="priceBtnWrap clearfix">
		                <div id="prodPrice" class="width_3 fl">

			                    
				                    <c:choose>
									<c:when test="${not empty personalisedCode && not empty refNum}">
										<c:if test="${inCartFlagSKU && registryView eq 'guest'}">
											<div class="disPriceString"> 
										    	 <bbbl:label key="lbl_discounted_incart_text" language="${pageContext.request.locale.language}" /> 
										    </div>
										</c:if>
										<div class="prodPrice">														
                                                   ${personlisedPrice}	
										</div>
										<span class="staticTextOnRLP">* Price subject to change.</span>
										
									</c:when>
									<c:otherwise>
									<div class="prodPrice">
				                    <dsp:include page="/browse/product_details_price.jsp">	
										<dsp:param name="product" param="productId"/>
										<dsp:param name="sku" param="skuId"/>
										<dsp:param name="priceLabelCodeSKU" value="${priceLabelCodeSKU}" />
								        <dsp:param name="inCartFlagSKU" value="${inCartFlagSKU}" />
								        <c:if test="${!registryView eq 'owner'}">
								        <dsp:param name="showInCartPrice" value="true" />
								        </c:if>
									</dsp:include>
									 </div>
									</c:otherwise>
									</c:choose>							
			                   
		                </div>
		                <%--BBBSL-11683 | Adding refnum check to disable click of personalized items on guest view --%>
		                <c:if test="${not ((ltlItemFlag || not empty refNum) && registryView eq 'guest')}">
							<c:if test="${!(not empty refNum && disableItemDetails eq 'true')}">
		                		<div id="btnViewDetails" class="width_3 fr marLeft_10">
		                   			 <div class="button button_active button_active_orange">
		                       			 <a  aria-labelledby="viewMsg productTitle" href="${contextPath}${pdpURL}"><bbbl:label key="lbl_pdp_view_item_detail" language="${pageContext.request.locale.language}" /></a>
		                    				<span class="txtOffScreen" id="viewMsg">View more details about the</span>
									</div>
		                		</div>
							 </c:if>
		                </c:if>
		                <div class="clear"></div>
		            </div>
		            <div class="freeShipBadge">
						<c:if test="${showShipCustomMsg}">
							${displayShipMsg}
						</c:if>
						</div>
		            <div class="clear"></div>
	            
		        </div>
		        <div class="clear"></div>
		    </div>
		</div>		
		
	</dsp:oparam>
	<dsp:oparam name="error">
					<c:set var="productNotfound" value="true"/>
	</dsp:oparam>
</dsp:droplet>

	<%-- R2.2 Omniture Implementation Start --%>
	<c:choose>	
		<c:when test="${(skuVO.customizableRequired || skuVO.customizationOffered) && not empty skuVO.customizableCodes}">
			<script type="text/javascript">
                var productNotfound = ${productNotfound},
                productOOS = ${productOOS},
                collectionPDP = ${collection};
                var BBB = BBB || {};
                var omni_channel = '${fn:replace(fn:replace(channel,'\'',''),'"','')}';
                var omni_refinedNameProduct = '${fn:replace(fn:replace(refinedNameProduct,'\'',''),'"','')}';
                var omni_prop2Var = '${fn:replace(fn:replace(prop2Var,'\'',''),'"','')}';
                var omni_prop3Var = '${fn:replace(fn:replace(prop3Var,'\'',''),'"','')}';
                if (typeof s !== "undefined") {
                    BBB.omnitureObj.backup_s("eVar1,eVar2,eVar3,eVar4,eVar5,eVar6,eVar7,eVar8,eVar47,productNum,prop7,prop8,prop25");
                    BBB.omnitureObj.clear_s("eVar3");
                    BBB.omnitureObj.delete_s("eVar1,eVar2,eVar4,eVar5,eVar6,eVar7,eVar8,eVar46,eVar47,productNum,prop7,prop8,prop25");
                    s.prop1="Product Details Page";
					s.prop12="Product Quick View";
                    if(productNotfound){
                        s.pageName="product not available";
                        s.channel="Error page";
                        s.products="${omni_prod}".replace(/,(?=[^,]*$)/, '');
                        s.prop2="Error page";
                        s.prop3="Error page";
                        s.prop4="";
                        s.prop5="";
                        s.events="event57";
                    } else {
                        s.pageName="Product Detail > " + omni_refinedNameProduct;
                        s.channel=omni_channel;
                        s.products="${omni_prod}".replace(/,(?=[^,]*$)/, '');
                        s.prop2=omni_prop2Var;
                        s.prop3=omni_prop3Var;
                        s.prop5="${omniProp5}";
                        if (productOOS && !collectionPDP) {                       	
                            		s.events="event57,event17,event81";                              
                        } else {                        	
                        		s.events="event57,event81";
                        }
                    }
                    fixOmniSpacing();
                    s.t();
                    BBB.omnitureObj.restore_s("eVar1,eVar2,eVar3,eVar4,eVar5,eVar6,eVar7,eVar8,eVar47,productNum,prop7,prop8,prop25");
                }
			</script>
		</c:when>
		<c:otherwise>
				<script type="text/javascript">
                var productNotfound = ${productNotfound},
                productOOS = ${productOOS},
                collectionPDP = ${collection};
                var BBB = BBB || {};
                var omni_channel = '${fn:replace(fn:replace(channel,'\'',''),'"','')}';
                var omni_refinedNameProduct = '${fn:replace(fn:replace(refinedNameProduct,'\'',''),'"','')}';
                var omni_prop2Var = '${fn:replace(fn:replace(prop2Var,'\'',''),'"','')}';
                var omni_prop3Var = '${fn:replace(fn:replace(prop3Var,'\'',''),'"','')}';
			
                if (typeof s !== "undefined") {
                    BBB.omnitureObj.backup_s("eVar1,eVar2,eVar3,eVar4,eVar5,eVar6,eVar7,eVar8,eVar47,productNum,prop7,prop8,prop25");
                    BBB.omnitureObj.clear_s("eVar3");
                    BBB.omnitureObj.delete_s("eVar1,eVar2,eVar4,eVar5,eVar6,eVar7,eVar8,eVar46,eVar47,productNum,prop7,prop8,prop25");
                    s.prop1="Product Details Page";
					s.prop12="Product Quick View";
                    if(productNotfound){
                        s.pageName="product not available";
                        s.channel="Error page";
                        s.products="${omni_prod}".replace(/,(?=[^,]*$)/, '');
                        s.prop2="Error page";
                        s.prop3="Error page";
                        s.prop4="";
                        s.prop5="";
                        s.events="event57";
                    } else {
                        s.pageName="Product Detail > " + omni_refinedNameProduct;
                        s.channel=omni_channel;
                        s.products="${omni_prod}".replace(/,(?=[^,]*$)/, '');
                        s.prop2=omni_prop2Var;
                        s.prop3=omni_prop3Var;
                        s.prop5="${omniProp5}";
                        if (productOOS && !collectionPDP) {                      	
                            		s.events="event57,event17";                              
                        } else {                        	
                        		s.events="event57";
                        }
                    }
                    fixOmniSpacing();
                    s.t();
                    BBB.omnitureObj.restore_s("eVar1,eVar2,eVar3,eVar4,eVar5,eVar6,eVar7,eVar8,eVar47,productNum,prop7,prop8,prop25");
                }
			</script>
		</c:otherwise>
	</c:choose>
			<%-- R2.2 Omniture Implementation End --%>
</dsp:page>
