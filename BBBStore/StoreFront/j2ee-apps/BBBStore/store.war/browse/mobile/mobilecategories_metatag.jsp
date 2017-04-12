<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/MobileCategoryOrderDroplet"/>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:getvalueof id="contextroot" idtype="java.lang.String" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof var="CategoryId" param="categoryId"/>
<dsp:getvalueof var="appid" bean="Site.id" />
<c:set var="categoryPath" value="" scope="request" />
	<dsp:droplet name="MobileCategoryOrderDroplet">
	<dsp:param name="categoryId" param="categoryId" />
	<dsp:param name="siteId" value="${appid}"/>
		<dsp:oparam name="output">
			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
			<dsp:param name="array" param="categoryOrder" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="size" param="size"/>
					<dsp:getvalueof var="count" param="count"/>
					<c:set var="categoryPath" scope="request">${categoryPath}<dsp:valueof param="element.categoryName" /></c:set>		
					<c:if test="${size ne count}">
						<c:set var="categoryPath" scope="request">${categoryPath}|</c:set>
					</c:if>
				</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>	
</dsp:page>