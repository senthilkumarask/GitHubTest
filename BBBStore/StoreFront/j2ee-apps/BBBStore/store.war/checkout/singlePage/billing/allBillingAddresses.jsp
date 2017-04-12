<dsp:page>

	<dsp:importbean bean="/com/bbb/commerce/checkout/formhandler/BBBSPBillingAddressFormHandler" />
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/BBBPackNHoldDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:getvalueof var="defaultCountry" bean="Site.defaultCountry" />
	<dsp:getvalueof param="colg" var="college"/>	
	<dsp:input type="hidden" bean="BBBSPBillingAddressFormHandler.collegeAddress" value="${college}" />
	<c:set var="internationalCCFlagBill" value="${sessionScope.internationalCreditCard}"/>
	<dsp:setvalue bean="BBBSPBillingAddressFormHandler.userSelectedOption" paramvalue="selectedAddrKey" />
	<dsp:getvalueof id ="order" bean="ShoppingCart.current" />
	<dsp:getvalueof id ="payPalOrder" bean="ShoppingCart.current.payPalOrder" />
	<dsp:getvalueof param="gotBillingAddress" var="gotBillingAddress"/>
	<dsp:getvalueof param="currentBillingAddress" var="currentBillingAddress"/>

	
	<dsp:getvalueof var="defaultBillingID"	bean="Profile.billingAddress.repositoryId" />

	


	<c:if test="${gotBillingAddress}">

		
		<dsp:getvalueof var ="id" param="currentBillingAddress.id" />
		<c:if test="${not payPalOrder}">
			<dsp:getvalueof var ="payPalOrder" param="currentBillingAddress.fromPaypal"/>
		</c:if>
		<c:choose>
			<c:when test="${payPalOrder eq true && order.billingAddress.id eq id}">
				<c:set var="isPayPal" value="true"/>
			</c:when>
			<c:otherwise>
				<c:set var="isPayPal" value="false"/>
			</c:otherwise>
		</c:choose>

		<div class="radioItem input clearfix <c:if test = "${!payPalOrder}">preselected</c:if>">

			<h3><bbbl:label key="lbl_spc_billing_address_header" language="${pageContext.request.locale.language}" /></h3>	
		 	
		 	<%-- R2.2 Story - AY - send userSelectedOption as 'Paypal' if continue to paypal --%>
		 	
			<div class="label">
				
				
				<c:choose>
					<c:when test = "${isPayPal eq 'true'}">
						<dsp:input type="radio" value="paypalAddress"
						bean="BBBSPBillingAddressFormHandler.userSelectedOption"
							name="addressToShip" id="billingAddressOption${loopIndex}" iclass="paypalBillingAdd" checked="true">
                             <dsp:tagAttribute name="aria-checked" value="false"/>
                             <dsp:tagAttribute name="aria-labelledby" value="lblbillingAddressOption${loopIndex}"/>
                         </dsp:input>
					</c:when>
					<%--
					<c:otherwise>						
						<dsp:input type="hidden" value="${id}"
						bean="BBBSPBillingAddressFormHandler.userSelectedOption"
							name="addressToShip" id="billingAddressOption${loopIndex}" iclass="">
                             <dsp:tagAttribute name="aria-checked" value="false"/>
                             <dsp:tagAttribute name="aria-labelledby" value="lblbillingAddressOption${loopIndex}"/>
                         </dsp:input>
					</c:otherwise>
					--%>
				</c:choose>
				<%----%>


				<%-- R2.2 Story - AY - display 'continue to paypal' if order is of type Paypal --%>
				<c:choose>
					<c:when test="${isPayPal eq 'true'}">
						<bbbl:label key="lbl_spc_continue_with_paypal" language="${pageContext.request.locale.language}" />
					</c:when>
					<c:otherwise>
						<label> 
							<span> <dsp:valueof
									param="currentBillingAddress.firstName" /> <dsp:valueof
									param="currentBillingAddress.lastName" /> </span>
									 
									 <c:if test="${college ne true}">										  
									    <dsp:getvalueof var="tempCompanyName" param="currentBillingAddress.companyName" />
										<c:if test="${tempCompanyName != ''}">
												 <span><dsp:valueof param="currentBillingAddress.companyName" /></span>
										</c:if>											
									  </c:if>
									 
									<span>
										<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
											<dsp:param name="value" param="currentBillingAddress.address2"/>
											<dsp:oparam name="true">
												<dsp:valueof param="currentBillingAddress.address1"  />
											</dsp:oparam>
											<dsp:oparam name="false">
												<dsp:valueof param="currentBillingAddress.address1"  />, <dsp:valueof param="currentBillingAddress.address2" />
											</dsp:oparam>
										</dsp:droplet>
									</span>
									<span>
										<dsp:valueof	param="currentBillingAddress.city" />, <dsp:valueof param="currentBillingAddress.state" />
								 		<dsp:valueof param="currentBillingAddress.postalCode" />  

						            <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
                              	<dsp:param name="value" param="currentBillingAddress.country" />
                              	<dsp:oparam name="false">
                              		<c:choose>
                              			<c:when test="${internationalCCFlagBill}">
                              				<dsp:valueof param="currentBillingAddress.country" />
                              			</c:when>
                              			<c:otherwise>
                              				<span class="countryName hidden"><dsp:valueof param="currentBillingAddress.country" /></span>
                              			</c:otherwise>
                              		</c:choose>
                              		<span class="country hidden"><dsp:valueof param="currentBillingAddress.country" /></span>
                              	</dsp:oparam>
                              	<dsp:oparam name="true">
                              		<span class="country hidden">${defaultCountry}</span>
                              	</dsp:oparam>
                              </dsp:droplet>
                           </span>
							</label>

							<div id="editBillingAddressLinks">
							   <a id="editBillingAddress" href="#"><bbbl:label key="lbl_spc_edit_billing_address" language="${pageContext.request.locale.language}" /></a> |
							   <a id="addNewBillingAddress" href="#"><bbbl:label key="lbl_spc_add_new_billing_address" language="${pageContext.request.locale.language}" /></a>
							</div>
						</c:otherwise>
				</c:choose>

				

			</div>


			



		</div>
	</c:if>
				
</dsp:page>
