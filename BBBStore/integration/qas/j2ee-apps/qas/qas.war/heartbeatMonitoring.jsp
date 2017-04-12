<%@ taglib uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"
	prefix="dsp"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<dsp:page>
	<dsp:importbean
		bean="/com/bbb/rest/stofu/droplet/HeartbeatMonitoringDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/Range"/>
	<head>
	<title>Heartbeat Monitoring</title>
	<style>

body {
   
    margin:0;
    margin-bottom:20px;
    font-family: 'trebuchet MS', 'Lucida sans', Arial;
    font-size: 14px;
    color: #444;
    padding:0 20px;
   
}
h1{
margin:0;
padding:20px 0;
}

table {
    *border-collapse: collapse; /* IE7 and lower */
    border-spacing: 0;
   
     table-layout: fixed;
}

.bordered {
    border: solid #ccc 1px;
    -moz-border-radius: 6px;
    -webkit-border-radius: 6px;
    border-radius: 6px;
    -webkit-box-shadow: 0 1px 1px #ccc; 
    -moz-box-shadow: 0 1px 1px #ccc; 
    box-shadow: 0 1px 1px #ccc;     
    width:100%;    
}

.bordered tr:hover {
    background: #fbf8e9;
    -o-transition: all 0.1s ease-in-out;
    -webkit-transition: all 0.1s ease-in-out;
    -moz-transition: all 0.1s ease-in-out;
    -ms-transition: all 0.1s ease-in-out;
    transition: all 0.1s ease-in-out;     
}    
    
.bordered td, .bordered th {
    border-left: 1px solid #ccc;
    border-top: 1px solid #ccc;
    padding: 10px;
    text-align: left;    
    word-wrap: break-word;
}

.bordered th {
    background-color: #dce9f9;
    background-image: -webkit-gradient(linear, left top, left bottom, from(#ebf3fc), to(#dce9f9));
    background-image: -webkit-linear-gradient(top, #ebf3fc, #dce9f9);
    background-image:    -moz-linear-gradient(top, #ebf3fc, #dce9f9);
    background-image:     -ms-linear-gradient(top, #ebf3fc, #dce9f9);
    background-image:      -o-linear-gradient(top, #ebf3fc, #dce9f9);
    background-image:         linear-gradient(top, #ebf3fc, #dce9f9);
    -webkit-box-shadow: 0 1px 0 rgba(255,255,255,.8) inset; 
    -moz-box-shadow:0 1px 0 rgba(255,255,255,.8) inset;  
    box-shadow: 0 1px 0 rgba(255,255,255,.8) inset;        
    border-top: none;
    text-shadow: 0 1px 0 rgba(255,255,255,.5); word-wrap: break-word;
}

.bordered td:first-child, .bordered th:first-child {
    border-left: none;
}

.bordered th:first-child {
    -moz-border-radius: 6px 0 0 0;
    -webkit-border-radius: 6px 0 0 0;
    border-radius: 6px 0 0 0;
}

.bordered th:last-child {
    -moz-border-radius: 0 6px 0 0;
    -webkit-border-radius: 0 6px 0 0;
    border-radius: 0 6px 0 0;
}

.bordered th:only-child{
    -moz-border-radius: 6px 6px 0 0;
    -webkit-border-radius: 6px 6px 0 0;
    border-radius: 6px 6px 0 0;
}

.bordered tr:last-child td:first-child {
    -moz-border-radius: 0 0 0 6px;
    -webkit-border-radius: 0 0 0 6px;
    border-radius: 0 0 0 6px;
}

.bordered tr:last-child td:last-child {
    -moz-border-radius: 0 0 6px 0;
    -webkit-border-radius: 0 0 6px 0;
    border-radius: 0 0 6px 0;
}
 
</style>
</head>
	
	<h1 style="text-align: center;">Heartbeat Monitoring Tool</h1>
	<table class="bordered">
		<tr>
			<th>Store ID</th>
			<th>Channel</th>
			<th>Theme</th>
			<th>Terminal ID</th>
			<th>Friendly Name</th>
			<th>App ID</th>
			<th>Timestamp</th>
			<th>Last App Status</th>
			<th>Current Health</th>
		</tr>
		<dsp:getvalueof var="pageNumber" param="pageNumber" />
		<dsp:getvalueof var="pageSize" param="pageSize" />
		<dsp:droplet name="HeartbeatMonitoringDroplet">
			<dsp:param name="storeId" param="storeId" />
			<dsp:param name="appId" param="appId" />
			<dsp:param name="health" param="health" />			
			<dsp:oparam name="output">			
			<dsp:getvalueof param="listHeartbeatMonitoringVO" var="list" />
					<dsp:droplet name="Range">
					<dsp:param name="array" param="listHeartbeatMonitoringVO" />
					<c:if test="${pageNumber eq null and pageSize eq null }">
					<dsp:param name="start" value="1"/>
					<dsp:param name="howMany" value="${fn:length(list) }"/>
					</c:if>
					<c:if test="${pageNumber ne null and pageSize eq null }">
					<c:set var="pageSize" value="50"></c:set>
					<dsp:param name="start" value="${pageSize*(pageNumber-1) + 1}"/>
					<dsp:param name="howMany" value="${pageSize }"/>
					</c:if>
					<c:if test="${pageNumber eq null and pageSize ne null }">
					<dsp:param name="start" value="1"/>
					<dsp:param name="howMany" value="${pageSize}"/>
					</c:if>
					<c:if test="${pageNumber ne null and pageSize ne null }">					
					<dsp:param name="start" value="${pageSize*(pageNumber-1) + 1}"/>
					<dsp:param name="howMany" value="${pageSize }"/>
					</c:if>					
					<dsp:oparam name="output">				
					<tr>
					<td><dsp:valueof param="element.storeId" /></td>
					<td><dsp:valueof param="element.channel" /></td>
					<td><dsp:valueof param="element.channelTheme" /></td>
					<td><dsp:valueof param="element.terminalId" /></td>
					<td><dsp:valueof param="element.friendlyName" /></td>
					<td><dsp:valueof param="element.appId" /></td>
					<td><dsp:valueof param="element.logTime" /></td>
					<td><dsp:valueof param="element.appState" /></td>
					<dsp:getvalueof param="element.health" var="healthVal" />
					<c:if test="${healthVal eq 'red' }"><td bgcolor="red"></td></c:if>
					<c:if test="${healthVal eq 'yellow' }"><td bgcolor="yellow"></td></c:if>
					<c:if test="${healthVal eq 'green' }"><td bgcolor="green"></td></c:if>
					<c:if test="${healthVal eq 'white' }"><td bgcolor="white"></td></c:if>									
					</tr>					
					</dsp:oparam>
					</dsp:droplet>
			</dsp:oparam>
		</dsp:droplet>
	</table>
</dsp:page>