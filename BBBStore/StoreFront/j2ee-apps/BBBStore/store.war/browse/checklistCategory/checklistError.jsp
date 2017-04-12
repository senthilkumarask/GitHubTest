<dsp:page>	

<bbb:pageContainer>
		
<jsp:attribute name="section">browse</jsp:attribute>
<jsp:attribute name="pageWrapper"></jsp:attribute>
<jsp:attribute name="titleString"></jsp:attribute>

   
 <jsp:body>
<dsp:getvalueof var="displayAction" param="displayAction"/>
<dsp:getvalueof var="checkListName" param="displayAction"/>

<dsp:getvalueof var="checkListDisplayName" param="checkListDisplayName"/>
<dsp:getvalueof var="categoryDisplayName" param="categoryDisplayName"/>

<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO" var="registrySummaryVO" />
<jsp:useBean id="placeHolderMap" class="java.util.HashMap" scope="request" />
<c:set var ="valueNotFoundForKey" value="VALUE NOT FOUND FOR KEY"/>
<c:set var ="registryId" value="${registrySummaryVO.registryId}"/>
<c:set var ="eventType" value="${registrySummaryVO.eventType}"/>
<div id="content" class="container_12 clearfix" role="main">
    <div id="oopsPageContent">            	
     	<div class="grid_12 oops_img">                    
            <div class="oops_message">
				<c:choose>
				
					<c:when test="${'inValidUrl' eq displayAction }">
						<c:choose>
								<c:when test="${not empty registryId && not empty eventType}">
									<c:set target="${placeHolderMap}" property="registryId" value="${registryId}" />
		                            <c:set target="${placeHolderMap}" property="eventType" value="${eventType}" />
		                            <bbbt:textArea key="txt_invalid_checklist_link_rlp_page" placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" />
								</c:when>
								<c:otherwise>
								     <bbbt:textArea key="txt_invalid_checklist_link_my_registry_page" language="${pageContext.request.locale.language}" />
								</c:otherwise>
						</c:choose>
					</c:when>
					
					<c:when test="${'nonLoginUser' eq displayAction }">
						<bbbt:textArea key="txt_disable_guide_type_checklist" language="${pageContext.request.locale.language}" />
					</c:when>
					<c:when test="${'icConfigOff' eq displayAction }">
						<bbbt:textArea key="txt_checklist_config_key_off" language="${pageContext.request.locale.language}" />
					</c:when>
					
					<c:when test="${'LoginUser' eq displayAction }">
							<c:choose>
								<c:when test="${'true' ne checkListEnable}">
									    <c:set var ="textAreaErrKey" value="txt_disable_checklist_${checkListDisplayName}"/>
									   
										 <c:set target="${placeHolderMap}" property="registryId" value="${registryId}" />
		                                 <c:set target="${placeHolderMap}" property="eventType" value="${eventType}" />
									     <c:set var ="textArea"> <bbbt:textArea key="${textAreaErrKey}" placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" /></c:set>
										 <c:choose>
											<c:when test="${not empty registryId && not empty eventType && not empty textArea 
															&& ! fn:containsIgnoreCase(textArea, valueNotFoundForKey)}">
										 		<dsp:valueof value="${textArea}" valueishtml="true" />
										 	</c:when>
										 	<c:when test="${ not empty registryId && not empty eventType && not empty textArea 
															&&  fn:containsIgnoreCase(textArea, valueNotFoundForKey)}">
										 		<c:set target="${placeHolderMap}" property="CheckListDisplayName" value="${checkListDisplayName}" />
										 		<bbbt:textArea key="txt_disable_checklist_name_undefined" placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" />
										 	</c:when>
										 	<c:otherwise>
										 		<c:set target="${placeHolderMap}" property="CheckListDisplayName" value="${checkListDisplayName}" />
										 		<bbbt:textArea key="txt_disable_checklist_link_my_registry_page" placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" />
										 	</c:otherwise>
										 </c:choose>
								</c:when>
								<c:when test="${'true' ne categoryEnable  }">
										<c:set target="${placeHolderMap}" property="CheckListCategoryDisplayName" value="${categoryDisplayName}" />
										<c:set target="${placeHolderMap}" property="CheckListDisplayName" value="${checkListDisplayName}" />
										<bbbt:textArea key="txt_checklist_disable_category" placeHolderMap="${placeHolderMap}" language="${pageContext.request.locale.language}" />
								</c:when>
							</c:choose>
					</c:when>
					
					
					
				</c:choose>
	 		  </div>
         </div>
     </div>
</div>
</jsp:body>
<jsp:attribute name="footerContent">

</jsp:attribute>
</bbb:pageContainer>
</dsp:page>
