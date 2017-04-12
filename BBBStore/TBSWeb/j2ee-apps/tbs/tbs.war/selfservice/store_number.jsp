<dsp:importbean var="TBSStoreNumberFormHandler" bean="/com/bbb/selfservice/TBSStoreNumberFormHandler"/>
<dsp:page>
    <dsp:getvalueof var="storeNumber" value="${sessionScope.storenumber}" />
    <c:set var="storeNumberHeader"><bbbl:label key="lbl_storenumber_storenumber" language ="${pageContext.request.locale.language}"/></c:set>
    
    	
    
    <div class="row">
       <div class="small-12 columns">
           <c:choose>
				<c:when test="${storeNumberHeader == 'VALUE NOT FOUND FOR KEY lbl_storenumber_storenumber'}">
					<h1>Store Number</h1>
				</c:when>
				<c:otherwise>
					<h1><bbbl:label key="lbl_storenumber_storenumber" language ="${pageContext.request.locale.language}"/></h1>
				</c:otherwise>
			</c:choose>
         
       </div>
    </div>
    <div class="row">
	
			<div class="small-12 columns">
			  <dsp:form id="storeNumberUpdateForm"  action="store_number_ajax.jsp" method="post" iclass="form post">
				<dsp:include page="/global/gadgets/errorMessage.jsp">
				  <dsp:param name="formhandler" bean="TBSStoreNumberFormHandler"/>
				</dsp:include>
				
				
						<label for='txtStoreNumber' class='hide'>Enter Store Number</label>
						<dsp:input bean="TBSStoreNumberFormHandler.storeNumber" name="txtStoreNumber" id="txtStoreNumber" type="text" maxlength="4" value="${storeNumber}">
                            <dsp:tagAttribute name="aria-required" value="true"/>
                        </dsp:input>
					<%-- <small id="errStoreNum" class="error"></small> This small element needs to be added only when there is an error. --%>
				
				<%-- A variable used to check, is validation required on Store Number update call (only from CheckOut page) --%>
				<c:set var="url" value="${pageContext.request.requestURL }"></c:set>
				<c:choose>
					<c:when test="${fn:contains(url, 'checkoutType')}">
						<dsp:input id="validationRequired" bean="TBSStoreNumberFormHandler.validationRequired" value="true" type="hidden"/>
					</c:when>
					<c:otherwise>
						<dsp:input id="validationRequired" bean="TBSStoreNumberFormHandler.validationRequired" value="false" type="hidden"/>
					</c:otherwise>
				</c:choose>
				
				<dsp:input bean="TBSStoreNumberFormHandler.update" value="true" type="hidden"/>
				
				    	<c:set var="submitKey">
							<bbbl:label key="lbl_storenumber_submit" language ="${pageContext.request.locale.language}"/>
						</c:set>
						<c:choose>
							<c:when test="${submitKey == 'VALUE NOT FOUND FOR KEY lbl_storenumber_submit'}">
								<input type="button" class="button expand" id="updStoreNumBtn" value="<bbbl:label key='lbl_mng_regitem_update' language='${pageContext.request.locale.language}' />"/>
							</c:when>
							<c:otherwise>
								<input type="button" class="button expand" id="updStoreNumBtn" value="${submitKey}"/>
							</c:otherwise>
						</c:choose>
				<input type="submit" class="hide" aria-hidden="true">
			</dsp:form>
		</div>
	</div>
</dsp:page>