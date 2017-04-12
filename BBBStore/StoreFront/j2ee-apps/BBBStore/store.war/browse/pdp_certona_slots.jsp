<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProdToutDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ExitemIdDroplet" />
	<dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet" />
	<dsp:importbean bean="/com/bbb/certona/CertonaConfig" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean var="SystemErrorInfo" bean="/com/bbb/utils/SystemErrorInfo"/>

	<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${appid}" />
	<dsp:getvalueof var="parentProductId" value="${fn:escapeXml(param.parentProductId)}" />
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="linkStringNonRecproduct" value="${fn:escapeXml(param.linkStringNonRecproduct)}" />
	
	<dsp:getvalueof var="inStock" param="inStock" />
	<dsp:getvalueof var="isMultiSku" param="isMultiSku" />
	<dsp:getvalueof var="isLeadSKU" param="isLeadSKU"/>
	
	<c:set var="oos_flag" value="false" />
	<c:set var="appid" scope="request"><dsp:valueof bean="Site.id"/></c:set>
	<c:set var="BedBathUSSite" scope="request"><bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys"/></c:set>
	<c:set var="BuyBuyBabySite" scope="request"><bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys"/></c:set>
	<c:set var="BedBathCanadaSite" scope="request"><bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys"/></c:set>
    <c:set var="CertonaContext" scope="request">${parentProductId};</c:set>
	
	<c:set var="CertonaOn" scope="request">
		<tpsw:switch tagName="CertonaTag_us" />
	</c:set>
	<c:set var="scene7Path" scope="request">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
	<c:choose>
		<c:when test="${appid eq BedBathUSSite}">
			<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_us"/></c:set>
		</c:when>
		<c:when test="${appid eq BuyBuyBabySite}">
			<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_baby"/></c:set>
		</c:when>
		<c:otherwise>
			<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_ca"/></c:set>
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
		<c:set var="frequentlyBoughtDisplayFlag" value="false" />

		<dsp:droplet name="ProdToutDroplet">
			<dsp:param value="lastviewed" name="tabList" />
			<dsp:param param="categoryId" name="id" />
			<dsp:param value="${appid}" name="siteId" />
			<dsp:param name="productId" param="productId" />
			<dsp:oparam name="output">
				<div class="clearfix grid_12">
					<dsp:getvalueof var="clearanceProductsList" param="clearanceProductsList" />
					<dsp:getvalueof var="lastviewedProductsList" param="lastviewedProductsList" />
					<dsp:droplet name="ExitemIdDroplet">
						<dsp:param value="${lastviewedProductsList}" name="lastviewedProductsList" />
						<dsp:param name="certonaExcludedItems" value="${linkStringNonRecproduct}" />
						<dsp:oparam name="output">
							<dsp:getvalueof var="productList" param="productList" />
						</dsp:oparam>
					</dsp:droplet>
					<c:if test="${CertonaOn}">
						<c:set var="custAlsoViewedProdMax" scope="request">
							<bbbc:config key="PDPCustAlsoViewProdMax" configName="CertonaKeys" />
						</c:set>
						<c:set var="frequentlyBuyProdMax" scope="request">
							<bbbc:config key="PDPFreqBoughtProdMax" configName="CertonaKeys" />
						</c:set>
						<c:set var="PDPOOSMax" scope="request">
							<bbbc:config key="PDPOOSMax" configName="CertonaKeys" />
						</c:set>
						
						<c:choose>
								<c:when test="${(inStock eq false) or (isMultiSku eq true)}">
									<c:set var="schemeName" value="pdp_oos;pdp_cav;pdp_fbw" />
									<c:set var="number" value="${PDPOOSMax};${custAlsoViewedProdMax};${frequentlyBuyProdMax}" />
									<c:set var="groupForResLink" value="pdp_oos;pdp_cav;pdp_fbw|pdp_cav;pdp_fbw" />
									<c:set var="schemeId" value="0" />
									<c:set var="oos_flag" value="true" />
								</c:when>
								
								<c:otherwise>									
									<c:set var="schemeName" value="pdp_cav;pdp_fbw" />
									<c:set var="groupForResLink" value="pdp_cav;pdp_fbw" />
									<c:set var="number" value="${custAlsoViewedProdMax};${frequentlyBuyProdMax}" />									
								</c:otherwise>
						</c:choose>									
						<dsp:droplet name="CertonaDroplet">
							<dsp:param name="scheme" value="${schemeName}" />
							<dsp:param name="schemeId" value="${schemeId}" />
							<dsp:param name="userid" value="${userId}" />
							<dsp:param name="context" value="${CertonaContext}" />
							<dsp:param name="exitemid" value="${productList}" />
							<dsp:param name="productId" value="${parentProductId}" />
							<dsp:param name="OUTPUT" value="xml" />
							<dsp:param name="siteId" value="${appid}" />
							<dsp:param name="number" value="${number}" />
							<dsp:param name="groupForResLink" value="${groupForResLink}" />
							<dsp:oparam name="output">
								<c:set var="schemeArray" value="${fn:split(schemeName, ';')}" />
								<dsp:getvalueof var="relatedItemsProductsVOsList" param="certonaResponseVO.resonanceMap.pdp_cav.productsVOsList" />
								<dsp:getvalueof var="frequentlyBoughtProductsVOsList" param="certonaResponseVO.resonanceMap.pdp_fbw.productsVOsList" />
								<c:set var="inStockScheme" value="pdp_cav;pdp_fbw" />
								<dsp:getvalueof var="linksCertona" param="certonaResponseVO.resxLinksMap.${inStockScheme}" />			
								
									<c:if test="${oos_flag eq true}">	
									<dsp:getvalueof var="linksCertonaForOOSscheme" param="certonaResponseVO.resxLinksMap.${schemeName}" />	
									
									</c:if>			
								<dsp:getvalueof var="pageIdCertona" param="certonaResponseVO.pageId" />
								<dsp:getvalueof var="oosProductVo" param="certonaResponseVO.oosProductVO" />
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
														<dsp:getvalueof var="isRobot" param="isRobot" />
								<c:if test="${isRobot}">
									<%-- CERTONA_REQUESTED_BY_ROBOT --%>
								</c:if>
							</dsp:oparam>
						</dsp:droplet>
					</c:if>
					<input type="hidden" id="certonaOOSRecommAvlbl" value="${not empty oosProductVo}" />
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

					<c:if test="${(not empty relatedItemsProductsVOsList) || (not empty lastviewedProductsList) || (not empty frequentlyBoughtProductsVOsList)}">
						<%--<h2>
							<bbbl:label key='lbl_pdp_product_also_checkout' language="${pageContext.request.locale.language}" />
						</h2>--%>
						<div class="categoryProductTabs marTop_20">
							<c:set var="customerAlsoViewedTab">
								<bbbl:label key='lbl_pdp_product_related_items' language="${pageContext.request.locale.language}" />
							</c:set>
							<c:set var="lastViewedTab">
								<bbbl:label key='lbl_pdp_product_last_viewed' language="${pageContext.request.locale.language}" />
							</c:set>
							<c:set var="frequentlyBoughtWithTab">
								<bbbl:label key='lbl_pdp_product_frequently_bought' language="${pageContext.request.locale.language}" />
							</c:set>

							<%--<ul class="categoryProductTabsLinks">--%>
								<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
									<dsp:param name="value" value="${relatedItemsProductsVOsList}" />
									<dsp:oparam name="false">
										<%--<li><div class="arrowSouth"></div> <a title="Customer Also Viewed" href="#botCrossSell-tabs1"><bbbl:label key='lbl_pdp_product_related_items'
													language="${pageContext.request.locale.language}" /></a></li>--%>
										<c:set var="relatedItemsDisplayFlag" value="true" />
									</dsp:oparam>
									<dsp:oparam name="true">
										<c:set var="relatedItemsDisplayFlag" value="false" />
									</dsp:oparam>
								</dsp:droplet>

								<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
									<dsp:param name="value" value="${frequentlyBoughtProductsVOsList}" />
									<dsp:oparam name="false">
										<%--<li><div class="arrowSouth"></div> <a title="Frequently Bought With" href="#botCrossSell-tabs2"><bbbl:label key='lbl_pdp_product_frequently_bought'
													language="${pageContext.request.locale.language}" /></a></li>--%>
										<c:set var="frequentlyBoughtDisplayFlag" value="true" />
									</dsp:oparam>
									<dsp:oparam name="true">
										<c:set var="frequentlyBoughtDisplayFlag" value="false" />
									</dsp:oparam>
								</dsp:droplet>

								<c:if test="${not empty lastviewedProductsList}">
									<%--<li><div class="arrowSouth"></div> <a title="Last Viewed Items" href="#botCrossSell-tabs3"><bbbl:label key='lbl_pdp_product_last_viewed'
												language="${pageContext.request.locale.language}" /></a></li>--%>
								</c:if>
							<%--</ul>--%>

							<c:if test="${relatedItemsDisplayFlag eq 'true'}">
                                <h2><bbbl:label key='lbl_pdp_product_related_items' language="${pageContext.request.locale.language}" /></h2>
                                	
                                	<div id="pdp_cav">
	                                		<div id="oos_recommendation" class="categoryProductTabsData noBorder ">
									        	<dsp:include page="certona_prod_oos.jsp">
													<dsp:param name="oosProductVo" value="${oosProductVo}" />
												</dsp:include>
									       	</div>
										<div id="botCrossSell-tabs1" class="categoryProductTabsData noBorder">
											<dsp:include page="certona_prod_carousel.jsp">
												<dsp:param name="productsVOsList" value="${relatedItemsProductsVOsList}" />
												<dsp:param name="crossSellFlag" value="true" />
												<dsp:param name="desc" value="Customer Also Viewed (pdp)" />
												<dsp:param name="oosProductVo" value="${oosProductVo}" />
											</dsp:include>
										</div>
									</div>
							</c:if>

							<c:if test="${frequentlyBoughtDisplayFlag eq 'true'}">
                                <h2><bbbl:label key='lbl_pdp_product_frequently_bought' language="${pageContext.request.locale.language}" /></h2>
                                	<div id="pdp_fbw">
										<div id="botCrossSell-tabs2" class="categoryProductTabsData noBorder">
											<dsp:include page="certona_prod_carousel.jsp">
												<dsp:param name="productsVOsList" value="${frequentlyBoughtProductsVOsList}" />
												<dsp:param name="crossSellFlag" value="true" />
												<dsp:param name="desc" value="Frequently Bought(pdp)" />
											</dsp:include>
										</div>
									</div>
							</c:if>

							<c:if test="${not empty lastviewedProductsList and fn:length(lastviewedProductsList) > 1}">
                                <h2><bbbl:label key='lbl_pdp_product_last_viewed' language="${pageContext.request.locale.language}" /></h2>
								<div id="botCrossSell-tabs3" class="categoryProductTabsData noBorder">
									<dsp:include page="last_viewed.jsp">
										<dsp:param name="lastviewedProductsList" value="${lastviewedProductsList}" />
										<dsp:param name="desc" value="Last  Viewed Item (pdp)" />
									</dsp:include>
								</div>
							</c:if>
						</div>
					</c:if>

<input id="certonaInStockLinks" type="hidden" value="${linksCertona}${linkStringNonRecproduct}${productList}" />
<input id="certonaOutOfStock" type="hidden" value="${linksCertonaForOOSscheme}${linkStringNonRecproduct}${productList}" />

<c:if test="${isLeadSKU eq true }">
	<c:set var="linkStringNonRecproduct" value="${parentProductId}"></c:set>
</c:if>


<script type="text/javascript">
	setTimeout(function(){
		resx.event = "product";
		resx.itemid = '${linkStringNonRecproduct}';
		resx.pageid = "${pageIdCertona}";
		if($("#stockStatus").val() === "true"){
			
			resx.links = '${linksCertona}' + '${productList}';
			}
		else{			
			resx.links = '${linksCertonaForOOSscheme}' + '${productList}';
		}
		if (typeof BBB.loadCertonaJS === "function") { BBB.loadCertonaJS(); }
	}, 100);

	function callCertonaOOSPixel(stockStatus){
		if(stockStatus === "true"){			
			resx.event = "product";
			resx.itemid = '${linkStringNonRecproduct}';
			resx.pageid = "${pageIdCertona}";
			resx.links = '${linksCertonaForOOSscheme}' + '${linkStringNonRecproduct}' + '${productList}';			
			if(typeof certonaResx === 'object') { certonaResx.run(); }
			$("#stockStatus").val("false");
		}		
	}

</script>

				</div>
			</dsp:oparam>
		</dsp:droplet>
</dsp:page>