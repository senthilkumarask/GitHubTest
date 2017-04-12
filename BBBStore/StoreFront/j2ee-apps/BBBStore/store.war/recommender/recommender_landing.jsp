<dsp:page>
<dsp:importbean bean="/com/bbb/cms/droplet/RecommendationLandingPageTemplateDroplet"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
    <dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/com/bbb/cms/formhandler/RecommenderLoginFormHandler" />
	
	

        <dsp:droplet name="RecommendationLandingPageTemplateDroplet">           	
                <dsp:param value="BedBathUS" name="siteId" />
                <dsp:param value="DesktopWeb" name="channel" />
                <dsp:param value="Wedding" name="registryType" />
                 <dsp:param value="/store/_includes/modals/inviteFriendsRegistry.jsp?eventType=Wedding&registryId=203521845" name="registryURL" />
                <dsp:oparam name="output">
                    <dsp:getvalueof  param="recommenderTemplateVO" var="recVO" scope="request"></dsp:getvalueof>
                </dsp:oparam>
            </dsp:droplet>
            <dsp:getvalueof var="isLoggedIn" bean="/atg/userprofiling/Profile.transient"/>
             <c:choose>
                    	<c:when test="${!isLoggedIn}">
                        	<a href="${contextPath}/account/login.jsp">Login</a>
                        </c:when>
                        <c:otherwise>
							<a href="${contextPath}/account/login.jsp"><bbbl:label key="lbl_footer_order_inquiry" language="${pageContext.request.locale.language}" /></a>
						</c:otherwise>
                    </c:choose>


</dsp:page>

