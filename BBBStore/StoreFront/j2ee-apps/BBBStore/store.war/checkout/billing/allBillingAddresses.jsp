<dsp:page>

	<dsp:importbean bean="/com/bbb/commerce/checkout/formhandler/BBBBillingAddressFormHandler" />
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/BBBPackNHoldDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="defaultCountry" bean="Site.defaultCountry" />
	<dsp:getvalueof param="colg" var="college"/>	
	<dsp:input type="hidden" bean="BBBBillingAddressFormHandler.collegeAddress" value="${college}" />
	<c:set var="internationalCCFlagBill" value="${sessionScope.internationalCreditCard}"/>
	<dsp:setvalue bean="BBBBillingAddressFormHandler.userSelectedOption" paramvalue="selectedAddrKey" />
	<dsp:getvalueof id ="order" bean="ShoppingCart.current" />
	<dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
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
					<div class="radioItem input clearfix">
					 <dsp:getvalueof param="count" var="loopIndex"/>
					 	<%-- R2.2 Story - AY - send userSelectedOption as 'Paypal' if continue to paypal --%>
					 	<div class="radio">
					 		<%--R2.2 Story - AY - Disable field if order is of type Paypal and continue to paypal is selected --%>
							<c:choose>
								<c:when test = "${isPayPal eq 'true'}">
									<dsp:input type="radio" value="paypalAddress"
										bean="BBBBillingAddressFormHandler.userSelectedOption"
										name="addressToShip" id="billingAddressOption${loopIndex}" iclass="paypalBillingAdd" checked="true">
		                                <dsp:tagAttribute name="aria-checked" value="false"/>
		                                <dsp:tagAttribute name="aria-labelledby" value="lblbillingAddressOption${loopIndex}"/>
		                            </dsp:input>
								</c:when>
								<c:otherwise>
									<dsp:input type="radio" value="${id}"
										bean="BBBBillingAddressFormHandler.userSelectedOption"
										name="addressToShip" id="billingAddressOption${loopIndex}" iclass="">
		                                <dsp:tagAttribute name="aria-checked" value="false"/>
		                                <dsp:tagAttribute name="aria-labelledby" value="lblbillingAddressOption${loopIndex}"/>
		                            </dsp:input>
								</c:otherwise>
							</c:choose>
							
						</div>
						<div class="label addressDetails">
							<%-- R2.2 Story - AY - display 'continue to paypal' if order is of type Paypal --%>
							<c:choose>
								<c:when test="${isPayPal eq 'true'}">
									<bbbl:label key="lbl_continue_with_paypal" language="${pageContext.request.locale.language}" />
								</c:when>
								<c:otherwise>
									<label id="lblbillingAddressOption${loopIndex}" for="billingAddressOption${loopIndex}"> <span> <dsp:valueof
												param="element.firstName" /> <dsp:valueof
												param="element.lastName" /> </span>
												 
												 <c:if test="${college ne true}">										  
												    <dsp:getvalueof var="tempCompanyName" param="element.companyName" />
													<c:if test="${tempCompanyName != ''}">
															 <span><dsp:valueof param="element.companyName" /></span>
													</c:if>											
												  </c:if>
												 
												<span>
													<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
														<dsp:param name="value" param="element.address2"/>
														<dsp:oparam name="true">
															<dsp:valueof param="element.address1"  />
														</dsp:oparam>
														<dsp:oparam name="false">
															<dsp:valueof param="element.address1"  />, <dsp:valueof param="element.address2" />
														</dsp:oparam>
													</dsp:droplet>
												</span> 
												<span> <dsp:valueof
												param="element.city" />, <dsp:valueof param="element.state" />
											 <dsp:valueof param="element.postalCode" /> </span>
											  <span>
		                                          <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
		                                          	<dsp:param name="value" param="element.countryName" />
		                                          	<dsp:oparam name="false">
		                                          		<c:choose>
		                                          			<c:when test="${internationalCCFlagBill}">
		                                          				<span class="countryName internationalCountry"><dsp:valueof param="element.countryName" /></span>
		                                          			</c:when>
		                                          			<c:otherwise>
		                                          				<span class="countryName hidden"><dsp:valueof param="element.countryName" /></span>
		                                          			</c:otherwise>
		                                          		</c:choose>
		                                          		<span class="country hidden"><dsp:valueof param="element.country" /></span>
		                                          	</dsp:oparam>
		                                          	<dsp:oparam name="true">
		                                          		<span class="country hidden">${defaultCountry}</span>
		                                          	</dsp:oparam>
		                                          </dsp:droplet>
		                                       </span>
										</label>
									</c:otherwise>
							</c:choose>
						</div>
					</div>
				</dsp:oparam>
			</dsp:droplet>
</dsp:page>
