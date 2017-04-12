<dsp:page>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<dsp:importbean
	bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryVODroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/RegistryInfoDisplayDroplet" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/DateCalculationDroplet" />
	<dsp:importbean bean="/com/bbb/email/EmailHolder"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/RedirectDroplet"/>
	<dsp:importbean bean="/com/bbb/framework/security/ValidateParametersDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/RecommendationInfoDisplayDroplet"/>
	<dsp:importbean bean="com/bbb/selfservice/ChatDroplet" />

	<dsp:getvalueof var="newRegistration" vartype="boolean" bean="Profile.newRegistration" />
	<dsp:getvalueof var="scheme" bean="/OriginatingRequest.scheme"/>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <dsp:getvalueof var="serverName" bean="/OriginatingRequest.serverName"/>
    <dsp:getvalueof var="serverPort" bean="/OriginatingRequest.serverPort"/>
	<dsp:getvalueof var="requestURIWithQueryString" bean="/OriginatingRequest.requestURIWithQueryString"/>
    <dsp:getvalueof var="registryId" param="registryId" scope="request"/>
    <dsp:getvalueof var="eventType" param="eventType"/>
	<dsp:getvalueof var="eventTypeForUpdate" param="eventType"/>
  	<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof bean="SessionBean.registryTypesEvent" id="registryEventType"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof var="c1name" param="c1name" scope="request"/>
	<dsp:getvalueof var="c2name" param="c2name" scope="request"/>
	<dsp:getvalueof var="c3name" param="c3name" scope="request"/>
	<dsp:getvalueof var="qty" param="qty" scope="request"/>
	<dsp:getvalueof var="c1id" param="c1id" scope="request"/>
	<dsp:getvalueof var="c2id" param="c2id" scope="request"/>
	<dsp:getvalueof var="c3id" param="c3id" scope="request"/>
	<dsp:setvalue  bean="EmailHolder.values.senderEmail" beanvalue="Profile.email"/>
	<dsp:getvalueof var="userEmail" bean="EmailHolder.values.senderEmail" />
	<c:set var="saSrc"><bbbc:config key="socialAnnexURL" configName="ThirdPartyURLs" /></c:set>
	<%-- <c:set var="saSrc" value="//cdn.socialannex.com/partner/9411181/universal.js" /> --%>
	
	<c:set var="eventTypeName" value="${eventType}"/>
	<c:if test="${eventType eq 'Commitment Ceremony'}">
		<c:set var="eventTypeName" value="Commitment_Ceremony"/>
	</c:if>
	<input type="hidden" id="eventTypeName" value="${eventTypeName}"/>
	<c:choose>
	<c:when test="${newRegistration eq true}">
		<c:set var="omniValuesForCreateAccAndReg" value="event21,event30,event36" />
	</c:when>
	<c:otherwise>
		<c:set var="omniValuesForCreateAccAndReg" value="event21" />
	</c:otherwise>
	</c:choose>
	<c:set var="WarrantyOn" scope="request">
		<bbbc:config key="WarrantyOn" configName="FlagDrivenFunctions" />
	</c:set>
	<dsp:getvalueof var="hoorayModal" param="hoorayModal"/>
	<c:set var="lbl_registry_show_hooray_modal"><bbbl:label key='lbl_registry_show_hooray_modal' language="${pageContext.request.locale.language}" /></c:set>


	<%-- for defect PS-17524 :start--%>

	<dsp:getvalueof var="cItemId" bean="GiftRegistryFormHandler.productId"/>
	<dsp:getvalueof var="cItemQty" bean="GiftRegistryFormHandler.quantity"/>
	<dsp:getvalueof var="cItemPrice" bean="GiftRegistryFormHandler.addedItemPrice"/>
	<dsp:getvalueof var="isItemAdded" bean="GiftRegistryFormHandler.itemAddedToRegistry"/>
	<dsp:getvalueof var="cItemSkuId" bean="GiftRegistryFormHandler.skuIds"/>
  <dsp:getvalueof var="hideInviteFriends" param="invite"/>

<c:set var="inviteFriend"><bbbc:config key="Invite_Friends_Key" configName="FlagDrivenFunctions" /></c:set>
<c:set var="inviteFriend" value="${fn:toLowerCase(inviteFriend)}"/>
	<%-- for defect PS-17524 :Ends --%>

	<%-- Validate external parameters --%>
	<c:if test="${( not empty registryId ) && !isTransient }">
		<dsp:droplet name="ValidateParametersDroplet">
		    <dsp:param value="registryId;eventType" name="paramArray" />
		    <dsp:param value="${param.registryId};${param.eventType}" name="paramsValuesArray" />
		    <dsp:oparam name="error">
		      <dsp:droplet name="RedirectDroplet">
		        <dsp:param name="url" value="/404.jsp" />
		      </dsp:droplet>
		    </dsp:oparam>
	    </dsp:droplet>
	</c:if>

	<%-- <c:choose>
		<c:when test="${not empty eventType && (eventType eq 'Wedding' || eventType eq 'Commitment Ceremony')}">
			<c:set var="pageVariation" value="br" scope="request" />
		</c:when>
		<c:otherwise>
			<c:set var="pageVariation" value="" scope="request" />
		</c:otherwise>
	</c:choose> --%>
    <%-- as per update from Raj/Lokesh all registry pages will use Purple theme on BedBathUS and BedBathCA --%>
    <c:choose>
        <c:when test="${not empty appid && appid eq 'BuyBuyBaby'}">
            <c:set var="pageVariation" value="" scope="request" />
        </c:when>
        <c:when test="${not empty eventType && eventType eq 'Baby'}">
            <c:set var="pageVariation" value="" scope="request" />
        </c:when>
        <c:otherwise>
            <c:set var="pageVariation" value="br" scope="request" />
        </c:otherwise>
    </c:choose>
    

	<dsp:getvalueof var="status" param="status"/>
	<c:set var="event" value="event49"/>

	<c:if test="${(null ne status) && (status eq 'update')}">
		<c:set var="event" value="event20"/>
	</c:if>
	<c:if test="${(null ne status) && (status eq 'remove')}">
		<c:set var="event" value="event19"/>
	</c:if>


<dsp:getvalueof var="isTransient" bean="Profile.transient"/>
<dsp:getvalueof var="sortSeq" param="sorting"/>
<dsp:getvalueof var="view" param="view"/>
	<input class="newRegistrationHoorayModal" name="newRegistration" value="${newRegistration}" type="hidden">
	<input type="hidden" name="cProd" value="${cItemId}" />
	<input type="hidden" name="cQty" value="${cItemQty}" />
	<input type="hidden" name="cPrice" value="${cItemPrice}" />
	<input type="hidden" name="cAddItemFlag" value="${isItemAdded}" />
	<input type="hidden" name="cSkuId" value="${cItemSkuId}" />

 <c:if test="${( not empty registryId ) && !isTransient }">

	<dsp:droplet name="GetRegistryVODroplet">
        <dsp:param name="siteId" value="${appid}"/>
        <dsp:param value="${registryId}" name="registryId" />
        <dsp:oparam name="output">
            <dsp:getvalueof  param="registryVO" var="regVO" scope="request"></dsp:getvalueof>
            <dsp:getvalueof var="regIsPublic" value="${regVO.isPublic}"/>
            <c:if test="${regIsPublic == 1}">
                <dsp:getvalueof var="regPublic" value="true"/>
            </c:if>
        </dsp:oparam>
    </dsp:droplet>

 
	<%-- 
		On first visit we want to show a modal window if the registry is private, 
		then set on the session to not show again 
		showPrivateRegistryModal: ${showPrivateRegistryModal}
	
	<c:if test="${empty showPrivateRegistryModal || showPrivateRegistryModal ne false}">
		<c:set var="showPrivateRegistryModal" scope="session" value="${false}" />
		<c:set var="showPrivateRegistryModalClass"  value="showPrvtRegModal" />
	</c:if>
	--%>	


<dsp:droplet name="RegistryInfoDisplayDroplet">
		<dsp:param value="${registryId}" name="registryId" />
		<dsp:param name="displayView" value="owner" />
		<dsp:oparam name="output">

  <dsp:getvalueof var="eventTypeParam" param="eventType"/>
  <!-- <dsp:getvalueof var="hideInviteFriends" param="invite"/>-->
    <dsp:getvalueof var="eventTypeVar" param="registrySummaryVO.eventType"/>
    <dsp:getvalueof var="eventTypeCode" param="registrySummaryVO.registryType.registryTypeName"/>
    <c:set var="regTypeName" value="${eventTypeCode}" scope="request"/>
	<dsp:getvalueof var="regFirstName" param="registrySummaryVO.primaryRegistrantFirstName"/>
	<dsp:getvalueof var="primaryRegistrantMobileNum" param="registrySummaryVO.primaryRegistrantMobileNum"/>
    <dsp:getvalueof var="regEventDate" param="registrySummaryVO.eventDate"/>
    <dsp:getvalueof var="registryVO" param="registryVO"/>
	<dsp:getvalueof var="coRegOwner" param="registryVO.coRegOwner"/>
   <dsp:getvalueof var="registrySummaryVO" param="registrySummaryVO"/>
    <dsp:getvalueof var="isChecklistDisabled" param="isChecklistDisabled"/>
   
    <input type="hidden" name="altNumber" value="${primaryRegistrantMobileNum}"/>
    <%-- SET UP VALUES FOR SKEDGE ME IFRAME/LINK --%>
    <dsp:getvalueof var="favouriteStoreId" param="registryVO.prefStoreNum"/>
	<dsp:droplet name="/com/bbb/selfservice/SearchStoreDroplet">
    	<dsp:param name="storeId" value="${favouriteStoreId}"/>
        <dsp:param name="searchType" value="2"/>
        <dsp:oparam name="output">
        	<dsp:getvalueof var="storeDetails" param="StoreDetails"/>
		</dsp:oparam>
	</dsp:droplet>
		<dsp:getvalueof var="registryTitle" param="registrySummaryVO.regTitle" />

		<!-- 	BBBSL-9360 | Checking whether the registry is public or private and setting the index and follow value -->
	
		<c:set var= "indexValue" value="false"></c:set>
		<c:set var= "followValue" value="false"></c:set>
	
<!-- 	BBBSL-9360 ends -->
	<bbb:pageContainer index="${indexValue}" follow="${followValue}">
		<jsp:attribute name="section">registry</jsp:attribute>
		<jsp:attribute name="pageVariation">${pageVariation}</jsp:attribute>
		<jsp:attribute name="pageWrapper">useStoreLocator giftView ownerView useFB useCertonaJs useBazaarVoice updateFormPopup useGoogleAddress</jsp:attribute>
		<jsp:attribute name="regTitle">${registryTitle}</jsp:attribute>
		<jsp:attribute name="PageType">RegistryOwner</jsp:attribute>
	<jsp:body>
	<dsp:getvalueof var="registryVOskg" param="registrySummaryVO"/>
    <c:set var="allowedToScheduleAStoreAppointment" value="${registryVOskg.allowedToScheduleAStoreAppointment}"/>        
    <c:set var="showSkedgeButton" value="false"/>

    <dsp:getvalueof bean="SessionBean.values.registrySkinnyVOList" var="registrySkinnyVOList" /> 
  	<input type="hidden" id="registryCount" name="registryCount" value=${fn:length(registrySkinnyVOList)} />
		<dsp:droplet name="ForEach">
			<dsp:param name="array" value="${registrySkinnyVOList}" />
			<dsp:oparam name="output">
				<dsp:param name="futureRegList" param="element" />
				<dsp:getvalueof var="regId"	param="futureRegList.registryId" />
		
				
				 <dsp:getvalueof var="alternateNumber"
					param="futureRegList.alternatePhone" />
					<dsp:getvalueof param="futureRegList.poBoxAddress" var="poBoxAddress"/>
					<input type="hidden" name="altNumber" id="${registryId}" value="${alternateNumber}"/>
					
					<c:if test="${regId eq registryId }">
						<input type="hidden" id="regPoBoxFlag" value="${poBoxAddress}" />
				
						<c:choose>
						<c:when test="${not empty alternateNumber}">
							<input type="hidden" name="altNumber" id="${registryId}" value="${alternateNumber}"/>
						</c:when>
						<c:otherwise>
							<input type="hidden" name="altNumber" id="${registryId}" value="${registryVO.primaryRegistrant.cellPhone}"/>
						</c:otherwise>
						</c:choose>
					</c:if>
			</dsp:oparam>
		</dsp:droplet>
  	<c:if test="${registryVOskg.allowedToScheduleAStoreAppointment}">
		<c:set var="skedgeURL">
			<bbbc:config key="skedgeURL" configName="ThirdPartyURLs" />
		</c:set>
		<dsp:getvalueof var="registryVOskg" param="registrySummaryVO"/>
		<c:if test="${not empty skedgeURL}">
			<c:set var="showSkedgeButton" value="true"/>					
		</c:if>        
	</c:if>	

	
    
	<c:choose>
	<c:when test="${eventTypeVar eq  eventTypeParam}">
    <div id="content" class="container_12 clearfix" role="main">
	
	<%--
	<style>
	#disabledtopItems{display:block !important;}
	#disabledtopItems .grid_2{width:976px !important;}
	.certonaProduct{width:300px;float:left;}
	#disabledtopItems .chatNow{display:none;}
	#disabledtopItems h6{padding-bottom:7px;}
	</style>
	--%>
       	<div class="grid_12 clearfix">
		<%-- Droplet for showing error messages --%>
		<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
			<dsp:param param="GiftRegistryFormHandler.formExceptions" name="exceptions"/>
			<dsp:oparam name="output">
                <div class="error marTop_20"><dsp:valueof param="message"/></div>
			</dsp:oparam>
		</dsp:droplet>



		<dsp:getvalueof var="eventDateStr" param="eventDate"/>
		<dsp:getvalueof var="giftRegistered" param="registrySummaryVO.giftRegistered"/>
		<c:set var="giftsRegistered" value="${giftRegistered}" scope="session"/>
		<dsp:getvalueof var="giftPurchased" param="registrySummaryVO.giftPurchased"/>
		<dsp:getvalueof var="eventTypeCode" param="registrySummaryVO.registryType.registryTypeName"/>
		<dsp:getvalueof var="babyGender" param="registrySummaryVO.eventVO.babyGender"/>
		<dsp:getvalueof var="guestCount" param="registrySummaryVO.eventVO.guestCount"/>

		<%-- faking percent purchased
		<c:set var="giftPurchased" >
			<fmt:formatNumber type="number" maxFractionDigits="0" value="${giftRegistered-(giftRegistered/3)}" />
		</c:set>
		--%>

		<dsp:getvalueof var="giftRemaining" param="registrySummaryVO.giftRemaining"/>
		<%-- get percentage to closest 5% --%>
		<c:choose>
			<c:when test="${giftRegistered eq '0' }">
			</c:when>
			<c:otherwise>
				<dsp:getvalueof var="giftsPercentagePurchased" value="${giftPurchased/giftRegistered}"/>
				<c:set var="giftsPercentagePurchased" >
					<fmt:formatNumber type="number" maxFractionDigits="0" value="${(giftsPercentagePurchased/5)*100}" />
				</c:set>
				<c:set var="giftsPercentagePurchased" >
					<fmt:formatNumber type="number" maxFractionDigits="0" value="${giftsPercentagePurchased*5}" />
				</c:set>
			</c:otherwise>
		</c:choose>




		<div id="registryHeader" class="grid_12 alpha omega"  >


			<div id="regEditCtrlRow" class="">
    			<ul>
    				<li>
    					<span class="icon-fallback-text"> 
    						<c:set var="lbl_registry_header_my_profile">
    							<bbbl:label key='lbl_registry_header_my_profile' language="${pageContext.request.locale.language}" />
    						</c:set>
    						<a class="icon icon-pencil editRegInfo" href="#" title="${lbl_registry_header_my_profile}"  aria-label="${lbl_registry_header_my_profile}" >
							</a>
							<span class="icon-text">${lbl_registry_header_my_profile}</span>
						</span>
					</li>
    				<li>
    					<c:set var="btnPrintTitle"><bbbl:label key='lbl_mng_regitem_printreg' language="${pageContext.request.locale.language}" /></c:set>
    					<span class="icon-fallback-text"> 
							<a class="icon icon-print btnPrint" title="${btnPrintTitle}" href="#" aria-label="${btnPrintTitle}" >								 
							</a>
							<span class="icon-text">${btnPrintTitle}</span>
						</span>
					</li>
    			</ul>


    		</div>

			<div id="regHeaderWrap" class="grid_9 alpha omega">

				<div id="sa_s22_instagram" class="img_placeholder grid_3 alpha omega ">

					<div id="saPlaceholder">
						<span class="icon-fallback-text">
	                		<span class="icon-gift" aria-hidden="true"></span>
	                		<span class="icon-text"></span>
	                	</span>
	                </div>
				</div>
				<script type="text/javascript">
					var
						site_id= '9411181',
						sa_page="1",
						sa_instagram_user_email= '${userEmail}',
						sa_instagram_registry_id= '${registryId}',
						sa_instagram_registry_type= '${eventType}',
						sa_instagram_registry_is_owner_view= '1';
					(function() {function sa_async_load() {
						var sa = document.createElement('script');sa.type = 'text/javascript';
						sa.async = true;sa.src = '${saSrc}';
						var sax = document.getElementsByTagName('script')[0];sax.parentNode.insertBefore(sa, sax);}
						if (window.attachEvent) {window.attachEvent('onload', sa_async_load);}
						else {window.addEventListener('load', sa_async_load,false);}})();
				</script>
				
		    	<div class="grid_6 alpha omega">
	            	<div class="regInfo">
	                    <%-- <h2 class="registryType"><dsp:valueof param="registrySummaryVO.eventType"/> <bbbl:label key="lbl_registry_header_registry" language="${pageContext.request.locale.language}" /></h2> --%>
	                    <h2 class="registryType"><bbbl:label key="lbl_registry_header_welcome_back" language="${pageContext.request.locale.language}" /></h2>
	                    <h1 class="registrantNames">
	                    	<dsp:getvalueof param="registrySummaryVO.coRegistrantFirstName" var="coRegistrantFirstName" />
	                    	                    	
	                    	<dsp:valueof param="registrySummaryVO.primaryRegistrantFirstName"/>
	                    	<%--
	                    	<c:choose>
								<c:when test="${empty coRegistrantFirstName && eventTypeVar ne 'College/University'}">
		                    		<span class="icon-fallback-text">
					                	<span class="icon-reg-ampersand" aria-hidden="true"></span>
					                	<span class="icon-text">and</span>
					                </span>

		                    		<a class="editRegInfo" href="#">_______________</a>
		                    	</c:when>
		                    	<c:when test="${eventTypeVar eq 'College/University'}">	                    		
		                    	</c:when>
								<c:otherwise>
									<span class="icon-fallback-text">
					                	<span class="icon-reg-ampersand" aria-hidden="true"></span>
					                	<span class="icon-text">and</span>
					                </span>
									${coRegistrantFirstName}
								</c:otherwise>
							</c:choose>	
	                    	--%>
	                    	<c:if test="${not empty coRegistrantFirstName }">
	                    		<span class="icon-fallback-text">
				                	<span class="icon-reg-ampersand" aria-hidden="true"></span>
				                	<span class="icon-text">and</span>
				                </span>
								${coRegistrantFirstName}
							</c:if>
	                    </h1>

	                    <div class="eventDateRow">
	                    	<span class="eventDateHeader">
		                    	<c:choose>
									<c:when test="${eventTypeCode eq 'BA1' }">
										Expected Arrival Date:
									</c:when>
									<c:otherwise>
										<dsp:valueof param="registrySummaryVO.eventType"/> <bbbl:label key="lbl_registry_header_date" language="${pageContext.request.locale.language}" />
									</c:otherwise>
								</c:choose>
							</span>
	                        <c:choose>
								<c:when test="${empty eventDateStr  || eventDateStr == '0'}">
		                    		<span class="eventDate"><a class="editRegInfo" href="#">_______________</a></span>
		                    	</c:when>
								<c:otherwise>
									<span class="eventDate">${eventDateStr}</span>
								</c:otherwise>
							</c:choose>
							
							<c:if test="${eventType == 'Baby'}">
		                        <span class="getGender"><bbbl:label key="lbl_event_baby_gender" language="${pageContext.request.locale.language}" />:
		                        	<c:choose>
		                        		<c:when test="${babyGender == 'B'}">
			                        		It's a Boy!
			                        	</c:when>
		                        		<c:when test="${babyGender == 'G'}">
		                        			It's a Girl!
		                        		</c:when>
		                        		<c:when test="${babyGender == 'T'}">
		                        			It's multiples!
		                        		</c:when>
		                        		<c:otherwise>
		                        			It's a surprise!
		                        		</c:otherwise>
		                        	</c:choose>
		                        </span>
		                        <div class="clearfix"> </div>
		                    </c:if>

		                    <span class="registryIdHeader"><bbbl:label key="lbl_registry_id" language="${pageContext.request.locale.language}" /></span>
							<span class="registryId"><dsp:valueof param="registrySummaryVO.registryId"/></span>
						
	                    </div>

							


	                    
	                    <c:choose>
	                		<c:when test="${regPublic}">
	                    		
	                    	</c:when>
	                		<c:otherwise>
	                			
								<div id="regCompleteBtnRow">	
			                    	<button class="button-Med btnPrimary editRegInfo"><bbbl:label key="lbl_registry_complete_profile" language="${pageContext.request.locale.language}" /></button>
			                    	<p><bbbl:label key="lbl_registry_complete_profile_hdr_msg" language="${pageContext.request.locale.language}" /></p>
			                    </div>		
	                		</c:otherwise>
	                	</c:choose>
	                    

	                    <div id="registryVisibilityRow">
							<span class="regVisibilityLbl"><bbbl:label key="lbl_registry_visibility" language="${pageContext.request.locale.language}" /></span> 
							<c:choose>
		                		<c:when test="${regPublic}">
		                    		<span class="regVisibility"><bbbl:label key="lbl_registry_visibility_public" language="${pageContext.request.locale.language}" /></span>
		                    	</c:when>
		                		<c:otherwise>
		                			<span class="regVisibility"><bbbl:label key="lbl_registry_visibility_private" language="${pageContext.request.locale.language}" /></span>
		                		</c:otherwise>
		                	</c:choose>
	                    

							<div class="whatsThis">
								<span class="icon-fallback-text"> 
									<a class="info icon icon-question-circle" id="regPrivateInfo"  aria-hidden="true">
										<span>
											<strong><bbbl:label key="lbl_registry_public_vs_private" language="${pageContext.request.locale.language}" /></strong>
											<bbbl:label key="lbl_registry_public_vs_private_info" language="${pageContext.request.locale.language}"/></span> 
									</a>
									<span class="icon-text"><bbbl:label key="lbl_registry_public_vs_private_info" language="<c:out param='${language}'/>"/></span>
								</span>
							</div> 
						</div>

		            </div>
		    	</div>

	    	</div>

	    	<div class="grid_3 fr alpha omega hidden" id="regHeaderQuoteWrapper">
	        	<c:choose>
            		<c:when test="${regPublic}">
                		<c:choose>
							<c:when test="${not empty eventType && eventType eq 'Wedding'}">
								<bbbt:textArea key="txt_registry_quotes_wedding_public" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'Baby'}">
								<bbbt:textArea key="txt_registry_quotes_baby_public" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'Birthday'}">
								<bbbt:textArea key="txt_registry_quotes_birthday_public" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'Retirement'}">
								<bbbt:textArea key="txt_registry_quotes_retirement_public" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'Anniversary'}">
								<bbbt:textArea key="txt_registry_quotes_anniversary_public" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'Housewarming'}">
								<bbbt:textArea key="txt_registry_quotes_housewarming_public" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'Commitment Ceremony'}">
								<bbbt:textArea key="txt_registry_quotes_commitment_public" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${not empty eventType && fn:contains(eventType, 'University')}">
								<bbbt:textArea key="txt_registry_quotes_college_public" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'Other'}">
								<bbbt:textArea key="txt_registry_quotes_other_public" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:otherwise>								
								<bbbt:textArea key="txt_registry_quotes_wedding_public" language ="${pageContext.request.locale.language}"/>
							</c:otherwise>
						</c:choose>
                	</c:when>
            		<c:otherwise>
            			<c:choose>
							<c:when test="${not empty eventType && eventType eq 'Wedding'}">
								<bbbt:textArea key="txt_registry_quotes_wedding_private" language ="${pageContext.request.locale.language}"/>

							</c:when>
							<c:when test="${not empty eventType && eventType eq 'Baby'}">
								<bbbt:textArea key="txt_registry_quotes_baby_private" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'Birthday'}">
								<bbbt:textArea key="txt_registry_quotes_birthday_private" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'Retirement'}">
								<bbbt:textArea key="txt_registry_quotes_retirement_private" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'Anniversary'}">
								<bbbt:textArea key="txt_registry_quotes_anniversary_private" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'Housewarming'}">
								<bbbt:textArea key="txt_registry_quotes_housewarming_private" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'Commitment Ceremony'}">
								<bbbt:textArea key="txt_registry_quotes_commitment_private" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${not empty eventType && fn:contains(eventType, 'University')}">
								<bbbt:textArea key="txt_registry_quotes_college_private" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'Other'}">
								<bbbt:textArea key="txt_registry_quotes_other_private" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:otherwise>								
								<bbbt:textArea key="txt_registry_quotes_wedding_private" language ="${pageContext.request.locale.language}"/>
							</c:otherwise>
						</c:choose>
            		</c:otherwise>
            	</c:choose>

	        	
	        </div>



	    </div>

		<div id="regToolbar" class="noprint grid_12 alpha omega">
			<div class="regGiftTools topLevel fl">
				<ul>
					<c:set var="lbl_registry_browse_add_gifts"><bbbl:label key='lbl_registry_browse_add_gifts' language="${pageContext.request.locale.language}" /></c:set>

	                <li id="browseAddGifts">
	                	<a href="#" aria-label="${lbl_registry_browse_add_gifts}">
		                	<span class="icon-fallback-text">
		                		<span class="icon-gift" aria-hidden="true"></span>
		                		<span class="icon-text">${lbl_registry_browse_add_gifts}</span>
		                	</span>
		                	<span class="toolLbl">${lbl_registry_browse_add_gifts}</span>
		                </a>
	               </li>
	                <li class="nolink">
	                	<span class="giftCounter" id="regGiftsWanted"><dsp:valueof param="registrySummaryVO.giftRegistered"/></span> 
	                	<span class="toolLbl"><bbbl:label key="lbl_registry_header_gifts_in_registry" language="${pageContext.request.locale.language}" /></span>
	                </li>
	                <li  class="nolink">
	                	<span class="giftCounter">${giftPurchased}</span> 
	                	<span class="toolLbl"><bbbl:label key="lbl_registry_header_gifts_purchased" language="${pageContext.request.locale.language}" /></span></li>
	             </ul>
			</div>
			<div class="regUtilityLinks topLevel fr">
	            <ul>
	            	<%-- temp for debugging 
	            	<c:set var="showSkedgeButton" value="${true}" /> --%>
	            	<%-- if registry preferred store is set, pass it as a data param in the link --%>
	            	<%-- only show for registry type wedding, commitement, college, baby (but not baby on bedbath)            	
	            	UPDATE: leaving this to be determined by data
	            	<c:if test="${	eventTypeCode=='BRD' || 
	            					eventTypeCode=='COM' || 
	            					eventTypeCode=='COL' || 
	            					(eventTypeCode=='BA1' && (appid eq 'BuyBuyBaby' || appid eq 'BedBathCanada')) }">
			        </c:if>    
	            	--%>	 
	            		<c:choose>
	            			<c:when test="${eventTypeCode=='BA1' && (appid eq 'BuyBuyBaby' || appid eq 'BedBathCanada')}">
			            		<c:set var="appointmentcode" value="BBY" />
							</c:when>
			            	<c:when test="${eventTypeCode == 'COL'}">
			            		<c:set var="appointmentcode" value="COL" />
							</c:when>
							<c:when test="${eventTypeCode == 'BRD' || eventTypeCode == 'COM'}">
			            		<c:set var="appointmentcode" value="REG" />
							</c:when>				
							<c:otherwise>
								<c:set var="appointmentcode" value="" />
			             	</c:otherwise>
			            </c:choose>
		            	<c:choose>
			            	<c:when test="${showSkedgeButton eq true}">
			            		<li id="scheduleAppointment" class="scheduleAppointmentCall" data-param-isregheader="true" data-param-appointmentcode="${appointmentcode}" data-param-storeid="${favouriteStoreId}" data-param-registryId="${registryId}" data-param-coregFN="" data-param-coregLN="" data-param-eventDate="${regEventDate}" ></li>				
			            		<%--
								<c:set var="scheduleAppointment"><bbbl:label key='lbl_mng_regitem_schedule_appointment' language="${pageContext.request.locale.language}" /></c:set>
								<li id="scheduleAppointment">	       
										<a id="registryHeaderSchedule" href="#" class="launchApointmentScheduler" title="${scheduleAppointment}">
											<span class="icon-fallback-text">
												<span class="icon-calendar" aria-hidden="true"></span>
												<span class="icon-text"></span>
											</span>
											<span class="toolLbl">${scheduleAppointment}</span>								
										</a>
								</li>		
								--%>	
							</c:when>						
							<c:otherwise>
								<li id="scheduleAppointment" class="scheduleAppointmentCall" data-param-isregheader="true" data-param-appointmentcode="${appointmentcode}" data-param-storeid="" data-param-registryId="${registryId}" data-param-coregFN="" data-param-coregLN="" data-param-eventDate="${regEventDate}" ></li>
			             	</c:otherwise>
			            </c:choose>
			        
					<dsp:droplet name="ChatDroplet">
				    	<dsp:oparam name="output">
				    		<dsp:getvalueof var="chatglobalFlag" param="chatglobalFlag"></dsp:getvalueof>

				    		<c:if test="${chatglobalFlag == 'true'}">
								<li>
									<c:set var="linkContent">
										<span class="icon-fallback-text">
											<span class="icon-bubbles" aria-hidden="true"></span>
											<span class="icon-text"></span>
										</span>
										<span class="toolLbl">Chat Live</span>
									</c:set>
									<div id="chatNowLink">
										<dsp:include page="/common/click2chatlink.jsp">
								        	<dsp:param name="pageId" value="2"/>
								        	<dsp:param name="linkContent" value="${linkContent}"/>
								        	<dsp:param name="divApplied" value=""/>
								        </dsp:include>
							        </div>
						        </li>
						    </c:if>
   						</dsp:oparam>
   					</dsp:droplet>    

			        <%--
	                <li id="myProfile">
						<a href="${contextPath}/giftregistry/registry_creation_form.jsp?registryId=${registryId}" title="<bbbl:label key="lbl_registry_header_my_profile" language="${pageContext.request.locale.language}" />"  aria-label="<bbbl:label key="lbl_registry_header_my_profile" language="${pageContext.request.locale.language}" />">
							<span class="icon-fallback-text"><span class="icon-user" aria-hidden="true"></span></span><bbbl:label key="lbl_registry_header_my_profile" language="${pageContext.request.locale.language}" />
	                	</a>
	                </li>
	                --%>
	             <dsp:droplet name="DateCalculationDroplet">
					  <dsp:param param="registrySummaryVO.eventDate" name="eventDate" />
					  <dsp:param name="convertDateFlag" value="true" />
					  <dsp:oparam name="output">
					   <dsp:getvalueof var="displayInviteFriendsCheck" param="daysCheck"/>
					  </dsp:oparam>
				 </dsp:droplet>
				 <%-- calling only to bring the count for the recommendation tab
				 	  by passing the parameter
					  fromViewRegistryOwner=true --%>
				 <dsp:droplet name="RecommendationInfoDisplayDroplet">
				 	<dsp:param name="registryId" value="${registryId}"/>
				 	<dsp:param name="tabId" value="0"/>
				 	<dsp:param name="fromViewRegistryOwner" value="true"/>
				 	<dsp:oparam name="output">
				 		<dsp:getvalueof var="recommendationCount" param="recommendationCount"/>
				 		<dsp:getvalueof var="emptyRegistrant" param="recommendationProductSize"/>
				 		<c:if test="${displayInviteFriendsCheck eq false}">
				 			<dsp:getvalueof var="pendingItemCount" param="recommendationProductSize"/>
				 		</c:if>
				 	</dsp:oparam>
				 </dsp:droplet>
	             <li class="hidden">
	             <c:choose>
	                 <c:when test="${emptyRegistrant eq 0}">
	              		<input type="hidden" name="emptyRegistrant" value="true" />
	             	</c:when>
	             	<c:otherwise>
	             		<input type="hidden" name="emptyRegistrant" value="false" />
	             	</c:otherwise>
	             </c:choose>
	              </li>
				 <c:choose>
	                <c:when test="${displayInviteFriendsCheck == 'true'}">
	                       <c:set var="showRecommendationTab">true</c:set>
	                </c:when>
	                <c:otherwise>
	                    <c:choose>
		                   <c:when test="${pendingItemCount > 0}">
	                               <c:set var="showRecommendationTab">true</c:set>
		                   </c:when>
		                   <c:otherwise>
		                           <c:set var="showRecommendationTab">false</c:set>
		                   </c:otherwise>
	                    </c:choose>
	                </c:otherwise>
					
	             </c:choose>
	             	<%-- invite link is removed for registry header redesign 
	                <c:if test="${inviteFriend eq 'false' && displayInviteFriendsCheck eq 'true'}">
		                 <li id="inviteFriends">
		                 <!-- BPS-1112 Persist registry Details in GiftRegistryRepository -->
		                 <c:set var="inviteFriendURL">
		                 ${contextPath}/_includes/modals/inviteFriendsRegistry.jsp?eventType=${eventType}&registryId=${registryId}&regEventDate=${regEventDate}&regFirstName=${regFirstName}&emptyRegistrant=${emptyRegistrant}
		                 </c:set>
		                <a href='${inviteFriendURL}' title='<bbbl:label key="lbl_invite_friends" language="${pageContext.request.locale.language}"/>'><span></span><bbbl:label key="lbl_invite_friends_link" language="${pageContext.request.locale.language}" /></a>
		                </li>
	                </c:if>
	                --%>
	                <li id="share">
	                	<c:set var="shareLabel"><bbbl:label key='lbl_registry_header_share' language="${pageContext.request.locale.language}" /></c:set>
	                	<a href="#" id="regHeaderShare" title="${shareLabel}" aria-label='${shareLabel}' >
	                		<span class="icon-fallback-text">
	                			<span class="icon-share-alt" aria-hidden="true"></span>
	                			<span class="icon-text"></span>
	                		</span>
	                		<span class="toolLbl">${shareLabel}</span>
	                	</a>

	                	<dsp:form method="post" action="index.jsp" id="frmRegistryInfo">

							<dsp:droplet name="DateCalculationDroplet">
								<dsp:param param="registrySummaryVO.eventDate" name="eventDate" />
								<dsp:param name="convertDateFlag" value="true" />
								<dsp:oparam name="output">
									<dsp:setvalue bean="EmailHolder.values.daysToGo" paramvalue="daysToGo" />
									<dsp:setvalue  bean="EmailHolder.values.dateCheck" paramvalue="check" />
									<dsp:setvalue  bean="EmailHolder.values.daysToNextCeleb" paramvalue="daysToNextCeleb" />
								</dsp:oparam>
							</dsp:droplet>

							<dsp:getvalueof var="guest_registry_uri" param="registryURL"/>

		          			<c:url var="registryURL"
								value="${scheme}://${serverName}:${serverPort}${contextPath}${guest_registry_uri}">
		            			<c:param name="registryId" value="${registryId}"/>
		            			<c:param name="eventType" value="${eventType}"/>
	        					<c:param name="pwsurl" value="${pwsurl}"/>
		          			</c:url>

							<dsp:setvalue  bean="EmailHolder.values.eventType" paramvalue="registrySummaryVO.registryType.registryTypeName"/>
							<dsp:setvalue  bean="EmailHolder.values.registryURL" value="${registryURL}"/>
							<dsp:setvalue  bean="EmailHolder.values.senderEmail" beanvalue="Profile.email"/>
							<dsp:setvalue  bean="EmailHolder.values.registryId" value="${param.registryId}" />
							<dsp:setvalue  bean="EmailHolder.formComponentName"
													value="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
							<dsp:setvalue  bean="EmailHolder.handlerName" value="EmailRegistry" />
							<c:set var="emailTitle"><bbbl:label key='lbl_mng_regitem_emailreg_title' language="${pageContext.request.locale.language}" /></c:set>
							<dsp:setvalue  bean="EmailHolder.values.title" value="${emailTitle}" />



							<dsp:setvalue  bean="EmailHolder.values.pRegFirstName" paramvalue="registrySummaryVO.primaryRegistrantFirstName" />
							<dsp:setvalue  bean="EmailHolder.values.pRegLastName" paramvalue="registrySummaryVO.primaryRegistrantLastName" />
							<dsp:setvalue  bean="EmailHolder.values.coRegFirstName" paramvalue="registrySummaryVO.coRegistrantFirstName" />
							<dsp:setvalue  bean="EmailHolder.values.coRegLastName" paramvalue="registrySummaryVO.coRegistrantLastName" />
							<dsp:setvalue  bean="EmailHolder.values.eventDate" paramvalue="registrySummaryVO.eventDate" />
							<dsp:setvalue  bean="EmailHolder.values.eventTypeRegistry" paramvalue="registrySummaryVO.eventType" />
							<c:set var="defaultSubjectTxt"><bbbl:label key='lbl_emai_registry_subject' language='${pageContext.request.locale.language}'/></c:set>
						    <dsp:setvalue  bean="EmailHolder.values.subject" value="${defaultSubjectTxt}" />

						    <div id="emailAFriendData">
							<dsp:getvalueof bean="EmailHolder.values.dateCheck" var="dateCheck"/>
							<dsp:getvalueof bean="EmailHolder.values.daysToNextCeleb" var="daysToNextCeleb"/>
	                        <dsp:getvalueof bean="EmailHolder.values.daysToGo" var="daysToGo"/>
	                        <dsp:getvalueof bean="EmailHolder.values.eventType" var="eventType"/>
	                        <dsp:getvalueof bean="EmailHolder.values.registryURL" var="registryURL"/>
	                        <dsp:getvalueof bean="EmailHolder.values.senderEmail" var="senderEmail"/>
	                        <dsp:getvalueof bean="EmailHolder.values.registryId" var="registryId"/>
	                        <dsp:getvalueof bean="EmailHolder.formComponentName" var="formComponentName"/>
	                        <dsp:getvalueof bean="EmailHolder.handlerName" var="handlerName"/>
	                        <dsp:getvalueof bean="EmailHolder.values.title" var="title"/>
	                        <dsp:getvalueof bean="EmailHolder.values.pRegFirstName" var="pRegFirstName"/>
	                        <dsp:getvalueof bean="EmailHolder.values.pRegLastName" var="pRegLastName"/>
	                        <dsp:getvalueof bean="EmailHolder.values.coRegFirstName" var="coRegFirstName"/>
	                        <dsp:getvalueof bean="EmailHolder.values.coRegLastName" var="coRegLastName"/>
	                        <dsp:getvalueof bean="EmailHolder.values.eventDate" var="eventDate"/>
	                        <dsp:getvalueof bean="EmailHolder.values.eventTypeRegistry" var="eventTypeRegistry"/>
	                        <dsp:getvalueof bean="EmailHolder.values.subject" var="subject"/>

							<input type="hidden" name="dateCheck" value="${dateCheck}" class="emailAFriendFields"/>
							<input type="hidden" name="daysToNextCeleb" value="${daysToNextCeleb}" class="emailAFriendFields"/>
	                        <input type="hidden" name="daysToGo" value="${daysToGo}" class="emailAFriendFields"/>
	                        <input type="hidden" name="eventType" value="${eventType}" class="emailAFriendFields"/>
	                        <input type="hidden" name="registryURL" value="${registryURL}" class="emailAFriendFields"/>
	                        <input type="hidden" id="userEmail" name="senderEmail" value="${senderEmail}" class="emailAFriendFields"/>
	                        <input type="hidden" name="registryId" value="${registryId}" class="emailAFriendFields"/>
	                        <input type="hidden" name="formComponentName" value="${formComponentName}" class="emailAFriendFields"/>
	                        <input type="hidden" name="handlerName" value="${handlerName}" class="emailAFriendFields"/>
	                        <input type="hidden" name="title" value="${title}" class="emailAFriendFields"/>
	                        <input type="hidden" name="pRegFirstName" value="${pRegFirstName}" class="emailAFriendFields"/>
	                        <input type="hidden" name="pRegLastName" value="${pRegLastName}" class="emailAFriendFields"/>
	                        <input type="hidden" name="coRegFirstName" value="${coRegFirstName}" class="emailAFriendFields"/>
	                        <input type="hidden" name="coRegLastName" value="${coRegLastName}" class="emailAFriendFields"/>
	                        <input type="hidden" name="eventDate" value="${eventDate}" class="emailAFriendFields"/>
	                        <input type="hidden" name="eventTypeRegistry" value="${eventTypeRegistry}" class="emailAFriendFields"/>
	                        <input type="hidden" name="subject" value="${subject}" class="emailAFriendFields"/>
	                        <dsp:input type="hidden" name="isInternationalCustomer"bean="SessionBean.internationalShippingContext" value="${isInternationalCustomer}" />
	                        </div>
	                        <input type="submit" class="visuallyhidden hidden" value=" " />

						</dsp:form>
						<dsp:getvalueof var="eventType" param="eventType"/>

	            		<div id="regSharePopup" class="hidden" >
							
							<c:if test="${!regPublic}">
								<div class=" alert alert-alert  clearfix">
									<bbbl:label key='lbl_registry_share_modal_private_alert' language="${pageContext.request.locale.language}" />
								</div>
								<div class="overlay"></div>
							</c:if>

	            			<%-- make this URL rewrite value --%>
	    					<c:set var="shareURL" >${pageContext.request.requestURL}?${pageContext.request.queryString}</c:set>
	    					<dsp:droplet name="/com/bbb/account/droplet/BBBURLEncodingDroplet">
	                            <dsp:param name="URL" value="${registryURL}"/>
	                            <dsp:oparam name="output">
	                                <dsp:getvalueof var="shareURL" param="encodedURL"/>
	                            </dsp:oparam>
	                        </dsp:droplet>
	                        
	                        <c:set var="logoURL" value="${scheme}://${serverName}" />
		            		
		            		<c:choose>
	                            <c:when test="${currentSiteId == BedBathCanadaSite || currentSiteId == BedBathUSSite}">
	                                <c:choose>
	                                    <c:when test="${themeName == 'br'}">		                                            
	                                    	<c:set var="logoURL" >${logoURL}/_assets/global/images/logo/logo_br${logoCountry}.png</c:set>
	                                    </c:when>
	                                    <c:when test="${themeName == 'bc'}">
	                                        <c:set var="logoURL" >${logoURL}/_assets/global/images/logo/logo_bbb${logoCountry}.png</c:set>		                                            
	                                    </c:when>
	                                    <c:otherwise>
	                                        <c:set var="logoURL" >${logoURL}/_assets/global/images/logo/logo_bbb${logoCountry}.png</c:set>
	                                    </c:otherwise>
	                                </c:choose>
	                            </c:when>
	                            <c:otherwise>
	                                <c:set var="logoURL" >${logoURL}/_assets/global/images/logo/logo_bb_circle.png</c:set>		                                    
	                            </c:otherwise>
	                        </c:choose>
	                        

		                	<%--
		                	Facebook: http://www.facebook.com/sharer.php?s=100&p[url]={url}&p[images][0]={img}&p[title]={title}&p[summary]={desc}
		                	Twitter: https://twitter.com/share?url={url}&text={title}&via={via}&hashtags={hashtags}
		                	Google + https://plus.google.com/share?url={url}
		                	Pinterest: https://pinterest.com/pin/create/bookmarklet/?media={img}&url={url}&is_video={is_video}&description={title}
		                	--%>

		                	<div id="regSharePopupLinks" class="sharePopupSection">

		                		<h2><bbbl:label key='lbl_registry_share_modal_share_title' language="${pageContext.request.locale.language}" /></h2>
		                		<h3><bbbl:label key='lbl_registry_share_modal_share_subtitle' language="${pageContext.request.locale.language}" /></h3>

		                		<div>						
			            			<a href="http://www.facebook.com/sharer.php?s=100&p[url]=${shareURL}" target="_blank" class="social_button icon_facebook" aria-label="<bbbl:label key='lbl_share_facebook' language='${pageContext.request.locale.language}' />" ></a>
				                	<a href="https://twitter.com/share?url=${shareURL}" target="_blank" class="social_button icon_twitter" aria-label="<bbbl:label key='lbl_share_registry_twitter' language='${pageContext.request.locale.language}' />"></a>
				                	<a href="https://pinterest.com/pin/create/bookmarklet/?url=${shareURL}&media=${logoURL}" target="_blank" class="social_button icon_pin" aria-label="<bbbl:label key='lbl_share_registry_pinterest' language='${pageContext.request.locale.language}' />" ></a>
				                	<a href="https://plus.google.com/share?url=${shareURL}" target="_blank" class="social_button icon_gplus" aria-label="<bbbl:label key='lbl_share_registry_googleplus' language='${pageContext.request.locale.language}' />" ></a>
			            			<a href="#" class="btnEmail " title="<bbbl:label key='lbl_mng_regitem_emailreg_title' language="${pageContext.request.locale.language}" />" aria-label="<bbbl:label key='lbl_share_registry_email' language='${pageContext.request.locale.language}' />" >				            	
				            			<span class="icon-fallback-text">
				                			<span class="icon-envelope" aria-hidden="true"></span>
				                			<span class="icon-text"><bbbl:label key='lbl_mng_regitem_emailreg_title' language="${pageContext.request.locale.language}" /></span>
				                		</span>
			            			</a>
								</div>

								<input name="bedBath" id="shareRegURL" type="text" class="width_4" readonly="readonly" value="${registryURL}" aria-required="false" aria-hidden="true" aria-labelledby="shareRegURL" /> 
								<div class="clearfix"></div>

								<c:choose>
		            				<c:when test="${regPublic}">
		            					<a href="#" class="copy2Clip button-Med btnSecondary" data-copy-source="shareRegURL" aria-label="Copy Your ${eventType} Registry link" ><bbbl:label key='lbl_registry_share_modal_copy_url' language="${pageContext.request.locale.language}" /></a>		
		            				</c:when>
		            				<c:otherwise>
							            <input type="button" class="button-Med btnSecondary" value="<bbbl:label key='lbl_registry_share_modal_copy_url' language="${pageContext.request.locale.language}"/>" />
							        </c:otherwise>
							    </c:choose>


								
								
							<%--
		            			<input type="text" id="shareURL" value="${registryURL}" />
		            			<div class="clearfix"></div>
		            			<input type="button" class="button-Med btnSecondary" value="<bbbl:label key='lbl_registry_share_modal_copy_url' language="${pageContext.request.locale.language}" />" />
							--%>
	            			</div>	            			
	            			<c:choose>
	            				<c:when test="${inviteFriend eq 'false' && displayInviteFriendsCheck eq 'true' && regPublic}">
	            					<c:set var="disabledCSS" value="" />
	            				</c:when>
	            				<c:when test="${!regPublic}">
	            					<c:set var="disabledCSS" value="" />
	            				</c:when>
	            				<c:otherwise>
						            <c:set var="disabledCSS" value="disabled"  />
						        </c:otherwise>
						    </c:choose>

	            			<div id="regSharePopupSocialRec" class="sharePopupSection ${disabledCSS}">

	            				<h2><bbbl:label key='lbl_registry_share_modal_rec_title' language="${pageContext.request.locale.language}" /></h2>
		                		<h3><bbbl:label key='lbl_registry_share_modal_rec_subtitle' language="${pageContext.request.locale.language}" /></h3>

		                		<p><bbbl:label key='lbl_registry_share_modal_rec_info' language="${pageContext.request.locale.language}" /></p>


								<c:choose>
									<c:when test="${inviteFriend eq 'false' && displayInviteFriendsCheck eq 'true'}">
						            
						                 <!-- BPS-1112 Persist registry Details in GiftRegistryRepository -->
						                 <c:set var="inviteFriendURL">
						                 ${contextPath}/_includes/modals/inviteFriendsRegistry.jsp?eventType=${eventType}&registryId=${registryId}&regEventDate=${regEventDate}&regFirstName=${regFirstName}&emptyRegistrant=${emptyRegistrant}
						                 </c:set>
						            
						                <a href='${inviteFriendURL}' class="button-Med btnPrimary inviteFriends" title='<bbbl:label key="lbl_registry_share_modal_rec_invite_now" language="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_registry_share_modal_rec_invite_now" language="${pageContext.request.locale.language}" /></a>
						            
					                </c:when>				                
		            				<c:otherwise>
							            <input type="button" disabled="disabled" class="button-Med btnPrimary" value="<bbbl:label key='lbl_registry_share_modal_rec_invite_now' language="${pageContext.request.locale.language}" />" />
							        </c:otherwise>
							    </c:choose>

	            			</div>
	            		</div>


	                </li>

	                <%--
	                <li id="print">
	                	<c:set var="btnPrintTitle"><bbbl:label key='lbl_mng_regitem_printreg' language="${pageContext.request.locale.language}" /></c:set>
						<a href="#" class="btnPrint" title="${btnPrintTitle}">
							<span class="icon-fallback-text"><span class="icon-print" aria-hidden="true"></span><span class="icon-text"></span></span>${btnPrintTitle}
						</a>
					</li>
					--%>
					<c:set var="lbl_reg_tools"><bbbl:label key='lbl_registry_header_registry_tools' language="${pageContext.request.locale.language}" /></c:set>
					
					<li  class="last">
	                	<a id="registryTools" href="#"  title="${lbl_reg_tools}" />
							<span class="icon-fallback-text">
								<span class="icon-menu" aria-hidden="true"></span>
								<span class="icon-text">${lbl_reg_tools}</span>
							</span>
							<span class="toolLbl">${lbl_reg_tools}</span>	
						</a>	


						<div class="grid_3 fr" id="regToolsFlyout" style="display:none;">

							<a href="#" class="regToolsClose">
								<span class="icon-fallback-text">
									<span class="icon-times" aria-hidden="true"></span>
									<span class="icon-text">close registry tools</span>
								</span>
							</a>
							<bbbt:textArea key="txt_registry_tools" language ="${pageContext.request.locale.language}"/>
							<%--
							<ul id="regToolsList">
								<li class="header">registry tools</li>
								<li><a title="Guides &amp; Advice" href="/store/registry/GuidesAndAdviceLandingPage">Guides &amp; Advice</a></li>										
								<li><a href="/store/bbregistry/BridalBook" title="Wedding Registry Book">Wedding Registry Book</a></li>
								<li><a href="/store/printCards/printCards.jsp" title="Registry Announcement Cards">Announcement Cards</a></li>
								<li><a title="Personalized Invitations" href="/store/registry/PersonalizedInvitations">Personalized Invitations</a></li>
								<li class="separator"></li>
								<li><a title="Registry Features" href="/store/registry/RegistryFeatures">Registry Features</a></li>
								<li><a title="Registry Incentives" href="/store/registry/RegistryIncentives">Registry Incentives</a></li>
							</ul>
							--%>
						</div>							
					</li>

					
					<%--
					<li id="announcementCards" class="last">
	                	<c:set var="announcementCards"><bbbl:label key='lbl_registry_header_announcement_cards' language="${pageContext.request.locale.language}" /></c:set>
	                	<c:set var="printAtHomeLink">
								<bbbl:label key="bbb_printathome_url" language="${pageContext.request.locale.language}" />
						</c:set>                            	
						<a href="${contextPath}/${printAtHomeLink}?registryId=${registryId}" class="" title="${announcementCards}" />
							<span class="icon-fallback-text"><span class="icon-file-text-o" aria-hidden="true"></span><span class="icon-text">a</span></span>${announcementCards}
						</a>								
					</li>
					--%>
	            </ul>
	        </div>
		</div>

		<%-- gift completion messege, except for baby, Housewarming and Anniversary  --%>
		<c:if test="${not empty eventType && eventType != 'Baby' && !fn:contains(eventType, 'University')}">

			<div id="regCompletionMsg" class="grid_12">			
				<c:choose>
	                <c:when test="${regPublic && guestCount > 0}">

						<c:set var="factor" >
							<bbbl:label key="lbl_registry_completion_msg_factor" language="${pageContext.request.locale.language}" />
						</c:set>             	
	                	
						<dsp:getvalueof var="giftRegistered" param="registrySummaryVO.giftRegistered"/>					
						<dsp:getvalueof var="totalGiftsNeeded" value="${guestCount*factor}"/>
						<dsp:getvalueof var="giftsPercentageAdded" value="${giftRegistered/totalGiftsNeeded}"/>
						<c:set var="giftsPercentageFormatted" >
							<fmt:formatNumber type="number" maxFractionDigits="0" value="${giftsPercentageAdded*100}" />%
						</c:set>


						<%-- debugging 
						giftsPercentageAdded: ${giftsPercentageAdded}<br />
						factor: ${factor}<br />
						totalGiftsNeeded: ${totalGiftsNeeded}<br />
						giftsPercentageFormatted: ${giftsPercentageFormatted}<br />
						--%>
						<c:if test="${giftsPercentageAdded > 1}">
							<c:set var="giftsPercentageFormatted">100%</c:set>
						</c:if>

						<c:set var="completionMsg" >
							<bbbl:label key="lbl_registry_completion_msg_public" language="${pageContext.request.locale.language}" />
						</c:set>

						<c:set var="completionMsg" value="${fn:replace(completionMsg, '$guestCount', guestCount)}" />
						<c:set var="completionMsg" value="${fn:replace(completionMsg, '$percentage', giftsPercentageFormatted)}" />

	                    ${completionMsg}
	            	</c:when>
	            	<c:when test="${regPublic && (guestCount == 0 || empty guestCount)}">
	            		<p><bbbl:label key="lbl_registry_completion_msg_no_guests" language="${pageContext.request.locale.language}" /></p>
	            	</c:when>
	        		<c:otherwise>
	        			
	                    <p><bbbl:label key="lbl_registry_completion_msg_private" language="${pageContext.request.locale.language}" /></p>
	                    	
	        		</c:otherwise>
	        	</c:choose>
			</div>
		</c:if>

		<c:set var="showPromoSlots"><bbbc:config key="registry_owner_view_show_top_promo_slots" configName="FlagDrivenFunctions" /></c:set>
				
		<c:if test="${showPromoSlots == 'true'}">
			<div style="margin-top: 20px;" class="grid_12 alpha omega clearfix noprint">
				<dsp:include page="/giftregistry/frags/registry_promo_boxes.jsp">
					<dsp:param name="pageName" value="registryView" />
					<dsp:param name="promoSpot" value="Top" />
					<dsp:param name="registryType" param="registrySummaryVO.eventType" />
					<dsp:param name="siteId" value="${appid}" />
					<dsp:param name="channel" value="Desktop Web" />
				</dsp:include>
			</div>
		</c:if>
		
		



	<c:set var="maxBulkSize" scope="request">
		<bbbc:config key="MaxSizeRegistryItems" configName="ContentCatalogKeys" />
	</c:set>
	<dsp:getvalueof var="eventTypeCode" param="registrySummaryVO.registryType.registryTypeName"/>

	<input type="hidden" name="eventTypeCode" value="${eventTypeCode}"/>
    <!-- BPS-2650  Added extra parameter which will be used while making an AJAX call-->
    <input type="hidden" name="regEventDate" value="${regEventDate}"/>
    <input type="hidden" name="regFirstName" value="${regFirstName}"/>
    <c:set var="regAddress">
           <dsp:valueof value="${registryVO.shipping.shippingAddress.addressLine1}"  />
           <dsp:valueof value="${registryVO.shipping.shippingAddress.addressLine2}" />
    </c:set>


	<div class="registryTabs alpha omega grid_12 clearfix">
	  <div id="categoryTabs">
        <input type="hidden" id="reloadMyItemsTab" value="false"/>
		<ul class="noprint" role="tablist">
			<c:set var="browseGiftsLbl" >
				<bbbl:label key="lbl_registry_browse_add_gifts_tab" language="${pageContext.request.locale.language}" />
			</c:set>
			<li id="browseGiftsTab" aria-labelledby="browseGiftsLbl" role="tab" aria-selected="false" aria-controls="browseGifts"><a href="#browseGifts" aria-label="${browseGiftsLbl}" class="akickStartersTab" id="browseGiftsLink">${browseGiftsLbl}</a></li>
			<span class="visuallyhidden" id="browseGiftsLbl">${browseGiftsLbl}</span>	
			
			<li id="myItemsTab" aria-labelledby="myItemsMsg" role="tab" aria-selected="true" aria-controls="myItems"><a href="#myItems" class="amyItemsTab" id="myItemsLink" onclick="javascript:typeof s !== 'undefined' && omniRegistryTabs('My Items');" data-omniparam='<bbbl:label key="LBL_RegistryMyItems" language="${pageContext.request.locale.language}" />'><bbbl:label key="lbl_registry_owner_myitems" language="${pageContext.request.locale.language}" /></a></li>
			<span class="visuallyhidden" id="myItemsMsg">My Items Tab</span>
			
			<c:if test="${inviteFriend eq 'false' && (!regPublic || (regPublic && showRecommendationTab eq 'true'))}">
				<li id="recommendationsTab" role="tab" aria-labelledby="recommendationsMsg" aria-controls="recommendations">
					<a href="#recommendations" id="recommendationTab" data-omniparam='<bbbl:label key="LBL_Browse_AddGifts_Reccomendations" language="${pageContext.request.locale.language}" />'>
						<bbbl:label key="lbl_registry_owner_recommendations" language="${pageContext.request.locale.language}" />
						<c:if test="${recommendationCount gt 0}">
							<span class="recommendationCount">${recommendationCount}</span>
						</c:if>
					</a>
				</li>
				<span class="visuallyhidden" id="recommendationsMsg"><bbbl:label key="lbl_registry_owner_recommendations" language="${pageContext.request.locale.language}" /> Tab</span>
			</c:if>
			
			<c:set var="lbl_registry_owner_checklist_tab" >
				<bbbl:label key="lbl_registry_owner_checklist_tab" language="${pageContext.request.locale.language}" />
			</c:set>
			<li id="checklistTab" aria-labelledby="checklistMsg" role="tab" aria-selected="true" aria-controls="myItems">
				<a 	href="#checklist" 
					data-registryid="${registryId}" 
					data-eventTypeCode="${eventTypeCode}" 
					data-eventtype="${eventTypeVar}" 
					class="amyItemsTab" 
					id="checklistLink" 
					onclick="javascript:typeof s !== 'undefined' && omniRegistryTabs('Checklist');">${lbl_registry_owner_checklist_tab}</a>
			</li>
			<span class="visuallyhidden" id="checklistMsg">${lbl_registry_owner_checklist_tab}</span>
		</ul>
		</div>
		<div id="browseGifts" role="tabpanel" aria-labelledby="browseAddGiftsTabs">

			<div id="browseAddGiftsTabs">
				<ul class="noprint" role="tablist">
					<c:set var="kickstartersLbl" >
						<bbbl:label key="lbl_registry_owner_kickstarters" language="${pageContext.request.locale.language}" />
					</c:set>
					<li id="kickStartersTab" aria-labelledby="kickStrtMsg" role="tab" aria-selected="true" aria-controls="kickstarters">
						<a href="#kickstarters" class="akickStartersTab" id="kickStartersLink" data-omniparam='<bbbl:label key="LBL_Browse_AddGifts_Kickstarters" language="${pageContext.request.locale.language}" />'>${kickstartersLbl}</a>
					</li>
					<span class="visuallyhidden" id="kickStrtMsg">${kickstartersLbl}</span>	
					
					<c:set var="lbl_shopthelook" >
						<bbbl:label key="lbl_shopthelook" language="${pageContext.request.locale.language}" />
					</c:set>
					<li id="shopThisLookTab" aria-labelledby="shopThisLookMsg" role="tab" aria-selected="false" aria-controls="shopThisLook">
							<a 	href="#shopThisLook" 
								data-registryid="${registryId}" 
								data-eventTypeCode="${eventTypeCode}" 
								data-eventtype="${eventTypeVar}"
								class="ashopThisLookTab" 
								id="shopThisLookLink" 
								onclick="javascript:typeof s !== 'undefined' && omniRegistryTabs('Browse & Add Gifts: shop this look');">${lbl_shopthelook}</a>
					</li>
					<span class="visuallyhidden" id="shopThisLookMsg">${lbl_shopthelook}</span>

					<c:set var="lbl_registry_owner_registry_favorites" >
						<bbbl:label key="lbl_registry_owner_registry_favorites" language="${pageContext.request.locale.language}" />
					</c:set>
					
					<li id="registryFavoritesTab" aria-labelledby="registryFavoritesMsg" role="tab" aria-selected="false" aria-controls="registryFavorites">
						<%--
						<c:choose>
							<c:when test="${(currentSiteId eq BedBathUSSite)}">
						    	<c:set var="favoriteUrl" value="/store/category/registry-favorites/12952" scope="request" />
					    	</c:when>
					   		<c:when test="${(currentSiteId eq BuyBuyBabySite)}">
					   			<c:set var="favoriteUrl" value="/store/category/baby-registry-favorites/32689/" scope="request" />
					   		</c:when>
					   		<c:when test="${(currentSiteId eq BedBathCanadaSite)}">
						    
					   		</c:when>
					   	</c:choose>
						--%>
						<a 	href="#registryFavorites" 
							data-registryid="${registryId}" 
							data-eventTypeCode="${eventTypeCode}" 
							data-eventtype="${eventTypeVar}"
							id="registryFavoritesLink" 
							onclick="javascript:typeof s !== 'undefined' && omniRegistryTabs('Browse & Add Gifts: registry  favorites');">${lbl_registry_owner_registry_favorites}</a>
					</li>
					<span class="visuallyhidden" id="registryFavoritesMsg">${lbl_registry_owner_registry_favorites}</span>

					
					<c:set var="lblGiftIdeas" >
						<bbbl:label key="lbl_registry_owner_gift_ideas" language="${pageContext.request.locale.language}" />
					</c:set>
					<li id="giftIdeasTab" aria-labelledby="giftIdeasMsg" role="tab" aria-selected="true" aria-controls="giftIdeas">
						<a href="#giftIdeas" data-registryid="${registryId}" data-eventTypeCode="${eventTypeCode}" 
							data-eventtype="${eventTypeVar}"
							data-sortseq="${sortSeq}"
							data-view="${view}"
							class="agiftIdeasTab" 
							id="giftIdeasLink" onclick="javascript:typeof s !== 'undefined' && omniRegistryTabs(' Browse & Add Gifts: gift ideas');">${lblGiftIdeas}</a>
					</li>
					<span class="visuallyhidden" id="giftIdeasMsg">${lblGiftIdeas}</span>
				</ul>

				<div id="kickstarters">
					<dsp:include page="kickstarters/top_consultants_grid.jsp?eventType=${eventType}" flush="true" ></dsp:include>

					<div  class="grid_12 kickstarterSection">
						<dsp:include page="/giftregistry/frags/registry_promo_boxes.jsp">
							<dsp:param name="pageName" value="kickStartersView" />
							<dsp:param name="promoSpot" value="Bottom" />
							<dsp:param name="registryType" param="registrySummaryVO.eventType" />
							<dsp:param name="siteId" value="${appid}" />
							<dsp:param name="channel" value="Desktop Web" />
						</dsp:include>
					</div>

					<%-- START COPY REGISTRY--%>
					<div id="copyRegistrySection" class="kickstarterSection grid_12">
						<div class="kickstarterSectionHeader grid_12">
							<div class="grid_5 alpha ">
						    	<h2><bbbt:textArea key="txt_copy_registry_header" language ="${pageContext.request.locale.language}"/></h2>
					        </div>
					        <div class="grid_7 omega">
						           	<p><bbbt:textArea key="txt_copy_registry_description" language ="${pageContext.request.locale.language}"/></p>
					        </div>
						</div>
		                <div id="registryFilterFormWrap" class="clearfix grid_12 alpha omega">
		                	<dsp:include page="/giftregistry/registry_search_filter_form.jsp">
		                        <dsp:param name="showOnlySearchFields" value="true"  />
		                        <dsp:param name="formId" value="2"  />
		                    </dsp:include>
		                </div>
		            </div>
		            <%-- ENDCOPY REGISTRY--%>

		            <dsp:include page="kickstarters/popular_items_grid.jsp?eventType=${eventType}" flush="true" >
						<dsp:param name="omniDesc" value="Popular items (viewKickStarters)" />
					</dsp:include>
				</div>
				<div id="shopThisLook">				
					<div class="ajaxLoadWrapper">
						<bbbt:textArea key="txt_viewregistry_imageloader" language ="${pageContext.request.locale.language}"/>
				</div>
					<%--
						<dsp:include page="kickstarters/shop_look_grid.jsp?eventType=${eventType}" flush="true" ></dsp:include> --%>
				</div>
	            <div id="registryFavorites">	            	
	            	<div class="ajaxLoadWrapper">
						<bbbt:textArea key="txt_viewregistry_imageloader" language ="${pageContext.request.locale.language}"/>
					</div>  
	            	<%--  	
	            	<c:set var="registryFavoritesContent">
						<c:choose>
							<c:when test="${not empty eventType && eventType eq 'Wedding'}">
								<bbbt:textArea key="txt_registry_favorites_tab_content_wedding" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'Baby'}">
								<bbbt:textArea key="txt_registry_favorites_tab_content_baby" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'Birthday'}">
								<bbbt:textArea key="txt_registry_favorites_tab_content_birthday" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'Retirement'}">
								<bbbt:textArea key="txt_registry_favorites_tab_content_retirement" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'Anniversary'}">
								<bbbt:textArea key="txt_registry_favorites_tab_content_anniversary" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'Housewarming'}">
								<bbbt:textArea key="txt_registry_favorites_tab_content_housewarming" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'Commitment Ceremony'}">
								<bbbt:textArea key="txt_registry_favorites_tab_content_commitment" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${not empty eventType && fn:contains(eventType, 'University')}">
								<bbbt:textArea key="txt_registry_favorites_tab_content_college" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:when test="${not empty eventType && eventType eq 'Other'}">
								<bbbt:textArea key="txt_registry_favorites_tab_content_other" language ="${pageContext.request.locale.language}"/>
							</c:when>
							<c:otherwise>
								<bbbt:textArea key="txt_registry_favorites_tab_content_wedding" language ="${pageContext.request.locale.language}"/>
							</c:otherwise>
						</c:choose>
					</c:set>		
					${registryFavoritesContent}	
					--%>
				</div>
				<div id="giftIdeas">
					
					<div class="ajaxLoadWrapper">
						<bbbt:textArea key="txt_viewregistry_imageloader" language ="${pageContext.request.locale.language}"/>
					</div>

					<%--
					<dsp:include page="kickstarters/gift_ideas.jsp?eventType=${eventType}" flush="true" >
						<dsp:param name="omniDesc" value="Popular items (viewKickStarters)" />
					</dsp:include>
					--%>


					<%--
					<dsp:include page="kickstarters/popular_items_grid.jsp?eventType=${eventType}" flush="true" >
						<dsp:param name="omniDesc" value="Popular items (viewKickStarters)" />
					</dsp:include>
					<div >
						<dsp:include page="/giftregistry/kickstarters/top_registry_items.jsp" flush="true" >
							<dsp:param name="registryId" value="${fn:escapeXml(param.registryId)}"/>
							<dsp:param name="sorting" value="${param.sortSeq}"/>
							<dsp:param name="view" value="${param.view}"/>
							<dsp:param name="skuList" value="${skuList}"/>
							<dsp:param name="certonaSkuList" value="${certonaSkuList}"/>
						</dsp:include>					            
					</div>
					--%>
				</div>
			</div>
		</div>
		<div id="myItems" role="tabpanel" aria-labelledby="myItemsTab">
			<div id="regAjaxLoad" class="grid_12 alpha omega" data-currentView="owner"  data-regEventDate="${regEventDate}" data-regAddress="${regAddress}" data-registryId="${registryId}" data-startIdx="0" data-isGiftGiver="false" data-blkSize="${maxBulkSize}" data-isAvailForWebPurchaseFlag="true"  data-sortSeq="${sortSeq}" data-eventTypeCode="${eventTypeCode}" data-eventType="${eventTypeVar}" data-pwsurl="${pwsurl}" data-view="${view}" data-isChecklistDisabled="${isChecklistDisabled}" >
				<div class="ajaxLoadWrapper">
					<bbbt:textArea key="txt_viewregistry_imageloader" language ="${pageContext.request.locale.language}"/>
				</div>
			</div>
			<c:if test="${WarrantyOn}">
				<div class="moreInfoWarrColor clearfix grid_12 alpha omega">
	           	    <bbbt:textArea key="txt_warranty_link_registry" language ="${pageContext.request.locale.language}"/>
	            </div>
	       	</c:if>
	       	
		</div>
		
		<!-- Recommendation Tab Surge BPS-793  -->
		<input type="hidden" name="registryType" value="${eventTypeVar}"/>		
		<c:if test="${inviteFriend eq 'false' && (!regPublic || (regPublic && showRecommendationTab eq 'true'))}">
	   		<div id="recommendations" role="tabpanel" aria-labelledby="recommendationsTab">
	   			<div class="ajaxLoadWrapper">
						<bbbt:textArea key="txt_viewregistry_imageloader" language ="${pageContext.request.locale.language}"/>
				</div>
			</div>
		</c:if>
		<!-- Recommendation Tab Surge BPS-793  -->
		
		<div id="checklist">
			
			<div class="ajaxLoadWrapper">
				<bbbt:textArea key="txt_viewregistry_imageloader" language ="${pageContext.request.locale.language}"/>
			</div>

			<%--
			<dsp:include page="/giftregistry/frags/registry_promo_boxes.jsp">
				<dsp:param name="pageName" value="registryCheckList" />
				<dsp:param name="promoSpot" value="Top" />
				<dsp:param name="registryType" param="registrySummaryVO.eventType" />
				<dsp:param name="siteId" value="${appid}" />
				<dsp:param name="channel" value="Desktop Web" />
			</dsp:include>
			--%>
		</div>

	</div>


<%--including the Top Registry Items Page--%>


<%--
<dsp:input bean="GiftRegistryFormHandler.successURL" type="hidden" value="view_registry_owner.jsp" />
<dsp:input bean="GiftRegistryFormHandler.errorURL"	type="hidden" value="view_registry_owner.jsp" />
--%>
</div>
</div> <%-- END <div id="content" --%>

<dsp:include page="simpleReg_update_form.jsp" flush="true">
	<dsp:param name="registryId" value="${registryId}" />
	<dsp:param name="eventTypeForUpdate" value="${eventTypeForUpdate}" />
	<dsp:param name="appid" value="${appid}" />
	<dsp:param name="isTransient" value="${isTransient}" />
	<dsp:param name="cssPath" value="${cssPath}" />
	<dsp:param name="registryEventType" value="${eventTypeCode}" />
	<dsp:param name="regPublic" value="${regPublic}" />
	<dsp:param name="coRegOwner" value="${coRegOwner}" />
</dsp:include>

<%-- no longer required.
<div id="showPrivateRegistryModal" class="hidden">
	<h2>Your Registry is currently private</h2>
	<p>Complete and save your registry so guests can view and purchase gifts.</p>
	<a href="#" class="editRegInfo button-Med btnPrimary close">Complete Profile</a>
	<a href="#" class="close">Remind Me Later</a>
</div>
--%>

<c:if test="${fn:contains(requestURIWithQueryString,'hoorayModal')}">
	<dsp:include page="/giftregistry/frags/registry_marketing_pixel.jsp"></dsp:include>
</c:if>

<c:if test="${hoorayModal eq 'true' && lbl_registry_show_hooray_modal eq 'yes'}">

	<div id="hoorayModal" class="hidden">
		
		<c:set var="hooray_modal_content">
			<c:choose>
				<c:when test="${not empty eventType && eventType eq 'Wedding'}">
					<bbbt:textArea key="txt_registry_hooray_modal_wedding" language ="${pageContext.request.locale.language}"/>
				</c:when>
				<c:when test="${not empty eventType && eventType eq 'Baby'}">
					<bbbt:textArea key="txt_registry_hooray_modal_baby" language ="${pageContext.request.locale.language}"/>
				</c:when>
				<c:when test="${not empty eventType && eventType eq 'Birthday'}">
					<bbbt:textArea key="txt_registry_hooray_modal_birthday" language ="${pageContext.request.locale.language}"/>
				</c:when>
				<c:when test="${not empty eventType && eventType eq 'Retirement'}">
					<bbbt:textArea key="txt_registry_hooray_modal_retirement" language ="${pageContext.request.locale.language}"/>
				</c:when>
				<c:when test="${not empty eventType && eventType eq 'Anniversary'}">
					<bbbt:textArea key="txt_registry_hooray_modal_anniversary" language ="${pageContext.request.locale.language}"/>
				</c:when>
				<c:when test="${not empty eventType && eventType eq 'Housewarming'}">
					<bbbt:textArea key="txt_registry_hooray_modal_housewarming" language ="${pageContext.request.locale.language}"/>
				</c:when>
				<c:when test="${not empty eventType && eventType eq 'Commitment Ceremony'}">
					<bbbt:textArea key="txt_registry_hooray_modal_commitment" language ="${pageContext.request.locale.language}"/>
				</c:when>
				<c:when test="${not empty eventType && fn:contains(eventType, 'University')}">
					<bbbt:textArea key="txt_registry_hooray_modal_college" language ="${pageContext.request.locale.language}"/>
				</c:when>
				<c:when test="${not empty eventType && eventType eq 'Other'}">
					<bbbt:textArea key="txt_registry_hooray_modal_other" language ="${pageContext.request.locale.language}"/>
				</c:when>
				<c:otherwise>
					<bbbt:textArea key="txt_registry_hooray_modal_wedding" language ="${pageContext.request.locale.language}"/>
				</c:otherwise>
			</c:choose>
		</c:set>

		<%-- Need to replace the variables with values for the following: 
		eventType
		skedgeURL
		storeId
		email
		regFN
		regLN
		coregFN
		coregLN
		contactNum
		registryId
		site
		eventDate
		$hideScheduler --%>		    
		

		<c:set var="skedgeURL">
			<bbbc:config key="skedgeURL" configName="ThirdPartyURLs" />
		</c:set>

		<c:set var="$hideScheduler" value="" />
		<c:if test="${empty favouriteStoreId}">
			<c:set var="$hideScheduler" value="hidden" />
		</c:if>

		
		<c:set var="hooray_modal_content" value="${fn:replace(hooray_modal_content, '$eventType', eventType)}" />
		<c:set var="hooray_modal_content" value="${fn:replace(hooray_modal_content, '$skedgeURL', skedgeURL)}" />
		<c:set var="hooray_modal_content" value="${fn:replace(hooray_modal_content, '$storeId', favouriteStoreId)}" />
		<c:set var="hooray_modal_content" value="${fn:replace(hooray_modal_content, '$email',registrySummaryVO.primaryRegistrantEmail)}" />
		<c:set var="hooray_modal_content" value="${fn:replace(hooray_modal_content, '$regFN',registrySummaryVO.primaryRegistrantFirstName)}" />
		<c:set var="hooray_modal_content" value="${fn:replace(hooray_modal_content, '$regLN', registrySummaryVO.primaryRegistrantLastName)}" />
		<c:set var="hooray_modal_content" value="${fn:replace(hooray_modal_content, '$coregFN',registrySummaryVO.coRegistrantFirstName)}" />
		<c:set var="hooray_modal_content" value="${fn:replace(hooray_modal_content, '$coregLN',registrySummaryVO.coRegistrantLastName)}" />
		<c:set var="hooray_modal_content" value="${fn:replace(hooray_modal_content, '$contactNum',registrySummaryVO.primaryRegistrantPrimaryPhoneNum)}" />
		<c:set var="hooray_modal_content" value="${fn:replace(hooray_modal_content, '$registryId',registrySummaryVO.registryId)}" />
		<c:set var="hooray_modal_content" value="${fn:replace(hooray_modal_content, '$siteId',currentSiteId)}" />
		<c:set var="hooray_modal_content" value="${fn:replace(hooray_modal_content, '$eventDate',registrySummaryVO.eventDate)}" />
		<c:set var="hooray_modal_content" value="${fn:replace(hooray_modal_content, '$hideScheduler',hideScheduler)}" />

		${hooray_modal_content}

	</div>
</c:if>




		</c:when>
		<c:otherwise>
			<div id="content" class="container_12 clearfix" role="main">
			<div class="grid_12 clearfix marTop_20">
				<h3><span class="error">
				Access Denied!!!
				</span></h3>
			</div>
			</div>
		</c:otherwise>
		</c:choose>
</jsp:body>
<jsp:attribute name="footerContent">
<c:set var ="operation" scope="request">
	<dsp:valueof bean="GiftRegSessionBean.registryOperation"/>
	</c:set>

	<input type="hidden" id="operationType" value="${operation}" />
	<input type="hidden" id="eventType" value="${event}" />
	<input type="hidden" id="sEvar23Val" value="${fn:escapeXml(eventType)}" />
	<input type="hidden" id="sEvar24Val" value="${fn:escapeXml(registryId)}" />
        <script type="text/javascript"> 
        	var operation ='${operation}';
            if(typeof s !=='undefined') { 
                s.pageName = 'Registry View Page';
                s.channel = 'Registry';
                s.prop1 = 'Registry';
                s.prop2 = 'Registry';
                s.prop3 = 'Registry';
                s.eVar23 = '${fn:escapeXml(eventType)}';
                s.eVar24 = '${fn:escapeXml(registryId)}';
                if (operation == 'updated') {
                    s.pageName= "Registry Confirmation Page";
                    s.events="event20";
                }
                else{
                    s.events="${event}";
                }
            }
        </script>

		<!--<c:if test="${fn:contains(pageContext.request.queryString, 'hoorayModal')}">
		<script type="text/javascript">
            if(typeof s !=='undefined') {
				s.linkTrackVars='prop1,prop2,prop3,prop18,prop28,eVar23,eVar24';
				s.linkTrackEvents="${omniValuesForCreateAccAndReg}";
				s.pageName = 'Registry Confirm Modal';
                s.channel = 'Registry';
				s.prop1 = 'Registry';
                s.prop2 = 'Registry';
                s.prop3 = 'Registry';
				s.eVar23 = '${fn:escapeXml(eventType)}';
                s.eVar24 = '${fn:escapeXml(registryId)}';
				s.events= '${omniValuesForCreateAccAndReg}';
				s.tl(true,'o', 'Registry Created');
				s.linkTrackVars='None';
				s.linkTrackEvents='None';
            }
        </script>
		</c:if>-->

</jsp:attribute>
	</bbb:pageContainer>
		</dsp:oparam>
		<dsp:oparam name="error">
			<dsp:getvalueof param="errorMsg"  var="errorMsg"/>
			<div class="container_12 clearfix">
				<div class="grid_12">
                	<div class="error marTop_20"><bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}"/></div>
                </div>
            </div>
		</dsp:oparam>
	</dsp:droplet>

</c:if>

<script type="text/javascript">

	$(document).ready(function() {
		checkForFieldsValidation('${regPublic}');			  	
	});
</script>


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
			 <c:choose>
			 <c:when test="${(currentSiteId eq BedBathUSSite)}">
		    		   <c:set var="cat"><bbbc:config key="cat_registry_bedBathUS" configName="RKGKeys" /></c:set>
		    		   <c:set var="src"><bbbc:config key="src_bedBathUS" configName="RKGKeys" /></c:set>
		    		   <c:set var="type"><bbbc:config key="type_1_bedBathUS" configName="RKGKeys" /></c:set>
	    		 	</c:when>
	   		 <c:when test="${(currentSiteId eq BuyBuyBabySite)}">
	   		   <c:set var="cat"><bbbc:config key="cat_registry_baby" configName="RKGKeys" /></c:set>
	   		   <c:set var="src"><bbbc:config key="src_baby" configName="RKGKeys" /></c:set>
	   		   <c:set var="type"><bbbc:config key="type_1_baby" configName="RKGKeys" /></c:set>
	   		 </c:when>
	   		  <c:when test="${(currentSiteId eq BedBathCanadaSite)}">
		    		   <c:set var="cat"><bbbc:config key="cat_registry_bedbathcanada" configName="RKGKeys" /></c:set>
		    		   <c:set var="src"><bbbc:config key="src_bedbathcanada" configName="RKGKeys" /></c:set>
		    		   <c:set var="type"><bbbc:config key="type_1_bedbathcanada" configName="RKGKeys" /></c:set>
	   		 </c:when>
	   		 </c:choose>
	 		<dsp:include page="/_includes/double_click_tag.jsp">
	 			<dsp:param name="doubleClickParam" 
	 			value="src=${src};type=${type};cat=${cat};u10=null;u11=null"/>
	 		</dsp:include>
 		</c:if>
		DoubleClick Floodlight END --%>


<script>
$("li[role='tab']").click(function(){
	$("li[role='tab']").attr("aria-selected","false"); 
 	$(this).attr("aria-selected","true");
})
$(".amyItemsTab").click(function(){
	$("li[role='tab']").attr("aria-selected","false"); 
 	$(this).parent().attr("aria-selected","true");
});
$(".akickStartersTab").click(function(){
	$("li[role='tab']").attr("aria-selected","false"); 
 	$(this).parent().attr("aria-selected","true");
});
</script>
</dsp:page>
