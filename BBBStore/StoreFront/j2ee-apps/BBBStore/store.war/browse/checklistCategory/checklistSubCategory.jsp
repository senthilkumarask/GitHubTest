<dsp:page>
	<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductComparisonList"/>
	<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListHandler"/>
	<dsp:importbean bean="/com/bbb/browse/AddContextPathDroplet"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="defaultView" param="view"/>
	
	<c:set var="saSrc"><bbbc:config key="socialAnnexURL" configName="ThirdPartyURLs" /></c:set>
	<c:set var="checklistCategoryDim"><bbbc:config key="Checklist_Category" configName="DimDisplayConfig" /></c:set>
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

			<dsp:importbean bean="/com/bbb/search/droplet/ResultListDroplet" />
			<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
			<dsp:importbean bean="/atg/multisite/Site"/>
			<dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
			<dsp:importbean bean="/atg/userprofiling/Profile" />
			<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

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
			<dsp:getvalueof var="categoryLevel" param="browseSearchVO.checkListSeoUrlHierarchy.categoryLevel" scope="request" />
			<dsp:getvalueof var="childsSeoUrls" param="browseSearchVO.checkListSeoUrlHierarchy.childsSeoUrls" scope="request" />

			<c:set var="isFilterApplied" value="false" scope="request" />
			<dsp:getvalueof	var="descriptorsList" param="browseSearchVO.descriptors"/>
			<dsp:droplet name="ForEach">
			 <dsp:param name="array" value="${descriptorsList}" />
			 <dsp:oparam name="output">
				<dsp:getvalueof var="filterDescName" param="element.rootName"/>
				<c:if test="${not empty filterDescName  && ( filterDescName eq 'PRICE RANGE'||filterDescName eq 'Brand' || filterDescName eq 'COLORGROUPING' )}">
						<c:set var="isFilterApplied" value="true" scope="request" />	
				</c:if>
			</dsp:oparam>
			</dsp:droplet>

			<dsp:getvalueof var="browseSearchVO" param="browseSearchVO"/>
			<c:set var="count" value="0"/>
			<c:set var="brandstring" value=""/>
			<c:set var="colorstring" value=""/>
			<c:set var="departmentstring" value=""/>
			<c:set var="lastColorstring" value=""/>
			<c:set var="lastBrandstring" value=""/>
			
			<!-- Revisit  this block -->
			<c:forEach var="descriptor" items="${browseSearchVO.descriptors}">
				<c:if test="${(descriptor.rootName ne checklistCategoryDim)}">
					<c:choose>
						 <c:when test="${fn:containsIgnoreCase(descriptor.rootName,'COLORGROUPING')}">
						 	<c:choose>
						 		<c:when test="${empty colorstring || colorstring == ''}">
						 			<c:set var="colorstring" value="${descriptor.name}"/>
						 		</c:when>
						 		<c:otherwise>
						 			<c:set var="colorstring" value="${colorstring}, ${descriptor.name}"/>
						 		</c:otherwise>
						 	</c:choose>	
						 	<c:set var="lastColorstring" value="${descriptor.name}"/>
						</c:when>
						<c:when test="${fn:containsIgnoreCase(descriptor.rootName,'BRAND')}">
							<c:choose>
						 		<c:when test="${empty brandstring || brandstring == ''}">
						 			<c:set var="brandstring" value="${descriptor.name}"/>
						 		</c:when>
						 		<c:otherwise>
						 			<c:set var="brandstring" value="${brandstring}, ${descriptor.name}"/>
						 		</c:otherwise>
						 	</c:choose>	
						 	<c:set var="lastBrandstring" value="${descriptor.name}"/>
						</c:when> 
						<c:when test="${fn:containsIgnoreCase(descriptor.rootName,checklistCategoryDim)}">
							<c:set var="departmentstring" value="${departmentstring} ${browseSearchVO.checkListSeoUrlHierarchy.checkListCategoryName}"/>
						</c:when>
						<c:when test="${not empty titleFilterStringfacet && (not fn:containsIgnoreCase(descriptor.rootName,'Price'))}">
							<c:set var="titleFilterStringfacet" value="${titleFilterStringfacet} - ${descriptor.name}"/>
						</c:when>
						<c:otherwise>
							<c:if test="${not fn:containsIgnoreCase(descriptor.rootName,'Price') && not fn:containsIgnoreCase(descriptor.rootName,f)}">
								<c:set var="titleFilterStringfacet" value="${descriptor.name}"/>
							</c:if>
						</c:otherwise>
					</c:choose>
				</c:if>
			</c:forEach>
			
			<!-- Repalce last , with & for colorString-->
			<c:set var="relaceColorstring" value=", ${lastColorstring}"/>
			<c:set var="changeBrandstring" value=" & ${lastColorstring}"/>
			<c:set var="colorstring" value="${fn:replace(colorstring,relaceColorstring,changeBrandstring)}"/>
			<!-- End colorString -->
			
		
			<%  String colorstring = (String)pageContext.getAttribute("colorstring");
				int ind = colorstring.lastIndexOf(",");
				if( ind>=0 ){
					String colorstringnew = (new StringBuilder(colorstring).replace(ind,ind+1," &" )).toString();
					pageContext.setAttribute("colorstring", colorstringnew);
				}%> 
			<c:set var="relaceColorstring" value=", ${lastBrandstring}"/>
			<c:set var="changeBrandstring" value=" & ${lastBrandstring}"/>
			<c:set var="brandstring" value="${fn:replace(brandstring,relaceColorstring,changeBrandstring)}"/>
				<!-- Repalce last , with & for brandString-->
			<%  String brandstring = (String)pageContext.getAttribute("brandstring");
				 ind = brandstring.lastIndexOf(",");
				if( ind>=0 ){
					String brandstringnew = (new StringBuilder(brandstring).replace(ind,ind+1," &" )).toString();
					pageContext.setAttribute("brandstring", brandstringnew);
				}%> 
			<!-- End brandString -->
			<c:if test="${not empty browseSearchVO.checkListSeoUrlHierarchy}">
				<c:set var="departmentstring" value="${browseSearchVO.checkListSeoUrlHierarchy.checkListCategoryName}" />
			</c:if>
			
			<!-- Append color brand and department name to the title -->
			<c:set var="titleFilterString" value="${colorstring}"/>
			<c:choose>
				<c:when test="${not empty titleFilterString && not empty brandstring}">
					<c:set var="titleFilterString" value="${titleFilterString} ${brandstring}"/>
				</c:when>
				<c:when test="${empty titleFilterString && not empty brandstring}">
					<c:set var="titleFilterString" value="${brandstring}"/>
				</c:when>
			</c:choose>
			<c:choose>
				<c:when test="${not empty titleFilterString && not empty departmentstring}">
					<c:set var="titleFilterString" value="${titleFilterString} ${departmentstring}"/>
				</c:when>
				<c:when test="${empty titleFilterString && not empty departmentstring}">
					<c:set var="titleFilterString" value="${departmentstring}"/>
				</c:when>
			</c:choose>
			<!-- Append category name for category pages -->
			<!-- revisit this block -->
			<c:choose>
				<c:when test ="${(empty frmBrandPage or not frmBrandPage) && (not empty titleFilterString || not empty titleFilterStringfacet)}">
					<c:if test="${empty categoryNameStopWord || categoryNameStopWord ne 'true' }">
						<c:set var="titleFilterString" value="${titleFilterString} ${browseSearchVO.currentCatName}"/>
					</c:if>
					<c:if test="${not empty titleFilterStringfacet }">
						<c:set var="titleFilterString" value="${titleFilterString} - ${titleFilterStringfacet}"/>
					</c:if>
				</c:when>
				<c:otherwise>
					<c:set var="titleFilterString" value="${titleFilterString}"/>
					<c:if test="${not empty titleFilterStringfacet }">
						<c:set var="titleFilterString" value="${titleFilterString} - ${titleFilterStringfacet}"/>
					</c:if>
				</c:otherwise>
			</c:choose>	
		<bbb:pageContainer>
		
			<jsp:attribute name="section">browse</jsp:attribute>
			<jsp:attribute name="pageWrapper">subCategory useCertonaJs useStoreLocator ${pageGridClass} useScene7</jsp:attribute>
			<jsp:attribute name="pageVariation">${variation}</jsp:attribute>
			<jsp:attribute name="PageType">CheckListCategoryDetails</jsp:attribute>
			<jsp:attribute name="titleString">${titleFilterString }</jsp:attribute>

	       <jsp:body>
			
			<input type="hidden" value="${radius_default_selected}" id="defaultRadius" name="defaultRadius">
		   
			<script type="text/javascript">
				var resx = new Object();
				var linksCertona='';
			</script>

			<c:set var="department"><bbbl:label key="lbl_department" language="${pageContext.request.locale.language}" /></c:set>
			<dsp:getvalueof param="browseSearchVO" var="browseSearchVO" />
			<dsp:getvalueof var="linkString" param="linkString" />
			<script type="text/javascript">
				linksCertona =linksCertona + '${linkString}';
			</script>
			
					<div id="subHeader" class="container_12 clearfix">
						<div class="grid_12 marTop_20">
						<c:set var="categoryLevel" value="${browseSearchVO.checkListSeoUrlHierarchy.categoryLevel}" />
						<c:choose>
							<c:when test="${categoryLevel eq 3  }">
								    <c:set var="parentCategoryName" value="${browseSearchVO.checkListSeoUrlHierarchy.parentSeoUrl.checkListCategoryName}"/>
									<div class="categoryTitle">	<h3 class="catHeader">${parentCategoryName}</h3></div>
							</c:when>
							<c:when test="${categoryLevel eq 2 }">	
									<c:set var="selectedCategoryName" value="${browseSearchVO.checkListSeoUrlHierarchy.checkListCategoryName}"/>
									<div class="categoryTitle">	<h3 class="catHeader">${selectedCategoryName}</h3></div>
                            </c:when>
                            <c:when test="${categoryLevel eq 1 }">
									<c:set var="selectedCategoryName" value="${browseSearchVO.checkListSeoUrlHierarchy.checkListCategoryName}"/>
									<div class="categoryTitle">	<h3 class="catHeader"><bbbl:label key='lbl_link_all_categories' language="${pageContext.request.locale.language}" />&nbsp;${selectedCategoryName}</h3></div>	
							</c:when>
                         </c:choose>
						</div>
					</div>
					<dsp:getvalueof var="portrait" value="false"/>
					
					<%-- Compare Matrix Div Begins --%>
					<div id="ec_adaptivenav"></div>
					<%-- Compare Matrix Div Ends --%>
					
					<%--R2.2 Story 116C Changes--%>
					<div id="content" class="container_12 clearfix">
					<dsp:getvalueof var="facets" param="browseSearchVO.facets"/>
					<dsp:getvalueof var="descriptorList" param="browseSearchVO.descriptors" />
						<c:if test="${not empty facets or fn:length(descriptorList)>0 }">
					     <div class="<c:out value="${facetClass}"/> marTop_5">
							<dsp:include page="/_includes/modules/faceted_bar_checklist_plp.jsp">
								<dsp:param name="browseSearchVO" param="browseSearchVO" />
								<dsp:param name="seoUrl" value="${seoUrl}" />
								<dsp:param name="dontShowAllTab" value="false" />
								<dsp:param name="totSize" value="${totSize}" />
								<dsp:param name="view" value="${defaultView}" />
								<dsp:param name="floatNode" value="${floatNode}"/>			
							</dsp:include>
						</div>
						</c:if>						
						
						<div id="prodGridContainer" class="<c:out value="${gridClass}"/> noMarTop clearfix ec_gridwall">
				            <div id="pagTop" class="<c:out value="${gridClass}"/> alpha omega">
								<dsp:include page="/_includes/modules/pagination_top_1bar.jsp">
								<dsp:param name="view" value="${defaultView}"/>
								<dsp:param name="showViewOptions" value="true"/>
								<dsp:param name="pageSectionValue" value="topOfResultSet"/>
								<dsp:param name="sortOptions" value="${sortOptions}"/>
								<dsp:param name="seoUrl" value="${seoUrl}" />
								</dsp:include>
							</div>
								<%-- Start: If view is selected as list, then list view of PLP will be shown else grid view is displayed. R2.2 116-g --%>
								<c:choose>
									<c:when test="${defaultView=='list'}">
										<dsp:include page="/_includes/modules/product_list.jsp">
											<dsp:param name="BBBProductListVO" value="${browseSearchVO.bbbProducts}" />
											<dsp:param name="promoSC" value="${promoSC}"/>
											<dsp:param name="url" value="${url}" />
											<dsp:param name="portrait" value="${portrait}"/>
											<dsp:param name="browseSearchVO" value="${browseSearchVO}"/>
										</dsp:include>
									</c:when>
									<c:otherwise>
										<dsp:include page="/_includes/modules/product_grid_5x4.jsp">
											<dsp:param name="BBBProductListVO" value="${browseSearchVO.bbbProducts}" />
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
								<dsp:param name="view" value="${defaultView}"/>
								<dsp:param name="narrowDown" value="${param.narrowDown}"/>
								<dsp:param name="pageSectionValue" value="bottomOfResultSet"/>
								<dsp:param name="sortOptions" value="${sortOptions}"/>
								<dsp:param name="seoUrl" value="${seoUrl}" />
								</dsp:include>
							</div>
							<div id="sa_s22_instagram" class="sa_s22_instagram_category"></div>
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
							<script>
								if($('#pageWrapper')[0].classList.contains('subCategory')){
									catType = function (){
							            var
							            	_path = document.location.pathname,
							                arr = document.location.href.split('/'),
							                ndx = arr.indexOf('store');

							                if (_path.indexOf('college') > -1) {
							                	catType = 'college';
							                } else {
							                	catType = arr[ndx + 1];
							                }
							                
							            return catType;
							        }
									var sa_page="3"; // constant for the page slider
									var sa_site_id= '${saSrc}';//pass by client
									var sa_instagram_category_type = catType(); // category | brand | search | college i.e. for id type
									
									if(catType != null && catType != undefined) {
										if(catType === 'brand') {
											var sa_instagram_category_code ='${brandName}';	
										} else {
											var sa_instagram_category_code = '${categoryId}';
										}
									}
									
									(function() {
										function sa_async_load() {
											var sa = document.createElement('script');sa.type = 'text/javascript';
											sa.async = true;sa.src = sa_site_id;
											var sax = document.getElementsByTagName('script')[0];sax.parentNode.insertBefore(sa, sax);
								       }
									if (window.attachEvent) {
										window.attachEvent('onload', sa_async_load);
									} else {
										window.addEventListener('load', sa_async_load,false);
									}
									}());
								}
							</script>
				
					</div>
			<c:if test="${TellApartOn}">
				<bbb:tellApart actionType="pv" pageViewType="ChecklistCategory" />
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
		<script type="text/javascript">
				resx.appid = "${appIdCertona}";
				resx.links = linksCertona;
				resx.customerid = "${userId}";


		</script>

	<%--BBBSL-8716 | PLP Fixes for BOTs --%>	
	<c:if test="${not isRobotOn}">				 
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
			var c1var = "${param.cat1Name}";
			var c2var = "${param.cat2Name}";
			var c3var = "${param.cat3Name}";
			var c2eVar = "";
			var c3eVar = "";
			var pageName = c1var;
			var channel = c1var;
			var prop12var="";
			var prop1var = "";
			var prop2var = "";
			var prop3var = "";
			var prop4var = "Registry";
			var isChecklist = "${browseSearchVO.checkListSeoUrlHierarchy.regTypeCheckList}";
			var displayName = "${browseSearchVO.checkListSeoUrlHierarchy.checkListDisplayName}";
			var childsSeoUrls = "${childsSeoUrls}";
			var varE1 = 'Checklist; Registry';
			
			if(c3var) {
				c1var = "${browseSearchVO.checkListSeoUrlHierarchy.parentSeoUrl.parentSeoUrl.checkListCategoryName}";
				c2var = "${browseSearchVO.checkListSeoUrlHierarchy.parentSeoUrl.checkListCategoryName}";
				c3var = "${browseSearchVO.checkListSeoUrlHierarchy.checkListCategoryName}";
			} else if(c2var) {
				c1var = "${browseSearchVO.checkListSeoUrlHierarchy.parentSeoUrl.checkListCategoryName}";
				c2var = "${browseSearchVO.checkListSeoUrlHierarchy.checkListCategoryName}";
			} else {
				c1var = "${browseSearchVO.checkListSeoUrlHierarchy.checkListCategoryName}";
			}
			
			if(isChecklist === 'true') {
				if(c2var) {
					c2eVar = c1var + '>' + c2var;
					if(c3var) {
						c3eVar = c2eVar + '>' + c3var;
						pageName = pageName + '>' + c2var + '>' + c3var;
						prop1var = 'Sub Category Page';
						prop2var = c1var + '>' + c2var;
						prop3var = c1var + '>' + c2var + '>' + c3var;	
					} else {
						c3eVar = c2eVar + '>All';
						if(0 === childsSeoUrls.length){
							pageName = pageName + '>' + c2var;
							prop1var = 'Sub Category Page';
							prop2var = c1var;
							prop3var = c1var + '>' + c2var;
						} else {
							pageName = pageName + '>' + c2var + '>All';
							prop1var = 'Category Page';
							prop2var = c1var + '>' + c2var;
							prop3var = 'Category Page';
						}
						
					}
				} else {
					pageName = pageName + '>All';
					prop1var = "Category Page";
					prop2var = c1var;
					prop3var = "Category Page";
					c2eVar = c1var + '>All';
					c3eVar = c2eVar + '>All';
				}
			} else {
				varE1 = 'Checklist;'+displayName.toLowerCase();
				prop12var = 'checklist:guide:'+displayName.toLowerCase();
				pageName = 'Content Page>Guides Checklist Landing Page';
				channel = 'Content Page';
				prop1var = prop2var = prop3var = 'Content Page';
				prop4var = 'Guides & Advice';
				if(c2var) {
					c2eVar = c1var + '>' + c2var;
					if(c3var) {
						c3eVar = c2eVar + '>' + c3var;
					} else {
						c3eVar = c2eVar + '>All';
					}
				}
				
			}
			
			BBB.checklistCategoryPlp = {
					pageNameIdentifier: 'checklistCategoryPLP',
					pageName: pageName,
					channel: channel,
					prop1: prop1var,
					prop2: prop2var,
					prop3: prop3var,
					prop4: prop4var,
					eVar1: varE1,
					eVar2: 'non search',
					eVar3: 'non internal campaign',
					eVar4: c1var,
					eVar5: c2eVar,
					eVar6: c3eVar,
					prop12: prop12var,
					prop13: pageName,
					events: ""
				};				
		</script>
	</c:if>
		
		<dsp:setvalue bean="ProductComparisonList.url" value="${url}${queryParam}"/>
		<c:import url="../../_includes/modules/change_store_form.jsp" />
		<c:import url="../../selfservice/find_in_store.jsp" />
		<c:import url="../../selfservice/store/find_store_pdp.jsp" ></c:import>
</jsp:body>
		<jsp:attribute name="footerContent">

         </jsp:attribute>
	</bbb:pageContainer>
</dsp:page>
