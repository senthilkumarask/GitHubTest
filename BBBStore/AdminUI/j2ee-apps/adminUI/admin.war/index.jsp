<%@ page language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="dspTaglib" prefix="dsp"%>

<%	
	request.setAttribute("theme", getCookie(request.getCookies(), "theme", "redmond").getValue());
	request.setAttribute("data_source", getCookie(request.getCookies(), "data_source", "MiscDS").getValue());
%>

<dsp:page>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
<dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
<c:choose>
  <c:when test="${isTransient}">
      <dsp:droplet name="/atg/dynamo/droplet/Redirect">   <dsp:param name="url" value="${contextPath}/login.jsp"/> </dsp:droplet>
  </c:when>
  <c:otherwise>
      <dsp:getvalueof var="user" bean="/atg/userprofiling/Profile.firstName"/>
  </c:otherwise>
</c:choose>

<html lang="us">
<head>
	<meta charset="utf-8">
	<meta http-equiv="Cache-Control" content="no-store,no-cache,must-revalidate"> 
	<meta http-equiv="Pragma" content="no-cache"> 
	<meta http-equiv="Expires" content="-1">
	<meta http-equiv="X-UA-Compatible" content="IE=EDGE" />

	<title>Beyond&#174; Catalog Configuration Wizard</title>

<script src="jquery-ui/external/jquery/jquery.js"></script>
<script src="jquery-ui/jquery-ui.js"></script>
<script src="scripts/jquery.ui.autocomplete.html.js"></script>
<script src="scripts/common.js"></script>
<script src="jstree/dist/jstree.min.js"></script>
<script src="scripts/jstreehacks.js"></script>
<script src="scripts/tree.js"></script>
<script src="scripts/forms.js"></script>
<script src="scripts/messages.js"></script>


<link id="theme" href="jquery-ui/themes/<c:out value="${theme}"></c:out>/jquery-ui.css" rel="stylesheet">
<link rel="stylesheet" href="jstree/dist/themes/default/style.min.css" />
<link rel="stylesheet" href="css/styles.css" />
<link rel="shortcut icon" type="image/x-icon" href="images/favicon.ico" />

<script>

var global = {

	config:"json/config.json",

	getNextSequence:function() {
	
		this.sequence = this.sequence || 1000;
		return this.sequence++;
	},
	state: {
		site_flag:1
	},
	isAdmin:false
};
<c:if test="${not empty pageContext.request.userPrincipal}">

    <c:if test="${pageContext.request.isUserInRole('admin')}">
	global.isAdmin = true;
    </c:if>

</c:if>

$(function() {

	$.ajaxSetup({
    		timeout: 120000
	});

	$( "#tabs" ).tabs({
		activate: function( event, ui ) {

			global.state.site_flag = ui.newTab.index() + 1;
		
			if (ui.newTab.index() == 0) {

			}
			if (ui.newTab.index() == 1) {


			}
			if (ui.newTab.index() == 2) {

			}
			if (ui.newTab.index() == 3) {
				global.state.site_flag = 5;
			}

			ui.oldTab[0].style.borderWidth = '';
			ui.oldTab[0].style.borderColor = '';
			ui.newTab[0].style.borderWidth = '1px 1px 0px 1px';
			ui.newTab[0].style.borderColor = '#000000';

			$.jstree.reference('#list-tree').refresh();

			$('#list-tree').focus().blur();
		},
		create: function( event, ui ) {

		 	ui.tab[0].style.borderWidth = '1px 1px 0px 1px';
			ui.tab[0].style.borderColor = '#000000';
		}
	});

	global.$alertDialog = $("#alertDialog");
	global.$confirmDialog = $("#confirmDialog");
	global.$list_tree = $('#list-tree');

	// get the config object
	getJson(function(data) {

		global.config = data;

		if (global.config.showDBC) {
			$("#dbc").show();
			createCookie("data_source", $("#data_source option:selected").text());
		}

		// get site data and cache to global object
		getJson(function(data) {global.site = data;}, global.config.api.BBB_SITES_select);

		// get list_type data and cache to global object
		getJson(function(result) {
			
			global.list_type = result.data;

			//TODO: remove this sort, the backend should do it
			if (global.list_type && global.list_type.length) {
				global.list_type.sort(function(a, b) {

					if (a.type_name == b.type_name) {
						if (a.sub_type_name > b.sub_type_name) return 1;
						if (a.sub_type_name < b.sub_type_name) return -1;
						return 0;
					}
					if (a.type_name > b.type_name) return 1;
					return -1;
				});
			}

		}, global.config.api.BBB_LIST_TYPE_getListTypes);

		listTreeConfig(global.$list_tree);

	}, global.config);
});
</script>
</head>
<body>

<div id="header" style="height:64px;">
	<div style="float:left">
		<a href="."><img id="logo" border="0" src="images/logo-redmond.png" style="height:60px;padding-top:0px"></a>
	</div>
	<div style="float:right;">

	<table style="margin-top:0px">
	<tr>

	<c:if test="${not isTransient}">
	<td>
		<dsp:form action="login.jsp" method="POST" style="display:inline">
		<dsp:input bean="/atg/userprofiling/InternalProfileFormHandler.logout" value="Logout" id="logout" type="hidden"></dsp:input>
		<button class="ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only"  role="button" aria-disabled="false" title="Logout <c:out value="${user}"/>" onclick="this.form.submit();return false;">
			<span class="ui-button-icon-primary ui-icon ui-icon-person"></span>
			<span class="ui-button-text">Logout</span>
		</button>
		</dsp:form>
	</td>
	</c:if>

	<td id="dbc" style="display:none">
		<div style="padding:5px 5px;" class="ui-widget ui-widget-content ui-corner-all">
			<div class="ui-widget-header">Database</div>
			<select id="data_source" name="data_source" onchange="return changeDataSource(saveDataSource(getSelectedOption(this)));">
			<c:set var="index" scope="request" value="${0}"/>
			<c:forTokens items="ATGTST3,ATGDEV,ATGMDEV,ATGDEV2,ATGDEV3,ATGDEV4,ATGDEV5,ATGBREAKEAST,ATGWEST,BBBRACP1" delims="," var="item">
				<option  value="<c:out value="${index}"/>" <c:out value="${item == data_source?'SELECTED':''}"/>><c:out value="${item}"/></option>
				<c:set var="index" scope="request" value="${index+1}"/>
			</c:forTokens>
			</select>
		</div>
	</td>
	<td>
		<div style="padding:5px 5px;" class="ui-widget ui-widget-content ui-corner-all">
			<div class="ui-widget-header">Theme</div>
			<select id="theme" name="theme" onchange="return saveTheme(changeTheme(getSelectedOption(this)));">
			<c:set var="index" scope="request" value="${0}"/>
			<c:forTokens items="redmond,cupertino,ui-lightness,dot-luv,dark-hive,black-tie,blitzer,smoothness,flick,vader,eggplant,start,pepper-grinder,hot-sneaks,excite-bike,sunny,le-frog,overcast,humanity,south-street,mint-choc,trontastic,swanky-purse" delims="," var="item">
				<option  value="<c:out value="${index}"/>" <c:out value="${item == theme?'SELECTED':''}"/>><c:out value="${item}"/></option>
				<c:set var="index" scope="request" value="${index+1}"/>
			</c:forTokens>
			</select>
		</div>
	</td>
	</tr>
	</table>

	</div>
</div>

<div id="tabs">
	<ul>
		<li><a href="#tabs-1"><img src="images/bed-bath-beyond.png" height="24" border="0"></a></li>
		<li><a href="#tabs-1"><img src="images/buy-buy-baby.png" height="24" border="0"></a></li>
		<li><a href="#tabs-1"><img src="images/bed-bath-beyond-ca.png" height="24" border="0"></a></li>
		<li style="display:none"><a href="#tabs-1"><img src="images/bed-bath-beyond-mx.png" height="24" border="0"></a></li>
	</ul>
	<div id="tabs-1">
		<img src="images/start-here.png" style="height:44;top:0px;left:0px;position:relative">
		<div id="list-tree"></div>
	</div>
</div>

<form id="listForm" name="listForm" style="display:none">
    <table style="font:inherit;width:100%">
    <tr>
    <td nowrap colspan="2" valign="top">
    <div class="input-field-container">
        <div class="input-field-label" style="font-weight:bold;">List Name</div>
        <div class="input-field-wrapper"><input style="width:100%;" id="listForm_display_name" type="text" name="listForm_display_name"/></div>
        <div class="input-field-error" id="listForm_display_name-error"></div>
    </div>
    </td>
    </tr>
    <tr>
    <td nowrap valign="top">
    <div class="input-field-container">
        <div class="input-field-wrapper"><input type="checkbox" checked id="listForm_is_enabled" name="listForm_is_enabled"/><label for="listForm_is_enabled">Enabled</label></div>
        <div class="input-field-error" id="listForm_is_enabled-error"></div>
    </div>
    </td>
    <td nowrap valign="top">
    <div class="input-field-container">
        <div class="input-field-wrapper"><input type="checkbox" id="listForm_allow_duplicates" name="listForm_allow_duplicates"/><label for="listForm_allow_duplicates">Allow Duplicates</label></div>
        <div class="input-field-error" id="listForm_allow_duplicates-error"></div>
    </div>
    </td>
    </tr>
    <tr>
    <td nowrap valign="top">
    <div class="input-field-container">
        <div class="input-field-label" style="font-weight:bold;">Site</div>
        <div class="input-field-wrapper"><select style="width:100%;" size="3" multiple id="listForm_site_flag" type="text" name="listForm_site_flag"></select></div>
        <div class="input-field-error" id="listForm_site_flag-error"></div>
    </div>
    </td>
    <td nowrap valign="top">
    <div class="input-field-container">
        <div class="input-field-label" style="font-weight:bold;">Type</div>
        <div class="input-field-wrapper"><select style="width:100%;" id="listForm_type_name" type="text" name="listForm_type_name"></select></div>
        <div class="input-field-error" id="listForm_type_name-error"></div>
    </div>
    <div class="input-field-container" style="font-weight:bold;">
        <div class="input-field-label">Sub-Type</div>
        <div class="input-field-wrapper"><select style="width:100%;" id="listForm_sub_type_code" type="text" name="listForm_sub_type_code"></select></div>
        <div class="input-field-error" id="listForm_sub_type_code-error"></div>
    </div>
    </td>
    </tr>
    </table>
    <div style="margin-top:10px;text-align:center;font-weight:bold;color:red" id="listForm_error"></div>
    <div style="text-align:center;font-weight:bold" id="listForm_message"></div>
</form>
<form id="selectCategoryForm" name="selectCategoryForm" style="display:none">
    <fieldset style="margin-top:5px;">
        <legend id="selectCategoryForm_searchLabel">Search</legend>
	<table style="font:inherit;width:100%">
    	<tr>
    	<td nowrap colspan="2" valign="top">
		<div class="input-field-container">
            		<div class="input-field-wrapper"><input style="width:100%;" id="selectCategoryForm_searchString" type="text" name="selectCategoryForm_searchString" placeholder="type name to search, <space> for all" autocomplete="off"/></div>
            		<div class="input-field-error" id="selectCategoryForm_searchString-error"></div>
        	</div>
	</td>
	</tr>
    	<tr>
    	<td nowrap valign="top">
    		<div class="input-field-container">
        		<div class="input-field-wrapper"><input type="radio" checked id="selectCategoryForm_searchField1" value="name" name="selectCategoryForm_searchField"/><label for="selectCategoryForm_searchField">Name</label></div>
    		</div>
    	</td>
    	<td nowrap valign="top">
    		<div class="input-field-container">
        		<div class="input-field-wrapper"><input type="radio" id="selectCategoryForm_searchField2" value="id" name="selectCategoryForm_searchField"/><label for="selectCategoryForm_searchField">ID</label></div>
    		</div>
    	</td>
    	</tr>
	</table>
    </fieldset>
    <fieldset id="selectCategoryForm_selectedFieldset" disabled style="color:silver;margin-top:15px;">
        <legend id="selectCategoryForm_selectedLabel">Selected</legend>
	<ul style="min-height:30px;font:inherit;font-weight:bold;padding:10px 10px;margin: 0px 0px 0px 5px;" class="category" id="selectCategoryForm_selected">

	</ul>
    </fieldset>
    <div style="margin-top:5px;text-align:center;font-weight:bold;color:red" id="selectCategoryForm_error"></div>
    <div style="text-align:center;font-weight:bold" id="selectCategoryForm_message"></div>
</form>
<form id="selectEPHForm" name="selectEPHForm" style="display:none">
    <fieldset style="margin-top:5px;">
        <legend id="selectEPHForm_searchLabel">Search</legend>
	<table style="font:inherit;width:100%">
    	<tr>
    	<td nowrap colspan="2" valign="top">
		<div class="input-field-container">
            		<div class="input-field-wrapper"><input style="width:100%;" id="selectEPHForm_searchString" type="text" name="selectEPHForm_searchString" placeholder="type name to search, <space> for all" autocomplete="off"/></div>
            		<div class="input-field-error" id="selectEPHForm_searchString-error"></div>
        	</div>
	</td>
	</tr>
    	<tr>
    	<td nowrap valign="top">
    		<div class="input-field-container">
        		<div class="input-field-wrapper"><input type="radio" checked id="selectEPHForm_searchField1" value="name" name="selectEPHForm_searchField"/><label for="selectEPHForm_searchField">Name</label></div>
    		</div>
    	</td>
    	<td nowrap valign="top">
    		<div class="input-field-container">
        		<div class="input-field-wrapper"><input type="radio" id="selectEPHForm_searchField2" value="id" name="selectEPHForm_searchField"/><label for="selectEPHForm_searchField">ID</label></div>
    		</div>
    	</td>
    	</tr>
	</table>
    </fieldset>
    <fieldset id="selectEPHForm_selectedFieldset" disabled style="color:silver;margin-top:15px;">
        <legend id="selectEPHForm_selectedLabel">Selected</legend>
	<ul style="min-height:30px;font:inherit;font-weight:bold;padding:10px 10px;margin: 0px 0px 0px 5px;" class="eph" id="selectEPHForm_selected">

	</ul>
    </fieldset>
    <div class="input-field-container" style="float:right" id="selectEPHForm_include-container">
        <div class="input-field-wrapper"><input type="checkbox" id="selectEPHForm_includeAll" name="selectEPHForm_includeAll"/><label for="selectEPHForm_includeAll">Include All</label></div>
        <div class="input-field-error" id="selectEPHForm_includeAll-error"></div>
    </div>
    <div class="input-field-container">
	<a id="selectEPHForm_selected-remove" href="#">remove</a>
    </div>
    <div style="margin-top:5px;text-align:center;font-weight:bold;color:red" id="selectEPHForm_error"></div>
    <div style="text-align:center;font-weight:bold" id="selectEPHForm_message"></div>
</form>
<form id="selectJDAForm" name="selectJDAForm" style="display:none">
    <fieldset style="margin-top:5px;">
        <legend id="selectJDAForm_searchLabel">Search</legend>
	<table style="font:inherit;width:100%">
    	<tr>
    	<td nowrap colspan="2" valign="top">
		<div class="input-field-container">
            		<div class="input-field-wrapper"><input style="width:100%;" id="selectJDAForm_searchString" type="text" name="selectJDAForm_searchString" placeholder="type name to search, <space> for all" autocomplete="off"/></div>
            		<div class="input-field-error" id="selectJDAForm_searchString-error"></div>
        	</div>
	</td>
	</tr>
    	<tr>
    	<td nowrap valign="top">
    		<div class="input-field-container">
        		<div class="input-field-wrapper"><input type="radio" checked id="selectJDAForm_searchField1" value="name" name="selectJDAForm_searchField"/><label for="selectJDAForm_searchField">Name</label></div>
    		</div>
    	</td>
    	<td nowrap valign="top">
    		<div class="input-field-container">
        		<div class="input-field-wrapper"><input type="radio" id="selectJDAForm_searchField2" value="id" name="selectJDAForm_searchField"/><label for="selectJDAForm_searchField">ID</label></div>
    		</div>
    	</td>
    	</tr>
	</table>
    </fieldset>
    <fieldset id="selectJDAForm_selectedFieldset" disabled style="color:silver;margin-top:15px;">
        <legend id="selectJDAForm_selectedLabel">Selected</legend>
	<ul style="min-height:30px;font:inherit;font-weight:bold;padding:10px 10px;margin: 0px 0px 0px 5px;" class="JDA" id="selectJDAForm_selected">

	</ul>
    </fieldset>
    <div class="input-field-container">
	<a id="selectJDAForm_selected-remove" href="#">remove</a>
    </div>
    <div style="margin-top:5px;text-align:center;font-weight:bold;color:red" id="selectJDAForm_error"></div>
    <div style="text-align:center;font-weight:bold" id="selectJDAForm_message"></div>
</form>
<form id="selectSKUForm" name="selectSKUForm" style="display:none">
    <fieldset style="margin-top:5px;">
        <legend>Search</legend>
	<table style="font:inherit;width:100%">
    	<tr>
    	<td nowrap colspan="3" valign="top">
		<div class="input-field-container">
            		<div class="input-field-wrapper"><input style="width:100%;" id="selectSKUForm_searchString" type="text" name="selectSKUForm_searchString" placeholder="" autocomplete="off"/></div>
            		<div class="input-field-error" id="selectSKUForm_searchString-error"></div>
        	</div>
	</td>
	</tr>
    	<tr>
    	<td nowrap valign="top">
    		<div class="input-field-container">
        		<div class="input-field-wrapper"><input type="radio" id="selectSKUForm_searchField1" value="name" name="selectSKUForm_searchField"/><label for="selectSKUForm_searchField">Name</label></div>
    		</div>
    	</td>
    	<td nowrap valign="top">
    		<div class="input-field-container">
        		<div class="input-field-wrapper"><input type="radio" id="selectSKUForm_searchField2" value="id" name="selectSKUForm_searchField"/><label for="selectSKUForm_searchField">ID</label></div>
    		</div>
    	</td>
    	<td nowrap valign="top">
    		<div class="input-field-container">
        		<div class="input-field-wrapper"><input type="radio" checked id="selectSKUForm_searchField3" value="none" name="selectSKUForm_searchField"/><label for="selectSKUForm_searchField">None</label></div>
    		</div>
    	</td>
    	</tr>
	</table>
    </fieldset>
    <fieldset id="selectSKUForm_selectedFieldset" disabled style="color:silver;margin-top:15px;">
        <legend>Selected</legend>
    <!--<div style="margin-top:10px;height:30px;text-align:center;font-weight:bold" id="XselectSKUForm_selected"></div>-->
	<ul style="min-height:30px;font:inherit;font-weight:bold;padding:10px 10px;margin: 0px 0px; 0px 5px" class="sku" id="selectSKUForm_selected">

	</ul>
    </fieldset>
    <div class="input-field-container">
        <div class="input-field-wrapper"><input type="checkbox" checked id="selectSKUForm_include" name="selectSKUForm_include"/><label for="selectSKUForm_include">Include</label></div>
        <div class="input-field-error" id="selectSKUForm_include-error"></div>
    </div>
    <div style="margin-top:5px;text-align:center;font-weight:bold;color:red" id="selectSKUForm_error"></div>
    <div style="text-align:center;font-weight:bold" id="selectSKUForm_message"></div>
</form>
<form id="selectRuleForm" name="selectRuleForm" style="display:none">
    <fieldset style="margin-top:5px;">
        <legend>Search</legend>
	<table style="font:inherit;width:100%">
    	<tr>
    	<td nowrap colspan="3" valign="top">
		<div class="input-field-container">
            		<div class="input-field-wrapper"><input style="width:100%;" id="selectRuleForm_searchString" type="text" name="selectRuleForm_searchString" placeholder="type name to search, <space> for all" autocomplete="off"/></div>
            		<div class="input-field-error" id="selectRuleForm_searchString-error"></div>
        	</div>
	</td>
	</tr>
    	<tr>
    	<td nowrap valign="top">
    		<div class="input-field-container">
        		<div class="input-field-wrapper"><input type="radio" checked id="selectRuleForm_searchField1" value="name" name="selectRuleForm_searchField"/><label for="selectRuleForm_searchField">Name</label></div>
    		</div>
    	</td>
    	<td nowrap valign="top">
    		<div class="input-field-container">
        		<div class="input-field-wrapper"><input type="radio" id="selectRuleForm_searchField2" value="id" name="selectRuleForm_searchField"/><label for="selectRuleForm_searchField">ID</label></div>
    		</div>
    	</td>
    	<td nowrap valign="top" style="display:none">
    		<div class="input-field-container">
        		<div class="input-field-wrapper"><input type="radio" id="selectRuleForm_searchField3" value="none" name="selectRuleForm_searchField"/><label for="selectRuleForm_searchField">None</label></div>
    		</div>
    	</td>
    	</tr>
	</table>
    </fieldset>
    <fieldset id="selectRuleForm_selectedFieldset" disabled style="color:silver;margin-top:15px;">
        <legend>Selected</legend>
    <!--<div style="margin-top:10px;min-height:30px;font-weight:bold" id="XselectRuleForm_selected"></div>-->
	<ul style="min-height:30px;font:inherit;font-weight:bold;padding:10px 10px;margin: 0px 0px; 0px 5px" class="sku" id="selectRuleForm_selected">

	</ul>
    </fieldset>
    <div style="margin-top:5px;text-align:center;font-weight:bold;color:red" id="selectRuleForm_error"></div>
    <div style="text-align:center;font-weight:bold" id="selectRuleForm_message"></div>
</form>
<form id="selectFacetForm" name="selectFacetForm" style="display:none">
    <fieldset style="margin-top:5px;">
        <legend>Search</legend>
	<table style="font:inherit;width:100%">
    	<tr>
    	<td nowrap colspan="2" valign="top">
		<div class="input-field-container">
            		<div class="input-field-wrapper"><input style="width:100%;" id="selectFacetForm_searchString" type="text" name="selectFacetForm_searchString" placeholder="type name to search, <space> for all" autocomplete="off"/></div>
            		<div class="input-field-error" id="selectFacetForm_searchString-error"></div>
        	</div>
	</td>
	</tr>
    	<tr>
    	<td nowrap valign="top">
    		<div class="input-field-container">
        		<div class="input-field-wrapper"><input type="radio" checked id="selectFacetForm_searchField1" value="name" name="selectFacetForm_searchField"/><label for="selectFacetForm_searchField">Name</label></div>
    		</div>
    	</td>
    	<td nowrap valign="top">
    		<div class="input-field-container">
        		<div class="input-field-wrapper"><input type="radio" id="selectFacetForm_searchField2" value="id" name="selectFacetForm_searchField"/><label for="selectFacetForm_searchField">ID</label></div>
    		</div>
    	</td>
    	</tr>
	</table>
    </fieldset>
    <fieldset id="selectFacetForm_selectedFieldset" disabled style="color:silver;margin-top:15px;">
        <legend>Selected</legend>
    <!--<div style="min-height:15px;margin-top:10px;height:15px;text-align:center;font-weight:bold" id="XselectFacetForm_selected"></div>-->
	<ul style="min-height:15px;font:inherit;font-weight:bold;padding:10px 10px;margin: 0px 0px 0px 5px;" class="facet" id="selectFacetForm_selected">

	</ul>
    	<div class="input-field-container">
        	<div class="input-field-label">Facet Values</div>
        	<div class="input-field-wrapper"><select style="width:100%" id="selectFacetForm_facetvalue" name="selectFacetForm_facetvalue"></select></div>
        	<div class="input-field-error" id="selectFacetForm_facetvalue-error"></div>
    	</div>
    </fieldset>
    <div style="margin-top:10px;text-align:center;font-weight:bold;color:red" id="selectFacetForm_error"></div>
    <div style="text-align:center;font-weight:bold" id="selectFacetForm_message"></div>
</form>
<form id="categoryForm" name="categoryForm" style="font-size:.90em;display:none;">
    <table cellpadding="1" cellspacing="1" style="font:inherit;width:100%">
    <tr>
    <td nowrap valign="top" width="50%">
    <div class="input-field-container">
        <div class="input-field-label" style="font-weight:bold;">Name</div>
        <div class="input-field-wrapper"><input style="width:100%;" id="categoryForm_name" type="text" name="categoryForm_name"/></div>
        <div class="input-field-error" id="categoryForm_name-error"></div>
    </div>
    </td>
    <td nowrap valign="top" width="50%">
    <div class="input-field-container">
        <div class="input-field-label" style="font-weight:bold;">Display Name</div>
        <div class="input-field-wrapper"><input style="width:100%;" id="categoryForm_display_name" type="text" name="categoryForm_display_name"/></div>
        <div class="input-field-error" id="categoryForm_display_name-error"></div>
    </div>
    </td>
    </tr>
    <tr>
    <td nowrap valign="top" colspan="2">

    <table cellpadding="1" cellspacing="1" style="font:inherit;width:100%">
    <tr>
    <td nowrap valign="top">
    <fieldset style="margin-top:0px;height:190px">
        <legend style="font-weight:bold;">Primary Category</legend>
        <fieldset style="margin-top:10px;">
            <legend style="font-weight:bold;">Search</legend>
	    <table style="font:inherit;width:100%">
    	    <tr>
    	    <td nowrap colspan="2" valign="top">
		<div class="input-field-container">
            		<div class="input-field-wrapper"><input style="width:100%;" id="categoryForm_primary_parent_cat_SearchString" type="text" name="categoryForm_primary_parent_cat_SearchString" placeholder="type name to search, <space> for all" autocomplete="off"/></div>
            		<div class="input-field-error" id="categoryForm_primary_parent_cat_SearchString-error"></div>
        	</div>
	    </td>
	    </tr>
    	    <tr>
    	    <td nowrap valign="top">
    		<div class="input-field-container">
        		<div class="input-field-wrapper"><input type="radio" checked id="categoryForm_primary_parent_cat_SearchField1" value="name" name="categoryForm_primary_parent_cat_SearchField"/><label for="categoryForm_primary_parent_cat_SearchField">Name</label></div>
    		</div>
    	    </td>
    	    <td nowrap valign="top">
    		<div class="input-field-container">
        		<div class="input-field-wrapper"><input type="radio" id="categoryForm_primary_parent_cat_SearchField2" value="id" name="categoryForm_primary_parent_cat_SearchField"/><label for="categoryForm_primary_parent_cat_SearchField">ID</label></div>
    		</div>
    	    </td>
    	    </tr>
	    </table>
        </fieldset>
        <fieldset id="categoryForm_primary_parent_cat_SelectedFieldset" disabled style="color:silver;margin-top:10px;border:1px solid silver">
            <legend>Selected</legend>
    	    <div style="margin-top:10px;margin-bottom:10px;height:15px;text-align:center;font-weight:bold" id="categoryForm_primary_parent_cat_Selected"></div>
        </fieldset>
	<div class="input-field-container">
		<a id="categoryForm_primary_parent_cat-remove" href="#">remove</a>
        </div>
	<input type="hidden" id="categoryForm_primary_parent_cat_id" name="categoryForm_primary_parent_cat_id"/>
	<div class="input-field-error" id="categoryForm_primary_parent_cat_id-error"></div>
    </fieldset>
    </td>
    <td nowrap valign="top">
    <fieldset style="margin-top:0px;height:190px">
    <legend style="font-weight:bold;">Attributes</legend>
    <div class="input-field-container">
        <div class="input-field-wrapper"><input type="checkbox" id="categoryForm_is_enabled" name="categoryForm_is_enabled"/><label for="categoryForm_is_enabled">Enabled</label></div>
        <div class="input-field-error" id="categoryForm_is_enabled-error"></div>
    </div>
    <div class="input-field-container">
        <div class="input-field-wrapper"><input type="checkbox" id="categoryForm_is_config_complete" name="categoryForm_is_config_complete"/><label for="categoryForm_is_config_complete">Config Complete</label></div>
        <div class="input-field-error" id="categoryForm_is_config_complete-error"></div>
    </div>
    <div class="input-field-container">
        <div class="input-field-wrapper"><input type="checkbox" id="categoryForm_is_visible_on_checklist" name="categoryForm_is_visible_on_checklist"/><label for="categoryForm_is_visible_on_checklist">Visible On Checklist</label></div>
        <div class="input-field-error" id="categoryForm_is_visible_on_checklist-error"></div>
    </div>
    <div class="input-field-container">
        <div class="input-field-wrapper"><input type="checkbox" id="categoryForm_is_visible_on_reg_list" name="categoryForm_is_visible_on_reg_list"/><label for="categoryForm_is_visible_on_reg_list">Visible On Reg List</label></div>
        <div class="input-field-error" id="categoryForm_is_visible_on_reg_list-error"></div>
    </div>
    <div class="input-field-container">
        <div class="input-field-wrapper"><input type="checkbox" id="categoryForm_is_child_prd_needed_to_disp" name="categoryForm_is_child_prd_needed_to_disp"/><label for="categoryForm_is_child_prd_needed_to_disp">Child Prd Needed To Display</label></div>
        <div class="input-field-error" id="categoryForm_is_child_prd_needed_to_disp-error"></div>
    </div>
    <div class="input-field-container">
        <div class="input-field-wrapper"><input type="radio" id="categoryForm_is_deleted" disabled name="categoryForm_is_deleted" value="1"/><label for="categoryForm_is_deleted">Deleted</label></div>
        <div class="input-field-error" id="categoryForm_is_deleted-error"></div>
    </div>
    </fieldset>
    </td>
    <td nowrap valign="top">

    <fieldset style="margin-top:0px;height:190px">
        <legend style="font-weight:bold;">Facet Package Count</legend>
        <fieldset style="margin-top:10px;">
            <legend style="font-weight:bold;">Search</legend>
	    <table style="font:inherit;width:100%">
    	    <tr>
    	    <td nowrap colspan="2" valign="top">
		<div class="input-field-container">
            		<div class="input-field-wrapper"><input style="width:100%;" id="categoryForm_facet_id_pkg_cnt_SearchString" type="text" name="categoryForm_facet_id_pkg_cnt_SearchString" placeholder="type name to search, <space> for all" autocomplete="off"/></div>
            		<div class="input-field-error" id="categoryForm_facet_id_pkg_cnt_SearchString-error"></div>
        	</div>
	    </td>
	    </tr>
    	    <tr>
    	    <td nowrap valign="top">
    		<div class="input-field-container">
        		<div class="input-field-wrapper"><input type="radio" checked id="categoryForm_facet_id_pkg_cnt_SearchField1" value="name" name="categoryForm_facet_id_pkg_cnt_SearchField"/><label for="categoryForm_facet_id_pkg_cnt_SearchField">Name</label></div>
    		</div>
    	    </td>
    	    <td nowrap valign="top">
    		<div class="input-field-container">
        		<div class="input-field-wrapper"><input type="radio" id="categoryForm_facet_id_pkg_cnt_SearchField2" value="id" name="categoryForm_facet_id_pkg_cnt_SearchField"/><label for="categoryForm_facet_id_pkg_cnt_SearchField">ID</label></div>
    		</div>
    	    </td>
    	    </tr>
	    </table>
        </fieldset>
        <fieldset id="categoryForm_facet_id_pkg_cnt_SelectedFieldset" disabled style="color:silver;margin-top:10px;border:1px solid silver">
            <legend>Selected</legend>
    	    <div style="margin-top:10px;margin-bottom:10px;height:15px;text-align:center;font-weight:bold" id="categoryForm_facet_id_pkg_cnt_Selected"></div>
        </fieldset>
	<div class="input-field-container">
		<a id="categoryForm_facet_id_pkg_cnt-remove" href="#">remove</a>
        </div>
	<input type="hidden" id="categoryForm_facet_id_pkg_cnt" name="categoryForm_facet_id_pkg_cnt"/>
	<div class="input-field-error" id="categoryForm_facet_id_pkg_cnt-error"></div>
    </fieldset>
    </td>
    </tr>
    </table>
    </td>

    </tr>
    <tr>
    <td nowrap valign="top">


    <fieldset style="margin-top:0px;">
        <legend style="font-weight:bold;">Category URL</legend>
	<table border="0" cellpadding="1" cellspacing="0" style="font:inherit;font-size:.9em;width:100%">
        <tr bgcolor="#D0D0D0">
	    <td nowrap rowspan="5" align="center">&nbsp;US&nbsp;</td><td colspan="4"></td>
	</tr>
        <tr bgcolor="#D0D0D0">
            <td valign="middle"><a href="#" onclick="return (this.getAttribute('href') != '#');" id="categoryForm_category_url_link" target="categoryURL"><img src="images/computer.png"></a></td>
            <td width="100%"><input style="width:100%;" id="categoryForm_category_url" type="text" name="categoryForm_category_url" autocomplete="off"/></td>
	    <td>
		<input id="categoryForm_url_override" name="categoryForm_url_override" style="margin:0px 0px" type="checkbox"/>
	    </td>
	    <td>
		<button style="height:15px;width:15px" type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only"  role="button" aria-disabled="false" title="open URL" onclick="return openCategoryURL('categoryForm_category_url', 1, 0);">
			<span class="ui-button-icon-primary ui-icon ui-icon-extlink"></span>
		</button>
	    </td>
	</tr>
	<tr bgcolor="#D0D0D0">
            <td colspan="4" align="center" class="input-field-error" id="categoryForm_category_url-error"></td>
        </tr>
        <tr bgcolor="#D0D0D0">
            <td valign="middle"><a href="#" onclick="return (this.getAttribute('href') != '#');" id="categoryForm_mob_category_url_link" target="categoryURL"><img src="images/mobile.png"></a></td>
            <td width="100%"><input style="width:100%;" id="categoryForm_mob_category_url" type="text" name="categoryForm_mob_category_url" autocomplete="off"/></td>
	    <td>
		<input id="categoryForm_mob_url_override" name="categoryForm_mob_url_override" style="margin:0px 0px" type="checkbox"/>
	    </td>
	    <td>
		<button style="height:15px;width:15px" type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only"  role="button" aria-disabled="false" title="open URL" onclick="return openCategoryURL('categoryForm_mob_category_url', 1, 1);">
			<span class="ui-button-icon-primary ui-icon ui-icon-extlink"></span>
		</button>
	    </td>
	</tr>
	<tr bgcolor="#D0D0D0">
            <td colspan="4" align="center" class="input-field-error" id="categoryForm_mob_category_url-error"></td>
        </tr>

        <tr>
	    <td nowrap rowspan="5" align="center">&nbsp;BABY&nbsp;</td><td colspan="4"></td>
	</tr>
        <tr>
            <td valign="middle"><a href="#" onclick="return (this.getAttribute('href') != '#');" id="categoryForm_baby_category_url_link" target="categoryURL"><img src="images/computer.png"></a></td>
            <td class="input-field-wrapper"><input style="width:100%;" id="categoryForm_baby_category_url" type="text" name="categoryForm_baby_category_url" autocomplete="off"/></td>
	    <td>
		<input id="categoryForm_baby_url_override" name="categoryForm_baby_url_override" style="margin:0px 0px" type="checkbox"/>
	    </td>
	    <td>
		<button style="height:15px;width:15px" type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only"  role="button" aria-disabled="false" title="open URL" onclick="return openCategoryURL('categoryForm_baby_category_url', 2, 0);">
			<span class="ui-button-icon-primary ui-icon ui-icon-extlink"></span>
		</button>
	    </td>
        </tr>
        <tr>    
	    <td colspan="4" align="center" class="input-field-error" id="categoryForm_baby_category_url-error"></td>
        </tr>

        <tr>
            <td valign="middle"><a href="#" onclick="return (this.getAttribute('href') != '#');" id="categoryForm_mob_baby_category_url_link" target="categoryURL"><img src="images/mobile.png"></a></td>
            <td class="input-field-wrapper"><input style="width:100%;" id="categoryForm_mob_baby_category_url" type="text" name="categoryForm_mob_baby_category_url" autocomplete="off"/></td>
	    <td>
		<input id="categoryForm_mob_baby_url_override" name="categoryForm_mob_baby_url_override" style="margin:0px 0px" type="checkbox"/>
	    </td>
	    <td>
		<button style="height:15px;width:15px" type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only"  role="button" aria-disabled="false" title="open URL" onclick="return openCategoryURL('categoryForm_mob_baby_category_url', 2, 1);">
			<span class="ui-button-icon-primary ui-icon ui-icon-extlink"></span>
		</button>
	    </td>
        </tr>
        <tr>    
	    <td colspan="4" align="center" class="input-field-error" id="categoryForm_mob_baby_category_url-error"></td>
        </tr>

        <tr bgcolor="#D0D0D0">
	    <td nowrap rowspan="5" align="center">&nbsp;CA&nbsp;</td><td colspan="4"></td>
	</tr>
        <tr bgcolor="#D0D0D0">
            <td valign="middle"><a href="#" onclick="return (this.getAttribute('href') != '#');" id="categoryForm_ca_category_url_link" target="categoryURL"><img src="images/computer.png"></a></td>
            <td><input style="width:100%;" id="categoryForm_ca_category_url" type="text" name="categoryForm_ca_category_url" autocomplete="off"/></td>
	    <td>
		<input id="categoryForm_ca_url_override" name="categoryForm_ca_url_override" style="margin:0px 0px" type="checkbox"/>
	    </td>
	    <td>
		<button style="height:15px;width:15px" type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only"  role="button" aria-disabled="false" title="open URL" onclick="return openCategoryURL('categoryForm_ca_category_url', 3, 0);">
			<span class="ui-button-icon-primary ui-icon ui-icon-extlink"></span>
		</button>
	    </td>
        </tr>
        <tr bgcolor="#D0D0D0">
            <td colspan="4" align="center" class="input-field-error" id="categoryForm_ca_category_url-error"></td>
        </tr>

        <tr bgcolor="#D0D0D0">
            <td valign="middle"><a href="#" onclick="return (this.getAttribute('href') != '#');" id="categoryForm_mob_ca_category_url_link" target="categoryURL"><img src="images/mobile.png"></a></td>
            <td><input style="width:100%;" id="categoryForm_mob_ca_category_url" type="text" name="categoryForm_mob_ca_category_url" autocomplete="off"/></td>
	    <td>
		<input id="categoryForm_mob_ca_url_override" name="categoryForm_mob_ca_url_override" style="margin:0px 0px" type="checkbox"/>
	    </td>
	    <td>
		<button style="height:15px;width:15px" type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only"  role="button" aria-disabled="false" title="open URL" onclick="return openCategoryURL('categoryForm_mob_ca_category_url', 3, 1);">
			<span class="ui-button-icon-primary ui-icon ui-icon-extlink"></span>
		</button>
	    </td>
        </tr>
        <tr bgcolor="#D0D0D0">
            <td colspan="4" align="center" class="input-field-error" id="categoryForm_mob_ca_category_url-error"></td>
        </tr>

        <tr>
	    <td nowrap rowspan="3" align="center">&nbsp;US-TBS&nbsp;</td><td colspan="4"></td>
	</tr>
        <tr>
            <td valign="middle"><a href="#" onclick="return (this.getAttribute('href') != '#');" id="categoryForm_tbs_category_url_link" target="categoryURL"><img src="images/computer.png"></a></td>
            <td><input style="width:100%;" id="categoryForm_tbs_category_url" type="text" name="categoryForm_tbs_category_url" autocomplete="off"/></td>
	    <td>
		<input id="categoryForm_tbs_url_override" name="categoryForm_tbs_url_override" style="margin:0px 0px" type="checkbox"/>
	    </td>
	    <td>
		<button style="height:15px;width:15px" type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only"  role="button" aria-disabled="false" title="open URL" onclick="return openCategoryURL('categoryForm_tbs_category_url', 6, 0);">
			<span class="ui-button-icon-primary ui-icon ui-icon-extlink"></span>
		</button>
	    </td>
        </tr>
        <tr>
            <td colspan="5" align="center" class="input-field-error" id="categoryForm_tbs_category_url-error"></td>
        </tr>

        <tr bgcolor="#D0D0D0">
	    <td nowrap rowspan="3" align="center">&nbsp;BAB-TBS&nbsp;</td><td colspan="4"></td>
	</tr>
        <tr bgcolor="#D0D0D0">
            <td valign="middle"><a href="#" onclick="return (this.getAttribute('href') != '#');" id="categoryForm_tbs_baby_category_url_link" target="categoryURL"><img src="images/computer.png"></a></td>
            <td><input style="width:100%;" id="categoryForm_tbs_baby_category_url" type="text" name="categoryForm_tbs_baby_category_url" autocomplete="off"/></td>
	    <td>
		<input id="categoryForm_tbs_baby_url_override" name="categoryForm_tbs_baby_url_override" style="margin:0px 0px" type="checkbox"/>
	    </td>
	    <td>
		<button style="height:15px;width:15px" type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only"  role="button" aria-disabled="false" title="open URL" onclick="return openCategoryURL('categoryForm_tbs_baby_category_url', 7, 0);">
			<span class="ui-button-icon-primary ui-icon ui-icon-extlink"></span>
		</button>
	    </td>
        </tr>
        <tr bgcolor="#D0D0D0">
            <td colspan="5" align="center" class="input-field-error" id="categoryForm_tbs_baby_category_url-error"></td>
        </tr>

        <tr>
	    <td nowrap rowspan="3" align="center">&nbsp;CA-TBS&nbsp;</td><td colspan="4"></td>
	</tr>
        <tr>
            <td valign="middle"><a href="#" onclick="return (this.getAttribute('href') != '#');" id="categoryForm_tbs_ca_category_url_link" target="categoryURL"><img src="images/computer.png"></a></td>
            <td><input style="width:100%;" id="categoryForm_tbs_ca_category_url" type="text" name="categoryForm_tbs_ca_category_url" autocomplete="off"/></td>
	    <td>
		<input id="categoryForm_tbs_ca_url_override" name="categoryForm_tbs_ca_url_override" style="margin:0px 0px" type="checkbox"/>
	    </td>
	    <td>
		<button style="height:15px;width:15px" type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only"  role="button" aria-disabled="false" title="open URL" onclick="return openCategoryURL('categoryForm_tbs_ca_category_url', 8, 0);">
			<span class="ui-button-icon-primary ui-icon ui-icon-extlink"></span>
		</button>
	    </td>
        </tr>
        <tr>
            <td colspan="5" align="center" class="input-field-error" id="categoryForm_tbs_ca_category_url-error"></td>
        </tr>

        </table>
    </fieldset>

    </td>
    <td nowrap valign="top">
    <fieldset style="margin-top:0px;">
        <legend style="font-weight:bold;">Image URL</legend>
	<table border="0" cellpadding="1" cellspacing="0" style="font:inherit;font-size:.9em;width:100%">
        <tr bgcolor="#D0D0D0">
	    <td nowrap rowspan="5" align="center">&nbsp;US&nbsp;</td><td colspan="4"></td>
	</tr>
        <tr bgcolor="#D0D0D0">
            <td valign="middle"><a href="#" onclick="return (this.getAttribute('href') != '#');" id="categoryForm_image_url_link" target="categoryURL"><img src="images/computer.png"></a></td>
            <td width="100%"><input style="width:100%;" id="categoryForm_image_url" type="text" name="categoryForm_image_url" autocomplete="off"/></td>
	    <td>
	    </td>
	    <td>
		<button style="height:15px;width:15px" type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only"  role="button" aria-disabled="false" title="open URL" onclick="return openCategoryURL('categoryForm_image_url', 1, 0);">
			<span class="ui-button-icon-primary ui-icon ui-icon-extlink"></span>
		</button>
	    </td>
	</tr>
	<tr bgcolor="#D0D0D0">
            <td colspan="4" align="center" class="input-field-error" id="categoryForm_image_url-error"></td>
        </tr>
        <tr bgcolor="#D0D0D0">
            <td valign="middle"><a href="#" onclick="return (this.getAttribute('href') != '#');" id="categoryForm_mob_image_url_link" target="categoryURL"><img src="images/mobile.png"></a></td>
            <td width="100%"><input style="width:100%;" id="categoryForm_mob_image_url" type="text" name="categoryForm_mob_image_url" autocomplete="off"/></td>
	    <td>
	    </td>
	    <td>
		<button style="height:15px;width:15px" type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only"  role="button" aria-disabled="false" title="open URL" onclick="return openCategoryURL('categoryForm_mob_image_url', 1, 1);">
			<span class="ui-button-icon-primary ui-icon ui-icon-extlink"></span>
		</button>
	    </td>
	</tr>
	<tr bgcolor="#D0D0D0">
            <td colspan="4" align="center" class="input-field-error" id="categoryForm_mob_image_url-error"></td>
        </tr>

        <tr>
	    <td nowrap rowspan="5" align="center">&nbsp;BABY&nbsp;</td><td colspan="4"></td>
	</tr>
        <tr>
            <td valign="middle"><a href="#" onclick="return (this.getAttribute('href') != '#');" id="categoryForm_baby_image_url_link" target="categoryURL"><img src="images/computer.png"></a></td>
            <td class="input-field-wrapper"><input style="width:100%;" id="categoryForm_baby_image_url" type="text" name="categoryForm_baby_image_url" autocomplete="off"/></td>
	    <td>
	    </td>
	    <td>
		<button style="height:15px;width:15px" type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only"  role="button" aria-disabled="false" title="open URL" onclick="return openCategoryURL('categoryForm_baby_image_url', 2, 0);">
			<span class="ui-button-icon-primary ui-icon ui-icon-extlink"></span>
		</button>
	    </td>
        </tr>
        <tr>    
	    <td colspan="4" align="center" class="input-field-error" id="categoryForm_baby_image_url-error"></td>
        </tr>

        <tr>
            <td valign="middle"><a href="#" onclick="return (this.getAttribute('href') != '#');" id="categoryForm_mob_baby_image_url_link" target="categoryURL"><img src="images/mobile.png"></a></td>
            <td class="input-field-wrapper"><input style="width:100%;" id="categoryForm_mob_baby_image_url" type="text" name="categoryForm_mob_baby_image_url" autocomplete="off"/></td>
	    <td>
	    </td>
	    <td>
		<button style="height:15px;width:15px" type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only"  role="button" aria-disabled="false" title="open URL" onclick="return openCategoryURL('categoryForm_mob_baby_image_url', 2, 1);">
			<span class="ui-button-icon-primary ui-icon ui-icon-extlink"></span>
		</button>
	    </td>
        </tr>
        <tr>    
	    <td colspan="4" align="center" class="input-field-error" id="categoryForm_mob_baby_image_url-error"></td>
        </tr>

        <tr bgcolor="#D0D0D0">
	    <td nowrap rowspan="5" align="center">&nbsp;CA&nbsp;</td><td colspan="4"></td>
	</tr>
        <tr bgcolor="#D0D0D0">
            <td valign="middle"><a href="#" onclick="return (this.getAttribute('href') != '#');" id="categoryForm_ca_image_url_link" target="categoryURL"><img src="images/computer.png"></a></td>
            <td><input style="width:100%;" id="categoryForm_ca_image_url" type="text" name="categoryForm_ca_image_url" autocomplete="off"/></td>
	    <td>
	    </td>
	    <td>
		<button style="height:15px;width:15px" type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only"  role="button" aria-disabled="false" title="open URL" onclick="return openCategoryURL('categoryForm_ca_image_url', 3, 0);">
			<span class="ui-button-icon-primary ui-icon ui-icon-extlink"></span>
		</button>
	    </td>
        </tr>
        <tr bgcolor="#D0D0D0">
            <td colspan="4" align="center" class="input-field-error" id="categoryForm_ca_image_url-error"></td>
        </tr>

        <tr bgcolor="#D0D0D0">
            <td valign="middle"><a href="#" onclick="return (this.getAttribute('href') != '#');" id="categoryForm_mob_ca_image_url_link" target="categoryURL"><img src="images/mobile.png"></a></td>
            <td><input style="width:100%;" id="categoryForm_mob_ca_image_url" type="text" name="categoryForm_mob_ca_image_url" autocomplete="off"/></td>
	    <td>
	    </td>
	    <td>
		<button style="height:15px;width:15px" type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only"  role="button" aria-disabled="false" title="open URL" onclick="return openCategoryURL('categoryForm_mob_ca_image_url', 3, 1);">
			<span class="ui-button-icon-primary ui-icon ui-icon-extlink"></span>
		</button>
	    </td>
        </tr>
        <tr bgcolor="#D0D0D0">
            <td colspan="4" align="center" class="input-field-error" id="categoryForm_mob_ca_image_url-error"></td>
        </tr>

        <tr>
	    <td nowrap rowspan="3" align="center">&nbsp;US-TBS&nbsp;</td><td colspan="4"></td>
	</tr>
        <tr>
            <td valign="middle"><a href="#" onclick="return (this.getAttribute('href') != '#');" id="categoryForm_tbs_image_url_link" target="categoryURL"><img src="images/computer.png"></a></td>
            <td><input style="width:100%;" id="categoryForm_tbs_image_url" type="text" name="categoryForm_tbs_image_url" autocomplete="off"/></td>
	    <td>
	    </td>
	    <td>
		<button style="height:15px;width:15px" type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only"  role="button" aria-disabled="false" title="open URL" onclick="return openCategoryURL('categoryForm_tbs_image_url', 4, 0);">
			<span class="ui-button-icon-primary ui-icon ui-icon-extlink"></span>
		</button>
	    </td>
        </tr>
        <tr>
            <td colspan="5" align="center" class="input-field-error" id="categoryForm_tbs_image_url-error"></td>
        </tr>

        <tr bgcolor="#D0D0D0">
	    <td nowrap rowspan="3" align="center">&nbsp;BAB-TBS&nbsp;</td><td colspan="4"></td>
	</tr>
        <tr bgcolor="#D0D0D0">
            <td valign="middle"><a href="#" onclick="return (this.getAttribute('href') != '#');" id="categoryForm_tbs_baby_image_url_link" target="categoryURL"><img src="images/computer.png"></a></td>
            <td><input style="width:100%;" id="categoryForm_tbs_baby_image_url" type="text" name="categoryForm_tbs_baby_image_url" autocomplete="off"/></td>
	    <td>
	    </td>
	    <td>
		<button style="height:15px;width:15px" type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only"  role="button" aria-disabled="false" title="open URL" onclick="return openCategoryURL('categoryForm_tbs_baby_image_url', 4, 0);">
			<span class="ui-button-icon-primary ui-icon ui-icon-extlink"></span>
		</button>
	    </td>
        </tr>
        <tr bgcolor="#D0D0D0">
            <td colspan="5" align="center" class="input-field-error" id="categoryForm_tbs_baby_image_url-error"></td>
        </tr>

        <tr>
	    <td nowrap rowspan="3" align="center">&nbsp;CA-TBS&nbsp;</td><td colspan="4"></td>
	</tr>
        <tr>
            <td valign="middle"><a href="#" onclick="return (this.getAttribute('href') != '#');" id="categoryForm_tbs_ca_image_url_link" target="categoryURL"><img src="images/computer.png"></a></td>
            <td><input style="width:100%;" id="categoryForm_tbs_ca_image_url" type="text" name="categoryForm_tbs_ca_image_url" autocomplete="off"/></td>
	    <td>
	    </td>
	    <td>
		<button style="height:15px;width:15px" type="button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only"  role="button" aria-disabled="false" title="open URL" onclick="return openCategoryURL('categoryForm_tbs_ca_image_url', 4, 0);">
			<span class="ui-button-icon-primary ui-icon ui-icon-extlink"></span>
		</button>
	    </td>
        </tr>
        <tr>
            <td colspan="5" align="center" class="input-field-error" id="categoryForm_tbs_ca_image_url-error"></td>
        </tr>

        </table>
    </fieldset>

    </td>
    </tr>
    <tr>
    <td nowrap valign="top">

    <table cellpadding="0" cellspacing="0" style="font:inherit;width:100%">
    <tr>
    <td nowrap valign="top">
    <fieldset style="margin-top:0px;">
        <legend style="font-weight:bold;">Checklist</legend>
        <div class="input-field-container">
            <div>Suggested Quantity</div>
            <div class="input-field-wrapper"><input style="width:100%;" id="categoryForm_suggested_qty" type="text" name="categoryForm_suggested_qty" autocomplete="off"/></div>
            <div class="input-field-error" id="categoryForm_suggested_qty-error"></div>
	</div>
    </fieldset>
    </td>
    <td nowrap valign="top">
    <fieldset style="margin-top:0px;">
        <legend style="font-weight:bold;">Porch</legend>
        <div class="input-field-container">
	    <div>Service Type Code</div>
            <div class="input-field-wrapper"><input style="width:100%;" id="categoryForm_service_type_cd" type="text" name="categoryForm_service_type_cd" autocomplete="off"/></div>
            <div class="input-field-error" id="categoryForm_service_type_cd-error"></div>
	</div>
    </fieldset>
    </td>
    </tr>
    </table>

    </td>
    <td nowrap valign="top">

    <fieldset style="margin-top:0px;">
        <legend style="font-weight:bold;">Promotion</legend>
	<table cellpadding="0" cellspacing="0" style="font:inherit;width:100%">
        <tr>
	<td nowrap valign="top">
        <div class="input-field-container">
	    <div>Threshold Amount</div>
            <div class="input-field-wrapper"><input style="width:100%;" id="categoryForm_threshold_amt" type="text" name="categoryForm_threshold_amt" autocomplete="off"/></div>
            <div class="input-field-error" id="categoryForm_threshold_amt-error"></div>
	</div>
	</td>
	<td nowrap valign="top">
        <div class="input-field-container">
	    <div>Threshold Quantity</div>
            <div class="input-field-wrapper"><input style="width:100%;" id="categoryForm_threshold_qty" type="text" name="categoryForm_threshold_qty" autocomplete="off"/></div>
            <div class="input-field-error" id="categoryForm_threshold_qty-error"></div>
	</div>
	</td>
	</tr>
	</table>
    </fieldset>

    </td>
    </tr>
    </table>
    <div style="margin-top:5px;text-align:center;font-weight:bold;color:red" id="categoryForm_error"></div>
    <div style="text-align:center;font-weight:bold" id="categoryForm_message"></div>
</form>
<form id="ruleForm" name="ruleForm" style="display:none">
    <div class="input-field-container">
        <div class="input-field-label" style="font-weight:bold;">Name</div>
        <div class="input-field-wrapper"><input style="width:100%;" id="ruleForm_facet_rule_name" type="text" name="ruleForm_facet_rule_name"/></div>
        <div class="input-field-error" id="ruleForm_facet_rule_name-error"></div>
    </div>
    <div style="margin-top:5px;text-align:center;font-weight:bold;color:red" id="ruleForm_error"></div>
    <div style="text-align:center;font-weight:bold" id="ruleForm_message"></div>
</form>
<form id="propForm" name="propForm" style="display:none">
    <table style="font:inherit;width:100%">
    <tr>
    <td valign="top">
	<ul style="cursor:hand;min-height:30px;font:inherit;padding-left:25px;padding-bottom:0px" id="propForm_info_ul">
        <li style="font-weight:bold" id="propForm_info_li"></li>
	</ul>
    </td>
    </tr>
    <tr>
    <td align="center" valign="top">
	<span id="propForm_id"></span>
    </td>
    </tr>
    <tr>
    <td>
    <fieldset style="margin-top:0px;">
        <legend style="font-weight:bold;">Audit Information</legend>
    	<table style="font:inherit;font-size:.8em;width:100%">
	<tr>
	<td nowrap>
            <span>Create User:</span>
	</td>
	<td nowrap>
            <span id="propForm_create_user"></span>
	</td>
	</tr>
	<tr>
	<td nowrap>
            <span>Create Date:</span>
	</td>
	<td nowrap>
            <span id="propForm_create_date"></span>
	</td>
	</tr>
	<tr>
	<td nowrap>
            <span>Last Modified User:</span>
	</td>
	<td nowrap>
            <span id="propForm_last_mod_user"></span>
	</td>
	</tr>
	<tr>
	<td nowrap>
            <span>Last Modified Date:</span>
	</td>
	<td nowrap>
            <span id="propForm_last_mod_date"></span>
	</td>
	</tr>
	</table>
    </fieldset>
    </td>
    </tr>
    </table>
    <div style="margin-top:5px;text-align:center;font-weight:bold;color:red" id="propForm_error"></div>
    <div style="text-align:center;font-weight:bold" id="propForm_message"></div>
</form>
<span id="text-ruler" style="position:absolute;visibility:hidden;padding:0px 0px;margin 0px 0px"></span>
<div id="alertDialog"><p><span class="ui-icon ui-icon-alert" style="display:none;float:left; margin:0 7px 20px 0;"></span><span id="alertDialog-message"></span></p></div>
<div id="confirmDialog"><p><span class="ui-icon ui-icon-alert" style="display:none;float:left; margin:0 7px 20px 0;"></span><span id="confirmDialog-message"></span></p></div>
<div id="hidden" style="visibility:hidden;height:150px"></div>
</body>
</html>
<%!
	public Cookie getCookie(Cookie[] cookies, String name, String value) {
		if (cookies != null) {
			for(int i = 0; i < cookies.length; i++) { 
		    		if (cookies[i].getName().equals(name)) {
		        		return cookies[i];
		    		}
			}
		}
		return new Cookie(name, value);
	}
%>
</dsp:page>
