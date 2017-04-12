<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BreadcrumbDroplet"/>
	<dsp:getvalueof var="siteId" bean="Site.id" />
	<dsp:getvalueof var="categoryId" param="categoryId"/>
		
	<dsp:droplet name="BreadcrumbDroplet">
		<dsp:param name="categoryId" param="categoryId" />
		<dsp:param name="siteId" param="siteId"/>
			<dsp:oparam name="output">
				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
				<dsp:param name="array" param="breadCrumb" />
					<dsp:oparam name="outputStart">
						<c:set var="categoryPath" scope="request"></c:set>
					</dsp:oparam>
					<dsp:oparam name="output">
						<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="element.categoryName" /></c:set>		
					</dsp:oparam>
				</dsp:droplet>
			</dsp:oparam>
	</dsp:droplet>	
	<%-- Begin TagMan --%>
	<script type="text/javascript"> 
			// client configurable parameters 
			window.tmParam.action_type = 'pv';
			window.tmParam.pageType = 'ProductCategory';
			window.tmParam.page_breadcrumb = '${categoryPath}';		
			window.tmParam.page_type = 'CategoryLanding';
		</script>
	<%-- End TagMan --%>
</dsp:page>