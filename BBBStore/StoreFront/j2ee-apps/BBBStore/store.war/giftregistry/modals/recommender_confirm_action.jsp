<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler"/>
	<dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
	<dsp:getvalueof id="contextroot" idtype="java.lang.String" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="registryId" param="registryId" />
	<dsp:getvalueof var="recommenderProfileId" param="recommenderProfileId" />
	<dsp:getvalueof var="requestedFlag" param="requestedFlag" />
	<dsp:getvalueof var="requiredFlag" param="requiredFlag" />
	<dsp:getvalueof var="recommenderName" param="recommenderName" />
	<dsp:getvalueof var="scheme" bean="/OriginatingRequest.scheme"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof var="serverName" bean="/OriginatingRequest.serverName"/>
	<dsp:getvalueof var="serverPort" bean="/OriginatingRequest.serverPort"/>
	<dsp:getvalueof id="contextroot" idtype="java.lang.String" bean="/OriginatingRequest.contextPath"/>
	<c:set var="unblock"><bbbl:label key="lbl_recommenders_tab_unblock" language="${pageContext.request.locale.language}" /></c:set>
	<c:set var="block"><bbbl:label key="lbl_recommenders_tab_block" language="${pageContext.request.locale.language}" /></c:set>
	<dsp:form action="" id="blockRecommender" method="post">
		<c:choose>
		
			<c:when test="${requestedFlag == 'unblock'}">
				<h3 class="modalTitle">					
					<bbbl:label key="lbl_recommenders_tab_unblock_message" language="${pageContext.request.locale.language}" /> ${recommenderName} <bbbl:label key="lbl_recommenders_tab_questionmark" language="${pageContext.request.locale.language}" />
					<c:set var="message">${recommenderName} <bbbl:textArea key="txt_recommenders_tab_unblock" language="${pageContext.request.locale.language}" />
					<bbbl:textArea key="txt_recommenders_tab_unblock_noemail" language="${pageContext.request.locale.language}" /></c:set>
				</h3>
			</c:when>
			<c:when test="${requestedFlag == 'block'}">
			<h3 class="modalTitle">
				<bbbl:label key="lbl_recommenders_tab_block_message" language="${pageContext.request.locale.language}" /> ${recommenderName} <bbbl:label key="lbl_recommenders_tab_questionmark" language="${pageContext.request.locale.language}" />
				<c:set var="message"><bbbl:textArea key="txt_recommenders_tab_block" language="${pageContext.request.locale.language}" /> ${recommenderName}.
				<bbbl:textArea key="txt_recommenders_tab_block_noemail" language="${pageContext.request.locale.language}" /></c:set>
			</h3>
			</c:when>
		</c:choose>
	

		<div class="recommenderconfirmBlock">${message}</div>
		<dsp:input bean="GiftRegistryFormHandler.registryId" value="${registryId }" type="hidden" />
		<dsp:input bean="GiftRegistryFormHandler.recommenderProfileId"	value="${recommenderProfileId}" type="hidden" />
		<dsp:input bean="GiftRegistryFormHandler.requestedFlag"	value="${requestedFlag }" type="hidden" />
		<dsp:input bean="GiftRegistryFormHandler.queryParam"  type="hidden" value="requestedFlag=${requestedFlag}&registryId=${registryId}&recommenderName=${recommenderName}" />
		<%-- Client DOM XSRF | Part -1
		<c:set var="successURL">${scheme}://${serverName}${contextPath}/giftregistry/modals/recommender_confirm_action_json.jsp?requestedFlag=${requestedFlag}&registryId=${registryId}&recommenderName=${recommenderName}</c:set>
		<dsp:input bean="GiftRegistryFormHandler.toggleSuccessURL"	value="${successURL }" type="hidden" />
		<dsp:input bean="GiftRegistryFormHandler.toggleFailureURL"	value="${successURL }" type="hidden" /> --%>
		<dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden" value="toggleBlockRecommender" />
			
			<c:choose>
				<c:when test="${requestedFlag == 'unblock' }">
					<c:set var="label"><bbbl:label key="lbl_recommenders_tab_unblock" language="${pageContext.request.locale.language}" /></c:set>
				</c:when>
				<c:when test="${requestedFlag == 'block' }">
					<c:set var="label"><bbbl:label key="lbl_recommenders_tab_block" language="${pageContext.request.locale.language}" /></c:set>
				</c:when>
			</c:choose>
			<div>
				<dsp:input bean="GiftRegistryFormHandler.ToggleBlockRecommender" type="submit" value="${label}" iclass="button-Med btnPrimary"/>
			</div>
			
		<a href="#" id="cancelButton" class="buttonTextLink close-any-dialog capitalize">Cancel</a>
	</dsp:form>
	<div class="blockRecommenderMsg">
		<h3 class="modalTitle"></h3>
		<div>
			<input type="button" value="ok" class="button-Med btnPrimary closeDailogue"/>
		</div>
	</div>
</dsp:page>