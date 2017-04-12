<dsp:getvalueof param="currentResultSize" var="currentResultSize" />
<dsp:getvalueof param="totalCount" var="totalCount" />
<dsp:getvalueof param="perPage" var="perPage" />
<dsp:getvalueof param="totalTab" var="totalTab" />

<div class="row result-pagination">

	<fmt:parseNumber var="total" integerOnly="true" type="number" value="${totalTab}" />

	<c:set var="lowerRange" value="${1 + (perPage * (pagNum - 1 ))}"/>
	<c:set var="upperRange" value="${perPage * pagNum}"/>

	<c:choose>
		<c:when test="${totalCount > perPage}">
			<c:if test="${pagNum eq total}">
				<c:set var="upperRange" value="${totalCount}"/>
			</c:if>
			<c:set var="currentPagRange" value="${lowerRange} - ${upperRange}" />
		</c:when>
		<c:otherwise>
			<c:set var="currentPagRange" value="${totalCount}" />
		</c:otherwise>
	</c:choose>

	<div class="small-12 large-6 columns">
		<h3 class="show-for-medium-down">${currentPagRange} <span class="subheader">of ${totalCount} Registries</span></h3>
		<h3 class="show-for-large-up"><bbbl:label key='lbl_regcreate_showing' language="${pageContext.request.locale.language}" /> ${currentPagRange} <span class="subheader">of ${totalCount} Registries</span></h3>
	</div>
	<div class="small-12 large-6 columns pagination">
		<div class="grid-pagination">
			<ul class="inline-list right">
				<li>
					<a href="#" class="button left secondary"><span></span></a>
				</li>
				<li class="current-page-drop"><a class="small download button dropdown" href="#" data-dropdown="currentPage">1<span>&nbsp;</span></a>
						<ul id="currentPage" class="f-dropdown current-page" name="currentPage" data-dropdown-content="">
							<li><a href="#">2</a></li>
							<li><a href="#">3</a></li>
							<li><a href="#">4</a></li>
						</ul>
				</li>
				<li>
					<a class="button right secondary" href="#"><span></span></a>
				</li>
			</ul>
		</div>
		<h3><bbbl:label key='lbl_regsrchguest_perpage' language="${pageContext.request.locale.language}" /> <span class="subheader">${perPage}</span></h3>
	</div>

	<%-- TODO: do we even need a filter drop down? can't find it in the comps --%>
	<%--
	<select class="filterOptions" id="pagFilterOpt" name="pagFilterOpt" aria-required="false" aria-labelledby="pagFilterOpt errorpagFilterOpt" >
		<c:choose>
			<c:when test="${perPage eq 24}">
				<option value="24">24</option>
				<option value="48">48</option>
				<option value="96">96</option>
			</c:when>
			<c:when test="${perPage eq 48}">
				<option value="24">24</option>
				<option selected="selected" value="48" >48</option>
				<option value="96">96</option>
			</c:when>
			<c:when test="${perPage eq 96}">
				<option value="24">24</option>
				<option value="48">48</option>
				<option selected="selected" value="96">96</option>
			</c:when>
		</c:choose>
	</select>
	--%>

	<%-- TODO: build pagination controls --%>
	<%--
	<c:if test="${fn:substringBefore(totalTab, '.') >= 2}">
		<c:choose>
			<c:when test="${pagNum eq 1}">
				<a title="1" href="#1">${pagNum}</a>

				<c:if test="${fn:substringBefore(totalTab, '.') >= 2}">
					<a title="2" href="#2">2</a>
				</c:if>
				<c:if test="${fn:substringBefore(totalTab, '.') >= 3}">
					<a title="3" href="#3">3</a>
				</c:if>
				<c:if test="${fn:substringBefore(totalTab, '.') > 3}">
					<a title="${fn:substringBefore(totalTab, '.')}"  href="#${fn:substringBefore(totalTab, '.')}">
						${fn:substringBefore(totalTab, '.')}
					</a>
					<a title="Next Page" href="#<c:out value="${pagNum+1}"/>" class="pagArrow">&gt;</a>
				</c:if>
				<c:if test="${fn:substringBefore(totalTab, '.') == 2}">
					<a title="Next Page" href="#<c:out value="${pagNum+1}"/>" class="pagArrow">&gt;</a>
				</c:if>
			</c:when>
			<c:when test="${fn:substringBefore(totalTab, '.') eq pagNum}">
				<a title="Previous Page" href="#<c:out value="${pagNum-1}"/>"
					class="pagArrow">&lt;</a>
				<a title="1" href="#1">1</a>
				<c:if test="${fn:substringBefore(totalTab, '.') == 2}">
					<a title="2" href="#2">2</a>
				</c:if>
				<c:if test="${fn:substringBefore(totalTab, '.') == 3}">
					<a title="<c:out value="${pagNum-1}"/>" href="#<c:out value="${pagNum-1}"/>" /><c:out value="${pagNum-1}"/></a>
					<a title="${pagNum}" href="#${pagNum}"><c:out value="${pagNum}" /></a>
				</c:if>
				<c:if test="${fn:substringBefore(totalTab, '.') == 4}">
					<a title="<c:out value="${pagNum-2}"/>" href="#<c:out value="${pagNum-2}"/>" /><c:out value="${pagNum-2}"/></a>
					<a title="<c:out value="${pagNum-1}"/>" href="#<c:out value="${pagNum-1}"/>" /><c:out value="${pagNum-1}"/></a>
					<a title="${pagNum}" href="#${pagNum}"><c:out value="${pagNum}" /></a>
				</c:if>
				<c:if test="${fn:substringBefore(totalTab, '.') >= 5}">
					<a title="<c:out value="${pagNum-2}"/>" href="#<c:out value="${pagNum-2}"/>" /><c:out value="${pagNum-2}"/></a>
					<a title="<c:out value="${pagNum-1}"/>" href="#<c:out value="${pagNum-1}"/>" /><c:out value="${pagNum-1}"/></a>
					<a title="${pagNum}" href="#${pagNum}"><c:out value="${pagNum}" /></a>
				</c:if>
			</c:when>
			<c:otherwise>
				<a title="Previous Page" href="#${pagNum-1}" class="pagArrow">&lt;</a>
				<c:if test="${pagNum > 2}">
					<a title="1" href="#1">1</a>
					<c:if test="${pagNum ne 3}">
						<li class="ellips">...</li>
					</c:if>
				</c:if>
				<a title="<c:out value="${pagNum-1}"/>" href="#${pagNum-1}"><c:out value="${pagNum-1}" /></a>
				<a title="${pagNum}" href="#${pagNum}"><c:out value="${pagNum}" /></a>
				<a title="<c:out value="${pagNum+1}"/>" href="#${pagNum+1}"><c:out value="${pagNum+1}" /></a>
				<c:if test="${pagNum <= fn:substringBefore(totalTab, '.') - 2}">
					<a title="${fn:substringBefore(totalTab, '.')}" href="#${fn:substringBefore(totalTab, '.')}"><c:out value="${fn:substringBefore(totalTab, '.')}" /></a>
				</c:if>
				<a title="Next Page" href="#<c:out value="${pagNum+1}"/>" class="pagArrow">&gt;</a>
			</c:otherwise>
		</c:choose>
	</c:if>
	--%>

</div>
