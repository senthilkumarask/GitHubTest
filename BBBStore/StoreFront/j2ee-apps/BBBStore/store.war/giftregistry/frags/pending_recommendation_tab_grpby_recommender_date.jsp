<dsp:page>
<script type="text/javascript">
	$(".recommendationCount").css("display", "none");
</script>

<dsp:getvalueof var="skuId" param="skuId"/>
<dsp:getvalueof var="skuDisplayName" param="skuDisplayName" />
<dsp:getvalueof var="skuColor" param="skuColor" />
<dsp:getvalueof var="skuSize" param="skuSize" />
<dsp:getvalueof var="displayShipMsg" param="displayShipMsg" />

<dsp:getvalueof var="upc" param="upc" />
<dsp:getvalueof var="comment" param="comment" />
<dsp:getvalueof var="firstName" param="firstName" />
<dsp:getvalueof var="lastName" param="lastName" />
<dsp:getvalueof var="skuListPrice" param="skuListPrice" />
<dsp:getvalueof var="skuSalePrice"  param="skuSalePrice" />
<dsp:getvalueof var="recommendedQuantity" param="recommendedQuantity" />
<dsp:getvalueof var="bazaarVoiceVo" param="bazaarVoiceVo" />
<dsp:getvalueof var="skuImage" param="skuImage" />
<dsp:getvalueof var="productId" param="productId" />
<dsp:getvalueof var="fname_profileId" param="fname_profileId" />
<dsp:getvalueof var="repositoryId" param="repositoryId" />
<dsp:getvalueof var="ltlItem" param="ltlItem" />
<dsp:getvalueof var="scene7Path" param="scene7Path" />
<dsp:getvalueof var="refNum" param="refnum" />
<dsp:getvalueof var="regItem" param="regItem" />
<dsp:getvalueof var="skuIncartPrice" param="skuIncartPrice"/>
<dsp:getvalueof var="skuIncartFlag" param="skuIncartFlag"/>

<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<c:if test='${not empty skuSalePrice && 0 ne skuSalePrice}'>
		<c:set var="skuListPrice">
			<dsp:valueof converter="currency" value="${skuSalePrice}" />
		</c:set>
	</c:if>
	
	<%--BBBH-4958 | incart pricing on recommendation tab --%>
	<c:if test="${skuIncartFlag}">
		<c:set var="skuListPrice">
			<dsp:valueof converter="currency" value="${skuIncartPrice}" />
		</c:set>
	</c:if>
	
<c:set var="lblReviewsCount"><bbbl:label key="lbl_reviews_count" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblReviewCount"><bbbl:label key="lbl_review_count" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblOfThe"><bbbl:label key="lbl_accessibility_of_the" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblAboutThe"><bbbl:label key="lbl_accessibility_about_the" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblProductQuickView"><bbbl:label key='lbl_mng_regitem_quick_view' language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblForThe"><bbbl:label key="lbl_accessibility_for_the" language="${pageContext.request.locale.language}" /></c:set>

<ul class="productDetailList giftViewProduct">												
							<li class="grid_12 pendingMessage">
								<h2 class="action_message accepted hidden">
									<span>${skuDisplayName}</span> has been added to your registry.									
								</h2>
								<h2 class="action_message decline hidden">
									<span>${skuDisplayName}</span> has been declined
								</h2>
							</li>
				            <dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
                               <dsp:param name="id" value="${productId}" />
                               <dsp:param name="itemDescriptorName" value="product" />
                               <dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
                               <dsp:oparam name="output">
                                  <dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
                               </dsp:oparam>
                             </dsp:droplet>
                             <li class="pending_recommendation grid_12 alpha omega qtyWrapper btngiftViewTopMargin giftViewProductChangeStoreWrapper addToCartGiftRegWrapper clearfix">							
							<c:if test="${ltlItem}">
							<div class="ltlMessage"><bbbl:label key='lbl_rlp_ltl_registry_message' language="${pageContext.request.locale.language}" /></div>
							</c:if>
							<a class="lnkQVPopup" href="${contextPath}${finalUrl}?skuId=${skuId}&registryId=${registryId}" data-pdp-qv-url="pdp_quick_view.jsp" data-skuId="${skuId}" data-registryId="${registryId}" data-productId="${productId}" data-refNum="${refNum}" title="${skuDisplayName}&nbsp;${skuListPrice}">
								<img class="fl productImage noImageFound" src="${scene7Path}/${skuImage}" alt="${skuDisplayName}&nbsp;${skuListPrice}"
									title="${skuDisplayName}&nbsp;${skuListPrice}" height="146" width="146" />
							</a>

							<dsp:getvalueof var="reviews" value="${bazaarVoiceVo.totalReviewCount}" vartype="java.lang.Integer"/>
							<dsp:getvalueof var="ratings" value="${bazaarVoiceVo.averageOverallRating}" vartype="java.lang.Float"/>
							<dsp:getvalueof var="rating" value="${ratings * 10}" vartype="java.lang.Integer"/>
                            <dsp:getvalueof var="ratingsTitle" value="${bazaarVoiceVo.ratingsTitle}"/>

							<div class="productContainer grid_10 omega">
								<div class="productTab productContent prodInfo clearfix">
									<div class="prodName grid_4 metaFeedback">
										<span class="blueName prodTitle">
										<a href="${contextPath}${finalUrl}?skuId=${skuId}&registryId=${registryId}"
															class="lnkQVPopup prodTitle" data-pdp-qv-url="pdp_quick_view.jsp" data-skuId="${skuId}" data-registryId="${registryId}" data-productId="${productId}" data-refNum="${refNum}"  title="${productName}">
											 ${skuDisplayName}
                                          </a>
                                      </span>
									<c:choose>
										<c:when test="${ratings ne null && ratings ne 0}">

										<span class="prodReviews clearfix metaFeedback"><span class="ratingTxt ratingsReviews prodReviews<fmt:formatNumber value="${rating}" pattern="#0" />"><span class="ariaLabel">${ratingsTitle}</span></span>
										<c:if test="${reviews ne null && reviews ne '0' && reviews gt '1'}">
									<span class="prodReviewSpanFont reviewTxt">
									   <a href="${contextPath}${finalUrl}?showRatings=true" title="${productName}"  role="link" aria-label="${reviews} ${lblReviewsCount} ${lblForThe} ${productName}">
									    <c:out value="${reviews }" />
									    	<bbbl:label key="lbl_reviews_count" language="${pageContext.request.locale.language}" />
										</a>
										</span>
									</c:if>
									<c:if test="${reviews ne null && reviews ne '0' && reviews eq '1'}">
									<span class="prodReviewSpanFont reviewTxt">
									   <a href="${contextPath}${finalUrl}?showRatings=true" title="${productName}"  role="link" aria-label="${reviews} ${lblReviewCount} ${lblForThe} ${productName}">
									    <c:out value="${reviews }" />
										<bbbl:label key="lbl_review_count" language="${pageContext.request.locale.language}" />
									   </a></span>
									</c:if>
								</span>
							</c:when>
							<c:otherwise>

								<span class="prodReviews metaFeedback">
									<c:set var="writeReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
									<span class="ratingTxt ratingsReviews"><span class="ariaLabel">${ratingsTitle}</span></span><span class="writeReview reviewTxt"><a href="${contextPath}${finalUrl}?categoryId=${CategoryId}&writeReview=true" title="${writeReviewLink}"  role="link" aria-label="${writeReviewLink} ${lblAboutThe} ${productName}">${writeReviewLink}</a></span>
								</span>
							</c:otherwise>

						</c:choose>
										<dl class="productAttributes">
										<c:if test="${skuColor != null}">
											<dt>
												<bbbl:label key="lbl_color"
													language="${pageContext.request.locale.language}" />
												:
											</dt>
											<c:if test="${empty skuColor}">									
												<br />
											</c:if>
											<dd>${skuColor}</dd>
											</c:if>
											<c:if test="${skuSize != null}">
												<dt>
													<bbbl:label key="lbl_item_size"
														language="${pageContext.request.locale.language}" />
													:
												</dt>
												<c:if test="${empty skuSize}">									
													<br />
												</c:if>
												<dd>${skuSize}</dd>
											</c:if>
											<dt>
												<bbbl:label key="lbl_sku_colon"
													language="${pageContext.request.locale.language}" />
											</dt>
											<dd>${skuId}</dd>
										</dl> <span class="quickViewLink"> <c:set var="quickViewURL">
				                     	/store/giftregistry/pdp_quick_view.jsp?skuId=${skuId}&registryId=${registryId}&productId=${productId}&bbbModalDialog=true
				                     </c:set> <a href="${quickViewURL}"
											class="lnkQVPopup prodTitle"
											data-pdp-qv-url="pdp_quick_view.jsp" data-skuId="${skuId}" data-registryId="${registryId}" data-productId="${productId}" data-refNum="${refNum}"
											title="${skuDisplayName}" role="link" aria-label="${lblProductQuickView} ${lblOfThe} ${skuDisplayName}"> <span
												class="icon-fallback-text"> <span class="icon-add"
													aria-hidden="true"></span> <span class="icon-text"><bbbl:label
															key='lbl_mng_regitem_quick_view'
															language="${pageContext.request.locale.language}" /></span></span> <bbbl:label
													key='lbl_mng_regitem_quick_view'
													language="${pageContext.request.locale.language}" />
										</a>
									</span>
								</div>
									<div class="price grid_2 omega "><span class="columnHeader"><bbbl:label
												key="lbl_mng_regitem_sortprice"
												language="${pageContext.request.locale.language}" /></span>
										         ${skuListPrice} <br/> ${displayShipMsg}
										         
									</div>
									<div class="requested grid_2 omega"><span
										class="columnHeader"><bbbl:label
												key="lbl_cartdetail_quantity"
												language="${pageContext.request.locale.language}" /></span>
										<div class="quantityPDP">
											<div class="input alpha marTop_5 clearfix spinner">
												<div class="text clearfix">
													<a title="Decrease quantity" class="scrollDown down"
														href="#"> <bbbl:label key="err_top_consultants_decrease_qty"
															language="${pageContext.request.locale.language}" />
													</a>
													<input name="cartQuantity" id="pqty${skuId}"
														title="Enter Quantity"
														class="fl pqty qty addItemToRegis _qty frmAjaxSubmitData  addItemToList escapeHTMLTag resetInitialQTY"
														type="text" value="${recommendedQuantity}"
														data-initialqty="1" data-change-store-submit="qty"
														data-change-store-errors="required digits nonZero"
														role="textbox" aria-required="true"
														aria-describedby="pqty${skuId}" maxlength="2" />
													<a title="Increase quantity" class="scrollUp up" href="#">
														<bbbl:label key="err_top_consultants_increase_qty"
															language="${pageContext.request.locale.language}" />
													</a>
												</div>
											</div>
										</div>
									</div>
									<div class="productLastColumn grid_2 omega">
										<input type="hidden" name="repositoryId" value="${repositoryId}" />
										<input type="hidden" name="skuId" value="${skuId}" />
										<input type="hidden" name="productId" value="${productId}"/>
										<input type="hidden" name="skuListPrice" value="${skuListPrice}" />
										<input type="hidden" name="fromPendingTab" value="true" />
										<input type="hidden" name="isLtlItem" value="${ltlItem}" />
											<c:if test="${not(ltlItem && fn:containsIgnoreCase(enableLTLRegForSite,'false'))}">
												<a href="#" class="btnAddToRegistryRecommendation button-Med btnPrimary centerTxt <c:if test="${isInternationalCustomer}">disabled</c:if>" aria-label="<bbbl:label key="lbl_add_to_registry" language="${pageContext.request.locale.language}"/>"
												data-notify-reg="true" >
											<bbbl:label key="lbl_add_to_registry" language="${pageContext.request.locale.language}"/></a>
											</c:if>
											<a href="#" class="btnAddToRegistryRecommendation declineRecommendation button-Med btnSecondary marTop_10">Decline</a>
									</div>
								</div>
                            <c:if test="${comment!='null'}">
                            	<div class="recommendation_message">
									
										<div class="note_icon">
										<span class="recommendation_note_icon"></span>
										</div>
										<div>
											<h3 class="blueName">${firstName} ${lastName} <bbbl:label key="lbl_recommended_quantity" language="${pageContext.request.locale.language}" /></h3>
											<p>${comment}</p>
										</div>
									
									
								</div>
                             </c:if>
									<div class="productFooter clearfix">
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
													</div>												
											</div>
							</div>
							</li>
						</ul> 
						

</dsp:page>