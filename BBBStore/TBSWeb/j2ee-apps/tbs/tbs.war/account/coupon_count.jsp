<dsp:page>
	
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/com/bbb/account/GetCouponsDroplet" />
		
<%--jsp:body--%>
	<dsp:droplet name="GetCouponsDroplet">
	<dsp:param name="EMAIL_ADDR" bean="Profile.email"/>
	<dsp:param name="MOBILE_NUMBER" bean="Profile.mobileNumber"/>
	<dsp:oparam name="output">
		<span class="count"><dsp:valueof param="couponCount"/> </span>
		<span class="msg"><bbbl:label key="lbl_overview_coupons" language ="${pageContext.request.locale.language}"/></span>
	</dsp:oparam>
	<dsp:oparam name="error">
	<p class="p-secondary"><bbbl:error key="err_mycoupons_system_error" language ="${pageContext.request.locale.language}"/></p>
	</dsp:oparam>
	</dsp:droplet>
<%--/jsp:body--%>	
</dsp:page>