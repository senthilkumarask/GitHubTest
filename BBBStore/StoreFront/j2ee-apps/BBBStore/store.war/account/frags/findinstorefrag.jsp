<dsp:page>
<bbb:pageContainer index="false" follow="false">
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<dsp:importbean bean="com/bbb/selfservice/SearchStoreFormHandler" />
	<dsp:importbean bean="com/bbb/selfservice/SearchStoreDroplet" />
	<dsp:valueof param="SearchStoreFormHandler.mapQuestSearchString"></dsp:valueof>
	<c:set var="lbl_find_in_store_heading"><bbbl:label key="lbl_find_in_store_heading" language="${pageContext.request.locale.language}" /></c:set>
	<div id="changeStoreDialogWrapper" class="clearfix hidden marLeft_10" title="${lbl_find_in_store_heading}">
		<div id="changeStoreStaticWrap" class="changeStoreStaticWrap clearfix">
			<div class="width_5 fl marRight_10">
				<p>
					<span class="showWithFindInStore hidden"><bbbl:label key="lbl_findinstorefrag_pickup" language="${pageContext.request.locale.language}"/></span>
					<span class="showWithFindAStore hidden"><bbbl:label key="lbl_findinstorefrag_favstore" language="${pageContext.request.locale.language}"/></span>
					<a class="showWithResult hidden searchAgainBtn" href="#"><bbbl:label key="lbl_findinstorefrag_search" language="${pageContext.request.locale.language}"/></a>
				</p>
				<bbbl:label key="lbl_findinstorefrag_skuname" language="${pageContext.request.locale.language}"/>
				<div class="width_3 showWithResult itemDetails clearfix marTop_10 marBottom_20 hidden">
					<img class="fl productImage" height="83" width="83" src="/_assets/global/images/placeholder/productimage_smallb_63x63.jpg" alt="SKU NAME and PRICE" />
					<p><strong class="productName"><bbbl:label key="lbl_findinstorefrag_prodname" language="${pageContext.request.locale.language}"/></strong></p>
				</div>
				<div id="changeStoreFormWrapper" class="marTop_20 hidden">
					<dsp:form method="post" action="/_ajax/change_store_error_status.jsp" name="changeStoreForm" id="changeStoreForm">
					<dsp:input bean="SearchStoreFormHandler.siteContext" type="hidden" beanvalue="/atg/multisite/Site.id"/>
						<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
							<dsp:oparam name="output">
									<dsp:valueof param="message" />
							</dsp:oparam>
						</dsp:droplet>
						<div id="searchStoreBox" class="width_6 alpha omega clearfix">
							<div class="width_2 fl">
								
								<div class="input clearfix">
									<div class="label">
										<label id="lbltxtStoreZip" for="txtStoreZip"><bbbl:label key="lbl_findinstorefrag_zip" language="${pageContext.request.locale.language}"/></label>
									</div>
									<div class="text">
										<div class="width_1 alpha omega">
											<dsp:input bean="SearchStoreFormHandler.storeLocator.postalCode" type="text" id="txtStoreZip" >
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtStoreZip errortxtStoreZip"/>
                                            </dsp:input>
										</div>
										<label id="errortxtStoreZip" for="txtStoreZip" generated="true" class="error"></label>
									</div>
									<div class="error"></div>
								</div>
							</div>
							
							<div class="width_1 fl marRight_20 marTop_25">
								<div id="orText">
									<bbbl:label key="lbl_findinstorefrag_or" language="${pageContext.request.locale.language}"/>
								</div>
							</div>
							
							<div class="width_2 fl marLeft_10">
								
								<div class="input clearfix">
									<div class="label">
										<label id="lbltxtStoreAddress" for="txtStoreAddress"><bbbl:label key="lbl_findinstorefrag_address" language="${pageContext.request.locale.language}"/></label>
									</div>
									<div class="text">
										<div class="width_2 alpha omega">
											<dsp:input bean="SearchStoreFormHandler.storeLocator.address" type="text" id="txtStoreAddress" >
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtStoreAddress errortxtStoreAddress"/>
                                            </dsp:input>
										</div>
									</div>
								</div>
								
								
								<div class="input clearfix">
									<div class="label">
										<label id="lbltxtStoreCity" for="txtStoreCity"><bbbl:label key="lbl_findinstorefrag_city" language="${pageContext.request.locale.language}"/><span class="required">*</span></label>
									</div>
									<div class="text">
										<div class="width_2 alpha omega">
											<dsp:input bean="SearchStoreFormHandler.storeLocator.city" type="text" maxlength="25" id="txtStoreCity" >
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtStoreCity errortxtStoreCity"/>
                                            </dsp:input>
										</div>
									</div>
								</div>
								
								
								<div class="input clearfix">
									<div class="label">
										<label id="lbltxtStoreState" for="txtStoreState"><bbbl:label key="lbl_findinstorefrag_state" language="${pageContext.request.locale.language}"/><span class="required"> * </span></label>
									</div>
									<div class="select">
										<div class="width_2 alpha suffix_3">
											<dsp:input bean="SearchStoreFormHandler.storeLocator.state" type="text" id="txtStoreState">
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbltxtStoreState errortxtStoreState"/>
                                            </dsp:input>
										</div>
										<label id="errortxtStoreState" for="txtStoreState" generated="true" class="error"></label>
									</div>
								</div>
							</div>
						</div>
						<div class="grid_5 alpha omega clearfix marTop_10">
							<div class="grid_1 alpha">
								
								<div class="clearfix">
									<div class="label width_1 fl marTop_5">
										<label id="lblselRadius" for="selRadius"><bbbl:label key="lbl_findinstorefrag_radius" language="${pageContext.request.locale.language}"/></label>
									</div>
									<div class="select fl">
										<div class="width_1 alpha">
											 <dsp:input	bean="SearchStoreFormHandler.storeLocator.radius" type="text" id="selRadius" >
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblselRadius errorselRadius"/>
                                            </dsp:input>
										</div>
									</div>
								</div>
							</div>
						</div>
						<input type="submit" class="hidden" value=""/>
					</dsp:form>
				</div>
			</div>
		</div>
		<div id="changeStoreResultWrapper"></div>
	</div>
</bbb:pageContainer>
</dsp:page>
