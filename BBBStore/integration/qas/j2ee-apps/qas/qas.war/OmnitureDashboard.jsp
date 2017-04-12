<!DOCTYPE html>
<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<dsp:page>
<dsp:importbean bean="com/bbb/thirdparty/omniture/formhandler/OmnitureDashboardFormhandler" />
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
<dsp:getvalueof var="conceptsMap" bean="OmnitureDashboardFormhandler.conceptsMap" />
<dsp:getvalueof var="reportStatusTypesMap" bean="OmnitureDashboardFormhandler.reportStatusTypesMap" />
<dsp:getvalueof var="allReportTypesMap" bean="OmnitureDashboardFormhandler.allReportTypesMap" />
<dsp:getvalueof var="reqReportStatusType" bean="OmnitureDashboardFormhandler.reportStatusType" />
<dsp:getvalueof var="reportStatusVOList" bean="OmnitureDashboardFormhandler.reportStatusVOList" />
<dsp:getvalueof var="inputReportIdList" bean="OmnitureDashboardFormhandler.inputReportIdList" />
<dsp:getvalueof var="numberOfDays" bean="OmnitureDashboardFormhandler.numberOfDays" />
<dsp:getvalueof var="displayResult" bean="OmnitureDashboardFormhandler.displayResult" />
<c:set var="failedReport" value="Failed-Cancel" />
<c:set var="allReportType" value="Queued-Success-Failed-Cancel" />
<c:set var="byReportId" value="ByReportId" />

<head>
<title>Omniture Report Status </title>
<style>
table { margin: 0px 0;}

body {-webkit-appearance:none;}
table select { 	width: 100%; }

.lblClass {
	 		background-color: 	#F5F5DC;
   	 		line-height: 20px;
   	 		padding: 2px;
    		display: block;
    		float: left;
    		width: 100%;
			}

.SuccessRsRowCSS { background-color: 	#7FFF00; }
.QueuedRsRowCSS { background-color: 	#FFFF66; }
.FailedRsRowCSS {background-color:#ff8000}
.headerInfo {background-color:#F5F5DC;}
option.findByReportId {background-color: #F5CBA7;}
.redErrorMsg {color:#ff8000;}
</style>
<script src="/qas/jquery/js/jquery-1.4.2.min.js"></script>
<script>	
$(document).ready(function () { hideUnHide() });
$(document).ready(function () { $("#reportStatusType").change(function(){hideUnHide()  }); });
	 
function hideUnHide()
{ 		var e = document.getElementById("reportStatusType");
        var value = e.options[e.selectedIndex].value;
		 
		 if(value == "ByReportId") {
            $('#reportIdContainer').show();
			 $('#conceptFilter').hide();
			 $('#numOfDaysFilter').hide();
			  $('#reportTypeFilter').hide();
        } else {
            $('#reportIdContainer').hide();
			$('#conceptFilter').show();
			$('#numOfDaysFilter').show();
			 $('#reportTypeFilter').show();
       }
}	 
</script>
</head>
<html>
<dsp:form action="OmnitureDashboard.jsp" method="post">
<body>


			<h2 style="color: Blue">Omniture Report Status</h2>
<table>
<tr>
		<td>
		<dsp:droplet name="ErrorMessageForEach">
			<dsp:param param="OmnitureDashboardFormhandler.formExceptions" name="exceptions" />
			<dsp:oparam name="outputStart">
			</dsp:oparam>
			<dsp:oparam name="output">
			<ul class="redErrorMsg">
				<li class="error"><dsp:getvalueof param="message" var="err_msg_key" /> ${err_msg_key}
				</li>
			<dsp:oparam name="outputEnd">
			</dsp:oparam>
			</ul>
			</dsp:oparam>
		</dsp:droplet>
		</td>
</tr>
<tr>
<td>
				<table class="lblClass" >	
				 <tr  >
						<td ><strong>Admin Name:</strong><strong style="color:red">*</strong></td>
						<td colspan="2" >
								<dsp:input type="text" bean="OmnitureDashboardFormhandler.userName" maxlength="40" name="userName" id="userName" >
								<dsp:tagAttribute name="placeholder" value="Enter User Name"/>
								</dsp:input>
							</td> 
				</tr>
				<tr>				
						<td><strong>Admin password:</strong><strong style="color:red">*</strong></td>
						<td colspan="2" >
							<dsp:input type="password" bean="OmnitureDashboardFormhandler.password" maxlength="40" name="password" id="password" >
							<dsp:tagAttribute name="placeholder" value="Enter Password"/>
							<dsp:tagAttribute name="autocomplete" value="off"/>
							</dsp:input>
						</td> 
				</tr>
				
				<tr>						
						<td><strong>Find Report by Status/ReportId</strong><strong style="color:red">*</strong></td>
						<td colspan="2" >						
							<dsp:select bean="OmnitureDashboardFormhandler.reportStatusType" id="reportStatusType">
								<dsp:option value="" selected="true">--Select Concept--</dsp:option>
								<c:forEach items="${reportStatusTypesMap}" var="reportStatusEntry">
								
								<c:choose>
								<c:when test="${byReportId eq reportStatusEntry.key }">
									<dsp:option iClass="findByReportId" value="${reportStatusEntry.key}">${reportStatusEntry.value}</dsp:option>
								</c:when>
								<c:otherwise>
									<dsp:option value="${reportStatusEntry.key}">${reportStatusEntry.value}</dsp:option>
								</c:otherwise>
							</c:choose>
							
					            
					            </c:forEach>
					         </dsp:select>
						</td>
						<td id="reportIdContainer">
							<dsp:input type="text" bean="OmnitureDashboardFormhandler.inputReportIdList"  maxlength="170" size="70" name="inputReportIdList" id="inputReportIdList" > 
								<dsp:tagAttribute name="placeholder" value="Enter list of ReportId"/>
							</dsp:input>
						</td>
				</tr>
					<tr id="conceptFilter">
						<td><strong>Concept</strong></td>
						<td colspan="2">						
							<dsp:select bean="OmnitureDashboardFormhandler.concept">
								<dsp:option value="" selected="true">--Select Concept--</dsp:option>
								<c:forEach items="${conceptsMap}" var="conceptEntry">
					            <dsp:option value="${conceptEntry.key}">${conceptEntry.value}</dsp:option>
					            </c:forEach>
					         </dsp:select>
						</td>
						 
				</tr>
				
				<tr id="reportTypeFilter">
						<td><strong>Report Types</strong></td>
						<td colspan="2">						
							<dsp:select bean="OmnitureDashboardFormhandler.reportType">
								<dsp:option value="" selected="true">--Select report Types--</dsp:option>
								<c:forEach items="${allReportTypesMap}" var="reportTypeEntry">
					            <dsp:option value="${reportTypeEntry.key}">${reportTypeEntry.value}</dsp:option>
					            </c:forEach>
					         </dsp:select>
						</td>
						 
				</tr>
				<tr id="numOfDaysFilter">
						<td><strong>Number of Days</strong></td>
						<td colspan="2">						
							<dsp:input type="text" bean="OmnitureDashboardFormhandler.numberOfDays" maxlength="1" size="1" name="numberOfDays" id="numberOfDays"   />
						</td>
						 
				</tr>
			</div>	
			</table>
</td>
</tr>
<tr>
<td>
	<table>
		<tr>
			<td> <dsp:input bean="OmnitureDashboardFormhandler.getDetail" id="getDetail" type="Submit" value="Get Report Detail" /></td>
			<td> <dsp:input bean="OmnitureDashboardFormhandler.clear" id="clear" type="Submit" value="Clear" /></td>
		</tr>
	</table>
</td>
</tr>
<tr>
<td> 
<table >
	<tr>
		<td>
		  <table border="0">
		 
			<c:choose>
				<c:when test="${ (failedReport  eq reqReportStatusType|| byReportId eq reqReportStatusType || allReportType eq reqReportStatusType ) && fn:length(reportStatusVOList) > 0}">
					<tr class="headerInfo">
						<c:choose>
							<c:when test="${failedReport  eq reqReportStatusType}">
							<th colspan ="14">${fn:length(reportStatusVOList)} result found with Failed Status</strong></th>
							</c:when>
							<c:when test="${allReportType  eq reqReportStatusType}">
							<th colspan ="14">${fn:length(reportStatusVOList)} result found with Queued,Success and Failed Status</strong></th>
							</c:when>
							<c:when test="${byReportId eq reqReportStatusType && not empty inputReportIdList && fn:length(reportStatusVOList) > 0}">
							<th colspan ="14"><strong >${fn:length(reportStatusVOList)} result found for provided ReportId</th>
							</c:when>
						</c:choose>
					</tr>
					
					<tr style ="background:#C0C0C0">
						<th width="6%">ReportId</th>
						<th width="6%">Report Type</th>
						<th width="6%">Concept</th>
						<th width="6%">Current Status</th>
						<th width="6%">Queued Date</th>
						<th width="10%">Last Time of Get Attempt</th>
						<th width="6%">BatchId</th>
						<th width="6%">Batch Seq</th>
						<th width="6%">Count</th>
						<th width="5%">RangeFrom</th>
						<th width="5%">RangeTO</th>
						<th width="2%">Get Attempts</th>
						<th width="8%">ErrorCode</th>
						<th width="22%">ErrorDescription</th>
					</tr>
					<c:forEach var="reportStatusVO" items="${reportStatusVOList}">
					<c:set var= "rsTrCSS"/>
					<c:choose>
						<c:when test="${ ( byReportId eq reqReportStatusType || allReportType eq reqReportStatusType ) && 'Failed' eq reportStatusVO.reportOperationStatus}">
							<c:set var ="rsTrCSS" value="FailedRsRowCSS"/>
						</c:when>
						<c:when test="${ ( byReportId eq reqReportStatusType || allReportType eq reqReportStatusType ) && 'Queued' eq reportStatusVO.reportOperationStatus}">
							<c:set var= "rsTrCSS" value="QueuedRsRowCSS"/>
						</c:when>
						<c:when test="${ ( byReportId eq reqReportStatusType || allReportType eq reqReportStatusType ) && 'Success' eq reportStatusVO.reportOperationStatus}">
							<c:set var ="rsTrCSS" value="SuccessRsRowCSS"/>
						</c:when>
					</c:choose>
						<tr class="${rsTrCSS}">
									<td >${reportStatusVO.reportId}</td>
									<td >${reportStatusVO.reportType}</td>
									<td >${reportStatusVO.concept}</td>
									<td >${reportStatusVO.reportOperationStatus}</td>
									<td >${reportStatusVO.queuedDate}</td>
									<td >${reportStatusVO.reportGetTime1}</td>
							<c:choose>
								<c:when test="${not empty reportStatusVO.batchId }">
									<td >${reportStatusVO.batchId}</td>
									<td >${reportStatusVO.batchSeq}</td>
								</c:when>
								<c:otherwise>
									<td width="10%">NA</td>
									<td width="10%">NA</td>
								</c:otherwise>
							</c:choose>
									<td >${reportStatusVO.count}</td>
									<td >${reportStatusVO.rangeFrom}</td>
									<td >${reportStatusVO.rangeTo}</td>
									<td >${reportStatusVO.attempts}</td>
									<td >${reportStatusVO.errorCode}</td>
									<td >${reportStatusVO.errorDescription}</td>
									
					</tr>
					</c:forEach>
				</c:when>
				<c:when test="${not empty reqReportStatusType && fn:length(reportStatusVOList) > 0 }">
							<tr class="headerInfo">
										<th colspan ="14">  ${fn:length(reportStatusVOList)} result found with ${reqReportStatusType} Status</th>
									</tr>
									<tr style ="background:#C0C0C0">
										<th width="9%">ReportId</th>
										<th width="9%">Report Type </th>
										<th width="9%">Concept</th>
										<th width="9%">Current Status</th>
										<th width="9%">Queued Date</th>
										<th width="12%">Last Time of Get Attempt</th>
										<th width="9%">BatchId</th>
										<th width="9%">Batch Seq</th>
										<th width="9%">Count</th>
										<th width="7%">RangeFrom</th>
										<th width="7%">RangeTo</th>
										<th width="2%">Get Attempts</th>
									</tr>
									<c:forEach var="reportStatusVO" items="${reportStatusVOList}">
										<tr>
												<td >${reportStatusVO.reportId}</td>
												<td >${reportStatusVO.reportType}</td>
												<td >${reportStatusVO.concept}</td>
												<td >${reportStatusVO.reportOperationStatus}</td>
												<td >${reportStatusVO.queuedDate}</td>
												<td >${reportStatusVO.reportGetTime1}</td>
											<c:choose>
											<c:when test="${not empty reportStatusVO.batchId }">
												<td >${reportStatusVO.batchId}</td>
												<td >${reportStatusVO.batchSeq}</td>
											</c:when>
											<c:otherwise>
												<td >NA</td>
												<td >NA</td>
											</c:otherwise>
											</c:choose>
												<td >${reportStatusVO.count}</td>
												<td >${reportStatusVO.rangeFrom}</td>
												<td >${reportStatusVO.rangeTo}</td>
												<td >${reportStatusVO.attempts}</td>
										</tr>
									</c:forEach>
				</c:when>
				<c:when test="${ displayResult && fn:length(reportStatusVOList) == 0 }">
							<tr class="headerInfo redErrorMsg">
										<th colspan ="8" > No Result Found</th>
							</tr>
									
				</c:when>
				
			</c:choose>
			
		  </table>
		</td>
	</tr>
</table>

</td>
</tr>
</table>
</body>	
</dsp:form>
</html>
</dsp:page>
