<dsp:page>
	<dsp:importbean	bean="/com/bbb/profile/session/SessionBean"/>
	<dsp:getvalueof id="searchTerm" value="${Keyword}"/>
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
		<jsp:attribute name="pageWrapper">searchList Other Results</jsp:attribute>
		<jsp:attribute name="titleString">Search Results for ${searchTerm}: Other Results</jsp:attribute>
		<jsp:attribute name="index">${index}</jsp:attribute>
		<jsp:attribute name="follow">${follow}</jsp:attribute>
		<jsp:attribute name="PageType">Search</jsp:attribute>
		<jsp:attribute name="bodyClass">search-grid</jsp:attribute>   
		<jsp:body>
			<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
			<dsp:getvalueof var="browseSearchVO" param="browseSearchVO"/>
			<dsp:getvalueof var="searchAssetType" param="searchAssetType"/>
			<dsp:getvalueof var="url" param="url"/>
			<c:set var="prodCount"><dsp:valueof param="browseSearchVO.assetMap.Product.count"/></c:set>
			<c:set var="videoCount"><dsp:valueof param="browseSearchVO.assetMap.Media.count"/></c:set>
			<c:set var="guideCount"><dsp:valueof param="browseSearchVO.assetMap.Guide_TBS.count"/></c:set>
			<c:set var="otherCount"><dsp:valueof param="browseSearchVO.bbbProducts.bbbProductCount"/></c:set>
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
			<c:if test="${TagManOn}">
				<dsp:include page="/tagman/frag/search_frag.jsp" >
					<dsp:param name="searchTerm" value="${searchTerm}"/>
				</dsp:include>
			</c:if>
			
			<c:set var="promoKeyTop"><bbbl:label key="lbl_promo_key_top" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="promoKeyLeft"><bbbl:label key="lbl_promo_key_left" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="promoKeyCenter"><bbbl:label key="lbl_promo_key_center" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="promoKeyRelated"><bbbl:label key="lbl_promo_key_related" language="${pageContext.request.locale.language}" /></c:set>
			
			<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
            <dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
            <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
			
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
           	
			<div class="row">
				<div class="small-12 columns no-padding">
					<h1><bbbl:label key="lbl_header_search_results_page_2" language="${pageContext.request.locale.language}" /></h1>
					<h2>${otherCount+videoCount+prodCount+guideCount}&nbsp;<bbbl:label key="lbl_header_search_results_page_4" language="${pageContext.request.locale.language}" />&nbsp;<span class="bold">&ldquo;<c:out value="${searchTerm}" escapeXml="true"/>&rdquo;</span></h2>
					<h2 class="subTitle block noMar">
						<c:if test="${autoPhrase eq 'false'}"> 
	                    <c:if test="${autoCorrected eq 'true'}">
	                     <c:set var="keyword"><dsp:valueof param="Keyword"/></c:set>
	                    	<bbbl:label key="lbl_spell_correction_result" language="${pageContext.request.locale.language}" />
	                    	<span class="bold">&ldquo;<c:out value="${keyword}" escapeXml="false"/>&rdquo;</span>
	                    	<bbbl:label key="lbl_spell_correction_2" language="${pageContext.request.locale.language}" />&ldquo; 
	                    	<span class="bold">&rdquo;<dsp:valueof value="${spellCorrection}"/>&ldquo;</span>&#46;
	                    	
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
				         			<a href="${urlPrefixForSuggestion}<dsp:valueof param="element.dymSuggestion"/>${searchQueryParamsForSuggestion}" title="<dsp:valueof param="element.dymSuggestion"/>" class="bold"><dsp:valueof param="element.dymSuggestion"/></a>
				         			<c:if test="${totalSuggestions ne currentCount}"><bbbl:label key="lbl_did_you_mean_2" language="${pageContext.request.locale.language}" /></c:if>
				           		</dsp:oparam>
				           		<dsp:oparam name="outputEnd"><bbbl:label key="lbl_did_you_mean_3" language="${pageContext.request.locale.language}" />
				         		</dsp:oparam>
				           	</dsp:droplet>
	                    </c:if>
						</c:if>
	                </h2>
				</div>
			</div>
				
				<div class="row">
					<div id="left-nav" class="large-3 columns small-medium-right-off-canvas-menu left-nav">
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
			        
					<div class="small-12 large-9 columns otherresults-grid-container no-padding-left">
				
						<dsp:include page="/_includes/modules/top_bar_otherResults.jsp" >
							<dsp:param name="searchAssetType" value="${searchAssetType}"/>
							<dsp:param name="browseSearchVO" param="browseSearchVO" />	
						</dsp:include>
					<hr>
					<dsp:valueof param="elementList"/>
					<dsp:droplet name="ForEach">
                     	<dsp:param name="array" param="browseSearchVO.bbbProducts.otherResults" />
                     	<dsp:oparam name="outputStart">
                     	</dsp:oparam>
                     	<dsp:oparam name="outputEnd">
                     	</dsp:oparam>
                        <dsp:oparam name="output">			
							<dsp:droplet name="ForEach">
		                     	<dsp:param name="array" param="element" />
		                     	<dsp:getvalueof var="otherPageType" param="element.otherPageType"/>
		                        <dsp:oparam name="output">
		                        	<dsp:getvalueof var ="othResLink" param="element.othResLink"/>
									<c:set var="othResLinkNew" value="${fn:replace(othResLink, '/store','/tbs')}" />                   
		                			<c:choose>
										<c:when test="${otherPageType eq '202'}">
		                                	<h2 class="subheader"><a href="${othResLinkNew}" class="popup" title="<dsp:valueof param="key"/>"><dsp:valueof param="key" valueishtml="true"/></a></h2>
											<p class="noMar"><a href="${othResLinkNew}"><dsp:valueof param="element.othResTitle" valueishtml="true"/></a></p>
		                               	</c:when>
		                                <c:otherwise>
		                                	<h2 class="subheader"><a href="${othResLinkNew}"><dsp:valueof param="key" valueishtml="true"/></a></h2>
		                                	<p><a href="${othResLinkNew}"><dsp:valueof param="element.othResTitle" valueishtml="true"/></a></p>
		                                </c:otherwise>	
									</c:choose>
								</dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
					</dsp:droplet>
				</div>
			</div>
		</jsp:body>
	</bbb:pageContainer>
</dsp:page>