<%@ taglib uri="http://htmlcompressor.googlecode.com/taglib/compressor" prefix="compress" %> 
<dsp:page>
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet" />
	<dsp:importbean
		bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/RegistryInfoDisplayDroplet" />
	<dsp:importbean var="SystemErrorInfo" bean="/com/bbb/utils/SystemErrorInfo"/>
	<dsp:importbean var="Profile" bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/com/bbb/certona/CertonaConfig" />
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof id="appIdCertona"
		bean="CertonaConfig.siteIdAppIdMap.${appid}" />

	<dsp:getvalueof var="contextPath"
		bean="/OriginatingRequest.contextPath" />

	<dsp:getvalueof var="registryId" param="registryId" />
	<dsp:getvalueof var="eventType" param="eventType" />
	<dsp:getvalueof var="skuList" param="skuList" />
	<dsp:getvalueof var="certonaSkuList" param="${certonaSkuList}" />
	<dsp:getvalueof var="itemList" param="itemList" />
	<c:set var="lblAboutThe">
		<bbbl:label key="lbl_accessibility_about_the"
			language="${pageContext.request.locale.language}" />
	</c:set>
	
	
	<c:set var="ShipMsgDisplayFlag" scope="request"><bbbc:config key="ShipMsgDisplayFlag" configName="FlagDrivenFunctions"/></c:set>
	<c:set var="shippingAttributesList">
		<bbbc:config key="shippingAttributesList" configName="ContentCatalogKeys" />
	</c:set>
	<dsp:getvalueof var="isMyItemsCheckList" param="isMyItemsCheckList"/>
	<dsp:getvalueof var="c1id" param="c1id" />
	<dsp:getvalueof var="addedCount" param="addedCount" />
	
	<c:set var="disableLazyLoadS7Images">
		<bbbc:config key="disableLazyLoadS7ImagesFlag" configName="FlagDrivenFunctions" />
	</c:set>
	
	<c:set var="prodImageAttrib">class="productImage noImageFound" src</c:set>
	
	<c:if test="${(not empty disableLazyLoadS7Images and not disableLazyLoadS7Images)}">
		<c:set var="prodImageAttrib">class="productImage lazyLoad loadingGIF" src="${imagePath}/_assets/global/images/blank.gif" data-lazyloadsrc</c:set>
	</c:if>

	<c:if test = "${isMyItemsCheckList eq 'true' && ( not empty c1id)}"> 
	<div class="grid_12 seeAllItemsBtn omega alpha">
		<div class="grid_3 omega clearfix fr">
			<c:set var="capitalizeTextClass" value="" />
			<c:if test="${appid ne 'BuyBuyBaby'}">
				<c:set var="capitalizeTextClass" value="allCaps" />
			</c:if>
			<dsp:a href="../view_registry_owner.jsp" bean="GiftRegistryFormHandler.viewEditRegistry" value=""  iclass="${capitalizeTextClass} button-Small btnSecondary fr" requiresSessionConfirmation="false">
			<dsp:param name="registryId" value="${registryId}" />
			<dsp:param name="eventType" value="${eventType}" />
			<span class="txtOffScreen">Edit my ${eventType} registry .   </span>
			<c:choose>
			<c:when test="${appid eq 'BuyBuyBaby'}">
				 <span><bbbl:label key='lbl_ic_see_all_items' language="${pageContext.request.locale.language}" /></span>
			</c:when>
			<c:otherwise>
				 <strong><bbbl:label key='lbl_ic_see_all_items' language="${pageContext.request.locale.language}" /></strong>
			</c:otherwise>
			</c:choose>
			</dsp:a>
		</div>
	</div>
	</c:if>
	<dsp:droplet name="Switch">
		<dsp:param name="value" bean="Profile.transient" />
		<dsp:oparam name="false">
			<dsp:getvalueof var="userId" bean="Profile.id" />
		</dsp:oparam>
		<dsp:oparam name="true">
			<dsp:getvalueof var="userId" value="" />
		</dsp:oparam>
	</dsp:droplet>
	<dsp:include page="/_includes/third_party_on_of_tags.jsp" />
	<div class="grid_12 alpha omega marTop_10">

		<c:set var="topRegistryMax" scope="request">
			<bbbc:config key="TopRegistryMax" configName="CertonaKeys" />
		</c:set>

		<c:if test="${CertonaOn}">
			<dsp:droplet name="CertonaDroplet">
				<dsp:param name="scheme" value="gru_tri" />
				<dsp:param name="giftregid" value="${registryId}" />
				<dsp:param name="userid" value="${userId}" />
				<dsp:param name="context" value="${itemList}" />
				<dsp:param name="number" value="${topRegistryMax}" />
				<dsp:param name="siteId" value="${appid}" />
				<dsp:param name="exitemid" value="${certonaSkuList}" />
				<dsp:oparam name="output">
			<%--BBBSL-6574 --%>
				<dsp:getvalueof var="requestURL" param="certonaResponseVO.requestURL" scope="request"/>
				<dsp:getvalueof var="responseXML" param="certonaResponseVO.responseXML" scope="request"  />
		
			   <!--
			 <div id="certonaRequestResponse" class="hidden"> 
				<ul> 
					<li id="requestURL"><dsp:valueof value="${requestURL}" valueishtml="true" /></li>
					<li id="responseXML"><dsp:valueof value="${responseXML}" valueishtml="true"/></li>  
				</ul> 
			</div>  
			 --> 
			 		<input type="hidden" class="certonaItemList" value="${itemList}" />

					<dsp:getvalueof var="skuDetailVOList"
						param="certonaResponseVO.resonanceMap.${'gru_tri'}.skuDetailVOList" />

					<dsp:getvalueof var="pageIdCertona"
						param="certonaResponseVO.pageId" />
					<dsp:getvalueof var="linksCertona"
						param="certonaResponseVO.resxLinks" />

					<div class="certonaProducts clearfix" id="gru_tri">
						<dsp:getvalueof var="sorting" param="sorting" />
						<dsp:getvalueof var="view" param="view" />
						<c:if test="${empty sorting}">
							<dsp:getvalueof var="sorting" value="1" />
						</c:if>
						<c:if test="${empty view}">
							<dsp:getvalueof var="view" value="1" />
						</c:if>

						<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:getvalueof var="idx" param="index" />
							<dsp:form name="frmCertonaProduct" method="post"
								id="formID${idx}" onsubmit="return addItemRegistryOmniTest(this)">
								<dsp:param name="array" value="${skuDetailVOList}" />
								<dsp:param name="elementName" value="skuVO" />
								<dsp:oparam name="outputStart">
									<h6>
										<bbbl:label key="lbl_reg_top_item"
											language="${pageContext.request.locale.language}" />
									</h6>
								</dsp:oparam>
								<dsp:oparam name="output">
									<div class="certonaProduct grid_4 alpha omega clearfix">
										<dsp:getvalueof var="skuId" param="skuVO.skuId" />
										<dsp:getvalueof var="customizableRequired"
											param="skuVO.customizableRequired" />
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param name="array"
												param="certonaResponseVO.resonanceMap.${'gru_tri'}.skuParentProductMap" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="skuIdParam" param="key" />
												<c:if test="${skuIdParam eq skuId}">
													<dsp:getvalueof var="productIdParam"
														param="certonaResponseVO.resonanceMap.${'gru_tri'}.skuParentProductMap.${skuIdParam}" />

												</c:if>
											</dsp:oparam>
										</dsp:droplet>

										<dsp:getvalueof var="eventType" param="eventType" />

										<dsp:input bean="GiftRegistryFormHandler.productId"
											type="hidden" name="prodId" value="${productIdParam}" />
										<dsp:input bean="GiftRegistryFormHandler.skuIds" type="hidden"
											name="skuId" value="${skuId}" />
										<dsp:input bean="GiftRegistryFormHandler.quantity"
											type="hidden" name="qty" value="1" />
										<dsp:input bean="GiftRegistryFormHandler.registryId"
											type="hidden" name="registryId" value="${registryId}" />
										<dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="updateItemRegistry" />
										<dsp:input bean="GiftRegistryFormHandler.queryParam"  type="hidden" value="eventType=${eventType}&registryId=${registryId}&sorting=${sorting}&view=${view}" />
										<%-- Client DOM XSRF | Part -1
										<dsp:input bean="GiftRegistryFormHandler.successURL"
											type="hidden"
											value="/store/giftregistry/view_registry_owner.jsp?eventType=${eventType}&registryId=${registryId}&sorting=${sorting}&view=${view}" />
										<dsp:input bean="GiftRegistryFormHandler.errorURL"
											type="hidden"
											value="/store/giftregistry/view_registry_owner.jsp?eventType=${eventType}&registryId=${registryId}&sorting=${sorting}&view=${view}" /> --%>
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

										<dsp:getvalueof var="smallImage"
											param="skuVO.skuImages.smallImage" />

										<div class="productContent">
											<c:set var="productName">
												<dsp:valueof param="skuVO.displayName" valueishtml="true" />
											</c:set>
											<dsp:a iclass="prodImg" page="${finalUrl}?skuId=${skuId}"
												title="${productName}"
												onclick="javascript:s_crossSell('registry update, top registry items');">
												<c:choose>
													<c:when test="${empty smallImage}">
														<img class="productImage"
															src="${imagePath}/_assets/global/images/no_image_available.jpg"
															height="83" width="83"
															alt="<dsp:valueof param="productVO.name"/>" />
													</c:when>
													<c:otherwise>
														<img ${prodImageAttrib}="${scene7Path}/${smallImage}" 
															height="83" width="83" alt="<dsp:valueof param="productVO.name"/>" />
													</c:otherwise>
												</c:choose>
											</dsp:a>
											<ul class="prodInfo">
												<li class="prodName"><dsp:a
														page="${finalUrl}?skuId=${skuId}" title="${productName}"
														onclick="javascript:s_crossSell('registry update, top registry items');">${productName}</dsp:a></li>

												<li class="prodPrice prodPriceSALE"><dsp:getvalueof
														var="certonaPrice" value="" scope="request" /> <dsp:getvalueof
														var="isSale" param="skuVO.onSale" /><compress:html removeIntertagSpaces="true">  <dsp:include
														page="/cart/cart_product_details_price.jsp">
														<dsp:param name="product" value="${productIdParam}" />
														<dsp:param name="sku" param="skuVO.skuId" />
														<dsp:param name="isSale" value="${isSale}" />
														<dsp:param name="priceLabelCodeSKU" param="skuVO.pricingLabelCode" />
														<dsp:param name="inCartFlagSKU" param="skuVO.inCartFlag" />
														<dsp:param name="showInCartPrice" value="true" />
													</dsp:include> <input type="hidden" name="certonaPrice"
													value="${omniPrice}" /> <dsp:input
														bean="GiftRegistryFormHandler.addedItemPrice"
														type="hidden" name="itemPrice" value="${omniPrice}" /></compress:html></li>

												<li class="prodSubInfo clearfix">
													<ul>
													<c:set var="showShipCustomMsg" value="true"/>
														<dsp:droplet name="/atg/dynamo/droplet/ForEach">
															<dsp:param name="array" param="skuVO.skuAttributes" />
															<dsp:param name="elementName" value="attributeVOList" />
															<dsp:oparam name="output">
																<dsp:getvalueof var="pageName" param="key" />
																<c:if test="${pageName eq 'RLP'}">
																	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
																		<dsp:param param="attributeVOList" name="array" />
																		<dsp:param name="elementName" value="attributeVO" />
																		<dsp:param name="sortProperties" value="+priority" />
																		<dsp:oparam name="output">
																		<dsp:getvalueof var="attrId" param="attributeVO.attributeName" />
																		<c:if test="${fn:contains(shippingAttributesList,attrId)}">
																			<c:set var="showShipCustomMsg" value="false"/>
																		</c:if>
																			<li class="highlight prodAttribWrapper"><dsp:valueof
																					param="attributeVO.attributeDescrip"
																					valueishtml="true" /></li>
																		</dsp:oparam>
																	</dsp:droplet>
																</c:if>
															</dsp:oparam>
														</dsp:droplet>
													</ul>
												</li>
													
													<c:if test="${ShipMsgDisplayFlag && showShipCustomMsg}">
														<li><dsp:valueof param="skuVO.displayShipMsg" valueishtml="true" /></li>
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
															<dsp:getvalueof
																param="productVO.bvReviews.totalReviewCount"
																var="totalReviewCount" />
															<dsp:getvalueof var="fltValue"
																param="productVO.bvReviews.averageOverallRating" />

															<c:choose>

																<c:when test="${ratings ne null && ratings ne '0' && (totalReviewCount eq '1' || totalReviewCount gt '1') }">
																	<c:choose>
																		<c:when test="${totalReviewCount == 1}">
																			<li class="clearfix metaFeedback">
																				<span title="${fltValue}" class="ratingTxt prodReview prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>">
																					<span class="ariaLabel">
																						<dsp:valueof param="productVO.bvReviews.ratingsTitle" />
																					</span>
																				</span>
																				<span class="reviewTxt">(<dsp:valueof param="productVO.bvReviews.totalReviewCount" />
																					<bbbl:label key='lbl_review_count' language="${pageContext.request.locale.language}" />)
																				</span>
																			</li>
																		</c:when>
																		<c:otherwise>
																			<li class="clearfix metaFeedback">
																				<span title="${fltValue}" class="ratingTxt prodReview prodReview<fmt:formatNumber value="${fltValue * 10}" maxFractionDigits="0"/>">
																					<span class="ariaLabel"><dsp:valueof param="productVO.bvReviews.ratingsTitle" /></span>
																				</span>
																				<span class="reviewTxt">(<dsp:valueof param="productVO.bvReviews.totalReviewCount" />
																					<bbbl:label key='lbl_reviews_count' language="${pageContext.request.locale.language}" />)
																				</span>
																			</li>
																		</c:otherwise>
																	</c:choose>
																</c:when>
																<c:otherwise>
																	<c:set var="writeReviewLink">
																		<bbbl:label key="lbl_grid_write_review_link" language="${pageContext.request.locale.language}" />
																	</c:set>
																	<li class="metaFeedback">
																		<span class="ratingTxt prodReview">
																			<span class="ariaLabel">
																				<dsp:valueof param="productVO.bvReviews.ratingsTitle" />
																			</span> 
																		</span>
																		<span class="writeReview reviewTxt">
																			<a href="${finalUrl}?writeReview=true" title="${writeReviewLink}" role="link" aria-label="${writeReviewLink} ${lblAboutThe} ${productName}">
																				<bbbl:label key='lbl_grid_write_review_link' language="${pageContext.request.locale.language}" />
																			</a>
																		</span>
																	</li>
																</c:otherwise>

															</c:choose>
														</dsp:oparam>
													</dsp:droplet>
												</c:if>
											</ul>
											<c:choose>
												<c:when test="${customizableRequired}">
												
													<div class="button button_secondary marBottom_10 button_active button_disabled">
														<dsp:input disabled="true"
															bean="GiftRegistryFormHandler.addItemToGiftRegistryFromCetona"
															type="submit" name="submit" value="Add to Registry">
															<dsp:tagAttribute name="aria-pressed" value="false" />
															<dsp:tagAttribute name="role" value="button" />
														</dsp:input>
													</div>
												</c:when>
												<c:otherwise>
													<div class="button button_secondary marBottom_10 button_active">
														<dsp:input
															bean="GiftRegistryFormHandler.addItemToGiftRegistryFromCetona"
															type="submit" name="submit" value="Add to Registry">
															<dsp:tagAttribute name="aria-pressed" value="false" />
															<dsp:tagAttribute name="role" value="button" />
														</dsp:input>
													</div>
												</c:otherwise>
											</c:choose>


										</div>
									</div>
								</dsp:oparam>
							</dsp:form>
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

					</div>
				</dsp:oparam>
				<dsp:oparam name="error">
				<dsp:getvalueof var="requestURL" param="requestURL" scope="request"/>
				<dsp:getvalueof var="errorMsg" param="errorMsg" scope="request"  />
				   <!--
				 <div id="certonaRequestResponse" class="hidden"> 
					<ul> 
						<li id="requestURL"><dsp:valueof value="${requestURL}" valueishtml="true" /></li>
						<li id="errorMsg"><dsp:valueof value="${errorMsg}" valueishtml="true" /></li>
					</ul> 
				</div>  
				 --> 
				</dsp:oparam>
				<dsp:oparam name="empty">
						<dsp:getvalueof var="requestURL" param="requestURL" scope="request"/>
						<dsp:getvalueof var="errorMsg" param="errorMsg" scope="request"  />
				   <!--
						 <div id="certonaRequestResponse" class="hidden"> 
							<ul> 
								<li id="requestURL"><dsp:valueof value="${requestURL}" valueishtml="true" /></li>
								<li id="errorMsg"><dsp:valueof value="${errorMsg}" valueishtml="true" /></li>
							</ul> 
						</div>  
						 --> 
				</dsp:oparam>
			</dsp:droplet>
		</c:if>

	</div>

	<script type="text/javascript">
	var resx = new Object();
	resx.appid = "${appIdCertona}";
	resx.links = '${linksCertona}' +"${itemList}";
	resx.pageid = "${pageIdCertona}";
	resx.customerid = "${userId}";
	resx.itemid="${itemList}";
	resx.event="registry+"+"${eventType}";
	resx.transactionid = "${registryId}";
		if (typeof certonaResx === 'object') {
			certonaResx.run();
		}

   (function() {
			var cAddItemFlag = $('input[name="cAddItemFlag"]').val() || "false", cfProdId = $(
					'input[name="cProd"]').val() || 0, cfSkuId = $(
					'input[name="cSkuId"]').val() || 0, cfQty = $(
					'input[name="cQty"]').val() || 0, cfPrice = $(
					'input[name="cPrice"]').val() || 0;

        if (cAddItemFlag && cAddItemFlag.trim().toLowerCase() === "true") {
            resx.appid = "${appIdCertona}";
				resx.links = '${linksCertona}' + '${linkStringNonRecproduct}'
						+ '${productList}';
            resx.pageid = "${pageIdCertona}";
            resx.customerid = "${userId}";

            resx.event = "op_Registry+${eventType}";
            resx.itemid = cfProdId;
            resx.qty = cfQty;
            resx.price = ((""+cfPrice).replace('$','') * cfQty);
            resx.transactionid = "${registryId}";
				if (typeof certonaResx === 'object') {
					certonaResx.run();
				}
				/*if (typeof s !== 'undefined'
						&& typeof addItemRegistryOmniture === 'function') {
                addItemRegistryOmniture({
						'product' : ';' + cfProdId + ";;;event22=" + cfQty
								+ "|event23=" + cfPrice + ';eVar30=' + cfSkuId,
                    'var1': 'Top Registry Items (registry page)',
                    'var23': '${eventType}',
                    'var24': '${registryId}',
                    'var46': 'registry update, top registry items'
                });
            }*/
        }
    }());
   function addItemRegistryOmniTest(el) { 
		var cfProdId=$(el).find('input[name="prodId"]').val(),
		cfSkuId=$(el).find('input[name="skuId"]').val(),
		cfQty=$(el).find('input[name="qty"]').val(),
		cfPrice=$(el).find('input[name="itemPrice"]').val();
			if (typeof s !== 'undefined' && typeof addItemRegistryOmniture === 'function') {
	            addItemRegistryOmniture({
						'product' : ';' + cfProdId + ";;;event22=" + cfQty
								+ "|event23=" + cfPrice + ';eVar30=' + cfSkuId,
	                'var1': 'Top Registry Items (registry page)',
	                'var23': '${eventType}',
	                'var24': '${registryId}',
	                'var46': 'registry update, top registry items'
	            },'topRegistryItem');
        }
       return true;
    }
</script>

</dsp:page>
