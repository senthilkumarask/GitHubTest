<%@ taglib prefix="dsp"
	uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<dsp:page>
	<dsp:importbean bean="/com/bbb/integration/csr/CSRFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/integration/csr/RegistryInfoBean" />
	<dsp:importbean bean="/com/bbb/integration/csr/StoreInfoBean" />
	<html>
<head>
<title>Customer Service Representative - Store</title>
<link rel="stylesheet" href="/tbsqas/tbsqas.css" />
<style>
.textarea textarea {
    border: none;
    min-height:80px;
    color: #594D43;
    font-family: Arial;
    font-size: 14px;
    width:100%;
    border: 2px solid #b2dcf3;
}
.error {
	color: red;
}

.success {
	color: green;
}

.mainContainer {
	width: 1000px;
	text-align: center;
}

.leftContainer {
	width: 400px;
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
		<h2>Customer Service Representative - Store</h2>
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
				<td><dsp:form id="storeInfo" iclass="clearfix"
						action="csr_store_form.jsp" method="post">
						<table class="leftContainer">

							<thead>
								<tr>
									<th colspan="2">View Store Details</th>

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
								<td><strong>Store Id</strong><span class="required">*</span>
								</td>
								<td><dsp:input type="text" bean="CSRFormHandler.storeId"
										maxlength="40" name="storeId" id="storeId" /></td>
							</tr>
							<tr>
								<td></td>
								<td></td>
							</tr>
							<tr>
								<td colspan="2"><dsp:input
										bean="CSRFormHandler.viewStoreDetails" id="viewStoreDetails"
										type="Submit" value="View Store Details" /></td>
										
								
							</tr>
							<tr>
								<td colspan="2"><dsp:input
										bean="CSRFormHandler.viewBopusDisalbedStores" id="viewDisabledStoreDetails"
										type="Submit" value="View All Disabled Stores" />
										
								<dsp:input
										bean="CSRFormHandler.viewBopusDisalbedStates" id="viewBopusDisalbedStates"
										type="Submit" value="View Disabled States" /></td>
							</tr>
						</table>
					</dsp:form></td>
				<td><dsp:form id="updateStoreInfo" iclass="clearfix"
						action="csr_store_form.jsp" method="post">
						<table class="rightContainer">
							<thead>
								<tr>
									<th colspan="2">Update Bopus flag for store</th>

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
								<td><strong>Store Id</strong><span class="required">*</span>
								</td>
								<td><dsp:input type="text" bean="CSRFormHandler.storeId"
										maxlength="40" name="storeId" id="storeId" /></td>
							</tr>
							<tr>
								<td><strong>Update Bopus Status</strong><span
									class="required">*</span></td>
								<td><%-- <dsp:input id="bopusFlagBaby" name="bopusFlag"
										type="radio" bean="CSRFormHandler.bopusFlag" value="Baby" />Update
									Bopus for Buy Buy Baby<br /> <dsp:input id="bopusFlagUS"
										name="bopusFlag" type="radio" bean="CSRFormHandler.bopusFlag"
										value="US" />Update Bopus for BedBath Beyond<br />  --%>
										
										<dsp:input
										id="bopusFlagBoth" name="bopusFlag" type="radio"
										bean="CSRFormHandler.bopusFlag" value="Both" checked="true" />Update Bopus
									for Both Sites</td>
							</tr>
							<tr>
								<td></td>
								<td></td>
							</tr>
							<tr>
								<td colspan="2"><dsp:input
										bean="CSRFormHandler.enableBopus" id="enableBopus"
										type="Submit" value="Enable Bopus" /> <dsp:input
										bean="CSRFormHandler.disableBopus" id="disableBopus"
										type="Submit" value="Disable Bopus" />
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
		<dsp:getvalueof param="showStatesData" id="showStatesData" />
		<dsp:getvalueof param="failure" id="failure" />
		<dsp:getvalueof param="updated" id="updated" />
		<dsp:getvalueof id="showDisabledStore" bean="CSRFormHandler.showDisabledStores" />
		
		<c:if test="${not empty failure}">
			<span class="error">Please fix above issue !!</span>
		</c:if>
		<c:if test="${not empty updated}">
			<span class="success">Update done successfully !!</span>
		</c:if>
		<c:if test="${not empty showData}">
		
			<dsp:form id="updateStoreStatus" iclass="clearfix"
							action="csr_store_form.jsp" method="post">
					<h2>Store Details</h2>
					<div>
						<table border="noborder" style="width: 700px;">
						<dsp:droplet name="ForEach">
								<dsp:param name="array" bean="CSRFormHandler.storeInfoBeanList" />
								<dsp:param name="elementName" value="StoreInfoBean" />
								<dsp:oparam name="outputStart">
								<thead>
								<c:if test="${showDisabledStore eq 'true'}">
									<tr>
										<td colspan="2"><strong>CSR Password</strong><span class="required">*</span></td>
										<td colspan="2"><dsp:input type="password"
											bean="CSRFormHandler.password" maxlength="40" name="password"
											id="password" /></td>
									</tr>
								</c:if>
									<tr>
										<c:if test="${showDisabledStore eq 'true'}">
											<th>Enable Store?</th>
										</c:if>
										<th>Store ID</th>
										<th>City</th>
										<th>State</th>
										<th>Zip Code</th>
										<th>Bopus Enabled</th>
									</tr>
								</thead>
									</dsp:oparam>
									<dsp:oparam name="output">
										<tr>
										 
											<c:if test="${showDisabledStore eq 'true'}">
												<td>									
										<dsp:getvalueof var="count" param="index"/>
												<c:set var="storeId">
													<dsp:valueof bean="StoreInfoBean.storeId" />
												</c:set>
											 	<dsp:input type="checkbox" name="selectedStoreIdsToEnable"  bean="CSRFormHandler.selectedStoreIdsToEnable" 
											 		beanvalue="CSRFormHandler.StoreInfoBeanList[${count}].storeId"/>
											 											 	
													</td>
											 </c:if>
											<td><dsp:valueof param="StoreInfoBean.storeId" /></td>
											<td><dsp:valueof param="StoreInfoBean.storeCity" /></td>
											<td><dsp:valueof param="StoreInfoBean.storeState" /></td>
											<td><dsp:valueof param="StoreInfoBean.storeZipCode" /></td>
											<td><dsp:valueof param="StoreInfoBean.storeBopusFlagBaby" /></td>
										</tr>
										
								
									</dsp:oparam>
									<dsp:oparam name="outputEnd">
												<c:if test="${showDisabledStore eq 'true'}">
											<tr>
											<td>
										<dsp:input
											bean="CSRFormHandler.updateStoreBopusStatus" id="enableBopus"
											type="Submit" value="Enable Bopus" />
											</td>
											</tr>
											</c:if>
									</dsp:oparam>
							</dsp:droplet>
						</table>
					</div>
				</dsp:form>
		</c:if>
		<c:if test="${not empty showStatesData}">
		
			<h2>States having bopus disabled</h2>
				<div>
					<table border="noborder" style="width: 700px;">
					
						<dsp:droplet name="ForEach">
								<dsp:param name="array" bean="CSRFormHandler.bopusDisabledStates" />
								<dsp:param name="elementName" value="State" />
								<dsp:oparam name="outputStart">
									<thead>
										<tr>
											<th>State code</th>
											<th>State desc</th>
										</tr>
									</thead>
								</dsp:oparam>
								<dsp:oparam name="output">
									<tr>
										<td><dsp:valueof param="State" /></td>
										<td><dsp:valueof param="StateDescription" /></td>
									</tr>
								</dsp:oparam>
						</dsp:droplet>
					</table>
				</div>
		</c:if>
	</div>
	<p/>
	<br/><br/>
	<div class="mainContainer" style="margin-top:50px;">
			<dsp:form id="updateStoreStatus" iclass="clearfix"
							action="csr_store_form.jsp" method="post">
					<h2>Deactivate Multiple Stores</h2>
					<div>
						<table border="noborder" style="width: 700px;">
							<tr>
								<td><strong>CSR Password</strong><span class="required">*</span>
								</td>
								<td><dsp:input type="password"
										bean="CSRFormHandler.password" maxlength="40" name="password"
										id="password" /></td>
							</tr>
							<tr>
								<td>	
									Enter max 100 stores ids separated by comma(,)
								</td>
								<td>
								<div class="textarea">
									<div class="width_8">
									<dsp:textarea name="disableBopusStoresText" id="disableBopusStoresText" bean="CSRFormHandler.storesList" maxlength="1500"></dsp:textarea>
									</div>
								</div>
								</td>
							</tr>
							<tr>
								<td colspan="2">
									<dsp:input
										bean="CSRFormHandler.disableBopusStores" id="disableBopusStores"
										type="Submit" value="Disable Bopus Stores" />
								</td>
							</tr>
						</table>
					</div>
			</dsp:form>
	</div>


</body>
	</html>
</dsp:page>
