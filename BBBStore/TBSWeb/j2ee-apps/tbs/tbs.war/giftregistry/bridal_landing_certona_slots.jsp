<dsp:page>
    <%-- import required beans --%>
    <dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet"/>
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <dsp:importbean bean="/atg/multisite/Site"/>
    <dsp:importbean bean="/atg/userprofiling/Profile"/>
    <dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
    <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>


    <%-- init default values/variables --%>
    <c:set var="BedBathUSSite" scope="request"><bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys"/></c:set>
    <c:set var="BuyBuyBabySite" scope="request"><bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys"/></c:set>
    <c:set var="BedBathCanadaSite" scope="request"><bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys"/></c:set>
    <c:set var="scene7Path" scope="request"><bbbc:config key="scene7_url" configName="ThirdPartyURLs"/></c:set>
    <c:set var="appid" scope="request"><dsp:valueof bean="Site.id"/></c:set>
    <c:set var="appIdCertona" scope="request"><dsp:valueof bean="CertonaConfig.siteIdAppIdMap.${appid}"/></c:set>
    
    
    <%-- set default flags --%>
    <c:set var="topRegistryItemsFlag" value="false"/>
    <c:set var="displayFlag" value="true"/>
    <c:set var="displayFlagTopReg" value="false"/>
    <c:set var="certonaDefaultFlag" value="true"/>
    
    
     <%-- capture url params --%>
    <dsp:getvalueof var="scheme" param="scheme"/>
    <dsp:getvalueof var="number" param="number"/>
    <dsp:getvalueof var="certVO_topreg" param="topreg"/>
    <dsp:getvalueof var="topRegistryItemsFlagParam" param="topRegistryItemsFlagParam"/>
    <dsp:getvalueof var="certonaSwitch" param="certonaSwitch"/>
	<dsp:getvalueof var="productList" param="productList" />
	<dsp:getvalueof var="tpb" param="${fn:escapeXml(tpb)}"/>
	<c:set var="BazaarVoiceOn" scope="request" value="${param.BazaarVoiceOn}"/>
	<dsp:getvalueof var="babyCAMode" param="babyCAMode"/>
    
	  <%-- check if certona if off --%>
    <c:if test="${not empty certonaSwitch}">
        <c:set var="certonaDefaultFlag" value="${certonaSwitch}"/>
    </c:if>
    
    <c:if test= "${certonaDefaultFlag}">
    	<c:if test="${not empty topRegistryItemsFlagParam}">
        	<c:set var="topRegistryItemsFlag" value="${topRegistryItemsFlagParam}"/>
    	</c:if>
    
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
    
	<c:if test="${(topRegistryItemsFlag eq true)}" >
				<dsp:droplet name="CertonaDroplet">
					<dsp:param name="scheme" value="${scheme}"/>
					<dsp:param name="userid" value="${userId}"/>
					<dsp:param name="exitemid" value="${productList}"/>
					<dsp:param name="siteId" value="${appid}"/>
					<dsp:param name="number" value="${number}"/>
					<dsp:param name="babyCAMode" value="${babyCAMode}"/>
						<dsp:oparam name="output">
							<dsp:getvalueof var="productsVOsList" param="certonaResponseVO.resonanceMap.${certVO_topreg}.productsVOsList"/>
							<dsp:getvalueof var="linksCertona" param="certonaResponseVO.resxLinks"/>
				            <dsp:getvalueof var="pageIdCertona" param="certonaResponseVO.pageId"/>							
						</dsp:oparam>
						<dsp:oparam name="error">
						  <c:set var="displayFlag" value="false"/>
						</dsp:oparam>			
						<dsp:oparam name="empty">
						  <c:set var="displayFlag" value="false"/>
						</dsp:oparam>
				</dsp:droplet>
	</c:if>
		
		<c:if test="${displayFlag eq true}">
			<dsp:droplet name="Switch">
				<dsp:param param="topRegistryItemsFlagParam" name="value"/>
				<dsp:oparam name="true">
					<dsp:droplet name="IsEmpty">
					<dsp:param name="value" value="${productsVOsList}"/>
					<dsp:oparam name="false">
							<c:set var="displayFlagTopReg" value="true"/>
					</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
			</dsp:droplet>
		</c:if>
		
		<div class="clearfix">  
			<c:if test="${displayFlagTopReg eq true }">
				<div id="grl_tri">
					<div id="registryAjaxTab">  
						<dsp:include page="/browse/certona_prod_carousel.jsp" >
					 	<dsp:param name="productsVOsList" value="${productsVOsList}"/>	
					    <dsp:param name="desc" value="${tpb}"/> 
						<dsp:param name="crossSellFlag" value="true"/>
						</dsp:include>
					</div>
				</div>
			</c:if>
		
	        	 
	<%-- certona JS call --%>
        <script type="text/javascript">
            setTimeout(function(){
                resx.appid = "${appIdCertona}";
                resx.pageid = "${pageIdCertona}";
                resx.customerid = "${userId}";
                resx.links = '${linksCertona}'+'${productList}';

                if (typeof BBB.loadCertonaJS === "function") { BBB.loadCertonaJS(); }
            }, 100);
        </script>        	
	  </div>
	</c:if>
</dsp:page>