<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<dsp:page>
	<dsp:importbean bean="/com/bbb/integration/csr/CSRFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/com/bbb/integration/csr/RegistryInfoBean" />


	<html>
<head>
<title>Customer Service Representative - Registry</title>
<link rel="stylesheet" href="/qas/qas.css" />
</head>
<body>
	<h2>Customer Service Representative - Registry</h2>

	<dsp:droplet name="ErrorMessageForEach">
		<dsp:param param="formhandler.formExceptions" name="exceptions" />
		<dsp:oparam name="outputStart">
			<ul class="error">
		</dsp:oparam>
		<dsp:oparam name="output">
			<li class="error"><dsp:getvalueof param="message" var="err_msg_key" /> ${err_msg_key}
			</li>
		<dsp:oparam name="outputEnd">
		</dsp:oparam>
			</ul>
		</dsp:oparam>
	</dsp:droplet>

	<dsp:form id="personalInfo" iclass="clearfix"
		action="csr_registry_form.jsp" method="post">
		<table border="noborder" style="width: 500px;">
			<tr>
				<td><strong>CSR Name</strong><span class="required">*</span>
				</td>
				<td><dsp:input type="text" bean="CSRFormHandler.csrName"
						maxlength="40" name="csrName" id="csrName" /></td>
			</tr>
			<tr>
				<td><strong>CSR Password</strong><span class="required">*</span>
				</td>
				<td><dsp:input type="password" bean="CSRFormHandler.password"
						maxlength="40" name="password" id="password" /></td>
			</tr>
			<tr>
				<td><strong>Registry Id</strong><span class="required">*</span>
				</td>
				<td><dsp:input type="text" bean="CSRFormHandler.registryId"
						maxlength="40" name="registryId" id="registryId" /></td>
			</tr>
			
			<tr>
				<td></td>
				<td></td>
			</tr>
			<tr>
				<td colspan="2"><dsp:input
						bean="CSRFormHandler.viewRegistryDetails" id="personalInfoBtn"
						type="Submit" value="View Registry Details" />
				</td>
			</tr>
		</table>

	<dsp:getvalueof param="showData" id="showData" />
	<dsp:getvalueof param="failure" id="failure" />
	<dsp:getvalueof param="updated" id="updated" />
	
	<c:if test="${not empty failure}">
		Please fix the issue mentioned above !!
	</c:if>
	<c:if test="${not empty updated}">
		Update done successfully. Please click on the View Registry Details button to verify the updates !
	</c:if>	
	<c:if test="${not empty showData}">
		<h2>Registry System Information</h2>
		<div>
			<table border="noborder" style="width: 700px;">
				<thead>
					<tr>
						<th>Registry ID</th>
						<th>Event Date</th>
						<th>Event Type</th>
						<th>Owner Email ID</th>
						<th>Owner First Name</th>
						<th>Owner Last Name</th>
						<th>Owner Profile ID</th>
						<th>Co-owner Email ID</th>
						<th>Co-owner First Name</th>
						<th>Co-owner Last Name</th>
						<th>Co-owner Profile ID</th>						
					</tr>
				</thead>
				<tr>
					<td><dsp:valueof bean="RegistryInfoBean.registryId"/></td>
					<td><dsp:valueof bean="RegistryInfoBean.eventDate"/></td>
					<td><dsp:valueof bean="RegistryInfoBean.eventType"/></td>
					<td><dsp:valueof bean="RegistryInfoBean.ownerEmailAddress"/></td>
					<td><dsp:valueof bean="RegistryInfoBean.ownerFirstName"/></td>
					<td><dsp:valueof bean="RegistryInfoBean.ownerLastName"/></td>
					<td><dsp:valueof bean="RegistryInfoBean.ownerProfileId"/></td>
					<td><dsp:valueof bean="RegistryInfoBean.coownerEmailAddress"/></td>
					<td><dsp:valueof bean="RegistryInfoBean.coOwnerFirstName"/></td>
					<td><dsp:valueof bean="RegistryInfoBean.coOwnerLastName"/></td>
					<td><dsp:valueof bean="RegistryInfoBean.coownerProfileId"/></td>

				</tr>
			</table>
		</div>
		<h2>Account Information</h2>
		<div>
			<table border="noborder" style="width: 700px;">
				<thead>
					<tr>
						<th>Registry ID</th>
						<th>Event Date</th>
						<th>Event Type</th>
						<th>Owner Email-ID</th>
						<th>Owner Profile ID</th>
						<th>Owner First Name</th>
						<th>Owner Last Name</th>
						<th>Co-owner Email-ID</th>
						<th>Co-owner First Name</th>
						<th>Co-owner Last Name</th>
						<th>Co-owner Profile ID</th>
						<th>Registry belongs to input email?</th>
						<th>Owner Registration Date</th>
						<th>Co-owner Registration Date</th>
					</tr>
				</thead>
				<tr>
					<td><dsp:valueof bean="RegistryInfoBean.registryIDATG"/></td>
					<td><dsp:valueof bean="RegistryInfoBean.eventDateATG"/></td>
					<td><dsp:valueof bean="RegistryInfoBean.eventTypeATG"/></td>
					<td><dsp:valueof bean="RegistryInfoBean.ownerEmailAddressATG"/></td>
					<td><dsp:valueof bean="RegistryInfoBean.ownerFirstNameATG"/></td>
					<td><dsp:valueof bean="RegistryInfoBean.ownerLastNameATG"/></td>
					<td><dsp:valueof bean="RegistryInfoBean.ownerProfileIDATG"/></td>
					<td><dsp:valueof bean="RegistryInfoBean.coownerEmailAddressATG"/></td>
					<td><dsp:valueof bean="RegistryInfoBean.coOwnerFirstNameATG"/></td>
					<td><dsp:valueof bean="RegistryInfoBean.coOwnerLastNameATG"/></td>
					<td><dsp:valueof bean="RegistryInfoBean.coownerProfileIDATG"/></td>
					<td><dsp:valueof bean="RegistryInfoBean.ownerAndInputEmailSame"/></td>
					<td><dsp:valueof bean="RegistryInfoBean.ownerRegistrationDateATG"/></td>
					<td><dsp:valueof bean="RegistryInfoBean.coownerRegistrationDateATG"/></td>
				</tr>
			</table>
		</div>	
	</c:if>

<BR/>
<h2>Update Registry Email</h2>
<BR/>
<strong>Email Id*</strong> <span class="required"></span>
<dsp:input type="text" bean="CSRFormHandler.emailAddress" maxlength="100" name="emailAddress" id="emailAddress" />
<BR/>
<BR/>
<strong>Select an option to update*(Default option is Registrant)</strong> <span class="required"></span>
<BR/>
					<dsp:getvalueof id="check" bean="CSRFormHandler.regCoreg"/>
					 <dsp:input id="registrant" name="regCoreg" type="radio" bean="CSRFormHandler.regCoreg" value="Registrant" checked="Registrant" default="true"/>Registrant
<BR/>
					 <dsp:input id="coregistrant" name="regCoreg" type="radio" bean="CSRFormHandler.regCoreg" value="CoRegistrant" checked="Registrant"/>CoRegistrant

<BR/>
<BR/>
<dsp:input bean="CSRFormHandler.updateRegistrant" id="personalInfoBtn"	type="Submit" value="Update Registry" /> 	
</dsp:form>
	
</body>
	</html>
</dsp:page>
