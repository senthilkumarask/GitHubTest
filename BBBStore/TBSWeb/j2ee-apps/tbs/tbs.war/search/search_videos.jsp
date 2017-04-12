<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:page>
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof id="searchTerm" value="${Keyword}"/>
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
		<jsp:attribute name="pageWrapper">searchList videos useLiveClicker</jsp:attribute>
		<jsp:attribute name="titleString">Search Results for ${searchTerm}: Videos</jsp:attribute>
		<%-- 504D implementation --%>
		<jsp:attribute name="index">${index}</jsp:attribute>
		<jsp:attribute name="follow">${follow}</jsp:attribute>
		<jsp:attribute name="PageType">Search</jsp:attribute> 
		<jsp:attribute name="bodyClass">search-grid videos useLiveClicker</jsp:attribute> 
		<jsp:body>
			<c:if test="${TagManOn}">
				<dsp:include page="/tagman/frag/search_frag.jsp" >
					<dsp:param name="searchTerm" value="${searchTerm}"/>
				</dsp:include>
			</c:if>
			<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
			<dsp:getvalueof var="browseSearchVO" param="browseSearchVO"/>
			<dsp:getvalueof var="searchAssetType" param="searchAssetType"/>
			<dsp:getvalueof var="url" param="url"/>
			<c:set var="prodCount"><dsp:valueof param="browseSearchVO.assetMap.Product.count"/></c:set>
			<c:set var="videoCount"><dsp:valueof param="browseSearchVO.bbbProducts.bbbProductCount"/></c:set>
			<c:set var="guideCount"><dsp:valueof param="browseSearchVO.assetMap.Guide_TBS.count"/></c:set>
			<c:set var="otherCount"><dsp:valueof param="browseSearchVO.assetMap.Other.count"/></c:set>
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
			<c:set var="idString" >
			<dsp:droplet name="ForEach"><dsp:param name="array" param="browseSearchVO.bbbProducts.bbbProducts" /><dsp:oparam name="output"><dsp:getvalueof var="videoId" param="element.videoId"/><dsp:getvalueof var="count" param="count"/><dsp:getvalueof var="size" param="size"/><c:choose><c:when test="${count ne size }"><dsp:valueof param="element.videoId"/>,</c:when><c:otherwise><dsp:valueof param="element.videoId"/></c:otherwise></c:choose></dsp:oparam></dsp:droplet></c:set>
			
			
<script type="text/javascript" language="javascript">
//Mashup source
var mashup_url =
'<bbbc:config key="liveclicker_searchresults_${appid}" configName="BBBLiveClicker"/><dsp:valueof value="${idString}"/>';
// Keep track of the iframe height.

</script>
			<c:set var="promoKeyTop"><bbbl:label key="lbl_promo_key_top" language="${pageContext.request.locale.language}" /></c:set>
			<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
			<c:set var="promoKeyCenter"><bbbl:label key="lbl_promo_key_center" language="${pageContext.request.locale.language}" /></c:set>
			<c:set var="promoKeyRelated"><bbbl:label key="lbl_promo_key_related" language="${pageContext.request.locale.language}" /></c:set>
			
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
				<div class="small-12 large-offset-3 large-9 columns video-grid-container no-padding-left">
					<dsp:include page="/_includes/modules/top_bar_videos.jsp" >
						<dsp:param name="searchAssetType" value="${searchAssetType}"/>
						<dsp:param name="browseSearchVO" param="browseSearchVO" />	
					</dsp:include>
				</div>
				<hr>
				<c:if test="${LiveClickerOn}">	
                <div class="clear"></div>
				<div id="iframe">
				</div>
				</c:if>
					
		</jsp:body>
	</bbb:pageContainer>
</dsp:page>