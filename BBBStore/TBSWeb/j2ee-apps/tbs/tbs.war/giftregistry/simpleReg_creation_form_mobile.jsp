<%@ page import="com.bbb.constants.BBBCoreConstants" %>
<dsp:page>
<dsp:importbean bean="/atg/userprofiling/Profile" />
     <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
     <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
     <dsp:importbean bean="/com/bbb/selfservice/StateDroplet" />
     	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" /> 
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryVODroplet" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryTypeNameDroplet"/>
  <dsp:importbean bean="/com/bbb/simplifyRegistry/droplet/SimpleRegFieldsDroplet" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFlyoutDroplet"/>
     <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />   
     	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
     <dsp:importbean bean="/atg/multisite/Site" />
      <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
      <dsp:getvalueof var="isTransient" bean="Profile.transient"/>
         <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
      <dsp:getvalueof bean="SessionBean.registryTypesEvent" id="event"/>
      <dsp:getvalueof var="appid" bean="Site.id" />
        <dsp:getvalueof bean="GiftRegistryFormHandler.registryEventType" var="registryEventTypeName"/> 
	 <c:set var="section" value="browse" scope="request" />
	 <c:set var="sectionName" value="registry" scope="request" />
     <c:set var="pageWrapper" value="createSimpleRegistry useCertonaAjax useFB useAdobeActiveContent" scope="request" />
     <c:set var="titleString" value="Bed Bath & Beyond - Create Registry" scope="request" />
     <c:set var="pageVariation" value="bb" scope="request" />
     <c:set var="topRegistryItemFlag" value="false"/>
     <c:set var="displayFlag" value="true"/>
	<c:set var="BedBathUSSite">
		<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BuyBuyBabySite">
		<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>
    <c:set var="BedBathCanadaSite">
        <bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
         <c:set var="pageVariation" value="bb br" scope="request" />
    </c:set> 
        <c:if test="${appid != 'TBS_BuyBuyBaby'}">
		            <c:set var="topClass" value="usreg" scope="request" />
        </c:if>
        
        <dsp:getvalueof var="contactAddressLine1FromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine1"/>
		<dsp:getvalueof var="contactAddressLine2FromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine2"/>
		<dsp:getvalueof var="contactCityFromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.city"/>
		<dsp:getvalueof var="contactStateFromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.state"/>
		<dsp:getvalueof var="contactZipFromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.zip"/>
		<c:set var="contactAddressLabelFromBean">
			${contactAddressLine1FromBean}<c:if test="${not empty contactAddressLine2FromBean}">, ${contactAddressLine2FromBean}</c:if>, ${contactCityFromBean}, ${contactStateFromBean}, ${contactZipFromBean}"
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
	<bbb:pageContainer section="${section}" titleString="${titleString}" pageVariation="${pageVariation}">
      <jsp:attribute name="bodyClass"> ${topClass} createRegistry createRegistryOnMobile createSimpleRegistryBody useCertonaAjax useFB useAdobeActiveContent</jsp:attribute>
       <dsp:droplet name="GetRegistryTypeNameDroplet">
                        		<dsp:param name="siteId" value="${appid}"/>
							 	<dsp:param name="registryTypeCode" value="${event}"/>
    							<dsp:oparam name="output">
									<dsp:getvalueof var="eventType" param="registryTypeName" scope="request"/>
								</dsp:oparam>
							</dsp:droplet> 
      <jsp:body>

       <%-- <c:choose>
            <c:when test="${minifiedCSSFlag == 'true'}">
                <link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/registry_combined.css?v=${buildRevisionNumber}" />
            </c:when>
            <c:otherwise>
				<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/legacy/bbregistry/css/theme.css?v=${buildRevisionNumber}" />
				<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/legacy/bbbaby/css/theme.css?v=${buildRevisionNumber}" />
				<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/legacy/bbregistry/css/registry.css?v=${buildRevisionNumber}" />
				<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/legacy/bbbaby/css/registry.css?v=${buildRevisionNumber}" />
				<link rel="stylesheet" type="text/css" href="${cssPath}/_assets/tbs_assets/css/legacy/simpleRegistry.css?v=${buildRevisionNumber}" />
			</c:otherwise>
        </c:choose> --%>
        
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

	  <div class="createSimpleRegistry createSimpleRegistryMobile useCertonaAjax useFB useAdobeActiveContent" id="pageWrapper">
      <dsp:getvalueof id="currentSiteId" bean="Site.id" />
			<div class="large-12 row">				
				<div class="pushUp large-12 columns">
					<div id="registryTitles">
						<h2>
							<span class="simpleRegSubText"><bbbl:label key="lbl_create_your_reg" language ="${pageContext.request.locale.language}"/></span>
						</h2>
							
						
							<span id="RegistrySelectType" class="clearfix">
							<dsp:include page= "/giftregistry/simpleReg_type_select_mobile.jsp">
						    	   <dsp:param name="eventType" param="eventType" />
						    	</dsp:include>
							</span>
						
					</div>
				</div>
			</div>
			<c:if test="${event == 'BA1'}">
				 <p class="columns marTop_5 marBottom_5 allFieldReq"> <bbbl:label key="lbl_reg_all_fields_required" language="${pageContext.request.locale.language}" /></p>
			</c:if>
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
								<dsp:getvalueof var="registryInputsByTypeVO" param="registryInputsByTypeVO" />
								<dsp:getvalueof var="registryInputList" param="registryInputList" />
								<dsp:getvalueof var="inputListMap" param="inputListMap" />
					  	</dsp:oparam>
				</dsp:droplet> 
			
			<%-- <dsp:form id="registryFormPost" action="?_DARGS=/store/giftregistry/simpleReg_creation_form.jsp.registryFormPost#" class="cb form post" method="post"> --%>
				 <dsp:form id="registryFormPost" iclass="clearfix cb form post noMarTop" method="post" formid="registryFormPost" autocomplete="off">
							<dsp:input id="registryEventType" bean="GiftRegistryFormHandler.registryEventType" type="hidden" value="${event}" />
			                <dsp:input id="createSimplified" bean="GiftRegistryFormHandler.createSimplified" type="hidden" value="true" />
			                <dsp:input id="coRegEmailFoundPopupStatus" bean="GiftRegistryFormHandler.coRegEmailFoundPopupStatus" type="hidden" name="coRegEmailFoundPopupStatus"/>
			                <dsp:input id="coRegEmailNotFoundPopupStatus" bean="GiftRegistryFormHandler.coRegEmailNotFoundPopupStatus" type="hidden" name="coRegEmailNotFoundPopupStatus"/>
			 
				<div class="row padBottom_10">
					<div class="columns large-6 cb">                
						<label class="padBottom_5"><bbbl:label key="lbl_reg_your_name" language ="${pageContext.request.locale.language}"/></label>
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
									<input type="text" id="fullName" placeholder="<bbbl:label key='lbl_reg_ph_fullname' language ='${pageContext.request.locale.language}'/>" class="required cannotStartWithSpecialChars alphabasicpunc escapeHTMLTag" value="${firstnm} ${lastnm}"/>
							</c:when>
							<c:otherwise>
								<input type="text" id="fullName" placeholder="<bbbl:label key='lbl_reg_ph_fullname' language ='${pageContext.request.locale.language}'/>" class="required cannotStartWithSpecialChars alphabasicpunc escapeHTMLTag" <c:if test="${not empty regVO.primaryRegistrant.firstName}">value="${regVO.primaryRegistrant.firstName} ${regVO.primaryRegistrant.lastName}"</c:if>/>
							</c:otherwise>
							</c:choose>
									</div>
							<div class="row splitName hidden">
								<div class="inputField cb large-6 columns small-12">
									<c:set var="firstName"><bbbl:label key='lbl_reg_ph_firstname' language ='${pageContext.request.locale.language}'/></c:set>
									<c:choose>
									<c:when test="${isTransient}">
										<dsp:input type="text"
												bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.firstName"
												id="txtPrimaryRegistrantFirstName"
												name="txtPrimaryRegistrantFirstNameAltName"
												iclass="required cannotStartWithSpecialChars alphabasicpunc escapeHTMLTag" value="${regVO.primaryRegistrant.firstName}">
				                        <dsp:tagAttribute name="aria-required"
													value="true" />
				                        <dsp:tagAttribute name="aria-labelledby"
													value="txtPrimaryRegistrantFirstNameAlt errortxtPrimaryRegistrantFirstNameAlt" />
				                        <dsp:tagAttribute name="placeholder"
													value="${firstName}" />
				                         </dsp:input>
				                        </c:when>
				                         <c:otherwise>
				                         	<dsp:input type="text"
												id="txtPrimaryRegistrantFirstName"
												value="${firstnm}" 
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
								<div class="inputField cb large-6 columns small-12">
									<c:set var="lastName"><bbbl:label key='lbl_reg_ph_lastname' language ='${pageContext.request.locale.language}'/></c:set>
									<c:choose>
									<c:when test="${isTransient}">
										 <dsp:input type="text"
												bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.lastName" value="${regVO.primaryRegistrant.lastName}"
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
					                  		 <dsp:input type="text"
												id="txtPrimaryRegistrantLastName"
												value="${lastnm}" 
												name="txtPrimaryRegistrantLastNameAltName"
												bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.lastName"
												iclass="borderLeftnone borderRightnone">
				                        <dsp:tagAttribute name="aria-required"
													value="true" />
				                        <dsp:tagAttribute name="aria-labelledby"
													value="lastNameReg errorlastNameReg" />
										<dsp:tagAttribute name="placeholder" value="${lastName}"/>			
				                       </dsp:input>
					                  </c:otherwise>  
					                  </c:choose>
								</div>
								
							</div>
								
						</div> 
					</div>
					
					
					
					<div class="columns large-6 cb">                
						<label class="padBottom_5"><bbbl:label key="lbl_reg_maiden_name" language ="${pageContext.request.locale.language}"/></label>
							<div class="inputField cb">
						<c:set var="regmaidenname">	<bbbl:label key="lbl_reg_ph_maidenname" language ="${pageContext.request.locale.language}"/></c:set>
								<!-- <input type="text" id="maidenName" placeholder="Maiden Name" class="required escapeHTMLTag"> -->
								<dsp:input type="text" id="txtPrimaryRegistrantbabyMaidenName" name="txtPrimaryRegistrantbabyMaidenName" iclass="cannotStartWithSpecialChars alphabasicpunc escapeHTMLTag"  bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.babyMaidenName">
		                        <dsp:tagAttribute name="minlength" value="2"/>
								<dsp:tagAttribute name="aria-required" value="true"/>
		                        <dsp:tagAttribute name="aria-labelledby" value="lastNameReg errorlastNameReg"/>
		                         <dsp:tagAttribute name="placeholder" value="${regmaidenname}"/>
             				  </dsp:input>
							</div>
					</div>
				</div>
				
				
				<div class="row padBottom_10">
					<label class="columns large-12 padBottom_5"><bbbl:label key="lbl_account_info" language ="${pageContext.request.locale.language}"/></label>
						<dsp:getvalueof param="showLegacyPwdPopup" id="showLegacyPwdPopup"/>
							<dsp:getvalueof param="showMigratedPopup" id="showMigratedPopup"/>
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
														<div class="showPassDiv cb"><div class="checker" id="uniform-showPassword" style="display: none;"><span><input name="showPassword" type="checkbox" value="" data-toggle-class="showpassLoginFrag" class="showPassword" id="showPassword" style="opacity: 0;"></span></div>
																		<label for="showPassword" class="lblShowPassword">Show</label>
																	</div>
										<div class="row forgotpassword hidden">
											<a id="forgotPasswordDialogLink" title="Reset&nbsp;Password?" class="small forgot-password popupShipping" onclick="javascript:internalLinks('Forgot Password : Forgot Password Link')" href="/tbs/giftregistry/modals/forgot_password_simplifyRegistry.jsp?omnitureForCheckout=false" data-reveal-ajax="true">Forgot your Password?</a>
										</div>
										<div id="forgotPasswordDialog" class="reveal-modal" data-reveal>
										</div>
										<div class="passwordValidationRule">
									<span><bbbl:label key='lbl_reg_newPassword' language ='${pageContext.request.locale.language}'/></span>
									
								</div>
									</c:when>
									<c:otherwise>
										<dsp:input  id="password" bean="GiftRegistryFormHandler.password" name="passwordReg" beanvalue="Profile.password" iclass="disabled required" type="password" disabled="true"></dsp:input>
									</c:otherwise>
									</c:choose>
									
						  <c:if test="${inputListMap['confirmPassword'].isDisplayonForm && isTransient}"> 
						  <c:set var="required" value=""></c:set> 
							  	<c:if
											test="${inputListMap['confirmPassword'].isMandatoryOnCreate}"> <c:set
												var="required" value="required" />   </c:if>
								<div class="row  confirmPasswordWrap">
									<input id="confirmPassword" placeholder="<bbbl:label key='lbl_reg_ph_confirmpassword' language ='${pageContext.request.locale.language}'/>"
									name="confirmPasswordReg" value="" class="${required}" type="password">
								</div>
								</c:if>
					</div> 
				</div>
				
				<div class="row padBottom_10">
                    <c:if test="${inputListMap['CoRegistrantFirstName'].isDisplayonForm}">                                               
	                   	<div class="columns large-6 cb">
	                   		<label class="padBottom_5"><bbbl:label key="lbl_co_reg" language ="${pageContext.request.locale.language}"/></label>
							<div class="inputField cb" id="regCoRegisName">
	                        	<div class="inputField cb hidden">
	                        		<c:set var="required" value=""></c:set> 
									<c:if test="${inputListMap['CoRegistrantFirstName'].isMandatoryOnCreate}">
										<c:set var="required" value="required" />
									</c:if>
									<c:set var="firstName"><bbbl:label key='lbl_reg_ph_firstname' language ='${pageContext.request.locale.language}'/></c:set>
									<c:choose>
										<c:when test="${regVO.coRegistrant.firstName != '' && regVO.coRegistrant.firstName != null}">
											<input type="text" id="regCoRegisFullName"
											name="regCoRegisFullName"
											placeholder="<bbbl:label key='lbl_reg_ph_fullname' language ='${pageContext.request.locale.language}'/>"
											class="${required} cannotStartWithSpecialChars alphabasicpunc escapeHTMLTag"
											autocomplete="off" value="${regVO.coRegistrant.firstName} ${regVO.coRegistrant.lastName}">
										</c:when>
										<c:otherwise>
											<input type="text" id="regCoRegisFullName"
											name="regCoRegisFullName"
											placeholder="<bbbl:label key='lbl_reg_ph_fullname' language ='${pageContext.request.locale.language}'/>"
											class="${required} cannotStartWithSpecialChars alphabasicpunc escapeHTMLTag"
											autocomplete="off">
										</c:otherwise>
									</c:choose>
	                        		
	                            </div>
								<div class="row splitName">
									<div class="inputField cb large-6 small-12 columns">
										<dsp:input id="regCoRegisFirstName"
											name="txtCoRegistrantFirstNameName"
											value="${regVO.coRegistrant.firstName}"
											bean="GiftRegistryFormHandler.registryVO.coRegistrant.firstName"
											iclass="${required} cannotStartWithSpecialChars alphabasicpunc escapeHTMLTag"
											type="text">
											<dsp:tagAttribute name="autocomplete" value="off"/>
											<dsp:tagAttribute name="aria-required" value="true" />
											<dsp:tagAttribute name="aria-labelledby" value="lbltxtCoRegistrantFirstName errortxtCoRegistrantFirstName" />
											<dsp:tagAttribute name="placeholder" value="${firstName}" />
										</dsp:input>
					               	</div>
					               	<c:if test="${inputListMap['CoRegistrantLastName'].isDisplayonForm}">	
										<c:set var="required" value=""></c:set>
										<c:if test="${inputListMap['CoRegistrantLastName'].isMandatoryOnCreate}">
											<c:set var="required" value="required" />
										</c:if>
										<c:set var="lastName"><bbbl:label key='lbl_reg_ph_lastname' language ='${pageContext.request.locale.language}'/></c:set> 	
										<div class="inputField cb large-6 small-12 columns">
					                    	<dsp:input id="regCoRegisLastName"
												name="txtCoRegistrantLastNameName" value="${regVO.coRegistrant.lastName}"
												bean="GiftRegistryFormHandler.registryVO.coRegistrant.lastName"
												iclass="${required} cannotStartWithSpecialChars alphabasicpunc escapeHTMLTag"
												type="text">
												<dsp:tagAttribute name="autocomplete" value="off"/>
												<dsp:tagAttribute name="maxlength" value="30"/>	
												<dsp:tagAttribute name="minlength" value="2"/>	
	                                            <dsp:tagAttribute name="aria-required" value="true" />
	                                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtCoRegistrantLastName errortxtCoRegistrantLastName" />
	                                            <dsp:tagAttribute name="placeholder" value="${lastName}" />
											</dsp:input>
					               		</div>
				               		</c:if>
			               		</div>
	               			</div>
	               	    </div>
               	    </c:if>
               	  <c:if test="${inputListMap['CoRegistrantEmail'].isDisplayonForm}">
			      	  <c:set var="required" value=""></c:set>
			      	  <c:if test="${inputListMap['CoRegistrantEmail'].isMandatoryOnCreate}">
			      	  	<c:set var="required" value="required" />
			      	  </c:if>     
	                  <div class="columns large-6 cb">
	                  	<label class="padBottom_5"><bbbl:label key="lbl_coReg_email" language ="${pageContext.request.locale.language}"/></label>
	                  		<div class="row">
	                  			<div class="inputField cb large-6 columns">
	                  				<c:set var="coregemail"><bbbl:label key='lbl_reg_ph_coregemail' language ='${pageContext.request.locale.language}'/></c:set>
									<dsp:input id="coRegisEmailField"
										bean="GiftRegistryFormHandler.registryVO.coRegistrant.email"
										name="emailRegCo" value="${regVO.coRegistrant.email}" iclass="${required} email widthHundred escapeHTMLTag" type="text">
										<dsp:tagAttribute name="autocomplete" value="off"/>
										<dsp:tagAttribute name="aria-required" value="true" />
										<dsp:tagAttribute name="aria-labelledby" value="lbltxtCoRegisEmailField errortxtCoRegisEmailField" />
										<dsp:tagAttribute name="placeholder" value="${coregemail}" />
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
					<c:set var="phdate"><bbbl:label key='lbl_reg_ph_date' language ='${pageContext.request.locale.language}'/></c:set>
					<div class="columns">                
						<div class="row">
						  <c:if test="${inputListMap['showerDate'].isDisplayonForm}"> 
						  <c:set var="required" value=""></c:set> 
						  	<c:if test="${inputListMap['showerDate'].isMandatoryOnCreate}"> <c:set var="required" value="required"/>  </c:if> 	
							<div class="inputField cb columns eventsDate marBottom_5">
								<!-- <label class="padBottom_5" for="showerDate">shower date</label>
								<input id="showerDate" placeholder="mm/dd/yyyy" name="showerDate" value="" class="required" type="text">
									<span class="icon-calendar" aria-hidden="true" id="showerDateButton"></span> -->
									<label class="padBottom_5" for="showerDate"><bbbl:label key="lbl_reg_shower_date" language ="${pageContext.request.locale.language}"/></label>
								<!-- <input id="showerField" placeholder="${phdate}" name="showerDate" value="" class="required" type="text"> -->
								<div class="inputWithCalender small-6">
									<dsp:input id="showerField"
		                                       bean="GiftRegistryFormHandler.registryVO.event.showerDate"
		                                            type="text" name="showerDate" readonly='true'  value="${regVO.event.showerDate}" iclass="${required}">
		                                            <dsp:tagAttribute name="aria-required" value="true"/>
		                                            <%-- <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryExpectedDate${CADate} errortxtRegistryExpectedDate${CADate}"/> --%>
		                                       		 <dsp:tagAttribute name="placeholder" value="${phdate}"/>
										<span class="icon-calendar" aria-hidden="true" id="showerDateButton"></span>
									</dsp:input>
								</div>	
							</div>
							</c:if>
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
                         <div class="inputField cb  columns eventsDate marBottom_5">
								<label class="padBottom_5" for="arrivalDate">${eventLabelHolder}</label>
								<div class="inputWithCalender small-6">
									<dsp:input id="eventField"
	                                           bean="GiftRegistryFormHandler.registryVO.event.eventDate"
	                                                type="text" name="arrivalDate" readonly='true'  iclass="${required}">
	                                                <dsp:tagAttribute name="aria-required" value="true"/>
	                                                <%-- <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryExpectedDate${CADate} errortxtRegistryExpectedDate${CADate}"/> --%>
	                                           		 <dsp:tagAttribute name="placeholder" value="${phdate}"/>
										<span class="icon-calendar" aria-hidden="true" id="registryExpectedDateButton"></span>
									</dsp:input>
								</div>	
							</div>
                            </c:if> 
							 <%--  <c:if test="${inputListMap['babyExpectedArivalDate'].isDisplayonForm || inputListMap['eventDate'].isDisplayonForm}"> 
						  		<c:if test="${inputListMap['babyExpectedArivalDate'].isMandatoryOnCreate}"> <c:set var="required" value="required"/>  </c:if> 							
							<div class="inputField cb large-6 columns eventsDate">
								<label class="padBottom_5" for="arrivalDate"><bbbl:label key="lbl_reg_expt_arrival_date" language ="${pageContext.request.locale.language}"/></label>
								<!-- <input id="eventField" placeholder="mm/dd/yyyy" name="arrivalDate" value="" class="required" type="text"> -->
								<dsp:input id="eventField"
                                           bean="GiftRegistryFormHandler.registryVO.event.eventDate"
                                                type="text"  readonly='true' name="arrivalDate"  iclass="${required}">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryExpectedDate${CADate} errortxtRegistryExpectedDate${CADate}"/>
                                           		 <dsp:tagAttribute name="placeholder" value="${phdate}"/>
									<span class="icon-calendar" aria-hidden="true" id="registryExpectedDateButton"></span>
									</dsp:input>
							</div>
							</c:if> --%>
						</div>
					</div>
					<c:if test="${inputListMap['babyGender'].isDisplayonForm}"> 
					<c:set var="required" value=""></c:set> 
						  		<c:if test="${inputListMap['babyGender'].isMandatoryOnCreate}"> <c:set var="required" value="required"/>  </c:if> 		
						<div class="columns large-6 cb">
							<label class="padBottom_5" for="genderField"><bbbl:label key="lbl_do_you_know_babyGender" language ="${pageContext.request.locale.language}"/></label>					
							<div class="surpriseRadios surpriseRadiosHolder row"> 
								<div class="radio columns large-6" id="uniform-sBabyGender">
									<span>
										<input type="radio" name="knowBabyGender" class="${required} knowBabyGender" id="sBabyGender" style="opacity: 0;" autocomplete="off">
										<label for="sBabyGender" class="sGenderLabel">
										<bbbl:label key="lbl_reg_yes" language ="${pageContext.request.locale.language}"/>
										</label>
									</span>
								</div>   
								<div class="radio columns large-6" id="uniform-surprise">
									<span>
										<input id="surprise" name="knowBabyGender" value="S" class="${required} knowBabyGender" type="radio" style="opacity: 0;" autocomplete="off">
										<label for="surprise" class="surpriseLabel"> 
										 <bbbl:label key="lbl_reg_its_surprise" language ="${pageContext.request.locale.language}"/>
										</label>
									</span>
								</div>
								<input name="_D:knowBabyGender" value=" " type="hidden"> 
							</div>
							<div class="customRadios customRadiosHolder row">
								<div class="radio columns large-3" id="uniform-boy">
									<span>
										<input id="boy" name="boyOrGirl" value="B" class="${required} boyOrGirl" type="radio" style="opacity: 0;">
										<label for="boy" class="labelContent"> 
										 <bbbl:label key="lbl_reg_boy" language ="${pageContext.request.locale.language}"/>
										</label>
									</span>
								</div>   
								<div class="radio columns large-3" id="uniform-girl">
									<span>
										<input id="girl" name="boyOrGirl" value="G" class="${required} boyOrGirl" type="radio" style="opacity: 0;">
										<label for="girl" class="labelContent"> 
										  <bbbl:label key="lbl_reg_girl" language ="${pageContext.request.locale.language}"/>
										</label>
									</span>
								</div>
								<input name="_D:boyOrGirl" value=" " type="hidden">
							</div>
							<dsp:input name="doYouKnowBabyGender" id= "doYouKnowBabyGender" value=" " type="hidden" bean="GiftRegistryFormHandler.registryVO.event.babyGender"/>
						</div>
					</c:if>
				</div>
				<div class="row padBottom_10">
			 	
			 	 <c:if test="${regVO.primaryRegistrant.contactAddress.addressLine1 ne '' && regVO.primaryRegistrant.contactAddress.addressLine1 ne null}">
							<c:set var="address1" value="${regVO.primaryRegistrant.contactAddress.addressLine1}" />
								<c:set var="address2" value="${regVO.primaryRegistrant.contactAddress.addressLine2}" />
								<c:set var="profileShippingAddr">
									${address1}<c:if test="${not empty address2}"> ,${address2}</c:if>, ${regVO.primaryRegistrant.contactAddress.city}, ${regVO.primaryRegistrant.contactAddress.state}, ${regVO.primaryRegistrant.contactAddress.zip}
								</c:set>
						<dsp:setvalue
						bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine1"
						value="${regVO.primaryRegistrant.contactAddress.addressLine1}" />
					<dsp:setvalue
						bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine2"
						value="${regVO.primaryRegistrant.contactAddress.addressLine2}" />
					<dsp:setvalue
						bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.city"
						value="${regVO.primaryRegistrant.contactAddress.city}" />
					<dsp:setvalue
						bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.state"
						value="${regVO.primaryRegistrant.contactAddress.state}" />
					<dsp:setvalue
						bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.zip"
						value="${regVO.primaryRegistrant.contactAddress.zip}" />		
			</c:if>
					<c:if test="${!isTransient && (profileShippingAddr eq '' || profileShippingAddr eq null)}"> 
					<dsp:getvalueof var="address1" bean="Profile.shippingAddress.address1" />
					       <dsp:getvalueof var="address2" bean="Profile.shippingAddress.address2" />  
					    <c:set var="profileShippingAddr">
					    <dsp:valueof bean="Profile.shippingAddress.address1" /><c:if test="${not empty address2}">, ${address2}</c:if>, <dsp:valueof bean="Profile.shippingAddress.city" />, <dsp:valueof bean="Profile.shippingAddress.state" />, <dsp:valueof
					                                bean="Profile.shippingAddress.PostalCode" />
					                            </c:set>
					           <dsp:setvalue
									bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine1"
									beanvalue="Profile.shippingAddress.address1" />
								<dsp:setvalue
									bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine2"
									beanvalue="Profile.shippingAddress.address2" />
								<dsp:setvalue
									bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.city"
									beanvalue="Profile.shippingAddress.city" />
								<dsp:setvalue
									bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.state"
									beanvalue="Profile.shippingAddress.state" />
								<dsp:setvalue
									bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.zip"
									beanvalue="Profile.shippingAddress.PostalCode" />                 
					   </c:if>
  					<c:if test="${!inputListMap['useContactAddrAsShippingAddr'].isDisplayonForm}">
						<c:set var="cAddr" value="cAddr"/>
						<c:set var="sAddr" value="sAddr"/>
						<c:set var="sApt" value="sApt"/>
					</c:if>
					<div class="inputField cb large-6 columns">
					<div id="regContactAddrLine1">
					<c:if test="${inputListMap['showContactAddress'].isDisplayonForm || inputListMap['showShippingAddress'].isDisplayonForm}">
						<label id="lbltxtRegistryRegistrantContactAddress1"
								for="txtRegistryRegistrantContactAddress" class="padBottom_5">
									<bbbl:label key="lbl_reg_contactAddress" language ="${pageContext.request.locale.language}"/> </label>
					</c:if>
					<c:if test="${inputListMap['showContactAddress'].isDisplayonForm }"> 
					<c:set var="required" value=""></c:set>  
					 <c:if test="${inputListMap['showContactAddress'].isMandatoryOnCreate}"> <c:set var="required" value="required"/>  </c:if> 	
						<c:set var="contactadd"><bbbl:label key="lbl_reg_ph_contactAdress" language ="${pageContext.request.locale.language}"/></c:set>
						<c:choose>
								<c:when test="${!isTransient && address1 ne '' && address1 ne null}">
								 <input tabindex="1" id="txtRegistryRegistrantContactAddress"
		                            type="text" aria-labelledby=""
		                            name="txtRegistryRegistrantContactAddressName"
		                            class="cannotStartWithWhiteSpace QASAddress1  escapeHTMLTag ${required} ${cAddr} ${iclassValue}" maxlength="120" value="${profileShippingAddr}" autocomplete="off">
		                       	<%--  <dsp:tagAttribute name="aria-required" value="true"/>
		                       	 <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactAddress errortxtRegistryRegistrantContactAddress"/> --%>
		                        </c:when>
			                    <c:otherwise>
	                   				 <input tabindex="1" id="txtRegistryRegistrantContactAddress"
	                          	 		 type="text"
			                            name="txtRegistryRegistrantContactAddress"
			                            class="cannotStartWithWhiteSpace QASAddress1  escapeHTMLTag ${required} ${cAddr} ${iclassValue}" maxlength="120" autocomplete="off" placeholder="${contactadd}" <c:if test="${not empty contactAddressLine1FromBean}">value="${contactAddressLabelFromBean}"</c:if>>
			                       <%--  <dsp:tagAttribute name="aria-required" value="true"/>
			                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryRegistrantContactAddress1 errortxtRegistryRegistrantContactAddress1"/>
			                         <dsp:tagAttribute name="placeholder" value="${contactadd}"/> --%>
                   			 </c:otherwise>
		                        </c:choose>
		                  	      
		                  <%-- </c:if> --%>
						
						<dsp:getvalueof var="secondaryAddressId" bean="Profile.shippingAddress.repositoryId" />
					
						<c:choose>
							<c:when test="${not empty secondaryAddressId}">
								<c:set var="contactAddrId" value="${secondaryAddressId}"/>
							</c:when>
							<c:otherwise>
								<c:set var="contactAddrId" value="newPrimaryRegAddress"/>
							</c:otherwise>
						</c:choose>
					
						<dsp:input type="hidden" bean="GiftRegistryFormHandler.regContactAddress" id="newPrimaryRegAddress" value="${contactAddrId}"/>
		                
		                <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.firstName" id="contactFirstName" iclass="QASFirstName"/>
						<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.lastName" id="contactLastName" iclass="QASLastName"/>
		                
		                <input name="_D:txtRegistryRegistrantContactAddressName" value=" " type="hidden">
						<dsp:input type="hidden" id="txtRegistryRegistrantContactAddress1"
							bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine1" iclass="address1"/>
						<dsp:input type="hidden" id="txtRegistryRegistrantContactAddress2"
							bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine2" iclass="address2"/>
						<dsp:input type="hidden" id="txtRegistryRegistrantContactCity"
							bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.city" iclass="QASCity"/>
						<dsp:input type="hidden" id="selRegistryRegistrantContactState"
							bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.state" iclass="QASState"/>
						<dsp:input type="hidden" id="txtRegistryRegistrantContactZip"
							bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.zip" iclass="QASZip"/>
							<input type="hidden" name="txtRegistryRegistrantBtn" id="txtRegistryRegistrantBtn" value=""	 />
							<input type="hidden" name="isQASValidate" id="isQASValidate" value="true"	 />
					</c:if>
					</div>
					<c:if test="${inputListMap['showShippingAddress'].isDisplayonForm && inputListMap['useContactAddrAsShippingAddr'].isDisplayonForm && inputListMap['showContactAddress'].isDisplayonForm }">
							<c:set var="required" value=""></c:set> 
								 	<c:if test="${inputListMap['showShippingAddress'].isMandatoryOnCreate}"> <c:set var="required" value="required"/>  </c:if> 		
						<div class="row">
							<div class="large-12 columns addressFields">
								<label class="inline-rc checkbox" for="radRegistryCurrentShippingAddress"> 
									<input 
												id="radRegistryCurrentShippingAddress"
												name="radRegistryCurrentShippingAddressName" 
												value="shipAdrressSameAsRegistrant" type="checkbox"
												checked="checked"  aria-labelledby="lblradRegistryCurrentShippingAddress" >
												<%-- <dsp:tagAttribute
													name="aria-required" value="true" />
													<dsp:tagAttribute
													name="aria-checked" value="true" />
		                                        <dsp:tagAttribute
													name="aria-labelledby"
													value="lblradRegistryCurrentShippingAddress" /> --%>
													
													<dsp:input 
												id="radRegistryCurrentShippingAddressReg"
												name="radRegistryCurrentShippingAddressReg" bean="GiftRegistryFormHandler.shippingAddress"
												 type="hidden"	value="shipAdrressSameAsRegistrant" />
												<span></span>
												 <bbbl:label key="lbl_reg_useAsShpiping" language ="${pageContext.request.locale.language}"/> <br/> <bbbl:label key="lbl_reg_send_gifts" language ="${pageContext.request.locale.language}"/>
								</label>
							</div>
						</div>
					</c:if>
					<c:if test="${inputListMap['showShippingAddress'].isDisplayonForm}">	
						<c:set var="shipAdd"><bbbl:label key="lbl_reg_ph_shippingAddress" language ="${pageContext.request.locale.language}"/></c:set>
						<c:set var="required" value=""></c:set> 
								 	<c:if test="${inputListMap['showShippingAddress'].isMandatoryOnCreate}"> <c:set var="required" value="required"/>  </c:if>
						<c:choose>
							<c:when test="${inputListMap['showShippingAddress'].isDisplayonForm && inputListMap['useContactAddrAsShippingAddr'].isDisplayonForm && inputListMap['showContactAddress'].isDisplayonForm}">
								<c:set var="splitShipAddress" value="splitShipAddress"/>
							</c:when>
							<c:otherwise>
								<dsp:input id="radRegistryCurrentShippingAddressReg"  bean="GiftRegistryFormHandler.shippingAddress"
									 type="hidden"	value="newShippingAddress" />
							</c:otherwise>
						</c:choose>
						<div class="row ${splitShipAddress}" id="shippingNewAddrLine1">
							<div class="inputField cb large-6 small-6 columns ${sAddr} combineInput">
						<input type="text" name="txtRegistryCurrentShippingAddressName"
					id="txtRegistryCurrentShippingAddress"
					class="${required} cannotStartWithWhiteSpace QASAddress1 escapeHTMLTag ${iclassValue}"
					checked="true" maxlength="120" placeholder="${shipAdd}" <c:if test="${address1 ne '' && address1 ne null}">value="${profileShippingAddr}"</c:if>>
					</div>
					<div class="inputField cb large-6 small-6 columns ${sApt}">
					<c:set var="aptBuild"><bbbl:label key="lbl_reg_ph_apt_building" language ="${pageContext.request.locale.language}"/></c:set>
					<dsp:input id="txtRegistryCurrentShippingAddressApt" maxlength="30" name="txtRegistryCurrentShippingAddress1Name" value=""
								 iclass="cannotStartWithWhiteSpace escapeHTMLTag"  bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine2"
								 type="text"  autocomplete="off">
									 <dsp:tagAttribute name="aria-required" value="true"/>
                                      <dsp:tagAttribute name="placeholder" value="${aptBuild}"/>
								</dsp:input><%-- <dsp:tagAttribute name="aria-checked" value="true" />
					<dsp:tagAttribute name="aria-labelledby"
						value="lblradRegistryCurrentShippingAddress errortxtRegistryCurrentShippingAddress" />
					<dsp:tagAttribute name="placeholder" value="${shipAdd}" /> --%>
				</input>
							</div>	
									
						<%--	<div class="inputField cb large-6 columns">
								<input id="apartmentNum" placeholder="Apt/Bldg (Optional)" name="apartmentNum" value="" class="" type="text">
							</div>
							--%>
							
							<c:if test="${! isTransient}">
				                       <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.firstName" beanvalue="Profile.firstName"/>
							<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.lastName" beanvalue="Profile.lastName"/>
				                     </c:if>
							<dsp:input type="hidden" bean="GiftRegistryFormHandler.shippingAddress" id="shippingAddressSelect"/>
				            <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.firstName" id="shipFirstName" iclass="QASFirstName"/>
				            <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.lastName" id="shipLastName" iclass="QASLastName"/>
				            <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine1" id="txtRegistryCurrentShippingAddress1" iclass="address1"/>
							<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine2" id="txtRegistryCurrentShippingAddress2" iclass="address2"/>
							<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.city" id="txtRegistryCurrentShippingCity" iclass="QASCity"/>
							<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.state" id="selRegistryCurrentShippingState"  iclass="QASState"/>
							<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.zip" id="txtRegistryCurrentShippingZip" iclass="QASZip"/>
							<input type="hidden" id="txtRegistryCurrentShippingBtn"/>
							<input type="hidden" name="isCurrentShippingQASValidate" id="isCurrentShippingQASValidate" value="true"	 />
						</div>
					</c:if>
						<c:if
										test="${inputListMap['futureShippingDate'].isDisplayonForm || inputListMap['showFutureShippingAddr'].isDisplayonForm}">
						<div class="row">
							<div class="large-12 columns addressFields">
								<label class="inline-rc checkbox" for="radRegistryFutureShippingAddressName">
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
							</div>
						</div>
						<c:set var="required" value=""></c:set> 
						<c:if
											test="${inputListMap['futureShippingDate'].isMandatoryOnCreate}"> <c:set
												var="required" value="required" />   </c:if> 
						<div class="row hidden">
							<div class="cb large-6 columns eventsDate futureField">
								<c:set var="futuredate"><bbbl:label key='lbl_reg_ph_futuredate' language ='${pageContext.request.locale.language}'/></c:set>
							<!-- 	<label class="padBottom_5 hidden" for="futureDate">future date</label> -->
								<%-- <input id="futureField" placeholder="Future Date" name="futureDate" value="" class="${required}" type="text"> --%>
								<div class='inputWithCalender'>
									<dsp:input id="futureField" 
														name="futureDate" value="" iclass="${required}" type="text"
														bean="GiftRegistryFormHandler.registryVO.shipping.futureShippingDate"
														readonly="true">
														<dsp:tagAttribute name="placeholder" value="${futuredate}" />
										<span class="icon-calendar" aria-hidden="true" id="futureDateButton"></span>
										</dsp:input>
								</div>		
							</div>
						</div>
						<c:set var="required" value=""></c:set> 
						<c:if
											test="${inputListMap['showFutureShippingAddr'].isMandatoryOnCreate}"> <c:set
												var="required" value="required" />   </c:if> 
						<div class="row hidden futureAddField">
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
									<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.city" id="txtSimpleRegFutureShippingCity" iclass="QASCity"/>
									<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.state" id="txtSimpleRegFutureShippingState" iclass="QASState"/>
									<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.futureshippingAddress.zip" id="txtSimpleRegFutureShippingZip"  iclass="QASZip"/>
									<input type="hidden" id="txtRegistryFutureShippingBtn"/>
									<input type="hidden" name="isFutureShippingQASValidate" id="isFutureShippingQASValidate" value="true"	 />
								</div>
						</div>
						</c:if>
					</div>
				
					<div class="columns large-6 cb">                
						<div class="row">	
						 <c:if test="${inputListMap['PhoneNumber'].isDisplayonForm}">	
						 <c:set var="required" value=""></c:set> 
							 	<c:if test="${inputListMap['PhoneNumber'].isMandatoryOnCreate}"> <c:set var="required" value="required"/>  </c:if> 	
							<div class="inputField cb large-6 columns">
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
												<input id="contactPhone" placeholder="${phphone}" name="contactPhone" value="${mobNo}" maxlength="14" class="${required} form-control bfh-phone" type="tel" data-format="(ddd) ddd-dddd">
											
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
							 <c:if test="${inputListMap['MobileNumber'].isDisplayonForm}">	
							 <c:set var="required" value=""></c:set> 
							 	<c:if test="${inputListMap['MobileNumber'].isMandatoryOnCreate}"> <c:set var="required" value="required"/>  </c:if> 
									<div class="inputField cb large-6 columns">
										<c:set var="phphone"><bbbl:label key='lbl_reg_ph_phone' language ='${pageContext.request.locale.language}'/></c:set>
										<input type="hidden" id="requiredBccMob" value="${inputListMap['MobileNumber'].isMandatoryOnCreate}">
										<label class="padBottom_5" for="cellPhone"><bbbl:label key="lbl_reg_cell_no" language ="${pageContext.request.locale.language}"/></label>
										<c:choose>
											<c:when test="${isTransient}"> 
											<dsp:input id="mobilePhoneMask" 
												bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.cellPhone"
												 name="mobilePhoneMask" maxlength="10"
												iclass="${required}" type="hidden">
												</dsp:input>
												<input id="mobilePhone" name="mobilePhone" value=""  maxlength="14" placeholder="${phphone}" 
												class="phone phoneField widthHundred phoneNumberMask  ${required} escapeHTMLTag valid form-control bfh-phone" type="tel" data-format="(ddd) ddd-dddd" aria-labelledby="errortxtRegistrantMobileNumber">
																
										 </c:when>
											<c:otherwise>
											<dsp:getvalueof var="mobNo" bean="Profile.mobileNumber"></dsp:getvalueof>
											<c:if test="${regVO.primaryRegistrant.cellPhone ne '' && regVO.primaryRegistrant.cellPhone ne null}">
													<dsp:getvalueof var="mobNo" value="${regVO.primaryRegistrant.cellPhone}"></dsp:getvalueof>
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
					</div>
				</div>
				<dsp:include page="simpleReg_store_form.jsp">
                    <dsp:param name="siteId" value="${appid}" />
                    <dsp:param name="inputListMap" value="${inputListMap}"/>
                </dsp:include>
				<div class="row padBottom_10">
					<div class="inputField columns large-6 cb">                
						<%-- <label class="padBottom_10" for="favStoreId"><bbbl:label key="lbl_registry_store_favorite_store" language ="${pageContext.request.locale.language}"/></label>
						<div class="row padBottom_10">
							<div class="inputField cb large-12 columns fav-store-text">
								<span>You have not selected a Favorite Store.</span>
							</div>
						</div>
						<div class="row">
							<div class="inputField cb large-12 columns">
								<input id="favStoreId" placeholder="Enter city and State or Zip Code" name="favStoreId" value="" class="required" type="text">
								<span class="txtFindStoresearch"><span class="icon icon-search block" aria-hidden="true"></span></span>
							</div>			
						</div> --%>
						
						<dsp:include page="/giftregistry/simpleReg_emailOptIn_form.jsp"></dsp:include>   
						<%-- <div class="row padBottom_10">
							<div class="large-12 columns emailOffer addressFields ">
								<label class="inline-rc checkbox" for="exclusiveEmailOffer"> 
									<input aria-checked="true" id="exclusiveEmailOffer" aria-labelledby="lblexclusiveEmailOffer" name="exclusiveEmailOffer" value="exclusiveEmailOffer" type="checkbox" checked="checked" style="opacity: 0;"><span></span> <bbbt:textArea key="txt_exclusiveEmailOffer" language ="${pageContext.request.locale.language}"/>
								</label>
							</div>
						</div> --%>
						
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
						
						<div class="row padBottom_10">
							<div class="columns large-4 cb">                
								<div class="createRegistry-button">
								<c:set var="createReg"><bbbl:label key="lbl_reg_createRegistry" language ="${pageContext.request.locale.language}"/></c:set>
								  <!--  <input id="submitRegistry" name="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler.createRegistry" value="Create Registry" tabindex="11" class="submitRegistry" type="button">
									 --> <dsp:input type="submit" id="submitRegistry" value="${createReg}" bean="GiftRegistryFormHandler.createRegistry" iclass="submitRegistry"/>
						 <%-- <dsp:input bean="GiftRegistryFormHandler.registryCreationSuccessURL" type="hidden" value="${contextPath}/giftregistry/view_registry_owner.jsp"/>
						<dsp:input bean="GiftRegistryFormHandler.registryCreationErrorURL" type="hidden" value="${contextPath}/giftregistry/simpleReg_creation_form.jsp"/> --%>
						<dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="simpleRegCreationFormMob" />
	  					<dsp:input bean="GiftRegistryFormHandler.desktop" type="hidden" value="true"/>
								</div>
							</div>
						</div>
					</div>
					<!-- <div class="inputField columns large-6 cb nurseryTheme">                
						<label class="padBottom_5" for="nurseryTheme">nursery decor or theme</label>
						<div class="row">
							<div class="inputField cb large-4 columns">
								<input id="nurseryTheme" placeholder="Nursery Decor" name="nurseryTheme" value="" class="required" type="text">
							</div>
						</div>
					</div> -->
					
				</div>
				
				
			</dsp:form>
			<dsp:include page="/giftregistry/frags/simpleReg_regAddModal.jsp"></dsp:include>
	  </div>
</jsp:body>
</bbb:pageContainer>
	<dsp:include page="/account/idm/idm_login.jsp" />
</dsp:page>
<dsp:droplet name="/com/bbb/account/droplet/BBBConfigKeysDroplet">
        <dsp:param name="configKey" value="QASKeys"/>
        <dsp:oparam name="output">
            <dsp:getvalueof var="qasConfigMap" param="configMap"/>
        </dsp:oparam>
    </dsp:droplet>
<dsp:include page="/_includes/modals/qasModal.jsp" />
<c:choose>
    <c:when test="${minifiedJSFlag}">
        <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/tbsqas.min.js?v=${buildRevisionNumber}"></script>
    </c:when>
    <c:otherwise>
        <script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/tbsqas.js?v=${buildRevisionNumber}"></script>
    </c:otherwise>
</c:choose>
<script type="text/javascript">
    if(QAS_Variables) {
        <c:choose>
            <c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
                QAS_Variables.DEFAULT_DATA = "CAN";
            </c:when>
            <c:otherwise>
                QAS_Variables.DEFAULT_DATA = "USA";
            </c:otherwise>
        </c:choose>
        BBB.executeQASValidation = ${qasConfigMap[currentSiteId]};

    }
</script>