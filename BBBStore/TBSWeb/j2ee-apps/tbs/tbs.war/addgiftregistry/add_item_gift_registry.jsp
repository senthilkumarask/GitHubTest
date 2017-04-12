<dsp:page>
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/AddItemToGiftRegistryDroplet" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    
        <dsp:getvalueof var="appid" bean="Site.id" />
        <dsp:getvalueof var="ltlFlag" param="ltlFlag" />
        <dsp:getvalueof var="isCustomizationRequired" param="isCustomizationRequired"/>
        <dsp:getvalueof var="isItemExcluded" param="isItemExcluded" />
        <dsp:getvalueof var="transient" bean="Profile.transient" />
        <dsp:getvalueof var="disableCTA" param="disableCTA"/>

        <c:if test="${empty disableCTA}">
			<c:set var="disableCTA" value="${isCustomizationRequired}" />
		</c:if>
		<!-- Changes for HYD-59 starts -->
     	<c:set var="enableLTLRegForSite">
			<bbbc:config key="enableLTLRegForSite" configName="FlagDrivenFunctions" />
		</c:set>
		 <c:set var="disableRegistry" value="${false}" />
		 <c:if test="${ltlFlag && 'false' eq fn:toLowerCase(enableLTLRegForSite)}">
			<c:set var="disableRegistry" value="${true}" />
		</c:if>
		<!-- Changes for HYD-59 Ends -->
            <dsp:droplet name="AddItemToGiftRegistryDroplet">
                <dsp:param name="siteId" value="${appid}"/>
            </dsp:droplet>
             <c:set var="submitAddToRegistryBtn"><bbbl:label key='lbl_add_to_registry' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
                    <dsp:getvalueof bean="SessionBean.values" var="sessionMapValues"/>
                    <dsp:getvalueof value="${sessionMapValues.registrySkinnyVOList}" var="registrySkinnyVOList"/>
                    <dsp:getvalueof var="sizeValue" value="${fn:length(registrySkinnyVOList)}"/>
            <c:choose>
                    <c:when test="${transient == 'false'}">
                          <dsp:getvalueof var="kickStarterPage" param="kickStarterPage" />
                            <c:choose>
                                <c:when test="${kickStarterPage=='yes' && sizeValue>1 }">
                                <dsp:droplet name="ForEach">
                                                <dsp:param name="array" value="${registrySkinnyVOList}" />
                                                <dsp:oparam name="output">
                                                 <dsp:getvalueof var="index" param="index" />
                                                   <c:choose>
                                                         <c:when test="${index==1}">    
                                                                                                     
                                                            <dsp:param name="futureRegList" param="element" />
                                                             <dsp:getvalueof var="regId"
                                                                          param="futureRegList.registryId" />
                                                             <dsp:getvalueof var="registryName"
                                                                            param="futureRegList.eventType" />
                                                    <c:if test="${empty regIdLst}"> 
                                                    <input  type="hidden"value="${regId}" name="registryId" class="addItemToRegis" />
                                                    </c:if>
                                                    <c:if test="${empty registryNameLst}">  
                                                    <input  type="hidden"value="${registryName}" name="registryName" class="addItemToRegis" />
                                                    </c:if>
                                                    <c:if test="${not empty regId}">
                                                               <dsp:setvalue  bean="SessionBean.registryId" value="${regId}"/>
                                                     </c:if>
                                                     <c:if test="${not empty registryName}">
                                                                 <dsp:setvalue  bean="SessionBean.eventType" value="${registryName}"/>
                                                     </c:if>
                                                         </c:when>
                                                   </c:choose>
                                                </dsp:oparam>
                                            </dsp:droplet>
                                           
                                            <div class="fl addToRegistry">
                                                <div class="<c:if test='${disableRegistry || isItemExcluded || caDisabled || disableCTA}'>button_disabled</c:if>">
                                                <input name="btnAddToRegistry" class="tiny button expand secondary btnAddToRegistry"
                                                        type="button" value="${submitAddToRegistryBtn}" role="button" aria-pressed="false" <c:if test='${disableRegistry || isItemExcluded || disableCTA || caDisabled }'>disabled="disabled"</c:if> />
                                                </div>
                                            </div>
                                        </c:when>
                                        <c:when test="${sizeValue>1}">
                                                <div class="fl addToRegistry addToRegistrySel">
                                                <div class="select">
                                                <c:choose>
                                                    <c:when test="${disableRegistry || isItemExcluded || disableCTA || caDisabled }">
                                                        <dsp:select bean="GiftRegistryFormHandler.registryId" id="addItemToRegMultipleRegID" name="registryId" iclass="disabled addItemToRegis addItemToRegMultipleReg selector_primaryAlt">
                                                    <dsp:option> <bbbl:label key="lbl_add_to_registry"
                                                    language="${pageContext.request.locale.language}" /></dsp:option>
                                                        <dsp:droplet name="ForEach">
                                                            <dsp:param name="array" value="${registrySkinnyVOList}" />
                                                            <dsp:oparam name="output">
                                                                <dsp:param name="futureRegList" param="element" />
                                                            
                                                                <dsp:getvalueof var="regId" param="futureRegList.registryId" />
                                                                <dsp:getvalueof var="event_type" param="element.eventType" />
                                                                <dsp:option value="${regId}"  iclass="${event_type}">
                                                                    <dsp:valueof param="element.eventType"></dsp:valueof> <dsp:valueof param="element.eventDate"></dsp:valueof>
                                                                </dsp:option>
                                                            </dsp:oparam>
                                                        </dsp:droplet>
                                                        <dsp:tagAttribute name="aria-required" value="false"/>
                                                        <dsp:tagAttribute name="disabled" value="disabled" />
                                                    </dsp:select>
                                                    </c:when>
                                                    <c:otherwise>
                                                    <dsp:select bean="GiftRegistryFormHandler.registryId" id="addItemToRegMultipleRegID" name="registryId" iclass="addItemToRegis addItemToRegMultipleReg selector_primaryAlt">
                                                    <dsp:option> <bbbl:label key="lbl_add_to_registry"
                                                    language="${pageContext.request.locale.language}" /></dsp:option>
                                                        <dsp:droplet name="ForEach">
                                                            <dsp:param name="array" value="${registrySkinnyVOList}" />
                                                            <dsp:oparam name="output">
                                                                <dsp:param name="futureRegList" param="element" />
                                                            
                                                                <dsp:getvalueof var="regId" param="futureRegList.registryId" />
                                                                <dsp:getvalueof var="event_type" param="element.eventType" />
                                                                <dsp:option value="${regId}"  iclass="${event_type}">
                                                                    <dsp:valueof param="element.eventType"></dsp:valueof> <dsp:valueof param="element.eventDate"></dsp:valueof>
                                                                </dsp:option>
                                                            </dsp:oparam>
                                                        </dsp:droplet>
                                                        <dsp:tagAttribute name="aria-required" value="false"/>
                                                    </dsp:select>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                                </div>
                                            </c:when>
                                            <c:when test="${sizeValue==1}">
                                            <dsp:droplet name="ForEach">
                                                <dsp:param name="array" value="${registrySkinnyVOList}" />
                                                <dsp:oparam name="output">
                                                    <dsp:param name="futureRegList" param="element" />
                                                    <dsp:getvalueof var="regId"
                                                        param="futureRegList.registryId" />
                                                        <dsp:getvalueof var="registryName"
                                                        param="futureRegList.eventType" />
                                                    <input  type="hidden"value="${regId}" name="registryId" class="addItemToRegis" />
                                                    <input  type="hidden"value="${registryName}" name="registryName" class="addItemToRegis" />
                                                     <c:if test="${not empty regId}">
                                                               <dsp:setvalue  bean="SessionBean.registryId" value="${regId}"/>
                                                  </c:if>
                                                 <c:if test="${not empty registryName}">
                                                                 <dsp:setvalue  bean="SessionBean.eventType" value="${registryName}"/>
                                                  </c:if>
                                                </dsp:oparam>
                                            </dsp:droplet>
                                            <div class="fl addToRegistry">
                                <div class="<c:if test='${disableRegistry || isItemExcluded || disableCTA || caDisabled }'>button_disabled</c:if>">
                                <input name="btnAddToRegistry" class="tiny button expand secondary btnAddToRegistry"
                                        type="button" value="${submitAddToRegistryBtn}" role="button" aria-pressed="false" <c:if test='${disableRegistry || isItemExcluded || disableCTA || caDisabled }'>disabled="disabled"</c:if>/>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                    		<div class="fl addToRegistry"> <%-- addToRegistryCreate ... this class should only be used if the value/label of the button is "Create Registry" --%>
                                <div class="<c:if test='${disableRegistry || isItemExcluded || disableCTA || caDisabled }'>button_disabled</c:if>">
                                 <input type="button" class="tiny button expand secondary btnAddToRegNoRegistry" name="btnAddToRegNoRegistry" 
 									value="${submitAddToRegistryBtn}" <c:if test='${disableRegistry || isItemExcluded || disableCTA || caDisabled }'>disabled="disabled"</c:if> />
                                    </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:when test="${transient == 'true'}">
                     <div class="fl addToRegistry">
                     
                        <div class="<c:if test='${disableRegistry || isItemExcluded || disableCTA || caDisabled }'>button_disabled</c:if>">
                        	<input name="addItemToRegNoLogin" class="tiny button expand secondary addItemToRegNoLogin" 
                                type="button" value="${submitAddToRegistryBtn}" <c:if test='${disableRegistry || isItemExcluded || disableCTA || caDisabled }'>disabled="disabled"</c:if> />
                        </div>
                     </div>
                </c:when>
            </c:choose>
</dsp:page>