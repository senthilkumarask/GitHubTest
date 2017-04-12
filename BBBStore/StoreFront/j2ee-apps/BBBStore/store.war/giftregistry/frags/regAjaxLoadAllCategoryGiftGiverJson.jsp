<%@page contentType="application/json"%>
<dsp:page>
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/GuestRegistryItemsDisplayDroplet" />
        <dsp:droplet name="/com/bbb/common/droplet/EximCustomizationDroplet">
				<dsp:oparam name="output">
					 <dsp:getvalueof var="eximCustomizationCodesMap" param="eximCustomizationCodesMap" />
				</dsp:oparam>
		</dsp:droplet>
		<dsp:droplet name="GuestRegistryItemsDisplayDroplet">
			<dsp:param name="isSecondAjaxCall" value="true"/>
			<dsp:param name="registryId" param="registryId"/>
			<dsp:param name="eventTypeCode" param="eventTypeCode"/>
			<dsp:param name="inventoryCallEnabled" param="inventoryCallEnabled"/>
			<dsp:oparam name="output">
					<dsp:getvalueof var="categoryBuckets" param="categoryBuckets" />
					<dsp:getvalueof var="notInStockCategoryList" param="notInStockCategoryList" />
					<dsp:getvalueof var="showStartBrowsing" param="showStartBrowsing" />
					<dsp:getvalueof var="regItemCount" param="regItemCount" />
						<json:object escapeXml="false">
						 <json:object name="items" escapeXml="false">
						      <c:forEach items="${categoryBuckets}" var="categoryBucket">
						         <json:array name="In_${categoryBucket.value.categoryId}" escapeXml="false">
									<json:property name="itemsCount" value="${fn:length(categoryBucket.value.registryItemList)}"/>
									<json:property name="itemsContent">
										<dsp:include page="registry_items_guest_category_details_instock.jsp">
											<dsp:param name="registryItemList" value="${categoryBucket.value.registryItemList}"/>
											<dsp:param name="registryId" param="registryId"/>
												<dsp:param name="startIdx" value="0"/>
												<dsp:param name="isGiftGiver" value="true"/>
												<dsp:param name="blkSize" value="${maxBulkSize}"/>	
												<dsp:param name="isAvailForWebPurchaseFlag" value="false"/>
												<dsp:param name="eventType" param="eventType"/>
												<dsp:param name="eventTypeCode" value="${eventTypeCode}"/>
												<dsp:param name="eximCustomizationCodesMap" value="${eximCustomizationCodesMap}"/>
										</dsp:include>
									</json:property>
						        </json:array>
						     </c:forEach>
						 </json:object>       
						  <json:array name="notInStockCategoryList" escapeXml="false">  
					    	  <c:forEach items="${notInStockCategoryList}" var="category">
						      <json:property name="itemsContent" value="${category}"/>
						    </c:forEach>
						  </json:array>
						</json:object>
			</dsp:oparam>
		</dsp:droplet>
	

</dsp:page>