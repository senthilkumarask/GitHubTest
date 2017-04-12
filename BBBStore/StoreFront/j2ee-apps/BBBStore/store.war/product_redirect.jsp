<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/atg/targeting/RepositoryLookup"/>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProductRedirectDroplet"/>
	<dsp:importbean bean="/atg/multisite/Site"/>	
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/RedirectDroplet"/>
	<dsp:getvalueof var="sku_id" param="sku" />
	<dsp:getvalueof var="writeReview" param="writeReview" />
	<dsp:getvalueof var="url" value="/404.jsp" />
	<dsp:getvalueof var="appid" bean="Site.id" />

	<dsp:droplet name="ProductRedirectDroplet">
		<dsp:param name="siteId" value="${appid}"/>
		<dsp:param name="skuId" value="${sku_id}"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="url" param="product.seoUrl"/>
			<%-- Got The product URL ${url}--%>
		</dsp:oparam>
	</dsp:droplet>
	<dsp:droplet name="IsEmpty">
		<dsp:param name="value" value="${writeReview}" />
		<dsp:oparam name="false">
			<dsp:getvalueof var="url" value="${url}?writeReview=${writeReview}" />
		</dsp:oparam>
	</dsp:droplet>
	
	<dsp:droplet name="IsEmpty">
		<dsp:param name="value" value="${url}" />
		<dsp:oparam name="true">
			<dsp:include page="/404.jsp" />
		</dsp:oparam>
	</dsp:droplet>
	
	<c:choose>
		<c:when test="${fn:contains(url,'/404.jsp')}">
			<dsp:include page="/404.jsp" />
		</c:when>
		<c:otherwise>
			<dsp:droplet name="RedirectDroplet">
				<dsp:param name="url" value="${url}" />
			</dsp:droplet>
		</c:otherwise>
	</c:choose>
</dsp:page>