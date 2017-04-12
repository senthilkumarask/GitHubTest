<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/CollegeLookup" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/StatesLookup" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:getvalueof var="stateIdUrl" param="stateId" />
	<c:set var="section" value="browse" scope="request" />
	<c:set var="pageWrapper" value="college collegiateMerchandise"
		scope="request" />
	<c:set var="titleString"
		value="Bed Bath &amp; Beyond - College - Shop By College"
		scope="request" />
	<c:set var="pageVariation" value="bc" scope="request" />
	<c:set var="ViewMore"><bbbl:label key="lbl_college_merchandise_viewMore" language="<c:out param='${pageContext.request.locale.language}'/>"/></c:set>
	<c:set var="ViewLess"><bbbl:label key="lbl_college_merchandise_viewLess" language="<c:out param='${pageContext.request.locale.language}'/>"/></c:set>
	<bbb:pageContainer>
		<dsp:droplet name="CollegeLookup">
			<dsp:oparam name="output">
				<div id="content" class="container_12 clearfix" role="main">
					<div class="grid_12 clearfix">
						<bbbt:textArea key="txt_colleges_landing_title"
							language="${pageContext.request.locale.language}" />
					</div>
					<div class="grid_12 clearfix stateAlphabetRow">
						<div class="grid_2 alpha clearfix">
							
							<dsp:droplet name="StatesLookup">
								<dsp:oparam name="output">
									<form name="frmCollegeMerchandise"
										action="collegiate_merchandise.jsp" method="get"
										id="frmCollegeMerchandise">
										<div class="inputField stateListBBB">
											<select name="stateId" id="collegeStateName" class="uniform" aria-required="false" aria-labelledby="collegeStateName errorcollegeStateName">
												<option value="0" selected="selected">
													<bbbl:label key="lbl_colleges_landing_state"
														language="${pageContext.request.locale.language}" />
												</option>
												<dsp:droplet name="ForEach">
													<dsp:param name="array" param="listOfStates" />
													<dsp:oparam name="output">
														<dsp:getvalueof var="stateId" param="element.stateCode" />
														<c:choose>
															<c:when test="${stateIdUrl eq stateId}">
																<option value="${stateId}" selected="selected">
																	<dsp:valueof param="element.stateName" />
																</option>
															</c:when>
															<c:otherwise>
																<option value="${stateId}">
																	<dsp:valueof param="element.stateName" />
																</option>
															</c:otherwise>
														</c:choose>
													</dsp:oparam>
												</dsp:droplet>
											</select>
										</div>
									</form>
								</dsp:oparam>
							</dsp:droplet>
						</div>
						<div class="grid_10 omega clearfix">
							<bbbt:textArea key="txt_colleges_landing_alpha_section"
								language="${pageContext.request.locale.language}" />
							<dsp:getvalueof var="collegesCount" param="collegesCount"></dsp:getvalueof>
							<c:set var="collegeMaxCount">
								<bbbc:config key="collegeMaxCount"
									configName="ContentCatalogKeys" />
							</c:set>
							<c:choose>
								<c:when test="${collegesCount lt collegeMaxCount }">
									<div class="alphabeticalSortAll">
										<dsp:getvalueof var="alphabetList"
											bean="CollegeLookup.alphabetList" />
										<dsp:tomap param="collegesMap" var="collegesMap" />
										<dsp:droplet name="ForEach">
											<dsp:param name="array" value="${alphabetList}" />
											<dsp:oparam name="output">
												<dsp:getvalueof var="alphaBetValue" param="element" />
												<dsp:contains var="isPresent" values="${collegesMap}"
													object='${alphaBetValue}' />
												<c:choose>
													<c:when test="${isPresent}">
														<a href="#collegeChar${alphaBetValue}">${alphaBetValue}</a>
													</c:when>
													<c:otherwise>
                                                        <span class="inactive">${alphaBetValue}</span>
											</c:otherwise>
												</c:choose>
											</dsp:oparam>
										</dsp:droplet>
									</div>
								</c:when>
								<c:otherwise>
									<div class="alphabeticalSortGroup">
										<dsp:getvalueof var="collegeGroupList"
											bean="CollegeLookup.collegeGroupList" />
										<dsp:getvalueof var="CollegeNameGrp" param="CollegeNameGrp" />					
										<dsp:droplet name="ForEach">
											<dsp:param name="array" value="${collegeGroupList}" />
											<dsp:oparam name="output">
											<dsp:getvalueof var="size" param="size" scope="request"/>
											<c:set var="count" scope="request"><dsp:valueof param="count"/></c:set>											
												<dsp:getvalueof var="alphaBetGroupValue" param="element" />
												<dsp:contains var="isPresent" values="${CollegeNameGrp}"
													object='${alphaBetGroupValue}' />
												<c:choose>
													<c:when test="${isPresent}">
														<c:set var="continue" value="true" />
														<dsp:droplet name="ForEach">
															<dsp:param name="array" param="collegesMap" />

															<dsp:oparam name="output">
																<c:if test="${continue==true}">
																	<dsp:getvalueof var="key" param="key" />
																	
																	<dsp:getvalueof var="length"
																		value="${fn:length(alphaBetGroupValue)}" />
																	<dsp:getvalueof var="first"
																		value="${fn:substring(alphaBetGroupValue, 0, 1)}" />
																	<dsp:getvalueof var="last"
																		value="${fn:substring(alphaBetGroupValue, length-1, length)}" />

																	<c:choose>
																	
																		<c:when test="${key eq first}">
																		<c:set var="continue" value="false" />
				
																			<c:set var="linkKey">${key}</c:set>
																		</c:when>
																		<c:when test="${key eq last}">
																			<c:set var="linkKey">${key}</c:set>
																		<c:set var="continue" value="false" />	
																		</c:when>
																		
																		<c:when test="${key gt first and key lt last}">
																		<c:set var="continue" value="false" />
																			
																			<c:set var="linkKey">${key}</c:set>
																		</c:when>
																	</c:choose>
																</c:if>
															</dsp:oparam>
														</dsp:droplet>


														<a href="#collegeChar${linkKey}">
															${alphaBetGroupValue}</a>
															<c:if test="${count < size}">
																<span>|</span>
															</c:if>
													</c:when>
													<c:otherwise>
												${alphaBetGroupValue} 
												<c:if test="${count < size}">
												<span>|</span>
												</c:if>												
											</c:otherwise>
												</c:choose>												
											</dsp:oparam>
										</dsp:droplet>
									</div>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="collegesMap" />
						<dsp:oparam name="output">
							<dsp:param name="listofColleges" param="element" />
							<div class="grid_12 clearfix collegeProductRow">
									
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="listofColleges" />
											<dsp:oparam name="outputStart">
												<dsp:getvalueof var="size" param="size" />
														
														<c:choose>
														<c:when test="${size le 5  }">
														<c:set var="collegeClass">singleProdRowAlpha 
														</c:set>
														</c:when> 
														<c:otherwise>
														<c:set var="collegeClass">doubleProdRowAlpha
														</c:set>
														</c:otherwise>
														</c:choose>
												<div class="grid_2 alpha clearfix ${collegeClass}" id="collegeChar<dsp:valueof param="key"/>">
															<div class="alphabetSingle"  name="collegeChar<dsp:valueof param="key"/>">
																<dsp:valueof param="key" />
															</div>
														</div>
														<div class="grid_10 omega clearfix">
																
											</dsp:oparam>
											<dsp:oparam name="output">
												<dsp:getvalueof var="count" param="count"/>
												<c:set var="collegeListULClass" value="" />
												
												<c:choose>
													<c:when test="${count == 1 || count mod 5 == 1}">
														<ul class="prodRow clearfix">
														<c:set var="collegeListULClass" value=" alpha" />
													</c:when>
													<c:when test="${count mod 5 == 0}">
														<c:set var="collegeListULClass" value=" omega" />
													</c:when>
												</c:choose>
												
												<c:set var="keyword"><dsp:valueof valueishtml="true" param="element.collegeName" /></c:set>
												<c:url var="url"
													value="/search/search.jsp?view=grid&_dyncharset=UTF-8&fromCollege=true">
													<c:param name="Keyword" value="${keyword}" />
												</c:url>
													
												<li class="grid_2${collegeListULClass}">
													<a href="${url}"title="<dsp:valueof param="element.collegeName"/>">
														<div class="collegeImgHolder">
															<img src="<dsp:valueof param="element.collegeLogo"/>" alt="<dsp:valueof param="element.collegeName"/>" width="69" height="58" />
														</div>
														<div class="collegeName">
															<dsp:valueof param="element.collegeName" />
														</div>
													</a>
												</li>
												
												<c:if test="${count mod 5 eq 0}">
													</ul>
												</c:if>
											
											</dsp:oparam>
											<dsp:oparam name="outputEnd">
												<dsp:getvalueof var="size" param="size" />
												<c:if test="${size <= 5 || size mod 5 ne 0}">
													</ul>
												</c:if>
												<c:if test="${size > 10}">
												<div class="expandCollapseCollegeList">
												  <a href="javascript:void(0);" class="expandCollegeList" title="${ViewMore}">${ViewMore}</a>
												  <a href="javascript:void(0);" class="collapseCollegeList hidden" title="${ViewLess}">${ViewLess}</a>												  
												</div>

												</c:if>
											</dsp:oparam>
										</dsp:droplet>
									
								</div>
							</div>
						</dsp:oparam>
						<dsp:oparam name="empty">
						<dsp:valueof param="noResult"/>
						</dsp:oparam>
					</dsp:droplet>

				</div>
			</dsp:oparam>
		</dsp:droplet>
	</bbb:pageContainer>
</dsp:page>