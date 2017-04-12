<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<div class="grid_6 omega findCreateReg">
    <div class="grid_3 omega">
        <div class="findARegistryFormTitle">
            <h2><bbbt:textArea key="txt_find_registry_title" language ="${pageContext.request.locale.language}"/></h2>
            <p><bbbt:textArea key="txt_find_registry_desc" language ="${pageContext.request.locale.language}"/></p>
        </div>
        <dsp:form id="findRegHome" action="${contextPath}/giftregistry/registry_search_guest.jsp"  method="post" requiresSessionConfirmation="false">
            <div class="grid_2 omega alpha">
                
                <div class="inputField">
					<c:set var="titleFirstName">
						<bbbl:label key="lbl_registrants_firstname" language="${pageContext.request.locale.language}" />
					</c:set>
                    <label id="lblfirstNameReg" for="firstNameReg" title="${titleFirstName}"><bbbl:label key="lbl_registrants_firstname" language="${pageContext.request.locale.language}" /></label>
                    <dsp:input type="text" name="firstNameReg" title="${titleFirstName}" value="" id="firstNameReg" bean="GiftRegistryFormHandler.registrySearchVO.firstName" >
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lblfirstNameReg errorfirstNameReg"/>
                    </dsp:input>
                </div>
                <label id="errorfirstNameReg" for="firstNameReg" generated="true" class="error" style="display: block"></label>
            </div>
            <div class="grid_2 omega alpha">
                <div class="inputField">
					<c:set var="titleLastName">
						<bbbl:label key="lbl_registrants_lastname" language="${pageContext.request.locale.language}" />
					</c:set>
                    <label id="lbllastNameReg" for="lastNameReg" title="${titleLastName}"><bbbl:label key="lbl_registrants_lastname" language="${pageContext.request.locale.language}" /></label>
                    <dsp:input type="text" name="lastNameReg" title="${titleLastName}" value="" id="lastNameReg" bean="GiftRegistryFormHandler.registrySearchVO.lastName" >
                        <dsp:tagAttribute name="aria-required" value="true"/>
                        <dsp:tagAttribute name="aria-labelledby" value="lbllastNameReg errorlastNameReg"/>
                    </dsp:input>
                </div>
                <label id="errorlastNameReg" for="lastNameReg" generated="true" class="error block"></label>
            </div>
            <dsp:input bean="GiftRegistryFormHandler.hidden" type="hidden" value="1" />
            <div class="grid_1 butGo">
                
                <div class="input submit clearfix">
                    <c:set var="lbl_look_reg_submit_button">
                        <bbbl:label key='lbl_look_reg_submit_button' language='${pageContext.request.locale.language}' />
                    </c:set>
                    <div class="button">
                        <dsp:input type="submit" value="${lbl_look_reg_submit_button}" name="btnFindRegistry" id="btnFindRegistry" bean="GiftRegistryFormHandler.registrySearchFromHomePage" >
                            <dsp:tagAttribute name="aria-pressed" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="btnFindRegistry errorbtnFindRegistry"/>
                        </dsp:input>
                    </div>
                </div>
            </div>
        </dsp:form>
    </div>
    <div class="grid_3 omega">
        <div class="findARegistryFormTitle marRight_10">
            <h2><bbbt:textArea key="txt_create_registry_title" language ="${pageContext.request.locale.language}"/></h2>
            <p><bbbt:textArea key="txt_create_registry_select" language ="${pageContext.request.locale.language}"/></p>
        </div>
            <div id="findARegistrySelectType" class="width_2"><dsp:include page="/giftregistry/my_registry_type_select.jsp" ></dsp:include></div>
    </div>
</div>
</dsp:page>