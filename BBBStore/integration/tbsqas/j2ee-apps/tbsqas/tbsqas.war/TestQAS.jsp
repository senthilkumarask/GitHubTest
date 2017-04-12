<%-- <%@ taglib uri="dsp" prefix="dsp" %> --%>
<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<dsp:page>
 <dsp:importbean bean="/test/TestQASFormHandler"/>
<html>
<head><title>Test QAS</title>
<!--  Need to update the field and button IDs in tbsqas.js -->
<script src="/tbsqas/jquery/js/jquery-1.4.2.min.js"></script>
<script src="/tbsqas/jquery/js/jquery-ui-1.8.6.custom.min.js"></script>
<script src="/tbsqas/tbsqas.js"></script>
<link rel="stylesheet" href="/tbsqas/tbsqas.css"/>
<link rel="stylesheet" href="/tbsqas/jquery/css/qas/jquery-ui-1.8.6.custom.css"/>
</head>
<body><h2>Test QAS Page</h2>
 <dsp:form action="${pageContext.request.requestURI}" method="post" formid="addressform">
<table border="noborder">
    <tr>
      <td>First Name<span class="required">*</span></td>
	  <td><dsp:input type="text" bean="TestQASFormHandler.address.firstName" maxlength="40" iclass="required" name="atg_store_firstNameInput" id="atg_store_firstNameInput" required="true" value=""/></td>
	</tr>
	<tr>
      <td>Last Name<span class="required">*</span></td>
	  <td><dsp:input type="text" bean="TestQASFormHandler.address.lastName" maxlength="40" iclass="required" name="atg_store_lastNameInput" id="atg_store_lastNameInput" required="true" value=""/></td>
	</tr>
	<tr>
      <td>Address 1<span class="required">*</span></td>
	  <td><dsp:input type="text" bean="TestQASFormHandler.address.address1" maxlength="40" iclass="required" name="atg_store_streetAddressInput" id="atg_store_streetAddressInput" required="true" value=""/></td>
	</tr>
	<tr>
      <td>Address 2</td>
	  <td><dsp:input type="text" bean="TestQASFormHandler.address.address2" maxlength="40" name="atg_store_streetAddressOptionalInput" id="atg_store_streetAddressOptionalInput" required="true" value=""/></td>
	</tr>
	<tr>
      <td>City</td>
	  <td><dsp:input type="text" bean="TestQASFormHandler.address.city" maxlength="40" iclass="required" name="atg_store_localityInput" id="atg_store_localityInput" required="true" value=""/></td>
	</tr>
	<tr>
      <td>State</td>
	  <td><dsp:select bean="TestQASFormHandler.address.state" iclass="custom_select" name="atg_store_stateSelect" id="atg_store_stateSelect">
	  		<dsp:option value="VA">Virginia</dsp:option>
	  		<dsp:option value="NJ">New Jersey</dsp:option>
	  		<dsp:option value="NY">New York</dsp:option>
	  		<dsp:option value="TX">Texas</dsp:option>
	  	  </dsp:select>
	  </td>
	</tr>
	<tr>
      <td>Zip</td>
	  <td><dsp:input type="text" bean="TestQASFormHandler.address.postalCode" maxlength="10" iclass="required" name="atg_store_postalCodeInput" id="atg_store_postalCodeInput" required="true" value=""/></td>
	</tr>
	<tr>
      <td>Country</td>
	  <td>USA <dsp:input type="hidden" bean="TestQASFormHandler.address.country" name="atg_store_Country" id="atg_store_Country" value="USA"/></td>
	</tr>
</table>
<dsp:input iclass="\"onClick=\"javaScript:QAS_Verify_Address(); return false;\"" 
bean="TestQASFormHandler.testValidateAddress" type="submit" id="testValidateAddress" value="testValidateAddress"/>
 </dsp:form>

 </body>
</html>
</dsp:page>
