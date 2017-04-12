<dsp:page>
<script type="text/javascript">
	$(".recommendationCount").css("display", "none");
</script>
<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof var="skuId" param="skuId"/>
<dsp:getvalueof var="skuDisplayName" param="skuDisplayName" />
<dsp:getvalueof var="skuColor" param="skuColor" />
<dsp:getvalueof var="skuSize" param="skuSize" />
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
<dsp:getvalueof var="acceptedQuantity" param="acceptedQuantity" />
<dsp:getvalueof var="refNum" param="regItem.refNum" />
<dsp:getvalueof var="displayShipMsg" param="displayShipMsg" />
<c:set var="lblProductQuickView"><bbbl:label key='lbl_mng_regitem_quick_view' language="${pageContext.request.locale.language}" /></c:set>
<dsp:getvalueof var="ltlShipMethodDesc" param="ltlShipMethodDesc"/>
<dsp:getvalueof var="deliverySurcharge" param="deliverySurcharge"/>
<dsp:getvalueof var="assemblyFee" param="assemblyFee"/>
<dsp:getvalueof var="shipMethodUnsupported" param="shipMethodUnsupported"/>
<dsp:getvalueof var="ltlShipMethod" param="ltlShipMethod"/>
<dsp:getvalueof var="skuIncartPrice" param="skuIncartPrice"/>
<dsp:getvalueof var="skuIncartFlag" param="skuIncartFlag"/>
<jsp:useBean id="placeHolderMapServiceLevel" class="java.util.HashMap" scope="request"/>
<c:set var="lblReviewsCount"><bbbl:label key="lbl_reviews_count" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblReviewCount"><bbbl:label key="lbl_review_count" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblOfThe"><bbbl:label key="lbl_accessibility_of_the" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblAboutThe"><bbbl:label key="lbl_accessibility_about_the" language="${pageContext.request.locale.language}" /></c:set>
<c:set var="lblForThe"><bbbl:label key="lbl_accessibility_for_the" language="${pageContext.request.locale.language}" /></c:set>
	<%--BBBH-4958 | incart price on recommnedation tab --%>
	<c:choose>
		<c:when test="${skuIncartFlag}">
			<c:set var="skuListPrice">
			<dsp:valueof converter="currency" value="${skuIncartPrice}" />
		</c:set>
		</c:when>
		<c:otherwise>
			<c:if test='${not empty skuSalePrice && 0 ne skuSalePrice}'>
				<c:set var="skuListPrice">
					<dsp:valueof converter="currency" value="${skuSalePrice}" />
				</c:set>
			</c:if>
		</c:otherwise>
		
	</c:choose>
	<ul class="productDetailList giftViewProduct">
							<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
                               <dsp:param name="id" value="${productId}" />
                               <dsp:param name="itemDescriptorName" value="product" />
                               <dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
                               <dsp:oparam name="output">
                                  <dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
                               </dsp:oparam>
                             </dsp:droplet>
				  			<c:if test="${ltlItem && (ltlShipMethod eq null || ltlShipMethod eq '' || shipMethodUnsupported)}">
								<div class="ltlMessage"><bbbl:label key='lbl_rlp_ltl_registry_message' language="${pageContext.request.locale.language}" /></div>
							</c:if>
							<c:if test="${ltlItem && (ltlShipMethod ne null && ltlShipMethod ne '')}">
								<c:set var="ltlOpt" value="${ltlShipMethod}"></c:set>
							</c:if>
                             <li class="pending_recommendation grid_12 alpha omega qtyWrapper btngiftViewTopMargin giftViewProductChangeStoreWrapper addToCartGiftRegWrapper clearfix">


							<a class="lnkQVPopup" href="${contextPath}${finalUrl}?skuId=${skuId}&registryId=${registryId}&sopt=${ltlOpt}" data-pdp-qv-url="pdp_quick_view.jsp" data-skuId="${skuId}" data-registryId="${registryId}" data-sopt="${ltlOpt}" data-productId="${productId}" data-refNum="${refNum}" title="${skuDisplayName}&nbsp;${skuListPrice}">
								<img class="fl productImage noImageFound" src="${scene7Path}/${skuImage}" alt="${skuDisplayName}&nbsp;${skuListPrice}"
									title="${skuDisplayName}&nbsp;${skuListPrice}" height="146" width="146" />
							</a>

							<dsp:getvalueof var="reviews" value="${bazaarVoiceVo.totalReviewCount}" vartype="java.lang.Integer"/>
							<dsp:getvalueof var="ratings" value="${bazaarVoiceVo.averageOverallRating}" vartype="java.lang.Float"/>
							<dsp:getvalueof var="rating" value="${ratings * 10}" vartype="java.lang.Integer"/>
                            <dsp:getvalueof var="ratingsTitle" value="${bazaarVoiceVo.ratingsTitle}"/>


							<div class="productContainer grid_10 omega">
								<div class="productTab productContent prodInfo clearfix">
									<div class="prodName grid_4">
										<span class="blueName prodTitle">
										<a class="lnkQVPopup prodTitle" href="${contextPath}${finalUrl}?skuId=${skuId}&registryId=${registryId}&sopt=${ltlOpt}" data-pdp-qv-url="pdp_quick_view.jsp" data-skuId="${skuId}" data-registryId="${registryId}" data-sopt="${ltlOpt}" data-productId="${productId}" data-refNum="${refNum}" title="${skuDisplayName}&nbsp;${skuListPrice}">
											 ${skuDisplayName}
                                         </a>
                                     </span>
									<c:choose>
										<c:when test="${ratings ne null && ratings ne 0}">
											<span class="prodReviews clearfix metaFeedback">	<span class="ratingTxt ratingsReviews prodReviews<fmt:formatNumber value="${rating}" pattern="#0" />"><span class="ariaLabel">${ratingsTitle}</span></span>
												<c:if test="${reviews ne null && reviews ne '0' && reviews gt '1'}">
													<span class="prodReviewSpanFont reviewTxt">
													   <a href="${contextPath}${finalUrl}?showRatings=true" title="${productName}" role="link" aria-label="${reviews} ${lblReviewsCount} ${lblForThe} ${productName}">
														    <c:out value="${reviews }" />
														    <bbbl:label key="lbl_reviews_count" language="${pageContext.request.locale.language}" />
														</a>
													</span>
												</c:if>
												<c:if test="${reviews ne null && reviews ne '0' && reviews eq '1'}">
													<span class="prodReviewSpanFont reviewTxt">
													   <a href="${contextPath}${finalUrl}?showRatings=true" title="${productName}" role="link" aria-label="${reviews} ${lblReviewCount} ${lblForThe} ${productName}">
														    <c:out value="${reviews }" />
															<bbbl:label key="lbl_review_count" language="${pageContext.request.locale.language}" />
													   </a>
												   </span>
												</c:if>
											</span>
										</c:when>
										<c:otherwise>

											<span class="prodReviews metaFeedback">
												<c:set var="writeReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
											<span class="ratingTxt ratingsReviews"><span class="ariaLabel">${ratingsTitle}</span> </span><span class="writeReview reviewTxt"><a href="${contextPath}${finalUrl}?categoryId=${CategoryId}&writeReview=true" title="${writeReviewLink}" role="link" aria-label="${writeReviewLink} ${lblAboutThe} ${productName}">${writeReviewLink}</a></span></span>
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
										</dl> <span class="quickViewLink">
									<c:set var="quickViewURL">
				                     	/store/giftregistry/pdp_quick_view.jsp?skuId=${skuId}&registryId=${registryId}&productId=${productId}&sopt=${ltlOpt}&bbbModalDialog=true
				                    </c:set>
				                    <a href="${quickViewURL}" class="lnkQVPopup prodTitle"
				                    	data-pdp-qv-url="pdp_quick_view.jsp" data-skuId="${skuId}" data-registryId="${registryId}" data-sopt="${ltlOpt}"  data-productId="${productId}" data-refNum="${refNum}"
											title="${skuDisplayName}" role="link" aria-label="${lblProductQuickView} ${lblOfThe} ${skuDisplayName}">
										<span class="icon-fallback-text"> <span class="icon-add"
													aria-hidden="true"></span> <span class="icon-text"><bbbl:label
															key='lbl_mng_regitem_quick_view'
															language="${pageContext.request.locale.language}" /></span></span> <bbbl:label
													key='lbl_mng_regitem_quick_view'
													language="${pageContext.request.locale.language}" />
									</a>
									</span>
								</div>
									<c:choose>
									<c:when test="${ltlItem}">
													<div class="price grid_2 omega ltlPrice">
														<span class="columnHeader"> <bbbl:label key="lbl_mng_regitem_sortprice" language="${pageContext.request.locale.language}" /></span>	
														  
															<!--div class="totalLtl"> Total</div-->
															  <div class="toalpriceLtl">
															  	<dsp:droplet name="PriceDroplet">
																<dsp:param name="product" value="${productId}" />
																<dsp:param name="sku" value="${skuId}" />
																<dsp:oparam name="output">
																	<dsp:setvalue param="theListPrice" paramvalue="price" />
																	<dsp:getvalueof var="profileSalePriceList"
																		bean="Profile.salePriceList" />
																	<c:choose>
																		<c:when test="${not empty profileSalePriceList}">
																			<dsp:droplet name="PriceDroplet">
																				<dsp:param name="priceList" bean="Profile.salePriceList" />
																				<dsp:oparam name="output">
																					<dsp:getvalueof var="price" vartype="java.lang.Double"
																						param="theListPrice.listPrice" />
																					<dsp:getvalueof var="listPrice" vartype="java.lang.Double"
																						param="price.listPrice" />
																					<%--BBBH-4958 | incart price on recommnedation tab --%>	
																					<c:if test="${skuIncartFlag}">
																						<c:set var="listPrice" value="${skuIncartPrice}"/>
																					</c:if>	
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
																					<dsp:getvalueof var="price" vartype="java.lang.Double"
																						param="theListPrice.listPrice" />
																					<%--BBBH-4958 | incart price on recommnedation tab --%>	
																					<c:if test="${skuIncartFlag}">
																						<c:set var="price" value="${skuIncartPrice}"/>
																					</c:if>	
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
																			<dsp:getvalueof var="price" vartype="java.lang.Double"
																				param="theListPrice.listPrice" />
																			<%--BBBH-4958 | incart price on recommnedation tab --%>	
																			<c:if test="${skuIncartFlag}">
																				<c:set var="price" value="${skuIncartPrice}"/>
																			</c:if>
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
														</div>
															
															<c:choose>
																	<c:when test="${ltlShipMethod == null or shipMethodUnsupported}">
											                                        <div class="deliveryLtl"><bbbl:label key="lbl_cart_delivery_surcharge" language="<c:out param='${language}'/>"/>:</div>
											                                        <div class="deliverypriceLtl"><span class="deliverypriceLtlClass">TBD</span><img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" /></div>
											                         </c:when>
																	<c:otherwise>
																		 <c:choose>
																			 <c:when test="${ltlShipMethod== 'LWA' }">
																			   <div class="deliveryLtl">  Incl White Glove <span>With Assembly:</span></div>
																			   <div class="deliverypriceLtl"><span class="deliverypriceLtlClass"><dsp:valueof value="${deliverySurcharge+assemblyFee}" number="0.00" converter="currency"/></span>
																			   <img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" /></div>
																			</c:when>
																			<c:otherwise>
																			<div class="deliveryLtl">
																					 Incl ${ltlShipMethodDesc}:
												                            </div>
												                            <div class="deliverypriceLtl"><span class="deliverypriceLtlClass">       <c:if test="${deliverySurcharge eq 0.0}"> FREE</c:if>
												                                                                 <c:if test="${deliverySurcharge gt 0.0}"><dsp:valueof value="${deliverySurcharge}" number="0.00" converter="currency"/></c:if>
																				</span> <img src="${imagePath}/_assets/global/images/LTL/quesMark.png" height="10" width="10" alt="${productName}" class="dslInfo" />
																			</div>
																				</c:otherwise>
																		</c:choose>
																	</c:otherwise>
																 </c:choose>
															<c:if test="${ltlShipMethod != null && ltlShipMethod != '' && !shipMethodUnsupported}">	 
															  <div class="itemLtl">Item Price
																<span class="itempriceLtl">
																	<%--BBBH-4958 | incart price on recommnedation tab --%>
																	<c:choose>
																	<c:when test="${skuIncartFlag}">
																		<dsp:include page="/global/gadgets/formattedPrice.jsp">
																			<dsp:param name="price" value="${skuIncartPrice }"/>
																		</dsp:include>
																		<input type="hidden" value="${skuIncartPrice}" class="itemPrice"/>
																	</c:when>
																	<c:otherwise>
																	<dsp:include page="registry_items_guest_category_frag.jsp">
																		<dsp:param name="productId" value="${productId}"/>
																		<dsp:param name="skuID" value="${skuId}"/>												
																	</dsp:include>	
																	</c:otherwise>
																	</c:choose>
																</span>
															</div>
															</c:if>														
														</div> 
												</c:when>
												<c:otherwise>
												<div class="price grid_2 omega"><span class="columnHeader">
													<bbbl:label key="lbl_mng_regitem_sortprice" language="${pageContext.request.locale.language}" /></span>
											        ${skuListPrice} <br/> ${displayShipMsg}
												</div>
												</c:otherwise>
									</c:choose>
									
							         <div class="requested grid_2 omega">
										<span class="columnHeader"><bbbl:label key="lbl_recommended_quantity" language="${pageContext.request.locale.language}" /></span>
										<span>${recommendedQuantity}</span>
									</div>
									<div class="requested grid_2 omega">
										<span class="columnHeader"><bbbl:label key="lbl_requested_quantity" language="${pageContext.request.locale.language}" /></span>
										<span>${acceptedQuantity}</span>
									</div>


								</div>
								<div class="productFooter clearfix">								
										<div class="noMar clearfix freeShipContainer prodAttribWrapper">
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
													<dsp:param name="array" param="skuAttributes" />
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
							</div>
							</li>
						</ul>
</dsp:page>