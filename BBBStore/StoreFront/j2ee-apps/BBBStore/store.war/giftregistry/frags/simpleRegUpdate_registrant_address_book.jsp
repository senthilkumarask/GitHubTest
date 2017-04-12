<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:importbean bean="/atg/commerce/util/MapToArrayDefaultFirst" />
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:getvalueof var="appid" bean="/atg/multisite/Site.id" />
<c:set var="focusRegABClass" value="" />
<div class="favStoreZipWrapper simpleRegForm">
<dsp:getvalueof var="isTransient" bean="Profile.transient"/>
    <fieldset class="radioGroup clearfix">
<c:set var="profileShippingAddr" value=""/>
<c:set var="voShippingAddr" value=""/>
	<c:if test="${!isTransient}">
		<dsp:getvalueof var="contactAddr" value="${regVO.primaryRegistrant.contactAddress}" />
		<dsp:getvalueof var="addressLine1" value="${regVO.primaryRegistrant.contactAddress.addressLine1}" />
		<dsp:getvalueof var="address2" value="${regVO.primaryRegistrant.contactAddress.addressLine2}" />
		<dsp:getvalueof var="shipAddr" value="${regVO.shipping.shippingAddress}" />
		<dsp:getvalueof var="shipAddressLine1" value="${regVO.shipping.shippingAddress.addressLine1}" />
		<dsp:getvalueof var="shipAddress2" value="${regVO.shipping.shippingAddress.addressLine2}" />
		<c:choose>
		<c:when test="${contactAddr ne ''  && contactAddr ne null && addressLine1 ne '' && addressLine1 ne null}">
			<c:set var="profileShippingAddr">
				${addressLine1} , <dsp:valueof value="${regVO.primaryRegistrant.contactAddress.city}" />, <dsp:valueof value="${regVO.primaryRegistrant.contactAddress.state}" />, <dsp:valueof value="${regVO.primaryRegistrant.contactAddress.zip}" />
			</c:set>
			<dsp:setvalue bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine1" value="${regVO.primaryRegistrant.contactAddress.addressLine1}" />
			<dsp:setvalue bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine2" value="${regVO.primaryRegistrant.contactAddress.addressLine2}" />
			<dsp:setvalue bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.city" value="${regVO.primaryRegistrant.contactAddress.city}" />
			<dsp:setvalue bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.state" value="${regVO.primaryRegistrant.contactAddress.state}" />
			<dsp:setvalue bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.zip" value="${regVO.primaryRegistrant.contactAddress.zip}" />
		</c:when>
		<c:otherwise>
			<dsp:getvalueof var="profileAddress" bean="Profile.shippingAddress" />
			<dsp:getvalueof var="addressLine1" bean="Profile.shippingAddress.address1" />
			<dsp:getvalueof var="address2" bean="Profile.shippingAddress.address2" />
			<c:if test="${profileAddress ne ''  && profileAddress ne null && addressLine1 ne '' && addressLine1 ne null}">
				<c:set var="profileShippingAddr">
					${addressLine1} , <dsp:valueof bean="Profile.shippingAddress.city" />, <dsp:valueof bean="Profile.shippingAddress.state" />, <dsp:valueof bean="Profile.shippingAddress.PostalCode" />
				</c:set>
			</c:if>
			<dsp:setvalue bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine1" beanvalue="Profile.shippingAddress.address1" />
			<dsp:setvalue bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine2" beanvalue="Profile.shippingAddress.address2" />
			<dsp:setvalue bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.city" beanvalue="Profile.shippingAddress.city" />
			<dsp:setvalue bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.state" beanvalue="Profile.shippingAddress.state" />
			<dsp:setvalue bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.zip" beanvalue="Profile.shippingAddress.PostalCode" />
		</c:otherwise>
		</c:choose>
		<c:if test="${shipAddr ne ''  && shipAddr ne null && shipAddressLine1 ne '' && shipAddressLine1 ne null}">
			<c:set var="voShippingAddr">
				${shipAddressLine1} , <dsp:valueof value="${regVO.shipping.shippingAddress.city}" />, <dsp:valueof value="${regVO.shipping.shippingAddress.state}" />, <dsp:valueof value="${regVO.shipping.shippingAddress.zip}" />
			</c:set>
			<dsp:setvalue bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine1" value="${regVO.shipping.shippingAddress.addressLine1}" />
			<dsp:setvalue bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine2" value="${regVO.shipping.shippingAddress.addressLine2}" />
			<dsp:setvalue bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.city" value="${regVO.shipping.shippingAddress.city}" />
			<dsp:setvalue bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.state" value="${regVO.shipping.shippingAddress.state}" />
			<dsp:setvalue bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.zip" value="${regVO.shipping.shippingAddress.zip}" />
		</c:if>
	</c:if>
        <div class="input last clearfix">
		<c:choose>
		<c:when test="${event == 'BA1' || appid == 'BuyBuyBaby'}">
            <div class="clearfix validateFormWithQAS">
		</c:when>
		<c:otherwise>
			<div class="subForm clearfix validateFormWithQAS">
		</c:otherwise>
		</c:choose>
             
                <div class="inputField clearfix marRight_10 grid_4" id="regContactAddrLine1">
                    <label id="lbltxtRegistryRegistrantContactAddress1" for="txtRegistryRegistrantContactAddress">
                       <bbbl:label key="lbl_reg_contactAddress" language ="${pageContext.request.locale.language}"/>
                    </label>
					<div class="grid_4 omega alpha">
					<input id="txtRegistryRegistrantContactAddress"
                            type="text"
                            name="txtRegistryRegistrantContactAddress"
                            class="${iclassValue} cannotStartWithWhiteSpace QASAddress1 ${focusRegABClass} isMandatoryField" maxlength="60" placeholder="<bbbl:label key='lbl_reg_ph_contactAdress' language ='${pageContext.request.locale.language}'/>" value="${profileShippingAddr}">
					<input type="hidden" name="txtRegistryRegistrantContactAddressHidden" class='addressHidden' id="txtRegistryRegistrantContactAddressHidden" value="${profileShippingAddr}">
					
					<dsp:input type="hidden" bean="GiftRegistryFormHandler.regContactAddress" id="newPrimaryRegAddress" value=""/> 
					<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.firstName" value="${regVO.primaryRegistrant.contactAddress.firstName}" id="contactFirstName" iclass="QASFirstName"/>
                    <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.lastName" value="${regVO.primaryRegistrant.contactAddress.lastName}" id="contactLastName" iclass="QASLastName"/>
                    <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine1" id="txtRegistryRegistrantContactAddress1" iclass="address1"/>
					<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.addressLine2" id="txtRegistryRegistrantContactAddress2"  iclass="address2"/>
					<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.city" id="txtRegistryRegistrantContactCity" iclass="QASCity"/>
					<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.state" id="selRegistryRegistrantContactState" iclass="QASState"/>
					<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.primaryRegistrant.contactAddress.zip" id="txtRegistryRegistrantContactZip" iclass="QASZip"/>
					<input type="hidden" id="txtRegistryRegistrantContactCountryName"  value="" class="countryName"/>
					<input type="hidden" id="PoBoxFlag"  value="" class="PoBoxFlag"/>
					<input type="hidden" id="PoBoxStatus"  value="" class="PoBoxStatus"/>
					<input type="hidden" id="txtRegistryRegistrantBtn"/>
                </div>
					<div class="grid_2 omega alpha marTop_15">
					<label class="txtOffScreen" id="ApartmentNoLabel" for="txtRegistryRegistrantContactAddressApt">${lbl_reg_ph_apt_building}</label>
						<input id="txtRegistryRegistrantContactAddressApt" maxlength="30" name="txtRegistryCurrentShippingAddress2Name" type="text" autocomplete="off"  class="cannotStartWithWhiteSpace ApartmentNo escapeHTMLTag" placeholder="Apt/Bldg (optional)" aria-labelledby="ApartmentNoLabel" value="${address2}" data-previousapt="${address2}">   
					</div>
                </div>
               
				<div class="clearfix"></div>
				
				<c:choose>
				<c:when test="${not empty voShippingAddr && profileShippingAddr ne voShippingAddr || address2 ne shipAddress2}">
					<c:set var="isShipAddrSameAsContAddr" value="false"/>
					<c:set var="newOrSameShipAddrAsRegistrant" value=""/>
					<c:set var="hideShipAddrField" value=""/>
				</c:when>
				<c:otherwise>
					<c:set var="isShipAddrSameAsContAddr" value="true"/>
					<c:set var="newOrSameShipAddrAsRegistrant" value="shipAdrressSameAsRegistrant"/>
					<c:set var="hideShipAddrField" value="hidden"/>
				</c:otherwise>
				</c:choose>
               	<div class="checkboxItem inputField clearfix marTop_5">
							<div class="checkbox">
										<dsp:input type="checkbox" name="radRegistryCurrentShippingAddressName" id="radRegistryCurrentShippingAddress"
											bean="GiftRegistryFormHandler.shippingAddress" value="shipAdrressSameAsRegistrant" checked="${isShipAddrSameAsContAddr}" iclass="">
	                                <dsp:tagAttribute name="aria-checked" value="true"/>
	                                <dsp:tagAttribute name="aria-labelledby" value="lblradRegistryCurrentShippingAddress"/>
	                           </dsp:input>
							</div>
							<div class="label">
								<label id="lblradRegistryCurrentShippingAddress" for="radRegistryCurrentShippingAddress">
									<bbbl:label key="lbl_reg_useAsShpiping" language ="${pageContext.request.locale.language}"/>
								</label>
								<input type="hidden" name="zipvalue" class="favStoreZip" value="" />
							</div>
				</div>
						
				<c:choose>
                <c:when test="${event == 'BA1' || appid == 'BuyBuyBaby'}">
				  <div class="validateFormWithQAS">
                  <div class="inputField clearfix ${leftgrid} ${hideShipAddrField}" id="shippingNewAddrLine1">
                  </c:when>
                  <c:otherwise>
				  <div class="subForm validateFormWithQAS">
                  <div class="inputField clearfix grid_4 ${hideShipAddrField}" id="shippingNewAddrLine1">
                  </c:otherwise>
                  </c:choose> 
                
					<div class="grid_4 alpha omega marTop_10">
                    <input id="txtRegistryCurrentShippingAddress"
                        type="text"
                        name="txtRegistryCurrentShippingAddress1Name" class="cannotStartWithWhiteSpace QASAddress1" maxlength="90" placeholder="Shipping Address" value="${voShippingAddr}">
					<input type="hidden" class='addressHidden' name="txtRegistryCurrentShippingAddressHidden" id="txtRegistryCurrentShippingAddressHidden" value="${voShippingAddr}">
					</div>
				<div class="grid_2 alpha omega marTop_15">
                       <input id="txtRegistryCurrentShippingAddressApt" maxlength="30" name="txtRegistryCurrentShippingAddress2Name" type="text" autocomplete="off"  class="cannotStartWithWhiteSpace ApartmentNo escapeHTMLTag" placeholder="Apt/Bldg (optional)" value="${shipAddress2}" data-previousapt="${shipAddress2}"> 
                </div>
                        <c:if test="${! isTransient}">
		                            <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.firstName" beanvalue="Profile.firstName" iclass="QASFirstName"/>
										<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.lastName" beanvalue="Profile.lastName" iclass="QASLastName"/>
		                          </c:if>
		                        <dsp:input type="hidden" bean="GiftRegistryFormHandler.shippingAddress" id="shippingAddressSelect" value="${newOrSameShipAddrAsRegistrant}"/>
					            <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.firstName" id="shipFirstName" iclass="QASFirstName"/>
					            <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.lastName" id="shipLastName" iclass="QASLastName"/>
					            <dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine1" id="txtRegistryCurrentShippingAddress1" iclass="address1"/>
								<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.addressLine2" id="txtRegistryCurrentShippingAddress2" iclass="address2"/>
								<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.city" id="txtRegistryCurrentShippingCity"  iclass="QASCity"/>
								<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.state" id="selRegistryCurrentShippingState" iclass="QASState"/>
								<dsp:input type="hidden" bean="GiftRegistryFormHandler.registryVO.shipping.shippingAddress.zip" id="txtRegistryCurrentShippingZip" iclass="QASZip"/>
								<input type="hidden" id="txtRegistryCurrentShippingCountryName"  value="" class="countryName"/>
								<input type="hidden" id="PoBoxFlag"  value="" class="PoBoxFlag"/>
								<input type="hidden" id="PoBoxStatus"  value="" class="PoBoxStatus"/>
								<input type="hidden" id="txtRegistryCurrentShippingBtn"/>
                </div>

                <div class="clear"></div>
            </div>
            <div class="clear"></div>
        </div>
        <div class="clear"></div>
	</div>
    </fieldset>
<div class="clear"></div>
</div>
</dsp:page>
