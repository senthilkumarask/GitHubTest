<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Range" />
	<dsp:importbean bean="/com/bbb/cms/droplet/CustomLandingTemplateDroplet" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="facets" param="browseSearchVO.facets"/>
	<dsp:getvalueof var="requestPath" vartype="java.lang.String" bean="/OriginatingRequest.requestURI"/>
	<c:set var="inStoreParam" value=""/>
	<c:set var="storeIdParam" value=""/>
	<c:if test="${inStore == 'true'}">
		<c:set var="inStoreParam" value="&inStore=true"/>
	</c:if>
	<c:if test="${not empty storeIdFromURL}">
		<c:set var="storeIdParam" value="/store-${storeIdFromURL}/"/>
	</c:if>
	<%--R2.2 Story 116A Changes--%>
	<dsp:getvalueof var="browseSearchVO" param="browseSearchVO" />
	<dsp:getvalueof var="partialFlag" param="browseSearchVO.partialFlag"/>
	<dsp:getvalueof var="swsTermsList" param="swsTermsList" />
	<dsp:getvalueof var="getPreviousPageLink" param="getPreviousPageLink" scope="request"/>
	<c:if test="${not empty partialFlag}">
		<c:set var="partialFlagUrl" value="&partialFlag=${partialFlag}"/>
	</c:if>
	<c:set var="pageSize" value="1-${size}"/>
	<dsp:getvalueof	var="descriptorsList" param="browseSearchVO.descriptors"/>
	<dsp:getvalueof var="totSize" value="${fn:length(descriptorsList)}"/>
	<dsp:getvalueof var="view" param="view"/>
	<dsp:getvalueof var="isFromSWD" value="${fn:escapeXml(param.isFromSWD)}"/>
	<dsp:getvalueof var="emptyFacets" param="browseSearchVO.emptyFacets"/>
	<dsp:getvalueof var="emptyBox" param="browseSearchVO.emptyBox"/>
	<dsp:getvalueof var="deptAvail" param="browseSearchVO.deptAvail"/>
	<dsp:getvalueof var="showNarrow" param="browseSearchVO.deptAvail"/>
	<c:set var="Attributes"><bbbc:config key="Attributes" configName="DimDisplayConfig" /></c:set>
	<c:choose>
		<c:when test="${not empty isFromSWD && isFromSWD eq 'true'}">
		    <c:set var="swdFlag" value="&isFromSWD=true"/>
		</c:when>
		<c:otherwise>
			 <c:set var="swdFlag" value="&isFromSWD=false"/>
		</c:otherwise>
	</c:choose>
	<%--R2.2 Defect BED-208 fixed : Start --%>
	<c:if test="${not empty frmSubCatBrandPage && frmSubCatBrandPage}">
		<c:set var="searchQueryParams" value="&view=${view}"/>
	</c:if>
	
	<%-- Retrieving Vendor Parameter for Vendor Story --%>
	<%@ include file="getVendorParam.jsp"%>
	
	<c:set var="url" value="${url}/"></c:set>
	<c:if test="${not empty param.narrowDown}">
		<c:set var="url" value="${url}${param.narrowDown}/"></c:set>
	</c:if>
	<%--R2.2 Defect BED-208 fixed : End --%>

	<c:set var="department"><bbbl:label key="lbl_department" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="bracOpen"><bbbl:label key="lbl_regsrchguest_bracOpen" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="bracClose"><bbbl:label key="lbl_regsrchguest_bracClose" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="priceLabel"><bbbl:label key="lbl_facet_price_range" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="brandDisplayDim"><bbbc:config key="Brand" configName="DimDisplayConfig" /></c:set>
	<c:set var="leftAccordionExpand"><bbbl:label key="lbl_search_left_nav_accordion_expand" language ="${pageContext.request.locale.language}" />'</c:set>
	<c:set var="leftAccordionCollapse"><bbbl:label key="lbl_search_left_nav_accordion_collapse" language ="${pageContext.request.locale.language}" /></c:set>
<%-- Band-503/602 CLP Link on Search Results Page changes start--%>	
	<dsp:getvalueof var="clp" param="fromClp"/>
	<c:choose>
		<c:when test="${fn:contains(requestPath,'search') && clp ne true}">
			<c:set var="isCLP" value="forClp"/>
		</c:when>
		<c:otherwise>
			<dsp:getvalueof  var="referrerUrl" value="${header.referer}"/>
			<c:if test="${fn:contains(referrerUrl,'custom')}">
				<c:set var="fromClpName" value="${fn:substringAfter(referrerUrl, '/category/custom/')}"/>
				<c:set var="referrerCatId" value="${fn:substringAfter(fromClpName, '/')}"/>
				
				<dsp:droplet name="CustomLandingTemplateDroplet">
				<dsp:param value="${referrerCatId}" name="categoryId" />
				<dsp:param name="templateName" value="customLandingTemplate" />
					<dsp:oparam name="output">
					<dsp:param name="cmResponseVO" param="cmsResponseVO.responseItems[0]"/>
						<dsp:getvalueof var="clpName"  param="cmResponseVO.clpName"/>
						<dsp:getvalueof var="id"  param="cmResponseVO.id"/>
					 	
					 	<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
						<dsp:param name="id" value="${id}" />
						<dsp:param name="itemDescriptorName" value="customLandingTemplate" />
						<dsp:param name="repositoryName" value="/com/bbb/cms/repository/CustomLandingTemplate" />
							<dsp:oparam name="output">
								<dsp:getvalueof var="clpUrl" vartype="java.lang.String" param="url" />
                    	  	</dsp:oparam>
                		</dsp:droplet>
                		
					</dsp:oparam>
				</dsp:droplet>
			</c:if>	
			<c:choose>
				<c:when test="${not empty clpName}">
					<c:set var="isCLP" value="fromClp"/>
				</c:when>
				<c:otherwise>
					<c:set var="isCLP" value="forClp"/>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>
	
	<c:if test="${emptyFacets}">
		<dsp:getvalueof var="facets" param="browseSearchVO.emptyFacetsList"/>
		<c:if test="${empty facets}">
			<dsp:include page="/_includes/modules/blue_pills.jsp">
				<dsp:param name="searchQueryParams" value="${searchQueryParams}"/>
				<dsp:param name="url" value="${url}"/>
				<dsp:param name="swdFlag" value="${swdFlag}"/>
			 </dsp:include>
		 </c:if>
	</c:if>

	<%-- Band-503/602 CLP Link on Search Results Page changes end--%>
	<dsp:droplet name="ForEach">
		<dsp:param name="array" value="${facets}" />
		<dsp:getvalueof var="facetedBarcount" param="count" />
		<dsp:getvalueof var="size" param="size" />

		<c:if test="${(emptyFacets && size ne 1) || !emptyFacets}">
			<dsp:oparam name="outputStart">
				<c:if test="${!emptyFacets || (emptyFacets && emptyBox)}">
					<div id="facetBox" class="forClp">
					<%-- Navigating back to custom landing page --%>
						<c:if test="${isCLP eq 'fromClp' && deptAvail eq false}">
			 				 <dsp:getvalueof var="subCatHeader"  param="browseSearchVO.categoryHeader.name"/>
			 				 <li>
								 <a  href="${contextPath}${clpUrl}" title="" class="redirPage clpLink" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}">
                 				   	<span class="backArrow">&nbsp;</span>${clpName}
       							</a>
       						</li>
						</c:if>
				</c:if>
			</dsp:oparam>
		</c:if>

		<dsp:oparam name="output">
			<dsp:getvalueof var="facetName" param="element.name" />
			<dsp:getvalueof var="multiSelectFacet" param="element.multiSelect" />
			<dsp:getvalueof var="refinedFacetName" param="element.facetRefinedName" />
			<dsp:getvalueof var="facetListStyle" param="element.displayProperties.facetListStyle" />
			<dsp:getvalueof var="fieldsetClass" param="element.displayProperties.fieldsetClass" />
			<dsp:getvalueof var="offScreen" param="element.displayProperties.offScreen" />
			<dsp:getvalueof var="divClass" param="element.displayProperties.divClass" />
			<dsp:getvalueof var="title" param="element.displayProperties.title" />
			<dsp:getvalueof var="displayName" param="element.displayProperties.displayName" />
			<dsp:getvalueof var="count" param="count" />

				<c:choose>
					<c:when test="${size eq 1 or (facetedBarcount eq 2 and  showNarrow)}">
						<dsp:include page="/_includes/modules/blue_pills.jsp">
							<dsp:param name="searchQueryParams" value="${searchQueryParams}"/>
							<dsp:param name="url" value="${url}"/>
							<dsp:param name="swdFlag" value="${swdFlag}"/>
						 </dsp:include>
						<c:set var="showNarrow" value="${false}"/>
					</c:when>
					<c:when test="${facetedBarcount eq 1 and !showNarrow and size ne 1}">
						<dsp:include page="/_includes/modules/blue_pills.jsp">
							<dsp:param name="searchQueryParams" value="${searchQueryParams}"/>
							<dsp:param name="url" value="${url}"/>
							<dsp:param name="swdFlag" value="${swdFlag}"/>
						 </dsp:include>
					</c:when>
				</c:choose>
			
				<c:if test="${facetName ne 'RECORD TYPE' && !(facetName == 'PRICE RANGE' && defaultUserCountryCode eq 'MX') && !(facetName == 'PRICE RANGE MX' && defaultUserCountryCode ne 'MX')}">
					
					<fieldset class="${fieldsetClass}">
					<legend class="hidden">${title}</legend>
					
						<div class="${divClass}">
							<c:choose>
								<c:when test="${facetName eq department}">
										<c:set var="scrollerClass">facetScrollerDept ${isCLP}</c:set>
										<h3>${displayName}
										</h3>
								</c:when>
								<c:otherwise>
									<c:set var="scrollerClass">facetScroller</c:set>
										<a href="#" title="${title}" aria-label="${displayName} ${leftAccordionCollapse}">${displayName} </a>
						
								</c:otherwise>
							</c:choose>
						</div>
						
						<div class="facetContent">
							<c:choose>
								<%-- For displaying brand typeahead box --%>
								<c:when test="${facetName == brandDisplayDim}">
									<div class="facetBrandSearch">
										<c:set var="guide_text">
											<bbbl:label key="lbl_brand_typeaheadbox_guide_text" language="${pageContext.request.locale.language}" />
										</c:set>
										<input class="txtFacetSearch" type="text" value="${guide_text}" title="${guide_text}" aria-required="false" />
									</div>
								</c:when>
								<c:when test="${facetName == 'COLLEGE'}">
									<div class="facetBrandSearch">
										<c:set var="guide_text">
											<bbbl:label key="lbl_college_typeaheadbox_guide_text" language="${pageContext.request.locale.language}" />
										</c:set>
										<input class="txtFacetSearch" type="text" value="${guide_text}" title="${guide_text}" aria-required="false" />
									</div>
								</c:when>
							</c:choose>
							
							<div aria-hidden="false" class="${scrollerClass}" >
							<ul class="${facetListStyle}">
							
							<%-- For multi selected facets show selected refinements/descriptors --%>
								<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
									<dsp:param name="value" param="element.facetDescriptors" />
									<dsp:oparam name="false">
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="element.facetDescriptors" />
											<dsp:param name="elementName" value="selectedFacetRefItem" />
											
											<dsp:oparam name="output">
												<dsp:getvalueof var="selectedFacetRefItemName" param="selectedFacetRefItem.name" />
												<dsp:getvalueof var="refinedName_0" param="selectedFacetRefItem.refinedName" />
												<dsp:getvalueof var="selectedRefItemRoot" param="selectedFacetRefItem.rootName" />
												<dsp:getvalueof var="facetItemRemoveQuery" param="selectedFacetRefItem.removalquery" />
												<dsp:getvalueof var="facetDescFilter" param="selectedFacetRefItem.descriptorFilter" />
												<%-- Setting up the counter to display clp nav link only once --%>
												<c:set var="count" value="0"/>
												<%-- Only Brand, Attribute & Color are multiselect facets. So when selected descriptor is child either of these then show this descriptor as selected checkox--%>
												<li class="facetListItem clearfix">
												<c:set var="brandNameTitle"><dsp:valueof param="selectedFacetRefItem.name" valueishtml="true" /></c:set> 
												<%-- R2.2 SEO Friendly URL changes --%> 
												<input type="checkbox" id="${refinedFacetName}_${selectedFacetRefItemName}" value="${url}${facetDescFilter}/${pageSize}${storeIdParam}?${facetItemRemoveQuery}${subCategoryPageParam}${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" name="brandSearch" title="${brandNameTitle}" checked="checked" class="checkbox dynFormSubmit"
												       onclick="omnitureRefineCall('${facetName}', '${refinedName_0}');"
													   data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}"
													   aria-checked="true" aria-describedby="${facetName}_${selectedFacetRefItemName}" aria-label="<dsp:valueof param="element.name"/>&nbsp;. <dsp:valueof param="element.size" />&nbsp;.Selecting this item will reload your page"/> <label for="${refinedFacetName}_${selectedFacetRefItemName}"><dsp:valueof param="selectedFacetRefItem.name" valueishtml="true" /> </label> 
												</li>
											</dsp:oparam>
										</dsp:droplet>
										<%-- End ForEach--%>
									</dsp:oparam>
								</dsp:droplet>
								<%-- End IsEmpty--%>
								
							<%-- Show facets refinements--%>
							<dsp:getvalueof var="facetParentVoName" param="element.name"/>
								<dsp:droplet name="ForEach">
									<dsp:param name="array" param="element.facetRefinement" />
									<dsp:oparam name="output">
										<c:choose>
											<%-- Show refinements for brand, attributes and color as checkbox--%>
											<c:when test="${multiSelectFacet}">
													<li class="facetListItem clearfix">
														<dsp:getvalueof var="facetRefItem" param="element.name" /> 
														<dsp:getvalueof var="facetRefFilter" param="element.facetRefFilter" />
														<dsp:getvalueof var="facetItemQuery" param="element.query" />
														<dsp:getvalueof var="refinedName_1" param="element.refinedName" />
														<dsp:getvalueof var="idx" param="index" />
														<c:set var="facetItemTitle"><dsp:valueof param="element.name" valueishtml="true" /></c:set> 
														<dsp:getvalueof var="intlFlagAtt" param="element.intlFlag"/>
														
														<c:choose>
															<c:when test="${facetParentVoName eq 'Attributes' && isInternationalCustomer}">
																<c:if test="${intlFlagAtt eq 'Y'}"> 
																	<input type="checkbox" id="${refinedFacetName}_${idx}" onclick="omnitureRefineCall('${facetName}', '${refinedName_1}');" value="${url}${facetRefFilter}/${pageSize}${storeIdParam}?${facetItemQuery}${subCategoryPageParam}${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" name="brandSearch" title="${facetItemTitle}" class="checkbox dynFormSubmit" 
																	data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}"
																	aria-checked="false" aria-describedby="${facetName}_${idx}" aria-label="<dsp:valueof param="element.name"/>&nbsp;. <dsp:valueof param="element.size" />&nbsp;.Selecting this item will reload your page"/><label for="${refinedFacetName}_${idx}">
																	<dsp:valueof param="element.name" valueishtml="true" />
																	&nbsp;<span class="facetCount">${bracOpen}<dsp:valueof param="element.size" />${bracClose}</span> </label>
																</c:if>
															</c:when>
															<c:otherwise>
																<input type="checkbox" id="${refinedFacetName}_${idx}" onclick="omnitureRefineCall('${facetName}', '${refinedName_1}');" value="${url}${facetRefFilter}/${pageSize}${storeIdParam}?${facetItemQuery}${subCategoryPageParam}${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" name="brandSearch" title="${facetItemTitle}" class="checkbox dynFormSubmit" 
																data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}"
																aria-checked="false" aria-describedby="${facetName}_${idx}" aria-label="<dsp:valueof param="element.name"/>&nbsp;. <dsp:valueof param="element.size" />&nbsp;.Selecting this item will reload your page"/><label for="${refinedFacetName}_${idx}">
																<dsp:valueof param="element.name" valueishtml="true" />
																&nbsp;<span class="facetCount">${bracOpen}<dsp:valueof param="element.size" />${bracClose}</span> </label>
															</c:otherwise>
														</c:choose>
													</li>
											</c:when>
											
											<c:when test="${facetName eq department}">
												<%-- Band-503/602 CLP Link on Search Results Page changes start--%>
												<!-- GB comments: Do it outside the loops at the top of page and populate a variable for fn:contains(requestPath,'brand')-->
												<c:if test="${count eq 1 && fn:contains(requestPath,'brand') && not empty clpName}">
													<li class="facetListItem clearfix ">
															 <a  href="${contextPath}${clpUrl}${swdFlag}" title="" class="redirPage clpLink" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}">
				                           				    	 <span class="backArrow">&nbsp;</span>${clpName}
				                							</a>
													</li>
												</c:if>
												<c:if test="${count eq 1 && clp eq true}">
													<li class="facetListItem clearfix ">
															 <a  href="${contextPath}${clpUrl}${swdFlag}" title="" class="redirPage clpLink" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}">
				                           				    	 <span class="backArrow">&nbsp;</span>${clpName}
				                							</a>
													</li>
												</c:if>
												<%-- Band-503/602 CLP Link on Search Results Page changes end--%>
												
												<li class="facetListItem clearfix">
													<c:set var="escUrl"><c:out value="${url}"/></c:set>
													<dsp:getvalueof var="refinedName_3" param="element.refinedName"/>
													<dsp:getvalueof var="facetRefFilter" param="element.facetRefFilter"/>
													<dsp:getvalueof var="facetItemQuery" param="element.query"/>
													<dsp:getvalueof var="facetsRefinementVOs" param="element.facetsRefinementVOs"/>
														
													<a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" 
													onclick="omnitureRefineCall('${facetName}', '${refinedName_3}');" href="${escUrl}${facetRefFilter}/${pageSize}${storeIdParam}?${facetItemQuery}${subCategoryPageParam}${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" title="<dsp:valueof param="element.name"/>"  aria-label="<dsp:valueof param="element.name"/>&nbsp;. <dsp:valueof param="element.size" />&nbsp;. Selecting this item will reload your page"/> <dsp:valueof param="element.name" /> 
													<span class="facetCount">${bracOpen}<dsp:valueof param="element.size" />${bracClose}</span> </a>
													
													<c:if test="${not empty facetsRefinementVOs}">
														<dsp:droplet name="ForEach">
															<dsp:param name="array" param="element.facetsRefinementVOs" />
															<dsp:oparam name="output">
																<ul class="facetl2ListItem">
			
																	<dsp:getvalueof var="refinedName_3_l2" param="element.refinedName"/>
																	<dsp:getvalueof var="facetRefFilter_l2" param="element.facetRefFilter"/>
			
																	<li><a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value='<%=request.getParameter("swsterms")%>' 
																	onclick="omnitureRefineCall('${facetName}', '${refinedName_3}:${refinedName_3_l2}');" href="${escUrl}${facetRefFilter_l2}/${pageSize}${storeIdParam}?${facetItemQuery}${subCategoryPageParam}${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" title="<dsp:valueof param="element.name"/>"  aria-label="<dsp:valueof param="element.name"/>&nbsp;. <dsp:valueof param="element.size" />&nbsp;. Selecting this item will reload your page"/> 
																	<dsp:valueof param="element.name"/> <span class="facetCount">${bracOpen}<dsp:valueof param="element.size"/>${bracClose}</span> </a>
																	</li>
																</ul>
																<ul class="facetl3ListItem">
																<dsp:droplet name="ForEach">
																	<dsp:param name="array" param="element.facetsRefinementVOs" />
			
																	<dsp:oparam name="output">
																	<dsp:getvalueof var="refinedName_3_l3" param="element.refinedName"/>
																	<dsp:getvalueof var="facetRefFilter_l3" param="element.facetRefFilter"/>
			
																	<li><a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" 
																	onclick="omnitureRefineCall('${facetName}', '${refinedName_3}:${refinedName_3_l2}:${refinedName_3_l3}');" href="${escUrl}${facetRefFilter_l3}/${pageSize}${storeIdParam}?${facetItemQuery}${subCategoryPageParam}${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" title="<dsp:valueof param="element.name"/>" aria-label="<dsp:valueof param="element.name"/>&nbsp;. <dsp:valueof param="element.size" />&nbsp;. Selecting this item will reload your page"/> 
																	 <dsp:valueof param="element.name"/> <span class="facetCount">${bracOpen}<dsp:valueof param="element.size"/>${bracClose}</span> </a>
																	</li>
			
																	</dsp:oparam>
																</dsp:droplet>
																</ul>
															</dsp:oparam>
														</dsp:droplet>
													</c:if>
												</li>
											</c:when>
											
											<c:otherwise>
												<li class="facetListItem clearfix">
													<c:set var="escUrl"><c:out value="${url}"/></c:set>
													<dsp:getvalueof var="refinedName_3" param="element.refinedName"/>
													<dsp:getvalueof var="facetRefFilter" param="element.facetRefFilter"/>
													<dsp:getvalueof var="facetItemQuery" param="element.query"/>
													
													<a class="dynFormSubmit" data-submit-param-length="2" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" data-submit-param2-name="swsterms" data-submit-param2-value="${swsterms}" 
													onclick="omnitureRefineCall('${facetName}', '${refinedName_3}');" href="${escUrl}${facetRefFilter}/${pageSize}${storeIdParam}?${facetItemQuery}${subCategoryPageParam}${searchQueryParams}${partialFlagUrl}${vendorParam}${inStoreParam}${swdFlag}" title="<dsp:valueof param="element.name"/>"  aria-label="<dsp:valueof param="element.name"/>&nbsp;. <dsp:valueof param="element.size" />&nbsp;. Selecting this item will reload your page"/> <dsp:valueof param="element.name" /> 
													<span class="facetCount">${bracOpen}<dsp:valueof param="element.size" />${bracClose}</span> </a>
												</li>
			
											</c:otherwise>
											</c:choose>
										<c:set var="count" value="${count +1}"/>
									</dsp:oparam>
								</dsp:droplet>
							</ul></div>
						</div>
					</fieldset>
				</c:if>
			</dsp:oparam>
          	
			<dsp:oparam name="outputEnd">
				<c:if test="${(emptyFacets && size ne 1) || !emptyFacets}">
				<c:if test="${!emptyFacets || (emptyFacets && emptyBox)}">
						<input type="submit" class="hidden" value="" />
						</form>
					</div>
				</c:if>
				</c:if>
			</dsp:oparam>

	</dsp:droplet>

	<script type="text/javascript">
	function omnitureRefineCallInitial(refineString){
		var selPriceRange = document.getElementById("selPriceRange").value;
		var indexValuePrice=selPriceRange.lastIndexOf('=');
		var subRefineString = selPriceRange.substring(indexValuePrice+1, selPriceRange.length);
		omnitureRefineCall(refineString, subRefineString);
	}
		function omnitureRefineCall(refineString, subRefineString) {
		   if (typeof refinementTrack === 'function' && typeof s !== 'undefined') {
			   refinementTrack(refineString, subRefineString); }
		}
	</script>
</dsp:page>