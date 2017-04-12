<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/repository/seo/CanonicalItemLink"/>
	
	<dsp:getvalueof var="count" param="BBBProductListVO.bbbProductCount" />
	<dsp:getvalueof var="CategoryId" param="CatalogRefId" />
	<c:set var="rowIndex" value="1" />
	<dsp:getvalueof var="promoSC" param="promoSC"/>
	<dsp:getvalueof var="plpGridSize" param="plpGridSize"/>
	<dsp:getvalueof param="browseSearchVO" var="browseSearchVO"/>
	<%-- R2.2 Stroy 116. Setting variables based on grid size of PLP --%>
	<c:choose>
	   <%-- Adding condition for new 4x4 view |List View Redesign Story |Sprint2 |START --%>
	    <c:when test="${plpGridSize == '4'}">
			<c:set var="gridClass" value=""/>
			<c:set var="liGridClass" value="grid_2_3"/>
			<c:set var="is_gridview_3x3" value="false"/>
			<c:set var="imageSize" value="170"/>
			<c:set var="gridSize" value="4"/>
			<c:set var="parentGridClass" value="grid_10"/>
		</c:when>
		<%-- Adding condition for new 4x4 view  |List View Redesign Story |Sprint2 |End --%> 
		<c:when test="${plpGridSize == '3'}">
			<c:set var="gridClass" value="grid_3"/>
			<c:set var="liGridClass" value="grid_3"/>
			<c:set var="is_gridview_3x3" value="true"/>
			<c:set var="imageSize" value="229"/>
			<c:set var="gridSize" value="3"/>
			<c:set var="parentGridClass" value="grid_9"/>
		</c:when>
		<c:otherwise>
			<c:set var="gridClass" value=""/>
			<c:set var="liGridClass" value="grid_2"/>
			<c:set var="is_gridview_3x3" value="false"/>
			<c:set var="imageSize" value="146"/>
			<c:set var="gridSize" value="5"/>
			<c:set var="parentGridClass" value="grid_10"/>
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test="${promoSC and not is_gridview_3x3}">
			<div class="grid_8 alpha <c:out value="${gridClass}"/>">
		</c:when>
		<c:otherwise>
			<div class="<c:out value="${parentGridClass}"/> noMar <c:out value="${gridClass}"/>">
		</c:otherwise>
	</c:choose>
	
	<%-- R2.2 Story - 178-a Product Comparison tool Changes | Check if compare functionality is enabled --%>
	<c:set var="compareProducts"><bbbc:config key="compareProducts" configName="FlagDrivenFunctions" /></c:set> 
	
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="BBBProductListVO.bbbProducts" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="productId" param="element.productID" />
			<dsp:getvalueof var="productName" param="element.productName"/>
			<%-- Check for Swatch Flag attribute returned from Search Engine--%>
			<dsp:getvalueof var="swatchFlag" param="element.swatchFlag"/>
			<dsp:getvalueof var="rollupFlag" param="element.rollupFlag"/>
			<dsp:getvalueof var="collectionFlag" param="element.collectionFlag"/>
			<dsp:getvalueof var="promoSC" param="promoSC"/>
			<dsp:getvalueof var="portrait" param="portrait"/>
			<dsp:getvalueof id="count" param="count" />
			<dsp:getvalueof id="productCountDisplayed" param="size" />
		 	<c:set var="custClass" value="" />
			<c:choose>
				<c:when test="${promoSC and not is_gridview_3x3}">
					<c:if test="${count % 4 == 1}">
						<ul id="row<c:out value="${rowIndex}"/>" class="clearfix prodGridRow">
							<c:set var="custClass" value="alpha" />
					</c:if>
					<c:if test="${count % 4 == 0}">
						<c:set var="custClass" value="omega" />
					</c:if>
				</c:when>
				<c:otherwise>
					<c:if test="${count % gridSize == 1}">
						<ul id="row<c:out value="${rowIndex}"/>" class="clearfix prodGridRow">
							<c:set var="custClass" value="alpha" />
					</c:if>
					<c:if test="${count % gridSize == 0}">
						<c:set var="custClass" value="omega" />
					</c:if>
				</c:otherwise>
			</c:choose>
			<li class="<c:out value="${liGridClass}"/> product <c:out value="${custClass}"/> registryDataItemsWrap listDataItemsWrap">
				<div class="productShadow"></div>
				<div class="productContent">
				<dsp:getvalueof var="seoUrl" param="element.seoUrl"/>
					<c:choose>
						<c:when test="${empty seoUrl}">
							<dsp:droplet name="CanonicalItemLink">
								<dsp:param name="id" param="element.productID" />
								<dsp:param name="itemDescriptorName" value="product" />
								<dsp:param name="repositoryName"
									value="/atg/commerce/catalog/ProductCatalog" />
								<dsp:oparam name="output">
								<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
										param="url" />
								</dsp:oparam>
							</dsp:droplet>
						</c:when>
						<c:otherwise>
							<c:set var="finalUrl" value="${seoUrl}"></c:set>
						</c:otherwise>
					</c:choose>
					<dsp:getvalueof var="pdpUrl" value="${finalUrl}" />
					<dsp:getvalueof var="portraitClass" value=""/>
					<dsp:getvalueof var="imageURL" param="element.imageURL"/>
					
					<%-- R2.2 Stroy 116. Changing image sizes to 229 px if grid size is 3x3 --%>
					<c:if test="${is_gridview_3x3}">
						<c:set var="imageURL"><c:out value='${fn:replace(imageURL,"$146$","$229$")}'/></c:set>
					</c:if>
					
					<c:if test="${portrait eq 'true'}">
						<dsp:getvalueof var="portraitClass" value="prodImgPortrait"/>
						<dsp:getvalueof var="imgHeight" value="200"/>
						<dsp:getvalueof var="imageURL" param="element.verticalImageURL"/>
					</c:if>
		        <!--defect fixed by @psin52 -->
					<c:choose>
					<c:when test="${CategoryId eq null}">
					<dsp:a iclass="prodImg ${portraitClass}" page="${finalUrl}" title="${productName}">
						<%-- Thumbnail image exists OR not--%>
						<c:choose>
							<c:when test="${not empty imageURL}">
								<img class="productImage noImageFound" src="${scene7Path}/${imageURL}" height="${imageSize}" width="${imageSize}" alt="${productName}" />
							</c:when>
							<c:otherwise>
								<img class="productImage" src='${imagePath}/_assets/global/images/no_image_available.jpg' alt="${productName}" width="${imageSize}" height="${imageSize}" />
							</c:otherwise>
						</c:choose>
					</dsp:a>
					</c:when>
					<c:otherwise>
					<dsp:a iclass="prodImg ${portraitClass}" page="${finalUrl}?categoryId=${CategoryId}" title="${productName}">
						<%-- Thumbnail image exists OR not--%>
						<c:choose>
							<c:when test="${not empty imageURL}">
								<img class="productImage noImageFound" src="${scene7Path}/${imageURL}" height="${imageSize}" width="${imageSize}" alt="${productName}" />
							</c:when>
							<c:otherwise>
								<img class="productImage" src='${imagePath}/_assets/global/images/no_image_available.jpg' alt="${productName}" width="${imageSize}" height="${imageSize}" />
							</c:otherwise>
						</c:choose>
					</dsp:a>
					</c:otherwise>
					</c:choose>
					<c:choose>
		     	    	<c:when test="${collectionFlag eq 1}">   
		        			<c:set var="quickViewClass" value="showOptionsCollection"/>
		   				</c:when>
						<c:otherwise>
		        			<c:set var="quickViewClass" value="showOptionMultiSku"/>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${rollupFlag eq 'true' || collectionFlag == '1'}">
							<c:set var="isMultiRollUpFlag" value="true" />	
						</c:when>
						<c:otherwise>
							<c:set var="isMultiRollUpFlag" value="false" />
						</c:otherwise>
					</c:choose>
					<%--  Start | R2.2.1 Story 131. Quick View on PLP Search Pages --%>
					<c:choose>
						<c:when test="${plpGridSize == 3}">
							<div class="${liGridClass} alpha padBottom_10 fl quickViewAndCompare">
	                    	<span class="quickView ${quickViewClass} fl marRight_30 padRight_5"><bbbl:label key="lbl_product_quick_view" language="${pageContext.request.locale.language}" /></span>
		                    <div class="checkboxItem input clearfix noPad fl noBorder">
		                     <c:if test="${compareProducts eq true}">
		                        <dsp:getvalueof var="productId" param="element.productID"/>
								<dsp:getvalueof var="inCompareDrawer" param="element.inCompareDrawer"/>
									<c:choose>
										<c:when test="${inCompareDrawer eq true}">
		                        			<div class="checkbox noMar">
		                            			<input name="Compare" id="compareChkTxt_${productId}" class="compareChkTxt" type="checkbox" value="compareItem" data-productId="${productId}"  checked = "true" />
		                        			</div>
		                        			<div class="label">
		                            			<label for="compareChkTxt_${productId}" class="compareChkTxt"><bbbl:label key="lbl_compare_product" language="${pageContext.request.locale.language}" /></label>
		                        			</div>
		                        		</c:when>
		                        		<c:otherwise>
		                        			<div class="checkbox noMar">
		                            			<input name="Compare" id="compareChkTxt_${productId}" class="compareChkTxt" type="checkbox" value="compareItem" data-productId="${productId}" />
		                        			</div>
		                        			<div class="label">
		                            			<label for="compareChkTxt_${productId}" class="compareChkTxt"><bbbl:label key="lbl_compare_product" language="${pageContext.request.locale.language}" /></label>
		                        			</div>
		                        		</c:otherwise>
		                        	</c:choose>
		                        </c:if>
		                    </div>
	                    
		                    <!-- Data to submit for Add to Cart / Find In Store -->
							<input type="hidden" class="addItemToList addItemToRegis itemQuantity" value="1" name="qty" data-change-store-submit="qty" />
							<input type="hidden" data-change-store-internaldata="true" data-change-store-errors="required" data-change-store-submit="prodId" value="${productId}" class="_prodId addItemToRegis productId addItemToList" name="prodId"/>
							<input type="hidden" data-change-store-internaldata="true" data-change-store-errors="required" data-change-store-submit="skuId" class="addItemToRegis _skuId addItemToList changeStoreSkuId" value="${skuID}" name="skuId"/>
							<input type="hidden" name="price" value="${omniPrice}" class="addItemToList addItemToRegis" />
							<input type="hidden" data-change-store-storeid="storeId" value="" name="storeId" class="addToCartSubmitData"/>
							<input type="hidden" name="parentProductId" value="${productId}" class="addItemToList addItemToRegis" />
							<input type="hidden" value="" class="addToCartSubmitData" name="bts" data-change-store-storeid="bts"/>
							<input type="hidden" value="true" class="addItemToList addItemToRegis" name="fromSearch"/>
							<input type="hidden" value="${LiveClickerOn}" name="lcFlag"/>
							<input type="hidden" value="${CategoryId}" class="categoryId"/>
							<input type="hidden" value="${isMultiRollUpFlag}" class="isMultiRollUpFlag"/>
							<input type="hidden" value="" class="selectedRollUpValue"/>
	               		</div>
	               	</c:when>
	               	<c:otherwise>
	               	
	               	    <div class="${liGridClass} alpha padBottom_10 textCenter quickViewAndCompare">
	                    <div class="padTop_10"><span class="quickView ${quickViewClass} "><bbbl:label key="lbl_product_quick_view" language="${pageContext.request.locale.language}" /></span></div>
	                    
	                     <c:if test="${compareProducts eq true}">
	                        <dsp:getvalueof var="productId" param="element.productID"/>
							<dsp:getvalueof var="inCompareDrawer" param="element.inCompareDrawer"/>
								<c:choose>
									<c:when test="${inCompareDrawer eq true}">	                        			
										<input name="Compare" id="compareChkTxt_${productId}" class="compareChkTxt" type="checkbox" value="compareItem" data-productId="${productId}"  checked = "true" />
										<label for="compareChkTxt_${productId}" class="compareChkTxt"><bbbl:label key="lbl_compare_product" language="${pageContext.request.locale.language}" /></label>
									</c:when>
	                        		<c:otherwise>
										<input name="Compare" id="compareChkTxt_${productId}" class="compareChkTxt" type="checkbox" value="compareItem" data-productId="${productId}" />
										<label for="compareChkTxt_${productId}" class="compareChkTxt"><bbbl:label key="lbl_compare_product" language="${pageContext.request.locale.language}" /></label>
									</c:otherwise>
	                        	</c:choose>
	                        </c:if>
	                    
	                    
	                    <!-- Data to submit for Add to Cart / Find In Store -->
						<input type="hidden" class="addItemToList addItemToRegis itemQuantity" value="1" name="qty" data-change-store-submit="qty" />
						<input type="hidden" data-change-store-internaldata="true" data-change-store-errors="required" data-change-store-submit="prodId" value="${productId}" class="_prodId addItemToRegis productId addItemToList" name="prodId"/>
						<input type="hidden" data-change-store-internaldata="true" data-change-store-errors="required" data-change-store-submit="skuId" class="addItemToRegis _skuId addItemToList changeStoreSkuId" value="${skuID}" name="skuId"/>
						<input type="hidden" name="price" value="${omniPrice}" class="addItemToList addItemToRegis" />
						<input type="hidden" data-change-store-storeid="storeId" value="" name="storeId" class="addToCartSubmitData"/>
						<input type="hidden" name="parentProductId" value="${productId}" class="addItemToList addItemToRegis" />
						<input type="hidden" value="" class="addToCartSubmitData" name="bts" data-change-store-storeid="bts"/>
						<input type="hidden" value="true" class="addItemToList addItemToRegis" name="fromSearch"/>
						<input type="hidden" value="${LiveClickerOn}" name="lcFlag"/>
						<input type="hidden" value="${CategoryId}" class="categoryId"/>
						<input type="hidden" value="${isMultiRollUpFlag}" class="isMultiRollUpFlag"/>
						<input type="hidden" value="" class="selectedRollUpValue"/>
	               </div>
	               	   
	               	</c:otherwise>
	              </c:choose>
	               
	               <%--  End | R2.2.1 Story 131. Quick View on PLP Search Pages --%>
	               
					<c:if test="${swatchFlag == '1'}">
						<div class="prodSwatchesContainer clearfix">
							<div class="prodSwatches">
								<dsp:droplet name="ForEach">
									<dsp:param name="array" param="element.colorSet" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="id" param="key"/>
										
										<!-- Added for R2-141 -->
										<c:choose>
										 <c:when test="${rollupFlag eq 'true' || collectionFlag == '1'}">
										 <dsp:getvalueof var="colorValue" param="element.color"/>
										   <dsp:getvalueof var="colorParam" value="color"/> 
                                           
                                            <c:url value="${finalUrl}" var="colorProdUrl">
                                                <c:param name="categoryId" value="${CategoryId}"/>
                                                <c:param name="color" value="${colorValue}"/>
                                            </c:url>
										  </c:when>
										 <c:otherwise>
										   <dsp:getvalueof var="colorValue" param="element.skuID"/>
										   <dsp:getvalueof var="colorParam" value="skuId"/>
                                           
                                           <c:url value="${finalUrl}" var="colorProdUrl">
                                                <c:param name="categoryId" value="${CategoryId}"/>
                                                <c:param name="skuId" value="${colorValue}"/>
                                            </c:url>
										 </c:otherwise>										
										</c:choose>		
										
										
										<dsp:getvalueof var="productUrl" param="element.skuMedImageURL"/>
										<c:if test="${portrait eq 'true'}">
											<dsp:getvalueof var="productUrl" param="element.skuVerticalImageURL"/>
										</c:if>
										<dsp:getvalueof var="swatchUrl" param="element.skuSwatchImageURL"/>
										<dsp:getvalueof var="colorName" param="element.color"/>
										
										<%-- R2.2 Stroy 116. Changing image sizes to 229 px if grid size is 3x3 --%>
										<c:if test="${is_gridview_3x3}">
											<c:set var="productUrl"><c:out value='${fn:replace(productUrl,"$146$","$229$")}'/></c:set>
										</c:if>
										<c:choose>
											<c:when test="${not empty productUrl}">
												<a href="${colorProdUrl}" class="fl" title="${colorName}"  data-color-value="${colorValue}" data-color-param="${colorParam}"  data-main-image-src="${scene7Path}/${productUrl}">
													<span>
														<img src="${scene7Path}/${swatchUrl}" height="10" width="10" alt="${colorName}"/>
													</span>
												</a>
											</c:when>
											<c:otherwise>
												<a href="${colorProdUrl}" class="fl" title="${colorName}" data-color-value="${colorName}" data-color-value="${colorValue}" data-color-param="${colorParam}" data-main-image-src="${imagePath}/_assets/global/images/no_image_available.jpg">
													<span>
														<img src="${scene7Path}/${swatchUrl}" height="10" width="10" alt="${colorName}"/>
													</span>
												</a>
											</c:otherwise>
										</c:choose> 	 
									</dsp:oparam>
								</dsp:droplet>
								<div class="clear"></div>
							</div>
						</div>
					</c:if>
					
					<%-- Start :- R2.2 Story 546. Adding Ribbon for Featured Product --%>
					<dsp:include page="/search/featured_product.jsp">
		 				<dsp:param name="browseSearchVO" param="browseSearchVO" />
						<dsp:param name="productId" value="${productId}"/>
						<dsp:param name="productCountDisplayed" value="${productCountDisplayed}"/>
					</dsp:include>
					<%-- End :- R2.2 Story 546. Adding Ribbon for Featured Product --%>
					
					<ul class="prodInfo">
						<li class="prodName">
						<dsp:getvalueof var="seoUrl" param="element.seoUrl"/>
						<c:choose>
						<c:when test="${empty seoUrl}">
							<dsp:droplet name="CanonicalItemLink">
								<dsp:param name="id" param="element.productID" />
								<dsp:param name="itemDescriptorName" value="product" />
								<dsp:param name="repositoryName"
									value="/atg/commerce/catalog/ProductCatalog" />
								<dsp:oparam name="output">
								<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
										param="url" />
								</dsp:oparam>
							</dsp:droplet>
						</c:when>
						<c:otherwise>
							<c:set var="finalUrl" value="${seoUrl}"></c:set>
						</c:otherwise>
						</c:choose>
					<c:set var="prodName">
						<dsp:valueof param="element.productName" valueishtml="true"/>
					</c:set>
				 <!--Defect fixed by @psin52 -->
					<c:choose>
					 <c:when test="${CategoryId eq null}">
					 <dsp:a  page="${finalUrl}"  title="${prodName}">
					 <dsp:valueof param="element.productName" valueishtml="true" />
					 </dsp:a>
					 </c:when>
					 <c:otherwise>
					 <dsp:a  page="${finalUrl}?categoryId=${CategoryId}"  title="${prodName}">
					 <dsp:valueof param="element.productName" valueishtml="true" />
					 </dsp:a>
					 </c:otherwise>
					</c:choose>
				</li>
				
				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="element.attribute" />
					<dsp:oparam name="output">
						<li><dsp:valueof param="element" valueishtml="true"/></li>
					</dsp:oparam>
				</dsp:droplet>				
						
				<c:if test="${BazaarVoiceOn}">
					<dsp:getvalueof var="reviews" param="element.reviews"/>
					<dsp:getvalueof var="ratings" param="element.ratings" vartype="java.lang.Integer"/>
					<dsp:getvalueof var="rating" value="${ratings * 10}" vartype="java.lang.Integer"/>
					<c:choose>
						<c:when test="${ratings ne null && ratings ne '0'}">
							<li class="prodReviews ratingsReviews clearfix prodReviews<fmt:formatNumber value="${rating}" pattern="#0" />">
								<span class="prodReviewSpanFont">
								<c:if test="${reviews ne null && reviews ne '0' && reviews gt '1'}">
								   <dsp:a page="${pdpUrl}?showRatings=true" title="${productName}">
								   	<dsp:valueof param="element.reviews"/><bbbl:label key="lbl_reviews_count" language="${pageContext.request.locale.language}" />
								   </dsp:a>
								</c:if>
								<c:if test="${reviews ne null && reviews ne '0' && reviews eq '1'}">
								   <dsp:a page="${pdpUrl}?showRatings=true" title="${productName}">
									<dsp:valueof param="element.reviews"/><bbbl:label key="lbl_review_count" language="${pageContext.request.locale.language}" />
								   </dsp:a>
								</c:if>
								</span>
							</li>
						</c:when>
						<c:otherwise>
							<li class="prodReviews ratingsReviews writeReview">
							<span class="prodReviewSpanFont">
								<dsp:getvalueof var="seoUrl" param="element.seoUrl"/>
								<c:choose>
									<c:when test="${empty seoUrl}">
										<dsp:droplet name="CanonicalItemLink">
											<dsp:param name="id" param="element.productID" />
											<dsp:param name="itemDescriptorName" value="product" />
											<dsp:param name="repositoryName"
												value="/atg/commerce/catalog/ProductCatalog" />
											<dsp:oparam name="output">
											<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
													param="url" />
											</dsp:oparam>
										</dsp:droplet>
									</c:when>
									<c:otherwise>
										<c:set var="finalUrl" value="${seoUrl}"></c:set>
									</c:otherwise>
								</c:choose>
								<c:set var="writeReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
								<dsp:a page="${finalUrl}?categoryId=${CategoryId}&writeReview=true" title="${writeReviewLink}">${writeReviewLink}</dsp:a> 
							</span>		
							</li>
						</c:otherwise>
					</c:choose>
				</c:if>
					
				<li class="prodPrice"><dsp:valueof param="element.priceRange" valueishtml="true" /></li>
						
			</ul>
		</div>
	</li>
	<c:choose>
		<c:when test="${promoSC and not is_gridview_3x3}">
			<c:if test="${count % 4 == 0}">
				<dsp:getvalueof var="rowIndex" value="${rowIndex+1}" />
				</ul>
			</c:if>
			</c:when>
			<c:otherwise>
				<c:if test="${count % gridSize == 0}">
					<dsp:getvalueof var="rowIndex" value="${rowIndex+1}" />
					</ul>
				</c:if>
			</c:otherwise>
			</c:choose>
		</dsp:oparam>
	</dsp:droplet>
	</div>
</dsp:page>