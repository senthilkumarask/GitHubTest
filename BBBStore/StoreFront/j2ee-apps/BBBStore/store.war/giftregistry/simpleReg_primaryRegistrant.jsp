<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>	
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="isTransient" bean="Profile.transient"/>
	 <input type="hidden" id="isTransientProfile" value="${isTransient}">
	<dsp:getvalueof var="event" param="event"/>
	<dsp:getvalueof var ="securityStatus" bean="/atg/userprofiling/Profile.securityStatus"/>
	<c:if test="${securityStatus eq '2' }">
		<c:set var="recognisedUser">true</c:set>
	</c:if>
	
	<c:choose>
        <c:when test="${event == 'BA1'}">
		     <c:set var="accountInfoClass">alpha</c:set>
		     <c:set var="gridClass1">grid_5</c:set>
		     <c:set var="gridClass2">grid_4 noMar</c:set>
		     <c:set var="gridClass3">grid_8 noMar</c:set>
		     <c:set var="noMarClass">noMar</c:set>
		     <c:set var="floatRightClass">fr</c:set>
		     <c:set var="alphaClass">alpha</c:set>
		     <c:set var="gridClass4">grid_4</c:set>
			 <c:set var="customMarLeft10"></c:set>
			 <c:set var="customMarLeft0">noMarLeft</c:set>
			 <c:set var="babyReg">babyReg</c:set>

        </c:when>
        <c:otherwise>
            <c:set var="accountInfoClass">omega</c:set>
             <c:set var="gridClass1">grid_6</c:set>
             <c:set var="gridClass2">grid_3</c:set>
             <c:set var="gridClass3">grid_6</c:set>
             <c:set var="noMarClass"></c:set>
             <c:set var="floatRightClass"></c:set>
             <c:set var="alphaClass"></c:set>
              <c:set var="gridClass4">grid_3</c:set>
			  <c:set var="customMarLeft10">marLeft_10</c:set>
			 <c:set var="customMarLeft0">noMarLeft</c:set>
			 <c:set var="babyReg"></c:set>
        </c:otherwise>
    </c:choose>      
	 <c:choose>
	        <c:when test="${isTransient}">
			    <c:set var="applyAlpha"></c:set>
	        </c:when>
	        <c:otherwise>
	            <c:set var="applyAlpha">alpha</c:set>
	        </c:otherwise>
	 </c:choose>
	
	
	 <dsp:a iclass="makeFavorite" href="/store/selfservice/store/find_store.jsp?favouriteStoreId=dummyId"  id="storeLocatorHiddenLink" style="display:none;">
			<bbbl:label key="lbl_find_store_make_fav_store"	language="${pageContext.request.locale.language}" />
			<dsp:property bean="SearchStoreFormHandler.modifyFavStore" value=""/>
		</dsp:a>
	
	 <div class="grid_4 alpha clearfix">   
	   <dsp:getvalueof var="inputListMap" param="inputListMap"/>
                <label class="padBottom_5" for="fullName"><bbbl:label key="lbl_your_info" language ="${pageContext.request.locale.language}"/></label>
		<c:set var="firstNamePlaceHolder"><bbbl:label key='lbl_reg_ph_firstname' language ='${pageContext.request.locale.language}'/></c:set>
		<c:set var="lastNamePlaceHolder"><bbbl:label key='lbl_reg_ph_lastname' language ='${pageContext.request.locale.language}'/></c:set>
		 <dsp:getvalueof var="frstNameValueFromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.firstName" />
		<dsp:getvalueof var="lstNameValueFromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.lastName" />
               <c:choose>
                   <c:when test="${isTransient}"> 
                    <div class="inputField noMarRight clearfix alpha omega" id="regFullName">
                     <div class="full-name form-group">	
					<c:choose>
					<c:when test="${not empty frstNameValueFromBean && not empty lstNameValueFromBean}">
						<input type="text" id="fullName" name="txtRegistrantFullName" placeholder="<bbbl:label key='lbl_reg_ph_fullname' language ='${pageContext.request.locale.language}'/>" class="required" value="${frstNameValueFromBean} ${lstNameValueFromBean}"/>
					</c:when>
					<c:otherwise>
						<input type="text" id="fullName" name="txtRegistrantFullName" placeholder="<bbbl:label key='lbl_reg_ph_fullname' language ='${pageContext.request.locale.language}'/>" class="required"/>
					</c:otherwise>
					</c:choose>
				            
			         </div>
					 <div class="inputField noMarRight clearfix alpha omega ${firstnamegrid} width130 ">
					 <label class="txtOffScreen" for="txtPrimaryRegistrantFirstName" id="lbltxtPrimaryRegistrantFirstName">${firstNamePlaceHolder}</label>
		                    <dsp:input type="text" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.firstName" id="txtPrimaryRegistrantFirstName" name="txtPrimaryRegistrantFirstNameAltName" value="${frstNameValueFromBean}" iclass="required cannotStartWithSpecialChars alphabasicpunc borderrightnone hidden">
		                        <dsp:tagAttribute name="aria-required" value="true"/>
		                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtPrimaryRegistrantFirstName errortxtPrimaryRegistrantFirstNameAlt"/>
		                        <dsp:tagAttribute name="placeholder" value="${firstNamePlaceHolder}"/>
		                    </dsp:input>
		            </div>			
		                <div class="inputField noMarRight clearfix alpha omega grid_2 width180  ">
		                <label class="txtOffScreen" for="lblPrimaryRegistrantLastName" id="lbltxtPrimaryRegistrantLastName">${lastNamePlaceHolder}</label>               
		                       <dsp:input type="text"	bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.lastName" id="txtPrimaryRegistrantLastName" name="txtPrimaryRegistrantLastNameAltName" value="${lstNameValueFromBean}" iclass="required cannotStartWithSpecialChars alphabasicpunc hidden">
		                        <dsp:tagAttribute name="aria-required" value="true"/>
		                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtPrimaryRegistrantLastName errorlastNameReg"/>
		                        <dsp:tagAttribute name="placeholder" value="${lastNamePlaceHolder}"/>
		                    </dsp:input>
		                </div>
         			  </div>
                   </c:when>
                   <c:otherwise>
		                 <div class="inputField noMarRight clearfix alpha omega grid_4" id="regFullName">
		                  <c:choose>
		                   <c:when test="${not empty frstNameValueFromBean || not empty lstNameValueFromBean}">
				                   <dsp:getvalueof var="firstName" value="${frstNameValueFromBean}"/>
		                          <dsp:getvalueof var="lastName" value="${lstNameValueFromBean}"/>
							</c:when>
							<c:otherwise>
							   <dsp:getvalueof var="firstName" bean="Profile.firstName"/>
                               <dsp:getvalueof var="lastName" bean="Profile.lastName"/>
							</c:otherwise>
		                 </c:choose>
							<input type="text" id="fullName" name="txtRegistrantFullName" placeholder="<bbbl:label key='lbl_reg_ph_fullname' language ='${pageContext.request.locale.language}'/>" class="required" value="${firstName} ${lastName}"/>
						
		                    <div class="inputField noMarRight clearfix alpha omega ${firstnamegrid} width130">
				                <dsp:input type="text" id="txtPrimaryRegistrantFirstName" value="${firstName}" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.firstName" name="txtPrimaryRegistrantFirstNameAltName">
				                <dsp:tagAttribute name="aria-required" value="true"/>
				                <dsp:tagAttribute name="aria-labelledby" value="txtPrimaryRegistrantFirstNameAlt errortxtPrimaryRegistrantFirstNameAlt"/>
				                <dsp:tagAttribute name="class" value="required borderrightnone hidden"/>
				                 <dsp:tagAttribute name="placeholder" value="${firstNamePlaceHolder}"/>
				                </dsp:input>
		           			    </div>			
				                <div class="inputField noMarRight clearfix alpha omega ${lastnamegrid} width180">
				                       <dsp:input type="text" id="txtPrimaryRegistrantLastName" value="${lastName}" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.lastName" iclass="borderLeftnone" name="txtPrimaryRegistrantLastNameAltName">
				                        <dsp:tagAttribute name="aria-required" value="true"/>
				                        <dsp:tagAttribute name="aria-labelledby" value="lastNameReg errorlastNameReg"/>
				                        <dsp:tagAttribute name="class" value="required hidden"/>
				                        <dsp:tagAttribute name="placeholder" value="${lastNamePlaceHolder}"/>
				                       </dsp:input>
		                        </div>
		                     </div>            
                       </c:otherwise>
                </c:choose>
                 <c:if test="${event == 'BRD' || event == 'COM'}">
                    <div class="grid_1 clearfix alpha omega radioPosLeft">
 						<div class="square-radio brideBox">
 						 		<label id="lblBG" for="brideRadioBtn" aria-hidden="true"><span aria-hidden="true">B</span><br><span class="smlTxt"><bbbl:label key="lbl_reg_bride" language ="${pageContext.request.locale.language}"/></span></label>
                              	<dsp:input type="radio" iclass="square-radio--content" bean="GiftRegistryFormHandler.regBG"  value="B" >
                                  	<dsp:tagAttribute name="id" value="brideRadioBtn"/>
                                 	<dsp:tagAttribute name="aria-hidden" value="false"/>
                                 	<dsp:tagAttribute name="aria-label" value="bride"/>
                        		</dsp:input>
                            </div>
						<div class="square-radio brideBox">
							<label id="lblBG" for="groomRadioBtn" class="right2" aria-hidden="true"><span aria-hidden="true">G</span><br><span class="smlTxt"><bbbl:label key="lbl_reg_groom" language ="${pageContext.request.locale.language}"/></span></label>
							<dsp:input type="radio" iclass="square-radio--content" bean="GiftRegistryFormHandler.regBG"  value="G">
                               	<dsp:tagAttribute name="aria-hidden" value="false"/>
                               	<dsp:tagAttribute name="id" value="groomRadioBtn"/>
                               	<dsp:tagAttribute name="aria-label" value="groom"/>
                           </dsp:input>
        				</div>                
       				</div>
  				</c:if>
             </div>
             
               <c:if test="${event == 'BA1'}">  
               	        <div class="grid_4 fr omega">  
             				 <div class="inputField grid_4 noMarRight clearfix alpha omega marTop_22">
             				 <c:set var="regmaidenname">	<bbbl:label key="lbl_reg_ph_maidenname" language ="${pageContext.request.locale.language}"/></c:set>
             				 <label class="txtOffScreen" for="txtPrimaryRegistrantbabyMaidenName"  id="lblMaidenName">${regmaidenname}</label>
             				  <dsp:input type="text" id="txtPrimaryRegistrantbabyMaidenName"  bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.babyMaidenName" iclass="cannotStartWithSpecialChars alphabasicpunc escapeHTMLTag" name="madianNameReg">
		                        <dsp:tagAttribute name="minlength" value="2"/>
		                        <dsp:tagAttribute name="aria-labelledby" value="lblMaidenName errortxtPrimaryRegistrantbabyMaidenName"/>
		                        <dsp:tagAttribute name="placeholder" value="${regmaidenname}"/>
             				  </dsp:input>
        					  </div>
        				   </div>
                <%-- row2 --%>
                <div class="clearfix grid_8 alpha omega flatform marTop_25" id="signInWrap">
				</div>
                </c:if>
       
                <div class="${gridClass3} ${accountInfoClass} ${applyAlpha}">
              		<c:choose>
                    	<c:when test="${isTransient}"> 
                          <label class="padBottom_5 labelForEmail" for="email" ><bbbl:label key="lbl_account_info" language ="${pageContext.request.locale.language}"/>
                          </label>
                 				<div class="${gridClass3} <c:if test="${event ne 'BA1'}"> fr </c:if> flatform" id="signInWrap">
									<div class="${gridClass3} alpha omega fl">
										<span class="txtOffScreen" aria-live="polite" aria-atomic="true" id="readLabel"></span>
										<dsp:getvalueof param="showLegacyPwdPopup" id="showLegacyPwdPopup"/>
										<dsp:getvalueof param="showMigratedPopup" id="showMigratedPopup"/>
										<div class="${gridClass2} alpha noMarTop clearfix">
											<div class="input_wrap ${gridClass2}" aria-live="assertive">					
												<dsp:input id="email" name="simpleCreateEmail" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.email" type="text">						
													<dsp:tagAttribute name="aria-describedby" value="readLabel"/>
													<dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
													<dsp:tagAttribute name="class" value="required email widthHundred"/>
													 <c:set var="lbl_reg_ph_emaillogin_placeholder">
													<bbbl:label key="lbl_reg_ph_emaillogin" language="${pageContext.request.locale.language}" /></c:set>
                                                    <dsp:tagAttribute name="placeholder" value="${lbl_reg_ph_emaillogin_placeholder}"/>
												</dsp:input>
												<div class="emailalreadyexists hidden">
													<span>You may already have an account. Use your password to continue.</span>
												</div>
											</div>
										</div>
										<div class="formRow ${gridClass2} clearfix noMar ${floatRightClass}">
											<div class="inputField alpha ${gridClass2}">
											<div class="passwordWrap ${gridClass2} clearfix ${babyReg}">
											<label class="txtOffScreen" for="password" id="lblPassword">password</label>
											<c:set var="passwordPlaceHolder"><bbbl:label key='lbl_reg_ph_password' language ='${pageContext.request.locale.language}'/></c:set>
												<dsp:input id="password" name="password" bean="GiftRegistryFormHandler.password" type="password" autocomplete="off">
													<dsp:tagAttribute name="aria-required" value="true"/>
													<dsp:tagAttribute name="aria-labelledby" value="lblPassword errorpassword passWordWrapRule "/>
													<dsp:tagAttribute name="placeholder" value="${passwordPlaceHolder}"/>
													<dsp:tagAttribute name="class" value="required showpassLoginFrag grid_3 ${noMarClass}"/>
												</dsp:input>
												<!--BBBP-10412 -->
													<div class="showPassDiv grid_3 clearfix ${noMarClass}" aria-hidden="false">
							                 			  <input aria-hidden='true' name="showPassword" type="text" role="text" value="" data-toggle-class="showpassLoginFrag" class="showPassword" id="showPassword" />
															<a href="javascript:void(0);" aria-hidden="false" role="text" class="lblShowPassword showPasswordLink"><bbbl:label key='lbl_reg_show' language ='${pageContext.request.locale.language}'/></a>
							             		  </div>
											</div>

											<c:choose>
										        <c:when test="${event == 'BA1'}">
												    <div class="forgotPasswordWrap grid_3 fr">
													<dsp:a iclass="forgotPassword grid_3 textRight" page="/account/frags/forgot_password.jsp?omnitureForCheckout=${omnitureForCheckout}&isFromRegistry=true&pageName=simpleRegPrimaryRegistrant" title="${lbl_login_frag__forgot_password}" onclick="javascript:internalLinks('Forgot Password : Forgot Password Link');">Forgot your Password?</dsp:a>
                                                    </div>
										        </c:when>
										        <c:otherwise>
										            <div class="forgotPasswordWrap grid_3">
													<dsp:a iclass="forgotPassword grid_3" page="/account/frags/forgot_password.jsp?omnitureForCheckout=${omnitureForCheckout}&isFromRegistry=true&pageName=simpleRegPrimaryRegistrant" title="${lbl_login_frag__forgot_password}" onclick="javascript:internalLinks('Forgot Password : Forgot Password Link');">Forgot your Password?</dsp:a>
											</div>

										        </c:otherwise>
										    </c:choose>
											
                                            
                                            <c:choose>
											<c:when test="${event == 'BA1'}"> 
                                            	<div class="PasswordWrapRule grid_4 alpha">
													<div class="marTop_3 alpha"><label id="passWordWrapRule" class="simpleRegLabelText"><bbbl:label key='lbl_reg_newPassword' language ='${pageContext.request.locale.language}'/></label></div>
												</div>
											</c:when>
											<c:otherwise>
												<div class="PasswordWrapRule grid_3">
													<div class="marTop_3 font10 ${customMarLeft10}"><label id="passWordWrapRule" class="simpleRegLabelText"><bbbl:label key='lbl_reg_newPassword' language ='${pageContext.request.locale.language}'/></label></div>
												</div>
											</c:otherwise>
											</c:choose>

											


											</div>
											<div class="confirmPasswordWrap ${babyReg} ${rightgridPassword} clearfix marTop_10 ${alphaClass}">
											<c:if test="${inputListMap['confirmPassword'].isDisplayonForm}">
												     <label class="txtOffScreen" id="lblConfirmPassword" for="confirmPassword">Confirm Password</label>   
											         <c:set var="isRequired" value=""></c:set> 
													 <c:if test="${inputListMap['confirmPassword'].isMandatoryOnCreate}">
													 <c:set var="isRequired" value="required"></c:set>
													 </c:if>    
						                        <div class="${gridClass4} ${alphaClass} ${customMarLeft0}">
						                            <dsp:input bean="GiftRegistryFormHandler.value.confirmpassword" type="password" autocomplete="off" name="confirmPassword" id="confirmPassword">
						           					<dsp:tagAttribute name="aria-required" value="true"/>
						          					 <dsp:tagAttribute name="aria-labelledby" value="lblConfirmPassword errorconfirmPassword"/>
						          					 <dsp:tagAttribute name="placeholder" value="Confirm Password"/>
						          					  <dsp:tagAttribute name="class" value="${isRequired} showpassLoginFrag ${customMarLeft10}"/>
						       					</dsp:input>
						                        </div>
						              			</c:if>
						                    </div>
										</div>	
										 <div class="button hidden">
				                       		  <dsp:input id="checkUser" type="button" value="checkUser" bean="GiftRegistryFormHandler.verifyUser" iclass="btnAddtocart button-Med enableOnDOMReady" />
										</div>

		</div>
		<label id="errorlookup" class="error" style="display:none;">success found</label>
	   </div><!-- <c:if test="${event == 'BA1'}"> 
                                            	 </div>
											</c:if> -->
                              <!-- end login form -->
                        </c:when>
                        <c:otherwise>     
                        				<label class="${customMarLeft10} padBottom_5" for="email"><bbbl:label key="lbl_account_info" language ="${pageContext.request.locale.language}"/></label>              
                          				 <div class="grid_3 alpha  clearfix">
                          				 
                          				 <div class="input_wrap ${gridClass2}" aria-live="assertive">
                          				 <dsp:input id="email" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.email" name="email" beanvalue="Profile.email" type="text" iclass="disabled widthHundred" >
                          				 <dsp:tagAttribute name="readonly" value="true" />
                          				 <dsp:tagAttribute name="aria-disabled" value="true" />
                          				 </dsp:input>
                          				 </div>
                          				 </div>
                          				 <div class="formRow ${gridClass2} clearfix noMarTop marLeft_10 ${floatRightClass}">
											<div class="inputField alpha ${gridClass2}">
											<div class="passwordWrap ${gridClass2} clearfix">
												
												<c:choose>
												  <c:when test="${recognisedUser}">
												 <%--  For a recognised user the password field should be editabble. --%>
												 <c:set var="passwordPlaceHolder"><bbbl:label key='lbl_reg_ph_password' language ='${pageContext.request.locale.language}'/></c:set>
												 		<label class="txtOffScreen" for="password" id="lblPassword">${passwordPlaceHolder}</label>
												     <dsp:input id="password" name="loginPasswd" bean="GiftRegistryFormHandler.password" type="password" autocomplete="off">
													<dsp:tagAttribute name="aria-required" value="false"/>
													<dsp:tagAttribute name="aria-labelledby" value="lblPassword"/>
													<dsp:tagAttribute name="placeholder" value="${passwordPlaceHolder}"/>
													<dsp:tagAttribute name="class" value="required showpassLoginFrag grid_3 ${noMarClass}"/>
												</dsp:input>
												<div class="showPassDiv grid_3 clearfix ${noMarClass}">
							                 			  <input aria-hidden='true' name="showPassword" type="checkbox" value="" data-toggle-class="showpassLoginFrag" class="showPassword" id="showPassword" />
															<a href="javascript:void(0);" aria-hidden="false" role="text" class="lblShowPassword showPasswordLink"><bbbl:label key='lbl_reg_show' language ='${pageContext.request.locale.language}'/></a>
							             		  </div>
												  </c:when>
												<c:otherwise>
													<label class="txtOffScreen" for="password" id="lblPassword">password</label>
												   <dsp:input id="password" name="loginPasswd" bean="GiftRegistryFormHandler.password" beanvalue="Profile.password" type="password" iclass="disabled" >
												   <dsp:tagAttribute name="readonly" value="true" />
												   <dsp:tagAttribute name="disabled" value="disabled" />
												   <dsp:tagAttribute name="aria-disabled" value="true" />
												   <dsp:tagAttribute name="aria-labelledby" value="lblPassword" />
												   </dsp:input>
												</c:otherwise>
												</c:choose>
													
											</div>
											
											
											</div>
											
										</div>		
                        </c:otherwise>
                        </c:choose>  
            		</div>
</dsp:page>
