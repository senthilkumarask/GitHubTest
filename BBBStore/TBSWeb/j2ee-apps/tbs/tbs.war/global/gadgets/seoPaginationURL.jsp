<c:set var="filterAppliedWithPrefix" value="/${filterApplied}"></c:set>
<c:set var="filterAppliedWithSuffix" value="${filterApplied}/"></c:set>
<c:if test="${isFilterApplied eq 'false'}">
	<c:set var="filterAppliedWithPrefix" value=""></c:set>
	<c:set var="filterAppliedWithSuffix" value=""></c:set>
</c:if>
<dsp:getvalueof var="PageType" param="PageType" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
<c:set var="domainName">
	<bbbc:config key="requestDomainName" configName="MobileWebConfig" />
</c:set>
<c:set var="http" value="http://" />

<c:set var="seoPageNoNext" value="${currentPage+1}-${size}"></c:set>
<c:set var="seoPageNoPrev" value="${currentPage-1}-${size}"></c:set>
<c:set var="seoPageCurrent" value="${currentPage}-${size}"></c:set>

<c:set var="prevLinkFlag" value="false" />
<c:set var="nextLinkFlag" value="false" />

<c:if test="${currentPage >= 1 && currentPage < pageCount}">
	<%-- This block will create next link for on all the pages other then last page --%>
	<c:set var="nextLinkFlag" value="true" />
</c:if>
<c:if test="${currentPage > 1 && currentPage <= pageCount}">
	<%-- This block will create prev link for on all the pages other then first page --%>
	<c:set var="prevLinkFlag" value="true" />
</c:if>
<%-- This block is to generate next pagination link for SEO --%>
<c:if test="${nextLinkFlag eq true}">
	<c:choose>
		<c:when test="${PageType eq 'Search'}">
			<c:choose>
				<c:when test="${pageTypeForCanonicalURL eq 'Brand'}">
					<link rel="next"
						href="${http}${domainName}${contextPath}/brand/${brandNameForUrl}/${searchTerm}${filterAppliedWithPrefix}/${seoPageNoNext}/" />
				</c:when>
				<c:otherwise>
					<link rel="next"
						href="${http}${domainName}${contextPath}/s/${searchTerm}${filterAppliedWithPrefix}/${seoPageNoNext}" />
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:when test="${PageType eq 'SubCategoryDetails'}">
			<c:choose>
			<%-- R2.2 Story 116E Start--%>
				<c:when test="${pageTypeForCanonicalURL eq 'Brand'}">
					<link rel="next"
						href="${http}${domainName}${contextPath}/brand/${brandNameForUrl}/${searchTerm}${filterAppliedWithPrefix}/${seoPageNoNext}/" />
				</c:when>
				<c:otherwise>
					<link rel="next"
						href="${http}${domainName}${contextPath}${seoUrl}${filterAppliedWithSuffix}${seoPageNoNext}/" />
				</c:otherwise>
			<%-- R2.2 Story 116E End--%>	
			</c:choose>
		</c:when>
	</c:choose>
</c:if>

<%-- This block is to generate prev pagination link for SEO --%>
<c:if test="${prevLinkFlag eq true }">
	<c:choose>
		<c:when test="${PageType eq 'Search'}">
			<c:choose>
				<c:when test="${pageTypeForCanonicalURL eq 'Brand'}">
					<link rel="prev"
						href="${http}${domainName}${contextPath}/brand/${brandNameForUrl}/${searchTerm}${filterAppliedWithPrefix}/${seoPageNoPrev}/" />
				</c:when>
				<c:otherwise>
					<link rel="prev"
						href="${http}${domainName}${contextPath}/s/${searchTerm}${filterAppliedWithPrefix}/${seoPageNoPrev}" />
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:when test="${PageType eq 'SubCategoryDetails'}">
		<c:choose>
		<%-- R2.2 Story 116E Start--%>
				<c:when test="${pageTypeForCanonicalURL eq 'Brand'}">
					<link rel="prev"
						href="${http}${domainName}${contextPath}/brand/${brandNameForUrl}/${searchTerm}${filterAppliedWithPrefix}/${seoPageNoPrev}/" />
				</c:when>
				<c:otherwise>
					<link rel="prev" href="${http}${domainName}${contextPath}${seoUrl}${filterAppliedWithSuffix}${seoPageNoPrev}/" />
				</c:otherwise>
		</c:choose>
		<%-- R2.2 Story 116E End--%>
		</c:when>
	</c:choose>
</c:if>


