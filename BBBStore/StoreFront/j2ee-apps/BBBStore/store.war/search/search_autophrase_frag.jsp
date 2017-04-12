<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:getvalueof id="servername" idtype="java.lang.String" bean="/OriginatingRequest.servername"/>
<dsp:getvalueof id="scheme" idtype="java.lang.String" bean="/OriginatingRequest.scheme"/>
<dsp:page>
<dsp:getvalueof var="lastEnteredSWSKeyword" param="lastEnteredSWSKeyword" />
<dsp:getvalueof var="searchAssetType" param="searchAssetType" />
	<c:set var="prodCount">
		<dsp:valueof param="browseSearchVO.assetMap.Product.count" />
	</c:set>
	<c:choose>
		<c:when test="${fn:containsIgnoreCase(searchAssetType, 'media')}">
			<c:set var="videoCount">
				<dsp:valueof param="browseSearchVO.bbbProducts.bbbProductCount" />
			</c:set>
		</c:when>
		<c:otherwise>
	<c:set var="videoCount">
				<dsp:valueof param="browseSearchVO.assetMap.Media.count" />
			</c:set>
		</c:otherwise>
	</c:choose>
	<c:choose>
		<c:when test="${fn:containsIgnoreCase(searchAssetType, 'guide')}">
			<c:set var="guideCount">
		<dsp:valueof param="browseSearchVO.bbbProducts.bbbProductCount" />
	</c:set>
		</c:when>
		<c:otherwise>
	<c:set var="guideCount">
		<dsp:valueof param="browseSearchVO.assetMap.Guide.count" />
	</c:set>
		</c:otherwise>
	</c:choose>
	<c:set var="otherCount">
		<dsp:valueof param="browseSearchVO.assetMap.Other.count" />
	</c:set>
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
	<c:choose>
		<c:when test="${not empty lastEnteredSWSKeyword}">
			<c:set var="primarySearchTerm">
				<dsp:valueof param="Keyword" valueishtml="true" />
			</c:set>
		</c:when>
		<c:otherwise>
			<c:set var="primarySearchTerm">
				<c:out value="${searchTerm}" escapeXml="true" />
			</c:set>
		</c:otherwise>
	</c:choose>
	<div class="breadcrumbs grid_12">
		<a href="${scheme}://${servername}"
			title="<bbbl:label key="lbl_breadcrumb_home_link" language="${pageContext.request.locale.language}"/>"><bbbl:label
				key="lbl_breadcrumb_home_link"
				language="${pageContext.request.locale.language}" /></a> <span
			class="rightCarrot">&gt;</span> <span><bbbl:label
				key="lbl_header_search_results_page_1"
				language="${pageContext.request.locale.language}" /> <span
			class="bold">&ldquo;${primarySearchTerm}&rdquo;
		</span></span>
	</div>
	<div class="pageTitle grid_12">
		<h1 class="mainTitle">
			<bbbl:label key="lbl_header_search_results_page_2"
				language="${pageContext.request.locale.language}" />
		</h1>
		<h2 class="subTitle marLeft_5">
			<bbbl:label key="lbl_header_search_results_page_3"
				language="${pageContext.request.locale.language}" />
			&nbsp;${otherCount+videoCount+prodCount+guideCount}&nbsp;
			<bbbl:label key="lbl_header_search_results_page_4"
				language="${pageContext.request.locale.language}" />
			&nbsp;<span class="bold">&ldquo;${primarySearchTerm}&rdquo;
			</span>
		</h2>
		<h2 class="subTitle block noMar">
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
				<c:if test="${autoCorrected eq 'true' && not empty spellCorrection}">
					<c:set var="keyword">
						<dsp:valueof param="Keyword" />
					</c:set>
					<bbbl:label key="lbl_spell_correction_result"
						language="${pageContext.request.locale.language}" />
					<span class="bold">&ldquo;<dsp:valueof value="${autoSuggestTerms}" valueishtml="true" />&rdquo;</span>
					<bbbl:label key="lbl_spell_correction_2"
						language="${pageContext.request.locale.language}" />
					<span class="bold">&ldquo;<dsp:valueof
							value="${spellCorrection}" />&rdquo;
					</span>&#46;
	                    </c:if>
				<c:if test="${dymSuggestion eq 'true' && empty lastEnteredSWSKeyword}">
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="browseSearchVO.autoSuggest" />
						<dsp:oparam name="outputStart">
							<bbbl:label key="lbl_did_you_mean_1"
								language="${pageContext.request.locale.language}" />
						</dsp:oparam>
						<dsp:oparam name="output">
							<dsp:getvalueof var="totalSuggestions" param="size" />
							<dsp:getvalueof var="currentCount" param="count" />
							<%-- R2.2 Story - SEO Friendly URL changes --%>
							 <c:set var="dymSuggestion"><dsp:valueof param="element.dymSuggestion"/></c:set>
							<a
								href="${urlPrefixForSuggestion}${fn:replace(dymSuggestion,' ','-')}${searchQueryParamsForSuggestion}"
								title="<dsp:valueof param="element.dymSuggestion"/>"
								class="bold"><dsp:valueof param="element.dymSuggestion" /></a>
							<c:if test="${totalSuggestions ne currentCount}">
								<bbbl:label key="lbl_did_you_mean_2"
									language="${pageContext.request.locale.language}" />
							</c:if>
						</dsp:oparam>
						<dsp:oparam name="outputEnd">
							<bbbl:label key="lbl_did_you_mean_3"
								language="${pageContext.request.locale.language}" />
						</dsp:oparam>
					</dsp:droplet>
				</c:if>
			</c:if>
			</dsp:droplet>
		</h2>
	</div>
</dsp:page>
