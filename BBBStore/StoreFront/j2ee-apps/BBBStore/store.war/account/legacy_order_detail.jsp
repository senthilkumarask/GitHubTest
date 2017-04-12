<dsp:page>
	<dsp:importbean bean="com/bbb/account/LegacyOrderDetailsDroplet" />
	<bbb:pageContainer index="false" follow="false">
		<jsp:attribute name="bodyClass">atg_store_pageHome</jsp:attribute>
		<jsp:attribute name="section">accounts</jsp:attribute>
		<jsp:attribute name="pageWrapper">wishlist orderDetailWrapper myAccount</jsp:attribute>
		<jsp:body>
		<div id="content" class="container_12 clearfix" role="main">
		<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
		<dsp:droplet name="LegacyOrderDetailsDroplet">
			<c:set var="orderId"><c:out value="${param.orderId}"></c:out> </c:set>
			<dsp:param name="orderId" value="${orderId}" />
			<dsp:param name="email" bean="/atg/userprofiling/Profile.email" />
			<dsp:oparam name="output">
			<dsp:getvalueof var="order" param="orderDetails"/>				
			<div class="grid_12">			
				<h1 class="account fl"><bbbl:label key="lbl_personalinfo_myaccount" language ="${pageContext.request.locale.language}"/></h1>
				<h3 class="subtitle fl"><bbbl:label key="lbl_myaccount_orders" language ="${pageContext.request.locale.language}"/></h3>
				<div class="fl orderIdInfo">
					<dl class="fl noMar">
						<dt class="fl marLeft_20 marRight_5 capitalize">
									<strong><bbbl:label key="lbl_order_id" language="${pageContext.request.locale.language}"/></strong>
								</dt>
						<dd class="fl marRight_5">${param.orderId}</dd>
						<dsp:getvalueof id="orderDetails" param="orderDetails"/>
						<dt class="fl marLeft_20 marRight_5 capitalize">
									<strong><bbbl:label key="lbl_order_date" language="${pageContext.request.locale.language}"/></strong>
								</dt>
						<dd class="fl marRight_5"><c:out value="${order.orderInfo.orderHeaderInfo.orderDt}"/></dd>
					</dl>
					<a href="${contextPath}/account/order_summary.jsp" class="fl marLeft_10 upperCase"><strong><bbbl:label key="lbl_view_all_order" language="${pageContext.request.locale.language}"/></strong></a>
				</div>
			</div>

			<div class="grid_2">
				<c:import url="/account/left_nav.jsp" />
			</div>

			<div class="grid_10">
				<div class="clearfix suffix_1 prefix_1">
					<div class="marTop_25">
							<dsp:include page="/checkout/preview/frag/legacy_order_details_frag.jsp" >
							<dsp:param name="orderDetails" value="${orderDetails}"/>
							<dsp:param name="isFromOrderHistory" value="true"/>
							<dsp:param name="orderId" value="${orderId}"/>
							</dsp:include>
					</div>
				</div>
			</div>
			</dsp:oparam>
			</dsp:droplet>
		</div>	
	</jsp:body>
	</bbb:pageContainer>
</dsp:page>