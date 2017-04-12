<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProdToutDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ExitemIdDroplet" />
	<dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet" />
	<dsp:importbean bean="/com/bbb/certona/CertonaConfig" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean var="SystemErrorInfo" bean="/com/bbb/utils/SystemErrorInfo"/>

	<dsp:getvalueof var="parentProductId" param="parentProductId" />
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="searchterms" param="searchterms" />
	<dsp:getvalueof var="number" value="${fn:escapeXml(param.number)}"/>
	<dsp:getvalueof var="linkStringNonRecproduct" param="linkStringNonRecproduct" />
	<dsp:getvalueof var="productList" value="${fn:escapeXml(param.productList)}" />

	<c:set var="appid" scope="request"><dsp:valueof bean="Site.id"/></c:set>
	<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${appid}" />
	<c:set var="BedBathUSSite" scope="request"><bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys"/></c:set>
	<c:set var="BuyBuyBabySite" scope="request"><bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys"/></c:set>
	<c:set var="BedBathCanadaSite" scope="request"><bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys"/></c:set>
    <c:set var="CertonaContext" scope="request">${parentProductId}</c:set>
    <c:set var="schemeName" scope="request"><bbbc:config key="certona_search_scheme" configName="CertonaKeys"/></c:set>
	
	<c:set var="relatedItemsDisplayFlag" value="false" />
	<c:set var="scene7Path" scope="request">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
	</c:set>
	<c:choose>
		<c:when test="${appid eq BedBathUSSite}">
			<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_us"/></c:set>
			<c:set var="CertonaOn" scope="request"><tpsw:switch tagName="CertonaTag_us" /></c:set>
		</c:when>
		<c:when test="${appid eq BuyBuyBabySite}">
			<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_baby"/></c:set>
			<c:set var="CertonaOn" scope="request"><tpsw:switch tagName="CertonaTag_baby" /></c:set>
		</c:when>
		<c:otherwise>
			<c:set var="BazaarVoiceOn" scope="request"><tpsw:switch tagName="BazaarVoiceTag_ca"/></c:set>
			<c:set var="CertonaOn" scope="request"><tpsw:switch tagName="CertonaTag_ca" /></c:set>
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
	<div class="clearfix grid_9">
	
	<c:if test="${CertonaOn}">
		<dsp:droplet name="CertonaDroplet">
			<dsp:param name="scheme" value="${schemeName}"/>
	        <dsp:param name="exitemid" value="${productList}"/>
	        <dsp:param name="searchterms" value="${searchterms}" />
	        <dsp:param name="userid" value="${userId}"/>
	        <dsp:param name="siteId" value="${appid}"/>
	        <dsp:param name="number" value="${number}"/>
	        <dsp:param name="output" value="xml" />
			<dsp:oparam name="output">
				<c:set var="schemeArray" value="${fn:split(schemeName, ';')}" />
				<dsp:getvalueof var="relatedItemsProductsVOsList" param="certonaResponseVO.resonanceMap.${schemeName}.productsVOsList" />
				<dsp:getvalueof var="linksCertona" param="certonaResponseVO.resxLinks" />
				<dsp:getvalueof var="pageIdCertona" param="certonaResponseVO.pageId" />
							 	<%--BBBSL-6574 | Printing WS calls of Certona on view source--%>
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
	 

	<c:if test="${not empty relatedItemsProductsVOsList}">
		<div class="categoryProductTabs">
			<c:set var="customerAlsoViewedTab">
				<bbbl:label key='lbl_pdp_product_related_items' language="${pageContext.request.locale.language}" />
			</c:set>

			<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
				<dsp:param name="value" value="${relatedItemsProductsVOsList}" />
				<dsp:oparam name="false">
					<c:set var="relatedItemsDisplayFlag" value="true" />
				</dsp:oparam>
				<dsp:oparam name="true">
					<c:set var="relatedItemsDisplayFlag" value="false" />
				</dsp:oparam>
			</dsp:droplet>

			<c:if test="${relatedItemsDisplayFlag eq 'true'}">
            	<h2><bbbl:label key='lbl_other_recom_products' language="${pageContext.request.locale.language}" /></h2>

				<div id="pdp_cav">
					<div id="botCrossSell-tabs1" class="categoryProductTabsData noBorder">
						<dsp:include page="certona_search_carousel.jsp">
							<dsp:param name="productsVOsList" value="${relatedItemsProductsVOsList}" />
							<dsp:param name="crossSellFlag" value="true" />
							<dsp:param name="desc" value="Other Recommended Products (search)" />
							<dsp:param name="oosProductVo" value="${oosProductVo}" />
						</dsp:include>
					</div>
				</div>
			</c:if>
		</div>
	</c:if>

	<%-- certona JS call --%>
    <script type="text/javascript">
  setTimeout(function(){
	resx.appid = "${appIdCertona}";
	/*resx.event = "search_rr";*/	/*GFT-2268*/
	resx.links = '${linksCertona}' + '${productList}';
	resx.customerid = "${userId}";
	resx.pageid = "${pageIdCertona}";	
	       if (typeof BBB.loadCertonaJS === "function") { BBB.loadCertonaJS(); }
	 },100);      

    </script>
	</div>
</dsp:page>