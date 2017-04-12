<%-- R2.2 Story - SEO Friendly URL changes - Urls are changed for this story --%>
<dsp:page>
	<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductComparisonList"/>
	<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListHandler"/>
	<dsp:importbean bean="/atg/commerce/catalog/CategoryLookup" />
	<dsp:importbean bean="/com/bbb/browse/AddContextPathDroplet"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="nextHref" param="nextHref"/>
	<dsp:getvalueof var="prevHref" param="prevHref"/>
	<dsp:getvalueof var="catID" param="catID"/>
	<dsp:getvalueof var="fromCollege" param="fromCollege"/>
	<dsp:getvalueof var="defaultView" param="view"/>
	<dsp:getvalueof var="prop9Var" param="brandNameForUrl" />
	<c:set var="view" scope="request"><c:out value="${param.view}"/></c:set>
	<c:set var="plpGridSize">
		<bbbc:config key="GridSize" configName="ContentCatalogKeys" />
	</c:set>
	
	 <c:set var="pageGridClass" value=""/>
	<c:set var="gridClass" value="grid_10"/>
	<c:set var="facetClass" value="grid_2"/>
	
	<c:if test="${plpGridSize == '3'}">
		<c:if test="${defaultView == 'grid'}">
			<c:set var="pageGridClass" value="plp_view_3"/>
		</c:if>
		<c:set var="gridClass" value="grid_9"/>
		<c:set var="facetClass" value="grid_3"/>
	</c:if>

	<c:if test="${fromCollege eq 'true' }">
		<c:set var="variation" value="bc" scope="request" />
    </c:if>


			<dsp:importbean bean="/com/bbb/search/droplet/ResultListDroplet" />
			<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
			<dsp:importbean bean="/atg/multisite/Site"/>
			<dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
			<dsp:importbean bean="/atg/userprofiling/Profile" />
			<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
			<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BreadcrumbDroplet"/>

			<dsp:getvalueof var="categoryId" param="categoryId"/>

			<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
		    <dsp:getvalueof id="applicationId" bean="Site.id" />
			<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${applicationId}"/>


			<%-- Added as part of R2.2 SEO friendly URL Story : Start --%>
			<dsp:getvalueof	var="currentPage" param="browseSearchVO.pagingLinks.currentPage" scope="request" />
			<dsp:getvalueof	var="size" param="browseSearchVO.pagingLinks.pageSize" scope="request" />
			<dsp:getvalueof var="firstPageURL" param="browseSearchVO.pagingLinks.firstPage" scope="request" />
			<dsp:getvalueof var="filterApplied" param="browseSearchVO.pagingLinks.pageFilter" scope="request" />
			<dsp:getvalueof var="pageCount" param="browseSearchVO.pagingLinks.pageCount" scope="request" />
			<dsp:getvalueof var="nextPageParams" param="browseSearchVO.pagingLinks.nextPage" scope="request" />
			<dsp:getvalueof var="prevPageParams" param="browseSearchVO.pagingLinks.previousPage" scope="request" />

			<dsp:getvalueof param="CatalogRefId" var="CatalogRefId" />
			<dsp:getvalueof param="CatalogId" var="CatalogId" />
			<c:set var="subCategoryPageParam" value="" scope="request" />
			<%-- R2.2 - Story 116E Start --%>
			<c:if test="${empty frmBrandPage}">
				<c:if test="${not empty param.subCatPlp && param.subCatPlp eq true}">
					<c:set var="subCategoryPageParam">&subCatPlp=true&a=1</c:set>
				</c:if>
				<dsp:droplet name="/atg/repository/seo/CanonicalItemLink">
					<dsp:param name="id" value="${CatalogRefId}" />
					<dsp:param name="itemDescriptorName" value="category" />
					<dsp:param name="repositoryName" value="/atg/commerce/catalog/ProductCatalog" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="seoUrl" vartype="java.lang.String" param="url" scope="request" />
					</dsp:oparam>
				</dsp:droplet>
			</c:if>


			<c:set var="isFilterApplied" value="false" scope="request" />
			<c:choose>
				<c:when test="${not empty frmBrandPage and frmBrandPage eq true}">
					 <dsp:droplet name="ForEach">
						<dsp:param name="array" param="browseSearchVO.descriptors" />
						<dsp:oparam name="outputStart">
							<dsp:getvalueof var="descriptorLength" param="size"/>
						    <dsp:getvalueof var="type" param="element.rootName"/>
						    <c:if test="${(descriptorLength ge 1) && (type ne 'RECORD TYPE')}">
								<c:set var="isFilterApplied" value="true" scope="request" />
						     </c:if>
						</dsp:oparam>
					</dsp:droplet>
				</c:when>
				<c:otherwise>
					<dsp:droplet name="ForEach">
		               	<dsp:param name="array" param="browseSearchVO.descriptors" />
		               	<dsp:oparam name="outputStart">
		               		<dsp:getvalueof var="descriptorLength" param="size"/>
		                   	<dsp:getvalueof var="type" param="element.rootName"/>
		                 	<c:if test="${(descriptorLength ge 1) && (type ne 'DEPARTMENT')}">
		                 		<c:set var="isFilterApplied" value="true" scope="request" />
		                 	</c:if>
						</dsp:oparam>
		            </dsp:droplet>
				</c:otherwise>

			</c:choose>

		<%-- Added as part of R2.2 SEO friendly URL Story : End --%>
<%-- R2.2 - Story 116E END --%>
		<bbb:pageContainer>
			<jsp:attribute name="section">browse</jsp:attribute>
			<jsp:attribute name="pageWrapper">subCategory useCertonaJs ${pageGridClass} useScene7</jsp:attribute>
			<jsp:attribute name="pageVariation">${variation}</jsp:attribute>
			<jsp:attribute name="PageType">SubCategoryDetails</jsp:attribute>
	       <jsp:body>
			<script type="text/javascript">
				var resx = new Object();
				var linksCertona='';
			</script>

			<c:set var="department"><bbbl:label key="lbl_department" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="promoKeyTop"><bbbl:label key="lbl_promo_key_top" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="promoKeyRight"><bbbl:label key="lbl_promo_key_right" language="${pageContext.request.locale.language}" /></c:set>
			<dsp:getvalueof param="CatalogRefId" var="CatalogRefId" />
			<dsp:getvalueof param="browseSearchVO" var="browseSearchVO" />
			<dsp:getvalueof var="linkString" param="linkString" />
			<script type="text/javascript">
				linksCertona =linksCertona + '${linkString}';
			</script>
			<c:if test="${not frmBrandPage}">
				<dsp:droplet name="BreadcrumbDroplet">
					<dsp:param name="categoryId" param="categoryId" />
						<dsp:param name="siteId" value="${applicationId}"/>
						<dsp:oparam name="output">
							<dsp:droplet name="/atg/dynamo/droplet/ForEach">
							<dsp:param name="array" param="breadCrumb" />
								<dsp:oparam name="outputStart">
									<c:set var="categoryPath" scope="request"></c:set>
								</dsp:oparam>
								<dsp:oparam name="output">
									<%-- Start: Added for Scope # 81 H1 tags --%>
									<dsp:getvalueof var="count" param="count" />
										<c:if test="${count eq 2 }">
											<c:set var="CatFlagL2" scope="request" value="true"></c:set>
											<c:set var="CatNameL2" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
										</c:if>
										<c:if test="${count eq 3 }">
											<c:set var="CatFlagL3" scope="request" value="true"></c:set>
											<c:set var="CatNameL3" scope="request"><dsp:valueof param="element.categoryName" /></c:set>
										</c:if>
										<%-- End: Added for Scope # 81 H1 tags --%>
									<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="element.categoryName" /></c:set>
								</dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
				</dsp:droplet>
				<c:if test="${TagManOn}">
					<c:choose>
						<c:when test="${frmBrandPage}">
							<dsp:include page="/tagman/frag/search_frag.jsp" >
								<dsp:param name="searchTerm" value="${Keyword}"/>
							</dsp:include>
						</c:when>
						<c:otherwise>
							<dsp:include page="/tagman/frag/subcategory_frag.jsp">
			       				<dsp:param name="categoryPath" value="${categoryPath}"/>
			      			</dsp:include>
						</c:otherwise>
					</c:choose>
				</c:if>
				<dsp:droplet name="ForEach">
					<dsp:param name="array" param="browseSearchVO.categoryHeader.categoryTree" />
	             	<dsp:oparam name="output">
	             		<dsp:getvalueof var="index" param="index"/>
	             		<c:if test="${index eq 0}">
	             			<dsp:getvalueof var="var1" param="element"/>
	             		</c:if>
	             		<c:if test="${index eq 1}">
	             			<dsp:getvalueof var="var2" param="element"/>
	             		</c:if>
	             		<c:if test="${index eq 2}">
	             			<dsp:getvalueof var="var3" param="element"/>
	             		</c:if>
	             	</dsp:oparam>
				</dsp:droplet>

			</c:if>
			
			<%-- KP COMMENT: TEMP WRAPPER --%>
			<div class="row">

					<div id="subHeader" class="container_12 clearfix">
						<div class="grid_12 marTop_10">

							<!-- JIRA Defect # BBBSL-2 START -->
							<c:if test="${not frmBrandPage}">
								<dsp:getvalueof var="subCatHeader"  param="browseSearchVO.categoryHeader.name"/>
								<dsp:getvalueof var="floatNode"  value="false"/>
								<c:set var="floatCatName"><bbbc:config key="float_node_cat_name" configName="DimNonDisplayConfig" /></c:set>

								<%--<c:if test="${subCatHeader eq 'Floating Nodes'}">--%>
								<c:if test="${fn:toLowerCase(subCatHeader) == fn:toLowerCase(floatCatName)}">
									<dsp:droplet name="/atg/commerce/catalog/CategoryLookup">
										<dsp:param name="id" param="catID" />
										<dsp:param name="elementName" value="mainCategory" />
										<dsp:oparam name="output">
						                	<dsp:getvalueof var="categoryName" param="mainCategory.displayName" />
						                </dsp:oparam>
						           	</dsp:droplet>
									<dsp:getvalueof var="subCatHeader"  value="${categoryName}"/>
									<dsp:getvalueof var="floatNode"  value="true"/>
								</c:if>
								<!-- JIRA Defect # BBBSL-2 END  -->

								<!-- RS # 2094542 -- To see if chat link will come on this page request or not. If yes, -->
								<dsp:getvalueof var="categoryVO" param="categoryVO" />
			                   	<dsp:getvalueof var="chatLinkPlaceholder" param="categoryVO.chatLinkPlaceholder" />
								<dsp:getvalueof var="chatEnabled" param="categoryVO.isChatEnabled" />
								<dsp:getvalueof var="chatLink" value=""/>
								<c:choose>
									<c:when test="${(not empty chatEnabled)  && (chatEnabled eq 'true') && (chatLinkPlaceholder eq 'Top Right')}">
										<dsp:getvalueof var="chatLink" value="true"/>
									</c:when>
									<c:when test="${(empty chatEnabled)}">
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
											<dsp:param bean="/atg/commerce/catalog/CatalogNavHistory.navHistory" name="array" />
											<dsp:param name="reverseOrder" value="true" />
											<dsp:param name="elementName" value="cat" />
											<dsp:oparam name="output">
												<dsp:getvalueof param="size" id="size"/>
												<dsp:getvalueof param="count" id="count"/>
													<c:if test="${(size eq 4) and (count eq 2)}">
														<dsp:droplet name="/com/bbb/commerce/browse/droplet/CategoryLandingDroplet">
															<dsp:param param="cat.repositoryId" name="id" />
															<dsp:param name="siteId" value="${applicationId}"/>
															<dsp:oparam name="error">
																<dsp:include page="../404.jsp" flush="true"/>
															</dsp:oparam>
															<dsp:oparam name="subcat">
																<dsp:getvalueof id="parentCatVO" param="categoryVO"/>
																<dsp:getvalueof var="parentCatChatLinkPlaceholder" value="${parentCatVO.chatLinkPlaceholder}" />
																<dsp:getvalueof var="parentChatEnabled" value="${parentCatVO.isChatEnabled}" />
																<c:if test="${(not empty parentChatEnabled)  && (parentChatEnabled eq 'true') && (parentCatChatLinkPlaceholder eq 'Top Right')}">
																	<dsp:getvalueof var="chatLink" value="true"/>
																</c:if>
															</dsp:oparam>
														</dsp:droplet>
													</c:if>
											</dsp:oparam>
										</dsp:droplet>
									</c:when>
								</c:choose>
							</c:if>
							<!-- RS # 2094542 -- To see if chat link will come on this page request or not. If yes, -->

							<c:if test="${frmBrandPage }">
								<h1><dsp:valueof value="${Keyword}" valueishtml="true"/></h1>
							</c:if>

							<dsp:droplet name="ForEach">
		                     	<dsp:param name="array" param="browseSearchVO.promoMap" />
		                        <dsp:oparam name="output">
		                        	<dsp:getvalueof var="elementList" param="element"/>
		                            <dsp:getvalueof var="key1" param="key"/>
		                            <%-- <c:choose>	    --%>
		                            <c:if test="${(empty frmBrandPage || !frmBrandPage) && key1 eq 'CENTER'}">
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
							                    <dsp:getvalueof var="imageAlt" param="element.imageAlt"/>
							                    <dsp:getvalueof var="seoText" param="element.seoText"/>
							                </dsp:oparam>
							              	<dsp:oparam name="empty">
							              		<dsp:getvalueof var="imageHREF" value=""/>
							                    <dsp:getvalueof var="imageURL" value=""/>
							                    <dsp:getvalueof var="imageAlt" value=""/>
							                    <dsp:getvalueof var="seoText" value=""/>
					                        </dsp:oparam>
							           	</dsp:droplet>
							           	<%-- Start: Added for Scope # 81 H1 tags --%>
							           	<c:choose>
					                    	<c:when test="${(not empty seoText) && (not empty imageURL) }">
						                    	<div class="grid_7 alpha">
						                    		<c:choose>
						                    			<c:when test="${!(CatFlagL3 eq null)}">
						                    				<h1 class="txtOffScreen"><dsp:valueof value="${CatNameL3}" valueishtml="true"/></h1>
						                    				<h2 class="txtOffScreen"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h2>
						                    				<p class="catHeader"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></p>
						                    			</c:when>
						                    			<c:otherwise>
						                    				<c:choose>
						                    					<c:when test="${!(CatFlagL2 eq null) and (subCatHeader eq CatNameL2)}">
								                    				<h1 ><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
						                    					</c:when>
						                    					<c:otherwise>
						                    						<c:choose>
						                    							<c:when test="${!(CatFlagL2 eq null) and (subCatHeader ne CatNameL2)}">
						                    								<h1 class="txtOffScreen"><dsp:valueof value="${CatNameL2}" valueishtml="true"/></h1>
						                    								<h2 class="txtOffScreen"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h2>
						                    								<p class="catHeader"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></p>
						                    							</c:when>
						                    							<c:otherwise>
						                    								<h1><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
						                    							</c:otherwise>
						                    						</c:choose>
						                    					</c:otherwise>
						                    				</c:choose>
						                    			</c:otherwise>
						                    		</c:choose>
													<p class="catSEOText">${seoText}</p>
												</div>
												<div class="grid_5 omega marBottom_10 promoImage">
													<a href="${imageHREF}" title="${imageAlt}"><img src="${scene7Path}/${imageURL}" alt="${imageAlt}"  height="80" width="395"/></a>
												</div>
						                    </c:when>
						                   	<c:otherwise>
						                   		<!-- RM Defect 15730: ALL Concepts: Endeca Center Column Promo Bug -->
						                   		<!-- <div class="grid_7 alpha"> -->
						                   			<c:choose>
							                   			<c:when test="${(not empty seoText)}">

							                   				<!-- RS # 2094542 -- If no promo image and chat link is on, then change grid_12 to grid_9 -->
															<c:choose>
							                   					<c:when test="${chatLink eq true}">
							                   						<div class="grid_9 alpha">
							                   					</c:when>
							                   					<c:otherwise>
							                   						<div class="grid_12 alpha">
							                   					</c:otherwise>
							                   				</c:choose>
							                   				<!-- RS # 2094542 -- If no promo image and chat link is on, then change grid_12 to grid_9 -->
							                   						<c:choose>
										                    			<c:when test="${!(CatFlagL3 eq null)}">
										                    				<h1 class="txtOffScreen"><dsp:valueof value="${CatNameL3}" valueishtml="true"/></h1>
										                    				<h2 class="txtOffScreen"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h2>
										                    				<p class="catHeader"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></p>
										                    			</c:when>
										                    			<c:otherwise>
										                    				<c:choose>
										                    					<c:when test="${!(CatFlagL2 eq null) and (subCatHeader eq CatNameL2)}">
												                    				<h1><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
										                    					</c:when>
										                    					<c:otherwise>
										                    						<c:choose>
										                    							<c:when test="${!(CatFlagL2 eq null) and (subCatHeader ne CatNameL2)}">
										                    								<h1 class="txtOffScreen"><dsp:valueof value="${CatNameL2}" valueishtml="true"/></h1>
										                    								<h2 class="txtOffScreen"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h2>
										                    								<p class="catHeader"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></p>
										                    							</c:when>
										                    							<c:otherwise>
										                    								<h1><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
										                    							</c:otherwise>
										                    						</c:choose>
										                    					</c:otherwise>
										                    				</c:choose>
										                    			</c:otherwise>
										                    		</c:choose>
								                   				<p class="catSEOText">${seoText}</p>
								                   			</div>
														</c:when>
														<c:otherwise>
															<c:choose>
																<c:when test="${empty imageURL}">

																	<!-- RS # 2094542 -- If no promo image and chat link is on, then change grid_12 to grid_9 -->
																	<c:choose>
									                   					<c:when test="${chatLink eq true}">
									                   						<div class="grid_9 alpha">
									                   					</c:when>
									                   					<c:otherwise>
																			<div class="grid_12 alpha">
									                   					</c:otherwise>
									                   				</c:choose>
									                   				<!-- RS # 2094542 -- If no promo image and chat link is on, then change grid_12 to grid_9 -->
											                   				<c:choose>
												                    			<c:when test="${!(CatFlagL3 eq null)}">
												                    				<h1 class="txtOffScreen"><dsp:valueof value="${CatNameL3}" valueishtml="true"/></h1>
												                    				<h2 class="txtOffScreen"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h2>
												                    				<p class="catHeader"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></p>
												                    			</c:when>
												                    			<c:otherwise>
												                    				<c:choose>
												                    					<c:when test="${!(CatFlagL2 eq null) and (subCatHeader eq CatNameL2)}">
														                    				<h1 ><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
												                    					</c:when>
												                    					<c:otherwise>
												                    						<c:choose>
												                    							<c:when test="${!(CatFlagL2 eq null) and (subCatHeader ne CatNameL2)}">
												                    								<h1 class="txtOffScreen"><dsp:valueof value="${CatNameL2}" valueishtml="true"/></h1>
												                    								<h2 class="txtOffScreen"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h2>
												                    								<p class="catHeader"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></p>
												                    							</c:when>
												                    							<c:otherwise>
												                    								<h1><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
												                    							</c:otherwise>
												                    						</c:choose>
												                    					</c:otherwise>
												                    				</c:choose>
												                    			</c:otherwise>
												                    		</c:choose>
																	</div>
																</c:when>
																<c:otherwise>
																	<div class="grid_7 alpha">
																		<c:choose>
											                    			<c:when test="${!(CatFlagL3 eq null)}">
											                    				<h1 class="txtOffScreen"><dsp:valueof value="${CatNameL3}" valueishtml="true"/></h1>
											                    				<h2 class="txtOffScreen"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h2>
											                    				<p class="catHeader"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></p>
											                    			</c:when>
											                    			<c:otherwise>
											                    				<c:choose>
											                    					<c:when test="${!(CatFlagL2 eq null) and (subCatHeader eq CatNameL2)}">
													                    				<h1><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
											                    					</c:when>
											                    					<c:otherwise>
											                    						<c:choose>
											                    							<c:when test="${!(CatFlagL2 eq null) and (subCatHeader ne CatNameL2)}">
											                    								<h1 class="txtOffScreen"><dsp:valueof value="${CatNameL2}" valueishtml="true"/></h1>
											                    								<h2 class="txtOffScreen"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h2>
											                    								<p class="catHeader"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></p>
											                    							</c:when>
											                    							<c:otherwise>
											                    								<h1><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
											                    							</c:otherwise>
											                    						</c:choose>
											                    					</c:otherwise>
											                    				</c:choose>
											                    			</c:otherwise>
											                    		</c:choose>
																	</div>
																	<div class="grid_5 omega marBottom_10 promoImage">
																		<a href="${imageHREF}" title="${imageAlt}"><img src="${scene7Path}/${imageURL}" alt="${imageAlt}" height="80" width="395"/></a>
																	</div>
																</c:otherwise>
															</c:choose>
														</c:otherwise>
													</c:choose>
						                   		<!-- </div> -->
						                   		<!-- RM Defect 15730: ALL Concepts: Endeca Center Column Promo Bug -->
						                   	</c:otherwise>
						             	</c:choose>
		                            </c:if>
		                            <c:if test="${key1 eq promoKeyRight}">
		                         		<dsp:droplet name="ForEach">
		                                	<dsp:param name="array" value="${elementList}" />
		                                    <dsp:oparam name="output">
				                            	 <c:set var="promoSC" value="true"></c:set>
		                                  	</dsp:oparam>
		                            	</dsp:droplet>
			                        </c:if>
			                      	<!-- R2.2 BRAND Promo container from BCC Start -->
			                        <c:if test="${(frmBrandPage and !isPromoContentAvailable) && key1 eq promoKeyTop &&  not empty elementList}">
		                           		<div class="clearfix promo-12col">
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
			                        <!-- R2.2 BRAND Promo container from BCC END -->
                         			</c:if>
			                            <%--
			                            <c:otherwise>
				                        	<div class="grid_12 alpha">
				                        		<h1><dsp:valueof param="browseSearchVO.categoryHeader.name" /></h1>
				                        	</div>
				                        </c:otherwise> --%>
			                        <%-- </c:choose> --%>
		                        </dsp:oparam>
		                        <dsp:oparam name="empty">
		                            <div class="grid_7 alpha omega">
		                            	<c:choose>
			                    			<c:when test="${!(CatFlagL3 eq null)}">
			                    				<h1 class="txtOffScreen"><dsp:valueof value="${CatNameL3}" valueishtml="true"/></h1>
			                    				<h2 class="txtOffScreen"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h2>
			                    				<p class="catHeader"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></p>
			                    			</c:when>
			                    			<c:otherwise>
			                    				<c:choose>
			                    					<c:when test="${!(CatFlagL2 eq null) and (subCatHeader eq CatNameL2)}">
					                    				<h1 ><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
			                    					</c:when>
			                    					<c:otherwise>
			                    						<c:choose>
			                    							<c:when test="${!(CatFlagL2 eq null) and (subCatHeader ne CatNameL2)}">
			                    								<h1 class="txtOffScreen"><dsp:valueof value="${CatNameL2}" valueishtml="true"/></h1>
			                    								<h2 class="txtOffScreen"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h2>
			                    								<p class="catHeader"><dsp:valueof value="${subCatHeader}" valueishtml="true"/></p>
			                    							</c:when>
			                    							<c:otherwise>
			                    								<h1><dsp:valueof value="${subCatHeader}" valueishtml="true"/></h1>
			                    							</c:otherwise>
			                    						</c:choose>
			                    					</c:otherwise>
			                    				</c:choose>
			                    			</c:otherwise>
			                    		</c:choose>
		                        	</div>
		                        </dsp:oparam>

		                   	<%-- End: Added for Scope # 81 H1 tags --%>
		                   	</dsp:droplet>
							<c:if test="${frmBrandPage and isPromoContentAvailable}">
							    <script type="text/javascript" src="${jsFilePath}"></script>
								<link rel="stylesheet" type="text/css" href="${cssFilePath }" />
							        <div id="bannerContent" class="marBottom_10 clearfix">
										${promoContent}
									</div>
							</c:if>

		                   	<dsp:droplet name="/atg/commerce/catalog/CategoryLookup">
								<dsp:param param="categoryId" name="id" />
								<dsp:oparam name="output">
									<dsp:droplet name="/atg/commerce/catalog/CatalogNavHistoryCollector">
										<dsp:param param="element" name="item" />
										<dsp:param value="jump" name="navAction" />
									</dsp:droplet>
								</dsp:oparam>
							</dsp:droplet>
		                   	<!-- included as part of Release 2.1 implementation -->
							<dsp:getvalueof var="categoryVO" param="categoryVO" />
		                   	<dsp:getvalueof var="chatLinkPlaceholder" param="categoryVO.chatLinkPlaceholder" />
							<dsp:getvalueof var="chatEnabled" param="categoryVO.isChatEnabled" />

							<c:if test="${not frmBrandPage}">
								<!-- RS # 2094542 -- To render chat on link here only if there is no promotion set. -->
								<c:if test="${empty imageURL}">
								<!-- RS # 2094542 -- To render chat on link here only if there is no promotion set. -->
									<c:choose>
										<%-- Commenting Click to Chat as part of 34473 
										<c:when test="${(not empty chatEnabled)  && (chatEnabled eq 'true') && (chatLinkPlaceholder eq 'Top Right')}">
											<dsp:include page="/common/click2chatlink.jsp">
												<dsp:param name="chatURL" value="${categoryVO.chatURL}"/>
												<dsp:param name="pageId" value="${categoryVO.chatCode}"/>
												<dsp:param name="catId" value="${catID}"/>
												<dsp:param name="divApplied" value="${true}"/>
												<dsp:param name="divClass" value="grid_3 omega fr"/>
											</dsp:include>
										</c:when>
										--%>
										<c:when test="${(empty chatEnabled)}">
											<dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param bean="/atg/commerce/catalog/CatalogNavHistory.navHistory" name="array" />
												<dsp:param name="reverseOrder" value="true" />
												<dsp:param name="elementName" value="cat" />
												<dsp:oparam name="output">
													<dsp:getvalueof param="size" id="size"/>
													<dsp:getvalueof param="count" id="count"/>
													<c:if test="${(size eq 4) and (count eq 2)}">
														<dsp:droplet name="/com/bbb/commerce/browse/droplet/CategoryLandingDroplet">
															<dsp:param param="cat.repositoryId" name="id" />
															<dsp:param name="siteId" value="${applicationId}"/>
															<dsp:oparam name="error">
																<dsp:include page="../404.jsp" flush="true"/>
															</dsp:oparam>
															<dsp:oparam name="subcat">
																<dsp:getvalueof id="parentCatVO" param="categoryVO"/>
																<dsp:getvalueof var="parentCatChatLinkPlaceholder" value="${parentCatVO.chatLinkPlaceholder}" />
																<dsp:getvalueof var="parentChatEnabled" value="${parentCatVO.isChatEnabled}" />
																<dsp:getvalueof id="chatEnabled" value="${parentCatVO.isChatEnabled}" />
																<%-- Commenting Click to Chat as part of 34473 
																<c:if test="${(not empty parentChatEnabled)  && (parentChatEnabled eq 'true') && (parentCatChatLinkPlaceholder eq 'Top Right')}">
																	 <dsp:include page="/common/click2chatlink.jsp">
																		<dsp:param name="chatURL" value="${categoryVO.chatURL}"/>
																		<dsp:param name="pageId" value="${categoryVO.chatCode}"/>
																		<dsp:param name="catId" value="${catID}"/>
																		<dsp:param name="divApplied" value="${true}"/>
																		<dsp:param name="divClass" value="grid_3 omega fr"/>
																	 </dsp:include>
																</c:if>
																--%>
									                        </dsp:oparam>
									                   	</dsp:droplet>
													</c:if>
												</dsp:oparam>
											</dsp:droplet>
										</c:when>
									</c:choose>
								</c:if>
			                   	 <c:if test="${fromCollege eq 'true' }">
			                   	  <bbbl:textArea key="txt_college_cat_image_grid" language="${pageContext.request.locale.language}"/>
								 </c:if>
							</c:if>
						</div>
					</div>
					<dsp:getvalueof var="portrait" value="false"/>
					<dsp:getvalueof var="refinements" param="browseSearchVO.categoryHeader.categoryRefinement"/>
					<dsp:getvalueof var="phantomCategory" param="browseSearchVO.categoryHeader.phantomCategory"/>
					<c:if test="${(refinements eq null || empty refinements ) && phantomCategory}">
						<c:set var="dontShowAllTab" value="true"></c:set>
					</c:if>
					<%--R2.2 Story 116C Changes--%>
					<div id="content" class="container_12 clearfix">

						<%-- Commented as part of R2.2 SEO friendly URL Story : Start --%>
						<%--
						<dsp:getvalueof var="url" vartype="java.lang.String"  value="?a=1"/>
						<c:if test="${param.subCatPlp eq true}">
							<dsp:getvalueof var="url" vartype="java.lang.String" value="?subCatPlp=true&a=1"/>
						</c:if>
						--%>
						<%-- Commented as part of R2.2 SEO friendly URL Story : End --%>

						<dsp:getvalueof var="facets" param="browseSearchVO.facets"/>
						<c:if test="${not empty facets}">
							<div class="<c:out value="${facetClass}"/> marTop_20">
								<c:if test="${not frmBrandPage}">
									<!-- included as part of Release 2.1 implementation -->
									<dsp:getvalueof var="chatLinkPlaceholder" param="categoryVO.chatLinkPlaceholder" />
									<dsp:getvalueof var="chatURL" param="categoryVO.chatURL" />
									<dsp:getvalueof var="chatCode" param="categoryVO.chatCode" />

									<dsp:getvalueof var="parChatEnabled" value="${parentCatVO.isChatEnabled}" />
									<dsp:getvalueof var="parCatChatLinkPlaceholder" value="${parentCatVO.chatLinkPlaceholder}" />
									<dsp:getvalueof var="parChatURL" value="${parentCatVO.chatURL}" />
									<dsp:getvalueof var="parChatCode" value="${parentCatVO.chatCode}" />
									
									<%-- Commenting Click to Chat as part of 34473 
									<c:choose>
										<c:when test="${not empty parentCatVO}">
											<c:if test="${parChatEnabled && (parCatChatLinkPlaceholder eq 'Top Left')}">
												<dsp:include page="/common/click2chatlink.jsp">
													<dsp:param name="chatURL" value="${parChatURL}"/>
													<dsp:param name="pageId" value="${parChatCode}"/>
													<dsp:param name="catId" value="${catID}"/>
													<dsp:param name="divApplied" value="${true}"/>
													<dsp:param name="divClass" value="marTop_20 marBottom_20 chatNowLeft"/>
												</dsp:include>
											</c:if>
										</c:when>
										<c:when test="${chatEnabled && (chatLinkPlaceholder eq 'Top Left')}">
											<dsp:include page="/common/click2chatlink.jsp">
										 		<dsp:param name="chatURL" value="${chatURL}"/>
							             		<dsp:param name="pageId" value="${chatCode}"/>
							             		<dsp:param name="catId" value="${catID}"/>
							                	<dsp:param name="divApplied" value="${true}"/>
												<dsp:param name="divClass" value="marTop_20 marBottom_20 chatNowLeft"/>
							             	</dsp:include>
										</c:when>
									</c:choose>
									--%>
								</c:if>
								<dsp:droplet name="ForEach">
	                               	<dsp:param name="array" param="browseSearchVO.descriptors" />
	                               	<dsp:oparam name="outputStart">
	                               		<dsp:getvalueof var="totSize" param="size"/>
	                       			</dsp:oparam>
	                          	</dsp:droplet>

	                          <%--R2.2 Story 116C  Changes--%>
							<c:choose>
								<c:when test="${frmBrandPage }">
									<dsp:include page="/_includes/modules/faceted_bar.jsp">
										<dsp:param name="frmSubCatBrandPage" value="${frmBrandPage}"/>
										<dsp:param name="view" value="${defaultView}" />
									</dsp:include>
								</c:when>
								<c:otherwise>
									<dsp:include page="/_includes/modules/faceted_bar_plp.jsp">
										<dsp:param name="browseSearchVO" param="browseSearchVO" />
										<dsp:param name="seoUrl" value="${seoUrl}" />
										<dsp:param name="showDepartment" value="false" />
										<dsp:param name="totSize" value="${totSize}" />
										<dsp:param name="view" value="${defaultView}" />
										<dsp:param name="floatNode" value="${floatNode}"/>
									</dsp:include>
								</c:otherwise>
							</c:choose>


								<!-- included as part of Release 2.1 implementation -->
								<%-- R2.2 - Story 116E Start --%>
								<%-- Commenting Click to Chat as part of 34473 
								<c:if test="${not frmBrandPage}">
									<c:choose>
										<c:when test="${not empty parentCatVO}">
											<c:if test="${parChatEnabled and (parCatChatLinkPlaceholder eq 'Bottom Left')}">
												<dsp:include page="/common/click2chatlink.jsp">
													<dsp:param name="chatURL" value="${parChatURL}"/>
													<dsp:param name="pageId" value="${parChatCode}"/>
													<dsp:param name="catId" value="${catID}"/>
													<dsp:param name="divApplied" value="${true}"/>
													<dsp:param name="divClass" value="marTop_10 chatNowLeft"/>
												</dsp:include>
											</c:if>
										</c:when>
										<c:when test="${chatEnabled and (chatLinkPlaceholder eq 'Bottom Left')}">
											<dsp:include page="/common/click2chatlink.jsp">
												<dsp:param name="chatURL" value="${chatURL}"/>
									            <dsp:param name="pageId" value="${chatCode}"/>
												<dsp:param name="catId" value="${catID}"/>
												<dsp:param name="divApplied" value="${true}"/>
												<dsp:param name="divClass" value="marTop_10 chatNowLeft"/>
									    	</dsp:include>
								        </c:when>
									</c:choose>
								</c:if>
								--%>
								<%-- R2.2 - Story 116E End --%>
							</div>
						</c:if>

						<div id="prodGridContainer" class="<c:out value="${gridClass}"/> noMarTop clearfix">


							<div id="pagTop" class="<c:out value="${gridClass}"/> alpha omega">

								<dsp:include page="/_includes/modules/pagination_top_1bar.jsp">
								<dsp:param name="subCategoryPageParam" value="${subCategoryPageParam}"/>
								<dsp:param name="view" value="${defaultView}"/>
								<dsp:param name="showViewOptions" value="true"/>
								</dsp:include>
							</div>
								<%-- Start: If view is selected as list, then list view of PLP will be shown else grid view is displayed. R2.2 116-g --%>
								<dsp:droplet name="ResultListDroplet">
									<dsp:param name="browseSearchVO" value="${browseSearchVO}" />
									<dsp:oparam name="output">
									<c:choose>
										<c:when test="${defaultView=='list'}">
											<dsp:include page="/_includes/modules/product_list.jsp">
												<dsp:param name="BBBProductListVO" param="BBBProductListVO" />
												<dsp:param name="promoSC" value="${promoSC}"/>
												<dsp:param name="url" value="${url}" />
												<dsp:param name="portrait" value="${portrait}"/>
												<dsp:param name="browseSearchVO" value="${browseSearchVO}"/>
											</dsp:include>
										</c:when>
										<c:otherwise>
											<dsp:include page="/_includes/modules/product_grid_5x4.jsp">
												<dsp:param name="BBBProductListVO" param="BBBProductListVO" />
												<dsp:param name="promoSC" value="${promoSC}"/>
												<dsp:param name="url" value="${url}" />
												<dsp:param name="plpGridSize" value="${plpGridSize}"/>
												<dsp:param name="portrait" value="${portrait}"/>
												<dsp:param name="browseSearchVO" value="${browseSearchVO}"/>
												<%-- Including new jsp for 4x4 view |List View Redesign Story |Sprint2 |START --%>
												<c:if test="${defaultView=='grid4'}">
													<dsp:param name="plpGridSize" value="4" />
												</c:if>
												<%-- Including new jsp for 4x4 view |List View Redesign Story |Sprint2 |End --%> 
									   </dsp:include>
										</c:otherwise>
									</c:choose>
									</dsp:oparam>
								</dsp:droplet>
								<%-- End: If view is selected as list, then list view of PLP will be shown else grid view is displayed. R2.2 116-g --%>

							<%-- R2.2 Stroy 116. Display promo content images only when grid size is 5x5 --%>
							<c:if test="${promoSC and plpGridSize == '5'}">
							<div class="<c:out value="${facetClass}"/> omega">
	                        	<dsp:droplet name="ForEach">
		                     		<dsp:param name="array" param="browseSearchVO.promoMap" />
		                        	<dsp:oparam name="output">
		                        		<dsp:getvalueof var="elementList" param="element"/>
		                            	<dsp:getvalueof var="key2" param="key"/>
			                            <c:if test="${key2 eq promoKeyRight}">
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
								                    <dsp:getvalueof var="imageAlt" param="element.imageAlt"/>
													<div class="promo-2col">
					                                	<a href="${imageHREF}" title="${imageAlt}"><img src="${imagePath}${imageURL}" alt="${imageAlt}" /></a>
		                                            </div>
			                                    </dsp:oparam>
			                             	</dsp:droplet>
			                          	</c:if>
		                    		</dsp:oparam>
		                    	</dsp:droplet>
							</div>
						</c:if>

							<div id="pagBot" class="<c:out value="${gridClass}"/> alpha omega">
								<dsp:include page="/_includes/modules/pagination_top_1bar.jsp">
								<dsp:param name="subCategoryPageParam" value="${subCategoryPageParam}"/>
								<dsp:param name="view" value="${defaultView}"/>
								</dsp:include>
							</div>
							<%-- START : Get Blurb of text in Botom of PLP if set. --%>
							<dsp:droplet name="ForEach">
	                     		<dsp:param name="array" param="browseSearchVO.promoMap" />
	                        	<dsp:oparam name="output">
	                        		<dsp:getvalueof var="elementList" param="element"/>
	                            	<dsp:getvalueof var="key2" param="key"/>
		                            <c:if test="${key2 eq 'FOOTER'}">
		                            	<div class="<c:out value="${gridClass}"/> alpha omega clearfix blurbTxt">
		                            	<dsp:droplet name="ForEach">
		                                   <dsp:param name="array" value="${elementList}" />
		                                    <dsp:oparam name="output">
		                                    	<p><dsp:valueof param="element.blurbPlp" valueishtml="true"/></p>
		                                    </dsp:oparam>
		                             	</dsp:droplet>
		                             	</div>
		                          	</c:if>
	                    		</dsp:oparam>
	                    	</dsp:droplet>
	                    	<%-- END : Get Blurb of text in Botom of PLP if set. --%>
						</div>
					</div>
				<%-- KP COMMENT CLOSING TEMP CONTAINER --%>
				</div>
				
			<c:if test="${TellApartOn}">
				<c:choose>
					<c:when test="${frmBrandPage }">
						<bbb:tellApart actionType="pv" pageViewType="SearchResult" />
					</c:when>
					<c:otherwise>
						<bbb:tellApart actionType="pv" pageViewType="ProductCategory" />
					</c:otherwise>
				</c:choose>
			</c:if>
			<dsp:droplet name="Switch">
			<dsp:param name="value" bean="Profile.transient"/>
			<dsp:oparam name="false">
				<dsp:getvalueof var="userId" bean="Profile.id"/>
			</dsp:oparam>
			<dsp:oparam name="true">
				<dsp:getvalueof var="userId" value=""/>
			</dsp:oparam>
		</dsp:droplet>
<!-- 		<script type="text/javascript">
				resx.appid = "${appIdCertona}";
				resx.links = linksCertona;
				resx.customerid = "${userId}";


		</script> -->



		<dsp:getvalueof var="var1" value="" />
		<dsp:getvalueof var="var2" value="" />
		<dsp:getvalueof var="var3" value="" />
		 <dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param name="array"
					param="browseSearchVO.categoryHeader.categoryTree" />
		             	<dsp:oparam name="output">
		             		<dsp:getvalueof var="index" param="index" />
		             		<c:if test="${index eq 0}">
		             			<dsp:getvalueof var="var1" param="element" />
		             		</c:if>
		             		<c:if test="${index eq 1}">
		             			<dsp:getvalueof var="var2" param="element" />
		             		</c:if>
		             		<c:if test="${index eq 2}">
		             			<dsp:getvalueof var="var3" param="element" />
		             		</c:if>
		             	</dsp:oparam>
					</dsp:droplet>
				<dsp:importbean bean="/atg/multisite/Site"/>
				<dsp:getvalueof var="applicationId" bean="Site.id" />

			<dsp:droplet name="/com/bbb/account/droplet/BBBConfigKeysDroplet">
			    <dsp:param name="configKey" value="DimNonDisplayConfig"/>
			    <dsp:oparam name="output">
			        <dsp:getvalueof var="configMap" param="configMap"/>
			    </dsp:oparam>
			</dsp:droplet>
			<c:forEach var="conMap" items="${configMap}">
			    <c:if test="${conMap.key eq applicationId}">
				    <c:choose>
					<c:when test="${fn:contains(conMap.value, var1)}">
						<c:set var="prop2Var" value=""/>
						<c:choose>
						<c:when test="${empty var3}">
							<c:set var="prop3Var">${var1} > ${var1} > ${var2}</c:set>
						</c:when>
						<c:otherwise>
							<c:set var="prop3Var">${var1} > ${var2} > ${var3}</c:set>
						</c:otherwise>
						</c:choose>
						<dsp:getvalueof var="phantomCategory" param="browseSearchVO.categoryHeader.phantomCategory"/>
						<dsp:getvalueof var="phantomCategoryName" param="browseSearchVO.categoryHeader.name"/>
						<c:if test="${not empty phantomCategory && phantomCategory == 'true'}">
							<c:set var="pageName">${phantomCategoryName}</c:set>
							<c:set var="var1">Floating nodes</c:set>
							<c:set var="prop1Var">Category Page</c:set>
							<c:set var="prop2Var">${phantomCategoryName}</c:set>
							<c:set var="prop3Var">Category Page</c:set>
							<c:set var="var3">${var2}</c:set>
						</c:if>
						<c:set var="prop4Var" value=""/>
						<c:set var="prop5Var" value=""/>
						<c:set var="pageName">Associate Incentive</c:set>
						<c:set var="var1">Associate Incentive</c:set>
						<c:set var="prop1Var">Category Page</c:set>
						<c:set var="prop2Var">Associate Incentive</c:set>
						<c:set var="prop3Var">Category Page</c:set>
					</c:when>
					<c:otherwise>
						<c:set var="prop2Var">${var1} > ${var2}</c:set>
						<c:choose>
						<c:when test="${empty var3}">
							<c:choose>
							<c:when test="${var1=='Whats New' || var1=='Clearance'}">
								<c:set var="pageName">${var1} > ${var1} > ${var2}</c:set>
								<c:set var="prop1Var">Sub Category Page</c:set>
								<c:set var="prop2Var"></c:set>
								<c:set var="prop3Var">${var1} > ${var1} > ${var2}</c:set>
								<c:if test="${empty var2 || var2==1 || var2==2 || var2==3}">
									<c:set var="prop1Var">Category Page</c:set>
									<c:set var="pageName">${var1} > ${var1} > All</c:set>
									<c:set var="prop2Var">${var1} > ${var1}</c:set>
									<c:set var="prop3Var">${var1} > ${var1} > All</c:set>
								</c:if>
							</c:when>
							<c:otherwise>
							<c:set var="pageName">${var1} > ${var2}</c:set>
							<c:set var="prop1Var">Category Page</c:set>
								<c:set var="prop3Var">Category Page</c:set>
							</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${var3 == 1 || var3 == 2 || var3 == 3}">
									<c:choose>
										<c:when test="${var1=='Whats New'}">
											<c:set var="pageName">${var1} > ${var1} > ${var2}</c:set>
											<c:set var="prop1Var">Sub Category Page</c:set>
											<c:set var="prop2Var"></c:set>
											<c:set var="prop3Var">${var1} > ${var1} > ${var2}</c:set>
											<c:if test="${empty var2 || var2==1 || var2==2}">
												<c:set var="prop1Var">Category Page</c:set>
												<c:set var="pageName">${var1} > ${var1} > All</c:set>
												<c:set var="prop2Var">${var1} > ${var1}</c:set>
                                                <c:set var="prop3Var">${var1} > ${var1} > All</c:set>
											</c:if>
										</c:when>
										<c:when test="${var1=='Clearance'}">
											<c:set var="pageName">${var1} > ${var1} > ${var2}</c:set>
											<c:set var="prop1Var">Sub Category Page</c:set>
											<c:set var="prop2Var"></c:set>
											<c:set var="prop3Var">${var1} > ${var1} > ${var2}</c:set>
											<c:if test="${empty var2 || var2==1 || var2==2 || var2==3}">
												<c:set var="prop1Var">Category Page</c:set>
												<c:set var="pageName">${var1} > ${var1} > All</c:set>
												<c:set var="prop2Var">${var1} > ${var1}</c:set>
												<c:set var="prop3Var">${var1} > ${var1} > All</c:set>
											</c:if>
										</c:when>
										<c:otherwise>
									<c:set var="pageName">${var1} > ${var2}</c:set>
									<c:set var="prop1Var">Category Page</c:set>
											<c:set var="prop3Var">${var1} > ${var2}</c:set>
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise>
									<c:set var="pageName">${var1} > ${var2} > ${var3}</c:set>
									<c:set var="prop1Var">Sub Category Page</c:set>
									<c:set var="prop3Var">${var1} > ${var2} > ${var3}</c:set>
								</c:otherwise>
							</c:choose>
						</c:otherwise>
						</c:choose>
						<c:set var="prop4Var">List of Products</c:set>
						<c:set var="prop5Var">List of Products in sub-categories</c:set>
					</c:otherwise>
					</c:choose>
				</c:if>
			</c:forEach>
			<%--DoubleClick Floodlight START --%>
			<%-- Commenting out DoubleClick as part of 34473
	       	<c:if test="${DoubleClickOn}">
		    	<c:if test="${(currentSiteId eq TBS_BedBathUSSite)}">
					<c:set var="cat"><bbbc:config key="cat_category_bedBathUS" configName="RKGKeys" /></c:set>
					<c:set var="src"><bbbc:config key="src_bedBathUS" configName="RKGKeys" /></c:set>
					<c:set var="type"><bbbc:config key="type_1_bedBathUS" configName="RKGKeys" /></c:set>
				</c:if>
				<c:if test="${(currentSiteId eq TBS_BuyBuyBabySite)}">
					<c:set var="cat"><bbbc:config key="cat_category_baby" configName="RKGKeys" /></c:set>
					<c:set var="src"><bbbc:config key="src_baby" configName="RKGKeys" /></c:set>
					<c:set var="type"><bbbc:config key="type_1_baby" configName="RKGKeys" /></c:set>
				</c:if>
				<dsp:include page="/_includes/double_click_tag.jsp">
					<dsp:param name="doubleClickParam" value="src=${src};type=${type};cat=${cat};u4=null;u5=null;u11=${var1},${subCatHeader},${var4}"/>
				</dsp:include>
			</c:if>
			--%>
	 		 <%--DoubleClick Floodlight END --%>

		<script type="text/javascript">
		 function TLGetCookie(c_name){
				if(document.cookie.length>0){
			    c_start=document.cookie.indexOf(c_name + "=");
				if (c_start!=-1){
			      c_start=c_start + c_name.length+1;
			      c_end=document.cookie.indexOf(";",c_start);
				  if (c_end==-1) c_end=document.cookie.length;
			      return unescape(document.cookie.substring(c_start,c_end));
				  }
				}
				 return "";
				}
			var BBB = BBB || {};
			var var1 = '${fn:replace(fn:replace(var1,'\'',''),'"','')}';
			var prop2var='';
			var var2= '';
			var var3='';
            var CatFlagL3 = '${fn:replace(fn:replace(CatFlagL3,'\'',''),'"','')}';
            var prop9Var = '${fn:replace(fn:replace(prop9Var,'\'',''),'"','')}';
			var prop3var = '${fn:replace(fn:replace(prop3Var,'\'',''),'"','')}';
            var prop1Var ='${fn:replace(fn:replace(prop1Var,'\'',''),'"','')}';
            var subCatHeader ='${fn:replace(fn:replace(subCatHeader,'\'',''),'"','')}';
			var omni_var4 ='${fn:replace(fn:replace(var4,'\'',''),'"','')}';
			var pageName='';
			var var9=prop9Var;
			var var10='${frmBrandPage}';
			var var11='Brand'+">"+var9;
			var var13=CatFlagL3;
			if(isNaN("${subCatHeader}")){
			var2 = var1+">"+subCatHeader;
			prop2var =  var1+">"+subCatHeader;
			pageName =prop2var;
			}
			if(isNaN("${var4}")) {
				var var4=omni_var4;
			var3 = var2+ '>'+omni_var4;
			prop3var = prop2var+">"+omni_var4;
			pageName=prop3var;
			if(var4.toLowerCase() != 'all'){
				prop1Var = 'Sub Category Page';
			}
			}
			if(!var13 && ('${var1=='Whats New' || var1=='Clearance'}' == 'false')){
				prop3var = prop3var+">"+'All';
			}
			if(var10)
			{
			var var12 ="Brand Page ";
			BBB.subCategoryPageInfo = {
					pageNameIdentifier: 'SubCategoryPage',
					pageName: var11,
					channel: var12,
					prop1: var12,
					prop2: var12,
					prop3: var12,
					eVar4: var12,
					eVar5: var12,
					eVar6: var11,
					prop6: '${pageContext.request.serverName}',
					prop9: '${pageContext.request.serverName}'
			};
			}
			else {
				BBB.subCategoryPageInfo = {
					pageNameIdentifier: 'SubCategoryPage',
					pageName: prop3var,
					channel: var1,
					prop1: prop1Var,
					prop2: prop2var,
					prop3: prop3var,
					eVar4: var1,
					eVar5: var2,
					eVar6: prop3var,
					prop6: '${pageContext.request.serverName}',
					prop9: '${pageContext.request.serverName}'
				};
				}
		</script>
		<%-- R2.2 Story 178-a4 Product Comparison page | Start--%>
		<%-- Sets the url of last navigated PLP in a user session--%>
		<c:if test="${fromCollege}">
			<c:set var="queryParam" value="?fromCollege=${fromCollege}"/>
		</c:if>
		<dsp:setvalue bean="ProductComparisonList.url" value="${url}${queryParam}"/>
		<%-- R2.2 Story 178-a4 Product Comparison page | End--%>
		
		<!-- Find/Change Store Form jsps-->	
		<c:import url="../_includes/modules/change_store_form.jsp" />
		<c:import url="../selfservice/find_in_store.jsp" />
		
</jsp:body>
		<jsp:attribute name="footerContent">

         </jsp:attribute>
	</bbb:pageContainer>
</dsp:page>