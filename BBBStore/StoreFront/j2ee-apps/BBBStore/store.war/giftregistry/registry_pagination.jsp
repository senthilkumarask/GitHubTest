
	<div class="pagination">
			<fmt:parseNumber var="total" integerOnly="true" type="number" value="${totalTab}" />	
			<fieldset class="pagControls clearfix">
				<div class="pagSort">
					<c:set var="lowerRange" value="${1 + (perPage * (pagNum - 1 ))}"/>
					<c:set var="upperRange" value="${perPage * pagNum}"/>
					<ul class="pagLevel1UL">
						<li class="listCount">
							<strong><bbbl:label key='lbl_regcreate_showing' language="${pageContext.request.locale.language}" /> 
								<c:choose>
									<c:when test="${totalCount > perPage}">
										<c:if test="${pagNum eq total}">
											<c:set var="upperRange" value="${totalCount}"/>
										</c:if>
										${lowerRange} - ${upperRange}
									</c:when>
									<c:otherwise>
										<c:out value="${totalCount}" />
									</c:otherwise>
								</c:choose>
							</strong>
							<span>of ${totalCount} registries</span>
							<h3 class="txtOffScreen">
								<bbbl:label key='lbl_regcreate_showing' language="${pageContext.request.locale.language}" /> 
								<c:choose>
									<c:when test="${totalCount > perPage}">
										<c:if test="${pagNum eq total}">
											<c:set var="upperRange" value="${totalCount}"/>
										</c:if>
										${lowerRange} - ${upperRange}
									</c:when>
									<c:otherwise>
										<c:out value="${totalCount}" />
									</c:otherwise>
								</c:choose>
							</h3>
						</li>
					</ul> 
				</div> 
				<div class="pagFilter">
					<ul class="pagLevel1UL">  
						<li class="listFilter"> 
							<strong><bbbl:label key='lbl_regsrchguest_perpage' language="${pageContext.request.locale.language}" /></strong>&nbsp;
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
						</li>
						
						<c:if test="${fn:substringBefore(totalTab, '.') >= 2}">
						<li class="listPageNumbers">
							<ul title="pagNum">
								
							<c:choose>
							 <c:when test="${pagNum eq 1}"> 
									<li class="active"><a title="1" href="#1">${pagNum}</a>
									</li> 
									<c:if test="${fn:substringBefore(totalTab, '.') >= 2}">
										<li><a title="2" href="#2">2</a></li>
									</c:if>
									<c:if test="${fn:substringBefore(totalTab, '.') >= 3}">
										<li><a title="3" href="#3">3</a></li> 
									</c:if>
									<c:if test="${fn:substringBefore(totalTab, '.') > 3}">
										<li class="ellips">...</li>
										<li>
											<a title="${fn:substringBefore(totalTab, '.')}"  href="#${fn:substringBefore(totalTab, '.')}">
												${fn:substringBefore(totalTab, '.')}
											</a>
										</li>
										<li class="lnkNextPage arrow"> 
											<a title="Next Page" href="#<c:out value="${pagNum+1}"/>"
											class="pagArrow">&gt;</a>
										</li>
									</c:if>
									
									<c:if test="${fn:substringBefore(totalTab, '.') == 2}">
									<li class="lnkNextPage arrow"> 
											<a title="Next Page" href="#<c:out value="${pagNum+1}"/>"
											class="pagArrow">&gt;</a>
										</li>
									</c:if>
									
								</c:when>
					
							
							<c:when test="${fn:substringBefore(totalTab, '.') eq pagNum}">
											<li class="lnkPrevPage arrow"><a title="Previous Page" href="#<c:out value="${pagNum-1}"/>"
												class="pagArrow">&lt;</a></li>
											<li><a title="1" href="#1">1</a>
											</li>
											
											<c:if test="${fn:substringBefore(totalTab, '.') == 2}">
											<li class="active"><a title="2" href="#2">2</a>
											</li>
											</c:if>
											
											<c:if test="${fn:substringBefore(totalTab, '.') == 3}">
											<li><a title="<c:out value="${pagNum-1}"/>" 
												href="#<c:out value="${pagNum-1}"/>" /><c:out value="${pagNum-1}"/></a></li> 


											<li class="active"><a title="${pagNum}"
												href="#${pagNum}"><c:out
														value="${pagNum}" /></a></li>
														
											</c:if>
											
											<c:if test="${fn:substringBefore(totalTab, '.') == 4}">
											<li><a title="<c:out value="${pagNum-2}"/>"  
												href="#<c:out value="${pagNum-2}"/>" /><c:out value="${pagNum-2}"/></a></li>

											<li><a title="<c:out value="${pagNum-1}"/>" 
												href="#<c:out value="${pagNum-1}"/>" /><c:out value="${pagNum-1}"/></a></li> 


											<li class="active"><a title="${pagNum}"
												href="#${pagNum}"><c:out
														value="${pagNum}" /></a></li>
														
											</c:if>
											
											<c:if test="${fn:substringBefore(totalTab, '.') >= 5}">
											<li class="ellips">...</li>

											<li><a title="<c:out value="${pagNum-2}"/>"  
												href="#<c:out value="${pagNum-2}"/>" /><c:out value="${pagNum-2}"/></a></li>

											<li><a title="<c:out value="${pagNum-1}"/>" 
												href="#<c:out value="${pagNum-1}"/>" /><c:out value="${pagNum-1}"/></a></li> 


											<li class="active"><a title="${pagNum}"
												href="#${pagNum}"><c:out
														value="${pagNum}" /></a></li>
														
											</c:if>
														
														
							</c:when>
							
							<c:otherwise>
								<li class="lnkPrevPage arrow"><a title="Previous Page"
									href="#${pagNum-1}"
									class="pagArrow">&lt;</a></li>
								<c:if test="${pagNum > 2}">
									<li><a title="1"
										href="#1">1</a>
									</li>
									<c:if test="${pagNum ne 3}">
										<li class="ellips">...</li>
									</c:if>
								</c:if>
								<li><a title="<c:out value="${pagNum-1}"/>"
									href="#${pagNum-1}"><c:out
											value="${pagNum-1}" /></a></li>

								<li class="active"><a title="${pagNum}"
									href="#${pagNum}"><c:out
											value="${pagNum}" /></a></li>

								<li><a title="<c:out value="${pagNum+1}"/>"
									href="#${pagNum+1}"><c:out
											value="${pagNum+1}" /></a></li>

								<c:if test="${pagNum <= fn:substringBefore(totalTab, '.') - 2}">
									<li class="ellips">...</li>
									<li ><a title="${fn:substringBefore(totalTab, '.')}"
										href="#${fn:substringBefore(totalTab, '.')}"><c:out
												value="${fn:substringBefore(totalTab, '.')}" /></a></li>
								</c:if> 
								 
								<li class="lnkNextPage arrow"><a title="Next Page"
									href="#<c:out value="${pagNum+1}"/>" 
									class="pagArrow">&gt;</a></li>
							</c:otherwise>
							 
								</c:choose> 
								
								
							</ul>
						</li>
						
						</c:if>
						
						</ul>
				</div>
			</fieldset>
		
	</div> 
