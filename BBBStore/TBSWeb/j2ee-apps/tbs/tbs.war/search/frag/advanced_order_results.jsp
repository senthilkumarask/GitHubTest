<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/dynamo/droplet/Range"/>
    <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
	<dsp:importbean bean="/com/bbb/search/bean/TBSAdvancedOrderSearchBean"/>
	<dsp:importbean bean="/com/bbb/search/bean/TBSAdvancedOrderSearchResultsCacheBean"/>
	<dsp:importbean bean="/com/bbb/search/droplet/TBSProcessOrder"/>
    
    <dsp:getvalueof param="perPage" var="perPage"/>
    <dsp:getvalueof param="pageNumber" var="pageNumber"/>
    <dsp:getvalueof param="sortOption" var="sortOption"/>
    <dsp:getvalueof param="sortOrder" var="sortOrder"/>
    <dsp:getvalueof param="sortOrderDate" var="sortOrderDate"/>
	
	
    <c:if test="${empty pageNumber}">
    	<c:set var="pageNumber" value="1"/>
    </c:if>
    <c:if test="${empty perPage}">
		<c:set var="perPage" value="48" scope="request" />
	 </c:if>

	 <dsp:getvalueof bean="TBSAdvancedOrderSearchBean.searchTerm" var="searchKey"/>
	 <dsp:getvalueof bean="TBSAdvancedOrderSearchBean.totalOrders" var="totalOrers"/>
	 <dsp:getvalueof bean="TBSAdvancedOrderSearchBean.orderThresholdExceeded" var="isOrderThresholdExceeded"/>
	 <dsp:getvalueof bean="TBSAdvancedOrderSearchResultsCacheBean.outputOrders" var="orderMap"/>
	 <dsp:getvalueof var="orderItemAndVO" value="${orderMap[searchKey]}"/>
	 
	 <c:forEach items="${orderItemAndVO}" var="entry">
    		<dsp:getvalueof var="orders" value="${entry.value}"/>
	</c:forEach>
	<dsp:getvalueof var="ordersSize" value="${fn:length(orders)}"/>
	 
	  <c:if test="${totalOrers gt ordersSize}">
	 	<dsp:droplet name="TBSProcessOrder">
	 		<dsp:param name="perPage" value="${perPage}"/>
	 		<dsp:param name="itemAndVO" value="${orderMap}"/>
	 		<dsp:param name="searchKey" value="${searchKey}"/>
	 		<dsp:oparam name="empty">
	 			<dsp:getvalueof var="isOrderItemAndVOEmpty" value="true"/>
	 		</dsp:oparam>
	 	</dsp:droplet>
	  </c:if>
	 <dsp:getvalueof var="orderItemAndVO" value="${orderMap[searchKey]}"/>
	 
	 <c:if test="${!isOrderItemAndVOEmpty}">
	 	<c:forEach items="${orderItemAndVO}" var="entry">
    		<dsp:getvalueof var="orderListKey" value="${entry.key}"/>
    		<dsp:getvalueof var="orders" value="${entry.value}"/>
		</c:forEach>
	</c:if>
	 
	 
	 
	 <c:if test="${empty orders}">
	 	<dsp:setvalue bean="TBSAdvancedOrderSearchBean.searchTerm" value=""/>	
	 </c:if>
	 
	 <div class="row grid-nav" id="replaceWithAjaxData"> 
	    	<dsp:param name="pageNumber" value="${pageNumber}"/>
	    	<dsp:param name="perPage" value="${perPage}"/>
	
	<c:choose>
	    <c:when test="${totalOrers gt 0 && not empty orders}">
	    	<c:if test="${isOrderThresholdExceeded}">
				<c:set var="resultSetSizeValueFromConfigKey"><bbbc:config key="ResultSetSize" configName="AdvancedOrderInquiryKeys" /></c:set>
	    		<p class="error"> Your search criteria exceeds the maximum of ${resultSetSizeValueFromConfigKey} results. For additional results, please adjust your search criteria. </p>
	    	</c:if>
			<c:set var="totalTabFlot" value="${totalOrers/perPage}" /> 
			<c:set var="totalTab" value="${totalTabFlot+(1-(totalTabFlot%1))%1}" scope="request" />
			<div class="showing-text hide-on-print"> 
				<h3 class="show-for-large-up">Total <span class="subheader"> :&nbsp;<c:out value="${totalOrers}"/> </span> </h3> 
			</div> 	
			<div class="clearfix"></div>
			<dsp:droplet name="Range">
				<dsp:param name="array" value="${orders}"/>
				<dsp:param name="sortProperties" value="${sortOrderDate}submittedDate"/>
				<c:if test="${not empty sortOption && not empty sortOrder && not empty sortOrderDate}">
					<dsp:param name="sortProperties" value="${sortOrder}${sortOption},${sortOrderDate}submittedDate"/>
				</c:if>
				<dsp:param name="howMany" value="${perPage}"/>
				<dsp:param name="start" value="1"/>
				<dsp:param name="elementName" value="orderVO"/>
				<dsp:oparam name="outputStart">
								  <div class="searchResultsHeader searchResultsRow row">
						<div class="small-1 columns no-padding-left plusMinus hide-for-print">
						
						</div>
						<div class="small-12 medium-2 columns no-padding-left no-padding-right">
							<span id="orderNumArrow" class="sort-arrow left"></span>
							<input type="submit" id="ajaxOrderSort" name="ajaxOrderSort" class="btnSort" value="Order Number" onclick="orderNumberSort()">
						</div>
						<div class="small-12 medium-1 columns no-padding-left">
							<span id="dateArrow" class="sort-arrow left"></span>
							<input type="submit" id="ajaxSortOne" name="" class="btnSort" value="Date" onclick="dateSort()">
						</div>
						<div class="small-12 medium-2 columns no-padding-left">
							<span id="customerArrow" class="sort-arrow left"></span>
							<input type="submit" id="ajaxCustomerSort" name="ajaxCustomerSort" class="btnSort" value="Customer" onclick="customerSort()">
						</div>
						<div class="small-12 medium-2 columns no-padding-left">
							<span id="emailArrow" class="sort-arrow left"></span>
							<input type="submit" id="ajaxEmailSort" name="ajaxEmailSort" class="btnSort" value="Email" onclick="emailSort()">
						</div>
						<div class="small-12 medium-1 columns no-padding-left no-padding-right">
							<span id="amountArrow" class="sort-arrow left"></span>
							<input type="submit" id="ajaxAmountSort" name="ajaxAmountSort" class="btnSort" value="Amount" onclick="amountSort()">
						</div>
						<div class="small-12 medium-1 columns no-padding-left">
							<span id="statusArrow" class="sort-arrow left"></span>
							<input type="submit" id="ajaxStatusSort" name="ajaxStatusSort" class="btnSort" value="Status" onclick="statusSort()">
						</div>
						<div class="small-12 medium-1 columns no-padding-left">
							<span id="storeArrow" class="sort-arrow left"></span>
							<input type="submit" id="ajaxStoreSort" name="ajaxStoreSort" class="btnSort" value="Store" onclick="storeSort()">
						</div>
						<div class="small-12 medium-1 columns no-padding-left no-padding-right">
							<span id="associateArrow" class="sort-arrow left"></span>
							<input type="submit" id="ajaxAssociateSort" name="ajaxAssociateSort" class="btnSort" value="Associate" onclick="associateSort()">
						</div>
						<div class="clearfix"></div>
					   </div>

				</dsp:oparam>
				<dsp:oparam name="output">
					<div class="searchResultsRow row">
						<div class="small-1 columns no-padding-left plusMinus hide-for-print">
						</div>
						<div class="small-12 medium-2 columns no-padding-right">
							<dsp:a href="/tbs/account/order_detail.jsp">
								<dsp:valueof param="orderVO.onlineOrderNumber"/>
								<dsp:param name="orderId" param="orderVO.orderNumber"/>
							</dsp:a>
						</div>
						<div class="small-12 medium-1 columns no-padding-left">
							<dsp:getvalueof var="subDate" param="orderVO.submittedDate"/>
							<c:choose>
								<c:when test="${ empty subDate }">
									<p></p>
								</c:when>
								<c:otherwise>
									<dsp:valueof param="orderVO.submittedDate"/>
								</c:otherwise>
							</c:choose>
							</div>
						<div class="small-12 medium-2 columns no-padding-left">
							<dsp:valueof param="orderVO.firstName"/> <dsp:valueof param="orderVO.lastName"/>&nbsp;&nbsp;
						</div>
						<div class="small-12 medium-2 columns no-padding-left no-padding-right">
							<dsp:valueof param="orderVO.emailId"/>
						</div>
						<div class="small-12 medium-1 columns no-padding-left">
							<dsp:valueof param="orderVO.orderAmount" locale="en_US" converter="currency"/>
						</div>
						<div class="small-12 medium-1 columns no-padding-left">
							<dsp:valueof param="orderVO.orderStatus"/>
						</div>
						<div class="small-12 medium-1 columns no-padding-left">
							<dsp:valueof param="orderVO.store"/>
						</div>
						<div class="small-12 medium-1 columns no-padding-left no-padding-right left">
							<dsp:valueof param="orderVO.associate"/>
						</div>
					</div>
					</dsp:oparam>
					<dsp:oparam name="outputEnd">
					</dsp:oparam>
			</dsp:droplet>				
		</c:when>
		<c:when test="${totalOrers gt 0 && empty orders}">
			<%-- Please retry with same search terms --%>
		</c:when>
		<c:otherwise>
			<div class="orderRegError">				
				<div class="small-12 columns">
					<p class="error"><bbbl:label key='lbl_no_orders_found' language="${pageContext.request.locale.language}" /></p> 
				</div>
			</div>
		</c:otherwise>
	</c:choose>
</div>
	<script type="text/javascript">
		$(document).ready(function(){
			if($('#result').find('p.error') && $('#result').find('p.error').length > 0){
				$('#content').prev('.row').html($('#result').find('.orderRegError').html());
			}
			if(${totalOrers} > 0) {
				getFieldToUpdate();
				updateSortArrowIcon();
			}
		})
	</script>
	
</dsp:page>
		
