<dsp:page>
	<div class="pagination">
		<form id="frmPagination" method="post" action="#">
			<fieldset class="pagGroupings clearfix">
				<legend class="offScreen"><bbbl:label key="lbl_search_tab_text_videos" language="${pageContext.request.locale.language}" /></legend>
		
				<dsp:getvalueof param="browseSearchVO" var="browseSearchVO"/>
				<dsp:getvalueof var="searchAssetType" param="searchAssetType"/>
	
				<%-- search tabs --%>
				<dsp:include page="/search/search_tabs.jsp">
						<dsp:param name="searchAssetType" value="${searchAssetType}"/>
						<dsp:param name="browseSearchVO" param="browseSearchVO" />	
				</dsp:include>
				
			</fieldset>		
		</form>
	</div>
</dsp:page>