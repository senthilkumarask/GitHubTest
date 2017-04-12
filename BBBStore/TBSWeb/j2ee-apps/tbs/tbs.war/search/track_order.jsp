<dsp:page>


	<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE10">
	<meta http-equiv="X-UA-Compatible" content="IE=EmulateIE9">

<script src="${jsPath}/_assets/tbs_assets/js/src/legacy/jquery-1.7.1.min.js"></script>
<script type="text/javascript" src="${jsPath}/_assets/tbs_assets/js/src/legacy/advancedOrderSearch.js"></script>
 <dsp:importbean bean="/com/bbb/search/handler/TBSOrderSearchFormHandler" />
 <dsp:importbean bean="/com/bbb/search/bean/TBSAdvancedOrderSearchBean"/>
 <dsp:importbean bean="/atg/multisite/Site" />

<dsp:getvalueof bean="TBSAdvancedOrderSearchBean.firstName" var="fname"/>
<dsp:getvalueof bean="TBSAdvancedOrderSearchBean.lastName" var="lname"/>
<dsp:getvalueof bean="TBSAdvancedOrderSearchBean.startDate" var="stdate"/>
<dsp:getvalueof bean="TBSAdvancedOrderSearchBean.endDate" var="endate"/>
<dsp:getvalueof bean="TBSAdvancedOrderSearchBean.registryNum" var="regId"/>
<dsp:getvalueof bean="TBSAdvancedOrderSearchBean.storeNum" var="storeId"/>
<dsp:getvalueof bean="TBSAdvancedOrderSearchBean.searchTerm" var="searchTerm"/>

 <dsp:getvalueof bean="TBSOrderSearchFormHandler.orderResults" var="orderResults" />
 <dsp:getvalueof param="pageNumber" var="pageNumber" />
 <dsp:getvalueof param="perPage" var="perPage" />
 
 <c:set var="perPageValueFromConfigKey"><bbbc:config key="PerPage" configName="AdvancedOrderInquiryKeys" /></c:set>
 <c:set var="dateRangeFromConfigKey"><bbbc:config key="DateRange" configName="AdvancedOrderInquiryKeys" /></c:set>
 <c:set var="resultSetSizeValueFromConfigKey"><bbbc:config key="ResultSetSize" configName="AdvancedOrderInquiryKeys" /></c:set>
 <c:set var="maxDateRangeVal"><bbbl:textArea key="lbl_date_criterion_message" language="${pageContext.request.locale.language}"/> </c:set>
 <c:set var="defaultTab">basic</c:set>
 <c:set var="showLegacySearchLink"><bbbc:config key="showLegacySearchLink" configName="FlagDrivenFunctions" /></c:set>
 <c:set var="legacySiteURL"><bbbc:config key="legacySiteURL" configName="ThirdPartyURLs" /></c:set>
 <dsp:getvalueof var="appid" bean="Site.id" />
 <c:if test="${not empty sessionScope.defaultTab}"><c:set var="defaultTab">${sessionScope.defaultTab}</c:set></c:if>
 
 <c:if test="${fn:containsIgnoreCase(perPageValueFromConfigKey, 'VALUE NOT FOUND FOR KEY') || empty perPageValueFromConfigKey}">
	<c:set var="perPageValueFromConfigKey" value="48"/>
 </c:if>
 
 <c:if test="${fn:containsIgnoreCase(dateRangeFromConfigKey, 'VALUE NOT FOUND FOR KEY') || empty dateRangeFromConfigKey}">
	<c:set var="dateRangeFromConfigKey" value="35"/>
 </c:if>
 
 <c:if test="${fn:containsIgnoreCase(resultSetSizeValueFromConfigKey, 'VALUE NOT FOUND FOR KEY') || empty resultSetSizeValueFromConfigKey}">
	<c:set var="resultSetSizeValueFromConfigKey" value="500"/>
 </c:if>
 
<dsp:getvalueof var="advancedTab" param="tab"/>
 <dsp:getvalueof var="isAdvancedTab" value="0"/>
	<c:if test="${advancedTab == 'advanced'}">
		<dsp:getvalueof var="isAdvancedTab" value="1"/>
	</c:if>

 <bbb:pageContainer>
    <jsp:attribute name="bodyClass">track-order</jsp:attribute>
  <jsp:body>
  <div id="box"><img style="padding:6px;" src="//s7d9.scene7.com/is/image/BedBathandBeyond/arrow_up?$PNG$&fmt=png-alpha" width="13" height="8"></div>

		  <div class="row"> <div class="small-12 columns no-padding"> <h1><bbbl:label key="lbl_tbs_order_inquiry" language="<c:out param='${pageContext.request.locale.language}'/>" /></h1> </div> </div>
			<c:if test="${isAdvancedTab eq '0'}">
				<div class="row">
					<dsp:include page="/global/gadgets/errorMessage.jsp">
						<dsp:param name="formhandler" bean="TBSOrderSearchFormHandler"/>
					</dsp:include>
				</div>
			</c:if>
            <div class="content" role="main">
            	<div class="row hide-on-print">
            		<div class="right print-email"> <a href="#" class="pdp-sprite email" id="emailIcon" title="Email Cart"><span></span></a><span id="emailPrintSeperator">|</span><a href="#" class="pdp-sprite print print-trigger-aoi" title="Print"><span></span></a> </div>
	                <ul class="tabs" data-tab> 
	                    <li class="tab-title  <c:if test="${defaultTab eq 'basic'}">active</c:if> " onclick="showEmailIcon()">
	                        <a href="#panel1"><bbbl:label key="lbl_tbs_ord_basic" language="<c:out param='${pageContext.request.locale.language}'/>" /></a>
	                    </li> 
	                    <li class="tab-title <c:if test="${defaultTab eq 'advance'}">active</c:if> " onclick="hideEmailIcon()">
	                        <a href="#panel2"><bbbl:label key="lbl_tbs_ord_adv" language="<c:out param='${pageContext.request.locale.language}'/>" /></a>
	                    </li> 
	                </ul> 
                </div>
                <div class="tabs-content row hide-on-print"> 
                    <div class="content <c:if test="${defaultTab eq 'basic'}">active</c:if> " id="panel1">
                        <div class="contentImages">
                            <h3><bbbl:label key="lbl_tbs_ord_enter_either_fields" language="<c:out param='${pageContext.request.locale.language}'/>" /></h3>
                            <dsp:form method="post" action="track_order.jsp" id="searchFormBasic">
                                	<div class="small-12 medium-3 columns no-padding">
                                    	<dsp:input bean="TBSOrderSearchFormHandler.value.orderId" id="orderId" type="text" value="">
                                    	   <dsp:tagAttribute name="placeholder" value="Order Id *"/>
                                        </dsp:input>
                                	</div>
                        			<div class="small-1 columns no-padding or"><bbbl:label key="lbl_tbs_ord_or" language="<c:out param='${pageContext.request.locale.language}'/>" /></div>
                                	<div class="small-12 medium-3 columns no-padding">
                                    	<dsp:input bean="TBSOrderSearchFormHandler.value.email" id="email" type="text" value="">
                                        	<dsp:tagAttribute name="placeholder" value="Email *"/>
                                        </dsp:input>
                                	</div>
                                	<div class="small-2 medium-3 columns">
                                    	<dsp:input bean="TBSOrderSearchFormHandler.basicOrderSearch" iclass="tiny expand button service" type="submit" value="Search"/>
                                	</div>
                                </div>
                                <dsp:input id="successURL" bean="TBSOrderSearchFormHandler.basicOrderSearchSucessURL" type="hidden" value="/tbs/search/track_order.jsp"/>
                                <dsp:input id="errorURL" bean="TBSOrderSearchFormHandler.basicOrderSearchErrorURL" type="hidden" value="/tbs/search/track_order.jsp"/>
                            </dsp:form>
                            <br><br><br><br>
                            <c:if test="${not empty appid && appid ne 'TBS_BedBathCanada'}">
                            <c:if test="${fn:containsIgnoreCase(showLegacySearchLink, 'VALUE NOT FOUND FOR KEY') || showLegacySearchLink}">
                            <h3><bbbl:textArea key="txt_legacy_basic" language="${pageContext.request.locale.language}"/> <a href="${legacySiteURL}" target="_blank">Click here</a>.</h3>
                            </c:if>
                            </c:if>
                         <br>
                        <div id="result">
           
    		<%-- order_results.jsp will get invoked for Basic Order Search --%>
    		<c:if test="${(not empty orderResults || not empty pageNumber) && (isAdvancedTab eq '0')}">
            	<dsp:include page="frag/order_results.jsp"/>
            </c:if>
			

						</div>
		</div>
					<%-- Removed asterisks from placeholder tagAttribute for all the fields --%>
                    <div class="aoicontent <c:if test="${defaultTab eq 'advance'}">active</c:if> " id="panel2">
                    <c:if test="${isAdvancedTab eq '1'}">
							<div class="row">
								<dsp:include page="/global/gadgets/errorMessage.jsp">
									<dsp:param name="formhandler" bean="TBSOrderSearchFormHandler"/>
								</dsp:include>
							</div>
						</c:if>
                        <div class="contentImages">
		            <c:set var="minSearchCritMsg"><bbbl:textArea key="advanced_order_search_min_req" language="${pageContext.request.locale.language}"/></c:set>
					<dsp:valueof value="${fn:replace(minSearchCritMsg, 'resultSetSize', resultSetSizeValueFromConfigKey)}" valueishtml="true"/>
                            <div class="row">
                            <dsp:form method="post" action="track_order.jsp" id="searchFormAdvanced">
                                <div class="small-12 medium-2 columns">
                                    <dsp:input bean="TBSOrderSearchFormHandler.value.firstName" id="fname" type="text" value="${fname}">
                                    <dsp:tagAttribute name="placeholder" value="First Name "/>
                                    </dsp:input> 
                                </div>
                                <div class="small-12 medium-2 columns">
                                    <dsp:input bean="TBSOrderSearchFormHandler.value.lastName" id="lname" type="text" value="${lname}">
                                    <dsp:tagAttribute name="placeholder" value="Last Name "/>
                                    </dsp:input> 
                                </div>
                                <div class="small-12 medium-2 columns">
                                <c:if test="${not empty stdate && not empty endate}"><c:set var="dtrange" value="${stdate} - ${endate}"/></c:if>
                                	<dsp:input bean="TBSOrderSearchFormHandler.value.strtDate" iclass="dateRange" type="text" value="${dtrange}">
                                	<dsp:tagAttribute name="placeholder" value="Start Date-End Date"/>
                                    </dsp:input>
                                </div>
								<%-- Removed and/or code --%>
                                <div class="small-12 medium-2 columns">
                                    <dsp:input bean="TBSOrderSearchFormHandler.value.registryId" id="regis" type="text" value="${regId}">
                                    <dsp:tagAttribute name="placeholder" value="Registry # "/>
                                    </dsp:input> 
                                </div>
                                <div class="small-12 medium-2 columns">
                                    <dsp:input bean="TBSOrderSearchFormHandler.value.storeId" id="store" type="text" size="4" value="${storeId}">
                                    <dsp:tagAttribute name="placeholder" value="Store # "/>
                                    </dsp:input> 
                                </div>
                                <div class="small-2 medium-2 columns">
                                    <dsp:input bean="TBSOrderSearchFormHandler.advanceOrderSearch" iclass="tiny expand button service" type="submit" value="Search"/>
                                </div>
                                <dsp:input bean="TBSOrderSearchFormHandler.advanceOrderSearchSucessURL" type="hidden" value="/tbs/search/track_order.jsp?tab=advanced"/>
                                <dsp:input bean="TBSOrderSearchFormHandler.advanceOrderSearchErrorURL" type="hidden" value="/tbs/search/track_order.jsp?tab=advanced"/>
                            </dsp:form>
                            <c:if test="${not empty appid && appid ne 'TBS_BedBathCanada'}">
                            <c:if test="${fn:containsIgnoreCase(showLegacySearchLink, 'VALUE NOT FOUND FOR KEY') || showLegacySearchLink}">
                            <h3><bbbl:textArea key="txt_legacy_advance" language="${pageContext.request.locale.language}"/> <a href="${legacySiteURL}" target="_blank">Click here</a>.</h3>
                            </c:if>
                            </c:if>
                        </div>
                        
                        			 <c:set var="minimalOrderDetails">
        		<bbbc:config key="TBS_MINIMAL_ORDERDETAIL" configName="FlagDrivenFunctions" />
    		</c:set>
    				
    		</div>			
    		<c:if test="${not empty searchTerm}">
    		<c:choose>
    			<c:when test="${minimalOrderDetails}">
    				<%-- order_results.jsp will get invoked for Advanced Order Search --%>		
		    		    <dsp:include page="frag/minimal_advanced_order_results.jsp">
                        	<dsp:param name="perPage"
											value="${perPageValueFromConfigKey}" />
                        	<dsp:param name="sortOrderDate" value="-" />
                        </dsp:include>
						<dsp:getvalueof var="totalNumberOfOrders" bean="TBSAdvancedOrderSearchBean.totalOrders"/>
		   		</c:when>
    			<c:otherwise>
    				<%-- order_results.jsp will get invoked for Advanced Order Search --%>		
		    		    <dsp:include page="frag/advanced_order_results.jsp">
                        	<dsp:param name="perPage" value="${perPageValueFromConfigKey}"/>
                        	<dsp:param name="sortOrderDate" value="-"/>
                        </dsp:include>
						<dsp:getvalueof var="totalNumberOfOrders" bean="TBSAdvancedOrderSearchBean.totalOrders"/>
		    		
    			</c:otherwise>
    		</c:choose>
            </c:if>            
                     
                </div>
                </div>
				                
        

		<div class="ajaxLoaderImg text-center">
			<img src="${jsPath}/_assets/tbs_assets/img/ajax-loader.gif" alt="Loader Image"  id="ajaxLoaderImage" style="visibility:hidden">
		</div>
		<input type="submit" id="ajaxScroll" name="ajaxScroll" value="ajaxScroll" style="visibility:hidden">
		<div id="ajaxLoaderImage" class="text-center" style="visibility:hidden">
			<img src="${jsPath}/_assets/tbs_assets/img/ajax-loader.gif" alt="Loader Image" >
		</div>
		<input type="hidden" id="isAdvancedTab" value="${isAdvancedTab}">
		<input type="hidden" id="totalNumberOfOrders" value="${totalNumberOfOrders}">
		<input type="hidden" id="perPage" value="${perPageValueFromConfigKey}">
		<input type="hidden" id="sortOrder" value="">
		<input type="hidden" id="sortOrderDate" value="-">
		<input type="hidden" id="sortOption" value="">
		<input type="hidden" id="dateRangeFromConfigKey" value="${dateRangeFromConfigKey}">
		<input type="hidden" id="fieldToUpdate" value="">
		<input type="hidden" id="fieldArrowValue" value="">
	
	</jsp:body>
 </bbb:pageContainer>

<script type="text/javascript">

var maxNumberOfOrderRendered = 0;
var	pageNum = 1;
var sortOption = "submittedDate";
var perPag = 0;
var makeAjaxCall = true;
	
    $(document).ready(function() {
    	var activeTab = "${fn:escapeXml(param['tab'])}"
    		
   		if(activeTab == '' || activeTab != 'advanced') {
   			showEmailIcon();
   		} else if(activeTab == 'advanced') {
   			hideEmailIcon();
   		}

		var isAdvancedTab = document.getElementById('isAdvancedTab').value;
			totalOrders = document.getElementById('totalNumberOfOrders').value;
			perPage = document.getElementById('perPage').value;
			
			perPag = perPage;
			dateRangeFromConfigKey = document.getElementById('dateRangeFromConfigKey').value;
    	  $('.dateRange').daterangepicker({
						dateLimit:{days:dateRangeFromConfigKey},
			locale: {
					cancelLabel: 'Clear'
			}
		  });
		$('.dateRange').on('cancel.daterangepicker', function(ev, picker) {
			$(this).val('');
			picker.setStartDate(moment());
			picker.setEndDate(moment());
		});
		
		// print page -- prints all orders irrespective of any number of orders being displayed on the page
		$('body').on('click', '.print-trigger-aoi', function(){
			if(totalOrders>0){
				if(parseInt(perPag)<parseInt(totalOrders)){
					perPag = totalOrders;
					getOrders(true);
				}else{
					getOrders(true);
				}
			}else{
				window.print();
			}
		});
		
		$(window).scroll(function() {
			var offSetTop = $('#ajaxScroll').offset().top,
			ajaxOuterHeight = $('#ajaxScroll').outerHeight(),
			ajaxWindowHeight = $(window).height(),
			ajaxScrollPos = $(this).scrollTop();
			if (ajaxScrollPos > (offSetTop+ajaxOuterHeight-ajaxWindowHeight)){
				if (isAdvancedTab == 1 && makeAjaxCall){
					perPag = parseInt(perPag) + parseInt(perPage);
					$("#ajaxLoaderImage").css("visibility", "visible");
					if(totalOrders > 0 && totalOrders > maxNumberOfOrderRendered){
						//alert("showing "+perPag+" of total "+totalOrders+" orders");
						getOrders(false);
					}else{
						$("#ajaxLoaderImage").css("visibility", "hidden");
						perPag = maxNumberOfOrderRendered;
					}	
				}
			}
			if(activeTab == 'advanced') {
				if ($(this).scrollTop() > 100) {
					$("#box").fadeIn();
				} else {
					$("#box").fadeOut();
				}
			}
		});
		
		var txt = document.getElementsByClassName('range_inputs')[0].innerHTML;
		document.getElementsByClassName('range_inputs')[0].innerHTML = txt + "<p></p><h4>${maxDateRangeVal}</h4>";
    });	
	
	function getOrders(isPrint){
		makeAjaxCall = false;
		$.ajax(
				{
					cache:false, //done for IE10
					type: "GET",
					url: BBB.config.url.advancedOrderSearch.url,
					data: {"perPage": perPag,
					"pageNumber": pageNum,
					"sortOption": document.getElementById('sortOption').value,
					"sortOrder" : document.getElementById('sortOrder').value,
					"sortOrderDate" : document.getElementById('sortOrderDate').value
					},
								
					success: function(data) {
						$("#replaceWithAjaxData").html(data);
						$("#ajaxLoaderImage").css("visibility", "hidden");
						maxNumberOfOrderRendered = perPag * pageNum;
						document.getElementById('perPage').value = maxNumberOfOrderRendered;
						makeAjaxCall=true;
						if(isPrint){
							window.print();
						}
					},
					error: function (jqXHR, exception) {
						$("#ajaxLoaderImage").css("visibility", "hidden");
						var msg = '';
						if (jqXHR.status === 0) {
							msg = 'Not connect.\n Verify Network.';
						} else if (jqXHR.status == 404) {
							msg = 'Requested page not found. [404]';
						} else if (jqXHR.status == 500) {
							msg = 'Internal Server Error [500].';
						} else if (exception === 'parsererror') {
							msg = 'Requested JSON parse failed.';
						} else if (exception === 'timeout') {
							msg = 'Time out error.';
						} else if (exception === 'abort') {
							msg = 'Ajax request aborted.';
						} else {
							msg = 'Uncaught Error.\n' + jqXHR.responseText;
						}
							console.log(msg);
					}
	            
				});
			}
			
	function showEmailIcon() {
		document.getElementById('emailPrintSeperator').setAttribute('class','');
		document.getElementById('emailIcon').setAttribute('class','pdp-sprite email');
	}
	
	function hideEmailIcon() {
		document.getElementById('emailPrintSeperator').setAttribute('class','hidden');
		document.getElementById('emailIcon').setAttribute('class','pdp-sprite email hidden');
	}
	
	//code to scroll to the top of the page
	$("#box").on("click", function(e) {
		e.preventDefault();

		$("body,html").animate({
			scrollTop: 0
		}, 800);
		return false;
	});

</script>
</dsp:page>