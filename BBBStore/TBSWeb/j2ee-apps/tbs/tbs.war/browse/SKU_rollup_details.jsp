<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/SKURollUpListDroplet"/>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/com/bbb/commerce/droplet/TBSItemExclusionDroplet"/>
<dsp:getvalueof var="appid" bean="Site.id" />
<c:set var="scene7Path">
    <bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
</c:set>
<c:set var="imagePath">
    <bbbc:config key="image_host" configName="ThirdPartyURLs" />
</c:set>
<c:set var="AttributePDPTOP">
	<bbbl:label key='lbl_pdp_attributes_top' language="${pageContext.request.locale.language}" />
</c:set>
<c:set var="AttributePDPMiddle">
	<bbbl:label key='lbl_pdp_attributes_middle' language="${pageContext.request.locale.language}" />
</c:set>
<c:set var="AttributePDPPrice">
	<bbbl:label key='lbl_pdp_attributes_price' language="${pageContext.request.locale.language}" />
</c:set>
<c:set var="AttributePDPCollection">
	<bbbl:label key='lbl_pdp_attributes_collection' language="${pageContext.request.locale.language}" />
</c:set>
<c:set var="LtlTruckDeliveryOption">
	<bbbl:label key='ltl_truck_delivery_options' language="${pageContext.request.locale.language}" />
</c:set>
<c:set var="SurchargeMayApply">
	<bbbl:label key='ltl_delivery_surcharge_may_apply' language="${pageContext.request.locale.language}" />
</c:set>
<dsp:getvalueof var="prodColor" param="prodColor"/>
<dsp:getvalueof var="prodSize" param="prodSize"/>
<json:object>
		<dsp:droplet name="SKURollUpListDroplet">
			<dsp:param name="id" param="productId" />
			<dsp:param name="prodColor" param="prodColor"/>
			<dsp:param name="prodSize" param="prodSize"/>
			<dsp:param name="prodFinish" param="prodFinish"/>
			<dsp:param name="registryId" param="registryId"/>
					<dsp:oparam name="output">
						<dsp:getvalueof var="skuId" param="SKUDetailsVO.skuId"/>
						<dsp:getvalueof var="displayName" param="SKUDetailsVO.displayName"/>
						<dsp:getvalueof var="description" param="SKUDetailsVO.description"/>
						<dsp:getvalueof var="longDescription" param="SKUDetailsVO.longDescription"/>
						<dsp:getvalueof var="skuAttributes" param="SKUDetailsVO.skuAttributes"/>
						<dsp:getvalueof var="oos" param="inStock"/>
						<dsp:getvalueof var="image" param="SKUDetailsVO.skuImages.largeImage"/>
						<dsp:getvalueof var="smallImage" param="SKUDetailsVO.skuImages.smallImage"/>
                        <dsp:getvalueof var="thumbnailImage" param="SKUDetailsVO.skuImages.thumbnailImage"/>
						<dsp:getvalueof var="emailOn" param="SKUDetailsVO.emailStockAlertsEnabled"/>
						<dsp:getvalueof var="zoomAvailable" param="SKUDetailsVO.zoomAvailable"/>
						<dsp:getvalueof var="ltlFlag" param="SKUDetailsVO.ltlItem"/>
						<dsp:getvalueof var="isLTLProduct" param="ltlProduct"/>
						<dsp:getvalueof var="ltlShippingMethods" param="shipMethodVOList"/>
						<dsp:getvalueof var="upc" param="SKUDetailsVO.upc"/>
						<dsp:getvalueof var="vdcSku" param="SKUDetailsVO.vdcSku"/>
						<dsp:getvalueof var="isCustomizationRequired" param="SKUDetailsVO.customizableRequired"/>
						<dsp:getvalueof var="personalizationType" param="SKUDetailsVO.personalizationType"/>
						<dsp:getvalueof var="customizationOffered" param="SKUDetailsVO.customizationOffered"/>
						<dsp:getvalueof var="customizableCodes" param="SKUDetailsVO.customizableCodes"/>
						<dsp:getvalueof var="isItemInWishlist" param="isItemInWishlist"/>	
						<c:choose>
							<c:when test="${requestScope.personalizedSku ne null }">
								<c:set var="itemAlreadyPersonalized" value="true" />
								<c:set var="personalizedItem" value="${personalizedSku }" />
							</c:when>
							<c:otherwise>
								<c:set var="itemAlreadyPersonalized" value="false" />
							</c:otherwise>
						</c:choose>
								<c:if test="${itemAlreadyPersonalized }">
									<c:forEach var="element" items="${personalizedSku.eximResponse.images[0].previews }">
										<c:choose>
											<c:when test="${element.size eq 'large'}">
												<c:set var="personalizedLargeImage" value="${element.url}" />
											</c:when>
											<c:when test="${element.size eq 'medium'}">
												<c:set var="personalizedMediumImage" value="${element.url}" />
											</c:when>
											<c:when test="${element.size eq 'x-small'}">
												<c:set var="personalizedSmallImage" value="${element.url}" />
											</c:when>
										</c:choose>
										</c:forEach>
									</c:if>					
						<c:if test="${skuAttributes != null }">
						<json:property name="prodAttribs" escapeXml="false">
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param param="SKUDetailsVO.skuAttributes" name="array" />
									<dsp:oparam name="output">
									<dsp:getvalueof var="placeHolderTop" param="key"/>
									<c:if test="${(placeHolderTop != null) && (placeHolderTop eq AttributePDPTOP)}">
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param param="element" name="array" />
									<dsp:param name="sortProperties" value="+priority"/>
										<dsp:oparam name="outputStart">
											<div>
										</dsp:oparam>
										<dsp:oparam name="output">
                                                <div>
											<dsp:getvalueof var="attributeDescripTop" param="element.attributeDescrip"/>
											<dsp:getvalueof var="imageURLTop" param="element.imageURL"/>
											<dsp:getvalueof var="actionURLTop" param="element.actionURL"/>
												<c:choose>
													     <c:when test="${null ne attributeDescripTop}">
													            <c:choose>
													                   <c:when test="${null ne imageURLTop}">
													                        	<img src="${imageURLTop}" alt="${attributeDescripTop}" /><span>${attributeDescripTop}</span>
													                   </c:when>
													                   <c:otherwise>
													                         <c:choose>
													                                 <c:when test="${null ne actionURLTop}">
													                                      <a href="${actionURLTop}" class="newOrPopup"><span>${attributeDescripTop}</span></a>
													                                 </c:when>
													                                 <c:otherwise>
													                                       <span>${attributeDescripTop}</span>
													                                 </c:otherwise>
													                          </c:choose>
													                   </c:otherwise>
													             </c:choose>
													     </c:when>
													     <c:otherwise>
													           <c:if test="${null ne imageURLTop}">
													                   <c:choose>
													                          <c:when test="${null ne actionURLTop}">
													                                	<a href="${actionURLTop}" class="newOrPopup"><img src="${imageURLTop}" alt="" /></a>
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
										<dsp:oparam name="outputEnd">
											</div>
										</dsp:oparam>
									</dsp:droplet>
									</c:if>
									</dsp:oparam>
							</dsp:droplet>
							</json:property>
							<json:property name="moreProdAttribs" escapeXml="false">
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param param="SKUDetailsVO.skuAttributes" name="array" />
									<dsp:oparam name="output">
									<dsp:getvalueof var="placeHolderMiddle" param="key"/>
									<c:if test="${(not empty placeHolderMiddle) && (placeHolderMiddle eq AttributePDPMiddle)}">
											<dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param param="element" name="array" />
												<dsp:param name="sortProperties" value="+priority"/>
													<dsp:oparam name="outputStart">
														<div>
													</dsp:oparam>
													<dsp:oparam name="output">
                                                    <div>
													<dsp:getvalueof var="placeHolderMiddle" param="element.placeHolder"/>
													<dsp:getvalueof var="attributeDescripMiddle" param="element.attributeDescrip"/>
													<dsp:getvalueof var="imageURLMiddle" param="element.imageURL"/>
													<dsp:getvalueof var="actionURLMiddle" param="element.actionURL"/>
																	<c:choose>
																		 <c:when test="${null ne attributeDescripMiddle}">
																				<c:choose>
																					   <c:when test="${null ne imageURLMiddle}">
																							 <img src="${imageURLMiddle}" alt="${attributeDescripMiddle}" /><span>${attributeDescripMiddle}</span>
																					   </c:when>
																					   <c:otherwise>
																							 <c:choose>
																									 <c:when test="${null ne actionURLMiddle}">
																										   <a href="${actionURLMiddle}" class="newOrPopup"><span>${attributeDescripMiddle}</span></a>
																									 </c:when>
																									 <c:otherwise>
																										   <span>${attributeDescripMiddle}</span>
																									 </c:otherwise>
																							  </c:choose>
																					   </c:otherwise>
																				 </c:choose>
																		 </c:when>
																		 <c:otherwise>
																			   <c:if test="${null ne imageURLMiddle}">
																					   <c:choose>
																							  <c:when test="${null ne actionURLMiddle}">
																									<a href="${actionURLMiddle}" class="newOrPopup"><img src="${imageURLMiddle}" alt="" /></a>
																							  </c:when>
																							  <c:otherwise>
																									<img src="${imageURLMiddle}" alt="" />
																							  </c:otherwise>
																					   </c:choose>
																				</c:if>
																		 </c:otherwise>
																	</c:choose>
                                                                    </div>
													</dsp:oparam>
													<dsp:oparam name="outputEnd">
														</div>
													</dsp:oparam>
											</dsp:droplet>
										</c:if>
									</dsp:oparam>
							</dsp:droplet>
							</json:property>
						</c:if>
						
						<c:if test="${skuId != null }">
						<json:property name="skuId"><dsp:valueof param="SKUDetailsVO.skuId"/></json:property>
						<json:property name="productId"><dsp:valueof param="productId"/></json:property>
						<json:property name="vdcSku"><dsp:valueof value="${vdcSku}"/></json:property>
						</c:if>
						<c:if test="${prodColor != null }">
						<json:property name="prodColor" escapeXml="false"><dsp:valueof value="${prodColor}" valueishtml="true"/></json:property>
						</c:if>
						<c:if test="${prodSize != null }">
						<json:property name="prodSize" escapeXml="false"><dsp:valueof value="${prodSize}"/></json:property>
						</c:if>
						<json:property name="isItemInWishlist">${isItemInWishlist}</json:property>
						<c:if test="${displayName != null }">
						<json:property name="productDetails" escapeXml="false"><dsp:valueof param="SKUDetailsVO.displayName" valueishtml="true"/></json:property>
						</c:if>
						<c:if test="${description != null }">
						<json:property name="productDesc" escapeXml="false"><dsp:valueof param="SKUDetailsVO.description" valueishtml="true"/>&nbsp;<a href="#" class="readMore"><bbbl:label key='lbl_see_full_details' language="${pageContext.request.locale.language}" /></a></json:property>
						</c:if>
						<c:if test="${emailOn != null }">
						<json:property name="emailOn" escapeXml="false"><dsp:valueof param="SKUDetailsVO.emailStockAlertsEnabled" valueishtml="true"/></json:property>
						</c:if>
						<c:if test="${upc != null }">
						<json:property name="upc"><dsp:valueof param="SKUDetailsVO.upc"/></json:property>
						</c:if>						
						<c:if test="${isCustomizationRequired !=null }">
						<json:property name="isCustomizationRequired">${isCustomizationRequired}</json:property>
						</c:if>
						<c:if test="${not empty personalizationType}">
						<json:property name="personalizationType">${personalizationType}</json:property>
						</c:if>
						<c:if test="${not empty customizationOffered}">
						<json:property name="customizationOffered">${customizationOffered}</json:property>
						</c:if>
						<c:if test="${not empty customizableCodes}">
						<json:property name="customizableCodes">${customizableCodes}</json:property>
						</c:if>
						<!-- BBBH-2889 - adding new json property for handling personlization for incart eligible skus -->
						<json:property name="inCartFlag"><dsp:valueof param="SKUDetailsVO.inCartFlag"/></json:property>
						<c:set var="price">
						<dsp:include page="/browse/product_details_price.jsp">	
							<dsp:param name="product" param="productId"/>
							<dsp:param name="sku" param="SKUDetailsVO.skuId"/>
							<dsp:param name="inCartFlag" param="SKUDetailsVO.inCartFlag" />
						</dsp:include>
						</c:set>

						<c:if test="${price != null }">
						<dsp:include page="/browse/product_details_price_json.jsp">	
							<dsp:param name="product" param="productId"/>
							<dsp:param name="sku" param="SKUDetailsVO.skuId"/>
							<dsp:param name="inCartFlag" param="SKUDetailsVO.inCartFlag" />
						</dsp:include>
						</c:if>
						
						<c:if test="${oos != null }">
						<json:property name="inStock" value="${oos}"></json:property>
						<json:property name="availabilityMsg" escapeXml="false">
							<c:choose>
                             <c:when test="${oos==false}">
                             	<link itemprop="availability" href="//schema.org/OutOfStock"/><bbbl:label key='lbl_pdp_out_of_stock' language="${pageContext.request.locale.language}" />
                             </c:when>
                             <c:otherwise>
                             	<link itemprop="availability" href="//schema.org/InStock"/><bbbl:label key='lbl_pdp_in_stock' language="${pageContext.request.locale.language}" />
                             </c:otherwise>
                             </c:choose>
						</json:property>
						</c:if>
						
						<c:choose>
						<c:when test="${image != null }">
						<json:object name="imageData">
								<json:property name="imgURL">${scene7Path}/${image}</json:property>
								<json:property name="imgURLSmall">${scene7Path}/${smallImage}</json:property>
                                <json:property name="imgURLThumb">${scene7Path}/${thumbnailImage}</json:property>
								<json:property name="s7ImgsetID">${image}</json:property>
								<json:property name="zoomFlag">${zoomAvailable}</json:property>
						</json:object>
						</c:when>
						<c:otherwise>
						<json:object name="imageData">
							<c:choose>
							<c:when test="${imagePath != 'null' }">
								<json:property name="imgURL">${imagePath}/_assets/global/images/no_image_available.jpg</json:property>
                                <json:property name="imgURLSmall">${imagePath}/_assets/global/images/no_image_available.jpg</json:property>
                                <json:property name="imgURLThumb">${imagePath}/_assets/global/images/no_image_available.jpg</json:property>
							</c:when>
							<c:otherwise>
								<json:property name="imgURL">/_assets/global/images/no_image_available.jpg</json:property>
                                <json:property name="imgURLSmall">/_assets/global/images/no_image_available.jpg</json:property>
                                <json:property name="imgURLThumb">/_assets/global/images/no_image_available.jpg</json:property>
							</c:otherwise>
							</c:choose>
						</json:object>
						</c:otherwise>
						</c:choose>
						
						<json:property name="rebateContainer" escapeXml="false">
							<dsp:getvalueof var="hasRebate" param="SKUDetailsVO.hasRebate"/>
							<dsp:getvalueof var="chkEligibleRebates" param="SKUDetailsVO.eligibleRebates"/>
							<c:set var="rebatesOn" value="${false}" />
							<c:if test="${skuAttributes != null }">
							<c:if test="${not empty hasRebate && hasRebate}">
								<c:if test="${(null != chkEligibleRebates) && (fn:length(chkEligibleRebates) == 1 )}"> 
									<c:set var="rebatesOn" value="${true}" />
								</c:if>
							</c:if>
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param param="SKUDetailsVO.skuAttributes" name="array" />
										<dsp:oparam name="output">
										<dsp:getvalueof var="placeHolderPrice" param="key"/>
										<c:if test="${(placeHolderPrice != null) && (placeHolderPrice eq AttributePDPPrice)}">
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
												<dsp:getvalueof var="attributeDescripPrice" param="element.attributeDescrip"/>
												<dsp:getvalueof var="imageURLPrice" param="element.imageURL"/>
												<dsp:getvalueof var="actionURLPrice" param="element.actionURL"/>
													<c:choose>
														     <c:when test="${null ne attributeDescripPrice}">
														            <c:choose>
														                   <c:when test="${null ne imageURLPrice}">
														                        	 <span class="attribs ${sep}"><img src="${imageURLPrice}" alt="${attributeDescripPrice}" /><span>${attributeDescripPrice}</span></span>
														                   </c:when>
														                   <c:otherwise>
														                         <c:choose>
														                                 <c:when test="${null ne actionURLPrice}">
														                                      <span class="attribs ${sep}"><a href="${actionURLPrice}" class="newOrPopup"><span>${attributeDescripPrice}</span></a></span>
														                                 </c:when>
														                                 <c:otherwise>
														                                      <span class="attribs ${sep}">${attributeDescripPrice}</span>
														                                 </c:otherwise>
														                          </c:choose>
														                   </c:otherwise>
														             </c:choose>
														     </c:when>
														     <c:otherwise>
														           <c:if test="${null ne imageURLPrice}">
														                   <c:choose>
														                          <c:when test="${null ne actionURLPrice}">
														                                	<span class="attribs ${sep}"><a href="${actionURLPrice}" class="newOrPopup"><img src="${imageURLPrice}" alt="" /></a></span>
														                          </c:when>
														                          <c:otherwise>
														                                	<span class="attribs ${sep}"><img src="${imageURLPrice}" alt="" /></span>
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
							</c:if>
							
							<c:if test="${not empty hasRebate && hasRebate}">
							<dsp:getvalueof var="eligibleRebates" param="SKUDetailsVO.eligibleRebates"/>
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" value="${eligibleRebates}"/>
								<dsp:oparam name="output">
									<dsp:getvalueof var="chkCount1" param="count"/>
									<dsp:getvalueof var="chkSize1" param="size"/>
									<c:set var="sep1" value="seperator" />
									<c:if test="${chkCount1 == chkSize1}">
										<c:set var="sep1" value="" />
									</c:if>
									<dsp:getvalueof var="rebate" param="element"/>
									<span class="attribs ${sep1}" ><a href="${rebate.rebateURL}" class="links" target="_blank" title="Rebate"><c:out value="${rebate.rebateDescription}" escapeXml="false"/></a></span>
								</dsp:oparam>
							</dsp:droplet>
							</c:if>
						</json:property>
						
						<json:property name="collectionRebateContainer" escapeXml="false">
							<dsp:getvalueof var="hasRebate" param="SKUDetailsVO.hasRebate"/>
							
							<dsp:getvalueof var="chkEligibleRebates" param="SKUDetailsVO.eligibleRebates"/>
							<c:set var="rebatesOn" value="${false}" />
							<c:if test="${skuAttributes != null }">
							<c:if test="${not empty hasRebate && hasRebate}">
								<c:if test="${(null != chkEligibleRebates) && (fn:length(chkEligibleRebates) == 1 )}"> 
									<c:set var="rebatesOn" value="${true}" />
								</c:if>
							</c:if>
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param param="SKUDetailsVO.skuAttributes" name="array" />
										<dsp:oparam name="output">
										<dsp:getvalueof var="placeHolderPrice" param="key"/>
										<c:if test="${(placeHolderPrice != null) && (placeHolderPrice eq AttributePDPCollection)}">
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
												<dsp:getvalueof var="attributeDescripPrice" param="element.attributeDescrip"/>
												<dsp:getvalueof var="imageURLPrice" param="element.imageURL"/>
												<dsp:getvalueof var="actionURLPrice" param="element.actionURL"/>
													<c:choose>
														     <c:when test="${null ne attributeDescripPrice}">
														            <c:choose>
														                   <c:when test="${null ne imageURLPrice}">
														                        	 <span class="attribs ${sep}"><img src="${imageURLPrice}" alt="${attributeDescripPrice}" /><span>${attributeDescripPrice}</span></span>
														                   </c:when>
														                   <c:otherwise>
														                         <c:choose>
														                                 <c:when test="${null ne actionURLPrice}">
														                                      <span class="attribs ${sep}"><a href="${actionURLPrice}" class="newOrPopup"><span>${attributeDescripPrice}</span></a></span>
														                                 </c:when>
														                                 <c:otherwise>
														                                      <span class="attribs ${sep}">${attributeDescripPrice}</span>
														                                 </c:otherwise>
														                          </c:choose>
														                   </c:otherwise>
														             </c:choose>
														     </c:when>
														     <c:otherwise>
														           <c:if test="${null ne imageURLPrice}">
														                   <c:choose>
														                          <c:when test="${null ne actionURLPrice}">
														                                	<span class="attribs ${sep}"><a href="${actionURLPrice}" class="newOrPopup"><img src="${imageURLPrice}" alt="" /></a></span>
														                          </c:when>
														                          <c:otherwise>
														                                	<span class="attribs ${sep}"><img src="${imageURLPrice}" alt="" /></span>
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
							</c:if>
							
							<c:if test="${not empty hasRebate && hasRebate}">
							<dsp:getvalueof var="eligibleRebates" param="SKUDetailsVO.eligibleRebates"/>
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" value="${eligibleRebates}"/>
								<dsp:oparam name="output">
									<dsp:getvalueof var="chkCount1" param="count"/>
									<dsp:getvalueof var="chkSize1" param="size"/>
									<c:set var="sep1" value="seperator" />
									<c:if test="${chkCount1 == chkSize1}">
										<c:set var="sep1" value="" />
									</c:if>
									<dsp:getvalueof var="rebate" param="element"/>
									<span class="attribs ${sep1}" ><a href="${rebate.rebateURL}" class="links" target="_blank" title="Rebate"><c:out value="${rebate.rebateDescription}" escapeXml="false"/></a></span>
								</dsp:oparam>
							</dsp:droplet>
							</c:if>
						</json:property>
						<json:property  name="exclusions">
							<dsp:droplet name="TBSItemExclusionDroplet">
							<dsp:param name="siteId" value="${appid}"/>
							<dsp:param name="skuId" value="${skuId}"/>
							<dsp:oparam name="output">
								<dsp:getvalueof var="isItemExcluded" param="validItemExcludedSku"/>
								<dsp:getvalueof var="caDisabled" param="caDisabled"/>
								<dsp:getvalueof var="reasonCode" param="reasonCode"/>
							</dsp:oparam>
							</dsp:droplet>
							<dsp:valueof value="${isItemExcluded}"/>
						</json:property>
						<json:property  name="shipMsgFlag">
							<dsp:valueof param="SKUDetailsVO.shipMsgFlag" />
						</json:property>
						<json:property  name="ltlflag">
							<dsp:valueof value="${ltlFlag}" />
						</json:property>
						<json:property  name="ltlTruckDeliveryOption">
							<dsp:valueof value="${LtlTruckDeliveryOption}" />
						</json:property>
						<json:property name="itemAlreadyPersonalized">
							${itemAlreadyPersonalized }
						</json:property>
						<c:if test="${itemAlreadyPersonalized }">
						<json:object name="personalizedItem">
							<json:property name="cost-adder">
								${personalizedSku.eximResponse.costPriceAdder }
							</json:property>
							<json:property name="customization_service">
								${personalizedSku.eximResponse.customizationService }
							</json:property>
							<json:property name="customization_status">
								${personalizedSku.eximResponse.customizationStatus }
							</json:property>
							<json:property name="description">
								${personalizedSku.eximResponse.description }
							</json:property>
							<json:property name="imageURL">
								${personalizedLargeImage }
							</json:property>
							<json:property name="imageURL_thumb">
								${personalizedMediumImage }
							</json:property>
							<json:property name="mobileURL">
								${personalizedLargeImage }
							</json:property>
							<json:property name="mobileURL_thumb">
								${personalizedSmallImage }
							</json:property>
							<json:property name="namedrop">
								${personalizedSku.eximResponse.namedrop }
							</json:property>
							<json:property name="price_adder">
								${personalizedSku.eximResponse.retailPriceAdder }
							</json:property>
							<json:property name="quantity">
								${quantity }
							</json:property>
							<json:property name="refnum">
								${personalizedSku.eximResponse.refnum }
							</json:property>
							<json:property name="sku">
								${personalizedSku.eximResponse.vendorSku }
							</json:property>
							<json:property name="eximItemPrice">
								${eximItemPrice }
							</json:property>
							<json:property name="eximPersonalizedPrice">
								${eximPersonalizedPrice }
							</json:property>
							<json:property name="shopperCurrency">
								${shopperCurrency }
							</json:property>	
						</json:object>
						</c:if>
						<json:object  name="ltlShippingMethods" escapeXml="false">
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param name="array" value="${ltlShippingMethods}" />
								<dsp:oparam name="output">
									<dsp:getvalueof var="shipMethodId" param="element.shipMethodId"/>
									<dsp:getvalueof var="shipMethodDescription" param="element.shipMethodDescription"/>
									<dsp:getvalueof var="deliverySurchargeval" param="element.deliverySurcharge"/>
									<c:if test="${deliverySurchargeval gt 0}">
										<c:set var="deliverySurcharge"><dsp:valueof  converter="currency" param="element.deliverySurcharge" /></c:set>
									</c:if>
									<c:if test="${deliverySurchargeval eq 0}">
										<c:set var="deliverySurcharge">Free</c:set>
									</c:if>
									<json:object name="ltlShippingMethods${shipMethodId}">
									<json:property  name="shipMethodId">
										<dsp:valueof value="${shipMethodId}" />
									</json:property>
									<json:property  name="shipMethodDescription">
										<dsp:valueof value="${shipMethodDescription}" />
									</json:property>
									<json:property  name="deliverySurcharge">
										<dsp:valueof value="${deliverySurcharge}" />
									</json:property>
									</json:object>
					</dsp:oparam>
		</dsp:droplet>
</json:object>
						<c:if test="${not ltlFlag && not vdcSku}">
							<json:property name="nearByStoreInfo" escapeXml="false">
							  <dsp:include src="/tbs/browse/frag/Near_by_store_info.jsp" flush="true">
									<dsp:param name="skuId" value="${skuId}"/>
								</dsp:include>
							</json:property>	
						</c:if>
						<c:if test="${vdcSku eq true}">
						<json:property name="vdcShipMsg" escapeXml="false">
						<dsp:droplet name="/com/bbb/commerce/browse/VDCShippingMessagingDroplet">
							<dsp:param name="skuId" value="${skuId}"/>
							<dsp:param name="fromShippingPage" value="true"/>
							<dsp:oparam name="output">
								<dsp:getvalueof var="vdcDelTime" param="vdcShipMsg"/>
							</dsp:oparam>
							<dsp:oparam name="error">
							</dsp:oparam>
						</dsp:droplet>
						<c:if test="${not empty vdcDelTime }">
							<a href="/tbs/browse/dynamicStaticContent.jsp?pageName=ItemShippedDirectlyFromVendor&isVdcSku=${vdcSku}&skuId=${skuId}" class="pdp-sprite online popupShipping <c:if test="${timeframe eq '0004'}"> no-stock</c:if>" <c:if test="${timeframe eq 0004}">disabled='disabled'</c:if>>
							<bbbl:label key="lbl_vdc_arrives_in" language="${pageContext.request.locale.language}" />  ${vdcDelTime}
							</a>
						</c:if>
						</json:property>
						</c:if>
					</dsp:oparam>
		</dsp:droplet>
</json:object>

</dsp:page>