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
              
						<c:choose>
						<c:when test="${event == 'BA1'}">
							<div id="" class="grid_10 alpha omega clearfix">
							<c:set var="chngLblForBabyReg" value="chngLblForBabyReg" />
						</c:when>
						<c:otherwise>
							<div id="" class="grid_4 alpha omega clearfix">
						</c:otherwise>
						</c:choose>
                            <div class="simpleRegForm">
                  <dsp:getvalueof var="inputListMap" param="inputListMap"/>
								<c:if test="${event == 'BA1'}">
								<div class="inputField clearfix grid_2 alpha omega" id="showerDate" >
                                    <div class="label">
                                        <label class="${chngLblForBabyReg}" id="lbltxtRegistryExpectedDateShower${CADate}" for="txtRegistryShowerDate${CADate}"><bbbl:label key='lbl_reg_shower_date'
										language='${pageContext.request.locale.language}' /></label>
                           
                                    </div>
                                    <div class="text">
                                        <div class="grid_2 alpha omega" aria-live="assertive">
										<c:choose>
											<c:when test="${not empty regVO.event.showerDate}">
											<dsp:input tabindex="1" bean="GiftRegistryFormHandler.registryVO.event.showerDate"
                                                type="text" id="txtRegistryShowerDate${CADate}"  name="simpletxtShowerDate${CADate}"  iclass="step1FocusField" value="${regVO.event.showerDate}">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryExpectedDateShower${CADate} errortxtRegistryExpectedDate${CADate}"/>
                                            </dsp:input>
											</c:when>
											<c:otherwise>
											<dsp:input tabindex="1" bean="GiftRegistryFormHandler.registryVO.event.showerDate"
                                                type="text" id="txtRegistryShowerDate${CADate}"  name="simpletxtShowerDate${CADate}"  iclass="step1FocusField">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryExpectedDateShower${CADate} errortxtRegistryExpectedDate${CADate}"/>
                                                <dsp:tagAttribute name="placeholder" value="${datePlaceHolder}"/>
                                            </dsp:input>
											</c:otherwise>
											</c:choose>
                                              <div id="txtRegistryShowerDateButton${CADate}">
                                              <span class="icon-fallback-text"><span class="icon-calendar" aria-hidden="true"></span><span class="icon-text"></span></span>
											  </div>
										</div>
									</div>
                                </div>
								</c:if>
                                
                                <div id="eventflex1" class="inputField clearfix grid_2 alpha omega" id="eventDate" >
                                 <c:set var="datePlaceHolder"><bbbl:label key='lbl_reg_ph_date' language ='${pageContext.request.locale.language}'/></c:set>

                                 <c:choose>
                				<c:when test="${event == 'BA1' || event == 'BRD'}">
                				<c:choose>
                				 <c:when test="${event == 'BA1'}">
								 <div class="label grid_3" style="margin-left: 0;">
                				 <label id="lbltxtRegistryExpectedDate${CADate}" for="txtRegistryExpectedDate${CADate}"><bbbl:label key="lbl_reg_expt_arrival_date" language ="${pageContext.request.locale.language}"/></label>
                				 </c:when>
                				 <c:otherwise>
                				 <div class="label">
                				 <label id="lbltxtRegistryExpectedDate${CADate}" for="txtRegistryExpectedDate${CADate}"><bbbl:label key="lbl_reg_wedding_date" language ="${pageContext.request.locale.language}"/></label>
                				 
                				 </c:otherwise>
                				
                				</c:choose>
                				
                			 </c:when>
							 <c:when test="${event == 'COL'}">
							 <div class="label">
							 <label id="lbltxtRegistryExpectedDate${CADate}" for="txtRegistryExpectedDate${CADate}"><bbbl:label key="lbl_event_graduation_date" language ="${pageContext.request.locale.language}"/></label>
							 </c:when>
					         <c:otherwise>
								<div class="label">
                                      <label id="lbltxtRegistryExpectedDate${CADate}" for="txtRegistryExpectedDate${CADate}"><bbbl:label key="lbl_reg_event_date" language ="${pageContext.request.locale.language}"/></label>
                              </c:otherwise>
                                      </c:choose>
                                    </div>
                                    <div class="text">
                                        <div class="grid_2 alpha omega" aria-live="assertive">
										<c:choose>
										<c:when test="${not empty regVO.event.eventDate}">
											<dsp:input tabindex="1" bean="GiftRegistryFormHandler.registryVO.event.eventDate" id="txtRegistryExpectedDate${CADate}"
                                                type="text" name="simpletxtRegDate${CADate}" value="${regVO.event.eventDate}">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryExpectedDate${CADate} errortxtRegistryExpectedDate${CADate}"/>
                                                <dsp:tagAttribute name="class" value="isMandatoryField step1FocusField"/>
                                            </dsp:input>
										</c:when>
										<c:otherwise>
											<dsp:input tabindex="1" bean="GiftRegistryFormHandler.registryVO.event.eventDate" id="txtRegistryExpectedDate${CADate}"
                                                type="text" name="simpletxtRegDate${CADate}">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryExpectedDate${CADate} errortxtRegistryExpectedDate${CADate}"/>
                                                <dsp:tagAttribute name="placeholder" value="${datePlaceHolder}"/>
                                                <dsp:tagAttribute name="class" value="isMandatoryField step1FocusField"/>
                                            </dsp:input>
										</c:otherwise>
										</c:choose>
                                              <div id="registryExpectedDateButton${CADate}">
                                              <span class="icon-fallback-text"><span class="icon-calendar" aria-hidden="true"></span><span class="icon-text"></span></span>
                                        </div>
                                    </div>
                                </div>
                                </div>
								
								<c:if test="${event == 'BA1'}">
								
								<div class="grid_5 inputField alpha omega clearfix" id="eventflex2">
									<div class="label">
										<label id="lblselRegistryGender" for="sBabyGender"><bbbl:label key="lbl_do_you_know_babyGender" language ="${pageContext.request.locale.language}"/></label>
									</div>
                                                             
								<div class="surpriseRadios surpriseRadiosHolder alpha omega grid_4">
								<c:choose>
									<c:when test="${regVO.event.babyGender == 'B' || regVO.event.babyGender == 'G'}">
										<input type="radio" name="knowBabyGender" class="knowBabyGender" id="sBabyGender" checked="checked"/>
									</c:when>
									<c:otherwise>
										<input type="radio" name="knowBabyGender" class="knowBabyGender" id="sBabyGender" />
									</c:otherwise>
								</c:choose>
									<label for="sBabyGender" class="sGenderLabel">
										<span class=""><bbbl:label key="lbl_reg_yes" language ="${pageContext.request.locale.language}"/></span>
									</label>
									
								<c:choose>
									<c:when test="${regVO.event.babyGender == 'S'}">
										<dsp:input type="radio" value="S" name="knowBabyGender" iclass="knowBabyGender" id="surprise" bean="GiftRegistryFormHandler.registryVO.event.babyGender" checked="true">
										<label for="surprise" class="surpriseLabel">
											<span class=""><bbbl:label key="lbl_reg_its_surprise" language ="${pageContext.request.locale.language}"/></span>
										</label>
										</dsp:input>
									</c:when>
									<c:otherwise>
										<dsp:input type="radio" value="S" name="knowBabyGender" iclass="knowBabyGender" id="surprise" bean="GiftRegistryFormHandler.registryVO.event.babyGender">
										<label for="surprise" class="surpriseLabel">
											<span class=""><bbbl:label key="lbl_reg_its_surprise" language ="${pageContext.request.locale.language}"/></span>
										</label>
										</dsp:input>
									</c:otherwise>
								</c:choose>
								
								</div>      


								<div class="grid_2 inputField clearfix">
									<div class="customRadios customRadiosHolder grid_2">
									<c:choose>
									<c:when test="${regVO.event.babyGender == 'B'}">
										<dsp:input type="radio" value="B" name="boyOrGirl" iclass="boyOrGirl" id="boy" bean="GiftRegistryFormHandler.registryVO.event.babyGender" checked="true">
										<label for="boy" class="labelContent">
											<span class=""><bbbl:label key="lbl_reg_boy" language ="${pageContext.request.locale.language}"/></span>
										</label>
										</dsp:input>
									</c:when>
									<c:otherwise>
										<dsp:input type="radio" value="B" name="boyOrGirl" iclass="boyOrGirl" id="boy" bean="GiftRegistryFormHandler.registryVO.event.babyGender" >
										<label for="boy" class="labelContent">
											<span class=""><bbbl:label key="lbl_reg_boy" language ="${pageContext.request.locale.language}"/></span>
										</label>
										</dsp:input>
									</c:otherwise>
									</c:choose>
									
									<c:choose>
									<c:when test="${regVO.event.babyGender == 'G'}">
										<dsp:input type="radio" value="G" name="boyOrGirl" iclass="boyOrGirl" id="girl" bean="GiftRegistryFormHandler.registryVO.event.babyGender" checked="true">
										<label for="girl" class="labelContent">
											<span class=""><bbbl:label key="lbl_reg_girl" language ="${pageContext.request.locale.language}"/></span>
										</label>
										</dsp:input>
									</c:when>
									<c:otherwise>
										<dsp:input type="radio" value="G" name="boyOrGirl" iclass="boyOrGirl" id="girl" bean="GiftRegistryFormHandler.registryVO.event.babyGender" >
										<label for="girl" class="labelContent">
											<span class=""><bbbl:label key="lbl_reg_girl" language ="${pageContext.request.locale.language}"/></span>
										</label>
										</dsp:input>
									</c:otherwise>
									</c:choose>

									</div>
								</div>
								</div>
								</c:if>

								<c:if test="${event != 'BA1' && event != 'COL'}">
								
								<c:if test="${event == 'BRD' || event == 'COM'}">
									<c:set var="isMandatoryField" value="isMandatoryField"/>
								</c:if>
                                
                                <div class="inputField clearfix grid_2 alpha omega noMarRight" id="guestCount" >
                                    <div class="label">
                                        <label id="lbltxtRegistryNumberOfGuests" for="txtRegistryNumberOfGuests"><bbbl:label key="lbl_event_num_of_guests" language ="${pageContext.request.locale.language}"/></label>
                                    </div>
                                    <div class="text">
                                        <div class="grid_2 alpha">
                                        <c:set var="regAppNo"><bbbl:label key='lbl_reg_ph_approxNo' language ='${pageContext.request.locale.language}'/></c:set>
										<c:choose>
											<c:when test="${not empty regVO.event.guestCount && regVO.event.guestCount ne 0}">
											<dsp:input tabindex="2" bean="GiftRegistryFormHandler.registryVO.event.guestCount"
                                                type="text" id="txtRegistryNumberOfGuests" name="txtRegistryNumberOfGuestsName"  maxlength="4" priority="1" value="${regVO.event.guestCount}">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryNumberOfGuests errortxtRegistryNumberOfGuests"/>
                                                <dsp:tagAttribute name="class" value="${isMandatoryField} digits"/>
                                            </dsp:input>
											</c:when>
										<c:otherwise>
											<dsp:input tabindex="2" bean="GiftRegistryFormHandler.registryVO.event.guestCount"
                                                type="text" id="txtRegistryNumberOfGuests" name="txtRegistryNumberOfGuestsName"  maxlength="4" priority="1" value="">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryNumberOfGuests errortxtRegistryNumberOfGuests"/>
                                                <dsp:tagAttribute name="placeholder" value="${regAppNo}"/>
                                                <dsp:tagAttribute name="class" value="${isMandatoryField} digits"/>
                                            </dsp:input>
										</c:otherwise>
										</c:choose>
                                        </div>
                                    </div>
                                </div>
								</c:if>
								
								<c:if test="${event == 'COL'}">
								<div class="inputField clearfix grid_2 alpha omega noMarRight" id="college" style="float:left;">
                                    <div class="label">
                                        <label id="lbltxtRegistryCollegeName" for="txtRegistryCollegeName">
                                            <bbbl:label key="lbl_event_college" language ="${pageContext.request.locale.language}"/>
                                            <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
                                        </label>
                                    </div>
								<div class="text">
								<div class="grid_2 alpha">
								<c:choose>
									<c:when test="${not empty regVO.event.college}">
                                        <dsp:input tabindex="2" bean="GiftRegistryFormHandler.registryVO.event.college" type="text" id="txtRegistryCollegeName" iclass="isMandatoryField cannotStartWithSpecialChars alphabasicpuncwithcomma cannotStartWithNumber" maxlength="30"  priority="1" value="${regVO.event.college}">
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryCollegeName errortxtRegistryCollegeName"/>
                                        </dsp:input>
									</c:when>
									<c:otherwise>
										<dsp:input tabindex="2" bean="GiftRegistryFormHandler.registryVO.event.college" type="text" id="txtRegistryCollegeName" iclass="isMandatoryField cannotStartWithSpecialChars alphabasicpuncwithcomma cannotStartWithNumber" maxlength="30"  priority="1" >
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryCollegeName errortxtRegistryCollegeName"/>
                                        </dsp:input>
									</c:otherwise>
								</c:choose>
                                </div>
								</div>
								</div>
								</c:if>

                               <%-- hidden fields for all registries --%>
								 <c:if test="${event != 'COL'}">
									<dsp:input bean="GiftRegistryFormHandler.registryVO.event.college" type="hidden"  value="" />
								 </c:if>
								 <c:if test="${event eq 'COL'}">
									<dsp:input bean="GiftRegistryFormHandler.registryVO.event.guestCount" type="hidden" value="" />
									<dsp:input bean="GiftRegistryFormHandler.registryVO.event.showerDate" type="hidden" value=""/>
									<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.coRegistrant.firstName" value=""/>
									<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.coRegistrant.lastName" value=""/>    
									<dsp:input type="hidden"  bean="GiftRegistryFormHandler.registryVO.coRegistrant.email" value="" />
									<dsp:input  type="hidden" value="" bean="GiftRegistryFormHandler.registryVO.coRegistrant.babyMaidenName"/>
								 </c:if>
								 <c:if test="${event eq 'OTH'}">
									<dsp:input bean="GiftRegistryFormHandler.registryVO.event.showerDate" type="hidden" value=""/>
								 </c:if>
								 <c:if test="${event != 'BIR'}">
									<dsp:input bean="GiftRegistryFormHandler.registryVO.event.birthDate" type="hidden" value="" />
								 </c:if>
								 <c:if test="${event != 'BA1'}">
									<dsp:input bean="GiftRegistryFormHandler.registryVO.event.babyGender" type="hidden" value="" /> 
									<dsp:input bean="GiftRegistryFormHandler.registryVO.event.babyNurseryTheme" type="hidden" value=""/>
									<dsp:input bean="GiftRegistryFormHandler.registryVO.event.babyName" type="hidden" value="" />
								 </c:if>
                          
                            </div>
                    </div>
				
</dsp:page>