<dsp:page>
<dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>    
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFlyoutDroplet"/>
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <c:choose>
      	<c:when test="${siteId eq 'BedBathUS'}">
      		 <div class="container_12 clearfix regFormBg">
      	</c:when>
      	<c:otherwise>
      		   <div class="container_12 clearfix">
      	</c:otherwise>
      </c:choose>
	<dsp:droplet name="GiftRegistryFlyoutDroplet">
			<dsp:param name="profile" bean="Profile"/>
			    <dsp:oparam name="output">
			        <dsp:droplet name="/atg/dynamo/droplet/Switch">
			            <dsp:param name="value" param="userStatus"/>
				                 <dsp:oparam name="1">
				                 <c:choose>
				                 	<c:when test="${siteId eq 'BedBathUS'}">
				                 		<dsp:include page="bridal_landing_non_logged_registry_us.jsp"/>
				                 	</c:when>
				                 	<c:otherwise>
				                 		  <dsp:include page="bridal_landing_non_logged_registry.jsp"/>
				                 	</c:otherwise>
				                 </c:choose>
								</dsp:oparam>
								
								<dsp:oparam name="2">
									<dsp:include page="bridal_landing_logged_no_registry.jsp"/>
								</dsp:oparam>
								
								<dsp:oparam name="4">
									<dsp:include page="bridal_landing_logged_mul_registry.jsp">
										<dsp:param name="registrySummaryVO" param="registrySummaryVO" />
										<dsp:param name="multiReg" value="true"/>
									</dsp:include>
								</dsp:oparam>
								
								<dsp:oparam name="3">
									<dsp:include page="bridal_landing_logged_mul_registry.jsp">
										<dsp:param name="multiReg" value="false"/>
									</dsp:include>
								</dsp:oparam>
								
				     	</dsp:droplet>
			</dsp:oparam>
     </dsp:droplet>
     </div>
</dsp:page>