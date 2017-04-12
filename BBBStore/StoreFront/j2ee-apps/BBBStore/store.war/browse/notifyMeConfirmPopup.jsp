<dsp:page>


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

<c:set var="BedBathUSSite" scope="request">
	<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
</c:set>
<c:set var="BuyBuyBabySite" scope="request">
	<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
</c:set>
<c:set var="BedBathCanadaSite" scope="request">
	<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
</c:set>
<c:choose>
	<c:when test="${currentSiteId eq BedBathUSSite}">
		<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_us"/></c:set>
	</c:when>
	<c:when test="${currentSiteId eq BuyBuyBabySite}">
		<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_baby"/></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="MapQuestOn" scope="request"><tpsw:switch tagName="MapQuestTag_ca"/></c:set>
	</c:otherwise>
</c:choose>

<div title="<bbbl:label key='lbl_notifyme_email_me_when_instock' language="${pageContext.request.locale.language}" />">

<dsp:droplet name="ProductDetailDroplet">
	<dsp:param name="id" param="productId" />
	<dsp:param name="isMainProduct" value="true"/>
	<dsp:param name="siteId" param="siteId"/>
	<dsp:param name="skuId" param="skuId"/>
		<dsp:oparam name="output">
			<div class="product clearfix">
			<p><bbbl:label key='lbl_notifyme_confirm_placeholder' language="${pageContext.request.locale.language}" /></p>
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
					<dsp:getvalueof var="isBopus" param="pSKUDetailVO.bopusAllowed" />
					<dsp:getvalueof var="isVdcSku" param="pSKUDetailVO.vdcSku" />
					<c:if test="${MapQuestOn}">
						
						<a class="changeStore upperCase <c:if test='${!isBopus || isVdcSku}'> disabled </c:if>" title="<bbbl:label key='lbl_notifyme_find_store_near_you' language="${pageContext.request.locale.language}" />" href="#"><bbbl:label key='lbl_notifyme_find_store_near_you' language="${pageContext.request.locale.language}" /></a>
						
					</c:if>
				</div>
			</div>
			<jsp:useBean id="holderMap" class="java.util.HashMap" scope="request"/>
			<c:set target="${holderMap}" property="email"><c:out value="${eMail}"/></c:set>
			<c:set target="${holderMap}" property="prodName"><dsp:valueof param="productVO.name"/></c:set>
			<p><bbbl:label key='lbl_notifyme_request_emailsent1' language="${pageContext.request.locale.language}" placeHolderMap="${holderMap}"/></p>
			<div class="formRow clearfix">
				<div class="button">
					<input type="button" value="Close" class="close-any-dialog" role="button" aria-pressed="false" />
				</div>
			</div>
			</div>
		</dsp:oparam>
</dsp:droplet>

</div>
</dsp:page>

