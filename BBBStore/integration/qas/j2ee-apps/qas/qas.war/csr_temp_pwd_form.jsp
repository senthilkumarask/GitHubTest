<%@ taglib prefix="dsp"
	uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<dsp:page>

	<dsp:importbean bean="/com/bbb/integration/csr/CSRFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/com/bbb/integration/csr/ProfileInfoBean" />
	<dsp:importbean bean="com/bbb/integration/csr/CSRGetOrderListDroplet" />
	<dsp:importbean bean="/com/bbb/integration/csr/CSRCheckAccessDroplet" />
	
	<dsp:droplet name="CSRCheckAccessDroplet">
	<dsp:oparam name="output">
	<dsp:getvalueof var="isIPAllowed" param="isIPAllowed" />>
	</dsp:oparam>
	</dsp:droplet>
	<html>
<head>
<title>Customer Service Representative - Temporary Password</title>
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
<c:choose>
	<c:when test="${isIPAllowed eq false}">
<body>	
	<div class="centerAlign error">
		<h2>The page you are trying to access is either not available or you do not have privileges</h2>
	</div>
</body>
	</c:when>
	<c:otherwise>
<body>
	<div class="centerAlign">
		<h2>Customer Service Representative - Temporary Password</h2>
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
				<td>&nbsp;<dsp:form id="orderDetailInfo" iclass="clearfix"
						action="csr_temp_pwd_form.jsp" method="post">
						<table class="leftContainer">

							<thead>
								<tr>
									<th colspan="2">View Order Details</th>
								</tr>
							</thead>
							<tr>
								<td>&nbsp;<strong>CSR Name</strong><span class="required">*</span>
								</td>
								<td>&nbsp;<dsp:input type="text"
										bean="CSRFormHandler.csrName" maxlength="40" name="csrName"
										id="csrName" /></td>
							</tr>
							<tr>
								<td>&nbsp;<strong>CSR Password</strong><span class="required">*</span>
								</td>
								<td>&nbsp;<dsp:input type="password"
										bean="CSRFormHandler.password" maxlength="40" name="password"
										id="password" /></td>
							</tr>
							<tr>
								<td>&nbsp;<strong>Email Id</strong><span class="required">*</span>
								</td>
								<td>&nbsp;<dsp:input type="text" bean="CSRFormHandler.emailAddress"
										maxlength="40" name="emailAddress" id="emailAddress" /></td>
							</tr>
							<tr>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td colspan="2"><dsp:input
										bean="CSRFormHandler.viewOrderDetails" id="viewOrderDetails"
										type="Submit" value="View Details" /></td>
							</tr>
						</table>
						
					</dsp:form></td>
					
					<td>&nbsp;<dsp:form id="updateProfileInfo" iclass="clearfix"
						action="csr_temp_pwd_form.jsp" method="post" >
						<table class="rightContainer">
							<thead>
								<tr>
									<th colspan="2">Set Temporary Password</th>

								</tr>
							</thead>
							<tr>
								<td>&nbsp;<strong>Password</strong><span class="required">*</span>
								</td>
								<td>&nbsp;<dsp:input type="password"
										bean="CSRFormHandler.tempPassword" maxlength="40" name="tempPassword"
										id="tempPassword" /></td>
							</tr>
							<tr>
								<td>&nbsp;<strong>Email Id</strong><span class="required">*</span>
								</td>
								<td>&nbsp;<dsp:input type="text" bean="CSRFormHandler.emailAddress"
										maxlength="40" name="emailAddress" id="emailAddress" /></td>
							</tr>
							<tr>
								<td>&nbsp;</td>
								<td>&nbsp;</td>
							</tr>
							<tr>
								<td colspan="2"><dsp:input
										bean="CSRFormHandler.setTemporaryPassword" id="setTemporaryPassword"
										type="Submit" value="Reset Password" /> 
								</td>
							</tr>
						</table>
					</dsp:form></td>
			</tr>
		</table>
	</div>

	


	<div class="mainContainer">


		<dsp:getvalueof param="showData" id="showData" />
		<dsp:getvalueof param="email" id="email"/>
		<dsp:getvalueof param="fName" id="fName"/>
		<dsp:getvalueof param="lstName" id="lstName"/>
		<dsp:getvalueof param="failure" id="failure" />
		<dsp:getvalueof param="updated" id="updated" />
		<c:if test="${not empty failure}">
		Please fix above issue !!
	</c:if>
		<c:if test="${not empty updated}">
		Update done successfully !!
	</c:if>
		<c:if test="${not empty showData && not empty email }">
			<h2>Order Details</h2>
			<div>
				<table border="noborder" style="width: 700px;">
							
							<dsp:droplet name="CSRGetOrderListDroplet">
								<dsp:param name="email" value="${email}"/>
								<dsp:oparam name="orderOutputStart">
									<thead>
										<tr>
											<th>First Name</th>
											<th>Last Name</th>
											<th>Order Id's</th>
											<th>View Details</th>
										</tr>
						
									</thead>
								</dsp:oparam>
								<dsp:oparam name="orderOutput">
									<tbody>
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param name="array" param="OrderList" />
											<dsp:param name="elementName" value="Orders" />
											<dsp:oparam name="output">
											<dsp:getvalueof var="orderType" param="Orders.orderType" />
											<dsp:getvalueof	var="orderNum" param="Orders.orderNumber" />
											<dsp:getvalueof var="orderStatus" param="Orders.orderStatus" />
											<c:choose>
												<c:when test="${orderType ne 1}"><%-- 1 value is for ATG orders --%>	
												</c:when>
												<c:otherwise>
												<dsp:getvalueof var="onlineOrderNumber" param="Orders.onlineOrderNumber" />
												<dsp:getvalueof var="bopusOrderNumber" param="Orders.bopusOrderNumber" />
													<tr>	
														<td>&nbsp;${fName}</td>
														<td>&nbsp;${lstName}</td>												
														<td>&nbsp;
														<c:choose>
                       										<c:when test="${not empty onlineOrderNumber}">
                       											<dsp:valueof value="${onlineOrderNumber}"/>
                       										</c:when>
                       										<c:otherwise>
                       											<dsp:valueof value="${bopusOrderNumber}"/>
                       										</c:otherwise>
                       									</c:choose>														
														</td>
														
														<td>&nbsp;
															<c:set var="orderDetailsURL" value="csr_order_details.jsp?orderId=${orderNum}" />
															<dsp:a href="${orderDetailsURL}" title="View Details" target="_blank">View Details
															</dsp:a>
														</td>
													</tr>
													<c:if test="${not empty onlineOrderNumber && not empty bopusOrderNumber}">
														<tr>
														<td>&nbsp;</td>
														<td>&nbsp;</td>
														<td>&nbsp;<dsp:valueof value="${bopusOrderNumber}"/></td>
														<td colspan="4"></td>
														
														
														</tr>
													</c:if>
												</c:otherwise>
											</c:choose>
											</dsp:oparam>
										</dsp:droplet>
									</tbody>
								</dsp:oparam>
								<dsp:oparam name="orderEmpty">
										
											&nbsp; No Orders Found For This Profile !!
																			
								</dsp:oparam>
								<dsp:oparam name="error">
									&nbsp; Error In Retrieving Order History !!
								</dsp:oparam>
							</dsp:droplet>

				</table>
			</div>
		</c:if>
	</div>

</body>
</c:otherwise>
</c:choose>
</html>
	
</dsp:page>
