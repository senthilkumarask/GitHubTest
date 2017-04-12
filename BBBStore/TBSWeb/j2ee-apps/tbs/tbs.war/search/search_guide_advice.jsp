<dsp:page>
	<dsp:getvalueof var="searchTerm" value="${Keyword}"/>
    <dsp:getvalueof var="pagNum" param="pagNum"/>
    <dsp:getvalueof var="CatalogId" param="CatalogId"/>
    <dsp:importbean	bean="/com/bbb/profile/session/SessionBean"/>
    <dsp:getvalueof var="metaVendorParam" bean="SessionBean.vendorParam"/>
    <c:choose>
		<c:when test="${not empty metaVendorParam}">
    		<c:set var="follow" value="false"/>
			<c:set var="index" value="false"/>
    	</c:when>
		<c:otherwise>
			<c:set var="follow" value="false"/>
			<c:set var="index" value="true"/>
		</c:otherwise>
	</c:choose>
    <bbb:pageContainer>
    	<jsp:attribute name="section">search</jsp:attribute>
    	<jsp:attribute name="pageWrapper">searchList searchResults useFB</jsp:attribute>
    	<jsp:attribute name="titleString">Search Results for ${searchTerm}: Guides</jsp:attribute>
    	<jsp:attribute name="index">${index}</jsp:attribute>
		<jsp:attribute name="follow">${follow}</jsp:attribute>
    	<jsp:attribute name="PageType">Search</jsp:attribute> 
		<jsp:attribute name="bodyClass">search-grid</jsp:attribute> 
    <jsp:body>
		<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
		<dsp:getvalueof var="browseSearchVO" param="browseSearchVO"/>
		<dsp:getvalueof var="searchAssetType" param="searchAssetType"/>
		<dsp:getvalueof var="view" param="view"/>
		<dsp:getvalueof var="frmBrandPage" param="frmBrandPage"/>
		<dsp:getvalueof var="frmCollegePage" param="fromCollege"/>
		<dsp:getvalueof var="isRedirect" param="isRedirect"/>
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
		
		<c:set var="prodCount"><dsp:valueof param="browseSearchVO.assetMap.Product.count"/></c:set>
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
		</c:if>
                     
                     
        <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
        <dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
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
        <c:set var="promoKeyTop"><bbbl:label key="lbl_promo_key_top" language="${pageContext.request.locale.language}" /></c:set>
        <c:set var="promoKeyCenter"><bbbl:label key="lbl_promo_key_center" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="promoKeyRelated"><bbbl:label key="lbl_promo_key_related" language="${pageContext.request.locale.language}" /></c:set>
			
			<div class="row">
				<div class="small-12 columns no-padding">
					<h1><bbbl:label key="lbl_header_search_results_page_2" language="${pageContext.request.locale.language}" /></h1>
					
					<c:choose>
		                    <c:when test="${prodCount!=0 || (prodCount==0 and empty partialSearchList)}">
		                    	<h2>${otherCount+videoCount+prodCount+guideCount}&nbsp;<bbbl:label key="lbl_header_search_results_page_4" language="${pageContext.request.locale.language}" />&nbsp;&ldquo;<c:out value="${searchTerm}" escapeXml="true"/>&rdquo;</h2>	
		                    </c:when>
		                   	<c:when test="${ prodCount==0 and not empty partialSearchList}">
		                   		<h2>${otherCount+videoCount+prodCount+guideCount}&nbsp;<bbbl:label key="lbl_header_search_results_page_4" language="${pageContext.request.locale.language}" />&nbsp;&ldquo;<c:out value="${searchTerm}" escapeXml="true"/>&rdquo;<bbbl:label key="lbl_partialforzero_search_results" language="${pageContext.request.locale.language}" /></h2>	
		                   	</c:when>
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
	     	
	     	<div class="row">
		     	<div id="left-nav" class="large-3 columns small-medium-right-off-canvas-menu left-nav">
		     		<h3><bbbl:label key="lbl_search_filter_box_header" language="${pageContext.request.locale.language}" /></h3>
		     		<div class="searchContent searchKeyword clearfix">
                        <div class="searchListTitle"><bbbl:label key="lbl_search_filter_box_keyword" language="${pageContext.request.locale.language}" /></div>
                        <div class="searchListItem"><c:out value="${searchTerm}" escapeXml="true"/></div>
                    </div>
			        <div class="row show-for-medium-down">
						<dsp:include page="/search/search_types.jsp">
							<dsp:param name="searchAssetType" value="${searchAssetType}"/>
							<dsp:param name="browseSearchVO" param="browseSearchVO" />	
							<dsp:param name="Keyword" param="Keyword"/>
						</dsp:include>
					</div>
			        
                    <dsp:include page="/_includes/modules/faceted_bar.jsp">
						<dsp:param name="browseSearchVO" param="browseSearchVO" />
						<dsp:param name="view" value="${view}"/>
                            <dsp:param name="frmBrandPage" value="${frmBrandPage}"/>
                            <dsp:param name="url" value="${url}"/>
					</dsp:include>
		        </div>
		        
		        
            	<div class="small-12 large-9 columns guideadvice-grid-container no-padding-left">
                
                	<dsp:include page="/_includes/modules/pagination_top_bar_guide_advice.jsp" >
                          <dsp:param name="searchAssetType" value="${searchAssetType}"/>
                          <dsp:param name="browseSearchVO" param="browseSearchVO" />     
                   </dsp:include>
				<hr>
				
         		<dsp:droplet name="ForEach">
                	<dsp:param name="array" param="browseSearchVO.bbbProducts.bbbProducts" />
                	<dsp:oparam name="outputStart">
                		
                	</dsp:oparam>
                	<dsp:oparam name="outputEnd">
                		
                	</dsp:oparam>
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
                            
                            <div class="row">
                            	<div class="small-12 medium-2 columns">
	                            	<dsp:getvalueof var="guideImage" param="element.guideImage"/>
                               		<c:choose>
                                    	 <c:when test="${not empty guideImage }">
                                        	<img src='<dsp:valueof param="element.guideImage"/>' class="noImageFound" alt="${guideTitle}" title="${guideTitle}"/>
                                     	 </c:when>
	                                     <c:otherwise>
	                                     	<img src="${imagePath}/_assets/global/images/no_image_available.jpg" alt="${guideTitle}" title="${guideTitle}"/>
	                                     </c:otherwise>
                                  	</c:choose>
                            	</div>
                            	<div class="small-12 medium-10 columns">
	                            	<c:url value="${finalUrl}" var="url">
                                	   <c:param name="guideId" value="${guideId}"/>
									   <c:param name="CatalogId" value="${CatalogId}"/>
			                        <c:param name="Keyword" value="${searchTerm}"/>
			                        <c:param name="CatalogId" value="${CatalogId}"/>
		                           </c:url>
									<h2 class="subheader"><dsp:a href="${url}" title="${guideTitle}">${guideTitle}</dsp:a></h2>
		                           <p><dsp:valueof param="element.guideShortDesc"/></p>
                                </div>
                            </div>
                            <hr>
						</dsp:oparam>
					</dsp:droplet>
                    <dsp:include page="/_includes/modules/pagination_bottom_full.jsp">
                     	<dsp:param name="Keyword" param="Keyword"/>
                          <dsp:param name="view" value="${view}"/>
                    </dsp:include>
             	</div>
	     	</div>
		</jsp:body>
			<jsp:attribute name="footerContent">
           <script type="text/javascript">
           var pagNum='${pagNum}';
           var serchTerm = '<dsp:valueof value="${searchTerm}"/>';
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
    </jsp:attribute>
	</bbb:pageContainer>
</dsp:page>