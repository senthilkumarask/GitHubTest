<div class="grid_12 pushDown alpha clearfix">
<dsp:getvalueof id="email" param="email"/>
     <p>
		<jsp:useBean id="placeHolderEmail" class="java.util.HashMap" scope="request"/>
		<c:set target="${placeHolderEmail}" property="fieldName">${email}</c:set>
        <bbbl:textArea key="txt_couponinfo_email_coupon" language="${pageContext.request.locale.language}" placeHolderMap="${placeHolderEmail}"/>
     </p>
 </div> 
 <dsp:getvalueof id="cssClass" param="cssClass"/>
 <div class="${cssClass}">
     <div class="couponAccept button">
     	<dsp:getvalueof id="href" param="buttonHref"/>
     	<dsp:getvalueof id="buttonValue" param="buttonValue"/>
         <dsp:a href="${href}"><dsp:valueof value="${buttonValue}"/></dsp:a>
     </div>
 </div>
 <div class="grid_12 pushDown alpha clearfix">
     <p>
         <bbbl:textArea key="txt_couponinfo_contact_coupon" language="${pageContext.request.locale.language}" />
     </p>
 </div>