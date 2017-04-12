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
		<c:choose>
			<c:when test="${searchAssetType eq product}">
				<li class="active">
					<a href="#" rel="nofollow" class="redirPage" ><bbbl:label key="lbl_search_tab_text_product" language="${pageContext.request.locale.language}" />&nbsp;(<dsp:valueof param="browseSearchVO.bbbProducts.bbbProductCount"/>)</a>
				</li>			
			</c:when>
			<c:when test="${searchAssetType ne product}">
						<li>
							<c:set var="productLabel"><bbbl:label key="lbl_search_tab_text_product" language="${pageContext.request.locale.language}" /> </c:set>
							<c:set var="productCount"><dsp:valueof value="2"/></c:set>
							<a data-submit-param-length="1" data-submit-param1-name="origSearchTerm" data-submit-param1-value="${fn:escapeXml(origSearchTerm)}" rel="nofollow" class="redirPage dynFormSubmit" href="${url}/1-${size}?type=upc" onclick="javascript:searchTabTrack('${productLabel}','${productCount}')">
								<bbbl:label key="lbl_search_tab_text_product" language="${pageContext.request.locale.language}" />&nbsp;(<dsp:valueof param="browseSearchVO.assetMap.Product.count"/>)</a>
						</li>									
						<li>
							<c:set var="productLabel"><bbbl:label key="lbl_search_tab_text_product" language="${pageContext.request.locale.language}" /> </c:set>
							<c:set var="productCount">0</c:set>
							<a href="#" rel="nofollow" class="redirPage" onclick="javascript:searchTabTrack('${productLabel}','${productCount}')"><bbbl:label key="lbl_search_tab_text_product" language="${pageContext.request.locale.language}" />&nbsp;(0)</a>
						</li>
			</c:when>
		 </c:choose>
	</ul>
</dsp:page>