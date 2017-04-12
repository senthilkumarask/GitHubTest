<dsp:page>
	<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
	<dsp:getvalueof var="beddingKit" param="beddingKit" />
	<dsp:getvalueof var="collegeName" param="collegeName"/>
	<dsp:getvalueof var="weblinkOrder" param="weblinkOrder"/>
    
	<div class="shippingMethod borderRightPreview">
		<p class = "previewText">
			<bbbl:label key="lbl_preview_shippingaddress" language="<c:out param='${language}'/>"/>
		</p>
		<c:if test="${not empty showLinks}">
			<p class="marTop_10 editPreviewLink">
				<a class="upperCase" href="${pageContext.request.contextPath}/checkout/shipping/shipping.jsp?isFromPreview=true" title="<bbbl:label key="lbl_preview_edit" language="<c:out param='${language}'/>"/>"><span class="txtOffScreen">Edit Shipping address</span><strong><bbbl:label key="lbl_preview_edit" language="<c:out param='${language}'/>"/></strong></a>
			</p>
		</c:if>
		<c:choose>
			<c:when test="${not empty shippingGroup.registryId}">
                <dsp:getvalueof param="order.registryMap.${shippingGroup.registryId}" var="registratantVO"/>
				<p class="marTop_5 previewShippingText">
					<strong>${registratantVO.primaryRegistrantFirstName} ${registratantVO.primaryRegistrantLastName}
                    <c:if test="${registratantVO.coRegistrantFirstName ne null}">
                        &amp; ${registratantVO.coRegistrantFirstName} ${registratantVO.coRegistrantLastName}
                    </c:if>
                    </strong>
				</p>
			</c:when>
			<c:otherwise>
				
				<p class="marTop_5 previewShippingText"><dsp:valueof param="shippingGroup.shippingAddress.firstName" valueishtml="true"/> <dsp:valueof param="shippingGroup.shippingAddress.lastName" valueishtml="true"/></p>
				<c:choose>
					<c:when test="${not empty beddingKit && beddingKit eq true}">
						<span><dsp:valueof param="collegeName" valueishtml="true"/></span>
					</c:when>
					<c:when test="${not empty weblinkOrder && weblinkOrder eq true}">
						<span><dsp:valueof param="collegeName" valueishtml="true"/></span>
					</c:when>
					<c:otherwise>
						<c:if test="${shippingGroup.shippingAddress.companyName != ''}">
							<span class="previewShippingText"><dsp:valueof param="shippingGroup.shippingAddress.companyName" valueishtml="true"/></span>
						</c:if>									
					</c:otherwise>
				</c:choose>
				
				<p class="previewShippingText">
					<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
						<dsp:param name="value" param="shippingGroup.shippingAddress.address2"/>
						<dsp:oparam name="true">
							<dsp:valueof param="shippingGroup.shippingAddress.address1" valueishtml="true"/>
						</dsp:oparam>
						<dsp:oparam name="false">
							<dsp:valueof param="shippingGroup.shippingAddress.address1" valueishtml="true"/>, <dsp:valueof param="shippingGroup.shippingAddress.address2" valueishtml="true"/>
						</dsp:oparam>
					</dsp:droplet>
				</p>
				<p class="previewShippingText"><dsp:valueof param="shippingGroup.shippingAddress.city" valueishtml="true"/>, <dsp:valueof param="shippingGroup.shippingAddress.state" /> <dsp:valueof param="shippingGroup.shippingAddress.postalCode" valueishtml="true"/></p>
						
                     
				       
		<dsp:droplet name="/atg/dynamo/droplet/ForEach">
			<dsp:param name="array" param="shippingGroup.commerceItemRelationships" />
			<dsp:param name="elementName" value="commerceItemRelationship" />
			<dsp:oparam name="output">
			<dsp:getvalueof var="ltlItem" param="commerceItemRelationship.commerceItem.ltlItem"/>
			
		
	      <c:if test="${ltlItem}">
	      <p class="previewShippingText ltlContactInfo">
	     <span iclass="phone" ><dsp:valueof param="shippingGroup.shippingAddress.phoneNumber" valueishtml="true"/></span>
	     <span  iclass="alternatePhone"><dsp:valueof param="shippingGroup.shippingAddress.alternatePhoneNumber" valueishtml="true"/></span>
		 <span  class="grid_6 alpha" iclass="email"><dsp:valueof param="shippingGroup.shippingAddress.email" valueishtml="true"/></span>
				
				 
			</p>	          
		</c:if>
		
			</dsp:oparam>
		</dsp:droplet>
				
			</c:otherwise>				
		</c:choose>
				
	</div>
</dsp:page>