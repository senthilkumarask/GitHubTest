
<dsp:page>
	<div class="grid_12 pushDown">
		 <div class="grid_6 push_5">
	       	<div class="couponAccept button">
	       		<dsp:getvalueof id="href" param="buttonHref"/>
	     		<dsp:getvalueof id="buttonValue" param="buttonValue"/>
	     		
	        	<dsp:a href="${href}" ><dsp:valueof value="${buttonValue}"/></dsp:a>
	    	</div>
		</div>
	 	<div class="grid_12 pushDown alpha clearfix">
	       <p>
	           <bbbl:textArea key="txt_couponinfoerror_contact_coupon" language="${pageContext.request.locale.language}" />
	       </p>
	  	</div>
	</div>
</dsp:page>	