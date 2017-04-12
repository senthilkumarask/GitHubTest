<%@page contentType="application/json"%>
<dsp:page>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof var="c1id" param="c1id" />
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/RegistryItemsDisplayDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<json:object escapeXml="false">
		<dsp:droplet name="RegistryItemsDisplayDroplet">
			<dsp:param name="isSecondCall" value="true"/>
			<dsp:param name="categoryId" param="categoryId"/>
			<dsp:param name="isGiftGiver" value="false"/>
			<dsp:param name="siteId" value="${appid}"/>
			<dsp:param name="c1id" value="${c1id}"/>
			<dsp:oparam name="output">
			<dsp:getvalueof var="itemList" param="itemList" />
				<dsp:form id="formID1" method="post">
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="ephCategoryBuckets" />
						<dsp:param name="elementName" value="categoryVO"/>
						<dsp:oparam name="output">
								<dsp:getvalueof var="catId" param="categoryVO.categoryId" />
								<json:array name="${catId}_item" escapeXml="false">
									<json:property name="itemsCount">
										<dsp:valueof param="categoryVO.registryItemsCount"/>
									</json:property>
									<json:property name="categoryName">
										<dsp:valueof param="categoryVO.displayName"/>
									</json:property>
									<json:property name="itemsContent">
										<dsp:include page="registry_items_frag.jsp">
											<dsp:param name="categoryVO" param="categoryVO"/>
										</dsp:include>
									</json:property>
								</json:array>
						</dsp:oparam>
					</dsp:droplet>
				</dsp:form>
			</dsp:oparam>
		</dsp:droplet>
	<json:property name="certonaItems" escapeXml="false">
		<div id="disabledtopItems">
			<dsp:include page="/giftregistry/kickstarters/top_registry_items.jsp" flush="true">
				<dsp:param name="registryId"
					value="${fn:escapeXml(param.registryId)}" />
				<dsp:param name="sorting" value="${param.sortSeq}" />
				<dsp:param name="view" value="${param.view}" />
				<dsp:param name="skuList" value="${skuList}" />
				<dsp:param name="certonaSkuList" value="${certonaSkuList}" />
				<dsp:param name="isMyItemsCheckList" value="${fn:escapeXml(param.isMyItemsCheckList)}" />
				<dsp:param name="c1id" value="${fn:escapeXml(param.c1id)}" />
				<dsp:param name="itemList" value="${itemList}" />
			</dsp:include>

		</div>
	</json:property>
	</json:object>

</dsp:page>