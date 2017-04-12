<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:getvalueof var="currentSiteId" bean="/atg/multisite/Site.id" />
	 <dsp:getvalueof bean="GiftRegistryFormHandler.value.receiveEmail" id="flag"></dsp:getvalueof>
	
	
	<div class="row">
									<div class="large-12 columns emailOffer addressFields ">
										<label class="inline-rc checkbox" for="exclusiveEmailOffer"> 
											<!-- <input  id="exclusiveEmailOffer"
											aria-labelledby="lblexclusiveEmailOffer"
											name="exclusiveEmailOffer" value="exclusiveEmailOffer"
											type="checkbox" checked="checked" style="opacity: 0;"> -->
											 <c:choose>
                <c:when test="${currentSiteId eq 'TBS_BedBathCanada'}">
                    <dsp:input type="checkbox" bean="GiftRegistryFormHandler.emailOptIn" id="exclusiveEmailOffer" name="exclusiveEmailOffer" checked="false" >
                    <dsp:tagAttribute name="aria-labelledby" value="lblexclusiveEmailOffer"/>
                    </dsp:input>
                </c:when>
                <c:otherwise>
                    <c:if test="${flag == 'yes'}">
                        <dsp:input type="checkbox" bean="GiftRegistryFormHandler.emailOptIn" id="exclusiveEmailOffer" name="exclusiveEmailOffer" checked="true" value="true" >
                        <dsp:tagAttribute name="aria-checked" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblexclusiveEmailOffer"/>
                        </dsp:input>
                    </c:if>
                    <c:if test="${flag == 'no'}">
                      <dsp:input type="checkbox" bean="GiftRegistryFormHandler.emailOptIn" id="exclusiveEmailOffer" name="exclusiveEmailOffer" checked="false" >
                       
                      <dsp:tagAttribute name="aria-labelledby" value="lblexclusiveEmailOffer"/>
                      </dsp:input>
                    </c:if>
                </c:otherwise>
              </c:choose>

											<span class="checkBoxRegistry checked"></span> 
											<bbbt:textArea
												key="txt_exclusiveEmailOffer"
												language="${pageContext.request.locale.language}" />
										</label>
									</div>
								</div> 
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	                     <%--Adding code for email optIn --%>
	  <%--   <div class="row padBottom_10">
									<div class="large-12 columns emailOffer addressFields ">
									<label class="inline-rc checkbox" for="exclusiveEmailOffer"> 
											<span></span>
	    <dsp:getvalueof bean="GiftRegistryFormHandler.value.receiveEmail" id="flag">
              <c:choose>
                <c:when test="${currentSiteId eq BedBathCanadaSite}">
                    <dsp:input type="checkbox" bean="GiftRegistryFormHandler.emailOptIn" id="exclusiveEmailOffer" name="exclusiveEmailOffer" checked="false" >
                     
                    <dsp:tagAttribute name="aria-labelledby" value="lblexclusiveEmailOffer"/>
                    </dsp:input>
                </c:when>
                <c:otherwise>
                    <c:if test="${flag == 'yes'}">
                        <dsp:input type="checkbox" bean="GiftRegistryFormHandler.emailOptIn" id="exclusiveEmailOffer" name="exclusiveEmailOffer" checked="true" value="true" >
                        <dsp:tagAttribute name="aria-checked" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblexclusiveEmailOffer"/>
                        </dsp:input>
                    </c:if>
                    <c:if test="${flag == 'no'}">
                      <dsp:input type="checkbox" bean="GiftRegistryFormHandler.emailOptIn" id="exclusiveEmailOffer" name="exclusiveEmailOffer" checked="false" >
                       
                      <dsp:tagAttribute name="aria-labelledby" value="lblexclusiveEmailOffer"/>
                      </dsp:input>
                    </c:if>
                </c:otherwise>
              </c:choose>
              </dsp:getvalueof>
	                     
	  
										 <bbbt:textArea
												key="txt_exclusiveEmailOffer"
												language="${pageContext.request.locale.language}" />
										</label> --%>
											<%-- <input id="exclusiveEmailOffer"
											aria-labelledby="lblexclusiveEmailOffer"
											name="exclusiveEmailOffer" value="exclusiveEmailOffer"
											type="checkbox" checked="checked" style="opacity: 0;">
											
											<dsp:getvalueof var="babyCA" bean="SessionBean.babyCA"/> --%>
            <%-- Baby Canada --%>
	            <c:if test="${currentSiteId eq BedBathCanadaSite}">
		              <dsp:getvalueof bean="PropertyManager.receiveEmailPropertyName" id="flag">
		              <c:choose>
		                <c:when test="${currentSiteId eq BedBathCanadaSite}">
		                    <dsp:input type="checkbox" bean="GiftRegistryFormHandler.emailOptIn_BabyCA" id="emailOptIn_BabyCA" name="emailOptIn_BabyCA" checked="false" iclass="fl">
		                     
		                    <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
		                    </dsp:input>
		                </c:when>
		                <c:otherwise>
		                    <c:if test="${flag == 'yes'}">
		                        <dsp:input type="checkbox" bean="GiftRegistryFormHandler.emailOptIn_BabyCA" id="emailOptIn_BabyCA" name="emailOptIn_BabyCA" checked="true" value="true" iclass="fl">
		                        <dsp:tagAttribute name="aria-checked" value="true"/>
		                        <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
		                        </dsp:input>
		                    </c:if>
		                    <c:if test="${flag == 'no'}">
		                      <dsp:input type="checkbox" bean="GiftRegistryFormHandler.emailOptIn_BabyCA" id="emailOptIn_BabyCA" name="emailOptIn_BabyCA" checked="false" iclass="fl">
		                       
		                      <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
		                      </dsp:input>
		                    </c:if>
		                </c:otherwise>
		              </c:choose>
		              </dsp:getvalueof>
		                <%-- input name="Opt-In" type="checkbox" value="" checked="checked" id="optIn" class="fl" />--%>
		              <label id="lbloptIn" for="emailOptIn_BabyCA" class="textDgray11 wrapTextAfterCheckBox"><bbbl:label key="lbl_email_optin_baby_canada" language="${pageContext.request.locale.language}"/></label>
	            </c:if>
											
											
	                     
	                     
	                     
	                     
	                     
	                     
	                     
	                     
	                     
	                     
	                     
	                     
               <%-- <div class="grid_4 alpha omega clearfix marTop_20">
				<div class="optIn">
              <dsp:getvalueof bean="GiftRegistryFormHandler.value.receiveEmail" id="flag">
              <c:choose>
                <c:when test="${currentSiteId eq BedBathCanadaSite}">
                    <dsp:input type="checkbox" bean="GiftRegistryFormHandler.emailOptIn" id="optIn" name="emailOptIn" checked="false" iclass="fl">
                     
                    <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
                    </dsp:input>
                </c:when>
                <c:otherwise>
                    <c:if test="${flag == 'yes'}">
                        <dsp:input type="checkbox" bean="GiftRegistryFormHandler.emailOptIn" id="optIn" name="emailOptIn" checked="true" value="true" iclass="fl">
                        <dsp:tagAttribute name="aria-checked" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
                        </dsp:input>
                    </c:if>
                    <c:if test="${flag == 'no'}">
                      <dsp:input type="checkbox" bean="GiftRegistryFormHandler.emailOptIn" id="optIn" name="emailOptIn" checked="false" iclass="fl">
                       
                      <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
                      </dsp:input>
                    </c:if>
                </c:otherwise>
              </c:choose>
              </dsp:getvalueof>
              <label id="lbloptIn" for="optIn" class="textDgray11 wrapTextAfterCheckBox emailSubs"><bbbl:label key="lbl_email_optin_bedbath_canada" language="${pageContext.request.locale.language}"/></label>
            </div>
            <dsp:getvalueof var="babyCA" bean="SessionBean.babyCA"/>
            Baby Canada
	            <c:if test="${currentSiteId eq BedBathCanadaSite}">
		            <div class="optIn padTop_10">
		              <dsp:getvalueof bean="PropertyManager.receiveEmailPropertyName" id="flag">
		              <c:choose>
		                <c:when test="${currentSiteId eq BedBathCanadaSite}">
		                    <dsp:input type="checkbox" bean="GiftRegistryFormHandler.emailOptIn_BabyCA" id="emailOptIn_BabyCA" name="emailOptIn_BabyCA" checked="false" iclass="fl">
		                     
		                    <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
		                    </dsp:input>
		                </c:when>
		                <c:otherwise>
		                    <c:if test="${flag == 'yes'}">
		                        <dsp:input type="checkbox" bean="GiftRegistryFormHandler.emailOptIn_BabyCA" id="emailOptIn_BabyCA" name="emailOptIn_BabyCA" checked="true" value="true" iclass="fl">
		                        <dsp:tagAttribute name="aria-checked" value="true"/>
		                        <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
		                        </dsp:input>
		                    </c:if>
		                    <c:if test="${flag == 'no'}">
		                      <dsp:input type="checkbox" bean="GiftRegistryFormHandler.emailOptIn_BabyCA" id="emailOptIn_BabyCA" name="emailOptIn_BabyCA" checked="false" iclass="fl">
		                       
		                      <dsp:tagAttribute name="aria-labelledby" value="lbloptIn erroroptIn"/>
		                      </dsp:input>
		                    </c:if>
		                </c:otherwise>
		              </c:choose>
		              </dsp:getvalueof>
		                input name="Opt-In" type="checkbox" value="" checked="checked" id="optIn" class="fl" />
		              <label id="lbloptIn" for="emailOptIn_BabyCA" class="textDgray11 wrapTextAfterCheckBox"><bbbl:label key="lbl_email_optin_baby_canada" language="${pageContext.request.locale.language}"/></label>
		           </div>
	            </c:if>
                </div>
                <div class="clear"></div> --%>
</dsp:page>