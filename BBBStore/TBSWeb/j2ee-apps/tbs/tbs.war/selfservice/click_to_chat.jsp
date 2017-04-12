<dsp:page>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="com/bbb/selfservice/common/SelfServiceUtil" />
	<dsp:importbean bean="com/bbb/selfservice/ClickToChatDroplet" />
	<dsp:getvalueof var="chatURL" param="chatURL"/>
    <dsp:droplet name="ClickToChatDroplet">
    	<dsp:oparam name="output">
    		<c:if test="${empty chatURL}">
    			<dsp:getvalueof var="chatURL" param="chatURL"></dsp:getvalueof>
    		</c:if>
    		<dsp:getvalueof var="isChatWindowOpen" param="chatOpenFlag"></dsp:getvalueof>
    		<dsp:getvalueof var="onOffFlag" param="onOffFlag"></dsp:getvalueof>
    		<c:set var="chatAvailable" ><bbbl:label key="lbl_chat_available" language="${pageContext.request.locale.language}"/></c:set>
    		<c:set var="chatUnavailable" ><bbbl:label key="lbl_chat_unavailable" language="${pageContext.request.locale.language}"/></c:set>
			<c:choose>
				<c:when test="${onOffFlag=='false'}">
					<div title="${chatUnavailable}">
                        <div class="clearfix">
                            <p class="bold">
                                <span class="error">
                                    <bbbe:error key="err_chat_closed_error" language="${pageContext.request.locale.language}"/>
                                </span>
                            </p>
                        </div>
                        <div class="marTop_20 buttonpane clearfix">
                            <div class="ui-dialog-buttonset">
                                <div class="button button_active">
                                    <input type="button" value="Ok" name="btnCancel" class="close-any-dialog" title="Ok" id="btnCancel" role="button" aria-pressed="false" aria-labelledby="btnCancel" />
                                </div>
                            </div>
                        </div>
					</div>
			    </c:when>
			    <c:when test="${isChatWindowOpen=='false'}">
					<div title="${chatUnavailable}">
                        <div class="clearfix">
                            <p class="bold marBottom_20"><bbbe:error key="err_chat_closed_error" language="${pageContext.request.locale.language}"/></p>
                            <jsp:useBean id="placeHolderChat" class="java.util.HashMap" scope="request"/>
							<c:set target="${placeHolderChat}" property="chatWeekDayStartTime"><dsp:valueof param="weekDayOpenTime"/></c:set>
							<c:set target="${placeHolderChat}" property="chatWeekDayEndTime"><dsp:valueof param="weekDayCloseTime"/></c:set>
							<c:set target="${placeHolderChat}" property="chatWeekEndStartTime"><dsp:valueof param="weekEndOpenTime"/></c:set>
							<c:set target="${placeHolderChat}" property="chatWeekEndEndTime"><dsp:valueof param="weekEndCloseTime"/></c:set>
			    			<p class="noMar"><bbbt:textArea key="txt_chat_window_closed" language="${pageContext.request.locale.language}" placeHolderMap="${placeHolderChat}"/></p>
                        </div>
                        <div class="marTop_20 buttonpane clearfix">
                            <div class="ui-dialog-buttonset">
                                <div class="button button_active">
                                    <input type="button" value="Ok" name="btnCancel" class="close-any-dialog" title="Ok" id="btnCancel" role="button" aria-pressed="false" aria-labelledby="btnCancel" />
                                </div>
                            </div>
                        </div>
					</div>
			    </c:when>
				<c:otherwise>
					<div title="${chatAvailable}">
						<dsp:form id="frmLiveChat" method="post" action="${chatURL}">
                            <input type="hidden" value="${chatURL}" name="chatURL" />
                            <div class="input clearfix formRow">
				   				<div class="label width_2 fl">
									<label id="lblfirstName" for="firstName">
										<bbbl:label key="lbl_chat_firstname" language="${pageContext.request.locale.language}"/> <span class="required"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}"/></span>
									</label>
								</div>
								<div class="text width_3 fl">
							    	<dsp:input id="firstName" iclass="chatData" type="text" bean="Profile.firstName" name="firstName">
                                        <dsp:tagAttribute name="data" value="first_name"/>
                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblfirstName errorfirstName"/>
							    	</dsp:input>
							    </div>
							</div>
                            
                            <div class="input clearfix formRow">
				   				<div class="label width_2 fl">
				   					<label id="lbllastName" for="lastName">
										<bbbl:label key="lbl_chat_lastname" language="${pageContext.request.locale.language}"/> <span class="required"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}"/></span>
									</label>
								</div>
								<div class="text width_3 fl">
									<dsp:input id="lastName" iclass="chatData" type="text" bean="Profile.lastName" name="lastName" >
                                        <dsp:tagAttribute name="data" value="last_name"/>
                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lbllastName errorlastName"/>
									</dsp:input>
								</div>
							</div>

                            <div class="input clearfix formRow">
				   				<div class="label width_2 fl">
				   					<label id="lblemail" for="email">
										<bbbl:label key="lbl_chat_email" language="${pageContext.request.locale.language}"/> <span class="required"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}"/></span>
									</label>
								</div>
								<div class="text width_3 fl">
									<dsp:input id="email" iclass="chatData" type="text" bean="Profile.email" name="email">
                                        <dsp:tagAttribute name="data" value="email"/>
                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
									</dsp:input>
								</div>
							</div>
                            
                            <p class="noMarBot"><label><span class="required"><bbbl:label key="lbl_mandatory" language="${pageContext.request.locale.language}"/> </span><bbbl:label key="lbl_click_to_chat_mandatory_note" language="${pageContext.request.locale.language}"/></label></p>
                            
                            <div class="marTop_20 buttonpane clearfix">
                                <div class="ui-dialog-buttonset">
                                    <div class="button button_active">
                                        <input type="submit" value="CHAT NOW" name="chatNow" id="chatNow" onclick="javascript:omnitureExternalLinks('rightnow: chat button')" role="button" aria-pressed="false" aria-labelledby="chatNow" />
                                    </div>
                                    <a href="#" class="buttonTextLink close-any-dialog">Cancel</a>
                                </div>
                            </div>
						</dsp:form>
					</div>
				</c:otherwise>
			</c:choose>
		</dsp:oparam>
    	<dsp:oparam name="error">
    		<dsp:getvalueof var="varSystemSrror" param="systemerror"></dsp:getvalueof>
    	</dsp:oparam>
	</dsp:droplet>
	<script type="text/javascript">
	 function omnitureExternalLinks(data){
       	if (typeof s !== 'undefined') {
       	externalLinks(data);
       	}
       }</script>
</dsp:page>