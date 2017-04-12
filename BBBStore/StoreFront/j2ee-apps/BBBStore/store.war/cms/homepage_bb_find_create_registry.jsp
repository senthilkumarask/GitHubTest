<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
                <div class="grid_6 omega findCreateReg">
                    <div class="grid_3 omega">
                        <div class="findARegistryFormTitle">
                            <h2><bbbt:textArea key="txt_find_registry_title" language ="${pageContext.request.locale.language}"/></h2>
                            <p><bbbt:textArea key="txt_registrants_enter_Info" language ="${pageContext.request.locale.language}"/></p>                            
                        </div>
						<div class="findCreateRegForm">
							<dsp:form id="findRegHome" action="${contextPath}/giftregistry/registry_search_guest.jsp" method="post" requiresSessionConfirmation="false">
								<div class="grid_2 omega alpha marBottom_10"> 
									
									<div class="inputField">
										<c:set var="lbl_registrants_firstname">
											<bbbl:label key="lbl_registrants_firstname" language="${pageContext.request.locale.language}" />
										</c:set>
										<label id="lblfirstNameReg" for="firstNameReg" title="${lbl_registrants_firstname}"><bbbl:label key="lbl_registrants_firstname" language="${pageContext.request.locale.language}" /></label>
										<dsp:input id="firstNameReg" value="" name="firstNameReg" title="${lbl_registrants_firstname}" type="text" 
													bean="GiftRegistryFormHandler.registrySearchVO.firstName">
                                            <dsp:tagAttribute name="placeholder" value="${lbl_registrants_firstname}" />
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblfirstNameReg errorfirstNameReg"/>
										</dsp:input>
									</div>
									<label id="errorfirstNameReg" for="firstNameReg" generated="true" class="error"></label>
								</div>
								<div class="grid_2 omega alpha marBottom_10">
									<div class="inputField">
										<c:set var="lbl_registrants_lastname">
											<bbbl:label key="lbl_registrants_lastname" language="${pageContext.request.locale.language}" />
										</c:set>
										<label id="lbllastNameReg" for="lastNameReg" title="${lbl_registrants_lastname}"><bbbl:label key="lbl_registrants_lastname" language="${pageContext.request.locale.language}" /></label>
										<dsp:input id="lastNameReg" value="" name="lastNameReg" title="${lbl_registrants_lastname}" type="text" 
													bean="GiftRegistryFormHandler.registrySearchVO.lastName">
                                            <dsp:tagAttribute name="placeholder" value="${lbl_registrants_lastname}" />
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lbllastNameReg errorlastNameReg"/>
										</dsp:input>
									</div>
									<label id="errorlastNameReg" for="lastNameReg" generated="true" class="error"></label>
								</div>
								<dsp:input bean="GiftRegistryFormHandler.hidden" type="hidden" value="1" />
								<div class="grid_2 omega alpha"> 
									
									<div class="input submit small">
									<c:set var="lbl_find_reg_submit_button">
										<bbbl:label key='lbl_find_reg_submit_button' language='${pageContext.request.locale.language}' />
									</c:set>
										<div class="button">											
											<dsp:input type="submit" bean="GiftRegistryFormHandler.registrySearchFromHomePage" value="${lbl_find_reg_submit_button}" id="btnFindRegistry" name="btnFindRegistry">
                                               
                                                <dsp:tagAttribute name="aria-labelledby" value="btnFindRegistry"/>
                                                
                                            </dsp:input>
										</div>
									</div>
								</div>
							</dsp:form>
							<div class="clear"></div>
						</div>
                    </div>
                    <div class="grid_3 omega">
                        <div class="findARegistryFormTitle marRight_10">
                            <h2><bbbt:textArea key="txt_create_registry_title" language ="${pageContext.request.locale.language}"/></h2>
                            <p><bbbt:textArea key="txt_create_registry_select" language ="${pageContext.request.locale.language}"/></p>
                        </div>
                                <div id="findARegistrySelectType" class="width_2">
                                    <dsp:include page="/giftregistry/my_registry_type_select.jsp" ></dsp:include>
                                </div>
                    </div>
                </div>

</dsp:page>