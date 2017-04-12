<dsp:page>
<jsp:useBean id="cookies" class="java.util.HashMap" scope="request"/>
<c:set target="${cookies}" property="siteId"><dsp:valueof bean="/atg/multisite/Site.id"/></c:set>
<dsp:importbean bean="/com/bbb/account/droplet/BBBSetCookieDroplet"/>
<dsp:setvalue bean="BBBSetCookieDroplet.cookies" value="${cookies}" />
	<dsp:droplet name="BBBSetCookieDroplet">
		<dsp:oparam name="output">
		</dsp:oparam>
	</dsp:droplet>
<bbb:pageContainer>
    <jsp:attribute name="section">browse</jsp:attribute>
	<jsp:attribute name="pageWrapper">homePage</jsp:attribute>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
   	<c:set var="htmlString" scope="request"><bbbl:textArea key="txt_levolor_landing_page" language="${pageContext.request.locale.language}"/></c:set>  
   <dsp:droplet name="/com/bbb/account/droplet/BBBChangeAnchorTagUrlDroplet">
   	<dsp:param name="htmlString" value="${htmlString}"/>
   	<dsp:param name="requestParamName" value="kirschRedirectUrl"/>
   	<dsp:param name="changedUrl" value="${contextPath}/global/kirschintercept.jsp"/>
   <dsp:oparam name="output">
	<dsp:getvalueof var="changedHtmlString" param="changedHtmlString"/>
   </dsp:oparam>
   </dsp:droplet>
  
   <div id="content" class="container_12 clearfix" role="main">
	${changedHtmlString}
	</div>
</bbb:pageContainer>
</dsp:page>