<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/bbb/commerce/checkout/formhandler/BBBBillingAddressFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/commerce/common/BBBCountriesDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>

	<%-- Variables --%>
	<dsp:getvalueof var="defaultCountry" bean="Site.defaultCountry" />
	<dsp:getvalueof param="colg" var="college"/>
	<c:set var="internationalCCFlagBill" value="${sessionScope.internationalCreditCard}"/>
	<dsp:setvalue bean="BBBBillingAddressFormHandler.userSelectedOption" paramvalue="selectedAddrKey" />
	<dsp:getvalueof id ="order" bean="ShoppingCart.current" />
	<dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
	<dsp:getvalueof var="selectedAddrKey" param="selectedAddrKey" />
	
	<dsp:input type="hidden" bean="BBBBillingAddressFormHandler.collegeAddress" value="${college}" />

	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="addresses" />
		<dsp:oparam name="output">
			<dsp:getvalueof var ="id" param="element.id" />
			<c:if test="${not payPalOrder}">
				<dsp:getvalueof var ="payPalOrder" param="element.fromPaypal"/>
			</c:if>
			<c:choose>
				<c:when test="${payPalOrder eq true && order.billingAddress.id eq id}">
					<c:set var="isPayPal" value="true"/>
				</c:when>
				<c:otherwise>
					<c:set var="isPayPal" value="false"/>
				</c:otherwise>
			</c:choose>
			<dsp:getvalueof param="count" var="loopIndex"/>

			<label class="inline-rc radio gray-panel billing-address" id="lblbillingAddressOption${loopIndex}" for="billingAddressOption${loopIndex}">
				<%--R2.2 Story - AY - Disable field if order is of type Paypal and continue to paypal is selected --%>
				<c:choose>
					<c:when test="${isPayPal eq 'true'}">
						<dsp:input type="radio" value="paypalAddress" bean="BBBBillingAddressFormHandler.userSelectedOption" name="addressToShip" id="billingAddressOption${loopIndex}" iclass="paypalBillingAdd" checked="true">
							<dsp:tagAttribute name="aria-checked" value="false"/>
							<dsp:tagAttribute name="aria-labelledby" value="lblbillingAddressOption${loopIndex}"/>
						</dsp:input>
					</c:when>
					<c:otherwise>
						<dsp:input type="radio" value="${id}" bean="BBBBillingAddressFormHandler.userSelectedOption" name="addressToShip" id="billingAddressOption${loopIndex}" iclass="">
							<dsp:tagAttribute name="aria-checked" value="false"/>
							<dsp:tagAttribute name="aria-labelledby" value="lblbillingAddressOption${loopIndex}"/>
						</dsp:input>
					</c:otherwise>
				</c:choose>
				<span></span>
				<%-- R2.2 Story - AY - display 'continue to paypal' if order is of type Paypal --%>
				<c:choose>
					<c:when test="${isPayPal eq 'true'}">
						<bbbl:label key="lbl_continue_with_paypal" language="${pageContext.request.locale.language}" />
					</c:when>
					<c:otherwise>
						<ul class="address">
							<li><dsp:valueof param="element.firstName" /> <dsp:valueof param="element.lastName" /></li>
							<c:if test="${college ne true}">
								<dsp:getvalueof var="tempCompanyName" param="element.companyName" />
								<c:if test="${tempCompanyName != ''}">
									<li><dsp:valueof param="element.companyName" /></li>
								</c:if>
							</c:if>
							<li>
								<dsp:droplet name="IsEmpty">
									<dsp:param name="value" param="element.address2"/>
									<dsp:oparam name="true">
										<dsp:valueof param="element.address1" />
									</dsp:oparam>
									<dsp:oparam name="false">
										<dsp:valueof param="element.address1" />, <dsp:valueof param="element.address2" />
									</dsp:oparam>
								</dsp:droplet>
							</li>
							<li><dsp:valueof param="element.city" />, <dsp:valueof param="element.state" /> <dsp:valueof param="element.postalCode" /></li>
							<c:if test="${selectedAddrKey eq id}">
								<input type="hidden" name="selectedBillingAddress" data-firstName="<dsp:valueof param="element.firstName"/>"  
									data-lastName="<dsp:valueof param="element.lastName"/>"
									data-address1="<dsp:valueof param="element.address1"/>"
									data-address2="<dsp:valueof param="element.address2"/>"
									data-companyName="<dsp:valueof param="element.companyName"/>"
									data-city="<dsp:valueof param="element.city"/>"
									data-state="<dsp:valueof param="element.state"/>"
									data-postalCode="<dsp:valueof param="element.postalCode"/>"
									data-countryName="<dsp:valueof param="element.countryName"/>"
									data-country="<dsp:valueof param="element.country"/>" />
							</c:if>
							<dsp:droplet name="IsEmpty">
								<dsp:param name="value" param="element.countryName" />
								<dsp:oparam name="false">
									<c:choose>
										<c:when test="${internationalCCFlagBill}">
											<li class="countryName internationalCountry"><dsp:valueof param="element.countryName" /></li>
										</c:when>
										<c:otherwise>
											<li class="countryName"><dsp:valueof param="element.countryName" /></li>
										</c:otherwise>
									</c:choose>
									<li class="country hidden"><dsp:valueof param="element.country" /></li>
									<c:set var="countryCodes"><dsp:valueof param="element.country" /></c:set>
								</dsp:oparam>
								<dsp:oparam name="true">
								<c:choose>
								<c:when test="${defaultCountry eq 'Canada'}">
									<c:set var="countryCodes"> <dsp:valueof value="CA"/></c:set>
								</c:when>
								<c:otherwise>
									<c:set var="countryCodes"> <dsp:valueof value="${defaultCountry}"/></c:set>
								</c:otherwise>
								</c:choose>
									<li class="country hidden">${defaultCountry}</li>
									<li class="countryName">
										<dsp:droplet name="BBBCountriesDroplet">
											<dsp:param name="countryCode" value="${countryCodes}"/>
											<dsp:oparam name="output">
												<dsp:droplet name="ForEach">
													<dsp:param name="array" param="country" />
													<dsp:param name="elementName" value="contname" />
													<dsp:oparam name="output">
														<dsp:getvalueof param="key" var="countryCode"/>
														<c:if test="${countryCodes eq countryCode}">
															<dsp:valueof param="contname"/>
														</c:if>
													</dsp:oparam>
												</dsp:droplet>
											</dsp:oparam>
										</dsp:droplet>
									</li>
								</dsp:oparam>
							</dsp:droplet>
							<c:if test="${selectedAddrKey eq id}">
								<input type="hidden" name="selectedBillingAddress" data-firstName="<dsp:valueof param="element.firstName"/>"  
									data-lastName="<dsp:valueof param="element.lastName"/>"
									data-address1="<dsp:valueof param="element.address1"/>"
									data-address2="<dsp:valueof param="element.address2"/>"
									data-companyName="<dsp:valueof param="element.companyName"/>"
									data-city="<dsp:valueof param="element.city"/>"
									data-state="<dsp:valueof param="element.state"/>"
									data-postalCode="<dsp:valueof param="element.postalCode"/>"
									data-countryName="<dsp:valueof param="element.countryName"/>"
									data-country="${countryCodes}" />
							</c:if>
						</ul>
					</c:otherwise>
				</c:choose>
			</label>
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
