<dsp:page>
	<dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet" />
	<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
	<dsp:importbean
		bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet" />
	<dsp:importbean
		bean="/atg/commerce/order/purchase/CartModifierFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean bean="/com/bbb/certona/CertonaConfig" />
	 <dsp:importbean var="SystemErrorInfo" bean="/com/bbb/utils/SystemErrorInfo"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof var="contextPath"
		value="${pageContext.request.contextPath}" scope="request" />
	<dsp:getvalueof var="key" param="key" />

	<%-- init default values/variables --%>
	<c:set var="BedBathUSSite" scope="request">
		<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BuyBuyBabySite" scope="request">
		<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BedBathCanadaSite" scope="request">
		<bbbc:config key="BedBathCanadaSiteCode"
			configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="scene7Path" scope="request">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
	<c:set var="appid" scope="request">
		<dsp:valueof bean="Site.id" />
	</c:set>
	<c:set var="appIdCertona" scope="request">
		<dsp:valueof bean="CertonaConfig.siteIdAppIdMap.${appid}" />
	</c:set>
	<c:set var="lblAboutThe"><bbbl:label key="lbl_accessibility_about_the" language="${pageContext.request.locale.language}" /></c:set>
	<dsp:getvalueof var="CertonaContext" value="${fn:escapeXml(param.CertonaContext)}" />
 	<dsp:getvalueof var="RegistryContext" value="${fn:escapeXml(param.RegistryContext)}" />
 	<dsp:getvalueof var="shippingThreshold" value="${fn:escapeXml(param.shippingThreshold)}" />
	<dsp:getvalueof var="registryId" param="registryId" />
	<dsp:getvalueof var="certonaSwitch" param="certonaSwitch" />
	<dsp:getvalueof var="recomSkuId" value="${fn:escapeXml(param.recomSkuId)}"/>
	<c:set var="BazaarVoiceOn" scope="request"
		value="${param.BazaarVoiceOn}" />
	<c:set var="ShipMsgDisplayFlag" scope="request"><bbbc:config key="ShipMsgDisplayFlag" configName="FlagDrivenFunctions"/></c:set>
	<c:set var="shippingAttributesList">
		<bbbc:config key="shippingAttributesList" configName="ContentCatalogKeys" />
	</c:set>

	<%-- check if certona if off --%>
	<c:if test="${not empty certonaSwitch}">
		<c:set var="certonaDefaultFlag" value="${certonaSwitch}" />
	</c:if>

	<div id="clearfix">

		<c:if test="${certonaDefaultFlag}">
			<div id="fc_lmi">
				<div id="certonaLastMinute">

					<dsp:droplet name="Switch">
						<dsp:param name="value" bean="Profile.transient" />
						<dsp:oparam name="false">
							<dsp:getvalueof var="userId" bean="Profile.id" />
						</dsp:oparam>
						<dsp:oparam name="true">
							<dsp:getvalueof var="userId" value="" />
						</dsp:oparam>
					</dsp:droplet>

					<!-- condition added for fetching different no of  certona product for  intl user-->
					    <c:choose>
								<c:when test="${isInternationalCustomer}">
									<c:set var="lastMinItemsMax" scope="request">
										<bbbc:config key="LastMinItemsMaxIntlUser" configName="CertonaKeys" />
									</c:set>
								</c:when>
								<c:otherwise>
									<c:set var="lastMinItemsMax" scope="request">
										<bbbc:config key="LastMinItemsMax" configName="CertonaKeys" />
									</c:set>
								</c:otherwise>
						</c:choose>
					
					<%--BBBSL-7222 | Passing recomSKu Id in exitem Id parameter --%>	
 				<c:if test="${not empty recomSkuId}">
 					<c:set var="recomSkuId" value="${recomSkuId};"/>
 				</c:if>

				
					 <dsp:droplet name="/atg/commerce/order/droplet/FetchSavedItemsProductIdsDroplet">
						<dsp:oparam name="output">
							<dsp:getvalueof var="prodIdList" param="prodIdList"/>
						</dsp:oparam>
					</dsp:droplet>
					<dsp:droplet name="CertonaDroplet">
						<dsp:param name="scheme" value="fc_lmi" />
						<dsp:param name="context" value="${CertonaContext}" />
						<dsp:param name="exitemid" value="${RegistryContext}${recomSkuId}${prodIdList}"/>
						<dsp:param name="userid" value="${userId}" />
						<dsp:param name="number" value="${lastMinItemsMax}" />
						<dsp:param name="siteId" value="${appid}" />
						<dsp:param name="shippingThreshold" value="${shippingThreshold}" />
						<dsp:param name="isInternationalCustomer" value="${isInternationalCustomer}" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="skuDetailVOList"
								param="certonaResponseVO.resonanceMap.${'fc_lmi'}.skuDetailVOList" />
							
							<%--BBBSL-6574 --%>
							<dsp:getvalueof var="requestURL" param="certonaResponseVO.requestURL" scope="request"/>
							<dsp:getvalueof var="responseXML" param="certonaResponseVO.responseXML" scope="request"  />
							<dsp:getvalueof var="errorMsg" param="errorMsg" scope="request"  />
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param name="array" value="${skuDetailVOList}" />
								<dsp:param name="elementName" value="skuVO" />
								<dsp:oparam name="outputStart">
									<h6>
										<bbbl:label key='lbl_certona_slots_last_minute_items'
											language="${pageContext.request.locale.language}" />
									</h6>
								</dsp:oparam>
							
								<dsp:oparam name="output">
									<dsp:getvalueof var="pageIdCertona"
										param="certonaResponseVO.pageId" />
									<dsp:getvalueof var="linksCertona"
										param="certonaResponseVO.resxLinks" />

									<div class="certonaProduct clearfix">
										<dsp:form name="frmCertonaProduct" method="post" onsubmit="addItemCartOmniture(this)";>
											<dsp:getvalueof var="skuId" param="skuVO.skuId" />
											<dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param name="array"
													param="certonaResponseVO.resonanceMap.${'fc_lmi'}.skuParentProductMap" />
												<dsp:oparam name="output">
													<dsp:getvalueof var="skuIdParam" param="key" />
													<c:if test="${skuIdParam eq skuId}">
														<dsp:getvalueof var="productIdParam"
															param="certonaResponseVO.resonanceMap.${'fc_lmi'}.skuParentProductMap.${skuIdParam}" />
													</c:if>
												</dsp:oparam>
											</dsp:droplet>
											<dsp:setvalue bean="CartModifierFormHandler.addItemCount"
												value="1" />
											<dsp:input bean="CartModifierFormHandler.fromPage" type="hidden"
												value="addItemToOrder" />
											<%-- Client DOM XSRF
											<dsp:input
												bean="CartModifierFormHandler.addItemToOrderSuccessURL"
												type="hidden" value="${contextPath}/cart/cart.jsp" />
											<dsp:input
												bean="CartModifierFormHandler.addItemToOrderErrorURL"
												type="hidden" value="${contextPath}/cart/cart.jsp" />
												--%>
											<dsp:input bean="CartModifierFormHandler.addItemCount"
												type="hidden" iclass="addItemCount" value="1" />
											<dsp:input bean="CartModifierFormHandler.items[0].productId"
												type="hidden" name="prodId" value="${productIdParam}" />
											<dsp:input
												bean="CartModifierFormHandler.items[0].catalogRefId"
												type="hidden" name="skuId" value="${skuId}" />
											<dsp:input bean="CartModifierFormHandler.items[0].quantity"
												type="hidden" name="qty" value="1" />
											<dsp:input name="bts"
												bean="CartModifierFormHandler.value.bts" type="hidden"
												iclass="isBTS" value="false" />
											<div class="productContent">
												<dsp:getvalueof var="smallImage"
													param="skuVO.skuImages.smallImage" />
												<dsp:getvalueof var="thumbnailImage"
													param="skuVO.skuImages.thumbnailImage" />

												<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
													<dsp:param name="id" value="${productIdParam}" />
													<dsp:param name="itemDescriptorName" value="product" />
													<dsp:param name="repositoryName"
														value="/atg/commerce/catalog/ProductCatalog" />
													<dsp:oparam name="output">
														<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
															param="url" />
													</dsp:oparam>
												</dsp:droplet>
												<c:set var="productName">
													<dsp:valueof param="skuVO.displayName" valueishtml="true" />
												</c:set>
												<dsp:a iclass="prodImg block textCenter"
													page="${finalUrl}?skuId=${skuId}" title="${productName}"
													onclick="javascript:pdpCrossSellProxy('pfm','Last Minute Items(cart)')">
													<c:choose>
														<c:when test="${empty thumbnailImage}">
															<img class="productImage" align="center"
																src="${imagePath}/_assets/global/images/no_image_available.jpg"
																height="83" width="83" alt="image of ${productName}" />
														</c:when>
														<c:otherwise>
															<img class="productImage noImageFound" align="center"
																src="${scene7Path}/${thumbnailImage}" height="83"
																width="83" alt="image of ${productName}" />
														</c:otherwise>
													</c:choose>
													<span class="prodInfo inlineBlock cb fl padTop_10 noMar textLeft">
														<span class="prodName">${productName}</span>
													</span>
													<span class="clear block"></span>
												</dsp:a>
												<div class="prodInfo cb">
												   <tbody>
													<dsp:getvalueof var="isSale" param="skuVO.onSale" />
													<div class="prodPrice">

													<dsp:include page="/browse/product_details_price.jsp">
														<dsp:param name="product" param="productId" />
														<dsp:param name="sku" param="skuVO.skuId" />
														<dsp:param name="isSale" value="${isSale}" />
														<dsp:param name="priceLabelCodeSKU" param="skuVO.pricingLabelCode" />
														<dsp:param name="inCartFlagSKU" param="skuVO.inCartFlag" />
													</dsp:include>
													</div>
													<c:set var="showShipCustomMsg" value="true"/>
													
													<dsp:droplet name="/atg/dynamo/droplet/ForEach">
														<dsp:param name="array" param="skuVO.skuAttributes" />
														<dsp:param name="elementName" value="attributeVOList" />
														<dsp:oparam name="outputStart">
														<div class="prodSubInfo ">													
														</dsp:oparam>
														
														<dsp:oparam name="output">
															<dsp:getvalueof var="pageName" param="key" />
															<c:if test="${pageName eq 'RLP'}">
																<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																	<dsp:param param="attributeVOList" name="array" />
																	<dsp:param name="sortProperties" value="+priority" />
																	<dsp:param name="elementName" value="attributeVO" />
																	<dsp:oparam name="output">
																		<div class="highlight prodAttribWrapper">
																		<dsp:valueof param="attributeVO.attributeDescrip" valueishtml="true" /></div>
																	</dsp:oparam>
																</dsp:droplet>
															</c:if>
														</dsp:oparam>
														
														<dsp:oparam name="outputEnd">
														</div>		
														</dsp:oparam>
													</dsp:droplet>
													<c:if test="${ShipMsgDisplayFlag && showShipCustomMsg}">
														<div class="prodSubInfo "><dsp:valueof param="skuVO.displayShipMsg" valueishtml="true" /></div>
													</c:if>
													
							<c:if test="${BazaarVoiceOn}">
														<dsp:droplet name="ProductDetailDroplet">
															<dsp:param name="id" value="${productIdParam}" />
															<dsp:param name="siteId" value="${appid}" />
															<dsp:param name="skuId" param="skuVO.skuId" />
															<dsp:param name="registryId" value="${registryId}" />
															<dsp:param name="isMainProduct" value="true"/>
															<dsp:oparam name="output">
																<dsp:getvalueof var="ratingAvailable"
																	param="productVO.bvReviews.ratingAvailable"></dsp:getvalueof>
																<dsp:getvalueof var="ratings"
																	param="productVO.bvReviews.averageOverallRating"
																	vartype="java.lang.Integer" />
																<dsp:getvalueof var="rating" value="${ratings * 10}"
																	vartype="java.lang.Integer" />
																<dsp:getvalueof param="productVO.bvReviews.totalReviewCount"
																	var="totalReviewCount" />
																<dsp:getvalueof var="fltValue"
																	param="productVO.bvReviews.averageOverallRating" />
						
																<c:choose>
																	<c:when
																		test="${ratings ne null && ratings ne '0' && (totalReviewCount eq '1' || totalReviewCount gt '1') }">
																		<c:choose>
																			<c:when test="${totalReviewCount == 1}">
																				<div class="metaFeedback"><span title="${fltValue}" class="ratingTxt prodReview prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel"><dsp:valueof param="productVO.bvReviews.ratingsTitle"/></span></span><span class="reviewTxt">(<dsp:valueof param="productVO.bvReviews.totalReviewCount"/><bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" />)</span></div>
																			</c:when>
																			<c:otherwise>
																				<div class="metaFeedback"><span title="${fltValue}" class="ratingTxt prodReview prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>"><span class="ariaLabel"><dsp:valueof param="productVO.bvReviews.ratingsTitle"/></span></span><span class="reviewTxt">(<dsp:valueof param="productVO.bvReviews.totalReviewCount"/><bbbl:label key='lbl_reviews_count' language="${pageContext.request.locale.language}" />)</span></div>
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:set var="writeReviewLink"><bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" /></c:set>
																		<div class="metaFeedback"><span class="ratingTxt prodReview"><span class="ariaLabel"><dsp:valueof param="productVO.bvReviews.ratingsTitle"/></span> </span><span class="writeReview reviewTxt"><a href="${finalUrl}?writeReview=true"  title="${writeReviewLink}" role="link" aria-label="${writeReviewLink} ${lblAboutThe} ${productName}"><bbbl:label key='lbl_grid_write_review_link' language="${pageContext.request.locale.language}" /></a></span></div>
																	</c:otherwise>
																</c:choose>
															</dsp:oparam>
														</dsp:droplet>
										</c:if>
										</tbody>
									</div>
							<dsp:getvalueof var="isIntlRestricted"
								param="skuVO.intlRestricted" />
							<dsp:getvalueof var="isCustomizableRequired"
								param="skuVO.customizableRequired" />

							<c:choose>
								<c:when test="${isInternationalCustomer && isIntlRestricted}">
									<div class="button button_secondary button_disabled">
										<dsp:input bean="CartModifierFormHandler.addItemToOrder" 
											type="submit" name="submit" value="ADD TO CART "
											disabled="true">
											<%-- <dsp:tagAttribute name="aria-pressed" value="false"/> --%>
											<dsp:tagAttribute name="role" value="button" />
											<%--   <dsp:tagAttribute name="class" value="btnSecondary button-Small"/> --%>
										</dsp:input>
									</div>
									<div class="notAvailableIntShipMsg padTop_15 cb clearfix">
										<bbbl:label key="lbl_plp_intl_restrict_list_msg"
											language="${pageContext.request.locale.language}" />
									</div>
								</c:when>
								<c:when test="${isCustomizableRequired}">
									<div class="button button_secondary button_disabled">
										<dsp:input bean="CartModifierFormHandler.addItemToOrder" 
											type="submit" name="submit" value="ADD TO CART"
											disabled="true">
											<%-- <dsp:tagAttribute name="aria-pressed" value="false"/> --%>
											<dsp:tagAttribute name="role" value="button" />
											<%--   <dsp:tagAttribute name="class" value="btnSecondary button-Small"/> --%>
										</dsp:input>
									</div>
								</c:when>
								<c:otherwise>
									<div class="button button_secondary" tabindex="0" aria-label="Add ${productName} to Your Cart">
										<dsp:input bean="CartModifierFormHandler.addItemToOrder" 
											type="submit" name="submit" value="ADD TO CART" title="Add ${productName} to Your Cart">
											<%-- <dsp:tagAttribute name="aria-pressed" value="false"/> --%>
											<dsp:tagAttribute name="role" value="button" />
											<dsp:tagAttribute name="aria-label" value="Add ${productName} to Your Cart" />
											<%--  <dsp:tagAttribute name="class" value="btnSecondary button-Small"/> --%>
										</dsp:input>

									</div>

								</c:otherwise>
							</c:choose>
							<div class="clear"></div>
				</div>
				</dsp:form>
			</div>

			</dsp:oparam>
			</dsp:droplet>
			</dsp:oparam>
			<dsp:oparam name="error">
				<dsp:getvalueof var="requestURL" param="requestURL" scope="request"/>
				<dsp:getvalueof var="errorMsg" param="errorMsg" scope="request"  />
			</dsp:oparam>
			<dsp:oparam name="empty">
			<dsp:getvalueof var="requestURL" param="requestURL" scope="request"/>
			<dsp:getvalueof var="errorMsg" param="errorMsg" scope="request"  />

			</dsp:oparam>
			</dsp:droplet>
			
		<%--BBBSL-6574 | Printing Certona WS call on source --%>
			<c:choose>
	            <c:when test="${not empty errorMsg }">
	            			   <!--
					 <div id="certonaRequestResponse" class="hidden"> 
						<ul> 
							<li id="requestURL"><dsp:valueof value="${requestURL}" valueishtml="true" /></li>
							<li id="errorMsg"><dsp:valueof value="${errorMsg}" valueishtml="true" /></li>
						</ul> 
					</div>  
					 --> 
	            </c:when>
	            <c:otherwise>
	             <!--
						 <div id="certonaRequestResponse" class="hidden"> 
							<ul> 
								<li id="requestURL"><dsp:valueof value="${requestURL}" valueishtml="true" /></li>
								<li id="responseXML"><dsp:valueof value="${responseXML}" valueishtml="true"/></li>  
							</ul>
						</div>
						 -->
	            </c:otherwise>
	        </c:choose>
	        
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
	
	</div>
	</div>
<script type="text/javascript">

setTimeout(function(){
	var recomSkuId ='${recomSkuId}';
	resx.pageid = "${pageIdCertona}";
	resx.links = "${linksCertona}" + "${prodIdList}" + recomSkuId;

	if (typeof BBB.loadCertonaJS === "function") { BBB.loadCertonaJS(); }
}, 100);

</script>
	</c:if>

	<script type="text/javascript">
	function addItemCartOmniture(el) {       
		var productId  = $(el).find("input[name=prodId]").val();
		var skuId   = $(el).find("input[name=skuId]").val();
		var $priceWrapper  = $(el).find(".prodPrice");                      
		var $priceWrapper = $(el).find(".prodPrice"),
			_itemPrice = 0,
			regexp =  /[0-9.]/g;
		if ($priceWrapper[0].childNodes.length === 1) {
			_itemPrice = $priceWrapper.text();
		} else {
			_itemPrice = $priceWrapper.find('.isPrice').text();
		}              
		_itemPrice = _itemPrice.match(regexp).join("");
		if (typeof addItemCartLastMinute === 'function'){
			addItemCartLastMinute(productId, skuId, _itemPrice);              
		}                              
	}


    /*function pdpOmnitureProxy(event1,desc) {

		   if(event1 == 'pfm') {

			   if (typeof s_crossSell === 'function') { s_crossSell(desc); }
		   }

	   }*/
</script>
	</div>
</dsp:page>
