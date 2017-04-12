<dsp:page>
  <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
   <dsp:getvalueof var="isFromPDP" param="isFromPDP"/>
  <%-- The first call to price droplet is going to get the price from the profile's list price or 
       the default price list --%>
      
              <dsp:droplet name="PriceDroplet">
                <dsp:param name="product" param="product"/>
                <dsp:param name="sku" param="sku"/>
                <dsp:oparam name="output">
                  <dsp:setvalue param="theListPrice" paramvalue="price"/>
                  <%-- The second call is in case the sale price exists --%>

                      <dsp:droplet name="PriceDroplet">
                        <dsp:param name="priceList" bean="Profile.salePriceList"/>
                        <dsp:oparam name="output">

						<json:object name="price">
						<json:property name="wasPrice">
                            <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
                              <dsp:include page="/global/gadgets/formattedPrice.jsp">
                              <dsp:param name="isFromPDP" value="false" />
                                <dsp:param name="price" value="${price }"/>
                              </dsp:include>
						</json:property>
						<json:property name="isPrice">	
                            <dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
                            <dsp:include page="/global/gadgets/formattedPrice.jsp">
                            <dsp:param name="isFromPDP" value="false" />
                              <dsp:param name="price" value="${listPrice }"/>
                            </dsp:include>
                         </json:property>
						<json:property name="isPriceHTML">	
                            <dsp:getvalueof var="listPrice" vartype="java.lang.Double" param="price.listPrice"/>
                            <dsp:include page="/global/gadgets/formattedPrice.jsp">
                            <dsp:param name="isFromPDP" value="${isFromPDP}" />
                              <dsp:param name="price" value="${listPrice }"/>
                            </dsp:include>
                         </json:property>
                         </json:object>   
                        </dsp:oparam>
                        
                        <dsp:oparam name="empty">
						<json:property name="price">
                          <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
                          <dsp:include page="/global/gadgets/formattedPrice.jsp">
                          <dsp:param name="isFromPDP" value="false" />
                            <dsp:param name="price" value="${price }"/>
                          </dsp:include>
                         </json:property>
                         <json:property name="priceHTML">
                          <dsp:getvalueof var="price" vartype="java.lang.Double" param="theListPrice.listPrice"/>
                          <dsp:include page="/global/gadgets/formattedPrice.jsp">
                          <dsp:param name="isFromPDP" value="${isFromPDP}" />
                            <dsp:param name="price" value="${price }"/>
                          </dsp:include>
                         </json:property>
                        </dsp:oparam>
                      </dsp:droplet><%-- End price droplet on sale price --%>
                </dsp:oparam>
              </dsp:droplet><%-- End Price Droplet --%>
</dsp:page>