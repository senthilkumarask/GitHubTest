<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/checkout/formhandler/BBBBillingAddressFormHandler" />
	<dsp:importbean bean="/com/bbb/commerce/common/BBBPopulateStatesDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/com/bbb/commerce/common/BBBCountriesDroplet"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:getvalueof id="isAnonymousProfile" bean="Profile.transient" />	
	<c:set var="internationalCCFlag" value="${sessionScope.internationalCreditCard}"/>	
<div id="editAddressDialog" title="Billing Address" style="display:none;">
    <dsp:droplet name="/atg/dynamo/droplet/Switch">
        <dsp:param name="value" bean="BBBBillingAddressFormHandler.formError"/>
        <dsp:oparam name="false">
            <dsp:setvalue bean="BBBBillingAddressFormHandler.orderAddress" beanvalue="/atg/commerce/ShoppingCart.current.billingAddress"/>
        </dsp:oparam>
    </dsp:droplet>
	<dsp:form iclass="form clearfix" name="form" method="post" id="editAddressDialogForm" action="/store/checkout/payment/display_billing_address.jsp">
    <%-- <form class="form" id="editAddressDialogForm" action="index.jsp" method="post"> --%>
        <div class="container_6 clearfix">
            <p class="ccNameNumber hidden marTop_10 marBottom_10"><bbbl:label key="lbl_billing_address_edit_for" language="<c:out param='${language}'/>"/> <span class="ccInfo"></span></p>
            <div class="grid_3 noMar">
                <dsp:input type="hidden" name="checkoutEmail" id="checkoutEmail"
                    bean="BBBBillingAddressFormHandler.billingAddress.email" beanvalue="ShoppingCart.current.billingAddress.email"/>
                <dsp:getvalueof var="mobileNumber" bean="/atg/commerce/ShoppingCart.current.billingAddress.mobileNumber"/>
                <c:if test="${empty mobileNumber}">
	                <dsp:getvalueof var="mobileNumber" bean="/atg/userprofiling/Profile.mobileNumber"/>
	            </c:if>
                <dsp:input type="hidden" id="checkoutPhone"
                    bean="BBBBillingAddressFormHandler.billingAddress.mobileNumber" value="${mobileNumber}" />
                 <div class="inputField clearfix">
                    <label id="lbltxtAddressBookEditFirstName" for="txtAddressBookEditFirstName"><bbbl:label key="lbl_shipping_new_first_name" language="<c:out param='${language}'/>"/> <span class="required">*</span></label>
                    <dsp:input type="text" name="txtAddressBookAddFirstNameName" id="txtAddressBookEditFirstName"
								bean="BBBBillingAddressFormHandler.billingAddress.firstName" >
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookEditFirstName errortxtAddressBookEditFirstName"/>
                    </dsp:input>
                </div>

               <div class="inputField clearfix">
                    <label id="lbltxtAddressBookEditLastName" for="txtAddressBookEditLastName"><bbbl:label key="lbl_shipping_new_last_name" language="<c:out param='${language}'/>"/> <span class="required">*</span></label>
                    <%-- <input type="text" name="txtAddressBookAddLastNameName" id="txtAddressBookEditLastName" /> --%>
                    <dsp:input type="text"  name="txtAddressBookAddLastNameName" id="txtAddressBookEditLastName"
							bean="BBBBillingAddressFormHandler.billingAddress.lastName" >
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookEditLastName errortxtAddressBookEditLastName"/>
                    </dsp:input>
                </div>
                
               <div class="inputField clearfix">
                    <label id="lbltxtAddressBookEditCompany" for="txtAddressBookEditCompany"><bbbl:label key="lbl_shipping_new_company" language="<c:out param='${language}'/>"/></label>
                    <%-- <input type="text" name="txtAddressBookAddCompanyName" id="txtAddressBookEditCompany" /> --%>
                    <dsp:input type="text"  name="txtAddressBookAddCompanyName" id="txtAddressBookEditCompany"
								bean="BBBBillingAddressFormHandler.billingAddress.companyName" >
                        <dsp:tagAttribute name="aria-required" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookEditCompany errortxtAddressBookEditCompany"/>
                    </dsp:input>
                </div>
                <c:if test="${not isAnonymousProfile}">
                    <c:choose>
                        <c:when test="${internationalCCFlag}">
                            <div class="checkboxItem clearfix input marTop_10 noBorder saveToAccountWrapper hidden">
                                <div class="checkbox fl noMarRight">
                                    <dsp:input type="checkbox" name="saveToAccount" bean="BBBBillingAddressFormHandler.saveToAccount" disabled="true" id="saveToAccountEDITADDR" value="TRUE" >
                                        <dsp:tagAttribute name="aria-checked" value="true"/>
                                    </dsp:input>
                                </div>
                                <div class="label">
                                    <label for="saveToAccountEDITADDR"><bbbl:label key="lbl_shipping_new_save_account" language="${pageContext.request.locale.language}" /></label>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="checkboxItem clearfix input marTop_10 noBorder saveToAccountWrapper">
                                <div class="checkbox fl noMarRight">
                                    <dsp:input type="checkbox" name="saveToAccount" bean="BBBBillingAddressFormHandler.saveToAccount" id="saveToAccountEDITADDR" value="TRUE" >
                                        <dsp:tagAttribute name="aria-checked" value="true"/>
                                    </dsp:input>
                                </div>
                                <div class="label">
                                    <label for="saveToAccountEDITADDR"><bbbl:label key="lbl_shipping_new_save_account" language="${pageContext.request.locale.language}" /></label>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </c:if>
            </div>

            <div class="grid_3 noMar padLeft_20">
<%-- 			R2.1.1 International Credit Card  --%>
<%-- 			INPUT SELECT   --%>
				<div class="inputSelect countrySelectWrapper" >
						<div class="label">
                        	<label id="lblcountryName" for="countryName"><bbbl:label key="lbl_Country" language="${pageContext.request.locale.language}" /> <span class="required">*</span></label>
                    	</div>
					<div class="select">
						<dsp:droplet name="BBBCountriesDroplet">
							<dsp:oparam name="output">
								<dsp:getvalueof var="currentCountryName" bean="BBBBillingAddressFormHandler.billingAddress.country"/>
									<c:choose>
										<c:when test="${empty preFillValues || preFillValues}">
											<dsp:select name="countryName" id="countryName"  bean="BBBBillingAddressFormHandler.billingAddress.country" iclass="uniform">
			                                    <dsp:tagAttribute name="aria-required" value="true"/>
			                                    <dsp:tagAttribute name="aria-labelledby" value="lblcountryName errorcountryName"/>
												<option value=""><bbbl:label key="lbl_intl_shipping_modal_select_country" language="${pageContext.request.locale.language}" /></option>
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
											<dsp:select name="countryName" id="countryName"  bean="BBBBillingAddressFormHandler.billingAddress.country" nodefault="true" iclass="uniform">
			                                    <dsp:tagAttribute name="aria-required" value="true"/>
			                                    <dsp:tagAttribute name="aria-labelledby" value="lblcountryName errorcountryName"/>
												<option value=""><bbbl:label key="lbl_intl_shipping_modal_select_country" language="${pageContext.request.locale.language}" /></option>
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
                <div class="inputField clearfix">
                   <label id="lbltxtAddressBookEditAddress1" for="txtAddressBookEditAddress1"><bbbl:label key="lbl_shipping_address1" language="<c:out param='${language}'/>"/> <span class="required">*</span></label>
                   <%-- <input type="text" name="txtAddressBookAddAddress1Name" id="txtAddressBookEditAddress1" /> --%>
                   <dsp:input type="text"  name="txtAddressBookAddAddress1Name" id="txtAddressBookEditAddress1"
							bean="BBBBillingAddressFormHandler.billingAddress.address1">
                       <dsp:tagAttribute name="aria-required" value="true"/>
                       <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookEditAddress1 errortxtAddressBookEditAddress1"/>
					   <dsp:tagAttribute name="autocomplete" value="off"/>
                   </dsp:input>
             	  </div>  
                
                <div class="inputField clearfix">
                    <label id="lbltxtAddressBookEditAddress2" for="txtAddressBookEditAddress2"><bbbl:label key="lbl_shipping_address2" language="<c:out param='${language}'/>"/></label>
                    <%-- <input type="text" name="shippingAddress2" id="txtAddressBookEditAddress2" /> --%>
                    <dsp:input type="text"  name="txtAddressBookEditAddress2Name" id="txtAddressBookEditAddress2"
									bean="BBBBillingAddressFormHandler.billingAddress.address2" >
                        <dsp:tagAttribute name="aria-required" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookEditAddress2 errortxtAddressBookEditAddress2"/>
                    </dsp:input>
                </div>
                
                <div class="inputField clearfix">
                    <label id="lbltxtAddressBookEditCity" for="txtAddressBookEditCity"><bbbl:label key="lbl_shipping_new_city" language="<c:out param='${language}'/>"/> <span class="required">*</span></label>
                    <%-- <input type="text" name="city" id="txtAddressBookEditCity" /> --%>
                    <dsp:input type="text"  name="txtAddressBookEditCityName" id="txtAddressBookEditCity"
								bean="BBBBillingAddressFormHandler.billingAddress.city" maxlength="25" >
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookEditCity errortxtAddressBookEditCity"/>
                    </dsp:input>
                </div>
                
                 <div class="inputField clearfix stateSelectWrapper">
                    <label for="selAddressBookEditState" id="stateLbl"><span class="stateLabelText"><bbbl:label key="lbl_shipping_new_state" language="<c:out param='${language}'/>"/></span> <span class="required">*</span></label>
                    
                    <dsp:droplet name="BBBPopulateStatesDroplet">
                    <dsp:param name="NoShowUSTerr" value="noShowOnBilling" />
							<dsp:oparam name="output">
                                <dsp:getvalueof var="stateName"
                                                bean="BBBBillingAddressFormHandler.billingAddress.state"/>
									<c:choose>
										<c:when test="${empty preFillValues || preFillValues}">
											<dsp:select name="selAddressBookEditStateName" id="selAddressBookEditState"
												bean="BBBBillingAddressFormHandler.billingAddress.state" iclass="uniform">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="stateLbl errorselAddressBookEditState"/>
												<option value=""><bbbl:label key="lbl_shipping_new_selectstate" language="<c:out param='${language}'/>"/></option>
												<dsp:droplet name="ForEach">
													<dsp:param name="array" param="states" />
													<dsp:param name="elementName" value="state" />
													<dsp:oparam name="output">
                                                        <dsp:getvalueof var="currentStateName"
                                                 param='state.stateCode'/>
                                                        <c:choose>
                                                            <c:when test="${stateName ne null && currentStateName eq stateName}">
                                                                <option value="<dsp:valueof param='state.stateCode'/>" selected='selected'>
                                                                    <dsp:valueof param='state.stateName' />
                                                                </option>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <option value="<dsp:valueof param='state.stateCode'/>">
                                                                    <dsp:valueof param='state.stateName' />
                                                                </option>
                                                            </c:otherwise>
                                                        </c:choose>
													</dsp:oparam>
												</dsp:droplet>
											</dsp:select>
										</c:when>
										<c:otherwise>
											<dsp:select name="selAddressBookEditStateName" id="selAddressBookEditState"
												bean="BBBBillingAddressFormHandler.billingAddress.state"
												nodefault="true" iclass="uniform">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="stateLbl errorselAddressBookEditState"/>
												<option value=""><bbbl:label key="lbl_shipping_new_selectstate" language="<c:out param='${language}'/>"/></option>
												<dsp:droplet name="ForEach">
													<dsp:param name="array" param="states" />
													<dsp:param name="elementName" value="state" />
													<dsp:oparam name="output">
                                                        <dsp:getvalueof var="currentStateName"
                                                 param='state.stateCode'/>
                                                        
														<c:choose>
                                                            <c:when test="${stateName ne null && currentStateName eq stateName}">
                                                                <option value="<dsp:valueof param='state.stateCode'/>" selected='selected'>
                                                                    <dsp:valueof param='state.stateName' />
                                                                </option>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <option value="<dsp:valueof param='state.stateCode'/>">
                                                                    <dsp:valueof param='state.stateName' />
                                                                </option>
                                                            </c:otherwise>
                                                        </c:choose>
													</dsp:oparam>
												</dsp:droplet>
											</dsp:select>
										</c:otherwise>
									</c:choose>
								<label id="errorselAddressBookEditState" for="selAddressBookEditState" generated="true" class="error"></label>
								<label for="selAddressBookEditState" class="offScreen"><bbbl:label key="lbl_shipping_new_state" language="<c:out param='${language}'/>"/> <span class="required">*</span></label>
							</dsp:oparam>
						</dsp:droplet>
                </div>
                <%-- INPUT SELECT --%>
                <div class="inputField stateTextWrapper hidden">
                    <div class="label">
                        <label for="stateText" id="stateLbl"><span class="stateLabelText">State</span> <span class="required">*</span></label>
                    </div>
                    <div class="text">
                        <div>
                            <input type="text" value="" id="stateText" name="stateText" aria-required="true" aria-labelledby="stateLbl errorstateText" maxlength="40"/>
	                        <dsp:input type="hidden" value="" id="stateTextInt" bean="BBBBillingAddressFormHandler.billingAddress.state" />
                        </div>
                    </div>
                </div>
                
                <%-- END INPUT SELECT --%>
				<div class="inputField clearfix">
					<label for="txtAddressBookEditZip" id="lblZipCode"><span class="zipCodeLabelText"><bbbl:label key="lbl_shipping_new_zip" language="<c:out param='${language}'/>"/></span> <span class="required">*</span></label> 
                    <c:choose>
                        <c:when test="${defaultCountry ne 'US'}">
                          <c:set var="zipCodeClass" value="zipCA" scope="page"/>
                        </c:when>
                        <c:otherwise>
                          <c:set var="zipCodeClass" value="zipUS" scope="page"/>
                        </c:otherwise>
                    </c:choose>		
					<input type="text" name="${zipCodeClass}" iclass="width_1" id="txtAddressBookEditZip" aria-required="true" aria-labelledby="lblZipCode errortxtAddressBookEditZip"/>
                    <dsp:input type="hidden" id="zipAll" value="" bean="BBBBillingAddressFormHandler.billingAddress.postalCode" ></dsp:input> 	
				</div>
				
            </div>
        </div>
        
        <dsp:input type="hidden" bean="BBBBillingAddressFormHandler.userSelectedOption" value="EDIT"/>
        <!--<dsp:input type="hidden" bean="BBBBillingAddressFormHandler.successURL" value="/store/checkout/payment/billing_payment.jsp"/>
        <dsp:input type="hidden" bean="BBBBillingAddressFormHandler.errorURL" value="/store/checkout/payment/billing_payment.jsp"/>
        <dsp:input type="hidden" bean="BBBBillingAddressFormHandler.systemErrorURL" value="/store/checkout/payment/billing_payment.jsp"/>-->
        <%-- USED TO HANDLE RESPONSE --%>
        <dsp:input type="hidden" bean="BBBBillingAddressFormHandler.fromAjaxSubmission" value="true"/>
        
        <div class="marTop_20 buttonpane clearfix">
            <div class="ui-dialog-buttonset">
                <div class="button button_active">
                    <c:set var="lbl_button_save" scope="page">
                        <bbbl:label key="lbl_payament_button_save"
                            language="${pageContext.request.locale.language}" />
                    </c:set>

                    <dsp:input bean="BBBBillingAddressFormHandler.saveBillingAddress"
                                type="submit" value="${lbl_button_save}" id="editAddressSave" >
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="editAddressSave"/>
                        <dsp:tagAttribute name="role" value="button"/>
                    </dsp:input>
                </div>
                <a href="#" class="buttonTextLink close-any-dialog" role="link"><bbbl:label key="lbl_bridalbook_cancel" language="${pageContext.request.locale.language}"/></a>
            </div>
        </div>
        <input class="cardId" type="hidden" />

	<%-- </form> --%>
    </dsp:form>
</div>
<div id="updateAddressWarningDialog" title="Update Address" style="display:none;">
    <bbbl:label key="lbl_billingaddresschange_note1" language="${pageContext.request.locale.language}" />
</div>
<div id="removeAddressWarningDialog" title="Remove Address" style="display:none;">
    <bbbl:label key="lbl_billingaddresschange_note2" language="${pageContext.request.locale.language}" />
</div>
</dsp:page>
