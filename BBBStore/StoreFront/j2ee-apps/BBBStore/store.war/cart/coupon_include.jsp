 <dsp:page>
 <dsp:importbean bean="/atg/commerce/ShoppingCart" />
 <dsp:importbean bean="/atg/userprofiling/Profile" var="Profile" />
 <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
 <dsp:getvalueof id="orderHasErrorPrsnlizedItem" param="orderHasErrorPrsnlizedItem" />
<c:set var="cart_one_column_layout_enable" scope="request"><bbbc:config key="cart_one_column_layout_enable" configName="FlagDrivenFunctions" /></c:set>
	<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="page"/>
	<div id="leftCol" class="grid_6 alpha clearfix">
	   <c:if test="${cart_one_column_layout_enable}">
        <div id="certonaSlots">
			<dsp:include page="/cart/subCart.jsp">
              <dsp:param name="CertonaContext" value="${CertonaContext}"/>
              <dsp:param name="RegistryContext" value="${RegistryContext}"/>
              <dsp:param name="shippingThreshold" value="${shippingThreshold}"/>
            </dsp:include>
		</div>
	  </c:if>
	
	  <dsp:droplet name="Switch">
		<dsp:param name="value" bean="Profile.transient"/>
		<dsp:oparam name="false">
			<dsp:getvalueof id="bopusOnly" bean="ShoppingCart.current.OnlineBopusItemsStatusInOrder" />
			<c:choose>
				<c:when test="${CouponOn}" >
			 	   <dsp:include page="/cart/couponDisplay.jsp">
					 <dsp:param name="action" value="${contextPath}/cart/cart.jsp"/>
			    	 <dsp:param name="cartCheck" value="true"/>
			    	 <dsp:param name="orderHasErrorPrsnlizedItem" value="${orderHasErrorPrsnlizedItem}"/>
				   </dsp:include>
				 </c:when>
				 <c:otherwise>
					 &nbsp;
	        	 </c:otherwise>
			</c:choose>
		 </dsp:oparam>
		 <dsp:oparam name="true">
			 &nbsp;
		 </dsp:oparam>
	  </dsp:droplet>
		
	</div>
</dsp:page>                