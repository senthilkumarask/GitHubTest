<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>   
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFlyoutDroplet"/>
  
     <dsp:getvalueof var="registrySummaryVO" param="registrySummaryVO"/>
     <dsp:getvalueof var="userStatus" param="userStatus"/>
	 
     <dsp:getvalueof var="regbaby" param="regbaby" />
     <dsp:getvalueof var="regType" param="regType" />
     <jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request"/>
     <c:set target="${placeHolderMap}" property="imagePath"><dsp:valueof value="${imagePath}"/></c:set>
     
     <dsp:include page="baby_sub_header.jsp">
     	<dsp:param name="userStatus" value="${userStatus}"/>
     </dsp:include>
<%--   <c:choose>
     <c:when test="${regbaby eq true }">
       <bbbt:textArea key="txt_${regType}_registry_landing" language ="${pageContext.request.locale.language}"/>
     </c:when>
     <c:otherwise> --%>
 
	        <dsp:droplet name="Switch">
	            <dsp:param name="value" param="userStatus"/>
	                <dsp:oparam name="1">
					    <dsp:include page="baby_landing_non_logged_registry.jsp"/>
					</dsp:oparam>
					
					<dsp:oparam name="2">
					<dsp:include page="baby_landing_logged_no_registry.jsp"/>
					</dsp:oparam>
					
					<dsp:oparam name="4">
					<dsp:include page="baby_landing_logged_mul_registry.jsp">
						<dsp:param name="registrySummaryVO" value="${registrySummaryVO}" />
						<dsp:param name="multiReg" value="true"/>
					</dsp:include>
					</dsp:oparam>
					
					<dsp:oparam name="3">
					<dsp:include page="baby_landing_logged_mul_registry.jsp">
					 	<dsp:param name="registrySummaryVO" value="${registrySummaryVO}" />
					 	<dsp:param name="multiReg" value="false"/>
					</dsp:include>
					</dsp:oparam>
			</dsp:droplet>
 <%--  </c:otherwise>
 </c:choose> --%>
</dsp:page>   