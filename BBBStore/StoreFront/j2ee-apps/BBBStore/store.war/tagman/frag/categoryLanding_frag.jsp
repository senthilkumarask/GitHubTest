<dsp:page>
	<dsp:getvalueof id="categoryPath" param="categoryPath" />
	<c:set var="homeLabel" scope="request"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></c:set>
	
	<%-- Begin TagMan --%>
	<script type="text/javascript">		
		window.tmParam.page_breadcrumb = '${homeLabel} ${categoryPath}';
	</script>
	<%-- End TagMan --%>
</dsp:page>