<dsp:page>
    <dsp:importbean bean="/atg/commerce/ShoppingCart" />
    <dsp:importbean bean="/atg/userprofiling/Profile" />
   	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
   	<dsp:importbean bean="/atg/repository/seo/CanonicalItemLink"/>
   	<dsp:importbean bean="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet"/>
   	<dsp:importbean bean="/com/bbb/commerce/order/droplet/EcoFeeApplicabilityCheckDroplet"/>
    <c:set var="enableKatoriFlag" scope="request"><bbbc:config key="enableKatori" configName="EXIMKeys"/></c:set>
	<dsp:getvalueof var="fromMiniCart" param="fromMiniCart"/>
    <c:set var="imagePath">
        <bbbc:config key="image_host" configName="ThirdPartyURLs" />
    </c:set>
    <c:set var="scene7Path">
        <bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
    </c:set>
        <div id="miniCartItemsWrapper" class="small-12 columns">
                <dsp:droplet name="ForEach">
                    <dsp:param name="array" bean="ShoppingCart.current.commerceItems" />
                    <dsp:param name="elementName" value="commItem" />
                    <dsp:param name="sortProperties" value="-lastModifiedDate"/>
                    <dsp:oparam name="output">
                        <%--KP Comment need more clarification on this droplet usage --%>
                          <dsp:getvalueof param="commItem.repositoryItem.type" var="cItemType"/>
                          <c:if test="${cItemType eq  'tbsCommerceItem'}">
                                <dsp:getvalueof var="freeShippingMethod" param="commItem.freeShippingMethod"/>  
                                <dsp:droplet name="CanonicalItemLink">
                                    <dsp:param name="id" param="commItem.repositoryItem.productId" />
                                    <dsp:param name="itemDescriptorName" value="product" />
                                    <dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
                                    <dsp:oparam name="output">
                                        <dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
                                    </dsp:oparam>
                                </dsp:droplet>
                                <div class="row mini-cart-item">
                                    <dsp:getvalueof var="commCount" param="count"></dsp:getvalueof>
                                    <c:if test="${commCount le param.commItemCount}">
                                        <div class="lblJustAdded"><bbbl:label key="lbl_orderitems_Justadded" language="${pageContext.request.locale.language}" /></div>
                                    </c:if>
                                        
                                        <dsp:droplet name="BBBPriceDisplayDroplet">
                                            <dsp:param name="priceObject" param="commItem" />
                                            <dsp:param name="profile" bean="Profile"/>
                                            <dsp:param name="elementName" value="priceInfoVO" />
                                            <dsp:oparam name="output">
                                                <dsp:getvalueof var="commItem" param="commItem" />
                                                <c:if test="${commItem.storeSKU}">
                                                    <dsp:getvalueof var="finalUrl" vartype="java.lang.String" value="" />
                                                </c:if>
                                                <c:set var="isEximErrorExists">${commItem.eximErrorExists}</c:set>
                                           		<dsp:getvalueof var="kirsch" value="${commItem.kirsch}"/>
                                            	<dsp:getvalueof var="cmo" value="${commItem.CMO}"/>
                                          		<dsp:getvalueof var="tbsItemInfo" value="${commItem.TBSItemInfo }"/>

                                                <div class="small-3 columns">
                                                  <dsp:getvalueof var="displayName" param="commItem.auxiliaryData.catalogRef.displayName"/>
                                                   <dsp:getvalueof var="unitSavedAmount" param="priceInfoVO.unitSavedAmount"/>
                                                   <dsp:getvalueof var="unitListPrice" param="priceInfoVO.unitListPrice"/>
                                                   <dsp:getvalueof var="unitSalePrice" param="priceInfoVO.unitSalePrice"/>
                                                   <dsp:getvalueof var="prodImg" param="commItem.auxiliaryData.catalogRef.mediumImage">
                                                	<c:if test="${not empty commItem.referenceNumber}">
													    <c:set var="prodImg">${commItem.fullImagePath}</c:set>
													</c:if>
	                                                <c:choose>
														<c:when test="${kirsch or cmo}">
														 <%--<c:choose>
																<c:when test="${tbsItemInfo ne null}">
																	<dsp:getvalueof var="kirschCmoImg" value="${tbsItemInfo.productImage}"/>
																	<dsp:img iclass="prodImg noImageFound" width="63" height="63" src="${kirschCmoImg}" alt="${displayName}" />
																</c:when>
																<c:otherwise> --%>
																	<dsp:img iclass="prodImg" width="63" height="63" src="/_assets/global/images/no_image_available.jpg" alt="${displayName} $${unitSalePrice}"/>
																<%-- </c:otherwise>
															</c:choose> --%>
														</c:when>
														<c:otherwise>
															<dsp:a page="${finalUrl}?skuId=${commItem.catalogRefId}">
		                                                      
															<c:choose>
                                                            <c:when test="${not empty commItem.referenceNumber && not empty prodImg && (empty isEximErrorExists or !isEximErrorExists)}">
                                 								<dsp:img iclass="prodImg noImageFound" src="${prodImg}" height="63" width="63" alt="${displayName}"/>
                                 					    	</c:when>
                                                            <c:when test="${unitSalePrice gt 0.0}">
                                                                <c:choose>
                                                                    <c:when test="${empty prodImg}">
                                                                        <dsp:img iclass="prodImg" width="63" height="63" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${displayName} $${unitSalePrice}"/>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <dsp:img iclass="prodImg noImageFound" width="63" height="63" src="${scene7Path}/${prodImg}" alt="${displayName}" />
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:choose>
                                                                    <c:when test="${empty prodImg}">
                                                                        <dsp:img iclass="prodImg" width="63" height="63" src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${displayName} $${unitListPrice}"/>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <dsp:img iclass="prodImg noImageFound" width="63" height="63" src="${scene7Path}/${prodImg}" alt="${displayName}" />
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:otherwise>
                                                        </c:choose>
		                                                       
		                                                    </dsp:a>
														</c:otherwise>
													</c:choose>
																										
                                                 </dsp:getvalueof>
                                                    
                                                </div>
                                                <div class="small-9 columns">
                                                    <ul class="prodInfo">
                                                        <li class="prodName padBottom_5">
	                                                       	<c:choose>
																<c:when test="${kirsch or cmo}">
																	<c:out value="${displayName}" escapeXml="false"/>
																</c:when>
																<c:otherwise>
																	<dsp:a page="${finalUrl}?skuId=${commItem.catalogRefId}"><c:out value="${displayName}" escapeXml="false"/></dsp:a>
																</c:otherwise>
															</c:choose>
                                                        </li>
                                                         <c:choose>
		                                                    <c:when test="${not empty commItem.referenceNumber && (isEximErrorExists || !enableKatoriFlag)}">  
		                                                    	<li class="prodPrice">
		                                                                    TBD
		                                                        </li>
		                                                    </c:when>
		                                                    <c:otherwise>                                                        
	                                                       		<li class="prodPrice">
		                                                            <c:choose>
		                                                                <c:when test="${unitSalePrice gt 0.0}">
		                                                                    <fmt:formatNumber value="${unitSalePrice}"  type="currency"/>
		                                                                </c:when>
		                                                                <c:otherwise>
		                                                                    <fmt:formatNumber value="${unitListPrice}"  type="currency"/>
		                                                                </c:otherwise>
		                                                            </c:choose>
	                                                        	</li>
		                                                        <li class="prodSubInfo clearfix">
		                                                            <ul class="clearfix">
		                                                                <c:if test="${unitSalePrice gt 0.0}">
		                                                                    <jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request"/>
		                                                                    <c:set target="${placeHolderMap}" property="unitListPrice" value="${unitListPrice}"/>
		                                                                    <li>
		                                                                       <bbbl:label key="lbl_checkout_open_bracket" language="<c:out param='${language}'/>"/><bbbl:label key="lbl_preview_reg" language="<c:out param='${language}'/>"/> <dsp:valueof value="${unitListPrice}" converter="currency"/>
		                                                                       <bbbl:label key="lbl_checkout_closing_bracket" language="<c:out param='${language}'/>"/>
		                                                                    </li>
		                                                                    <li>
		                                                                        <bbbl:label key="lbl_cartdetail_yousave" language="${pageContext.request.locale.language}" />
		                                                                        <dsp:valueof param="priceInfoVO.unitSavedAmount" number="0.00" converter="currency"/>
		                                                                        <%-- <bbbl:label key="lbl_checkout_open_bracket" language="<c:out param='${language}'/>"/>
		                                                                        <dsp:valueof param="priceInfoVO.totalSavedPercentage" number="0.00"/>
		                                                                        <bbbl:label key="lbl_checkout_percent_sign" language="<c:out param='${language}'/>"/>
		                                                                        <bbbl:label key="lbl_checkout_closing_bracket" language="<c:out param='${language}'/>"/> --%>
		                                                                    </li>
		                                                                </c:if>
		                                                                <c:set var="TBS_BedBathCanadaSite">
		                                                                    <bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
		                                                                </c:set>
		                                                                <c:if test="${currentSiteId == TBS_BedBathCanadaSite}"  >
		                                                                    <dsp:droplet name="EcoFeeApplicabilityCheckDroplet">
		                                                                        <dsp:param name="skuId" param="commItem.catalogRefId" />
		                                                                        <dsp:oparam name="true">
		                                                                            <li><bbbl:label key="lbl_cartdetail_elegibleforecofee" language="<c:out param='${language}'/>"/></li>
		                                                                        </dsp:oparam>
		                                                                    </dsp:droplet>
		                                                                </c:if>
		                                                                <li><bbbl:label key="lbl_orderitems_quantity" language="${pageContext.request.locale.language}" /> <strong><dsp:valueof param="commItem.quantity" /></strong></li>
		                                                            </ul>
		                                                        </li>
		                                                        <c:if test="${fn:length(freeShippingMethod) gt 0}">
		                                                            <li class="prodPrimaryAttribute">
		                                                                <bbbl:label key="lbl_orderitems_freeshipping" language="${pageContext.request.locale.language}" />
		                                                            </li>
		                                                        </c:if>
                                                        	</c:otherwise>
                                                        </c:choose>
                                                    </ul>
                                                </div>
                                            </dsp:oparam>
                                        </dsp:droplet>
                                   
                                </div>
                        </c:if>  
                    </dsp:oparam>
                </dsp:droplet>
            
        </div>
</dsp:page>