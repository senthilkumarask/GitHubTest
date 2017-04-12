 
	<div class="pagination">
		<form action="#" method="post" id="frmPagination">
		<dsp:getvalueof var="contentType" param="contentType" />
			<fieldset class="pagControls clearfix">
				<div class="pagSort">
					<ul class="pagLevel1UL">
						<li class="listCount">
							<strong><bbbl:label key="lbl_pagination_header_1" language="${pageContext.request.locale.language}"/> ${guideReturnListSize}</strong>&nbsp;<span>of ${lstGuidesTemplateSize} <bbbl:label key="lbl_products_lowercase_dsk" language="${pageContext.request.locale.language}"/></span>
							<h3 class="txtOffScreen">
								<bbbl:label key="lbl_pagination_header_1" language="${pageContext.request.locale.language}"/> ${guideReturnListSize}&nbsp;of ${lstGuidesTemplateSize} <bbbl:label key="lbl_products_lowercase_dsk" language="${pageContext.request.locale.language}"/>
							</h3>
						</li>
					</ul>
				</div>
				<div class="pagFilter">
					<ul class="pagLevel1UL">
						<li class="lnkAllItems"><a class="redirPage" href="guides_advice.jsp?seeAll=true&contentType=${contentType}" title="See all"><bbbl:label key="lbl_regsrchguest_seeall" language="${pageContext.request.locale.language}"/></a></li>
						<li class="listFilter">
							<strong><bbbl:label key="lbl_regsrchguest_perpage" language="${pageContext.request.locale.language}"/></strong>&nbsp;
							<select class="filterOptions" id="pagFilterOpt" name="pagFilterOpt" aria-required="false" aria-labelledby="pagFilterOpt" >
								 <dsp:droplet name="/atg/dynamo/droplet/ForEach">
									  <dsp:param param="lstDropDown" name="array"/>
									  <dsp:getvalueof var="count" param="element" />
										<dsp:oparam name="output">	
											   <c:choose>
													<c:when test="${perPage eq count}">
														<option selected="selected" value="${count}">${count}</option>
													</c:when>
													<c:otherwise>
														<option value="${count}" >${count}</option>
													</c:otherwise>
												</c:choose>
									   </dsp:oparam>
								 </dsp:droplet>
							</select>
						</li>
						<li class="listPageNumbers guidesAdvicePagenum">
							<ul>
								<c:choose>
									<c:when test="${pagNum eq 1}">
										<li class="active"><a title="1" href="#1">${pagNum}</a>
										</li>
										<c:if test="${totalTab >= '2'}">
											<li><a title="2" href="#2">2</a></li>
										</c:if>
										<c:if test="${totalTab == '3'}">
											<li><a title="3" href="#3">3</a></li>
										</c:if>
										<c:if test="${totalTab > '3'}">
											<li class="ellips">...</li>
											<li>
												<a title="${totalTab}" href="#${totalTab}">${totalTab}</a>
											</li>
										</c:if>
										<li class="lnkNextPage arrow">
											<a title="Next Page" href="#2" class="pagArrow">&gt;</a>
										</li>
									</c:when>
									<c:when test="${totalTab eq pagNum and totalTab > 5}">										
										<li class="lnkPrevPage arrow">
											<a title="Previous Page" href="#${totalTab-1}" class="pagArrow">&lt;</a>
										</li>
										<li><a title="1" href="#1">1</a>
										</li>
										<li class="ellips">...</li>
										<li>
											<a title="${pagNum-2}" href="#${currentPage-2}"><c:out value="${pagNum-2}" /></a>
										</li>
										<li>
											<a title="${pagNum-1}" href="#${currentPage-1}"><c:out value="${pagNum-1}" /></a>
										</li>
										<li class="active">
											<a title="${pagNum}" href="#${pagNum}"><c:out value="${pagNum}" /></a>
										</li>
									</c:when>
									<c:otherwise>
										<li class="lnkPrevPage arrow">
											<a title="Previous Page" href="#${pagNum-1}" class="pagArrow">&lt;</a>
										</li>
										<c:if test="${pagNum > '2'}">
											<li>
												<a title="1" href="#1">1</a>
											</li>
											<c:if test="${pagNum ne '3'}">
												<li class="ellips">...</li>
											</c:if>
										</c:if>
										<li>
											<a title="${pagNum-1}" href="#${pagNum-1}"><c:out value="${pagNum-1}" /></a>
										</li>
										<li class="active">
											<a title="${pagNum}" href="#${currentPage}"><c:out value="${pagNum}" /></a>
										</li>
										<c:if test="${totalTab >= (pagNum+1)}">
										<li>
											<a title="${pagNum+1}" href="#${pagNum+1}"><c:out value="${pagNum+1}" /></a>
										</li>
										<c:if test="${totalTab == pagNum+2 }">
											<li>
												<a title="${totalTab}" href="#${totalTab}"><c:out value="${totalTab}" /></a>
											</li>
										</c:if>
										<c:if test="${totalTab > pagNum+2 }">
											<li class="ellips">...</li>
											<li>
												<a title="${totalTab}" href="#${totalTab}"><c:out value="${totalTab}" /></a>
											</li>
										</c:if>
										<li class="lnkNextPage arrow">
											<a title="Next Page" href="#${pagNum+1}" class="pagArrow">&gt;</a>
										</li>
										</c:if>
									</c:otherwise>
								</c:choose>
							</ul>
						</li>
						<%--  <li class="listPageNumbers">
							<ul>
								<li class="lnkPrevPage arrow"><a href="javascript:void(0);" onclick="javascript:previousPage();" title="Previous Page">&lt;</a></li>
								<c:forEach var="countTab" begin="1" end="${totalTab}" step="1">
									<c:choose>
									<c:when test="${pagNum == countTab}">
									<li class="active"><a href="#${countTab}" title="${countTab}">${countTab}</a></li>
									</c:when>
									<c:otherwise>
									<li><a href="#${countTab}" title="${countTab}">${countTab}</a></li>
									</c:otherwise>
									</c:choose>
								</c:forEach>
								<li class="lnkNextPage arrow"><a href="javascript:void(0);" onclick="javascript:nextPage();" title="Next Page">&gt;</a></li>
							</ul>
						</li>--%>
					</ul>
				</div>
			</fieldset>
			<input type="submit" class="hidden" value="" />
		</form>
	</div>
 
