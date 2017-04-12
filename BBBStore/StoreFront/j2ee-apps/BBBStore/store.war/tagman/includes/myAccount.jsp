<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	
	<dsp:getvalueof var="email" bean="Profile.email"/>
	<dsp:getvalueof var="firstName" bean="Profile.firstName"/>
	<dsp:getvalueof var="lastName" bean="Profile.lastName"/>
	<dsp:getvalueof var="address" bean="Profile.billingAddress"/>
	<dsp:getvalueof var="address1" bean="Profile.billingAddress.address1"/>
	<dsp:getvalueof var="city" bean="Profile.billingAddress.city"/>
	<dsp:getvalueof var="state" bean="Profile.billingAddress.state"/>
	<dsp:getvalueof var="postalCode" bean="Profile.billingAddress.postalCode"/>
	<dsp:getvalueof var="country" bean="Profile.billingAddress.country"/>
	<%-- Begin TagMan --%>
	<script type="text/javascript">	 
		// client configurable parameters 
		window.tmParam.action_type = 'login';
		window.tmParam.customer_name = '${firstName} ${lastName}';

		window.tmParam.customer_billaddr = '${address1}';
		window.tmParam.customer_city = '${city}';
		window.tmParam.customer_county = '${state}';
		window.tmParam.customer_postcode = '${postalCode}';
		window.tmParam.page_type = 'MyAccount';
	</script>
	<%-- End TagMan --%>	  
</dsp:page>