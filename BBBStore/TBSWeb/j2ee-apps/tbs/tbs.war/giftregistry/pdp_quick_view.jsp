<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>
	<dsp:importbean bean="/atg/multisite/Site"/>   
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	
	<c:set var="AttributePDPTOP">
		<bbbl:label key='lbl_pdp_attributes_top' language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="AttributePDPPrice">
		<bbbl:label key='lbl_pdp_attributes_price' language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="scene7Path" scope="request">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
	<c:set var="TBS_BedBathUSSite" scope="request">
		<bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BuyBuyBabySite" scope="request">
		<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BedBathCanadaSite" scope="request">
		<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="customizeCTACodes">
		<bbbc:config key="CustomizeCTACodes" configName="EXIMKeys"/>
	</c:set>
	<c:choose>
		<c:when test="${currentSiteId eq TBS_BedBathUSSite}">
			<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_us"/></c:set>
		</c:when>
		<c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
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
	<dsp:getvalueof var="personalizationType" param="personalizationType" />
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
	<dsp:param name="isDefaultSku" value="true"/>
	<dsp:oparam name="output">
		<dsp:getvalueof var="skuVO" param="pSKUDetailVO" />
		<dsp:getvalueof var="ltlItemFlag" param="pSKUDetailVO.ltlItem"/>
		<dsp:getvalueof var="skuInCartFlag" param="pSKUDetailVO.inCartFlag" />
		<c:choose>
			<c:when test="${not empty personalisedCode && fn:contains(customizeCTACodes, personalisedCode)}">
				<c:set var="customizeTxt" value="true"/>
			</c:when>
			<c:otherwise>
				<c:set var="customizeTxt" value="false"/>
			</c:otherwise>
		</c:choose>
		<c:choose>
				<c:when test="${not empty skuVO}">
					<dsp:getvalueof var="attribs" param="pSKUDetailVO.skuAttributes"/>
				</c:when>
				<c:otherwise>
					<dsp:getvalueof var="attribs" param="productVO.attributesList"/>
				</c:otherwise>
		</c:choose>
		<dsp:getvalueof var="productName" param="productVO.name"/>
		<br/>
		<br/>
		<div title="${productName}" class="row <c:if test="${not empty refNum}">eximItem</c:if> " >
			<c:if test="${not empty refNum}">
		    	<div class= "personalizedPDPContainer">
					<h3 class= "savePersonalization">
					<c:choose>
				 		<c:when test="${customizeTxt eq true}">
				 			<bbbl:label key='lbl_tbs_quick_view_item_ready_to_purchase_custom' language="${pageContext.request.locale.language}" /></h3>
				 		</c:when>
				 		<c:otherwise>
				 			<bbbl:label key='lbl_tbs_quick_view_item_ready_to_purchase' language="${pageContext.request.locale.language}" /></h3>
				 		</c:otherwise>
				 	</c:choose>
		    	</div>
		    </c:if>
		    <div class="width_10 clearfix row" itemscope="" itemtype="http://schema.org/Product">
		        <div class="small-12 medium-6 columns">
		     
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
						 	<img id="mainProductImg" data-original-url="${scene7Path}/${largeImage}" src="${scene7Path}/${largeImage}" class="loadingGIF mainProductImage noImageFound" height="380" width="380" alt="${productName}" />
						</c:otherwise>
					</c:choose>		     
		            
		            
		        </div>
		        <div class="small-12 medium-6 columns product-info productDetails quickView">
		        
					<div id="prodAttribs" class="prodAttribs prodAttribWrapper">
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param name="array" value="${attribs}"/>
						<dsp:oparam name="output">
						<dsp:getvalueof var="placeHolderTop" param="key"/>
							<c:if test="${(not empty placeHolderTop) && (placeHolderTop eq AttributePDPTOP)}">
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param param="element" name="array" />
									<dsp:param name="sortProperties" value="+priority"/>
										<dsp:oparam name="output">
										<dsp:getvalueof var="placeHolderTop" param="element.placeHolder"/>
										<dsp:getvalueof var="attributeDescripTop" param="element.attributeDescrip"/>
										<dsp:getvalueof var="imageURLTop" param="element.imageURL"/>
										<dsp:getvalueof var="actionURLTop" param="element.actionURL"/>
										<div class="attribs">
											<c:choose>
												 <c:when test="${null ne attributeDescripTop}">
													<c:choose>
														   <c:when test="${null ne imageURLTop}">
																 <a href="${actionURLTop}" class="qvAttribPopup"><img src="${imageURLTop}" alt="<dsp:valueof param="element.attributeDescrip" valueishtml="true"/>"/></a>
														   </c:when>
														   <c:otherwise>
																 <c:choose>
																		 <c:when test="${null ne actionURLTop}">
																			   <a href="${actionURLTop}" class="qvAttribPopup"><span class="prod-attrib"><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a>
																		 </c:when>
																		 <c:otherwise>
																			   <span class="prod-attrib"><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span>
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
										</dsp:oparam>
								</dsp:droplet>
							</c:if>
							</dsp:oparam>
						</dsp:droplet>
					</div> <%-- prodAttribs --%>        			
		        			
		        			
					<dsp:getvalueof var="productId" param="productId"/>
					<dsp:getvalueof var="registryId" param="registryId"/>
					<dsp:getvalueof var="skuId" param="skuId"/>
					<dsp:getvalueof var="registryView" param="registryView" />
					<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
						<dsp:param name="id" value="${productId}"/>
						<dsp:param name="itemDescriptorName" value="product" />
						<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="pdpURL" vartype="java.lang.String" param="url" />
						</dsp:oparam>
					</dsp:droplet>
					<c:set var="pdpURL" value="${pdpURL}?skuId=${skuId}&registryId=${registryId}" />
					<div class="clear"></div>
					<div id="productTitle" class="productDetails small-12 columns product-title">
					<%--<c:if test="${not empty refNum}">
						<div class="personalizationAttr">
							<span class="eximIcon"  aria-hidden="true">${personalizationOptionsDisplay}</span>
							<strong class="eximStaticText">Personalization Available</strong>
						</div>
					</c:if> --%>
					 <%--BBBSL-11683 | Adding refnum check to disable click of personalized items on guest view --%>
					<c:choose>
						<c:when test="${(ltlItemFlag || not empty refNum) && registryView eq 'guest'}">
							<h2><dsp:valueof param="productVO.name" valueishtml="true"/></h2>
						</c:when>
						<c:otherwise>
							<a href="${contextPath}${pdpURL}"><h2><dsp:valueof param="productVO.name" valueishtml="true"/></h2></a>
						</c:otherwise>
					</c:choose>
					</div>
					<div class="clear"></div>
					<div class="small-12 columns collection-link">
					<div id="productDesc" class="productDesc small-12 "><dsp:valueof param="productVO.shortDescription" valueishtml="true"/></div>
					</div>
					<div class="clear"></div>

					<c:if test="${!(registryView eq 'guest' && ltlItemFlag)}">					
						<c:if test="${BazaarVoiceOn}">
							<div id="prodRatings" class="small-12 columns product-reviews">
							<dsp:getvalueof var="ratingAvailable" param="productVO.bvReviews.ratingAvailable"></dsp:getvalueof>
							<c:choose>
								<c:when test="${ratingAvailable == true}">
									<dsp:getvalueof var="fltValue" param="productVO.bvReviews.averageOverallRating"/>
									<dsp:getvalueof param="productVO.bvReviews.totalReviewCount" var="totalReviewCount"/>
									<div class="prodReview clearfix ratingsReviews prodReviews41 prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="productVO.bvReviews.averageOverallRating"/>">
										<a href="${contextPath}${pdpURL}&showRatings=true">
											<c:choose>
												<c:when test="${totalReviewCount == 1}">
													(<dsp:valueof param="productVO.bvReviews.totalReviewCount"/><bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" />)
												</c:when>
												<c:otherwise>
													(<dsp:valueof param="productVO.bvReviews.totalReviewCount"/><bbbl:label key='lbl_reviews_count' language="${pageContext.request.locale.language}" />)
												</c:otherwise>
											</c:choose>
										</a>
									</div>
								</c:when>
								<c:otherwise>
									<c:set var="writeReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
									<div class="prodReviews ratingsReviews clearfix prodReviews41">
									 <%--BBBSL-11683 | Adding refnum check to disable click of personalized items on guest view --%>
										  <c:choose>
											<c:when test="${(registryView eq 'guest' && not empty refNum)}">
													<bbbl:label key='lbl_grid_write_review_link' language="${pageContext.request.locale.language}" />
											</c:when>
											<c:otherwise>
												<a href="${contextPath}${pdpURL}&writeReview=true">
													<bbbl:label key='lbl_grid_write_review_link' language="${pageContext.request.locale.language}" />
												</a>
											</c:otherwise>
										 </c:choose>
									</div>
								</c:otherwise>
							</c:choose>
							</div>
						</c:if>	
					</c:if>
						
						
					<c:if test="${not empty refNum}">
							
		                <div class="personalizedPDPWrapper clearfix">
		                <div class="productContent small-4 large-3 left">
	                	    <img src="${personalizedImageUrlThumbs}" class="personalizedImg" alt="<c:out value='${productName}'/>" height="100px" width="100px" onerror="this.src = '${imagePath}/_assets/global/images/no_image_available.jpg';"/> 
		                </div>
			            <div class="productContent clearfix left small-8 large-9">				       
				           <ul class="prodInfo">
				           
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
																			   <div class="attribs ${sep}"><a href="${actionURLPrice}" class="qvAttribPopup"><span class="prod-attrib"><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a></div>
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
		            <div class="priceBtnWrap clearfix ">
		                <div id="prodPrice" class="product-info small-12 large-5 columns price prodPrice no-padding-right">
		                    <div class="prodPrice">
		                    <c:choose>
								<c:when test="${not empty personalisedCode && not empty refNum}">
									<div class="prodPrice">														
                                                  ${personlisedPrice}	
									</div>
								</c:when>
								<c:otherwise>
				                    <dsp:include page="/browse/product_details_price.jsp">	
										<dsp:param name="product" param="productId"/>
										<dsp:param name="sku" param="skuId"/>
										<dsp:param name="inCartFlag" value="${skuInCartFlag}"/>
									</dsp:include>
								</c:otherwise>
							</c:choose>
		                    </div>
		                </div>
		                 <%--BBBSL-11683 | Adding refnum check to disable click of personalized items on guest view --%>
		                <c:if test="${not ((ltlItemFlag ||not empty refNum)  && registryView eq 'guest')}">
			                <div id="btnViewDetails" class="width_3 fr  small-12 large-7 columns">
			                    <div class="button_active viewPicksButton">
			                        <a class="button tiny" href="${contextPath}${pdpURL}"><bbbl:label key="lbl_pdp_view_item_detail" language="${pageContext.request.locale.language}" /></a>
			                    </div>
			                </div>
		                </c:if>
		                <div class="clear"></div>
		            </div>
		            <div class="clear"></div>
	            
		        </div>
		        <div class="clear"></div>
		    </div>
		</div>		
		
	</dsp:oparam>
</dsp:droplet>
</dsp:page>
