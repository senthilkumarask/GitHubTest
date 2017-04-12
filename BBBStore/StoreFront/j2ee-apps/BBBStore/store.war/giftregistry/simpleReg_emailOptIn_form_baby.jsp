<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
     <dsp:importbean bean="/atg/userprofiling/PropertyManager" />
	<dsp:getvalueof var="currentSiteId" bean="/atg/multisite/Site.id" />
	 <dsp:getvalueof var="babyCA" bean="SessionBean.babyCA"/>
	 <dsp:getvalueof var="formExceptions" bean="GiftRegistryFormHandler.formExceptions"/>
	                     <%--Adding code for email optIn --%>
               <div class="grid_10 alpha omega clearfix marTop_20">
                <c:choose>
               <c:when test="${not empty formExceptions && !babyCA}">
               <div class="optIn">
              <dsp:getvalueof var="emailOptinFromBean" bean="GiftRegistryFormHandler.emailOptIn"/>
                <dsp:input type="checkbox" bean="GiftRegistryFormHandler.emailOptIn" id="optIn" name="emailOptIn" checked="${emailOptinFromBean}" iclass="fl">
                    <dsp:tagAttribute name="aria-hidden" value="false"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
                    </dsp:input>
                       <label id="lbloptIn" for="optIn" class="textDgray11 wrapTextAfterCheckBox emailSubs grid_4"><bbbl:label key="lbl_email_optin_bedbath_canada" language="${pageContext.request.locale.language}"/></label>
               </div>
               </c:when>
               <c:when test="${not empty formExceptions && babyCA}">
               <div class="optIn padTop_10">
              <dsp:getvalueof var="emailOptinFromBean" bean="GiftRegistryFormHandler.emailOptIn_BabyCA"/>
                <dsp:input type="checkbox" bean="GiftRegistryFormHandler.emailOptIn_BabyCA" id="emailOptIn_BabyCA" name="emailOptIn_BabyCA" checked="${emailOptinFromBean}" iclass="fl">
		                        <dsp:tagAttribute name="aria-hidden" value="false"/>
		                        <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
		                        </dsp:input>
                       <label id="lbloptIn" for="optIn" class="textDgray11 wrapTextAfterCheckBox emailSubs grid_4"><bbbl:label key="lbl_email_optin_bedbath_canada" language="${pageContext.request.locale.language}"/></label>
               </div>
               </c:when>
               <c:otherwise>
               <c:choose>
               <c:when test="${!babyCA}">
				<div class="optIn">
              <dsp:getvalueof bean="GiftRegistryFormHandler.value.receiveEmail" id="flag">
              <c:choose>
                <c:when test="${currentSiteId eq 'BedBathCanada'}">
                    <dsp:input type="checkbox" bean="GiftRegistryFormHandler.emailOptIn" id="optIn" name="emailOptIn" checked="false" iclass="fl">
                    <dsp:tagAttribute name="aria-hidden" value="false"/>
                    <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
                    </dsp:input>
                </c:when>
                <c:otherwise>
                    <c:if test="${flag == 'yes'}">
                        <dsp:input type="checkbox" bean="GiftRegistryFormHandler.emailOptIn" id="optIn" name="emailOptIn" checked="true" iclass="fl">
                        <dsp:tagAttribute name="aria-hidden" value="false"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
                        </dsp:input>
                    </c:if>
                    <c:if test="${flag == 'no'}">
                      <dsp:input type="checkbox" bean="GiftRegistryFormHandler.emailOptIn" id="optIn" name="emailOptIn" checked="false" iclass="fl">
                      <dsp:tagAttribute name="aria-hidden" value="false"/>
                      <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
                      </dsp:input>
                    </c:if>
                </c:otherwise>
              </c:choose>
              </dsp:getvalueof>
              <label id="lbloptIn" for="optIn" class="textDgray11 wrapTextAfterCheckBox emailSubs grid_4"><bbbl:label key="lbl_email_optin_bedbath_canada" language="${pageContext.request.locale.language}"/></label>
            </div>
           </c:when>
            <%-- Baby Canada --%>
	            <c:otherwise>
		            <div class="optIn padTop_10">
		              <dsp:getvalueof bean="PropertyManager.receiveEmailPropertyName" id="flag">
		              <c:choose>
		                <c:when test="${currentSiteId eq 'BedBathCanada'}">
		                    <dsp:input type="checkbox" bean="GiftRegistryFormHandler.emailOptIn_BabyCA" id="emailOptIn_BabyCA" name="emailOptIn_BabyCA" checked="false" iclass="fl">
		                    <dsp:tagAttribute name="aria-hidden" value="false"/>
		                    <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
		                    </dsp:input>
		                </c:when>
		                <c:otherwise>
		                    <c:if test="${flag == 'yes'}">
		                        <dsp:input type="checkbox" bean="GiftRegistryFormHandler.emailOptIn_BabyCA" id="emailOptIn_BabyCA" name="emailOptIn_BabyCA" checked="true" iclass="fl">
		                        <dsp:tagAttribute name="aria-hidden" value="false"/>
		                        <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
		                        </dsp:input>
		                    </c:if>
		                    <c:if test="${flag == 'no'}">
		                      <dsp:input type="checkbox" bean="GiftRegistryFormHandler.emailOptIn_BabyCA" id="emailOptIn_BabyCA" name="emailOptIn_BabyCA" checked="false" iclass="fl">
		                      <dsp:tagAttribute name="aria-hidden" value="false"/>
		                      <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
		                      </dsp:input>
		                    </c:if>
		                </c:otherwise>
		              </c:choose>
		              </dsp:getvalueof>
		                <%-- input name="Opt-In" type="checkbox" value="" checked="checked" id="optIn" class="fl" />--%>
		              <label id="lbloptIn" for="emailOptIn_BabyCA" class="textDgray11 wrapTextAfterCheckBox"><bbbl:label key="lbl_email_optin_baby_canada" language="${pageContext.request.locale.language}"/></label>
		           </div>
	            </c:otherwise>
	            </c:choose>
	            </c:otherwise>
	            </c:choose>
                </div>
                <div class="clear"></div>
</dsp:page>