<%@ page import="com.bbb.constants.BBBCoreConstants"%>
<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryVODroplet" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryTypeNameDroplet" />
	<dsp:importbean
		bean="/com/bbb/simplifyRegistry/droplet/SimpleRegFieldsDroplet" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFlyoutDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/atg/multisite/Site" />
	    <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	<dsp:getvalueof var="isTransient" bean="Profile.transient" />
	<dsp:getvalueof bean="SessionBean.registryTypesEvent" id="event" />
	<dsp:getvalueof bean="GiftRegistryFormHandler.registryEventType"
		var="registryEventTypeName" />
	<c:if
		test="${registryEventTypeName eq 'BA1' || registryEventTypeName eq 'Baby' || event eq 'BA1'}">
		<c:redirect
			url="${contextPath}/giftregistry/simpleReg_creation_form_mobile.jsp" />
	</c:if>
	<c:set var="section" value="browse" scope="request" />
	<c:set var="sectionName" value="registry" scope="request" />
	<c:set var="pageWrapper"
		value="createSimpleRegistry useCertonaAjax useFB useAdobeActiveContent"
		scope="request" />
	<c:set var="titleString" value="Bed Bath & Beyond - Create Registry"
		scope="request" />
		<c:choose>
		<c:when test="${currentSiteId == 'TBS_BuyBuyBaby'}">
			<c:set var="pageVariation" value="bb" scope="request" />
		</c:when>
		<c:otherwise>
			<c:set var="pageVariation" value="br" scope="request" />
		</c:otherwise>
		</c:choose>
	
	<c:set var="topRegistryItemFlag" value="false" />
	<c:set var="displayFlag" value="true" />
	<c:set var="BedBathUSSite">
		<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BuyBuyBabySite">
		<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BedBathCanadaSite">
		<bbbc:config key="BedBathCanadaSiteCode"
			configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="shipTo_POBoxOn" scope="request"><bbbc:config key="shipTo_POBoxOn" configName="FlagDrivenFunctions"/></c:set>
		<c:choose>
		<c:when test="${not empty shipTo_POBoxOn &&  shipTo_POBoxOn && (currentSiteId ne BedBathCanadaSite)}">
		<c:set var="iclassValue" value=""/>
		</c:when>
		<c:otherwise>
		<c:set var="iclassValue" value="poBoxAddNotAllowed"/>
		</c:otherwise>
	</c:choose>
	<bbb:pageContainer section="${section}"
		titleString="${titleString}" pageVariation="${pageVariation}">
		<jsp:attribute name="bodyClass">createRegistry createSimpleRegistryBody useCertonaAjax useFB useAdobeActiveContent notBaby createRegistryOnMobile</jsp:attribute>
		<dsp:droplet name="GetRegistryTypeNameDroplet">
			<dsp:param name="siteId" value="${appid}" />
			<dsp:param name="registryTypeCode" value="${event}" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="eventType" param="registryTypeName"
					scope="request" />
			</dsp:oparam>
		</dsp:droplet>
		<jsp:body>


	<c:choose>
        <c:when test="${minifiedCSSFlag}">
        <c:choose>
            <c:when test="${currentSiteId == 'TBS_BuyBuyBaby'}">
            	<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/legacy/bbbaby/css/babyRegistry.min.css?v=${buildRevisionNumber}" />
            	 <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/legacy/simpleRegistry.min.css?v=${buildRevisionNumber}" />
            </c:when>
			<c:otherwise>
				<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/legacy/bbbaby/css/bbbyRegistry.min.css?v=${buildRevisionNumber}" />
				 <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/legacy/simpleRegistry.min.css?v=${buildRevisionNumber}" />
			</c:otherwise>
		</c:choose>
        </c:when>
        <c:otherwise>
         <c:choose>
        	<c:when test="${currentSiteId == 'TBS_BuyBuyBaby'}">
				<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/legacy/bbbaby/css/theme.css?v=${buildRevisionNumber}" />
		 	 	<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/legacy/bbbaby/css/registry.css?v=${buildRevisionNumber}" />
		 	 	 <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/legacy/simpleRegistry.css?v=${buildRevisionNumber}" />
			</c:when>
			<c:otherwise>
				<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/legacy/bbregistry/css/theme.css?v=${buildRevisionNumber}" />
		  		<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/legacy/bbregistry/css/registry.css?v=${buildRevisionNumber}" />
		  		 <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/legacy/simpleRegistry.css?v=${buildRevisionNumber}" />
			</c:otherwise>
			</c:choose>
 		</c:otherwise>
    </c:choose>


		<c:choose>
			
		</c:choose>
		
      
	 
	 <div class="createSimpleRegistry useCertonaAjax useFB useAdobeActiveContent" id="pageWrapper">
      <dsp:getvalueof id="currentSiteId" bean="Site.id" />
			<div class="small-12 large-12 row">				
				<div class="pushUp small-12 large-12 columns">
					<div id="registryTitles">
						<h2 class="clearfix">
							<span class="columns large-6 simpleRegSubText"><bbbl:label key="lbl_create_your_reg" language ="${pageContext.request.locale.language}"/></span>
						</h2>
						
							<span id="RegistrySelectType" class="clearfix">
 							<dsp:include page="/giftregistry/simpleReg_type_select_mobile.jsp">
						    	   <dsp:param name="eventType" param="eventType" />
						    	</dsp:include>
							</span>
						
					</div>
				</div>
			</div>
			 <dsp:droplet name="IsEmpty">
		<dsp:param name="value" bean="GiftRegistryFormHandler.formExceptions"/>
		<dsp:oparam name="false">
		  <dsp:setvalue param="regVO" beanvalue="SessionBean.simplifyRegVO" /> <dsp:getvalueof var="regVO" bean="SessionBean.simplifyRegVO" /> 
		<div class="row topError">
  			<span class="error">
	  		 Error occurred.</span>
	  		 <span class="error">
				<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
					<dsp:param bean="GiftRegistryFormHandler.formExceptions" name="exceptions"/>
					<dsp:oparam name="output">						
							<dsp:getvalueof var="errormsg"param="message"/> 
							<c:choose>
							<c:when test="${ fn:contains(errormsg,'err_')}">
							<bbbe:error key="${errormsg}"
					language="${pageContext.request.locale.language}" />
					</c:when>
					<c:otherwise>
						<dsp:valueof param="message"></dsp:valueof>
					</c:otherwise>	
					</c:choose>				
					</dsp:oparam>				
				</dsp:droplet>
				</span>
				</div>
		</dsp:oparam>
						
	</dsp:droplet>
			<hr>
			<dsp:droplet name="SimpleRegFieldsDroplet">
						<dsp:param name="eventType" value="${eventType}" />
						<dsp:oparam name="output">
								<dsp:getvalueof var="registryInputsByTypeVO"
						param="registryInputsByTypeVO" />
								<dsp:getvalueof var="registryInputList"
						param="registryInputList" />
								<dsp:getvalueof var="inputListMap" param="inputListMap" />
					  	</dsp:oparam>
				</dsp:droplet> 
			
			 <dsp:form id="registryFormPost"
				iclass="clearfix cb form post noMarTop" method="post" autocomplete="off">
			 <dsp:input id="registryEventType" bean="GiftRegistryFormHandler.registryEventType" type="hidden" value="${event}" />
                <dsp:input id="createSimplified" bean="GiftRegistryFormHandler.createSimplified" type="hidden" value="true" />
                <dsp:input id="coRegEmailFoundPopupStatus" bean="GiftRegistryFormHandler.coRegEmailFoundPopupStatus" type="hidden" name="coRegEmailFoundPopupStatus"/>
                <dsp:input id="coRegEmailNotFoundPopupStatus" bean="GiftRegistryFormHandler.coRegEmailNotFoundPopupStatus" type="hidden" name="coRegEmailNotFoundPopupStatus"/>
 
			 <div class="row padBottom_10">
					<div class="columns large-5 cb">                
						<label class="padBottom_5"><bbbl:label key="lbl_your_info" language ="${pageContext.request.locale.language}"/></label>
						<div class="inputField cb" id="regFullName">
						 
							<div class="full-name form-group">	
							 
							   <c:choose>
							   <c:when test="${not isTransient}">	
							    <dsp:getvalueof var="firstnm" bean="Profile.firstName" />
							     <c:if test="${regVO.primaryRegistrant.firstName ne '' &&  regVO.primaryRegistrant.firstName ne null}">
							   	 <dsp:getvalueof var="firstnm" value="${regVO.primaryRegistrant.firstName}" />
							   </c:if>
							    <dsp:setvalue bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.firstName" value="${firstnm}"/>	
							    <dsp:setvalue bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.firstName" value="${firstnm}"/>	
								<dsp:setvalue bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.firstName" value="${firstnm}" />	
							   <dsp:getvalueof var="lastnm" bean="Profile.lastName" />
							    <c:if test="${regVO.primaryRegistrant.lastName ne '' &&  regVO.primaryRegistrant.lastName ne null}">
							   	 <dsp:getvalueof var="lastnm" value="${regVO.primaryRegistrant.lastName}" />
							   </c:if>
							    <dsp:setvalue bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.lastName" value="${lastnm}"/>	
								<dsp:setvalue bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.lastName" value="${lastnm}" />	
								<dsp:setvalue bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.lastName" value="${lastnm}" />
									<input type="text" id="fullName"
											placeholder="<bbbl:label key='lbl_reg_ph_fullname' language ='${pageContext.request.locale.language}'/>"
											class="required cannotStartWithSpecialChars alphabasicpunc escapeHTMLTag"
											value="${firstnm} ${lastnm}" />
							</c:when>
							<c:otherwise>
								<input type="text" id="fullName"
											placeholder="<bbbl:label key='lbl_reg_ph_fullname' language ='${pageContext.request.locale.language}'/>"
											class="required cannotStartWithSpecialChars alphabasicpunc escapeHTMLTag" <c:if test="${not empty regVO.primaryRegistrant.firstName}">value="${regVO.primaryRegistrant.firstName} ${regVO.primaryRegistrant.lastName}"</c:if> />
							</c:otherwise>
							</c:choose>
							</div>
							<div class="row splitName hidden">
								<div class="inputField cb large-6 columns small-12">
									<c:set var="firstName"><bbbl:label key='lbl_reg_ph_firstname' language ='${pageContext.request.locale.language}'/></c:set>
									<c:choose>
									<c:when test="${isTransient}">
										<dsp:input type="text"
												bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.firstName" value="${regVO.primaryRegistrant.firstName}"
												id="txtPrimaryRegistrantFirstName"
												name="txtPrimaryRegistrantFirstNameAltName"
												iclass="required cannotStartWithSpecialChars alphabasicpunc escapeHTMLTag">
				                        <dsp:tagAttribute name="aria-required"
													value="true" />
				                        <dsp:tagAttribute name="aria-labelledby"
													value="txtPrimaryRegistrantFirstNameAlt errortxtPrimaryRegistrantFirstNameAlt" />
				                        <dsp:tagAttribute name="placeholder"
													value="${firstName}" />
				                         </dsp:input>
				                        </c:when>
				                         <c:otherwise>
				                         	<dsp:input type="text" 	value="${firstnm}" 
												id="txtPrimaryRegistrantFirstName"
												 
												name="txtPrimaryRegistrantFirstNameAltName"
												bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.firstName">
						                    <dsp:tagAttribute name="aria-required"
													value="true" />
						                    <dsp:tagAttribute name="aria-labelledby"
													value="txtPrimaryRegistrantFirstNameAlt errortxtPrimaryRegistrantFirstNameAlt" />
											<dsp:tagAttribute name="placeholder"
											value="${firstName}" />
						                    </dsp:input>
				                         </c:otherwise>
				                         </c:choose>
								</div>			
								<div class="inputField cb small-12 columns">
									<c:set var="lastName"><bbbl:label key='lbl_reg_ph_lastname' language ='${pageContext.request.locale.language}'/></c:set>
									<c:choose>
									<c:when test="${isTransient}">
										 <dsp:input type="text" value="${regVO.primaryRegistrant.firstName}"
												bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.lastName"
												id="txtPrimaryRegistrantLastName"
												name="txtPrimaryRegistrantLastNameAltName"
												iclass="required cannotStartWithSpecialChars alphabasicpunc escapeHTMLTag">
					                        <dsp:tagAttribute name="aria-required"
													value="true" />
					                        <dsp:tagAttribute name="aria-labelledby"
													value="lastNameReg errorlastNameReg" />
					                        <dsp:tagAttribute name="placeholder"
													value="${lastName}" />
					                    </dsp:input>
					                  </c:when>
					                  <c:otherwise>
					                  		 <dsp:input type="text"  value="${lastnm}" 
												id="txtPrimaryRegistrantLastName"
												
												name="txtPrimaryRegistrantLastNameAltName"
												bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.lastName"
												iclass="borderLeftnone borderRightnone">
				                        <dsp:tagAttribute name="aria-required"
													value="true" />
				                        <dsp:tagAttribute name="aria-labelledby"
													value="lastNameReg errorlastNameReg" />
										<dsp:tagAttribute name="placeholder"
										value="${lastName}" />
				                       </dsp:input>
					                  </c:otherwise>  
					                  </c:choose>
								</div> 
							</div>
							<c:if test="${event == 'BRD' || event == 'COM' }">
							 <div class="grid_1 clearfix alpha omega radioPosLeft">
								<div class="square-radio brideBox">
								<dsp:input type="radio" iclass="square-radio--content"
											name="regGender" id="regBride" value='B'
											bean="GiftRegistryFormHandler.regBG">
                                         	<dsp:tagAttribute
												name="aria-checked" value="false" />
									<label id="lblBG" for="regBride"><span>B</span><br><span
												class="smlTxt"><bbbl:label key="lbl_reg_bride" language ="${pageContext.request.locale.language}"/></span></label>
									</dsp:input>
								<!-- </input> -->
								</div>
								<div class="square-radio brideBox">
									<dsp:input type="radio" iclass="square-radio--content"
											name="regGender" id="regGroom" value="G"
											bean="GiftRegistryFormHandler.regBG">
                                           	<dsp:tagAttribute
												name="aria-checked" value="false" />
									<label id="lblBG" for="regGroom"><span>G</span><br><span
												class="smlTxt"><bbbl:label key="lbl_reg_groom" language ="${pageContext.request.locale.language}"/></span></label>
									</dsp:input>
								</div> 
								<input name="regGender" value=" " type="hidden" id="regGender" />               
							</div> 
							</c:if>
						</div> 
					</div>
					
					
					
					<div class="columns large-7 cb">                
						<div class="row">
							<dsp:getvalueof param="showLegacyPwdPopup"
								id="showLegacyPwdPopup" />
							<dsp:getvalueof param="showMigratedPopup" id="showMigratedPopup" />
							<div class="inputField cb large-6 columns">
								<c:choose>
									<c:when test="${isTransient}">
									<c:set var="emailLogin"><bbbl:label key='lbl_reg_ph_emaillogin' language ='${pageContext.request.locale.language}'/></c:set>
												<dsp:input id="emailField" name="emailReg"
											bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.email" value="${regVO.primaryRegistrant.email}"
											type="text" iclass="required" >						
																<dsp:tagAttribute name="aria-describedby"
												value="readLabel" />
																<dsp:tagAttribute name="aria-labelledby"
												value="lblemail erroremail" />
			                                                    <dsp:tagAttribute
												name="placeholder" value="${emailLogin}"/>
													</dsp:input>
												</c:when>
												<c:otherwise>
													<dsp:input id="emailField"
											bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.email"
											name="emailReg" beanvalue="Profile.email" type="text" 
											iclass="disabled required" readonly="true"></dsp:input>
											<dsp:setvalue bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.email" beanvalue="Profile.email"/>
												</c:otherwise>
												</c:choose>
								<div class="row emailalreadyexists hidden">
									<span class="invite-co"><bbbl:label key='lbl_reg_existAccount' language ='${pageContext.request.locale.language}'/></span>
								</div>
							</div>
							<dsp:getvalueof var="pwdFromBean" bean="GiftRegistryFormHandler.password"/>
							<div class="inputField cb large-6 columns">							
								<div class="passwordWrapper">
									<c:choose>
									<c:when test="${isTransient}">
									<c:set var="password"><bbbl:label key='lbl_reg_ph_password' language ='${pageContext.request.locale.language}'/></c:set>
										<dsp:input id="password"
												bean="GiftRegistryFormHandler.password" type="password"
												name="passwordReg" autocomplete="off" iclass="required" maxlength='20' value="${pwdFromBean}">
														<dsp:tagAttribute name="aria-required" value="false" />
														<dsp:tagAttribute name="aria-describedby"
													value="readLabel" />
														 <dsp:tagAttribute name="placeholder" value="${password}" />
													</dsp:input>
											<div class="showPassDiv cb">
													<div class="checker" id="uniform-showPassword"
														style="display: none;">
														<span><input name="showPassword" type="checkbox"
															value="" data-toggle-class="showpassLoginFrag"
															class="showPassword" id="showPassword" style="opacity: 0;"></span>
													</div>
												<label for="showPassword" class="lblShowPassword"><bbbl:label key='lbl_reg_show' language ='${pageContext.request.locale.language}'/></label>
											</div>
											
								</div>
										<div class="forgotpassword hidden">
												<a id="forgotPasswordDialogLink"
										title="Reset&nbsp;Password?"
										class="small forgot-password popupShipping"
										onclick="javascript:internalLinks('Forgot Password : Forgot Password Link')"
										href="/tbs/giftregistry/modals/forgot_password_simplifyRegistry.jsp?omnitureForCheckout=false"
										data-reveal-ajax="true"><bbbl:label key='lbl_reg_forgot_password' language ='${pageContext.request.locale.language}'/></a>
										
											</div>
											<div id="forgotPasswordDialog" class="reveal-modal"
									data-reveal>
										</div>
										<div class="passwordValidationRule">
									<span><bbbl:label key='lbl_reg_newPassword' language ='${pageContext.request.locale.language}'/></span>
									
								</div>
									</c:when>
									<c:otherwise>
										<dsp:input id="password"
										bean="GiftRegistryFormHandler.password"
										beanvalue="Profile.password" name="passwordReg"
										iclass="disabled required" type="password" disabled="true"></dsp:input>
											
							</div>
									</c:otherwise>
									</c:choose>
						  <c:if test="${inputListMap['confirmPassword'].isDisplayonForm && isTransient}"> 
						  <c:set var="required" value=""></c:set> 
							  	<c:if
											test="${inputListMap['confirmPassword'].isMandatoryOnCreate}"> <c:set
												var="required" value="required" />   </c:if>
								<div class="confirmPasswordWrap">
									<input id="confirmPassword" placeholder="<bbbl:label key='lbl_reg_ph_confirmpassword' language ='${pageContext.request.locale.language}'/>"
									name="confirmPasswordReg" value="" class="${required}" type="password">
								</div>
								</c:if>
							</div> 
						</div> 
					</div>
				</div> 
				
				

        <dsp:getvalueof bean="Profile.email" var="profilemail" />

				<div class="row padBottom_10">
				    <c:if
						test="${inputListMap['CoRegistrantFirstName'].isDisplayonForm}">
					<div class="columns large-5 cb">                
						<label class="padBottom_5"><bbbl:label key="lbl_co_reg" language ="${pageContext.request.locale.language}"/></label>
						<div class="inputField cb" id="regCoRegisName">
						 <c:set var="required" value=""></c:set> 
							<c:if
											test="${inputListMap['CoRegistrantFirstName'].isMandatoryOnCreate}"> <c:set
												var="required" value="required" />   </c:if>
							<c:set var="firstName"><bbbl:label key='lbl_reg_ph_firstname' language ='${pageContext.request.locale.language}'/></c:set>
							<div class="inputField cb">			
								<c:choose>
							  <c:when test="${regVO.coRegistrant.firstName != '' && regVO.coRegistrant.firstName != null}">		
								<input type="text" id="regCoRegisFullName"
										name="regCoRegisFullName"
										placeholder="<bbbl:label key='lbl_reg_ph_fullname' language ='${pageContext.request.locale.language}'/>"
										class="${required} cannotStartWithSpecialChars alphabasicpunc escapeHTMLTag" value="${regVO.coRegistrant.firstName} ${regVO.coRegistrant.lastName}">
							</c:when>
							<c:otherwise>
								<input type="text" id="regCoRegisFullName"
										name="regCoRegisFullName"
										placeholder="<bbbl:label key='lbl_reg_ph_fullname' language ='${pageContext.request.locale.language}'/>"
										class="${required} cannotStartWithSpecialChars alphabasicpunc escapeHTMLTag">
							
							</c:otherwise>
							</c:choose>	</div>
							<div class="row splitName hidden">
								<div class="inputField cb large-6 columns small-12">
								 <dsp:input id="regCoRegisFirstName" type="text"
											name="txtCoRegistrantFirstNameName"
											value="${regVO.coRegistrant.firstName}"
											bean="GiftRegistryFormHandler.registryVO.coRegistrant.firstName"
											iclass="${required} cannotStartWithSpecialChars alphabasicpunc escapeHTMLTag">
                                                    <dsp:tagAttribute
												name="aria-required" value="true" />
                                                    <dsp:tagAttribute
												name="aria-labelledby"
												value="lbltxtCoRegistrantFirstName errortxtCoRegistrantFirstName" />
                                                    <dsp:tagAttribute
												name="placeholder" value="${firstName}" />
                        </dsp:input>
								</div>	
								 <c:if
										test="${inputListMap['CoRegistrantLastName'].isDisplayonForm}">	
										<c:set var="required" value=""></c:set> 	
								 	<c:if
											test="${inputListMap['CoRegistrantLastName'].isMandatoryOnCreate}"> <c:set
												var="required" value="required" />   </c:if>
	<c:set var="lastName"><bbbl:label key='lbl_reg_ph_lastname' language ='${pageContext.request.locale.language}'/></c:set>
									<div class="inputField cb large-6 columns small-12">
									 <dsp:input id="regCoRegisLastName" type="text"
												name="txtCoRegistrantLastNameName"
												value="${regVO.coRegistrant.lastName}"
												bean="GiftRegistryFormHandler.registryVO.coRegistrant.lastName"
												iclass="${required} cannotStartWithSpecialChars alphabasicpunc escapeHTMLTag"
												maxlength="30">
												<dsp:tagAttribute name="minlength" value="2"/>	
                                                    <dsp:tagAttribute
													name="aria-required" value="true" />
                                                    <dsp:tagAttribute
													name="aria-labelledby"
													value="lbltxtCoRegistrantLastName errortxtCoRegistrantLastName" />
                                                      <dsp:tagAttribute
													name="placeholder" value="${lastName}" />
                                                </dsp:input>
									</div> 
								</c:if>
							</div>
							<c:if test="${event == 'BRD' || event == 'COM' }">
							 <div class="grid_1 clearfix alpha omega radioPosLeftCo">
								<div class="square-radio brideBox">
								<dsp:input type="radio" iclass="square-radio--content"
												name="regGender" id="regBrideCo" value="B"
												bean="GiftRegistryFormHandler.coRegBG">
			                                                	<dsp:tagAttribute
													name="aria-checked" value="false" />
											<label id="lblBG" for="regBrideCo"><span>B</span><br><span
													class="smlTxt"><bbbl:label key='lbl_reg_bride' language ='${pageContext.request.locale.language}'/></span></label>
									</dsp:input>
								</div>
								<div class="square-radio brideBox">	
								<dsp:input type="radio" iclass="square-radio--content"
												name="regGender" id="regGroomCo" value="G"
												bean="GiftRegistryFormHandler.coRegBG">
			                                                	<dsp:tagAttribute
													name="aria-checked" value="false" />
			                                                	<label id="lblBG"
													for="regGroomCo"><span>G</span><br><span class="smlTxt"><bbbl:label key='lbl_reg_groom' language ='${pageContext.request.locale.language}'/></span></label>
			                                                </dsp:input>
								</div>  
								<input name="coRegGender" value=" " type="hidden"
											id="coRegGender" />             
							</div> 
							</c:if>
						</div>
					</div>
					</c:if>
					
					  <c:if test="${inputListMap['CoRegistrantEmail'].isDisplayonForm}">  
					  <c:set var="required" value=""></c:set> 
					  	<c:if
							test="${inputListMap['CoRegistrantEmail'].isMandatoryOnCreate}"> <c:set
								var="required" value="required" />   </c:if> 
							<div class="columns large-7 cb">                
								<div class="row">
									<div class="inputField cb large-6 columns">
									<c:set var="coregemail"><bbbl:label key='lbl_reg_ph_coregemail' language ='${pageContext.request.locale.language}'/></c:set>
										 <dsp:input id="coRegisEmailField" type="text" name="emailRegCo"
										bean="GiftRegistryFormHandler.registryVO.coRegistrant.email" value="${regVO.coRegistrant.email}"
										iclass="${required} email widthHundred">
                                                    <dsp:tagAttribute
											name="aria-required" value="true" />
                                                    <dsp:tagAttribute
											name="aria-labelledby"
											value="lbltxtCoRegisEmailField errortxtCoRegisEmailField" />
                                                      <dsp:tagAttribute
											name="placeholder" value="${coregemail}" />
                                                </dsp:input>
									</div>			
									<div class="inputField cb large-6 columns">
										<span class="invite-co"><bbbl:label key='lbl_reg_inviteCoreg' language ='${pageContext.request.locale.language}'/></span>
									</div> 
								</div> 
							</div>
					</c:if>
				</div>
				
				<div class="row padBottom_10">
					<div class="columns large-5 cb">                
						<div class="row">
						  <c:if test="${inputListMap['weddingDate'].isDisplayonForm}">
						  <c:set var="required" value=""></c:set> 
						  	<c:if test="${inputListMap['weddingDate'].isMandatoryOnCreate}"> <c:set
										var="required" value="required" />  </c:if> 
							<div class="cb inputField large-6 columns eventsDate">
							<c:set var="phdate"><bbbl:label key='lbl_reg_ph_date' language ='${pageContext.request.locale.language}'/></c:set>
								<label class="padBottom_5" for=""><bbbl:label key="lbl_reg_wedding_date" language ="${pageContext.request.locale.language}"/></label>
								<div class='inputWithCalender'>
									<dsp:input id="eventField"
											bean="GiftRegistryFormHandler.registryVO.event.eventDate"
											type="text" readonly='true' name="eventDate"
											value="${regVO.event.eventDate}"
											iclass="${required} step1FocusField">
	                                                <dsp:tagAttribute
												name="aria-required" value="true" />
	                                                <%-- <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryExpectedDate${CADate} errortxtRegistryExpectedDate${CADate}"/> --%>
	                                           		 <dsp:tagAttribute
												name="placeholder" value="${phdate}" />
										<span class="icon-calendar" aria-hidden="true"
												id="registryExpectedDateButton"></span>
									</dsp:input>
								</div>	
							</div>
							</c:if> 
							<c:if test="${inputListMap['eventDate'].isDisplayonForm}">
							<c:set var="required" value=""></c:set> 
								<c:if test="${inputListMap['eventDate'].isMandatoryOnCreate}"> <c:set
										var="required" value="required" />   </c:if> 
										<c:set var="phdate"><bbbl:label key='lbl_reg_ph_date' language ='${pageContext.request.locale.language}'/></c:set>
							<div class="cb large-6 inputField  columns eventsDate">
								<label class="padBottom_5" for=""><bbbl:label key="lbl_reg_event_date" language ="${pageContext.request.locale.language}"/></label>
								<div class='inputWithCalender'>
									<dsp:input id="eventField"
										bean="GiftRegistryFormHandler.registryVO.event.eventDate"
										type="text" name="eventDate"
										value="${regVO.event.eventDate}"
										iclass="${required} step1FocusField">
                                                <dsp:tagAttribute
											name="aria-required" value="true" />
                                                <%-- <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryExpectedDate${CADate} errortxtRegistryExpectedDate${CADate}"/> --%>
                                           		 <dsp:tagAttribute
											name="placeholder" value="${phdate}" />
									<span class="icon-calendar" aria-hidden="true"
											id="registryExpectedDateButton"></span>
									 </dsp:input>
								</div>	 
							</div>
							</c:if> 
							 <c:if test="${inputListMap['numberOfGuests'].isDisplayonForm}">
							 <c:set var="required" value=""></c:set> 	
							 	<c:if
									test="${inputListMap['numberOfGuests'].isMandatoryOnCreate}"> <c:set
										var="required" value="required" />   </c:if> 					
								<div class="cb large-6 columns inputField">
									<label class="padBottom_5"><bbbl:label key="lbl_reg_noOfGuests" language ="${pageContext.request.locale.language}"/></label>	
									 <dsp:getvalueof var="guestCount"
										value="${regVO.event.guestCount}" />
                                        	<c:if
										test="${regVO.event.guestCount eq '0'}">
                                        		<dsp:getvalueof
											var="guestCount" value="" />
                                        	</c:if>
                                        	<c:set var="regAppNo"><bbbl:label key='lbl_reg_ph_approxNo' language ='${pageContext.request.locale.language}'/></c:set>
                                            <dsp:input
										bean="GiftRegistryFormHandler.registryVO.event.guestCount"
										iclass="${required} digits" type="text"
										id="txtRegistryNumberOfGuests"
										name="txtRegistryNumberOfGuestsName" value="${guestCount}"
										maxlength="4" priority="1">
                                                <dsp:tagAttribute
											name="aria-required" value="true" />
                                                <dsp:tagAttribute
											name="aria-labelledby"
											value="lbltxtRegistryNumberOfGuests errortxtRegistryNumberOfGuests" />
                                                <dsp:tagAttribute
											name="placeholder" value="${regAppNo}" />
                                            </dsp:input>
								</div> 
							</c:if>
							 <dsp:input
								bean="GiftRegistryFormHandler.registryVO.event.college"
								type="hidden" value="" />
                                 <dsp:input
								bean="GiftRegistryFormHandler.registryVO.event.birthDate"
								type="hidden" name="txtBirthdayDateName" value="" />
 								 <dsp:input
								bean="GiftRegistryFormHandler.registryVO.event.babyGender"
								type="hidden" value="" /> 
                                 <dsp:input
								bean="GiftRegistryFormHandler.registryVO.event.babyNurseryTheme"
								type="hidden" value="" />
                                 <dsp:input
								bean="GiftRegistryFormHandler.registryVO.event.babyName"
								type="hidden" value="" />
						</div>
						<div class="row">
						  <c:if test="${inputListMap['showerDate'].isDisplayonForm}"> 
						  <c:set var="required" value=""></c:set> 
						  	<c:if test="${inputListMap['showerDate'].isMandatoryOnCreate}"> <c:set
										var="required" value="required" />   </c:if> 			
							<div class="cb large-6 columns inputField  eventsDate">
								<label class="padBottom_5" for=""><bbbl:label key="lbl_reg_shower_date" language ="${pageContext.request.locale.language}"/></label>
								<c:set var="phdate"><bbbl:label key='lbl_reg_ph_date' language ='${pageContext.request.locale.language}'/></c:set>
								<div class="inputWithCalender">
									<dsp:input id="showerField"
											bean="GiftRegistryFormHandler.registryVO.event.showerDate"
											type="text" name="showerDate" readonly='true'
											value="${regVO.event.showerDate}" iclass="${required}">
	                                                <dsp:tagAttribute
												name="aria-required" value="true" />
	                                                <%-- <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryExpectedDate${CADate} errortxtRegistryExpectedDate${CADate}"/> --%>
	                                           		 <dsp:tagAttribute
												name="placeholder" value="${phdate}" />
										<span class="icon-calendar" aria-hidden="true"
												id="showerDateButton"></span>
									</dsp:input>
								</div>	
							</div>
							 </c:if> 
							 <c:if test="${inputListMap['MobileNumber'].isDisplayonForm}">	
							 <c:set var="required" value=""></c:set> 
							 	<c:if
									test="${inputListMap['MobileNumber'].isMandatoryOnCreate}"> <c:set
										var="required" value="required" />   </c:if> 
								<div class="inputField cb large-6 columns ">
								<c:set var="phphone"><bbbl:label key='lbl_reg_ph_phone' language ='${pageContext.request.locale.language}'/></c:set>
								<input type="hidden" id="requiredBccMob" value="${inputListMap['MobileNumber'].isMandatoryOnCreate}">
									<label class="padBottom_5" for="mobilePhone"><bbbl:label key="lbl_reg_MobilePhone" language ="${pageContext.request.locale.language}"/></label>
									<c:choose>
											<c:when test="${isTransient}"> 
											<dsp:input id="mobilePhoneMask" 
												bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.cellPhone"
												 name="mobilePhoneMask" maxlength="10"
												iclass="${required}" type="hidden">
												</dsp:input>
												<input id="mobilePhone" name="mobilePhone" value="${regVO.primaryRegistrant.cellPhone}"  maxlength="14" placeholder="${phphone}" 
												class="phone phoneField widthHundred phoneNumberMask  ${required} form-control bfh-phone escapeHTMLTag valid" type="tel" data-format="(ddd) ddd-dddd" aria-labelledby="errortxtRegistrantMobileNumber">
																
															</c:when>
											<c:otherwise>
											<dsp:getvalueof var="mobNo" bean="Profile.mobileNumber"></dsp:getvalueof>
												<c:if test="${regVO.primaryRegistrant.cellPhone ne '' && regVO.primaryRegistrant.cellPhone ne null}">
													<dsp:getvalueof var="mobNo" value=="${regVO.primaryRegistrant.cellPhone}"></dsp:getvalueof>
												</c:if>
												<dsp:input id="mobilePhoneMask" 
												bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.cellPhone"
												type="hidden" name="mobilePhoneMask" maxlength="10" value="${mobNo}"
												iclass="${required}">
												</dsp:input>
												
												<input id="mobilePhone" placeholder="${phphone}" name="mobilePhone" value="${mobNo}" maxlength="14" class="${required} form-control bfh-phone" type="tel" data-format="(ddd) ddd-dddd">
											</c:otherwise>
										</c:choose>
								</div> 
							</c:if>
						</div>
						<dsp:include page="simpleReg_store_form.jsp">
		                    <dsp:param name="siteId" value="${appid}" />
		                    <dsp:param name="inputListMap" value="${inputListMap}"/>
		                </dsp:include>
						 <div class="row">
							<div class="columns large-12 cb"> 
							<dsp:include page="/giftregistry/simpleReg_emailOptIn_form.jsp"></dsp:include>               
								<%--  <label class="padBottom_10" for="favStoreId"><bbbl:label key="lbl_registry_store_favorite_store" language ="${pageContext.request.locale.language}"/></label>
								<div class="row">
									<div class="inputField cb large-12 columns fav-store-text">
										<span>You have not selected a Favorite Store.</span>
									</div>
								</div>
								<div class="row">
									<div class="inputField cb large-12 columns">
										<input id="favStoreId"
											placeholder="Enter city and State or Zip Code"
											name="favStoreId" value="" class="required" type="text">
										<span class="txtFindStoresearch"><span
											class="icon icon-search block" aria-hidden="true"></span></span>
									</div>			
								</div>  
								--%>
								
								<%-- <div class="row padBottom_10">
									<div class="large-12 columns emailOffer addressFields ">
										<label class="inline-rc checkbox" for="exclusiveEmailOffer"> 
											<input aria-checked="true" id="exclusiveEmailOffer"
											aria-labelledby="lblexclusiveEmailOffer"
											name="exclusiveEmailOffer" value="exclusiveEmailOffer"
											type="checkbox" checked="checked" style="opacity: 0;"><span></span> <bbbt:textArea
												key="txt_exclusiveEmailOffer"
												language="${pageContext.request.locale.language}" />
										</label>
									</div>
								</div>  --%>
								
							</div>
						</div> 
					</div>
				
					<div class="columns large-7 cb">                
						<div class="row">
							<div class="inputField cb large-8 columns">
								 <%@ include file="frags/simpleReg_registrant_address_book.jsp"%>
								<c:if
										test="${inputListMap['futureShippingDate'].isDisplayonForm || inputListMap['showfutureShippingAddr'].isDisplayonForm}">
									<div class="row">
										<div class="large-12 columns addressFields">
											<label class="inline-rc checkbox"
												for="radRegistryFutureShippingAddressName"> 
												<%-- <dsp:input 
												id="radRegistryFutureShippingAddressName"
												bean="GiftRegistryFormHandler.futureShippingDateSelected" value="futureShippingDateSelected"
												name="radRegistryFutureShippingAddressName"  type="checkbox"
												checked="true" >
												<dsp:tagAttribute
													name="aria-required" value="true" />
												<dsp:tagAttribute
													name="aria-checked" value="true" />
		                                        <dsp:tagAttribute
													name="aria-labelledby"
													value="lblradRegistryFutureShippingAddressName" />
												</dsp:input> --%>
												
												
												<dsp:input 
												id="radRegistryFutureShippingAddressName"
												name="radRegistryFutureShippingAddressName" bean="GiftRegistryFormHandler.futureShippingDateSelected"
												value="futureShippingDateSelected" type="checkbox"
												checked="false" >
												<dsp:tagAttribute
													name="aria-required" value="true" />
													<dsp:tagAttribute
													name="aria-checked" value="true" />
		                                        <dsp:tagAttribute
													name="aria-labelledby"
													value="lblradRegistryFutureShippingAddressName" />
													
												</dsp:input>
											
												<span></span> <bbbl:label key='lbl_reg_moving' language ='${pageContext.request.locale.language}'/>
											</label>	
											<dsp:input  type="hidden" id="futureShippingAddress"  bean="GiftRegistryFormHandler.futureShippingAddress"
												value="newFutureShippingAddress" />
										</div>
									</div>
									<c:set var="required" value=""></c:set> 
											<c:if
											test="${inputListMap['futureShippingDate'].isMandatoryOnCreate}"> <c:set
												var="required" value="required" />   </c:if> 
											<div class="row hidden">
												<div class="cb large-6 columns inputField eventsDate futureField">
														<c:set var="futuredate"><bbbl:label key='lbl_reg_ph_futuredate' language ='${pageContext.request.locale.language}'/></c:set>
													<div class='inputWithCalender'>	
														<dsp:input id="futureField" 
														name="futureDate" iclass="${required}" type="text" value="${regVO.shipping.futureShippingDate}"
														bean="GiftRegistryFormHandler.registryVO.shipping.futureShippingDate"
														readonly="true">
														<dsp:tagAttribute name="placeholder" value="${futuredate}" />
															<span class="icon-calendar" aria-hidden="true"
														id="futureDateButton"></span>
														</dsp:input>
													</div>	
												</div>
											</div>
											<c:set var="required" value=""></c:set> 
										<c:if
											test="${inputListMap['showFutureShippingAddr'].isMandatoryOnCreate}"> <c:set
												var="required" value="required" />   </c:if> 
										<div class="row futureAddField hidden">
											<div class="large-12 columns addressFields" id="shippingFutureAdd">
											<c:if test="${regVO.shipping.futureshippingAddress.addressLine1 ne '' && regVO.shipping.futureshippingAddress.addressLine1 ne null}">
												<dsp:getvalueof var="address1" value="${regVO.shipping.futureshippingAddress.addressLine1}" />
													<dsp:getvalueof var="address2" value=="${regVO.shipping.futureshippingAddress.addressLine2}" />
													<c:set var="futureShippingAddr">${address1}<c:if test="${not empty address2}">, ${address2}</c:if>, ${regVO.shipping.futureshippingAddress.city}, ${regVO.shipping.futureshippingAddress.state}, ${regVO.shipping.futureshippingAddress.zip}
													</c:set>
													<dsp:setvalue
														bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.addressLine1"
														value="${regVO.shipping.futureshippingAddress.addressLine1}" />
													<dsp:setvalue
														bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.addressLine2"
														value="${regVO.shipping.futureshippingAddress.addressLine2}" />
													<dsp:setvalue
														bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.city"
														value="${regVO.shipping.futureshippingAddress.city}" />
													<dsp:setvalue
														bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.state"
														value="${regVO.shipping.futureshippingAddress.state}" />
													<dsp:setvalue
														bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.zip"
														value="${regVO.shipping.futureshippingAddress.zip}" />
											</c:if>
												<input id="txtRegistryFutureShippingAddress"
													aria-labelledby="lbltxtRegistryFutureShippingAddress errortxtRegistryFutureShippingAddress"
													maxlength="120" name="txtRegistryFutureShippingAddress"
													value="${futureShippingAddr}"
													class="${required} cannotStartWithWhiteSpace QASAddress1 escapeHTMLTag ${iclassValue}"
													type="text" aria-required="true"
													placeholder="<bbbl:label key='lbl_reg_ph_futureaddress' language ='${pageContext.request.locale.language}'/>" autocomplete="off">
												<c:if test="${! isTransient}">
						                            <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.firstName" beanvalue="Profile.firstName"/>
														<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.lastName" beanvalue="Profile.lastName"/>
						                          </c:if>
						                        <dsp:input type="hidden" bean="GiftRegistryFormHandler.futureShippingAddress" id="newFutureShippingAddress"/>
				                    			<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.firstName" id="futureFirstName" iclass="QASFirstName"/>
												<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.lastName" id="futureLastName" iclass="QASLastName"/>
												<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.addressLine1" id="txtSimpleRegFutureShippingAddress1" iclass="address1"/>
												<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.addressLine2" id="txtSimpleRegFutureShippingAddress2" iclass="address2"/>
												<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.city" id="txtSimpleRegFutureShippingCity"  iclass="QASCity"/>
												<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.state" id="txtSimpleRegFutureShippingState" iclass="QASState"/>
												<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.zip" id="txtSimpleRegFutureShippingZip" iclass="QASZip"/>
												<input type="hidden" id="txtRegistryFutureShippingBtn"/>
												<input type="hidden" name="isFutureShippingQASValidate" id="isFutureShippingQASValidate" value="true"	 />
											</div>
										</div>
								</c:if>
								
							</div>	
							 <c:if test="${inputListMap['PhoneNumber'].isDisplayonForm}">	
							 <c:set var="required" value=""></c:set> 
							 	<c:if test="${inputListMap['PhoneNumber'].isMandatoryOnCreate}"> <c:set
										var="required" value="required" />   </c:if> 	
								<div class="inputField cb large-4 columns">
								<input type="hidden" id="requiredBccPhn" value="${inputListMap['PhoneNumber'].isMandatoryOnCreate}">
									<label class="padBottom_5" for="contactPhone"><bbbl:label key="lbl_reg_phone" language ="${pageContext.request.locale.language}"/></label>
									<c:set var="phphone"><bbbl:label key='lbl_reg_ph_phone' language ='${pageContext.request.locale.language}'/></c:set>
									<c:choose>
										<c:when test="${isTransient}"> 
										<dsp:input id="contactPhoneMak"
												bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.primaryPhone"
												type="hidden" name="contactPhoneMak" maxlength="10"
												iclass="${required}">
											</dsp:input>
												<input id="contactPhone" placeholder="${phphone}" name="contactPhone" value="${regVO.primaryRegistrant.primaryPhone}" maxlength="14" class="${required} form-control bfh-phone" type="tel" data-format="(ddd) ddd-dddd">
											
										</c:when>
										<c:otherwise>
										<dsp:getvalueof var="mobNo" bean="Profile.PhoneNumber"></dsp:getvalueof>
											<c:if test="${regVO.primaryRegistrant.primaryPhone ne '' && regVO.primaryRegistrant.primaryPhone ne null}">
													<dsp:getvalueof var="mobNo" value="${regVO.primaryRegistrant.primaryPhone}"></dsp:getvalueof>
												</c:if>
											<dsp:input id="contactPhoneMak"
												bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.primaryPhone"
												type="hidden" name="contactPhoneMak" maxlength="10" value="${mobNo}"
												iclass="${required}">
											</dsp:input>
											
												<input id="contactPhone" placeholder="${phphone}" name="contactPhone" value="${mobNo}" maxlength="14" class="${required} form-control bfh-phone" type="tel" data-format="(ddd) ddd-dddd">
											
										</c:otherwise>
									</c:choose>
								</div> 
							</c:if>
						</div> 
					</div>
				</div>
				
				<c:if test="${inputListMap['networkAffiliation'].isDisplayonForm}">
					<div class="row">
					<div class="columns large-12 cb"> 
					<div class="row padBottom_10">
					<div class="large-12 columns addressFields">
						<label class="inline-rc checkbox" for="networkAff"> 
							<input aria-checked="true" id="networkAff" aria-labelledby="lblnetworkAffiliation" name="optInOutCheck" value="true" type="checkbox">
							<span></span> <bbbt:label key='lbl_update_modal_opt_in_and_out_msg' language="${pageContext.request.locale.language}" />
						</label>
					</div>
					</div> 
					</div>
					</div>
					<dsp:input id="networkAffiliation" bean="GiftRegistryFormHandler.registryVO.networkAffiliation" type="hidden" value="N"/>
				</c:if>
				
				<div class="row">
					<div class="columns large-2 cb">                
						<div class="createRegistry-button">
						<c:set var="createReg"><bbbl:label key="lbl_reg_createRegistry" language ="${pageContext.request.locale.language}"/></c:set>
						     <dsp:input type="submit" id="submitRegistry"
								value="${createReg}"
								bean="GiftRegistryFormHandler.createRegistry"
								iclass="submitRegistry" />
						 <%-- <dsp:input
								bean="GiftRegistryFormHandler.registryCreationSuccessURL"
								type="hidden"
								value="${contextPath}/giftregistry/view_registry_owner.jsp" />
						<dsp:input bean="GiftRegistryFormHandler.registryCreationErrorURL"
								type="hidden"
								value="${contextPath}/giftregistry/simpleReg_creation_form_bbus_mobile.jsp" /> --%>
						<dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="simpleRegformBusMobile" />
	  					<dsp:input bean="GiftRegistryFormHandler.desktop" type="hidden" value="true"/>
						</div>
					</div>
				</div>
		</div>		
				
			</dsp:form>
			<dsp:include page="/giftregistry/frags/simpleReg_regAddModal.jsp"></dsp:include>
</jsp:body>
	</bbb:pageContainer>
	<dsp:include page="/account/idm/idm_login.jsp" />
</dsp:page>