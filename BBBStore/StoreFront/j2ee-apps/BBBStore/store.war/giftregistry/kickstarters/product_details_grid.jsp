<dsp:page>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

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
<c:set var="lblReviewsCount"><bbbl:label key="lbl_reviews_count" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblReviewCount"><bbbl:label key="lbl_review_count" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblForThe"><bbbl:label key="lbl_accessibility_for_the" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblAboutThe"><bbbl:label key="lbl_accessibility_about_the" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="scene7Path"><bbbc:config key="scene7_url" configName="ThirdPartyURLs" /></c:set>
<c:set var="enableLTLRegForSite"><bbbc:config key="enableLTLRegForSite" configName="FlagDrivenFunctions" /></c:set>
<c:set var="lblreviews"><bbbl:label key="lbl_reviews_count" language="<c:out param='${language}'"/></c:set>
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
            <div class="clearfix collectionList">
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
				<dsp:getvalueof var="ltlItem" param="SKUDetailsVO.ltlItem" />
				<dsp:getvalueof var="displayShipMsg" param="TopSKUDetailVO.displayShipMsg" />
				
				<c:set var="price">
					<dsp:include page="/browse/product_details_price.jsp">
						<dsp:param name="product" param="productId" />
						<dsp:param name="sku" param="SKUDetailsVO.skuId" />
						<dsp:param name="inCartFlagSKU" param="TopSKUDetailVO.inCartFlag" />
						<dsp:param name="priceLabelCodeSKU" param="TopSKUDetailVO.pricingLabelCode" />
						<c:choose>
						<c:when test="${!isTransient}">
							<dsp:param name="showInCartPrice" value="true"/>
						</c:when>
						<c:otherwise>
							<dsp:param name="showInCartPrice" value="false"/>
						</c:otherwise>
						</c:choose>
					</dsp:include>
				</c:set>

				<%--Adding sku html --%>
				<c:choose>
					<c:when test="${currentCount % 4 == 0}">
						<div class="grid_3  collectionItems last clearfix">
					</c:when>
					<c:otherwise>
						<div class="grid_3  collectionItems clearfix">
					</c:otherwise>
				</c:choose>
				<!-- <input type="hidden" name="isLtlItem" value="${ltlItem}"/> -->

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

                        	<div class="grid_3 prodImageWrap alpha marBottom_10">

                            	<dsp:a page="${finalUrl}" title="${displayName}" iclass="prodImg fl" >
                            		<img class="fl noImageFound" src="${scene7Path}/${image}" width="229" height="229" alt="${displayName}" />
                           		</dsp:a>

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

							<div class="grid_3 alpha">
                                <div class="prodInfoGrid clearfix" >
                                    <div class="prodNameGrid prodName" style="float:none;">
                                        <div >
                                            <dsp:a page="${finalUrl}" title="${displayName}" >${displayName}</dsp:a>
                                        </div>
                                    </div>
                                        <dsp:getvalueof var="ratingAvailable" param="BV.ratingAvailable"></dsp:getvalueof>
											<dsp:getvalueof var="ratings" param="BV.averageOverallRating" vartype="java.lang.Integer"/>
											<dsp:getvalueof var="rating" value="${ratings * 10}" vartype="java.lang.Integer"/>
											<dsp:getvalueof param="BV.totalReviewCount" var="totalReviewCount"/>
											<dsp:getvalueof var="fltValue" param="BV.averageOverallRating"/>
											<dsp:getvalueof param="BV.totalReviewCount" var="totalReviewCount"/>
											<dsp:getvalueof var="ratingsTitle" param="BV.ratingsTitle"/>
											
											<c:choose>
												<c:when test="${ratings ne null && ratings ne '0' && (totalReviewCount eq '1' || totalReviewCount gt '1') }">
													<c:choose>
														<c:when test="${totalReviewCount == 1}">
															<div class="prodReviewGrid prodReview clearfix metaFeedback prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span title="${fltValue}" class="ratingTxt prodReview "></span><span tabindex="0" aria-label="${ratingsTitle} ${lblForThe} ${displayName}" class="writeReview reviewTxt"><dsp:a page="${finalUrl}?showRatings=true">
															<span aria-label="${totalReviewCount} ${lblreviews} ${lblForThe} ${displayName}">${totalReviewCount} ${lblreviews}</span>
														</dsp:a></span></div>
														</c:when>
														<c:otherwise>
															<div class="prodReviewGrid prodReview clearfix metaFeedback prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span title="${fltValue}" class="ratingTxt prodReview"></span><span tabindex="0" aria-label="${ratingsTitle} ${lblForThe} ${displayName}" class="writeReview reviewTxt">
															<dsp:a page="${finalUrl}?showRatings=true">
															<span aria-label="${totalReviewCount} ${lblreviews} ${lblForThe} ${displayName}">${totalReviewCount} ${lblreviews}</span>
														</dsp:a></span></div>
														</c:otherwise>
													</c:choose>
												</c:when>
												<c:otherwise>
													<div class="prodReviewGrid prodReview metaFeedback prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ratingTxt prodReview"></span><span tabindex="0" aria-label="${ratingsTitle} ${lblForThe} ${displayName}" class="writeReview reviewTxt">
														<dsp:a page="${finalUrl}?showRatings=true">
															<span aria-label="${totalReviewCount} ${lblreviews} ${lblForThe} ${displayName}">${totalReviewCount} ${lblreviews}</span>													
														</dsp:a>
														</span>
													</div>
												</c:otherwise>
											</c:choose>
                                    <div>
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
                                    </div>

                                    <div class="prodpriceGrid" style="float:none;"><dsp:valueof converter="currency" value="${price}" />
                                    <c:if test="${not empty displayShipMsg }">
									<span class="freeShipbadge">${displayShipMsg}</span>
									</c:if>
									</div>
									<div class="prodbuttonGrid" style="float:none;">
									
                                    	<c:choose>
       										<c:when test="${isTransient == 'false' && sizeValue>=1}">
		                                    	<div class="buttons">
		                                        	<div class="spinner fl">
		                                                 <a title="Decrease quantity" aria-label="Decrease quantity of ${displayName}" class="scrollDown down" href="#"><bbbl:label key="lbl_decrease_quantity" language="${pageContext.request.locale.language}" /></a>
		                                                 <input name="qty" id="pqty${skuId}" title="Enter Quantity" class="fl pqty qty addItemToRegis _qty itemQuantity addItemToList escapeHTMLTag resetInitialQTY" type="text" value="${recommanded_qty}" data-initialqty="${recommanded_qty}" data-change-store-submit="qty" data-change-store-errors="required digits nonZero"  role="textbox" aria-required="true" aria-describedby="pqty${skuId}" aria-label="Enter quantity for ${displayName} " maxlength="2" />
		                                                 <a title="Increase quantity" aria-label="Increase quantity of ${displayName}" class="scrollUp up" href="#"><bbbl:label key="lbl_increase_quantity" language="${pageContext.request.locale.language}" /></a>
		                                            </div>
		                                            <input type="hidden" name="isLtlItemNew" class="_isLtlItemNew addItemToRegis addItemToList" value="${ltlItem}" />
		                                            <input type="hidden" name="prodId" class="_prodId addItemToRegis productId addItemToList" value="${productId}" data-change-store-submit="prodId" data-change-store-errors="required" data-change-store-internaldata="true" />
		                                            <input type="hidden" name="skuId" value="${skuId}" class="addItemToRegis _skuId addItemToList changeStoreSkuId" data-change-store-submit="skuId" data-change-store-errors="required" data-change-store-internaldata="true" />
		                                            <input type="hidden" name="price" value="${omniPrice}" class="addItemToList addItemToRegis" />
		                                            <input type="hidden" name="heading1" value="${heading1}" class="addItemToRegis _heading1 addItemToList heading1" data-change-store-submit="heading1"/>
		                                            <c:if test="${isTransient == 'false'}">
														<div class="fr btnAddToRegistryWrapper">	
		                                                	<div class="button button_active <c:if test='${ltlItem && fn:containsIgnoreCase(enableLTLRegForSite,"false") || isInternationalCustomer }'>button_disabled</c:if>">
		                                                 		<c:set var="btnAddToRegistry"><bbbl:label key="lbl_add_to_registry__button" language="${pageContext.request.locale.language}" /></c:set>
		                                                     	<input type="button" name="btnAddToRegistry" class="btnAddToRegistry" aria-label="add ${displayName} to registry" value="${btnAddToRegistry}" onclick="" role="button" aria-pressed="false" 
		                                                     	data-notify-reg="true" <c:if test='${ltlItem && fn:containsIgnoreCase(enableLTLRegForSite,"false") || isInternationalCustomer}'>disabled="disabled"</c:if> />
		                                                 	</div>
		                                             	</div>
													</c:if>
		                                         </div>
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
                                        <div style="clear:both;"></div>
                                    </div>
                                </div>
                            </div>
                        </fieldset>
                    </div>
					<%--End adding sku html --%>
			</dsp:oparam>
		</dsp:droplet><%--End TopSkusDroplet --%>
		<c:if test="${currentCount % 4 == 0 }">
           </div>
        </c:if>
	</dsp:oparam>
</dsp:droplet>	<%-- end ForEach pickListItem.topSkus --%>

</dsp:page>