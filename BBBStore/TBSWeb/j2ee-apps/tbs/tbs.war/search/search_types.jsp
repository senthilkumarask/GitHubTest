<%-- R2.2 SEO Friendly URL changes --%>
<dsp:page>
	<dsp:getvalueof id="searchTerm" value="${Keyword}"/>
	<dsp:getvalueof var="defaultView" param="view"/>
	
	<dsp:getvalueof var="browseSearchVO" param="browseSearchVO"/>
	<dsp:getvalueof var="searchMode" param="browseSearchVO.urlSearchMode"/>
	<c:set var="searchMode" value="&searchMode=${searchMode}"/>
	<dsp:getvalueof var="partialFlag" param="browseSearchVO.partialFlag"/>
	<c:set var="partialFlag" value="&partialFlag=${partialFlag}"/>
	<dsp:getvalueof var="searchAssetType" param="searchAssetType"/>
	
	
	<h3>Results</h3>
	<ul class="category-list">
		<c:set var="product"><bbbl:label key="lbl_search_tab_value_product" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="guide"><bbbl:label key="lbl_search_tab_value_guides" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="media"><bbbl:label key="lbl_search_tab_value_videos" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="guide_tbs">Guide_TBS</c:set>
		<c:set var="other"><bbbl:label key="lbl_search_tab_value_others" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="bracOpen"><bbbl:label key="lbl_regsrchguest_bracOpen" language="${pageContext.request.locale.language}" /></c:set>
		<c:set var="bracClose"><bbbl:label key="lbl_regsrchguest_bracClose" language="${pageContext.request.locale.language}" /></c:set>
		<c:choose>
			<c:when test="${searchAssetType eq product}">
				<li class="active">
					<a href="#" rel="nofollow" class="redirPage" ><bbbl:label key="lbl_search_tab_text_product" language="${pageContext.request.locale.language}" />&nbsp;(<dsp:valueof param="browseSearchVO.bbbProducts.bbbProductCount"/>)</a>
				</li>			
			</c:when>
			<c:when test="${searchAssetType ne product}">
				<%-- If Product is in assetMap, show it is inactive --%>	
				<dsp:droplet name="/atg/dynamo/droplet/IsNull">
					<dsp:param name="value" param="browseSearchVO.assetMap.Product"/>
					<dsp:oparam name="false">
						<li>
							<c:set var="productLabel"><bbbl:label key="lbl_search_tab_text_product" language="${pageContext.request.locale.language}" /> </c:set>
							<c:set var="productCount"><dsp:valueof param="browseSearchVO.assetMap.Product.count"/></c:set>
							<a data-submit-param-length="1" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${fn:escapeXml(origSearchTerm)}" rel="nofollow" class="redirPage dynFormSubmit" href="${url}/1-${size}?<dsp:valueof param="browseSearchVO.assetMap.Product.query"/>${searchQueryParams}${partialFlag}${searchMode}" onclick="javascript:searchTabTrack('${productLabel}','${productCount}')">
								<bbbl:label key="lbl_search_tab_text_product" language="${pageContext.request.locale.language}" />&nbsp;(<dsp:valueof param="browseSearchVO.assetMap.Product.count"/>)</a>
						</li>									
					</dsp:oparam>	
					<dsp:oparam name="true">
						<li>
							<c:set var="productLabel"><bbbl:label key="lbl_search_tab_text_product" language="${pageContext.request.locale.language}" /> </c:set>
							<c:set var="productCount">0</c:set>
							<a href="#" rel="nofollow" class="redirPage" onclick="javascript:searchTabTrack('${productLabel}','${productCount}')"><bbbl:label key="lbl_search_tab_text_product" language="${pageContext.request.locale.language}" />&nbsp;(0)</a>
						</li>
					</dsp:oparam>							
				</dsp:droplet>
			</c:when>
		 </c:choose>
<%--  		 	 <c:choose>
		 	<c:when test="${searchAssetType eq media}">
				<li class="active">
					<a href="#" rel="nofollow" class="redirPage"><bbbl:label key="lbl_search_tab_text_videos" language="${pageContext.request.locale.language}" />&nbsp;(<dsp:valueof param="browseSearchVO.bbbProducts.bbbProductCount"/>)</a>
				</li>			
			</c:when>
			<c:when test="${searchAssetType ne media}">
				<!-- If Media is in assetMap, show it is inactive -->
				<dsp:droplet name="/atg/dynamo/droplet/IsNull">
					<dsp:param name="value" param="browseSearchVO.assetMap.Media"/>
					<dsp:oparam name="false">
						<li>
							<c:set var="mediaLabel"><bbbl:label key="lbl_search_tab_text_videos" language="${pageContext.request.locale.language}" /> </c:set>
                            <c:set var="mediaCount"><dsp:valueof param="browseSearchVO.assetMap.Media.count"/></c:set>
                            <dsp:getvalueof var="assetFilter" param="browseSearchVO.assetMap.Media.assetFilter"/>
                            <a data-submit-param-length="1" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" rel="nofollow" class="redirPage dynFormSubmit" rel="nofollow" href="${url}/${assetFilter}/1-${size}?<dsp:valueof param="browseSearchVO.assetMap.Media.query"/>${searchQueryParams}&media=true&pagFilterOpt=${mediaCount}${partialFlag}${searchMode}" onclick="javascript:searchTabTrack('${mediaLabel}','${mediaCount}')"><bbbl:label key="lbl_search_tab_text_videos" language="${pageContext.request.locale.language}" />&nbsp;(${mediaCount})</a>
                        </li>									
					</dsp:oparam>				
				</dsp:droplet>		
			</c:when>
  		 </c:choose> --%>
  		 
		
		<c:choose>
			<c:when test="${searchAssetType eq guide_tbs}">
				<li class="active">
					<a href="#" rel="nofollow" class="redirPage"><bbbl:label key="lbl_search_tab_text_guides" language="${pageContext.request.locale.language}" />&nbsp;(<dsp:valueof param="browseSearchVO.bbbProducts.bbbProductCount"/>)</a>
				</li>			
			</c:when>
			<c:when test="${searchAssetType ne guide_tbs}">
				<%-- If Guides is in assetMap, show it is inactive --%>		
				<dsp:droplet name="/atg/dynamo/droplet/IsNull">
					<dsp:param name="value" param="browseSearchVO.assetMap.Guide_TBS"/>
					<dsp:oparam name="false">
						<li>
							<c:set var="guideLabel"><bbbl:label key="lbl_search_tab_text_guides" language="${pageContext.request.locale.language}" /> </c:set>
							<c:set var="guideCount"><dsp:valueof param="browseSearchVO.assetMap.Guide_TBS.count"/></c:set>
							<dsp:getvalueof var="assetFilter" param="browseSearchVO.assetMap.Guide_TBS.assetFilter"/>
							<a data-submit-param-length="1" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${fn:escapeXml(origSearchTerm)}" rel="nofollow" class="redirPage dynFormSubmit" href="${url}/${assetFilter}/1-${size}?<dsp:valueof param="browseSearchVO.assetMap.Guide_TBS.query"/>${searchQueryParams}${partialFlag}${searchMode}" rel="nofollow" onclick="javascript:searchTabTrack('${guideLabel}','${guideCount}')"><bbbl:label key="lbl_search_tab_text_guides" language="${pageContext.request.locale.language}" />&nbsp;(<dsp:valueof param="browseSearchVO.assetMap.Guide_TBS.count"/>)</a>
						</li>									
					</dsp:oparam>				
				</dsp:droplet>			
			</c:when>				 		
		</c:choose>
<%--   		<c:choose>
			<c:when test="${searchAssetType eq other}">
				<li class="active">
					<a href="#" rel="nofollow" class="redirPage"><bbbl:label key="lbl_search_tab_text_others" language="${pageContext.request.locale.language}" />&nbsp;(<dsp:valueof param="browseSearchVO.bbbProducts.bbbProductCount"/>)</a>
				</li>			
			</c:when>
			<c:when test="${searchAssetType ne other}">
				<!-- If Other is in assetMap, show it is inactive -->		
				<dsp:droplet name="/atg/dynamo/droplet/IsNull">
					<dsp:param name="value" param="browseSearchVO.assetMap.Other"/>
					<dsp:oparam name="false">
						<li>
							<c:set var="othersLabel"><bbbl:label key="lbl_search_tab_text_others" language="${pageContext.request.locale.language}" /> </c:set>
                            <c:set var="othersCount"><dsp:valueof param="browseSearchVO.assetMap.Other.count"/></c:set>
                            <dsp:getvalueof var="assetFilter" param="browseSearchVO.assetMap.Other.assetFilter"/>
							<a data-submit-param-length="1" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${origSearchTerm}" rel="nofollow" class="redirPage dynFormSubmit" rel="nofollow" href="${url}/${assetFilter}/1-${size}?<dsp:valueof param="browseSearchVO.assetMap.Other.query"/>${searchQueryParams}${partialFlag}${searchMode}" onclick="javascript:searchTabTrack('${othersLabel}','${othersCount}')"><bbbl:label key="lbl_search_tab_text_others" language="${pageContext.request.locale.language}" />&nbsp;(<dsp:valueof param="browseSearchVO.assetMap.Other.count"/>)</a>
						</li>
					</dsp:oparam>				
				</dsp:droplet>	
			</c:when>
		 </c:choose> --%>			 		
	</ul>
</dsp:page>