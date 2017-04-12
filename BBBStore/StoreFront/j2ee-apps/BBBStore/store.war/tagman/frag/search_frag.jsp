<dsp:page>
	<dsp:getvalueof id="searchterm" param="searchTerm"/>
	<%-- Begin TagMan --%>
		<script type="text/javascript">	
			if (typeof window.tmParam !== 'undefined') { window.tmParam.search_query = '${fn:escapeXml(searchterm)}'; }
		</script>
	<%-- End TagMan --%>
</dsp:page>