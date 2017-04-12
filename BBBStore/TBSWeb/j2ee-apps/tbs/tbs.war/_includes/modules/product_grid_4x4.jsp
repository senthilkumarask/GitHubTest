<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Range" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
	<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
	
	<dsp:getvalueof var="count" param="BBBProductListVO.bbbProductCount" />
	<dsp:getvalueof var="searchTerm" param="Keyword"/>
	<dsp:getvalueof var="promoSR" param="promoSR"/>
	<dsp:getvalueof param="browseSearchVO" var="browseSearchVO"/>
	<dsp:getvalueof param="type" var="searchType"/>
	
	<c:set var="rowIndex" value="1" />
	<dsp:getvalueof var="plpGridSize" param="plpGridSize"/>
	
	<%-- R2.2 Stroy 116. Setting variables based on grid size of search results page --%>
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
	<c:when test="${promoSR and not is_gridview_3x3}">
	<div class="grid_8 alpha <c:out value="${gridClass}"/>">
	</c:when>
	<c:otherwise>
	<div class="<c:out value="${parentGridClass}"/> noMar <c:out value="${gridClass}"/>">
	</c:otherwise>
	</c:choose>
	
	<%-- R2.2 Story - 178-a Product Comparison tool Changes | Check if compare functionality is enabled --%>
	<c:set var="compareProducts"><bbbc:config key="compareProducts" configName="FlagDrivenFunctions" /></c:set>
	
	<dsp:form action="" method="post">
	
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="BBBProductListVO.bbbProducts" />
		<dsp:oparam name="output">
		
			<dsp:getvalueof var="productId" param="element.productID" />
			<dsp:getvalueof var="productName" param="element.productName"/>
			<%-- Check for Swatch Flag attribute returned from Endeca--%>
			<dsp:getvalueof var="swatchFlag" param="element.swatchFlag"/>
			<dsp:getvalueof var="rollupFlag" param="element.rollupFlag"/>
			<dsp:getvalueof var="collectionFlag" param="element.collectionFlag"/>
			<dsp:getvalueof var="index" param="index"/>
			<dsp:getvalueof id="count" param="count" />
			<dsp:getvalueof id="productCountDisplayed" param="size" />
			<c:set var="custClass" value="" />
			<c:choose>
			<c:when test="${promoSR and not is_gridview_3x3}">
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
			
			<dsp:droplet name="/com/bbb/commerce/browse/droplet/ProductStatusDroplet">
					<dsp:param name="productId" param="element.productID" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="isProductActive" param="isProductActive" />
					</dsp:oparam>
			</dsp:droplet>
			
			<li class="<c:out value="${liGridClass}"/> product <c:out value="${custClass}"/> registryDataItemsWrap listDataItemsWrap">
				<div class="productShadow"></div>
				<div class="productContent">
					<dsp:getvalueof var="seoUrl" param="element.seoUrl"/>
					<c:choose>
						<c:when test="${empty seoUrl}">
							<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
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
					<c:url value="${finalUrl}" var="urlSe">
						<c:param name="Keyword" value="${searchTerm}"/>
					</c:url>
					<dsp:getvalueof var="pdpUrl" value="${finalUrl}" />
					
					<c:if test="${not empty searchType }">
						<c:set var="urlSe" value="#"/>
					</c:if>
					<dsp:a iclass="prodImg" href="${urlSe}" title="${productName}">
						<dsp:getvalueof var="imageURL" param="element.imageURL"/>
						
						<%-- R2.2 Stroy 116. Changing image sizes to 229 px if grid size is 3x3 --%>
						<c:if test="${is_gridview_3x3}">
							<c:set var="imageURL"><c:out value='${fn:replace(imageURL,"$146$","$229$")}'/></c:set>
						</c:if>
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
	                    
	               </div>
	               </c:when>
	             <c:otherwise>
	               	
	               	    <div class="${liGridClass} alpha padBottom_10 textCenter quickViewAndCompare">
	                    <div class="padTop_10"><span class="quickView ${quickViewClass}"><bbbl:label key="lbl_product_quick_view" language="${pageContext.request.locale.language}" /></span></div>
	                    
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
                                                <c:param name="Keyword" value="${searchTerm}"/>
                                            </c:url>
										  </c:when>
										 <c:otherwise>
										   <dsp:getvalueof var="colorValue" param="element.skuID"/>
										   <dsp:getvalueof var="colorParam" value="skuId"/>
                                           <c:url value="${finalUrl}" var="colorProdUrl">
                                                <c:param name="categoryId" value="${CategoryId}"/>
                                                <c:param name="skuId" value="${colorValue}"/>
                                                <c:param name="Keyword" value="${searchTerm}"/>
                                            </c:url>
										 </c:otherwise>										
										</c:choose>								
										<dsp:getvalueof var="productUrl" param="element.skuMedImageURL"/>
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
									<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
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
							<c:url value="${finalUrl}" var="urlSe">
								<c:param name="Keyword" value="${searchTerm}"/>
							</c:url>
							<dsp:getvalueof var="prodName" param="element.productName"/>
							<c:choose>
								<c:when test="${empty searchType }">
								<dsp:a  href="${urlSe}" title="${prodName}">
									<dsp:valueof param="element.productName" valueishtml="true" />
								</dsp:a>
							</c:when>
							<c:otherwise>
								<dsp:valueof param="element.productName" valueishtml="true" />
							</c:otherwise>
							</c:choose>
							
						</li>
						
						<dsp:droplet name="ForEach">
							<dsp:param name="array" param="element.attribute" />
							<dsp:oparam name="output">
								<li><dsp:valueof param="element" valueishtml="true"/></li>
							</dsp:oparam>
						</dsp:droplet>
						<c:if test="${empty searchType }">
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
													<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
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
											<c:url value="${finalUrl}" var="urlSe">
												<c:param name="Keyword" value="${searchTerm}"/>
											</c:url>
											<c:choose>
											<c:when test="${isProductActive}">
											<dsp:a href="${urlSe}&writeReview=true">
												<bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" />
											</dsp:a>
											</c:when>
											<c:otherwise>
											<dsp:a href="${urlSe}&writeReview=false">
												<bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" />
											</dsp:a>
											</c:otherwise>
											</c:choose>
										</span>
										</li>
									</c:otherwise>
								</c:choose>
							</c:if>
						</c:if>
							
						<li class="prodPrice"><dsp:valueof param="element.priceRange" valueishtml="true" /></li>
						
						<li>
							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="element.skuSet"/>
								<dsp:param name="elementName" value="skuId"/>
								<dsp:oparam name="output">
									<dsp:getvalueof param="key" var="resultSkuId"/>
									<c:if test="${fn:contains(searchTerm, resultSkuId)}">
										<dsp:input bean="CartModifierFormHandler.productIds" type="hidden" name="prodId" value="${productId }" />
										<dsp:input bean="CartModifierFormHandler.catalogRefIds"  type="hidden" name="skuId" value="${resultSkuId}"/>
										
							            <dsp:input bean="CartModifierFormHandler.quantity" name="qty" value="1" size="2"/>
							            
											 
							            <%-- <dsp:input bean="CartModifierFormHandler.addMultipleItemsToOrderSuccessURL"  type="hidden" value="/tbs/cart/cart.jsp" />
							            <dsp:input bean="CartModifierFormHandler.addMultipleItemsToOrderErrorURL"  type="hidden" value="/tbs/s/${searchTerm}" /> --%>
							            <dsp:input bean="CartModifierFormHandler.errorQueryParam"
												type="hidden"
												value="/${searchTerm}" /> 
							           <dsp:input bean="CartModifierFormHandler.fromPage"  type="hidden" value="productGrid" />
									</c:if>
								</dsp:oparam>
							</dsp:droplet>
						</li>
							
						<%-- R2.2 Story - 178-a Product Comparison tool Changes : Start --%>
						<c:if test="${compareProducts eq true}">
							<dsp:getvalueof var="productId" param="element.productID"/>
							<dsp:getvalueof var="inCompareDrawer" param="element.inCompareDrawer"/>
							<c:choose>
								<c:when test="${inCompareDrawer eq true}">
									<li><input id="compareChkTxt_${productId}" type="checkbox" name ="Compare" class="compareChkTxt" data-productId="${productId}"  checked = "true" />
									<label class="compareChkTxt" for="compareChkTxt_${productId}"><bbbl:label key="lbl_compare_product" language="${pageContext.request.locale.language}" /></label>
									</li>
								</c:when>
								<c:otherwise>
									<li><input id="compareChkTxt_${productId}" type="checkbox" name ="Compare" class="compareChkTxt" data-productId="${productId}" />
										<label class="compareChkTxt" for="compareChkTxt_${productId}"><bbbl:label key="lbl_compare_product" language="${pageContext.request.locale.language}" /></label>
									</li>
								</c:otherwise>
							</c:choose>
							
						</c:if>
						<%-- R2.2 Story - 178-a Product Comparison tool Changes : End --%>
					</ul>
				</div>
			</li>
			<c:choose>
			<c:when test="${promoSR and not is_gridview_3x3}">
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
	
	<c:if test="${not empty searchType }">
		<dsp:input name="submit" bean="CartModifierFormHandler.addMultipleItemsToOrder" type="submit" value="Add Items To Cart"/>
	</c:if>
	</dsp:form>
</dsp:page>