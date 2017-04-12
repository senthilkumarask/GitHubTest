<dsp:page>
<dsp:importbean bean="/com/bbb/selfservice/StateDroplet" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<div class="gModalPop hidden">
					<form id="addAddressDialogForm" action="#" class="form clearfix" method="post" novalidate>
  <div class="container_6 clearfix">
    <div class="grid_4 omega alpha flatform">
	  <div class="error inputField gModalAddressFieldError"><bbbl:label key='lbl_contact_address_update_error' language="${pageContext.request.locale.language}" /></div>
      <div class="inputField gModalAddressBook clearfix">
		  <div class="addr1">
		 <label class="gModalAddressField"><bbbl:label key="lbl_gModal_contact" language ="${pageContext.request.locale.language}"/></label>  
        <input id="txtAddressBookAddAddress1" aria-labelledby="lbltxtAddressBookAddAddress1 errortxtAddressBookAddAddress1" maxlength="30" name="txtAddressBookAddAddress1Name" type="text" aria-required="true" class="required escapeHTMLTag addressAlphanumericbasicpunc cannotStartWithWhiteSpace" placeholder="Contact Address" autocomplete="off">
			</div>
			<div class="aptNo ">
			<label class="gModalAddressField"><bbbl:label key="lbl_gModal_addres2" language ="${pageContext.request.locale.language}"/></label>
				<label class="txtOffScreen" id="ApartmentNoLabel" for="ApartmentNo">${lbl_reg_ph_apt_building}</label>
				<input id="txtAddressBookAddAddress1Apt" aria-labelledby="ApartmentNoLabel" maxlength="30" name="txtAddressBookAddAddress1Name" type="text" autocomplete="off" value="${contAddrLineTwoFromBean}" placeholder="Apt/Bldg (optional)" class="ApartmentNo cannotStartWithWhiteSpace escapeHTMLTag">
			</div>
      </div>
      <c:choose>
		<c:when test="${siteId eq 'BedBathCanada'}">
      <div class="inputField gModalAddressBook  clearfix">
	  <label class="gModalAddressField"><bbbl:label key="lbl_gModal_zip" language ="${pageContext.request.locale.language}"/></label>
        <input id="txtAddressBookAddZip" aria-labelledby="lbltxtAddressBookAddZip errortxtAddressBookAddZip" maxlength="10" name="txtAddressBookAddZipName" type="text" aria-required="true" class="escapeHTMLTag required zipCA" placeholder="Postal Code">
      </div>
      </c:when>
	  <c:otherwise>
		<div class="inputField gModalAddressBook  clearfix">
		<label class="gModalAddressField"><bbbl:label key="lbl_gModal_zip" language ="${pageContext.request.locale.language}"/></label>
        <input id="txtAddressBookAddZip" aria-labelledby="lbltxtAddressBookAddZip errortxtAddressBookAddZip" maxlength="10" name="txtAddressBookAddZipName" type="text" aria-required="true" class="escapeHTMLTag required zipUS" placeholder="Zip Code">
      </div>
	  </c:otherwise>
	  </c:choose>
      <div class="inputField clearfix">
	  <label class="gModalAddressField"><bbbl:label key="lbl_gModal_city" language ="${pageContext.request.locale.language}"/></label>
        <input id="txtAddressBookAddCity" aria-labelledby="lbltxtAddressBookAddCity errortxtAddressBookAddCity" maxlength="25" name="txtAddressBookAddCityName" type="text" aria-required="false" class="escapeHTMLTag required" placeholder="City">
      </div>
       <div class="inputField clearfix hidden">
        <input id="txtAddressBookAddCountry" maxlength="25" name="txtAddressBookAddCountryName" type="hidden">
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
      <div class="button">
        <input id="newAddressButton" aria-labelledby="newAddressButton" name="" aria-pressed="false" value="APPLY" role="button" type="submit">
      </div>
      <a href="#" class="buttonTextLink close-any-dialog" role="link">Cancel</a> 
  </div>
</form>
		</div>
</dsp:page>
