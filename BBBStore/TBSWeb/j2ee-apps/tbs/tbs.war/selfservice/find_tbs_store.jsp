<dsp:page>
    <dsp:importbean bean="/com/bbb/selfservice/TBSNearbyStoreSearchDroplet" />
    <dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/com/bbb/framework/security/ValidateParametersDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/RedirectDroplet"/>
        
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
    <dsp:getvalueof var="productId" value="${param.id}" />
    <dsp:getvalueof var="siteId" value="${param.siteId}" />
    <dsp:getvalueof var="skuId" value="${param.skuId}" />
    <dsp:getvalueof var="skuid" value="${param.skuid}" />
    <dsp:getvalueof var="registryId" value="${param.registryId}" />
    <dsp:getvalueof var="pageFrom" param="pageFrom" />
    <dsp:getvalueof var="storeId" param="storeId" />
    <dsp:getvalueof var="Zipcode" param="Zipcode" />
    <dsp:getvalueof var="latLongMap" param="latLongMap" />
    <dsp:getvalueof var="changedRadius" param="changedRadius" />
    <dsp:getvalueof var="productVO" value=""></dsp:getvalueof>
    <dsp:getvalueof var="SceneSevenOn" value="true"></dsp:getvalueof>
    <dsp:getvalueof var="commerceId" param="commerceId"/>
    <dsp:getvalueof var="shipid" param="shipid"/>
    
        
    <!--<c:set var="Zipcode"><dsp:valueof value="${fn:escapeXml(param.Zipcode)}"/></c:set>
	<c:set var="productId"><dsp:valueof value="${fn:escapeXml(param.id)}"/></c:set>
	<c:set var="siteId"><dsp:valueof value="${fn:escapeXml(param.siteId)}"/></c:set>
	<c:set var="skuid"><dsp:valueof value="${fn:escapeXml(param.skuid)}"/></c:set>
	<c:set var="registryId"><dsp:valueof value="${fn:escapeXml(param.registryId)}"/></c:set>    
	<c:set var="commerceId"><dsp:valueof value="${fn:escapeXml(param.commerceId)}"/></c:set>
    <c:set var="shipid"><dsp:valueof value="${fn:escapeXml(param.shipid)}"/></c:set>-->
    <c:set var="scene7Path">
            <bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
    </c:set> 
	<c:if test="${not empty param.skuid}">
		<c:set var="parameter" value="skuid"></c:set>
		<c:set var="parametervalue" value="${param.skuid}"></c:set>
	</c:if>
	<c:if test="${not empty param.registryId}">
		<c:set var="parameter" value="${parameter};registryId"></c:set>
		<c:set var="parametervalue" value="${parametervalue};${param.registryId}"></c:set>
	</c:if>
	<c:if test="${not empty param.siteId}">
		<c:set var="parameter" value="${parameter};siteId"></c:set>
		<c:set var="parametervalue" value="${parametervalue};${param.siteId}"></c:set>
	</c:if>
	<c:if test="${not empty param.commerceId}">
		<c:set var="parameter" value="${parameter};commerceId"></c:set>
		<c:set var="parametervalue" value="${parametervalue};${param.commerceId}"></c:set>
	</c:if>
	<c:if test="${not empty param.shipid}">
		<c:set var="parameter" value="${parameter};shipid"></c:set>
		<c:set var="parametervalue" value="${parametervalue};${param.shipid}"></c:set>
	</c:if>
	<c:if test="${not empty param.id}">
		<c:set var="parameter" value="${parameter};productId"></c:set>
		<c:set var="parametervalue" value="${parametervalue};${param.id}"></c:set>
	</c:if>
	<c:if test="${not empty param.Zipcode}">
		<c:set var="parameter" value="${parameter};Zipcode"></c:set>
		<c:set var="parametervalue" value="${parametervalue};${param.Zipcode}"></c:set>
	</c:if>
		<dsp:droplet name="ValidateParametersDroplet">
		<dsp:param value="${parameter}" name="paramArray" />
        <dsp:param value="${parametervalue}" name="paramsValuesArray" />
			<dsp:oparam name="error">
				<dsp:droplet name="RedirectDroplet">
					<dsp:param name="url" value="/404.jsp" />
				</dsp:droplet>
			</dsp:oparam>
			</dsp:droplet>
    <input type="hidden" name="registryId" value="${fn:escapeXml(registryId)}" />
    <input type="hidden" name="skuId" value="${fn:escapeXml(skuId)}" />
    <input type="hidden" name="skuid" value="${fn:escapeXml(skuid)}" />
    <input type="hidden" name="siteId" value="${fn:escapeXml(siteId)}" />
    <input type="hidden" name="productId" value="${fn:escapeXml(productId)}" />
    
    <div class="row">
    <div class="small-12 columns">
        <h2>Availability</h2>
    </div>
	<a class="close-reveal-modal" href="javascript:void(0);" aria-hidden="false" aria-label="close">&#215;</a>
	
    <c:if test="${empty pageFrom}">
    <div class="small-12 columns">
        
        <dsp:droplet name="ProductDetailDroplet">
            <dsp:param name="id" value="${productId}" />
            <dsp:param name="siteId" value="${param.siteId}"/>
            <dsp:param name="skuId" value="${skuId}"/>
            <dsp:param name="skuid" value="${skuid}"/>
            <dsp:param name="registryId" param="${param.registryId}" />
            <dsp:param name="isDefaultSku" value="true"/>
            <dsp:oparam name="output">
                <dsp:getvalueof var="productVO" param="productVO" />
                <dsp:getvalueof var="skuVO" param="pSKUDetailVO" />
                <dsp:getvalueof var="giftFlag" param="productVO.giftCertProduct"/>
                
                <div class="small-2 columns">
                    <dsp:getvalueof var="largeImage" param="productVO.productImages.largeImage"/>
                    <dsp:getvalueof var="smallImage" param="productVO.productImages.smallImage"/>
                    <dsp:getvalueof var="thumbnailImage" param="productVO.productImages.thumbnailImage" />
                    <dsp:getvalueof var="productName" param="productVO.name" scope="request"/>
                    <dsp:getvalueof var="refinedNameProduct" param="productVO.refinedName" scope="request"/>
                    <c:choose>
                        <c:when test="${empty largeImage || !SceneSevenOn}">
                            <img id="mainProductImg" src="${imagePath}/_assets/global/images/no_image_available.jpg" class="mainProductImage noImageFound" height="380" width="380" alt="${productName}" onClick="_gaq.push(['_trackEvent', 'zoom', 'hero image', '${refinedNameProduct}']);"/>
                        </c:when>
                        <c:when test="${SceneSevenOn && (not empty largeImage)}">
                            <img id="mainProductImg" src="${scene7Path}/${smallImage}" alt="${productName}" />
                        </c:when>
                        <c:otherwise>
                            <img id="mainProductImg" src="${imagePath}/_assets/global/images/blank.gif" class="mainProductImage noImageFound" height="380" width="380" alt="${productName}" onClick="_gaq.push(['_trackEvent', 'zoom', 'hero image', '${refinedNameProduct}']);"/>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="small-10 columns">
                    <h2 id="productTitle" class="subheader" itemprop="name"><dsp:valueof param="productVO.name" valueishtml="true"/></h2>
                    <h2 class="subheader">SKU#:&nbsp;${param.skuid}</h2>
                </div>
            </dsp:oparam>
        </dsp:droplet>
    </div>
    <c:choose>
        <c:when test="${currentSiteId eq 'TBS_BedBathCanada'}">
            <c:set var="zipCodeClass" value="zipCA" scope="page"/>
            <c:set var="placeholderText"><bbbl:label key="lbl_subscribe_canadazipcode" language="${pageContext.request.locale.language}" /></c:set>
        </c:when>
        <c:otherwise>
            <c:set var="zipCodeClass" value="zipUS" scope="page"/>
            <c:set var="placeholderText"><bbbl:label key="lbl_shipping_new_zip" language="${pageContext.request.locale.language}" /></c:set>
        </c:otherwise>
    </c:choose>
    <div class="small-12 columns">
        <a id="toggleStoreDiv">Change Location</a></p>
        <div id="storeDiv" class="hidden">
        <form id="findTbsStores" name="findTbsStores">
            <div class="small-12 medium-4 columns">
                <input id="Zipcode" aria-labelledby="lblzip errorzip" maxlength="10" placeholder="${placeholderText} *" name="${zipCodeClass}" value="" type="text" aria-required="true" class="escapeHTMLTag" autocomplete="off">
            </div>
            <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
            <c:set var="TBS_BedBathUSSite" scope="request">
                <bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
            </c:set>
            <c:set var="TBS_BuyBuyBabySite" scope="request">
                <bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
            </c:set>
            <c:set var="TBS_BedBathCanadaSite" scope="request">
                <bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
            </c:set>
                        
            <div class="small-12 medium-4 columns">
                <!-- <input id="radius" type="text" placeholder="Search Radius"></input> -->
                <c:set var="radius_default_us"><bbbc:config key="radius_default_us" configName="MapQuestStoreType" /></c:set> 
                <c:set var="radius_default_baby"><bbbc:config key="radius_default_baby" configName="MapQuestStoreType" /></c:set> 
                <c:set var="radius_default_ca"><bbbc:config key="radius_default_ca" configName="MapQuestStoreType" /></c:set>
                <c:set var="radius_range_us"><bbbc:config key="radius_range_us" configName="MapQuestStoreType" /></c:set> 
                <c:set var="radius_range_baby"><bbbc:config key="radius_range_baby" configName="MapQuestStoreType" /></c:set>
                <c:set var="radius_range_ca"><bbbc:config key="radius_range_ca" configName="MapQuestStoreType" /></c:set> 
                <c:set var="radius_range_type"><bbbc:config key="radius_range_type" configName="MapQuestStoreType" /></c:set> 
                <c:set var="radius_range_type_ca"><bbbc:config key="radius_range_type_ca" configName="MapQuestStoreType" /></c:set>
                <c:choose>
                    <c:when test="${currentSiteId eq TBS_BedBathUSSite}">
                        <c:set var="radius_default_selected">${radius_default_us}</c:set> 
                        <c:set var="radius_range">${radius_range_us}</c:set> 
                    </c:when>
                    <c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
                        <c:set var="radius_default_selected">${radius_default_baby}</c:set> 
                        <c:set var="radius_range">${radius_range_baby}</c:set> 
                    </c:when>
                    <c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
                        <c:set var="radius_default_selected">${radius_default_ca}</c:set> 
                        <c:set var="radius_range">${radius_range_ca}</c:set> 
                    </c:when>                                           
                    <c:otherwise>
                        <c:set var="radius_default_selected">${radius_default_us}</c:set> 
                        <c:set var="radius_range">${radius_range_us}</c:set> 
                    </c:otherwise>
                </c:choose>
        
                <select id="radius">
                    <c:forTokens items="${radius_range}" delims="," var="item">
                    <c:choose>
                    <c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
                    <c:choose>
                        <c:when test="${item == radius_default_selected}">
                            <option value="${item}" iclass="default" selected="true">${item} ${radius_range_type_ca}</option>
                        </c:when>
                        <c:otherwise>
                            <option value="${item}">${item} ${radius_range_type_ca} </option>
                        </c:otherwise>
                        </c:choose>
                        </c:when>
                    
                    <c:otherwise>
                    <c:choose>
                        <c:when test="${item == radius_default_selected}">
                            <option value="${item}" iclass="default" selected="true">${item} ${radius_range_type}</option>
                        </c:when>
                        <c:otherwise>
                            <option value="${item}">${item} ${radius_range_type} </option>
                        </c:otherwise>
                    </c:choose>
                    </c:otherwise>
                    </c:choose>
                    </c:forTokens>  
                </select>
            </div>
            <div class="small-12 medium-4 columns no-padding">
                <a id="updateZip" class="tiny button column transactional" 
                    href="/tbs/selfservice/new_tbs_store.jsp?itemQuantity=${param.itemQuantity}&skuid=${param.skuid}&skuId=${param.skuId}&id=${param.id}&commerceId=${param.commerceId}&shipid=${param.shipid}">
                    Update ${placeholderText}
                </a>
            </div>
          </form>
        </div>
            
    </div>
    </c:if>
    <hr/>
    <div class="small-12 columns">
    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
        <dsp:param name="array" bean="Profile.userSiteItems"/>
        <dsp:param name="elementName" value="sites"/>
        <dsp:oparam name="output">
            <dsp:getvalueof id="key" param="key"/>
            <c:if test="${currentSiteId eq key}">
                <dsp:getvalueof id="favouriteStoreId" param="sites.favouriteStoreId" scope="session"/>
            </c:if>
        </dsp:oparam>
    </dsp:droplet>
    <dsp:droplet name="TBSNearbyStoreSearchDroplet">
        <dsp:param name="latLongMap" value="${latLongMap}" />
        <dsp:param name="changedRadius" value="${changedRadius}" />
        <dsp:param name="pageFrom" value="${pageFrom}" />
        <dsp:param name="storeId" value="${storeId}" />
        <dsp:param name="sku" value="${param.skuid}" />
        <dsp:param name="orderedQty" value="${param.itemQuantity}" />
        <dsp:param name="siteId" bean="/atg/multisite/SiteContext.site.id"/>
        <dsp:oparam name="output">
            <ul class="small-block-grid-1 nearbyStoreULS">
                <dsp:droplet name="ForEach">
                    <dsp:param name="array" param="nearbyStores"/>
                    <dsp:param name="elementName" value="StoreDetail"/>
                    <dsp:oparam name="output">
                        <dsp:getvalueof var="counter" param="count"/>
                        <dsp:include page="tbs_store_result_info.jsp">
                            <dsp:param name="productVO" value="${productVO}" />
                            <dsp:param name="counter" value="${counter}" />
                            <dsp:param name="Zipcode" value="${Zipcode}" />
                            <dsp:param name="pageFrom" value="${pageFrom}" />
                            <dsp:param name="StoreDetail" param="StoreDetail" />
                            <dsp:param name="sku" value="${param.skuid}" />
                            <dsp:param name="siteId" bean="/atg/multisite/SiteContext.site.id"/>
                            <dsp:param name="commerceId" param="commerceId"/>
                            <dsp:param name="itemQuantity" param="itemQuantity"/>
                            <dsp:param name="shipid" param="shipid"/>
                            <dsp:param name="itemQuantity" param="itemQuantity"/>
                        </dsp:include>
                    </dsp:oparam>
                </dsp:droplet>
            </ul>
        </dsp:oparam>
        <dsp:oparam name="empty">
            <bbbl:label key="lbl_favoritestore_disablemessage" language="${pageContext.request.locale.language}"/>
        </dsp:oparam>
        <dsp:oparam name="error">
        </dsp:oparam>                                   
    </dsp:droplet>
    </div>
	
    </div>
<%--  New version of view map/get directions --%>
    <c:import url="/selfservice/store/find_store_pdp.jsp" ></c:import>
    

<%-- <c:choose>
    <c:when test="${minifiedJSFlag}">
        <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/legacy/pageJS/min/browse.min.js?v=${buildRevisionNumber}"></script>
    </c:when>
    <c:otherwise>
        <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/pageJS/browse.js?v=${buildRevisionNumber}"></script>
    </c:otherwise>
</c:choose> --%>
<script>
BBB.validationUtils.localizeAndValidate({
    form: '#findTbsStores',
    validatorExtends: {
        showErrorSummaryMsg: true
    }
});
if (typeof findInStore === 'function') { findInStore('${productId}');}

</script>
</dsp:page>