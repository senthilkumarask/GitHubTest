<dsp:page>
      <bbb:pageContainer divId="atg_store_pageNotFoundIntro" bodyClass="atg_store_pageNotFound" titleKey="">
      <div class="container_12 clearfix"><div class="grid_12 clearfix">   <div class="atg_store_nonCatHero">
          <h2 class="title"><%--global_409Page.title--%>Security Error</h2>
        </div>
        
		
		<dsp:getvalueof var="requestUrl" bean="/OriginatingRequest.requestURL" />
    	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    	<c:set var="pageSecured" value="${pageContext.request.secure}" scope="request" />
    	<c:set var="startIndex" value="${fn:indexOf(requestUrl, contextPath)}"/>
    	<c:set var="url" value="${fn:substring(requestUrl, 0, startIndex)}"/>
    	<c:set var="finalUrl" value="${url}${contextPath}/"/>
    	
    	<jsp:useBean id="linkURL" class="java.util.HashMap" scope="request"/>
		<c:set target="${linkURL}" property="homePageLink" value="${finalUrl}"/>
        <bbbe:error key="err_security_token_mismatch" placeHolderMap="${linkURL}" language="${pageContext.request.locale.language}"/>
        <%--global_409Page.Msg--%>
      </div></div>
     </bbb:pageContainer>
</dsp:page>
