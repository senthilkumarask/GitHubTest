<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet" />
<dsp:importbean bean="/com/bbb/common/droplet/ListPriceSalePriceDroplet" />
<dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler" />
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/commerce/catalog/ProductLookup" />
<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet" />
<dsp:importbean bean="/atg/multisite/SiteContext" />
<dsp:importbean bean="/atg/commerce/ShoppingCart" />
<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler" />
<dsp:importbean bean="/atg/multisite/Site" />
<dsp:importbean bean="/com/bbb/certona/CertonaConfig" />
<dsp:importbean bean="/atg/userprofiling/Profile" var="Profile" />
<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
<dsp:importbean bean="/atg/commerce/promotion/ClosenessQualifierDroplet" />
<dsp:importbean bean="/com/bbb/profile/session/BBBSavedItemsSessionBean" />
<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
<dsp:importbean bean="/com/bbb/commerce/cart/TopStatusChangeMessageDroplet" />
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />
<dsp:importbean bean="/atg/multisite/Site" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/SKUWishlistDetailDroplet"/>
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
<dsp:getvalueof var="priceMessageVoList" param="priceMessageVoList"/>
<dsp:getvalueof var="reqFrom" param="reqFrom" />
<c:if test="${empty reqFrom}">
	<c:set var="reqFrom" value="merge"/>
</c:if>
<dsp:page>

<dsp:droplet name="ForEach">
		<dsp:param name="array" value="${priceMessageVoList}" />
		<dsp:oparam name="output">
		<dsp:getvalueof var="priceMessageVO" param="element" />
		<c:if test="${(not empty priceMessageVO)}">
				<dsp:getvalueof var="transient" bean="Profile.transient" />
						<c:if test="${transient == 'false'}">
						<c:choose>
						<c:when test="${priceMessageVO.priceChange && !priceMessageVO.flagOff}">
							<div id="saveForLaterTopMessaging" class="clearfix grid_10 alpha omega">
								<div class="genericRedBox savedItemChangedWrapper clearfix closeWinWrapper marBottom_10 changeStoreItemWrap">
									<bbbt:textArea key="txt_item_change" language="${pageContext.request.locale.language}" />
									<ul class="clearfix">
									<c:choose>
										<c:when test="${empty skuImageURL}">
											<li class="fl liIMG grid-2"><img src="${imagePath}/_assets/global/images/no_image_available.jpg" width="63" height="63" alt="${skuDisplayName}" title="${skuDisplayName}" /></li>
										</c:when>
										<c:otherwise>
											<li class="fl liIMG grid-2"><img class="productImage" src="${scene7Path}/${skuImageURL}" width="63" height="63" alt="${skuDisplayName}" title="${skuDisplayName}" /></li>
										</c:otherwise>
									</c:choose>
									<li class="fl liINFO padLeft_10 grid-7">
										<ul class="prodInfo clearfix">
											<li class="padBottom_10"><c:choose>
												 	<c:when test="${priceMessageVO.flagOff or priceMessageVO.disableLink}">
														<span class="productName disableText">${skuDisplayName}</span>
													</c:when>
													<c:otherwise>
														<a class="prodName productName" href="${contextPath}${finalUrl}?skuId=${priceMessageVO.skuId}" title="${skuDisplayName}">${skuDisplayName}</a>
													</c:otherwise>
												</c:choose>
											</li>
											<li class="skuNum"><bbbl:label key="lbl_sfl_sku" language="<c:out param='${language}'/>"/> #${priceMessageVO.skuId}</li>
											<c:choose>
												<c:when test="${!priceMessageVO.flagOff && priceMessageVO.priceChange}">
													<li class="priceDetails"><span class="prodPriceOLD"><span><dsp:valueof value="${priceMessageVO.prevPrice}" converter="currency"/></span></span>&nbsp;&nbsp;<span class="prodPriceNEW"><dsp:valueof value="${priceMessageVO.currentPrice}" converter="currency"/></span></li>
												</c:when>
												<c:otherwise>
													<li class="priceDetails"><span><dsp:valueof value="${priceMessageVO.currentPrice}" converter="currency"/></span></li>
												</c:otherwise>
											</c:choose>
											
										</ul>
									</li>
								</ul>
								<input type="hidden" name="productId" class="productId" value="${priceMessageVO.prodId}" data-change-store-submit="prodId" />
								</div>
								<div class="clear"></div>
							</div>
						</c:when>	
						</c:choose>
						</c:if>
						
						<div class="clear"></div>
			</c:if>
</dsp:oparam>
</dsp:droplet>	
</dsp:page>
