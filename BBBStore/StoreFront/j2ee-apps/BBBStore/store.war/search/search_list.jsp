<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BrandsDroplet"/>
	<dsp:importbean bean="/com/bbb/omniture/OmnitureVariableDroplet"/>
	<dsp:importbean bean="/com/bbb/browse/AddContextPathDroplet"/>
	<dsp:importbean bean="/com/bbb/omniture/OmnitureVariableDroplet"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:getvalueof var="searchTerm" value="${Keyword}"/>
	<dsp:getvalueof var="filterString" param="filterString"/>
	<dsp:getvalueof var="lastEnteredSWSKeyword" param="lastEnteredSWSKeyword" />
	<dsp:getvalueof var="frmBrandPage" param="frmBrandPage"/>
	<dsp:getvalueof var="frmCollegePage" param="fromCollege"/>
	<dsp:getvalueof var="isRedirect" param="isRedirect"/>
	<dsp:getvalueof var="searchDepartment" param="searchDepartment"  />	
	<dsp:getvalueof var="typeAhead" param="typeAhead"  />
	<dsp:getvalueof var="parentCategory" param="browseSearchVO.parentCatName"/>
	<dsp:getvalueof var="currentCategory" param="browseSearchVO.currentCatName"/>
	<dsp:getvalueof var="ta" param="ta"></dsp:getvalueof>
	<dsp:getvalueof var="isFromSWD" value="${fn:escapeXml(param.isFromSWD)}"/>
	<c:set var="saSrc"><bbbc:config key="socialAnnexURL" configName="ThirdPartyURLs" /></c:set>
	<c:set var="socialAnnexFlag"><bbbc:config key="socialAnnexFlag" configName="FlagDrivenFunctions" /></c:set>
	<dsp:getvalueof id="servername" idtype="java.lang.String" bean="/OriginatingRequest.servername"/>
	<dsp:getvalueof id="scheme" idtype="java.lang.String" bean="/OriginatingRequest.scheme"/>
	<dsp:getvalueof var="queryString" bean="/OriginatingRequest.queryString"/>
	
	<c:set var="keywordBoostFlag"><bbbc:config key="KeywordBoostFlag" configName="FlagDrivenFunctions"/></c:set> 
	
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
	
	<c:if test="${not empty ta}">
		<c:set var="ta" value="typeahead"/>
	</c:if>	
	
	<%-- Retrieving Vendor Parameter for Vendor Story --%>
	<%@ include file="/_includes/modules/getVendorParam.jsp"%>
	<dsp:getvalueof var="metaVendorParam" bean="SessionBean.vendorParam"/>
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
			<c:set var="follow" value="true"/>
			<c:set var="index" value="true"/>
		</c:otherwise>
	</c:choose>
	
	<c:choose >
		<c:when test="${not empty filterString }">
			<c:set var="titleString">${searchTerm} - ${filterString }</c:set>
		</c:when>
		<c:otherwise>
			<c:set var="titleString">${searchTerm}</c:set>
		</c:otherwise>
	</c:choose>
	
	<bbb:pageContainer>
		<jsp:attribute name="section">search</jsp:attribute>
		<jsp:attribute name="pageWrapper">searchList searchResults useScene7 useCertonaAjax ${pageGridClass}</jsp:attribute>
		<jsp:attribute name="titleString">${titleString}</jsp:attribute>
		
		<jsp:attribute name="follow">${follow}</jsp:attribute>
		<jsp:attribute name="index">${index}</jsp:attribute>
		<jsp:attribute name="PageType">Search</jsp:attribute>  
		<jsp:body>
		
		<input type="hidden" name="fromPage" value="searchPage"/>
		<input type="hidden" name="inStoreValue" value="${inStore}"/>
		<input type="hidden" name="queryStringFromSearchPage" value="${queryString}"/>
		
		<c:set var="localStorePLPFlag" scope="request">
			<bbbc:config key="LOCAL_STORE_PLP_FLAG" configName="FlagDrivenFunctions" />
		</c:set>
		
		<c:if test="${localStorePLPFlag}">
		<c:set var="radius_default_us">
			<bbbc:config key="radius_default_us" configName="MapQuestStoreType" />
		</c:set>
		<c:set var="radius_default_baby">
			<bbbc:config key="radius_default_baby" configName="MapQuestStoreType" />
		</c:set>
		<c:set var="radius_default_ca">
			<bbbc:config key="radius_default_ca" configName="MapQuestStoreType" />
		</c:set>
		<c:set var="radius_range_us">
			<bbbc:config key="radius_range_us" configName="MapQuestStoreType" />
		</c:set>
		<c:set var="radius_range_baby">
			<bbbc:config key="radius_range_baby" configName="MapQuestStoreType" />
		</c:set>
		<c:set var="radius_range_ca">
			<bbbc:config key="radius_range_ca" configName="MapQuestStoreType" />
		</c:set>
		<c:set var="radius_range_type">
			<bbbc:config key="radius_range_type" configName="MapQuestStoreType" />
		</c:set>
		<c:choose>
			<c:when test="${currentSiteId eq 'BedBathUS'}">
				<c:set var="radius_default_selected">${radius_default_us}</c:set>
				<c:set var="radius_range">${radius_range_us}</c:set>
			</c:when>
			<c:when test="${currentSiteId eq 'BuyBuyBaby'}">
				<c:set var="radius_default_selected">${radius_default_baby}</c:set>
				<c:set var="radius_range">${radius_range_baby}</c:set>
			</c:when>
			<c:when test="${currentSiteId eq 'BedBathCanada'}">
				<c:set var="radius_default_selected">${radius_default_ca}</c:set>
				<c:set var="radius_range">${radius_range_ca}</c:set>
			</c:when>
			<c:otherwise>
				<c:set var="radius_default_selected">${radius_default_us}</c:set>
				<c:set var="radius_range">${radius_range_us}</c:set>
			</c:otherwise>
		</c:choose>
		<input type="hidden" value="${radius_default_selected}" id="defaultRadius" name="defaultRadius">
		</c:if>
		
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
			<dsp:importbean bean="/atg/multisite/Site"/>
			<dsp:importbean bean="/com/bbb/certona/CertonaConfig"/>
			<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
			 <dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
			<dsp:importbean bean="/atg/userprofiling/Profile" />
			<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
			<%--<dsp:getvalueof var="url" vartype="java.lang.String" bean="/OriginatingRequest.requestURI" />--%>
			
			<dsp:getvalueof var="browseSearchVO" param="browseSearchVO"/>
			<dsp:getvalueof var="linkString" param="linkString"/>
			<c:set var="prodCount"><dsp:valueof param="browseSearchVO.bbbProducts.bbbProductCount"/></c:set>
			<c:set var="videoCount"><dsp:valueof param="browseSearchVO.assetMap.Media.count"/></c:set>
			<c:set var="guideCount"><dsp:valueof param="browseSearchVO.assetMap.Guide.count"/></c:set>
			<c:set var="otherCount"><dsp:valueof param="browseSearchVO.assetMap.Other.count"/></c:set>
			<%-- PS: 22013:: set other count as 0 as we are removing other tab from search pages --%> 
			<c:set var="otherCount" value="0"/>
			<c:if test="${empty videoCount }">
			<c:set var="videoCount" value="0"/>
			</c:if>
			<c:if test="${empty prodCount }">
			<c:set var="prodCount" value="0"/>
			</c:if>
			<c:if test="${empty guideCount }">
			<c:set var="guideCount" value="0"/>
			</c:if>
			
			<script type="text/javascript">
				linksCertona = "${linkString}";
			</script>
		    <dsp:getvalueof id="applicationId" bean="Site.id" />
			<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${applicationId}"/>
			
			<c:set var="promoKeyTop"><bbbl:label key="lbl_promo_key_top" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="promoKeyRight"><bbbl:label key="lbl_promo_key_right" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="promoKeyCenter"><bbbl:label key="lbl_promo_key_center" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="promoKeyRelated"><bbbl:label key="lbl_promo_key_related" language="${pageContext.request.locale.language}" /></c:set>
			<dsp:getvalueof var="searchAssetType" param="searchAssetType"/>
			<dsp:droplet name="ForEach">
         		<dsp:param name="array" param="browseSearchVO.autoSuggest" />
           		<dsp:oparam name="output">
					<dsp:getvalueof var="autoPhraseFromVO" param="element.autoPhrase"/>
					<dsp:getvalueof var="searchTermsFromVO" param="element.searchTerms"/>
					<c:if test="${autoPhraseFromVO eq 'true' && searchTermsFromVO eq searchTerm}">
						<dsp:getvalueof var="searchTerm" param="element.spellCorrection"/>
					</c:if>
					<dsp:getvalueof var="spellCorrectionFromVO" param="element.spellCorrection"/>
					<dsp:droplet name="IsNull">
	         			<dsp:param name="value" value="${spellCorrectionFromVO}" />
	         			<dsp:oparam name="false">
	         				<c:if test="${searchTermsFromVO eq searchTerm}">
								<dsp:getvalueof var="searchTerm" param="element.spellCorrection"/>
							</c:if>
						</dsp:oparam>
	         		</dsp:droplet>
           		</dsp:oparam>
           	</dsp:droplet>

			<div id="content" class="subCategory container_12 clearfix" role="main">
			  			 <%--Start: BPS-1952 | Search Within Search | added code for fixing sws auto phrase issue --%>
                    	<c:choose>
                    		<c:when test="${not empty lastEnteredSWSKeyword}">
                   		 		 <c:set var="primarySearchTerm"><dsp:valueof param="Keyword" valueishtml="true" /></c:set>
                    		</c:when>
	                    	<c:otherwise>
	                    		<c:set var="primarySearchTerm"><c:out value="${searchTerm}" escapeXml="true" /></c:set>
	                    	</c:otherwise>
                    	</c:choose>
                    	  <%--End: BPS-1952 | Search Within Search | added code for fixing sws auto phrase issue --%>
				<div class="breadcrumbs grid_12">
					<a href="${scheme}://${servername}" title="<bbbl:label key="lbl_breadcrumb_home_link" language="${pageContext.request.locale.language}" />"><bbbl:label key="lbl_breadcrumb_home_link" language="${pageContext.request.locale.language}" /></a>
					<span class="rightCarrot">&gt;</span>
					<span><bbbl:label key="lbl_header_search_results_page_1" language="${pageContext.request.locale.language}" /> <span class="bold">&ldquo;${primarySearchTerm}"&rdquo;</span></span>
				</div>
				<div class="pageTitle grid_12">
					<h1 class="mainTitle"><bbbl:label key="lbl_header_search_results_page_2" language="${pageContext.request.locale.language}" /></h1>
					<h2 class="subTitle marLeft_5"><bbbl:label key="lbl_header_search_results_page_3" language="${pageContext.request.locale.language}" />&nbsp;${otherCount+videoCount+prodCount+guideCount}&nbsp;<bbbl:label key="lbl_header_search_results_page_4" language="${pageContext.request.locale.language}" />&nbsp;<span class="bold">&ldquo;${primarySearchTerm}&rdquo;</span></h2>
					<dsp:droplet name="ForEach">
		         		<dsp:param name="array" param="browseSearchVO.autoSuggest" />
		           		<dsp:oparam name="output">
		           			<dsp:getvalueof var="autoSuggestTerms" param="element.searchTerms"/>
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
                    <c:if test="${autoPhrase eq 'false'}">
                    <c:if test="${autoCorrected eq 'true' || dymSuggestion eq 'true'}">
                        <h2 class="subTitle block noMar">
                            <c:if test="${autoCorrected eq 'true' && not empty spellCorrection}">
                             <c:set var="keyword"><dsp:valueof param="Keyword"/></c:set>
                                <bbbl:label key="lbl_spell_correction_result" language="${pageContext.request.locale.language}" />
                                <span class="bold">&ldquo;<dsp:valueof value="${autoSuggestTerms}" valueishtml="true" />&rdquo;</span>
                                <%-- <dsp:valueof param="Keyword" valueishtml="true"/> --%>
                                <bbbl:label key="lbl_spell_correction_2" language="${pageContext.request.locale.language}" /> 
                                <span class="bold">&rdquo;<dsp:valueof value="${spellCorrection}"/>&ldquo;</span>&#46;
                            </c:if>
                            <c:if test="${dymSuggestion eq 'true' && empty lastEnteredSWSKeyword}">
                                <dsp:droplet name="ForEach">
                                    <dsp:param name="array" param="browseSearchVO.autoSuggest" />
                                    <dsp:oparam name="outputStart"><bbbl:label key="lbl_did_you_mean_1" language="${pageContext.request.locale.language}" />
                                    </dsp:oparam>
                                    <dsp:oparam name="output">
                                        <dsp:getvalueof var="totalSuggestions" param="size"/>
                                        <dsp:getvalueof var="currentCount" param="count"/>
                                        <%-- R2.2 Story - SEO Friendly URL changes --%>
                                         <c:set var="dymSuggestion"><dsp:valueof param="element.dymSuggestion"/></c:set>
                                        <a href="${contextPath}${urlPrefixForSuggestion}${fn:replace(dymSuggestion,' ','-')}${searchQueryParamsForSuggestion}${vendorParam}" class="bold" title="<dsp:valueof param="element.dymSuggestion"/>"><dsp:valueof param="element.dymSuggestion"/></a>
                                        <c:if test="${totalSuggestions ne currentCount}"><bbbl:label key="lbl_did_you_mean_2" language="${pageContext.request.locale.language}" /></c:if>
                                    </dsp:oparam>
                                    <dsp:oparam name="outputEnd"><bbbl:label key="lbl_did_you_mean_3" language="${pageContext.request.locale.language}" />
                                    </dsp:oparam>
                                </dsp:droplet>
                            </c:if>
                        </h2>
                    </c:if>
                    </c:if>
                    </dsp:droplet>
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
						                    		<a href="${imageHREF}${vendorParam}" title="<dsp:valueof param="element.imageAlt"/>">
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
													<c:if test="${not empty SeperatedValue}">
														<span class="bold marRight_10">${promoKeyRelated} :</span>
														    <dsp:droplet name="ForEach">
																<dsp:param name="array" value="${SeperatedValue}" />
																	<c:set var="currentCount">
																		<dsp:valueof param="count"/></c:set>
																		<c:set var="sanitizedKeyword"><dsp:valueof param="key"/></c:set>
																		<dsp:oparam name="output">
																			<dsp:getvalueof  var="keyWords" param="element" />
																				<c:set var="urlPrefixForSuggestion1" value="${contextPath}/s/" scope="request" />

																					<a href="${urlPrefixForSuggestion1}${sanitizedKeyword}?relatedsearch=true${vendorParam}" title="">${keyWords}<c:if test="${fn:length(SeperatedValue) ne currentCount}">,</c:if></a>
																					
																		</dsp:oparam>
															</dsp:droplet>
															</c:if>
											    </dsp:oparam>
	                                	</dsp:droplet>
                                	</div>
                            	</c:if>
                   </dsp:oparam>
 				</dsp:droplet>
 				
                 <c:set var="pageSize" value="1-${size}"/>
				<div class="<c:out value="${facetClass}"/>">
					 <%--R2.2 Story 116A Changes--%>
	                     <%-- <div id="searchBox">
						<form id="frmSearchCriteria" method="post">
							<fieldset class="searchGroup">
								<div class="searchTitle">
									<h3><bbbl:label key="lbl_search_filter_box_header" language="${pageContext.request.locale.language}" /></h3>
									<dsp:droplet name="ForEach">
                                         <dsp:param name="array" param="browseSearchVO.descriptors" />
                                         <dsp:oparam name="outputStart">
                                         	<dsp:getvalueof var="size" param="size"/>
                                           	<dsp:getvalueof var="type" param="element.rootName"/>
                                           	<c:if test="${(size ge 1) && (type ne 'RECORD TYPE')}">
                                       			
                                       			<a rel="nofollow" href="${url}?${searchQueryParams}" class="lnkSearchReset" title="Reset"><bbbl:label key="lbl_search_filter_box_reset" language="${pageContext.request.locale.language}" /></a>
                                       		</c:if>
                                  		</dsp:oparam>
                                    </dsp:droplet>
								</div>
								<div class="searchContent searchKeyword clearfix">
									<div class="searchListTitle"><bbbl:label key="lbl_search_filter_box_keyword" language="${pageContext.request.locale.language}" /></div>
									<div class="searchListItem"><c:out value="${searchTerm}" escapeXml="true"/></div> 
								</div>
								<div class="searchContent noBorder">
									<dsp:droplet name="ForEach">
	                                       <dsp:param name="array" param="browseSearchVO.descriptors" />
	                                       <dsp:oparam name="outputStart">
	                                       	<dsp:getvalueof var="size" param="size"/>
	                                       	<dsp:getvalueof var="type" param="element.rootName"/>
	                                       	<c:if test="${type ne 'RECORD TYPE'}">
	                                            <div class="searchListTitle"><bbbl:label key="lbl_search_filter_list_header" language="${pageContext.request.locale.language}" /></div>
	                                        </c:if>
	                                           <ul class="searchList"> 
	                                       </dsp:oparam>
	                                       <dsp:oparam name="output">
	                                       	<dsp:getvalueof var="type" param="element.rootName"/>
	                                       	<c:if test="${type ne 'RECORD TYPE'}">
	                                        	<li class="clearfix">
	                                                <div class="searchListItem">
	                                                    <dsp:valueof param="element.name" valueishtml="true"/>
	                                                    
	                                                    <dsp:getvalueof var="facetItemRemoveQuery" param="element.removalQuery"/>
	                                                    <dsp:getvalueof var="facetDescFilter" param="element.descriptorFilter"/>
	                                                    <a rel="nofollow" href="${url}/${facetDescFilter}/${pageSize}?${facetItemRemoveQuery}${searchQueryParams}" class="lnkSearchRemove" title="Remove">X</a>
	                                                </div> 
	                                            </li>
	                                       	</c:if>
	                                       </dsp:oparam>
	                                       <dsp:oparam name="outputEnd">
	                                           </ul>
	                                       </dsp:oparam>
	                            	</dsp:droplet>
								</div>
							</fieldset>
						</form>
					</div>--%>
					<dsp:include page="/_includes/modules/faceted_bar.jsp">
						<dsp:param name="browseSearchVO" param="browseSearchVO" />
						<dsp:param name="url" value="${url}" />
						<dsp:param name="showDepartment" value="true" />
					</dsp:include>
				</div>
				<div id="prodGridContainer" class="<c:out value="${gridClass}"/>">

					<div id="pagTop" class="<c:out value="${gridClass}"/> alpha omega">
						<dsp:include page="/_includes/modules/pagination_top_2bar.jsp">
								<dsp:param name="searchAssetType" value="${searchAssetType}"/>
								<dsp:param name="browseSearchVO" param="browseSearchVO" />	
								<dsp:param name="Keyword" param="Keyword"/>
								<dsp:getvalueof var="view" param="view" />
								<dsp:param name="view" value="${view}"/>
								<dsp:param name="pageSectionValue" value="topOfResultSet"/>
								<dsp:param name="swdFlag" value="${swdFlag}" />
						</dsp:include>
					</div>

					<div class="grid_8 alpha">
						<dsp:droplet name="ResultListDroplet">
							<dsp:param name="browseSearchVO" value="${browseSearchVO}"/>
							<dsp:oparam name="output">
							<dsp:getvalueof var="productList" param="productList" scope="request"/>
								<dsp:include page="/_includes/modules/product_list_1x10.jsp">
									<dsp:param name="browseSearchVO" param="browseSearchVO" />
									<dsp:param name="BBBProductListVO" param="BBBProductListVO"/>
									<dsp:param name="productList" param="productList"/>
								</dsp:include>
							</dsp:oparam>
						</dsp:droplet>
					</div>
					<c:if test="${plpGridSize == '5'}">
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
											<div class="promo-2col">
											<c:choose>
												<c:when test="${not empty imageHREF}">
			                                	<a href="${imageHREF}" title="<dsp:valueof param="element.imageAlt"/>"><img src="${scene7Path}/${imageURL}" alt="<dsp:valueof param="element.imageAlt"/>" /></a>	
												</c:when>
												<c:otherwise>
			                                		<img src="${scene7Path}/${imageURL}" alt="<dsp:valueof param="element.imageAlt"/>" />	
												</c:otherwise>
											</c:choose>
                                            </div>
	                                    </dsp:oparam>
	                             	</dsp:droplet>
	                          	</c:if>
                    		</dsp:oparam>
                    	</dsp:droplet>
					</div>
					</c:if>
					<div id="pagBot" class="<c:out value="${gridClass}"/> alpha omega">
						<c:import url="/_includes/modules/pagination_bottom_full.jsp" />
						<dsp:param name="pageSectionValue" value="bottomOfResultSet"/>
					</div>
					<c:set var="cert_prod_count"><bbbc:config key="certona_search_pdt_count" configName="CertonaKeys" /></c:set>
					<c:set var="searchTerm"><c:out value="${searchTerm}" escapeXml="true"/></c:set>		
					<input type="hidden" name="searchTerm" value="${searchTerm}"/>	
					<input type="hidden" name="currentEPHScheme" value="${currentEPHScheme}"/>
					<c:if test="${inStore != 'true'}">
					<div id="botCrossSell" class="clearfix searchCertona loadAjaxContent" role="complementary"
						data-ajax-url="${contextPath}/search/search_certona_slots.jsp"
						data-ajax-params-count="3"
						data-ajax-param1-name="searchterms"
						data-ajax-param1-value=""
						data-ajax-param2-name="productList"
						data-ajax-param2-value="${productList}"
						data-ajax-param3-name="number"
						data-ajax-param3-value="${cert_prod_count}">
						<div class="clearfix">
							<img width="20" height="20" alt="small loader" src="/_assets/global/images/widgets/small_loader.gif" />
						</div>
					</div>
					</c:if>
					<div id="sa_s22_instagram" class="sa_s22_instagram_category"></div>
				</div>
				

				
				<script>
				var socialAnnexFlagCondition = '${socialAnnexFlag}';
					if($('#pageWrapper').hasClass('searchResults') && socialAnnexFlagCondition.toLowerCase() == "true"){

						var sa_page="3"; // constant for the page slider
						var sa_site_id= '${saSrc}';//pass by client
						var sa_instagram_category_type = 'search'; // category | brand | search | college i.e. for id type
						var sa_instagram_category_code = '${searchTerm}'; // Category Code | College Name | Brand Name | Search Text pass by client
						console.log(sa_instagram_category_type);
						(function() {
							function sa_async_load() {
								var sa = document.createElement('script');sa.type = 'text/javascript';
								sa.async = true;sa.src = sa_site_id;
								var sax = document.getElementsByTagName('script')[0];sax.parentNode.insertBefore(sa, sax);
					       }
					       if(window.attachEvent){
							window.attachEvent('onload', sa_async_load);
					       } else {
							window.addEventListener('load', sa_async_load,false);
					       }
						}());
					} else{
						resx.appid = "${appIdCertona}";
						resx.links = linksCertona;
						resx.customerid = "${userId}";
						resx.Keyword = "${term}";
					}
				</script>
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
			
		</jsp:body>

        <dsp:getvalueof var="pagNum" param="pagNum"/>
		<jsp:attribute name="footerContent">
			<dsp:getvalueof var="frmBrandPage" param="frmBrandPage"/>
<%--BBBSL-8716 | PLP Fixes for BOTs --%>	
		<c:if test="${not isRobotOn }">				
           <script type="text/javascript">
           var pagNum='${pagNum}';
           var serchTerm = '<dsp:valueof value="${searchTerm}"/>';
           var searchDepartment = '${searchDepartment}';
           var typeAhead = decodeURIComponent('<dsp:valueof param="typeAhead" />');
           var searchType = (searchDepartment == '') ? ((typeAhead =='' || typeAhead =='typeahead' )? '': typeAhead.toLowerCase()) : searchDepartment.toLowerCase();
           var colon = searchType == '' ? '' : ':';
           var currentCat = "${currentCategory}";
           var parentCat = "${parentCategory}";
           var fromTypeAhead = '${ta}';
           var isFromSWD = "${isFromSWD}";
           //debugger;
		   var queryDataRelatedSearch = BBB.fn.parseQuerystring();  
				if(queryDataRelatedSearch.relatedsearch=='true'){
					s.eVar57="related searches>"+serchTerm;
				}           
			s.pageName='Search';
			s.channel='Search';
           if(typeof s !=='undefined') {
        	<c:if test="${frmBrandPage eq 'true'}">
				s.channel='Brand Search';
			</c:if>  

			s.prop1='Search';
			s.prop2='Search';
			s.prop3='Search';
			s.prop4='';
			s.prop5='';
			s.prop6='';
			s.eVar61="${OmnitureVariable}"
			if(isFromSWD == 'true'){				
				var appendParentCat = '';
				<c:if test="${!(parentCategory eq '')}">
					appendParentCat = parentCat + ' in ';
				</c:if>
				colon = ':';
				if(appendParentCat == '' && currentCat == ''){
					colon = '';
				}
				s.prop7= serchTerm.toLowerCase() + colon + appendParentCat + currentCat;
/* 			}
			else{
				s.prop7= serchTerm.toLowerCase() + colon + searchType;
			} */
			s.prop8=''
			s.prop25="List View";
			s.eVar47="List View";
			if(pagNum=='' || pagNum==1){
			s.events='event1';
           }
			s.eVar2=serchTerm.toLowerCase();
			var s_code=s.t();
			if(s_code)document.write(s_code);		
           }
        </script>
     </c:if>   
    </jsp:attribute>
		
		
	</bbb:pageContainer>
</dsp:page>
