<dsp:page>
	<bbb:pageContainer section="checkout" index="false" follow="false">
		<dsp:importbean bean="/com/bbb/commerce/checkout/formhandler/BBBBillingAddressFormHandler" />
		<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/BBBBillingAddressDroplet"/>
		<dsp:importbean bean="/atg/commerce/ShoppingCart" />
		<dsp:importbean bean="/atg/userprofiling/Profile" />
		<dsp:importbean bean="/com/bbb/commerce/common/BBBAddressContainer" />
		<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
		<%-- <dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler" /> --%>
		<%-- <dsp:importbean bean="atg/userprofiling/Profile" /> --%>
		<%-- <dsp:importbean bean="/atg/dynamo/droplet/Compare" /> --%>

		<div id="pageWrapper" class="${pageWrapper}">
			<div id="header" class="container_12 clearfix">
				<div class="marLeft_10 fl">
					<h1 class="noMar">
						<a href="#" title="Bed Bath & Beyond"><img src="${imagePath}/_assets/global/images/logo/logo_bbb2.png" width="146" height="42" alt="Bed Bath & Beyond" /> </a>
					</h1>
				</div>
				<div class="marRight_10 fr">
					<a class="returnToCart fl" href="#">Return to Cart</a>
					<div id="steps" class="fl">
						<a href="#" class="first"><span>1</span>Shipping</a>
						<a href="#" class="active"><span>2</span>Billing</a>
						<a href="#"><span>3</span>Payment</a>
						<a href="#"><span>4</span>Preview</a>
					</div>
				</div>
				<div id="subHeader" class="grid_12 clearfix">
					<h2 class="section">
						Billing:&nbsp;<span class="subSection">Address</span>
					</h2>
				</div>
				<%-- chatnow link here ... missing ... needs to be integrated by java devs --%>
			</div>
			<div id="content" class="container_12 clearfix">
				<div id="leftCol" class="grid_8 marTop_25">
					<div class="highlightSection">
						<p>Your billing address must match the address on your credit card statement or your credit card may be declined.</p>
					</div>
					<dsp:form id="addBillingAddress" name="form" method="post">
						<fieldset class="radioGroup">
							<legend class="hidden">Select an address to ship order to:</legend>

							<dsp:droplet name="BBBBillingAddressDroplet">
								<dsp:param name="order" bean="ShoppingCart.current" />
								<dsp:param name="profile" bean="Profile" />
								<dsp:param name="billingAddrContainer" bean="BBBAddressContainer" />
								<dsp:setvalue bean="BBBBillingAddressFormHandler.userSelectedOption" paramvalue="selectedAddrKey" />
								<%-- <dsp:getvalueof param="selectedAddrKey" var="selectedAddr" /> --%>
								<dsp:oparam name="output">
									<dsp:droplet name="ForEach">
										<dsp:param name="array" param="addresses" />
										<dsp:oparam name="output">
											<div class="radioItem input clearfix">
												<div class="radio">
													<dsp:input type="radio" paramvalue="element.id" bean="BBBBillingAddressFormHandler.userSelectedOption">
														<dsp:tagAttribute name="aria-checked" value="false"/>
													</dsp:input>
													<%-- <input type="radio" value="2" name="addressToShip" id="addressToShip2" /> --%>
												</div>
												<div class="label">
													<span><dsp:valueof param="element.firstName" /> <dsp:valueof param="element.lastName"  /></span>
													<span><dsp:valueof param="element.address1"  /></span>
													<span><dsp:valueof param="element.address2"  /></span>
													<span><dsp:valueof param="element.city"  />, <dsp:valueof param="element.state" /> <dsp:valueof param="element.postalCode"  /></span>
												</div>
											</div>
										</dsp:oparam>
									</dsp:droplet>
								</dsp:oparam>
							</dsp:droplet>

							<div class="radioItem input clearfix">
								<div class="radio">
									<input type="radio" value="2" name="addressToShip" id="addressToShip2" aria-checked="false" aria-labelledby="lbladdressToShip2" />
								</div>
								<div class="label">
									<label id="lbladdressToShip2" for="addressToShip2">
										<span>Bob Smith</span>
										<span>123 Main St</span>
										<span>Brooklyn, NY 11213</span>
									</label>
								</div>
							</div>
							<div class="radioItem input clearfix">
								<div class="radio">
									<input type="radio" value="3" name="addressToShip" id="addressToShip3" aria-checked="false" aria-labelledby="lbladdressToShip3" />
								</div>
								<div class="label">
									<label id="lbladdressToShip3" for="addressToShip3">
										<span>Bob Smith</span>
										<span>567 Other Street Apt 401</span>
										<span>Brooklyn, NY 10019</span>
									</label>
								</div>
							</div>
							<div class="radioItem input clearfix last">
								<div class="radio">
									<dsp:input type="radio" bean="BBBBillingAddressFormHandler.userSelectedOption" value="NEW" >
										<dsp:tagAttribute name="aria-checked" value="false"/>
									</dsp:input>
								</div>
								<div class="label">
									<label for="addressToShip4">
										<span>Add new billing address</span>
									</label>
								</div>
							</div>
							<div class="clear"></div>
							<div class="subForm clearfix hidden">
								<div class="fieldsInlineWrapper clearfix">

									<div class="input">
										<div class="label">
											<label id="lblfirstName" for="firstName">First Name <span class="required">*</span></label>
										</div>
										<div class="text">
											<dsp:input type="text" bean="BBBBillingAddressFormHandler.billingAddress.firstName" required="true" id="firstName" >
												<dsp:tagAttribute name="aria-required" value="true"/>
												<dsp:tagAttribute name="aria-labelledby" value="lblfirstName errorfirstName"/>
											</dsp:input>
										</div>
									</div>



									<div class="input">
										<div class="label">
											<label id="lblmidleName" for="midleName">Middle Name</label>
										</div>
										<div class="text">
											<dsp:input type="text" bean="BBBBillingAddressFormHandler.billingAddress.middleName" id="midleName" >
												<dsp:tagAttribute name="aria-required" value="false"/>
												<dsp:tagAttribute name="aria-labelledby" value="lblmidleName errormidleName"/>
											</dsp:input>
										</div>
									</div>

								</div>


								<div class="input clearfix">
									<div class="label">
										<label id="lbllastName" for="lastName">Last Name <span class="required">*</span></label>
									</div>
									<div class="text">
										<dsp:input type="text" bean="BBBBillingAddressFormHandler.billingAddress.lastName" required="true" id="lastName" >
											<dsp:tagAttribute name="aria-required" value="true"/>
											<dsp:tagAttribute name="aria-labelledby" value="lbllastName errorlastName"/>
										</dsp:input>
									</div>
								</div>



								<div class="input clearfix">
									<div class="label">
										<label for="companyName">Company</label>
									</div>
									<div class="text">
										<div>
											<dsp:input type="text" bean="BBBBillingAddressFormHandler.billingAddress.companyName" >
												<dsp:tagAttribute name="aria-required" value="false"/>
											</dsp:input>
										</div>
									</div>
								</div>

								<div class="fieldsInlineWrapper clearfix">

									<div class="input">
										<div class="label">
											<label id="lblshippingAddress1" for="shippingAddress1">Address 1 <span class="required">*</span></label>
										</div>
										<div class="text">
											<dsp:input type="text" bean="BBBBillingAddressFormHandler.billingAddress.address1" required="true" id="shippingAddress1" >
												<dsp:tagAttribute name="aria-required" value="true"/>
												<dsp:tagAttribute name="aria-labelledby" value="lblshippingAddress1 errorshippingAddress1"/>
											</dsp:input>
										</div>
									</div>



									<div class="input">
										<div class="label">
											<label id="lblshippingAddress2" for="shippingAddress2">Address 2</label>
										</div>
										<div class="text">
											<div class="text">
												<dsp:input type="text" bean="BBBBillingAddressFormHandler.billingAddress.address2" required="true" id="shippingAddress2" >
													<dsp:tagAttribute name="aria-required" value="false"/>
													<dsp:tagAttribute name="aria-labelledby" value="lblshippingAddress2 errorshippingAddress2"/>
												</dsp:input>
											</div>
										</div>
									</div>

								</div>

								<div class="fieldsInlineWrapper clearfix">

									<div class="input">
										<div class="label">
											<label for="cityName">City <span class="required">*</span></label>
										</div>
										<div class="text">
											<dsp:input type="text" bean="BBBBillingAddressFormHandler.billingAddress.city" required="true" >
												<dsp:tagAttribute name="aria-required" value="true"/>
											</dsp:input>
										</div>
									</div>



									<div class="input">
										<div class="label">
											<label id="lblstateName" for="stateName">State <span class="required">*</span></label>
										</div>
										<div class="select">
											<dsp:select bean="BBBBillingAddressFormHandler.billingAddress.state" name="stateName" id="stateName">
												<dsp:tagAttribute name="aria-required" value="true"/>
												<dsp:tagAttribute name="aria-labelledby" value="lblstateName errorstateName"/>
												<dsp:option value="VA">Virginia</dsp:option>
												<dsp:option value="NJ">New Jersey</dsp:option>
												<dsp:option value="NY">New York</dsp:option>
												<dsp:option value="TX">Texas</dsp:option>
											</dsp:select>
										</div>
									</div>

								</div>


								<div class="input clearfix">
									<div class="label">
										<label id="lblzipCode" for="zipCode">Zip <span class="required">*</span></label>
									</div>
									<div class="text">
										<dsp:input type="text" bean="BBBBillingAddressFormHandler.billingAddress.postalCode" name="zipCode" value="" id="zipCode" required="true" >
											<dsp:tagAttribute name="aria-required" value="true"/>
											<dsp:tagAttribute name="aria-labelledby" value="lblzipCode errorzipCode"/>
										</dsp:input>
									</div>
								</div>


								<div class="checkboxItem input clearfix">
									<div>
										<dsp:input type="checkbox" bean="BBBBillingAddressFormHandler.saveToAccount" name="saveToAccount" value="TRUE" >
											<dsp:tagAttribute name="aria-checked" value="true"/>
										</dsp:input>
									</div>
									<div class="label">
										<label for="saveToAccount">Save to account</label>
									</div>
								</div>
							</div>
						</fieldset>
						<fieldset>
							<h3 class="sectionHeading">Coupon info</h3>
							<legend class="hidden">Coupon info</legend>
							<div class="subForm">
								<div class="fieldsInlineWrapper clearfix">

									<div class="input">
										<div class="label">
											<label id="lblemail" for="email">Email <span class="required">*</span></label>
										</div>
										<div class="text">
											<dsp:input type="text" bean="BBBBillingAddressFormHandler.billingAddress.email" value="" id="email" required="true" >
												<dsp:tagAttribute name="aria-required" value="true"/>
												<dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
											</dsp:input>
										</div>
									</div>
								</div>



								<div class="input">
									<div class="label">
										<label id="lblemailConfirm" for="emailConfirm">Confirm Email <span class="required">*</span></label>
									</div>
									<div class="text">
										<dsp:input type="text" bean="BBBBillingAddressFormHandler.billingAddress.confirmedEmail" value="" id="emailConfirm" required="true" >
											<dsp:tagAttribute name="aria-required" value="true"/>
											<dsp:tagAttribute name="aria-labelledby" value="lblemailConfirm erroremailConfirm"/>
										</dsp:input>
									</div>
								</div>

							</div>
							<div class="grid_3 alpha clearfix">
								<div class="inputPhone clearfix">
									<label id="lblphoneNumber" for="phoneNumber">Phone <span class="required">*</span></label>
								</div>
								<div class="text">
									<dsp:input type="text" bean="BBBBillingAddressFormHandler.billingAddress.phoneNumber" value="" id="phoneNumber" required="true" >
										<dsp:tagAttribute name="aria-required" value="true"/>
										<dsp:tagAttribute name="aria-labelledby" value="lblphoneNumber errorphoneNumber"/>
									</dsp:input>
								</div>
							</div>
							<div class="clear"></div>
						</fieldset>
						<div class="button btnApply">
							<c:set var="lbl_button_next" scope="page">
								<bbbl:label key="lbl_billing_button_next" language="${pageContext.request.locale.language}" />
							</c:set>

							<dsp:input bean="BBBBillingAddressFormHandler.saveBillingAddress" type="submit" value="${lbl_button_next}" id="billingAddressNextBtn" >
								<dsp:tagAttribute name="aria-pressed" value="false"/>
								<dsp:tagAttribute name="aria-labelledby" value="billingAddressNextBtn"/>
								<dsp:tagAttribute name="role" value="button"/>
							</dsp:input>
						</div>
						<%-- <div class="button btnApply"><input type="button" value="Next" /></div> --%>
					</dsp:form>
				</div>
				<dsp:include page="/checkout/order_summary_frag.jsp" />
			</div>
		</div>
	</bbb:pageContainer>
</dsp:page>
