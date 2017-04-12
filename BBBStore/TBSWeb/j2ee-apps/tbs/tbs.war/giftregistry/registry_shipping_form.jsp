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

    <c:set var="TBS_BedBathCanadaSiteCode">
        <bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
    </c:set>

    <c:choose>
        <c:when test="${currentSiteId == TBS_BedBathCanadaSiteCode}">
            <c:set var="CADate" value="CA" scope="request" />
        </c:when>
        <c:otherwise>
            <c:set var="CADate" value="" scope="request" />
        </c:otherwise>
    </c:choose>
    
    <dsp:importbean bean="/atg/userprofiling/Profile" />
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <div class="steps step3">
        <div id="step3Preview" class="row ">
                <div class="regsitryItem">
                    <h3>
                        <span class="regsitryItemNumber">3</span>
                        <span class="regsitryItemTitle arrowClosedSmall"><bbbl:label key='lbl_regcreate_shipinfo_wheretoship' language="${pageContext.request.locale.language}" /> <a href="#edit3" id="step3EditLink"
                            class="editRegistryLink" title='<bbbl:label key='lbl_regcreate_reginfo_edit' language="${pageContext.request.locale.language}" />'><bbbl:label key='lbl_regcreate_reginfo_edit' language="${pageContext.request.locale.language}" /></a>
                        </span>
                    </h3>
                </div>
            
            
            <div id="step3Information" class="alpha omega stepInformation form ">
                <div class="small-6 columns">
                    <div class="entry pushDown">
                        <div class="field"><bbbl:label key="lbl_shipping_current_addr" language ="${pageContext.request.locale.language}"/></div>
                        <div class="value" id="valRegistryCurrentShippingAddress"></div>
                    </div>
                </div>
                <div class="small-6 columns" id="sectionRegistryFutureShippingAddress">
                    <div class="entry pushDown marBottom_10">
                        <div class="field"><bbbl:label key="lbl_preview_future_shipping_addr" language ="${pageContext.request.locale.language}"/></div>
                        <div class="value" id="valRegistryFutureShippingAddress"></div>
                    </div>
                    
                    <div class="entry">
                        <div class="field">Future shipping date</div>
                        <div class="value" id="valMovingDate"></div>
                    </div>
                </div>
                
            </div>
            
        </div>
        
        
        <div id="step3Form" class="grid_12 alpha omega ">
            <div id="step3FormPost" class="row alpha omega form post  padTop_20">
                <div class="large-8 columns alpha ">
                    <div class="grid_4 alpha">
                        <div class="borderRight padRight_10">
<%--                                 <legend class="formTitle"><bbbl:label key="lbl_shipping_current_addr" language ="${pageContext.request.locale.language}"/></legend>
                                <c:set var="tabIndex" value="1" /> 
 --%>                                <%-- Radio Buttons for Shipping Addresses : Address book--%>
                                <%@ include file="frags/registry_shipping_address_book.jsp" %>
                        </div>
                        
                    </div>
                    <div class="grid_4 alpha">
                        <fieldset>
                            <legend class="formTitle"><bbbl:label key="lbl_preview_future_shipping_addr" language ="${pageContext.request.locale.language}"/></legend>
                            <div class="registryForm">
                                <div class="clearfix checkboxItem input ">
                                    <div class="small-1 large-1 columns checkbox">
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
                                   
                                    <div class="label small-11 columns alpha">
                                        <label id="lblcbFutureAddress" for="cbFutureAddress"><bbbl:label key="lbl_shipping_future_addr" language ="${pageContext.request.locale.language}"/></label>
                                        <div class="subtext"><bbbt:textArea key='txt_regcreate_shipinfo_enterinfo' language="${pageContext.request.locale.language}" /></div>
                                        
                                        <div id="future-moving-date">
                                            <div class="inputField">
                                                <div class="small-6 columns no-padding-left label ">
                                                    <label id="lbltxtMovingDate${CADate}" for="txtMovingDate"> 
                                                        <bbbl:label key="lbl_shipping_date" language ="${pageContext.request.locale.language}"/>
                                                        <span class="required">*</span>
                                                    </label>
                                                    <br>
                                                </div>
                                                
                                                <div class="text small-6 columns end">
                                                    <div class="grid_2 alpha ">
                                                        <dsp:input tabindex="${tabIndex}" id="txtMovingDate${CADate}" type="text" 
                                                            name="txtMovingDateName" bean="GiftRegistryFormHandler.registryVO.shipping.futureShippingDate" value="${regVO.shipping.futureShippingDate}" iclass="requiredValidation" >
                                                            <dsp:tagAttribute name="aria-checked" value="true"/>
                                                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtMovingDate${CADate} errortxtMovingDate${CADate}"/>
                                                        </dsp:input>
                                                        <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                                                    </div>
                                                    <div class="grid_1 alpha omega ">
                                                        <div id="MovingDateButton" class="calendar"></div>
                                                    </div>
                                                    
                                                </div>
                                                
                                            </div>
                                            
                                        </div>
                                        
                                    </div>
                                    
                                </div>
                                

                                <%-- Future Shipping Address - Address book --%>
                                <%@ include file="frags/registry_future_ship_address_book.jsp" %>
                            </div>
                            
                        </fieldset>
                    </div>
                </div>
                <div class="large-4 columns  omega  padTop_20">
                    <div class="registryNotice">
                        <bbbt:textArea key="txt_reg_create_shipping_form" language="${pageContext.request.locale.language}"/>
                    </div>
                </div>
                
                <dsp:getvalueof bean="SessionBean.registryTypesEvent" id="event"/>
               
                    <c:set var="ShpLocStepOn" scope="request"><tpsw:switch tagName="ShoppingLocationStepTag_baby"/></c:set>
                    <c:if test="${regVO == null}">
				<div class="row"> 
					<div class="small-12 columns">
					<div class="small-12 large-3 columns input submit small pushDown">
                        <c:choose>
                            <c:when test="${currentSiteId eq TBS_BedBathCanadaSiteCode ||(event ne 'BRD'&& event ne 'COM')}">
                                <c:choose>
                                    <c:when test="${currentSiteId ne TBS_BedBathCanadaSiteCode && event eq 'BA1' && ShpLocStepOn == 'TRUE'}">     
                                            <input tabindex="${tabIndex}"  class="small button service expand"
                                                type="button" value="Next"  
                                                id="step3FormPostButton" role="button" aria-pressed="false" aria-labelledby="step3FormPostButton" />
                                    </c:when>
                                    <c:otherwise>
                                    <dsp:input type="hidden" name="favStoreId" bean="GiftRegistryFormHandler.registryVO.prefStoreNum" value=""/>
                                    <dsp:input type="hidden" name="contactMethod" bean="GiftRegistryFormHandler.registryVO.refStoreContactMethod"  value="" />
                                    <dsp:input type="hidden" name="emailPromotions"  bean="GiftRegistryFormHandler.registryVO.optInWeddingOrBump" value="" />
                                        <div class="button_active button_active_orange">
                                            <dsp:input tabindex="${tabIndex}"  bean="GiftRegistryFormHandler.createRegistry"
                                                type="submit" value="Create Registry"  iclass="small button transactional expand"
                                                id="step3FormPostButton" onclick="_gaq.push(['_trackEvent', 'bridal', 'click go', 'create']);createRegistry('3')" priority="-9999" >
                                                <dsp:tagAttribute name="aria-pressed" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="step3FormPostButton"/>
                                                <dsp:tagAttribute name="role" value="button"/>
                                            </dsp:input>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:otherwise>
                                    <input tabindex="${tabIndex}" class="small button service expand"
                                        type="button" value="Next"  
                                        id="step3FormPostButton" role="button" aria-pressed="false" aria-labelledby="step3FormPostButton" />
                            </c:otherwise>
                        </c:choose>
                        <c:set var="tabIndex" value="${tabIndex + 1}" /> 
                </div>
				</div>
                    </c:if>
             <%--       <c:if test="${regVO != null}">
                        <c:choose>
                            <c:when test="${currentSiteId eq TBS_BedBathCanadaSiteCode ||(event ne 'BRD'&& event ne 'COM')}">
                                <c:choose>
                                    <c:when test="${currentSiteId ne TBS_BedBathCanadaSiteCode && event eq 'BA1' && ShpLocStepOn == 'TRUE'}">
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
					</div>
            </div>
        </div>
        <hr/>
     </div>
</dsp:page>