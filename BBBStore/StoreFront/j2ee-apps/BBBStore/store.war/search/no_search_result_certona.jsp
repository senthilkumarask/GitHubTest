<dsp:page>
    <%-- import required beans --%>
    <dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProdToutDroplet"/>
    <dsp:importbean bean="/com/bbb/commerce/browse/droplet/ExitemIdDroplet"/>
    <dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet"/>
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:importbean bean="/atg/userprofiling/Profile"/>
    <dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
	<dsp:importbean var="SystemErrorInfo" bean="/com/bbb/utils/SystemErrorInfo"/>

    <%-- init default values/variables --%>
    <c:set var="BedBathUSSite" scope="request"><bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys"/></c:set>
    <c:set var="BuyBuyBabySite" scope="request"><bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys"/></c:set>
    <c:set var="BedBathCanadaSite" scope="request"><bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys"/></c:set>
    <c:set var="scene7Path" scope="request"><bbbc:config key="scene7_url" configName="ThirdPartyURLs"/></c:set>
    <c:set var="appid" scope="request"><dsp:valueof bean="Site.id"/></c:set>
    <c:set var="appIdCertona" scope="request"><dsp:valueof bean="CertonaConfig.siteIdAppIdMap.${appid}"/></c:set>
	<c:set var="BazaarVoiceOn" scope="request" value="${param.BazaarVoiceOn}"/>
	    
    <%-- set default flags --%>
    <c:set var="certonaDefaultFlag" value="true"/>
    
     <%-- capture url params --%>
    <dsp:getvalueof var="scheme" param="scheme"/>
    <dsp:getvalueof var="number" param="number"/>
    <dsp:getvalueof var="certonaSwitch" param="certonaSwitch"/>
	<dsp:getvalueof var="searchTerm" param="searchTerm"/>
	<dsp:getvalueof var="certonaPageName" param="certonaPageName"/>
	<dsp:getvalueof var="linkStringNonRecproduct" param="linkStringNonRecproduct"/>
	<dsp:getvalueof var="term" param="term"/>
	
	
	<%--BSL-4323 | Setting Number for No Search --%>
	<c:set var="noSearchMax" scope="request">
		<bbbc:config key="NOSEARCHMax" configName="CertonaKeys" />
	</c:set>
	
	    <%-- check if certona if off --%>
    <c:if test="${not empty certonaSwitch}">
        <c:set var="certonaDefaultFlag" value="${certonaSwitch}"/>
    </c:if>
    
    <c:if test="${certonaDefaultFlag eq true}">
        <%-- find userid (if logged-in) --%>
    <dsp:droplet name="Switch">
        <dsp:param name="value" bean="Profile.transient"/>
        <dsp:oparam name="false">
            <dsp:getvalueof var="userId" bean="Profile.id"/>
        </dsp:oparam>
        <dsp:oparam name="true">
            <dsp:getvalueof var="userId" value=""/>
        </dsp:oparam>
    </dsp:droplet>
    
    <%-- exclude last viewed items from certona call/product list --%>
    <dsp:droplet name="ProdToutDroplet">
        <dsp:param name="tabList" value="lastviewed"/>
        <dsp:param name="siteId" value="${appid}"/>
        <dsp:param name="id" param="categoryId"/>
        <dsp:oparam name="output">
            <dsp:getvalueof var="lastviewedProductsList"  vartype="java.util.List" param="lastviewedProductsList"/>
            <c:if test="${not empty lastviewedProductsList}">
                <dsp:droplet name="ExitemIdDroplet">
                    <dsp:param value="${lastviewedProductsList}" name="lastviewedProductsList"/>
                    <dsp:oparam name="output">
                        <dsp:getvalueof var="productList"  vartype="java.util.List" param="productList"/>
                    </dsp:oparam>
                </dsp:droplet>
            </c:if>
        </dsp:oparam>
    </dsp:droplet>
    
    <%-- get productsVOsList to show from Certona --%>
    
	    <dsp:droplet name="CertonaDroplet">
	        <dsp:param name="scheme" value="${scheme}"/>
	        <dsp:param name="exitemid" value="${productList}"/>
	        <dsp:param name="userid" value="${userId}"/>
	        <dsp:param name="siteId" value="${appid}"/>
	        <dsp:param name="number" value="${noSearchMax}"/>
	        <dsp:oparam name="output">
	            <c:if test="${certonaPageName eq 'NoSearchResult Page'}">
	            	<dsp:getvalueof var="relatedItemsProductsVOsList" param="certonaResponseVO.resonanceMap.${scheme}.productsVOsList"/>
	            </c:if>
	            <dsp:getvalueof var="linksCertona" param="certonaResponseVO.resxLinks"/>
	            <dsp:getvalueof var="pageIdCertona" param="certonaResponseVO.pageId"/>
			<%--BBBSL-6574 --%>
				<dsp:getvalueof var="requestURL" param="certonaResponseVO.requestURL" scope="request"/>
				<dsp:getvalueof var="responseXML" param="certonaResponseVO.responseXML" scope="request"  />
	        </dsp:oparam>
	        <dsp:oparam name="error">
	        <dsp:getvalueof var="requestURL" param="requestURL" scope="request"/>
	        <dsp:getvalueof var="errorMsg" param="errorMsg" scope="request"  />
			  <c:set var="displayFlag" value="false"/>
	        </dsp:oparam>
	        <dsp:oparam name="empty">
	        <dsp:getvalueof var="requestURL" param="requestURL" scope="request"/>
			<dsp:getvalueof var="errorMsg" param="errorMsg" scope="request"  />
	            <c:set var="displayFlag" value="false"/>
	        </dsp:oparam>
	    </dsp:droplet>
		
    
    
     <%-- ajax response markup wrapper --%>
    <div class="clearfix">
    	<div id="certonaRectangularSlot">
    	
		<%--BBBSL-6574 | Printing Certona WS call on source --%>
			<c:choose>
	            <c:when test="${not empty errorMsg }">
	            			   <!--
					 <div id="certonaRequestResponse" class="hidden"> 
						<ul> 
							<li id="requestURL"><dsp:valueof value="${requestURL}" valueishtml="true" /></li>
							<li id="errorMsg"><dsp:valueof value="${errorMsg}" valueishtml="true" /></li>
						</ul> 
					</div>  
					 --> 
	            </c:when>
	            <c:otherwise>
	             <!--
						 <div id="certonaRequestResponse" class="hidden"> 
							<ul> 
								<li id="requestURL"><dsp:valueof value="${requestURL}" valueishtml="true" /></li>
								<li id="responseXML"><dsp:valueof value="${responseXML}" valueishtml="true"/></li>  
							</ul>
						</div>
						 -->
	            </c:otherwise>
	        </c:choose>
	
    	<div id="nosearch_rr" class="categoryProductTabs marTop_20">
			<c:if test="${not empty relatedItemsProductsVOsList}">
				<jsp:useBean id="customKeys" class="java.util.HashMap" scope="request"/>
				<c:set target="${customKeys}" property="searchTerm">${searchTerm}</c:set>
				<div class="arrowSouth"></div><h3><bbbl:textArea key="txt_no_results_cust_also_viewed" language="${pageContext.request.locale.language}" placeHolderMap="${customKeys}" /></h3>
					<c:if test="${not empty relatedItemsProductsVOsList}">
						<div id="noSearchErr" class="categoryProductTabsData noBorderTop">
						<%--updated for Omniture Story |@psin52 --%>
							<dsp:include page="/browse/last_viewed.jsp" >
							<dsp:param name="lastviewedProductsList" value="${relatedItemsProductsVOsList}"/>
							<dsp:param name="desc" value="nullSearchPage_peopleSearch"/>
							</dsp:include>
						</div>
					</c:if>
				</c:if>
		</div>
		
		
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
	
	
	 <%-- certona JS call --%>
        <script type="text/javascript">
            setTimeout(function(){
				resx.appid = "${appIdCertona}";
				resx.links = '${linksCertona}' + '${linkStringNonRecproduct}' + '${productList}';
				resx.customerid = "${userId}";
				resx.Keyword = "${term}";
				resx.pageid = "${pageIdCertona}";

                if (typeof BBB.loadCertonaJS === "function") { BBB.loadCertonaJS(); }
            }, 100);
        </script>
    </div>
   </c:if>
    
</dsp:page>