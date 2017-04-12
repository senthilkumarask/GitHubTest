<dsp:page>
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/BridalToolkitLinkDroplet" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
		
	<div class="grid_3 clearfix omega">
		<dsp:form name="prodForm1" method="post" id="prodForm1">
			<dsp:getvalueof var="appid" bean="Site.id" />
			<dsp:droplet name="BridalToolkitLinkDroplet">
				<dsp:param name="siteId" value="${appid}" />

				<dsp:oparam name="output">
					<c:set var="sizeValue">
						<dsp:valueof param="size" />

					</c:set>
				</dsp:oparam>
			</dsp:droplet>
			<dsp:getvalueof var="transient" bean="Profile.transient" />
			<c:choose>
				<c:when test="${transient == 'false'}">
					<c:choose>
						<c:when test="${sizeValue>1}">
							<div class="lookWhatYouSee lookWhatYouSeeMulti clearfix">
								<bbbt:textArea key="txt_already_have_a_bridal" language ="${pageContext.request.locale.language}"/>

								<div class="fl addToRegistry addToRegistrySel">
									<div class="select">
										<dsp:select bean="GiftRegistryFormHandler.registryId"
											name="registryId"
											iclass="addItemToRegis redirNewPage selector_primaryAlt">
                                            <dsp:tagAttribute name="aria-required" value="false"/>
											<dsp:droplet name="BridalToolkitLinkDroplet">
												<dsp:param name="siteId" value="${appid}" />
												<dsp:oparam name="output">
													<dsp:option selected="true" value=""><bbbl:label key='lbl_start_bridal_toolkit' language ="${pageContext.request.locale.language}"></bbbl:label></dsp:option>
													<dsp:droplet name="ForEach">
														<dsp:param name="array" param="bridalRegistryVOList" />
														<dsp:oparam name="output">
															<dsp:param name="futureRegList" param="element" />

															<dsp:getvalueof var="regId"
																param="futureRegList.registryId" />
															<dsp:getvalueof var="event_type"
																param="element.eventType" />
															<dsp:getvalueof var="bridalToolkitToken"
																param="futureRegList.bridalToolkitToken" />
															<dsp:option
																value="${contextPath}/bbregistry/bridal_toolkit/bridal_toolkit_popup.jsp?bridaltoolkit=${bridalToolkitToken}"
																iclass="${event_type}">
																<dsp:valueof param="element.eventType"></dsp:valueof>
																<dsp:valueof param="element.eventDate"></dsp:valueof>
															</dsp:option>
														</dsp:oparam>
													</dsp:droplet>
												</dsp:oparam>
											</dsp:droplet>

										</dsp:select>

									</div>
								</div>
								<bbbt:textArea key="txt_view_demo_read_tips" language ="${pageContext.request.locale.language}"/>
							</div>
						</c:when>
						<c:when test="${sizeValue==1}">
							<div class="lookWhatYouSee lookWhatYouSeeSingle clearfix">
								<bbbt:textArea key="txt_already_have_a_bridal" language ="${pageContext.request.locale.language}"/>
								<dsp:droplet name="BridalToolkitLinkDroplet">
									<dsp:param name="siteId" value="${appid}" />
									<dsp:oparam name="output">
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="bridalRegistryVOList" />
											<dsp:oparam name="output">
												<dsp:param name="futureRegList" param="element" />
												<dsp:getvalueof var="regId" param="futureRegList.registryId" />
												<dsp:getvalueof var="registryName"
													param="futureRegList.eventType" />
												<dsp:getvalueof var="bridalToolkitToken"
													param="futureRegList.bridalToolkitToken" />
												<input type="hidden" value="${regId}" name="registryId"
													class="addItemToRegis" />
												<input type="hidden" value="${registryName}"
													name="registryName" class="addItemToRegis" />
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
								<div class="fl addToRegistry">
									<div class="button button_active">
										<a
											href="${contextPath}/bbregistry/bridal_toolkit/bridal_toolkit_popup.jsp?bridaltoolkit=${bridalToolkitToken}"
											class="btnAddToRegistry" name="btnAddToRegistry"
											target="_blank"><bbbl:label key='lbl_start_bridal_toolkit' language ="${pageContext.request.locale.language}"></bbbl:label> </a>
									</div>
								</div>
								<bbbt:textArea key="txt_mng_your_registry" language ="${pageContext.request.locale.language}"/>
								<bbbt:textArea key="txt_view_demo_read_tips" language ="${pageContext.request.locale.language}"/>
							</div>
						</c:when>
						<c:otherwise>
							<div class="lookWhatYouSee lookWhatYouSeeNoReg clearfix">
								<bbbt:textArea key="txt_create_bridal_reg_begin" language ="${pageContext.request.locale.language}"/>
								<div class="createRegistry">
									<div class="button button_active">
										<a
											href="${contextPath}/bridalregistries/create_wed_registry_popup.jsp"
											class="popup"><bbbl:label key="lbl_create_your_registry" language ="${pageContext.request.locale.language}"/></a>
									</div>
								</div>
								<bbbt:textArea key="txt_view_demo_read_tips" language ="${pageContext.request.locale.language}"/>
							</div>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:when test="${transient == 'true'}">
					<div class="lookWhatYouSee lookWhatYouSeeNotLoggedIn clearfix">
						<div class="fl addToRegistry">
							<div class="button button_active">
								<a href="${contextPath}/bbregistry/bridal_toolkit/bridal_toolkit_popup.jsp?bridaltoolkit=${bridalToolkitToken}">
									<bbbl:label key='lbl_start_bridal_toolkit' language ="${pageContext.request.locale.language}"></bbbl:label></a>
							</div>
						</div>
						<bbbt:textArea key="txt_view_demo_read_tips" language ="${pageContext.request.locale.language}"/>
					</div>
				</c:when>
			</c:choose>
		</dsp:form>
	</div>
</dsp:page>