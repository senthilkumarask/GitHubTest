<dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI" />
<div class="pagination small-12 columns">
 <fmt:parseNumber var="total" integerOnly="true" type="number" value="${totalTab}" />
 <div class="pagControls">
  <div class="pagSort small-12 medium-6 columns">
   <c:set var="lowerRange" value="${1 + (perPage * (pagNum - 1 ))}" />
   <c:set var="upperRange" value="${perPage * pagNum}" />
   <div class="pagLevel1UL">
    <div class="listCount">
     <strong><bbbl:label key='lbl_regcreate_showing' language="${pageContext.request.locale.language}" /> <c:choose>
       <c:when test="${totalCount > perPage}">
        <c:if test="${pagNum eq total}">
         <c:set var="upperRange" value="${totalCount}" />
        </c:if>
   		${lowerRange} - ${upperRange}
   	   </c:when>
       <c:otherwise>
        <c:out value="${totalCount}" />
       </c:otherwise>
      </c:choose> 
      </strong> <span>of ${totalCount} registries</span>
    </div>
   </div>
  </div>
  </div>
  <div class="pagFilter small-6 medium-4 columns">
   <ul class="pagLevel1UL inline-list right">
    <li><h3>
      <bbbl:label key="lbl_regsrchguest_perpage" language="${pageContext.request.locale.language}" />
     </h3></li>
    <li class="listFilter"><a class="small download radius button dropdown" data-dropdown="pagFilterOpt" href="#">${perPage}<span>&nbsp;</span>
    </a>
     <ul class="filterOptions f-dropdown current-page" data-dropdown-content="" id="pagFilterOpt" name="pagFilterOpt" aria-required="false" aria-labelledby="pagFilterOpt errorpagFilterOpt">
      <c:choose>
       <c:when test="${perPage eq 24}">
        <li value="24" class="selected"><a href="${requestURI}?pagFilterOpt=${perPage}&pagNum=${pagNum}">24</a></li>
        <li value="48"><a href="${requestURI}?pagFilterOpt=48&pagNum=${pagNum}">48</a></li>
        <li value="96"><a href="${requestURI}?pagFilterOpt=96&pagNum=${pagNum}">96</a></li>
       </c:when>
       <c:when test="${perPage eq 48}">
        <li value="24"><a href="${requestURI}?pagFilterOpt=24&pagNum=${pagNum}">24</a></li>
        <li class="selected" value="48"><a href="${requestURI}?pagFilterOpt=${perPage}&pagNum=${pagNum}">48</a></li>
        <li value="96"><a href="${requestURI}?pagFilterOpt=96&pagNum=${pagNum}">96</a></li>
       </c:when>
       <c:when test="${perPage eq 96}">
        <li value="24"><a href="${requestURI}?pagFilterOpt=24&pagNum=${pagNum}">24</a>
         </li>
        <li value="48"><a href="${requestURI}?pagFilterOpt=48&pagNum=${pagNum}">48</a>
         </li>
        <li class="selected" value="96"><a href="${requestURI}?pagFilterOpt=${perPage}&pagNum=${pagNum}">96</a></li>
       </c:when>
      </c:choose>
     </ul></li>
   </ul>
   </div>
  <div class="grid-pagination"> 
    <ul class="inline-list right">
     <c:if test="${fn:substringBefore(totalTab, '.') >= 2}">
     <li><a title="Previous Page" href="${requestURI}?pagFilterOpt=${perPage}&pagNum=<c:out value='${pagNum-1}'/>" class="button left secondary <c:if test='${pagNum == 1}'>disabled</c:if>"><span></span></a></li>
     <li class="current-page-drop">
     	 <a class="small download button dropdown open" data-dropdown="currentPage" href="#" aria-expanded="true">${pagNum}<span>&nbsp;</span></a> 
       <ul class="f-dropdown current-page" data-dropdown-content="" id="currentPage" name="currentPage">
        <%-- <c:choose>
         <c:when test="${pagNum eq 1}">
          <li class="active"><a title="1" href="#1">${pagNum}</a></li>
          <c:if test="${fn:substringBefore(totalTab, '.') >= 2}">
           <li><a title="2" href="#2">2</a></li>
          </c:if>
          <c:if test="${fn:substringBefore(totalTab, '.') >= 3}">
           <li><a title="3" href="#3">3</a></li>
          </c:if>
          <c:if test="${fn:substringBefore(totalTab, '.') > 3}">
           <li><a title="${fn:substringBefore(totalTab, '.')}" href="#${fn:substringBefore(totalTab, '.')}"> ${fn:substringBefore(totalTab, '.')} </a></li>
           <li class="lnkNextPage arrow"><a title="Next Page" href="#<c:out value="${pagNum+1}"/>" class="button right secondary"><span></span></a></li>
          </c:if>
          <c:if test="${fn:substringBefore(totalTab, '.') == 2}">
           <li class="lnkNextPage arrow"><a title="Next Page" href="#<c:out value="${pagNum+1}"/>" class="button right secondary"><span></span></a></li>
          </c:if>
         </c:when>
         <c:when test="${fn:substringBefore(totalTab, '.') eq pagNum}">
          <li class="lnkPrevPage arrow"><a title="Previous Page" href="#<c:out value="${pagNum-1}"/>" class="button left secondary"><span></span></a></li>
          <li><a title="1" href="#1">1</a></li>
          <c:if test="${fn:substringBefore(totalTab, '.') == 2}">
           <li class="active"><a title="2" href="#2">2</a></li>
          </c:if>
          <c:if test="${fn:substringBefore(totalTab, '.') == 3}">
           <li><a title="<c:out value="${pagNum-1}"/>" href="#<c:out value="${pagNum-1}"/>" />
           <c:out value="${pagNum-1}" /></a></li>
           <li class="active"><a title="${pagNum}" href="#${pagNum}"><c:out value="${pagNum}" /></a></li>
          </c:if>
          <c:if test="${fn:substringBefore(totalTab, '.') == 4}">
           <li><a title="<c:out value="${pagNum-2}"/>" href="#<c:out value="${pagNum-2}"/>" />
           <c:out value="${pagNum-2}" /></a></li>
           <li><a title="<c:out value="${pagNum-1}"/>" href="#<c:out value="${pagNum-1}"/>" />
           <c:out value="${pagNum-1}" /></a></li>
           <li class="active"><a title="${pagNum}" href="#${pagNum}"><c:out value="${pagNum}" /></a></li>
          </c:if>
          <c:if test="${fn:substringBefore(totalTab, '.') >= 5}">
           <li><a title="<c:out value="${pagNum-2}"/>" href="#<c:out value="${pagNum-2}"/>" />
           <c:out value="${pagNum-2}" /></a></li>
           <li><a title="<c:out value="${pagNum-1}"/>" href="#<c:out value="${pagNum-1}"/>" />
           <c:out value="${pagNum-1}" /></a></li>
           <li class="active"><a title="${pagNum}" href="#${pagNum}"><c:out value="${pagNum}" /></a></li>
          </c:if>
         </c:when>
         <c:otherwise> --%>
          <%-- <c:if test="${pagNum > 2}">
           <li><a title="1" href="#1">1</a></li>
          </c:if> --%>
    <%--       <li><a title="<c:out value="${pagNum-1}"/>" href="#${pagNum-1}"><c:out value="${pagNum-1}" /></a></li>
          <li class="active"><a title="${pagNum}" href="#${pagNum}"><c:out value="${pagNum}" /></a></li>
          <li><a title="<c:out value="${pagNum+1}"/>" href="#${pagNum+1}"><c:out value="${pagNum+1}" /></a></li> --%>
          <%-- <c:if test="${pagNum <= fn:substringBefore(totalTab, '.') - 2}">
           <li><a title="${fn:substringBefore(totalTab, '.')}" href="#${fn:substringBefore(totalTab, '.')}"><c:out value="${fn:substringBefore(totalTab, '.')}" /></a></li>
          </c:if> --%>
          <c:set var="totalTabNew" value="${fn:substringBefore(totalTab, '.')}"/>
          <c:forEach var="pageNum" begin="1" end="${totalTabNew}">
          	<c:choose>
          		<c:when test="${pageNum eq pagNum }">
          			<li class="selected"><a title="${pageNum}" href="${requestURI}?pagFilterOpt=${perPage}&pagNum=${pageNum}"><c:out value="${pageNum}"/></a></li>
          		</c:when>
          		<c:otherwise>
          			<li><a title="<c:out value='${pageNum}'/>" href="${requestURI}?pagFilterOpt=${perPage}&pagNum=${pageNum}"><c:out value="${pageNum}"/></a></li>
          		</c:otherwise>
          	</c:choose>
		  </c:forEach>
       </ul>
      </li>
       <li><a title="Next Page" href="${requestURI}?pagFilterOpt=${perPage}&pagNum=<c:out value='${pagNum+1}'/>" class="button right secondary <c:if test='${pagNum == totalTabNew}'>disabled</c:if>"><span></span></a></li>
     </c:if>
    </ul>
   </div>
 </div>
