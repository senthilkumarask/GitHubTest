<dsp:page>
<dsp:importbean var="CurrentDate" bean="/atg/dynamo/service/CurrentDate"/>
	<dsp:getvalueof var="currentYear" bean="CurrentDate.year"/>
   	<jsp:useBean id="placeHolderMapCopyRight" class="java.util.HashMap" scope="request"/>
	<c:set target="${placeHolderMapCopyRight}" property="currentYear" value="${currentYear}"/>
	<bbbt:textArea key="txt_vbv_learnmore_visa" placeHolderMap="${placeHolderMapCopyRight}" language="<c:out param='${language}'/>"/>
	
	<%-- TODO This jsp needs to be replaced by bcc popup --%>
</dsp:page>

