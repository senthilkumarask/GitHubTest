<dsp:page>
	<div class="small-12 columns large-no-padding-right">
		<form id="frmPagination" method="post" action="#">
			<dsp:getvalueof param="browseSearchVO" var="browseSearchVO"/>
			<dsp:getvalueof var="searchAssetType" param="searchAssetType"/>

			<%-- search tabs --%>
			<div class="row show-for-large-up">
				<dsp:include page="/search/search_tabs.jsp">
					<dsp:param name="searchAssetType" value="${searchAssetType}"/>
					<dsp:param name="browseSearchVO" param="browseSearchVO" />	
				</dsp:include>
			</div>

			<div class="row">
				<div class="small-12 right columns show-for-medium-down no-padding">
					<a class="right-off-canvas-toggle right secondary expand button"><span>Filter</span></a>
				</div>
			</div>
			<input type='submit' class='hide' value='submit' name='submit'/>
		</form>
	</div>
</dsp:page>