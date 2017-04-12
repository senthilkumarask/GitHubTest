<dsp:page>
    <dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
    <dsp:importbean bean="/com/bbb/selfservice/StateDroplet" /> 
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/RegistryAddressOrderDroplet"/>
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/POBoxValidateDroplet" />
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />

    <c:set var="BedBathCanadaSiteCode">
        <bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
    </c:set>

    <c:choose>
        <c:when test="${currentSiteId == BedBathCanadaSiteCode}">
            <c:set var="CADate" value="CA" scope="request" />
        </c:when>
        <c:otherwise>
            <c:set var="CADate" value="" scope="request" />
        </c:otherwise>
    </c:choose>
    
    <dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <div class="steps step3 clearfix grid_12 alpha omega">
        <div id="step3Preview" class="grid_12 alpha omega clearfix">
            <div class="grid_12 alpha omega clearfix">
                <div class="clearfix regsitryItem">
					<h3>
						<span class="regsitryItemNumber">3</span>
						<span class="regsitryItemTitle arrowClosedSmall"><bbbl:label key='lbl_regcreate_shipinfo_wheretoship' language="${pageContext.request.locale.language}" /> <a href="#edit3" id="step3EditLink"
                            class="editRegistryLink" title='<bbbl:label key='lbl_regcreate_reginfo_edit' language="${pageContext.request.locale.language}" />'><bbbl:label key='lbl_regcreate_reginfo_edit' language="${pageContext.request.locale.language}" /></a>
						</span>
					</h3>
                </div>
            </div>
            
            <div class="clear"></div>
            <div id="step3Information" class="grid_12 alpha omega stepInformation form clearfix">
                <div class="grid_4 alpha">
                    <div class="entry pushDown">
                        <div class="field"><bbbl:label key="lbl_shipping_current_addr" language ="${pageContext.request.locale.language}"/></div>
                        <div class="value" id="valRegistryCurrentShippingAddress"></div>
                    </div>
                </div>
                <div class="grid_4" id="sectionRegistryFutureShippingAddress">
                    <div class="entry pushDown marBottom_10">
                        <div class="field"><bbbl:label key="lbl_preview_future_shipping_addr" language ="${pageContext.request.locale.language}"/></div>
                        <div class="value" id="valRegistryFutureShippingAddress"></div>
                    </div>
                    <div class="clear"></div>
                    <div class="entry">
                        <div class="field">Future shipping date</div>
                        <div class="value" id="valMovingDate"></div>
                    </div>
                </div>
                <div class="clear"></div>
            </div>
            <div class="clear"></div>
        </div>
        <div class="clear"></div>
        
        <div id="step3Form" class="grid_12 alpha omega clearfix">
            <div id="step3FormPost" class="grid_12 alpha omega form post clearfix padTop_20">
                <div class="grid_8 alpha clearfix">
                    <div class="grid_4 alpha">
                        <div class="borderRight padRight_10">
                            <fieldset>
                                <legend class="formTitle"><bbbl:label key="lbl_shipping_current_addr" language ="${pageContext.request.locale.language}"/></legend>
                                <c:set var="tabIndex" value="1" /> 
                                <%-- Radio Buttons for Shipping Addresses : Address book--%>
                                <%@ include file="frags/registry_shipping_address_book.jsp" %>
                            </fieldset>
                        </div>
                        <div class="clear"></div>
                    </div>
                    <div class="grid_4 alpha">
                        <fieldset>
                            <legend class="formTitle"><bbbl:label key="lbl_preview_future_shipping_addr" language ="${pageContext.request.locale.language}"/></legend>
                            <div class="registryForm">
                                <div class="checkboxItem input clearfix">
                                    <div class="checkbox">
                                        <c:choose>
                                            <c:when test="${null eq regVO}">
                                                <c:set var="futShippingSelected" value="false"/>
                                            </c:when>
                                            <c:when test="${null eq regVO.shipping.futureShippingDate}">
                                                <c:set var="futShippingSelected" value="false"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="futShippingSelected" value="true"/>
                                            </c:otherwise>
                                        </c:choose>
                                        <dsp:input tabindex="${tabIndex}" id="cbFutureAddress" type="checkbox"
                                            name="cbFutureAddressName" bean="GiftRegistryFormHandler.futureShippingDateSelected" value="futureShippingDateSelected"
                                            checked="${futShippingSelected}">
                                            <dsp:tagAttribute name="aria-checked" value="false"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblcbFutureAddress"/>
                                        </dsp:input>
                                        <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                                    </div>
                                    <div class="label grid_3 alpha">
                                        <label id="lblcbFutureAddress" for="cbFutureAddress"><bbbl:label key="lbl_shipping_future_addr" language ="${pageContext.request.locale.language}"/></label>
                                        <div class="subtext"><bbbt:textArea key='txt_regcreate_shipinfo_enterinfo' language="${pageContext.request.locale.language}" /></div>
                                        <div class="clear"></div>
                                        <div id="future-moving-date">
                                            <div class="inputField">
                                                <div class="label clearfix">
                                                    <label id="lbltxtMovingDate${CADate}" for="txtMovingDate"> 
                                                        <bbbl:label key="lbl_shipping_date" language ="${pageContext.request.locale.language}"/>
                                                        <span class="required">*</span>
                                                    </label>
                                                </div>
                                                <div class="clear"></div>
                                                <div class="text grid_3 alpha omega clearfix">
                                                    <div class="grid_2 alpha clearfix">
                                                        <dsp:input tabindex="${tabIndex}" id="txtMovingDate${CADate}" type="text"
                                                            name="txtMovingDateName" bean="GiftRegistryFormHandler.registryVO.shipping.futureShippingDate" value="${regVO.shipping.futureShippingDate}" iclass="requiredValidation" >
                                                            <dsp:tagAttribute name="aria-checked" value="true"/>
                                                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtMovingDate${CADate} errortxtMovingDate${CADate}"/>
                                                        </dsp:input>
                                                        <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                                                    </div>
                                                    <div class="grid_1 alpha omega clearfix">
                                                        <div id="MovingDateButton" class="calendar"></div>
                                                    </div>
                                                    <div class="clear"></div>
                                                </div>
                                                <div class="clear"></div>
                                            </div>
                                            <div class="clear"></div>
                                        </div>
                                        <div class="clear"></div>
                                    </div>
                                    <div class="clear"></div>
                                </div>
                                <div class="clear"></div>

                                <%-- Future Shipping Address - Address book --%>
                                <%@ include file="frags/registry_future_ship_address_book.jsp" %>
                            </div>
                            <div class="clear"></div>
                        </fieldset>
                    </div>
                </div>
                <div class="grid_4 omega clearfix padTop_20">
                    <div class="registryNotice">
                        <bbbt:textArea key="txt_reg_create_shipping_form" language="${pageContext.request.locale.language}"/>
                    </div>
                </div>
                <div class="clear"></div>
                <dsp:getvalueof bean="SessionBean.registryTypesEvent" id="event"/>
               
                    <c:set var="ShpLocStepOn" scope="request"><tpsw:switch tagName="ShoppingLocationStepTag_baby"/></c:set>
                    <c:if test="${regVO == null}">
                     <div class="input submit small pushDown">
                        <c:choose>
                            <c:when test="${(event ne 'BRD'&& event ne 'COM')}">
                                <c:choose>
                                    <c:when test="${event eq 'BA1' && ShpLocStepOn == 'TRUE'}">     
                                        <div class="button">
                                            <input tabindex="${tabIndex}" 
                                                type="button" value="Next"  
                                                id="step3FormPostButton" role="button" aria-pressed="false" aria-labelledby="step3FormPostButton" />
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                    <dsp:input type="hidden" name="favStoreId" bean="GiftRegistryFormHandler.registryVO.prefStoreNum" value=""/>
  									<dsp:input type="hidden" name="contactMethod" bean="GiftRegistryFormHandler.registryVO.refStoreContactMethod"  value="" />
  									<dsp:input type="hidden" name="emailPromotions"  bean="GiftRegistryFormHandler.registryVO.optInWeddingOrBump" value="" />
                                        <div class="button button_active button_active_orange">
											<%--Client DOM XSRF | Part -1
											  dsp:input bean="GiftRegistryFormHandler.registryCreationSuccessURL" type="hidden" value="${contextPath}/giftregistry/registry_creation_confirmation.jsp"/>
											<dsp:input bean="GiftRegistryFormHandler.registryCreationErrorURL" type="hidden" value="${contextPath}/giftregistry/simpleReg_creation_form.jsp"/>--%>
                                           <dsp:input bean="GiftRegistryFormHandler.fromPage" type="hidden" value="registryCreationConfirm"/> 
                                            <dsp:input tabindex="${tabIndex}"  bean="GiftRegistryFormHandler.createRegistry"
                                                type="submit" value="Create Registry"  
                                                id="step3FormPostButton" onclick="_gaq.push(['_trackEvent', 'bridal', 'click go', 'create']);createRegistry('3')" priority="-9999" iclass="createRegistrySubmit">
                                                <dsp:tagAttribute name="aria-pressed" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="step3FormPostButton"/>
                                                <dsp:tagAttribute name="role" value="button"/>
                                            </dsp:input>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:otherwise>
                               <div class="button">
                                    <input tabindex="${tabIndex}" 
                                        type="button" value="Next"  
                                        id="step3FormPostButton" role="button" aria-pressed="false" aria-labelledby="step3FormPostButton" />
                                </div>
                            </c:otherwise>
                        </c:choose>
                        <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                                            <div class="clear"></div>
                </div>
                    </c:if>
             <%--       <c:if test="${regVO != null}">
                        <c:choose>
                            <c:when test="${currentSiteId eq BedBathCanadaSiteCode ||(event ne 'BRD'&& event ne 'COM')}">
                                <c:choose>
                                    <c:when test="${currentSiteId ne BedBathCanadaSiteCode && event eq 'BA1' && ShpLocStepOn == 'TRUE'}">
                                        <div class="button">
                                            <dsp:input bean="GiftRegistryFormHandler.updateRegistry"
                                                type="button" value="Next" 
                                                id="step3FormPostButton"/>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="button button_active button_active_orange">
                                            <dsp:input bean="GiftRegistryFormHandler.updateRegistry"
                                                type="submit" value="Update Registry" 
                                                id="step3FormPostButton" onclick="_gaq.push(['_trackEvent', 'bridal', 'click go', 'update']); createRegistry('3')"/>
                                        </div>
                                    </c:otherwise>
                                 </c:choose>
                                         
                            </c:when>
                            <c:otherwise>
                                <div class="button">
                                    <dsp:input bean="GiftRegistryFormHandler.updateRegistry"
                                        type="button" value="Next" 
                                        id="step3FormPostButton"/>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </c:if> --%>

                <div class="clear"></div>
            </div>
            <div class="clear"></div>
        </div>
        <div class="clear"></div>
        
     </div>
    <div class="clear"></div>
</dsp:page>