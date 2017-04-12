<dsp:page>
<dsp:getvalueof var="inCartFlagSKU" param="inCartFlagSKU"/>
<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:getvalueof var="productId" param="productId" />
	<dsp:getvalueof var="skuID" param="skuID" />
	<dsp:getvalueof var="inCartFlagSKU" param="inCartFlagSKU" />
	
<c:if test="${inCartFlagSKU}">	
 		<div class="disPriceString"> <bbbl:label key="lbl_discounted_incart_text" language="${pageContext.request.locale.language}" /> </div>	
</c:if>
<dsp:droplet name="PriceDroplet">
		<dsp:param name="product" value="${productId}" />
		<dsp:param name="sku" value="${skuID}"/>
		<dsp:oparam name="output">
			<dsp:setvalue param="theListPrice" paramvalue="price"/>
			<dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
			<c:choose>
				<c:when test="${not empty profileSalePriceList}">
						<dsp:droplet name="PriceDroplet">
								<dsp:param name="priceList" bean="Profile.salePriceList"/>
								<dsp:oparam name="output">
										<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
										<dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>

											<c:if test="${listPrice gt 0.10}">
												 <dsp:include page="/global/gadgets/formattedPrice.jsp">
													<dsp:param name="price" value="${listPrice }"/>
													<c:set var="displayPrice" value="${listPrice}"/>
												 </dsp:include>
											</c:if>
								</dsp:oparam>
								 <dsp:oparam name="empty">
										<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>

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
							<dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>

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