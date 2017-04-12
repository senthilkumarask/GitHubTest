<dsp:page>    

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean var="Profile" bean="/atg/userprofiling/Profile"/>
<dsp:importbean var="SurveyFormHandler" bean="/com/bbb/selfservice/SurveyFormHandler"/>
<dsp:importbean bean="/com/bbb/selfservice/StateDroplet" />

<bbb:pageContainer>
    <jsp:attribute name="bodyClass">my-account</jsp:attribute>
    <jsp:attribute name="section">selfService</jsp:attribute>
    <jsp:attribute name="pageWrapper">survey</jsp:attribute>
    <jsp:body>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<c:set var ="surveyStatus" scope="request">false</c:set>
	<div id="content" class="container_12 clearfix" role="main">
		   <c:choose>
			<c:when test="${SurveyFormHandler.successMessage == false}" >
				<div class="grid_9 suffix_3">
					 <bbbt:textArea key="txtarea_survey_customersurveymessage" language ="${pageContext.request.locale.language}"/>
				</div>			
			 <dsp:form id="surveyForm"  action="survey.jsp" method="post" iclass="form post">

			    <dsp:include page="/global/gadgets/errorMessage.jsp">
			      <dsp:param name="formhandler" bean="SurveyFormHandler"/>
			    </dsp:include>
		
			 <!--	<dsp:input bean="SurveyFormHandler.surveySuccessURL" type="hidden" value="survey.jsp"/>
				<dsp:input bean="SurveyFormHandler.surveyErrorURL" type="hidden" value="survey.jsp"/> -->
				 <dsp:input bean="SurveyFormHandler.fromPage" type="hidden" value="survey" />
         	
                 
               
				<div class="grid_12">
					<label class="grid_12 alpha"><bbbl:label key="lbl_survey_sendemail" language ="${pageContext.request.locale.language}"/></label>
					
					<dsp:getvalueof id="emailRequired" bean="SurveyFormHandler.emailRequired"/>
					<c:choose>
					<c:when test="${emailRequired eq 'yes'}">
						<c:set var="emailSendYes" value="true"/>
						<c:set var="emailSendNo" value="false"/>
						<c:set var="hidenClass" value=""/>
					</c:when>
					<c:when test="${emailRequired eq 'no'}">
						<c:set var="emailSendYes" value="false"/>
						<c:set var="emailSendNo" value="true"/>
						<c:set var="hidenClass" value="style=display:none"/>
					</c:when>
					<c:otherwise>
						<c:set var="emailSendYes" value="true"/>
						<c:set var="emailSendNo" value="false"/>
						<c:set var="hidenClass" value=""/>
					</c:otherwise>
					</c:choose>
					<div class="marTop_10 updatedSiteOption">
					
						<dsp:input type="radio" bean="SurveyFormHandler.emailRequired" id="emailSendYes" name="emailSubscriptionOptBtn" value="yes" checked="${emailSendYes}">
                            <dsp:tagAttribute name="aria-checked" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblemailSendYes"/>
                        </dsp:input>
						<label id="lblemailSendYes" for="emailSendYes" class="marRight_10"><bbbl:label key="lbl_survey_yes" language ="${pageContext.request.locale.language}"/></label>
						
						<dsp:input type="radio" bean="SurveyFormHandler.emailRequired"  id="emailSendNo" name="emailSubscriptionOptBtn" value="no"  checked="${emailSendNo}">
                            <dsp:tagAttribute name="aria-checked" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblemailSendNo"/>
                        </dsp:input>
						<label id="lblemailSendNo" for="emailSendNo"><bbbl:label key="lbl_survey_no" language ="${pageContext.request.locale.language}"/></label>
						
			         </div>
			        <div class="input marLeft_20 marTop_10 fl" ${hidenClass}  id="emailUpdatedSite">
							<label id="lblemail" for="email" class="label fl"><bbbl:label key="lbl_survey_emailaddress" language ="${pageContext.request.locale.language}"/></label>
							<div class="grid_3 text marLeft_10">
							<c:choose>
							<c:when test="${Profile.transient == true}" >
						       <dsp:input bean="SurveyFormHandler.email" name="email" type="text" id="email">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
                                </dsp:input>
							</c:when>
							<c:otherwise>
							    <dsp:setvalue bean="SurveyFormHandler.email" beanvalue="Profile.email"/> 
						 		<dsp:input bean="SurveyFormHandler.email" name="email" type="text" id="email">
                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
                                </dsp:input>
							</c:otherwise>
						</c:choose>
						 </div>
				    </div>
				</div>
				
				
				 
				<div class="grid_12 input">
					<label class="input">
					<bbbl:label key="lbl_survey_haveyouever" language ="${pageContext.request.locale.language}"/></label>
					<div class="marTop_10">
						<dsp:input type="radio" bean="SurveyFormHandler.everShopped" id="shoppedYes" name="shoppedBefore" value="yes">
                            <dsp:tagAttribute name="aria-checked" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblshoppedYes"/>
                        </dsp:input>
						<label id="lblshoppedYes" for="shoppedYes" class="marRight_10"><bbbl:label key="lbl_survey_yes" language ="${pageContext.request.locale.language}"/></label>
						<dsp:input type="radio" bean="SurveyFormHandler.everShopped"  id="shoppedNo" name="shoppedBefore" value="no">
                            <dsp:tagAttribute name="aria-checked" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblshoppedNo"/>
                        </dsp:input>
						<label id="lblshoppedNo" for="shoppedNo"><bbbl:label key="lbl_survey_no" language ="${pageContext.request.locale.language}"/> </label>
					</div>
				</div>
				
				  
                 
              
                 
                    
				<div class="grid_12">
						<div class="input">
								<div class="label">
									<label for="featuresLike"><bbbl:label key="lbl_survey_whatfeatures" language ="${pageContext.request.locale.language}"/></label>
								</div>
								<div class="textarea">
									<div class="width_8">
										<dsp:textarea id="featuresLike" name="featuresLike" bean="SurveyFormHandler.featuresMesssage"></dsp:textarea>
									</div>
								</div>
								<div class="error"></div>
						</div>
				
						<div class="input">
							<div class="label">
								<label for="favoriteWeb">
								<bbbl:label key="lbl_survey_whatfavorite" language ="${pageContext.request.locale.language}"/></label>
							</div>
							<div class="textarea">
								<div class="width_8">
									<dsp:textarea id="favoriteWeb" name="favoriteWeb" bean="SurveyFormHandler.webSiteMessage"></dsp:textarea>
								</div>
							</div>
							<div class="error"></div>
						</div>
	
						<div class="input">
							<div class="label">
								<label for="txtEmailMessage"><bbbl:label key="lbl_survey_anything" language ="${pageContext.request.locale.language}"/></label>
							</div>
							<div class="textarea">
								<div class="width_8">
									<dsp:textarea id="txtEmailMessage" name="textArea1500" bean="SurveyFormHandler.otherMessage"></dsp:textarea>
								</div>
							</div>
							<div class="error"></div>
						</div>
					</div>
				
				<div class="grid_3 suffix_9">
					<div class="input">
						<div class="label">
							<label id="lblfirstNamesurvey" for="firstNamesurvey"><bbbl:label key="lbl_survey_name" language ="${pageContext.request.locale.language}"/></label>
						</div>
						<div class="text">
							<div class="width_3">
								<c:choose>
										<c:when test="${Profile.transient == true}" >
									       <dsp:input bean="SurveyFormHandler.userName" name="firstNamesurvey" id="firstNamesurvey" maxlength="60" type="text">
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblfirstNamesurvey errorfirstNamesurvey"/>
                                            </dsp:input>
										</c:when>
										<c:otherwise>
										    <c:set var="uName"><dsp:valueof bean="Profile.firstName"/> <dsp:valueof bean="Profile.lastName"/></c:set>
										    <dsp:setvalue bean="SurveyFormHandler.userName" value="${uName}"/> 
										 	<dsp:input bean="SurveyFormHandler.userName" name="firstNamesurvey" type="text">
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblfirstNamesurvey errorfirstNamesurvey"/>
                                            </dsp:input>
										</c:otherwise>
									</c:choose>
							</div>
							<div id="errorfirstNamesurvey" class="error"></div>
						</div>
					</div>
				 
				 
					
					<div class="input width_3">
							<label id="lblgender" class="block" for="gender"><bbbl:label key="lbl_survey_gender" language ="${pageContext.request.locale.language}"/></label>
						
						<dsp:select bean="SurveyFormHandler.selectedGender" id="gender" name="gender" iclass="uniform">
									<dsp:option value=""><bbbl:label key="lbl_survey_select_gender" language ="${pageContext.request.locale.language}"/></dsp:option>
							<dsp:droplet name="ForEach">
								<dsp:param name="array" bean="SurveyFormHandler.gender" />
								<dsp:oparam name="output">
									<dsp:getvalueof param="element" id="elementVal">
										<dsp:option value="${elementVal}">
											${elementVal}
										</dsp:option>
									</dsp:getvalueof>
								</dsp:oparam>
							</dsp:droplet>
                            <dsp:tagAttribute name="aria-required" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblgender errorgender"/>
						</dsp:select>
						
					</div>
				
					<div Class="input width_3">
							<label id="lblage" class="block" for="age"><bbbl:label key="lbl_survey_age" language ="${pageContext.request.locale.language}"/></label>
						
						<dsp:select bean="SurveyFormHandler.selectedAge" id="age" name="age" iclass="uniform">
									<dsp:option value=""><bbbl:label key="lbl_survey_select_age" language ="${pageContext.request.locale.language}"/></dsp:option>
							<dsp:droplet name="ForEach">
								<dsp:param name="array" bean="SurveyFormHandler.age" />
								<dsp:oparam name="output">
									<dsp:getvalueof param="element" id="elementVal">
										<dsp:option value="${elementVal}">
											${elementVal}
										</dsp:option>
									</dsp:getvalueof>
								</dsp:oparam>
							</dsp:droplet>
                            <dsp:tagAttribute name="aria-required" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblage errorage"/>
						</dsp:select>
						
					
					</div>
				
					<div Class="input width_3">
						<label id="lblselectLocation" class="block" for="selectLocation"><bbbl:label key="lbl_survey_location" language ="${pageContext.request.locale.language}"/></label>
					
									<dsp:select bean="SurveyFormHandler.location" id="selectLocation" name="select_location" iclass="uniform">
										<dsp:droplet name="StateDroplet">
											<dsp:oparam name="output">
											    <dsp:option value=""><bbbl:label key="lbl_survey_select_location" language ="${pageContext.request.locale.language}"/></dsp:option>
												<dsp:droplet name="ForEach">
													<dsp:param name="array" param="location" />
													<dsp:oparam name="output">
														<dsp:getvalueof param="element.stateName" id="elementVal">
														<dsp:option value="${elementVal}">
															${elementVal}
														</dsp:option>
														</dsp:getvalueof>
													</dsp:oparam>
												</dsp:droplet>
											</dsp:oparam>
										</dsp:droplet>
                                        <dsp:tagAttribute name="aria-required" value="false"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblselectLocation errorselectLocation"/>
									</dsp:select>
			
				
					</div>
					<div class="input width_3">
                        <div id="captchaDiv" class="clearfix marTop_10">
                            <div class="input clearfix">
                                <div class="label width_2">
                                    <label id="lblcaptchaAnswer" for="captchaAnswer"><bbbl:label key="lbl_captcha_answer" language ="${pageContext.request.locale.language}"/> <span class="required">*</span></label>
                                </div>
                                <div class="text width_3">
                                    <img width="237" height="100" src="<c:url value="/simpleCaptcha.png" />">
                                    <dsp:input type="text" name="captchaAnswer" id="captchaAnswer" value="" bean="SurveyFormHandler.captchaAnswer" iclass="marTop_10" autocomplete="off">
                                        <dsp:tagAttribute name="aria-required" value="false"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblcaptchaAnswer errorcaptchaAnswer"/>
                                    </dsp:input>
                                </div>
                            </div>
                        </div>
                    </div>
				</div>
				<div class="grid_3 suffix_9">
					<div class="button button_active formRow">
						<dsp:input type="submit" bean="SurveyFormHandler.requestInfo" value="SUBMIT">
                            <dsp:tagAttribute name="aria-pressed" value="false"/>
                            <dsp:tagAttribute name="role" value="button"/>
                        </dsp:input>
					</div>
				</div>
			 </dsp:form>
			 </c:when>
			 <c:otherwise>
				<div class="grid_9 suffix_3">
					<bbbt:textArea key="txtarea_survey_surveythanksmessage" language ="${pageContext.request.locale.language}"/>
					<dsp:setvalue bean="SurveyFormHandler.successMessage" value="false"/> 
					<c:set var ="surveyStatus" scope="request">true</c:set>
				</div>
			</c:otherwise>
		</c:choose>
</div>
</jsp:body>
</bbb:pageContainer>
<script type="text/javascript">
				if (typeof s !== 'undefined') {
			
					var surveyStatus ='${surveyStatus}';	
					var surveyPageName='Content Page>Customer Survey';
					if (surveyStatus =='true')
						{
					surveyPageName +=' Confirmation';
						}
					s.pageName= surveyPageName;// pageName
					s.server="${pageContext.request.serverName}";
					s.channel="Company Info"
					s.prop1="Content Page"
					s.prop2="Content Page"
					s.prop3="Content Page"
					s.prop4="Company Info"
					s.prop5=""
					
					var s_code = s.t();
					if (s_code)
						document.write(s_code);
				}
			</script>
</dsp:page>