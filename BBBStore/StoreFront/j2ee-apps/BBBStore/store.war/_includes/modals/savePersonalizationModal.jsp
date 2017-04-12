<%@ taglib uri="/WEB-INF/tld/BBBLblTxt.tld" prefix="bbb"%>
<!-- <div class="savePersonalizationPopUp">
    <h2><bbbl:label key='lbl_personalization_donot_loose' language="${pageContext.request.locale.language}" /></h2>
    <div class="personalizationPopUpBtn clearfix">
        <div class="clearfix">
            <input type="button" class="button-Med addtocart" value="add to cart" />
        </div>
        <div class="clearfix">
            <input type="button" class="button-Med btnPrimary btnContinue" value="continue shopping" />
        </div>
    </div>
</div> -->
<div class="navigatePersonalizationPopUP">
    <div>
     <bbbl:label key="lbl_personalization_lost" language="${pageContext.request.locale.language}" />
</br>
     <bbbl:label key="lbl_return_to_base_sku" language="${pageContext.request.locale.language}" />.
    </div>
    <div class="fl returnPersonalizationPDP">
		<div class="button button_active button_active_orange  ">			
		<input type="submit" name="returnToPage" value="Return"  role="button" class="" />
		</div>	
		<a href="#" class="continueNavigatefrmPopUp  ui-corner-all" role="button"><span><span><bbbl:label key="lbl_cartdetail_continueshopping" language="${pageContext.request.locale.language}" /></span></span></a>
		
    </div>
</div>