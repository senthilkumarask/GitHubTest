<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProdToutDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ExitemIdDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/atg/targeting/TargetingRandom"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet"/>
	<dsp:importbean bean="/atg/multisite/Site"/>	
	<dsp:importbean var="SystemErrorInfo" bean="/com/bbb/utils/SystemErrorInfo"/>	
<bbb:pageContainer pageVariation="${pageVariation}">
<jsp:attribute name="section">browse</jsp:attribute>
<jsp:attribute name="pageWrapper">productDetails ${pageWrapperClass} useMapQuest useStoreLocator printerFriendly</jsp:attribute>
<jsp:body>
<c:set var="SchemeName" value="pdp_cav"/>
<dsp:getvalueof var="appid" bean="Site.id" />
<div id="pageWrapper" class="${pageWrapper}">
<c:set var="CertonaContext" value="" scope="request"/>
<c:set var="AttributePDPTOP">
	<bbbl:label key='lbl_pdp_attributes_top' language="${pageContext.request.locale.language}" />
</c:set>
<c:set var="AttributePDPMiddle">
	<bbbl:label key='lbl_pdp_attributes_middle' language="${pageContext.request.locale.language}" />
</c:set>
<c:set var="AttributePDPPrice">
	<bbbl:label key='lbl_pdp_attributes_price' language="${pageContext.request.locale.language}" />
</c:set>
<dsp:droplet name="ProductDetailDroplet">
			<dsp:param name="id" param="productId" />
				<dsp:param name="siteId" param="siteId"/>
				<dsp:param name="skuId" param="skuId"/>
					<dsp:oparam name="output">
		<div id="content" class="container_12 clearfix">
		<dsp:getvalueof var="parentProductId" param="productId" />	
		<c:set var="CertonaContext" scope="request">${parentProductId};</c:set>
			<div class="grid_12 marTop_10 <c:if test='${param.npc == "on"}'> hasProdNextCollection </c:if>">
				<div class="grid_5 alpha zoomProduct">
					<%-- this is only shown on products with collections (triggered from dorm room collection) --%>
					<dsp:getvalueof var="largeImage" param="productVO.productImages.largeImage"/>
					
						<c:if test="${SceneSevenOn}">			
						<c:choose>
						<c:when test="${empty largeImage}">
							<img src="${imagePath}/_assets/global/images/no_image_available.jpg" class="mainProductImage" height="380" width="380" alt="${productName}" />
						</c:when>
						<c:otherwise>
							<img src="${scene7Path}/${largeImage}" class="mainProductImage noImageFound" height="380" width="380" alt="${productName}" />
						</c:otherwise>
						</c:choose>
						</c:if>
					
				</div>

				<div class="grid_7 omega productDetails" itemscope itemtype="http://schema.org/Product">
					
						<div class="registryDataItemsWrap">
						<c:set var="attributeCount" value="1" />
						<dsp:getvalueof var="skuVO" param="pSKUDetailVO" />
						<c:choose>
						<c:when test="${not empty skuVO}">
							<dsp:getvalueof var="attribs" param="pSKUDetailVO.skuAttributes"/>
						</c:when>
						<c:otherwise>
							<dsp:getvalueof var="attribs" param="productVO.attributesList"/>
						</c:otherwise>
						</c:choose>
							<div class="prodAttribs prodAttribWrapper">
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
												<div>
																<c:choose>
																     <c:when test="${null ne attributeDescripTop}">
																            <c:choose>
																                   <c:when test="${null ne imageURLTop}">
																                         <img src="${imageURLTop}" alt="" /><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span>
																                   </c:when>
																                   <c:otherwise>
																                         <c:choose>
																                                 <c:when test="${null ne actionURLTop}">
																                                       <a href="${actionURLTop}" class="newOrPopup"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a>
																                                 </c:when>
																                                 <c:otherwise>
																                                       <span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span>
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
										</dsp:droplet>
									</c:if>
								</dsp:oparam>
								</dsp:droplet>
							</div>
							
							<h2 class="productDetails" itemprop="name"><dsp:valueof param="productVO.name" valueishtml="true"/></h2>
							<div class="productDesc bulletsReset" itemprop="description">
								<p class="desclight"><dsp:valueof param="productVO.longDescription" valueishtml="true"/></p>
							
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param value="${attribs}" name="array" />
									<dsp:oparam name="output">
									<dsp:getvalueof var="placeHolderMiddle" param="key"/>
										<c:if test="${(not empty placeHolderMiddle) && (placeHolderMiddle eq AttributePDPMiddle)}">
											<dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param param="element" name="array" />
												<dsp:param name="sortProperties" value="+priority"/>
													<dsp:oparam name="output">
													<dsp:getvalueof var="placeHolderMiddle" param="element.placeHolder"/>
													<dsp:getvalueof var="attributeDescripMiddle" param="element.attributeDescrip"/>
													<dsp:getvalueof var="imageURLMiddle" param="element.imageURL"/>
													<dsp:getvalueof var="actionURLMiddle" param="element.actionURL"/>
                                                        <c:if test="${showMoreAttibsDiv == true}">
                                                            <div class="productLinks moreProdAttribs">
                                                            <c:set var="showMoreAttibsDiv" value="${false}" />
                                                        </c:if>
													<div>
																	<c:choose>
																		 <c:when test="${null ne attributeDescripMiddle}">
																				<c:choose>
																					   <c:when test="${null ne imageURLMiddle}">
																							 <img src="${imageURLMiddle}" alt="" /><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span>
																					   </c:when>
																					   <c:otherwise>
																							 <c:choose>
																									 <c:when test="${null ne actionURLMiddle}">
																										   <a href="${actionURLMiddle}" class="newOrPopup"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a>
																									 </c:when>
																									 <c:otherwise>
																										   <span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span>
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
											</dsp:droplet>
										</c:if>
									</dsp:oparam>
									</dsp:droplet>
								
									<div class="appendSKUInfo">
										<dsp:getvalueof var="childSKUId" param="pSKUDetailVO.skuId" />
										<c:if test="${not empty childSKUId}">
                                        	<p class="smalltext prodSKU"><bbbl:label key='lbl_pdp_sku_id' language="${pageContext.request.locale.language}" /> ${childSKUId}</p>
                                    	</c:if>
                                    <%-- R 2.2.1 || Harmon START --%>
                                    	<dsp:getvalueof var="harmonLongDescription" param="productVO.harmonLongDescription"/>
                                    	<c:if test="${not empty harmonLongDescription}">
                                    		<dsp:valueof param="productVO.harmonLongDescription" valueishtml="true"/>
                                    	</c:if>
                                    <%-- R 2.2.1 || Harmon END   --%>
                                    </div>
                                   
									<c:if test="${showMoreAttibsDiv == false}">
                                    </div>
                                    </c:if>
									
							<dsp:getvalueof var="colorParam" param="color"/>
						<dsp:getvalueof var="sizeParam" param="size"/>	
						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param param="productVO.rollupAttributes" name="array" />
							<dsp:oparam name="outputStart">
							<div class="bdrBot swatchPickers">
							</dsp:oparam>
							<dsp:oparam name="output">
								<dsp:getvalueof var="menu" param="key"/>
								<c:choose>
									<c:when test="${menu eq 'SIZE'}">
								<div class="sizePicker clearfix">
									<label class="fl">Size:</label>
									<div class="width_5 swatches">
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param param="element" name="array" />											
												<dsp:oparam name="output">	
												<dsp:getvalueof var="attribute" param="element.rollupAttribute"/>
												<c:choose>
													<c:when test="${(sizeParam != null) && (sizeParam eq attribute)}">
													<a href="#" class="size fl selected" data="${attribute}" title="${attribute}"><span><c:out value="${attribute}" escapeXml="false"/></span></a>
													</c:when>
													<c:otherwise>
													<a href="#" class="size fl" data="${attribute}" title="${attribute}"><span><c:out value="${attribute}" escapeXml="false"/></span></a>
													</c:otherwise>
												</c:choose>
												</dsp:oparam>	
											</dsp:droplet>
									</div>
								</div>
								</c:when>
								<c:otherwise>
										<c:choose>
										<c:when test="${menu eq 'COLOR'}">
											<div class="colorPicker clearfix">
											<label class="fl"><dsp:valueof param="key"/>:</label>
												<div class="width_5 swatches">
												<dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param param="element" name="array" />											
													<dsp:oparam name="output">
													<dsp:getvalueof var="colorImagePath" param="element.swatchImagePath"/>
													<dsp:getvalueof var="attribute" param="element.rollupAttribute"/>
													<c:choose>
													<c:when test="${(colorParam != null) && (colorParam eq attribute)}">
													<a href="#" class="fl selected" data="${fn:toLowerCase(attribute)}" title="${attribute}" >
													<span>
													<c:choose>
													<c:when test="${(colorImagePath != null) && (colorImagePath ne '')}">
														<img src="${scene7Path}/${colorImagePath}" height="20" width="20" alt="${attribute}" class="noImageFound"/>
													</c:when>
													<c:otherwise>
														<img src="${imagePath}/_assets/global/images/blank.gif" height="20" width="20" alt="${attribute}" />
													</c:otherwise>
													</c:choose>
													</span></a>
													</c:when>
													<c:otherwise>
													<a href="#" class="fl" data="${fn:toLowerCase(attribute)}" title="${attribute}" >
													<span>
													<c:choose>
													<c:when test="${(colorImagePath != null) && (colorImagePath ne '')}">
														<img src="${scene7Path}/${colorImagePath}" height="20" width="20" alt="${attribute}" class="noImageFound" />
													</c:when>
													<c:otherwise>
														<img src="${imagePath}/_assets/global/images/blank.gif" height="20" width="20" alt="${attribute}"/>
													</c:otherwise>
													</c:choose>
													</span></a>
													</c:otherwise>	
													</c:choose>												
													</dsp:oparam>								
												</dsp:droplet>
												</div>
											</div>
										</c:when>
										<c:otherwise>
											<c:if test="${menu eq 'FINISH'}">
											<div class="finishPicker clearfix">
											<label class="fl"><dsp:valueof param="key"/>:</label>
												<div class="width_5 swatches">
												<dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param param="element" name="array" />											
													<dsp:oparam name="output">
													<dsp:getvalueof var="finishImagePath" param="element.swatchImagePath"/>
													<dsp:getvalueof var="attribute" param="element.rollupAttribute"/>
													<c:choose>
													<c:when test="${(colorParam != null) && (colorParam eq attribute)}">
													<a href="#" class="${fn:toLowerCase(attribute)} fl selected" data="${attribute}" title="${attribute}" >
													<span>
													<c:choose>
													<c:when test="${(finishImagePath != null) && (finishImagePath ne '')}">
														<img src="${scene7Path}/${finishImagePath}" height="20" width="20" alt="${attribute}" class="noImageFound" />
													</c:when>
													<c:otherwise>
														<img src="${imagePath}/_assets/global/images/blank.gif" height="20" width="20" alt="${attribute}" />
													</c:otherwise>
													</c:choose>
													</span></a>
													</c:when>
													<c:otherwise>
													<a href="#" class="${fn:toLowerCase(attribute)} fl" data="${attribute}" title="${attribute}" >
													<span>
													<c:choose>
													<c:when test="${(finishImagePath != null) && (finishImagePath ne '')}">
														<img src="${scene7Path}/${finishImagePath}" height="20" width="20" alt="${attribute}" class="noImageFound" />
													</c:when>
													<c:otherwise>
														<img src="${imagePath}/_assets/global/images/blank.gif" height="20" width="20" alt="${attribute}" />
													</c:otherwise>
													</c:choose>
													</span></a>
													</c:otherwise>		
													</c:choose>											
													</dsp:oparam>	
												</dsp:droplet>
												</div>
											</div>
											</c:if>
										</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>	
														
							</dsp:oparam>
							<dsp:oparam name="outputEnd">
								</div>
							</dsp:oparam>							
						</dsp:droplet>
							
<dsp:getvalueof var="skuinStock" param="inStock" />
				<dsp:getvalueof var="oosProdId" param="productId" />
				<c:choose>
				<c:when test="${skuinStock eq null}">
					<c:set var="inStock" value="true" />
				</c:when>
				<c:otherwise>
					<dsp:getvalueof var="inStock" param="inStock" />
				</c:otherwise>
				</c:choose>
				<dsp:getvalueof var="skuVO" param="pSKUDetailVO" />
				<c:if test="${skuVO != null}">
					<dsp:getvalueof var="pDefaultChildSku" param="pSKUDetailVO.skuId" />
				</c:if>
			
			<dsp:getvalueof var="showTab" param="tabLookUp"/>
							<c:choose>
							<c:when test="${showTab}">
							<div class="priceQuantity <c:if test="${inStock==false}">priceQuantityNotAvailable</c:if>">
								<div itemprop="offers" itemscope itemtype="http://schema.org/Offer">
								<div class="prodPrice">
								<c:choose>
									<c:when test="${not empty pDefaultChildSku}">
										<dsp:include page="../product_details_price.jsp">	
											<dsp:param name="product" param="productId"/>
											<dsp:param name="sku" value="${pDefaultChildSku}"/>
										</dsp:include>
									</c:when>
									<c:otherwise>
											<dsp:droplet name="IsEmpty">
												<dsp:param name="value" param="productVO.salePriceRangeDescription"/>
													<dsp:oparam name="false">
														<li class="isPrice">
														<span class="highlightRed"><dsp:valueof converter="currency" param="productVO.salePriceRangeDescription" /></span>
														</li>
														<li class="wasPrice">
															<span class="was"><bbbl:label key='lbl_was_text' language="${pageContext.request.locale.language}" />
									  							<dsp:valueof converter="currency" param="productVO.priceRangeDescription" /></span>
														</li>
							  						 </dsp:oparam >
							  						 <dsp:oparam name="true">
														 <li class="isPrice"><dsp:valueof converter="currency" param="productVO.priceRangeDescription" valueishtml="true" />
													 </dsp:oparam>
											</dsp:droplet>
									</c:otherwise>
								</c:choose>
								</div>
								<span class="prodAvailabilityStatus hidden">
		                             <c:choose>
		                             <c:when test="${inStock==false}">
		                             	<span itemprop="availability" href="http://schema.org/OutOfStock"><bbbl:label key='lbl_pdp_out_of_stock' language="${pageContext.request.locale.language}" /></span>
		                             </c:when>
		                             <c:otherwise>
		                             	<span itemprop="availability" href="http://schema.org/InStock"><bbbl:label key='lbl_pdp_in_stock' language="${pageContext.request.locale.language}" /></span>
		                             </c:otherwise>
		                             </c:choose>
		                        </span>
								</div>
							</div>
							<c:set var="rebateDivCreated" value="${false}" />
							
								<dsp:getvalueof var="hasRebate" param="pSKUDetailVO.hasRebate"/>
								<c:set var="rebatesOn" value="${false}" />
								<c:if test="${not empty hasRebate && hasRebate}">
									<dsp:getvalueof var="chkEligibleRebates" param="pSKUDetailVO.eligibleRebates"/>
									<c:if test="${(null != chkEligibleRebates) && (fn:length(chkEligibleRebates) == 1 )}"> 
										<c:set var="rebatesOn" value="${true}" />
									</c:if>
								</c:if>
								
								
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
                                                                                    <div class="rebateContainer prodAttribWrapper">
                                                                                    <c:set var="rebateDivCreated" value="${true}" />
                                                                                </c:if>
																				<c:choose>
																					   <c:when test="${null ne imageURLPrice}">
																							 <span class="attribs ${sep}"><img src="${imageURLPrice}" alt="" /><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></span>
																					   </c:when>
																					   <c:otherwise>
																							 <c:choose>
																									 <c:when test="${null ne actionURLPrice}">
																										   <span class="attribs ${sep}"><a href="${actionURLPrice}" class="newOrPopup"><span><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span></a></span>
																									 </c:when>
																									 <c:otherwise>
																										   <span class="attribs ${sep}"><dsp:valueof param="element.attributeDescrip" valueishtml="true"/></span>
																									 </c:otherwise>
																							  </c:choose>
																					   </c:otherwise>
																				 </c:choose>
																		 </c:when>
																		 <c:otherwise>
																			   <c:if test="${null ne imageURLPrice}">
                                                                                    <c:if test="${rebateDivCreated == false}">
                                                                                        <div class="rebateContainer prodAttribWrapper">
                                                                                        <c:set var="rebateDivCreated" value="${true}" />
                                                                                    </c:if>
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
                                    <c:if test="${rebateDivCreated == true}">
                                    </div>
                                </c:if>							
					    </c:when>
					    <c:otherwise>
					    <div class="priceQuantity">
								<div itemprop="offers" itemscope itemtype="http://schema.org/Offer">
								<div class="prodPrice">
							<dsp:droplet name="IsEmpty">
							<dsp:param name="value" param="productVO.salePriceRangeDescription"/>
								<dsp:oparam name="false">
								<li class="isPrice">
									<span class="highlightRed"><dsp:valueof converter="currency" param="productVO.salePriceRangeDescription" /></span>
								</li>
								<li class="wasPrice">
									<span class="was"><bbbl:label key='lbl_was_text' language="${pageContext.request.locale.language}" />
									  <dsp:valueof converter="currency" param="productVO.priceRangeDescription" /></span>
								</li>
							   </dsp:oparam >
							   <dsp:oparam name="true">
								 <li class="isPrice"><dsp:valueof converter="currency" param="productVO.priceRangeDescription" valueishtml="true" />
							</dsp:oparam>
						</dsp:droplet>
								</div>								
								</div>
							</div>
					    </c:otherwise>
					    </c:choose>
					    <c:choose>
							<c:when test="${appid eq BedBathUSSite}">
								<c:set var="tipsOn"><tpsw:switch tagName="TipsOn_US"/></c:set>
							</c:when>
							<c:when test="${appid eq BuyBuyBabySite}">
								<c:set var="tipsOn"><tpsw:switch tagName="TipsOn_baby"/></c:set>
							</c:when>
							<c:when test="${appid eq BedBathCanadaSite}">
								<c:set var="tipsOn"><tpsw:switch tagName="TipsOn_CA"/></c:set>
							</c:when>
						</c:choose>		
						<c:if test="${tipsOn}">
						    <dsp:droplet name="TargetingRandom">
							    <dsp:param bean="/atg/registry/RepositoryTargeters/TargeterPDPSecond" name="targeter"/>
								  <dsp:param name="howMany" value="1"/>
								  <dsp:oparam name="output">
								     <dsp:getvalueof var="tipsTxt" param="element.tipsTxt"/>
								     <dsp:getvalueof var="tipsTitle" param="element.tipsTitle"/>
								</dsp:oparam>
						    </dsp:droplet>
							<c:if test="${not empty tipsTxt}">
								<div class="width_5 returnByStore">
								<h3> ${tipsTitle} </h3>
								   <div class="desclight">
									  ${tipsTxt} 
								   </div>
									<div class="botShadow"></div>
								</div>
							</c:if>
						</c:if>
						</div>
				
			
			</div>
			</div>
			<dsp:getvalueof var="isLeadSKU" param="collectionVO.leadSKU"/>
			
			<dsp:getvalueof var="collection" param="productVO.collection"/>
			<c:if test="${collection==true}">
			<c:choose>
			<c:when test="${isLeadSKU==true}">
			<dsp:include page="accessoriesForms.jsp">
			<dsp:param name="parentProductId" value="${parentProductId}"/>
			</dsp:include>
			<c:set var="SchemeName" value="pdp_acccav"/>
			</c:when>
			<c:otherwise>
			<dsp:include page="collectionForms.jsp">
			<dsp:param name="parentProductId" value="${parentProductId}"/>
			</dsp:include>
			<c:set var="SchemeName" value="pdp_collcav"/>
			</c:otherwise>
			</c:choose>
			</c:if>
			
			</div>
			<%-- <c:if test="${showTab}"> --%>
			<dsp:droplet name="ProdToutDroplet">
					<dsp:param value="lastviewed" name="tabList" />
						<dsp:param param="categoryId" name="id" />
						<dsp:param param="siteId" name="siteId" />
						<dsp:param name="productId" param="productId" />
					<dsp:oparam name="output">
					<dsp:getvalueof var="lastviewedProductsList" param="lastviewedProductsList" />
					<dsp:droplet name="ExitemIdDroplet">
			          <dsp:param value="${lastviewedProductsList}" name="lastviewedProductsList" />
			          <dsp:oparam name="output">
					    <dsp:getvalueof var="productList" param="productList" />
                               </dsp:oparam>
			        </dsp:droplet>
			        </dsp:oparam>
			  </dsp:droplet>
			  
			        
			<dsp:droplet name="Switch">
				<dsp:param name="value" bean="Profile.transient"/>
				<dsp:oparam name="false">
					<dsp:getvalueof var="userId" bean="Profile.id"/>
				</dsp:oparam>
				<dsp:oparam name="true">
					<dsp:getvalueof var="userId" value=""/>
				</dsp:oparam>
			</dsp:droplet>
			
			<div id="botCrossSell" class="marTop_20 grid_12">
			<c:if test="${CertonaOn}">
		        <dsp:droplet name="CertonaDroplet">
			 		<dsp:param name="scheme" value="${SchemeName}"/>
					 <dsp:param name="userid" value="${userId}"/>
					 <dsp:param name="context" value="${CertonaContext}"/>
					 <dsp:param name="exitemid" value="${productList}"/>
					 <dsp:param name="productId" value="${parentProductId}"/>
					 <dsp:param name="siteId" value="${appid}" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="relatedItemsProductsVOsList" param="certonaResponseVO.resonanceMap.${SchemeName}.productsVOsList"/>
						</dsp:oparam>
						<dsp:oparam name="error">
						</dsp:oparam>			
						<dsp:oparam name="empty">
						</dsp:oparam>
				</dsp:droplet>
				
				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param bean="SystemErrorInfo.errorList" name="array" />
					<dsp:param name="elementName" value="ErrorInfoVO" />
					<dsp:oparam name="outputStart"><div id="error" class="hidden"><ul></dsp:oparam>
					<dsp:oparam name="output">
						<li id="tl_atg_err_code"><dsp:valueof param="ErrorInfoVO.errorCode"/></li>
						<li id="tl_atg_err_value"><dsp:valueof param="ErrorInfoVO.errorDescription"/></li>
					</dsp:oparam>
					<dsp:oparam name="outputEnd"></ul></div></dsp:oparam>
			   </dsp:droplet>
			   
				</c:if>
				<c:if test="${(not empty relatedItemsProductsVOsList)}">
				<div class="categoryProductTabs marTop_20">
					<ul class="categoryProductTabsLinks">
						<li><a title="<bbbl:label key='lbl_pdp_product_related_items' language="${pageContext.request.locale.language}" />" href="#botCrossSell-tabs1">
						<bbbl:label key='lbl_pdp_product_related_items' language="${pageContext.request.locale.language}" /></a></li>
					</ul>
					<div id="botCrossSell-tabs1" class="categoryProductTabsData">
						<div class="carousel clearfix">
							<div class="carouselBody grid_12">
								<div class="grid_1 carouselArrow omega carouselArrowPrevious clearfix">
									&nbsp;
									<a class="carouselScrollPrevious" title="Previous" role="button" href="#">Previous</a>
								</div>
								
										<dsp:include page="../certona_prod_carousel.jsp" >
										 	<dsp:param name="productsVOsList" value="${relatedItemsProductsVOsList}"/>
										 	<dsp:param name="crossSellFlag" value="true"/>
									  	</dsp:include>
									
								
								<div class="grid_1 carouselArrow alpha carouselArrowNext clearfix">
									&nbsp;
									<a class="carouselScrollNext" role="button" title="Next" href="#">Next</a>
								</div>
							</div>
							<div class="carouselPages">
								<div class="carouselPageLinks clearfix">
									<a title="Page 1" class="selected" href="#">1</a>
									<a title="Page 2" href="#">2</a>
									<a title="Page 3" href="#">3</a>
									<a title="Page 4" href="#">4</a>
								</div>							
					</div>
				</div>
				</c:if>	
			</div>
		</div>
	</div>
</dsp:oparam>
		<dsp:oparam name="error">
		<div id="content" class="container_12 clearfix"><bbbl:label key='lbl_pdp_product_not_available' language="${pageContext.request.locale.language}" /></div>
		</dsp:oparam>
	</dsp:droplet>
	
</jsp:body>
</bbb:pageContainer>
</dsp:page>