<dsp:page>
<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler" />
<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
<dsp:getvalueof bean="SessionBean.buyoffStartBrowsingSummaryVO.eventType" var="buyOffEventType"/>
<dsp:getvalueof bean="SessionBean.buyoffStartBrowsingSummaryVO" var="buyOffRegVo"/>
<c:set var="registryAssociateUrl" value="../giftregistry/view_registry_guest.jsp"/>
<c:set var="cartUrl" value="/store/cart/cart.jsp"/>
	<dsp:setvalue bean="CartModifierFormHandler.buyOffAssociationSkuId" value="${param.skuid}" />
	<dsp:setvalue bean="CartModifierFormHandler.associateRegistryContextWithCart" />
	<dsp:getvalueof var="jasonObj" bean="CartModifierFormHandler.errorFlagBuyOffAssociation" />
	<dsp:getvalueof var="updateFlag" bean="CartModifierFormHandler.buyOffDuplicateItemFlag" />
	<dsp:getvalueof var="imageURL" bean="CartModifierFormHandler.imageURL"/>
		<c:choose>
			<c:when test="${jasonObj eq 'true'}">
				<json:object escapeXml="false">
					<json:property name="success" value="${true}"/>
					<json:property name="primaryregfirstname" value="${buyOffRegVo.primaryRegistrantFirstName}"/>
					<json:property name="imagePath"value="${imageURL}"/>
					<json:property name="coregFirstName" value="${buyOffRegVo.coRegistrantFirstName}"/>
					<json:property name="eventType" value="${buyOffRegVo.registryType.registryTypeDesc}"/>
					<json:property name="regUrl"> <a href="${registryAssociateUrl}?eventType=${buyOffRegVo.registryType.registryTypeDesc}&amp;registryId=${buyOffRegVo.registryId}&amp;inventoryCallEnabled=true">
 						<strong>${buyOffRegVo.primaryRegistrantFirstName}<c:if test="${not empty buyOffRegVo.coRegistrantFirstName}">&nbsp;&amp;&nbsp;${buyOffRegVo.coRegistrantFirstName}</c:if><bbbl:label key="lbl_cart_registry_name_suffix" language="<c:out param='${language}'/>"/></strong>
 					 	</a></json:property>
					<json:property name="updateFlag" value="${updateFlag}"/>
					<json:property name="cartUrl" value="${cartUrl}"/>
				</json:object>
			</c:when>			
			<c:otherwise>
				<json:object escapeXml="false">
					<json:property name="success" value="${false}"/>
				</json:object>
			</c:otherwise>
		</c:choose>
	
</dsp:page>

