<%@ page import="com.bbb.commerce.giftregistry.vo.RegistryVO" %>
<dsp:page> 
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean	bean="/com/bbb/tag/droplet/ReferralInfoDroplet" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean" />	
   	<dsp:importbean bean="/atg/multisite/Site"/>
   	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/CanadaURLGeneratorDroplet"/>
   	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/PrefStoreInPilotStoresValidationDroplet" />
	<dsp:getvalueof var="siteId" bean="Site.id" />
   	<dsp:getvalueof var="scheme" bean="/OriginatingRequest.scheme"/>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI"/>
    <dsp:getvalueof var="serverName" bean="/OriginatingRequest.serverName"/>
    <dsp:getvalueof var="serverPort" bean="/OriginatingRequest.serverPort"/>    
      	<dsp:getvalueof var="kId" bean="SessionBean.kickStarterId" />	
        <dsp:getvalueof var="kType" bean="SessionBean.kickStarterEventType" />	
    <c:set var="BedBathCanadaSite">
		<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>	
	<c:set var="registry_Success_Back_Btn_Url_Flag" value="redirectEnabled" scope="session"/>       
	<dsp:getvalueof var="guest_registry_uri" value="/giftregistry/view_registry_guest.jsp"/>	          			
	<dsp:importbean
	bean="/com/bbb/profile/session/SessionBean" />
		<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:setvalue param="regVO" beanvalue="GiftRegSessionBean.registryVO" />
	<dsp:getvalueof id="registryType"  param="regVO.registryType.registryTypeName"/>	
	<c:set var ="operation" scope="request">
	<dsp:valueof bean="GiftRegSessionBean.registryOperation"/>
	</c:set>
	<dsp:getvalueof id="registryDesc"  param="regVO.registryType.registryTypeDesc"/>
		<dsp:getvalueof id="registryIdForIcrossing"  param="regVO.registryId"/>
	<c:choose>
		<c:when test="${appid == 'BuyBuyBaby'}">
			<c:set var="pageVariation" value="bb" scope="request" />
		</c:when>
		<c:otherwise>
			<c:choose>
				<c:when test="${registryType == 'BA1' }">
					<c:set var="pageVariation" value="by" scope="request" />
				</c:when>
				<c:otherwise>
					<c:set var="pageVariation" value="br" scope="request" />
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
	<%-------------------------------------------------------------
	  gift Registry creation registry_creation_form_select.jsp             
  
	  registry type select
  
  -------------------------------------------------------------%>
  <bbb:pageContainer>	
	<jsp:attribute name="pageWrapper">registryConfirm</jsp:attribute>
	<jsp:attribute name="pageVariation">${pageVariation}</jsp:attribute>
	<jsp:attribute name="PageType">RegistryConfirmation</jsp:attribute>

	<jsp:body>
       	
    <jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request"/>
	<c:set target="${placeHolderMap}" property="imagePath"><dsp:valueof value="${imagePath}"/> </c:set>
	
		<dsp:getvalueof var="currencyId" value="${pageContext.response.locale}"/>
		<dsp:getvalueof id="registryDesc"  param="regVO.registryType.registryTypeDesc"/>
		<dsp:getvalueof id="registryIdForIcrossing"  param="regVO.registryId"/>
		<dsp:getvalueof var="regId" param="regVO.registryId"/>
		<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
		<c:set var="cookieName" value='${cookie.refId.value}'/>
		<c:if test="${ not empty registryDesc && (registryDesc eq 'Wedding' && WeddingchannelOn)}">
			<dsp:droplet name="ReferralInfoDroplet">
				<dsp:param name="currentPage" value="${requestURI}"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="refId" param="refId"/>
					<dsp:getvalueof var="wcRegUrl" param="wcRegUrl" />
					<dsp:getvalueof var="wcRegParam" param="wcRegParam" />
					<dsp:getvalueof var="wcRegParam" value="&retailerRegistryCode=${regId}" />
					<dsp:getvalueof var="refUrl" value="${wcRegUrl}${wcRegParam}" />
				</dsp:oparam>
			</dsp:droplet>
		</c:if>
		<c:if test="${ not empty registryDesc && (registryDesc eq 'Baby' && TheBumpsOn) }">
			<dsp:droplet name="ReferralInfoDroplet">
				<dsp:param name="currentPage" value="${requestURI}"/>
				<dsp:oparam name="output">
					<dsp:getvalueof var="refId" param="refId"/>
					<dsp:getvalueof var="bpRegUrl" param="bpRegUrl" />
					<dsp:getvalueof var="bpRegParam" param="bpRegParam" />
					<dsp:getvalueof var="regParam" value="&retailerRegistryCode=${regId}" />
					<dsp:getvalueof var="refUrl" value="${bpRegUrl}${regParam}" />
				</dsp:oparam>
			</dsp:droplet>
		</c:if>
		<c:if test="${ ( not empty registryDesc ) && CommisionJunctionOn && (cookieName eq 'cj')}">
					<dsp:droplet name="ReferralInfoDroplet">
						<dsp:oparam name="output">
							<dsp:getvalueof var="refId" param="refId"/>
							<c:set var="currencyId">USD</c:set>
							<c:choose>
			       	 		<c:when test="${currentSiteId eq BuyBuyBabySite}">
				       			<c:set var="cj_cid"><bbbc:config key="cj_cid_baby" configName="ReferralControls" /></c:set>
			    	   			<c:set var="cj_type"><bbbc:config key="cj_type_registry_confirmation_baby" configName="ReferralControls" /></c:set>
		       		 		</c:when>
		       		 		<c:when test="${currentSiteId eq BedBathCanadaSite}">
				       			<c:set var="cj_cid"><bbbc:config key="cj_cid_ca" configName="ReferralControls" /></c:set>
			    	   			<c:set var="cj_type"><bbbc:config key="cj_type_registry_confirmation_ca" configName="ReferralControls" /></c:set>
		       		 			<c:set var="currencyId">CAD</c:set>
		       		 		</c:when>
			       	 		<c:otherwise>
			       				<c:set var="cj_cid"><bbbc:config key="cj_cid_us" configName="ReferralControls" /></c:set>
			       				<c:set var="cj_type"><bbbc:config key="cj_type_registry_confirmation_us" configName="ReferralControls" /></c:set>
		    	   	 		</c:otherwise>
		       		 		</c:choose>
							<dsp:getvalueof var="cjSaleUrl" param="cjSaleUrl" />
							<dsp:getvalueof var="cjSaleParam" param="cjSaleParam" />
							<dsp:getvalueof var="refUrl" value="${cjSaleUrl}CID=${cj_cid}&OID=${registryIdForIcrossing}&AMOUNT=0&CURRENCY=${currencyId}&TYPE=${cj_type}&METHOD=IMG" />
						</dsp:oparam>
					</dsp:droplet>
					
					
				</c:if>
	
        <div class="container_12 clearfix">
            <div class="grid_12 infoContentWraper bdrBottom">
				<div class="grid_6 infoContent alpha">
					<h3><bbbl:label key='lbl_regcreate_confirm_hoorah' language="${pageContext.request.locale.language}" /></h3>
					<p class="subHead">
                	<dsp:valueof param="regVO.primaryRegistrant.firstName"/><dsp:droplet name="IsEmpty"><dsp:param param="regVO.coRegistrant.firstName" name="value"/><dsp:oparam name="false"> &amp; <dsp:valueof param="regVO.coRegistrant.firstName"/></dsp:oparam></dsp:droplet><bbbl:label key='lbl_regcreate_confirm_your' language="${pageContext.request.locale.language}" /> ${registryDesc} <bbbl:label key='lbl_regcreate_confirm_reghasbeen' language="${pageContext.request.locale.language}" /> <dsp:valueof bean="GiftRegSessionBean.registryOperation"/> <bbbl:label key='lbl_regcreate_confirm_successfully' language="${pageContext.request.locale.language}" /></p>
					<p class="txt_big"><bbbl:label key='lbl_regcreate_confirm_yourregnum' language="${pageContext.request.locale.language}" /> <strong><dsp:valueof param="regVO.registryId"/></strong></p>
					<p id="lblshareRegURL"><bbbl:label key='lbl_regcreate_confirm_shareurl' language="${pageContext.request.locale.language}" /></p>
					<div class="input clearfix grid_4 alpha">
						<div class="text width_4">
							<dsp:getvalueof var="registryId" param="regVO.registryId"/>
                                            <c:url var="registryURL"	
								value="${scheme}://${serverName}${contextPath}${guest_registry_uri}">
									<c:param name="registryId" value="${registryId}"/>
									<c:param name="eventType" value="${registryDesc}"/>
							</c:url>
                            <dsp:getvalueof var="sessionBabyCA" bean="SessionBean.babyCA"/>	
			                <c:if test="${sessionBabyCA eq 'true' && registryDesc eq 'Baby' }"> 
			                 <dsp:droplet name="CanadaURLGeneratorDroplet">
				              <dsp:oparam name="output">
					               <c:url var="registryURL"	
								value="${scheme}://${serverName}${contextPath}${guest_registry_uri}">
									<c:param name="registryId" value="${registryId}"/>
									<c:param name="eventType" value="${registryDesc}"/>
                                    <c:param name="bbBABY" value="bbBABY"/>
							</c:url>
				              </dsp:oparam> 
			                </dsp:droplet>  
                          </c:if>		   	                              	
			<input name="bedBath" id="shareRegURL" type="text" class="width_4" readonly="readonly" value="${registryURL}" aria-required="false" aria-hidden="true" aria-labelledby="shareRegURL" />                        
                    	</div>
                	</div>
 					<div class="grid_1 marTop_5 marLeft_10" title='<bbbl:label key='lbl_regcreate_confirm_copy' language="${pageContext.request.locale.language}" />'><a href="#" class="copy2Clip" data-copy-source="shareRegURL"  aria-label="Copy Your ${registryDesc} Registry link"><bbbl:label key='lbl_regcreate_confirm_copy' language="${pageContext.request.locale.language}" /></a></div>                
				</div>	
				
				<%-- link back to kickstarters --%>				
				<c:if test="${not empty kType }">
					<div class="kickstarterReturnLink grid_6 omega">						
						<h2><bbbt:textArea key="txt_regcreate_back_to_kickstarter_header" language="${pageContext.request.locale.language}" /></h2>						 
						<div class="button button_active">
							<a href="${contextPath}/kickstarters" ><bbbl:label key='lbl_regcreate_back_to_kickstarter_btn' language="${pageContext.request.locale.language}" /></a>
						</div>	
					</div>
				</c:if>
		</div>
		
		<dsp:getvalueof var="registryVOskg" param="regVO"/>
		<%-- BBBSL-2654 Defect fixed. Call to SearchStoreDroplet only when preffered store id is not empty --%>
		<c:if test="${not empty registryVOskg.prefStoreNum}">
			<dsp:droplet name="/com/bbb/selfservice/SearchStoreDroplet">
	                 <dsp:param name="storeId" value="${registryVOskg.prefStoreNum}"/>
	                 <dsp:param name="searchType" value="2"/>
	                 <dsp:oparam name="output">
	                      <dsp:getvalueof var="storeDetails" param="StoreDetails"/>
	                </dsp:oparam>
	        </dsp:droplet>
		</c:if>
        <c:set var="contactFlag" value="${storeDetails.contactFlag}"/>
        <c:set var="skedgeURL">
			<bbbc:config key="skedgeURL" configName="ThirdPartyURLs" />
			</c:set>
			<dsp:droplet name="PrefStoreInPilotStoresValidationDroplet">
				<dsp:param name="registryTypeCode" value="${registryType}"/>
				<dsp:param name="prefredStoreId" value="${registryVOskg.prefStoreNum}"/>
			 	<dsp:oparam name="output">
					<dsp:getvalueof var="showIFrame" param="isUserAllowedToScheduleAStoreAppointment"/>
				</dsp:oparam>
			</dsp:droplet>
  			
 			
						<c:if test="${not empty skedgeURL and showIFrame}">
						<iframe type="some_value_to_prevent_js_error_on_ie7" id="apointmentScheduler" class="grid_12 omega apointmentScheduler" src="${skedgeURL}&storeId=${registryVOskg.prefStoreNum}&email=${registryVOskg.primaryRegistrant.email}&regFN=${registryVOskg.primaryRegistrant.firstName}&regLN=${registryVOskg.primaryRegistrant.lastName}&coregFN=${registryVOskg.coRegistrant.firstName}&coregLN=${registryVOskg.coRegistrant.lastName}&contactNum=${registryVOskg.primaryRegistrant.primaryPhone}&registryId=${registryVOskg.registryId}&site=${currentSiteId}&eventDate=${registryVOskg.event.eventDate}" ></iframe>
						</c:if>        
	    </div>
      	  
      
	<dsp:form id="announcementCardsNum" action="${contextPath}/giftregistry/registry_confirmation.jsp">
    <div id="content" class="container_12 clearfix" role="main">
	<dsp:include page="/giftregistry/frags/registry_promo_boxes.jsp">	
					<dsp:param name="pageName" value="registryCreationConformation" />					
					<dsp:param name="promoSpot" value="Top" />
					<dsp:param name="registryType" value="${registryDesc}" />
					<dsp:param name="siteId" value="${siteId}" />
					<dsp:param name="channel" value="Desktop Web" />
	   </dsp:include>	
		<dsp:include page="/giftregistry/frags/registry_promo_boxes.jsp">	
					<dsp:param name="pageName" value="registryCreationConformation" />					
					<dsp:param name="promoSpot" value="Bottom" />
					<dsp:param name="registryType" value="${registryDesc}" />
					<dsp:param name="regId" value="${registryId}" />
					<dsp:param name="siteId" value="${siteId}" />
					<dsp:param name="channel" value="Desktop Web" />
	   </dsp:include>
</div>
	<div>
		<dsp:droplet name="ErrorMessageForEach">
			<dsp:param bean="GiftRegistryFormHandler.formExceptions"
				name="exceptions" />
			<dsp:oparam name="output">
				<dsp:valueof param="message" />
				<br>
			</dsp:oparam> 
		</dsp:droplet>

	</div>
	 <%-- Client DOM XSRF | Part -1
	 dsp:input bean="GiftRegistryFormHandler.successURL" type="hidden" value="${contextPath}/giftregistry/registry_confirmation.jsp"/>
     <dsp:input bean="GiftRegistryFormHandler.errorURL" type="hidden" value="${contextPath}/giftregistry/registry_confirmation.jsp?failure=true"/> --%>
     <dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden" 
					value="regCreationConfirmation"/>
	  <dsp:input type="hidden" value="Submit" bean="GiftRegistryFormHandler.announcementCardCount" id="registryAnnouncementSubmit" iclass="announcementSubmit" >
	         <dsp:tagAttribute name="aria-pressed" value="false"/>
	         <dsp:tagAttribute name="aria-labelledby" value="registryAnnouncementSubmit"/>
	         <dsp:tagAttribute name="role" value="button"/>
	   </dsp:input>
      <dsp:input name="registryAnnouncement" type="hidden" bean="GiftRegSessionBean.registryVO.numRegAnnouncementCards" />
      <input type="button" class="visuallyhidden" value=" " />
 	</dsp:form>
<%--RKG micro pixel starts --%>
<c:if test="${appid == 'BuyBuyBaby'}">
<c:set var="midValue" value="buybuybaby"></c:set>
</c:if>
<c:if test="${appid == 'BedBathUS'}">
<c:set var="midValue" value="bedbathbeyond"></c:set>
</c:if>
<c:if test="${appid == 'BedBathCanada'}">
<c:set var="midValue" value="bedbathcanada"></c:set>
</c:if>
<img src="https://micro.rkdms.com/micro.gif?mid=${midValue}&type=registry&cid=${registryId}" alt="micro rkdms image" height="1" width="1"/>
<%--RKG micro pixel ends --%>

<%-- BBBSL-4343 DoubleClick Floodlight START  
   <c:if test="${DoubleClickOn}">
     <c:set var="rkgcollectionProd" value="${fn:trim(rkgProductList)}" />
	 <c:choose>
		 <c:when test="${not empty rkgcollectionProd }">
		 	<c:set var="rkgProductList" value="${rkgcollectionProd}"/>
		 </c:when>
		 <c:otherwise>
		 	<c:set var="rkgProductList" value="null"/>
		 </c:otherwise>
	 </c:choose>
	 <dsp:getvalueof var="zip" param="regVO.shipping.shippingAddress.zip"/>
 	<dsp:getvalueof var="state" param="regVO.shipping.shippingAddress.state"/>
 	<dsp:getvalueof var="regId" param="regVO.registryId"/>
 	<c:set var="revenue"><bbbc:config key="rkg_registry_revenue" configName="RKGKeys" /></c:set>
	 <c:choose>
  		 <c:when test="${(currentSiteId eq BuyBuyBabySite)}">
  		 	<c:set var="cat"><bbbc:config key="cat_registry_baby" configName="RKGKeys" /></c:set>
  		   <c:set var="src"><bbbc:config key="src_baby" configName="RKGKeys" /></c:set>
  		   <c:set var="type"><bbbc:config key="type_2_baby" configName="RKGKeys" /></c:set>
  		   <dsp:include page="/_includes/double_click_tag.jsp">
				<dsp:param name="doubleClickParam" value="src=${src};type=${type};cat=${cat};qty=1;cost=${revenue};u3=${revenue};u6=${zip};u7=${state};u10=null;u11=baby;u12=${regId}"/>
			</dsp:include>
  		 </c:when>
  		 <c:when test="${(currentSiteId eq BedBathUSSite)}">
  		 	<c:set var="cat"><bbbc:config key="cat_registry_bedBathUS" configName="RKGKeys" /></c:set>
		    <c:set var="src"><bbbc:config key="src_bedBathUS" configName="RKGKeys" /></c:set>
		    <c:set var="type"><bbbc:config key="type_3_bedBathUS" configName="RKGKeys" /></c:set>
		    <dsp:include page="/_includes/double_click_tag.jsp">
				<dsp:param name="doubleClickParam" value="src=${src};type=${type};cat=${cat};qty=1;cost=${revenue};u3=${revenue};u6=${zip};u7=${state};u10=null;u11=null;u15=${regId}"/>
			</dsp:include>
  		 </c:when>
  		  <c:when test="${(currentSiteId eq BedBathCanadaSite)}">
		    		   <c:set var="cat"><bbbc:config key="cat_registry_confirm_bedbathcanada" configName="RKGKeys" /></c:set>
		    		   <c:set var="src"><bbbc:config key="src_bedbathcanada" configName="RKGKeys" /></c:set>
		    		   <c:set var="type"><bbbc:config key="type_3_bedbathcanada" configName="RKGKeys" /></c:set>
		    		    <dsp:include page="/_includes/double_click_tag.jsp">
				<dsp:param name="doubleClickParam" value="src=${src};type=${type};cat=${cat};qty=1;cost=${revenue};u3=${revenue};u6=${zip};u7=${state};u10=null;u11=null;u15=${regId}"/>
			</dsp:include>
	   		 </c:when>
  		 </c:choose>
		
	</c:if>
DoubleClick Floodlight END --%>

 	<c:set var ="pageAction" scope="request">
 		<bbbc:config key="icrossing_pageaction_other" configName="ReferralControls" />
 	</c:set>
 	<c:if test="${registryType eq 'BA1'}">
 		<c:set var ="pageAction" scope="request">
 			<bbbc:config key="icrossing_pageaction_baby" configName="ReferralControls" />
 		</c:set>
 	</c:if>

<%--Start code for commission junction --%> 	
			<c:if test="${refId == 'cj' }">
			<dsp:include page="/_includes/commissionJunction.jsp">
			<dsp:param name="refUrl" value="${refUrl}"/>
			</dsp:include>
			</c:if>
<%-- Start code for wedding channel and bump pixel --%>
<c:if test="${refId == 'wc' || refId == 'bp'}">
<img src="${refUrl}" height="1" width="1">
</c:if>
<%-- end code for wedding channel and bump pixel --%>

<%--FB & Twitter Pixel tracking starts --%>
<%-- <c:if test="${pixelFbOn}">
	<c:set var="fb_regCreate"><bbbc:config key="pixel_fb_regCreate" configName="ContentCatalogKeys" /></c:set>
	<script>(function() {
	  var _fbq = window._fbq || (window._fbq = []);
	  if (!_fbq.loaded) {
	    var fbds = document.createElement('script');
	    fbds.async = true;
	    fbds.src = '//connect.facebook.net/en_US/fbds.js';
	    var s = document.getElementsByTagName('script')[0];
	    s.parentNode.insertBefore(fbds, s);
	    _fbq.loaded = true;
	  }
	})();
	window._fbq = window._fbq || [];
	window._fbq.push(['track', '${fb_regCreate}', {'value':'0.00','currency':'USD'}]);
	</script>
	<noscript><img height="1" width="1" alt="" style="display:none" src="https://www.facebook.com/tr?ev=${fb_regCreate}&amp;cd[value]=0.00&amp;cd[currency]=USD&amp;noscript=1" /></noscript>
</c:if> --%>

<c:if test="${pixelTwtOn}">
	<c:set var="twt_regCreate"><bbbc:config key="pixel_twt_regCreate" configName="ContentCatalogKeys" /></c:set>
	
	<!-- script src="//platform.twitter.com/oct.js" type="text/javascript"></script -->
	<script type="text/javascript">
	var twttrTracking = setInterval(isTwitterApiLoaded, 1000);
    function isTwitterApiLoaded() {
    	if (BBB.twttrjs) {
    		twttr.conversion.trackPid('${twt_regCreate}');
    		clearInterval(twttrTracking);
    	}
    }
	</script>
	<noscript>
	<img height="1" width="1" style="display:none;" alt="" src="https://analytics.twitter.com/i/adsct?txn_id=${twt_regCreate}&p_id=Twitter" />
	<img height="1" width="1" style="display:none;" alt="" src="//t.co/i/adsct?txn_id=${twt_regCreate}&p_id=Twitter" />
	</noscript>
</c:if>
<%--FB & Twitter Pixel tracking ends --%>

</jsp:body>
<jsp:attribute name="footerContent">
<script type="text/javascript">
    //Omniture
    var operation = '${operation}';
    var registryDesc = '${registryDesc}';
    var registryId =  '${registryIdForIcrossing}';
    if (typeof s !== 'undefined') {
        s.channel = 'Registry';
        s.prop1 = 'Registry';
        s.prop2 = 'Registry';
        s.prop3 = 'Registry';
        s.eVar23 = registryDesc;
        s.eVar24 = registryId;
        s.pageName = "Registry Confirm Page";
        if (operation == 'created') {
            s.events = "event21";
        } else {
            s.events = "event20";
        }
        var s_code = s.t();
        if (s_code) document.write(s_code);
    }
</script>
    </jsp:attribute>
</bbb:pageContainer> 
</dsp:page>
