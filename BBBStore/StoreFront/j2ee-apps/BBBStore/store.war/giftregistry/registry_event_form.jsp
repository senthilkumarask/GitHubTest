<dsp:page>
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <dsp:importbean bean="/com/bbb/account/droplet/BBBConfigKeysDroplet" />
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
    <dsp:getvalueof bean="SessionBean.registryTypesEvent" id="event">
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

    <div class="steps step1 clearfix grid_12 alpha omega">
        <c:choose>
            <c:when test="${event == 'BA1'}">
                <div id="step1Preview" class="grid_12 alpha omega clearfix">
                    <div class="grid_12 alpha omega clearfix">
                        <div class="clearfix regsitryItem">
							<h3>
								<span class="regsitryItemNumber">1</span>
								<c:choose>
								<c:when test="${empty regVO}">
									<span class="regsitryItemTitle arrowOpenSmall"><bbbl:label key="lbl_regcreate_tell_us_abt_event" language="${pageContext.request.locale.language}"/> <a href="#edit1" id="step1EditLink" class="editRegistryLink" title='<bbbl:label key="lbl_regcreate_edit" language="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_regcreate_edit" language="${pageContext.request.locale.language}"/></a></span>
									
							 
								</c:when>
								<c:otherwise>
								<span class="regsitryItemTitle arrowClosedSmall"><bbbl:label key="lbl_regcreate_tell_us_abt_event" language="${pageContext.request.locale.language}"/> <a href="#edit1" id="step1EditLink" class="editRegistryLink" title='<bbbl:label key="lbl_regcreate_edit" language="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_regcreate_edit" language="${pageContext.request.locale.language}"/></a></span>
								</c:otherwise>
								</c:choose>
							</h3>
                        </div>
                    </div>
                    <div class="clear"></div>
                    <div id="step1Information" class="grid_12 alpha omega stepInformation form clearfix">
                        <div class="grid_3 alpha pushDown">
                            <div class="entry">
                                <div class="field"><bbbl:label key="lbl_event_arrival_date" language="${pageContext.request.locale.language}"/>:</div>
                                <c:choose>
                                    <c:when test="${siteId == 'BuyBuyBaby'}">
                                        <div class="value" id="valRegistryExpectedDate"></div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="value" id="valRegistryExpectedDate"></div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <div class="grid_2 pushDown">
                            <div class="entry">
                                <div class="field"><bbbl:label key="lbl_event_shower_date" language="${pageContext.request.locale.language}"/>:</div>
                                <c:choose>
                                    <c:when test="${siteId == 'BuyBuyBaby'}">
                                        <div class="value" id="valRegistryDate"></div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="value" id="valRegistryDate"></div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <div class="grid_2 pushDown">
                            <div class="entry">
                                <div class="field"><bbbl:label key="lbl_event_baby_gender" language="${pageContext.request.locale.language}"/>:</div>
                                <c:choose>
                                    <c:when test="${siteId == 'BuyBuyBaby'}">
                                        <div class="value" id="valRegistryGender"></div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="value" id="valRegistryGender"></div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <div class="grid_2 pushDown">
                            <div class="entry">
                                <div class="field"><bbbl:label key="lbl_event_baby_name" language="${pageContext.request.locale.language}"/>:</div>
                                <c:choose>
                                    <c:when test="${siteId == 'BuyBuyBaby'}">
                                        <div class="value" id="valRegistryBabyName"></div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="value" id="valRegistryBabyName"></div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <div class="grid_2 omega pushDown">
                            <div class="entry">
                                <div class="field"><bbbl:label key="lbl_event_theme" language="${pageContext.request.locale.language}"/>:</div>
                                <c:choose>
                                    <c:when test="${siteId == 'BuyBuyBaby'}">
                                        <div class="value" id="valRegistryTheme"></div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="value" id="valRegistryTheme"></div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <div class="clear"></div>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
                <div id="step1Form" class="grid_12 alpha omega clearfix">
                    <div id="step1FormPost" class="grid_12 alpha omega form post clearfix padTop_10">
                        <fieldset class="grid_8 alpha clearfix">
                            <legend class="formTitle offScreen"><bbbl:label key="lbl_event_info_title" language="${pageContext.request.locale.language}"/></legend>
                            <div class="registryForm">
                                <div class="inputField clearfix">
                                    <div class="label">
                                        <label id="lbltxtRegistryExpectedDate${CADate}" for="txtRegistryExpectedDate${CADate}">
                                            <bbbl:label key="lbl_event_arrival_date" language="${pageContext.request.locale.language}"/>
                                            <span class="required"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}"/></span>
                                        </label>
                                    </div>
                                    <div class="text">
                                        <div class="grid_2 alpha" aria-live="assertive">
                                            <dsp:input tabindex="1" 
                                                bean="GiftRegistryFormHandler.registryVO.event.eventDate"
                                                type="text" id="txtRegistryExpectedDate${CADate}"  name="txtRegistryExpectedDateName" value="${regVO.event.eventDate}" iclass="required step1FocusField">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryExpectedDate${CADate} errortxtRegistryExpectedDate${CADate}"/>
                                            </dsp:input>
                                        </div>
                                        <div class="grid_1 alpha omega">
                                            <div id="registryExpectedDateButton" class="calendar"></div>
                                        </div>
                                    </div>
                                </div>
                                <div class="inputField clearfix" id="showerDate">
                                    <div class="label">
                                        <label id="lbltxtRegistryDate${CADate}" for="txtRegistryDate">
                                           <bbbl:label key="lbl_event_shower_date" language="${pageContext.request.locale.language}"/>
                                        </label>
                                    </div>
                                    <div class="text">
                                        <div class="grid_2 alpha">
                                            <dsp:input tabindex="2" bean="GiftRegistryFormHandler.registryVO.event.showerDate"
                                                type="text" id="txtRegistryDate${CADate}" name="txtShowerDateName" value="${regVO.event.showerDate}">
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryDate${CADate} errortxtRegistryDate${CADate}"/>
                                            </dsp:input>
                                        </div>
                                        <div class="grid_1 alpha omega">
                                            <div id="registryDateButton" class="calendar"></div>
                                        </div>
                                    </div>
                                </div>
                                <div class="inputField clearfix" id="babyGender">
                                    <div class="label">
                                        <label id="lblselRegistryGender" for="selRegistryGender"><bbbl:label key="lbl_event_baby_gender" language="${pageContext.request.locale.language}"/></label>
                                    </div>
									<c:set var="regVOBabyGender">${regVO.event.babyGender}</c:set>
                                    <div class="select">
                                        <div class="grid_2 alpha">
                                            <dsp:droplet name="BBBConfigKeysDroplet">
                                                <dsp:param name="configKey" value="GenderMap"/>
                                                <dsp:oparam name="output">
                                                    <dsp:getvalueof var="gender" param="configMap"/>
                                                    <dsp:select tabindex="3" bean="GiftRegistryFormHandler.babygenderStr"  id="selRegistryGender" iclass="uniform"> 
                										<dsp:tagAttribute name="aria-required" value="false"/>
                                                        <dsp:tagAttribute name="aria-labelledby" value="lblselRegistryGender errorselRegistryGender"/>
                                                        <dsp:droplet name="ForEach">
                                                            <dsp:param name="array" value="${gender}"/>
                                                            <dsp:param name="elementName" value="gender" />
                                                            <dsp:param name="sortProperties" value="+_key" />
                                                            <dsp:oparam name="output">
															<dsp:getvalueof var="fullGenderKey" param="key"/>
                                                               
                                                                <dsp:droplet name="/com/bbb/commerce/giftregistry/droplet/GetGenderKeyDroplet">
                                                            		<dsp:param name="genderKey" param="key"/>
                                                            		<dsp:oparam name="output">
                                                            			<dsp:getvalueof var="genderCode" param="genderCode" />
                                                            			                                                            																						
																		<c:choose>
																			<c:when test="${regVO ne null}">
																				<c:choose>
																				<c:when test="${empty regVOBabyGender}">
																					<c:choose>
																					<c:when test="${fullGenderKey eq '1_S'}">
				                                                                        <dsp:option paramvalue="genderCode" selected="true">
				                                                                               <dsp:valueof param="gender" />
				                                                                         </dsp:option>
				                                                                    </c:when>
				                                                                    <c:otherwise>
				                                                                        <dsp:option paramvalue="genderCode">
				                                                                            <dsp:valueof param="gender" />
				                                                                        </dsp:option>
				                                                                    </c:otherwise>	
																					</c:choose>
																				</c:when>
																				<c:otherwise>
																					<c:choose>
					                                                                    <c:when test="${regVO.event.babyGender eq genderCode}">
					                                                                        <dsp:option paramvalue="genderCode" selected="true">
					                                                                               <dsp:valueof param="gender" />
					                                                                         </dsp:option>
					                                                                    </c:when>
					                                                                    <c:otherwise>
					                                                                        <dsp:option paramvalue="genderCode">
					                                                                            <dsp:valueof param="gender" />
					                                                                        </dsp:option>
					                                                                    </c:otherwise>
					                                                                </c:choose>																
																				</c:otherwise>
																				</c:choose>
																			</c:when>
																			<c:otherwise>
																			        <c:choose>
																					<c:when test="${fullGenderKey eq '1_S'}">
				                                                                        <dsp:option paramvalue="genderCode" selected="true">
				                                                                               <dsp:valueof param="gender" />
				                                                                         </dsp:option>
				                                                                    </c:when>
				                                                                    <c:otherwise>
				                                                                        <dsp:option paramvalue="genderCode">
				                                                                            <dsp:valueof param="gender" />
				                                                                        </dsp:option>
				                                                                    </c:otherwise>	
																					</c:choose>
		                                                                	</c:otherwise>
																		</c:choose>	
                                                            		</dsp:oparam>		    
                                                                </dsp:droplet>
                                                               
                                                            </dsp:oparam>
                                                        </dsp:droplet>
                                                    </dsp:select>
                                                </dsp:oparam>
                                            </dsp:droplet>
                                        </div>
                                    </div>
                                </div>
                                <div class="inputField clearfix" id="babyName">
                                    <div class="label">
                                        <label id="lbltxtRegistryBabyName" for="txtRegistryBabyName">
                                          <bbbl:label key="lbl_event_baby_name" language="${pageContext.request.locale.language}"/>
                                        </label>
                                    </div>
                                    <div class="text">
                                        <div class="grid_2 alpha">
                                            <dsp:input tabindex="4" 
                                                bean="GiftRegistryFormHandler.registryVO.event.babyName"
                                                type="text" id="txtRegistryBabyName" value="${regVO.event.babyName}" iclass="cannotStartWithSpecialChars cannotStartWithNumber" maxlength="30" >
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryBabyName errortxtRegistryBabyName"/>
                                            </dsp:input>
                                        </div>
                                    </div>
                                </div>
                                <div class="inputField clearfix" id="babyNurseryTheme">
                                    <div class="label">
                                        <label for="txtRegistryTheme">
                                           <bbbl:label key="lbl_event_theme" language="${pageContext.request.locale.language}"/>
                                        </label>
                                    </div>
                                    <div class="textarea">
                                        <div class="grid_4 alpha">
                                      
                                            <dsp:textarea tabindex="5" 
                                                bean="GiftRegistryFormHandler.registryVO.event.babyNurseryTheme" id="txtRegistryTheme"><c:out value="${regVO.event.babyNurseryTheme}" /></dsp:textarea>
                                        </div>
                                    </div>
                                   
                                </div>
                                <%-- Step 1 baby registry hidden fields --%>
                                 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.college" type="hidden"  value="" />
                      			 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.guestCount" type="hidden"  name="txtRegistryNumberOfGuestsName" value="" />
                      			 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.birthDate"  type="hidden" name="txtBirthdayDateName" value="" />
                                <div class="clear"></div>
                            </div>
                            <div class="clear"></div>
                            <c:if test="${empty regVO}">
                                <div class="input submit small pushDown">
                                    <div class="button">
                                        <input tabindex="6" id="step1FormPostButton" type="button" name="" value="<bbbl:label key="lbl_event_next" language="${pageContext.request.locale.language}"/>" role="button" aria-pressed="false" />
                                    </div>
                                    <div class="clear"></div>
                                </div>
                        	</c:if>
                        </fieldset>
                        <div class="grid_4 omega clearfix marLeft_20">
                            <div class="registryNotice">
                                <bbbt:textArea key="txt_event_congrats" language="${pageContext.request.locale.language}"/>
                            </div>
                        </div>
                        
                        
                        <div class="clear"></div>                        
                    </div>
                    <div class="clear"></div>
                </div>
            </c:when>
            <c:when test="${event == 'COL'}">
                <div id="step1Preview" class="grid_12 alpha omega clearfix">
                    <div class="grid_12 alpha omega clearfix">
                        <div class="clearfix regsitryItem">
							<h3>
								<span class="regsitryItemNumber">1</span>
								<c:choose>
								<c:when test="${empty regVO}">
									<span class="regsitryItemTitle arrowOpenSmall"><bbbl:label key="lbl_regcreate_tell_us_abt_event" language="${pageContext.request.locale.language}"/> <a href="#edit1" id="step1EditLink" class="editRegistryLink" title='<bbbl:label key="lbl_regcreate_edit" language="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_regcreate_edit" language="${pageContext.request.locale.language}"/></a></span>
							 
								</c:when>
								<c:otherwise>
								<span class="regsitryItemTitle arrowClosedSmall"><bbbl:label key="lbl_regcreate_tell_us_abt_event" language="${pageContext.request.locale.language}"/> <a href="#edit1" id="step1EditLink" class="editRegistryLink" title='<bbbl:label key="lbl_regcreate_edit" language="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_regcreate_edit" language="${pageContext.request.locale.language}"/></a></span>
								</c:otherwise>
								</c:choose>
							</h3>
                        </div>
                    </div>
                    <div class="clear"></div>
                    <div id="step1Information" class="grid_12 alpha omega stepInformation form clearfix">
                        <div class="grid_2 alpha pushDown">
                            <div class="entry">
                                <div class="field"><bbbl:label key="lbl_event_graduation_date" language ="${pageContext.request.locale.language}"/></div>
                                <div class="value" id="valRegistryExpectedDate"></div>
                            </div>
                        </div>
                        <div class="grid_4 pushDown">
                            <div class="entry">
                                <div class="field"><bbbl:label key="lbl_event_college" language ="${pageContext.request.locale.language}"/></div>
                                <div class="value" id="valRegistryBabyName"></div>
                            </div>
                        </div>
                        <div class="clear"></div>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
                <div id="step1Form" class="grid_12 alpha omega clearfix">
                    <div id="step1FormPost" class="grid_12 alpha omega form post clearfix padTop_10">
                        <fieldset class="grid_8 alpha clearfix">
                            <legend class="formTitle offScreen"><bbbl:label key="lbl_event_info_title" language ="${pageContext.request.locale.language}"/></legend>
                            <div class="registryForm">
                                <div class="inputField clearfix" id="eventDate" >
                                    <div class="label">
                                        <label id="lbltxtRegistryExpectedDate${CADate}" for="txtRegistryExpectedDate${CADate}">
                                            <bbbl:label key="lbl_event_graduation_date" language ="${pageContext.request.locale.language}"/>
                                            <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
                                        </label>
                                    </div>
                                    <div class="text">
                                        <div class="grid_2 alpha" aria-live="assertive">
                                            <dsp:input tabindex="1" 
                                                bean="GiftRegistryFormHandler.registryVO.event.eventDate"
                                                type="text" id="txtRegistryExpectedDate${CADate}"  name="txtRegistryExpectedDateName" value="${regVO.event.eventDate}" iclass="required step1FocusField">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryExpectedDate${CADate} errortxtRegistryExpectedDate${CADate}"/>
                                            </dsp:input>
                                        </div>
                                        <div class="grid_1 alpha omega">
                                            <div id="registryExpectedDateButton" class="calendar"></div>
                                        </div>
                                    </div>
                                </div>
                                <div class="inputField clearfix" id="college">
                                    <div class="label">
                                        <label id="lbltxtRegistryCollegeName" for="txtRegistryCollegeName">
                                            <bbbl:label key="lbl_event_college" language ="${pageContext.request.locale.language}"/>
                                            <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
                                        </label>
                                    </div>
                                    <div class="text">
                                        <div class="grid_2 alpha">
                                            <dsp:input tabindex="2" 
                                                bean="GiftRegistryFormHandler.registryVO.event.college"
                                                type="text" id="txtRegistryCollegeName" value="${regVO.event.college}" iclass="required cannotStartWithSpecialChars alphabasicpuncwithcomma cannotStartWithNumber" maxlength="30"  priority="1" >
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryCollegeName errortxtRegistryCollegeName"/>
                                            </dsp:input>
                                        </div>
                                    </div>
                                    
                                </div>
                               <%-- Step 1 COLLEGE registry hidden fields --%>
                             <dsp:input bean="GiftRegistryFormHandler.registryVO.event.babyName"  type="hidden"  value="" />
                      		 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.guestCount" type="hidden"  name="txtRegistryNumberOfGuestsName" value="" />
							 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.birthDate"  type="hidden"   name="txtBirthdayDateName" value="" />
							 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.showerDate" type="hidden"  name="txtShowerDateName" value=""/>
							 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.babyNurseryTheme" type="hidden"  value=""/>
                                <div class="clear"></div>
                            </div>
                            <div class="clear"></div>
                             <c:if test="${empty regVO}">
								<div class="input submit small pushDown">
                                    <div class="button">
                                        <input tabindex="3" id="step1FormPostButton" type="button" name="" value="<bbbl:label key="lbl_event_next" language="${pageContext.request.locale.language}"/>" role="button" aria-pressed="false" aria-labelledby="step1FormPostButton" />
                                    </div>
                                    <div class="clear"></div>
	                        	</div>
                            </c:if>
                        </fieldset>
                        <div class="grid_4 omega clearfix marLeft_20">
                            <div class="registryNotice">
                                <bbbt:textArea key="txt_event_congrats" language="${pageContext.request.locale.language}"/>
                            </div>
                        </div>
                        <div class="clear"></div>                        
                    </div>
                    <div class="clear"></div>
                </div>
            </c:when>
            <c:when test="${event == 'BRD' || event == 'COM'}">
                <div id="step1Preview" class="grid_12 alpha omega clearfix">
                    <div class="grid_12 alpha omega clearfix">
                        <div class="clearfix regsitryItem">
							<h3>
								<span class="regsitryItemNumber">1</span>
								<c:choose>
								<c:when test="${empty regVO}">
									<span class="regsitryItemTitle arrowOpenSmall"><bbbl:label key="lbl_regcreate_tell_us_abt_event" language="${pageContext.request.locale.language}"/> <a href="#edit1" id="step1EditLink" class="editRegistryLink" title='<bbbl:label key="lbl_regcreate_edit" language="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_regcreate_edit" language="${pageContext.request.locale.language}"/></a></span>
							 
								</c:when>
								<c:otherwise>
								<span class="regsitryItemTitle arrowClosedSmall"><bbbl:label key="lbl_regcreate_tell_us_abt_event" language="${pageContext.request.locale.language}"/> <a href="#edit1" id="step1EditLink" class="editRegistryLink" title='<bbbl:label key="lbl_regcreate_edit" language="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_regcreate_edit" language="${pageContext.request.locale.language}"/></a></span>
								</c:otherwise>
								</c:choose>
							</h3>
                        </div>
                    </div>
                    <div class="clear"></div>
                    <div id="step1Information" class="grid_12 alpha omega stepInformation form clearfix">
                        <div class="grid_2 alpha pushDown">
                            <div class="entry">
                                <div class="field"><bbbl:label key="lbl_event_eventdate" language ="${pageContext.request.locale.language}"/></div>
                                <div class="value" id="valRegistryExpectedDate"></div>
                            </div>
                        </div>
                        <div class="grid_2 pushDown">
                            <div class="entry">
                                <div class="field"><bbbl:label key="lbl_event_num_of_guests" language ="${pageContext.request.locale.language}"/></div>
                                <div class="value" id="valRegistryNumberOfGuests"></div>
                            </div>
                        </div>
                        <div class="grid_2 pushDown">
                            <div class="entry">
                                <div class="field"><bbbl:label key="lbl_event_shower_date" language ="${pageContext.request.locale.language}"/></div>
                                <div class="value" id="valRegistryDate"></div>
                            </div>
                        </div>
                        <div class="clear"></div>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
                <div id="step1Form" class="grid_12 alpha omega clearfix">
                    <div id="step1FormPost" class="grid_12 alpha omega form post clearfix padTop_10">
                        <fieldset class="grid_8 alpha clearfix">
                            <legend class="formTitle offScreen"><bbbl:label key="lbl_event_info_title" language ="${pageContext.request.locale.language}"/></legend>
                            <div class="registryForm">
                                <div class="inputField clearfix" id="eventDate" >
                                    <div class="label">
                                        <label id="lbltxtRegistryExpectedDate${CADate}" for="txtRegistryExpectedDate${CADate}">
                                            <bbbl:label key="lbl_event_eventdate" language ="${pageContext.request.locale.language}"/>
                                            <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
                                        </label>
                                    </div>
                                    <div class="text">
                                        <div class="grid_2 alpha" aria-live="assertive">
                                            <dsp:input tabindex="1" 
                                                bean="GiftRegistryFormHandler.registryVO.event.eventDate"
                                                type="text" id="txtRegistryExpectedDate${CADate}"  name="txtRegistryExpectedDateName" value="${regVO.event.eventDate}" iclass="required step1FocusField">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryExpectedDate${CADate} errortxtRegistryExpectedDate${CADate}"/>
                                            </dsp:input>
                                        </div>
                                        <div class="grid_1 alpha omega">
                                            <div id="registryExpectedDateButton" class="calendar"></div>
                                        </div>
                                    </div>
                                </div>
                                <div class="inputField clearfix" id="guestCount" >
                                    <div class="label">
                                        <label id="lbltxtRegistryNumberOfGuests" for="txtRegistryNumberOfGuests">
                                            <bbbl:label key="lbl_event_num_of_guests" language ="${pageContext.request.locale.language}"/>
                                            <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
                                        </label>
                                    </div>
                                    <div class="text">
                                        <div class="grid_2 alpha">
                                        <dsp:getvalueof var="guestCount" value="${regVO.event.guestCount}"/>
                                        	<c:if test="${regVO.event.guestCount eq '0'}">
                                        		<dsp:getvalueof var="guestCount" value=""/>
                                        	</c:if>
                                            <dsp:input tabindex="2" 
                                                bean="GiftRegistryFormHandler.registryVO.event.guestCount" iclass="required digits"
                                                type="text" id="txtRegistryNumberOfGuests" name="txtRegistryNumberOfGuestsName" value="${guestCount}" maxlength="4" priority="1">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryNumberOfGuests errortxtRegistryNumberOfGuests"/>
                                            </dsp:input>
                                        </div>
                                    </div>
                                </div>
                                <div class="inputField clearfix" id="showerDate">
                                    <div class="label">
                                        <label id="lbltxtRegistryDate${CADate}" for="txtRegistryDate">
                                           <bbbl:label key="lbl_event_shower_date" language ="${pageContext.request.locale.language}"/>
                                        </label>
                                    </div>
                                    <div class="text">
                                        <div class="grid_2 alpha">
                                            <dsp:input tabindex="3" bean="GiftRegistryFormHandler.registryVO.event.showerDate"
                                                type="text" id="txtRegistryDate${CADate}" name="txtShowerDateName" value="${regVO.event.showerDate}">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryDate${CADate} errortxtRegistryDate${CADate}"/>
                                            </dsp:input>
                                        </div>
                                        <div class="grid_1 alpha omega">
                                            <div id="registryDateButton" class="calendar"></div>
                                        </div>
                                    </div>
                                </div>
                                <%-- hidden fields for wedding and cermony registries --%>
                                 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.college" type="hidden"  value="" />
                                 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.birthDate"  type="hidden"   name="txtBirthdayDateName" value="" />
                                 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.babyNurseryTheme" type="hidden"  value=""/>
                                 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.babyName"  type="hidden" value="" />
                                <div class="clear"></div>
                            </div>
                            <div class="clear"></div>
                             <c:if test="${empty regVO}">
								<div class="input submit small pushDown">
                                    <div class="button">
                                        <input tabindex="4" id="step1FormPostButton" type="button" name="" value="<bbbl:label key="lbl_event_next" language="${pageContext.request.locale.language}"/>" role="button" aria-pressed="false" aria-labelledby="step1FormPostButton" />
                                    </div>
                                    <div class="clear"></div>
	                        	</div>
	                        </c:if>
                        </fieldset>
                        <div class="grid_4 omega clearfix marLeft_20">
                            <div class="registryNotice">
                                <bbbt:textArea key="txt_event_congrats" language="${pageContext.request.locale.language}"/>
                            </div>
                        </div>
                        <div class="clear"></div>                        
                    </div>
                    <div class="clear"></div>
                </div>
            </c:when>
            <c:when test="${event == 'BIR'|| event== 'BR1' || event == 'ANN'}">
                <div id="step1Preview" class="grid_12 alpha omega clearfix">
                    <div class="grid_12 alpha omega clearfix">
                        <div class="clearfix regsitryItem">
							<h3>
								<span class="regsitryItemNumber">1</span>
								<c:choose>
								<c:when test="${empty regVO}">
									<span class="regsitryItemTitle arrowOpenSmall"><bbbl:label key="lbl_regcreate_tell_us_abt_event" language="${pageContext.request.locale.language}"/> <a href="#edit1" id="step1EditLink" class="editRegistryLink" title='<bbbl:label key="lbl_regcreate_edit" language="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_regcreate_edit" language="${pageContext.request.locale.language}"/></a></span>
							 
								</c:when>
								<c:otherwise>
								<span class="regsitryItemTitle arrowClosedSmall"><bbbl:label key="lbl_regcreate_tell_us_abt_event" language="${pageContext.request.locale.language}"/> <a href="#edit1" id="step1EditLink" class="editRegistryLink" title='<bbbl:label key="lbl_regcreate_edit" language="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_regcreate_edit" language="${pageContext.request.locale.language}"/></a></span>
								</c:otherwise>
								</c:choose>
							</h3>
                        </div>
                    </div>
                    <div class="clear"></div>
                    <div id="step1Information" class="grid_12 alpha omega stepInformation form clearfix">

                        <div class="grid_2 <c:if test="${siteId != 'BuyBuyBaby'}"> alpha </c:if> pushDown">
                            <div class="entry">
                                <div class="field"><bbbl:label key="lbl_regsrchguest_EventDate" language ="${pageContext.request.locale.language}"/></div>
                                <c:choose>
                                    <c:when test="${siteId == 'BuyBuyBaby'}">
                                        <div class="value" id="valRegistryExpectedDate"></div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="value" id="valRegistryExpectedDate"></div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <c:if test="${siteId == 'BuyBuyBaby'}">
                            <div class="grid_2 alpha pushDown">
                                <div class="entry">
                                    <div class="field">
                                      	<bbbl:label key="lbl_regsrchguest_BirthDate" language ="${pageContext.request.locale.language}"/>
	                                </div>
                                    <div class="value" id="valBirthdayDate"></div>
                                </div>
                            </div>
                        </c:if>
                        <c:if test="${siteId != 'BuyBuyBaby'}">
                            <div class="grid_2 pushDown">
                                <div class="entry">
                                    <div class="field"><bbbl:label key="lbl_event_num_of_guests" language ="${pageContext.request.locale.language}"/></div>
                                    <div class="value" id="valRegistryNumberOfGuests"></div>
                                </div>
                             </div>
                        </c:if>
                        <div class="clear"></div>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
                <div id="step1Form" class="grid_12 alpha omega clearfix">
                    <div id="step1FormPost" class="grid_12 alpha omega form post clearfix padTop_10">
                        <fieldset class="grid_8 alpha clearfix">
                            <legend class="formTitle offScreen"><bbbl:label key="lbl_event_info_title" language ="${pageContext.request.locale.language}"/></legend>
                            <div class="registryForm">
                                <div class="inputField clearfix" id="eventDate" >
                                    <div class="label">
                                        <label id="lbltxtRegistryExpectedDate${CADate}" for="txtRegistryExpectedDate${CADate}">
                                            <bbbl:label key="lbl_regsrchguest_EventDate" language ="${pageContext.request.locale.language}"/>
                                            <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
                                        </label>
                                    </div>
                                    <div class="text">
                                        <div class="grid_2 alpha" aria-live="assertive">
                                            <dsp:input tabindex="1" 
                                                bean="GiftRegistryFormHandler.registryVO.event.eventDate"
                                                type="text" id="txtRegistryExpectedDate${CADate}"  name="txtRegistryExpectedDateName" value="${regVO.event.eventDate}" iclass="required step1FocusField">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryExpectedDate${CADate} errortxtRegistryExpectedDate${CADate}"/>
                                            </dsp:input>
                                        </div>
                                        <div class="grid_1 alpha omega">
                                            <div id="registryExpectedDateButton" class="calendar"></div>
                                        </div>
                                    </div>
                                </div>
                                <c:if test="${siteId == 'BuyBuyBaby'}">
                                    <div id="birthdayDate" class="inputField clearfix">
                                        <div class="label">
                                            <label id="lbltxtBirthdayDate${CADate}" for="txtBirthdayDate">
                                            <bbbl:label key="lbl_regsrchguest_BirthDate" language ="${pageContext.request.locale.language}"/>
                                            </label>
                                        </div>
                                        <div class="text">
                                            <div class="grid_2 alpha">
                                                <dsp:input tabindex="2" 
                                                    bean="GiftRegistryFormHandler.registryVO.event.birthDate"
                                                    type="text"id="txtBirthdayDate${CADate}"  name="txtBirthdayDateName" value="${regVO.event.birthDate}" >
                                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtBirthdayDate${CADate} errortxtBirthdayDate${CADate}"/>
                                                </dsp:input>
                                            </div>
                                            <div class="grid_1 alpha omega">
                                                <div class="calendar" id="birthdayDateButton"></div>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                                <c:if test="${siteId ne 'BuyBuyBaby'}">
                                    <div class="inputField clearfix" id="guestCount" >
                                        <div class="label">
                                            <label id="lbltxtRegistryNumberOfGuests" for="txtRegistryNumberOfGuests">
                                                 <bbbl:label key="lbl_event_num_of_guests" language ="${pageContext.request.locale.language}"/>
                                            </label>
                                        </div>
                                        <div class="text">
                                            <div class="grid_2 alpha">
                                            <dsp:getvalueof var="guestCount" value="${regVO.event.guestCount}"/>
                                        	<c:if test="${regVO.event.guestCount eq '0'}">
                                        		<dsp:getvalueof var="guestCount" value=""/>
                                        	</c:if>
                                                <dsp:input tabindex="3" 
                                                    bean="GiftRegistryFormHandler.registryVO.event.guestCount" iclass="digits"
                                                    type="text" id="txtRegistryNumberOfGuests" name="txtRegistryNumberOfGuestsName" value="${guestCount}" maxlength="4">
                                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryNumberOfGuests errortxtRegistryNumberOfGuests"/>
                                                </dsp:input>
                                            </div>
                                        </div>
                                    </div>
                                 </c:if>
                                 <c:choose>
                                  <c:when test="${event== 'BR1'}">
                                  <%-- hidden fields for birthday registries on Baby site --%>
                                 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.college" type="hidden"  value="" />
                                 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.guestCount" type="hidden"  name="txtRegistryNumberOfGuestsName" value="" />
								 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.babyNurseryTheme" type="hidden"  value=""/>
								 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.babyName"  type="hidden" value="" />
								 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.showerDate" type="hidden" name="txtShowerDateName" value=""/> 
								</c:when>
								<c:otherwise>
								<%-- hidden fields for Anniversary and birthday registry for Canada and bedbath site --%>
                                 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.college" type="hidden"  value="" />
								<dsp:input bean="GiftRegistryFormHandler.registryVO.event.birthDate"  type="hidden"   name="txtBirthdayDateName" value="" />
								 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.babyNurseryTheme" type="hidden"  value=""/>
								 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.babyName"  type="hidden" value="" />
								 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.showerDate" type="hidden"  name="txtShowerDateName" value=""/>
 
								</c:otherwise>
								</c:choose>
                                <div class="clear"></div>
                            </div>
                            <div class="clear"></div>
                             <c:if test="${empty regVO}">
								<div class="input submit small pushDown">
                                    <div class="button">
                                        <input tabindex="4" id="step1FormPostButton" type="button" name="" value="<bbbl:label key="lbl_event_next" language="${pageContext.request.locale.language}"/>" role="button" aria-pressed="false" aria-labelledby="step1FormPostButton" />
                                    </div>
                                    <div class="clear"></div>
	                        	</div>
	                        </c:if>
                        </fieldset>
                        <div class="grid_4 omega clearfix marLeft_20">
                            <div class="registryNotice">
                                <bbbt:textArea key="txt_event_congrats" language="${pageContext.request.locale.language}"/>
                            </div>
                        </div>
                        <div class="clear"></div>                        
                    </div>
                    <div class="clear"></div>
                </div>
            </c:when>
            <c:when test="${event== 'HSW' || event== 'RET'}">
                <div id="step1Preview" class="grid_12 alpha omega clearfix">
                    <div class="grid_12 alpha omega clearfix">
                        <div class="clearfix regsitryItem">
							<h3>
								<span class="regsitryItemNumber">1</span>
								<c:choose>
								<c:when test="${empty regVO}">
									<span class="regsitryItemTitle arrowOpenSmall"><bbbl:label key="lbl_regcreate_tell_us_abt_event" language="${pageContext.request.locale.language}"/> <a href="#edit1" id="step1EditLink" class="editRegistryLink" title='<bbbl:label key="lbl_regcreate_edit" language="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_regcreate_edit" language="${pageContext.request.locale.language}"/></a></span>
							 
								</c:when>
								<c:otherwise>
								<span class="regsitryItemTitle arrowClosedSmall"><bbbl:label key="lbl_regcreate_tell_us_abt_event" language="${pageContext.request.locale.language}"/> <a href="#edit1" id="step1EditLink" class="editRegistryLink" title='<bbbl:label key="lbl_regcreate_edit" language="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_regcreate_edit" language="${pageContext.request.locale.language}"/></a></span>
								</c:otherwise>
								</c:choose>
							</h3>
                        </div>
                    </div>
                    <div class="clear"></div>
                    <div id="step1Information" class="grid_12 alpha omega stepInformation form clearfix">
                        <div class="grid_2 alpha pushDown">
                            <div class="entry">
                                <div class="field"><bbbl:label key="lbl_regsrchguest_EventDate" language ="${pageContext.request.locale.language}"/></div>
                                <div class="value" id="valRegistryExpectedDate"></div>
                            </div>
                        </div>
                        <div class="grid_2 pushDown">
                            <div class="entry">
                                <div class="field"><bbbl:label key="lbl_event_num_of_guests" language ="${pageContext.request.locale.language}"/></div>
                                <div class="value" id="valRegistryNumberOfGuests"></div>
                            </div>
                        </div>
                        <div class="clear"></div>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
                <div id="step1Form" class="grid_12 alpha omega clearfix">
                    <div id="step1FormPost" class="grid_12 alpha omega form post clearfix padTop_10">
                        <fieldset class="grid_8 alpha clearfix">
                            <legend class="formTitle offScreen"><bbbl:label key="lbl_event_info_title" language ="${pageContext.request.locale.language}"/></legend>
                            <div class="registryForm">
                                <div class="inputField clearfix" id="eventDate" >
                                    <div class="label">
                                        <label id="lbltxtRegistryExpectedDate${CADate}" for="txtRegistryExpectedDate${CADate}">
                                            <bbbl:label key="lbl_regsrchguest_EventDate" language ="${pageContext.request.locale.language}"/>
                                            <span class="required"><bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span>
                                        </label>
                                    </div>
                                    <div class="text">
                                        <div class="grid_2 alpha" aria-live="assertive">
                                            <dsp:input tabindex="1" 
                                                bean="GiftRegistryFormHandler.registryVO.event.eventDate"
                                                type="text" id="txtRegistryExpectedDate${CADate}"  name="txtRegistryExpectedDateName" value="${regVO.event.eventDate}" iclass="required step1FocusField">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryExpectedDate${CADate} errortxtRegistryExpectedDate${CADate}"/>
                                            </dsp:input>
                                        </div>
                                        <div class="grid_1 alpha omega">
                                            <div id="registryExpectedDateButton" class="calendar"></div>
                                        </div>
                                    </div>
                                </div>
                                <div class="inputField clearfix" id="guestCount" >
                                    <div class="label">
                                        <label id="lbltxtRegistryNumberOfGuests" for="txtRegistryNumberOfGuests">
                                            <bbbl:label key="lbl_event_num_of_guests" language ="${pageContext.request.locale.language}"/>
                                        </label>
                                    </div>
                                    <div class="text">
                                        <div class="grid_2 alpha">
                                        <dsp:getvalueof var="guestCount" value="${regVO.event.guestCount}"/>
                                        	<c:if test="${regVO.event.guestCount eq '0'}">
                                        		<dsp:getvalueof var="guestCount" value=""/>
                                        	</c:if>
                                            <dsp:input tabindex="2" 
                                                bean="GiftRegistryFormHandler.registryVO.event.guestCount" iclass="digits"
                                                type="text" id="txtRegistryNumberOfGuests" name="txtRegistryNumberOfGuestsName" value="${guestCount}" maxlength="4">
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryNumberOfGuests errortxtRegistryNumberOfGuests"/>
                                            </dsp:input>
                                        </div>
                                    </div>
                                </div>
                                <%-- hidden fields for House warmining  and retirement registry  --%>
                                 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.college" type="hidden"  value="" />
                                <dsp:input bean="GiftRegistryFormHandler.registryVO.event.birthDate"  type="hidden"   name="txtBirthdayDateName" value="" />
								 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.babyNurseryTheme" type="hidden"  value=""/>
								 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.babyName"  type="hidden" value="" />
								 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.showerDate" type="hidden"  name="txtShowerDateName" value=""/>
							<div class="clear"></div>
                            </div>
                            <div class="clear"></div>
                             <c:if test="${empty regVO}">
								<div class="input submit small pushDown">
                                    <div class="button">
                                        <input tabindex="3" id="step1FormPostButton" type="button" name="" value="<bbbl:label key="lbl_event_next" language="${pageContext.request.locale.language}"/>" role="button" aria-pressed="false" aria-labelledby="step1FormPostButton" />
                                    </div>
                                    <div class="clear"></div>
	                        	</div>
	                        </c:if>
                        </fieldset>
                        <div class="grid_4 omega clearfix marLeft_20">
                            <div class="registryNotice">
                                <bbbt:textArea key="txt_event_congrats" language="${pageContext.request.locale.language}"/>
                            </div>
                        </div>
                        <div class="clear"></div>                        
                    </div>
                    <div class="clear"></div>
                </div>
            </c:when>
            <c:otherwise>
                <div id="step1Preview" class="grid_12 alpha omega clearfix">
                    <div class="grid_12 alpha omega clearfix">
                        <div class="clearfix regsitryItem">
							<h3>
								<span class="regsitryItemNumber">1</span>
								<c:choose>
								<c:when test="${empty regVO}">
									<span class="regsitryItemTitle arrowOpenSmall"><bbbl:label key="lbl_regcreate_tell_us_abt_event" language="${pageContext.request.locale.language}"/> <a href="#edit1" id="step1EditLink" class="editRegistryLink" title='<bbbl:label key="lbl_regcreate_edit" language="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_regcreate_edit" language="${pageContext.request.locale.language}"/></a></span>
							 
								</c:when>
								<c:otherwise>
								<span class="regsitryItemTitle arrowClosedSmall"><bbbl:label key="lbl_regcreate_tell_us_abt_event" language="${pageContext.request.locale.language}"/> <a href="#edit1" id="step1EditLink" class="editRegistryLink" title='<bbbl:label key="lbl_regcreate_edit" language="${pageContext.request.locale.language}"/>'><bbbl:label key="lbl_regcreate_edit" language="${pageContext.request.locale.language}"/></a></span>
								</c:otherwise>
								</c:choose>
							</h3>
                        </div>
                    </div>
                    <div class="clear"></div>
                    <div id="step1Information" class="grid_12 alpha omega stepInformation form clearfix">
                        <div class="grid_2 alpha pushDown">
                            <div class="entry">
                                <div class="field"><bbbl:label key="lbl_regsrchguest_EventDate" language="${pageContext.request.locale.language}"/></div>
                                <c:choose>
                                    <c:when test="${siteId == 'BuyBuyBaby'}">
                                        <div class="value" id="valRegistryExpectedDate"></div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="value" id="valRegistryExpectedDate"></div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
						
                       
                        <c:if test="${siteId ne 'BuyBuyBaby'}">
	                       <div class="grid_2 pushDown">
	                            <div class="entry">
	                                <div class="field"><bbbl:label key="lbl_event_num_of_guests" language ="${pageContext.request.locale.language}"/></div>
	                                <div class="value" id="valRegistryNumberOfGuests"></div>
	                            </div>
	                        </div>
						</c:if>	                        
	                        
                        <div class="clear"></div>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="clear"></div>
                <div id="step1Form" class="grid_12 alpha omega clearfix">
                    <div id="step1FormPost" class="grid_12 alpha omega form post clearfix padTop_10">
                        <fieldset class="grid_8 alpha clearfix">
                            <legend class="formTitle offScreen"><bbbl:label key="lbl_event_info_title" language="${pageContext.request.locale.language}"/></legend>
                            <div class="registryForm">
                                <div class="inputField clearfix">
                                    <div class="label">
                                        <label id="lbltxtRegistryExpectedDate${CADate}" for="txtRegistryExpectedDate${CADate}"><bbbl:label key="lbl_regsrchguest_EventDate" language="${pageContext.request.locale.language}"/> <span class="required">*</span></label>
                                    </div>
                                    <div class="text">
                                        <div class="grid_2 alpha" aria-live="assertive">
                                            <dsp:input tabindex="1" 
                                                bean="GiftRegistryFormHandler.registryVO.event.eventDate"
                                                type="text" id="txtRegistryExpectedDate${CADate}"  name="txtRegistryExpectedDateName" value="${regVO.event.eventDate}" iclass="required step1FocusField">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryExpectedDate${CADate} errortxtRegistryExpectedDate${CADate}"/>
                                            </dsp:input>
                                        </div>
                                        <div class="grid_1 alpha omega">
                                            <div id="registryExpectedDateButton" class="calendar"></div>
                                        </div>
                                    </div>
                                </div>
                                 <c:if test="${siteId ne 'BuyBuyBaby'}">
                                <div class="inputField clearfix" id="guestCount" >
                                    <div class="label">
                                        <label id="lbltxtRegistryNumberOfGuests" for="txtRegistryNumberOfGuests">
                                            <bbbl:label key="lbl_event_num_of_guests" language ="${pageContext.request.locale.language}"/>
                                        </label>
                                    </div>
                                    <div class="text">
                                        <div class="grid_2 alpha">
                                        <dsp:getvalueof var="guestCount" value="${regVO.event.guestCount}"/>
                                        	<c:if test="${regVO.event.guestCount eq '0'}">
                                        		<dsp:getvalueof var="guestCount" value=""/>
                                        	</c:if>
                                            <dsp:input tabindex="2" 
                                                bean="GiftRegistryFormHandler.registryVO.event.guestCount" iclass="digits"
                                                type="text" id="txtRegistryNumberOfGuests" name="txtRegistryNumberOfGuestsName" value="${guestCount}" maxlength="4">
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryNumberOfGuests errortxtRegistryNumberOfGuests"/>
                                            </dsp:input>
                                        </div>
                                    </div>
                                </div>
                                </c:if>
                                <div class="clear"></div>
                            </div>
                             <c:choose>
                             <c:when test="${event== 'OTH'}">
                             <%-- hidden fields for Other type registries for Canada and bedbath site --%>
                              <dsp:input bean="GiftRegistryFormHandler.registryVO.event.college" type="hidden" value="" />
                             <dsp:input bean="GiftRegistryFormHandler.registryVO.event.birthDate"  type="hidden"   name="txtBirthdayDateName" value="" />
							 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.babyNurseryTheme" type="hidden"  value=""/>
							 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.babyName"  type="hidden" value="" />
							 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.showerDate" type="hidden"  name="txtShowerDateName" value=""/> 
								</c:when>
								<c:otherwise>
								<%-- hidden fields for Other type registries for baby site --%>
                                 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.college" type="hidden"  value="" />
                             	<dsp:input bean="GiftRegistryFormHandler.registryVO.event.guestCount" type="hidden"  name="txtRegistryNumberOfGuestsName" value="" />
								 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.birthDate"  type="hidden"   name="txtBirthdayDateName" value="" />
								 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.babyNurseryTheme" type="hidden"  value=""/>
								 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.babyName"  type="hidden" value="" />
								 <dsp:input bean="GiftRegistryFormHandler.registryVO.event.showerDate" type="hidden"  name="txtShowerDateName" value=""/> 
								</c:otherwise>
								</c:choose>
                            <div class="clear"></div>
                             <c:if test="${empty regVO}">
							<div class="input submit small pushDown">
                                    <div class="button">
                                        <input tabindex="3" id="step1FormPostButton" type="button" name="" value="<bbbl:label key="lbl_event_next" language="${pageContext.request.locale.language}"/>" role="button" aria-pressed="false" aria-labelledby="step1FormPostButton" />
                                    </div>
                                    <div class="clear"></div>
                            </div>
                        </c:if>
                        </fieldset>
                        <div class="grid_4 omega clearfix marLeft_20">
                            <div class="registryNotice">
                                <bbbt:textArea key="txt_event_congrats" language="${pageContext.request.locale.language}"/>
                            </div>
                        </div>
                        <div class="clear"></div>                        
                    </div>
                    <div class="clear"></div>
                </div>
            </c:otherwise>
        </c:choose>
        <div class="clear"></div>
    </div>
    <div class="clear"></div>
</dsp:page>
