<dsp:page>
	<dsp:importbean bean="com/bbb/selfservice/ChatDroplet" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <dsp:getvalueof var="pageId" param="pageId"/>
    <dsp:getvalueof var="prodId" param="prodId"/>
    <dsp:getvalueof var="catId" param="catId"/>
    <dsp:getvalueof var="divApplied" param="divApplied"/>
    <dsp:getvalueof var="divClass" param="divClass"/>
    <dsp:getvalueof var="linkContent" param="linkContent"/>


    <c:set var="chatPageId" value="" />
    <c:if test="${not empty pageId}">
        <c:set var="chatPageId" value="${pageId}/" />
    </c:if>
    <c:choose>
    <c:when test="${not empty prodId}">
    	<c:set var="chatData" value="_icf_1/${chatPageId}product/${prodId}" />
    </c:when>
    <c:when test="${not empty catId}">
    	<c:set var="chatData" value="_icf_1/${chatPageId}category/${catId}" />
    </c:when>
    <c:otherwise>
    	<c:set var="chatData" value="_icf_1/${pageId}" />
    </c:otherwise>
    </c:choose>
    <dsp:droplet name="ChatDroplet">
    	<dsp:oparam name="output">
    		<dsp:getvalueof var="chatglobalFlag" param="chatglobalFlag"></dsp:getvalueof>
    		<c:if test="${chatglobalFlag == 'true'}">
    			<c:choose>
    				<c:when test="${divApplied}">
	    				<div class="${divClass}">
    						<a title="<bbbl:label key='lbl_click_to_chat_image_title' language='${pageContext.request.locale.language}'/>" class="chatNow liveChat" data-chatdata="${chatData}" href="${contextPath}/selfservice/click_to_chat.jsp" onclick="javascript:externalLinks('Click to Chat');">${linkContent}</a>
    					</div>
    				</c:when>
    				<c:otherwise>
    					<a title="<bbbl:label key='lbl_click_to_chat_image_title' language='${pageContext.request.locale.language}'/>" class="chatNow liveChat" data-chatdata="${chatData}" href="${contextPath}/selfservice/click_to_chat.jsp" onclick="javascript:externalLinks('Click to Chat');">${linkContent}</a>
    				</c:otherwise>
    			</c:choose>
   			</c:if>
   		</dsp:oparam>
   	</dsp:droplet>
</dsp:page>
