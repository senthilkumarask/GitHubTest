<dsp:page>
<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/atg/userprofiling/Profile" />
   	 <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFlyoutDroplet"/>
<c:set var="enableRegSearchById" scope="request"><bbbc:config key="enableRegSearchById" configName="FlagDrivenFunctions" /></c:set>
<dsp:getvalueof var="siteId" bean="Site.id" />		
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>		
	  <dsp:getvalueof var="transient" bean="Profile.transient"></dsp:getvalueof>
	  <c:if test='${!transient}'><c:set var="loggedStatusCss">loggedInNew</c:set></c:if> 	
      <dsp:droplet name="GiftRegistryFlyoutDroplet">
			<dsp:param name="profile" bean="Profile" />
			    <dsp:oparam name="output">
			        <dsp:droplet name="/atg/dynamo/droplet/Switch">
			            <dsp:param name="value" param="userStatus" />
				                 <dsp:oparam name="1">
				                 <c:set var="loggedStatusCss">loggedOut</c:set>
								</dsp:oparam>	
								<dsp:oparam name="2">
									<c:set var="loggedStatusCss">loggedInNew</c:set>  
								</dsp:oparam>	
								<dsp:oparam name="4">
								<c:set var="loggedStatusCss">loggedInMultiple</c:set> 
								</dsp:oparam>
								<dsp:oparam name="3">
									<c:set var="loggedStatusCss">loggedInSingle</c:set> 
								</dsp:oparam>
				     	</dsp:droplet>
			</dsp:oparam>
      </dsp:droplet> 
 <c:choose>
	<c:when test="${enableRegSearchById == 'true'}">
		 <div class="grid_6 logIn enableRegSearchById">
	</c:when>
	<c:otherwise>
		 <div class="grid_6 logIn">
	</c:otherwise>
</c:choose>	


			<c:if test="${siteId eq 'BedBathUS' and loggedStatusCss eq 'loggedOut'}">
	 			<!-- <h3><bbbl:label key="lbl_find_registry_nonlogged" language ="${pageContext.request.locale.language}"/></h3>  -->
	 			<h3>find<span>a registry</span></h3>
		      	<!-- 
		      	<div class="alreadyRegCta">
		        <p class="alreadyRegText"><bbbl:label key="lbl_already_registered" language ="${pageContext.request.locale.language}"/></p>
		        <p>
					 <dsp:a href="${contextPath}/giftregistry/my_registries.jsp"><bbbl:label key="lbl_regsearch_login_to_accnt" language="${pageContext.request.locale.language}" /></dsp:a>
				</p>
		         </div> 
		         -->
	         </c:if>
         		<c:choose>
					<c:when test="${siteId == 'BuyBuyBaby'}">
						<div class="guests">						
							<h3><bbbl:label key="lbl_find_registry_registry_header" language ="${pageContext.request.locale.language}"/></h3>
							<p><span><bbbl:label key="lbl_regcreate_for" language ="${pageContext.request.locale.language}"/></span><bbbl:label key="lbl_regcreate_guests" language ="${pageContext.request.locale.language}"/></p>
						</div>
					</c:when>
				</c:choose>	
				<c:choose>
					<c:when test="${siteId == 'BuyBuyBaby'}">
						<c:set var="handlerMethodForSearch" value="registrySearchFromBabyLanding" scope="request" />
					</c:when>
					<c:otherwise>
						<c:set var="handlerMethodForSearch" value="registrySearchFromBridalLanding" scope="request" />
					</c:otherwise>
				</c:choose>	
				<c:set var="findButton"><bbbl:label key='lbl_find_reg_submit_button' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
				<c:set var="findRegistryFormCount" value="1" scope="request"/>
				<c:choose>
                 	<c:when test="${siteId eq 'BedBathUS'}">
                 		 <dsp:include page="/addgiftregistry/find_registry_widget_us.jsp">
							<dsp:param name="findRegistryFormId" value="frmFindRegistry" />
							<dsp:param name="submitText" value="${findButton}" />
							<dsp:param name="handlerMethod" value="${handlerMethodForSearch}" />
							<dsp:param name="bridalException" value="false" />
							<dsp:param name="findRegistryFormCount" value="${findRegistryFormCount}" />
						</dsp:include>
                 	</c:when>
                 	<c:otherwise>
                 		  <dsp:include page="/addgiftregistry/find_registry_widget.jsp">
							<dsp:param name="findRegistryFormId" value="frmFindRegistry" />
							<dsp:param name="submitText" value="${findButton}" />
							<dsp:param name="handlerMethod" value="${handlerMethodForSearch}" />
							<dsp:param name="bridalException" value="false" />
							<dsp:param name="findRegistryFormCount" value="${findRegistryFormCount}" />
						</dsp:include>
                 	</c:otherwise>
                 </c:choose>
				<c:if test="${siteId eq 'BedBathUS' and loggedStatusCss eq 'loggedOut'}">		 			
			      	<div class="alreadyRegCta">
			        <p class="alreadyRegText"><bbbl:label key="lbl_already_registered" language ="${pageContext.request.locale.language}"/></p>
			        <p>
						
						<dsp:a href="${contextPath}/giftregistry/my_registries.jsp">View & Manage Your Registry &gt; </dsp:a> 
					</p>
			         </div>
		         </c:if>
				</div>

</dsp:page>				