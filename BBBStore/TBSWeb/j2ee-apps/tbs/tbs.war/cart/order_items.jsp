<dsp:page>
    <dsp:importbean bean="/atg/commerce/ShoppingCart" />
    <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
    <dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
    <dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:getvalueof var="fromMiniCart" param="fromMiniCart"/>
    <c:set var="imagePath">
        <bbbc:config key="image_host" configName="ThirdPartyURLs" />
    </c:set>
    <c:set var="scene7Path">
        <bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
    </c:set>
    <c:set var="enableKatoriFlag" scope="request"><bbbc:config key="enableKatori" configName="EXIMKeys"/></c:set>
        <div id="miniCartItemsWrapper" class="small-12 columns" <c:if test="${cartItemsCount > 4}">class="viewport"</c:if>>
            <ul id="miniCartItems" class="<c:if test="${cartItemsCount > 4}">overview</c:if> noMar noPad clearfix">
                <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                    <dsp:param name="array" bean="ShoppingCart.current.commerceItems" />
                    <dsp:param name="elementName" value="commItem" />
                    <dsp:param name="sortProperties" value="-lastModifiedDate"/>
                    <dsp:oparam name="output">
                        <%--KP Comment need more clarification on this droplet usage --%>
                        <dsp:droplet name="/atg/dynamo/droplet/Compare">
                          <dsp:param name="obj1" param="commItem.repositoryItem.type"/>
                          <dsp:param name="obj2" value="tbsCommerceItem"/>
                            <dsp:oparam name="equal"> 
                                <dsp:getvalueof var="freeShippingMethod" param="commItem.freeShippingMethod"/>  
                                <dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
                                    <dsp:param name="id" param="commItem.repositoryItem.productId" />
                                    <dsp:param name="itemDescriptorName" value="product" />
                                    <dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
                                    <dsp:oparam name="output">
                                        <dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
                                    </dsp:oparam>
                                </dsp:droplet>
                                <li class="miniCartRow cartItem clearfix <c:if test="${cartItemsCount > 4}">miniCartRowOverflow</c:if>">
                                    <dsp:getvalueof var="commCount" param="count"></dsp:getvalueof>
                                    <div class="border clearfix <c:if test="${commCount le param.commItemCount}">justAdded</c:if>">
                                        <c:if test="${commCount le param.commItemCount}">
                                            <span class="lblJustAdded"><bbbl:label key="lbl_orderitems_Justadded" language="${pageContext.request.locale.language}" /></span><br/>
                                        </c:if>
                                        <dsp:droplet name="/com/bbb/commerce/pricing/droplet/BBBPriceDisplayDroplet">
                                            <dsp:param name="priceObject" param="commItem" />
                                            <dsp:param name="profile" bean="Profile"/>
                                            <dsp:param name="elementName" value="priceInfoVO" />
                                            <dsp:oparam name="output">
                                                <dsp:getvalueof var="commItem" param="commItem" />
                                                <c:if test="${commItem.storeSKU}">
                                                    <dsp:getvalueof var="finalUrl" vartype="java.lang.String" value="" />
                                                </c:if>
                                                <c:set var="isEximErrorExists">${commItem.eximErrorExists}</c:set>

                                                <div class="small-12 columns">
                                                    <dsp:a page="${finalUrl}?skuId=${commItem.catalogRefId}">
                                                        <dsp:getvalueof var="displayName" param="commItem.auxiliaryData.catalogRef.displayName"/>
                                                        <dsp:getvalueof var="unitSavedAmount" param="priceInfoVO.unitSavedAmount"/>
                                                        <dsp:getvalueof var="unitListPrice" param="priceInfoVO.unitListPrice"/>
                                                        <dsp:getvalueof var="unitSalePrice" param="priceInfoVO.unitSalePrice"/>
                                                        <dsp:getvalueof var="prodImg" param="commItem.auxiliaryData.catalogRef.mediumImage">
                                                        <c:if test="${!empty commItem.referenceNumber}">
													         <c:set var="prodImg">${commItem.fullImagePath}</c:set>
													    </c:if>
                                                            <c:choose>
                                                                <c:when test="${unitSalePrice gt 0.0}">
                                                                 <c:choose>
                                             			          <c:when test="${!empty commItem.referenceNumber}">
                                             				      	<c:choose>
                                             					    	<c:when test="${not empty prodImg && (empty isEximErrorExists or !isEximErrorExists)}">
                                             								<dsp:img iclass="prodImg noImageFound" src="${prodImg}" height="63" width="63" alt="${displayName}"/>
                                             					    	</c:when>
                                             					    	<c:otherwise>
                                             					 			<dsp:img iclass="prodImg" src="${imagePath}/_assets/global/images/no_image_available.jpg" height="63" width="63" alt="${displayName} $${unitSalePrice}"/>                                             						
                                             					    	</c:otherwise>
                                             				      	</c:choose>
                                                                  </c:when>
                                             			          <c:otherwise>
                                             				        <c:choose>
                                                       			    	<c:when test="${empty prodImg}">                                                       			 
                                                            				<dsp:img iclass="prodImg" src="${imagePath}/_assets/global/images/no_image_available.jpg" height="63" width="63" alt="${displayName} $${unitSalePrice}"/>
                                                        		    	</c:when>
                                                        		    	<c:otherwise>
                                                            				<dsp:img iclass="prodImg noImageFound" src="${scene7Path}/${prodImg}" height="63" width="63" alt="${displayName}" />
                                                        		   		</c:otherwise>
                                                 			      	</c:choose>
                                             			          </c:otherwise>
                                             		             </c:choose>
																</c:when>
															    <c:otherwise>
                                                                 	<c:choose>
                                             			          		<c:when test="${!empty commItem.referenceNumber}">
                                             				      			<c:choose>
                                             					    			<c:when test="${not empty prodImg && !isEximErrorExists && enableKatoriFlag}">
                                             										<dsp:img iclass="prodImg noImageFound" src="${prodImg}" height="63" width="63" alt="${displayName}"/>
                                             					    			</c:when>
                                             					    			<c:otherwise>
                                             					 					<dsp:img iclass="prodImg" src="${imagePath}/_assets/global/images/no_image_available.jpg" height="63" width="63" alt="${displayName} $${unitListPrice}"/>                                             						
                                             					    			</c:otherwise>
                                             				      			</c:choose>
                                                                  		</c:when>
                                             			          		<c:otherwise>
                                             				        	<c:choose>
                                                       			    		<c:when test="${empty prodImg}">                                                       			 
                                                            					<dsp:img iclass="prodImg" src="${imagePath}/_assets/global/images/no_image_available.jpg" height="63" width="63" alt="${displayName} $${unitListPrice}"/>
                                                        		    		</c:when>
                                                        		    		<c:otherwise>
                                                            					<dsp:img iclass="prodImg noImageFound" src="${scene7Path}/${prodImg}" height="63" width="63" alt="${displayName}" />
                                                        		   			</c:otherwise>
                                                 			      		</c:choose>
                                             			          		</c:otherwise>
                                             		             	</c:choose>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </dsp:getvalueof>
                                                    </dsp:a>
                                                </div>
                                                <div class="small-12 columns">
                                                    <ul class="prodInfo">
                                                        <li class="prodName padBottom_5"><dsp:a page="${finalUrl}?skuId=${commItem.catalogRefId}"><c:out value="${displayName}" escapeXml="false"/></dsp:a></li>
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
		                                                                    <dsp:valueof value="${unitSalePrice}" converter="currency"/>
		                                                                </c:when>
		                                                                <c:otherwise>
		                                                                    <dsp:valueof value="${unitListPrice}" converter="currency"/>
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
		                                                                        <bbbl:label key="lbl_checkout_open_bracket" language="<c:out param='${language}'/>"/>
		                                                                        <dsp:valueof param="priceInfoVO.totalSavedPercentage" number="0.00"/>
		                                                                        <bbbl:label key="lbl_checkout_percent_sign" language="<c:out param='${language}'/>"/>
		                                                                        <bbbl:label key="lbl_checkout_closing_bracket" language="<c:out param='${language}'/>"/>
		                                                                    </li>
		                                                                </c:if>
		                                                                <c:set var="TBS_BedBathCanadaSite">
		                                                                    <bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
		                                                                </c:set>
		                                                                <c:if test="${currentSiteId == TBS_BedBathCanadaSite}"  >
		                                                                    <dsp:droplet name="/com/bbb/commerce/order/droplet/EcoFeeApplicabilityCheckDroplet">
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
                                </li>
                            </dsp:oparam>
                        </dsp:droplet>   
                    </dsp:oparam>
                </dsp:droplet> 
            </ul>
        </div>
</dsp:page>