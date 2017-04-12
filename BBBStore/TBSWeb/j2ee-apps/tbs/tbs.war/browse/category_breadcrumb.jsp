<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BreadcrumbDroplet"/>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<dsp:getvalueof id="contextroot" idtype="java.lang.String" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof var="CategoryId" param="categoryId"/>
<dsp:getvalueof var="appid" bean="Site.id" />
<c:set var="categoryPath" value="" scope="request" />
	<dsp:droplet name="BreadcrumbDroplet">
	<dsp:param name="categoryId" param="categoryId" />
	<dsp:param name="siteId" value="${appid}"/>
		<dsp:oparam name="output">
			<dsp:droplet name="ForEach">
			<dsp:param name="array" param="breadCrumb" />
				<dsp:oparam name="outputStart">
				<c:set var="categoryPath" scope="request"><bbbl:label key='lbl_pdp_breadcrumb_home' language="${pageContext.request.locale.language}" /></c:set>
				</dsp:oparam>
				<dsp:oparam name="output">
					<c:set var="categoryPath" scope="request">${categoryPath} > <dsp:valueof param="element.categoryName" /></c:set>		
				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>	
</dsp:page>