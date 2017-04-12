<dsp:page>
	<dsp:importbean bean="atg/userprofiling/Profile"/>
	<dsp:importbean	bean="/com/bbb/search/droplet/TypeAheadDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BrandDetailDroplet"/>
	<dsp:importbean	bean="/atg/dynamo/droplet/ForEach" />
	<dsp:getvalueof id="contextroot" idtype="java.lang.String" bean="/OriginatingRequest.contextPath" />
	<c:set var="brandFacetDisplayName">
	    <bbbc:config key="Brand" configName="DimDisplayConfig" />
    </c:set>
	
	<dsp:droplet name="TypeAheadDroplet">
		<dsp:param name="search" param="search"/>
		<dsp:oparam name="output">
			<json:object>
				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="FacetQueryResults.results"/>
					<dsp:oparam name="output">
						<dsp:getvalueof param="element.facetName" var="facetName"/>
						<json:array name="${facetName}">
							<dsp:droplet name="ForEach">
								<dsp:param name="array" param="element.matches"/>
								<dsp:oparam name="output">
									<c:set var="strElement"><dsp:valueof param="element" valueishtml="true"/></c:set>
									<c:if test="${not empty strElement}">
										<json:object>
											<json:property name="title" escapeXml="false">
												<dsp:valueof param="element" valueishtml="true"/>
											</json:property>
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
												<c:when test="${facetName eq brandFacetDisplayName || facetName eq 'brand'}">
													<json:property name="url" escapeXml="false">
														<dsp:getvalueof param="key" var="id"/>
														<c:set var="brand"><dsp:valueof param="element" valueishtml="true"/></c:set>
														<%-- R2.2 SEO Friendly URL changes : Start--%>
														<dsp:droplet name="BrandDetailDroplet">
														<dsp:param name="origBrandName" value="${brand}"/>
														<dsp:oparam name="seooutput">
															<dsp:getvalueof var="seoUrl" param="seoUrl" />
															<c:set var="Keyword" value="${brand}" scope="request" />
															<c:url var="url" value="${contextPath}${seoUrl}" scope="request" />
														</dsp:oparam>
													</dsp:droplet>
													<%-- R2.2 SEO Friendly URL changes : End--%>
													${url}
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
												</c:otherwise>
											</c:choose>
										</json:object>
									</c:if>
								</dsp:oparam>
							</dsp:droplet>
						</json:array>
					</dsp:oparam>
				</dsp:droplet>
			</json:object>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>
