<dsp:page>
	<dsp:importbean bean="/atg/commerce/order/OrderLookup" />
	<c:set var="BedBathCanadaSite">
		<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="saSrc"><bbbc:config key="socialAnnexURL" configName="ThirdPartyURLs" /></c:set>
	<bbb:pageContainer index="false" follow="false">
		<jsp:attribute name="bodyClass">atg_store_pageHome</jsp:attribute>
		<jsp:attribute name="section">accounts</jsp:attribute>
		<jsp:attribute name="pageWrapper">wishlist orderDetailWrapper myAccount useBazaarVoice usePorch</jsp:attribute>
		<jsp:body>			
		<div id="content" class="container_12 clearfix" role="main">
		<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
		<dsp:droplet name="OrderLookup">
			<c:set var="orderId"><c:out value="${param.orderId}"/></c:set>
			<dsp:param name="orderId" value="${orderId}" />
			<dsp:oparam name="output">
			<dsp:getvalueof var="order" param="result"/>			
			<div class="grid_12">			
				<h1 class="account fl"><bbbl:label key="lbl_personalinfo_myaccount" language ="${pageContext.request.locale.language}"/></h1>
				<h3 class="subtitle fl"><bbbl:label key="lbl_myaccount_orders" language ="${pageContext.request.locale.language}"/></h3>
				<div class="fl orderIdInfo">
					<dl class="fl noMar">
						<c:set var="onlineOrderNumber" value="${order.onlineOrderNumber}"/>
						<c:set var="bopusOrderNumber" value="${order.bopusOrderNumber}"/>	
						<c:if test="${empty onlineOrderNumber && not empty bopusOrderNumber || empty bopusOrderNumber && not empty onlineOrderNumber}">
							<dt class="fl marLeft_20 marRight_5 capitalize">
								<strong><bbbl:label key="lbl_order_id" language="${pageContext.request.locale.language}"/></strong>
							</dt>
							<c:choose>
								<c:when test="${empty onlineOrderNumber}">
									<dd class="fl marRight_5">${bopusOrderNumber}</dd>
								</c:when>
								<c:otherwise>
									<dd class="fl marRight_5">${onlineOrderNumber}</dd>
								</c:otherwise>
							</c:choose>
						</c:if>	

						<dsp:getvalueof id="orderDetails" param="orderDetails"/>
						<dt class="fl marLeft_20 marRight_5 capitalize">
									<strong><bbbl:label key="lbl_order_date" language="${pageContext.request.locale.language}"/></strong>
								</dt>
						<dd class="fl marRight_5">
							<c:choose>
								<c:when test="${currentSiteId == BedBathCanadaSite}"  >
									<dsp:valueof value="${order.submittedDate}"	converter="date" date="dd/MM/yyyy"/>
								</c:when>
								<c:otherwise>
									<dsp:valueof value="${order.submittedDate}"	converter="date" date="MM/dd/yyyy"/>
								</c:otherwise>
							</c:choose>
						</dd>
					</dl>
					<a href="${contextPath}/account/order_summary.jsp" class="fl marLeft_10 upperCase"><strong><bbbl:label key="lbl_view_all_order" language="${pageContext.request.locale.language}"/></strong></a>
				</div>
			</div>

			<div class="grid_2">
				<c:import url="/account/left_nav.jsp" />
			</div>


			<div class="grid_10">
				<div class="clearfix suffix_1 prefix_1">
					<div>
						<dsp:include page="/checkout/preview/frag/checkout_review_frag.jsp" flush="true" >
							<dsp:param name="order" value="${order}"/>
							<dsp:param name="displayTax" value="true"/>
							<dsp:param name="isFromOrderDetail" value="true"/>
							<dsp:param name="orderDate" value="${order.submittedDate}"/>
						</dsp:include>
					</div>	
				</div> 
			</div>
			</dsp:oparam>
			<dsp:oparam name="error">
				<script type="text/javascript">
					window.location.href = "/store/account/order_summary.jsp";
				</script>
			</dsp:oparam>
			<dsp:oparam name="empty">
				<script type="text/javascript">
					window.location.href = "/store/account/order_summary.jsp";
				</script>
			</dsp:oparam>
			</dsp:droplet>
		</div>	
		<div id="sa_s22_instagram" data-saSrc="${saSrc}"></div>		
	</jsp:body>
	<jsp:attribute name="footerContent">

			<script type="text/javascript">
			           if(typeof s !=='undefined') {
			        	s.pageName='My Account>Order view';
			        	s.channel='My Account';
			        	s.prop1='My Account';
			        	s.prop2='My Account';
			        	s.prop3='My Account';
			                
			            var s_code=s.t();
			            if(s_code)document.write(s_code);           
			           }
			           
			           var compare_list_array = [];
			           var compare_list = Array.prototype.map.call(document.querySelectorAll('div[id^=currentProduct]'),
			        	function (div) { 
			           var compare_var = div.getAttribute("data-trackproduct"); 
			           compare_var = compare_var.split(':'); 
			           compare_list_array[compare_var[0]] = compare_var[1];
			          
			           }); 
			         
			           var sa_multiple_products = compare_list_array; 
			           console.log(sa_multiple_products);
			           
			    	var sa_page="9";
			    	

			    	(function() {
			    	var sa = document.createElement('script');
			    	sa.type = 'text/javascript';
			    	sa.async = true;
			    	sa.src = '${saSrc}';
			    	var sax = document.getElementsByTagName('script')[0];
			    	sax.parentNode.insertBefore(sa, sax);
			    	 })();       
			    
			</script>
		</jsp:attribute>
	</bbb:pageContainer>
</dsp:page>