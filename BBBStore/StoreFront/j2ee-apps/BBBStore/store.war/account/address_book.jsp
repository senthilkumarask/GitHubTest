<dsp:page>

	<dsp:importbean var="ProfileFormHandler" bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/com/bbb/selfservice/StateDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/commerce/util/MapToArrayDefaultFirst" />
	<dsp:importbean bean="/atg/dynamo/droplet/Compare" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

	<c:set var="section" value="accounts" scope="request" />
	<c:set var="pageWrapper" value="addressBook myAccount useGoogleAddress" scope="request" />
	<c:set var="enter_new_address_heading">
		<bbbl:label key="lbl_enter_new_address_heading" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="edit_address_heading">
		<bbbl:label key="lbl_edit_address_heading" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="update_address_heading">
		<bbbl:label key="lbl_update_address_heading" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="remove_address_heading">
		<bbbl:label key="lbl_remove_address_heading" language="${pageContext.request.locale.language}" />
	</c:set>

	<bbb:pageContainer index="false" follow="false">
		<jsp:attribute name="bodyClass">atg_store_pageHome</jsp:attribute>
		<jsp:attribute name="section">${section}</jsp:attribute>
		<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
		<jsp:body>
    <dsp:getvalueof id="currentSiteId" bean="Site.id" />
   	<c:set var="BedBathCanadaSite">
		<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<dsp:getvalueof var="isOrder" value="true" />
	<dsp:getvalueof var="isRegistry" value="true" />

	<dsp:droplet name="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFlyoutDroplet">
		<dsp:param name="profile" bean="Profile" />
		<dsp:oparam name="output">
		<dsp:droplet name="/atg/dynamo/droplet/Switch">
			<dsp:param name="value" param="userStatus" />
					<dsp:oparam name="2">
						<dsp:getvalueof var="isRegistry" value="false" />
					</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>
	<dsp:droplet name="/atg/commerce/order/OrderLookup">
		<dsp:param name="userId" bean="Profile.id"/>
		<dsp:param name="state" value="open"/>
		<dsp:oparam name="empty">
				<dsp:getvalueof var="isOrder" param="false" />
		</dsp:oparam>
	</dsp:droplet>
     <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
            <div id="content" class="container_12 clearfix" role="main">
                <div class="grid_12">
                    <h1 class="account fl">
						<bbbl:label key="lbl_personalinfo_myaccount" language="${pageContext.request.locale.language}" />
					</h1>
                    <h3 class="subtitle">
						<bbbl:label key="lbl_addressbook_addressbooklabel" language="${pageContext.request.locale.language}" />
					</h3>
                    <div class="clear"></div>
                </div>
                <div class="grid_2">
                    <c:import url="/account/left_nav.jsp">
                  	  <c:param name="currentPage">
							<bbbl:label key="lbl_myaccount_address_book" language="${pageContext.request.locale.language}" />
						</c:param>
                    </c:import>
                </div>
                <div class="grid_8 prefix_1 suffix_1">
                     <div class="suffix_2 descWishList">
                        <p>
							<bbbt:textArea key="txt_addressbook_addressbooktxt" language="${pageContext.request.locale.language}" />
						</p>
                        <p class="txtHighlight">
                         <c:set var="lbl_addressbook_addnewaddress">
																   <bbbl:label key="lbl_addressbook_addnewaddress" language="${pageContext.request.locale.language}" />
																</c:set>
							<a class="addAddressEntry" href="#addEntry" title="${lbl_addressbook_addnewaddress}">+<bbbl:label key="lbl_addressbook_addnewaddress" language="${pageContext.request.locale.language}" />
							</a>
						</p>
                    </div>

                    <dsp:include page="/global/gadgets/errorMessage.jsp">
				      <dsp:param name="formhandler" bean="ProfileFormHandler" />
				    </dsp:include>
				    <c:set var="chkboxdisabled" value="true" />
                     <dsp:droplet name="MapToArrayDefaultFirst">
            			<dsp:param name="defaultId" bean="Profile.shippingAddress.repositoryId" />
            			<dsp:param name="defaultId2" bean="Profile.billingAddress.repositoryId" />
            			<dsp:param name="map" bean="Profile.secondaryAddresses" />
            			<dsp:param name="sortByKeys" value="true" />
            			<dsp:oparam name="output">
            			  <dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray" />
         			    	<c:choose>
         			    	<c:when test="${empty sortedArray}">
									<dsp:setvalue bean="ProfileFormHandler.useShippingAddressAsDefault" value="true" />
									<dsp:setvalue bean="ProfileFormHandler.useBillingAddressAsDefault" value="true" />
									<dsp:setvalue bean="ProfileFormHandler.useMailingAddressAsDefault" value="true" />
									<c:set var="chkboxdisabled" value="true" />
	    			    		</c:when>
            			      <c:otherwise>
	            			  <dsp:form id="cardUpdate" action="address_book.jsp">
	                      		<div id="addressBookEntries" class="cardView clearfix">
	            			 	 <c:set var="counter" value="0" />



	                				<c:set var="chkboxdisabled" value="false" />
	                				<dsp:setvalue bean="ProfileFormHandler.useShippingAddressAsDefault" value="false" />
									<dsp:setvalue bean="ProfileFormHandler.useBillingAddressAsDefault" value="false" />
									<dsp:setvalue bean="ProfileFormHandler.useMailingAddressAsDefault" value="false" />
					                  	<c:set var="idcount" value="0" />
					                  	<c:forEach var="shippingAddress" items="${sortedArray}">

					                  	    <c:set var="idcount" value="${idcount + 1}" />
					                    	<dsp:setvalue param="shippingAddress" value="${shippingAddress}" />
							                    <c:if test="${not empty shippingAddress}">
							                      <c:set var="count" value="0" />

														                      <dsp:droplet name="Compare">
														                        <dsp:param name="obj1" bean="Profile.shippingAddress.repositoryId" />
														                        <dsp:param name="obj2" param="shippingAddress.value.id" />
														                        <dsp:oparam name="equal">
														                          <c:set var="isDefault" value="true" />
														                        </dsp:oparam>
														                        <dsp:oparam name="default">
														                          <c:set var="isDefault" value="false" />
														                        </dsp:oparam>
														                      </dsp:droplet>

							                                                  <dsp:droplet name="Compare">
														                        <dsp:param name="obj1" bean="Profile.billingAddress.repositoryId" />
														                        <dsp:param name="obj2" param="shippingAddress.value.id" />
														                        <dsp:oparam name="equal">
														                          <c:set var="isBillDefault" value="true" />
														                        </dsp:oparam>
														                        <dsp:oparam name="default">
														                          <c:set var="isBillDefault" value="false" />
														                        </dsp:oparam>
														                      </dsp:droplet>
																			  
																			  <dsp:droplet name="Compare">
														                        <dsp:param name="obj1" bean="Profile.mailingAddress.repositoryId" />
														                        <dsp:param name="obj2" param="shippingAddress.value.id" />
														                        <dsp:oparam name="equal">
														                          <c:set var="isMailDefault" value="true" />
														                        </dsp:oparam>
														                        <dsp:oparam name="default">
														                          <c:set var="isMailDefault" value="false" />
														                        </dsp:oparam>
														                      </dsp:droplet>


									            <%-- Display Address Details --%>

									            <div class="grid_4 ${(idcount%2 == 1) ? 'alpha' : 'omega'}">

									            <div id="addressEntry-${idcount}" class="cardEntry ${isDefault? 'isPreferredShipping': ''} ${isBillDefault? 'isPreferredBilling': ''} ${isMailDefault? 'isPreferredMailing': ''} clearfix">
									                <c:set var="counter" value="${counter + 1}" />
									                <div class="grid_2 noMar">
					                                    <div class="item">Shipping</div>
					                                </div>
					                                <div class="grid_2 noMar">
					                                    <div class="item">
					                                    	<c:choose>
									                        	<c:when test="${isDefault}">
									                              <strong><bbbl:label key="lbl_addressbook_preferredaddress" language="${pageContext.request.locale.language}" />
																				</strong>
									                            </c:when>
									                           <c:otherwise>
									                           <c:set var="lbl_addressbook_makepreferredshipping">
																   <bbbl:label key="lbl_addressbook_makepreferredshipping" language="${pageContext.request.locale.language}" />
																</c:set>
									                                 <dsp:a  bean="ProfileFormHandler.defaultShippingAddress" title="${lbl_addressbook_makepreferredshipping}" href="${contextPath}/account/address_book.jsp" paramvalue="shippingAddress.key">
									                            	   <bbbl:label key="lbl_addressbook_makepreferredshipping"  language="${pageContext.request.locale.language}" />
									                            	</dsp:a>
									                           </c:otherwise>
									                	    </c:choose>
					                                   </div>
					                                </div>
					                                <hr />
					                                <div class="grid_2 noMar">
					                                    <div class="item">Billing</div>
					                                </div>
					                                <div class="grid_2 noMar">
					                                    <div class="item">
					                                    	<c:choose>
									                        	<c:when test="${isBillDefault}">
									                               <strong><bbbl:label key="lbl_addressbook_preferredbiiling" language="${pageContext.request.locale.language}" />
																				</strong>
									                            </c:when>
									                           <c:otherwise>
									                            <c:set var="lbl_addcreditcard_preferredbilling">
																     <bbbl:label key="lbl_addcreditcard_preferredbilling" language="${pageContext.request.locale.language}" />
																</c:set>
									                               <dsp:a  bean="ProfileFormHandler.defaultBillingAddress" title="${lbl_addcreditcard_preferredbilling}" href="${contextPath}/account/address_book.jsp" paramvalue="shippingAddress.key">
									                            	  <bbbl:label key="lbl_addcreditcard_preferredbilling" language="${pageContext.request.locale.language}" />
                                                                   </dsp:a>

									                           </c:otherwise>
									                	    </c:choose>
					                                    </div>
					                                </div>
					                                <hr />
													 <div class="grid_2 noMar">
					                                    <div class="item">Mailing</div>
					                                </div>
					                                <div class="grid_2 noMar">
					                                    <div class="item">
					                                    	<c:choose>
									                        	<c:when test="${isMailDefault}">
									                               <strong><bbbl:label key="lbl_addressbook_preferredbiiling" language="${pageContext.request.locale.language}" />
																	</strong>
									                            </c:when>
									                           <c:otherwise>
									                            <c:set var="lbl_addcreditcard_preferredMailing">
									                            <bbbl:label key="lbl_addcreditcard_preferredmailing" language="${pageContext.request.locale.language}" />
																</c:set>
									                               <dsp:a  bean="ProfileFormHandler.defaultMailingAddress" title="${lbl_addcreditcard_preferredMailing}" href="${contextPath}/account/address_book.jsp" paramvalue="shippingAddress.key">
									                               <bbbl:label key="lbl_addcreditcard_preferredmailing" language="${pageContext.request.locale.language}" />
									                            	</dsp:a>

									                           </c:otherwise>
									                	    </c:choose>
					                                    </div>
					                                </div>
					                                <hr />
					                                <div class="grid_4 noMar">
					                                    <dsp:include page="displayAddress.jsp" flush="false">
					                           				<dsp:param name="address" param="shippingAddress.value" />
					                            			<dsp:param name="private" value="false" />
					                            		</dsp:include>
					                                </div>
					                                <div class="grid_4 noMar">
					                                   <div class="item">
					                                     <c:set var="lbl_addressbook_edit">
																   <bbbl:label key="lbl_addressbook_edit" language="${pageContext.request.locale.language}" />
																</c:set>
															<dsp:a iclass="editAddressEntry" href="#editEntry" title="${lbl_addressbook_edit}" paramvalue="shippingAddress.key">
																 <bbbl:label key="lbl_addressbook_edit" language="${pageContext.request.locale.language}" />
															</dsp:a>
															
																
														</div>
					                                </div>
					                                <div class="grid_4 noMar">
					                                 <c:set var="lbl_addressbook_remove">
																   <bbbl:label key="lbl_addressbook_remove" language="${pageContext.request.locale.language}" />
																</c:set>
					                                    <div class="item">
					                    	                 <dsp:a iclass="removeAddressEntry" title="${lbl_addressbook_remove}" href="#removeEntry" paramvalue="shippingAddress.key">
					                        		              <bbbl:label key="lbl_addressbook_remove" language="${pageContext.request.locale.language}" />
					                        		         </dsp:a>
					                                    </div>
					                                </div>
					                                <dsp:input iclass="cardId" name="cardId" type="hidden" bean="ProfileFormHandler.editValue.nickname" paramvalue="shippingAddress.key" />
					                                <dsp:input iclass="defaultShippingAddress" name="defaultShippingAddress" type="hidden" bean="ProfileFormHandler.useShippingAddressAsDefault" value="${isDefault}" />
					                                <dsp:input iclass="defaultBillingAddress" name="defaultBillingAddress" type="hidden" bean="ProfileFormHandler.useBillingAddressAsDefault" value="${isBillDefault}" />
													<dsp:input iclass="defaultMailingAddress" name="defaultMailingAddress" type="hidden" bean="ProfileFormHandler.useMailingAddressAsDefault" value="${isMailDefault}" />
										        </div>
					                          </div>
					                    </c:if>
					                  </c:forEach>
	              			 </div>
	                     	</dsp:form>
                     		 <div class="suffix_2">
                     		  <c:set var="lbl_addressbook_addnewaddress">
																   <bbbl:label key="lbl_addressbook_addnewaddress" language="${pageContext.request.locale.language}" />
																</c:set>
		                        <p class="txtHighlight">
		                        	<a class="addAddressEntry" title="${lbl_addressbook_addnewaddress}" href="#addEntry">+<bbbl:label key="lbl_addressbook_addnewaddress" language="${pageContext.request.locale.language}" />
									</a>
		                        </p>
	                    	</div>
	                    	</c:otherwise>
	                    	</c:choose>
           			 </dsp:oparam>

          		</dsp:droplet> <%-- MapToArrayDefaultFirst (sort saved addresses) --%>






                </div>
            </div>

        <div id="addAddressDialog" title="${enter_new_address_heading}" style="display: none;">

            <dsp:form iclass="form clearfix" id="addAddressDialogForm" formid="addAddressDialogForm" action="address_book.jsp" method="post">

				<div class="container_6 clearfix">
					<div class="grid_3 alpha">
						<div class="inputField clearfix">
							<label id="lbltxtAddressBookAddFirstName" for="txtAddressBookAddFirstName"><bbbl:label key="lbl_addcreditcard_firstname" language="${pageContext.request.locale.language}" /> <span class="required"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}" />
								</span>
								</label>
							<dsp:getvalueof bean="ProfileFormHandler.value.firstName" id="firstName">
								<dsp:input type="text" bean="ProfileFormHandler.editValue.firstName" name="txtAddressBookAddFirstNameName" maxlength="30" id="txtAddressBookAddFirstName" value="${firstName}">
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookAddFirstName errortxtAddressBookAddFirstName"/>
                                </dsp:input>
							</dsp:getvalueof>
                            <input type="hidden" name="hiddentxtAddressBookAddFirstNameName" value="${firstName}" data-prefill-elem="txtAddressBookAddFirstName">
						</div>
						<div class="inputField clearfix">
							<label id="lbltxtAddressBookAddLastName" for="txtAddressBookAddLastName"><bbbl:label key="lbl_addcreditcard_lastname" language="${pageContext.request.locale.language}" /> <span class="required"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}" />
								</span>
								</label>
							<dsp:getvalueof bean="ProfileFormHandler.value.lastName" id="lastName">
								<dsp:input type="text" bean="ProfileFormHandler.editValue.lastName" name="txtAddressBookAddLastNameName" maxlength="30" id="txtAddressBookAddLastName" value="${lastName}">
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookAddLastName errortxtAddressBookAddLastName"/>
                                </dsp:input>
							</dsp:getvalueof>
                            <input type="hidden" name="hiddentxtAddressBookAddLastNameName" value="${lastName}" data-prefill-elem="txtAddressBookAddLastName">
						</div>
						<div class="inputField clearfix">
							<label id="lbltxtAddressBookAddCompany" for="txtAddressBookAddCompany"><bbbl:label key="lbl_addcreditcard_company" language="${pageContext.request.locale.language}" />
								</label>
							<dsp:input type="text" bean="ProfileFormHandler.editValue.companyName" name="txtAddressBookAddCompanyName" maxlength="20" value="" id="txtAddressBookAddCompany" >
                            <dsp:tagAttribute name="aria-required" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookAddCompany errortxtAddressBookAddCompany"/>
                            </dsp:input>
						</div>
						<div class="checkboxField clearfix">
						    <dsp:input type="checkbox" bean="ProfileFormHandler.useShippingAddressAsDefault" name="cbAddressBookAddPreferredShipping" id="cbAddressBookAddPreferredShipping" disabled="${chkboxdisabled}">
                            <dsp:tagAttribute name="aria-checked" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblcbAddressBookAddPreferredShipping errorcbAddressBookAddPreferredShipping"/>
                            </dsp:input>
						    <c:if test="${chkboxdisabled == true}">
						    	<dsp:input type="hidden" bean="ProfileFormHandler.useShippingAddressAsDefault" name="cbAddressBookAddPreferredShipping" id="cbAddressBookAddPreferredShipping" />
						    </c:if>
						 	<label id="lblcbAddressBookAddPreferredShipping" for="cbAddressBookAddPreferredShipping"><bbbl:label key="lbl_profile_makePreferredShipAddress" language="${pageContext.request.locale.language}" />
								</label>
						</div>
						<div class="checkboxField clearfix">
						    <dsp:input type="checkbox" bean="ProfileFormHandler.useBillingAddressAsDefault" name="cbAddressBookAddPreferredBilling" id="cbAddressBookAddPreferredBilling" disabled="${chkboxdisabled}" >
                            <dsp:tagAttribute name="aria-checked" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblcbAddressBookAddPreferredBilling errorcbAddressBookAddPreferredBilling"/>
                            </dsp:input>
   						    <c:if test="${chkboxdisabled == true}">
						    	<dsp:input type="hidden" bean="ProfileFormHandler.useBillingAddressAsDefault" name="cbAddressBookAddPreferredBilling" id="cbAddressBookAddPreferredBilling" />
						    </c:if>
						 	<label id="lblcbAddressBookAddPreferredBilling" for="cbAddressBookAddPreferredBilling"><bbbl:label key="lbl_addcreditcard_preferredbilling" language="${pageContext.request.locale.language}" />
								</label>
						</div>
						
						<div class="checkboxField clearfix">
						    <dsp:input type="checkbox" bean="ProfileFormHandler.useMailingAddressAsDefault" name="cbAddressBookAddPreferredMailing" id="cbAddressBookAddPreferredMailing" disabled="${chkboxdisabled}" >
                            <dsp:tagAttribute name="aria-checked" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblcbAddressBookAddPreferredMailing errorcbAddressBookAddPreferredMailing"/>
                            </dsp:input>
   						    <c:if test="${chkboxdisabled == true}">
						    	<dsp:input type="hidden" bean="ProfileFormHandler.useMailingAddressAsDefault" name="cbAddressBookAddPreferredMailing" id="cbAddressBookAddPreferredMailing" />
						    </c:if>
						 	<label id="lblcbAddressBookAddPreferredMailing" for="cbAddressBookAddPreferredMailing"><bbbl:label key="lbl_preferred_mail_address" language="${pageContext.request.locale.language}" />
								</label>
						</div>
					</div>
					<div class="grid_3 omega">
						<div class="inputField clearfix">
							<label id="lbltxtAddressBookAddAddress1" for="txtAddressBookAddAddress1"><bbbl:label key="lbl_addcreditcard_address1" language="${pageContext.request.locale.language}" /> <span class="required"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}" />
								</span>
								</label>
							<dsp:input type="text" bean="ProfileFormHandler.editValue.address1" name="txtAddressBookAddAddress1Name" maxlength="30" value="" id="txtAddressBookAddAddress1" >
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookAddAddress1 errortxtAddressBookAddAddress1"/>
							<dsp:tagAttribute name="autocomplete" value="off"/>
                            </dsp:input>
						</div>
						<div class="inputField clearfix">
							<label id="lbltxtAddressBookAddAddress2" for="txtAddressBookAddAddress2"><bbbl:label key="lbl_addcreditcard_address2" language="${pageContext.request.locale.language}" />
								</label>
							<dsp:input type="text" bean="ProfileFormHandler.editValue.address2" name="txtAddressBookAddAddress2Name" maxlength="30" value="" id="txtAddressBookAddAddress2" >
                            <dsp:tagAttribute name="aria-required" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookAddAddress2 errortxtAddressBookAddAddress2"/>
                            </dsp:input>
						</div>
						<div class="inputField clearfix">
							<label id="lbltxtAddressBookAddCity" for="txtAddressBookAddCity"><bbbl:label key="lbl_addcreditcard_city" language="${pageContext.request.locale.language}" /> <span class="required"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}" />
								</span>
								</label>
							<dsp:input type="text" bean="ProfileFormHandler.editValue.city" maxlength="25" value="" id="txtAddressBookAddCity" name="txtAddressBookAddCityName" >
                            <dsp:tagAttribute name="aria-required" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookAddCity errortxtAddressBookAddCity"/>
                            </dsp:input>
						</div>
						<div class="inputField">
							<c:choose>
                			<c:when test="${currentSiteId eq BedBathCanadaSite}">
								<label id="lblselAddressBookAddState" for="selAddressBookAddState"><bbbl:label key="lbl_shipping_new_province" language="${pageContext.request.locale.language}" /> <span class="required"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}" />
										</span>
										</label>
							</c:when>
							<c:otherwise>
								<label id="lblselAddressBookAddState" for="selAddressBookAddState"><bbbl:label key="lbl_addcreditcard_state" language="${pageContext.request.locale.language}" /> <span class="required"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}" />
										</span>
										</label>
							</c:otherwise>
							</c:choose>
							<dsp:select bean="ProfileFormHandler.editValue.state" name="selAddressBookAddStateName" id="selAddressBookAddState" iclass="uniform">
								<dsp:droplet name="StateDroplet">
								    <dsp:param name="NoShowUSTerr" value="noShowOnRegistry" />
									<dsp:oparam name="output">
										<dsp:option value="">
												<bbbl:label key="lbl_addressBook_select_state" language="${pageContext.request.locale.language}" />
											</dsp:option>
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="location" />
											<dsp:param name="sortProperties" value="+stateName" />
											<dsp:oparam name="output">
												<dsp:getvalueof param="element.stateName" id="elementVal">
												<dsp:getvalueof param="element.stateCode" id="elementCode">
													<dsp:option value="${elementCode}">
														${elementVal}
													</dsp:option>
												</dsp:getvalueof>
												</dsp:getvalueof>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lblselAddressBookAddState errorselAddressBookAddState"/>
							</dsp:select>
							<label id="errorselAddressBookAddState" for="selAddressBookAddState" generated="true" class="error"></label>
						</div>
						<div class="inputField clearfix">

						 <c:choose>
                			<c:when test="${currentSiteId eq BedBathCanadaSite}">
                			    <c:set var="zipCodeLabel"><bbbl:label key="lbl_addcreditcard_postal_code" language="${pageContext.request.locale.language}"/></c:set>
								<c:set var="zipCodeName" value="zipCA"/>
								<c:set var="maxLengthValue" value="7"/>
							</c:when>
							<c:otherwise>
								<c:set var="zipCodeLabel"><bbbl:label key="lbl_addcreditcard_zip" language="${pageContext.request.locale.language}" /></c:set>
								<c:set var="zipCodeName" value="zipUS"/>
								<c:set var="maxLengthValue" value="10"/>
							</c:otherwise>
						</c:choose>
						<label id="lbltxtAddressBookAddZip" for="txtAddressBookAddZip"><c:out value="${zipCodeLabel}" escapeXML="false" /> <span class="required"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}" /></span></label>
						<dsp:input type="text" bean="ProfileFormHandler.editValue.postalCode" name="${zipCodeName}" value="" id="txtAddressBookAddZip" maxlength="${maxLengthValue}">
							<dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookAddZip errortxtAddressBookAddZip"/>
						</dsp:input>
						</div>
						
						<%--Adding new input for POBox flag --%>
						<dsp:input type="hidden" bean="ProfileFormHandler.editValue.poBoxFlag" id="addPoBoxFlag" name="addPoBoxFlag" value=""/>
						<dsp:input type="hidden" bean="ProfileFormHandler.editValue.poBoxStatus" id="addPoBoxStatus" name="addPoBoxStatus" value=""/>
			
					</div>
				</div>
				<div class="marTop_20 buttonpane clearfix">
					<div class="ui-dialog-buttonset">
						<div class="button button_active">
							<dsp:input bean="ProfileFormHandler.newAddress" id="newAddressButton" type="Submit" value="ADD ADDRESS" iclass="btnSubmit" >
                            <dsp:tagAttribute name="aria-pressed" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="newAddressButton"/>
                            <dsp:tagAttribute name="role" value="button"/>
                            </dsp:input>
						</div>
                        <a href="#" class="buttonTextLink close-any-dialog" role="link"><bbbl:label key="lbl_profile_Cancel" language ="${pageContext.request.locale.language}"/></a>
					</div>
				</div>
            </dsp:form>
        </div>

        <div id="editAddressDialog" title="${edit_address_heading}" style="display: none;">
            <dsp:form iclass="form clearfix" id="editAddressDialogForm" action="address_book.jsp" method="post">
				<div class="container_6 clearfix">
                    <div class="grid_3 alpha">
						<div class="inputField clearfix">
							<label id="lbltxtAddressBookEditFirstName" for="txtAddressBookEditFirstName"><bbbl:label key="lbl_addcreditcard_firstname" language="${pageContext.request.locale.language}" /> <span class="required"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}" />
								</span>
								</label>
							<dsp:input type="text" bean="ProfileFormHandler.editValue.firstName" name="txtAddressBookEditFirstNameName" maxlength="30" id="txtAddressBookEditFirstName" >
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookEditFirstName errortxtAddressBookEditFirstName"/>
                            </dsp:input>
					  </div>
						<div class="inputField clearfix">
							<label id="lbltxtAddressBookEditLastName" for="txtAddressBookEditLastName"><bbbl:label key="lbl_addcreditcard_lastname" language="${pageContext.request.locale.language}" /> <span class="required"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}" />
								</span>
								</label>
							<dsp:input type="text" bean="ProfileFormHandler.editValue.lastName" name="txtAddressBookEditLastNameName" maxlength="30" id="txtAddressBookEditLastName" >
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookEditLastName errortxtAddressBookEditLastName"/>
                            </dsp:input>
						</div>
						<div class="inputField clearfix">
							<label id="lbltxtAddressBookEditCompany" for="txtAddressBookEditCompany"><bbbl:label key="lbl_addcreditcard_company" language="${pageContext.request.locale.language}" />
								</label>
							<dsp:input type="text" bean="ProfileFormHandler.editValue.companyName" name="txtAddressBookEditCompanyName" maxlength="20" id="txtAddressBookEditCompany" >
                            <dsp:tagAttribute name="aria-required" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookEditCompany errortxtAddressBookEditCompany"/>
                            </dsp:input>
						</div>
						<dsp:getvalueof var="defaultAddressId" bean="Profile.shippingAddress.repositoryId" />
						<dsp:getvalueof var="currentAddressId" param="currentAddressId" />


						<div class="checkboxField clearfix">
							<dsp:input type="checkbox" bean="ProfileFormHandler.useShippingAddressAsDefault" name="useShippingAddressAsDefault" id="cbAddressBookEditPreferredShipping" >
                            <dsp:tagAttribute name="aria-checked" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblcbAddressBookEditPreferredShipping errorcbAddressBookEditPreferredShipping"/>
                            </dsp:input>
							<label id="lblcbAddressBookEditPreferredShipping" for="cbAddressBookEditPreferredShipping"><bbbl:label key="lbl_profile_makePreferredShipAddress" language="${pageContext.request.locale.language}" />
								</label>
						</div>
						<div class="checkboxField clearfix">
							<dsp:input type="checkbox" bean="ProfileFormHandler.useBillingAddressAsDefault" name="useBillingAddressAsDefault" id="cbAddressBookEditPreferredBilling" >
                            <dsp:tagAttribute name="aria-checked" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblcbAddressBookEditPreferredBilling errorcbAddressBookEditPreferredBilling"/>
                            </dsp:input>
							<label id="lblcbAddressBookEditPreferredBilling" for="cbAddressBookEditPreferredBilling"><bbbl:label key="lbl_addcreditcard_preferredbilling" language="${pageContext.request.locale.language}" />
								</label>
						</div>
					    <div class="checkboxField clearfix">
							<dsp:input type="checkbox" bean="ProfileFormHandler.useMailingAddressAsDefault" name="useMailingAddressAsDefault" id="cbAddressBookEditPreferredMailing" >
                            <dsp:tagAttribute name="aria-checked" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lblcbAddressBookEditPreferredMailing errorcbAddressBookEditPreferredMailing"/>
                            </dsp:input>
							<label id="lblcbAddressBookEditPreferredMailing" for="cbAddressBookEditPreferredMailing"><bbbl:label key="lbl_preferred_mail_address" language="${pageContext.request.locale.language}" />
								</label>
						</div>
                    </div>
                    <div class="grid_3 omega">
						<div class="inputField clearfix">
							<label id="lbltxtAddressBookEditAddress1" for="txtAddressBookEditAddress1"><bbbl:label key="lbl_addcreditcard_address1" language="${pageContext.request.locale.language}" /> <span class="required"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}" />
								</span>
								</label>
							<dsp:input type="text" bean="ProfileFormHandler.editValue.address1" name="txtAddressBookEditAddress1Name" maxlength="30" id="txtAddressBookEditAddress1" >
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookEditAddress1 errortxtAddressBookEditAddress1"/>
                            </dsp:input>
						</div>
						<div class="inputField clearfix">
							<label id="lbltxtAddressBookEditAddress2" for="txtAddressBookEditAddress2"><bbbl:label key="lbl_addcreditcard_address2" language="${pageContext.request.locale.language}" />
								</label>
							<dsp:input type="text" bean="ProfileFormHandler.editValue.address2" name="txtAddressBookEditAddress2Name" maxlength="30" id="txtAddressBookEditAddress2" >
                            <dsp:tagAttribute name="aria-required" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookEditAddress2 errortxtAddressBookEditAddress2"/>
                            </dsp:input>
						</div>
						<div class="inputField clearfix">
							<label id="lbltxtAddressBookEditCity" for="txtAddressBookEditCity"><bbbl:label key="lbl_addcreditcard_city" language="${pageContext.request.locale.language}" /> <span class="required"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}" />
								</span>
								</label>
							<dsp:input type="text" bean="ProfileFormHandler.editValue.city" maxlength="25" value="" id="txtAddressBookEditCity" name="txtAddressBookEditCityName" >
                            <dsp:tagAttribute name="aria-required" value="true"/>
                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookEditCity errortxtAddressBookEditCity"/>
                            </dsp:input>
						</div>
                        <div class="inputField">
                           <c:choose>
                			<c:when test="${currentSiteId eq BedBathCanadaSite}">
                            <label id="lblselAddressBookEditState" for="selAddressBookEditState"><bbbl:label key="lbl_shipping_new_province" language="${pageContext.request.locale.language}" /> <span class="required"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}" />
										</span>
										</label>
                            </c:when>
                            <c:otherwise>
                            <label id="lblselAddressBookEditState" for="selAddressBookEditState"><bbbl:label key="lbl_addcreditcard_state" language="${pageContext.request.locale.language}" /> <span class="required"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}" />
										</span>
										</label>
                            </c:otherwise>
                            </c:choose>
                            <dsp:select bean="ProfileFormHandler.editValue.state" name="selAddressBookEditStateName" id="selAddressBookEditState" iclass="selAddressBookEditState uniform">
								<dsp:droplet name="StateDroplet">
								    <dsp:param name="NoShowUSTerr" value="noShowOnRegistry" />								
									<dsp:oparam name="output">
										<dsp:option value="">
												<bbbl:label key="lbl_addressBook_select_state" language="${pageContext.request.locale.language}" />
											</dsp:option>
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="location" />
											<dsp:param name="sortProperties" value="+stateName" />
											<dsp:oparam name="output">
												<dsp:getvalueof param="element.stateName" id="elementVal">
												<dsp:getvalueof param="element.stateCode" id="elementCode">
													<dsp:option value="${elementCode}">
														${elementVal}
													</dsp:option>
												</dsp:getvalueof>
												</dsp:getvalueof>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
                                <dsp:tagAttribute name="aria-required" value="true"/>
                                <dsp:tagAttribute name="aria-labelledby" value="lblselAddressBookEditState errorselAddressBookEditState"/>
							</dsp:select>
							<label id="errorselAddressBookEditState" for="selAddressBookEditState" generated="true" class="error"></label>
                        </div>
						<div class="inputField clearfix">
							 <c:choose>
								<c:when test="${currentSiteId eq BedBathCanadaSite}">
									<c:set var="zipCodeLabel"><bbbl:label key="lbl_addcreditcard_postal_code" language="${pageContext.request.locale.language}"/></c:set>
									<c:set var="zipCodeName" value="zipCA"/>
									<c:set var="maxLengthValue" value="7"/>
								</c:when>
								<c:otherwise>
									<c:set var="zipCodeLabel"><bbbl:label key="lbl_addcreditcard_zip" language="${pageContext.request.locale.language}" /></c:set>
									<c:set var="zipCodeName" value="zipUS"/>
									<c:set var="maxLengthValue" value="10"/>
								</c:otherwise>
							</c:choose>
							<label id="lbltxtAddressBookEditZip" for="txtAddressBookEditZip"><c:out value="${zipCodeLabel}" escapeXML="false" /> <span class="required"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}" /></span></label>
							<dsp:input type="text" bean="ProfileFormHandler.editValue.postalCode" name="${zipCodeName}" value="" id="txtAddressBookEditZip" maxlength="${maxLengthValue}">
								<dsp:tagAttribute name="aria-required" value="true"/>
								<dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookEditZip errortxtAddressBookEditZip"/>
							</dsp:input>
							
							
							
						</div>
						
						
                    </div>

                    <%--Adding new input for POBox flag --%>
		    <dsp:input type="hidden" bean="ProfileFormHandler.editValue.poBoxFlag" id="editPoBoxFlag" name="editPoBoxFlag" />
		    <dsp:input type="hidden" bean="ProfileFormHandler.editValue.poBoxStatus" id="editPoBoxStatus" name="editPoBoxStatus" />        
                    
                </div>
				<dsp:input iclass="cardId" bean="ProfileFormHandler.editValue.nickname" name="txtAddressBookEditCardId" id="txtAddressBookEditCardId" type="hidden" value="" />
				<div class="marTop_20 buttonpane clearfix">
					<div class="ui-dialog-buttonset">
						<div class="button button_active">
							<dsp:input bean="ProfileFormHandler.updateAddress" type="hidden" value="SAVE CHANGES" />
							<c:choose>
								<c:when test="${isOrder=='true' || isRegistry == 'true'}">
									<dsp:input bean="ProfileFormHandler.updateAddress" id="personalInfoBtnReg" type="Submit" value="SAVE CHANGES" iclass="btnSubmit" >
                                    <dsp:tagAttribute name="aria-pressed" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="personalInfoBtnReg"/>
                                    <dsp:tagAttribute name="role" value="button"/>
                                    </dsp:input>
								</c:when>
								<c:otherwise>
									<dsp:input bean="ProfileFormHandler.updateAddress" id="personalInfoBtn" type="Submit" value="SAVE CHANGES" iclass="btnSubmit" >
                                    <dsp:tagAttribute name="aria-pressed" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="personalInfoBtn"/>
                                    <dsp:tagAttribute name="role" value="button"/>
                                    </dsp:input>
								</c:otherwise>
							</c:choose>
							
							
						</div>
                        <a href="#" class="buttonTextLink close-any-dialog" role="link"><bbbl:label key="lbl_payament_button_cancel" language="${pageContext.request.locale.language}" /></a>
					</div>
				</div>
				
				
           </dsp:form>
        </div>
        <div id="updateAddressWarningDialog" title="${update_address_heading}" style="display: none;">
         <bbbt:textArea key="txt_addressbook_updatetxt" language="${pageContext.request.locale.language}" />
         <div class="marTop_20 buttonpane clearfix">
                        <div class="ui-dialog-buttonset clearfix">
                            <div class="button">
                                <dsp:a href="${contextPath}/giftregistry/my_registries.jsp"><bbbl:label key="lbl_go_to_registries" language="${pageContext.request.locale.language}" /></dsp:a>
                            </div>
                            <div class="button marLeft_10">
                            	<a href="#" class="close-any-dialog"><bbbl:label key="lbl_reg_ok" language="${pageContext.request.locale.language}" />
                                </a>
                             </div>
                        </div>
                    </div>
        </div>
        <div id="removeAddressWarningDialog" title="${remove_address_heading}" style="display: none;">
			<bbbt:textArea key="txt_addressbook_removetxt" language="${pageContext.request.locale.language}" />
			<dsp:form iclass="form clearfix" id="addressRemoveForm" action="address_book.jsp" method="post">
				<dsp:input id="defaultShippingAddressCard" bean="ProfileFormHandler.useShippingAddressAsDefault" type="hidden" />
				<dsp:input id="defaultBillingAddressCard" bean="ProfileFormHandler.useBillingAddressAsDefault" type="hidden" />
				<dsp:input id="defaultMailingAddressCard" bean="ProfileFormHandler.useMailingAddressAsDefault" type="hidden" />
				<dsp:input iclass="cardId" bean="ProfileFormHandler.addressId" id="cardId" type="hidden" value="" />
				<div class="marTop_20 buttonpane clearfix">
					<div class="ui-dialog-buttonset">
						<div class="button button_active">
							<dsp:input bean="ProfileFormHandler.removeAddress" id="removeBtn" type="Submit" value="OK" iclass="btnSubmit" >
                            <dsp:tagAttribute name="aria-pressed" value="false"/>
                            <dsp:tagAttribute name="aria-labelledby" value="removeBtn"/>
                            <dsp:tagAttribute name="role" value="button"/>
                            </dsp:input>
						</div>
                        <a href="#" class="buttonTextLink close-any-dialog" role="link"><bbbl:label key="lbl_payament_button_cancel" language="${pageContext.request.locale.language}" /></a>
					</div>
				</div>
			</dsp:form>
		</div>
<dsp:getvalueof bean="ProfileFormHandler.addressAdded" id="addr" />
<script type="text/javascript">
    $('#addAddressDialogForm').on('submit', function(){
        if (typeof acctUpdate === "function") {
            acctUpdate('added new address');
        }
    });
    $('#addressRemoveForm').on('submit', function(){
        if (typeof acctUpdate === "function") {
            acctUpdate('removed saved address');
        }
    });
    $('#editAddressDialogForm').on('submit', function(){
        if (typeof acctUpdate === "function") {
            acctUpdate('saved address info updated');
        }
    });
</script>

</jsp:body>
<jsp:attribute name="footerContent">
<script type="text/javascript">
	if (typeof s !== 'undefined') {
	s.pageName ='My Account>Address Book';
	s.channel = 'My Account';
	s.prop1 = 'My Account';
	s.prop2 = 'My Account';
	s.prop3 = 'My Account';
	s.prop6='${pageContext.request.serverName}';
	s.eVar9='${pageContext.request.serverName}';
	var s_code = s.t();
	if (s_code)
		document.write(s_code);
	}
</script>
</jsp:attribute>
</bbb:pageContainer>
</dsp:page>
