<dsp:page>
<dsp:importbean bean="/com/bbb/cms/droplet/GuidesLongDescDroplet"/>
	<dsp:getvalueof var="guideId" param="guideId"/>
	<dsp:getvalueof var="frmReg" param="frmReg"/>
	<dsp:getvalueof var="fromCollege" param="fromCollege"/>
	<c:choose>
    	<c:when test="${frmReg == true or fromCollege eq 'true'}">
    		<dsp:include page="article_detail_page.jsp" flush="true">
				<dsp:param name="guideId" value="${guideId}"/>
				<dsp:param name="fromCollege" param="fromCollege"/>
			</dsp:include>
    	</c:when>
    	<c:otherwise>
	    	<dsp:droplet name="GuidesLongDescDroplet">
	    		<dsp:param name="guideId" value="${guideId}"/>
				<dsp:oparam name="output">
					<dsp:include page="guide_detail_generic.jsp" flush="true">
						<dsp:param name="guideId" value="${guideId}"/>
						<dsp:param name="title" param="guidesLongDesc.title"/>
						<dsp:param name="longDescription" param="guidesLongDesc.longDescription"/>
						<dsp:param name="omnitureData" param="guidesLongDesc.omnitureData"/>
					</dsp:include>
				</dsp:oparam>
				<dsp:oparam name="empty">
					<dsp:include page="../404.jsp" flush="true"/>
				</dsp:oparam>
			</dsp:droplet>
    	</c:otherwise>
    </c:choose>
</dsp:page>
 