<%@page contentType="application/json"%>

<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/ManageRegistryChecklistDroplet" />
<dsp:importbean bean="/com/bbb/selfservice/ScheduleAppointmentDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/RecommendationInfoDisplayDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/DateCalculationDroplet" />
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryVODroplet" />
<dsp:importbean bean="/com/bbb/selfservice/ScheduleAppointmentDroplet"/>
<dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
<dsp:page>

<c:set var="BedBathUSSite">
		<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
</c:set>
<c:set var="BuyBuyBabySite">
		<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
</c:set>
<c:set var="BedBathCanadaSite">
			<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
</c:set>

<c:set var="skedgeURL"><bbbc:config key="skedgeURL" configName="ThirdPartyURLs" /></c:set>
<dsp:getvalueof var="currentSiteId" bean="/atg/multisite/Site.id" />
<c:set var="ManageRegistry"><bbbl:label key="lbl_manage_registry" language="${pageContext.request.locale.language}"/></c:set>
<c:set var="book_an_appointment"><bbbl:label key="lbl_book_an_appointment" language="${pageContext.request.locale.language}"/></c:set>
<c:set var="ask_a_friend"><bbbl:label key="lbl_ask_a_friend" language="${pageContext.request.locale.language}"/></c:set>
<c:set var="quick_picks"><bbbl:label key="lbl_quick_picks" language="${pageContext.request.locale.language}"/></c:set>
<c:set var="view_all_your_registries"><bbbl:label key="lbl_view_all_your_registries" language="${pageContext.request.locale.language}"/></c:set>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
<c:set var="inviteFriend"><bbbc:config key="Invite_Friends_Key" configName="FlagDrivenFunctions" /></c:set>
<c:set var="inviteFriend" value="${fn:toLowerCase(inviteFriend)}"/>
<c:set var="clickToView" scope="request"><bbbl:label key='lbl_click_tap_open' language="${pageContext.request.locale.language}"/></c:set>
<c:set var="manageRegDDLabel" scope="request"><bbbl:label key='lbl_manage_reg_dd' language="${pageContext.request.locale.language}"/></c:set>
<dsp:getvalueof param="registrySummaryVO" var="registrySummaryVO" scope="request"></dsp:getvalueof>
<c:set var="regFirstName" scope="request" value="${registrySummaryVO.primaryRegistrantFirstName}" />
<c:set var="coregFirstName" scope="request" value="${registrySummaryVO.coRegistrantFirstName}" />
<c:set var="regName" scope="request" value="${registrySummaryVO.registryType.registryTypeDesc}" />
<c:set var="registryText" scope="request"><bbbl:label key='lbl_checklist_reg_text' language="${pageContext.request.locale.language}"/></c:set>
<c:set var="mngRegLinkLabel">${regFirstName} <c:if test="${coregFirstName ne null}">and ${coregFirstName}</c:if>'s  ${regName} ${registryText}</c:set>                			
			<dsp:getvalueof var="ManageCheckListLink" param="ManageCheckListLink" />
			<dsp:getvalueof var="registrySummaryVO" param="registrySummaryVO" />
			
							<dsp:droplet name="DateCalculationDroplet">
								  <dsp:param param="registrySummaryVO.eventDate" name="eventDate" />
								  <dsp:param name="convertDateFlag" value="true" />
								  <dsp:oparam name="output">
								   <dsp:getvalueof var="displayInviteFriendsCheck" param="daysCheck"/>
								  </dsp:oparam>
							 </dsp:droplet>
							 <c:if test="${displayInviteFriendsCheck eq false}">
				   			 <dsp:droplet name="RecommendationInfoDisplayDroplet">
							      <dsp:param name="registryId" param="registryId"/>
							      <dsp:param name="tabId" value="0"/>
							      <dsp:param name="fromViewRegistryOwner" value="true"/>
							      <dsp:oparam name="output">
							      	<dsp:getvalueof var="pendingItemCount" param="recommendationProductSize"/>
			                      </dsp:oparam>
	               			 </dsp:droplet>
	        		     </c:if>
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
			<json:object escapeXml="false">
				<json:property name="manageRegistryAttribs" escapeXml="false">	
						<c:forEach items="${ManageCheckListLink.links}" var="linkVO">
							<c:if test="${linkVO.bannerText eq ManageRegistry}">
							<li >
                            	<a id="regCheckManageRegistry" aria-label="manage ${mngRegLinkLabel}" href="${contextPath}${linkVO.bannerLink}?registryId=${registrySummaryVO.registryId}&eventType=${registrySummaryVO.eventType}">${linkVO.bannerText}</a>
                  			</li>
                  			</c:if>
                  		<c:if test="${linkVO.bannerText eq book_an_appointment }">
                  			 <c:choose>
                                    <c:when test="${registrySummaryVO.registryType.registryTypeName == 'COL'}">
                                           <c:set var="appointmentcode" value="COL" />
                                      </c:when>
                                      <c:otherwise>
                                             <c:set var="appointmentcode" value="REG" />
                                      </c:otherwise>
                              </c:choose>
                                             
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
                             <dsp:droplet name="GetRegistryVODroplet">
                                                              <dsp:param name="siteId" value="${currentSiteId}"/>
                                                              <dsp:param value="${registrySummaryVO.registryId}" name="registryId" />
                                                              <dsp:oparam name="output">
                                                        <dsp:getvalueof var="favouriteStoreId" param="registryVO.prefStoreNum"/>
                                                              </dsp:oparam>
                             </dsp:droplet>

              <c:set var="buildRevisionNumber" scope="request"><bbbc:config key="buildRevisionNumber" configName="ThirdPartyURLs" /></c:set>
              <link rel="stylesheet" type="text/css" property="stylesheet" href="${cssPath}/_assets/global/css/contact_store.min.css?v=${buildRevisionNumber}" />

              <dsp:droplet name="ScheduleAppointmentDroplet">
                              <dsp:param name="appointmentType" value="${appointmentcode}"/>
                              <dsp:param name="siteId" value="${currentSiteId}"/>
                              <dsp:param name="favouriteStoreId" value="${favouriteStoreId}"/>
              <dsp:oparam name="output">
                              <dsp:getvalueof var="isScheduleAppointment" param="isScheduleAppointment" />
                              <dsp:getvalueof var="errorOnModal" param="errorOnModal" />
                              <dsp:getvalueof var="directSkedgeMe" param="directSkedgeMe" />
                              <c:if test="${isScheduleAppointment}">
                                              <c:if test="${(directSkedgeMe) and (not empty skedgeURL)}">
                                                              <dsp:setvalue value="${appointmentcode}" bean="SearchStoreFormHandler.defaultAppointment" />
                                                              <dsp:getvalueof var="preselectedServiceRef" bean="SearchStoreFormHandler.preSelectedServiceRef"/>

                                                              <%-- when invoked from the registry header, need to display custom html --%>
                               <c:set var="scheduleAppointment"><bbbl:label key='lbl_mng_regitem_schedule_appointment' language="${pageContext.request.locale.language}" /></c:set>
                              <li >
                                              <a id="regCheckScheduleAppoinmentSkedgeMe" href="javascript:void(0);" aria-label="${linkVO.bannerText} for ${mngRegLinkLabel}" class="skedgeMeModalBtn" title="${scheduleAppointment}"
                                                              data-submit-skedgeurl="${skedgeURL}" data-submit-storeId="${favouriteStoreId}"
                                                              data-submit-preselectedServiceRef="${preselectedServiceRef}"
                                                                                                              data-submit-regFN="${firstName}" data-submit-regLN="${lastName}"
                                                                                                              data-submit-coregFN="${coregFN}" data-submit-coregLN="${coregLN}" data-submit-email="${email}"
                                                                                                              data-submit-contactNum="${phoneNumber}" data-submit-registryId="${registryId}"
                                                                                                              data-submit-eventDate="${registrySummaryVO.eventDate}"
                                                                                                              data-submit-theme=1 >
                                                                                                              <span class="toolLbl">${linkVO.bannerText}</span>
                                                                                              </a>
                                                                                              </li>
                                              </c:if>
                                              <c:if test="${!directSkedgeMe}">
                                                              <input type="hidden" value="${appointmentType}" id="defaultAppointmentCode">
                                                              <input type="hidden" value="${errorOnModal}" id="errorOnModal"/>
                                                              <input type="hidden" value="${fn:escapeXml(registrySummaryVO.registryId)}" id="registryId"/>
                                                              <input type="hidden" value="${fn:escapeXml(registrySummaryVO.eventDate)}" id="eventDate"/>
                                                              <input type="hidden" value="${fn:escapeXml(coregFN)}" id="coregFN"/>
                                                              <input type="hidden" value="${fn:escapeXml(coregLN)}" id="coregLN"/>

                                                              <%-- when invoked from the registry header, need to display custom html --%>

                                              <c:set var="scheduleAppointment"><bbbl:label key='lbl_mng_regitem_schedule_appointment' language="${pageContext.request.locale.language}" /></c:set>
                                              <li >
                                              <a id="registryHeaderSchedule"  href="javascript:void(0);" aria-label="${linkVO.bannerText} for ${mngRegLinkLabel}" class="scheduleAppointmentLink" title="${scheduleAppointment}">
                                                                                                              <span class="toolLbl">${linkVO.bannerText}</span>
                                                                                              </a>
                                                                                              </li>

                                              </c:if>
                                                 </c:if>
                                 </dsp:oparam>
                 </dsp:droplet>
                  			</c:if>
                  			<c:if test="${linkVO.bannerText eq ask_a_friend && inviteFriend eq 'false' && (registrySummaryVO.isPublic eq '0' || (registrySummaryVO.isPublic eq '1' && showRecommendationTab eq 'true'))}">
                  			<li >
                  				<a id="regCheckAskFriend" aria-label="${linkVO.bannerText} for ${mngRegLinkLabel}" href="${contextPath}${linkVO.bannerLink}?registryId=${registrySummaryVO.registryId}&eventType=${registrySummaryVO.eventType}#t=recommendations">${linkVO.bannerText}</a>
                  			</li>
                  			</c:if>
                  			<c:if test="${linkVO.bannerText eq quick_picks}">
                  			<li >
                  				<a id="regCheckQuickPick" aria-label="${linkVO.bannerText} for ${mngRegLinkLabel}" href="${contextPath}${linkVO.bannerLink}?registryId=${registrySummaryVO.registryId}&eventType=${registrySummaryVO.eventType}#t=kickStarters">${linkVO.bannerText}</a>
                  			</li>
                  			</c:if>
                  			<c:if test="${linkVO.bannerText eq view_all_your_registries}">
                  			<li >
                            	<a id="regCheckViewAllRegistries" href="${contextPath}${linkVO.bannerLink}">${linkVO.bannerText}</a>
                            	</li>
                  			</c:if>
						</c:forEach>
				</json:property>
			</json:object>
</dsp:page>
