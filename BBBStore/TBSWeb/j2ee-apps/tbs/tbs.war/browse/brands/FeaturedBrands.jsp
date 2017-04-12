<dsp:importbean bean="/com/bbb/commerce/browse/droplet/BrandDetailDroplet" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:page>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
		<dsp:droplet name="ForEach">
			<dsp:param name="array" param="featuredBrands" />
			<dsp:oparam name="outputStart">
				<div class="featuredImg clearfix">	
			</dsp:oparam>
			<dsp:oparam name="output">
				<dsp:getvalueof var="imageURL" param="element.brandImage" />
				<dsp:getvalueof var="imageHref" param="element.featuredBrandURL" />
				<dsp:getvalueof var="brandName" param="element.brandImageAltText"/>
				<dsp:getvalueof var="brand" param="element.brandName" />
				<dsp:getvalueof var="url" param="element.seoURL"/>
				<dsp:getvalueof param="element.brandImageAltText" var="altText" />
				<%-- R2.2 Story - SEO Friendly URL changes --%>
				<a  href="${contextPath}${url}" 
					title="${brandName}"> <img src="${scene7Path}/${imageURL}" alt="${brandName}" width="137" height="103"/> </a>
			</dsp:oparam>
			<dsp:oparam name="outputEnd">
				</div>	
			</dsp:oparam>
		</dsp:droplet>
</dsp:page>