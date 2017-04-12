<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/bbb/commerce/checkout/formhandler/BBBBillingAddressFormHandler" />
	<dsp:importbean bean="/com/bbb/commerce/common/BBBPopulateStatesDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/common/BBBCountriesDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/com/bbb/commerce/shipping/droplet/CheckProfileAddress" />

	<%-- Variables --%>
	<dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
	<dsp:getvalueof var="addresses" param="addresses"/>
	<dsp:getvalueof var="selectedAddrKey" param="selectedAddrKey"/>
	<dsp:getvalueof var="guestuser" bean="Profile.transient"/>
	<c:set var="internationalCCFlag" value="${sessionScope.internationalCreditCard}"/>
	<c:set var="doHide" value=""/>
	<dsp:getvalueof var="regMap" bean="ShoppingCart.current.registryMap"/>
	<c:set var="isbillAddExists" value="false"></c:set> 
	<dsp:droplet name="ForEach">
	  <dsp:param name="array" param="addresses" />
	  <dsp:param name="elementName" value="address"/>
	  <dsp:oparam name="output">
	  	<dsp:getvalueof var ="firstname" param="address.firstName" />
	  	<dsp:getvalueof var ="lastname" param="address.LastName" />
	  	<c:if test="${not empty firstname && not empty lastname && isbillAddExists eq 'false'}">
	  		<c:set var="isbillAddExists">true</c:set> 
	  	</c:if>
	  	</dsp:oparam>
  	</dsp:droplet>
  	
  	<c:set var="qasAddrLength" scope="request"><bbbc:config key="qas_address_length" configName="QASKeys"/></c:set>
  	
	<div class="row">
		<div class="small-12 large-6 columns">
			<div class="saved-billing-addresses">
				<dsp:include page="/checkout/billing/allBillingAddresses.jsp">
					<dsp:param name="selectedAddrKey" param="selectedAddrKey"/>
					<dsp:param name="addresses" param="addresses"/>
				</dsp:include>
			</div>
			<label class="inline-rc radio gray-panel billing-address new-billing-address-trigger" id="lbladdressToShip4" for="addressToShip4">
				<c:choose>
					<c:when test="${addresses eq null || empty addresses}">
						<dsp:input type="radio" name="addressToShip" iclass="newAddOpt" bean="BBBBillingAddressFormHandler.userSelectedOption" value="NEW" id="addressToShip4" checked="true">
							<dsp:tagAttribute name="aria-checked" value="false"/>
							<dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip4"/>
						</dsp:input>
					</c:when>
					
					<%-- this check is for following scenario:
						 1. address will not be empty for normal products, address from shippihng will persist, so we need to show it selected by default
						 2. for ropis, we need to check address[0] is empty as there is an ATG address object in session at this time--%>
					<c:when test="${empty addresses && empty addresses[0]}">
						<dsp:input type="radio" name="addressToShip" iclass="newAddOpt" bean="BBBBillingAddressFormHandler.userSelectedOption" value="NEW" id="addressToShip4" checked="true">
							<dsp:tagAttribute name="aria-checked" value="false"/>
							<dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip4"/>
						</dsp:input>
					</c:when>
					<c:when test="${not empty addresses && not empty regMap  && empty firstname && empty lastname}">
						<dsp:input type="radio" name="addressToShip" iclass="newAddOpt" bean="BBBBillingAddressFormHandler.userSelectedOption" value="NEW" id="addressToShip4" checked="true">
							<dsp:tagAttribute name="aria-checked" value="false"/>
							<dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip4"/>
						</dsp:input>
					</c:when>
					<c:otherwise>
						<dsp:input type="radio" name="addressToShip" iclass="newAddOpt" bean="BBBBillingAddressFormHandler.userSelectedOption" value="NEW" id="addressToShip4">
							<dsp:tagAttribute name="aria-checked" value="false"/>
							<dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip4"/>
						</dsp:input>
					</c:otherwise>
				</c:choose>
				<span></span>
				Add a new billing address
			</label>
		</div>
		<c:choose>
			<c:when test="${addresses eq null || empty addresses}">
				<c:set var="doHide" value=""/>
			</c:when>
			<c:when test="${empty addresses && empty addresses[0]}">
				<c:set var="doHide" value=""/>
			</c:when>			
			<c:when test="${not empty addresses && not empty regMap && isbillAddExists eq 'false'}">
				<c:set var="doHide" value=""/>
			</c:when>
			<c:otherwise>
				<c:set var="doHide" value="hidden"/>
			</c:otherwise>
		</c:choose>
		<div class="small-12 large-6 columns new-billing-address ${doHide}">
			<div class="row">
				<div class="small-12 large-6 columns">
					<c:set var="placeholderText"><bbbl:label key="lbl_shipping_new_first_name" language="${pageContext.request.locale.language}" /></c:set>
					<dsp:input type="text" value="" name="checkoutfirstName" id="checkoutfirstName" bean="BBBBillingAddressFormHandler.billingAddress.firstName">
						<dsp:tagAttribute name="placeholder" value="${placeholderText} *"/>
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblcheckoutfirstName errorcheckoutfirstName"/>
					</dsp:input>
				</div>
				<div class="small-12 large-6 columns">
					<c:set var="placeholderText"><bbbl:label key="lbl_shipping_new_last_name" language="${pageContext.request.locale.language}" /></c:set>
					<dsp:input type="text" value="" id="checkoutlastName" name="checkoutlastName" bean="BBBBillingAddressFormHandler.billingAddress.lastName">
						<dsp:tagAttribute name="placeholder" value="${placeholderText} *"/>
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblcheckoutlastName errorcheckoutlastName"/>
					</dsp:input>
				</div>
				<div class="small-12 columns">
					<c:set var="placeholderText"><bbbl:label key="lbl_shipping_new_company" language="${pageContext.request.locale.language}" /></c:set>
					<dsp:input type="text" value="" id="company" name="company" bean="BBBBillingAddressFormHandler.billingAddress.companyName">
						<dsp:tagAttribute name="placeholder" value="${placeholderText}"/>
						<dsp:tagAttribute name="aria-required" value="false"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblcompany errorcompany"/>
					</dsp:input>
				</div>
				<div class="small-12 columns">
					<c:set var="placeholderText"><bbbl:label key="lbl_shipping_new_line1" language="${pageContext.request.locale.language}" /></c:set>
					<dsp:input type="text" value="" maxlength="${qasAddrLength}" id="address1" name="address1" bean="BBBBillingAddressFormHandler.billingAddress.address1">
						<dsp:tagAttribute name="placeholder" value="${placeholderText} *"/>
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lbladdress1 erroraddress1"/>
						<dsp:tagAttribute name="autocomplete" value="off"/>
					</dsp:input>
				</div>
				<div class="small-12 columns">
					<c:set var="placeholderText"><bbbl:label key="lbl_shipping_new_line2" language="${pageContext.request.locale.language}" /></c:set>
					<dsp:input type="text" value="" maxlength="${qasAddrLength}" id="address2" name="address2" bean="BBBBillingAddressFormHandler.billingAddress.address2">
						<dsp:tagAttribute name="placeholder" value="${placeholderText}"/>
						<dsp:tagAttribute name="aria-required" value="false"/>
						<dsp:tagAttribute name="aria-labelledby" value="lbladdress2 erroraddress2"/>
					</dsp:input>
				</div>
				<div class="small-12 columns">
					<c:set var="placeholderText"><bbbl:label key="lbl_shipping_new_city" language="${pageContext.request.locale.language}" /></c:set>
					<dsp:input type="text" value="" id="cityName" name="cityName" bean="BBBBillingAddressFormHandler.billingAddress.city" maxlength="25">
						<dsp:tagAttribute name="placeholder" value="${placeholderText} *"/>
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblcityName errorcityName"/>
					</dsp:input>
				</div>
				<div class="small-12 large-6 columns">
					<c:set var="placeholderText">
						<c:choose>
							<c:when test="${currentSiteId eq 'TBS_BedBathCanada'}">
								<bbbl:label key="lbl_addnewbillingaddressprovince" language="${pageContext.request.locale.language}" />
							</c:when>
							<c:otherwise>
								<bbbl:label key="lbl_shipping_new_state" language="${pageContext.request.locale.language}" />
							</c:otherwise>
						</c:choose>
					</c:set>
					<dsp:droplet name="BBBPopulateStatesDroplet">
						<dsp:param name="NoShowUSTerr" value="noShowOnBilling" />
						<dsp:oparam name="output">
							<c:choose>
								<c:when test="${empty preFillValues || preFillValues}">
									<dsp:select name="state" id="stateName" bean="BBBBillingAddressFormHandler.billingAddress.state">
										<dsp:tagAttribute name="aria-required" value="false"/>
										<dsp:tagAttribute name="aria-labelledby" value="stateLbl errorstateName"/>
										<option value="">
											<bbbl:label key="lbl_addressBook_select_state" language="${pageContext.request.locale.language}" />
										</option>
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="states" />
											<dsp:param name="elementName" value="state" />
											<dsp:oparam name="output">
												<option value="<dsp:valueof param='state.stateCode'/>">
													<dsp:valueof param='state.stateName' />
												</option>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:select>
								</c:when>
								<c:otherwise>
									<dsp:select name="state" id="stateName" bean="BBBBillingAddressFormHandler.billingAddress.state" nodefault="true">
										<dsp:tagAttribute name="aria-required" value="false"/>
										<dsp:tagAttribute name="aria-labelledby" value="stateLbl errorstateName"/>
										<option value="">
											<bbbl:label key="lbl_addressBook_select_state" language="${pageContext.request.locale.language}" />
										</option>
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="states" />
											<dsp:param name="elementName" value="state" />
											<dsp:oparam name="output">
												<option value="<dsp:valueof param='state.stateCode'/>">
													<dsp:valueof param='state.stateName' />
												</option>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:select>
								</c:otherwise>
							</c:choose>
						</dsp:oparam>
					</dsp:droplet>
                    <div class="input stateTextWrapper hidden">
                        <input type="text" value="" placeholder="${placeholderText} *" id="stateText" name="state" aria-required="true" aria-labelledby="stateLbl errorstateText" maxlength="40"/>
                        <%-- <dsp:input type="hidden" value="" id="stateTextInt" bean="BBBBillingAddressFormHandler.billingAddress.state"></dsp:input> --%>
                    </div>
				</div>
				<div id="zipContainer" class="small-12 large-6 columns">
					<c:set var="placeholderText">
						<c:choose>
							<c:when test="${currentSiteId eq 'TBS_BedBathCanada'}">
								<bbbl:label key="lbl_subscribe_canadazipcode" language="${pageContext.request.locale.language}" />
							</c:when>
							<c:otherwise>
								<bbbl:label key="lbl_shipping_new_zip" language="${pageContext.request.locale.language}" />
							</c:otherwise>
						</c:choose>
					</c:set>
					<c:if test='${defaultCountry eq "US"}'>
						<c:set var="zipClassCA" value="hidden"/>
						<c:set var="zipClassUS" value=""/>
					</c:if>
					<c:if test='${defaultCountry ne "US"}'>
						<c:set var="zipClassCA" value=""/>
						<c:set var="zipClassUS" value="hidden"/>
					</c:if>
							<dsp:input bean="BBBBillingAddressFormHandler.billingAddress.postalCode" type="text" value="" id="zipCodeCA" name="zipCodeCA" 
								iclass="zipCA ${zipClassCA}">
                            	<dsp:tagAttribute name="aria-required" value="true"/>
								<dsp:tagAttribute name="aria-labelledby" value="lblzipCode errorzipCode"/>
								<dsp:tagAttribute name="placeholder" value="${placeholderText} *"/>
							</dsp:input>
                            <dsp:input bean="BBBBillingAddressFormHandler.billingAddress.postalCode" type="text" value="" id="zipCode" name="zipCode" 
                            	iclass="zipUS  ${zipClassUS}">
                            	<dsp:tagAttribute name="aria-required" value="true"/>
								<dsp:tagAttribute name="aria-labelledby" value="lblzipCode errorzipCode"/>
								<dsp:tagAttribute name="placeholder" value="${placeholderText} *"/>
							</dsp:input>
				</div>
				
				<%-- <dsp:input bean="BBBBillingAddressFormHandler.billingAddress.postalCode" type="text" id="postalCode" value=""/> --%>
				
				<div class="small-12 large-offset-6 large-6 columns">
					<c:set var="placeholderText">Country</c:set>
					<dsp:droplet name="BBBCountriesDroplet">
						<dsp:oparam name="output">
							<dsp:getvalueof var="currentCountryName" bean="BBBBillingAddressFormHandler.billingAddress.country"/>
							<c:choose>
								<c:when test="${empty preFillValues || preFillValues}">
									<dsp:select name="countryName" id="countryName"  bean="BBBBillingAddressFormHandler.billingAddress.country">
										<dsp:tagAttribute name="aria-required" value="true"/>
										<dsp:tagAttribute name="aria-labelledby" value="lblcountryName errorcountryName"/>
										<option value="">Select Country</option>
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="country" />
											<dsp:param name="elementName" value="contname" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="countryName" param='key'/>
												<c:choose>
													<c:when test="${currentCountryName ne null && currentCountryName eq countryName}">
														<option value="<dsp:valueof param='key'/>" selected="selected">
															<dsp:valueof param='contname' />
														</option>
													</c:when>
													<c:when test="${(empty currentCountryName || currentCountryName eq null) && ((defaultCountry eq countryName) ||(defaultCountry eq 'Canada' &&  countryName eq 'CA'))}">
														<option value="<dsp:valueof param='key'/>" selected="selected">
															<dsp:valueof param='contname' />
														</option>
													</c:when>
													<c:otherwise>
														<option value="<dsp:valueof param='key'/>">
															<dsp:valueof param='contname' />
														</option>
													</c:otherwise>
												</c:choose>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:select>
								</c:when>
								<c:otherwise>
									<dsp:select name="countryName" id="countryName"  bean="BBBBillingAddressFormHandler.billingAddress.country" nodefault="true">
										<dsp:tagAttribute name="aria-required" value="true"/>
										<dsp:tagAttribute name="aria-labelledby" value="lblcountryName errorcountryName"/>
										<option value="">Select Country</option>
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="country" />
											<dsp:param name="elementName" value="contname" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="countryName" param='key'/>
												<c:choose>
													<c:when test="${currentCountryName ne null && currentCountryName eq countryName}">
														<option value="<dsp:valueof param='key'/>" selected="selected">
															<dsp:valueof param='contname' />
														</option>
													</c:when>
													<c:when test="${(empty currentCountryName || currentCountryName eq null) && ((defaultCountry eq countryName) ||(defaultCountry eq 'Canada' &&  countryName eq 'CA'))}">
														<option value="<dsp:valueof param='key'/>" selected="selected">
															<dsp:valueof param='contname' />
														</option>
													</c:when>
													<c:otherwise>
														<option value="<dsp:valueof param='key'/>">
															<dsp:valueof param='contname' />
														</option>
													</c:otherwise>
												</c:choose>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:select>
								</c:otherwise>
							</c:choose>
						</dsp:oparam>
					</dsp:droplet>
				</div>

				<dsp:droplet name="CheckProfileAddress">
					<dsp:param name="siteId" bean="ShoppingCart.current.siteId" />
					<dsp:param name="profile" bean="Profile" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="profileAddresses" param="profileAddresses"/>
					</dsp:oparam>
				</dsp:droplet>
				<c:choose>
					<c:when test="${isUserAuthed == 'TRUE'}">
						<div class="small-12 large-6 columns">
							<c:choose>
								<c:when test="${empty profileAddresses}">
									<label class="inline-rc checkbox disabled" id="lblsaveToAccount" for="saveAddressToAccount">
										<dsp:input type="checkbox" name="saveAddressToAccount" id="saveAddressToAccount" bean="BBBBillingAddressFormHandler.saveToAccount" value="TRUE" checked="true" disabled="true">
											<dsp:tagAttribute name="aria-checked" value="true"/>
											<dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
										</dsp:input>
										<dsp:input type="hidden" bean="BBBBillingAddressFormHandler.saveToAccount" value="true" />
										<span></span>
										<bbbl:label key="lbl_shipping_new_save_account" language="${pageContext.request.locale.language}" />
									</label>
								</c:when>
								<c:otherwise>
									<label class="inline-rc checkbox" id="lblsaveToAccount" for="saveAddressToAccount">
										<dsp:input type="checkbox" name="saveAddressToAccount" id="saveAddressToAccount" bean="BBBBillingAddressFormHandler.saveToAccount" value="TRUE" checked="true">
											<dsp:tagAttribute name="aria-checked" value="true"/>
											<dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
										</dsp:input>
										<span></span>
										<bbbl:label key="lbl_shipping_new_save_account" language="${pageContext.request.locale.language}" />
									</label>
								</c:otherwise>
							</c:choose>
						</div>
					</c:when>
					<c:otherwise>
						<dsp:input type="hidden" bean="BBBBillingAddressFormHandler.newAddress" value="true" />
					</c:otherwise>
				</c:choose>

			</div>
		</div>
	</div>
		<script type="text/javascript">
		$(document).ready(function(){
			 var $targetSave = $('#saveAddressToAccount');
			$('#countryName').change(function(){
				   if($(this).val() == 'PR'){ 
				    $targetSave.attr('checked',false);
				    $('label[for=saveAddressToAccount]').hide();
				  }else{
				  	$targetSave.attr('checked',true);
				    $('label[for=saveAddressToAccount]').show();
				  }
				});
			
		});
	
	</script>

</dsp:page>
