<dsp:page>
  <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFlyoutDroplet"/>
    <dsp:importbean bean="/atg/userprofiling/Profile" />
  <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryTypeNameDroplet"/>
    <dsp:include page="/_includes/third_party_on_of_tags.jsp"/> 
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
  <dsp:getvalueof bean="SessionBean.registryTypesEvent" id="event"/>

        <c:choose>
            <c:when test="${event == 'BA1' }">
                 <div id="header" class="babyRegistry">
                    <c:set var="CookieBlockerFlag"><bbbc:config key="CookieBlockerFlag" configName="FlagDrivenFunctions" /></c:set>         
                  <c:if test="${CookieBlockerFlag}">
                    <div id="cookie-block-msg-wrapper-registry" style="display:none;">
                      <bbbt:textArea key="txt_cookie_blocker_msg"  language ="${pageContext.request.locale.language}"/>
                    </div>
                  </c:if>
                 <div class="container_12 clearfix containerBaby">
            </c:when>
            <c:otherwise>
                <div id="header" class="otherRegistry">
                  <c:set var="CookieBlockerFlag"><bbbc:config key="CookieBlockerFlag" configName="FlagDrivenFunctions" /></c:set>         
                  <c:if test="${CookieBlockerFlag}">
                    <div id="cookie-block-msg-wrapper-registry" style="display:none;">
                      <bbbt:textArea key="txt_cookie_blocker_msg"  language ="${pageContext.request.locale.language}"/>
                    </div>
                  </c:if>
                <div class="container_12 clearfix">
            </c:otherwise>
        </c:choose>

             <dsp:include page="/navigation/gadgets/simpleRegLogo.jsp" flush="true">
              <dsp:param name="event" value="${event}"/>
             </dsp:include>
                <div class="pushDown">
                  <div id="registryTitles" class="center clearfix">
                  <h2 class="clearfix" aria-hidden="false" tabindex="0"><span class="simpleRegSubText"><bbbl:label key="lbl_create_your_reg" language="${pageContext.request.locale.language}" /></span></h2>
                    <div id="RegistrySelectType">
                  <dsp:include page= "/giftregistry/simpleReg_type_select.jsp">
                     <dsp:param name="event" value="${event}"/>
                     <dsp:param name="eventType" param="eventType" />
                  </dsp:include>
                </div>
                 </div>  
            </div>
        </div>
        </div>
       
</dsp:page>