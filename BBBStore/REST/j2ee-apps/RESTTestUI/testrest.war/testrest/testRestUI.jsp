<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml"%>
<%@ taglib prefix="dsp"	uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib uri="/BBBError" prefix="bbbe"%>
<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />	
	<meta http-equiv="content-type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
	<meta name="viewport" content="width=device-width,initial-scale=1" />
	<meta name="description" content="Description goes here." />
	<meta name="keywords" content="Keyword" />
	<title>Bed Bath &amp; Beyond - Internal - Instant Previe</title>

	<link rel="stylesheet" type="text/css" href="css/12colgrid.css" />
	<link rel="stylesheet" type="text/css" href="css/jquery-ui-1.8.16.custom.css">
	<link rel="stylesheet" type="text/css" href="css/preview.css">

	<script type="text/javascript" src="js/jquery-1.7.1.min.js"></script>
	<script type="text/javascript" src="js/json.js"></script>
	<script type="text/javascript" src="js/jquery.validate.js"></script>
	<script type="text/javascript" src="js/jquery.uniform.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery-ui-timepicker-addon.js"></script>
	<script type="text/javascript" src="js/jquery-ui-sliderAccess.js"></script>
	


<script type="text/javascript">
$(function() {
	function getFormData($form){
		var unindexed_array = $form.serializeArray(),
			indexed_array = {};
		
		$.map(unindexed_array, function(n, i){
			indexed_array[n['name']] = n['value'];
		});
		return indexed_array;
	}
	
	
	$("#theform").on('submit', function(e){
		e.preventDefault();
		$('#loading').removeClass('hidden');
		$.ajax({
		data: JSON.stringify(getFormData($("#theform").find('.reqBody'))),
		url: $('#theform').attr("action"),
		dataType: "json",
		headers:getFormData($("#theform").find('.reqHead')),
		method: "post",
		contentType:"application/json",
		success: function(data) {
		    $('#loading').addClass('hidden');
			if($("#atg-rest-output").val()== 'json')
			{
			$("#dataStore").val(JSON.stringify(data));
			}
			else{
			$("#dataStore").val(data.responseText);
			}
		},
		error: function(data) {
		    $('#loading').addClass('hidden');
			if($("#atg-rest-output").val()== 'json')
			{
			$("#dataStore").val(JSON.stringify(data));
			}
			else{
			$("#dataStore").val(data.responseText);
			}
			$("#errorMsg").text(JSON.stringify(data.responseText));
		}
	});
});
});
</script>
	<div class="container_12 clearfix">
		<div class="grid_12 boxWrapper boxShadow">
			<div id="header" class="grid_10 alpha omega suffix_2 clearfix">
				<a href="#"><img src="images/logo_bbb_by.png" alt="Bed Bath & Beyond" title="BBB Home" height="42" width="146" /> </a>
				<div class="clear"></div>
			</div>

			<div id="content" class="grid_10 alpha omega suffix_1 prefix_1 clearfix">
				
				<dsp:getvalueof	bean="/SessionConfirmationNumberHolder.sessionConfirmationNumber" id="sessionId" />
				<dsp:droplet name="/atg/dynamo/droplet/Switch">
					<dsp:param name="value" bean="/atg/userprofiling/Profile.transient" />
					<dsp:oparam name="false">
						<dsp:form action="/restui/testrest/testRestUI.jsp">
							 <dsp:input type="hidden" bean="ProfileFormHandler.logoutSuccessURL" value="/restui/testrest/testRestUI.jsp"/>
							 <dsp:input type="submit" bean="ProfileFormHandler.logout" value="click here to logout"/>
						</dsp:form>
			      </dsp:oparam>
				</dsp:droplet>
				<dsp:getvalueof param="var" id="var" />
				
				
				<c:import url="xmlstyles/masterRender.xsl" var="mxslt" />

				<%-- Master XML --%>
			    <dsp:droplet name="/com/bbb/restui/xml/droplet/XMLTextDroplet">
			    	<dsp:param name="xmlFileName" value="master.xml" />
			       	<dsp:oparam name="output">			       	
		   				<dsp:getvalueof id="xmlReader" param="reader" idtype="java.io.Reader"/>
		   			    <%-- Transform Masterxml to Rest API drop-down selection --%>
						<x:transform  xslt="${mxslt}" doc="${xmlReader}">
							<x:param name="sessionId" value="${sessionId}"></x:param>
						</x:transform>
			   		</dsp:oparam>
			   		<dsp:oparam name="empty">
				   			<div id="errorMsg" style="color:red">
				   		 		<bbbe:error key="err_missing_master_xml_msg" language="${pageContext.request.locale.language}"/>
				   		 	</div>
			   		</dsp:oparam>
				 </dsp:droplet>

				<c:if test="${not empty var}">
						
						<strong>API :</strong> <c:out value="${var}" />
						
						<c:import url="xmlstyles/createAPIForm.xsl" var="xslt" />
				
						<%-- API specific XML text --%>
					    <dsp:droplet name="/com/bbb/restui/xml/droplet/XMLTextDroplet">
					    	<dsp:param name="xmlFileName" value="${var}" />
					       	<dsp:oparam name="output"> 
				   					<dsp:getvalueof id="apiXmlReader" param="reader" idtype="java.io.Reader"/>					   			
				   					<%-- Transform API XML to Rest API Form selection --%>
									<x:transform doc="${apiXmlReader}" xslt="${xslt}">
										<x:param name="sessionId" value="${sessionId}"></x:param>
										<x:param name="actionHttpHost" value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}"></x:param>
									</x:transform>
					   		</dsp:oparam>
					   		<dsp:oparam name="empty">
								<div id="errorMsg" style="color:red">
									<bbbe:error key="err_missing_api_xml_msg" language="${pageContext.request.locale.language}"/>
								</div>
				   			</dsp:oparam>
						</dsp:droplet>
						<div class="clear"></div>
					<textarea id="dataStore" rows="50" cols="120"  value="result"></textarea>
					</c:if>
									
			</div>
			<div id="footer" class="grid_10 alpha omega suffix_1 prefix_1 clearfix">
				<p>&copy;1999-2011 Bed Bath & Beyond Inc. and its subsidiaries. All rights reserved.</p>
				<div class="clear"></div>
			</div>
			
		</div>
	</div>
</dsp:page>