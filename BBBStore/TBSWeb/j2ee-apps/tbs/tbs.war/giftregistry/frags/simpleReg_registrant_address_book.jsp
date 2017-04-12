<dsp:page>
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
		<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
		<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
		 <dsp:droplet name="IsEmpty">
			<dsp:param name="value" bean="GiftRegistryFormHandler.formExceptions"/>
			<dsp:oparam name="false">
				 <dsp:setvalue param="regVO" beanvalue="SessionBean.simplifyRegVO" /> <dsp:getvalueof var="regVO" bean="SessionBean.simplifyRegVO" />
			</dsp:oparam>
		</dsp:droplet>
		<c:set var="shipTo_POBoxOn" scope="request"><bbbc:config key="shipTo_POBoxOn" configName="FlagDrivenFunctions"/></c:set>
		<c:choose>
		<c:when test="${not empty shipTo_POBoxOn &&  shipTo_POBoxOn && (currentSiteId ne BedBathCanadaSite)}">
				<c:set var="iclassValue" value=""/>
			</c:when>
			<c:otherwise>
				<c:set var="iclassValue" value="delayPoBoxAddNotAllowed"/>
			</c:otherwise>
		</c:choose>
	    <c:if test="${regVO.primaryRegistrant.contactAddress.addressLine1 ne '' && regVO.primaryRegistrant.contactAddress.addressLine1 ne null}">
				<c:set var="address1" value="${regVO.primaryRegistrant.contactAddress.addressLine1}" />
					<c:set var="address2" value="${regVO.primaryRegistrant.contactAddress.addressLine2}" />
					<c:set var="profileShippingAddr">
						${address1}<c:if test="${not empty address2}"> ,${address2}</c:if>, ${regVO.primaryRegistrant.contactAddress.city}, ${regVO.primaryRegistrant.contactAddress.state}, ${regVO.primaryRegistrant.contactAddress.zip}
					</c:set>
			<dsp:getvalueof var="shippingAddressLine1FromBean" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine1"/>
			<dsp:getvalueof var="shippingAddressLine2FromBean" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine2"/>
			<dsp:getvalueof var="shippingCityFromBean" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.city"/>
			<dsp:getvalueof var="shippingStateFromBean" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.state"/>
			<dsp:getvalueof var="shippingZipFromBean" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.zip"/>
			
			<dsp:getvalueof var="contactAddressLine1FromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine1"/>
			<dsp:getvalueof var="contactAddressLine2FromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine2"/>
			<dsp:getvalueof var="contactCityFromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.city"/>
			<dsp:getvalueof var="contactStateFromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.state"/>
			<dsp:getvalueof var="contactZipFromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.zip"/>
			<c:set var="contactAddressLabelFromBean">
			${contactAddressLine1FromBean}<c:if test="${not empty contactAddressLine2FromBean}">, ${contactAddressLine2FromBean}</c:if>, ${contactCityFromBean}, ${contactStateFromBean}, ${contactZipFromBean}"
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
			<dsp:setvalue
			bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine1"
			value="${regVO.primaryRegistrant.contactAddress.addressLine1}" />
		<dsp:setvalue
			bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine2"
			value="${regVO.primaryRegistrant.contactAddress.addressLine2}" />
		<dsp:setvalue
			bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.city"
			value="${regVO.primaryRegistrant.contactAddress.city}" />
		<dsp:setvalue
			bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.state"
			value="${regVO.primaryRegistrant.contactAddress.state}" />
		<dsp:setvalue
			bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.zip"
			value="${regVO.primaryRegistrant.contactAddress.zip}" />	
		</c:if>
	<c:set var="regContactAddrRadioBtn" value="true" scope="page" />
<c:if test="${!isTransient && (profileShippingAddr eq '' || profileShippingAddr eq null)}">
	<dsp:getvalueof var="address1" bean="Profile.shippingAddress.address1" />
		<dsp:getvalueof var="address2" bean="Profile.shippingAddress.address2" />
		<dsp:getvalueof var="city" bean="Profile.shippingAddress.city" />
		<dsp:getvalueof var="state" bean="Profile.shippingAddress.state" />
		<dsp:getvalueof var="zip" bean="Profile.shippingAddress.PostalCode" />
		<c:set var="profileShippingAddr">
			<dsp:valueof bean="Profile.shippingAddress.address1" /><c:if test="${not empty address2}">,${address2}</c:if>, <dsp:valueof bean="Profile.shippingAddress.city" />, <dsp:valueof bean="Profile.shippingAddress.state" />,<dsp:valueof
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
	<div id="regContactAddrLine1">
	<c:if test="${inputListMap['showContactAddress'].isDisplayonForm || inputListMap['showShippingAddress'].isDisplayonForm}">
		<label id="lbltxtRegistryRegistrantContactAddress1"
				for="txtRegistryRegistrantContactAddress" class="padBottom_5">
					<bbbl:label key="lbl_reg_Address" language ="${pageContext.request.locale.language}"/> </label>
	</c:if>
	<c:if test="${inputListMap['showContactAddress'].isDisplayonForm}">
	   <c:set var="required" value=""></c:set> 
		<c:if test="${inputListMap['showContactAddress'].isMandatoryOnCreate}">
			<c:set var="required" value="required" />
		</c:if>
		<c:if test="${!inputListMap['useContactAddrAsShippingAddr'].isDisplayonForm}">
			<c:set var="cAddr" value="cAddr"/>
			<c:set var="sAddr" value="sAddr"/>
			<c:set var="sApt" value="sApt"/>
		</c:if>
		<c:set var="contactadd"><bbbl:label key="lbl_reg_ph_contactAdress" language ="${pageContext.request.locale.language}"/></c:set>
		
		<c:choose>
			<c:when test="${!isTransient && address1 ne '' && address1 ne null}"> 
				<input id="txtRegistryRegistrantContactAddress" type="text"
					name="txtRegistryRegistrantContactAddressName"
					class="${required} ${cAddr} cannotStartWithWhiteSpace QASAddress1  escapeHTMLTag ${iclassValue}"
					maxlength="120" value="${profileShippingAddr}" autocomplete="off">
			</c:when>
			<c:otherwise>
				<input id="txtRegistryRegistrantContactAddress" type="text"
					name="txtRegistryRegistrantContactAddress" 
					class="${required} ${cAddr} cannotStartWithWhiteSpace QASAddress1  escapeHTMLTag ${iclassValue}"
					maxlength="120" autocomplete="off" placeholder="${contactadd}" <c:if test="${not empty contactAddressLine1FromBean}">value="${contactAddressLabelFromBean}"</c:if>>
			</c:otherwise>
		</c:choose>
	
	<input name="_D:txtRegistryRegistrantContactAddressName" value=" "
		type="hidden">
		
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
		
	<dsp:input type="hidden" id="txtRegistryRegistrantContactAddress1"
		bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine1" iclass="address1"/> 
	<dsp:input type="hidden" id="txtRegistryRegistrantContactAddress2"
		bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine2" iclass="address2"/>
	<dsp:input type="hidden" id="txtRegistryRegistrantContactCity"
		bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.city" iclass="QASCity"/>
	<dsp:input type="hidden" id="selRegistryRegistrantContactState"
		bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.state" iclass="QASState" />
	<dsp:input type="hidden" id="txtRegistryRegistrantContactZip"
		bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.zip" iclass="QASZip"/>
		<input type="hidden" name="txtRegistryRegistrantBtn" id="txtRegistryRegistrantBtn" value=""	 />
		<input type="hidden" name="isQASValidate" id="isQASValidate" value="true"	 />
	</c:if>
	</div>
	<c:if test="${inputListMap['showShippingAddress'].isDisplayonForm && inputListMap['useContactAddrAsShippingAddr'].isDisplayonForm && inputListMap['showContactAddress'].isDisplayonForm}">
		
		<%-- <c:if test="${regVO.shipping.shippingAddress.addressLine1 ne '' && regVO.shipping.shippingAddress.addressLine1 ne null}">
				<c:set var="address1ship" value="${regVO.shipping.shippingAddress.addressLine1}" />
					<c:set var="address2ship" value="${regVO.shipping.shippingAddress.addressLine2}" />
					<c:set var="profileShippingAddrship">
						${address1ship}<c:if test="${not empty address2ship}"> ,${address2ship}</c:if>, ${regVO.shipping.shippingAddress.city}, ${regVO.shipping.shippingAddress.state}, ${regVO.shipping.shippingAddress.zip}
					</c:set>
					
		<dsp:setvalue
			bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine1"
			value="${regVO.shipping.shippingAddress.addressLine1}" />
		<dsp:setvalue
			bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine2"
			value="${regVO.shipping.shippingAddress.addressLine2}" />
		<dsp:setvalue
			bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.city"
			value="${regVO.shipping.shippingAddress.city}" />
		<dsp:setvalue
			bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.state"
			value="${regVO.shipping.shippingAddress.state}" />
		<dsp:setvalue
			bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.zip"
			value="${regVO.shipping.shippingAddress.zip}" />
		</c:if> --%>
		<div class="row">
			<div class="large-12 columns addressFields">
				 <dsp:getvalueof var="shippingAddressCheckboxFromBean" bean="GiftRegistryFormHandler.shippingAddress"/>
				<dsp:getvalueof var="formExceptions" bean="GiftRegistryFormHandler.formExceptions"/>
				
				<label class="inline-rc checkbox"
					for="radRegistryCurrentShippingAddress">
					<c:choose>
					<c:when test="${(not empty formExceptions && shippingAddressCheckboxFromBean eq 'shipAdrressSameAsRegistrant')|| empty formExceptions}"> 	
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
												 bean="GiftRegistryFormHandler.shippingAddress"
												 type="hidden"	value="shipAdrressSameAsRegistrant" />
												 <c:set var="showshipbox" value="hidden"/>
												 <c:set var="shippingAddressLine1FromBean" value=""/>
							</c:when>
					<c:when test="${not empty formExceptions && shippingAddressCheckboxFromBean eq 'newShippingAddress'}"> 				 
						<input 
												id="radRegistryCurrentShippingAddress"
												name="radRegistryCurrentShippingAddressName" 
												value="shipAdrressSameAsRegistrant" type="checkbox"
												aria-labelledby="lblradRegistryCurrentShippingAddress" >
												<%-- <dsp:tagAttribute
													name="aria-required" value="true" />
													<dsp:tagAttribute
													name="aria-checked" value="true" />
		                                        <dsp:tagAttribute
													name="aria-labelledby"
													value="lblradRegistryCurrentShippingAddress" /> --%>
													
													<dsp:input 
												id="radRegistryCurrentShippingAddressReg"
												 bean="GiftRegistryFormHandler.shippingAddress"
												 type="hidden"	value="newShippingAddress" />
												 <c:set var="showshipbox" value=""/>
																						 
										<c:set var="shippingAddressLabelFromBean">
										${shippingAddressLine1FromBean}<c:if test="${not empty shippingAddressLine2FromBean}">, ${shippingAddressLine2FromBean}</c:if>, ${shippingCityFromBean}, ${shippingStateFromBean}, ${shippingZipFromBean}"
										</c:set>		 
							</c:when>	
							</c:choose>				 
												<span></span>
												<div class="useAsShpipingText"><bbbl:label key="lbl_reg_useAsShpiping" language ="${pageContext.request.locale.language}"/></div>
					
				</label>
				
			</div>
		</div>
		</c:if>
		
		<c:if test="${inputListMap['showShippingAddress'].isDisplayonForm }">
		  <c:set var="required" value=""></c:set> 
			<c:if
				test="${inputListMap['showShippingAddress'].isMandatoryOnCreate}">
				<c:set var="required" value="required" />
			</c:if>
			<c:choose>
				<c:when test="${inputListMap['showShippingAddress'].isDisplayonForm && inputListMap['useContactAddrAsShippingAddr'].isDisplayonForm && inputListMap['showContactAddress'].isDisplayonForm}">
					<c:set var="splitShipAddress" value="splitShipAddress"/>
				</c:when>
				<c:otherwise>
					<dsp:input 
														id="radRegistryCurrentShippingAddressReg"
														 bean="GiftRegistryFormHandler.shippingAddress"
														 type="hidden"	value="newShippingAddress" />
				</c:otherwise>
			</c:choose>
		
		<div class="row ${splitShipAddress} ${showshipbox}" id="shippingNewAddrLine1">
			<div class="inputField cb large-6 small-6 columns ${sAddr} combineInput">
			<c:set var="shipAdd"><bbbl:label key="lbl_reg_ph_shippingAddress" language ="${pageContext.request.locale.language}"/></c:set>
				<label class="txtOffScreen" id="lbltxtRegistryCurrentShippingAddress" for="txtRegistryCurrentShippingAddress">${shipAdd}</label>
				<input type="text" name="txtRegistryCurrentShippingAddressName"
					id="txtRegistryCurrentShippingAddress"
					aria-labelledby="lbltxtRegistryCurrentShippingAddress "
					class="${required} cannotStartWithWhiteSpace QASAddress1 escapeHTMLTag ${iclassValue}"
					maxlength="120" placeholder="${shipAdd}" 
					<c:choose>
					<c:when test="${shippingAddressLine1FromBean ne ''}">
					value="${shippingAddressLabelFromBean}"
					</c:when>
					<c:otherwise>
					<c:if test="${address1 ne '' && address1 ne null}">
					value="${profileShippingAddr}"
					</c:if>
					</c:otherwise>
					</c:choose>>
			</div>
			<div class="inputField cb large-6 small-6 columns ${sApt}">	
			<c:set var="aptBuild"><bbbl:label key="lbl_reg_ph_apt_building" language ="${pageContext.request.locale.language}"/></c:set>
				<label class="txtOffScreen" id="lbltxtRegistryCurrentShippingAddressApt" for="txtRegistryCurrentShippingAddressApt">${aptBuild}</label>
				<dsp:input id="txtRegistryCurrentShippingAddressApt" maxlength="30" name="txtRegistryCurrentShippingAddress1Name" value=""
								  iclass="cannotStartWithWhiteSpace escapeHTMLTag"  bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine2"
								 type="text"  autocomplete="off">
							 <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryCurrentShippingAddressApt"/>
                              <dsp:tagAttribute name="placeholder" value="${aptBuild}"/>
				</dsp:input>
				
					<%-- <dsp:tagAttribute name="aria-checked" value="true" />
					<dsp:tagAttribute name="aria-labelledby"
						value="lblradRegistryCurrentShippingAddress errortxtRegistryCurrentShippingAddress" />
					<dsp:tagAttribute name="placeholder" value="${shipAdd}" /> --%>
			</div>
			<c:if test="${! isTransient}">
                       <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.firstName" beanvalue="Profile.firstName"/>
			<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.lastName" beanvalue="Profile.lastName"/>
                     </c:if>
			<dsp:input type="hidden" bean="GiftRegistryFormHandler.shippingAddress" id="shippingAddressSelect"/>
            <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.firstName" id="shipFirstName" iclass="QASFirstName"/>
            <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.lastName" id="shipLastName" iclass="QASLastName"/>
            <c:choose>
            <c:when test="${shippingAddressLine1FromBean ne '' && shippingAddressLine1FromBean ne null}">
            <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine1" id="txtRegistryCurrentShippingAddress1" value="${shippingAddressLine1FromBean}" iclass="address1"/> 
			<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine2" id="txtRegistryCurrentShippingAddress2" value="${shippingAddressLine2FromBean}" iclass="address2"/>
			<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.city" id="txtRegistryCurrentShippingCity" value="${shippingCityFromBean}" iclass="QASCity"/>
			<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.state" id="selRegistryCurrentShippingState" value="${shippingStateFromBean}" iclass="QASState"/>
			<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.zip" id="txtRegistryCurrentShippingZip" value="${shippingZipFromBean}" iclass="QASZip"/>
            </c:when>
            <c:otherwise>
            <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine1" id="txtRegistryCurrentShippingAddress1" value="${address1}" iclass="address1"/> 
			<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine2" id="txtRegistryCurrentShippingAddress2" value="${address2}" iclass="address2"/>
			<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.city" id="txtRegistryCurrentShippingCity" value="${city}" iclass="QASCity"/>
			<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.state" id="selRegistryCurrentShippingState" value="${state}" iclass="QASState"/>
			<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.zip" id="txtRegistryCurrentShippingZip" value="${zip}" iclass="QASZip"/>
			</c:otherwise>
			</c:choose>
			<input type="hidden" id="txtRegistryCurrentShippingBtn"/>
			<input type="hidden" name="isCurrentShippingQASValidate" id="isCurrentShippingQASValidate" value="true"	 />
			<!-- <div class="inputField cb large-6 columns">
				<input id="apartmentNum" placeholder="Apt/Bldg (Optional)"
					name="apartmentNum" value="" class="" type="text">
			</div> -->
		</div>
	</c:if>


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
