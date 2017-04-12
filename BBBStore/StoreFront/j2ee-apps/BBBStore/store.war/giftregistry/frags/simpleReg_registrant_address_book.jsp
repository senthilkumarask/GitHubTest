<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:importbean bean="/atg/commerce/util/MapToArrayDefaultFirst" />
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<c:set var="focusRegABClass" value="" />
<div class="favStoreZipWrapper simpleRegForm">
<dsp:getvalueof var="isTransient" bean="Profile.transient"/>
    <fieldset class="radioGroup clearfix">
<c:set var="profileShippingAddr" value=""/>
<c:set var="contAddr" value=""/>
<c:set var="shippingAddr" value=""/>

<dsp:getvalueof var="contAddrFirstNameFromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.firstName"/>
<dsp:getvalueof var="contAddrLastNameFromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.lastName"/>
<dsp:getvalueof var="contAddrLineOneFromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine1"/>
<dsp:getvalueof var="contAddrLineTwoFromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine2"/>
<dsp:getvalueof var="contCityFromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.city"/>
<dsp:getvalueof var="contStateFromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.state"/>
<dsp:getvalueof var="contZipFromBean" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.zip"/>

<dsp:getvalueof var="shipAddrFirstNameFromBean" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.firstName"/>
<dsp:getvalueof var="shipAddrLastNameFromBean" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.lastName"/>
<dsp:getvalueof var="shipAddrLineOneFromBean" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine1"/>
<dsp:getvalueof var="shipAddrLineTwoFromBean" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine2"/>
<dsp:getvalueof var="shipCityFromBean" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.city"/>
<dsp:getvalueof var="shipStateFromBean" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.state"/>
<dsp:getvalueof var="shipZipFromBean" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.zip"/>

	<c:if test="${contAddrLineOneFromBean ne '' && contAddrLineOneFromBean ne null}">
	<c:set var="contAddr">
			${contAddrLineOneFromBean}, ${contCityFromBean}, ${contStateFromBean}, ${contZipFromBean}
	</c:set>
	</c:if>
	
	<c:if test="${shipAddrLineOneFromBean ne '' && shipAddrLineOneFromBean ne null}">
	<c:set var="shippingAddr">
			${shipAddrLineOneFromBean}, ${shipCityFromBean}, ${shipStateFromBean}, ${shipZipFromBean}
	</c:set>
	</c:if>

	<c:if test="${!isTransient}">
	    <dsp:getvalueof var="profileAddress" bean="Profile.shippingAddress" />
		<dsp:getvalueof var="addressLine1" bean="Profile.shippingAddress.address1" />
		<dsp:getvalueof var="address2" bean="Profile.shippingAddress.address2" />
		<c:if test="${profileAddress ne ''  && profileAddress ne null && addressLine1 ne '' && addressLine1 ne null}">
		<c:set var="profileShippingAddr">
			${addressLine1}, <dsp:valueof bean="Profile.shippingAddress.city" />, <dsp:valueof bean="Profile.shippingAddress.state" />, <dsp:valueof	bean="Profile.shippingAddress.PostalCode" />
		</c:set>
		</c:if>
		<dsp:getvalueof  var="profileFirstName" bean="Profile.firstName" />
        <dsp:getvalueof  var="profileLastName" bean="Profile.lastName" />
		<dsp:getvalueof var="contactAddressLine1" bean="Profile.shippingAddress.address1" />
		<dsp:getvalueof var="contactAddressLine2" bean="Profile.shippingAddress.address2" />
		<dsp:getvalueof var="contactAddressCity" bean="Profile.shippingAddress.city" />
		<dsp:getvalueof var="contactAddressState" bean="Profile.shippingAddress.state" />
		<dsp:getvalueof var="contactAddressPostCode" bean="Profile.shippingAddress.PostalCode" />
	</c:if>
        <div class="input last clearfix">
            <div class="subForm clearfix validateFormWithQAS">
             <c:if test="${inputListMap['showContactAddress'].isDisplayonForm}"> 
                <div class="inputField clearfix marRight_10 grid_4" id="regContactAddrLine1">
                    <label id="lbltxtRegistryRegistrantContactAddress1" for="txtRegistryRegistrantContactAddress">
                       <bbbl:label key="lbl_reg_contactAddress" language ="${pageContext.request.locale.language}"/>
                    </label>
					<div class="grid_4 omega alpha">
					 <c:set var="isRequired" value=""></c:set> 
													 <c:if test="${inputListMap['showContactAddress'].isMandatoryOnCreate}">
													 <c:set var="isRequired" value="required"></c:set>
													 </c:if>
					
					<c:choose>
					<c:when test="${contAddr ne '' && contAddr ne null}">
					<input id="txtRegistryRegistrantContactAddress"
                            type="text"
                            name="txtRegistryRegistrantContactAddress"
                            class="${iclassValue} cannotStartWithWhiteSpace QASAddress1 ${focusRegABClass} ${isRequired}" maxlength="60" placeholder="<bbbl:label key='lbl_reg_ph_contactAdress' language ='${pageContext.request.locale.language}'/>" value="${contAddr}">
				<input type="hidden" class='addressHidden' name="txtRegistryRegistrantContactAddressHidden" id="txtRegistryRegistrantContactAddressHidden" value="${contAddr}">
					<dsp:input type="hidden" bean="GiftRegistryFormHandler.regContactAddress" id="newPrimaryRegAddress" value=""/>
					<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.firstName" value="${contAddrFirstNameFromBean}" id="contactFirstName" iclass="QASFirstName"/>
                    			<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.lastName" value="${contAddrLastNameFromBean}" id="contactLastName" iclass="QASLastName"/>
                    			<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine1" value="${contAddrLineOneFromBean}" id="txtRegistryRegistrantContactAddress1" iclass="address1"/>
								<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine2" value="${contAddrLineTwoFromBean}" id="txtRegistryRegistrantContactAddress2" iclass="address2"/>
								<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.city" value="${contCityFromBean}" id="txtRegistryRegistrantContactCity" iclass="QASCity"/>
								<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.state" value="${contStateFromBean}" id="selRegistryRegistrantContactState" iclass="QASState"/>
								<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.zip" value="${contZipFromBean}" id="txtRegistryRegistrantContactZip" iclass="QASZip"/>
								<input type="hidden" id="txtRegistryRegistrantContactCountryName"  value="" class="countryName"/>
								<input type="hidden" id="PoBoxFlag"  value="" class="PoBoxFlag"/>
								<input type="hidden" id="PoBoxStatus"  value="" class="PoBoxStatus"/>
								<input type="hidden" id="txtRegistryRegistrantBtn"/>
					</c:when>
					<c:otherwise>
					<input id="txtRegistryRegistrantContactAddress"
                            type="text"
                            name="txtRegistryRegistrantContactAddress"
                            class="${iclassValue} cannotStartWithWhiteSpace QASAddress1 ${focusRegABClass} ${isRequired}" maxlength="60" placeholder="<bbbl:label key='lbl_reg_ph_contactAdress' language ='${pageContext.request.locale.language}'/>" value="${profileShippingAddr}">
							<input type="hidden" class='addressHidden' name="txtRegistryRegistrantContactAddressHidden" id="txtRegistryRegistrantContactAddressHidden" value="${profileShippingAddr}">
				
										
					
						<dsp:input type="hidden" bean="GiftRegistryFormHandler.regContactAddress" id="newPrimaryRegAddress" value=""/>
						<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.firstName" value="${profileFirstName}" id="contactFirstName" iclass="QASFirstName"/>
						<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.lastName" value="${profileLastName}" id="contactLastName" iclass="QASLastName"/>
						<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine1" value="${contactAddressLine1}" id="txtRegistryRegistrantContactAddress1" iclass="address1"/>
						<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine2" value="${contactAddressLine2}" id="txtRegistryRegistrantContactAddress2" iclass="address2"/>
						<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.city" value="${contactAddressCity}" id="txtRegistryRegistrantContactCity" iclass="QASCity"/>
						<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.state" value="${contactAddressState}" id="selRegistryRegistrantContactState"  iclass="QASState"/>
						<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.zip" value="${contactAddressPostCode}" id="txtRegistryRegistrantContactZip" iclass="QASZip"/>
						<input type="hidden" id="txtRegistryRegistrantContactCountryName"  value="" class="countryName"/>
						<input type="hidden" id="PoBoxFlag"  value="" class="PoBoxFlag"/>
						<input type="hidden" id="PoBoxStatus"  value="" class="PoBoxStatus"/>
						<input type="hidden" id="txtRegistryRegistrantBtn"/>
                                      </c:otherwise>
					</c:choose>
<label for="txtRegistryRegistrantContactAddress" generated="true" class="contactValidAddMsg error errorLabel">
					</label>	
				</div>
				<div class="grid_2 omega alpha marTop_15">
				<label class="txtOffScreen" id="ApartmentNoLabel" for="txtRegistryRegistrantContactAddressApt">${lbl_reg_ph_apt_building}</label>
                 <input id="txtRegistryRegistrantContactAddressApt" maxlength="30" name="txtRegistryCurrentShippingAddress2Name" type="text" autocomplete="off"  class="cannotStartWithWhiteSpace ApartmentNo escapeHTMLTag" placeholder="Apt/Bldg (optional)" aria-labelledby="ApartmentNoLabel" value="${contactAddressLine2}" data-previousapt="${contactAddressLine2}">                             
						  
						  </div>
                </div>
               </c:if>
				<div class="clearfix"></div>
				<c:if test="${inputListMap['useContactAddrAsShippingAddr'].isDisplayonForm && inputListMap['showShippingAddress'].isDisplayonForm && inputListMap['showContactAddress'].isDisplayonForm}">
               	 <c:set var="isRequired" value=""></c:set> 
													 <c:if test="${inputListMap['useContactAddrAsShippingAddr'].isMandatoryOnCreate}">
													 <c:set var="isRequired" value="required"></c:set>
													 </c:if>
													  <dsp:getvalueof var="shippingAddressCheckboxFromBean" bean="GiftRegistryFormHandler.shippingAddress"/>
													   <dsp:getvalueof var="formExceptions" bean="GiftRegistryFormHandler.formExceptions"/>
               	<div class="checkboxItem inputField clearfix marTop_10">
							<div class="checkbox">
							<c:choose>
							<c:when test="${not empty formExceptions && shippingAddressCheckboxFromBean eq 'shipAdrressSameAsRegistrant'}">
										<dsp:input type="checkbox" name="radRegistryCurrentShippingAddressName" id="radRegistryCurrentShippingAddress"
											bean="GiftRegistryFormHandler.shippingAddress" value="shipAdrressSameAsRegistrant" checked="true" >
	                                <dsp:tagAttribute name="aria-hidden" value="false"/>
	                                <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryCurrentShippingAddress"/>
	                           </dsp:input>
	                           <input type="hidden" id="shippingAddressChecked" value="shipAdrressSameAsRegistrant">
	                           </c:when>
	                           <c:when test="${not empty formExceptions && shippingAddressCheckboxFromBean eq 'newShippingAddress'}">
	                           <dsp:input type="checkbox" name="radRegistryCurrentShippingAddressName" id="radRegistryCurrentShippingAddress"
											bean="GiftRegistryFormHandler.shippingAddress" value="newShippingAddress" checked="false" >
	                                <dsp:tagAttribute name="aria-hidden" value="false"/>
	                                <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryCurrentShippingAddress"/>
	                           </dsp:input>
	                           <%-- Added to unhide the shipping address text box if the value is from newShippingaddress from bean --%>
	                           <input type="hidden" id="shippingAddressChecked" value="newShippingAddress">
	                           </c:when>
	                           <c:otherwise>
	                           <dsp:input type="checkbox" name="radRegistryCurrentShippingAddressName" id="radRegistryCurrentShippingAddress"
											bean="GiftRegistryFormHandler.shippingAddress" value="shipAdrressSameAsRegistrant" checked="true" >
	                               <dsp:tagAttribute name="aria-hidden" value="false"/>
	                                <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryCurrentShippingAddress"/>
	                           </dsp:input>
	                           </c:otherwise>
	                           </c:choose>
							</div>
							<div class="label shippingAdd">
								<label id="lblradRegistryCurrentShippingAddress" for="lblradRegistryCurrentShippingAddress" class="addChkboxTxt txtStyle">
									<bbbl:label key="lbl_reg_useAsShpiping" language ="${pageContext.request.locale.language}"/>
								</label>
								<input type="hidden" name="zipvalue" class="favStoreZip" value="" />
							</div>
						</div>
						</c:if>
						<c:if test="${inputListMap['showShippingAddress'].isDisplayonForm && not inputListMap['showContactAddress'].isDisplayonForm}">
						<label id="lbltxtRegistryRegistrantShippingAddress" for="txtRegistryCurrentShippingAddress">
                       <bbbl:label key="lbl_reg_ph_shippingAddress" language ="${pageContext.request.locale.language}"/>
                    </label>
                    </c:if>
						<c:if test="${inputListMap['showShippingAddress'].isDisplayonForm}">
						<c:set var="isRequired" value=""></c:set> 
													 <c:if test="${inputListMap['showShippingAddress'].isMandatoryOnCreate}">
													 <c:set var="isRequired" value="required"></c:set>
													 </c:if>
						  <div class="subForm validateFormWithQAS">
           
                  <div class="inputField clearfix grid_4 marTop_5 marBottom_10" id="shippingNewAddrLine1">
                <c:set var="lbl_reg_ph_shippingAddress"><bbbl:label key="lbl_reg_ph_shippingAddress" language ="${pageContext.request.locale.language}"/></c:set>
                <c:set var="lbl_reg_ph_apt_building"><bbbl:label key="lbl_reg_ph_apt_building" language ="${pageContext.request.locale.language}"/></c:set>
					
					<c:choose>
					<c:when test="${shippingAddr ne '' && shippingAddr ne null}">
					<div class="grid_4 alpha omega">
					<input id="txtRegistryRegistrantContactAddress"
                            type="text"
                            name="txtRegistryRegistrantContactAddress"
                            class="${iclassValue} cannotStartWithWhiteSpace QASAddress1 ${focusRegABClass} ${isRequired}" maxlength="60" placeholder="<bbbl:label key='lbl_reg_ph_contactAdress' language ='${pageContext.request.locale.language}'/>" value="${shippingAddr}">
                            </div>
					<div class="grid_2 alpha omega marTop_15">
				<label class="txtOffScreen" id="lbltxtRegistryCurrentShippingAddress2" for="txtRegistryCurrentShippingAddressApt">${lbl_reg_ph_apt_building}</label>
                 <input id="txtRegistryCurrentShippingAddressApt" maxlength="30" name="txtRegistryCurrentShippingAddress2Name" type="text" autocomplete="off"  class="cannotStartWithWhiteSpace ApartmentNo escapeHTMLTag" placeholder="Apt/Bldg (optional)" aria-labelledby="lbltxtRegistryCurrentShippingAddress2" value="${address2}" data-previousapt="${address2}"> 
                </div>
					<dsp:input type="hidden" bean="GiftRegistryFormHandler.shippingAddress" id="shippingAddressSelect"/>
					            <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.firstName" value="${shipAddrFirstNameFromBean}" id="shipFirstName" iclass="QASFirstName"/>
					            <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.lastName" value="${shipAddrLastNameFromBean}" id="shipLastName" iclass="QASLastName"/>
					            <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine1" value="${shipAddrLineOneFromBean}" id="txtRegistryCurrentShippingAddress1" iclass="address1"/>
					            <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine2" value="${shipAddrLineTwoFromBean}" id="txtRegistryCurrentShippingAddress2" iclass="address2"/>
								<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.city" value="${shipCityFromBean}" id="txtRegistryCurrentShippingCity" iclass="QASCity"/>
								<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.state"  value="${shipStateFromBean}" id="selRegistryCurrentShippingState" iclass="QASState"/>
								<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.zip"  value="${shipZipFromBean}" id="txtRegistryCurrentShippingZip" iclass="QASZip"/>
								<input type="hidden" id="txtRegistryCurrentShippingCountryName"  value="" class="countryName"/>
								<input type="hidden" id="PoBoxFlag"  value="" class="PoBoxFlag"/>
								<input type="hidden" id="PoBoxStatus"  value="" class="PoBoxStatus"/>
								<input type="hidden" id="txtRegistryCurrentShippingBtn"/>
					</c:when>
					<c:otherwise>
					<div class="grid_4 alpha omega">
					<label for="txtRegistryCurrentShippingAddress" class="txtOffScreen" id="lblRegistryCurrentShippingAddress">${lbl_reg_ph_shippingAddress}</label>
                    <input id="txtRegistryCurrentShippingAddress"
                        type="text"
                        name="txtRegistryCurrentShippingAddress1Name"
                         class="${isRequired} cannotStartWithWhiteSpace QASAddress1" maxlength="90" placeholder="${lbl_reg_ph_shippingAddress}" value="${profileShippingAddr}" aria-labelledby="lblRegistryCurrentShippingAddress lblErrorRegistryCurrentShippingAddress">
					<label for="txtRegistryCurrentShippingAddress" generated="true" class="contactValidAddMsg error errorLabel" id="lblErrorRegistryCurrentShippingAddress">
					</label>
					<input type="hidden" class='addressHidden' name="txtRegistryCurrentShippingAddressHidden" id="txtRegistryCurrentShippingAddressHidden" value="${profileShippingAddr}">

					</div>
				<div class="grid_2 alpha omega marTop_15">
				<label class="txtOffScreen" id="lbltxtRegistryCurrentShippingAddress2" for="txtRegistryCurrentShippingAddressApt">${lbl_reg_ph_apt_building}</label>
                 <input id="txtRegistryCurrentShippingAddressApt" maxlength="30" name="txtRegistryCurrentShippingAddress2Name" type="text" autocomplete="off"  class="cannotStartWithWhiteSpace ApartmentNo escapeHTMLTag" placeholder="Apt/Bldg (optional)" aria-labelledby="lbltxtRegistryCurrentShippingAddress2" value="${address2}" data-previousapt="${address2}"> 

                </div>
		                        <dsp:input type="hidden" bean="GiftRegistryFormHandler.shippingAddress" id="shippingAddressSelect"/>
					            <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.firstName" value="${profileFirstName}" id="shipFirstName" iclass="QASFirstName"/>
					            <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.lastName" value="${profileLastName}" id="shipLastName" iclass="QASLastName"/>
					            <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine1" value="${contactAddressLine1}" id="txtRegistryCurrentShippingAddress1" iclass="address1"/>
								<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine2" value="${contactAddressLine2}" id="txtRegistryCurrentShippingAddress2" iclass="address2"/>
								<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.city" value="${contactAddressCity}" id="txtRegistryCurrentShippingCity" iclass="QASCity"/>
								<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.state"  value="${contactAddressState}" id="selRegistryCurrentShippingState"  iclass="QASState"/>
								<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.zip"  value="${contactAddressPostCode}" id="txtRegistryCurrentShippingZip"  iclass="QASZip"/>
								<input type="hidden" id="txtRegistryCurrentShippingCountryName"  value="" class="countryName"/>
								<input type="hidden" id="PoBoxFlag"  value="" class="PoBoxFlag"/>
								<input type="hidden" id="PoBoxStatus"  value="" class="PoBoxStatus"/>
								<input type="hidden" id="txtRegistryCurrentShippingBtn"/>
					</c:otherwise>
					</c:choose>
                </div>
                </c:if>

                <div class="clear"></div>
            </div>
            <div class="clear"></div>
        </div>
        <div class="clear"></div>
    </fieldset>
</div>
<div class="clear"></div>
</dsp:page>
