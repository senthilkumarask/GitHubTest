
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>	
	<jsp:useBean id="placeHolder" class="java.util.HashMap" scope="request"/>
	<c:set target="${placeHolder}" property="firstName"><dsp:valueof bean="Profile.firstName"/></c:set>
	<c:set target="${placeHolder}" property="password"><dsp:valueof param="newpassword"/></c:set>
	
	<bbbt:textArea key="txt_new_password_password" language="${pageContext.request.locale.language}" placeHolderMap="${placeHolder}"/>
	
</dsp:page>