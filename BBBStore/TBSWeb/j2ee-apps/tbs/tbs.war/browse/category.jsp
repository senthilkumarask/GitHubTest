<dsp:page>
	<dsp:importbean	bean="/com/bbb/commerce/browse/droplet/CategoryLandingDroplet" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/search/droplet/SearchDroplet" />
	<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductComparisonList"/>

	<dsp:getvalueof var="comparisonListItems" bean="ProductComparisonList.items" />
	<dsp:getvalueof var="siteId" bean="Site.id" />
	<dsp:getvalueof var="categoryId" param="categoryId" />
	<dsp:getvalueof var="catFlg" param="catFlg"/>
	<dsp:getvalueof var="subCatPlp" param="subCatPlp"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	
	<dsp:droplet name="CategoryLandingDroplet">
		<dsp:param param="categoryId" name="id" />
		<dsp:param name="siteId" value="${siteId}"/>
		<dsp:param name="catFlg" value="${catFlg}"/>
		<dsp:param name="subCatPlp" value="${subCatPlp}"/>
		
		<dsp:oparam name="output">
			<%-- KP COMMENT: Removing Breadcrumbs --%>
			<%-- <dsp:include page="category_breadcrumb.jsp" /> --%>
			<dsp:include page="category_landing.jsp" flush="true">
				<dsp:param name="categoryId" param="categoryId"/>
				<dsp:param name="landingTemplateVO" param="landingTemplateVO"/>
				<dsp:param name="subcategoriesList" param="subcategoriesList" />
				<dsp:param name="categoryL1" param="categoryL1" />
				<dsp:param name="categoryL2" param="categoryL2" />
				<dsp:param name="categoryL3" param="categoryL3" />
			</dsp:include>
		</dsp:oparam>
		<dsp:oparam name="subcat">
		<dsp:getvalueof var="seoUrl" param="categoryVO.seoURL"/>
			<dsp:droplet name="SearchDroplet">
				<dsp:param name="CatalogRefId" param="categoryId"/>
				<dsp:param name="CatalogId" param="categoryId"/>
				<dsp:param name="bccSortCode" param="categoryVO.sortOptionVO.defaultSortingOption.sortUrlParam"/>
				<dsp:param name="bccSortOrder" param="categoryVO.sortOptionVO.defaultSortingOption.ascending"/>
				<dsp:param name="comparisonListItems" value="${comparisonListItems}"/>
				<dsp:oparam name="error_PageNumOutOfBound">
			        <dsp:include page="../404.jsp" flush="true"/>
		        </dsp:oparam> 
				<dsp:oparam name="output">
					<%-- KP COMMENT: Removing Breadcrumbs --%>
					<%-- <dsp:include page="category_breadcrumb.jsp" /> --%>
					<dsp:include page="subcategory_container.jsp" flush="true">
						<dsp:param name="count" param="browseSearchVO.bbbProducts.bbbProductCount"/> 
						<dsp:param name="size" param="browseSearchVO.pagingLinks.pageSize"/> 
						<dsp:param name="currentPage" param="browseSearchVO.pagingLinks.currentPage"/>
						<dsp:param name="pageCount" param="browseSearchVO.pagingLinks.pageCount" />
						<dsp:param name="browseSearchVO" param="browseSearchVO" />
						<dsp:param name="CatalogRefId" param="categoryId"/>
						<dsp:param name="CatalogId" param="categoryId"/>
						<dsp:param name="categoryVO" param="categoryVO"/>
					</dsp:include>
				</dsp:oparam>
				<dsp:oparam name="redirect">
				<dsp:getvalueof	var="isRedirectToParent" param="redirectToParent"/>
					<c:if test="${isRedirectToParent eq 'true'}">
						<dsp:droplet name="/atg/dynamo/droplet/Redirect">
							<dsp:param name="url" value="${contextPath}${seoUrl}" />
						</dsp:droplet>
					</c:if>
				</dsp:oparam>	
				<dsp:oparam name="empty">
					<dsp:include page="../404.jsp" flush="true"/>
				</dsp:oparam>
				<dsp:oparam name="error">
					<dsp:include page="../global/serverError.jsp" flush="true"/>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
		<dsp:oparam name="error">
			<dsp:include page="../404.jsp" flush="true"/>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>