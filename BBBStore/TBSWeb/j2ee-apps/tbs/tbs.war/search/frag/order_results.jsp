<dsp:page>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/com/bbb/search/handler/TBSOrderSearchFormHandler" />
	<dsp:importbean bean="/atg/commerce/order/OrderLookup" />
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/dynamo/droplet/Range"/>
    <dsp:importbean bean="/com/bbb/commerce/order/droplet/TBSCommerceItemLookupDroplet"/>
    <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
    <dsp:importbean bean="/com/bbb/search/droplet/TBSOrderPaginationDroplet"/>
    <dsp:importbean bean="/com/bbb/search/bean/TBSOrderSearchSessionBean"/>
    
    <dsp:getvalueof param="perPage" var="perPage"/>
    <dsp:getvalueof param="pageNumber" var="pageNumber"/>
  	
    <c:if test="${empty pageNumber}">
    	<c:set var="pageNumber" value="1"/>
    </c:if>
    <c:if test="${empty perPage}">
		<c:set var="perPage" value="48" scope="request" />
	 </c:if>
	 <dsp:getvalueof var="tabNo" param="tabNo" />
		<c:if test="${ empty tabNo}">
			<c:set var="tabNo" value="1" />
		</c:if>
	 <div class="row grid-nav"> 
	    <dsp:droplet name="TBSOrderPaginationDroplet">
	    	<dsp:param name="pageNumber" value="${pageNumber}"/>
	    	<dsp:param name="email" bean="TBSOrderSearchSessionBean.email"/>
	    	<dsp:param name="orderId" bean="TBSOrderSearchSessionBean.orderId"/>
	    	<dsp:param name="firstName" bean="TBSOrderSearchSessionBean.firstName"/>
	    	<dsp:param name="lastName" bean="TBSOrderSearchSessionBean.lastName"/>
	    	<dsp:param name="registryNum" bean="TBSOrderSearchSessionBean.registryNum"/>
	    	<dsp:param name="storeNum" bean="TBSOrderSearchSessionBean.storeNum"/>
	    	<dsp:param name="startDate" bean="TBSOrderSearchSessionBean.startDate"/>
	    	<dsp:param name="endDate" bean="TBSOrderSearchSessionBean.endDate"/>
	    	<dsp:param name="perPage" value="${perPage}"/>
	    	<dsp:oparam name="outputStart">
	    		<dsp:getvalueof param="startIndex" var="startIndex"/>
	    		<dsp:getvalueof param="endIndex" var="endIndex"/>
	    		<dsp:getvalueof param="orders" var="orders"/>
	    		<dsp:getvalueof param="totalOrers" var="totalOrers"/>
	    		
				<c:set var="totalTabFlot" value="${totalOrers/perPage}" /> 
				<c:set var="totalTab" value="${totalTabFlot+(1-(totalTabFlot%1))%1}" scope="request" />
				<div class="showing-text hide-on-print"> 
					<h3 class="show-for-large-up">Showing <c:out value="${startIndex}"/>  - <c:out value="${endIndex}"/> <span class="subheader">of&nbsp;<c:out value="${totalOrers}"/> </span> </h3> 
				</div> 
				<div class="refinements hide-on-print"> 
					<div class="grid-pagination"> 
						<ul class="inline-list right <c:if test='${fn:substringBefore(totalTab, ".") < 2}'>hidden</c:if>">
							<c:choose>
								<c:when test="${pageNumber eq 1 }">
									<li> <dsp:a href="../track_order.jsp" iclass="button left disabled secondary"><dsp:param name="perPage" value="${perPage}"/><dsp:param name="pageNumber" value="${pageNumber}"/><span></span></dsp:a> </li>
								</c:when>
								<c:otherwise>
									<li> <dsp:a href="../track_order.jsp" iclass="button left secondary"><dsp:param name="perPage" value="${perPage}"/><dsp:param name="pageNumber" value="${pageNumber-1}"/><span></span></dsp:a> </li>
								</c:otherwise>
							</c:choose>
							<li class="current-page-drop"> <a class="small download button dropdown" data-dropdown="currentPage" href="#">${pageNumber}<span>&nbsp;</span></a> 
								<ul class="f-dropdown current-page" data-dropdown-content="" id="currentPage" name="currentPage"> 
								
			</dsp:oparam>
	    	<dsp:oparam name="output">	    	
	    	<dsp:getvalueof param="currentPage" var="currentPage"/>
	    		<li><dsp:a href="../track_order.jsp"><c:out value="${currentPage}"/>
	    			<dsp:param name="perPage" value="${perPage}"/>
	    			<dsp:param name="pageNumber" value="${currentPage}"/>
	    		</dsp:a></li>
	    	</dsp:oparam>
	    	<dsp:oparam name="outputEnd">
	    		<dsp:param name="pageEndNumber" value="${currentPage}"/>
	    	</dsp:oparam>
			<dsp:oparam name="empty">
				<div class="orderRegError">
					<div class="small-12 columns">
						<p class="error"><bbbl:label key='lbl_no_orders_found' language="${pageContext.request.locale.language}" /></p> 
					</div>
				</div>
	    	</dsp:oparam>
	    </dsp:droplet>
		<c:if test="${not empty totalOrers}">
			</ul> 
			</li>
			<c:choose>
				<c:when test="${endIndex eq totalOrers }">
					<li> <dsp:a href="../track_order.jsp" iclass="button right disabled secondary"><dsp:param name="perPage" value="${perPage}"/><dsp:param name="pageNumber" value="${pageNumber}"/><span>&nbsp;</span></dsp:a> </li>
				</c:when>
				<c:otherwise>
					<li> <dsp:a href="../track_order.jsp" iclass="button right secondary dynFormSubmit"><dsp:param name="perPage" value="${perPage}"/><dsp:param name="pageNumber" value="${pageNumber+1}"/><span>&nbsp;</span></dsp:a> </li>
				</c:otherwise>
			</c:choose> 
			</ul> 
			</div> 
			<div class="grid-page-control pagination hide-for-medium-down"> 
				<ul class="inline-list right"> 
					<li><h3>Per page:</h3></li> 
					<li> <a class="small download radius button dropdown" data-dropdown="perPage" href="#">${perPage}<span>&nbsp;</span></a> 
						<ul class="f-dropdown per-page" data-dropdown-content="" id="perPage" name="perPage">
							
					      <c:choose>
					       <c:when test="${perPage eq 24}">
					        <li selected="selected" value="24"><dsp:a href="../track_order.jsp" iclass="dynFormSubmit"><dsp:param name="perPage" value="${perPage}"/><dsp:param name="pageNumber" value="${pageNumber}"/>${perPage}</dsp:a></li>
					        <li value="48"><dsp:a href="../track_order.jsp" iclass="dynFormSubmit"><dsp:param name="perPage" value="48"/><dsp:param name="pageNumber" value="${pageNumber}"/>48</dsp:a></li>
					        <li value="96"><dsp:a href="../track_order.jsp" iclass="dynFormSubmit"><dsp:param name="perPage" value="96"/><dsp:param name="pageNumber" value="${pageNumber}"/>96</dsp:a></li>
					       </c:when>
					       <c:when test="${perPage eq 48}">
					        <li value="24"><dsp:a href="../track_order.jsp" iclass="dynFormSubmit"><dsp:param name="perPage" value="24"/><dsp:param name="pageNumber" value="${pageNumber}"/>24</dsp:a></li>
					        <li selected="selected" value="48"><dsp:a href="../track_order.jsp" iclass="dynFormSubmit"><dsp:param name="perPage" value="${perPage}"/><dsp:param name="pageNumber" value="${pageNumber}"/>${perPage}</dsp:a></li>
					        <li value="96"><dsp:a href="../track_order.jsp" iclass="dynFormSubmit"><dsp:param name="perPage" value="96"/><dsp:param name="pageNumber" value="${pageNumber}"/>96</dsp:a></li>
					       </c:when>
					       <c:when test="${perPage eq 96}">
					        <li value="24"><dsp:a href="../track_order.jsp" iclass="dynFormSubmit"><dsp:param name="perPage" value="24"/><dsp:param name="pageNumber" value="${pageNumber}"/>24</dsp:a></li>
					        <li value="48"><dsp:a href="../track_order.jsp" iclass="dynFormSubmit"><dsp:param name="perPage" value="48"/><dsp:param name="pageNumber" value="${pageNumber}"/>48</dsp:a></li>
					        <li selected="selected" value="96"><dsp:a href="../track_order.jsp" iclass="dynFormSubmit"><dsp:param name="perPage" value="${perPage}"/><dsp:param name="pageNumber" value="${pageNumber}"/>${perPage}</dsp:a></li>
					       </c:when>
					      </c:choose> 
						</ul>
					</li>
				</ul>
			</div>	
			</div>
		</c:if>
	    

	    </br>
	    </br>
	    		
		<dsp:droplet name="Range">
		<dsp:param name="array" value="${orders}"/>
		<dsp:param name="sortProperties" value="+onlineOrderNumber"/>
		<dsp:param name="howMany" value="${perPage}"/>
		<dsp:param name="start" value="${startIndex}"/>
		<dsp:param name="elementName" value="orderVO"/>
		<dsp:oparam name="outputStart">
		<div class="searchResultsHeader searchResultsRow row">
			<div class="small-1 columns no-padding-left plusMinus hide-for-print">
                
            </div>
            <div class="small-12 medium-2 columns no-padding-right">
				Order Number
			</div>
			<div class="small-12 medium-1 columns no-padding-left">
				Date
			</div>
			<div class="small-12 medium-2 columns no-padding-left">
				Customer
			</div>
			<div class="small-12 medium-2 columns no-padding-left">
				Email
			</div>
			<div class="small-12 medium-1 columns no-padding-left">
				Amount
			</div>
			<div class="small-12 medium-1 columns no-padding-left">
				Status
			</div>
			<div class="small-12 medium-1 columns no-padding-left">
				Store
			</div>
			<div class="small-12 medium-1 columns no-padding-left">
				Associate
			</div>
            <br/>
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
     				<dsp:valueof param="orderVO.submittedDate"/>
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
     			<div class="small-12 medium-1 columns no-padding-left left">
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
	<script type="text/javascript">
		$(document).ready(function(){
			if($('#result').find('p.error') && $('#result').find('p.error').length > 0){
				$('#content').prev('.row').html($('#result').find('.orderRegError').html());
			}
		})
	</script>
	</div>
</dsp:page>
		
