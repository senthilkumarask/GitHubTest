<dsp:page>
  <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  
  <%-- The first call to price droplet is going to get the price from the profile's list price or 
       the default price list --%>
      
  <dsp:getvalueof var="isSale" param="isSale"/> 
  <dsp:droplet name="PriceDroplet">
       <dsp:param name="product" param="product"/>
       <dsp:param name="sku" param="sku"/>
       <dsp:oparam name="output">
              <dsp:setvalue param="theListPrice" paramvalue="price"/>
              <%-- The second call is in case the sale price exists --%>

              <dsp:getvalueof var="profileSalePriceList" bean="Profile.salePriceList"/>
              
              
              <c:choose>
                <c:when test="${not empty profileSalePriceList}">
                  <dsp:droplet name="PriceDroplet">
                    <dsp:param name="priceList" bean="Profile.salePriceList"/>
                    <dsp:oparam name="output">
                        <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
                        
                        <dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
                        <c:set var="omniPrice" value="${listPrice}" scope="request"/>
						<li class="prodPrice">
							<span class="prodPriceOLD">
								<span><dsp:valueof converter="currency"	param="theListPrice.listPrice" /></span>
							</span>
							<dsp:getvalueof var="salePrice" param="price.listPrice"/>
							<c:choose>
								<c:when test="${fn:contains(salePrice,'-')}"><br /></c:when>
								<c:otherwise>&nbsp;</c:otherwise>
							</c:choose>
							<span class="prodPriceNEW">
								<span><dsp:valueof converter="currency"	param="price.listPrice" /></span>
							</span>
						</li>
                    </dsp:oparam>
                    <dsp:oparam name="empty">
	                    <li class="prodPrice">
		                      <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
		                      <c:set var="omniPrice" value="${price}" scope="request"/>
		                      <dsp:include page="/global/gadgets/formattedPrice.jsp">
		                    	    <dsp:param name="price" value="${price }"/>
		                      </dsp:include>  
	                    </li>  
                    </dsp:oparam>
                  </dsp:droplet><%-- End price droplet on sale price --%>
                </c:when>
                <c:otherwise>
                  <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
                  <c:set var="omniPrice" value="${price}" scope="request" />
                  <li class="prodPrice">
                  <dsp:include page="/global/gadgets/formattedPrice.jsp">
                    <dsp:param name="price" value="${price }"/>
                  </dsp:include>
                  </li>
                </c:otherwise>
              </c:choose><%-- End Is Empty Check --%>
            </dsp:oparam>
          </dsp:droplet><%-- End Price Droplet --%>
</dsp:page>              