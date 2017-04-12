<%@ page language="java"%>
<%@ taglib uri="dspTaglib" prefix="dsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<dsp:page>

<dsp:importbean bean="/com/admin/formhandler/BBBCategoryFormHandler"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof var="action" param="_action"/>

<dsp:form id="bbb_list_category" name="bbb_list_category" action="${contextPath}/api/bbb_form_response.jsp" method="post">
<dsp:input type="text" bean="BBBCategoryFormHandler.categoryId" id="bbb_list_category_list_cat_id" name="list_cat_id"/>
<!--  
<dsp:input type="hidden" bean="BBBCategoryFormHandler." id="bbb_list_category_list_id" name="list_id"/>
<dsp:input type="hidden" bean="BBBCategoryFormHandler." id="bbb_list_category_parent_list_cat_id" name="parent_list_cat_id"/>
-->
<dsp:input type="text" bean="BBBCategoryFormHandler.parentCategory" id="bbb_list_category_primary_parent_cat_id" name="primary_parent_cat_id"></dsp:input>
<dsp:input type="text" bean="BBBCategoryFormHandler.displayName" id="bbb_list_category_display_name" name="display_name"></dsp:input>
<dsp:input type="text" bean="BBBCategoryFormHandler.suggestedQty" id="bbb_list_category_suggested_qty" name="suggested_qty"></dsp:input>
<dsp:input type="text" bean="BBBCategoryFormHandler.categoryName" id="bbb_list_category_name" name="name"></dsp:input>
<dsp:input type="text" bean="BBBCategoryFormHandler.categoryUrlUS" id="bbb_list_category_category_url" name="category_url"></dsp:input>
<dsp:input type="text" bean="BBBCategoryFormHandler.categoryImageUrlUS" id="bbb_list_category_image_url" name="image_url"></dsp:input>
<dsp:input type="text" bean="BBBCategoryFormHandler.categoryUrlTBS" id="bbb_list_category_tbs_category_url" name="tbs_category_url"></dsp:input>
<dsp:input type="text" bean="BBBCategoryFormHandler.categoryImageUrlTBS" id="bbb_list_category_tbs_image_url" name="tbs_image_url"></dsp:input>
<dsp:input type="text" bean="BBBCategoryFormHandler.categoryUrlBABY" id="bbb_list_category_baby_category_url" name="baby_category_url"></dsp:input>
<dsp:input type="text" bean="BBBCategoryFormHandler.categoryImageUrlBABY" id="bbb_list_category_baby_image_url" name="baby_image_url"></dsp:input>
<dsp:input type="text" bean="BBBCategoryFormHandler.categoryUrlCA" id="bbb_list_category_ca_category_url" name="ca_category_url"></dsp:input>
<dsp:input type="text" bean="BBBCategoryFormHandler.categoryImageUrlCA" id="bbb_list_category_ca_image_url" name="ca_image_url"></dsp:input>
<dsp:input type="text" bean="BBBCategoryFormHandler.thresholdQty" id="bbb_list_category_threshold_qty" name="threshold_qty"></dsp:input>
<dsp:input type="text" bean="BBBCategoryFormHandler.thresholdAmt" id="bbb_list_category_threshold_amt" name="threshold_amt"></dsp:input>
<dsp:input type="text" bean="BBBCategoryFormHandler.serviceType" id="bbb_list_category_service_type_cd" name="service_type_cd"></dsp:input>
<dsp:input type="text" bean="BBBCategoryFormHandler.disabled" id="bbb_list_category_is_disabled" name="is_disabled"></dsp:input>
<dsp:input type="text" bean="BBBCategoryFormHandler.createdUser" id="bbb_list_category_create_user" name="create_user" value="1"></dsp:input>
<dsp:input type="text" bean="BBBCategoryFormHandler.lastModifiedUser" id="bbb_list_category_last_mod_user" name="bbb_list_category_last_mod_user" value="AdminUI"></dsp:input>
<dsp:input type="text" bean="BBBCategoryFormHandler.visibleOnChecklist" id="bbb_list_category_is_visible_on_checklist" name="is_visible_on_checklist"></dsp:input>
<dsp:input type="text" bean="BBBCategoryFormHandler.visibleOnRegList" id="bbb_list_category_is_visible_on_reg_list" name="is_visible_on_reg_list"></dsp:input>
<dsp:input type="text" bean="BBBCategoryFormHandler.childPrdNeededToDisp" id="bbb_list_category_is_child_prd_needed_to_display" name="is_child_prd_needed_to_display"></dsp:input>
<dsp:input type="text" bean="BBBCategoryFormHandler.configComplete" id="bbb_list_category_is_config_complete" name="is_config_complete"></dsp:input>
<c:if test="${action eq 'insert'}"><dsp:input type="hidden" value="create" bean="BBBCategoryFormHandler.createCategory"></dsp:input></c:if>
<c:if test="${action eq 'update'}"><dsp:input type="submit" value="update" bean="BBBCategoryFormHandler.editCategory"></dsp:input></c:if>
<c:if test="${action eq 'delete'}"><dsp:input type="submit" value="remove" bean="BBBCategoryFormHandler.removeCategory"></dsp:input></c:if>

<input type=submit></input>
</dsp:form>

    
</dsp:page>
