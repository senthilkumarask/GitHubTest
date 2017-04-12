<dsp:page>
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <dsp:importbean bean="/com/bbb/account/droplet/BBBConfigKeysDroplet" />
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
    <dsp:getvalueof bean="SessionBean.registryTypesEvent" id="event">
	<dsp:importbean bean="/com/bbb/simplifyRegistry/droplet/RegistryInputsDroplet" />
    <dsp:include page="/_includes/third_party_on_of_tags.jsp"/>
    <dsp:getvalueof var="step" param="step"/>
    <dsp:getvalueof var="link" param="link"/>
    <dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
    
        <dsp:getvalueof id="siteId" idtype="java.lang.String" param="siteId" />
        <c:choose>
            <c:when test="${siteId == 'BedBathCanada'}">
                <c:set var="CADate" value="CA" scope="request" />
            </c:when>
            <c:otherwise>
                <c:set var="CADate" value="" scope="request" />
            </c:otherwise>
        </c:choose>
    </dsp:getvalueof>
              
             
						<div id="" class="grid_4 alpha omega clearfix fl">
                            <div class="simpleRegForm">
                  <dsp:getvalueof var="inputListMap" param="inputListMap"/>
                         <c:if test="${inputListMap['eventDate'].isDisplayonForm}">
                                       <c:set var="isRequired" value=""></c:set> 
													 <c:if test="${inputListMap['eventDate'].isMandatoryOnCreate}">
													 <c:set var="isRequired" value="required"></c:set>
													 </c:if>
                                <div id="eventflex1" class="inputField clearfix grid_2 alpha omega" id="eventDate" >
                                 <c:set var="datePlaceHolder"><bbbl:label key='lbl_reg_ph_date' language ='${pageContext.request.locale.language}'/></c:set>
                                    <div class="label">
                                 <c:choose>
                				<c:when test="${event == 'BRD'}">
                				<label id="lbltxtRegistryExpectedDate${CADate}" for="txtRegistryExpectedDate${CADate}"><bbbl:label key="lbl_reg_wedding_date" language ="${pageContext.request.locale.language}"/></label>
                			 </c:when>
					         <c:otherwise>
                                      <label id="lbltxtRegistryExpectedDate${CADate}" for="txtRegistryExpectedDate${CADate}"><bbbl:label key="lbl_reg_event_date" language ="${pageContext.request.locale.language}"/></label>
                              </c:otherwise>
                                      </c:choose>
                                    </div>
                                    <div class="text">
                                        <div class="grid_2 alpha omega" aria-live="assertive">
										<c:choose>
										<c:when test="${not empty regVO.event.eventDate}">
											<dsp:input bean="GiftRegistryFormHandler.registryVO.event.eventDate" id="txtRegistryExpectedDate${CADate}"
                                                type="text" name="simpletxtRegDate${CADate}" value="${regVO.event.eventDate}">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryExpectedDate${CADate} errortxtRegistryExpectedDate${CADate}"/>
                                                <dsp:tagAttribute name="class" value="${isRequired} step1FocusField"/>
                                            </dsp:input>
										</c:when>
										<c:otherwise>
											<dsp:input bean="GiftRegistryFormHandler.registryVO.event.eventDate" id="txtRegistryExpectedDate${CADate}"
                                                type="text" name="simpletxtRegDate${CADate}">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryExpectedDate${CADate} errortxtRegistryExpectedDate${CADate}"/>
                                                <dsp:tagAttribute name="placeholder" value="${datePlaceHolder}"/>
                                                <dsp:tagAttribute name="class" value="${isRequired} step1FocusField"/>
                                            </dsp:input>
										</c:otherwise>
										</c:choose>
                                              <div id="registryExpectedDateButton${CADate}" class="calendericon">
                                              <span class="icon-fallback-text"><span class="icon-calendar" aria-hidden="true"></span><span class="icon-text"></span></span>
                                        </div>
                                    </div>
                                </div>
                                </div>
                              
                            </c:if> 
                            
                            <c:if test="${inputListMap['college'].isDisplayonForm}">
                            <c:set var="isRequired" value=""></c:set> 
											 <c:if test="${inputListMap['college'].isMandatoryOnCreate}">
											 <c:set var="isRequired" value="required"></c:set>
											 </c:if>
						<div class="inputField clearfix grid_2 alpha omega noMarRight" id="college" >
                         <div class="label">
                             <label id="lbltxtRegistryCollege" for="txtRegistryCollege"><bbbl:label key="lbl_event_college" language ="${pageContext.request.locale.language}"/></label>
                         </div>
                         <div class="text">
                             <div class="grid_2 alpha">
                              <c:set var="collgeNamePlaceholder"><bbbl:label key="lbl_reg_ph_collegeName" language ='${pageContext.request.locale.language}'/></c:set>
                              <dsp:input bean="GiftRegistryFormHandler.registryVO.event.college"
                                     type="text" id="txtRegistryCollege" iclass="required cannotStartWithSpecialChars alphabasicpuncwithcomma cannotStartWithNumber" name="txtRegistryCollegeName" priority="1">
                                     <dsp:tagAttribute name="aria-required" value="true"/>
                                     <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryCollege errortxtRegistryCollege"/>
                                     <dsp:tagAttribute name="placeholder" value="${collgeNamePlaceholder}"/>
                                 </dsp:input>
								
                             </div>
                         </div>
                     </div>
				      </c:if>
				   
                                 <c:if test="${inputListMap['numberOfGuests'].isDisplayonForm}">
                                 <c:set var="isRequired" value=""></c:set> 
													 <c:if test="${inputListMap['numberOfGuests'].isMandatoryOnCreate}">
													 <c:set var="isRequired" value="required"></c:set>
													 </c:if>
                                <div class="inputField clearfix grid_2 alpha omega noMarRight" id="guestCount" >
                                    <div class="label">
                                        <label id="lbltxtRegistryNumberOfGuests" for="txtRegistryNumberOfGuests"><bbbl:label key="lbl_event_num_of_guests" language ="${pageContext.request.locale.language}"/></label>
                                    </div>
                                    <div class="text">
                                        <div class="grid_2 alpha">
                                        <c:set var="regAppNo"><bbbl:label key='lbl_reg_ph_approxNo' language ='${pageContext.request.locale.language}'/></c:set>
										<c:choose>
											<c:when test="${not empty regVO.event.guestCount}">
											<dsp:input bean="GiftRegistryFormHandler.registryVO.event.guestCount"
                                                type="text" id="txtRegistryNumberOfGuests" name="txtRegistryNumberOfGuestsName"  maxlength="4" priority="1" value="${regVO.event.guestCount}">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryNumberOfGuests errortxtRegistryNumberOfGuests"/>
                                                <dsp:tagAttribute name="class" value="${isRequired} digits"/>
                                            </dsp:input>
											</c:when>
										<c:otherwise>
											<dsp:input bean="GiftRegistryFormHandler.registryVO.event.guestCount"
                                                type="text" id="txtRegistryNumberOfGuests" name="txtRegistryNumberOfGuestsName"  maxlength="4" priority="1">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryNumberOfGuests errortxtRegistryNumberOfGuests"/>
                                                <dsp:tagAttribute name="placeholder" value="${regAppNo}"/>
                                                <dsp:tagAttribute name="class" value="${isRequired} digits"/>
                                            </dsp:input>
										</c:otherwise>
										</c:choose>
                                        </div>
                                    </div>
                                </div>
                               </c:if>
                            </div>
                    </div>
				
</dsp:page>