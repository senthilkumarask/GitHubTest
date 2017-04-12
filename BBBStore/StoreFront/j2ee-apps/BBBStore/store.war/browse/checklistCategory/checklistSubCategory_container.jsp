<dsp:page>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="currentPage" param="currentPage"/>
	<dsp:getvalueof var="pageCount" param="pageCount"/>
	<dsp:getvalueof var="browseSearchVO" param="browseSearchVO"/>
	
	<dsp:include page="checklistSubCategory.jsp">
		<dsp:param name="browseSearchVO" value="${browseSearchVO}" />
		<dsp:param name="url" value="${url}" />
		<dsp:param name="chatEnabled" param="chatEnabled"/>
		<dsp:param name="seoUrl" value="${seoUrl}"/>
	</dsp:include>
		
</dsp:page>