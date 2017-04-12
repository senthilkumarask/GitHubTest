<dsp:page>
 <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
 <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
 <dsp:importbean bean="/atg/userprofiling/Profile" />
 <dsp:importbean bean="/com/bbb/cms/droplet/RecommenderLandingPageTemplateDroplet"/>
 <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/ValidateRecommenderDroplet" />
 <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

  <bbb:pageContainer>

 	<dsp:setvalue bean="SessionBean.RecommenderFlow" value="true"/>

	<dsp:droplet name="ValidateRecommenderDroplet">
		<dsp:param name="registryId" value="${param.registryId}" />
		<dsp:param name="eventType" value="${param.eventType}" />
		<dsp:param name="token" value="${param.token}" />
		<dsp:param name="fromURI" value="RLP" />
		<dsp:oparam name="output">
		</dsp:oparam>
		<dsp:oparam name="error">
           <dsp:getvalueof param="errormsg" var="errormsg" />
				<div class="container_12 clearfix">
					<div class="grid_12">
						<div class="error marTop_20 errorWrapper"><bbbe:error key="${errormsg}" language="${pageContext.request.locale.language}"/></div>
					</div>
	            </div>
		</dsp:oparam>
	</dsp:droplet>

	<dsp:getvalueof var="transient" bean="Profile.transient" />
	<c:choose>
		<c:when test="${transient == 'true' && not empty errormsg}">
			 <c:set var="tokenError">disabled</c:set>
			 <c:set var="recommenderURL">javascript:void(0)</c:set>
			 <c:set var="logInURL">javascript:void(0)</c:set>
	         <c:set var="visibilityFlag">block</c:set>
	         <c:set var="tokenErrorLogin">disabled</c:set>
	    </c:when>
	    <c:when test="${transient == 'false' && not empty errormsg}">
	         <c:set var="tokenError">disabled</c:set>
			 <c:set var="recommenderURL">javascript:void(0)</c:set>
			 <c:set var="logInURL">javascript:void(0)</c:set>
	         <c:set var="visibilityFlag">none</c:set>
	         <c:set var="tokenErrorLogin">disabled</c:set>
	    </c:when>
		<c:when test="${transient == 'true'}">
             <c:set var="tokenError"> </c:set>
		     <c:set var="recommenderURL">/store/account/login.jsp</c:set>
             <c:set var="visibilityFlag">block</c:set>
             <c:set var="logInURL">/store/account/login.jsp</c:set>
             <c:set var="tokenErrorLogin"> </c:set>
             <dsp:setvalue bean="SessionBean.RecommenderRedirectUrl"
			value="/store/giftregistry/view_registry_recommender.jsp?registryId=${param.registryId}&eventType=${param.eventType}&token=${param.token}"/>
        </c:when>
        <c:when test="${transient == 'false'}">
        	 <c:set var="tokenError"> </c:set>
             <c:set var="recommenderURL">/store/giftregistry/view_registry_recommender.jsp?registryId=${param.registryId}&eventType=${param.eventType}&token=${param.token}</c:set>
             <c:set var="visibilityFlag">none</c:set>
             <c:set var="logInURL">/store/account/login.jsp</c:set>
             <c:set var="tokenErrorLogin"> </c:set>
             <dsp:setvalue bean="SessionBean.RecommenderRedirectUrl"
			value="/store/giftregistry/view_registry_recommender.jsp?registryId=${param.registryId}&eventType=${param.eventType}&token=${param.token}"/>
        </c:when>
	</c:choose>

    <div class="tab_wrapper clearfix" id="recommenderPage">
      <dsp:droplet name="RecommenderLandingPageTemplateDroplet">
		<dsp:param name="siteId" param="siteId" />
		<dsp:param name="channel" param="channel" />
		<%--  BBSL-7610  --%>
        <dsp:param name="registryType" value="${param.eventType}" />
		<dsp:param name="recommenderURL" value="${recommenderURL}" />
		<dsp:param name="tokenError" value="${tokenError}" />
		<dsp:param name="visibilityFlag" value="${visibilityFlag}" />
		<dsp:param name="tokenErrorLogin" value="${tokenErrorLogin}" />
		<dsp:param name="logInURL" value="${logInURL}" />
        <dsp:oparam name="output">
		   <dsp:param name="promoBox" param="RecommenderTemplateVO.promoBox"/>
		   <dsp:getvalueof var="promoBoxContent" param="promoBox.promoBoxContent" />
		   <dsp:getvalueof var="altText" param="promoBox.imageAltText" />
		   <dsp:getvalueof var="imageLink" param="promoBox.imageLink" />
		   <dsp:getvalueof var="imageUrl123" param="promoBox.imageUrl" />
		   <dsp:getvalueof var="imageMapName" param="promoBox.imageMapName" />
		   <dsp:getvalueof var="imageMapContent" vartype="java.lang.String" param="promoBox.imageMapContent"/>
			 <c:choose>
				<c:when test="${not empty imageMapContent}">
				  <%-- Display Image Map --%>
				   <MAP NAME="${imageMapName}">${imageMapContent}</MAP>
				</c:when>
				<c:otherwise>
				   <a title="${altText}" href="${imageLink}">
					  <img alt="${altText}" src="${imageUrl}"/>
				   </a>
				</c:otherwise>
			 </c:choose>

		   <div class="container_12 contentWrapper">
			 <dsp:droplet name="ForEach">
			   <dsp:param name="array"  param="RecommenderTemplateVO.promoBoxList"/>
			   <dsp:oparam name="output">
				  <dsp:getvalueof var="promoBoxContent" param="element.promoBoxContent" />
				  <dsp:getvalueof var="altText" param="element.imageAltText" />
				  <dsp:getvalueof var="imageLink" param="element.imageLink" />
				  <dsp:getvalueof var="imageUrl" param="element.imageUrl" />
				  <dsp:getvalueof var="imageMapName" param="element.imageMapName" />
				  <dsp:getvalueof var="imageMapContent" vartype="java.lang.String" param="element.imageMapContent"/>
					<c:choose>
						<c:when test="${not empty imageMapContent}">
							<%-- Display Image Map --%>
								<MAP NAME="${imageMapName}">${imageMapContent}</MAP>
						</c:when>
						<c:otherwise>
							<a title="${altText}" href="${imageLink}">
								<img alt="${altText}" src="${imageUrl}"/>
							</a>
						</c:otherwise>
					</c:choose>
			   </dsp:oparam>
			 </dsp:droplet>
		   </div>

		   <dsp:param name="promoBoxBottom" param="RecommenderTemplateVO.promoBoxBottom"/>
		   <dsp:getvalueof var="promoBoxContent" param="promoBoxBottom.promoBoxContent" />
		   <dsp:getvalueof var="altText" param="promoBoxBottom.imageAltText" />
		   <dsp:getvalueof var="imageLink" param="promoBoxBottom.imageLink" />
		   <dsp:getvalueof var="imageUrl" param="promoBoxBottom.imageUrl" />
		   <dsp:getvalueof var="imageMapName" param="promoBoxBottom.imageMapName" />
		   <dsp:getvalueof var="imageMapContent" vartype="java.lang.String" param="promoBoxBottom.imageMapContent"/>
			 <c:choose>
				<c:when test="${not empty imageMapContent}">
					<%-- Display Image Map --%>
					<MAP NAME="${imageMapName}">${imageMapContent}</MAP>
				</c:when>
				<c:otherwise>
					<a title="${altText}" href="${imageLink}">
						<img alt="${altText}" src="${imageUrl}"/>
					</a>
				</c:otherwise>
			 </c:choose>
	    </dsp:oparam>
      </dsp:droplet>
    </div>
    <jsp:attribute name="footerContent">
           <script type="text/javascript">
           if(typeof s !=='undefined') {
				    s.pageName = 'Registry>Social Rec Landing Page';
				    s.channel = 'Registry';
				    s.prop1 = 'Registry';
				    s.prop2 = 'Registry';
				    s.prop3 = 'Registry';
				var s_code=s.t();if(s_code)document.write(s_code);
		   }
        </script>
    </jsp:attribute>
  </bbb:pageContainer>
</dsp:page>