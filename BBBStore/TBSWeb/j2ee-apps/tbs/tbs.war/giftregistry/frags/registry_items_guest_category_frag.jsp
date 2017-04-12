<dsp:page>
<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="skuID" param="skuID" />
	<dsp:getvalueof var="skuInCartFlag" param="skuInCartFlag"/>
	<dsp:getvalueof var="incartPriceList" param="incartPriceList"/>
<dsp:droplet name="PriceDroplet">
		<dsp:param name="product" value="${productId}" />
		<dsp:param name="sku" value="${skuID}"/>
		<dsp:oparam name="output">
			<dsp:setvalue param="theListPrice" paramvalue="price"/>
			<c:if test="${skuInCartFlag}">
				<dsp:droplet name="PriceDroplet">
					<dsp:param name="product" param="product" />
					<dsp:param name="sku" param="sku" />
					<dsp:param name="priceList" value="${incartPriceList}" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="inCartPrice" vartype="java.lang.Double"
							param="price.listPrice" />
					</dsp:oparam>
				</dsp:droplet>
		   </c:if>
			
			<dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
			<c:choose>
				<c:when test="${not empty profileSalePriceList}">
						<dsp:droplet name="PriceDroplet">
								<dsp:param name="priceList" bean="Profile.salePriceList"/>
								<dsp:oparam name="output">
										<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
										<c:choose>		
											<c:when test="${skuInCartFlag}">
												<c:set var="listPrice" value="${inCartPrice}" />
											</c:when>
											<c:otherwise>
												<dsp:getvalueof var="listPrice" vartype="java.lang.Double"
												param="price.listPrice" />
											</c:otherwise>
										</c:choose>
										
											<c:if test="${listPrice gt 0.10}">
												 <dsp:include page="/global/gadgets/formattedPrice.jsp">
													<dsp:param name="price" value="${listPrice }"/>
													<c:set var="displayPrice" value="${listPrice}"/>
												 </dsp:include>
											</c:if>
								</dsp:oparam>
								 <dsp:oparam name="empty">
										<c:choose>		
											<c:when test="${skuInCartFlag}">
												<c:set var="price" value="${inCartPrice}" />
											</c:when>
											<c:otherwise>
												<dsp:getvalueof var="price" vartype="java.lang.Double"
												param="theListPrice.listPrice" />
											</c:otherwise>
										</c:choose>

											<c:if test="${price gt 0.10}">
												<dsp:include page="/global/gadgets/formattedPrice.jsp">
													<dsp:param name="price" value="${price }"/>
													<c:set var="displayPrice" value="${price}"/>
												</dsp:include>
											</c:if>

								</dsp:oparam>
								</dsp:droplet><%-- End price droplet on sale price --%>
					</c:when>
					 <c:otherwise>
							<c:choose>		
								<c:when test="${skuInCartFlag}">
									<c:set var="price" value="${inCartPrice}" />
								</c:when>
								<c:otherwise>
									<dsp:getvalueof var="price" vartype="java.lang.Double"
									param="theListPrice.listPrice" />
								</c:otherwise>
							</c:choose>

								<c:if test="${price gt 0.10}">
									<dsp:include page="/global/gadgets/formattedPrice.jsp">
										<dsp:param name="price" value="${price }"/>
										<c:set var="displayPrice" value="${price}"/>
									</dsp:include>
								</c:if>

					</c:otherwise>
				</c:choose>
				<c:if test="${not empty listPrice}">
					<c:set value="${listPrice}" var="price"/>
				</c:if>
				<input type="hidden" value="${displayPrice}" class="itemPrice"/>
	</dsp:oparam>
</dsp:droplet>
</dsp:page>