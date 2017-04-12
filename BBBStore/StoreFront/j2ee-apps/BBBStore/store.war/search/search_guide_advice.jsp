<dsp:page>
	<dsp:getvalueof var="searchTerm" value="${Keyword}"/>
    <dsp:getvalueof var="pagNum" param="pagNum"/>
    <dsp:getvalueof var="CatalogId" param="CatalogId"/>
    <c:set var="lbl_bluepill_remove"><bbbl:label key="lbl_bluepill_remove" language ="${pageContext.request.locale.language}"/></c:set>
    <%@ include file="/_includes/modules/getVendorParam.jsp"%>
    <dsp:getvalueof var="metaVendorParam" bean="SessionBean.vendorParam"/>
    <c:choose>
		<c:when test="${not empty metaVendorParam}">
    		<c:set var="follow" value="false"/>
			<c:set var="index" value="false"/>
    	</c:when>
		<c:otherwise>
			<c:set var="follow" value="true"/>
			<c:set var="index" value="true"/>
		</c:otherwise>
	</c:choose>
    <bbb:pageContainer>
    <jsp:attribute name="section">search</jsp:attribute>
    <jsp:attribute name="pageWrapper">searchList searchResults useFB</jsp:attribute>
    <jsp:attribute name="titleString">${searchTerm}: Guides</jsp:attribute>
	<jsp:attribute name="index">${index}</jsp:attribute>
	<jsp:attribute name="follow">${follow}</jsp:attribute>
    <jsp:attribute name="PageType">Search</jsp:attribute>
    <jsp:body>
		<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
		<dsp:getvalueof var="browseSearchVO" param="browseSearchVO"/>
		<dsp:getvalueof var="searchAssetType" param="searchAssetType"/>
		<dsp:getvalueof var="view" param="view"/>
		<dsp:getvalueof var="frmBrandPage" param="frmBrandPage"/>
		<dsp:getvalueof var="frmCollegePage" param="fromCollege"/>
		<dsp:getvalueof var="isRedirect" param="isRedirect"/>
		<dsp:getvalueof var="searchDepartment" param="searchDepartment"  />
		<dsp:getvalueof var="typeAhead" param="typeAhead"  />
		<c:if test="${TagManOn}">
			<dsp:include page="/tagman/frag/search_frag.jsp" >
				<dsp:param name="searchTerm" value="${searchTerm}"/>
			</dsp:include>
		</c:if>
		<%-- R2.2 story PLP/Grid view demo feedback incorporation --%>
		<c:set var="guideGridClass" value="grid_6"/>
		<c:if test="${plpGridSize == '3'}">
			<c:set var="guideGridClass" value="grid_5"/>
		</c:if>
			     <%--Start: BPS-1952 | Search Within Search | added code for fixing sws auto phrase issue | Commentee below code and moved to search_autophrase_frag.jsp--%>

		<%-- <c:set var="prodCount"><dsp:valueof param="browseSearchVO.assetMap.Product.count"/></c:set>
		<c:set var="videoCount"><dsp:valueof param="browseSearchVO.assetMap.Media.count"/></c:set>
		<c:set var="guideCount"><dsp:valueof param="browseSearchVO.bbbProducts.bbbProductCount"/></c:set>
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
		</c:if> --%>

        <%-- <c:set var="totalCount">
        <c:if test="${not empty prodCount }">${prodCount}</c:if>
        <c:if test="${not empty videoCount }">${videoCount}</c:if>
        <c:if test="${not empty guideCount }">${guideCount}</c:if>
        <c:if test="${not empty otherCount }">${otherCount}</c:if>
        </c:set> --%>
       	     <%--End: BPS-1952 | Search Within Search | added code for fixing sws auto phrase issue | Commentee below code and moved to search_autophrase_frag.jsp--%>

        <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
        <dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
        <dsp:importbean bean="/com/bbb/browse/AddContextPathDroplet"/>
        	     <%--Start: BPS-1952 | Search Within Search | added code for fixing sws auto phrase issue | Commentee below code and moved to search_autophrase_frag.jsp--%>

		<%-- <dsp:droplet name="ForEach">
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
       	</dsp:droplet> --%>
       		     <%--End: BPS-1952 | Search Within Search | added code for fixing sws auto phrase issue | Commentee below code and moved to search_autophrase_frag.jsp--%>

        <c:set var="promoKeyTop"><bbbl:label key="lbl_promo_key_top" language="${pageContext.request.locale.language}" /></c:set>
        <c:set var="promoKeyCenter"><bbbl:label key="lbl_promo_key_center" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="promoKeyRelated"><bbbl:label key="lbl_promo_key_related" language="${pageContext.request.locale.language}" /></c:set>
        <div id="content" class="subCategory container_12 clearfix" role="main">
       				<%--Start: BPS-1952 | Search Within Search | added code for fixing sws auto phrase issue  --%>
					<dsp:include page="search_autophrase_frag.jsp"/>
					<%--End: BPS-1952 | Search Within Search | added code for fixing sws auto phrase issue --%>
        	     <%--Start: BPS-1952 | Search Within Search | added code for fixing sws auto phrase issue | Commentee below code and moved to search_autophrase_frag.jsp--%>
        	       <%--  <div class="breadcrumbs grid_12">
	            <a href="${contextPath}" title="<bbbl:label key="lbl_breadcrumb_home_link" language="${pageContext.request.locale.language}"/>"><bbbl:label key="lbl_breadcrumb_home_link" language="${pageContext.request.locale.language}" /></a>
	            <span class="rightCarrot">&gt;</span>
	            <span><bbbl:label key="lbl_header_search_results_page_1" language="${pageContext.request.locale.language}" /> <span class="bold">&ldquo;<c:out value="${searchTerm}" escapeXml="true"/>&rdquo;</span></span>
	        </div>
            <div class="pageTitle grid_12">
	            <h1 class="mainTitle"><bbbl:label key="lbl_header_search_results_page_2" language="${pageContext.request.locale.language}" /></h1>
	            <h2 class="subTitle marLeft_5"><bbbl:label key="lbl_header_search_results_page_3" language="${pageContext.request.locale.language}" />&nbsp;${otherCount+videoCount+prodCount+guideCount}&nbsp;<bbbl:label key="lbl_header_search_results_page_4" language="${pageContext.request.locale.language}" />&nbsp;<span class="bold">&ldquo;<c:out value="${searchTerm}" escapeXml="true"/>&rdquo;</span></h2>
	            <h2 class="subTitle block noMar">
					<c:if test="${autoPhrase eq 'false'}">
                    <c:if test="${autoCorrected eq 'true'}">
                     <c:set var="keyword"><dsp:valueof param="Keyword"/></c:set>
                    	<bbbl:label key="lbl_spell_correction_result" language="${pageContext.request.locale.language}" />
                    	<span class="bold">>&ldquo;<c:out value="${keyword}" escapeXml="false"/>&rdquo;</span>
                    	<bbbl:label key="lbl_spell_correction_2" language="${pageContext.request.locale.language}" />&rdquo;
                    	<span class="bold">&ldquo;<dsp:valueof value="${spellCorrection}"/> &rdquo;</span>&#46;
                    </c:if>
                    <c:if test="${dymSuggestion eq 'true'}">
                    	<dsp:droplet name="ForEach">
			         		<dsp:param name="array" param="browseSearchVO.autoSuggest" />
			         		<dsp:oparam name="outputStart"><bbbl:label key="lbl_did_you_mean_1" language="${pageContext.request.locale.language}" />
			         		</dsp:oparam>
			           		<dsp:oparam name="output">
			           			<dsp:getvalueof var="totalSuggestions" param="size"/>
			           			<dsp:getvalueof var="currentCount" param="count"/>
			         			R2.2 Story - SEO Friendly URL changes
			         			<a href="${urlPrefixForSuggestion}<dsp:valueof param="element.dymSuggestion"/>${searchQueryParamsForSuggestion}" title="<dsp:valueof param="element.dymSuggestion"/>" class="bold"><dsp:valueof param="element.dymSuggestion"/></a>
			         			<c:if test="${totalSuggestions ne currentCount}"><bbbl:label key="lbl_did_you_mean_2" language="${pageContext.request.locale.language}" /></c:if>
			           		</dsp:oparam>
			           		<dsp:oparam name="outputEnd"><bbbl:label key="lbl_did_you_mean_3" language="${pageContext.request.locale.language}" />
			         		</dsp:oparam>
			           	</dsp:droplet>
                    </c:if>
                    </c:if>
                </h2>
            </div> --%>
	     <%--End: BPS-1952 | Search Within Search | added code for fixing sws auto phrase issue | Commentee below code and moved to search_autophrase_frag.jsp--%>

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


            <div class="<c:out value="${facetClass}"/>">
            	<div id="searchBox">
            		<form id="frmSearchCriteria" method="post">
		                <fieldset class="searchGroup">
							<legend class="offScreen"><bbbl:label key="promoKeyRelated" language="${pageContext.request.locale.language}" /></legend>
	                            <div class="searchTitle">
		                        <h3><bbbl:label key="lbl_search_filter_box_header" language="${pageContext.request.locale.language}" /></h3>
		                         <dsp:getvalueof	var="descriptorsList" param="browseSearchVO.descriptors"/>
		                        <dsp:getvalueof var="size" value="${fn:length(descriptorsList)}"/>
		                        
		                         <c:if test="${(size ge 1) && (descriptorsList[size-1].rootName ne 'RECORD TYPE')}">
		                          	<%-- R2.2 Story - SEO Friendly URL changes --%>
		                          	<a  href="${url}?${searchQueryParams}" class="lnkSearchReset" title="Reset"><bbbl:label key="lbl_search_filter_box_reset" language="${pageContext.request.locale.language}" /></a>
		                          </c:if>
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
	                                                 <%-- R2.2 Story - SEO Friendly URL changes --%>
	                                                 <dsp:getvalueof var="facetItemRemoveQuery" param="element.removalQuery"/>
	                                                    <dsp:getvalueof var="facetDescFilter" param="element.descriptorFilter"/>
	                                                 <a  href="${url}/${facetDescFilter}/1-${size}?${facetItemRemoveQuery}${searchQueryParams}" class="lnkSearchRemove" aria-label="${lbl_bluepill_remove}" role="button" title="${lbl_bluepill_remove}">X</a>
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
					<div class="facetBoxArrow"></div>
				</div>
	            <c:import url="/_includes/modules/faceted_bar.jsp" />
	            <dsp:param name="url" value="${url}" />
	            <dsp:param name="showDepartment" value="true" />
	     	</div>
            <div id="prodGridContainer" class="<c:out value="${gridClass}"/> guideAdvice">
                <div id="pagTop">
                	<%--<c:import url="/_includes/modules/pagination_top_bar_guide_advice.jsp" /> --%>
                    <dsp:include page="/_includes/modules/pagination_top_bar_guide_advice.jsp" >
                          <dsp:param name="searchAssetType" value="${searchAssetType}"/>
                          <dsp:param name="browseSearchVO" param="browseSearchVO" />
                   </dsp:include>
				</div>
         		<dsp:droplet name="ForEach">
                	<dsp:param name="array" param="browseSearchVO.bbbProducts.bbbProducts" />
                    <dsp:oparam name="output">
	                    <dsp:getvalueof var="currentCount" param="index"/>
	                    <dsp:getvalueof var="guideTitle" param="element.guideTitle"/>
	                    <dsp:getvalueof var="seoUrl" param="element.seoUrl"/>
	                    <dsp:getvalueof var="total" param="size"/>
	                     <dsp:getvalueof var="guideId" param="element.guideId"/>
							<c:choose>
								<c:when test="${empty seoUrl}">
									<dsp:droplet name="/atg/repository/seo/GuideItemLink">
										<dsp:param name="repositoryName" value="/com/bbb/cms/repository/GuidesTemplate" />
	                                    <dsp:param name="itemDescriptor" value="guides" />
	                                    <dsp:param name="id" param="element.guideId"/>
										<dsp:oparam name="output">
											<dsp:getvalueof var="finalUrl" vartype="java.lang.String" param="url" />
										</dsp:oparam>
									</dsp:droplet>
								</c:when>
								<c:otherwise>
									<c:set var="finalUrl" value="${seoUrl}"></c:set>
								</c:otherwise>
							</c:choose>
                            <%-- disabling highlighting og first two results as there is no business logic involved
                            <c:if test="${currentCount==0 && (empty pagNum || pagNum eq 1)}">
                               <div class="grid_10 containerBorder ">
                            </c:if>
                            --%>
                            <div class="grid_9 alpha omega infoBox">
                            	<div class="grid_1">
                                	<dsp:getvalueof var="guideImage" param="element.guideImage"/>
                               		<c:choose>
                                    	 <c:when test="${not empty guideImage }">
                                        	<img src='<dsp:valueof param="element.guideImage"/>' class="noImageFound" height="63" width="63" alt="${guideTitle}" />
                                     	 </c:when>
	                                     <c:otherwise>
	                                     	<img src="${imagePath}/_assets/global/images/no_image_available.jpg" height="63" width="63" alt="${guideTitle}" />
	                                     </c:otherwise>
                                  	</c:choose>
								</div>
                                <div class="grid_8 alpha omega clearfix productInfoWrapper">
                                	<h3 class="<c:out value="${guideGridClass}"/> alpha omega noMar marBottom_10"><c:url value="${finalUrl}" var="url">
                                	   <c:param name="guideId" value="${guideId}"/>
									   <c:param name="CatalogId" value="${CatalogId}"/>
			                        <c:param name="Keyword" value="${searchTerm}"/>
			                        <c:param name="CatalogId" value="${CatalogId}"/>
		                           </c:url><dsp:a href="${url}" title="${guideTitle}">${guideTitle}</dsp:a></h3>
                                    <c:if test="${FBOn}">
                                    <!--[if IE 7]>
                                        <dsp:droplet name="/com/bbb/account/droplet/BBBURLEncodingDroplet">
                                            <dsp:param name="URL" value="${url}"/>
                                            <dsp:oparam name="output">
                                                <dsp:getvalueof id="encodedURL" param="encodedURL"/>
                                            </dsp:oparam>
                                        </dsp:droplet>
                                        <dsp:droplet name="/com/bbb/account/droplet/BBBConfigKeysDroplet">
                                            <dsp:param name="configKey" value="FBAppIdKeys"/>
                                            <dsp:oparam name="output">
                                                <dsp:getvalueof var="fbAppIDConfigMap" param="configMap"/>
                                            </dsp:oparam>
                                        </dsp:droplet>
                                        <div class="fb-like grid_3 alpha omega">
                                            <iframe type="some_value_to_prevent_js_error_on_ie7" title="Facebook Like" src="<bbbc:config key='fb_like_plugin_url' configName='ThirdPartyURLs' />?href=${encodedURL}&amp;send=false&amp;layout=button_count&amp;width=90&amp;show_faces=false&amp;action=like&amp;colorscheme=light&amp;font&amp;height=24&amp;appId=${fbConfigMap[currentSiteId]}" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:90px; height:24px;" allowTransparency="true"></iframe>
                                        </div>
                                    <![endif]-->
                                    <!--[if !IE 7]><!-->
                                        <div data-href="${pageContext.request.scheme}://${pageContext.request.serverName}${url}" class="fb-like grid_3 alpha omega" data-layout="button_count" data-send="false" data-width="90" data-show-faces="false"></div>
                                    <!--<![endif]-->
                                    </c:if>
                                    <p><dsp:valueof param="element.guideShortDesc"/></p>
                                </div>
							</div>
                            <%--
                            <c:if test="${((total eq 1 && currentCount==0) || (total ne 1 && currentCount eq 1)) && (empty pagNum || pagNum eq 1)}">
                      	</div>
                            </c:if> --%>
						</dsp:oparam>
					</dsp:droplet>
                    <div id="pagBot">
                           <c:import url="/_includes/modules/pagination_bottom_guide_advice_full.jsp" />
                    </div>
             	</div>
       		</div>
		</jsp:body>
			<jsp:attribute name="footerContent">
	
<%--BBBSL-8716 | PLP Fixes for BOTs --%>	
		<c:if test="${not isRobotOn }">		
           <script type="text/javascript">
           var pagNum='${pagNum}';
           var serchTerm = '<dsp:valueof value="${searchTerm}"/>';
           var searchDepartment = '${searchDepartment}';
           var typeAhead = decodeURIComponent('<dsp:valueof param="typeAhead" />');
           var searchType = (searchDepartment == '') ? (typeAhead =='' ? '': typeAhead.toLowerCase()) : searchDepartment.toLowerCase();
           var colon = searchType == '' ? '' : ':';
           if(typeof s !=='undefined') {
			s.channel='Search';
			s.pageName='Search';
			s.prop1='Search';
			s.prop2='Search';
			s.prop3='Search';
			s.prop4='';
			s.prop5='';
			s.prop6='';
			s.prop7=serchTerm.toLowerCase() + colon + searchType;
			s.prop8='<dsp:valueof value="${resultCount}"/>';
			s.prop9='<bbbl:label key="lbl_search_tab_text_guides" language="${pageContext.request.locale.language}"/>';
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