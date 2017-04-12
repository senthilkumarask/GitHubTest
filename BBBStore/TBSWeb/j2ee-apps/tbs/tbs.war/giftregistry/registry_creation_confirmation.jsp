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
    <c:set var="TBS_BedBathCanadaSite">
		<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
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
		<c:when test="${appid == 'TBS_BuyBuyBaby'}">
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
		<%-- Commenting out CommissionJunction as part of 34473
		<c:if test="${ ( not empty registryDesc ) && CommisionJunctionOn && (cookieName eq 'cj')}">
					<dsp:droplet name="ReferralInfoDroplet">
						<dsp:oparam name="output">
							<dsp:getvalueof var="refId" param="refId"/>
							<c:set var="currencyId">USD</c:set>
							<c:choose>
			       	 		<c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
				       			<c:set var="cj_cid"><bbbc:config key="cj_cid_baby" configName="ReferralControls" /></c:set>
			    	   			<c:set var="cj_type"><bbbc:config key="cj_type_registry_confirmation_baby" configName="ReferralControls" /></c:set>
		       		 		</c:when>
		       		 		<c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
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
		--%>
        <div class="row">
            <div class="small-12 columns infoContentWraper bdrBottom">
				<div class="small-12 large-6 columns infoContent alpha">
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
			<input name="bedBath" id="shareRegURL" type="text" class="width_4" readonly="readonly" value="${registryURL}" aria-required="false" aria-labelledby="shareRegURL" />                        
                    	</div>
                	</div>
 					<div class="small-12 columns no-padding" title='<bbbl:label key='lbl_regcreate_confirm_copy' language="${pageContext.request.locale.language}" />'><a href="#" class="copy2Clip" data-copy-source="shareRegURL" ><bbbl:label key='lbl_regcreate_confirm_copy' language="${pageContext.request.locale.language}" /></a></div>                
				</div>	
				
				<%-- link back to kickstarters --%>				
				<c:if test="${not empty kType }">
					<div class="kickstarterReturnLink small-6 columns omega">						
						<h2><bbbt:textArea key="txt_regcreate_back_to_kickstarter_header" language="${pageContext.request.locale.language}" /></h2>						 
						<c:choose>
							<c:when test="${kType == 'Top Consultant'}">
								<div class="button button_active">
						  			<a href="${contextPath}/topconsultant/${kId}/${registryId}/${registryDesc}" ><bbbl:label key='lbl_regcreate_back_to_kickstarter_btn' language="${pageContext.request.locale.language}" /></a>
						  		</div>						      	
					      	</c:when>
					       	<c:when test="${kType == 'Shop This Look'}">
					       		<div class="button button_active">
					        		<a href="${contextPath}/shopthislook/${kId}/${registryId}/${registryDesc}"><bbbl:label key='lbl_regcreate_back_to_kickstarter_btn' language="${pageContext.request.locale.language}" /></a>
					        	</div>         					         	
					        </c:when>
					        <c:when test="${kType == 'KickStarter'}">
					        	<div class="button button_active">
					        		<a href="${contextPath}/kickstarters"><bbbl:label key='lbl_regcreate_back_to_kickstarter_btn' language="${pageContext.request.locale.language}" /></a>
					        	</div>         					         	
							</c:when>
						</c:choose>			
					</div>
				</c:if>
		</div>
		
		<dsp:getvalueof var="registryVOskg" param="regVO"/>
		<%-- BBBSL-2654 Defect fixed. Call to SearchStoreDroplet only when preffered store id is not empty --%>
		<c:if test="${not empty registryVOskg.prefStoreNum}">
			<dsp:droplet name="/com/bbb/selfservice/TBSNearbyStoreSearchDroplet">
	                 <dsp:param name="storeId" value="${registryVOskg.prefStoreNum}"/>
	                 <dsp:param name="siteId" value="${siteId}" />
	                  <dsp:param name="pageFrom" value="registryConfirmation" />
	                 <dsp:param name="searchType" value="2"/>
	                 <dsp:oparam name="output">
										 <dsp:getvalueof var="StoreDetails" param="nearbyStores"/>
	                </dsp:oparam>
	        </dsp:droplet>
		</c:if>
        <c:set var="contactFlag" value="${storeDetails.contactFlag}"/>
        <c:set var="skedgeURL">
			<bbbc:config key="skedgeURL" configName="ThirdPartyURLs" />
			</c:set>
			<dsp:droplet name="PrefStoreInPilotStoresValidationDroplet">
				<dsp:param name="prefredStoreId" value="${registryVOskg.prefStoreNum}"/>
			 	<dsp:oparam name="output">
					<dsp:getvalueof var="showIFrame" param="isUserAllowedToScheduleAStoreAppointment"/>
				</dsp:oparam>
			</dsp:droplet>
  			
 			
				<c:choose>
					<c:when test="${registryType eq 'COM' or registryType eq 'BRD' }">	
						<c:if test="${not empty skedgeURL and showIFrame}">
						<iframe type="some_value_to_prevent_js_error_on_ie7" id="apointmentScheduler" class="grid_12 omega apointmentScheduler" src="${skedgeURL}&storeId=${registryVOskg.prefStoreNum}&email=${registryVOskg.primaryRegistrant.email}&regFN=${registryVOskg.primaryRegistrant.firstName}&regLN=${registryVOskg.primaryRegistrant.lastName}&coregFN=${registryVOskg.coRegistrant.firstName}&coregLN=${registryVOskg.coRegistrant.lastName}&contactNum=${registryVOskg.primaryRegistrant.primaryPhone}&registryId=${registryVOskg.registryId}&site=${currentSiteId}&eventDate=${registryVOskg.event.eventDate}" ></iframe>
						</c:if>        
					</c:when>
				</c:choose>
	    </div>
      	  
      
	<dsp:form id="announcementCardsNum" action="${contextPath}/giftregistry/registry_confirmation.jsp">
    <div id="content" class="small-12 columns" role="main">
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
	 <%-- <dsp:input bean="GiftRegistryFormHandler.successURL" type="hidden" value="${contextPath}/giftregistry/registry_confirmation.jsp"/>
     <dsp:input bean="GiftRegistryFormHandler.errorURL" type="hidden" value="${contextPath}/giftregistry/registry_confirmation.jsp?failure=true"/> --%>
             <dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="registryCreationConf" />
	  <dsp:input type="hidden" value="Submit" bean="GiftRegistryFormHandler.announcementCardCount" id="registryAnnouncementSubmit" iclass="announcementSubmit" >
	         <dsp:tagAttribute name="aria-pressed" value="false"/>
	         <dsp:tagAttribute name="aria-labelledby" value="registryAnnouncementSubmit"/>
	         <dsp:tagAttribute name="role" value="button"/>
	   </dsp:input>
      <dsp:input name="registryAnnouncement" type="hidden" bean="GiftRegSessionBean.registryVO.numRegAnnouncementCards" />
 	</dsp:form>
<%--RKG micro pixel starts --%>
<c:if test="${appid == 'TBS_BuyBuyBaby'}">
<c:set var="midValue" value="buybuybaby"></c:set>
</c:if>
<c:if test="${appid == 'TBS_BedBathUS'}">
<c:set var="midValue" value="bedbathbeyond"></c:set>
</c:if>
<c:if test="${appid == 'TBS_BedBathCanada'}">
<c:set var="midValue" value="bedbathcanada"></c:set>
</c:if>
<img src="https://micro.rkdms.com/micro.gif?mid=${midValue}&type=registry&cid=${registryId}" height="1" width="1"/>
<%--RKG micro pixel ends --%>
 	<c:set var ="pageAction" scope="request">
 		<bbbc:config key="icrossing_pageaction_other" configName="ReferralControls" />
 	</c:set>
 	<c:if test="${registryType eq 'BA1'}">
 		<c:set var ="pageAction" scope="request">
 			<bbbc:config key="icrossing_pageaction_baby" configName="ReferralControls" />
 		</c:set>
 	</c:if>

<%--Start code for commission junction --%> 
<%-- Commenting out CommissionJunction as part of 34473	
			<c:if test="${refId == 'cj' }">
			<dsp:include page="/_includes/commissionJunction.jsp">
			<dsp:param name="refUrl" value="${refUrl}"/>
			</dsp:include>
			</c:if>
--%>
<%-- Start code for wedding channel and bump pixel --%>
<c:if test="${refId == 'wc' || refId == 'bp'}">
<img src="${refUrl}" height="1" width="1">
</c:if>
<%-- end code for wedding channel and bump pixel --%>
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
