<dsp:page>

<dsp:importbean bean="/com/bbb/selfservice/TBSPDPInventoryDroplet" />
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>
<dsp:importbean bean="/com/bbb/browse/BBBBackInStockFormHandler"/>
<dsp:importbean bean="/atg/multisite/Site" />
<dsp:getvalueof var="appid" bean="Site.id" />

<dsp:setvalue bean="BBBBackInStockFormHandler.catalogRefId" paramvalue="skuId"/>
<dsp:setvalue bean="BBBBackInStockFormHandler.productId" paramvalue="productId"/>
<dsp:setvalue bean="BBBBackInStockFormHandler.productName" paramvalue="productName"/>
<dsp:setvalue bean="BBBBackInStockFormHandler.customerName" paramvalue="fullName"/>
<dsp:setvalue bean="BBBBackInStockFormHandler.emailAddress" paramvalue="email"/>
<dsp:setvalue bean="BBBBackInStockFormHandler.confirmEmailAddress" paramvalue="emailConfirm"/>

<dsp:setvalue bean="BBBBackInStockFormHandler.sendOOSEmail" paramvalue="Submit"/>
<dsp:getvalueof var="sucess" bean="BBBBackInStockFormHandler.customerName"/>
<dsp:getvalueof var="eMail" bean="BBBBackInStockFormHandler.emailAddress"/>
<c:set var="imagePath">
	<bbbc:config key="image_host" configName="ThirdPartyURLs" />
</c:set>
<c:set var="scene7Path">
	<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
</c:set>

<c:set var="TBS_BedBathUSSite" scope="request">
	<bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
</c:set>
<c:set var="TBS_BuyBuyBabySite" scope="request">
	<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
</c:set>
<c:set var="TBS_BedBathCanadaSite" scope="request">
	<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
</c:set>
<c:choose>
	<c:when test="${currentSiteId eq TBS_BedBathUSSite}">
		<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_tbs_us"/></c:set>
	</c:when>
	<c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
		<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_tbs_baby"/></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_tbs_ca"/></c:set>
	</c:otherwise>
</c:choose>

<div title="<bbbl:label key='lbl_notifyme_email_me_when_instock' language="${pageContext.request.locale.language}" />">
<h2><bbbl:label key='lbl_notifyme_email_me_when_instock' language="${pageContext.request.locale.language}" /></h2>

<dsp:droplet name="ProductDetailDroplet">
<dsp:param name="id" param="productId" />
	<dsp:param name="siteId" param="siteId"/>
	<dsp:param name="skuId" param="skuId"/>
	<dsp:param name="isDefaultSku" value="true"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="skuVO" param="pSKUDetailVO" />
			<div class="product clearfix">
			<p class="product-info"><bbbl:label key='lbl_notifyme_confirm_placeholder' language="${pageContext.request.locale.language}" /></p>
			<div class="product clearfix">
				<div class="prodImg fl marRight_10">
					
									<dsp:getvalueof var="productSmallImage"
										param="productVO.productImages.smallImage" />
                                    <dsp:getvalueof var="productMediumImageUrl"
                                        param="productVO.productImages.mediumImage" />
									<dsp:getvalueof var="imageAltText" param="productVO.name"/>
									<c:choose>
										<%-- <c:when test="${not empty productSmallImage && productSmallImage != 'null'}"> --%>
                                        <c:when test="${not empty productMediumImageUrl && productMediumImageUrl != 'null'}">
											<%-- Thumbnail image exists --%>
											<img width="146" height="146"
												src='${scene7Path}/${productMediumImageUrl}'
												title="${getDetailsTitleText}" alt="${imageAltText}" class="noImageFound"/>
										</c:when>
										<c:otherwise>
											<c:choose>
											<c:when test="${imagePath != 'null'}">
												<img 
												src='${imagePath}/_assets/global/images/no_image_available.jpg'
												alt="${imageAltText}" width="146" height="146" />
											</c:when>
											<c:otherwise>
												<img
												src='/_assets/global/images/no_image_available.jpg'
												alt="${imageAltText}" width="146" height="146" />
											</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>
		
		
				</div>
				<div class="prodText fl">
					<h3><dsp:valueof param="productVO.name" valueishtml="true"/></h3>
					<c:if test="${MapQuestOn && not skuVO.ltlItem && skuVO.vdcSku ne true && not skuVO.bopusAllowed}">
						<dsp:a iclass="nearByStoreLinkModal" href="/tbs/selfservice/find_tbs_store.jsp">
							         <dsp:param name="skuid" param="skuId" />
							         <dsp:param name="itemQuantity" value="1" />
							         <dsp:param name="id" param="productId" />
							         <dsp:param name="siteId" value="${appid}" />
							         <dsp:param name="skuId" param="skuId" />
							         <dsp:param name="registryId" param="registryId" />
							         <bbbl:label key='lbl_notifyme_find_store_near_you' language="${pageContext.request.locale.language}" />
					     </dsp:a>
					</c:if>
				</div>
				<div id="nearbyStore" class="reveal-modal" data-reveal>
				</div>
			</div>
			<jsp:useBean id="holderMap" class="java.util.HashMap" scope="request"/>
			<c:set target="${holderMap}" property="email"><c:out value="${eMail}"/></c:set>
			<c:set target="${holderMap}" property="prodName"><dsp:valueof param="productVO.name"/></c:set>
			<p class="product-info"><bbbl:label key='lbl_notifyme_request_emailsent1' language="${pageContext.request.locale.language}" placeHolderMap="${holderMap}"/></p>
			<div class="formRow clearfix hidden">
				<div class="button">
					<input type="button" value="Close" class="close-any-dialog" role="button" aria-pressed="false" />
				</div>
			</div>
			</div>
		</dsp:oparam>
</dsp:droplet>

</div>
<script type="text/javascript">
		$(document).ready(function(){
			$(".nearByStoreLinkModal").attr("data-reveal-id","nearbyStore");
			$(".nearByStoreLinkModal").attr("data-reveal-ajax","true");
		});
</script>
</dsp:page>

