<dsp:page>
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <dsp:importbean bean="/com/bbb/account/droplet/BBBConfigKeysDroplet" />
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
    <dsp:importbean bean="/atg/userprofiling/Profile"/>
    <dsp:include page="/_includes/third_party_on_of_tags.jsp"/>
    <dsp:getvalueof var="step" param="step"/>
    <dsp:getvalueof var="link" param="link"/>
    <dsp:getvalueof var="isTransient" bean="Profile.transient"/>
    <dsp:getvalueof id="siteId" idtype="java.lang.String" param="siteId" />
    
     <c:choose>
            <c:when test="${siteId == 'BedBathCanada'}">
                <c:set var="CADate" value="CA" scope="request" />
            </c:when>
            <c:otherwise>
                <c:set var="CADate" value="" scope="request" />
            </c:otherwise>
        </c:choose>
            <div class="grid_2 alpha clearfix">
              <div class="simpleRegForm">
                <dsp:getvalueof var="inputListMap" param="inputListMap"/>
                  <c:if test="${inputListMap['showerDate'].isDisplayonForm}">
                    <c:set var="isRequired" value=""></c:set> 
									   <c:if test="${inputListMap['showerDate'].isMandatoryOnCreate}">
													 <c:set var="isRequired" value="required"></c:set>
													 </c:if>
													  <c:set var="datePlaceHolder"><bbbl:label key='lbl_reg_ph_date' language ='${pageContext.request.locale.language}'/></c:set>
                                <div id="eventflex1" class="inputField clearfix grid_2 alpha omega" id="eventDate" >
                                    <div class="label grid_3 alpha">
                				<label id="lbltxtRegistryExpectedDateShower${CADate}" for="txtRegistryShowerDate"><bbbl:label key='lbl_reg_shower_date'
										language='${pageContext.request.locale.language}' /></label>
                                    </div>
                                    <div class="text grid_2 alpha">
                                        <div class="omega" aria-live="assertive">
                                            <dsp:input  
                                                bean="GiftRegistryFormHandler.registryVO.event.showerDate"
                                                type="text" id="txtRegistryShowerDate${CADate}"  name="simpletxtShowerDate${CADate}"  iclass="${isRequired} step1FocusField">
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtRegistryExpectedDateShower${CADate} errortxtRegistryExpectedDate${CADate}"/>
                                                 <dsp:tagAttribute name="placeholder" value="${datePlaceHolder}"/>
                                            </dsp:input>
                                              <div id="txtRegistryShowerDateButton${CADate}" class="calendericon">
                                              <span class="icon-fallback-text"><span class="icon-calendar" aria-hidden="true"></span><span class="icon-text"></span></span>
                                        </div>
                                    </div>
                                </div>
                                </div>
                               
                            </c:if> 
                            </div>
                    </div>
                 
</dsp:page>