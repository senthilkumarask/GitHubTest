<%@ taglib prefix="dsp"
	uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<dsp:page>
	<dsp:importbean bean="/com/bbb/integration/csr/CSRFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/com/bbb/integration/csr/ProfileInfoBean" />
	<html>
<head>
<title>Customer Service Representative - Profile</title>
<link rel="stylesheet" href="/qas/qas.css" />
<style>
.error {
	color: red;
}

.mainContainer {
	width: 1000px;
	text-align: center;
}

.leftContainer {
	width: 500px;
}

.rightContainer {
	width: 500px;
	float: right;
}

table,td,th {
	border: 1px solid green;
	vertical-align: top;
}

th {
	background-color: green;
	color: white;
}

.centerAlign {
	text-align: center
}

.mainContainer {
	margin-left: auto;
	margin-right: auto;
	width: 1000px;
}

.contentContainer {
	float: left;
	width: 1000px;
}
</style>
</head>
<body>
	<div class="centerAlign">
		<h2>Customer Service Representative - Pofile</h2>
	</div>
	<div class="mainContainer">
		<dsp:droplet name="ErrorMessageForEach">
			<dsp:param param="formhandler.formExceptions" name="exceptions" />
			<dsp:oparam name="outputStart">
				<ul class="error">
			</dsp:oparam>
			<dsp:oparam name="output">
				<li class="error"><dsp:getvalueof param="message"
						var="err_msg_key" /> ${err_msg_key}</li>
			</dsp:oparam>
			<dsp:oparam name="outputEnd">
				</ul>
			</dsp:oparam>
		</dsp:droplet>
	</div>

	<div class="mainContainer">
		<table class="contentContainer">
			<tr>
				<td><dsp:form id="profileInfo" iclass="clearfix"
						action="csr_profile_form.jsp" method="post">
						<table class="leftContainer">

							<thead>
								<tr>
									<th colspan="2">View Profile Details</th>
								</tr>
							</thead>
							<tr>
								<td><strong>CSR Password</strong><span class="required">*</span>
								</td>
								<td><dsp:input type="password"
										bean="CSRFormHandler.password" maxlength="40" name="password"
										id="password" /></td>
							</tr>
							<tr>
								<td><strong>Email Id</strong><span class="required">*</span>
								</td>
								<td><dsp:input type="text" bean="CSRFormHandler.emailAddress"
										maxlength="40" name="emailAddress" id="emailAddress" /></td>
							</tr>
							<tr>
								<td></td>
								<td></td>
							</tr>
							<tr>
								<td colspan="2"><dsp:input
										bean="CSRFormHandler.viewProfileDetails" id="viewProfileDetails"
										type="Submit" value="View Profile Details" /></td>
							</tr>
						</table>
					</dsp:form></td>
					
					<td><dsp:form id="updateProfileInfo" iclass="clearfix"
						action="csr_profile_form.jsp" method="post">
						<table class="rightContainer">
							<thead>
								<tr>
									<th colspan="2">Unlock Account</th>

								</tr>
							</thead>
							<tr>
								<td><strong>CSR Password</strong><span class="required">*</span>
								</td>
								<td><dsp:input type="password"
										bean="CSRFormHandler.password" maxlength="40" name="password"
										id="password" /></td>
							</tr>
							<tr>
								<td><strong>Email Id</strong><span class="required">*</span>
								</td>
								<td><dsp:input type="text" bean="CSRFormHandler.emailAddress"
										maxlength="40" name="emailAddress" id="emailAddress" /></td>
							</tr>
							<tr>
								<td></td>
								<td></td>
							</tr>
							<tr>
								<td colspan="2"><dsp:input
										bean="CSRFormHandler.unlockAccount" id="unlockAccount"
										type="Submit" value="Unlock Account" /> 
								</td>
							</tr>
						</table>
					</dsp:form></td>
			</tr>
		</table>
	</div>

	<p></p>

	<div class="mainContainer">


		<dsp:getvalueof param="showData" id="showData" />
		<dsp:getvalueof param="failure" id="failure" />
		<dsp:getvalueof param="updated" id="updated" />
		<c:if test="${not empty failure}">
		Please fix above issue !!
	</c:if>
		<c:if test="${not empty updated}">
		Update done successfully !!
	</c:if>
		<c:if test="${not empty showData}">
			<h2>Store Details</h2>
			<div>
				<table border="noborder" style="width: 700px;">
					<thead>
						<tr>
							<th>Profile Id</th>
							<th>Member Id</th>
							<th>First Name</th>
							<th>Last Name</th>
							<th>Email Address</th>
							<th>Registration Date</th>
							<th>Account Locked</th>
						</tr>
						
					</thead>
					<tr>
							<td><dsp:valueof bean="ProfileInfoBean.profileId" /></td>
							<td><dsp:valueof bean="ProfileInfoBean.memberId" /></td>
							<td><dsp:valueof bean="ProfileInfoBean.firstName" /></td>
							<td><dsp:valueof bean="ProfileInfoBean.lastName" /></td>
							<td><dsp:valueof bean="ProfileInfoBean.emailAddress" /></td>
							<td><dsp:valueof bean="ProfileInfoBean.registrationDate" /></td>
							<td><dsp:valueof bean="ProfileInfoBean.accountLocked" /></td>
						</tr>

				</table>
			</div>
		</c:if>
	</div>

</body>
	</html>
</dsp:page>
