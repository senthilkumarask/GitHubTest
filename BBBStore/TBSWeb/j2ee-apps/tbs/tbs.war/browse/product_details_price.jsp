<dsp:page>
  <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
   <c:set var="omniPrice" value="" scope="request"/>
   <dsp:getvalueof var="registryId" param="registryId"/>
   <dsp:getvalueof var="paramSkuId" param="skuId"/>
   <dsp:getvalueof var="paramProductId" param="productId"/>
   <dsp:getvalueof var="currentProductId" param="product"/>
   <dsp:getvalueof var="currentSkuId" param="sku"/>
   <dsp:getvalueof var="skuInCartFlag" param="inCartFlag"/>
   
   <c:choose>
		<c:when test="${currentSiteId eq TBS_BedBathUSSite || currentSiteId eq TBS_BuyBuyBabySite}">
			<c:set var="incartPriceList"><bbbc:config key="BedBathUSInCartPriceList" configName="ContentCatalogKeys" /></c:set>
		</c:when>
		<c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
			<c:set var="incartPriceList"><bbbc:config key="BedBathCanadaInCartPriceList" configName="ContentCatalogKeys" /></c:set>
		</c:when>
   </c:choose>
   
  <%-- The first call to price droplet is going to get the price from the profile's list price or 
       the default price list --%>
      
              <dsp:droplet name="PriceDroplet">
                <dsp:param name="product" param="product"/>
                <dsp:param name="sku" param="sku"/>
                <dsp:oparam name="output">
                  <dsp:setvalue param="theListPrice" paramvalue="price"/>
                  <%-- The second call is in case the incart price exists --%>
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

			<%-- The third call is in case the sale price exists --%>
			<dsp:getvalueof var="profileSalePriceList"
				bean="Profile.salePriceList" />
			<c:choose>
				<c:when test="${not empty profileSalePriceList}">
					<dsp:droplet name="PriceDroplet">
						<dsp:param name="priceList" value="${profileSalePriceList}" />
						<dsp:oparam name="output">

							<c:choose>
								<c:when test="${skuInCartFlag}">
									<c:set var="listPrice" value="${inCartPrice}" />
								</c:when>
								<c:otherwise>
									<dsp:getvalueof var="listPrice" vartype="java.lang.Double"
										param="price.listPrice" />
								</c:otherwise>
							</c:choose>


							<h1 class="price price-sale">
								<%-- <dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/> --%>
								<dsp:include page="/global/gadgets/formattedPrice.jsp">
									<dsp:param name="price" value="${listPrice}" />
								</dsp:include>
							</h1>
							<br />
							<h3 class="price price-original">
								<dsp:getvalueof var="price" vartype="java.lang.Double"
									param="theListPrice.listPrice" />
								<dsp:include page="/global/gadgets/formattedPrice.jsp">
									<dsp:param name="price" value="${price}" />
								</dsp:include>
							</h3>
							<c:set var="omniPrice" scope="request">
								<dsp:include page="/global/gadgets/formattedPrice.jsp">
									<dsp:param name="price" value="${listPrice}" />
								</dsp:include>
							</c:set>
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

							<%-- <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/> --%>
							<dsp:include page="/global/gadgets/formattedPrice.jsp">
								<dsp:param name="price" value="${price}" />
							</dsp:include>
							<c:set var="omniPrice" scope="request">
								<dsp:include page="/global/gadgets/formattedPrice.jsp">
									<dsp:param name="price" value="${price}" />
								</dsp:include>
							</c:set>
						</dsp:oparam>
					</dsp:droplet>
					<%-- End price droplet on sale price --%>
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

					<%--  <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/> --%>
					<dsp:include page="/global/gadgets/formattedPrice.jsp">
						<dsp:param name="price" value="${price}" />
					</dsp:include>
					<c:set var="omniPrice" scope="request">
						<dsp:include page="/global/gadgets/formattedPrice.jsp">
							<dsp:param name="price" value="${price}" />
						</dsp:include>
					</c:set>
				</c:otherwise>
			</c:choose>
			<%-- End Is Empty Check --%>
                </dsp:oparam>
              </dsp:droplet><%-- End Price Droplet --%>
              <c:if test="${not empty registryId && (paramSkuId == currentSkuId || paramProductId == currentProductId)}">
						  <input type="hidden" value="${price}" id="registryItemPrice" name="registryItemPrice" />
				</c:if>
</dsp:page>