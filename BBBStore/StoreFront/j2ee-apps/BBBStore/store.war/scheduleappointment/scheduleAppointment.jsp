<dsp:page>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<dsp:importbean bean="/com/bbb/selfservice/ScheduleAppointmentDroplet"/>
<dsp:importbean bean="com/bbb/selfservice/ScheduleAppointmentManager" />
<dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:getvalueof var="appointmentType1" param="appointmentType"/>
<dsp:getvalueof var="notloggedIn" bean="Profile.transient"/>
<dsp:getvalueof var="storeId1" param="storeId"/>
<dsp:getvalueof var="appointmentType" value="${fn:escapeXml(param.appointmentType)}" />	
<dsp:getvalueof var="storeId" value="${param.storeId}" />
<dsp:getvalueof var="findAStoreFlag" param="findAStoreFlag" />	
<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
<dsp:getvalueof var="registryId"  param="registryId"/>
<dsp:getvalueof var="eventDate"  param="eventDate"/>
<dsp:getvalueof var="coregFN"  param="coregFN"/>
<dsp:getvalueof var="coregLN"  param="coregLN"/>
<dsp:getvalueof var="isregheader"  param="isregheader"/>
<dsp:setvalue value="${fn:escapeXml(param.registryId)}" bean="ScheduleAppointmentManager.registryId" />
<dsp:setvalue value="${fn:escapeXml(param.eventDate)}" bean="ScheduleAppointmentManager.eventDate" />
<dsp:setvalue value="${fn:escapeXml(param.coregFN)}" bean="ScheduleAppointmentManager.coregFN" />
<dsp:setvalue value="${fn:escapeXml(param.coregLN)}" bean="ScheduleAppointmentManager.coregLN" />
 <c:set var="skedgeURL"><bbbc:config key="appointmentSkedgeURL" configName="ThirdPartyURLs" /></c:set>

<dsp:droplet name="/com/bbb/commerce/browse/droplet/ConfigURLDroplet">
	<dsp:param value="ThirdPartyURLs" name="configType" />
	<dsp:oparam name="output">
		<dsp:getvalueof var="cssPath" param="cssPath" scope="request"/>
	</dsp:oparam>
</dsp:droplet>
<c:set var="pageSecured" value="${pageContext.request.secure}" scope="request" />
<c:set var="REQURLPROTOCOL" value="http" scope="request" />
<c:if test="${pageSecured}">
    <c:set var="REQURLPROTOCOL" value="https" scope="request" />
</c:if>
<c:if test="${fn:indexOf(cssPath, '//') == 0}">
    <c:set var="cssPath" value="${REQURLPROTOCOL}:${cssPath}" scope="request" />
</c:if>

<!-- if user is not transient then get the profile details -->
<c:if test="${!notloggedIn}">
	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
		<dsp:param name="array" bean="Profile.userSiteItems"/>
		<dsp:param name="elementName" value="sites"/>
		<dsp:oparam name="output">
			<dsp:getvalueof id="key" param="key"/>
			<c:if test="${currentSiteId eq key}">
				<dsp:getvalueof id="favouriteStoreId" param="sites.favouriteStoreId" scope="session"/>
				<dsp:getvalueof var="firstName" bean="Profile.firstName" scope="session"/>
				<dsp:getvalueof var="lastName" bean="Profile.lastName" scope="session"/>
				<dsp:getvalueof var="email" bean="Profile.email" scope="session" />
				<dsp:getvalueof var="phoneNumber" bean="Profile.phoneNumber" scope="session" />
			</c:if>
		</dsp:oparam>
	</dsp:droplet>	
</c:if>

<dsp:droplet name="/com/bbb/utils/BBBLogBuildNumber">
	<dsp:oparam name="output">
		<dsp:getvalueof var="buildRevisionNumber" param="BUILD_TAG_NUM" />
	</dsp:oparam>
</dsp:droplet>
<!-- <link rel="stylesheet" type="text/css" property="stylesheet" href="${cssPath}/_assets/global/css/contact_store.min.css?v=${buildRevisionNumber}" /> -->

		<dsp:droplet name="ScheduleAppointmentDroplet">
			<dsp:param name="appointmentType" value="${appointmentType1}"/>
			<dsp:param name="siteId" value="${currentSiteId}"/>
			<c:set var="favouriteStoreId" value=""/>
			<c:if test="${(not empty storeId1)}">
				<dsp:param name="favouriteStoreId" value="${storeId1}"/>
            </c:if>
			<dsp:oparam name="output">
				<dsp:getvalueof var="isScheduleAppointment" param="isScheduleAppointment" />
				<dsp:getvalueof var="errorOnModal" param="errorOnModal" />
				<dsp:getvalueof var="directSkedgeMe" param="directSkedgeMe" />
				<c:if test="${isScheduleAppointment}">					
						<c:if test="${(directSkedgeMe) and (not empty skedgeURL)}">	
							<dsp:setvalue value="${appointmentType1}" bean="SearchStoreFormHandler.defaultAppointment" />
							<dsp:getvalueof var="preselectedServiceRef" bean="SearchStoreFormHandler.preSelectedServiceRef"/>									

							<%-- when invoked from the registry header, need to display custom html --%>
							<c:choose>
				                 <c:when test="${isregheader eq 'true'}">
				                 	<c:set var="scheduleAppointment"><bbbl:label key='lbl_mng_regitem_schedule_appointment' language="${pageContext.request.locale.language}" /></c:set>
				              		<a id="registryHeaderSchedule" href="#" class="skedgeMeModalBtn" title="${scheduleAppointment}" 
				              			data-submit-skedgeurl="${skedgeURL}" data-submit-storeId="${storeId1}" 
				              			data-submit-preselectedServiceRef="${preselectedServiceRef}" 
										data-submit-regFN="${firstName}" data-submit-regLN="${lastName}" 
										data-submit-coregFN="${coregFN}" data-submit-coregLN="${coregLN}" data-submit-email="${email}" 
										data-submit-contactNum="${phoneNumber}" data-submit-registryId="${registryId}" 
										data-submit-eventDate="${eventDate}" 
										data-submit-theme=1 >
										<span class="icon-fallback-text">
											<span class="icon-calendar" aria-hidden="true"></span>
											<span class="icon-text"></span>
										</span>
										<span class="toolLbl">${scheduleAppointment}</span>								
									</a>	
				             	</c:when>
				             	<c:otherwise>
				            		<div class="button button_secondary">									
										 	<input class="skedgeMeModalBtn" type="button" role="button" 
											value="Book an Appointment" data-submit-skedgeurl="${skedgeURL}" data-submit-storeId="${storeId1}" data-submit-preselectedServiceRef="${preselectedServiceRef}" 
											data-submit-regFN="${firstName}" data-submit-regLN="${lastName}" 
											data-submit-coregFN="${coregFN}" data-submit-coregLN="${coregLN}" data-submit-email="${email}" 
											data-submit-contactNum="${phoneNumber}" data-submit-registryId="${registryId}" 
											 data-submit-eventDate="${eventDate}" 
											data-submit-theme=1 name="btnFindInStore"/>
									</div>	
				             	</c:otherwise>
				            </c:choose>

							
						</c:if>
						<c:if test="${!directSkedgeMe}">
							<input type="hidden" value="${appointmentType}" id="defaultAppointmentCode">								
							<input type="hidden" value="${errorOnModal}" id="errorOnModal"/>
							<input type="hidden" value="${fn:escapeXml(registryId)}" id="registryId"/>
							<input type="hidden" value="${fn:escapeXml(eventDate)}" id="eventDate"/>
							<input type="hidden" value="${fn:escapeXml(coregFN)}" id="coregFN"/>
							<input type="hidden" value="${fn:escapeXml(coregLN)}" id="coregLN"/>

							<%-- when invoked from the registry header, need to display custom html --%>
							<c:choose>
				                 <c:when test="${isregheader eq 'true'}">
				                 	<c:set var="scheduleAppointment"><bbbl:label key='lbl_mng_regitem_schedule_appointment' language="${pageContext.request.locale.language}" /></c:set>
				              		<a id="registryHeaderSchedule" href="#" class="scheduleAppointmentLink" title="${scheduleAppointment}">
										<span class="icon-fallback-text">
											<span class="icon-calendar" aria-hidden="true"></span>
											<span class="icon-text"></span>
										</span>
										<span class="toolLbl">${scheduleAppointment}</span>								
									</a>	
				             	</c:when>
				             	<c:when test="${(appointmentType1 eq 'REG') or (appointmentType1 eq 'BBY')}">
				             		<c:choose>
                                        			<c:when test="${currentSiteId eq 'BuyBuyBaby'}">
                                        				<div class="button">
										<input id="scheduleAppointmentBtn" type="button" role="button" value="Book an Appointment" name="btnFindInStore"  class="" />
									</div>
                                        			</c:when>
                                        			<c:otherwise>
                                        				<div class="button button_secondary">
										<input id="scheduleAppointmentBtn" type="button" role="button" value="Book an Appointment" name="btnFindInStore"  class="" />
									</div>
                                        			</c:otherwise>
                                        		</c:choose>
				            		 		
				             	</c:when>
				             	<c:otherwise>
				            		<div class="button button_secondary">
										<input id="scheduleAppointmentBtn" type="button" role="button" value="Book an Appointment" name="btnFindInStore"/>
									</div> 		
				             	</c:otherwise>
				            </c:choose>																
								

								

						</c:if>
				</c:if>
			</dsp:oparam>								
		</dsp:droplet>    
</dsp:page>