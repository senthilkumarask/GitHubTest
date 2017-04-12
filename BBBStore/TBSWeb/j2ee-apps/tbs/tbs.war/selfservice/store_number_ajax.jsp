<%@page contentType="text/html" pageEncoding="UTF-8"%>
<dsp:page>
	<dsp:importbean bean="/atg/multisite/SiteContext"/>
	<dsp:getvalueof var="tbsSiteId" bean="SiteContext.Site.Id" />
	<json:object> 
	  <json:property name="status" value="${sessionScope.statusVO.status}"/>
	  <json:property name="message" value="${sessionScope.statusVO.message}"/>
	  <json:property name="storeNumber" value="${sessionScope.statusVO.storeNumber}"/>
	  <json:property name="redirectUrl" value="${sessionScope.statusVO.redirectUrl}"/>
	  <json:property name="tbsSiteId" value="${tbsSiteId}"/>
	</json:object> 
</dsp:page>
