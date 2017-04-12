<%@page contentType="application/json"%>

<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
<%-- <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/DisplayChecklistActiveCategoryDroplet"/> --%>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:page>
<%-- <dsp:droplet name="DisplayChecklistActiveCategoryDroplet">
   	<dsp:param name="registryType" value="${registryType}"/>
   	<dsp:param name="registryId" value="${registryId}"/>
   	<dsp:param name="fromRegistryActivity" value="true"/>
</dsp:droplet> --%>
<dsp:getvalueof var="prevNextCategoriesVO" bean="SessionBean.checkListPrevNextCategoriesVO"/>
<c:set var="categoryName" scope="request" value="${prevNextCategoriesVO.currentCatName}" />
<c:set var="categoryUrl" scope="request" value="${prevNextCategoriesVO.currentCatUrl}" />
<c:set var="addedItemCount" scope="request" value="${prevNextCategoriesVO.addedQuantity}" />
<c:set var="totalCount" scope="request" value="${prevNextCategoriesVO.suggestedQuantity}" />
<c:set var="previousCategory" scope="request" value="${prevNextCategoriesVO.prevCatName}" />
<c:set var="previousCategoryUrl" scope="request" value="${prevNextCategoriesVO.prevCatUrl}" />
<c:set var="previousCatId" scope="request" value="${prevNextCategoriesVO.prevCatId}" />
<c:set var="nextCategory" scope="request" value="${prevNextCategoriesVO.nextCatName}" />
<c:set var="nextCategoryUrl" scope="request" value="${prevNextCategoriesVO.nextCatUrl}" />
<c:set var="nextCatId" scope="request" value="${prevNextCategoriesVO.nextCatId}" />
<json:object escapeXml="false">
	
</json:object>
</dsp:page>
