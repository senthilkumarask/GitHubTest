<dsp:page>
	<dsp:importbean bean="/com/bbb/certona/droplet/CertonaDroplet"/>
	<dsp:importbean bean="/com/bbb/omniture/OmnitureVariableDroplet"/>
	<dsp:importbean bean="/com/bbb/browse/AddContextPathDroplet"/>
	<dsp:getvalueof var="searchTerm" value="${Keyword}"/>
	<dsp:getvalueof var="origSearchTerm" param="origSearchTerm"/>
	<dsp:getvalueof var="browseSearchVO" param="browseSearchVO"/>
	<dsp:getvalueof var="frmBrandPage" param="frmBrandPage"/>
	<dsp:getvalueof var="frmCollegePage" param="fromCollege"/>
	<dsp:getvalueof var="isRedirect" param="isRedirect"/>
	<dsp:getvalueof var="searchView" param="view"/>
	<dsp:importbean	bean="/com/bbb/profile/session/SessionBean"/>
	<dsp:getvalueof var="searchType" param="searchType"/>
	<c:set var="searchViewParam" value="&view=${searchView}"/>
	<dsp:getvalueof var="metaVendorParam" bean="SessionBean.vendorParam"/>
	
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
	<%-- 504D implementation --%>
	<c:choose>
		<c:when test="${frmBrandPage eq 'true' || frmCollegePage eq 'true'}">
			<c:set var="follow" value="true"/>
			<c:set var="index" value="true"/>
		</c:when>
		<c:when test="${not empty metaVendorParam}">
    		<c:set var="follow" value="false"/>
			<c:set var="index" value="false"/>
    	</c:when>
		<c:otherwise>
			<c:set var="follow" value="false"/>
			<c:set var="index" value="true"/>
		</c:otherwise>
	</c:choose>
			<c:set var="partialSearchList"><dsp:valueof param="browseSearchVO.partialSearchResults"/></c:set>
            <c:set var="prodCount"><dsp:valueof param="browseSearchVO.bbbProducts.bbbProductCount"/></c:set>
			<c:set var="videoCount"><dsp:valueof param="browseSearchVO.assetMap.Media.count"/></c:set>
			<c:set var="guideCount"><dsp:valueof param="browseSearchVO.assetMap.Guide_TBS.count"/></c:set>
			<c:set var="otherCount"><dsp:valueof param="browseSearchVO.assetMap.Other.count"/></c:set>
			<c:if test="${empty otherCount }">
			<c:set var="otherCount" value="0"/>
			</c:if>
			<c:if test="${empty videoCount }">
			<c:set var="videoCount" value="0"/>
			</c:if>
			<c:if test="${empty prodCount }">
			<c:set var="prodCount" value="0"/>
			</c:if>
			<c:if test="${empty guideCount }">
			<c:set var="guideCount" value="0"/>
			</c:if>
            <dsp:getvalueof var="resultCount" value="${otherCount+videoCount+prodCount+guideCount}"></dsp:getvalueof>
            <dsp:getvalueof var="pagNum" param="pagNum"/>
	<bbb:pageContainer>
		<jsp:attribute name="section">search</jsp:attribute>
		<jsp:attribute name="pageWrapper">searchGrid searchResults useScene7 useCertonaJs ${pageGridClass}</jsp:attribute>
		<jsp:attribute name="titleString">Search Results for ${searchTerm} </jsp:attribute>
		<jsp:attribute name="follow">${follow}</jsp:attribute>
		<jsp:attribute name="index">${index}</jsp:attribute>
		<jsp:attribute name="PageType">Search</jsp:attribute> 
		<jsp:attribute name="bodyClass">search-grid</jsp:attribute> 
		<jsp:body>
			<script type="text/javascript">
				var resx = new Object();
				var linksCertona='';
			</script>
			<c:if test="${TagManOn}">
				<dsp:include page="/tagman/frag/search_frag.jsp" >
					<dsp:param name="searchTerm" value="${searchTerm}"/>
				</dsp:include>
			</c:if>
			<dsp:importbean bean="/com/bbb/search/droplet/ResultListDroplet"/>
            <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
            <dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
            <dsp:importbean bean="/atg/multisite/Site"/>
            <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
            <dsp:importbean bean="/atg/userprofiling/Profile" />
            <dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
            <dsp:importbean bean="/com/bbb/search/droplet/TBSSearchReportDroplet"/>
			
           <dsp:getvalueof var="linkString" param="linkString"/>
			<script type="text/javascript">
				linksCertona = "${linkString}";
			</script>
			<input type="hidden" name="currentEPHScheme" value="${currentEPHScheme}"/>	
			<dsp:getvalueof id="applicationId" bean="Site.id" />
		    
			<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${applicationId}"/>
			<c:set var="promoKeyTop"><bbbl:label key="lbl_promo_key_top" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="promoKeyRight"><bbbl:label key="lbl_promo_key_right" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="promoKeyCenter"><bbbl:label key="lbl_promo_key_center" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="promoKeyRelated"><bbbl:label key="lbl_promo_key_related" language="${pageContext.request.locale.language}" /></c:set>
			
			<dsp:getvalueof var="view" param="view"/>
			
			<dsp:droplet name="ForEach">
         		<dsp:param name="array" param="browseSearchVO.autoSuggest" />
           		<dsp:oparam name="output">
           			<dsp:getvalueof var="spellCorrection" param="element.spellCorrection"/>
           			<dsp:droplet name="IsNull">
         				<dsp:param name="value" value="${spellCorrection}" />
         				<dsp:oparam name="false">
         					<dsp:getvalueof var="searchTerm" value="${spellCorrection}"/>
         					<dsp:getvalueof var="autoCorrected" value="true"/>
         				</dsp:oparam>
         			</dsp:droplet>
         			<dsp:getvalueof var="didYouMeanTerm" param="element.dymSuggestion"/>
         			<dsp:droplet name="IsNull">
         				<dsp:param name="value" value="${didYouMeanTerm}" />
         				<dsp:oparam name="false">
         					<dsp:getvalueof var="dymSuggestion" value="true"/>
         				</dsp:oparam>
         			</dsp:droplet>
					<dsp:getvalueof var="autoPhrase" value="false"/>
					<dsp:getvalueof var="isAutoPhrase" param="element.autoPhrase"/>
					<dsp:getvalueof var="autoPhrase" value="${isAutoPhrase}"/>
           		</dsp:oparam>
           	</dsp:droplet>
           	
            <dsp:getvalueof var="searchAssetType" param="searchAssetType"/>
        	<dsp:getvalueof var="origRequestURI" vartype="java.lang.String" bean="/OriginatingRequest.requestURI" />
			<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
			
		<div class="row">
			<dsp:droplet name="ForEach">
				<dsp:param name="array" param="browseSearchVO.promoMap" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="elementList" param="element"/>
						<dsp:getvalueof var="key1" param="key"/>
						<c:if test="${key1 eq promoKeyTop &&  not empty elementList}">
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
										    	 <div class="row promoHead">
										            <a href="${imageHREF}" title="<dsp:valueof param="element.imageAlt"/>">
					                                	<img src="${scene7Path}/${imageURL}" alt="<dsp:valueof param="element.imageAlt"/>" />
					                            	</a>
					                             </div>	
										        </c:when>
										        <c:otherwise>
										         <div class="row">
					                            	<img src="${scene7Path}/${imageURL}" alt="<dsp:valueof param="element.imageAlt"/>" />
					                             </div>	
										        </c:otherwise>
											</c:choose>
					                	</dsp:oparam>
					        </dsp:droplet>
				    	</c:if>
					</dsp:oparam>
			</dsp:droplet>
			<div class="small-12 columns no-padding">
				<c:choose>
					<c:when test="${not empty searchType && searchType eq 'upc'}">
						<h1><bbbl:label key="lbl_header_search_results_page_2" language="${pageContext.request.locale.language}" />:
						 	<dsp:droplet name="TBSSearchReportDroplet">
						 		<dsp:param name="keyword" param="searchTerm"/>
						 		<dsp:param name="resultProducts" param="browseSearchVO.bbbProducts.bbbProducts"/>
						 		<dsp:oparam name="output">
						 			<dsp:valueof param="resultsList"/><br/>
						 			<dsp:getvalueof param="noResultsList" var="noResultsList"/>
						 		</dsp:oparam>
						 	</dsp:droplet>
						</h1>
						<c:if test="${not empty noResultsList}">
							<span style="color: red">
			 				 	<img width="15" height="10" src="/tbs/resources/img/icons/Warning.jpg"/> No results for <c:out value="${noResultsList}"/>
			 				</span>
			 			</c:if>
					</c:when>
					<c:otherwise>
						<h1><bbbl:label key="lbl_header_search_results_page_2" language="${pageContext.request.locale.language}" /></h1>
						<c:choose>
		                    <c:when test="${prodCount!=0 || (prodCount==0 and empty partialSearchList)}">
		                    	<h2>${prodCount+guideCount}&nbsp;<bbbl:label key="lbl_header_search_results_page_4" language="${pageContext.request.locale.language}" />&nbsp;&ldquo;<span class="BBBSearchTerm"><c:out value="${origSearchTerm}" escapeXml="true"/></span>&rdquo;</h2>	
		                    </c:when>
		                   	<c:when test="${ prodCount==0 and not empty partialSearchList}">
		                   		<h2>${prodCount+guideCount}&nbsp;<bbbl:label key="lbl_header_search_results_page_4" language="${pageContext.request.locale.language}" />&nbsp;&ldquo;<c:out value="${searchTerm}" escapeXml="true"/>&rdquo;<bbbl:label key="lbl_partialforzero_search_results" language="${pageContext.request.locale.language}" /></h2>	
		                   	</c:when>
						</c:choose>
					</c:otherwise>
				</c:choose>
					
					<c:if test="${autoPhrase eq 'false'}"> 
	                    <c:if test="${autoCorrected eq 'true' || dymSuggestion eq 'true'}">
	                        <p>
	                            <c:if test="${autoCorrected eq 'true'}">
	                            	
	                                <bbbl:label key="lbl_spell_correction_result" language="${pageContext.request.locale.language}" />
	                                &ldquo;<dsp:valueof param="Keyword" valueishtml="true"/>&rdquo;<bbbl:label key="lbl_spell_correction_2" language="${pageContext.request.locale.language}" /> 
	                                &ldquo;<dsp:valueof value="${spellCorrection}" valueishtml="true"/>&rdquo;&#46;
	                            </c:if>
	                            <c:if test="${dymSuggestion eq 'true'}">
	                                <dsp:droplet name="ForEach">
	                                    <dsp:param name="array" param="browseSearchVO.autoSuggest" />
	                                    <dsp:oparam name="outputStart"><bbbl:label key="lbl_did_you_mean_1" language="${pageContext.request.locale.language}" />
	                                    </dsp:oparam>
	                                    <dsp:oparam name="output">
	                                        <dsp:getvalueof var="totalSuggestions" param="size"/>
	                                        <dsp:getvalueof var="currentCount" param="count"/>
	                                        <%-- R2.2 Story - SEO Friendly URL changes --%>
	                                        <a href="${contextPath}${urlPrefixForSuggestion}<dsp:valueof param="element.dymSuggestion"/>${searchQueryParamsForSuggestion}"><dsp:valueof param="element.dymSuggestion"/></a>
	                                        <c:if test="${totalSuggestions ne currentCount}"><bbbl:label key="lbl_did_you_mean_2" language="${pageContext.request.locale.language}" /></c:if>
	                                    </dsp:oparam>
	                                    <dsp:oparam name="outputEnd"><bbbl:label key="lbl_did_you_mean_3" language="${pageContext.request.locale.language}" />
	                                    </dsp:oparam>
	                                </dsp:droplet>
	                            </c:if>
	                        </p>
	                    </c:if>
					</c:if>
				
			</div>
			
		</div>
<!-- 		Commenting this code for Temporary Fix of "TBXPS-2786" -->
<%--  		<c:if test="${currentSiteId == TBS_BedBathCanadaSite || currentSiteId == TBS_BedBathUSSite}">	  --%>
<!-- 	        <div class="row"> -->
<!-- 	               <div class="small-12 columns no-padding">  -->
<!-- 	                   <label class="inline-rc checkbox" for="bbb">  -->
<%-- 	                   <dsp:getvalueof param="babyItems" var="babyItems"/> --%>
<%-- 	                   <c:choose> --%>
<%-- 	                    <c:when test="${not empty babyItems}"> --%>
<!-- 	                		<input type="checkbox" id="bbb" checked="checked"/>  -->
<%-- 		                    <span></span><h3><bbbl:label key="lbl_baby_items" language="${pageContext.request.locale.language}" /></h3></label> --%>
<%-- 	                    </c:when> --%>
<%-- 	                    <c:otherwise> --%>
<!-- 		                    <input type="checkbox" id="bbb"/>  -->
<%-- 		                    <span></span><h3><bbbl:label key="lbl_baby_items" language="${pageContext.request.locale.language}" /></h3></label> --%>
<%-- 	                    </c:otherwise> --%>
<%-- 	                   </c:choose> --%>
<!-- 	               </div> -->
<!-- 	           </div>               -->
<%-- 	     </c:if>                  --%>                 
        <div class="row">
	        <c:choose>
				<c:when test="${not empty searchType && searchType eq 'upc'}">
					<div id="left-nav" class="hide-for-large-up small-medium-right-off-canvas-menu left-nav">
				</c:when>
				<c:otherwise>
					<div id="left-nav" class="large-3 columns small-medium-right-off-canvas-menu left-nav">
				</c:otherwise>
			</c:choose>
	        
		        <div class="row show-for-medium-down">
		        
					<dsp:include page="/search/search_types.jsp">
						<dsp:param name="searchAssetType" value="${searchAssetType}"/>
						<dsp:param name="browseSearchVO" param="browseSearchVO" />	
						<dsp:param name="Keyword" param="Keyword"/>
					</dsp:include>
				</div>
				
				<div class="row show-for-medium-down">
					<h3><bbbl:label key="lbl_mng_regitem_sortby" language="${pageContext.request.locale.language}" /></h3>
					<ul class="category-list">
						
					<dsp:getvalueof var="pagSortOpt" param="pagSortOpt" /> 
							
						<c:set var="option1"><bbbl:label key="lbl_sortby_options_1" language="${pageContext.request.locale.language}" /></c:set>
						<c:choose>
							<c:when test="${(pagSortOpt == null)}">
								<li class="active"><a href="${url}${filterAppliedWithPrefix}?pagSortOpt=Best${searchQueryParams}${partialFlagUrl}"><bbbl:label key="lbl_sortby_options_1" language="${pageContext.request.locale.language}" /></a></li>
							</c:when>
							<c:otherwise>
								<li><a href="${url}${filterAppliedWithPrefix}?pagSortOpt=Best${searchQueryParams}${partialFlagUrl}"><bbbl:label key="lbl_sortby_options_1" language="${pageContext.request.locale.language}" /></a></li>
							</c:otherwise>
						</c:choose>
						<c:set var="option5"><bbbl:label key="lbl_sortby_options_5" language="${pageContext.request.locale.language}" /></c:set>
						<c:choose>
							<c:when test="${ (pagSortOpt == 'Price-0')}">
								<li class="active"><a href="${url}${filterAppliedWithPrefix}?pagSortOpt=Price-0${searchQueryParams}${partialFlagUrl}" ><bbbl:label key="lbl_sortby_options_5" language="${pageContext.request.locale.language}" /></a></li>
							</c:when>
							<c:otherwise>
								<li><a href="${url}${filterAppliedWithPrefix}?pagSortOpt=Price-0${searchQueryParams}${partialFlagUrl}"><bbbl:label key="lbl_sortby_options_5" language="${pageContext.request.locale.language}" /></a></li>
							</c:otherwise>
						</c:choose>
						<c:set var="option6"><bbbl:label key="lbl_sortby_options_6" language="${pageContext.request.locale.language}" /></c:set>
						<c:choose>
							<c:when test="${ (pagSortOpt == 'Price-1')}">
								<li class="active"><a href="${url}${filterAppliedWithPrefix}?pagSortOpt=Price-1${searchQueryParams}${partialFlagUrl}"><bbbl:label key="lbl_sortby_options_6" language="${pageContext.request.locale.language}" /></a></li>
							</c:when>
							<c:otherwise>
								<li><a href="${url}${filterAppliedWithPrefix}?pagSortOpt=Price-1${searchQueryParams}${partialFlagUrl}"><bbbl:label key="lbl_sortby_options_6" language="${pageContext.request.locale.language}" /></a></li>
							</c:otherwise>
						</c:choose>
							<c:if test="${currentSiteId ne TBS_BedBathCanadaSite}">
								<c:set var="option7"><bbbl:label key="lbl_sortby_options_7" language="${pageContext.request.locale.language}" /></c:set>
								<c:choose>
									<c:when test="${ (pagSortOpt == 'Ratings-1')}">
										<li class="active"><a href="${url}${filterAppliedWithPrefix}?pagSortOpt=Ratings-1${searchQueryParams}${partialFlagUrl}" ><bbbl:label key="lbl_sortby_options_7" language="${pageContext.request.locale.language}" /></a></li>
									</c:when>
									<c:otherwise>
										<li><a href="${url}${filterAppliedWithPrefix}?pagSortOpt=Ratings-1${searchQueryParams}${partialFlagUrl}"><bbbl:label key="lbl_sortby_options_7" language="${pageContext.request.locale.language}" /></a></li>
									</c:otherwise>
								</c:choose>
								</c:if>
								<c:set var="option9"><bbbl:label key="lbl_sortby_options_9" language="${pageContext.request.locale.language}" /></c:set>
								<c:choose>
									<c:when test="${ (pagSortOpt == 'Brand-0')}">
										<li class="active"><a href="${url}${filterAppliedWithPrefix}?pagSortOpt=Brand-0${searchQueryParams}${partialFlagUrl}" ><bbbl:label key="lbl_sortby_options_9" language="${pageContext.request.locale.language}" /></a></li>
									</c:when>
									<c:otherwise>
										<li><a href="${url}${filterAppliedWithPrefix}?pagSortOpt=Brand-0${searchQueryParams}${partialFlagUrl}"><bbbl:label key="lbl_sortby_options_9" language="${pageContext.request.locale.language}" /></a></li>
									</c:otherwise>
								</c:choose>
								<c:set var="option2"><bbbl:label key="lbl_sortby_options_2" language="${pageContext.request.locale.language}" /></c:set>
								<c:choose>
									<c:when test="${ (pagSortOpt == 'Date-1')}">
										<li class="active"><a href="${url}${filterAppliedWithPrefix}?pagSortOpt=Date-1${searchQueryParams}${partialFlagUrl}" ><bbbl:label key="lbl_sortby_options_2" language="${pageContext.request.locale.language}" /></a></li>
									</c:when>
									<c:otherwise>
										<li><a href="${url}${filterAppliedWithPrefix}?pagSortOpt=Date-1${searchQueryParams}${partialFlagUrl}"><bbbl:label key="lbl_sortby_options_2" language="${pageContext.request.locale.language}" /></a></li>
									</c:otherwise>

								</c:choose>
								<%-- Start : Added as part of R2.1 --%>
								<c:set var="option10"><bbbl:label key="lbl_sortby_options_10" language="${pageContext.request.locale.language}" /></c:set>
								<c:choose>
									<c:when test="${ (pagSortOpt == 'Sales-1')}">
										<li class="active"><a href="${url}${filterAppliedWithPrefix}?pagSortOpt=Sales-1${searchQueryParams}${partialFlagUrl}"><bbbl:label key="lbl_sortby_options_10" language="${pageContext.request.locale.language}" /></a></li>
									</c:when>
									<c:otherwise>
										<li><a href="${url}${filterAppliedWithPrefix}?pagSortOpt=Sales-1${searchQueryParams}${partialFlagUrl}"><bbbl:label key="lbl_sortby_options_10" language="${pageContext.request.locale.language}" /></a></li>
									</c:otherwise>
								</c:choose>
					
					</ul>
					</div>
			        
	                    <dsp:include page="/_includes/modules/faceted_bar.jsp">
							<dsp:param name="browseSearchVO" param="browseSearchVO" />
							<dsp:param name="view" value="${view}"/>
                             <dsp:param name="frmBrandPage" value="${frmBrandPage}"/>
                             <dsp:param name="url" value="${url}"/>
						</dsp:include>
		        </div>
		        
		        <c:choose>
					<c:when test="${not empty searchType && searchType eq 'upc'}">
						<div class="small-12 large-12 columns product-grid-container">
					</c:when>
					<c:otherwise>
						<div class="small-12 large-9 columns product-grid-container">
					</c:otherwise>
				</c:choose>
		        
		        	<div class="row grid-control show-for-medium-down">
						<div class="small-2 medium-1 large-1 right columns">
							<div class="sortIcons fr row">
								<c:choose>
									<c:when test="${view == 'list'}">
										<div class="small-6 columns">
											<a class="active layout-icon-list" data-view="list" title="List View" id="plpListView"><span> </span></a>
										</div>
										<div class="small-6 columns ">
											<a class="layout-icon-grid" data-view="grid" title="Grid View" id="plpGridView"><span> </span></a>
										</div>
									</c:when>
									<c:otherwise>
										<div class="small-6 columns">
											<a class="layout-icon-list" data-view="list" title="List View" id="plpListView"><span> </span></a>
										</div>
										<div class="small-6 columns ">
											<a class="active layout-icon-grid" data-view="grid" title="Grid View" id="plpGridView"><span> </span></a>
										</div>
									</c:otherwise>
								</c:choose>		
							</div>
						</div>
					</div>
				
					<div class="row">
						
						
	           	 <c:if test="${prodCount!=0 }">
               
                <%--The header for the partial match in 92F story --%>
                	<c:if test="${not empty partialSearchList}">
                		<h3 class="partialHeaderSearch"><bbbl:label key="lbl_partialheader_search_results_page_1" language="${pageContext.request.locale.language}" /></h3>
                	</c:if>
                    
                        <dsp:include page="/_includes/modules/pagination_top_2bar.jsp">
                                <dsp:param name="searchAssetType" value="${searchAssetType}"/>
                                <dsp:param name="browseSearchVO" param="browseSearchVO" />	
                                <dsp:param name="Keyword" param="Keyword"/>
                                <dsp:param name="view" value="${view}"/>
								
												<dsp:getvalueof var="searchAssetType" param="searchAssetType"/>
				<dsp:getvalueof var="searchView" param="view"/>
								
                        </dsp:include>
					<hr>
	                     <dsp:droplet name="ResultListDroplet">
	                         <dsp:param name="browseSearchVO" value="${browseSearchVO}"/>
	                         <dsp:oparam name="output">
	                             <dsp:include page="/_includes/modules/product_grid_5x4-rwd.jsp">
	                                 <dsp:param name="BBBProductListVO" param="BBBProductListVO"/>
	                                 <dsp:param name="promoSR" value="${promoSR}"/>
									 <dsp:param name="plpGridSize" value="${plpGridSize}"/>
									 <dsp:param name="browseSearchVO" value="${browseSearchVO}"/>
									 <c:if test="${view == 'grid4'}">
									 	 <dsp:param name="plpGridSize" value="4"/>	
									 </c:if>
									 <c:if test="${not empty searchType && searchType eq 'upc'}">
									 	<dsp:param name="searchType" value="${searchType}"/>
									 </c:if>
	                             </dsp:include>
	                         </dsp:oparam>
	                     </dsp:droplet>
	                   
						
                            <dsp:include page="/_includes/modules/pagination_bottom_full.jsp">
                             	<dsp:param name="Keyword" param="Keyword"/>
                                  <dsp:param name="view" value="${view}"/>
                            </dsp:include>
		                   			
               		 </c:if>
               		 
               		  <c:if test="${prodCount==0 && otherCount+videoCount+prodCount+guideCount!=0}">
	                        <dsp:include page="/_includes/modules/pagination_top_2bar.jsp">
	                                <dsp:param name="searchAssetType" value="${searchAssetType}"/>
	                                <dsp:param name="browseSearchVO" param="browseSearchVO" />	
	                                <dsp:param name="Keyword" param="Keyword"/>
	                        </dsp:include>
			                   	
               		  </c:if>
					</div>
				</div>
	        </div>	   
                   
			<dsp:droplet name="Switch">
				<dsp:param name="value" bean="Profile.transient"/>
				<dsp:oparam name="false">
					<dsp:getvalueof var="userId" bean="Profile.id"/>
				</dsp:oparam>
				<dsp:oparam name="true">
					<dsp:getvalueof var="userId" value=""/>
				</dsp:oparam>
			</dsp:droplet>
			<c:set var="term"><c:out value="${searchTerm}" escapeXml="true"/></c:set>
<%-- 			<c:set var="cert_scheme" scope="request">search_rr</c:set>
			<dsp:droplet name="CertonaDroplet">
			      <dsp:param name="scheme" value="${cert_scheme}"/>
			      <dsp:param name="userid" value="${userId}"/>
			      <dsp:param name="siteId" value="${applicationId}"/>
			      <dsp:oparam name="output">
						<dsp:valueof param="certonaResponseVO.resonanceMap"/>
			          <dsp:getvalueof var="certona_clearenceProductsList" param="certonaResponseVO.resonanceMap.${cert_scheme}.productsVOsList"/>
			          <dsp:getvalueof var="linksCertona" param="certonaResponseVO.resxLinks"/>
			          <dsp:getvalueof var="pageIdCertona" param="certonaResponseVO.pageId"/>
			      </dsp:oparam>
			      <dsp:oparam name="error">
			          <c:set var="displayFlag" value="false"/>
			      </dsp:oparam>
			      <dsp:oparam name="empty">
			          <c:set var="displayFlag" value="false"/>
			      </dsp:oparam>
			  </dsp:droplet>  --%>
			<script type="text/javascript">
				resx.appid = "${appIdCertona}";
				resx.top1 = 100000;
				resx.top2 = 100000;
				resx.links = linksCertona;
				resx.customerid = "${userId}";
				resx.pageid = "${pageIdCertona}";
				resx.Keyword = "${term}";
			</script>
		</jsp:body>
			<jsp:attribute name="footerContent">
			<dsp:getvalueof var="frmBrandPage" param="frmBrandPage"/>
           <script type="text/javascript">
           var pagNum='${pagNum}';
           var serchTerm = '<dsp:valueof value="${searchTerm}"/>';
			s.pageName='Search';
			s.channel='Search';
           if(typeof s !=='undefined') {
        	<c:if test="${frmBrandPage eq 'true'}">
				s.channel='Brand Search';
			</c:if>  
			
			<c:choose>
				<c:when test="${searchView eq 'list'}">
					s.prop25="List View";
					s.eVar47="List View";
				</c:when>
				<c:when test="${searchView eq 'grid4'}">
					s.prop25="Grid View-4";
					s.eVar47="Grid View-4";
				</c:when>
				<c:otherwise>
					s.prop25="Grid View-3";
					s.eVar47="Grid View-3";
				</c:otherwise>
			</c:choose>
			s.prop1='Search';
			s.prop2='Search';
			s.prop3='Search';
			s.prop4='';
			s.prop5='';
			s.prop6='';
			s.prop7=serchTerm.toLowerCase();
			s.prop8='<dsp:valueof value="${resultCount}"/>';
			s.eVar61="${OmnitureVariable}";
			if(pagNum=='' || pagNum==1){
			s.events='event1';
           }
			s.eVar2=serchTerm.toLowerCase();
			var s_code=s.t();
			if(s_code)document.write(s_code);		
           }
        </script>
    </jsp:attribute>
	</bbb:pageContainer>
</dsp:page>
