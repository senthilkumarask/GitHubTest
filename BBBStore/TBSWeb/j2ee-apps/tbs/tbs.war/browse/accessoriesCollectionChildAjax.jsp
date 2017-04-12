<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet"/>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
<dsp:importbean bean="/com/bbb/commerce/droplet/TBSItemExclusionDroplet"/>
<dsp:getvalueof var="appid" bean="Site.id" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof var="color" param="color" />
<%-- <c:set var="color" value="${color}"/> --%>
<c:set var="AttributePDPCollection">
	<bbbl:label key='lbl_pdp_attributes_collection' language="${pageContext.request.locale.language}" />
</c:set>
<c:set var="scene7Path" scope="request">
	<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
</c:set>
<dsp:getvalueof var="productId" param="productId"/>
<dsp:getvalueof var="currentChildProdIndex" param="nextIndex" scope="request"/>
<c:set var="collectionId_Omniture" scope="request"></c:set>
	<dsp:droplet name="ProductDetailDroplet">
		<dsp:param name="id" param="productId" />
		<dsp:param name="startIndex" param="nextIndex" />
		<dsp:param name="siteId" value="${appid}" />
		<%-- <dsp:param name="skuId" param="skuId" />
		<dsp:param name="color" value="${color}" />
		<dsp:param name="isDefaultSku" value="true" />
		<dsp:param name="registryId" param="registryId" /> --%>
		<dsp:param name="fromTBSPDP" value="${true}" />
		<dsp:param name="checkInventory" value="${false}"/>
		<dsp:param name="colorInQueryParamsTBS" value="true"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="nextIndex" param="nextIndex"/>
			<dsp:getvalueof var="isLeadSKU" param="collectionVO.leadSKU" />
			<dsp:getvalueof var="collection" param="productVO.collection" />
			<c:choose>
			<c:when test="${currentSiteId eq TBS_BedBathUSSite}">
				<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_us"/></c:set>
			</c:when>
			<c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
				<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_baby"/></c:set>
			</c:when>
			<c:otherwise>
				<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_ca"/></c:set>
			</c:otherwise>
		</c:choose>
			<c:choose>
				<c:when test="${isLeadSKU==true}">
					<dsp:include page="accessoriesForms.jsp">
						<dsp:param name="color" value="${color}" />
						<dsp:param name="parentProductId" param="productId" />
						<dsp:param name="crossSellFlag" value="true" />
						<dsp:param name="desc" value="Accessories (pdp)" />
						<dsp:param name="color" value="${color}" />
						<dsp:param name="pageURL" value="${contextPath}${pageURL}" />
					</dsp:include>
					<dsp:getvalueof var="childProducts" param="collectionVO.childProducts" />
					<c:set var="omniProp5" value="Product with Accessory" />
				</c:when>
				<c:otherwise>
					<dsp:include page="collectionForms.jsp">
						<dsp:param name="parentProductId" param="productId" />
						<dsp:param name="crossSellFlag" value="true" />
						<dsp:param name="desc" value="Collection (pdp)" />
						<dsp:param name="color" value="${color}" />
						<dsp:param name="pageURL" value="${contextPath}${pageURL}" />
						
					</dsp:include>
					<dsp:getvalueof var="childProducts" param="collectionVO.childProducts" />
					<c:set var="omniProp5" value="Collection" />
				</c:otherwise>
			</c:choose>
		</dsp:oparam>
	</dsp:droplet>
	
	<input type="hidden" id="nextIndex" value="${nextIndex}">
</dsp:page>