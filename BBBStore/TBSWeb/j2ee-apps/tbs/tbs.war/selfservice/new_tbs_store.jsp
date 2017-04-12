<dsp:page>
	<dsp:importbean bean="/com/bbb/selfservice/TBSMapQuestDroplet"/>
	<dsp:importbean bean="/com/bbb/framework/security/ValidateParametersDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/RedirectDroplet"/>
	<dsp:getvalueof var="ZipcodeParam" value="${param.Zipcode}" />
	<dsp:getvalueof var="radius" value="${param.radius}"/>
	<dsp:getvalueof var="siteId" bean="/atg/multisite/Site.id" />
	<dsp:getvalueof var="miles" value=""/>
	<dsp:getvalueof var="changedRadius" value=""/>
	<dsp:getvalueof var="latLongMap" value=""/>
	
	<c:set var="ZipcodeParam"><dsp:valueof value="${fn:escapeXml(param.Zipcode)}"/></c:set>
	<c:set var="radius"><dsp:valueof value="${fn:escapeXml(param.radius)}"/></c:set>
	<c:set var="Zipcode" value="${fn:replace(ZipcodeParam,' ', '')}" />
	<c:set var="skuid"><dsp:valueof value="${param.skuid}"/></c:set>
	<c:set var="itemQuantity"><dsp:valueof value="${param.itemQuantity}"/></c:set>
	<c:set var="id"><dsp:valueof value="${param.id}"/></c:set>
	<c:set var="registryId"><dsp:valueof value="${param.registryId}"/></c:set>
	<c:set var="commerceId"><dsp:valueof value="${param.commerceId}"/></c:set>
	<c:set var="shipid"><dsp:valueof value="${param.shipid}"/></c:set>
	<c:if test="${not empty param.skuid}">
		<c:set var="parameter" value="skuid"></c:set>
		<c:set var="parametervalue" value="${param.skuid}"></c:set>
	</c:if>
	<c:if test="${not empty param.registryId}">
		<c:set var="parameter" value="${parameter};registryId"></c:set>
		<c:set var="parametervalue" value="${parametervalue};${param.registryId}"></c:set>
	</c:if>
	<c:if test="${not empty param.siteId}">
		<c:set var="parameter" value="${parameter};siteId"></c:set>
		<c:set var="parametervalue" value="${parametervalue};${param.siteId}"></c:set>
	</c:if>
	<c:if test="${not empty param.commerceId}">
		<c:set var="parameter" value="${parameter};commerceId"></c:set>
		<c:set var="parametervalue" value="${parametervalue};${param.commerceId}"></c:set>
	</c:if>
	<c:if test="${not empty param.shipid}">
		<c:set var="parameter" value="${parameter};shipid"></c:set>
		<c:set var="parametervalue" value="${parametervalue};${param.shipid}"></c:set>
	</c:if>
	<c:if test="${not empty param.id}">
		<c:set var="parameter" value="${parameter};productId"></c:set>
		<c:set var="parametervalue" value="${parametervalue};${param.id}"></c:set>
	</c:if>
	<c:if test="${not empty param.Zipcode}">
		<c:set var="parameter" value="${parameter};Zipcode"></c:set>
		<c:set var="parametervalue" value="${parametervalue};${param.Zipcode}"></c:set>
	</c:if>
		<c:if test="${not empty param.itemQuantity}">
		<c:set var="parameter" value="${parameter};itemQuantity"></c:set>
		<c:set var="parametervalue" value="${parametervalue};${param.itemQuantity}"></c:set>
	</c:if>
	<dsp:droplet name="ValidateParametersDroplet">
		<dsp:param value="${parameter}" name="paramArray" />
        <dsp:param value="${parametervalue}" name="paramsValuesArray" />
			<dsp:oparam name="error">
				<dsp:droplet name="RedirectDroplet">
					<dsp:param name="url" value="/404.jsp" />
				</dsp:droplet>
			</dsp:oparam>
			</dsp:droplet>		
	<dsp:droplet name="TBSMapQuestDroplet">
		<dsp:param name="Zipcode" value="${Zipcode}"/>
		<dsp:param name="radius" value="${radius}"/>
		<dsp:param name="siteId" value="${siteId}"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="latLongMap" param="result"></dsp:getvalueof>
			<dsp:getvalueof var="miles" param="miles"/>
			<dsp:getvalueof var="changedRadius" param="changedRadius"/>
		</dsp:oparam>
		<dsp:oparam name="error">
			<dsp:getvalueof var="latLongMap" param="result"></dsp:getvalueof>
			error while retrieving the stores.
		</dsp:oparam>
	</dsp:droplet> 
	<c:if test="${empty changedRadius}">
		<c:set var="changedRadius"><dsp:valueof value="25"></dsp:valueof></c:set>
	</c:if>	
	<c:if test="${not empty latLongMap}">
	<dsp:include page="find_tbs_store.jsp">
		<dsp:param name="miles" value="${miles}"/>
		<dsp:param name="latLongMap" value="${latLongMap}"/>
		<dsp:param name="changedRadius" value="${changedRadius}"/>
		<dsp:param name="Zipcode" value="${Zipcode}"/>
		<dsp:param name="skuid" value="${skuid}" />
		<dsp:param name="itemQuantity" value="${itemQuantity}" />
		<dsp:param name="id" value="${id}" />
		<dsp:param name="siteId" value="${siteId}" />
		<dsp:param name="skuId" value="${param.skuId}" />
		<dsp:param name="registryId" value="${registryId}" />
		<dsp:param name="qty" value="${itemQuantity}" />
		<dsp:param name="commerceId" value="${commerceId}"/>
		<dsp:param name="shipid" value="${shipid}"/>
	</dsp:include>
	</c:if>
</dsp:page>