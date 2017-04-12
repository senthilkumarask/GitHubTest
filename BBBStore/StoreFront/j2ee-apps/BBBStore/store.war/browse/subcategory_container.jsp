<dsp:page>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="CatalogRefId" param="CatalogRefId"/>
	<dsp:getvalueof var="CatalogId" param="CatalogId"/>
	<dsp:getvalueof var="url" vartype="java.lang.String" value="/store/browse/subcategory_container.jsp?" />
	<dsp:getvalueof var="fromCollege" param="fromCollege" scope="request"/>
	<dsp:getvalueof var="count" param="count"/>
	<dsp:getvalueof var="size" param="size"/>
	<dsp:getvalueof var="currentPage" param="currentPage"/>
	<dsp:getvalueof var="pageCount" param="pageCount"/>
	<dsp:getvalueof var="browseSearchVO" param="browseSearchVO"/>
		<dsp:getvalueof var="filtersonLoad" param="filtersonLoad"/>
	<dsp:getvalueof var="categoryVO" param="categoryVO"/>
	<dsp:include page="category_breadcrumb.jsp">
		<dsp:param name="categoryId" value="${CatalogRefId}"/>
	</dsp:include>
		
	<%-- Removed the SEO URL for Next and previous page and place that in seoPaginationURL.jsp getting included from pageStart.jsp --%>  
	<c:choose>
		<c:when test="${not empty categoryVO.bccManagedPromoCategoryVO}">
			<dsp:include page="subcategory_promotion.jsp">
			<dsp:param name="browseSearchVO" value="${browseSearchVO}" />
			<dsp:param name="url" value="${url}" />
			<dsp:param name="showDepartment" value="false" />
			<dsp:param name="catID" value="${CatalogRefId}"/>
			<dsp:param name="categoryVO" value="${categoryVO}"/>
			<dsp:param name="chatEnabled" param="chatEnabled"/>
			<dsp:param name="narrowDown" value="${param.narrowDown}"/>
			<dsp:param name="fromCatPage" param="fromCatPage"/>
			<dsp:param name="filtersonLoad" value="${filtersonLoad}"/>
		</dsp:include>
		</c:when>
		<c:otherwise>
			<dsp:include page="subcategory.jsp">
			<dsp:param name="browseSearchVO" value="${browseSearchVO}" />
			<dsp:param name="url" value="${url}" />
			<dsp:param name="showDepartment" value="false" />
			<dsp:param name="catID" value="${CatalogRefId}"/>
			<dsp:param name="categoryVO" value="${categoryVO}"/>
			<dsp:param name="chatEnabled" param="chatEnabled"/>
			<dsp:param name="narrowDown" value="${param.narrowDown}"/>
			<dsp:param name="fromCatPage" param="fromCatPage"/>
		</dsp:include>
		</c:otherwise>
	</c:choose>	
		
		
</dsp:page>