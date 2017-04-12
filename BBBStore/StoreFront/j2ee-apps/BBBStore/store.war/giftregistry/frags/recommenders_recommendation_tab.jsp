<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/RecommendationInfoDisplayDroplet"/>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/DateCalculationDroplet" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler"/>
<dsp:importbean bean="/com/bbb/simplifyRegistry/droplet/RegistryStatusDroplet" />
<dsp:getvalueof param="registryId" var="registryId" />
<dsp:getvalueof param="pageNum" var="pageNum" />
<dsp:getvalueof var="emptyRegistrant" param="emptyRegistrant"/>
<dsp:getvalueof var="eventType" value="${fn:escapeXml(param.eventType)}"/>
<dsp:getvalueof var="eventTypeCode" param="eventTypeCode"/>
<dsp:getvalueof var="regEventDate" param="regEventDate"/>
<dsp:getvalueof var="regFirstName" param="regFirstName"/>
<dsp:getvalueof id="contextroot" idtype="java.lang.String" bean="/OriginatingRequest.contextPath"/>

<c:set var="checkDaily" value="false"/>
<c:set var="checkWeekly" value="false"/>
<c:set var="checkMonthly" value="false"/>
<c:set var="checkNever" value="false"/>


<dsp:droplet name="RegistryStatusDroplet">
	<dsp:param value="${registryId}" name="registryId" />
	<dsp:oparam name="output">
		<dsp:getvalueof var="regPublic" param="regPublic"/>
	</dsp:oparam>
</dsp:droplet>

   <div id="recommendations"  class="container_12 clearfix creditCard">
   		<div id="pending-registry">
			
				<div class="kickstarterSectionHeader grid_12 pending_recommendation_header noMar">
					<div class="grid_5">
						<bbbt:textArea key="txt_registry_recommendations"
							language="${pageContext.request.locale.language}"></bbbt:textArea>

					</div>
					<div class="grid_7 omega fr">
						<bbbt:textArea key="txt_registry_recomm_area"
							language="${pageContext.request.locale.language}"></bbbt:textArea>

					</div>
				</div>
				<div class="clear"></div>
				<div class="sort_container">
					<ul class="inner_tab fl">
						<li class="recommendInnerTab"><a href="javascript:void(0);" data-recomm="pendingRecommendation"><bbbl:label
									key="lbl_registry_owner_pending"
									language="${pageContext.request.locale.language}" /></a></li>
						<li class="recommendInnerTab"><a href="javascript:void(0);" data-recomm="acceptedRecommendation"><bbbl:label
									key="lbl_registry_owner_accepted"
									language="${pageContext.request.locale.language}" /></a></li>
						<li class="recommendInnerTab"><a href="javascript:void(0);" data-recomm="declinedRecommendation"><bbbl:label
									key="lbl_registry_owner_declined"
									language="${pageContext.request.locale.language}" /></a></li>
						<li class="recommendInnerTab"><a href="javascript:void(0);" data-recomm="recommendersRecommendation" class="active"><bbbl:label
									key="lbl_registry_owner_recommenders"
									language="${pageContext.request.locale.language}" /></a></li>

					</ul>
				</div>
				<input type="hidden" id="selectedRecommender" value="" />
				<div class="recommendersContent">
					<c:if test="${!regPublic}">
   						<div class="alert alert-alert recommendation clearfix">
   							<bbbl:label key='lbl_registry_share_modal_private_alert' language="${pageContext.request.locale.language}" />
   						</div>
   						<div class="overlay recommendersTab"></div>
   					</c:if>
						<dsp:droplet name="RecommendationInfoDisplayDroplet">
							<dsp:param name="registryId" value="${registryId }"/>
							<dsp:param name="tabId" value="3"/>
							<dsp:param name="sortOption" value="${sortOption }"/>
							<dsp:param name="pageSize" value="${pageSize }"/>
							<dsp:param name="pageNum" value="${pageNum }"/>
							<dsp:param name="emptyRegistrant" value="${emptyRegistrant }"/>
							<dsp:param name="fromRecommenderTab" value="true"/>
							<dsp:oparam name="output">
							<div class="grid_5 alpha">
								<dsp:getvalueof var="recommendationProductList"	param="recommendationProduct" />
								<dsp:droplet name="ForEach">
									<dsp:param name="array" value="${recommendationProductList}" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="el" param="element" ></dsp:getvalueof>
										<dsp:getvalueof var="recommendersFullName" value="${el.fullName }" />
										<dsp:getvalueof var="recommendedQuantity" value="${el.recommendedQuantity }" />
										<dsp:getvalueof var="acceptedQuantity" value="${el.acceptedQuantity }" />
										<dsp:getvalueof var="declinedQunatity" value="${el.declinedQuantity }" />
										<dsp:getvalueof var="profileActive" value="${el.profileActive }" />
										<dsp:getvalueof var="recommenderProfileId" value="${el.recommenderProfileId}" />
										<dsp:getvalueof var="emailOptInValue" param="emailOptInValue" />
										<div class="recommendersDetail" id="recommender${el.recommenderProfileId}">
										<c:set var="status" value="true" />
											<h2><c:out value="${recommendersFullName }" />
											<span class="blockedRecommender">
												<c:if test="${profileActive==false }">	
													<c:set var="status" value="false"/>
													<bbbl:label key="lbl_recommenders_tab_blocked" language="${pageContext.request.locale.language}"></bbbl:label>
												</c:if>
											</span>
											
											</h2>
											
											<ul>
												<li><bbbl:label key="lbl_recommenders_tab_recommended_quantity" language="${pageContext.request.locale.language}"></bbbl:label> <span>${recommendedQuantity }</span></li>
												<li><bbbl:label key="lbl_recommenders_tab_accepted_quantity" language="${pageContext.request.locale.language}"></bbbl:label> <span><c:out value="${acceptedQuantity }" /></span></li>
												<li><bbbl:label key="lbl_recommenders_tab_declined_quantity" language="${pageContext.request.locale.language}"></bbbl:label> <span><c:out value="${declinedQunatity }" /></span></li>
											</ul>
											
											<c:choose>
												<c:when test="${status eq true }">
												<c:set var="block"><bbbl:label key="lbl_recommenders_tab_block" language="${pageContext.request.locale.language}" /></c:set>

														<a href="${contextroot }/giftregistry/modals/recommender_confirm_action.jsp?registryId=${registryId }&recommenderProfileId=${el.recommenderProfileId }
														&recommenderName=${el.fullName }" requestedFlag="block" requiredFlag="unblock" class="blockStatus blockRecommender">${block}
														
														</a>
													
												</c:when>
												<c:otherwise>
												<c:set var="unblock"><bbbl:label key="lbl_recommenders_tab_unblock" language="${pageContext.request.locale.language}" /></c:set>	
												
														<a href="${contextroot }/giftregistry/modals/recommender_confirm_action.jsp?registryId=${registryId }&recommenderProfileId=${el.recommenderProfileId }
														&recommenderName=${el.fullName }" requestedFlag="unblock" requiredFlag="block" class="blockStatus blockRecommender">
														${unblock }
														</a>
													
												</c:otherwise>
											</c:choose>
										</div>
										<!-- <div class="recommendersDetail">
											<h2>Jane Smith<span class="blockedRecommender">Blocked</span></h2>
											<ul>
												<li>recommended: <span>42</span></li>
												<li>accepted: <span>42</span></li>
												<li>declined: <span>42</span></li>
				
											</ul>
											<a href="#">unblock</a>
										</div> -->
								</dsp:oparam>
								</dsp:droplet>
								</div>
								<bbbt:textArea key="txt_recommender_content"
							language="${pageContext.request.locale.language}"></bbbt:textArea>
							</dsp:oparam>
							<dsp:oparam name="error">
							  <div class="emptyRecommenderMsg noPad"><bbbt:textArea key="txt_empty_pending_recommendations" language ="${pageContext.request.locale.language}"/></div>
							</dsp:oparam>

						</dsp:droplet>
					
					<div class="grid_7">
						<h4><bbbl:label key="lbl_email_recommended_items" language="${pageContext.request.locale.language}" /></h4>
						<p><bbbl:label key="lbl_receive_digest" language="${pageContext.request.locale.language}" /></p>
                       
						<input type="hidden" name="emailOptIn" value="${emailOptInValue}"/>
						<form id="myForm">
							<ul>
								<li><input type="radio" name="radioEmailOption" value="0" role="radio" id="radioEmailOption_0"/>
									<label for="radioEmailOption_0"><bbbl:label key="lbl_Daily_dsk" language="${pageContext.request.locale.language}" /></label></li>
								<li><input type="radio" name="radioEmailOption" value="1" role="radio" id="radioEmailOption_1"/>
									<label for="radioEmailOption_1" ><bbbl:label key="lbl_Weekly_dsk" language="${pageContext.request.locale.language}" /></label></li>
								<li><input type="radio" name="radioEmailOption" value="2" role="radio" id="radioEmailOption_2"/>
									<label for="radioEmailOption_2"><bbbl:label key="lbl_Monthly_dsk" language="${pageContext.request.locale.language}" /></label></li>
								<li><input type="radio" name="radioEmailOption" value="-1" role="radio" id="radioEmailOption_3"/>
									<label for="radioEmailOption_3"><bbbl:label key="lbl_Never_dsk" language="${pageContext.request.locale.language}" /></label></li>
							</ul>
						</form>
					</div>
				
					<div class="grid_7">
						<h4><bbbl:label key="lbl_email_recommended_items" language="${pageContext.request.locale.language}" /></h4>
						<p>
							<bbbt:textArea key="txt_email_recommendation"
							language="${pageContext.request.locale.language}"></bbbt:textArea>
						</p>
						<dsp:droplet name="DateCalculationDroplet">
	                        <dsp:param name="eventDate" value="${regEventDate}"/>
	                        <dsp:param name="convertDateFlag" value="true" />
	                        <dsp:oparam name="output">
	                        <dsp:getvalueof param="daysCheck" var="isFutureDate"/>
	                        <c:if test="${not empty isFutureDate && isFutureDate eq 'true'}">
	                        <c:set var="inviteFriendURL">
			                 ${contextroot}/_includes/modals/inviteFriendsRegistry.jsp?eventType=${eventType}&registryId=${registryId}&regEventDate=${regEventDate}&regFirstName=${regFirstName}&emptyRegistrant=${emptyRegistrant}
			                 </c:set>
						  	<div id="inviteContainer">
						  		<a href="${inviteFriendURL}" class="prodTitle" data-pdp-qv-url="" title="" onclick="">
		                    	<span class="icon-fallback-text">
		                    	<span class="icon-envelope" aria-hidden="true"></span>
		                    	<span class="icon-text"><bbbl:label key="lbl_Invite_Via_Email" language="${pageContext.request.locale.language}"/></span></span><bbbl:label key="lbl_Invite_Via_Email" language="${pageContext.request.locale.language}"/>
                   		 	 	</a>                   		 	
                    	 	</div>
                    	 	</c:if>
							</dsp:oparam>
                   		</dsp:droplet>

					</div>
				</div>

   </div>
<script type="text/javascript">
	 var invitelink = $("#inviteFriends a").attr("href");
           $('#inviteContainer a').attr("href", invitelink);
</script>
</dsp:page>
