<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>

<c:set var="harteAndHanksURLCouponwallet"><bbbc:config key="harteAndHanksURLCouponwallet" configName="ThirdPartyURLs" /></c:set>


    <%-- 

    My Preferences Links to iframe (only for ATG logged in):
    BBBY URL for My Preferences iframe (passed to HH with ATG token)
    https://bbbprefcenter.test-harte-hanks.com/couponwallet/bedbathus/preferences/  ATG token

    bbb URL for My Preferences ifram (passed to HH with ATG token) 
    https://bbbprefcenter.test-harte-hanks.com/couponwallet/buybuybaby/preferences/ATG token
    --%>
    <%-- harte hanks pref iframe modal --%>
    
    <dsp:getvalueof var="dataCenter" bean="/atg/dynamo/service/IdGenerator.dcPrefix"/>
			
			 <c:forEach items="${cookie}" var="bbbCookie">				 
			     <c:if test="${bbbCookie.value.name == 'HarteHanks'}">  
			     <c:set var="dc">${bbbCookie.value.value}</c:set>
			     </c:if>
     		 </c:forEach>
			 
			 
		 <c:if test="${empty dc}">
				<c:set var="dc">1</c:set> 
			<c:choose>
				<c:when test="${dataCenter=='DC1'}">
					<c:set var="dc">1</c:set>
				</c:when>
				<c:when test="${dataCenter=='DC2'}">
					<c:set var="dc">2</c:set>
				</c:when>
				<c:when test="${dataCenter=='STG1'}">
					<c:set var="dc">s1</c:set>
				</c:when>
				<c:when test="${dataCenter=='STG2'}">
					<c:set var="dc">s2</c:set>
				</c:when>
			</c:choose>
		</c:if>
          
        <dsp:droplet name="/com/bbb/account/BBBStoreSessionDroplet">
            <dsp:param name="currSite" value="${currentSiteId}"/>
            <dsp:oparam name="success">
                <dsp:getvalueof  id="URL" param="URL"/>
                 <dsp:getvalueof var="token" param="atg_harte_hanks_token"/>
                                    
                <iframe 
                    type="some_value_to_prevent_js_error_on_ie7" 
                    id="hhIframe" 
                    title='<bbbl:label key="lbl_myaccount_preferences" language ="${pageContext.request.locale.language}"/>' 
                    src='${harteAndHanksURLCouponwallet}?token=${token}&dc=${dc}' 
                    width="100%" 
                    height="90%" 
                    scrolling="yes" 
                    frameBorder="0" 
                    class="noOverflow">
                </iframe>
                
            </dsp:oparam>
            <dsp:oparam name="fail">
                <div class="">
                    <bbbl:label key="lbl_prefrences_info" language ="${pageContext.request.locale.language}"/>
                </div>  
            </dsp:oparam>
        </dsp:droplet>
    