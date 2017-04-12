<dsp:page>

	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/com/bbb/commerce/common/BBBCountriesDroplet"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
	
	<%-- Variables --%>
	<dsp:getvalueof param="isConfirmation" var="isConfirmation" />
	<dsp:getvalueof param="isMultiShip" var="isMultiShip" />
	<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
	<dsp:getvalueof var="beddingKit" param="beddingKit" />
	<dsp:getvalueof var="collegeName" param="collegeName"/>
	<dsp:getvalueof var="weblinkOrder" param="weblinkOrder"/>

	<c:set var="amps" value="&amp;"/>
	<h3 class="checkout-title">
		<bbbl:label key="lbl_preview_shippingaddress" language="<c:out param='${language}'/>"/> 
		<c:if test="${not isConfirmation && isMultiShip}">
			<a class="edit-step tiny secondary button" href="#" data-step="shipping">Edit</a>
		</c:if>	
	</h3>
	<ul class="address">
		<c:choose>
			<c:when test="${not empty shippingGroup.registryId}">
				 <dsp:getvalueof param="order.registryMap.${shippingGroup.registryId}" var="registratantVO"/>
				<li>
					${registratantVO.primaryRegistrantFirstName} ${registratantVO.primaryRegistrantLastName}
                    <c:if test="${registratantVO.coRegistrantFirstName ne null}">
                        &amp; ${registratantVO.coRegistrantFirstName} ${registratantVO.coRegistrantLastName}
                    </c:if>
					<%-- <dsp:valueof param="shippingGroup.registryInfo" valueishtml="true"/> --%>
				</li>
			</c:when>
			<c:otherwise>
				<li>
					<dsp:valueof param="shippingGroup.shippingAddress.firstName" valueishtml="true"/> <dsp:valueof param="shippingGroup.shippingAddress.lastName" valueishtml="true"/>
				</li>
				<c:choose>
					<c:when test="${not empty beddingKit && beddingKit eq true}">
						<li><dsp:valueof param="collegeName" valueishtml="true"/></li>
					</c:when>
					<c:when test="${not empty weblinkOrder && weblinkOrder eq true}">
						<li><dsp:valueof param="collegeName" valueishtml="true"/></li>
					</c:when>
					<c:otherwise>
						<c:if test="${shippingGroup.shippingAddress.companyName != ''}">
							<li><dsp:valueof param="shippingGroup.shippingAddress.companyName" valueishtml="true"/></li>
						</c:if>
					</c:otherwise>
				</c:choose>
					<dsp:droplet name="IsEmpty">
						<dsp:param name="value" param="shippingGroup.shippingAddress.address2"/>
						<dsp:oparam name="true">
						<li>
							<dsp:valueof param="shippingGroup.shippingAddress.address1" valueishtml="true"/>
						</li>
						</dsp:oparam>
						<dsp:oparam name="false">
						<li>
							<dsp:valueof param="shippingGroup.shippingAddress.address1" valueishtml="true"/> 
						</li>	
						<li>
							<dsp:valueof param="shippingGroup.shippingAddress.address2" valueishtml="true"/>
						</li>
						</dsp:oparam>
					</dsp:droplet>
					
					<input type="hidden" name="selectedShipAddress" data-firstName="<dsp:valueof param="shippingGroup.shippingAddress.firstName"/>"  
						data-lastName="<dsp:valueof param="shippingGroup.shippingAddress.lastName"/>"
						data-address1="<dsp:valueof param="shippingGroup.shippingAddress.address1"/>"
						data-address2="<dsp:valueof param="shippingGroup.shippingAddress.address2"/>"
						data-companyName="<dsp:valueof param="shippingGroup.shippingAddress.companyName"/>"
						data-city="<dsp:valueof param="shippingGroup.shippingAddress.city"/>"
						data-state="<dsp:valueof param="shippingGroup.shippingAddress.state"/>"
						data-postalCode="<dsp:valueof param="shippingGroup.shippingAddress.postalCode"/>"	/>
				<li>
					<dsp:valueof param="shippingGroup.shippingAddress.city" valueishtml="true"/>, <dsp:valueof param="shippingGroup.shippingAddress.state" />
				</li>
				<li>
					<dsp:valueof param="shippingGroup.shippingAddress.postalCode" valueishtml="true"/>
				</li>
				<input type="hidden" name="selectedShipAddress" data-firstName="<dsp:valueof param="shippingGroup.shippingAddress.firstName"/>"  
						data-lastName="<dsp:valueof param="shippingGroup.shippingAddress.lastName"/>"
						data-address1="<dsp:valueof param="shippingGroup.shippingAddress.address1"/>"
						data-address2="<dsp:valueof param="shippingGroup.shippingAddress.address2"/>"
						data-companyName="<dsp:valueof param="shippingGroup.shippingAddress.companyName"/>"
						data-city="<dsp:valueof param="shippingGroup.shippingAddress.city"/>"
						data-state="<dsp:valueof param="shippingGroup.shippingAddress.state"/>"
						data-postalCode="<dsp:valueof param="shippingGroup.shippingAddress.postalCode"/>"	/>
						
					<dsp:droplet name="/atg/dynamo/droplet/ForEach">
			<dsp:param name="array" param="shippingGroup.commerceItemRelationships" />
			<dsp:param name="elementName" value="commerceItemRelationship" />
			<dsp:oparam name="output">
			<dsp:getvalueof var="ltlItem" param="commerceItemRelationship.commerceItem.ltlItem"/>
			
		
	      <c:if test="${ltlItem}">
	      <ul class="address">
	    <li> <dsp:valueof param="shippingGroup.shippingAddress.phoneNumber" valueishtml="true"/></li>
	    <li> <dsp:valueof param="shippingGroup.shippingAddress.alternatePhoneNumber" valueishtml="true"/></li>
		<li> <dsp:valueof param="shippingGroup.shippingAddress.email" valueishtml="true"/></li>
				</ul>
				 
				          
		</c:if>
		
			</dsp:oparam>
		</dsp:droplet>
				<%-- <li>
					<dsp:getvalueof param="shippingGroup.shippingAddress.countryName" var="selectedCountry"/>
					<c:if test="${empty selectedCountry}">
						<c:set var="selectedCountry"><dsp:valueof bean="Site.defaultCountry"/> </c:set>
					</c:if>
					<c:if test="${selectedCountry eq 'Canada'}">
						<c:set var="selectedCountry" value="CA"></c:set>
					</c:if>
					<dsp:droplet name="BBBCountriesDroplet">
						<dsp:param name="countryCode" value="${selectedCountry}"/>
						<dsp:oparam name="output">
							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="country" />
								<dsp:param name="elementName" value="contname" />
								<dsp:oparam name="output">
									<dsp:getvalueof param="key" var="countryCode"/>
									<c:if test="${selectedCountry eq countryCode}">
										<dsp:valueof param="contname"/>
									</c:if>
								</dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
					</dsp:droplet>
				</li> --%>
			</c:otherwise>
		</c:choose>
	</ul>

</dsp:page>
