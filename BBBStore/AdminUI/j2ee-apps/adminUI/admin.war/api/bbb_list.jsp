<%@ page language="java"%>
<%@ taglib uri="dspTaglib" prefix="dsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<dsp:page>

<dsp:importbean bean="/com/admin/formhandler/BBBListFormHandler"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof var="action" param="_action"/>

<dsp:form id="bbb_list" name="bbb_list" action="${contextPath}/api/bbb_form_response.jsp" method="post">
	<dsp:input type="hidden" bean="BBBListFormHandler.listId" id="bbb_list_list_id" name="list_id"></dsp:input>
	<dsp:input type="hidden" bean="BBBListFormHandler.displayName" id="bbb_list_display_name" name="bbb_list_display_name"></dsp:input>
	<dsp:input type="hidden" bean="BBBListFormHandler.disabled" id="bbb_list_is_disabled" name="is_disabled"></dsp:input>
	<dsp:input type="hidden" bean="BBBListFormHandler.allowDuplicate" id="bbb_list_allow_duplicates" name="bbb_list_allow_duplicates"></dsp:input>
	<dsp:input type="hidden" bean="BBBListFormHandler.sites" id="bbb_list_site_flag" name="bbb_list_site_flag"></dsp:input>
	<dsp:input type="hidden" bean="BBBListFormHandler.subTypeCode" id="bbb_list_sub_type_code" name="bbb_list_sub_type_code"></dsp:input>
	<dsp:input type="hidden" bean="BBBListFormHandler.sequenceNumber" id="bbb_list_sequence_num" name="bbb_list_sequence_num"></dsp:input>
	<dsp:input type="hidden" bean="BBBListFormHandler.lastModifiedUser" id="bbb_list_lastModifiedUser" name="last_modified_user" value="dummy"></dsp:input>
	<dsp:input type="hidden" bean="BBBListFormHandler.userCreated" id="bbb_list_user_created" name="bbb_list_user_created" value="dummy"></dsp:input>
	<c:if test="${action eq 'insert'}"><dsp:input type="hidden" bean="BBBListFormHandler.createList" id="createListBtn" value="create"></dsp:input></c:if>
	<c:if test="${action eq 'update'}"><dsp:input type="hidden" bean="BBBListFormHandler.editList" id="editListBtn" value="edit"></dsp:input></c:if>
	<c:if test="${action eq 'delete'}"><dsp:input type="hidden" bean="BBBListFormHandler.removeList" id="removeListBtn" value="remove"></dsp:input></c:if>
	
</dsp:form>

</dsp:page>