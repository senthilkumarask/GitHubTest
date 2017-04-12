<%@ page import="com.bbb.constants.BBBCoreConstants" %>
<dsp:page>
	<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFlyoutDroplet"/>
    <dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryTypeNameDroplet"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	
		
    <dsp:include page="/_includes/third_party_on_of_tags.jsp"/>	
	
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:getvalueof bean="SessionBean.registryTypesEvent" id="event"/>
 <div id="header">
            <div class="row">
             <dsp:include page="/navigation/gadgets/logo.jsp" flush="true">
             	<dsp:param name="event" value="${event}"/>
             </dsp:include>
                <div class="small-8 columns pushDown no-padding">
                    <div id="registryTitles" class="clearfix">
                        
                        	<dsp:droplet name="GiftRegistryFlyoutDroplet">
								<dsp:param name="profile" bean="Profile"/>
    							<dsp:oparam name="output">
    								<dsp:droplet name="/atg/dynamo/droplet/Switch">
            							<dsp:param name="value" param="userStatus"/>
									    <dsp:oparam name="<%= BBBCoreConstants.USER_LOGGED_IN_WITH_MULTIPLE_REGISTRIES %>">
                            	  			<a class="myRegistries" href="${contextPath}/giftregistry/my_registries.jsp"><bbbl:label key="lbl_event_myregistry" language ="${pageContext.request.locale.language}"/></a>
										</dsp:oparam>
										<dsp:oparam name="<%= BBBCoreConstants.USER_LOGGED_IN_WITH_SINGLE_REGISTRY %>">
		                                   	<a class="myRegistries" href="${contextPath}/giftregistry/my_registries.jsp"><bbbl:label key="lbl_event_myregistry" language ="${pageContext.request.locale.language}"/></a>
										</dsp:oparam>
									</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
                        
                        <%-- <h2 class="capitalize">
						<dsp:getvalueof var="registryId" param="registryId"/>
						<c:choose>
						<c:when test="${not empty registryId}">
								Update 
						</c:when>
						<c:otherwise>
								Create 
						</c:otherwise>
						</c:choose>
                        	<dsp:droplet name="GetRegistryTypeNameDroplet">
                        		<dsp:param name="siteId" value="${appid}"/>
							 	<dsp:param name="registryTypeCode" value="${event}"/>
    							<dsp:oparam name="output">
						<dsp:valueof param="registryTypeName"/>
								<dsp:getvalueof var="eventType" param="registryTypeName" scope="request"/>
								</dsp:oparam>
							</dsp:droplet>
						Registry</h2> --%>

                    </div>
                </div>
                <%-- <div class="grid_2 pushDown">
                     <div id="chatModal">
                    	<div id="chatModalDialogs"></div>
							<dsp:include page="/common/click2chatlink.jsp">
                        <dsp:param name="pageId" value="2"/>
                        </dsp:include>
                        						
                     <br>
                 	</div>
                </div> --%>

            </div>
        </div>
</dsp:page>