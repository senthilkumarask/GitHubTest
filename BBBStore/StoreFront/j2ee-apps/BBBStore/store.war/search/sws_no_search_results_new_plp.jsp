<dsp:page>
	<dsp:getvalueof id="servername" idtype="java.lang.String" bean="/OriginatingRequest.servername"/>
	<dsp:getvalueof id="scheme" idtype="java.lang.String" bean="/OriginatingRequest.scheme"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/browse/AddContextPathDroplet"/>
	<dsp:importbean bean="/com/bbb/omniture/OmnitureVariableDroplet"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:getvalueof var="frmBrandPage" param="frmBrandPage" scope="request" />
	<dsp:getvalueof var="searchTerm" param="enteredSearchTerm"/>
    <dsp:getvalueof	var="enteredNarrowDown" param="lastEnteredSWSKeyword"/>
    <dsp:getvalueof var="swsTermsList" param="swsTermsList" scope="request" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="browseSearchVO" param="browseSearchVO"/>
	<dsp:getvalueof var="url" param="url" />
	<dsp:getvalueof var="categoryId" param="categoryId"/>
		<dsp:getvalueof var="totSize" value="${fn:length(browseSearchVO.descriptors)}" />
		<dsp:getvalueof var="hideDepartmentOnNullResultPage" value="true" />
		<dsp:getvalueof var="size" param="size" />
	<c:if test="${not empty param.narrowDown}">
		 	<c:set var="flurl" value="${url}"></c:set>
          	<c:set var="url" value="${url}${param.narrowDown}/"></c:set>
         </c:if>
	<c:set var="pageSize" value="1-${size}"/>
    <c:set var="promoKeyTop"><bbbl:label key="lbl_promo_key_top" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="promoKeyRight"><bbbl:label key="lbl_promo_key_right" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="promoKeyCenter"><bbbl:label key="lbl_promo_key_center" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="promoKeyRelated"><bbbl:label key="lbl_promo_key_related" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="enteredNarrowDown"><c:out value="${enteredNarrowDown}" escapeXml="true"/></c:set>
	<c:forEach var="narrowDownFilter" items="${swsTermsList}">
	<c:set var="titleFilterString" value="${narrowDownFilter.name}" />
	</c:forEach>

		<!--BBBI-3804: omniture Tagging :start -->
			<c:set var="keywordBoostFlag"><bbbc:config key="KeywordBoostFlag" configName="FlagDrivenFunctions"/></c:set>
			<c:set var="defaultOmnitureVar"><bbbc:config key="DefaultEndecaOmnitureVariable" configName="OmnitureBoosting"/></c:set>
			<dsp:getvalueof var="vendorParam" bean="SessionBean.vendorParam"/> 
			<c:choose>
				<c:when test="${(keywordBoostFlag eq true) || (not empty vendorParam) }">
					<dsp:droplet name="OmnitureVariableDroplet">
				  		<dsp:param name="pageName" value="SEARCH"/>
				  			<dsp:oparam name="output">
				    		<dsp:getvalueof var="OmnitureVariable" param="OmnitureVariable"/>
				 		</dsp:oparam>
			  		</dsp:droplet>
				</c:when>
				<c:otherwise>
					<c:set var="OmnitureVariable" value="${defaultOmnitureVar}"/>
				</c:otherwise>
			
			</c:choose>
	<!--BBBI-3804: omniture Tagging :end -->
   	<bbb:pageContainer>
		<jsp:attribute name="section">search</jsp:attribute>
		<jsp:attribute name="pageWrapper">searchList searchResults noSearchResults useCertonaAjax</jsp:attribute>
		<jsp:attribute name="titleString">No Search Results for ${titleFilterString}</jsp:attribute>
		<jsp:attribute name="follow">false</jsp:attribute>
		<jsp:attribute name="index">false</jsp:attribute>
		<jsp:attribute name="PageType">Search</jsp:attribute> 
		<jsp:body>
		
		<script type="text/javascript">
			var resx = new Object();
		</script>
		<c:if test="${TagManOn}">
			<dsp:include page="/tagman/frag/search_frag.jsp" >
				<dsp:param name="searchTerm" value="${searchTerm}"/>
			</dsp:include>
		</c:if>
			<c:set var="urlPrefixForSuggestion" value="${contextPath}/s/" scope="request" />
			<c:set var="searchQueryParams" value="&view=${view}&_dyncharset=UTF-8" scope="request" />
			<c:set var="searchQueryParamsWithoutView" value="_dyncharset=UTF-8" scope="request" />			
			<dsp:getvalueof	var="descriptorsList" param="browseSearchVO.descriptors"/>
			<dsp:getvalueof var="totSize" value="${fn:length(descriptorsList)}"/>

			<div id="content" class="subCategory newPlp container_12 clearfix" role="main">
				<dsp:getvalueof param="errorMsg"  var="errorMsg"/>
				
				<div class="breadcrumbs grid_12">
				<c:if test="${not frmBrandPage and  not empty searchTerm }">
					<a href="${scheme}://${servername}" title="<bbbl:label key="lbl_breadcrumb_home_link" language="${pageContext.request.locale.language}" />"><bbbl:label key="lbl_breadcrumb_home_link" language="${pageContext.request.locale.language}" /></a>
					<c:if test ="${errorMsg eq null }">
						<span class="rightCarrot">&gt;</span>
						<span><bbbl:label key="lbl_header_search_results_page_1" language ="${pageContext.request.locale.language}"/><span class="bold">&ldquo;<c:out value="${searchTerm}" escapeXml="true"/>&rdquo;</span></span>
					</c:if>
				</c:if>
				</div>			
				<dsp:droplet name="ForEach">
				<dsp:param name="array" param="browseSearchVO.promoMap" />
                  	<dsp:oparam name="output">
				  		<dsp:getvalueof var="elementList" param="element"/>
                         	<dsp:getvalueof var="key1" param="key"/>
                         	 <c:if test="${key1 eq promoKeyTop &&  not empty elementList}">
                           		<div class="grid_12 clearfix promo-12col">
	                           		<dsp:droplet name="ForEach">
	                                	<dsp:param name="array" value="${elementList}" />
	                                    <dsp:oparam name="output">
			                            	<dsp:getvalueof var="imageHREF" param="element.imageHref"/>
			                            	<c:if test="${not empty imageHREF}">
								                    <dsp:droplet name="AddContextPathDroplet">
				                            			<dsp:param name="InputLink" value="${imageHREF}" />
				                            			<dsp:oparam name="output">
				                            				<dsp:getvalueof var="imageHREF" param="OutputLink"/>
				                            			</dsp:oparam>
				                            		</dsp:droplet>
				                            </c:if>
						                    <dsp:getvalueof var="imageURL" param="element.imageSrc"/>
						                    <c:choose>
						                    	<c:when test="${not empty imageHREF}">
						                    		<a href="${imageHREF}" title="<dsp:valueof param="element.imageAlt"/>">
	                                          			<img src="${scene7Path}/${imageURL}" alt="<dsp:valueof param="element.imageAlt"/>" />
	                                          		</a>
						                    	</c:when>
						          	          	<c:otherwise>
	                                          			<img src="${scene7Path}/${imageURL}" alt="<dsp:valueof param="element.imageAlt"/>" />
						            	        </c:otherwise>
						                    </c:choose>
	                                  	</dsp:oparam>
	                            	</dsp:droplet>
	                            </div>
                         	</c:if>
							<c:if test="${key1 eq promoKeyCenter &&  not empty elementList}">
						  			<div class="grid_12 clearfix promo-12col1 relatedSearch">                                                   	
	                      	     		<dsp:droplet name="ForEach">
	                                 		<dsp:param name="array" value="${elementList}" />
												<dsp:oparam name="output">	
													<dsp:getvalueof  var="SeperatedValue" param="element.RelatedSeperated" />
														
														<c:if test="${fn:length(SeperatedValue) gt 0}">
														<span class="bold marRight_10">${promoKeyRelated} :</span>
														    <dsp:droplet name="ForEach">
																<dsp:param name="array" value="${SeperatedValue}" />
																	<c:set var="currentCount">
																		<dsp:valueof param="count"/></c:set>
																<dsp:oparam name="output">
																
																				<dsp:getvalueof  var="keyWords" param="element" />
																					<c:set var="urlPrefixForSuggestion1" value="${contextPath}/s/" scope="request" />
																						<a href="${urlPrefixForSuggestion1}${fn:replace(fn:trim(keyWords),' ','-')}" title="">${keyWords}<c:if test="${fn:length(SeperatedValue) ne currentCount}">,</c:if></a>																							
																</dsp:oparam>
															</dsp:droplet>
															</c:if>
											    </dsp:oparam>
	                                	</dsp:droplet>
                                	</div>
                            	</c:if>
                   </dsp:oparam>
 				</dsp:droplet>
 				<c:if test="${totSize ge 1}">
 				<c:if test="${not empty searchTerm}">
 				<div class="grid_3">
 				<dsp:include page="/_includes/modules/faceted_bar.jsp">
						<dsp:param name="browseSearchVO" param="browseSearchVO" />
						<dsp:getvalueof var="url" value="${url}" scope="request"/>
						<dsp:param name="showDepartment" value="true" />
						<dsp:param name="getPreviousPageLink" value="true" />
					</dsp:include>
					</div>
				</c:if>
				
				<c:if test="${empty searchTerm}">
				 			<c:set var="plpViewParam" value="&view=grid" />
							<c:set var="swsTermsLength" value="${fn:length(swsTermsList)}" />
							<c:set var="facetCountForRefType" value="${swsTermsLength}" />
							<c:set var="facetCountForRefTypeParam" value="${browseSearchVO.facetCountForRefTypeParam}" />
							<c:set var="facetCountForRefTypeParam" value="${facetCountForRefTypeParam + facetCountForRefType}" />
								<c:if test="${facetCountForRefTypeParam > 1}">
									<c:set var="refTypeParam" value="&refType=true" />
								</c:if>
						<div id="searchBox" class="grid_12">
					<form id="frmSearchCriteria" class="frmnewsearch" method="post">
						<fieldset class="searchGroup">
							<div class="searchTitle hidden">
								<dsp:getvalueof var="descriptors"
											param="browseSearchVO.descriptors" />
								
								<c:set var="elementDesc" value="${descriptors[totSize-1]}" />
								<dsp:getvalueof var="type" value="${elementDesc.rootName}" />
								<c:if test="${(type ne 'RECORD TYPE' and type ne 'DEPARTMENT') or swsTermsLength> 0 }">
									<h3>
									 <bbbl:label key="lbl_search_facet_bar" language="${pageContext.request.locale.language}" /> 
									</h3> 
								</c:if>
								<c:if test="${(totSize ge 0) && (type ne 'DEPARTMENT')}">
									<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
										<dsp:param name="id" value="${CatalogRefId}" />
										<dsp:param name="itemDescriptorName" value="category" />
										<dsp:param name="repositoryName"
													value="/atg/commerce/catalog/ProductCatalog" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="finalUrl" vartype="java.lang.String"
														param="url" />
											<c:url value="${finalUrl}${storeIdParamNoStartingSlash}"
														var="urlSe">
												<c:param name="subCatPlp" value="true" />
												<c:if test="${fromCollege eq 'true' }">
													<c:param name="fromCollege" value="true" />
												</c:if>
												<c:if test="${inStore eq 'true'}">
													<c:param name="inStore" value="true" />
												</c:if>
												<c:param name="view" value="${defaultView}" />
											</c:url>
										</dsp:oparam>
									</dsp:droplet>
									</c:if>							
								<c:set var="filtersSelected" value="false"/>							
								<c:set var="facetCountForRefType" value="${swsTermsLength}" />						
								<dsp:getvalueof var="facetCountForRefTypeParam" param="browseSearchVO.facetCountForRefTypeParam" />
								<c:set var="facetCountForRefTypeParam" value="${facetCountForRefTypeParam + facetCountForRefType}" />
								<c:if test="${facetCountForRefTypeParam > 1}">
									<c:set var="refTypeParam" value="&refType=true" />
								</c:if>
							</div>
							<div class="searchContent noBorder">
								<dsp:droplet name="ForEach">
									<dsp:param name="array" param="browseSearchVO.descriptors" />
									<dsp:param name="elementName" value="selectedFacetRefItem" />
									<dsp:oparam name="outputStart">
										<dsp:getvalueof var="size" param="size" />
										<dsp:getvalueof var="type"
													param="selectedFacetRefItem.rootName" />
										<ul class="searchList">									
											</dsp:oparam>							
									<dsp:oparam name="output">
										<dsp:getvalueof var="type" param="selectedFacetRefItem.rootName" />
										<dsp:getvalueof var="facetItemRemoveQuery" param="selectedFacetRefItem.removalquery" />
										<dsp:getvalueof var="facetDescFilter" param="selectedFacetRefItem.descriptorFilter" />										
										<c:choose>
											<c:when test="${type eq 'DEPARTMENT'}">
												<dsp:getvalueof var="facetFilter" param="selectedFacetRefItem.descriptorFilter" />
												<dsp:getvalueof var="catIdFilter" param="selectedFacetRefItem.categoryId" />
												<c:set var="filter" value="${catIdFilter}-${facetFilter}"></c:set>
											</c:when>
											<c:when test="${type ne 'RECORD TYPE'}">
												<c:if test="${not empty facetDescFilter}">
														<c:set var="filtersSelected" value="true"/>
													<li class="clearfix">
														<span class="searchListItem">
															<span class="searchItem">
																<dsp:valueof param="selectedFacetRefItem.name"
																			valueishtml="true" />
															</span>
															<a href="${contextPath}${url}${facetDescFilter}/${pageSize}${storeIdParam}?${facetItemRemoveQuery}${subCategoryPageParamNewPlp}${searchQueryParams}${plpViewParam}${refTypeParam}${inStoreParam}"
																	class="lnkSearchRemove" title="Remove"
																	data-submit-param-length="2"
																	data-submit-param1-name="origSearchTerm"
																	data-submit-param1-value="${origSearchTerm}"
																	data-value="${swsterms}">X</a> 
															<%-- <a  href="${redirectUrl}" class="lnkSearchRemove" title="Remove" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-value="${swsterms}">X</a> --%>
														</span>
													</li>
												</c:if>
											</c:when>
										</c:choose>
									</dsp:oparam>									
									<dsp:oparam name="outputEnd">
									<c:set var="filterParam"></c:set>
									<c:if test="${not empty filter}">
										<c:set var="filterParam">${filter}/</c:set>
									</c:if>
									<c:forEach var="narrowDownFilter" items="${swsTermsList}">
										<c:if test="${not empty narrowDownFilter}">
										<c:set var="filtersSelected" value="true"/>
			                               <li class="clearfix">
												<span class="searchListItem">
													<span class="searchItem">
														<dsp:valueof value="${narrowDownFilter.name}"
																		valueishtml="true" />
													</span>
													<c:choose>
														<c:when test="${empty narrowDownFilter.value}">
															<c:set var="narrowDownFilterValue" value=""></c:set>
														</c:when>
												 		<c:otherwise>
															<c:set var="narrowDownFilterValue"
																			value="${narrowDownFilter.value}/"></c:set>
														</c:otherwise> 
													</c:choose>
													<a href="${contextPath}${flurl}${narrowDownFilterValue}${filterParam}${pageSize}${storeIdParam}?${facetItemRemoveQuery}${subCategoryPageParamNewPlp}${searchQueryParams}${plpViewParam}${refTypeParam}${inStoreParam}"
																class="lnkSearchRemove" title="Remove"
																data-value="${narrowDownFilter.removalValue}">X</a>
													<%-- <a  href="${redirectUrl}" class="lnkSearchRemove" title="Remove" data-value="${narrowDownFilter.removalValue}">X</a> --%>
													<c:if test="${not empty hideDepartmentOnNullResultPage}">
														<c:set var="previousPage" scope="request">${contextPath}${flurl}${narrowDownFilterValue}${filterParam}${pageSize}?${facetItemRemoveQuery}${subCategoryPageParamNewPlp}${searchQueryParams}${plpViewParam}&refType=true</c:set>
														<c:set var="removalValue" scope="request">${narrowDownFilter.removalValue}</c:set>
													</c:if>
												</span>
											</li>
										</c:if>
                                      </c:forEach>

									<li class="clrAll">
										<c:if
														test="${(type ne 'RECORD TYPE' and type ne 'DEPARTMENT') or swsTermsLength> 0 }">
											<c:set var="clear">
                                      			<bbbl:label
																key="lbl_search_filter_box_clearall"
																language="${pageContext.request.locale.language}" />
                                      		</c:set>
                                   			<c:if test="${empty urlSe}">
                                   				<c:set var="urlSe">
												${contextPath}${flurl}${storeIdParamNoStartingSlash}?subCatPlp=true${fromCollegeUrl}&refType=true${inStoreParam}&clear=true
												</c:set>
                                   			</c:if>
											<a href="${contextPath}${flurl}${urlSe}&clear=true" class="lnkSearchReset" title="${clear}"> ${clear} </a>
										</c:if>
									</li>
									</ul>
									</dsp:oparam>
								</dsp:droplet>
							
							</div>
						</fieldset>
					</form>
				</div> 
				</c:if>
				</c:if>
			
			<div class="grid_12">
			<div class="grid_3 noResultDiv"></div>	
  				<div id="pagTop" class="grid_9 alpha omega clearfix">
   				 	<div class="searchGroupNoResults">
							<jsp:useBean id="placeHolderMapNullSearchMessage" class="java.util.HashMap" scope="request"/>
							<c:set target="${placeHolderMapNullSearchMessage}" property="enteredNarrowDownTerm" value="${enteredNarrowDown}"/>
							<c:set target="${placeHolderMapNullSearchMessage}" property="previousPage" value="${previousPage}"/>
							<c:set var="data-params" scope="request"></c:set>
							<c:set target="${placeHolderMapNullSearchMessage}" property="data-params" value="data-submit-param-length='1'
									data-submit-param1-name='swsterms'
									data-submit-param1-value='${removalValue}'"/>
							<bbbt:textArea key="txt_sws_nosearchresult_message" placeHolderMap="${placeHolderMapNullSearchMessage}" language ="${pageContext.request.locale.language}"/>
   			 		</div>
  				</div>
			</div>		
		</div>
							
			
			
			</jsp:body>
				<jsp:attribute name="footerContent">
           <script>
           var serchTerm = '<c:out value="${searchTerm}"/>';
           if(typeof s !=='undefined') {
			s.channel='Search';
			s.pageName='Search';
			s.prop1='Search';
			s.prop2='Search';
			s.prop3='Search';
			s.prop4='';
			s.prop5='';
			s.prop6=''; 
			s.prop7=serchTerm.toLowerCase();
			s.prop8='zero';
			s.eVar2=serchTerm.toLowerCase();
			s.eVar61="${OmnitureVariable}";
			var s_code=s.t();
			if(s_code)document.write(s_code);		
           }
        </script>
    </jsp:attribute>
		
	</bbb:pageContainer>
</dsp:page>