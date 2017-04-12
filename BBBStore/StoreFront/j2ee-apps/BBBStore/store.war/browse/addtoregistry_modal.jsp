<dsp:page>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${appid}"/>
	
	 <dsp:droplet name="Switch">
   <dsp:param name="value" bean="Profile.transient"/>
       <dsp:oparam name="false">
	     <dsp:getvalueof var="userId" bean="Profile.id"/>
       </dsp:oparam>
       <dsp:oparam name="true">
	     <dsp:getvalueof var="userId" value=""/>
       </dsp:oparam>
  </dsp:droplet>
<dsp:getvalueof var="porchWarning" param="porchWarning" />  
<dsp:getvalueof var="jsonObj" param="jsonObj"/>
<dsp:getvalueof var="personalizedImageUrls" param="personalizedImageUrls"/>
<c:set var="scene7Path">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
</c:set>
<dsp:droplet name="/com/bbb/commerce/giftregistry/droplet/ATRDisplayDroplet">
	<dsp:param name="jsonObj" value="${jsonObj}"/>
	<dsp:param name="skuId" param="skuId"/>
	<dsp:oparam name="output">
		<dsp:getvalueof var="registryId" param="registryId"/>
		<dsp:getvalueof var="registryName" param="registryName"/>
		<dsp:getvalueof var="quantity" param="quantity"/>
		<dsp:getvalueof var="price" param="price"/>
		<dsp:getvalueof var="largeImagePath" param="skuDetailVO.skuImages.largeImage"/>
		<dsp:getvalueof var="displayName" param="skuDetailVO.displayName"/>
		<dsp:getvalueof var="prodId" param="prodId"/>
	</dsp:oparam>
</dsp:droplet>
	<c:if test="${empty jsonObj}">
		<dsp:getvalueof var="registryId" param="registryId" />
		<dsp:getvalueof var="registryName" param="registryName" />
		<dsp:getvalueof var="quantity" param="totQuantity" />
		<dsp:getvalueof var="prodId" param="prodId" />

	</c:if>
	<c:choose>
	<c:when test="${not empty personalizedImageUrls}">
		<c:set var="imgSrc">
			${personalizedImageUrls}
		</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="imgSrc">
			${scene7Path}/${largeImagePath}
		</c:set>
	</c:otherwise>
</c:choose>
<div id="addtoregistry_rr"></div>
<div id="addModal">
  <div class="addedToRegistryText">
  <span class="icon-checkmark" aria-hidden='true'></span>
	<jsp:useBean id="placeHolderMapReg" class="java.util.HashMap" scope="request"/>
	<c:set target="${placeHolderMapReg}" property="registryName" value="${registryName}"/>
  	<span class="modalHead" aria-hidden='false'><bbbl:label key="lbl_added_registry_atr" placeHolderMap="${placeHolderMapReg}" language ="${pageContext.request.locale.language}"/></span>
  </div>
  <!-- <div class="addCartModalClose">
  <span class="icon-times"></span>
  </div> -->
  	<div class="addCartModalCheckout">
	        <div id="cartAdded">
		        <img class="mainProductImg" src="${imgSrc}" class="mainProductImage" alt="${displayName}">
		        <p class="registryAddedTitle">${displayName}</p>
				<p class="registryAddedQuant"><bbbl:label key="lbl_pdp_product_quantity"
				language="<c:out param='${language}'/>" />&nbsp;<span class='cartQty'>${quantity}</span></p>

				<%-- Check if porch service was added to this product, show msg saying service does not get attached--%>
								
				<c:if test="${porchWarning eq true}">
	            	<p class="porchWarning">	            		
	            		<bbbl:label key="lbl_bbby_porch_pdp_add_to_registry" language ="${pageContext.request.locale.language}"/>
	            	</p>
                </c:if>
			</div>
			<div class="addRegistrySummary">
			        <div class="keepShoopingButton button button_active button_active_orange">
					<a href="javascript:void(0);"><bbbl:label key="lbl_keep_shopping"
				language="<c:out param='${language}'/>" /></a>
					</div>
					
                    <a href="/store/giftregistry/view_registry_owner.jsp?registryId=${registryId}&eventType=${registryName}" class="viewRegistryLink">
					<input class="viewRegistry" type="button" role="button" value="VIEW REGISTRY" data-notify-reg="true">
					</a>
					
					
			</div>
	</div>
<div class="registryCarouselContainer modalBottomDataContainer">

<img class="loaderBottomSection" width="20" height="20" alt="loading" src="/_assets/global/images/widgets/small_loader.gif"/>
</div> 
</div>

</dsp:page>
