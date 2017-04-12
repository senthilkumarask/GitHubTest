<dsp:importbean bean="/com/bbb/selfservice/StateDroplet" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<!--  Allow PO Box address story -->
<c:set var="shipTo_POBoxOn" scope="request"><bbbc:config key="shipTo_POBoxOn" configName="FlagDrivenFunctions"/></c:set>
<c:choose>
	<c:when test="${not empty shipTo_POBoxOn &&  shipTo_POBoxOn && (currentSiteId ne BedBathCanadaSite)}">
		<c:set var="iclassValue" value=""/>
	</c:when>
	<c:otherwise>
		<c:set var="iclassValue" value="poBoxAddNotAllowed"/>
	</c:otherwise>
</c:choose>
<div id="regAddModal" class="reveal-modal tiny">
	<div class="gModalPop">
		<h2 class="title">Update Your Contact Address</h2>
		<form id="addAddressDialogForm" method="post" action="#" class="clearfix">
				  <div class="container_6 clearfix">
				    <div class="grid_4 omega alpha flatform ">
				      <div class="inputField clearfix">
				        <input id="txtAddressBookAddAddress1" aria-labelledby="lbltxtAddressBookAddAddress1 errortxtAddressBookAddAddress1" maxlength="30" name="txtAddressBookAddAddress1Name" type="text" aria-required="true" class="required escapeHTMLTag ${iclassValue}" placeholder="Contact Address" autocomplete="off">
				      </div>
				      <div class="inputField clearfix">
				      	<c:choose>
							<c:when test="${currentSiteId eq 'TBS_BedBathCanada'}">
								<input id="txtAddressBookAddZip" aria-labelledby="lbltxtAddressBookAddZip errortxtAddressBookAddZip" maxlength="7" name="zipCA" type="text" aria-required="true" class="escapeHTMLTag required zipCA" placeholder="Postal Code">
							</c:when>
							<c:otherwise>
								<input id="txtAddressBookAddZip" aria-labelledby="lbltxtAddressBookAddZip errortxtAddressBookAddZip" maxlength="5" name="zipUS" type="text" aria-required="true" class="escapeHTMLTag required zipUS" placeholder="Zip Code">
							</c:otherwise>
						</c:choose>
				      </div>
				      <div class="inputField clearfix">
				        <input id="txtAddressBookAddCity" aria-labelledby="lbltxtAddressBookAddCity errortxtAddressBookAddCity" maxlength="25" name="txtAddressBookAddCityName" type="text" aria-required="false" class="escapeHTMLTag required" placeholder="City">
				      </div>
				      <div class="inputField">
				          <select id="selAddressBookAddState" aria-labelledby="lblselAddressBookAddState errorselAddressBookAddState" name="selAddressBookAddStateName" class="required" aria-required="true">
								<dsp:droplet name="StateDroplet">
								    <dsp:param name="NoShowUSTerr" value="noShowOnRegistry" />
									<dsp:oparam name="output">
										<option value="">
												<bbbl:label key="lbl_addressBook_select_state" language="${pageContext.request.locale.language}" />
											</option>
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="location" />
											<dsp:param name="sortProperties" value="+stateName" />
											<dsp:oparam name="output">
												<dsp:getvalueof param="element.stateName" id="elementVal">
												<dsp:getvalueof param="element.stateCode" id="elementCode">
													<option value="${elementCode}">
														${elementVal}
													</option>
												</dsp:getvalueof>
												</dsp:getvalueof>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>
				          </select>
				      </div>
				    </div>
				  </div>
				  <div class="marTop_20 buttonpane clearfix">
				      <div>
				         <a id="newAddressButton" class="createRegistryButtons" href="javascript:;">APPLY</a>									     
				   		 <a href="#" class="createRegistryButtons close-qasmodal" role="link">Cancel</a> 
				      </div>
				  </div>
				</form>
			<a href="#" class="close-modal-qas">&#215;</a>
	</div>
</div>
