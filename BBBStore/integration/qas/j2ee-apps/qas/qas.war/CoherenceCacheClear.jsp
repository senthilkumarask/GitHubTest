<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<dsp:page>
	<dsp:importbean bean="/com/bbb/integration/csr/CSRFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:getvalueof var="cacheKeys" bean="CSRFormHandler.CoherenceCacheMap" />
	<dsp:getvalueof var="mobileCacheKeys" bean="CSRFormHandler.MobileCoherenceCacheMap" />
	<dsp:getvalueof var="listOfCacheKeys" bean="CSRFormHandler.listOfCacheKeys" />
	<dsp:getvalueof var="listOfMobileCacheKeys" bean="CSRFormHandler.listOfMobileCacheKeys" />
	<dsp:getvalueof var="maxCacheKeysLimit" bean="CSRFormHandler.maxCacheKeysLimit" />
	<html>
	<head>
	<title>Coherence Cache Clear - Admin Page</title>
	<link rel="stylesheet" href="/qas/qas.css" />
	</head>
	<body>
		<h2>Coherence Cache Clear - Admin Page</h2>
		<dsp:droplet name="ErrorMessageForEach">
			<dsp:param param="formhandler.formExceptions" name="exceptions" />
			<dsp:oparam name="outputStart">
			</dsp:oparam>
			<dsp:oparam name="output">
			<ul class="error">
				<li class="error"><dsp:getvalueof param="message" var="err_msg_key" /> ${err_msg_key}
				</li>
			<dsp:oparam name="outputEnd">
			</dsp:oparam>
			</ul>
			</dsp:oparam>
		</dsp:droplet>
		<dsp:form id="cacheInfo" iclass="clearfix"
			action="CoherenceCacheClear.jsp" method="post">
			<table border="noborder" style="width: 600px;">
				<tr>
					<td><strong>Admin Name</strong><span class="required">*</span>
					</td>
					<td><dsp:input type="text" bean="CSRFormHandler.adminName" maxlength="40" name="adminName" id="adminName" /></td>
				</tr>
				<tr>
					<td><strong>Admin Password</strong><span class="required">*</span>
					</td>
					<td><dsp:input type="password" bean="CSRFormHandler.adminPassword" maxlength="40" name="adminPassword" id="adminPassword" /></td>
				</tr>
				</table>
				<br/><strong>DESKTOP CACHES	: </strong><br/>
				
				<table border="noborder" style="width: 600px;">
				<tr>
					<td><strong>Desktop Cache Type</strong><span class="required">*</span>
					</td>
					<td>
					<dsp:select bean="CSRFormHandler.cacheType">
					<dsp:option value="" selected="true">Select Cache Type</dsp:option>
					<dsp:option value="ALL">ALL</dsp:option>
		            <c:forEach items="${cacheKeys}" var="entry">
		            <dsp:option value="${entry.key}">${entry.key}</dsp:option>
		            </c:forEach>
		            </dsp:select>
	        		</td>
				</tr>
				<tr>
					<td colspan="2"><dsp:input bean="CSRFormHandler.listAllKeys" id="cacheKeysBtn" type="Submit" value="List All Keys" />
					<dsp:input bean="CSRFormHandler.clearCache" id="cacheInfoBtn" type="Submit" value="CLEAR CACHE OF ALL KEYS" />
					</td>
				</tr>
				
				
			</table>
			
			
			
			<table border="noborder" style="width: 600px;">
				<tr>
					<td><strong>List of All Keys:</strong>
					</td>
				</tr>
				<tr>
					<td>
					<input type="checkbox" class="selectall" value="selectall" onClick="this.value=selectAll(this.form.selectedCacheKeysToDelete)" /> <b>SELECT ALL</b><br>
		            <c:forEach items="${listOfCacheKeys}" var="keys" varStatus="keysCounter">
		            	<c:if test="${keysCounter.count lt maxCacheKeysLimit or keysCounter.count eq maxCacheKeysLimit}">
		            		<dsp:input type="checkbox" bean="CSRFormHandler.selectedCacheKeysToDelete" name="selectedCacheKeysToDelete" value="${keys}"/>
		            		${keys}
		            		<br>
		            	</c:if>
		            </c:forEach>
	        		</td>
				</tr>
				<tr>
					<td colspan="2"><dsp:input bean="CSRFormHandler.clearCacheSelectedKeys" id="selectCacheKeysBtn" type="Submit" value="CLEAR CACHE OF SELECTED KEYS" />
					</td>
				</tr>
			</table>
			
			<dsp:getvalueof param="failure" id="failure" />
			<dsp:getvalueof param="updated" id="updated" />
			<c:if test="${not empty failure}">
				Please fix the issue mentioned above !!
			</c:if>
			<c:if test="${not empty updated}">
				<b>Cache Details:</b> <br> ${updated} <br>Selected operation performed successfully. For more details, please verify the updates on DYN/ADMIN!!!
			</c:if>	
			<br/><br><strong>MOBILE CACHES	: </strong><br/>
			<table border="noborder" style="width: 600px;">
			
				<tr>
					<td style="width: 190px;"><strong>Mobile Cache Type</strong><span class="required">*</span>
					</td>
					<td>
					<dsp:select bean="CSRFormHandler.mobileCacheType">
					<dsp:option value="" selected="true">Select Cache Type</dsp:option>
					<dsp:option value="ALL">ALL</dsp:option>
		            <c:forEach items="${mobileCacheKeys}" var="entry">
		            <dsp:option value="${entry.key}">${entry.key}Cache</dsp:option>
		            </c:forEach>
		            </dsp:select>
	        		</td>
				</tr>
				<tr>
					<td colspan="3">
					<dsp:input bean="CSRFormHandler.listAllMobileKeys" id="cacheKeysBtn" type="Submit" value="List All Keys" />
					<dsp:input bean="CSRFormHandler.getNearMobileCacheSize" id="cacheSizeBtn" type="Submit" value="GET CACHE SIZE" />
					<dsp:input bean="CSRFormHandler.clearMobileCache" id="cacheInfoBtn" type="Submit" value="CLEAR CACHE OF ALL KEYS" />
					</td>
				</tr>
			</table>
			
			<table border="noborder" style="width: 600px;">
				<tr>
					<td><strong>List of All Keys:</strong>
					</td>
				</tr>
				<tr>
					<td>
					<input type="checkbox" class="selectall" value="selectall" onClick="this.value=selectAll(this.form.selectedMobileCacheKeysToDelete)" /> <b>SELECT ALL</b><br>
		            <c:forEach items="${listOfMobileCacheKeys}" var="keys" varStatus="keysCounter">
		            	<c:if test="${keysCounter.count lt maxCacheKeysLimit or keysCounter.count eq maxCacheKeysLimit}">
		            		<dsp:input type="checkbox" bean="CSRFormHandler.selectedMobileCacheKeysToDelete" name="selectedMobileCacheKeysToDelete" value="${keys}"/>
		            		${keys}
		            		<br>
		            	</c:if>
		            </c:forEach>
	        		</td>
				</tr>
				<tr>
					<td colspan="2"><dsp:input bean="CSRFormHandler.clearMobileCacheSelectedKeys" id="selectCacheKeysBtn" type="Submit" value="CLEAR CACHE OF SELECTED KEYS" />
					</td>
				</tr>
			</table>
			
			
		<dsp:getvalueof param="failureMobile" id="failureMobile" />
		<dsp:getvalueof param="updatedMobile" id="updatedMobile" />
		<br/>
		<c:if test="${not empty failureMobile}">
			Please fix the issue mentioned above !!
		</c:if>
		<c:if test="${not empty updatedMobile}">
			<b>Cache Details:</b> <br> ${updatedMobile} <br>Selected operation performed successfully. For cache size details, please check by clicking on 'Get Cache Size' button!!!
		</c:if>	
		</dsp:form>
		<script>
		var checkflag = "false";
		function selectAll(field) {
			if (checkflag == "false") {
				for (i = 0; i < field.length; i++) {
					field[i].checked = true;
				}
				checkflag = "true";
				return "Uncheck All";
			} else {
				for (i = 0; i < field.length; i++) {
					field[i].checked = false;
				}
				checkflag = "false";
				return "Check All";
			}
		}
		</script>
	</body>
	</html>
</dsp:page>
