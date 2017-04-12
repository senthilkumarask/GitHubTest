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
              
             
                    <div class="grid_6 omega clearfix">
                        <div class="simpleRegForm">
                        <dsp:getvalueof var="inputListMap" param="inputListMap"/>
                        <c:choose>
                         <c:when test="${inputListMap['babyExpectedArivalDate'].isDisplayonForm}">
                              <c:set var="showEventDate" value="true" />
                               <c:set var="eventLabelHolder"><bbbl:label key="lbl_reg_expt_arrival_date" language ="${pageContext.request.locale.language}"/></c:set>
                              <c:set var="isRequired" value=""></c:set> 
                                <c:if test="${inputListMap['babyExpectedArivalDate'].isMandatoryOnCreate}">
                                    <c:set var="isRequired" value="required"></c:set>
                                </c:if>
                         </c:when>
                         <c:otherwise>
                            <c:if test="${inputListMap['eventDate'].isDisplayonForm}">
                                  <c:set var="showEventDate" value="true" />
                                  <c:set var="eventLabelHolder"><bbbl:label key="lbl_reg_event_date" language ="${pageContext.request.locale.language}"/></c:set>
                                  <c:set var="isRequired" value=""></c:set> 
                                <c:if test="${inputListMap['eventDate'].isMandatoryOnCreate}">
                                    <c:set var="isRequired" value="required"></c:set>
                                </c:if>
                         </c:if>
                         </c:otherwise>
                         </c:choose>
                        <c:if test="${showEventDate}">
                                <div id="eventflex1" class="inputField clearfix grid_2 alpha omega" id="eventDate" >
                                 <c:set var="datePlaceHolder"><bbbl:label key='lbl_reg_ph_date' language ='${pageContext.request.locale.language}'/></c:set>
                                    <div class="label grid_3 alpha">
                              <label id="lbltxtRegistryExpectedDate${CADate}" for="txtRegistryExpectedDate${CADate}">${eventLabelHolder}</label>
                                    </div>
                                    <div class="text">
                                        <div class="grid_2 alpha omega" aria-live="assertive">
                                            <dsp:input 
                                                bean="GiftRegistryFormHandler.registryVO.event.eventDate" id="txtRegistryExpectedDate${CADate}"
                                                type="text" name="simpletxtRegDate${CADate}">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryExpectedDate${CADate} errortxtRegistryExpectedDate${CADate}"/>
                                                 <dsp:tagAttribute name="placeholder" value="${datePlaceHolder}"/>
                                                 <dsp:tagAttribute name="class" value="${isRequired} step1FocusField"/>
                                            </dsp:input>
                                            <div id="registryExpectedDateButton${CADate}" class="calendericon">
                                              <span class="icon-fallback-text"><span class="icon-calendar" aria-hidden="true"></span><span class="icon-text"></span></span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                
                            </c:if> 
                            <c:if test="${inputListMap['babyGender'].isDisplayonForm}">
                            <c:set var="isRequired" value=""></c:set> 
                            <dsp:getvalueof var="formExceptions" bean="GiftRegistryFormHandler.formExceptions"/>
                            <c:if test="${not empty formExceptions}">
                             <dsp:getvalueof var="babyGender" bean="GiftRegistryFormHandler.registryVO.event.babyGender"/>
                               <input type="hidden" id="babyGender" value="${babyGender}">
                            </c:if>
                           
                                <c:if test="${inputListMap['babyGender'].isMandatoryOnCreate}">
                                    <c:set var="isRequired" value="required"></c:set>
                                </c:if>
                            <div class="grid_3 inputField alpha omega clearfix" id="eventflex2">
                                <div class="label">
                                    <label id="lblselRegistryGender" for="selRegistryGender"><bbbl:label key="lbl_do_you_know_babyGender" language ="${pageContext.request.locale.language}"/></label>
                                </div>
                                                             
                            <div class="surpriseRadios surpriseRadiosHolder alpha omega grid_4">
                                <input type="radio" name="knowBabyGender" class="knowBabyGender preLoadCss ${isRequired}" id="sBabyGender" aria-hidden='false'/>
                                <label for="sBabyGender" class="sGenderLabel preLoadCss">
                                    <span class=""><bbbl:label key="lbl_reg_yes" language ="${pageContext.request.locale.language}"/></span>
                                </label>

                                <dsp:input type="radio" value="S" name="knowBabyGender" iclass="knowBabyGender preLoadCss ${isRequired}" id="surprise" bean="GiftRegistryFormHandler.registryVO.event.babyGender">
                                <dsp:tagAttribute name="aria-hidden" value="false"/>
                                <label for="surprise" class="surpriseLabel preLoadCss">
                                    <span class=""><bbbl:label key="lbl_reg_its_surprise" language ="${pageContext.request.locale.language}"/></span>
                                </label>
                                </dsp:input>
                            </div>      


                        <div class="grid_2 inputField clearfix">
                            <div class="customRadios customRadiosHolder preLoadCss grid_2">
                            <dsp:input type="radio" value="B" name="boyOrGirl" iclass="boyOrGirl required" id="boy" bean="GiftRegistryFormHandler.registryVO.event.babyGender" >
                                <dsp:tagAttribute name="aria-hidden" value="false"/>
                                <label for="boy" class="labelContent">
                                <span class=""><bbbl:label key="lbl_reg_boy" language ="${pageContext.request.locale.language}"/></span>
                                </label>
                            </dsp:input>

                            <dsp:input type="radio" value="G" name="boyOrGirl" iclass="boyOrGirl required" id="girl" bean="GiftRegistryFormHandler.registryVO.event.babyGender" >
                            <dsp:tagAttribute name="aria-hidden" value="false"/>
                            <label for="girl" class="labelContent">
                                <span class=""><bbbl:label key="lbl_reg_girl" language ="${pageContext.request.locale.language}"/></span>
                            </label>
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
                                            <dsp:input
                                                bean="GiftRegistryFormHandler.registryVO.event.guestCount"
                                                type="text" id="txtRegistryNumberOfGuests" name="txtRegistryNumberOfGuestsName"  maxlength="4" priority="1">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryNumberOfGuests errortxtRegistryNumberOfGuests"/>
                                                 <dsp:tagAttribute name="placeholder" value="${regAppNo}"/>
                                                 <dsp:tagAttribute name="class" value="${isRequired} digits"/>
                                            </dsp:input>
                                        </div>
                                    </div>
                                </div>
                               </c:if>
                            </div>
                    </div>
                
</dsp:page>