<%--
This page is used to render a stored address. The NickName of the address is not displayed here-in.
If required, it must be rendered just before a include to this JSP

This page expects the following input parameters
- address - A ContactInfo Repository Item to display
- private - If true, we will hide the details of the address.value
--%>

<dsp:page>

	<%-- Variables --%>
	<dsp:getvalueof var="addressValue" param="address.country"/>

	<c:choose>
		<c:when test="${addressValue == ''}"/>
		<c:otherwise>
			<ul class="address">
				<li><span class="firstName"><dsp:valueof param="address.firstName" valueishtml="false"/></span> <span class="lastName"><dsp:valueof param="address.lastName" valueishtml="false"/></span></li>
				<li><span class="companyName"><dsp:valueof param="address.companyName" valueishtml="false" /></span></li>
				<li><span class="street1"><dsp:valueof param="address.address1" valueishtml="false"/></span></li>
				<li><span class="street2"><dsp:valueof param="address.address2" valueishtml="false" /></span></li>
				<li><span class="city"><dsp:valueof param="address.city" valueishtml="false"/></span>, <span class="state"><dsp:valueof param="address.state" valueishtml="false"/></span> <span class="zip"><dsp:valueof param="address.postalCode" valueishtml="false"/></span></li>
			</ul>
		</c:otherwise>
	</c:choose>

</dsp:page>

