<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/checkout/formhandler/BBBBillingAddressFormHandler" />
	<dsp:importbean bean="/com/bbb/commerce/common/BBBPopulateStatesDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/common/BBBCountriesDroplet"/>	
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/BBBBillingAddressDroplet" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/com/bbb/commerce/common/BBBAddressContainer" />
	<c:set var="internationalCCFlag" value="${sessionScope.internationalCreditCard}"/>
	
	<div class="clear"></div>
	<fieldset class="radioGroup">
		<legend class="hidden">
			<label> 
				<bbbl:label key="lbl_billing_address_match"
					language="${pageContext.request.locale.language}" /> 
			</label>
		</legend>
		<%-- <dsp:droplet name="BBBBillingAddressDroplet">
			<dsp:param name="order" bean="ShoppingCart.current" />
			<dsp:param name="profile" bean="Profile" />
			<dsp:param name="billingAddrContainer" bean="BBBAddressContainer" />		
			<dsp:oparam name="output">
				<dsp:include page="/checkout/billing/allBillingAddresses.jsp" >
					<dsp:param name="selectedAddrKey" param="selectedAddrKey"/>
					<dsp:param name="addresses" param="addresses"/>
				</dsp:include>
				<dsp:getvalueof var="addresses" param="addresses"/>				
			</dsp:oparam>
		</dsp:droplet> --%>
		<dsp:include page="/checkout/billing/allBillingAddresses.jsp" >
			<dsp:param name="selectedAddrKey" param="selectedAddrKey"/>
			<dsp:param name="addresses" param="addresses"/>
		</dsp:include>
		<dsp:getvalueof var="addresses" param="addresses"/>
		
		<div class="radioItem input clearfix last">
			<div class="radio">
				<c:choose>
					<c:when test="${addresses ne null && not empty addresses}">
						<dsp:input type="radio" name="addressToShip" iclass="newAddOpt"
							bean="BBBBillingAddressFormHandler.userSelectedOption" value="NEW" id="addressToShip4" >
                            <dsp:tagAttribute name="aria-checked" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip4"/>
                        </dsp:input>
					</c:when>
					<c:otherwise>
						<dsp:input type="radio" name="addressToShip" iclass="newAddOpt" checked="true"
							bean="BBBBillingAddressFormHandler.userSelectedOption" value="NEW" id="addressToShip4" >
                            <dsp:tagAttribute name="aria-checked" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip4"/>
                        </dsp:input>
					</c:otherwise>
				</c:choose>
				
			</div>
			<div class="label">
				<label id="lbladdressToShip4" for="addressToShip4"> <span>Add new billing
						address</span> </label>
			</div>
			<div class="clear"></div>
			<div class="subForm clearfix hidden">
				<div class="fieldsInlineWrapper clearfix">
					<div class="input">
						<div class="label">
							<label id="lblcheckoutfirstName" for="checkoutfirstName"> <bbbl:label
									key="lbl_shipping_new_first_name"
									language="${pageContext.request.locale.language}" /> <span
								class="required">*</span> </label>
						</div>
						<div class="text" aria-live="assertive">
							<dsp:input type="text" value="" name="checkoutfirstName" id="checkoutfirstName"
								bean="BBBBillingAddressFormHandler.billingAddress.firstName" >
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblcheckoutfirstName errorcheckoutfirstName"/>
                                <dsp:tagAttribute name="aria-describedby" value="errorcheckoutfirstName"/> 
                            </dsp:input>
						</div>
						<div class="clear"></div>
					</div>
				</div>

				
				<div class="input clearfix">
					<div class="label">
						<label id="lblcheckoutlastName" for="checkoutlastName">
						<bbbl:label
									key="lbl_shipping_new_last_name"
									language="${pageContext.request.locale.language}" />
						<span class="required">*</span>
						</label>
					</div>
					<div class="text" aria-live="assertive">
						<dsp:input type="text" value="" id="checkoutlastName" name="checkoutlastName"
							bean="BBBBillingAddressFormHandler.billingAddress.lastName" >
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblcheckoutlastName errorcheckoutlastName"/>
							<dsp:tagAttribute name="aria-describedby" value="errorcheckoutfirstName"/> 
                        </dsp:input>
					</div>
				</div>
				

				
				<div class="input clearfix">
					<div class="label">
						<label id="lblcompany" for="company">
						<bbbl:label
									key="lbl_shipping_new_company"
									language="${pageContext.request.locale.language}" />
						</label>
					</div>
					<div class="text">
						<div>
							<dsp:input type="text" value="" id="company" name="company"
								bean="BBBBillingAddressFormHandler.billingAddress.companyName" >
                                <dsp:tagAttribute name="aria-required" value="false"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lblcompany errorcompany"/>
                            </dsp:input>
						</div>
					</div>
				</div>
	
<%-- 				R2.1.1 International Credit Card  --%>
<%-- 				 INPUT SELECT   --%>
				<div class="input countrySelectWrapper" >
					<div class="label">
						<label id="lblcountryName" for="countryName">Country <span class="required">*</span></label>
					</div>
					<div class="select">
						<dsp:droplet name="BBBCountriesDroplet">
							<dsp:oparam name="output">
								<dsp:getvalueof var="currentCountryName" bean="BBBBillingAddressFormHandler.billingAddress.country"/>
									<c:choose>
										<c:when test="${empty preFillValues || preFillValues}">
											<dsp:select name="countryName" id="countryName"  bean="BBBBillingAddressFormHandler.billingAddress.country">
			                                    <dsp:tagAttribute name="aria-required" value="true"/>
			                                    <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblcountryName errorcountryName"/>
												<dsp:tagAttribute name="aria-describedby" value="errorcountryName"/> 
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
				</div>
			<%-- END INPUT SELECT --%>
<%-- 		R2.1.1 International Credit Card ends --%>
					<div class="fieldsInlineWrapper clearfix">
					<div class="input">
						<div class="label">
							<label id="lbladdress1" for="address1">
								<bbbl:label key="lbl_shipping_new_line1" language="${pageContext.request.locale.language}" />
								<span class="required">*</span>
							</label>
						</div>
						<div class="text" aria-live="assertive">
							<dsp:input type="text" value="" id="address1" name="address1"
								bean="BBBBillingAddressFormHandler.billingAddress.address1" >
                                <dsp:tagAttribute name="autocomplete" value="off"/>
								<dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lbladdress1 erroraddress1"/>
								<dsp:tagAttribute name="aria-describedby" value="erroraddress1"/> 
								
                            </dsp:input>
						</div>
					</div>
					
					<div class="input">
						<div class="label">
							<label id="lbladdress2" for="address2">
								<bbbl:label key="lbl_shipping_new_line2" language="${pageContext.request.locale.language}" />
							</label>
						</div>
						<div class="text">
							<div class="text">
								<dsp:input type="text" value="" id="address2" name="address2"
									bean="BBBBillingAddressFormHandler.billingAddress.address2" >
                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="lbladdress2 erroraddress2"/>
                                </dsp:input>
							</div>
						</div>
					</div>
					
				</div>

				<div class="fieldsInlineWrapper clearfix">
					<div class="input">
						<div class="label">
							<label id="lblcityName" for="cityName">
								<bbbl:label key="lbl_shipping_new_city" language="${pageContext.request.locale.language}" />
								<span class="required">*</span>
							</label>
						</div>
						<div class="text" aria-live="assertive">
							<dsp:input type="text" value="" id="cityName" name="cityName"
								bean="BBBBillingAddressFormHandler.billingAddress.city" maxlength="25">
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="errormessage1 lblcityName errorcityName"/>
								<dsp:tagAttribute name="aria-describedby" value="errorcityName"/> 
                            </dsp:input>
						</div>
					</div>
		
					<div class="input  stateSelectWrapper" aria-live="assertive">
						<div class="label">
							<label for="stateName" id="stateLbl">
								 <c:choose>
									 <c:when test="${currentSiteId eq 'BedBathCanada'}">
										  <span class="stateLabelText"><bbbl:label key="lbl_addnewbillingaddressprovince" language="${pageContext.request.locale.language}" /></span>
									  </c:when>
									  <c:otherwise>
									      <span class="stateLabelText"><bbbl:label key="lbl_shipping_new_state" language="${pageContext.request.locale.language}" /></span>
									  </c:otherwise>
								  </c:choose>
								<span class="required">*</span>
							</label>
						</div>
						<dsp:droplet name="BBBPopulateStatesDroplet">
						<dsp:param name="NoShowUSTerr" value="noShowOnBilling" />
							<dsp:oparam name="output">
								<div class="select noMarTop cb">
									<c:choose>
										<c:when test="${empty preFillValues || preFillValues}">
											<dsp:select name="stateName" id="stateName"
												bean="BBBBillingAddressFormHandler.billingAddress.state">
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="errormessage1 stateLbl errorstateName"/>
												<dsp:tagAttribute name="aria-describedby" value="errorstateName"/> 
												<c:choose>
													 <c:when test="${currentSiteId eq 'BedBathCanada'}">
														  <option value=""><bbbl:label key="lbl_js_province" language="${pageContext.request.locale.language}" /></option>	
													  </c:when>
													  <c:otherwise>
													      <option value=""><bbbl:label key="lbl_search_store_select_state" language="${pageContext.request.locale.language}" /></option>
													  </c:otherwise>
											   </c:choose>
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
											<dsp:select name="stateName" id="stateName"
												bean="BBBBillingAddressFormHandler.billingAddress.state"
												nodefault="true">
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="stateLbl errorstateName"/>
												<c:choose>
													 <c:when test="${currentSiteId eq 'BedBathCanada'}">
														  <option value=""><bbbl:label key="lbl_js_province" language="${pageContext.request.locale.language}" /></option>	
													  </c:when>
													  <c:otherwise>
													      <option value=""><bbbl:label key="lbl_search_store_select_state" language="${pageContext.request.locale.language}" /></option>
													  </c:otherwise>
											   </c:choose>
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
								</div>
							</dsp:oparam>
						</dsp:droplet>
					</div>
					
					 <%-- INPUT SELECT --%>
						<div class="input stateTextWrapper hidden">
							<div class="label">
								<label for="stateText" id="stateLbl"><span class="stateLabelText"><bbbl:label key="lblstate" language="${pageContext.request.locale.language}" /></span> <span class="required">*</span></label>
							</div>
	                                 <div class="text">
	                                     <input type="text" value="" id="stateText" name="stateText" aria-required="true" aria-labelledby="stateLbl errorstateText" maxlength="40"/>
	                                     
	                                    	<dsp:input type="hidden" value="" id="stateTextInt" bean="BBBBillingAddressFormHandler.billingAddress.state">
	                                    	</dsp:input>
	                                 </div>
						</div>
					<%-- END INPUT SELECT --%>
				</div>
					
				<div class="input clearfix">
					<div class="label">
						<label for="zip" id="lblZipCode">
							<c:choose>
								 <c:when test="${currentSiteId eq 'BedBathCanada'}">
									 <span class="zipCodeLabelText"><bbbl:label key="lbl_subscribe_canadazipcode" language="${pageContext.request.locale.language}" /></span>
								  </c:when>
								  <c:otherwise>
								      <span class="zipCodeLabelText"><bbbl:label key="lbl_shipping_new_zip" language="${pageContext.request.locale.language}" /></span>
								  </c:otherwise>
						   </c:choose>
						
							
							<span class="required">*</span>
						</label>
					</div>
                    <c:choose>
                        <c:when test="${defaultCountry ne 'US'}">
                          <c:set var="zipCodeClass" value="zipCA" scope="page"/>
                        </c:when>
                        <c:otherwise>
                          <c:set var="zipCodeClass" value="zipUS" scope="page"/>
                        </c:otherwise>
                    </c:choose>							
					<div class="text" aria-live="assertive">
							<input type="text" name="${zipCodeClass}" value="" id="zip" aria-required="true" aria-describedby="errorzip" aria-labelledby="errormessage1 lblZipCode errorzip" />
                        	<dsp:input type="hidden" bean="BBBBillingAddressFormHandler.billingAddress.postalCode" value="" id="zipAll" />
					</div>
				</div>
				<%-- <dsp:getvalueof var="isUserAuthenticated"
				<%-- <dsp:test var="isUserAuthenticated" value="TRUE"/> --%>
				<dsp:droplet name="/com/bbb/commerce/shipping/droplet/CheckProfileAddress">
					<dsp:param name="siteId" bean="ShoppingCart.current.siteId" />
					<dsp:param name="profile" bean="/atg/userprofiling/Profile" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="profileAddresses" param="profileAddresses"/>		
					</dsp:oparam>
				</dsp:droplet>
				<c:choose>
					<c:when test="${isUserAuthed == 'TRUE'}">
                        <div class="clear"></div>
						<div class="checkboxItem input clearfix saveToAccountWrapper">
							<div class="checkbox">
								<c:choose>
									<c:when test="${empty profileAddresses}">															
										<dsp:input type="checkbox" name="saveToAccount" id="saveToAccount"
											bean="BBBBillingAddressFormHandler.saveToAccount" value="TRUE" checked="true" disabled="true">
                                            <dsp:tagAttribute name="aria-checked" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
                                        </dsp:input>
                                        
										<dsp:input type="hidden" bean="BBBBillingAddressFormHandler.saveToAccount" value="true" />
									</c:when>
									
									<c:otherwise>
										<dsp:input type="checkbox" name="saveToAccount" id="saveToAccount"
											bean="BBBBillingAddressFormHandler.saveToAccount" value="TRUE" checked="true">
                                            <dsp:tagAttribute name="aria-checked" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
                                        </dsp:input>
									</c:otherwise>
								</c:choose>
							</div>
							<div class="label">
								<label id="lblsaveToAccount" for="saveToAccount">
									<bbbl:label key="lbl_shipping_new_save_account" language="${pageContext.request.locale.language}" />
								</label>
							</div>
						</div>
					</c:when>
					<c:otherwise>
						<dsp:input type="hidden" bean="BBBBillingAddressFormHandler.newAddress" value="true" />
					</c:otherwise>
				</c:choose>

			</div>
		</div>
	</fieldset>

</dsp:page>