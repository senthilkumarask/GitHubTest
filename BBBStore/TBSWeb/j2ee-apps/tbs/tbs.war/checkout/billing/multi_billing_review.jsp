<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	
	<%-- billing info --%>
	<dsp:getvalueof var="cartEmpty" param="ShoppingCart.empty"/>
	<dsp:getvalueof param="isConfirmation" var="isConfirmation" />
	<dsp:getvalueof param="isMultiShip" var="isMultiShip" />
	<c:if test="${not cartEmpty}">
		<div class="row" id="billReview">
			<div class="small-12 large-6 columns">
				<h3 class="checkout-title">
					<bbbl:label key="lbl_preview_billingaddress" language="<c:out param='${language}'/>"/>
					<c:if test="${not isConfirmation && isMultiShip}">
						<a class="edit-step tiny secondary button" href="#" data-step="billing">Edit</a>
					</c:if>		
				</h3>
				<ul class="address">
					<c:if test="${not isPayPal}">
						<dsp:getvalueof var="billingAddress" param="order.billingAddress"/>
						<li><dsp:valueof param="order.billingAddress.firstName" valueishtml="false"/> <dsp:valueof param="order.billingAddress.lastName" valueishtml="false"/></li>
						<c:if test="${order.billingAddress.companyName != ''}">
							<li><dsp:valueof param="order.billingAddress.companyName" valueishtml="false"/></li>
						</c:if>
						<li>
							<dsp:droplet name="IsEmpty">
								<dsp:param name="value" param="order.billingAddress.address2"/>
								<dsp:oparam name="true">
									<dsp:valueof param="order.billingAddress.address1" valueishtml="false"/>
								</dsp:oparam>
								<dsp:oparam name="false">
									<dsp:valueof param="order.billingAddress.address1" valueishtml="false"/>, <dsp:valueof param="order.billingAddress.address2" valueishtml="false"/>
								</dsp:oparam>
							</dsp:droplet>
						</li>
						<li>
							<dsp:valueof param="order.billingAddress.city" valueishtml="false"/>, <dsp:valueof param="order.billingAddress.state" /> <dsp:valueof param="order.billingAddress.postalCode" valueishtml="false"/>
						</li>
						<dsp:droplet name="IsEmpty">
							<dsp:param name="value" param="order.billingAddress.countryName"/>
							<dsp:oparam name="false">
								<c:choose>
									<c:when test="${internationalCCFlag}">
										<li class="countryName internationalCountry"><dsp:valueof param="order.billingAddress.countryName" valueishtml="false"/></li>
									</c:when>
									<c:otherwise>
										<li class="countryName"><dsp:valueof param="order.billingAddress.countryName" valueishtml="false"/></li>
									</c:otherwise>
								</c:choose>
								<li class="country hidden"><dsp:valueof param="order.billingAddress.country" /></li>
							</dsp:oparam>
							<dsp:oparam name="true">
										<c:choose>
											<c:when test="${internationalCCFlag}">
												<li class="countryName internationalCountry"><dsp:valueof param="order.billingAddress.country" valueishtml="false"/></li>
											</c:when>
											<c:otherwise>
												<li class="countryName"><dsp:valueof param="order.billingAddress.country" valueishtml="false"/></li>
											</c:otherwise>
										</c:choose>
										<li class="country hidden"><dsp:valueof param="order.billingAddress.country" /></li>
							</dsp:oparam>
						</dsp:droplet>
					</c:if>
				</ul>
			</div>
			<div class="small-12 large-6 columns">
				<h3 class="checkout-title">
					Contact Information
					<c:if test="${not isConfirmation && isMultiShip}">
						<a class="edit-step tiny secondary button" href="#" data-step="billing">Edit</a>
					</c:if>	
				</h3>
				<ul class="address">
					<li><dsp:valueof param="order.billingAddress.email" valueishtml="false"/></li>
					<li><dsp:valueof param="order.billingAddress.mobileNumber" valueishtml="false"/></li>
				</ul>
			</div>
		</div>
	</c:if>

</dsp:page>
