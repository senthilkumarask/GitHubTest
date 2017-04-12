<dsp:page>
	<dsp:importbean bean="/atg/commerce/ShoppingCart" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	
	<dsp:getvalueof param="isConfirmation" var="isConfirmation" />
	<dsp:getvalueof var="paypal" param="paypal" />
	<dsp:getvalueof var="isExpress" bean="ShoppingCart.current.expressCheckOut"/>
	<div class="row <c:if test='${not isConfirmation and not paypal and not isExpress}'>hidden</c:if>" id="billReview">

		<c:if test='${not isConfirmation}'>
			<div class="small-12 large-offset-10 large-2 columns move-up-tiny">
				<c:set var="editText"><bbbl:label key='lbl_preview_edit' language="<c:out param='${language}'/>"/></c:set>
				<a class="tiny button secondary right expand edit-step" data-step="billing" title="<c:out value='${editText}'/>">
					<c:out value='${editText}'/>
				</a>
			</div>
		</c:if>

		<%-- billing info --%>
		<div class="small-12 columns">
			<dsp:getvalueof var="cartEmpty" param="ShoppingCart.empty"/>
			<c:if test="${not cartEmpty}">
				<div class="row">
					<div class="small-12 large-4 columns">
						<h3 class="checkout-title">
							<bbbl:label key="lbl_preview_billingaddress" language="<c:out param='${language}'/>"/>
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
					<div class="small-12 large-4 columns">
						<h3 class="checkout-title">
							Contact Information
						</h3>
						<ul class="address">
							<li><dsp:valueof param="order.billingAddress.email" valueishtml="false"/></li>
                            <dsp:getvalueof param="order.billingAddress.mobileNumber" var="phone"/>
							<li class="phone"><c:out value="(${fn:substring(phone, 0, 3)}) ${fn:substring(phone, 3, 6)}-${fn:substring(phone, 6, fn:length(phone))}"/></li>
						</ul>
					</div>
					<div class="small-12 large-4 columns"></div>
				</div>
			</c:if>
		</div>

	</div>

</dsp:page>
