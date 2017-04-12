<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>
<dsp:importbean bean="/com/bbb/kickstarters/droplet/KickStarterDetailsDroplet" />
<dsp:importbean bean="/com/bbb/kickstarters/droplet/TopSkusDroplet" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
<dsp:getvalueof var="appid" bean="Site.id" />
<dsp:getvalueof var="topSkus" param="topSkus" />
<dsp:getvalueof var="sizeValue" param="sizeValue" />
<dsp:getvalueof var="heading1" param="heading1" />
<dsp:getvalueof var="kickStarterType" param="kickStarterType" />
<dsp:getvalueof var="registryType" param="registryType" />
<dsp:getvalueof var="registryId" param="registryId"/>

<c:set var="scene7Path"><bbbc:config key="scene7_url" configName="ThirdPartyURLs" /></c:set>

<dsp:droplet name="ForEach">									
	<dsp:param name="array" param="topSkus" />                                            
	<dsp:param name="elementName" value="topSku" />
	<dsp:oparam name="output">
      	<dsp:getvalueof var="topSku" param="topSku" />
        <dsp:getvalueof var="recommanded_qty" param="topSku.recommanded_qty" />
        <dsp:getvalueof var="pickListDescription" param="pickListItem.pickListDescription"/>
        <dsp:getvalueof var="totalSize"  param="size"/>
		<dsp:getvalueof var="currentCount" param="count"/>
		<dsp:getvalueof var="currentIndex" param="index"/>
		
		<%-- 4 items per row, close the list and start a new one --%>     
   		<c:if test="${currentCount%4 == 1}"> 			
            <ul class="clearfix collectionList small-block-grid-1 medium-block-grid-2 large-block-grid-4 collectionGridRow listGridToggle">
        </c:if>

		
		<dsp:droplet name="TopSkusDroplet">
			<dsp:param name="topSku" value="${topSku}" />
			<dsp:param name="skuId" value="${topSku.skuId}" />
			<dsp:getvalueof var="comment" param="topSku.comment" />										
			<dsp:param name="siteId" value="${currentSiteId}" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="skuId" param="SKUDetailsVO.skuId" />
				<dsp:getvalueof var="displayName" param="SKUDetailsVO.displayName" />
				<dsp:getvalueof var="description" param="SKUDetailsVO.description" />
				<dsp:getvalueof var="longDescription"param="SKUDetailsVO.longDescription" />
				<dsp:getvalueof var="skuAttributes"	param="SKUDetailsVO.skuAttributes" />
				<dsp:getvalueof var="oos" param="inStock" />
				<dsp:getvalueof var="image"	param="SKUDetailsVO.skuImages.largeImage" />
				<dsp:getvalueof var="smallImage" param="SKUDetailsVO.skuImages.smallImage" />
				<dsp:getvalueof var="thumbnailImage" param="SKUDetailsVO.skuImages.thumbnailImage" />
				<dsp:getvalueof var="emailOn" param="SKUDetailsVO.emailStockAlertsEnabled" />
				<dsp:getvalueof var="zoomAvailable"	param="SKUDetailsVO.zoomAvailable" />
				<dsp:getvalueof var="productId" param="productId" />											
				<dsp:getvalueof var="parentProdId" param="SKUDetailsVO.parentProdId" />
				<c:set var="price">
					<dsp:include page="/browse/product_details_price.jsp">	
						<dsp:param name="product" param="productId" />
						<dsp:param name="sku" param="SKUDetailsVO.skuId" />
						<dsp:param name="inCartFlag" param="TopSKUDetailVO.inCartFlag" />
					</dsp:include>
				</c:set>
				
				<%--Adding sku html --%>
				<c:choose>
					<c:when test="${currentCount % 4 == 0}">
						<li class="grid_4  registryDataItemsWrap collectionItems last clearfix">
					</c:when>
					<c:otherwise>
						<li class="grid_4  registryDataItemsWrap collectionItems clearfix">
					</c:otherwise>
				</c:choose>
				
				<%-- should we be getting final url this way?? --%>
				<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
					<dsp:param name="id" value="${productId}" />
					<dsp:param name="itemDescriptorName" value="product" />
					<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
							param="url" />								
					</dsp:oparam>
				</dsp:droplet>
                        <fieldset class="listDataItemsWrap" itemscope="" itemtype="http://schema.org/Product">
                        
                        	<div class="small-12 columns no-padding prodImageContainer">
                            	<c:choose>
                            		<c:when test="${not empty kickStarterType && kickStarterType eq 'Shop This Look'}">
                            			<dsp:a page="${finalUrl}" title="${displayName}" iclass="prodImg fl" onclick="javascript:s_crossSell('top consultant page- ${heading1}')">
		                            		<img class="fl noImageFound" src="${scene7Path}/${image}" width="229" height="229" alt="${displayName}" />
		                           		</dsp:a>
                            		</c:when>
                            		<c:otherwise>
                            			<dsp:a page="${finalUrl}" title="${displayName}" iclass="prodImg fl" onclick="javascript:s_crossSell('top consultant page')">
		                            		<img class="fl noImageFound" src="${scene7Path}/${image}" width="229" height="229" alt="${displayName}" />
		                           		</dsp:a>
                            		</c:otherwise>
                            	</c:choose>
                            	
								<c:if test="${not empty comment}">
									<dsp:a page="${finalUrl}" title="${displayName}" iclass="" >
										<div class="whyThisContent">
											<h6 class="whyThisItemHeader"><span>why this?</span></h6>
	                           				<p  class="whyThisItemText" style="display:none;">&ldquo;${comment}&rdquo;</p>
	                           			</div>
	                           		</dsp:a>
								</c:if>
							</div>

                            <div style="clear:both;"></div>

							<div class="small-12 columns no-padding">		                                
                                <ul class="prodInfoGrid clearfix prodInfo" >
                                    <li class="prodNameGrid prodName">
                                        <div >
                                            <dsp:a page="${finalUrl}" title="${displayName}" >${displayName}</dsp:a>
                                        </div>
                                    </li>

                                    
                           	        <dsp:droplet name="ProductDetailDroplet">
										<dsp:param name="id" param="productId"/>
										<dsp:param name="siteId" value="${appid}"/>
										<dsp:param name="skuId" param="SKUDetailsVO.skuId"/>
										<dsp:param name="registryId" value="${registryId}" />
										<dsp:param name="isDefaultSku" value="true"/>
										<dsp:oparam name="output">
											<dsp:getvalueof var="ratingAvailable" param="productVO.bvReviews.ratingAvailable"></dsp:getvalueof>
											<dsp:getvalueof var="ratings" param="productVO.bvReviews.averageOverallRating" vartype="java.lang.Integer"/>
											<dsp:getvalueof var="rating" value="${ratings * 10}" vartype="java.lang.Integer"/>
											<dsp:getvalueof param="productVO.bvReviews.totalReviewCount" var="totalReviewCount"/>
											<dsp:getvalueof var="fltValue" param="productVO.bvReviews.averageOverallRating"/>
											<c:choose>
												<c:when test="${ratings ne null && ratings ne '0' && (totalReviewCount eq '1' || totalReviewCount gt '1') }">
													<c:choose>
														<c:when test="${totalReviewCount == 1}">
															<li class="prodReviews ratingsReviews clearfix prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="productVO.bvReviews.averageOverallRating"/>">																
																<span class="prodReviewSpanFont"><dsp:a page="${finalUrl}?showRatings=true">
																	${totalReviewCount} <bbbl:label key="lbl_review_count" language="<c:out param='${language}'/>"/>
																</dsp:a></span>
															</li>
														</c:when>
														<c:otherwise>
															<li class="prodReviews ratingsReviews clearfix prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="productVO.bvReviews.averageOverallRating"/>">																
																<span class="prodReviewSpanFont"><dsp:a page="${finalUrl}?showRatings=true">
																	${totalReviewCount}<bbbl:label key="lbl_reviews_count" language="<c:out param='${language}'/>"/>
																</dsp:a></span>
															</li>
														</c:otherwise>
													</c:choose>
												</c:when>
												<c:otherwise>
													<li class="prodReviews ratingsReviews clearfix prodReviews<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>" title="<dsp:valueof param="productVO.bvReviews.averageOverallRating"/>">																
														<dsp:a page="${finalUrl}?showRatings=true">
															${totalReviewCount}<bbbl:label key="lbl_reviews_count" language="<c:out param='${language}'/>"/>
														</dsp:a>
													</li>
												</c:otherwise>
											</c:choose>
										</dsp:oparam>
									</dsp:droplet>
                                    
                                    <li>
                                        <input type="hidden" class="isInStock" value="${oos}" />

                                        <div class="priceQuantityNotAvailable hidden">
                                            <div class="gridMessage">
                                                <input type="hidden" name="oosProdId" value="${productId}" /> <input type="hidden" value="" name="oosSKUId" class="_oosSKUId" />

                                                <div class="error">
                                                    <strong><bbbl:label key="err_top_consultants_out_of_stock" language="${pageContext.request.locale.language}" /></strong>
                                                </div>

                                                <div class="info hidden">
                                                    <a class="info lnkNotifyOOS" href="#"><bbbl:label key="err_top_consultants_notify" language="${pageContext.request.locale.language}" /></a>
                                                </div>
                                            </div>
                                        </div>
                                    </li>              
                                </ul>
                            </div>
                            <div class="prodPrice fl price" itemprop="price"> ${price} </div>
                            <div class="small-12 columns no-padding">
                            	 <div class="fr prodInfoContainer alpha omega">		   
                                    
                                    	<c:choose>				        
       										<c:when test="${isTransient == 'false' && sizeValue>=1}">		                                                                              
		                                    	<div class="prodQty fl quantity">
		                                        	<div class="qty-spinner clearfix">
		                                                 <a href='#' class="button minus secondary"><span></span></a> 
		                                                 <input name="qty" id="pqty${productId}" title="Enter Quantity" class="fl pqty qty addItemToRegis _qty itemQuantity quantity-input addItemToList escapeHTMLTag resetInitialQTY" type="text" value="${recommanded_qty}" data-initialqty="${recommanded_qty}" data-change-store-submit="qty" data-change-store-errors="required digits nonZero"  role="textbox" aria-required="true" aria-describedby="pqty${skuId}" maxlength="2" /> 
		                                                <a href='#' class="button plus secondary"><span></span></a>
		                                            </div>
		                                             
		                                            <input type="hidden" name="prodId" class="_prodId addItemToRegis productId addItemToList" value="${productId}" data-change-store-submit="prodId" data-change-store-errors="required" data-change-store-internaldata="true" /> 
		                                            <input type="hidden" name="skuId" value="${skuId}" class="addItemToRegis _skuId addItemToList changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" data-change-store-internaldata="true" /> 
		                                            <input type="hidden" name="price" value="${omniPrice}" class="addItemToList addItemToRegis" />
		                                            <input type="hidden" name="heading1" value="${heading1}" class="addItemToRegis _heading1 addItemToList heading1" data-change-store-submit="heading1"/>
													<input type="hidden" name="parentProdId" value="${parentProdId}" class="addItemToRegis addItemToList parentProdId" data-change-store-submit="parentProdId"/>
													<input type="hidden" name="registryType" value="${registryType}" class="addItemToRegis addItemToList registryType" data-change-store-submit="registryType"/>
													<input type="hidden" name="registryId" value="${registryId}" class="addItemToRegis addItemToList registryId" data-change-store-submit="registryId"/>
													
		                                         </div>
		                                         <br/>
		                                         <c:if test="${isTransient == 'false'}">
													<div class="fr btnAddToRegistryWrapper">
	                                                	<div class="button_active">
	                                                 		<c:set var="btnAddToRegistry"><bbbl:label key="lbl_add_to_registry__button" language="${pageContext.request.locale.language}" /></c:set>
	                                                     	<input type="button" name="btnAddToRegistry" class="btnAddToRegistry button tiny transactional" value="${btnAddToRegistry}" onclick="" role="button" aria-pressed="false" />
	                                                 	</div>
	                                             	</div>
													</c:if>
                                  			</c:when>
                                  			<c:otherwise>
                                  				<div class="buttons consultantRecommends grid_3 alpha omega">
                                  				<c:choose>
											        <c:when test="${not empty kickStarterType && kickStarterType eq 'Shop This Look'}">
											            <bbbl:label key="lbl_kickstarters_consultant_recommends_baby" language="${pageContext.request.locale.language}" />
											        </c:when>											        
											        <c:otherwise>
                                  					<bbbl:label key="lbl_kickstarters_consultant_recommends" language="${pageContext.request.locale.language}" />
											        </c:otherwise>
											    </c:choose>
                                  					
                                  					<span class="recommendedQty">${recommanded_qty}</span>
                                  				</div>
                                  			</c:otherwise>
                               			</c:choose>		
                                        <div></div>                       
                            </div>
                        </fieldset>
                    </li>
					<%--End adding sku html --%>
			</dsp:oparam>
		</dsp:droplet><%--End TopSkusDroplet --%>		                                  
		<c:if test="${currentCount % 4 == 0 }">     			
           </ul>
        </c:if>
	</dsp:oparam>
</dsp:droplet>	<%-- end ForEach pickListItem.topSkus --%>  						
  						
</dsp:page>  						