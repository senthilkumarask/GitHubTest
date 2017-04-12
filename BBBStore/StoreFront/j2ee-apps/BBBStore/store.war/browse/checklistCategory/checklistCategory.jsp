<dsp:page>
<%@ page import="atg.servlet.ServletUtil" %>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="queryString" bean="/OriginatingRequest.queryString"/>
	<dsp:importbean	bean="/com/bbb/commerce/checklist/droplet/SeoUrlCheckListDroplet" />
	<dsp:importbean	bean="/com/bbb/commerce/checklist/droplet/ValidateCheckListCategoryDroplet" />
	<dsp:importbean	bean="/com/bbb/search/droplet/BBBDefaultSortOptionsDroplet"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/userprofiling/Profile" /> 
	<dsp:importbean bean="/com/bbb/search/droplet/SearchDroplet" />
	<dsp:getvalueof var="siteId" bean="Site.id" />
	<dsp:getvalueof var="checklistCategoryId" param="checklistCategoryId" />
	<dsp:getvalueof var="checklistId" param="checklistId" />
	<c:set var="clearFilter" scope="request"><c:out value="${param.clearFilters}"/></c:set>
	<c:set var="localStorePLPFlag" scope="request">false</c:set>
	<c:set var="isFromChecklistCategory" scope="request">true</c:set>
	
	<c:set var="forwardRequestURI"> <%=ServletUtil.getCurrentRequest().getAttribute("javax.servlet.forward.request_uri")%></c:set>
	
	<%-- remove session variables   --%>
	
	<c:remove var="redirectURL" scope="session" />
	<c:remove var="checkListFlow" scope="session" />
	<c:remove var="checkListflowSuccessUrl" scope="session" />
	
	<dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
	<dsp:droplet name="ValidateCheckListCategoryDroplet">
		<dsp:oparam name="output">
			<dsp:getvalueof var="categoryEnable" param="categoryEnable" scope="request" />
			<dsp:getvalueof var="checkListEnable" param="checkListEnable" scope="request" />
			<dsp:getvalueof var="checkListType" param="checkListType" scope="request" />
			<dsp:getvalueof var="isValidUrl" param="isValidUrl" scope="request" />
			<dsp:getvalueof var="checkListDisplayName" param="checkListDisplayName" scope="request" />
			
			<c:set var="interactiveCheckListFlag"><bbbc:config key="interactive_checklist_key" configName="FlagDrivenFunctions" /></c:set>
		
			<c:choose>
				<c:when test="${not interactiveCheckListFlag}">
				     <dsp:include page="checklistError.jsp" flush="true">
				     	<dsp:param name="displayAction" value="icConfigOff" />
				</dsp:include>
				</c:when>
			   <%-- send to login page
			       If url is not valid and user is non login then send user to login page
			       If non login user looking for a registry type checklist then send user to login page
			   --%>
				<c:when test="${isTransient && 'true' ne isValidUrl || ( isTransient  && 'guideType' ne checkListType )}">
				    <dsp:droplet name="/atg/dynamo/droplet/Redirect">
							<dsp:param name="url" value="${contextPath}/account/Login" />
							<c:set var="redirectURL" value="${forwardRequestURI}" scope="session" />
							<c:set var="checkListFlow" value="true" scope="session" />
							<c:set var="checkListflowSuccessUrl" value="${forwardRequestURI}" scope="session" />
							<c:set var="inItSkinnyVO" value="true" scope="session" />
					</dsp:droplet>
				</c:when>
				<%-- if url does not exist,show My Registry or RLP URL  with messages    --%>
				<c:when test="${'true' ne isValidUrl}">
				     <dsp:include page="checklistError.jsp" flush="true">
				     	<dsp:param name="displayAction" value="inValidUrl" />
					</dsp:include>
				</c:when>
				<%-- Show error page if non login user looking for Guide Checklist and checklist or category is disable    --%>
				<c:when test="${isTransient  && 'guideType' eq  checkListType  && ('true' ne categoryEnable || 'true' ne checkListEnable)}">
				   <dsp:include page="checklistError.jsp" flush="true">
				     	<dsp:param name="displayAction" value="nonLoginUser" />
					</dsp:include>
				</c:when>
				<%-- Show error page if login user looking for registry/guide Checklist and checklist or category is disable    --%>
				<c:when test="${ ! isTransient  && ('true' ne categoryEnable || 'true' ne checkListEnable)}">
				    <dsp:include page="checklistError.jsp" flush="true">
				     	<dsp:param name="displayAction" value="LoginUser" />
				    </dsp:include>
				</c:when>
				<c:otherwise>
									<dsp:droplet name="SeoUrlCheckListDroplet">
										<dsp:oparam name="output">
											<dsp:getvalueof var="seoUrl" param="seoURL" scope="request" />
										</dsp:oparam>
									</dsp:droplet>
				
									<dsp:droplet name="BBBDefaultSortOptionsDroplet">
										<dsp:oparam name="output">
											<dsp:getvalueof var="sortOptions" param="sortOptions" scope="request"/>
										</dsp:oparam>
									</dsp:droplet>
									<dsp:droplet name="SearchDroplet">
										<dsp:param name="checklistCategoryId" param="checklistCategoryId"/>
										<dsp:param name="checklistId" param="checklistId"/>
										<dsp:param name="bccSortCode" value="${sortOptions.defaultSortingOption.sortUrlParam}"/>
										<dsp:param name="bccSortOrder" value="${sortOptions.defaultSortingOption.ascending}"/>
										<dsp:param name="isFromChecklistCategory" value="${isFromChecklistCategory}"/>
										<dsp:oparam name="output">
										<dsp:include page="checklistSubCategory_container.jsp" flush="true">
											<dsp:param name="currentPage" param="browseSearchVO.pagingLinks.currentPage"/>
											<dsp:param name="pageCount" param="browseSearchVO.pagingLinks.pageCount" />
											<dsp:param name="browseSearchVO" param="browseSearchVO" />
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
										   <dsp:getvalueof	var="enteredNarrowDown" param="enteredNarrowDown" scope="request" />
										 <dsp:include page="/search/no_search_results.jsp?_dyncharset=UTF-8&" flush="true"  />
										</dsp:oparam>
										<dsp:oparam name="error">
											<dsp:include page="../../global/serverError.jsp" flush="true"/>
										</dsp:oparam>
									  	<dsp:oparam name="error_PageNumOutOfBound">
									        <dsp:include page="../404.jsp" flush="true"/>
								        </dsp:oparam> 
									</dsp:droplet>
				</c:otherwise>
			</c:choose>
		</dsp:oparam>
	</dsp:droplet>
	
</dsp:page>