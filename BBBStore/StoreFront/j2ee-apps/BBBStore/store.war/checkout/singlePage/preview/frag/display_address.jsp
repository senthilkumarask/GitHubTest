<dsp:page>
	<dsp:getvalueof var="shippingGroup" param="shippingGroup"/>
	<dsp:getvalueof var="beddingKit" param="beddingKit" />
	<dsp:getvalueof var="collegeName" param="collegeName"/>
	<dsp:getvalueof var="weblinkOrder" param="weblinkOrder"/>
    
	<ul class="SPCshippingMethod borderRightPreview">
		<span class = "grid_3 SPCpreviewText"><h3 class="subHeading"><bbbl:label key="lbl_spc_preview_shippingaddress" language="<c:out param='${language}'/>"/></h3>
			</span>		

		<c:choose>
			<c:when test="${not empty shippingGroup.registryId}">
                <dsp:getvalueof param="order.registryMap.${shippingGroup.registryId}" var="registratantVO"/>
				<span class="SPCpreviewShippingText">
					<strong>${registratantVO.primaryRegistrantFirstName} ${registratantVO.primaryRegistrantLastName}
                    <c:if test="${registratantVO.coRegistrantFirstName ne null}">
                        &amp; ${registratantVO.coRegistrantFirstName} ${registratantVO.coRegistrantLastName}
                    </c:if>
                    </strong>
				</span>
				<br><span><dsp:valueof param="shippingGroup.shippingAddress.email" /></span>, <span><dsp:getvalueof var="phoneNumber" bean="/atg/commerce/ShoppingCart.current.shippingAddress.phoneNumber"/>${fn:substring(phoneNumber, 0, 3)}-${fn:substring(phoneNumber, 3, 6)}-${fn:substring(phoneNumber, 6, 10)}</span>
			</c:when>
			<c:otherwise>
				
				<span class="SPCpreviewShippingText"><dsp:valueof param="shippingGroup.shippingAddress.firstName" valueishtml="true"/> <dsp:valueof param="shippingGroup.shippingAddress.lastName" valueishtml="true"/>,</span>
				<c:choose>
					<c:when test="${not empty beddingKit && beddingKit eq true}">
						<span><dsp:valueof param="collegeName" valueishtml="true"/></span>
					</c:when>
					<c:when test="${not empty weblinkOrder && weblinkOrder eq true}">
						<span><dsp:valueof param="collegeName" valueishtml="true"/></span>
					</c:when>
					<c:otherwise>
						<c:if test="${shippingGroup.shippingAddress.companyName != ''}">
							<span><dsp:valueof param="shippingGroup.shippingAddress.companyName" valueishtml="true"/></span>
						</c:if>									
					</c:otherwise>
				</c:choose>
				
				
				<span>
					<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
						<dsp:param name="value" param="shippingGroup.shippingAddress.address2"/>
						<dsp:oparam name="true">
							<dsp:valueof param="shippingGroup.shippingAddress.address1" valueishtml="true"/>,
						</dsp:oparam>
						<dsp:oparam name="false">
							<dsp:valueof param="shippingGroup.shippingAddress.address1" valueishtml="true"/>, <dsp:valueof param="shippingGroup.shippingAddress.address2" valueishtml="true"/>,
						</dsp:oparam>
					</dsp:droplet>
				</span>
				<span><dsp:valueof param="shippingGroup.shippingAddress.city" valueishtml="true"/>, <dsp:valueof param="shippingGroup.shippingAddress.state" /> <dsp:valueof param="shippingGroup.shippingAddress.postalCode" valueishtml="true"/>.</span><br>
			<c:choose>
				<c:when test="${shippingGroup.shippingAddress.email ne null}">
				<span><dsp:valueof param="shippingGroup.shippingAddress.email" /></span>,
				</c:when>
				<c:otherwise>
				<span><dsp:valueof bean="/atg/userprofiling/Profile.email"/></span>,
				</c:otherwise>
				</c:choose>
				<dsp:getvalueof var="phoneNumber" bean="/atg/commerce/ShoppingCart.current.shippingAddress.phoneNumber"/>
				<c:choose>
				<c:when test="${phoneNumber ne null}"><span>${fn:substring(phoneNumber, 0, 3)}-${fn:substring(phoneNumber, 3, 6)}-${fn:substring(phoneNumber, 6, 10)}</span>
				</c:when>
				<c:otherwise><span><dsp:valueof bean="/atg/userprofiling/Profile.mobileNumber"/></span></c:otherwise>
				</c:choose>
						
			</c:otherwise>				
		</c:choose>
				 		<c:if test="${not empty showLinks}">
			<span class="SPCeditPreviewLink">
				<a class="capitalize" href="${pageContext.request.contextPath}/checkout/checkout_single.jsp?isFromPreview=true#spcShipping" title="<bbbl:label key="lbl_spc_preview_edit" language="<c:out param='${language}'/>"/>"><strong><bbbl:label key="lbl_preview_edit" language="<c:out param='${language}'/>"/></strong></a>
			</span>
		</c:if> 
	</ul>
</dsp:page>