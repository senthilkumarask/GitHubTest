<dsp:page>
<dsp:importbean bean="/com/bbb/selfservice/SearchStoreFormHandler" />
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />

<dsp:getvalueof var="errMessageShown" param="errMessageShown" />
<c:set var="lbl_find_in_store_heading"> <bbbl:label key="lbl_find_in_store_heading" language="${pageContext.request.locale.language}" />
			</c:set>			
<div id="changeStoreDialogWrapper" class="clearfix row" title="${lbl_find_in_store_heading}">

	<div id="changeStoreStaticWrap" class="changeStoreStaticWrap clearfix">
	        <h3 class='find-store-title'>Find a Store</h3>
			<p>
				<span class="showWithFindAStore "><bbbl:label	key="lbl_find_a_store_header" language="${pageContext.request.locale.language}"/></span>
			</p>
			<div class="showWithResult itemDetails clearfix hidden">
				<div class="fl">
					<img class="fl productImage" height="83" width="83" <%-- onerror="this.src='${imagePath}/_assets/global/images/no_image_available.jpg'" --%> src="/_assets/global/images/placeholder/productimage_smallb_63x63.jpg" alt="SKU NAME and PRICE" />
				</div>
				<div class="fl padTop_5 width_7 alpha omega">
					<p class="padBottom_10 productName">Product name goes here.</p>
					<p class="modalSKUName"></p>
				</div>
				<div class="clear"></div>
			</div>
			<div id="changeStoreFormWrapper" class="marTop_20 ">
				<dsp:form method="post" action="/tbs/selfservice/change_store_error_status.jsp"
								name="changeStoreForm" id="changeStoreForm"  iclass="clearfix">
				<dsp:input bean="SearchStoreFormHandler.siteContext" type="" iclass="hidden" beanvalue="/atg/multisite/Site.id"/>
					<c:if test="${errMessageShown ne 'true'}">
						<div class="error">
							<dsp:droplet name="ErrorMessageForEach">
								<dsp:param bean="SearchStoreFormHandler.formExceptions" name="exceptions"/>
								<dsp:oparam name="output">
									<dsp:valueof param="message" />
								</dsp:oparam>
							</dsp:droplet>
						</div>
					</c:if>
					
					<div id="searchStoreBox" class="row alpha omega clearfix">
						<div class="large-5 small-12 column fl">
							
							<div class="input clearfix">
								<div class="label">
								<c:choose>
									<c:when test="${currentSiteId eq 'TBS_BedBathCanada'}">
										<label id="lbltxtStoreZip" for="txtStoreZip" class="padBottom_5"><bbbl:label
															key="lbl_search_store_Postal"
															language="${pageContext.request.locale.language}" /><span
														class="required"> * </span>
									</c:when>
									<c:otherwise>
										<label id="lbltxtStoreZip" for="txtStoreZip" class="padBottom_5"><bbbl:label
											key="lbl_search_store_zip"
											language="${pageContext.request.locale.language}" /><span
										class="required"> * </span>
									</c:otherwise>
								</c:choose>
									</label>
								</div>
								<div class="text">
									<div class="width_1 alpha omega">
										<c:choose>
											<c:when test="${currentSiteId eq 'TBS_BedBathCanada'}">
												<dsp:input type="text" id="txtStoreZip" name="storeZipCA"
													maxlength="7" bean="SearchStoreFormHandler.storeLocator.postalCode" iclass="txtTbsStoreZip" >
													<dsp:tagAttribute name="aria-required" value="true"/>
													<dsp:tagAttribute name="aria-labelledby" value="lbltxtStoreZip errortxtStoreZip"/>
												</dsp:input>
											</c:when>
											<c:otherwise>
												<dsp:input type="tel" id="txtStoreZip" name="storeZip"
													maxlength="5" bean="SearchStoreFormHandler.storeLocator.postalCode" >
													<dsp:tagAttribute name="aria-required" value="true"/>
													<dsp:tagAttribute name="aria-labelledby" value="lbltxtStoreZip errortxtStoreZip"/>
												</dsp:input>
											</c:otherwise>
										</c:choose>						
									</div>
									<label id="errortxtStoreZip" for="txtStoreZip" generated="true" class="error"></label>
								</div>
							</div>
						</div>

						<div class="large-2 small-12 column fl marTop_25">
							<div id="orText"><bbbl:label
														key="lbl_OR"
														language="${pageContext.request.locale.language}}" />
							</div>
						</div>

						<div class="large-5 small-12 column fl">
							
							<div class="input clearfix">
								<div class="label">
									<label id="lbltxtStoreAddress" for="txtStoreAddress" class="padBottom_5"><bbbl:label
														key="lbl_search_store_address"
														language="${pageContext.request.locale.language}}" />
									</label>
								</div>
								<div class="text">
									<div class="width_2 alpha omega">
										<dsp:input type="text" id="txtStoreAddress" name="add2"
														bean="SearchStoreFormHandler.storeLocator.address" >
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtStoreAddress errortxtStoreAddress"/>
                                        </dsp:input>
									</div>
								</div>
							</div>

							
							<div class="input clearfix">
								<div class="label">
									<label id="lbltxtStoreCity" for="txtStoreCity" class="padBottom_5"><bbbl:label
														key="lbl_search_store_city"
														language="${pageContext.request.locale.language}" /> <span
													class="required">*</span>
									</label>
								</div>
								<div class="text">
									<div class="width_2 alpha omega">
										<dsp:input type="text" id="txtStoreCity" name="storeCity"
														maxlength="25"
														bean="SearchStoreFormHandler.storeLocator.city" iclass="required" >
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtStoreCity errortxtStoreCity"/>
                                        </dsp:input>
									</div>
								</div>
							</div>

							
							<div class="input clearfix">
								<div class="label">
									<label id="lbltxtStoreState" for="txtStoreState" class="padBottom_5"><bbbl:label
														key="lbl_search_store_state"
														language="${pageContext.request.locale.language}" /><span
													class="required"> * </span>
									</label>
								</div>
								<div class="select">
									<div class="width_2 alpha suffix_3">
										<c:choose>
											<c:when test="${currentSiteId eq 'TBS_BedBathCanada'}">
												<c:set var="storeState" value="storeStateCA" scope="request"/>
											</c:when>
											<c:otherwise>
												<c:set var="storeState" value="storeState" scope="request"/>
											</c:otherwise>
										</c:choose>
										<dsp:select name="${storeState}" id="txtStoreState"
														bean="SearchStoreFormHandler.storeLocator.state" iclass="uniform required">
											<dsp:option value="">
												<bbbl:label key="lbl_bridalbook_select_state"
																language="${pageContext.request.locale.language}" />
											</dsp:option>
											<dsp:droplet name="ForEach">
												<dsp:param name="array"
																bean="SearchStoreFormHandler.stateList" />
												<dsp:param name="elementName" value="statelist" />
												<dsp:oparam name="output">
													<dsp:getvalueof id="stateName" param="statelist.stateName" />
													<dsp:getvalueof id="stateCode" param="statelist.stateCode" />
													<dsp:option value="${stateCode}">
														<c:out value="${stateName}" />
													</dsp:option>
												</dsp:oparam>
											</dsp:droplet>
                                            <dsp:tagAttribute name="aria-required" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lbltxtStoreState errortxtStoreState"/>
										</dsp:select>
									</div>
									<label id="errortxtStoreState" for="txtStoreState" generated="true" class="error"></label>
								</div>
							</div>
						</div>
					</div>
					<div class="fasRadiusWrapper alpha omega clearfix">
						<div class="row alpha">
							
							<div class="clearfix column large-8">
									<label id="lblselRadius" for="selRadius" class="padBottom_5 lblselRadius"><bbbl:label key="lbl_search_store_radius" language="${pageContext.request.locale.language}"/></label>
								<div class="select marLeft_20 large-4">
									<div class="">
										<c:set var="radius_default_us"><bbbc:config key="radius_default_us" configName="MapQuestStoreType" /></c:set> 
										<c:set var="radius_default_baby"><bbbc:config key="radius_default_baby" configName="MapQuestStoreType" /></c:set> 
										<c:set var="radius_range_us"><bbbc:config key="radius_range_us" configName="MapQuestStoreType" /></c:set> 
										<c:set var="radius_range_baby"><bbbc:config key="radius_range_baby" configName="MapQuestStoreType" /></c:set> 
										<c:choose>
											<c:when test="${currentSiteId eq 'TBS_BedBathCanada'}">
											<c:set var="radius_range_type"><bbbc:config key="radius_range_type_ca" configName="MapQuestStoreType" /></c:set>
						                   </c:when>
										<c:otherwise>
										 <c:set var="radius_range_type"><bbbc:config key="radius_range_type" configName="MapQuestStoreType" /></c:set>
										</c:otherwise>
										</c:choose>
										
										<c:choose>
											<c:when test="${currentSiteId eq TBS_BuyBuyBabySite}">
												<c:set var="radius_default_selected">${radius_default_baby}</c:set> 
												<c:set var="radius_range">${radius_range_baby}</c:set> 
											</c:when>
											<c:otherwise>
												<c:set var="radius_default_selected">${radius_default_us}</c:set> 
												<c:set var="radius_range">${radius_range_us}</c:set> 
											</c:otherwise>
										</c:choose>
								
										<dsp:select name="selRadiusName" id="selRadius"
														bean="SearchStoreFormHandler.storeLocator.radius" iclass="uniform">
                                            <dsp:tagAttribute name="aria-required" value="false"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblselRadius errorselRadius"/>
											<c:forTokens items="${radius_range}" delims="," var="item">
											<c:choose>
												<c:when test="${item == radius_default_selected}">
													<dsp:option value="${item}" iclass="default" selected="true">${item} ${radius_range_type}</dsp:option>
												</c:when>
												<c:otherwise>
													<dsp:option value="${item}">${item} ${radius_range_type} </dsp:option>
												</c:otherwise>
											</c:choose>
											</c:forTokens>	
										</dsp:select>
									</div>
								</div>
							</div>
							
						</div>
					</div>
					<div class="row">
						<div class="columns large-12">
							<a href="#" class="createRegistryButtons" id="findaTbsStore">GO</a>
							<a href="#" class="createRegistryButtons close-tbs-model">CANCEL</a>
						</div>
					</div>
					<%-- <dsp:input bean="SearchStoreFormHandler.searchStore" type="submit"
									value="GO" /> --%>
				<!--	<dsp:setvalue bean="SearchStoreFormHandler.searchStoreSuccessURL"
									value="find_store_reg.jsp" />
					<dsp:setvalue bean="SearchStoreFormHandler.searchStoreErrorURL"
									value="find_store_reg.jsp" /> -->
					<dsp:input bean="SearchStoreFormHandler.fromPage" type="hidden" value="find_store_reg" />				
					<input type="hidden" name="productId" data-dest-class="productId"
									value="${param.productId}" />
					<input type="hidden" name="registryId" data-dest-class="registryId" value="" />
					<input type="hidden" name="changeCurrentStore" data-dest-class="changeCurrentStore" value="" />
					<input type="hidden" name="orderedQty" data-dest-class="itemQuantity"
									value="${param.itemQuantity}" />
					<input type="hidden" name="skuid" data-dest-class="changeStoreSkuId" value="" />
					<input type="hidden" name="enableStoreSelection" value="${param.enableStoreSelection}" />
					<input type='submit' class='hide' value='submit' name='submit'/>
				</dsp:form>
			</div>
	</div>
	
	<div id="changeStoreResultWrapper"></div>
	<a class="close-nearbyStoreModal">&#215;</a>
</div>
    <c:import url="/selfservice/store/find_store_pdp.jsp" ></c:import>

	<dsp:include page="/selfservice/store/p2p_directions_input.jsp"/>
</dsp:page>		