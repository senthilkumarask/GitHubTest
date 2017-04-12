<dsp:page>
	
		<dsp:importbean
			bean="/com/bbb/commerce/browse/droplet/ProdToutDroplet" />
		<dsp:importbean
			bean="/com/bbb/commerce/browse/droplet/ProductDetailDroplet" />
		<dsp:importbean
			bean="/com/bbb/commerce/browse/droplet/ExitemIdDroplet" />
		<dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet" />
		<dsp:importbean bean="/com/bbb/certona/CertonaConfig" />
		<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
		<dsp:importbean bean="/atg/userprofiling/Profile" />
		<dsp:importbean bean="/atg/multisite/Site" />
		<dsp:importbean var="SystemErrorInfo"bean="/com/bbb/utils/SystemErrorInfo" />

		<dsp:getvalueof var="appid" bean="Site.id" />
		<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${appid}" />

		<dsp:getvalueof var="productId" param="productId" />
		<dsp:getvalueof var="type" param="type" />
		<dsp:getvalueof var="registryId" param="registryId" />
		<dsp:getvalueof var="registryName" param="registryName" />
		<dsp:getvalueof var="parentProductId" value="${productId}" />
		<dsp:getvalueof var="inStock" param="inStock" />
		<dsp:getvalueof var="showRecommendations" param="showRecommendations" />

		<dsp:getvalueof var="isMultiSku" param="isMultiSku" />
		<dsp:getvalueof var="isLeadSKU" param="isLeadSKU" />
		<c:set var="oos_flag" value="false" />
		<c:set var="appid" scope="request">
			<dsp:valueof bean="Site.id" />
		</c:set>
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
		<c:set var="CertonaContext" scope="request">${parentProductId};</c:set>

		<c:set var="CertonaOn" scope="request">
			<tpsw:switch tagName="CertonaTag_us" />
		</c:set>
		<c:set var="scene7Path" scope="request">
			<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
		</c:set>
		<c:choose>
			<c:when test="${appid eq BedBathUSSite}">
				<c:set var="BazaarVoiceOn" scope="request">
					<tpsw:switch tagName="BazaarVoiceTag_us" />
				</c:set>
			</c:when>
			<c:when test="${appid eq BuyBuyBabySite}">
				<c:set var="BazaarVoiceOn" scope="request">
					<tpsw:switch tagName="BazaarVoiceTag_baby" />
				</c:set>
			</c:when>
			<c:otherwise>
				<c:set var="BazaarVoiceOn" scope="request">
					<tpsw:switch tagName="BazaarVoiceTag_ca" />
				</c:set>
			</c:otherwise>
		</c:choose>

		<dsp:droplet name="Switch">
			<dsp:param name="value" bean="Profile.transient" />
			<dsp:oparam name="false">
				<dsp:getvalueof var="userId" bean="Profile.id" />
			</dsp:oparam>
			<dsp:oparam name="true">
				<dsp:getvalueof var="userId" value="" />
			</dsp:oparam>
		</dsp:droplet>

		<c:set var="relatedItemsDisplayFlag" value="false" />

		<%-- This will be passed from front end in case we need it --%>
		<dsp:getvalueof var="linkStringNonRecproduct" param="linkStringNonRecproduct" />

		<dsp:droplet name="ProdToutDroplet">
			<dsp:param value="lastviewed" name="tabList" />
			<dsp:param param="categoryId" name="id" />
			<dsp:param value="${appid}" name="siteId" />
			<dsp:param name="productId" value="${productId}" />
			<dsp:oparam name="output">
				<div class="container_12 clearfix modalCertona">
					<dsp:form name="certonaProdForm" method="post" id="certonaProdForm">
						<dsp:getvalueof var="lastviewedProductsList"
							param="lastviewedProductsList" />

						<dsp:droplet name="ExitemIdDroplet">
							<dsp:param value="${lastviewedProductsList}"
								name="lastviewedProductsList" />
							<dsp:param name="certonaExcludedItems"
								value="${linkStringNonRecproduct}" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="productList" param="productList" />
							</dsp:oparam>
						</dsp:droplet>


						<c:if test="${CertonaOn}">
							<c:set var="custAlsoViewedProdMax" scope="request">
								<bbbc:config key="ATCCustAlsoViewProdMax"
									configName="CertonaKeys" />
							</c:set>
							<c:set var="topRegistryMax" scope="request">
								<bbbc:config key="ATRTopRegistryMax" configName="CertonaKeys" />
							</c:set>

							<c:choose>
								<c:when test="${type eq 'cart'}">
									<c:set var="schemeName" scope="request">
										<bbbc:config key="ATCScheme"
											configName="CertonaKeys" />
									</c:set>

									<c:set var="schemeName" value="${schemeName}" />
									<c:set var="number" value="${custAlsoViewedProdMax}" />
									<c:set var="groupForResLink" value="${schemeName}" />
									<c:set var="recommendedProd" value="false" />
								</c:when>
								<c:when test="${type eq 'registry'}">
									<c:set var="schemeName" scope="request">
										<bbbc:config key="ATRScheme"
											configName="CertonaKeys" />
									</c:set>
									<c:set var="schemeName" value="${schemeName}" />
									<c:set var="number" value="${topRegistryMax}" />
									<c:set var="groupForResLink" value="${schemeName}" />
									<c:set var="recommendedProd" value="true" />
								</c:when>
							</c:choose>

							<%-- Fetch Certona Recommendations --%>
							<dsp:droplet name="CertonaDroplet">
								<dsp:param name="scheme" value="${schemeName}" />
								<dsp:param name="giftregid" value="${registryId}" />
								<dsp:param name="userid" value="${userId}" />
								<dsp:param name="recommendedProd" value="${recommendedProd}" />
								<dsp:param name="context" value="${CertonaContext}" />
								<dsp:param name="exitemid" value="${productList}" />
								<dsp:param name="productId" value="${parentProductId}" />
								<dsp:param name="OUTPUT" value="xml" />
								<dsp:param name="siteId" value="${appid}" />
								<dsp:param name="number" value="${number}" />
								<dsp:param name="groupForResLink" value="${groupForResLink}" />
								<dsp:oparam name="output">
									<c:set var="schemeArray" value="${fn:split(schemeName, ';')}" />
									<dsp:getvalueof var="linksCertona" param="certonaResponseVO.resxLinksMap.${schemeName}"/>
									<c:set var="linksCertona" value="${linksCertona}" scope="request"/>	
									
									<c:choose>
										<c:when test="${type eq 'cart'}">
											<dsp:getvalueof var="relatedItemsProductsVOsList"
												param="certonaResponseVO.resonanceMap.${schemeName}.productsVOsList" />
										</c:when>
										<c:when test="${type eq 'registry'}">
											<dsp:getvalueof var="triProductsVOsList"
												param="certonaResponseVO.resonanceMap.${schemeName}.productsVOsList" />
										</c:when>
									</c:choose>

									<dsp:getvalueof var="requestURL"
										param="certonaResponseVO.requestURL" scope="request" />
									<dsp:getvalueof var="responseXML"
										param="certonaResponseVO.responseXML" scope="request" />
								</dsp:oparam>
								<dsp:oparam name="error">
									<dsp:getvalueof var="requestURL" param="requestURL"
										scope="request" />
									<dsp:getvalueof var="errorMsg" param="errorMsg" scope="request" />
								</dsp:oparam>
								<dsp:oparam name="empty">
									<dsp:getvalueof var="requestURL" param="requestURL"
										scope="request" />
									<dsp:getvalueof var="errorMsg" param="errorMsg" scope="request" />
								</dsp:oparam>
							</dsp:droplet>

							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param bean="SystemErrorInfo.errorList" name="array" />
								<dsp:param name="elementName" value="ErrorInfoVO" />
								<dsp:oparam name="outputStart">
									<div id="error" class="hidden">
										<ul>
								</dsp:oparam>
								<dsp:oparam name="output">
									<li id="tl_atg_err_code"><dsp:valueof
											param="ErrorInfoVO.errorCode" /></li>
									<li id="tl_atg_err_value"><dsp:valueof
											param="ErrorInfoVO.errorDescription" /></li>
								</dsp:oparam>
								<dsp:oparam name="outputEnd">
									</ul>
									</div>
								</dsp:oparam>
							</dsp:droplet>
							
					<%-- Show the recommended products --%>
					<div class="marTop_10">
						<c:if test="${(not empty relatedItemsProductsVOsList)}">
						
							<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
								<dsp:param name="value" value="${relatedItemsProductsVOsList}" />
								<dsp:oparam name="false">
									<c:set var="relatedItemsDisplayFlag" value="true" />
								</dsp:oparam>
								<dsp:oparam name="true">
									<c:set var="relatedItemsDisplayFlag" value="false" />
								</dsp:oparam>
							</dsp:droplet>
							<h3>
								<bbbl:label key='lbl_you_may_also_like_${type}'
									language="${pageContext.request.locale.language}" />
							</h3>
			
								<div class="certona_like">
									<div id="botCrossSell-tabs1" class="categoryProductTabsData noBorder">
									<input type="hidden" name="siblingProductSize" value="${fn:length(relatedItemsProductsVOsList)}" />
										<c:if test="${relatedItemsDisplayFlag eq 'true'}">
											<dsp:include page="recommended_products.jsp">
												<dsp:param name="productsVOsList" value="${relatedItemsProductsVOsList}" />
												<dsp:param name="crossSellFlag" value="true" />
												<dsp:param name="desc" value="You may also like (desc)" />
												<dsp:param name="showRecommendations" value="${showRecommendations}" />
												<dsp:param name="productVOSize" value="${fn:length(relatedItemsProductsVOsList)}" />
												<dsp:param name="registryName" value="${registryName}" />
											</dsp:include>
							</c:if>
							
									</div>
								</div>
						</c:if>
						<c:if test="${(not empty triProductsVOsList)}">
						<input type="hidden" name="siblingProductSize" value="${fn:length(relatedItemsProductsVOsList)}" />
							<h3>
								<bbbl:label key='lbl_you_may_also_like_${type}'
									language="${pageContext.request.locale.language}" />
							</h3>
			
								<div class="certona_like">
									<div id="botCrossSell-tabs1"
										class="categoryProductTabsData noBorder">
							<dsp:include page="recommended_products.jsp">
								<dsp:param name="productsVOsList" value="${triProductsVOsList}" />
								<dsp:param name="crossSellFlag" value="true" />
								<dsp:param name="desc" value="You may also like (desc)" />
								<dsp:param name="showRecommendations" value="${showRecommendations}" />
								<dsp:param name="productVOSize" value="${fn:length(relatedItemsProductsVOsList)}" />
								<dsp:param name="registryName" value="${registryName}" />
							</dsp:include>
									</div>
								</div>
								
						</c:if>
						</c:if>		
					<%--- End CertonaOn If --%>
			
					</dsp:form>
					</div>
		</dsp:oparam>
		</dsp:droplet>

		<script type="text/javascript">
			   var resx = new Object();
			   resx.appid = "${appIdCertona}";
			   resx.event = "atr+atc";
			   resx.pageid = "";
			   resx.links = '${productId},${linkStringNonRecproduct}';
		</script>
</dsp:page>