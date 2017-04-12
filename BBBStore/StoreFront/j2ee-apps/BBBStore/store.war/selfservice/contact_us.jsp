<dsp:page>    

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean var="Profile" bean="/atg/userprofiling/Profile"/>
<dsp:importbean var="ContactUsFormHandler" bean="/com/bbb/selfservice/ContactUsFormHandler"/>
<dsp:importbean bean="/com/bbb/selfservice/ContactUSCategoryDroplet" />
<dsp:importbean bean="/com/bbb/selfservice/ContactUSTimeZoneDroplet" />
<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
<dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />

<bbb:pageContainer>
    <jsp:attribute name="bodyClass">atg_store_pageHome</jsp:attribute>
    <jsp:attribute name="section">selfService</jsp:attribute>
    <jsp:attribute name="pageWrapper">contactUs useCaptcha</jsp:attribute>
    <jsp:attribute name="PageType">ContactUs</jsp:attribute>
    <c:set var ="contactConfirmPage" scope="request">false</c:set>
    <jsp:body>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	
	
			<div id="content" class="container_12 clearfix" role="main">
                <div class="grid_12">
                    <div class="grid_9 alpha">
                        <h1><bbbl:label key="lbl_contactus_contactus" language ="${pageContext.request.locale.language}"/></h1>
                        <h3><bbbl:label key="lbl_contactus_wehear" language ="${pageContext.request.locale.language}"/></h3>
                        <bbbt:textArea key="txtarea_contactus_pleasetells" language ="${pageContext.request.locale.language}"/>
                        <hr />
                    </div>
                    				
                </div>
				
				<div class="grid_12 clearfix">
					<div class="grid_2 alpha">
						<h5><bbbl:label key="lbl_contactus_viaemail" language ="${pageContext.request.locale.language}"/></h5>
					</div>
	
			<div class="grid_7" role="application">
		   
		   <c:choose>
		  
			<c:when test="${ContactUsFormHandler.successMessage == false }" >
				
			  <div>
			 	 <bbbt:textArea key="txtarea_contactus_customerservice" language ="${pageContext.request.locale.language}"/>
			  </div>
			  <dsp:form id="contactForm"  action="contact_us.jsp" method="post" iclass="form post">
			 	  <c:if test="${ContactUsFormHandler.captchaError eq false}">
				    <dsp:include page="/global/gadgets/errorMessage.jsp">
				      <dsp:param name="formhandler" bean="ContactUsFormHandler"/>
								</dsp:include>         
						</c:if>
				   

        
                <div class="input width_7">
							<div class="label">
								<label id="lblselectSubject" for="selectSubject"><bbbl:label key="lbl_contactus_subject" language ="${pageContext.request.locale.language}"/><span class="required"> <bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
							</div>
							<div class="select" aria-live="assertive" aria-atomic="true">
								<dsp:select bean="ContactUsFormHandler.subjectCategory" id="selectSubject" name="selectSubject" iclass="selector">
										<dsp:droplet name="ContactUSCategoryDroplet">
											<dsp:oparam name="output">
											    <dsp:option value=""><bbbl:label key="lbl_contactus_select_subject" language ="${pageContext.request.locale.language}"/></dsp:option>
												<dsp:droplet name="ForEach">
													<dsp:param name="array" param="subjectCategoryTypes" />
													<dsp:oparam name="output">
														<dsp:getvalueof param="element" id="elementVal">
														<dsp:option value="${elementVal}">
															${elementVal}
														</dsp:option>
														</dsp:getvalueof>
													</dsp:oparam>
												</dsp:droplet>
											</dsp:oparam>
										</dsp:droplet>
                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblselectSubject errorselectSubject"/>
									</dsp:select>
							</div>
						</div>
								
				
				<div class="input width_7">
					<c:choose>
						<c:when test="${currentSiteId == 'BedBathUS'}">
							<div class="label">
								<label id="lbltxtOrderNumber" for="txtOrderNumber"><bbbl:label key="lbl_contactus_ordernumber" language ="${pageContext.request.locale.language}"/></label>
							</div>
							<div class="text">
								<dsp:input bean="ContactUsFormHandler.orderNumber" name="txtOrderNumberUS" id="txtOrderNumber" type="text" maxlength="20">
                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtOrderNumber errortxtOrderNumber"/>
                                </dsp:input>
							</div>
						</c:when>
						<c:when test="${currentSiteId == 'BuyBuyBaby'}">
							<div class="label">
								<label id="lbltxtOrderNumber" for="txtOrderNumber"><bbbl:label key="lbl_contactus_ordernumber" language ="${pageContext.request.locale.language}"/></label>
							</div>
							<div class="text">
								<dsp:input bean="ContactUsFormHandler.orderNumber" name="txtOrderNumberBB" id="txtOrderNumber" type="text" maxlength="20">
                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtOrderNumber errortxtOrderNumber"/>
                                </dsp:input>
							</div>
						</c:when>
						<c:otherwise>
							<div class="label">
								<label id="lbltxtOrderNumber" for="txtOrderNumber"><bbbl:label key="lbl_contactus_ordernumber" language ="${pageContext.request.locale.language}"/></label>
							</div>
							<div class="text">
								<dsp:input bean="ContactUsFormHandler.orderNumber" name="txtOrderNumberCA" id="txtOrderNumber" type="text" maxlength="20">
                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbltxtOrderNumber errortxtOrderNumber"/>
                                </dsp:input>
							</div>
						</c:otherwise>
					</c:choose>
				</div>
				 
				
				
				
				<div class="input width_7">
						<div class="label">
							<label for="txtEmailMessage"><bbbl:label key="lbl_contactus_emailmessage" language ="${pageContext.request.locale.language}"/><span class="required"> <bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
						</div>
						<div class="textarea" aria-atomic="true">
							<dsp:textarea id="txtEmailMessage" name="txtEmailMessage" bean="ContactUsFormHandler.emailMessage" rows="5" ></dsp:textarea>
						</div>

					</div>
				
				
				 
				
				
				
				<div class="input grid_3 alpha">
						<div class="label">
							<label id="lblfirstName" for="firstName"><bbbl:label key="lbl_profile_firstname" language ="${pageContext.request.locale.language}"/><span class="required"> <bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span> </label>
						</div>

						<div class="text" aria-live="assertive" aria-atomic="true">        
								<c:choose>
							<c:when test="${Profile.transient == true}" >
						       <dsp:input bean="ContactUsFormHandler.firstName" name="firstName" type="text" id="firstName">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lblfirstName errorfirstName"/>
                                </dsp:input>
							</c:when>
							<c:otherwise>
							    <dsp:setvalue bean="ContactUsFormHandler.firstName" beanvalue="Profile.firstName"/> 
						 		<dsp:input bean="ContactUsFormHandler.firstName" name="firstName" type="text" id="firstName">
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lblfirstName errorfirstName"/>
                                </dsp:input>
							</c:otherwise>
						</c:choose>
						</div>
					</div>
						
		         
		         <div class="input grid_3 suffix_1 omega clearfix">
						<div class="label">
							<label id="lbllastName" for="lastName"><bbbl:label key="lbl_profile_lastname" language ="${pageContext.request.locale.language}"/><span class="required"> <bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
						</div>

					<div class="text width_3" aria-live="assertive" aria-atomic="true">        
						<c:choose>
							<c:when test="${Profile.transient == true}" >
						       <dsp:input bean="ContactUsFormHandler.lastName" name="lastName" type="text" id="lastName" >
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbllastName errorlastName"/>
                                </dsp:input>
							</c:when>
							<c:otherwise>
							    <dsp:setvalue bean="ContactUsFormHandler.lastName" beanvalue="Profile.lastName"/>
						 		<dsp:input bean="ContactUsFormHandler.lastName" name="lastName" type="text" id="lastName" >
                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbllastName errorlastName"/>
                                </dsp:input>
							</c:otherwise>
						</c:choose>
					</div>
  				</div>
				<div class="input width_7">
					<fieldset id="genderField" class="radioGroup width_7"><legend class="genderSetField"><bbbl:label key="lbl_contactus_gender" language ="${pageContext.request.locale.language}"/></legend>
					
					<dsp:getvalueof id="check" bean="ContactUsFormHandler.gender"/>
					<c:choose>
						<c:when test="${check eq 'male'}">
							<c:set var="male" value="true"/>
						</c:when>
						<c:when test="${check eq 'female'}">
							<c:set var="female" value="true"/>
						</c:when>
					</c:choose>
					<div class="radioItem input">
							
						<c:choose>
							<c:when test="${Profile.transient == true}" >
							  <div class="radio">
								 <dsp:input id="female" name="gender" type="radio" bean="ContactUsFormHandler.gender" value="female" checked="${female}">
                                    
                                    <dsp:tagAttribute name="aria-labelledby" value="lblfemale"/>
                                </dsp:input>
                              </div>
							<div class="label">	 
							  	 <label id="lblfemale" for="female" aria-hidden="true"><span class="visuallyhidden"><bbbl:label key="lbl_contactus_gender" language ="${pageContext.request.locale.language}"/></span> <bbbl:label key="lbl_contactus_female" language ="${pageContext.request.locale.language}"/></label>
							 </div> 	 
						  </c:when>
							<c:otherwise>
								<div class="radio">
									 <dsp:setvalue bean="ContactUsFormHandler.gender" beanvalue="Profile.gender"/> 
							  	 	<dsp:input id="female" name="gender" type="radio" bean="ContactUsFormHandler.gender" value="female" checked="${female}">
                                        
                                        <dsp:tagAttribute name="aria-labelledby" value="lblfemale"/>
                                    </dsp:input>
							   	 </div>
								<div class="label">	 
							   		<label id="lblfemale" for="female" aria-hidden="true"><span class="visuallyhidden"><bbbl:label key="lbl_contactus_gender" language ="${pageContext.request.locale.language}"/></span> <bbbl:label key="lbl_contactus_female" language ="${pageContext.request.locale.language}"/></label>
							   	</div>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${Profile.transient == true}" >
							   <div class="radio marLeft_10" tabindex="0">
							  	 <dsp:input id="male" name="gender" type="radio" bean="ContactUsFormHandler.gender" value="male" checked="${male}">
								 <span class="visuallyhidden">Gender(optional) Male</span>
                                    
                                    <dsp:tagAttribute name="aria-labelledby" value="lblmale"/>
                                </dsp:input>
							   </div>
							  <div class="label">
							    <label id="lblmale" for="male" aria-hidden="true"><span class="visuallyhidden"><bbbl:label key="lbl_contactus_gender" language ="${pageContext.request.locale.language}"/></span> <bbbl:label key="lbl_contactus_male" language ="${pageContext.request.locale.language}"/></label>
							  </div>
						  </c:when>
							<c:otherwise>
							    <div class="radio marLeft_10">
							    	<dsp:setvalue bean="ContactUsFormHandler.gender" beanvalue="Profile.gender"/> 
							 	  	<dsp:input id="male" name="gender" type="radio" bean="ContactUsFormHandler.gender" value="male" checked="${male}">
                                        
                                        <dsp:tagAttribute name="aria-labelledby" value="lblmale"/>
                                    </dsp:input>
							     </div>
							     <div class="label">
							     	<label id="lblmale" for="male" aria-hidden="true"><span class="visuallyhidden"><bbbl:label key="lbl_contactus_gender" language ="${pageContext.request.locale.language}"/></span> <bbbl:label key="lbl_contactus_male" language ="${pageContext.request.locale.language}"/></label>
							     </div>
							</c:otherwise>
						</c:choose>
				
					</div>
				</fieldset>
				</div>
		         <div class="input width_7">
		         <fieldset class="radioGroup width_7"><legend class="emailPhnField">
					
							<bbbl:label key="lbl_contactus_iwouldlike" language ="${pageContext.request.locale.language}"/><span class="required cust_required"> <bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></legend>
					
				
				<dsp:getvalueof id="contactType" bean="ContactUsFormHandler.contactType"/>
				<c:choose>
					<c:when test="${contactType eq 'email'}">
						<c:set var="email" value="true"/>
					</c:when>
					<c:when test="${contactType eq 'phone'}">
						<c:set var="phone" value="true"/>
					</c:when>
					<c:otherwise>
						<c:set var="email" value="true"/>
					</c:otherwise>
				</c:choose>
				<div class="radioItem input">
					<div class="radio">
						<dsp:input type="radio" bean="ContactUsFormHandler.contactType"  name="prefContactMethod" id="prefContactEmail" checked="${email}" value="email">
                            
                            <dsp:tagAttribute name="aria-labelledby" value="lblprefContactEmail"/>
                        </dsp:input>
					</div>

					<div class="label">
						<label id="lblprefContactEmail" for="prefContactEmail" aria-hidden="true"><span class="visuallyhidden"><bbbl:label key="lbl_contactus_iwouldlike" language ="${pageContext.request.locale.language}"/><span class="required"> <bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></span> <bbbl:label key="lbl_profilemain_email" language ="${pageContext.request.locale.language}"/> </label>
					</div>
				</div>

					<div class="radioItem input marLeft_10">
						<div class="radio" tabindex="0">
						<span class="visuallyhidden">I would like to be contacted by * Phone</span>
							<dsp:input type="radio" bean="ContactUsFormHandler.contactType" name="prefContactMethod" id="prefContactPhone" checked="${phone}" value="phone">
                                
                                <dsp:tagAttribute name="aria-labelledby" value="lblprefContactPhone"/>
                            </dsp:input>
						</div>
						<div class="label">
							<label id="lblprefContactPhone" for="prefContactPhone" aria-hidden="true"><span class="visuallyhidden"><bbbl:label key="lbl_contactus_iwouldlike" language ="${pageContext.request.locale.language}"/><span class="required"> <bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></span> <bbbl:label key="lbl_contactus_phone" language ="${pageContext.request.locale.language}"/></label>
						</div>
					</div>

			</fieldset>
				</div>		
						
		    	
			
			   <div class="input width_3 suffix_4 clearfix">
						<div class="label">
							<label id="lblcontactUsEmail" for="contactUsEmail"><bbbl:label key="lbl_profilemain_email" language ="${pageContext.request.locale.language}"/><span class="required"> <bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
						</div>
						<div class="text" aria-live="assertive" aria-atomic="true">
							<c:choose>
								<c:when test="${Profile.transient == true}" >
						       		<dsp:input bean="ContactUsFormHandler.email" name="contactUsEmail" id="contactUsEmail" iclass="required" type="email">
                                        <dsp:tagAttribute name="aria-checked" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblcontactUsEmail errorcontactUsEmail"/>
                                    </dsp:input>
								</c:when>
								<c:otherwise>
								    <dsp:setvalue bean="ContactUsFormHandler.email" beanvalue="Profile.email"/> 
							 		<dsp:input bean="ContactUsFormHandler.email" name="contactUsEmail" id="contactUsEmail" type="email">
                                        <dsp:tagAttribute name="aria-checked" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblcontactUsEmail"/>
                                    </dsp:input>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
					
			   	
				
				
				
				
				<div class="input width_3 suffix_4 omega clearfix">
						<div class="label">
							<label id="lblcontactUsEmailConfirm" for="contactUsEmailConfirm"><bbbl:label key="lbl_contactus_confirmemail" language ="${pageContext.request.locale.language}"/><span class="required"> <bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
						</div>
						<div class="text" aria-live="assertive" aria-atomic="true">
							<c:choose>
								<c:when test="${Profile.transient == true}" >
							      <dsp:input bean="ContactUsFormHandler.confirmEmail" name="contactUsEmailConfirm" id="contactUsEmailConfirm" iclass="required" type="email">
                                    <dsp:tagAttribute name="aria-checked" value="true"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lblcontactUsEmailConfirm errorcontactUsEmailConfirm"/>
                                </dsp:input>
								</c:when>
								<c:otherwise>
								    <dsp:setvalue bean="ContactUsFormHandler.confirmEmail" beanvalue="Profile.email"/> 
							 		<dsp:input bean="ContactUsFormHandler.confirmEmail" name="contactUsEmailConfirm" id="contactUsEmailConfirm" type="email">
                                        <dsp:tagAttribute name="aria-checked" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblcontactUsEmailConfirm errorcontactUsEmailConfirm"/>
                                    </dsp:input>
								</c:otherwise>
							</c:choose>
						</div>
				</div>
				
				
				
				
				<div class="input width_7 phoneFieldWrap clearfix">
                    <fieldset class="phoneFields"><legend class="phoneFieldLegend"><bbbl:label key='lbl_phonefield_phnumber' language='${pageContext.request.locale.language}'/></legend>
					<div class="phone text  fl">
						<div class="label">
							<label id="lblbasePhoneFull" for="basePhoneFull"><bbbl:label key="lbl_contactus_primaryphone" language ="${pageContext.request.locale.language}"/><span class="required hidden"> <bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
						</div>
													
						<div class="text fl">
							<input id="basePhoneFull" role="textbox" type="text" name="basePhoneFull" class="phoneField" maxlength="10" aria-required="false" aria-labelledby="lblbasePhoneFull errorbasePhoneFull" />
						</div>
					</div>
					<div class="phone fl surveyExt">
						<div class="label">
							<label id="lblbasePhoneExt" for="basePhoneExt"><bbbl:label key="lbl_contactus_phoneext" language ="${pageContext.request.locale.language}"/></label> 
						</div>						
						<div class="phone text">								
							<dsp:input bean="ContactUsFormHandler.phoneExt" name="basePhoneExt" id="basePhoneExt" maxlength="6" type="text">
                                <dsp:tagAttribute name="aria-checked" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lblbasePhoneExt errorbasePhoneExt"/>
                            </dsp:input>
						</div>
					</div>
                            
							<c:choose>
								<c:when test="${Profile.transient == true}" >
							       <dsp:input bean="ContactUsFormHandler.phoneNumber" name="phone" type="hidden" iclass="fullPhoneNum"/>
								</c:when>
								<c:otherwise>
								    <dsp:setvalue bean="ContactUsFormHandler.phoneNumber" beanvalue="Profile.phoneNumber"/> 
									<dsp:input bean="ContactUsFormHandler.phoneNumber" name="phone" type="hidden" iclass="fullPhoneNum"/>
								</c:otherwise>
						</c:choose>
                        </fieldset>
					</div>
				
				
				
				<div class="input width_7 clearfix">
                      <div id="timeZoneField" class="fl">
                         <div class="label marTop_5">
							<label id="lbltimeZone" for="timeZone"><bbbl:label key="lbl_contactus_timezone" language ="${pageContext.request.locale.language}"/><span class="required hidden"> <bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
						</div>
						<div class="select fl">
						    <dsp:select bean="ContactUsFormHandler.selectedTimeZone"  id="timeZone" name="timeZone" iclass="selector">
                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lbltimeZone errortimeZone"/>
										<dsp:droplet name="ContactUSTimeZoneDroplet">
											<dsp:oparam name="output">
											    <dsp:option value=""><bbbl:label key="lbl_contactus_select_timezone" language ="${pageContext.request.locale.language}"/></dsp:option>
												<dsp:droplet name="ForEach">
													<dsp:param name="array" param="timeZoneTypes" />
													<dsp:oparam name="output">
														<dsp:getvalueof param="element" id="elementVal">
														<dsp:option value="${elementVal}">
															${elementVal}
														</dsp:option>
														</dsp:getvalueof>
													</dsp:oparam>
												</dsp:droplet>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:select>
					</div>
				</div>
				
				
				<div id="timeToCallField" class="fl">
				<div class="label marTop_5">
					<label id="lbltimeCall" for="timeCall"><bbbl:label key="lbl_contactus_besttimetocall" language ="${pageContext.request.locale.language}"/><span class="required hidden"> <bbbl:label key="lbl_mandatory" language ="${pageContext.request.locale.language}"/></span></label>
					</div>
					<div class="select fl">
					<dsp:select bean="ContactUsFormHandler.selectedTimeCall" id="timeCall" name="timeCall" iclass="selector">
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbltimeCall errortimeCall"/>
                        <dsp:option value=""><bbbl:label key="lbl_contactus_select_timetocall" language ="${pageContext.request.locale.language}"/></dsp:option>
						<dsp:droplet name="ForEach">
							<dsp:param name="array" bean="ContactUsFormHandler.timeCall" />
							<dsp:oparam name="output">
								<dsp:getvalueof param="element" id="elementVal">
									<dsp:option value="${elementVal}">
										${elementVal}
									</dsp:option>
								</dsp:getvalueof>
							</dsp:oparam>
						</dsp:droplet>
					</dsp:select>
					</div>
					<fieldset class="radioGroup fl marTop_5"><legend class="amPmField">AM/PM</legend>
					<div id="lableAMPM" class="fl marLeft_20">
					<div class="radioItem input">
						<div class="radio">
							<dsp:input type="radio" bean="ContactUsFormHandler.amPM"  id="timeCall_am" name="amPM" value="am">
                                
                                <dsp:tagAttribute name="aria-labelledby" value="lbltimeCall_am"/>
                            </dsp:input>
						</div>
						<div class="label">
							<label id="lbltimeCall_am" for="timeCall_am">
								<bbbl:label key="lbl_contactus_am" language ="${pageContext.request.locale.language}"/>
							</label>
						</div>
					</div>
					<div class="radioItem input marLeft_10">
						<div class="radio" tabindex="0">
						<span class="visuallyhidden">pm</span>
							<dsp:input type="radio" bean="ContactUsFormHandler.amPM" id="timeCall_pm" name="amPM" value="pm">
                               
                                <dsp:tagAttribute name="aria-labelledby" value="lbltimeCall_pm"/>
                            </dsp:input>
						</div>
						<div class="label">
							<label id="lbltimeCall_pm" for="timeCall_pm">
								<bbbl:label key="lbl_contactus_pm" language ="${pageContext.request.locale.language}"/></label>
						</div>
					</div>
				 </div>
				 </fieldset>
                 <div class="clear"></div>
				</div>
                <div class="clear"></div>
			</div>
			<c:if test="${ContactUsFormHandler.captchaError eq true}">
					<c:set var="CaptchError" scope="request" value="error"></c:set>
			</c:if>
			<!-- <div class="input width_7">
					
	            <div id="captchaDiv" class="clearfix">
    	            <div class="input clearfix">
        	            <div class="label width_4">
            	            <label id="lblcaptchaAnswer" for="captchaAnswer"><bbbl:label key="lbl_captcha_answer" language ="${pageContext.request.locale.language}"/> <span class="required">*</span></label>
                        </div>
                        <div class="text width_3" aria-live="assertive" aria-atomic="true">
                	        <img width="237" height="100" src="<c:url value="/simpleCaptcha.png" />" alt="captcha image">
							
							<dsp:input type="text" name="captchaAnswer" id="captchaAnswer" value="" bean="ContactUsFormHandler.captchaAnswer" iclass="marTop_10 ${CaptchError}" autocomplete="off">
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lblcaptchaAnswer errorcaptchaAnswer"/>
                            </dsp:input>				
						<c:if test="${ContactUsFormHandler.captchaError eq true}">
							<label for="captchaAnswer" generated="true" class="error errorLabel" id="errorcaptchaAnswer"><dsp:droplet name="Switch">
						  	<dsp:param bean="ContactUsFormHandler.propertyExceptions.captchaAnswer.errorCode" name="value"/>
						 		 <dsp:oparam name="err_captcha_invalid">
										<bbbl:error key="err_invalid_capatch_inserted" language="${pageContext.request.locale.language}" />
										
						  		</dsp:oparam>
								</dsp:droplet> 
						 </label>
						</c:if>	
						
							
							
							
						
							
							
                        </div>
                    </div>
               </div>
            </div> -->
            <c:set var="captchaKey"><bbbc:config key="contactUsCaptchaKey" configName="ContentCatalogKeys" /></c:set>
            <div class="g-recaptcha" data-callback="captcha_filled"
			   data-expired-callback="captcha_expired" data-sitekey="${captchaKey}"></div>
			   <div class="recaptcha-error-message" aria-hidden="true"><bbbl:label key="lbl_contact_us_captcha" language ="${pageContext.request.locale.language}"/></div>
		   
				<div class="input grid_7 alpha clearfix">
				    <div class="button button_active">
				    	<c:set var="submitKey">
							<bbbl:label key="lbl_contact_us_submit" language ="${pageContext.request.locale.language}"/>
						</c:set>
						<dsp:input type="submit" bean="ContactUsFormHandler.requestInfo" value="${submitKey}" >
                            <dsp:tagAttribute name="aria-pressed" value="false"/>
                            <dsp:tagAttribute name="role" value="button"/>
                        </dsp:input>
					</div>
				</div>
			 </dsp:form>
			 </c:when>
			 <c:otherwise>
			    <jsp:useBean id="placeHolderMap1" class="java.util.HashMap" scope="request"/>
			    <c:set var ="contactConfirmPage" scope="request">true</c:set>
					<c:set target="${placeHolderMap1}" property="category" value="${ContactUsFormHandler.subjectCategory}"/>
					<c:choose>
						<c:when test="${ContactUsFormHandler.contactType eq 'email'}">
							<c:set var="contactusType">
								<bbbl:label key='lbl_profilemain_email' language ='${pageContext.request.locale.language}'/>
							</c:set>
							<c:set target="${placeHolderMap1}" property="type" value="${contactusType}"/>
							<c:set target="${placeHolderMap1}" property="value" value="${ContactUsFormHandler.email}"/>
						</c:when>
						<c:otherwise>
							<c:set var="contactusType">
								<bbbl:label key='lbl_contactus_phone' language ='${pageContext.request.locale.language}'/>
							</c:set>
							<c:set target="${placeHolderMap1}" property="type" value="${contactusType}"/>
							<c:set target="${placeHolderMap1}" property="value" value="${ContactUsFormHandler.phoneNumber}"/>
						</c:otherwise>
					</c:choose>
					
			 
			  <div class="highlightBox">
			   <bbbt:textArea key="txtarea_contactus_emaildelivery" language ="${pageContext.request.locale.language}"  placeHolderMap="${placeHolderMap1}"/>
			   </div>
			 
				 <dsp:setvalue bean="ContactUsFormHandler.successMessage" value="false"/> 
			</c:otherwise>
			</c:choose>
			</div>
		<div class="grid_3 omega imgAgent"> <img src="${imagePath}/_assets/global/images/contact_us_agent_${themeName}.png" alt="We love to help you. If you have questions just let us know - Contact Us" /> </div>
		<div class="grid_9 suffix_3">
				<hr />
		</div>
		</div>
		
		<div class="grid_12 clearfix">
		<div class="grid_2">
		 		<h5><bbbt:label key="lbl_contactus_viaphone" language ="${pageContext.request.locale.language}"/></h5>
			</div>
		<div class="grid_7 suffix_2">
			 <bbbt:textArea key="txtarea_contactus_viaphone" language ="${pageContext.request.locale.language}"/>
		 </div>
		<div class="grid_9 suffix_3">
				<hr />
		</div>
		</div>
		<div class="grid_12 clearfix">
		<div class="grid_2 ">
				<h5> <bbbt:label key="lbl_contactus_instores" language ="${pageContext.request.locale.language}"/></h5>
			</div>
		<div class="grid_7 suffix_2">
			 <bbbt:textArea key="txtarea_contactus_instores" language ="${pageContext.request.locale.language}"/>
		</div>
		<div class="grid_9 suffix_3">
				<hr />
		</div>
		</div>
		<%-- Comment These Line  Defect # 14848
		<div class="grid_12 clearfix">
		<div class="grid_2">
		 <h5>
			 <bbbt:label key="lbl_contactus_viafax" language ="${pageContext.request.locale.language}"/>
			 </h5>
		</div>
		<div class="grid_7 suffix_2">
			 <bbbt:textArea key="txtarea_contactus_viafax" language ="${pageContext.request.locale.language}"/>
		 </div>
		<div class="grid_9 suffix_3">
			<hr />
		</div>
		</div>
		--%>
		<c:if test="${SurveyOn}">
		<div class="grid_12 clearfix">
			<div class="grid_2">
		    	<h5>
					<bbbt:label key="lbl_contactus_customersurvey" language ="${pageContext.request.locale.language}"/>
				</h5>
			</div>
			<div class="grid_7 suffix_2">
				 <bbbt:textArea key="txtarea_contactus_customersurvey" language ="${pageContext.request.locale.language}"/>
		 	</div>
		 </div>
		 </c:if>
 </div>
 <span id="errortxtOrderNumber"></span>
 <span id="errorfirstName"></span>
 <span id="errorlastName"></span>
 <span id="errorcontactUsEmail"></span>
 <span id="errorcontactUsEmailConfirm"></span>
 <span id="errorbasePhoneFull"></span>
 <span id="errorbasePhoneExt"></span>
</jsp:body>
</bbb:pageContainer>
<script type="text/javascript">
//rkg micro pixel 6/20/13
var appid = '${currentSiteId}';
var type = 'contact';
var success='${contactConfirmPage}';
$(function () {
	if('${contactConfirmPage}' == 'true'){
	rkg_micropixel(appid,type);
	}
});


	if (typeof s !== 'undefined') {
		var contactusPageName='Content Page>Contact Us';
		var contactConfirmPage ='${contactConfirmPage}';
		if (contactConfirmPage =='true'){
			contactusPageName +='>Confirmation Page';
		}
		s.pageName= contactusPageName;// pageName
		s.server="${pageContext.request.serverName}";
		s.channel="Company Info"
		s.prop1="Content Page"
		s.prop2="Content Page"
		s.prop3="Content Page"
		s.prop4="Company Info"

		var s_code = s.t();
		if (s_code)
			document.write(s_code);
	}
	var submitFlag = false;
	
	function captcha_filled () {
                submitFlag = true;     
                hideError();                 
            }

    function captcha_expired () {
                submitFlag = false;
                showError();
            }    
	function showError(){
	            $(".recaptcha-error-message").css("visibility","visible");
	            $(".recaptcha-error-message").attr("aria-hidden","false");
	        }

    function hideError(){
        $(".recaptcha-error-message").css("visibility","hidden"); 
        $(".recaptcha-error-message").attr("aria-hidden","true");
    }
</script>
<c:if test="${TellApartOn}">
	<bbb:tellApart actionType="pv" />		
</c:if>
</dsp:page>
<style>
  .cust_required{
   color:#00aeef;
}
</style>
<script>
  $(function(){
	  
var addTitle = function(){
	 $('#g-recaptcha-response').attr('title','captcha response');
}
       setTimeout(addTitle,5000);
});
</script>
