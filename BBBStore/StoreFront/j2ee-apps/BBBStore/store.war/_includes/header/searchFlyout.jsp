<dsp:page>
	<dsp:importbean bean="atg/userprofiling/Profile"/>
	<dsp:importbean	bean="/com/bbb/profile/session/SessionBean"/>
	<dsp:importbean	bean="/com/bbb/search/droplet/TypeAheadDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BrandDetailDroplet"/>
	<dsp:importbean	bean="/atg/dynamo/droplet/ForEach" />
	<dsp:getvalueof id="contextroot" idtype="java.lang.String" bean="/OriginatingRequest.contextPath" />
	<c:set var="brandDimDisplayConfig"><bbbc:config key="Brand" configName="DimDisplayConfig" /></c:set>
	<c:set var="l2DepartmentCheck">true</c:set>
	
	<%-- Retrieving Vendor Parameter for Vendor Story --%>
	<c:set var="vendorKey"><bbbc:config key="VendorParam" configName="VendorKeys"/></c:set>
	<c:if test="${!fn:containsIgnoreCase(vendorKey, 'VALUE NOT FOUND FOR KEY') && not empty vendorKey}">
		<dsp:getvalueof var="vendorParam" bean="SessionBean.vendorParam"/>
		<c:if test="${not empty vendorParam}">
		  <c:set var="vendorKey" value="${vendorKey}"/>
		  <c:set var="vendorParam" value="${vendorParam}"/>  
		</c:if>
	</c:if>
	
	<dsp:droplet name="TypeAheadDroplet">
		<dsp:param name="search" param="search"/>
		<dsp:param name="categoryId" param="categoryId"/>
		<dsp:oparam name="output">
		<dsp:getvalueof param="FacetQueryResults.topPopularTerm" var="topPopularTerm"/>
		<c:set var="showDefaultLink" value="true"></c:set>
		<c:set var="showDefaultUrl" value="true"></c:set>
		<json:object>
				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="FacetQueryResults.results"/>
					<dsp:oparam name="output">
						<dsp:getvalueof param="element.facetName" var="facetName"/>
						<dsp:getvalueof param="element.matches" var ="sizeOfRes"/>
						<c:if test="${not empty sizeOfRes}">
						<json:array name="${facetName}">
						<json:object>
							<c:choose>
								<c:when test="${facetName eq 'L2Department'}">
									<c:choose>
										<c:when test="${showDefaultLink}">
											<c:set var="showDefaultLink" value="false"></c:set>
											<json:property name="subTitle" escapeXml="false">
												${topPopularTerm} <bbbl:label key="lbl_in" language="${pageContext.request.locale.language}" /> 
											</json:property>
											<json:property name="title" escapeXml="false">
												<bbbl:label key="lbl_all_departments" language="${pageContext.request.locale.language}" />
											</json:property>
											<json:property name="url">
												<c:set var="facetH" value="${fn:replace(topPopularTerm,' ','-')}" />
												<c:url var="deptUrl" value="/s/${facetH}"/>
												 ${deptUrl}
											</json:property>
											<json:property name="vendorKey">
                                               <dsp:valueof value="${vendorKey}"/>
                                            </json:property>
                                            <json:property name="vendorParam">
                                               <dsp:valueof value="${vendorParam}"/>
                                            </json:property>
										</c:when>
									</c:choose>
								</c:when>
							</c:choose>
						</json:object>
							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="element.matches"/>
								<dsp:oparam name="output">
                                    <c:set var="strElement"><dsp:valueof param="element" valueishtml="true"/></c:set>
                                    <c:if test="${not empty strElement}">
                                        <json:object>
										<c:choose>
											<c:when test="${facetName eq 'L2Department'}">
												<json:property name="subTitle" escapeXml="false">
													${topPopularTerm} <bbbl:label key="lbl_in" language="${pageContext.request.locale.language}" /> 
												</json:property>
												<json:property name="title" escapeXml="false">
													<dsp:valueof param="element" valueishtml="true"/>
												</json:property>
											</c:when>
											<c:otherwise>
												<json:property name="title" escapeXml="false">
													<dsp:valueof param="element" valueishtml="true"/>
												</json:property>
											</c:otherwise>
										</c:choose>
                                            <c:choose>
                                                <c:when test="${facetName eq 'department'}">
                                                    <json:property name="url">
                                                        <dsp:getvalueof param="key" var="id"/>
                                                        <dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
                                                            <dsp:param name="id" value='${id}' />
                                                            <dsp:param name="itemDescriptorName" value="category" />
                                                            <dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
                                                            <dsp:oparam name="output">
                                                                <c:out value="${contextroot}"/><dsp:valueof param="url"/>
                                                            </dsp:oparam>
                                                        </dsp:droplet>
                                                    </json:property>
                                                </c:when>
                                                <c:when test="${facetName eq brandDimDisplayConfig}">
                                                    <json:property name="url" escapeXml="false">
                                                    	<dsp:getvalueof param="key" var="id"/>
                                                    	<c:set var="brand"><dsp:valueof param="element" valueishtml="true"/></c:set>
                                                    	<%-- R2.2 SEO Friendly URL changes : Start--%>
                                                    	<dsp:droplet name="BrandDetailDroplet">
														<dsp:param name="origBrandName" value="${brand}"/>
														<dsp:oparam name="seooutput">
															<dsp:getvalueof var="seoUrl" param="seoUrl" />
															<c:set var="Keyword" value="${brand}" scope="request" />
															<c:url var="brandUrl" value="${contextPath}${seoUrl}" scope="request" />
														</dsp:oparam>
													</dsp:droplet>
													<%-- R2.2 SEO Friendly URL changes : End--%>
													${brandUrl}
                                                    </json:property>
                                                </c:when>
												<c:when test="${facetName eq 'L2Department'}">
													<json:property name="url">
														<c:set var="facetH" value="${fn:replace(topPopularTerm,' ','-')}" />
														<dsp:getvalueof param="key" var="catId"/>
														<c:url var="deptUrl" value="/s/${facetH}/${catId}"/>
														${deptUrl}
													</json:property>
													<json:property name="vendorKey">
                                                       <dsp:valueof value="${vendorKey}"/>
                                                    </json:property>
                                                    <json:property name="vendorParam">
                                                       <dsp:valueof value="${vendorParam}"/>
                                                    </json:property>
												</c:when>
                                                <c:otherwise>	
                                                    <json:property name="url" escapeXml="false">
                                                        <c:set var="facet"><dsp:valueof param="element" valueishtml="true"/></c:set>
                                                        <c:set var="facetH" value="${fn:replace(facet,' ','-')}" />
                                                        <%-- R2.2 SEO Friendly URL changes --%>
                                                        <c:url var="url" value="/s/${facetH}"/>
                                                        ${url}
                                                    </json:property>
                                                    <json:property name="vendorKey">
                                                       <dsp:valueof value="${vendorKey}"/>
                                                    </json:property>
                                                    <json:property name="vendorParam">
                                                       <dsp:valueof value="${vendorParam}"/>
                                                    </json:property>
                                                </c:otherwise>
                                            </c:choose>
                                        </json:object>
                                    </c:if>
								</dsp:oparam>
							</dsp:droplet>
						</json:array>
						</c:if>
					</dsp:oparam>
				</dsp:droplet>
			</json:object>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>