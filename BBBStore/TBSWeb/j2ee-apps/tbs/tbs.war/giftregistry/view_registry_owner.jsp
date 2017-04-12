<dsp:page>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<dsp:importbean
	bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/RegistryInfoDisplayDroplet" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/DateCalculationDroplet" />
	<dsp:importbean bean="/com/bbb/email/EmailHolder"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/RedirectDroplet"/>
	<dsp:importbean bean="/com/bbb/framework/security/ValidateParametersDroplet" />

	<dsp:getvalueof var="scheme" bean="/OriginatingRequest.scheme"/>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <dsp:getvalueof var="serverName" bean="/OriginatingRequest.serverName"/>
    <dsp:getvalueof var="serverPort" bean="/OriginatingRequest.serverPort"/>
    <dsp:getvalueof var="registryId" param="registryId"/>
    <dsp:getvalueof var="eventType" param="eventType"/>
  	<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	
	<%-- for defect PS-17524 :start--%>
	
	<dsp:getvalueof var="cItemId" bean="GiftRegistryFormHandler.productId"/>
	<dsp:getvalueof var="cItemQty" bean="GiftRegistryFormHandler.quantity"/>
	<dsp:getvalueof var="cItemPrice" bean="GiftRegistryFormHandler.addedItemPrice"/>
	<dsp:getvalueof var="isItemAdded" bean="GiftRegistryFormHandler.itemAddedToRegistry"/>
	<dsp:getvalueof var="cItemSkuId" bean="GiftRegistryFormHandler.skuIds"/>
	
	

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
        <c:when test="${not empty appid && appid eq 'TBS_BuyBuyBaby'}">
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

	<bbb:pageContainer>
		<jsp:attribute name="section">registry</jsp:attribute>
		<jsp:attribute name="pageVariation">${pageVariation}</jsp:attribute>
		<jsp:attribute name="pageWrapper">giftView ownerView useFB useCertonaJs useBazaarVoice</jsp:attribute>
        <jsp:attribute name="bodyClass">registry</jsp:attribute>
	<jsp:body>

<dsp:getvalueof var="isTransient" bean="Profile.transient"/>
<dsp:getvalueof var="sortSeq" param="sorting"/>
<dsp:getvalueof var="view" param="view"/>
	
	<input type="hidden" name="cProd" value="${cItemId}" />
	<input type="hidden" name="cQty" value="${cItemQty}" />
	<input type="hidden" name="cPrice" value="${cItemPrice}" />
	<input type="hidden" name="cAddItemFlag" value="${isItemAdded}" />
	<input type="hidden" name="cSkuId" value="${cItemSkuId}" />

 <c:if test="${( not empty registryId ) && !isTransient }">
<dsp:droplet name="RegistryInfoDisplayDroplet">
		<dsp:param value="${registryId}" name="registryId" />
		<dsp:param name="displayView" value="owner" />
		<dsp:oparam name="output">

    <dsp:getvalueof var="eventTypeParam" param="eventType"/>
    <dsp:getvalueof var="eventTypeVar" param="registrySummaryVO.eventType"/>
    <dsp:getvalueof var="eventTypeCode" param="registrySummaryVO.registryType.registryTypeName"/>
    
    
    <%-- SET UP VALUES FOR SKEDGE ME IFRAME/LINK --%>
    <dsp:getvalueof var="favouriteStoreId" param="registrySummaryVO.favStoreId"/>
	<c:if test="${not empty favouriteStoreId}">
			<dsp:droplet name="/com/bbb/selfservice/TBSNearbyStoreSearchDroplet">
                 <dsp:param name="storeId" value="${favouriteStoreId}"/>
                 <dsp:param name="siteId" value="${appid}" />
                  <dsp:param name="pageFrom" value="registryConfirmation" />
                 <dsp:param name="searchType" value="2"/>
                 <dsp:oparam name="output">
									 <dsp:getvalueof var="StoreDetails" param="nearbyStores"/>
                </dsp:oparam>
        	</dsp:droplet>
	  </c:if>
	
	<dsp:getvalueof var="registryVOskg" param="registrySummaryVO"/>
    <c:set var="allowedToScheduleAStoreAppointment" value="${registryVOskg.allowedToScheduleAStoreAppointment}"/>        
    <c:set var="showSkedgeButton" value="false"/>
    
  	<c:if test="${registryVOskg.allowedToScheduleAStoreAppointment}">
  		<c:choose>
			<c:when test="${eventTypeCode eq 'COM' or eventTypeCode eq 'BRD' }">	
				<c:set var="skedgeURL">
					<bbbc:config key="skedgeURL" configName="ThirdPartyURLs" />
				</c:set>
				<dsp:getvalueof var="registryVOskg" param="registrySummaryVO"/>
				<c:if test="${not empty skedgeURL}">
					<c:set var="showSkedgeButton" value="true"/>					
				</c:if>        
			</c:when>
		</c:choose>
	</c:if>	
	
    
	<c:choose>
	<c:when test="${eventTypeVar eq  eventTypeParam}">
    <div id="content" role="main">
		<%-- Droplet for showing error messages --%>
		<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
			<dsp:param param="GiftRegistryFormHandler.formExceptions" name="exceptions"/>
			<dsp:oparam name="output">
                <div class="error marTop_20"><dsp:valueof param="message"/></div>
			</dsp:oparam>
		</dsp:droplet>



		<dsp:getvalueof var="eventDateStr" param="eventDate"/>
		<dsp:getvalueof var="giftRegistered" param="registrySummaryVO.giftRegistered"/>
		<dsp:getvalueof var="giftPurchased" param="registrySummaryVO.giftPurchased"/>
		<dsp:getvalueof var="eventTypeCode" param="registrySummaryVO.registryType.registryTypeName"/>

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


        <div class="row">
            <h2 class="registryType small-12 columns"><dsp:valueof param="registrySummaryVO.eventType"/> <bbbl:label key="lbl_registry_header_registry" language="${pageContext.request.locale.language}" /></h2>
            <div class="small-12 columns">
                <div class="left small-12">
                    <h3 class="registrantNames small-12 medium-4 columns no-padding print-4">
                        <dsp:droplet name="IsEmpty">
                            <dsp:param param="registrySummaryVO.coRegistrantFirstName" name="value"/>
                            <dsp:oparam name="false">
                                <dsp:valueof param="registrySummaryVO.primaryRegistrantFirstName"/>
                                &amp;
                                <dsp:valueof param="registrySummaryVO.coRegistrantFirstName"/>
                            </dsp:oparam>
                            <dsp:oparam name="true">
                                <dsp:valueof param="registrySummaryVO.primaryRegistrantFirstName"/>
                            </dsp:oparam>
                        </dsp:droplet>
                    </h3>
                    <div class="small-7 medium-4 columns no-padding print-4"><h3><bbbl:label key="lbl_regsrchguest_Registry" language="${pageContext.request.locale.language}" />:&nbsp;${registryId}</h3></div>
                    <div id="myProfile" class="small-5 medium-4 columns no-padding print-4">
                         <a href="${contextPath}/giftregistry/registry_creation_form.jsp?registryId=${registryId}" 
                              class="tiny button column primary" title="<bbbl:label key="lbl_registry_header_my_profile" 
                              language="${pageContext.request.locale.language}" />"><bbbl:label key="lbl_registry_header_my_profile" language="${pageContext.request.locale.language}" />
                         </a>
                     </div>
                </div>
                <div class="row">
                           
                           <div id="print" class="print-email" style="text-align:right;">
                                <c:set var="btnPrintTitle"><bbbl:label key='lbl_mng_regitem_printreg' language="${pageContext.request.locale.language}" /></c:set>
                                <a href="#" class="btnPrint pdp-sprite print print-trigger" title="${btnPrintTitle}"><%-- <bbbl:label key="lbl_registry_header_print" language="${pageContext.request.locale.language}" /> --%>
                                </a>|<a href="#" class="pdp-sprite email btnEmail"><%-- <bbbl:label key="lbl_registry_header_share" language="${pageContext.request.locale.language}" /> --%></a>

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
                                <input type="hidden" name="senderEmail" value="${senderEmail}" class="emailAFriendFields"/>
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
                                </div>


                            </dsp:form>

                            <div id="regHeaderShareLinks" class="reveal-modal tiny" data-reveal>
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
                                    <c:when test="${currentSiteId == TBS_BedBathCanadaSite || currentSiteId == TBS_BedBathUSSite}">
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

                                <a href="http://www.facebook.com/sharer.php?s=100&p[url]=${shareURL}" target="_blank" class="social_button"><img src="/_assets/global/images/kickstarters/button_facebook.png" ></a>
                                <a href="https://twitter.com/share?url=${shareURL}" target="_blank" class="social_button"><img src="/_assets/global/images/kickstarters/button_twitter.png" ></a>
                                <a href="https://pinterest.com/pin/create/bookmarklet/?url=${shareURL}&media=${logoURL}" target="_blank" class="social_button"><img src="/_assets/global/images/kickstarters/button_pinterest.png" ></a>
                                <a href="https://plus.google.com/share?url=${shareURL}" target="_blank" class="social_button"><img src="/_assets/global/images/kickstarters/button_google_plus.png" ></a>
                                <a href="#" class="btnEmail" title="Email Registry" >Email</a>
                                <a class="close-reveal-modal">&#215;</a>
                            </div>
                        </div>
                        <div id="emailModal" class="reveal-modal medium" data-reveal><a class="close-reveal-modal">&#215;</a></div>	
                        <%-- <c:if test="${showSkedgeButton eq true}">
                            <div id="scheduleAppointment" class="small-12 columns">
                                need label?
                                <a href="#" class="launchApointmentScheduler" title="Schedule an Appointment">Schedule an Appointment</a>                               
                            </div>                          
                        </c:if> --%>
                   </div>
            </div>
        </div>
		<div id="registryHeader" class="small-12 columns">
           	<div class="regInfo small-4 columns alpha no-padding">
                   <h3 class="eventDateRow subheader">
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
                       <span class="eventDate">${eventDateStr}</span>
                   </h3>
            </div>
           	<div class="counters small-8 columns right print-8">
                   <h3 class="counterWrapper subheader small-4 columns print-4">
                   	<dsp:getvalueof var="eventType" param="eventType"/>
                       <span class="counterValue">
                       	<dsp:droplet name="DateCalculationDroplet">
							<dsp:param param="registrySummaryVO.eventDate" name="eventDate" />
							<dsp:param name="convertDateFlag" value="true" />
							<dsp:oparam name="output">
                       			<dsp:droplet name="Switch">

                           			<dsp:param name="value" param="check" />
                           			<dsp:oparam name="true">
                           				<dsp:valueof param="daysToGo"/>
									</dsp:oparam>
									<dsp:oparam name="false">
										<c:choose>
		                					<c:when test="${not empty appid && appid eq 'TBS_BuyBuyBaby'}">
		                						<c:choose>
				                					<c:when test="${not empty eventType && eventType eq 'Baby'}">
				                						<dsp:valueof param="daysToGo"/>
				                					</c:when>
			                						<c:when test="${not empty eventType && eventType eq 'Birthday'}">
			                							<dsp:getvalueof var="daysToNextCeleb" param="daysToNextCeleb" />
														${daysToNextCeleb}
					                				</c:when>
					                				<c:otherwise>
					                					<dsp:valueof param="daysToGo"/>
					                				</c:otherwise>
			                					</c:choose>
		                					</c:when>
		                					<c:otherwise>
			                					<c:choose>
				                					<c:when test="${not empty eventType && eventType eq 'Wedding'}">
				                						<dsp:valueof param="daysToGo"/>
				                					</c:when>
			                						<c:when test="${not empty eventType && eventType eq 'Baby'}">
				                						<dsp:valueof param="daysToGo"/>
				                					</c:when>
			                						<c:when test="${not empty eventType && eventType eq 'Birthday'}">
			                							<dsp:getvalueof var="daysToNextCeleb" param="daysToNextCeleb" />
														${daysToNextCeleb}
				                					</c:when>
			                						<c:when test="${not empty eventType && eventType eq 'Retirement'}">
				                						<dsp:valueof param="daysToGo"/>
				                					</c:when>
			                						<c:when test="${not empty eventType && eventType eq 'Anniversary'}">
			                							<dsp:getvalueof var="daysToNextCeleb" param="daysToNextCeleb" />
														${daysToNextCeleb}
				                					</c:when>
			                						<c:when test="${not empty eventType && eventType eq 'Housewarming'}">
				                						<dsp:valueof param="daysToGo"/>
				                					</c:when>
				                					<c:when test="${not empty eventType && eventType eq 'Commitment Ceremony'}">
				                						<dsp:valueof param="daysToGo"/>
				                					</c:when>
				                					<c:otherwise>
					                					<dsp:valueof param="daysToGo"/>
				                					</c:otherwise>
			                					</c:choose>
		                					</c:otherwise>
	                					</c:choose>
									</dsp:oparam>
								</dsp:droplet>
                       		</dsp:oparam>
							<dsp:oparam name="error">
								<dsp:valueof param="errorMsg"></dsp:valueof>
							</dsp:oparam>
						</dsp:droplet>

                       </span>
                       <span class="counterHeader">
                       	<dsp:getvalueof var="eventType" param="eventType"/>
                       	<dsp:droplet name="DateCalculationDroplet">
							<dsp:param param="registrySummaryVO.eventDate" name="eventDate" />
							<dsp:param name="convertDateFlag" value="true" />
							<dsp:oparam name="output">
								<dsp:setvalue bean="EmailHolder.values.daysToGo" paramvalue="daysToGo" />
								<dsp:setvalue  bean="EmailHolder.values.dateCheck" paramvalue="check" />
								<dsp:setvalue  bean="EmailHolder.values.daysToNextCeleb" paramvalue="daysToNextCeleb" />

								<dsp:droplet name="Switch">

                           			<dsp:param name="value" param="check" />
                           			<dsp:oparam name="true">
                           				<bbbl:label key="lbl_registry_header_days_to_go" language="${pageContext.request.locale.language}" />
										<%-- <span class="txtDaysLeft"><dsp:valueof param="daysToGo"/></span>
										<sup><bbbl:label key='lbl_regflyout_daystogo' language="${pageContext.request.locale.language}" /></sup>   --%>
										<%--
										<c:choose>
	                					<c:when test="${not empty appid && appid eq 'TBS_BuyBuyBaby'}">
	                						<c:choose>
		                					<c:when test="${not empty eventType && eventType eq 'Baby'}">
		                						<span class="txtDaysLeft"><dsp:valueof param="daysToGo"/></span>
		                						<span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_baby_arrives' language="${pageContext.request.locale.language}" /></span>
		                					</c:when>
		                					<c:when test="${not empty eventType && eventType eq 'Birthday'}">
			                					<span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_only' language="${pageContext.request.locale.language}" /></span> <span class="txtDaysLeft"><dsp:valueof param="daysToGo"/> </span>
			                					<span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_daystogo' language="${pageContext.request.locale.language}" /></span>
			                				</c:when>
		                					</c:choose>
	                					</c:when>
	                					<c:otherwise>
		                					<c:choose>
		                					<c:when test="${not empty eventType && eventType eq 'Wedding'}">
		                						<span class="txtDaysLeft"><dsp:valueof param="daysToGo"/></span> <span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_day_until_wedding' language="${pageContext.request.locale.language}" /></span>
		                					</c:when>
	                						<c:when test="${not empty eventType && eventType eq 'Baby'}">
			                					<span class="txtDaysLeft"><dsp:valueof param="daysToGo"/></span> <span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_day_until_baby' language="${pageContext.request.locale.language}" /></span>
		                					</c:when>
		                					<c:when test="${not empty eventType && eventType eq 'Commitment Ceremony'}">
		                						<span class="txtDaysLeft"><dsp:valueof param="daysToGo"/></span> <span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_day_until_wedding' language="${pageContext.request.locale.language}" /></span>
		                					</c:when>
											<c:when test="${not empty eventType && eventType eq 'College'}">
					                			<span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_only' language="${pageContext.request.locale.language}" /></span>
					                			<span class="txtDaysLeft"><dsp:valueof param="daysToGo"/></span>
					                			<span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_daystogo' language="${pageContext.request.locale.language}" /></span>
					                		</c:when>
		                					<c:otherwise>
		                						<c:if test="${not empty eventType && eventType ne 'Other'}">
			                						<span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_only' language="${pageContext.request.locale.language}" /></span> <span class="txtDaysLeft"><dsp:valueof param="daysToGo"/></span>
													<span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_daystogo' language="${pageContext.request.locale.language}" /></span>
					                			</c:if>
				                			</c:otherwise>
											</c:choose>
										</c:otherwise>
										</c:choose>
										--%>
									</dsp:oparam>
									<dsp:oparam name="false">
										<%-- <c:choose>
											<c:when test="${ not empty eventType && eventType eq 'Baby'}">
												<span></span>
												<strong><bbbl:label key='lbl_mng_regitem_welcome_parenthood' language="${pageContext.request.locale.language}" /></strong>
											</c:when>
											<c:otherwise>
												<span class="txtDaysLeft"><dsp:valueof param="daysToGo"/></span>
												<strong><bbbl:label key='lbl_regflyout_everafter' language="${pageContext.request.locale.language}" /></strong>
											</c:otherwise>
										</c:choose> --%>

										<c:choose>
		                					<c:when test="${not empty appid && appid eq 'TBS_BuyBuyBaby'}">
		                						<c:choose>
			                					<c:when test="${not empty eventType && eventType eq 'Baby'}">
			                						<span class="txtDaysLeft"><dsp:valueof param="daysToGo"/></span> <span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_days_since_life' language="${pageContext.request.locale.language}" /></span>
			                					</c:when>
		                						<c:when test="${not empty eventType && eventType eq 'Birthday'}">
													<dsp:getvalueof var="daysToNextCeleb" param="daysToNextCeleb" />
							                		<span class="txtDaysLeft">${daysToNextCeleb}</span> <span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_days_next_celeb' language="${pageContext.request.locale.language}" /></span>
				                				</c:when>
					                				<c:otherwise>
				                						<span class="txtDaysLeft"><dsp:valueof param="daysToGo"/></span> <span class="txtAfter"> Days Since Event</span>
				                					</c:otherwise>
			                					</c:choose>
		                					</c:when>
		                					<c:otherwise>

			                					<c:choose>
				                					<c:when test="${not empty eventType && eventType eq 'Wedding'}">
				                						<span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" /></span> <span class="txtDaysLeft"><dsp:valueof param="daysToGo"/></span>
				                						<span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_happy_ever_after' language="${pageContext.request.locale.language}" /></span>
				                					</c:when>
			                						<c:when test="${not empty eventType && eventType eq 'Baby'}">
				                						<span class="txtDaysLeft"><dsp:valueof param="daysToGo"/></span> <span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_days_since_life' language="${pageContext.request.locale.language}" /></span>
				                					</c:when>
			                						<c:when test="${not empty eventType && eventType eq 'Birthday'}">
			                							<dsp:getvalueof var="daysToNextCeleb" param="daysToNextCeleb" />
						                				<span>${daysToNextCeleb}</span> <span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_days_next_celeb' language="${pageContext.request.locale.language}" /></span>
				                					</c:when>
			                						<c:when test="${not empty eventType && eventType eq 'Retirement'}">
				                						<span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" /></span> <span class="txtDaysLeft"><dsp:valueof param="daysToGo"/></span>
				                						<span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_rest_of_life' language="${pageContext.request.locale.language}" /></span>
				                					</c:when>
			                						<c:when test="${not empty eventType && eventType eq 'Anniversary'}">
			                							<dsp:getvalueof var="daysToNextCeleb" param="daysToNextCeleb" />
				                						<span class="txtDaysLeft">${daysToNextCeleb}</span> <span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_another_year' language="${pageContext.request.locale.language}" />*</span>
				                					</c:when>
			                						<c:when test="${not empty eventType && eventType eq 'Housewarming'}">
				                						<span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" /></span> <span class="txtDaysLeft"><dsp:valueof param="daysToGo"/></span>
				                						<span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_new_place' language="${pageContext.request.locale.language}" /></span>
				                					</c:when>
				                					<c:when test="${not empty eventType && eventType eq 'Commitment Ceremony'}">
					                						<span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" /></span> <span class="txtDaysLeft"><dsp:valueof param="daysToGo"/></span>
					                						<span class="txtAfter"><bbbl:label key='lbl_regflyout_daystogo_happy_ever_after' language="${pageContext.request.locale.language}" /></span>
				                					</c:when>
				                					<c:otherwise>					                					
				                						<span class="txtDaysLeft"><dsp:valueof param="daysToGo"/></span> <span class="txtAfter">Days Since Event</span>
				                					</c:otherwise>
			                					</c:choose>
		                					</c:otherwise>
	                					</c:choose>
									</dsp:oparam>
								</dsp:droplet>
							</dsp:oparam>
							<dsp:oparam name="error">
								<dsp:valueof param="errorMsg"></dsp:valueof>
							</dsp:oparam>
						</dsp:droplet>

                       </span>
                   </h3>
                   <h3 class="counterWrapper subheader small-4 columns print-4">
                       <span class="counterValue" id="regGiftsWanted"><dsp:valueof param="registrySummaryVO.giftRegistered"/></span>
                       <span class="counterHeader"><bbbl:label key="lbl_registry_header_gifts_in_registry" language="${pageContext.request.locale.language}" /></span>
                   </h3>
                   <h3 class="counterWrapper subheader small-4 columns print-4">
                       <span id="regGiftsGetting" class="counterValue percent-${giftsPercentagePurchased}">${giftPurchased}</span>
                       <span class="counterHeader"><bbbl:label key="lbl_registry_header_gifts_purchased" language="${pageContext.request.locale.language}" /></span>
                   </h3>
               </div>
                <hr/>
	    </div>

		
					
		<c:if test="${showSkedgeButton eq true}">					
			<iframe type="some_value_to_prevent_js_error_on_ie7" 
					id="apointmentScheduler" 
					class="grid_12 alpha omega apointmentScheduler"
					style="display:none;" 
					src="${skedgeURL}&storeId=${registryVOskg.favStoreId}&email=${registryVOskg.primaryRegistrantEmail}&regFN=${registryVOskg.primaryRegistrantFirstName}&regLN=${registryVOskg.primaryRegistrantLastName}&coregFN=${registryVOskg.coRegistrantFirstName}&coregLN=${registryVOskg.coRegistrantLastName}&contactNum=${registryVOskg.primaryRegistrantPrimaryPhoneNum}&registryId=${registryVOskg.registryId}&site=${currentSiteId}&eventDate=${registryVOskg.eventDate}" >
			</iframe>
		</c:if>
		
		<div class="row">
			<dsp:include page="/giftregistry/frags/registry_promo_boxes.jsp">
				<dsp:param name="pageName" value="registryView" />
				<dsp:param name="promoSpot" value="Top" />
				<dsp:param name="registryType" param="registrySummaryVO.eventType" />
				<dsp:param name="siteId" value="${appid}" />
				<dsp:param name="channel" value="Desktop Web" />
			</dsp:include><hr/>
		</div>


	<c:set var="maxBulkSize" scope="request">
		<bbbc:config key="MaxSizeRegistryItems" configName="ContentCatalogKeys" />
	</c:set>
	<dsp:getvalueof var="eventTypeCode" param="registrySummaryVO.registryType.registryTypeName"/>



	<div class="registryTabs alpha omega row">
		<ul class="tabs hide-on-print" data-tab>
          <li class="tab-title active"><a href="#myItems"><bbbl:label key="lbl_registry_owner_myitems" language="${pageContext.request.locale.language}" /></a></li>
          <li class="tab-title"><a href="#kickStarters"><bbbl:label key="lbl_registry_owner_kickstarters" language="${pageContext.request.locale.language}" /></a></li>
        </ul>
        <div class="tabs-content">
          <div class="content active" id="myItems">
            <div id="regAjaxLoad" data-currentView="owner" data-registryId="${registryId}" data-startIdx="0" data-isGiftGiver="false" data-blkSize="${maxBulkSize}" data-isAvailForWebPurchaseFlag="true"  data-sortSeq="${sortSeq}" data-eventTypeCode="${eventTypeCode}" data-eventType="${eventType}" data-pwsurl="${pwsurl}" data-view="${view}" >
                <div class="ajaxLoadWrapper">
                    <bbbt:textArea key="txt_viewregistry_imageloader" language ="${pageContext.request.locale.language}"/>
                </div>
            </div>
          </div>
          <div class="content" id="kickStarters">
            <dsp:include page="kickstarters/top_consultants_grid.jsp?eventType=${eventType}" flush="true" ></dsp:include>
            <dsp:include page="kickstarters/shop_look_grid.jsp?eventType=${eventType}" flush="true" ></dsp:include>

            <%-- START COPY REGISTRY--%>

            <div id="copyRegistrySection" class="kickstarterSection">
                <div class="kickstarterSectionHeader small-12 columns no-padding">
                    <h2><bbbt:textArea key="txt_copy_registry_header" language ="${pageContext.request.locale.language}"/></h2>
                    <p><bbbt:textArea key="txt_copy_registry_description" language ="${pageContext.request.locale.language}"/></p>
                </div>
                <div id="registryFilterFormWrap" class="row alpha omega">
                    <dsp:include page="/giftregistry/registry_search_filter_form.jsp">
                        <dsp:param name="showOnlySearchFields" value="true"  />
                        <dsp:param name="formId" value="2"  />
                    </dsp:include>
                </div>
            </div>
            <%-- ENDCOPY REGISTRY--%>

            <div  class="small-12 columns no-padding kickstarterSection">
                <dsp:include page="/giftregistry/frags/registry_promo_boxes.jsp">
                    <dsp:param name="pageName" value="kickStartersView" />
                    <dsp:param name="promoSpot" value="Bottom" />
                    <dsp:param name="registryType" param="registrySummaryVO.eventType" />
                    <dsp:param name="siteId" value="${appid}" />
                    <dsp:param name="channel" value="Desktop Web" />
                </dsp:include>
            </div>

            <dsp:include page="kickstarters/popular_items_grid.jsp?eventType=${eventType}" flush="true" ></dsp:include>
          </div>
        </div>


</div>
<%--including the Top Registry Items Page--%>


<%--
<dsp:input bean="GiftRegistryFormHandler.successURL" type="hidden" value="view_registry_owner.jsp" />
<dsp:input bean="GiftRegistryFormHandler.errorURL"	type="hidden" value="view_registry_owner.jsp" />
--%>
</div> <%-- END <div id="content" --%>


		</c:when>
		<c:otherwise>
			<div id="content" class="row" role="main">
				<h3><span class="error">
				Access Denied!!!
				</span></h3>
			</div>
		</c:otherwise>
		</c:choose>

		</dsp:oparam>
		<dsp:oparam name="error">
			<dsp:getvalueof param="errorMsg"  var="errorMsg"/>
			<div class="row ">
				<div class="small-12 columns">
                	<div class="error marTop_20"><bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}"/></div>
                </div>
            </div>
		</dsp:oparam>
	</dsp:droplet>

</c:if>
<c:set var ="operation" scope="request">
	<dsp:valueof bean="GiftRegSessionBean.registryOperation"/>
	</c:set>
<script type="text/javascript">
	var operation ='${operation}';
</script>

</jsp:body>
<jsp:attribute name="footerContent">
        <script type="text/javascript">
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
                var s_code=s.t();if(s_code)document.write(s_code);
            }
        </script>
</jsp:attribute>
	</bbb:pageContainer>
</dsp:page>
