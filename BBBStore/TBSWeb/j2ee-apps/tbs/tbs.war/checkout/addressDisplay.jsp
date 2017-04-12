<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/com/bbb/commerce/common/BBBCountriesDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/TBSGetStoreAddressDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>

	<%-- Variables --%>
	<dsp:getvalueof param="selectedAddress" var="selectedAddress"/>
	<dsp:getvalueof param="registriesAddresses" var="registriesAddresses"/>
	<dsp:getvalueof var="cmo" param="cmo"/>
	<c:if test="${cmo}">
		<dsp:droplet name="TBSGetStoreAddressDroplet">
		<dsp:oparam name="output">
			<dsp:getvalueof  param="storeDetails" var="storeDetails"/>
		</dsp:oparam>
		</dsp:droplet>
	</c:if>
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="profileAddresses" />
		<dsp:param name="elementName" value="address" />
		<dsp:param name="sortProperties" value="id"/>
		<dsp:oparam name="output">

			<c:set var="containsaddressForUser" scope="request" value="true"/>
			<dsp:getvalueof param="address.identifier" var="currentAddress"/>
			<dsp:getvalueof param="index" var="index"/>

			<label class="inline-rc radio gray-panel shipping-address" id="lbladdressToShip2p${index}" for="addressToShip2p${index}">
				<c:choose>
					<c:when test="${(fn:indexOf(selectedAddress, currentAddress) > -2) && isChecked eq 'true'}">
						<dsp:input type="radio" name="addressToShip" id="addressToShip2p${index}" value="${currentAddress}" checked="false" bean="BBBShippingGroupFormhandler.shipToAddressName">
							<dsp:tagAttribute name="aria-checked" value="false"/>
							<dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip2p${index} erroraddressToShip"/>
						</dsp:input>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${empty firstVisit && not empty registriesAddresses}">
								<dsp:input type="radio" name="addressToShip" id="addressToShip2p${index}" value="${currentAddress}" checked="false" bean="BBBShippingGroupFormhandler.shipToAddressName">
									<dsp:tagAttribute name="aria-checked" value="true"/>
									<dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip2p${index} erroraddressToShip"/>
								</dsp:input>
							</c:when>
							<c:when test="${selectedAddress eq currentAddress}">
								<c:set var="isChecked" value="true" scope="request" />
								<dsp:input type="hidden" name="editShipAddressID" value="${currentAddress}" bean="BBBShippingGroupFormhandler.editShipAddressID"/>
								<dsp:input type="radio" name="addressToShip" id="addressToShip2p${index}" value="${currentAddress}" checked="true" bean="BBBShippingGroupFormhandler.shipToAddressName">
									<dsp:tagAttribute name="aria-checked" value="true"/>
									<dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip2p${index} erroraddressToShip"/>
								</dsp:input>
							</c:when>
							<c:otherwise>
								<dsp:input type="radio" name="addressToShip" id="addressToShip2p${index}" value="${currentAddress}" checked="false" bean="BBBShippingGroupFormhandler.shipToAddressName">
									<dsp:tagAttribute name="aria-checked" value="false"/>
									<dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip2p${index} erroraddressToShip"/>
								</dsp:input>
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
				<span></span>
				<c:choose>
				<c:when test="${cmo}">
					<ul class="address">
						<li><c:out value="${storeDetails.storeName}"/>&nbsp;#<c:out value="${storeDetails.storeId}"/>
						<li><c:out value="${storeDetails.address}"/>
						<li><c:out value="${storeDetails.city}"/>, &nbsp;<c:out value="${storeDetails.state}"/>&nbsp;<c:out value="${storeDetails.postalCode}"/>
					</ul>
				</c:when>
				<c:otherwise>
					<ul class="address">
					<li><dsp:valueof param="address.firstName" /> <dsp:valueof param="address.lastName" /></li>
					<dsp:getvalueof var="tempCompanyName" param="address.companyName" />
					<c:if test="${tempCompanyName != ''}">
						<li><dsp:valueof param="address.companyName" /></li>
					</c:if>
					<li>
						<dsp:droplet name="IsEmpty">
							<dsp:param name="value" param="address.address2"/>
							<dsp:oparam name="true">
								<dsp:valueof param="address.address1" />
							</dsp:oparam>
							<dsp:oparam name="false">
								<dsp:valueof param="address.address1" />, <dsp:valueof param="address.address2" />
							</dsp:oparam>
						</dsp:droplet>
					</li>
					<li><dsp:valueof param="address.city" />, <dsp:valueof param="address.state"/> <dsp:valueof param="address.postalCode"/></li>
					<%-- <li>
						<dsp:droplet name="BBBCountriesDroplet">
							<dsp:param name="countryCode" param="address.country"/>
							<dsp:oparam name="output">
								<dsp:droplet name="ForEach">
									<dsp:param name="array" param="country" />
									<dsp:param name="elementName" value="contname" />
									<dsp:oparam name="output">
										<dsp:getvalueof param="key" var="countryCode"/>
										<dsp:getvalueof param="address.country" var="selectedCountry"/>
										<c:if test="${selectedCountry eq countryCode}">
											<dsp:valueof param="contname"/>
										</c:if>
									</dsp:oparam>
								</dsp:droplet>
							</dsp:oparam>
						</dsp:droplet>
					</li> --%>
				</ul>
				</c:otherwise>
				</c:choose>
			</label>
		</dsp:oparam>
	</dsp:droplet>

	<%-- if the address was new and was not saved in profile --%>
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="groupAddresses" />
		<dsp:param name="elementName" value="address" />
		<dsp:oparam name="output">
			<c:set var="containsaddressForUser" scope="request" value="true"/>
			<dsp:getvalueof param="address.identifier" var="currentAddress"/>
			<dsp:getvalueof param="selectedAddress" var="selectedAddress"/>
			<c:set var="containsaddressForGuest" scope="request" value="true"/>
			<c:if test="${fn:contains(currentAddress, 'PROFILE') && !fn:contains(selectedAddress, 'PROFILE')}">
				<c:set var="currentAddress"><c:out value="${fn:replace(currentAddress,'PROFILE', '')}"/></c:set>
			</c:if>
			<c:choose>
				<c:when test="${currentAddress == selectedAddress}">
					<label class="inline-rc radio gray-panel shipping-address" id="lbladdressToShip2s${index}" for="addressToShip2s${index}">
						<c:choose>
							<c:when test="${isChecked eq 'true'}">
								<dsp:input type="radio" name="addressToShip" id="addressToShip2s${index}" value="${currentAddress}" checked="false" bean="BBBShippingGroupFormhandler.shipToAddressName">
									<dsp:tagAttribute name="aria-checked" value="false"/>
									<dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip2s${index} erroraddressToShip"/>
								</dsp:input>
							</c:when>
							<c:otherwise>
								<c:set var="isChecked" value="true" scope="request" />
								<dsp:input type="radio" name="addressToShip" id="addressToShip2s${index}" value="${currentAddress}" checked="true" bean="BBBShippingGroupFormhandler.shipToAddressName">
									<dsp:tagAttribute name="aria-checked" value="true"/>
									<dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip2s${index} erroraddressToShip"/>
								</dsp:input>
							</c:otherwise>
						</c:choose>
						<span></span>
						<ul class="address">
							<c:choose>
								<c:when test="${cmo}">
										<li><c:out value="${storeDetails.storeName}"/>&nbsp;#<c:out value="${storeDetails.storeId}"/>
										<li><c:out value="${storeDetails.address}"/>
										<li><c:out value="${storeDetails.city}"/>, &nbsp;<c:out value="${storeDetails.state}"/>&nbsp;<c:out value="${storeDetails.postalCode}"/>
									</ul>
								</c:when>
								<c:otherwise>
									<li><dsp:valueof param="address.firstName" /> <dsp:valueof param="address.lastName" /></li>
									<dsp:getvalueof var="tempCompanyName" param="address.companyName" />
									<c:if test="${tempCompanyName != ''}">
										<li><dsp:valueof param="address.companyName" /></li>
									</c:if>
									<li><dsp:valueof param="address.address1" /> <dsp:valueof param="address.address2" /></li>
									<li><dsp:valueof param="address.city" />, <dsp:valueof param="address.state"/> <dsp:valueof param="address.postalCode" /></li>
									
									<input type="hidden" name="selectedShipAddress" data-firstName="<dsp:valueof param="address.firstName"/>"  
										data-lastName="<dsp:valueof param="address.lastName"/>"
										data-address1="<dsp:valueof param="address.address1"/>"
										data-address2="<dsp:valueof param="address.address2"/>"
										data-companyName="<dsp:valueof param="address.companyName"/>"
										data-city="<dsp:valueof param="address.city"/>"
										data-state="<dsp:valueof param="address.state"/>"
										data-postalCode="<dsp:valueof param="address.postalCode"/>"	/>
									
									<%-- <li>
									<c:set var="country"> <dsp:valueof param="address.country"/></c:set>
									<c:choose>
									<c:when test="${country eq 'Canada'}">
										<c:set var="countryCodes"> <dsp:valueof value="CA"/></c:set>
									</c:when>
									<c:otherwise>
										<c:set var="countryCodes"> <dsp:valueof param="address.country"/></c:set>
									</c:otherwise>
									</c:choose>
										<dsp:droplet name="BBBCountriesDroplet">
										<dsp:param name="countryCode" value="${countryCodes}"/>
											<dsp:oparam name="output">
												<dsp:droplet name="ForEach">
													<dsp:param name="array" param="country" />
													<dsp:param name="elementName" value="contname" />
													<dsp:oparam name="output">
														<dsp:getvalueof param="key" var="countryCode"/>
														<dsp:getvalueof param="address.country" var="selectedCountry"/>
														<c:if test="${countryCodes eq countryCode}">
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
					</label>
				</c:when>
			</c:choose>
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>
