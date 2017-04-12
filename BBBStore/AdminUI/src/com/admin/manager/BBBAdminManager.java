package com.admin.manager;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import atg.json.JSONArray;
import atg.json.JSONException;
import atg.json.JSONObject;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.admin.constant.BBBAdminConstant;

/**
 * This is a manager class which handles repository operations and other generic task.
 * @author Logixal
 *
 */
public class BBBAdminManager extends GenericService {

	public static final String LIST_BY_ID_QUERY = "listId equals ?0";
	public static final String LIST_BY_SITE_ID_QUERY = "sites contains ?0 ORDER BY sequenceNumber SORT ASC";
	public static final String ALL_QUERY_BY_SEQUENCE = "ALL ORDER BY sequenceNumber";
	public static final String LIST_TYPE_ALL_QUERY_ORDERBY = "ALL ORDER BY typeName,subTypeName";
	public static final String ALL_QUERY = "ALL";
	public static final String JDA_DEPT_ALL_QUERY  =  "ALL ORDER BY id,descrip";
	public static final String JDA_SUB_DEPT_ALL_QUERY  =  "jdaDeptId equals ?0";
	public static final String JDA_CLASS_ALL_QUERY  =  "jdaDeptId equals ?0 and jdaSubDeptId equals ?1";
	public static final String JDA_DEPT_BY_ID_SEARCH_QUERY = "id STARTS WITH IGNORECASE ?0";
	public static final String JDA_DEPT_BY_NAME_SEARCH_QUERY = "descrip STARTS WITH IGNORECASE ?0";
	public static final String JDA_SUBDEPT_BY_NAME_SEARCH_QUERY = "jdaDeptId equals ?0 and descrip STARTS WITH IGNORECASE ?1";
	public static final String JDA_SUBDEPT_BY_ID_SEARCH_QUERY = "jdaDeptId equals ?0 and id STARTS WITH IGNORECASE ?1";
	public static final String JDA_CLASS_BY_ID_SEARCH_QUERY = "jdaDeptId equals ?0 and jdaSubDeptId equals ?1  and jdaClass STARTS WITH IGNORECASE ?2";
	public static final String JDA_CLASS_BY_NAME_SEARCH_QUERY = "jdaDeptId equals ?0 and jdaSubDeptId equals ?1  and descrip STARTS WITH IGNORECASE ?2";
	public static final String SKU_BY_ID_SEARCH_QUERY  =  "id STARTS WITH IGNORECASE ?0";
	public static final String SKU_BY_NAME_SEARCH_QUERY  =  "displayNameDefault STARTS WITH IGNORECASE ?0";
	public static final String CATEGORY_BY_NAME_SEARCH_QUERY = "displayName STARTS WITH IGNORECASE ?0";
	public static final String CATEGORY_BY_ID_SEARCH_QUERY = "categoryId STARTS WITH IGNORECASE ?0";
	public static final String CATEGORY_ALL_QUERY = "ALL ORDER BY categoryId,displayName";
	public static final String PARENT_EPH_NODE_SEARCH_QUERY = "isRootNode=true and ephNodeId STARTS WITH IGNORECASE ?0";
	public static final String PARENT_EPH_SEARCH_BY_NAME_QUERY="isRootNode=true and displayName STARTS WITH IGNORECASE ?0";
	public static final String SEARCH_RULE_BY_ID_QUERY = "ruleId STARTS WITH IGNORECASE ?0";
	public static final String SEARCH_RULES_BY_NAME_QUERY = "facetRuleName STARTS WITH IGNORECASE ?0";
	public static final String EPH_FACETS_BY_EPH_ID_QUERY = "ephId equals ?0";
	public static final String EPH_FACETS_BY_FACET_ID_QUERY = "ephId equals ?0 and ephFacetId STARTS WITH IGNORECASE ?1";
	public static final String FACET_VALUE_BY_FACET_ID_QUERY = "ephId equals ?0 and ephFacetId equals ?1";
	public static final String ALL_EPH_PARENT_NODES_QUERY = "isRootNode=true";
	public static final String ALL_JDA_FACETS_WITH_JDACLASS_QUERY = "jdaDeptId equals ?0 and jdaSubDeptId equals ?1 and jdaClass equals ?2";
	public static final String ALL_JDA_FACETS_WITHOUT_JDACLASS_QUERY = "jdaDeptId equals ?0 and jdaSubDeptId equals ?1";
	public static final String JDA_FACET_BY_FACETID_WITH_JDACLASS_QUERY = "jdaDeptId equals ?0 and jdaSubDeptId equals ?1 and jdaClass equals ?2 and jdaFacetId STARTS WITH IGNORECASE ?3";
	public static final String JDA_FACET_BY_FACETID_WITHOUT_JDACLASS_QUERY = "jdaDeptId equals ?0 and jdaSubDeptId equals ?1 and jdaFacetId STARTS WITH IGNORECASE ?2";
	public static final String JDA_FACETVALUE_BY_FACETID_WITH_JDACLASS_QUERY = "jdaDeptId equals ?0 and jdaSubDeptId equals ?1 and jdaClass equals ?2 and jdaFacetId equals ?3";
	public static final String JDA_FACETVALUE_BY_FACETID_WITHOUT_JDACLASS_QUERY = "jdaDeptId equals ?0 and jdaSubDeptId equals ?1 and jdaFacetId equals ?2";
	public static final String FACET_VALUE_BY_FACET_ID = "facetId =?0";
	private static List<RepositoryItem> uniqueFacets = new<RepositoryItem> ArrayList();
	
	
	private MutableRepository adminRepository;
	private Repository mProductCatalog;
	private Repository skuFacetRepository;
	public Repository getSkuFacetRepository() {
		return skuFacetRepository;
	}
	
	public void setSkuFacetRepository(Repository skuFacetRepository) {
		this.skuFacetRepository = skuFacetRepository;
	}
	
	public Repository getProductCatalog() {
		return mProductCatalog;
	}
	public void setProductCatalog(Repository pProductCatalog) {
		this.mProductCatalog = pProductCatalog;
	}
	public MutableRepository getAdminRepository() {
		return adminRepository;
	}
	public void setAdminRepository(MutableRepository adminRepository) {
		this.adminRepository = adminRepository;
	}
	
	

	public BBBAdminManager() {

	}
	/**
	 * Overriden to create unique facets in list collection at startup and use it for Facet rules facet search to improve performance
	 */
	public void doStartService() throws ServiceException
        {
		super.doStartService();
		RepositoryItem[] facetItem;
		RepositoryView view;
		try {
			view = getAdminRepository().getView(BBBAdminConstant.FACET_VALUE_PAIRS_ITEM_DESCRIPTOR);
			RqlStatement facetStatement = RqlStatement.parseRqlStatement(ALL_QUERY);
			Object[]params = new Object[]{};
			facetItem = facetStatement.executeQuery(view,params);
			if(facetItem!=null && facetItem.length>0){
				for (RepositoryItem repositoryItem : facetItem) {
					RepositoryItem facetItems=(RepositoryItem) repositoryItem.getPropertyValue(BBBAdminConstant.FACET_ID_PROPERTY_NAME);
					if (!uniqueFacets.contains(facetItems)) {
						uniqueFacets.add(facetItems);
					}
				}
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		
		
        }
	
	/**
	 * 
	 * @param subTypeCode
	 * @return Array of ListType item
	 * @throws RepositoryException
	 */
	public RepositoryItem[] getAllListTypeItem(String subTypeCode) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : getAllListTypeItem() : Start");
		}
		RepositoryView view=getAdminRepository().getView(BBBAdminConstant.LIST_TYPE_ITEM_DESCRIPTOR_NAME);
		QueryBuilder queryBuilder=view.getQueryBuilder();
		QueryExpression subTypeName=queryBuilder.createPropertyQueryExpression(BBBAdminConstant.SUBTYPECODE_PROPERTY_NAME);
		QueryExpression value = queryBuilder.createConstantQueryExpression(subTypeCode);
		atg.repository.Query query=queryBuilder.createComparisonQuery(subTypeName, value, QueryBuilder.EQUALS);				
		RepositoryItem[] items=view.executeQuery(query);
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : getAllListTypeItem() : End");
		}
		return items;
	}

	public JSONObject validateUser(String userCreated, String lastModifiedUser) throws JSONException{
		JSONObject errorJson = new JSONObject(); 
		if(userCreated==null || lastModifiedUser==null){
			errorJson.put("id", "67");
			errorJson.put("description", "User Session Expired,Please login again.");
			return errorJson;
		}
		return errorJson;
	}

	public Timestamp getCurrentTimestampValue() {
		java.util.Date date = new java.util.Date();
		long t = date.getTime();
		java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(t);
		return sqlTimestamp;
	}



	public RepositoryItem createList(String pSubTypeCode,String pDisplayName,Integer pSequenceNumber,Boolean pIsDisabled,Boolean pAllowDuplicates,String pSiteProperty,String pCreatedUser,String pLastModifiedUser) throws RepositoryException, SQLException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createList() : Start");
		}
		RepositoryItem newListItem;
		RepositoryView view = getAdminRepository().getView(BBBAdminConstant.LIST_ITEM_DESCRIPTOR_NAME);
		Object[] params={};
		RqlStatement statement=RqlStatement.parseRqlStatement(ALL_QUERY);
		RepositoryItem[] items=statement.executeQuery(view,params);
		int getAvailableSequenceNo;
		if(items!=null && items.length>0){
		getAvailableSequenceNo = items.length;
		} else {
			getAvailableSequenceNo = 0;
		}
		MutableRepositoryItem listItem=getAdminRepository().createItem(BBBAdminConstant.LIST_ITEM_DESCRIPTOR_NAME);
		listItem.setPropertyValue(BBBAdminConstant.SUBTYPECODE_PROPERTY_NAME,pSubTypeCode);
		listItem.setPropertyValue(BBBAdminConstant.DISPLAY_NAME_PROPERTY_NAME, pDisplayName);
		listItem.setPropertyValue(BBBAdminConstant.SEQUENCE_NUMBER_PROPERTY_NAME,getAvailableSequenceNo);
		listItem.setPropertyValue(BBBAdminConstant.IS_DISABLED_PROPERTY_NAME, pIsDisabled);
		listItem.setPropertyValue(BBBAdminConstant.ALLOW_DUPLICATES_PROPERTY_NAME, pAllowDuplicates);
		listItem.setPropertyValue(BBBAdminConstant.SITE_PROPERTY_NAME, pSiteProperty);
		listItem.setPropertyValue(BBBAdminConstant.CREATED_USER_PROPERTY_NAME, pCreatedUser);
		listItem.setPropertyValue(BBBAdminConstant.CREATION_DATE_PROPERTY_NAME, getCurrentTimestampValue());
		listItem.setPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME, pLastModifiedUser);
		listItem.setPropertyValue(BBBAdminConstant.LAST_MODIFIED_DATE_PROPERTY_NAME, getCurrentTimestampValue());
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createList() : End");
		}
		newListItem = getAdminRepository().addItem(listItem);
		return newListItem;
	}

	public void updateList(String pListId,String pSubTypeCode,String pDisplayName,Boolean pIsDisabled,Boolean pAllowDuplicates,String pSiteProperty,String pLastModifiedUser) throws RepositoryException, SQLException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : updateList() : Start");
		}
		MutableRepositoryItem listItem=getAdminRepository().getItemForUpdate(pListId,BBBAdminConstant.LIST_ITEM_DESCRIPTOR_NAME);
		if(listItem!=null){
			listItem.setPropertyValue(BBBAdminConstant.SUBTYPECODE_PROPERTY_NAME,pSubTypeCode);
			listItem.setPropertyValue(BBBAdminConstant.DISPLAY_NAME_PROPERTY_NAME, pDisplayName);
			listItem.setPropertyValue(BBBAdminConstant.IS_DISABLED_PROPERTY_NAME, pIsDisabled);
			listItem.setPropertyValue(BBBAdminConstant.ALLOW_DUPLICATES_PROPERTY_NAME, pAllowDuplicates);
			listItem.setPropertyValue(BBBAdminConstant.SITE_PROPERTY_NAME, pSiteProperty);
			listItem.setPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME, pLastModifiedUser);
			getAdminRepository().updateItem(listItem);
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : updateList() : End");
		}
	}

	public void removeList(String pId, String pLastModifiedUser) throws RepositoryException, SQLException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : removeList() : Start");
		}
		RepositoryItem listItem = getAdminRepository().getItem(pId, BBBAdminConstant.LIST_ITEM_DESCRIPTOR_NAME);
		if(listItem!=null){
			getAdminRepository().removeItem(pId, BBBAdminConstant.LIST_ITEM_DESCRIPTOR_NAME);
		removeReOrderList();

		createListCategoryAudit(pId,"", listItem.getPropertyValue(BBBAdminConstant.DISPLAY_NAME_PROPERTY_NAME).toString(),listItem.getPropertyValue(BBBAdminConstant.SUBTYPECODE_PROPERTY_NAME).toString(),"", "DEL_LIST", pLastModifiedUser);
		deleteAllListCategoryAssocAudit(pId);
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : removeList() : end");
		}
	}




	public RepositoryItem[] fetchAllFromListType() throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : fetchAllFromListType() : Start");
		}
		RepositoryView view = getAdminRepository().getView(
				BBBAdminConstant.LIST_TYPE_ITEM_DESCRIPTOR_NAME);
		Object[] params={};
		RqlStatement statement=RqlStatement.parseRqlStatement(LIST_TYPE_ALL_QUERY_ORDERBY);
		RepositoryItem[] items=statement.executeQuery(view,params);
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : fetchAllFromListType() : End");
		}
		return items;

	}
	public RepositoryItem[] fetchAllItemsFromList() throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : fetchAllItemsFromList() : Start");
		}
		RepositoryView view = getAdminRepository().getView(
				BBBAdminConstant.LIST_ITEM_DESCRIPTOR_NAME);
		Object[] params={};
		RqlStatement statement=RqlStatement.parseRqlStatement(ALL_QUERY_BY_SEQUENCE);
		RepositoryItem[] items=statement.executeQuery(view,params);
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : fetchAllItemsFromList() : End");
		}
		return items;

	}
	public RepositoryItem[] fetchSiteList(String pSiteId) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : fetchSiteList() : Start");
		}
		RepositoryView view = getAdminRepository().getView(
				BBBAdminConstant.LIST_ITEM_DESCRIPTOR_NAME);
		Object params[] = new Object[1];
		RqlStatement statement=RqlStatement.parseRqlStatement(LIST_BY_SITE_ID_QUERY);
		params[0] = pSiteId;
		RepositoryItem[] items=statement.executeQuery(view,params);
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : fetchSiteList() : End");
		}
		return items;

	}
	public RepositoryItem fetchListById(String pListId) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : fetchListById() : Start");
		}
		RepositoryItem listItem = getAdminRepository().getItem(pListId, BBBAdminConstant.LIST_ITEM_DESCRIPTOR_NAME);

		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : fetchListById() : End");
		}
		return listItem;

	}

	public JSONObject createSingleListJsonObject(RepositoryItem pItem) throws JSONException, RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createSingleListJsonObject() : Start");
		}
		JSONObject jsonItem = new JSONObject();
		jsonItem.put("list_id",pItem.getRepositoryId());
		jsonItem.put("display_name",pItem.getPropertyValue(BBBAdminConstant.DISPLAY_NAME_PROPERTY_NAME));
		jsonItem.put("sub_type_code",pItem.getPropertyValue(BBBAdminConstant.SUBTYPECODE_PROPERTY_NAME));
		jsonItem.put("sequence_num",pItem.getPropertyValue(BBBAdminConstant.SEQUENCE_NUMBER_PROPERTY_NAME));
		jsonItem.put("is_disabled",pItem.getPropertyValue(BBBAdminConstant.IS_DISABLED_PROPERTY_NAME));
		jsonItem.put("allow_duplicates",pItem.getPropertyValue(BBBAdminConstant.ALLOW_DUPLICATES_PROPERTY_NAME));
		jsonItem.put("site_flag",pItem.getPropertyValue(BBBAdminConstant.SITE_PROPERTY_NAME));
		jsonItem.put("create_user",pItem.getPropertyValue(BBBAdminConstant.CREATED_USER_PROPERTY_NAME));
		jsonItem.put("create_date", pItem.getPropertyValue(BBBAdminConstant.CREATION_DATE_PROPERTY_NAME));
		jsonItem.put("last_mod_user", pItem.getPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME));
		jsonItem.put("last_mod_date", pItem.getPropertyValue(BBBAdminConstant.LAST_MODIFIED_DATE_PROPERTY_NAME));
		String subTypeCode=(String) pItem.getPropertyValue(BBBAdminConstant.SUBTYPECODE_PROPERTY_NAME);
		RepositoryItem[] listTypes = getAllListTypeItem(subTypeCode);
		if(listTypes!=null && listTypes.length>0){
			jsonItem.put("type_name", listTypes[0].getPropertyValue(BBBAdminConstant.TYPE_NAME_PROPERTY_NAME));
			jsonItem.put("sub_type_name", listTypes[0].getPropertyValue(BBBAdminConstant.SUB_TYPE_NAME_PROPERTY_NAME));
		} else {
			jsonItem.put("type_name", "");
			jsonItem.put("sub_type_name", "");
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createSingleListJsonObject() : End");
		}
		return jsonItem;

	}

	public JSONObject createSingleListTypeJsonObject(RepositoryItem pItem) throws JSONException, RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createSingleListTypeJsonObject() : Start");
		}	
		JSONObject jsonListType = new JSONObject();
		jsonListType.put("type_id", pItem.getRepositoryId());
		jsonListType.put("type_name", pItem.getPropertyValue(BBBAdminConstant.TYPE_NAME_PROPERTY_NAME));	
		jsonListType.put("sub_type_code", pItem.getPropertyValue(BBBAdminConstant.SUBTYPECODE_PROPERTY_NAME));	
		jsonListType.put("sub_type_name", pItem.getPropertyValue(BBBAdminConstant.SUB_TYPE_NAME_PROPERTY_NAME));	
		jsonListType.put("create_user", pItem.getPropertyValue(BBBAdminConstant.CREATED_USER_PROPERTY_NAME));	
		jsonListType.put("create_date", pItem.getPropertyValue(BBBAdminConstant.CREATION_DATE_PROPERTY_NAME));
		jsonListType.put("last_mod_user", pItem.getPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME));
		jsonListType.put("last_mod_date", pItem.getPropertyValue(BBBAdminConstant.LAST_MODIFIED_DATE_PROPERTY_NAME));

		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createSingleListTypeJsonObject() : End");
		}
		return jsonListType;

	}

	public JSONObject createSingleCategoryJsonObject(RepositoryItem pItem) throws JSONException, RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createSingleCategoryJsonObject() : Start");
		}
		JSONObject jsonCategory = new JSONObject();
		jsonCategory.put("list_cat_id", pItem.getRepositoryId());
		jsonCategory.put("category_url", pItem.getPropertyValue(BBBAdminConstant.CATEGORY_URL_US_PROPERTY_NAME));
		jsonCategory.put("image_url", pItem.getPropertyValue(BBBAdminConstant.CATEGORY_IMAGE_URL_US_PROPERTY_NAME));
		jsonCategory.put("suggested_qty", pItem.getPropertyValue(BBBAdminConstant.SUGGESTED_QUANTITY_PROPERTY_NAME));
		if (pItem.getPropertyValue(BBBAdminConstant.PARENT_CATEGORY_PROPERTY_NAME)!=null) {

			jsonCategory.put("primary_parent_cat_id",((RepositoryItem)pItem.getPropertyValue(BBBAdminConstant.PARENT_CATEGORY_PROPERTY_NAME)).getRepositoryId());
			jsonCategory.put("primary_parent_cat_display_name",((RepositoryItem)pItem.getPropertyValue(BBBAdminConstant.PARENT_CATEGORY_PROPERTY_NAME)).getPropertyValue(BBBAdminConstant.DISPLAY_NAME_PROPERTY_NAME));
		}
		jsonCategory.put("name", pItem.getPropertyValue(BBBAdminConstant.CATEGORY_NAME_PROPERTY_NAME));
		jsonCategory.put("display_name", pItem.getPropertyValue(BBBAdminConstant.DISPLAY_NAME_PROPERTY_NAME));
		jsonCategory.put("threshold_qty", pItem.getPropertyValue(BBBAdminConstant.THRESHOLD_QUANTITY_PROPERTY_NAME));
		jsonCategory.put("threshold_amt", pItem.getPropertyValue(BBBAdminConstant.THRESHOLD_AMOUNT_PROPERTY_NAME));
		jsonCategory.put("service_type_cd", pItem.getPropertyValue(BBBAdminConstant.SERVICE_TYPE_PROPERTY_NAME));
		jsonCategory.put("is_disabled", pItem.getPropertyValue(BBBAdminConstant.IS_DISABLED_PROPERTY_NAME));
		jsonCategory.put("baby_category_url", pItem.getPropertyValue(BBBAdminConstant.CATEGORY_URL_BABY_PROPERTY_NAME));
		jsonCategory.put("baby_image_url", pItem.getPropertyValue(BBBAdminConstant.CATEGORY_URL_IMAGE_URL_BABY_PROPERTY_NAME));
		jsonCategory.put("ca_category_url", pItem.getPropertyValue(BBBAdminConstant.CATEGORY_URL_CA_PROPERTY_NAME));
		jsonCategory.put("ca_image_url", pItem.getPropertyValue(BBBAdminConstant.CATEGORY_IMAGE_URL_CA_PROPERTY_NAME));
		jsonCategory.put("tbs_category_url", pItem.getPropertyValue(BBBAdminConstant.CATEGORY_URL_TBS_PROPERTY_NAME));
		jsonCategory.put("tbs_image_url", pItem.getPropertyValue(BBBAdminConstant.CATEGORY_IMAGE_URL_TBS_PROPERTY_NAME));
		
		jsonCategory.put("mob_category_url", pItem.getPropertyValue(BBBAdminConstant.MOB_CATEGORY_URL_US_PROPERTY_NAME));
		jsonCategory.put("mob_image_url", pItem.getPropertyValue(BBBAdminConstant.MOB_IMAGE_URL_US_PROPERTY_NAME));
		jsonCategory.put("mob_baby_category_url", pItem.getPropertyValue(BBBAdminConstant.MOB_CATEGORY_URL_BABY_PROPERTY_NAME));
		jsonCategory.put("mob_baby_image_url", pItem.getPropertyValue(BBBAdminConstant.MOB_IMAGE_URL_BABY_PROPERTY_NAME));
		jsonCategory.put("mob_ca_category_url", pItem.getPropertyValue(BBBAdminConstant.MOB_CATEGORY_URL_CA_PROPERTY_NAME));
		jsonCategory.put("mob_ca_image_url", pItem.getPropertyValue(BBBAdminConstant.MOB_IMAGE_URL_CA_PROPERTY_NAME));
		jsonCategory.put("tbs_baby_category_url", pItem.getPropertyValue(BBBAdminConstant.TBS_CATEGORY_URL_BABY_PROPERTY_NAME));
		jsonCategory.put("tbs_baby_image_url", pItem.getPropertyValue(BBBAdminConstant.TBS_IMAGE_URL_BABY_PROPERTY_NAME));
		jsonCategory.put("tbs_ca_category_url", pItem.getPropertyValue(BBBAdminConstant.TBS_CATEGORY_URL_CA_PROPERTY_NAME));
		jsonCategory.put("tbs_ca_image_url", pItem.getPropertyValue(BBBAdminConstant.TBS_IMAGE_URL_CA_PROPERTY_NAME));
		
		jsonCategory.put("create_user",pItem.getPropertyValue(BBBAdminConstant.CREATED_USER_PROPERTY_NAME));
		jsonCategory.put("create_date", pItem.getPropertyValue(BBBAdminConstant.CREATION_DATE_PROPERTY_NAME));
		jsonCategory.put("is_config_complete",pItem.getPropertyValue(BBBAdminConstant.CATEGORY_CONFIG_COMPLETE_PROPERTY_NAME));
		jsonCategory.put("is_visible_on_checklist",pItem.getPropertyValue(BBBAdminConstant.CATEGORY_VISIBLE_ON_CHECKLIST_PROPERTY_NAME));
		jsonCategory.put("is_visible_on_reg_list",pItem.getPropertyValue(BBBAdminConstant.CATEGORY_VISIBLE_ON_REGLIST_PROPERTY_NAME));
		jsonCategory.put("is_child_prd_needed_to_disp",pItem.getPropertyValue(BBBAdminConstant.CATEGORY_CHILDPRD_NEED_FOR_DISPLAY_PROPERTY_NAME));
		jsonCategory.put("last_mod_user", pItem.getPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME));
		jsonCategory.put("last_mod_date", pItem.getPropertyValue(BBBAdminConstant.LAST_MODIFIED_DATE_PROPERTY_NAME));
		jsonCategory.put("is_deleted", pItem.getPropertyValue(BBBAdminConstant.IS_DELETED_PROPERTY_NAME));
		jsonCategory.put("url_override", pItem.getPropertyValue(BBBAdminConstant.URL_OVERRIDE));
		jsonCategory.put("baby_url_override", pItem.getPropertyValue(BBBAdminConstant.BABY_URL_OVERRIDE));
		jsonCategory.put("ca_url_override", pItem.getPropertyValue(BBBAdminConstant.CA_URL_OVERRIDE));
		jsonCategory.put("mob_url_override", pItem.getPropertyValue(BBBAdminConstant.MOB_URL_OVERRIDE));
		jsonCategory.put("mob_baby_url_override", pItem.getPropertyValue(BBBAdminConstant.MOB_BABY_URL_OVERRIDE));
		jsonCategory.put("mob_ca_url_override", pItem.getPropertyValue(BBBAdminConstant.MOB_CA_URL_OVERRIDE));
		jsonCategory.put("tbs_url_override", pItem.getPropertyValue(BBBAdminConstant.TBS_URL_OVERRIDE));
		jsonCategory.put("tbs_baby_url_override", pItem.getPropertyValue(BBBAdminConstant.TBS_BABY_URL_OVERRIDE));
		jsonCategory.put("tbs_ca_url_override", pItem.getPropertyValue(BBBAdminConstant.TBS_CA_URL_OVERRIDE));
		jsonCategory.put("facet_id_pkg_cnt", pItem.getPropertyValue(BBBAdminConstant.FACET_ID_PKG_COUNT)!=null ? ((RepositoryItem)pItem.getPropertyValue(BBBAdminConstant.FACET_ID_PKG_COUNT)).getRepositoryId():null);
		jsonCategory.put("facet_id_pkg_cnt_description", pItem.getPropertyValue(BBBAdminConstant.FACET_ID_PKG_CNT_DESCRIPTION));

		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createSingleCategoryJsonObject() : End");
		}
		return jsonCategory;

	}
	

	
	public JSONObject createSingleEphRuleJsonObject(RepositoryItem pItem, String list_cat_id) throws JSONException, RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createSingleCategoryJsonObject() : Start");
		}
		JSONObject jsonEphRule = new JSONObject();
		if(pItem!=null)	{
			String ephId = (String)pItem.getPropertyValue(BBBAdminConstant.EPH_ID_PROPERTY_NAME);
		jsonEphRule.put("list_cat_id", list_cat_id);	
		jsonEphRule.put("rule_id", pItem.getRepositoryId());
		jsonEphRule.put("facet_rule_name", pItem.getPropertyValue(BBBAdminConstant.FACET_RULE_NAME_PROPERTY_NAME));
		jsonEphRule.put("facet_value_pair_list", pItem.getPropertyValue(BBBAdminConstant.FACET_VALUE_LIST_PROPERTY_NAME));
		String facetValuePair = (String)pItem.getPropertyValue(BBBAdminConstant.FACET_VALUE_LIST_PROPERTY_NAME);
		if(facetValuePair!=null && !facetValuePair.trim().equals("")){
		String facets = getEphFacetsDescription(ephId,facetValuePair);
		jsonEphRule.put("facets", facets);
		}
		jsonEphRule.put("create_user", pItem.getPropertyValue(BBBAdminConstant.CREATED_USER_PROPERTY_NAME));
		jsonEphRule.put("create_date", pItem.getPropertyValue(BBBAdminConstant.CREATION_DATE_PROPERTY_NAME));
		jsonEphRule.put("last_mod_user", pItem.getPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME));
		jsonEphRule.put("last_mod_date", pItem.getPropertyValue(BBBAdminConstant.LAST_MODIFIED_DATE_PROPERTY_NAME));
		jsonEphRule.put("rule_type_code","2" );
		jsonEphRule.put("eph_node_id", pItem.getPropertyValue(BBBAdminConstant.EPH_ID_PROPERTY_NAME));
		
		if (ephId!=null && !ephId.trim().equals("")) {
			RepositoryItem ephNode = getAdminRepository().getItem(ephId,BBBAdminConstant.EPH_NODE_ITEM_DESCRIPTOR_NAME);
			if (ephNode != null) {
				int childNodesCount = getEphNodeChildCount(ephNode);
				jsonEphRule.put("children", childNodesCount);
				jsonEphRule.put("eph_display_name", ephNode.getPropertyValue(BBBAdminConstant.DISPLAY_NAME_PROPERTY_NAME));
				String ephHierarchy = getEphHierarchy(ephNode);
				jsonEphRule.put("eph_nodes", ephHierarchy);
			}
		}
	}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createSingleCategoryJsonObject() : End");
		}
		return jsonEphRule;

	}

	public String  getEphHierarchy(RepositoryItem pEphNode){
		String ephHierarchy="";
		if(pEphNode!=null){
		ephHierarchy = pEphNode.getRepositoryId()+"\t"+pEphNode.getPropertyValue(BBBAdminConstant.DISPLAY_NAME_PROPERTY_NAME)+"\t"+getEphNodeChildCount(pEphNode);
		RepositoryItem parentNode;
		boolean hasParent=true;
		while(hasParent){
			Set<RepositoryItem> parentNodeSet = (Set<RepositoryItem>)pEphNode.getPropertyValue(BBBAdminConstant.EPH_NODE_PARENT_NODE_PROPERTY_NAME);
			if(!parentNodeSet.isEmpty()){
			for (Iterator<RepositoryItem> i = parentNodeSet.iterator(); i.hasNext();) {
				parentNode = (RepositoryItem)i.next();
				if(parentNode!=null){
					ephHierarchy = parentNode.getRepositoryId()+"\t"+parentNode.getPropertyValue(BBBAdminConstant.DISPLAY_NAME_PROPERTY_NAME)+"\t"+getEphNodeChildCount(parentNode)+"\n"+ephHierarchy;
				} else {
						hasParent = false;
				}
				pEphNode =  parentNode;	
			}
			} else {
				hasParent = false;
			}
			
		}
		
	}
		return ephHierarchy;
	}
	
	public int getEphNodeChildCount(RepositoryItem pEphNode){
		int childCount=0;
		if(pEphNode!=null){
		Set<RepositoryItem>childNodes = (Set<RepositoryItem>) pEphNode.getPropertyValue(BBBAdminConstant.CHILD_NODES_PROPERTY_NAME);
		if(childNodes!=null){
			childCount = childNodes.size();
		}
		}
		return childCount;
	}
	
	public String getEphFacetsDescription(String pEphId, String pFacetValuePairs) throws RepositoryException{
		String facetValuePairsDesc ="";
		String []facetPairs = pFacetValuePairs.split(",");
		for(String facetPair:facetPairs){
			String []facetIdValue = facetPair.split(":");
			String facetId = facetIdValue[0];
			String facetValueId = facetIdValue[1];
			RepositoryItem facetPairItem = (RepositoryItem)getAdminRepository().getItem(pEphId+":"+facetPair, BBBAdminConstant.EPH_FACET_ITEM_DESCRIPTOR_NAME);
			if(facetPairItem!=null){ 
			RepositoryItem facetItem = (RepositoryItem)facetPairItem.getPropertyValue(BBBAdminConstant.EPH_FACET_ID_PROPERTY_NAME);
			String facetIdDesc = facetItem.getPropertyValue(BBBAdminConstant.DESCRIPTION_PROPERTY_NAME).toString();
			String facetValueIdDesc = facetPairItem.getPropertyValue(BBBAdminConstant.FACET_VALUE_DESCRIPTION_PROPERTY_NAME).toString();
			facetValuePairsDesc =  facetValuePairsDesc+facetId+"\t"+facetIdDesc+"\r"+facetValueId+"\t"+facetValueIdDesc+"\n";
			}
		}
		
		return facetValuePairsDesc;
	}
	

	public JSONArray createSingleListCategoryJsonObject(RepositoryItem pItem) throws JSONException, RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createSingleListCategoryJsonObject() : Start");
		}
		JSONArray categoryList = new JSONArray();

		List<RepositoryItem> categories=(List<RepositoryItem>) pItem.getPropertyValue(BBBAdminConstant.CATEGORIES_PROPERTY_NAME);
		for (RepositoryItem category : categories) {
			JSONObject jsonListCategory = createSingleCategoryJsonObject(category);
			jsonListCategory.put("list_id",pItem.getRepositoryId());
			categoryList.put(jsonListCategory);
		}

		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createSingleListCategory	CategoryJsonObject() : End");
		}
		return categoryList;

	}

	public JSONArray createSingleChildCategoryJsonObject(String pItem) throws JSONException, RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createSingleChildCategoryJsonObject() : Start");
		}
		JSONArray childCategoriesJson = new JSONArray();
		RepositoryItem categoryItem = getAdminRepository().getItem(pItem, BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
		List<RepositoryItem> childCategories=(List<RepositoryItem>) categoryItem.getPropertyValue(BBBAdminConstant.CHILD_CATGEORY_PROPERTY_NAME);
		for (RepositoryItem category : childCategories) {
			JSONObject jsonListCategory = createSingleCategoryJsonObject(category);

			jsonListCategory.put("child_list_cat_id",category.getRepositoryId());
			jsonListCategory.put("list_cat_id",categoryItem.getRepositoryId());
			childCategoriesJson.put(jsonListCategory);
		}

		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createSingleListCategory	CategoryJsonObject() : End");
		}
		return childCategoriesJson;

	}

	public RepositoryItem createCategory(String pParentCatID,String pCategoryName,int pSuggestedQty,String pDisplayName,String pCatUrlUS,String pCatImageUrlUS,String pCatUrlTBS,String pCatImageUrlTBS,String pCatUrlBaby,String pCatImageUrlBaby,String pCatUrlCA,String pCatImageUrlCA,String pMobCatUrlUS, String pMobImageUrlUS,String pMobCatUrlBaby,String pMobImageUrlBaby,String pMobCatUrlCA,String pMobImageUrlCA,String pTbsCatUrlBaby,String pTbsImageUrlBaby,String pTbsCatUrlCA,String pTbsImageUrlCA, boolean pConfigComplete,boolean pVisibleOnChecklist,boolean pVisibleOnRegList,boolean pChildPrdNeedForDisplay,Integer pThresholdQty,Double pThresholdAmt,String pServiceType,Boolean pIsDisabled,String pCreatedUser,String pLastModifiedUser,String pListId, String pParentListCatId, boolean pUrlOverride, boolean pBabyUrlOverride, boolean pCAUrlOverride, boolean pMobUrlOverride, boolean pMobBabyUrlOverride, boolean pMobCAUrlOverride, boolean pTbsUrlOverride, boolean pTbsBabyUrlOverride, boolean pTbsCAUrlOverride, String facetIdPkgCnt) throws RepositoryException, SQLException{

		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createCategory : Start");
		}

		MutableRepositoryItem categoryItem=getAdminRepository().createItem(BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
		categoryItem.setPropertyValue(BBBAdminConstant.CATEGORY_NAME_PROPERTY_NAME,pCategoryName);
		categoryItem.setPropertyValue(BBBAdminConstant.SUGGESTED_QUANTITY_PROPERTY_NAME, pSuggestedQty);
		RepositoryItem parentCategoryItem = (RepositoryItem) getAdminRepository().getItem(pParentCatID, BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
		if (parentCategoryItem!=null) {
			categoryItem.setPropertyValue(
					BBBAdminConstant.PARENT_CATEGORY_PROPERTY_NAME, parentCategoryItem);
		}
		categoryItem.setPropertyValue(BBBAdminConstant.DISPLAY_NAME_PROPERTY_NAME, pDisplayName);
		categoryItem.setPropertyValue(BBBAdminConstant.CATEGORY_URL_US_PROPERTY_NAME, pCatUrlUS);
		categoryItem.setPropertyValue(BBBAdminConstant.CATEGORY_IMAGE_URL_US_PROPERTY_NAME,pCatImageUrlUS);
		categoryItem.setPropertyValue(BBBAdminConstant.CATEGORY_URL_TBS_PROPERTY_NAME, pCatUrlTBS);
		categoryItem.setPropertyValue(BBBAdminConstant.CATEGORY_IMAGE_URL_TBS_PROPERTY_NAME, pCatImageUrlTBS);
		categoryItem.setPropertyValue(BBBAdminConstant.CATEGORY_URL_BABY_PROPERTY_NAME, pCatUrlBaby);
		categoryItem.setPropertyValue(BBBAdminConstant.CATEGORY_URL_IMAGE_URL_BABY_PROPERTY_NAME, pCatImageUrlBaby);
		categoryItem.setPropertyValue(BBBAdminConstant.CATEGORY_URL_CA_PROPERTY_NAME,pCatUrlCA);
		categoryItem.setPropertyValue(BBBAdminConstant.CATEGORY_IMAGE_URL_CA_PROPERTY_NAME, pCatImageUrlCA);
		
		/* DM-387-Added New properties */
		categoryItem.setPropertyValue(BBBAdminConstant.MOB_CATEGORY_URL_US_PROPERTY_NAME, pMobCatUrlUS);
		categoryItem.setPropertyValue(BBBAdminConstant.MOB_IMAGE_URL_US_PROPERTY_NAME, pMobImageUrlUS);
		categoryItem.setPropertyValue(BBBAdminConstant.MOB_CATEGORY_URL_BABY_PROPERTY_NAME, pMobCatUrlBaby);
		categoryItem.setPropertyValue(BBBAdminConstant.MOB_IMAGE_URL_BABY_PROPERTY_NAME, pMobImageUrlBaby);
		categoryItem.setPropertyValue(BBBAdminConstant.MOB_CATEGORY_URL_CA_PROPERTY_NAME, pMobCatUrlCA);
		categoryItem.setPropertyValue(BBBAdminConstant.MOB_IMAGE_URL_CA_PROPERTY_NAME, pMobImageUrlCA);
		categoryItem.setPropertyValue(BBBAdminConstant.TBS_CATEGORY_URL_BABY_PROPERTY_NAME, pTbsCatUrlBaby);
		categoryItem.setPropertyValue(BBBAdminConstant.TBS_IMAGE_URL_BABY_PROPERTY_NAME, pTbsImageUrlBaby);
		categoryItem.setPropertyValue(BBBAdminConstant.TBS_CATEGORY_URL_CA_PROPERTY_NAME, pTbsCatUrlCA);
		categoryItem.setPropertyValue(BBBAdminConstant.TBS_IMAGE_URL_CA_PROPERTY_NAME, pTbsImageUrlCA);
		
		categoryItem.setPropertyValue(BBBAdminConstant.CATEGORY_CONFIG_COMPLETE_PROPERTY_NAME, pConfigComplete);
		categoryItem.setPropertyValue(BBBAdminConstant.CATEGORY_VISIBLE_ON_CHECKLIST_PROPERTY_NAME, pVisibleOnChecklist);
		categoryItem.setPropertyValue(BBBAdminConstant.CATEGORY_VISIBLE_ON_REGLIST_PROPERTY_NAME, pVisibleOnRegList);
		categoryItem.setPropertyValue(BBBAdminConstant.CATEGORY_CHILDPRD_NEED_FOR_DISPLAY_PROPERTY_NAME, pChildPrdNeedForDisplay);
		categoryItem.setPropertyValue(BBBAdminConstant.THRESHOLD_QUANTITY_PROPERTY_NAME, pThresholdQty);
		categoryItem.setPropertyValue(BBBAdminConstant.THRESHOLD_AMOUNT_PROPERTY_NAME, pThresholdAmt);
		categoryItem.setPropertyValue(BBBAdminConstant.SERVICE_TYPE_PROPERTY_NAME, pServiceType);
		categoryItem.setPropertyValue(BBBAdminConstant.IS_DISABLED_PROPERTY_NAME, pIsDisabled);
		categoryItem.setPropertyValue(BBBAdminConstant.CREATED_USER_PROPERTY_NAME, pCreatedUser);
		categoryItem.setPropertyValue(BBBAdminConstant.CREATION_DATE_PROPERTY_NAME, getCurrentTimestampValue());
		categoryItem.setPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME, pLastModifiedUser);
		categoryItem.setPropertyValue(BBBAdminConstant.LAST_MODIFIED_DATE_PROPERTY_NAME, getCurrentTimestampValue());
		categoryItem.setPropertyValue(BBBAdminConstant.IS_DELETED_PROPERTY_NAME, false);
		
		categoryItem.setPropertyValue(BBBAdminConstant.URL_OVERRIDE, pUrlOverride);
		categoryItem.setPropertyValue(BBBAdminConstant.BABY_URL_OVERRIDE, pBabyUrlOverride);
		categoryItem.setPropertyValue(BBBAdminConstant.CA_URL_OVERRIDE, pCAUrlOverride);
		categoryItem.setPropertyValue(BBBAdminConstant.MOB_URL_OVERRIDE, pMobUrlOverride);
		categoryItem.setPropertyValue(BBBAdminConstant.MOB_BABY_URL_OVERRIDE, pMobBabyUrlOverride);
		categoryItem.setPropertyValue(BBBAdminConstant.MOB_CA_URL_OVERRIDE, pMobCAUrlOverride);
		categoryItem.setPropertyValue(BBBAdminConstant.TBS_URL_OVERRIDE, pTbsUrlOverride);
		categoryItem.setPropertyValue(BBBAdminConstant.TBS_BABY_URL_OVERRIDE, pTbsBabyUrlOverride);
		categoryItem.setPropertyValue(BBBAdminConstant.TBS_CA_URL_OVERRIDE, pTbsCAUrlOverride);
		if(facetIdPkgCnt !=null){
		RepositoryItem facetIdPkgCntRepoItem =getSkuFacetRepository().getItem(facetIdPkgCnt, BBBAdminConstant.SKU_FACET_TYPE);
		categoryItem.setPropertyValue(BBBAdminConstant.FACET_ID_PKG_COUNT, facetIdPkgCntRepoItem);
		}
		
		RepositoryItem category = getAdminRepository().addItem(categoryItem);
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createCategory : Item successfully created");
		}

		if(pListId!=null && !pListId.trim().equals("")){
			if (isLoggingDebug()) {
				logDebug("BBBAdminManager : createCategory : Associating category: "+category.getRepositoryId()+" to list:"+pListId);
			}
			categoryToListAssociation(pListId,category.getRepositoryId(),pCreatedUser);
		}
		if(pParentListCatId!=null && !pParentListCatId.trim().equals("")){
			if (isLoggingDebug()) {
				logDebug("BBBAdminManager : createCategory : Associating category: "+category.getRepositoryId()+" to category:"+pParentListCatId);
			}
			childCategoryToCategoryAssociation(pParentListCatId,category.getRepositoryId(),pCreatedUser);
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createCategory : End");
		}
		return category;		
	}

	public void removeCategory(String categoryID, String pLastModifiedUser) throws RepositoryException, SQLException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : removeItemFromCategory() : Start");
		} 
		MutableRepositoryItem categoryItem = getAdminRepository().getItemForUpdate(categoryID, BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
		categoryItem.setPropertyValue(BBBAdminConstant.IS_DELETED_PROPERTY_NAME, true);
		categoryItem.setPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME, pLastModifiedUser);
		getAdminRepository().updateItem(categoryItem);
		String catDisplayName = (String)categoryItem.getPropertyValue(BBBAdminConstant.DISPLAY_NAME_PROPERTY_NAME);
		//createListCategoryAudit("",categoryID,"","",catDisplayName,"DEL_CAT",pLastModifiedUser);
		List<RepositoryItem> listItems = getCategoryLists(categoryItem);
		if(listItems!=null && !listItems.isEmpty()){ 
			for(RepositoryItem listItem : listItems){
				if (isLoggingDebug()) {
					logDebug("BBBAdminManager : removeItemFromCategory() : Making delete audit entry for category : "+categoryID+ "and list : "+listItem.getRepositoryId());
				}
				createListCategoryAudit(listItem.getRepositoryId(),categoryID,listItem.getPropertyValue(BBBAdminConstant.DISPLAY_NAME_PROPERTY_NAME).toString(),listItem.getPropertyValue(BBBAdminConstant.SUBTYPECODE_PROPERTY_NAME).toString(),catDisplayName,"DEL_CAT",pLastModifiedUser);	
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : removeItemFromCategory() : End");
		}
	}
	
	public List<RepositoryItem> getCategoryLists(RepositoryItem pCategoryItem) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : getCategoryLists() : End");
		}
		List<RepositoryItem> listItems = new ArrayList<RepositoryItem>();
		if(pCategoryItem!=null){
		RepositoryItem[] lists = fetchAllItemsFromList();
		if(lists!=null && lists.length>0){
			for(RepositoryItem list : lists){
				List<RepositoryItem>listCategories = (List<RepositoryItem>) list.getPropertyValue(BBBAdminConstant.CATEGORIES_PROPERTY_NAME);
				if(listCategories.contains(pCategoryItem)){
					listItems.add(list);
				}
			
			}
		}
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : getCategoryLists() : End");
		}
		return listItems;
	}

	public RepositoryItem updateCategory(String pCategoryId,String pCategoryName,int pSuggestedQty,String pParentCategoryId,String pDisplayName,String pCatUrlUS,String pCatImageUrlUS,String pCatUrlTBS,String pCatImageUrlTBS,String pCatUrlBaby,String pCatImageUrlBaby,String pCatUrlCA,String pCatImageUrlCA,String pMobCatUrlUS,String pMobImageUrlUS,String pMobCatUrlBaby,String pMobImageUrlBaby,String pMobCatUrlCA,String pMobImageUrlCA,String pTbsCatUrlBaby,String pTbsImageUrlBaby,String pTbsCatUrlCA,String pTbsImageUrlCA,Integer pThresholdQty,Double pThresholdAmt,String pServiceType,Boolean pIsDisabled,String pLastModifiedUser,boolean pConfigComplete,boolean pVisibleOnChecklist,boolean pVisibleOnRegList,boolean pChildPrdNeedForDisplay, boolean pUrlOverride, boolean pBabyUrlOverride, boolean pCAUrlOverride, boolean pMobUrlOverride, boolean pMobBabyUrlOverride, boolean pMobCAUrlOverride, boolean pTbsUrlOverride, boolean pTbsBabyUrlOverride, boolean pTbsCAUrlOverride,String facetIdPkgCnt) throws RepositoryException, SQLException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : updateCategory() : Start");
		}
		MutableRepositoryItem adminRepositoryDescriptor=getAdminRepository().getItemForUpdate(pCategoryId,BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.CATEGORY_NAME_PROPERTY_NAME,pCategoryName);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.SUGGESTED_QUANTITY_PROPERTY_NAME, pSuggestedQty);
		RepositoryItem categoryItem=(RepositoryItem) getAdminRepository().getItemDescriptor(BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME).getRepository().getItem(pParentCategoryId, BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.PARENT_CATEGORY_PROPERTY_NAME,categoryItem);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.DISPLAY_NAME_PROPERTY_NAME, pDisplayName);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.CATEGORY_URL_US_PROPERTY_NAME, pCatUrlUS);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.CATEGORY_IMAGE_URL_US_PROPERTY_NAME,pCatImageUrlUS);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.CATEGORY_URL_TBS_PROPERTY_NAME, pCatUrlTBS);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.CATEGORY_IMAGE_URL_TBS_PROPERTY_NAME, pCatImageUrlTBS);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.CATEGORY_URL_BABY_PROPERTY_NAME, pCatUrlBaby);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.CATEGORY_URL_IMAGE_URL_BABY_PROPERTY_NAME, pCatImageUrlBaby);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.CATEGORY_URL_CA_PROPERTY_NAME,pCatUrlCA);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.CATEGORY_IMAGE_URL_CA_PROPERTY_NAME, pCatImageUrlCA);
		
		/* DM-387-Added New properties */
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.MOB_CATEGORY_URL_US_PROPERTY_NAME, pMobCatUrlUS);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.MOB_IMAGE_URL_US_PROPERTY_NAME, pMobImageUrlUS);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.MOB_CATEGORY_URL_BABY_PROPERTY_NAME, pMobCatUrlBaby);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.MOB_IMAGE_URL_BABY_PROPERTY_NAME, pMobImageUrlBaby);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.MOB_CATEGORY_URL_CA_PROPERTY_NAME, pMobCatUrlCA);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.MOB_IMAGE_URL_CA_PROPERTY_NAME, pMobImageUrlCA);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.TBS_CATEGORY_URL_BABY_PROPERTY_NAME, pTbsCatUrlBaby);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.TBS_IMAGE_URL_BABY_PROPERTY_NAME, pTbsImageUrlBaby);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.TBS_CATEGORY_URL_CA_PROPERTY_NAME, pTbsCatUrlCA);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.TBS_IMAGE_URL_CA_PROPERTY_NAME, pTbsImageUrlCA);
		
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.THRESHOLD_QUANTITY_PROPERTY_NAME, pThresholdQty);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.THRESHOLD_AMOUNT_PROPERTY_NAME, pThresholdAmt);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.SERVICE_TYPE_PROPERTY_NAME, pServiceType);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.IS_DISABLED_PROPERTY_NAME, pIsDisabled);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME, pLastModifiedUser);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.LAST_MODIFIED_DATE_PROPERTY_NAME, getCurrentTimestampValue());
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.CATEGORY_CONFIG_COMPLETE_PROPERTY_NAME, pConfigComplete);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.CATEGORY_VISIBLE_ON_CHECKLIST_PROPERTY_NAME, pVisibleOnChecklist);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.CATEGORY_VISIBLE_ON_REGLIST_PROPERTY_NAME, pVisibleOnRegList);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.CATEGORY_CHILDPRD_NEED_FOR_DISPLAY_PROPERTY_NAME, pChildPrdNeedForDisplay);
		
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.URL_OVERRIDE, pUrlOverride);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.BABY_URL_OVERRIDE, pBabyUrlOverride);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.CA_URL_OVERRIDE, pCAUrlOverride);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.MOB_URL_OVERRIDE, pMobUrlOverride);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.MOB_BABY_URL_OVERRIDE, pMobBabyUrlOverride);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.MOB_CA_URL_OVERRIDE, pMobCAUrlOverride);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.TBS_URL_OVERRIDE, pTbsUrlOverride);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.TBS_BABY_URL_OVERRIDE, pTbsBabyUrlOverride);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.TBS_CA_URL_OVERRIDE, pTbsCAUrlOverride);
		if(facetIdPkgCnt !=null){
		RepositoryItem facetIdPkgCntRepoItem =getSkuFacetRepository().getItem(facetIdPkgCnt,BBBAdminConstant.SKU_FACET_TYPE);
		adminRepositoryDescriptor.setPropertyValue(BBBAdminConstant.FACET_ID_PKG_COUNT, facetIdPkgCntRepoItem);
		}

		getAdminRepository().updateItem(adminRepositoryDescriptor);
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : updateCategory() : End");
		}
		return adminRepositoryDescriptor;

	}


	public RepositoryItem fetchCategoryById(String pCategoryId) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : fetchCategoryById() : Start");
		}
		RepositoryItem  categoryItem=null;
		if (pCategoryId!=null && pCategoryId!="") {

			categoryItem=(RepositoryItem) getAdminRepository().getItem(pCategoryId, BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : fetchCategoryById() : End");
		}
		return categoryItem;	

	}

	public RepositoryItem[] searchCategoryByName(String pDisplayName) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : searchCategoryByName() : Start");
		}
		RepositoryItem[] categories = null;
		if (pDisplayName!=null) {

			RepositoryView view = getAdminRepository().getView(BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
			RqlStatement statement=RqlStatement.parseRqlStatement(CATEGORY_BY_NAME_SEARCH_QUERY);
			Object[]params= new Object[]{pDisplayName};
			categories = statement.executeQuery(view,params);
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : searchCategoryByName() : End");
		}
		return categories;

	}
	
	public RepositoryItem[] searchCategoryById(String pCategoryId) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : searchCategoryById() : Start");
		}
		RepositoryItem[] categories = null;
		if (pCategoryId!=null) {

			RepositoryView view = getAdminRepository().getView(BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
			RqlStatement statement=RqlStatement.parseRqlStatement(CATEGORY_BY_ID_SEARCH_QUERY);
			Object[]params= new Object[]{pCategoryId};
			categories = statement.executeQuery(view,params);
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : searchCategoryById() : End");
		}
		return categories;

	}
	
	public RepositoryItem[] getAllCategories() throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : getAllCategories() : Start");
		}
		RepositoryItem[] categories = null;
		

			RepositoryView view = getAdminRepository().getView(BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
			RqlStatement statement=RqlStatement.parseRqlStatement(CATEGORY_ALL_QUERY);
			Object[]params= new Object[]{};
			categories = statement.executeQuery(view,params);
		
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : getAllCategories() : End");
		}
		return categories;

	}

	public void  removeCategoryFromList(String pListId,String pCategoryId,String pLastModifiedUser) throws RepositoryException {
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : removeCategoryFromList() : Start");
		}
		if (pListId!=null && pCategoryId!=null) {
			MutableRepositoryItem listItem=getAdminRepository().getItemForUpdate(pListId,BBBAdminConstant.LIST_ITEM_DESCRIPTOR_NAME);
			List<RepositoryItem> listCategories=(List<RepositoryItem>) listItem.getPropertyValue(BBBAdminConstant.CATEGORIES_PROPERTY_NAME);
			RepositoryItem categoryItem = getAdminRepository().getItem(pCategoryId, BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME); 
			if(listCategories.contains(categoryItem)){
				listCategories.remove(categoryItem);	
				listItem.setPropertyValue(BBBAdminConstant.CATEGORIES_PROPERTY_NAME, listCategories);
				getAdminRepository().updateItem(listItem);
				deleteListCategoryAssocAudit(pListId,pCategoryId);	
				createListCategoryAudit(pListId,pCategoryId, listItem.getPropertyValue(BBBAdminConstant.DISPLAY_NAME_PROPERTY_NAME).toString(),listItem.getPropertyValue(BBBAdminConstant.SUBTYPECODE_PROPERTY_NAME).toString(),categoryItem.getPropertyValue(BBBAdminConstant.DISPLAY_NAME_PROPERTY_NAME).toString(), "REM_CAT", pLastModifiedUser);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : removeCategoryFromList() : End");
		}	

	}
	//category to list
	public void categoryToListAssociation(String pListId,String pCategoryId,String pUserCreated) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : categoryToListAssociation() : Start");
		}
		MutableRepositoryItem listItem = (MutableRepositoryItem) getAdminRepository().getItemForUpdate(pListId, BBBAdminConstant.LIST_ITEM_DESCRIPTOR_NAME);
		List<RepositoryItem> categoriesToList = (List<RepositoryItem>)listItem.getPropertyValue(BBBAdminConstant.CATEGORIES_PROPERTY_NAME);
		RepositoryItem categoryItem=(RepositoryItem) getAdminRepository().getItem(pCategoryId, BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
		boolean auditFlag = false;

		if (categoriesToList!=null && categoryItem!=null && !categoriesToList.contains(categoryItem)) {

			categoriesToList.add(categoryItem);
			auditFlag = true;
		}
		else if(categoryItem!=null && !categoriesToList.contains(categoryItem))
		{
			categoriesToList = new ArrayList<RepositoryItem>();
			categoriesToList.add(categoryItem);
			auditFlag  = true;
		} else if(categoriesToList.contains(categoryItem)){
			throw new RepositoryException("68");
		}
		
		if(auditFlag){
			listItem.setPropertyValue(BBBAdminConstant.CATEGORIES_PROPERTY_NAME, categoriesToList);
			getAdminRepository().updateItem(listItem);
			createListCategoryAssocAudit(listItem.getRepositoryId(),categoryItem.getRepositoryId(),pUserCreated);
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : categoryToListAssociation() : End");
		}
	}
	//child category to category
	public void childCategoryToCategoryAssociation(String pCategoryId,String pChildCategoryId, String pUserCreated) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : childCategoryToCategoryAssociation() : Start");
		}
		MutableRepositoryItem category=(MutableRepositoryItem) getAdminRepository().getItemForUpdate(pCategoryId, BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
		List<RepositoryItem> childCategories = (List<RepositoryItem>)category.getPropertyValue(BBBAdminConstant.CHILD_CATGEORY_PROPERTY_NAME);
		RepositoryItem childCategoryItem=(RepositoryItem) getAdminRepository().getItem(pChildCategoryId, BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
		List<RepositoryItem> childCatChildCategories = (List<RepositoryItem>)childCategoryItem.getPropertyValue(BBBAdminConstant.CHILD_CATGEORY_PROPERTY_NAME);
		boolean auditFlag=false;
		if (childCategories!=null && childCategoryItem!=null && !childCategories.contains(childCategoryItem) && !childCatChildCategories.contains(category)) {

			childCategories.add(childCategoryItem);
			auditFlag=true;
		}
		else if(childCategoryItem!=null && !childCategories.contains(childCategoryItem) && !childCatChildCategories.contains(category))
		{
			childCategories = new ArrayList<RepositoryItem>();
			childCategories.add(childCategoryItem);
			auditFlag=true;
		}
		else if(childCatChildCategories.contains(category)){
			if (isLoggingDebug()) {
				logDebug("BBBAdminManager : childCategoryToCategoryAssociation() : End");
			}
			throw new RepositoryException(BBBAdminConstant.NO_PARENT_IN_CHILD);
		} else if(childCategories.contains(childCategoryItem)){
			throw new RepositoryException("68");
		}
		
		if(auditFlag){
			category.setPropertyValue(BBBAdminConstant.CHILD_CATGEORY_PROPERTY_NAME, childCategories);
			getAdminRepository().updateItem(category);
			createCatChildCategoryAssocAudit(pCategoryId,pChildCategoryId,pUserCreated);
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : childCategoryToCategoryAssociation() : End");
		}

	}

	public void  removeChildCategoriesFromCategories(String pCategoryId,String pChildCategoryId) throws RepositoryException {
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : removeChildCategoriesFromCategories() : Start");
		}
		if (pCategoryId!=null  && pChildCategoryId!=null) {
			MutableRepositoryItem categoryItem = getAdminRepository().getItemForUpdate(pCategoryId,BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
			List<RepositoryItem> childCategories=(List<RepositoryItem>) categoryItem.getPropertyValue(BBBAdminConstant.CHILD_CATGEORY_PROPERTY_NAME);
			RepositoryItem childCategoryItem = getAdminRepository().getItem(pChildCategoryId, BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
			if(childCategories.contains(childCategoryItem)){
				childCategories.remove(childCategoryItem);
				categoryItem.setPropertyValue(BBBAdminConstant.CHILD_CATGEORY_PROPERTY_NAME, childCategories);	
				getAdminRepository().updateItem(categoryItem);
				deleteCatChildCategoryAssocAudit(pCategoryId,pChildCategoryId);
			}

		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : removeChildCategoriesFromCategories() : End");
		}
	}

	public List<RepositoryItem> fetchChildCategoriesFromCatgory(String pCategoryId) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : fetchChildCategoriesFromCatgory() : Start");
		}
		List<RepositoryItem> childCategoryProperty = null;

		if (pCategoryId!=null) {
			RepositoryItem categoryItem = getAdminRepository().getItem(pCategoryId, BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
			childCategoryProperty = (List<RepositoryItem>) categoryItem.getPropertyValue(BBBAdminConstant.CHILD_CATGEORY_PROPERTY_NAME);
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : fetchChildCategoriesFromCatgory() : End");
		}
		return childCategoryProperty;
	}

	/**
	 * Fetches categories form list
	 * @param pListId
	 * @return
	 * @throws RepositoryException
	 */
	public List<RepositoryItem> fetchCategoriesFromList(String pListId) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : fetchCategoriesFromList() : Start");
		}
		List<RepositoryItem> categories = null;

		if (pListId!=null) {

			RepositoryItem listItem = getAdminRepository().getItem(pListId, BBBAdminConstant.LIST_ITEM_DESCRIPTOR_NAME);
			categories = (List<RepositoryItem>) listItem.getPropertyValue(BBBAdminConstant.CATEGORIES_PROPERTY_NAME);
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : fetchCategoriesFromList() : End");
		}
		return categories;
	}


	public void reOrderListSequence(String pListId, int pNewSequenceNo, String pLastModifiedUser) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : reOrderListSequence() : Start");
		}
		int startSeq;
		MutableRepositoryItem currentItem = (MutableRepositoryItem)getAdminRepository().getItem(pListId, BBBAdminConstant.LIST_ITEM_DESCRIPTOR_NAME);
		int currentSequenceNo = (int) currentItem.getPropertyValue(BBBAdminConstant.SEQUENCE_NUMBER_PROPERTY_NAME);

		RepositoryView view = getAdminRepository().getView(
				BBBAdminConstant.LIST_ITEM_DESCRIPTOR_NAME);
		Object params[] = new Object[2];
		RqlStatement statement;
		if(pNewSequenceNo<currentSequenceNo){
			statement=RqlStatement.parseRqlStatement("sequenceNumber>=?0 and sequenceNumber<?1 order by sequenceNumber asc");
			params[0] = pNewSequenceNo;
			params[1] = currentSequenceNo;
			startSeq=pNewSequenceNo+1;
		} 
		else {
			statement=RqlStatement.parseRqlStatement("sequenceNumber>?0 and sequenceNumber<=?1 order by sequenceNumber asc");
			params[0] = currentSequenceNo;
			params[1] = pNewSequenceNo;
			startSeq=currentSequenceNo;
		}
		RepositoryItem[] items=statement.executeQuery(view,params);
		currentItem.setPropertyValue(BBBAdminConstant.SEQUENCE_NUMBER_PROPERTY_NAME, pNewSequenceNo);
		currentItem.setPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME, pLastModifiedUser);
		getAdminRepository().updateItem(currentItem);

		if(items!=null && items.length>0){
			for(int i=startSeq, j=0;j<items.length;i++,j++){
				MutableRepositoryItem item =  (MutableRepositoryItem)items[j];
				item.setPropertyValue(BBBAdminConstant.SEQUENCE_NUMBER_PROPERTY_NAME, i);
				item.setPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME, pLastModifiedUser);
				getAdminRepository().updateItem(item);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : reOrderListSequence() : End");
		}
	}

	public void reOrderListCategories(String pListId, String pCategoryId, int pNewSequenceNo,String pLastModifiedUser) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : reOrderListCategories() : Start");
		}
		if (pListId!="" && pCategoryId!="") {
			MutableRepositoryItem listItem = (MutableRepositoryItem) getAdminRepository()
					.getItem(pListId, BBBAdminConstant.LIST_ITEM_DESCRIPTOR_NAME);
			List<RepositoryItem> originalCategories = (List<RepositoryItem>) listItem
					.getPropertyValue(BBBAdminConstant.CATEGORIES_PROPERTY_NAME);
			RepositoryItem categoryItem = getAdminRepository().getItem(pCategoryId,
					BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
			if (categoryItem != null && originalCategories != null) {
				if (originalCategories.contains(categoryItem)) {
					originalCategories.remove(categoryItem);
					originalCategories.add(pNewSequenceNo, categoryItem);
				}
				listItem.setPropertyValue(
						BBBAdminConstant.CATEGORIES_PROPERTY_NAME,
						originalCategories);
				getAdminRepository().updateItem(listItem);
			for(RepositoryItem category:originalCategories){
				editListCategoryAssocAudit(pListId,category.getRepositoryId().toString(),pLastModifiedUser);
			}
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : reOrderListCategories() : End");
		}

	} 

	public void reOrderCatChildCategories(String pCategoryId, String pChildCategoryId, int pNewSequenceNo, String pLastModifiedUser) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : reOrderCatChildCategories() : Start");
		}
		if (pCategoryId!="" && pChildCategoryId!="") {
			MutableRepositoryItem categoryItem = (MutableRepositoryItem) getAdminRepository()
					.getItem(pCategoryId, BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
			List<RepositoryItem> childCategories = (List<RepositoryItem>) categoryItem
					.getPropertyValue(BBBAdminConstant.CHILD_CATGEORY_PROPERTY_NAME);
			RepositoryItem childCategoryItem = getAdminRepository().getItem(pChildCategoryId,
					BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
			if (categoryItem != null && childCategories != null) {
				if (childCategories.contains(childCategoryItem)) {
					childCategories.remove(childCategoryItem);
					childCategories.add(pNewSequenceNo, childCategoryItem);
				}
				categoryItem.setPropertyValue(
						BBBAdminConstant.CHILD_CATGEORY_PROPERTY_NAME,
						childCategories);
				getAdminRepository().updateItem(categoryItem);
				for(RepositoryItem childCategory:childCategories){
					editCatChildCategoryAssocAudit(pCategoryId,childCategory.getRepositoryId().toString(),pLastModifiedUser);
					}
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : reOrderCatChildCategories() : End");
		}

	}

	public void removeReOrderList() throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : removeReOrderList() : Start");
		}

		RepositoryItem[] listItems = fetchAllItemsFromList();
		if(listItems!=null && listItems.length>0){
			for(int i=0;i<listItems.length;i++){
				MutableRepositoryItem listItem =  (MutableRepositoryItem) listItems[i];
				int seqNo =(int)listItem.getPropertyValue(BBBAdminConstant.SEQUENCE_NUMBER_PROPERTY_NAME);
				if(i!=seqNo){
					listItem.setPropertyValue(BBBAdminConstant.SEQUENCE_NUMBER_PROPERTY_NAME, i);
					getAdminRepository().updateItem(listItem);
				}
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : removeReOrderList() : End");
		}

	}
	/**
	 * This method is used to log insert operations on categories inside list in audit tables.
	 * @param pListId
	 * @param pCategoryId
	 * @param pUserCreated
	 * @param pLastModifiedUser
	 * @throws RepositoryException
	 */
	public void createListCategoryAssocAudit(String pListId,String pCategoryId, String pUserCreated) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createListCategoryAssocAudit() : Start");
		}

		MutableRepositoryItem listCatAuditItem = getAdminRepository().createItem(BBBAdminConstant.LIST_CATEGORY_ASSOC_AUDIT_ITEM_DESCRIPTOR_NAME);
		listCatAuditItem.setPropertyValue(BBBAdminConstant.LIST_CAT_ASSOC_AUDIT_LIST_ID_PROPERTY_NAME, pListId);
		listCatAuditItem.setPropertyValue(BBBAdminConstant.LIST_CAT_ASSOC_AUDIT_LIST_CAT_ID_PROPERTY_NAME, pCategoryId);
		listCatAuditItem.setPropertyValue(BBBAdminConstant.LIST_CAT_ASSOC_AUDIT_CREATED_USER_PROPERTY_NAME, pUserCreated);
		listCatAuditItem.setPropertyValue(BBBAdminConstant.LIST_CAT_ASSOC_AUDIT_LAST_MODIFIED_USER_PROPERTY_NAME, pUserCreated);
		getAdminRepository().addItem(listCatAuditItem);
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createListCategoryAssocAudit() : End");
		}

	}
	/**
	 * This method is used to log insert operations on categories inside list in audit tables.
	 * @param pListId
	 * @param pCategoryId
	 * @param pLastModifiedUser
	 * @throws RepositoryException
	 */
	public void editListCategoryAssocAudit(String pListId,String pCategoryId, String pLastModifiedUser) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : editListCategoryAssocAudit() : Start");
		}

		MutableRepositoryItem listCatAuditItem = (MutableRepositoryItem) getAdminRepository().getItem(pListId+":"+pCategoryId,BBBAdminConstant.LIST_CATEGORY_ASSOC_AUDIT_ITEM_DESCRIPTOR_NAME);
		if(listCatAuditItem!=null){
		listCatAuditItem.setPropertyValue(BBBAdminConstant.LIST_CAT_ASSOC_AUDIT_LAST_MODIFIED_USER_PROPERTY_NAME, pLastModifiedUser);
		getAdminRepository().updateItem(listCatAuditItem);
		} else {
			createListCategoryAssocAudit(pListId,pCategoryId,pLastModifiedUser);
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : editListCategoryAssocAudit() : End");
		}

	}

	public void deleteListCategoryAssocAudit(String pListId,String pCategoryId) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : deleteListCategoryAssocAudit() : Start");
		}

		RepositoryItem listCatAuditItem = getAdminRepository().getItem(pListId+":"+pCategoryId,BBBAdminConstant.LIST_CATEGORY_ASSOC_AUDIT_ITEM_DESCRIPTOR_NAME);
		if(listCatAuditItem!=null){
			getAdminRepository().removeItem(listCatAuditItem.getRepositoryId(),BBBAdminConstant.LIST_CATEGORY_ASSOC_AUDIT_ITEM_DESCRIPTOR_NAME);
		}

		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : deleteListCategoryAssocAudit() : End");
		}

	}

	public void createListCategoryAudit(String pListId, String pCategoryId, String pListDisplayName, String pListSubTypeCode, String pCatDisplayName, String pReasonCode, String pLastModifiedUser) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createListCategoryAudit() : Start");
		}

		MutableRepositoryItem listCatAuditItem = getAdminRepository().createItem(BBBAdminConstant.LIST_CATEGORY_AUDIT_ITEM_DESCRIPTOR_NAME);
		listCatAuditItem.setPropertyValue(BBBAdminConstant.LIST_CAT_AUDIT_LIST_ID_PROPERTY_NAME, pListId);
		listCatAuditItem.setPropertyValue(BBBAdminConstant.LIST_CAT_AUDIT_LIST_CAT_ID_PROPERTY_NAME, pCategoryId);
		listCatAuditItem.setPropertyValue(BBBAdminConstant.LIST_CAT_AUDIT_LIST_DISPLAY_NAME_PROPERTY_NAME, pListDisplayName);
		listCatAuditItem.setPropertyValue(BBBAdminConstant.LIST_CAT_AUDIT_LIST_SUB_TYPE_CODE_PROPERTY_NAME, pListSubTypeCode);
		listCatAuditItem.setPropertyValue(BBBAdminConstant.LIST_CAT_AUDIT_CAT_DISPLAY_NAME_PROPERTY_NAME, pCatDisplayName);
		listCatAuditItem.setPropertyValue(BBBAdminConstant.LIST_CAT_AUDIT_REASON_CODE_PROPERTY_NAME, pReasonCode);
		listCatAuditItem.setPropertyValue(BBBAdminConstant.LIST_CAT_AUDIT_LAST_MODIFIER_USER_PROPERTY_NAME, pLastModifiedUser);
		getAdminRepository().addItem(listCatAuditItem);
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createListCategoryAudit() : End");
		}

	}

	public void deleteAllListCategoryAssocAudit(String pListId) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : deleteAllListCategoryAssocAudit() : Start");
		}
		RepositoryView view = getAdminRepository().getView(BBBAdminConstant.LIST_CATEGORY_ASSOC_AUDIT_ITEM_DESCRIPTOR_NAME);
		Object params[] = new Object[1];
		RqlStatement statement=RqlStatement.parseRqlStatement("listId equals ?0");
		params[0] = pListId;
		RepositoryItem[] listItems = statement.executeQuery(view,params);
		if(listItems!=null && listItems.length>0){
		for(RepositoryItem listItem : listItems){
			if(listItem!=null){
				getAdminRepository().removeItem(listItem.getRepositoryId(),BBBAdminConstant.LIST_CATEGORY_ASSOC_AUDIT_ITEM_DESCRIPTOR_NAME);
			}
		}
	}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : deleteAllListCategoryAssocAudit() : End");
		}

	}
	
	
	
	/**
	 * This method is used to log insert operations on child categories inside categories in audit tables.
	 * @param pListId
	 * @param pCategoryId
	 * @param pUserCreated
	 * @throws RepositoryException
	 */
	public void createCatChildCategoryAssocAudit(String pCategoryId,String pChildCategoryId, String pUserCreated) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createCatChildCategoryAssocAudit() : Start");
		}

		MutableRepositoryItem catChildCatAuditItem = getAdminRepository().createItem(BBBAdminConstant.CATEGORY_CHLDCATEGORY_ASSOC_AUDIT_ITEM_DESCRIPTOR_NAME);
		catChildCatAuditItem.setPropertyValue(BBBAdminConstant.CATEGORY_ID_PROPERTY_NAME, pCategoryId);
		catChildCatAuditItem.setPropertyValue(BBBAdminConstant.CHILD_CATGEORY_PROPERTY_NAME, pChildCategoryId);
		catChildCatAuditItem.setPropertyValue(BBBAdminConstant.CREATED_USER_PROPERTY_NAME, pUserCreated);
		catChildCatAuditItem.setPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME, pUserCreated);
		getAdminRepository().addItem(catChildCatAuditItem);
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createCatChildCategoryAssocAudit() : End");
		}

	}
	/**
	 * This method is used to log edit operations on child categories inside categories in audit tables.
	 * @param pListId
	 * @param pCategoryId
	 * @param pUserCreated
	 * @throws RepositoryException
	 */
	public void editCatChildCategoryAssocAudit(String pCategoryId,String pChildCategoryId, String pLastModifiedUser) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createCatChildCategoryAssocAudit() : Start");
		}

		MutableRepositoryItem catChildCatAuditItem = getAdminRepository().getItemForUpdate(pCategoryId+":"+pChildCategoryId, BBBAdminConstant.CATEGORY_CHLDCATEGORY_ASSOC_AUDIT_ITEM_DESCRIPTOR_NAME);
		if(catChildCatAuditItem!=null){
		catChildCatAuditItem.setPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME, pLastModifiedUser);
		getAdminRepository().updateItem(catChildCatAuditItem);
		} else {
			createCatChildCategoryAssocAudit(pCategoryId,pChildCategoryId,pLastModifiedUser);
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createCatChildCategoryAssocAudit() : End");
		}

	}
	

	/**
	 * This method is used to log delete operations on child categories inside categories in audit tables.
	 * @param pListId
	 * @param pCategoryId
	 * @param pUserCreated
	 * @throws RepositoryException
	 */
	public void deleteCatChildCategoryAssocAudit(String pCategoryId,String pChildCategoryId) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createCatChildCategoryAssocAudit() : Start");
		}

		RepositoryItem catChildCatAuditItem = getAdminRepository().getItem(pCategoryId+":"+pChildCategoryId, BBBAdminConstant.CATEGORY_CHLDCATEGORY_ASSOC_AUDIT_ITEM_DESCRIPTOR_NAME);
		
		if (catChildCatAuditItem!=null) {
			getAdminRepository().removeItem(catChildCatAuditItem.getRepositoryId(),BBBAdminConstant.CATEGORY_CHLDCATEGORY_ASSOC_AUDIT_ITEM_DESCRIPTOR_NAME);
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createCatChildCategoryAssocAudit() : End");
		}

	}
	
	public RepositoryItem createEphRule(String pCategoryId, String pEphId, String pFacetRuleName, String pFacetValuePair, String pCreatedUser) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createEphRule() : Start");
		}
		
		MutableRepositoryItem ephRuleItem = getAdminRepository().createItem(BBBAdminConstant.EPH_FACET_RULE_ITEM_DESCRIPTOR);
		ephRuleItem.setPropertyValue(BBBAdminConstant.EPH_ID_PROPERTY_NAME,pEphId);
		ephRuleItem.setPropertyValue(BBBAdminConstant.FACET_RULE_NAME_PROPERTY_NAME,pFacetRuleName);
		ephRuleItem.setPropertyValue(BBBAdminConstant.FACET_VALUE_LIST_PROPERTY_NAME,pFacetValuePair);
		ephRuleItem.setPropertyValue(BBBAdminConstant.CREATED_USER_PROPERTY_NAME,pCreatedUser);
		ephRuleItem.setPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME,pCreatedUser);
		RepositoryItem ruleItem = getAdminRepository().addItem(ephRuleItem);
		
		
		addEphRuleToCategory(pCategoryId,ruleItem);
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createEphRule() : End");
		}
		
		return ruleItem;

	}
	
	public void addEphRuleToCategory(String pCategoryId, RepositoryItem ephRuleItem) throws RepositoryException{
		MutableRepositoryItem categoryItem = (MutableRepositoryItem) getAdminRepository().getItem(pCategoryId, BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);

		List <RepositoryItem>ephRules = (List<RepositoryItem>)categoryItem.getPropertyValue(BBBAdminConstant.CATEGORY_EPH_RULES_PROPERTY_NAME);
		if(ephRuleItem!=null && ephRules!=null){
			ephRules.add(ephRuleItem);
		}
		else if(ephRuleItem!=null){
			ephRules = new <RepositoryItem>ArrayList();
			ephRules.add(ephRuleItem);
		}
		categoryItem.setPropertyValue(BBBAdminConstant.CATEGORY_EPH_RULES_PROPERTY_NAME, ephRules);
		getAdminRepository().updateItem(categoryItem);
	}
	
	public RepositoryItem editEphRule(String pEphId,String pFacetRuleName, String pFacetValuePairs, String pLastModifiedUser) throws RepositoryException{
		
		MutableRepositoryItem ephRuleItem  = getAdminRepository().getItemForUpdate(pEphId, BBBAdminConstant.EPH_FACET_RULE_ITEM_DESCRIPTOR);
		ephRuleItem.setPropertyValue(BBBAdminConstant.FACET_RULE_NAME_PROPERTY_NAME, pFacetRuleName);
		ephRuleItem.setPropertyValue(BBBAdminConstant.FACET_VALUE_LIST_PROPERTY_NAME, pFacetValuePairs);
		ephRuleItem.setPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME, pLastModifiedUser);
		
		getAdminRepository().updateItem(ephRuleItem);
		
		return (RepositoryItem)ephRuleItem;
		
	}
	
	
	
	/**
	 * This method is used to create JDA Rule item
	 * @param pCategoryId
	 * @param pJdaDeptId
	 * @param pJdaSubDeptId
	 * @param pJdaClass
	 * @param pFacetRuleName
	 * @param pFacetValuePair
	 * @param pCreatedUser
	 * @return
	 * @throws RepositoryException
	 */
	public RepositoryItem createJdaRule(String pCategoryId, String pJdaDeptId,String pJdaSubDeptId,String pJdaClass, String pFacetRuleName, String pFacetValuePair, String pCreatedUser) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createJdaRule() : Start");
		}
		
		MutableRepositoryItem jdaRuleItem = getAdminRepository().createItem(BBBAdminConstant.JDA_FACET_RULE_ITEM_DESCRIPTOR);
		jdaRuleItem.setPropertyValue(BBBAdminConstant.JDA_DEPT_ID_PROPERTY_NAME,pJdaDeptId);
		jdaRuleItem.setPropertyValue(BBBAdminConstant.JDA_SUB_DEPT_ID_PROPERTY_NAME,pJdaSubDeptId);
		jdaRuleItem.setPropertyValue(BBBAdminConstant.JDA_CLASS_PROPERTY_NAME,pJdaClass);
		jdaRuleItem.setPropertyValue(BBBAdminConstant.FACET_RULE_NAME_PROPERTY_NAME,pFacetRuleName);
		jdaRuleItem.setPropertyValue(BBBAdminConstant.FACET_VALUE_LIST_PROPERTY_NAME,pFacetValuePair);
		jdaRuleItem.setPropertyValue(BBBAdminConstant.CREATED_USER_PROPERTY_NAME,pCreatedUser);
		jdaRuleItem.setPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME,pCreatedUser);
		RepositoryItem ruleItem = getAdminRepository().addItem(jdaRuleItem);
		
		
		addJdaRuleToCategory(pCategoryId,ruleItem);
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : createJdaRule() : End");
		}
		
		return ruleItem;

	}
	
	public void addJdaRuleToCategory(String pCategoryId, RepositoryItem jdaRuleItem) throws RepositoryException{
		MutableRepositoryItem categoryItem = (MutableRepositoryItem) getAdminRepository().getItem(pCategoryId, BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);

		List <RepositoryItem>jdaRules = (List<RepositoryItem>)categoryItem.getPropertyValue(BBBAdminConstant.CATEGORY_JDA_RULES_PROPERTY_NAME);
		if(jdaRuleItem!=null && jdaRules!=null){
			jdaRules.add(jdaRuleItem);
		}
		else if(jdaRuleItem!=null){
			jdaRules = new <RepositoryItem>ArrayList();
			jdaRules.add(jdaRuleItem);
		}
		categoryItem.setPropertyValue(BBBAdminConstant.CATEGORY_JDA_RULES_PROPERTY_NAME, jdaRules);
		getAdminRepository().updateItem(categoryItem);
	}
	
	
	public RepositoryItem updateJdaRule(String pRuleId,String pJdaDeptId,String pJdaSubDeptId,String pJdaClass, String pFacetRuleName, String pFacetValuePair,String pLastModifiedUser) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : updateJdaRule() : Start");
		}
		MutableRepositoryItem jdaRuleItem=getAdminRepository().getItemForUpdate(pRuleId,BBBAdminConstant.JDA_FACET_RULE_ITEM_DESCRIPTOR);
		if (jdaRuleItem!=null) {
		String jdaDeptId = 	(String)jdaRuleItem.getPropertyValue(BBBAdminConstant.JDA_DEPT_ID_PROPERTY_NAME);
		String subDeptId = 	(String)jdaRuleItem.getPropertyValue(BBBAdminConstant.JDA_SUB_DEPT_ID_PROPERTY_NAME);
		String jdaClass =  (String)jdaRuleItem.getPropertyValue(BBBAdminConstant.JDA_CLASS_PROPERTY_NAME);	
			
		if(!pJdaDeptId.equals(jdaDeptId) || !pJdaSubDeptId.equals(subDeptId) || !pJdaClass.equals(jdaClass)){
			if (isLoggingDebug()) {
				logDebug("BBBAdminManager : updateJdaRule() : removing facets : "+pFacetValuePair);
			}
			pFacetValuePair = "";
		}	
			
		jdaRuleItem.setPropertyValue(BBBAdminConstant.JDA_DEPT_ID_PROPERTY_NAME,pJdaDeptId);
		jdaRuleItem.setPropertyValue(BBBAdminConstant.JDA_SUB_DEPT_ID_PROPERTY_NAME,pJdaSubDeptId);
		jdaRuleItem.setPropertyValue(BBBAdminConstant.JDA_CLASS_PROPERTY_NAME,pJdaClass);
		jdaRuleItem.setPropertyValue(BBBAdminConstant.FACET_RULE_NAME_PROPERTY_NAME,pFacetRuleName);
		jdaRuleItem.setPropertyValue(BBBAdminConstant.FACET_VALUE_LIST_PROPERTY_NAME,pFacetValuePair);
		jdaRuleItem.setPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME,pLastModifiedUser);
		getAdminRepository().updateItem(jdaRuleItem);
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : updateJdaRule() : End");
		}
		
		
		return jdaRuleItem;

	}
	
	
	public void deleteJdaRule(String pCategoryId,String pRuleId) throws RepositoryException, SQLException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : deleteJdaRule() : Start");
		}
		removeJdaRulesFromCategories(pCategoryId,pRuleId);
		getAdminRepository().removeItem(pRuleId, BBBAdminConstant.JDA_FACET_RULE_ITEM_DESCRIPTOR);
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : deleteJdaRule() : End");
		}
		
	}
	
	public void  removeJdaRulesFromCategories(String pCategoryId,String pRuleId) throws RepositoryException ,SQLException{
		  if (isLoggingDebug()) {
				logDebug("BBBAdminManager : removeJdaRulesFromCategories() : Start");
			}
			if (pCategoryId!=null  && pRuleId!=null) {
				MutableRepositoryItem categoryItem = getAdminRepository().getItemForUpdate(pCategoryId,BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
				List<RepositoryItem> jdaList=(List<RepositoryItem>) categoryItem.getPropertyValue(BBBAdminConstant.CATEGORY_JDA_RULES_PROPERTY_NAME);
				RepositoryItem jdaRuleItem = getAdminRepository().getItem(pRuleId, BBBAdminConstant.JDA_FACET_RULE_ITEM_DESCRIPTOR);
				if(jdaList.contains(jdaRuleItem)){
					jdaList.remove(jdaRuleItem);
					categoryItem.setPropertyValue(BBBAdminConstant.CATEGORY_JDA_RULES_PROPERTY_NAME, jdaList);	
					getAdminRepository().updateItem(categoryItem);
				}
				
			}
			if (isLoggingDebug()) {
				logDebug("BBBAdminManager : removeJdaRulesFromCategories() : End");
			}
		}
	
	public List<RepositoryItem> getJdaRules(String pCategoryId) throws RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : getJdaRules() : Start");
		}
		List<RepositoryItem> jdaRules = null;
		if (pCategoryId!=null) {
			RepositoryItem categoryItem = getAdminRepository().getItem(pCategoryId, BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
			jdaRules = (List)categoryItem.getPropertyValue(BBBAdminConstant.CATEGORY_JDA_RULES_PROPERTY_NAME);
			
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : getJdaRules() : End");
		}
		return jdaRules;
		
	}

/*--------------------------------------*/
	/*---------SKU API--------------*/
/*--------------------------------------*/
public RepositoryItem createSku(String pSkuId,String pCategoryId,String pRuleEvaluation,String pCreatedUser, String pLastModifiedUser ) throws RepositoryException,SQLException{
	
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createSku() : Start");
	}
	MutableRepositoryItem skuItem=getAdminRepository().createItem(BBBAdminConstant.SKU_RULE_ITEM_DESCRIPTOR_NAME);
	skuItem.setPropertyValue(BBBAdminConstant.SKU_ID_PROPERTY_NAME, pSkuId);
	//skuItem.setPropertyValue(BBBAdminConstant.CATEGORY_ID_PROPERTY_NAME, pCategoryId);
	//skuItem.setPropertyValue(BBBAdminConstant.SEQUENCE_NUMBER_PROPERTY_NAME, pSequenceNumber);
	skuItem.setPropertyValue(BBBAdminConstant.RULE_EVALUATION, pRuleEvaluation);
	skuItem.setPropertyValue(BBBAdminConstant.CREATED_USER_PROPERTY_NAME, pCreatedUser);
	skuItem.setPropertyValue(BBBAdminConstant.CREATION_DATE_PROPERTY_NAME, getCurrentTimestampValue());
	skuItem.setPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME, pLastModifiedUser);
	skuItem.setPropertyValue(BBBAdminConstant.LAST_MODIFIED_DATE_PROPERTY_NAME,getCurrentTimestampValue());
	getAdminRepository().addItem(skuItem);
	String pRuleId=skuItem.getRepositoryId();
	skuToCategory(pCategoryId,pRuleId);
	
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createSku() : End");
	}
	return skuItem;
}

public void  skuToCategory(String pCategoryId,String pRuleId) throws RepositoryException {
	MutableRepositoryItem categoryItem = (MutableRepositoryItem) getAdminRepository().getItemForUpdate(pCategoryId, BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
	RepositoryItem skuItem=(RepositoryItem) getAdminRepository().getItem(pRuleId, BBBAdminConstant.SKU_RULE_ITEM_DESCRIPTOR_NAME);
	List<RepositoryItem> categorySkuList = (List<RepositoryItem>)categoryItem.getPropertyValue(BBBAdminConstant.SKU_RULES_PROPERTY_NAME);
	if (categorySkuList!=null && categoryItem!=null && !categorySkuList.contains(skuItem)) {
		
		categorySkuList.add(skuItem);
	}
	else if(categoryItem!=null && !categorySkuList.contains(skuItem))
	{
		categorySkuList = new ArrayList<RepositoryItem>();
		categorySkuList.add(skuItem);
	}
	categoryItem.setPropertyValue(BBBAdminConstant.SKU_RULES_PROPERTY_NAME,categorySkuList);
	getAdminRepository().updateItem(categoryItem);
}

public void deleteSku(String pCategoryId,String pRuleId) throws RepositoryException, SQLException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : deleteSku() : Start");
	}
	removeSkuFromCategories(pCategoryId,pRuleId);
	getAdminRepository().removeItem(pRuleId, BBBAdminConstant.SKU_RULE_ITEM_DESCRIPTOR_NAME);
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : deleteSku() : End");
	}
	
}
public JSONObject createSingleSkuJsonObject(RepositoryItem pItem) throws JSONException, RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createSingleSkuJsonObject() : Start");
	}
	JSONObject skuJson= new JSONObject();
	skuJson.put("rule_id", pItem.getRepositoryId());
	skuJson.put("sku_id", pItem.getPropertyValue(BBBAdminConstant.SKU_ID_PROPERTY_NAME));
	skuJson.put("rule_evaluation_cd", pItem.getPropertyValue(BBBAdminConstant.RULE_EVALUATION));
	skuJson.put("create_user",pItem.getPropertyValue(BBBAdminConstant.CREATED_USER_PROPERTY_NAME));
	skuJson.put("create_date", pItem.getPropertyValue(BBBAdminConstant.CREATION_DATE_PROPERTY_NAME));
	skuJson.put("last_mod_user", pItem.getPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME));
	skuJson.put("last_mod_date", pItem.getPropertyValue(BBBAdminConstant.LAST_MODIFIED_DATE_PROPERTY_NAME));

	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createSingleSkuJsonObject() : End");
	}
	return skuJson;
	
}


public RepositoryItem updateSku(String pRuleId,String pRuleEvaluation,String pLastModifiedUser) throws RepositoryException,SQLException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : updateFacetRules() : Start");
	}
	MutableRepositoryItem skuItem=getAdminRepository().getItemForUpdate(pRuleId,BBBAdminConstant.SKU_RULE_ITEM_DESCRIPTOR_NAME);
	if (pRuleId!=null) {
		skuItem.setPropertyValue(BBBAdminConstant.RULE_EVALUATION, pRuleEvaluation);
		skuItem.setPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME, pLastModifiedUser);
		getAdminRepository().updateItem(skuItem);
	}
	
	 return skuItem;
	
}

public List<RepositoryItem> fetchSkuFromCategory(String pCategory) throws RepositoryException{
	  if (isLoggingDebug()) {
			logDebug("BBBAdminManager : fetchSkuFromCategory() : Start");
		}
	List<RepositoryItem> sku = null;

	if (pCategory!=null) {
		
		RepositoryItem categoryItem = getAdminRepository().getItem(pCategory, BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
		sku = (List<RepositoryItem>) categoryItem.getPropertyValue(BBBAdminConstant.SKU_RULES_PROPERTY_NAME);
	  }
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchSkuFromCategory() : End");
	}
	return sku;
}

public void  removeSkuFromCategories(String pCategoryId,String pRuleId) throws RepositoryException ,SQLException{
	  if (isLoggingDebug()) {
			logDebug("BBBAdminManager : removeSkuFromCategories() : Start");
		}
		if (pCategoryId!=null  && pRuleId!=null) {
			MutableRepositoryItem categoryItem = getAdminRepository().getItemForUpdate(pCategoryId,BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
			List<RepositoryItem> skuList=(List<RepositoryItem>) categoryItem.getPropertyValue(BBBAdminConstant.SKU_RULES_PROPERTY_NAME);
			RepositoryItem skuItem = getAdminRepository().getItem(pRuleId, BBBAdminConstant.SKU_RULE_ITEM_DESCRIPTOR_NAME);
			if(skuList.contains(skuItem)){
				skuList.remove(skuItem);
				categoryItem.setPropertyValue(BBBAdminConstant.SKU_RULES_PROPERTY_NAME, skuList);	
				getAdminRepository().updateItem(categoryItem);
			}
			
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : removeSkuFromCategories() : End");
		}
	}

public void reOrderCatSkus(String pCategoryId, String pRuleId, int pNewSequenceNo) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : reOrderCatSkus() : Start");
	}
	if (pCategoryId!="" && pRuleId!="") {
		MutableRepositoryItem categoryItem = (MutableRepositoryItem) getAdminRepository()
				.getItem(pCategoryId, BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
		List<RepositoryItem> skuList = (List<RepositoryItem>) categoryItem
				.getPropertyValue(BBBAdminConstant.SKU_RULES_PROPERTY_NAME);
		RepositoryItem skuItem = getAdminRepository().getItem(pRuleId,
				BBBAdminConstant.SKU_RULE_ITEM_DESCRIPTOR_NAME);
		if (categoryItem != null && skuList != null) {
			if (skuList.contains(skuItem)) {
				skuList.remove(skuItem);
				skuList.add(pNewSequenceNo, skuItem);
			}
			categoryItem.setPropertyValue(
					BBBAdminConstant.SKU_RULES_PROPERTY_NAME,
					skuList);
			getAdminRepository().updateItem(categoryItem);
		}
	}
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : reOrderCatChildCategories() : End");
	}

}
public JSONArray createCategorySkuJsonObject(String pItem) throws JSONException, RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createCategorySkuJsonObject() : Start");
	}
	JSONArray skuJson = new JSONArray();
	RepositoryItem categoryItem = getAdminRepository().getItem(pItem, BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
	List<RepositoryItem> skuList=(List<RepositoryItem>) categoryItem.getPropertyValue(BBBAdminConstant.SKU_RULES_PROPERTY_NAME);
	for (RepositoryItem sku : skuList) {
		JSONObject jsonSkuCategory = createSingleSkuJsonObject(sku);
		jsonSkuCategory.put("display_name", sku.getPropertyValue(BBBAdminConstant.SKU_ID_PROPERTY_NAME));
		jsonSkuCategory.put("list_cat_id",categoryItem.getRepositoryId());
		skuJson.put(jsonSkuCategory);
	}

	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createSingleListCategory	CategoryJsonObject() : End");
	}
	return skuJson;
	
}
//BBB_EPH_NODE Droplet Methods

public RepositoryItem[] searchParentEphNodeId(String pEphNodeId) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchByEphNodeId() : Start");
	}
	RepositoryItem[] ephNodeItem = null;
	if (pEphNodeId!=null) {

		RepositoryView view = getAdminRepository().getView(BBBAdminConstant.EPH_NODE_ITEM_DESCRIPTOR_NAME);
		RqlStatement ephNodeStatement=RqlStatement.parseRqlStatement(PARENT_EPH_NODE_SEARCH_QUERY);
		Object[]params= new Object[]{pEphNodeId};
		ephNodeItem = ephNodeStatement.executeQuery(view,params);
	}
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchByEphId() : End");
	}
	return ephNodeItem;
	
}
/*
public RepositoryItem[] fetchEphNodeById(String pEphNodeId) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchEphNodeById() : Start");
	}
	RepositoryItem[]  ephNodeItem=new RepositoryItem[1];
	if (pEphNodeId!=null && pEphNodeId!="") {

		ephNodeItem[0]=(RepositoryItem) getAdminRepository().getItem(pEphNodeId, BBBAdminConstant.EPH_NODE_ITEM_DESCRIPTOR_NAME);
		if(ephNodeItem!=null){
			ephNodeItem = searchByEphNodeId(pEphNodeId);
		}
	}
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchEphNodeById() : End");
	}
	return ephNodeItem;	

}*/
public JSONObject createSingleEphNodeJsonObject(RepositoryItem pItem) throws JSONException, RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createSingleEphNodeJsonObject() : Start");
	}
	JSONObject jsonEphNode= new JSONObject();
	jsonEphNode.put("eph_node_id", pItem.getRepositoryId());
	jsonEphNode.put("display_name", pItem.getPropertyValue(BBBAdminConstant.DISPLAY_NAME_PROPERTY_NAME));
	Set<RepositoryItem>childNodes = (Set<RepositoryItem>) pItem.getPropertyValue(BBBAdminConstant.CHILD_NODES_PROPERTY_NAME);
	int childNodesCount = 0;
	if(childNodes!=null){
		childNodesCount = childNodes.size();
	}
	jsonEphNode.put("children", childNodesCount);

	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createSingleEphNodeJsonObject() : End");
	}
	return jsonEphNode;
	
}



public RepositoryItem[] searchParentNodeByDisplayName(String pDisplayName) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchByDisplayName() : Start");
	}
	RepositoryItem[] ephNodeItem = null;
	if (pDisplayName!=null) {

		RepositoryView view = getAdminRepository().getView(BBBAdminConstant.EPH_NODE_ITEM_DESCRIPTOR_NAME);
		RqlStatement ephNodeStatement=RqlStatement.parseRqlStatement(PARENT_EPH_SEARCH_BY_NAME_QUERY);
		Object[]params= new Object[]{pDisplayName};
		ephNodeItem = ephNodeStatement.executeQuery(view,params);
	}
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchByDisplayName() : End");
	}
	return ephNodeItem;
	
}

public List<RepositoryItem> getEphRules(String pCategoryId) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchByDisplayName() : Start");
	}
	List<RepositoryItem> ephRules = null;
	if (pCategoryId!=null) {
		RepositoryItem categoryItem = getAdminRepository().getItem(pCategoryId, BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
		ephRules = (List)categoryItem.getPropertyValue(BBBAdminConstant.CATEGORY_EPH_RULES_PROPERTY_NAME);
		
	}
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchByDisplayName() : End");
	}
	return ephRules;
	
}

public RepositoryItem[] searchEphRulesById(String pRuleId) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchRules() : Start");
	}
	RepositoryItem[] rulesItem;
	RepositoryView view = getAdminRepository().getView(BBBAdminConstant.EPH_FACET_RULE_ITEM_DESCRIPTOR);
	RqlStatement rulesStatement = RqlStatement.parseRqlStatement(SEARCH_RULE_BY_ID_QUERY);
	Object[]params = new Object[]{pRuleId};
	rulesItem = rulesStatement.executeQuery(view,params);
	
	
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchRules() : End");
	}
	return rulesItem;
	
}

public RepositoryItem[] searchEphRulesByName(String pRuleName) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchRules() : Start");
	}
	RepositoryItem[] rulesItem;
	RepositoryView view = getAdminRepository().getView(BBBAdminConstant.EPH_FACET_RULE_ITEM_DESCRIPTOR);
	RqlStatement rulesStatement = RqlStatement.parseRqlStatement(SEARCH_RULES_BY_NAME_QUERY);
	Object[]params = new Object[]{pRuleName};
	rulesItem = rulesStatement.executeQuery(view,params);
	
	
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchRules() : End");
	}
	return rulesItem;
	
}

public RepositoryItem[] fetchAllRules() throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchRules() : Start");
	}
	RepositoryItem[] rulesItem;
	RepositoryView view = getAdminRepository().getView(BBBAdminConstant.EPH_FACET_RULE_ITEM_DESCRIPTOR);
	RqlStatement rulesStatement = RqlStatement.parseRqlStatement(ALL_QUERY);
	Object[]params = new Object[1];
	rulesItem = rulesStatement.executeQuery(view,params);
	
	
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchRules() : End");
	}
	return rulesItem;
	
}

/*Search on ephFacet item descriptor by ephId*/


public List<RepositoryItem> fetchEphFacetsByEphId(String pEphId) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchEphFacetsById() : Start");
	}
	RepositoryView view=getAdminRepository().getView(BBBAdminConstant.EPH_FACET_ITEM_DESCRIPTOR_NAME);
	RqlStatement rulesStatement = RqlStatement.parseRqlStatement(EPH_FACETS_BY_EPH_ID_QUERY);
	Object[]params = new Object[]{pEphId};
	RepositoryItem[] ephFacetItems = rulesStatement.executeQuery(view,params);
	List<RepositoryItem> ephFacets = new<RepositoryItem> ArrayList();
	if(ephFacetItems!=null && ephFacetItems.length>0){
		for(RepositoryItem ephFacetItem:ephFacetItems ){
			RepositoryItem facetItem = (RepositoryItem)ephFacetItem.getPropertyValue(BBBAdminConstant.EPH_FACET_ID_PROPERTY_NAME);
			if(facetItem!=null && !ephFacets.contains(facetItem)){
				ephFacets.add(facetItem);
			}
		}
	}
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchEphFacetsById() : End");

}
	return ephFacets;
}

public List<RepositoryItem> fetchEphFacetsByFacetId(String pEphId, String pFacetId) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchEphFacetsById() : Start");
	}
	RepositoryView view=getAdminRepository().getView(BBBAdminConstant.EPH_FACET_ITEM_DESCRIPTOR_NAME);
	RqlStatement rulesStatement = RqlStatement.parseRqlStatement(EPH_FACETS_BY_FACET_ID_QUERY);
	Object[]params = new Object[]{pEphId,pFacetId};
	RepositoryItem[] ephFacetItems = rulesStatement.executeQuery(view,params);
	List<RepositoryItem> ephFacets = new<RepositoryItem> ArrayList();
	
	if(ephFacetItems!=null && ephFacetItems.length>0){
		for(RepositoryItem ephFacetItem:ephFacetItems ){
			RepositoryItem facetItem = (RepositoryItem)ephFacetItem.getPropertyValue(BBBAdminConstant.EPH_FACET_ID_PROPERTY_NAME);
			
			if(facetItem!=null && !ephFacets.contains(facetItem)){
				ephFacets.add(facetItem);
				}
		}
	}
	
	
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchEphFacetsById() : End");

}
	return ephFacets;
}

public RepositoryItem[] fetchFacetValueByFacetId(String pEphId, String pFacetId) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchEphFacetsById() : Start");
	}
	RepositoryView view=getAdminRepository().getView(BBBAdminConstant.EPH_FACET_ITEM_DESCRIPTOR_NAME);
	RqlStatement rulesStatement = RqlStatement.parseRqlStatement(FACET_VALUE_BY_FACET_ID_QUERY);
	Object[]params = new Object[]{pEphId,pFacetId};
	RepositoryItem[] ephFacetItems = rulesStatement.executeQuery(view,params);
	
	
	
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchEphFacetsById() : End");

}
	return ephFacetItems;
}



public List<RepositoryItem> fetchEphFacetsByFacetName(String pEphId, String pFacetName) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchEphFacetsById() : Start");
	}
	RepositoryView view=getAdminRepository().getView(BBBAdminConstant.EPH_FACET_ITEM_DESCRIPTOR_NAME);
	RqlStatement rulesStatement = RqlStatement.parseRqlStatement(EPH_FACETS_BY_EPH_ID_QUERY);
	Object[]params = new Object[]{pEphId};
	RepositoryItem[] ephFacetItems = rulesStatement.executeQuery(view,params);
	List<RepositoryItem> ephFacets = new<RepositoryItem> ArrayList();
	if(ephFacetItems!=null && ephFacetItems.length>0){
		for(RepositoryItem ephFacetItem : ephFacetItems){
			RepositoryItem facetItem = (RepositoryItem)ephFacetItem.getPropertyValue(BBBAdminConstant.EPH_FACET_ID_PROPERTY_NAME);
			if(facetItem!=null){
				String facetName = (String)facetItem.getPropertyValue(BBBAdminConstant.DESCRIPTION_PROPERTY_NAME);
				if(facetName!=null && facetName.toLowerCase().startsWith(pFacetName.toLowerCase()) && !ephFacets.contains(facetItem)){
					ephFacets.add(facetItem);
				}
			}
		}




	}


	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchEphFacetsById() : End");

	}
	return ephFacets;
}



public JSONObject createSingleFacetJsonObject(RepositoryItem facetItem) throws JSONException, RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createSingleEphFacetJsonObject() : Start");
	}
	JSONObject facetJson= new JSONObject();
	if(facetItem!=null){
	facetJson.put("facet_id", facetItem.getPropertyValue(BBBAdminConstant.ID_PROPERTY_NAME));
	facetJson.put("description", facetItem.getPropertyValue(BBBAdminConstant.DESCRIPTION_PROPERTY_NAME));		
	}
	
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createSingleEphFacetJsonObject() : End");
	}
	return facetJson;
	
}

public JSONObject createSingleEphFacetValueJsonObject(RepositoryItem pItem) throws JSONException, RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createSingleEphFacetValueJsonObject() : Start");
	}
	JSONObject facetValueJson= new JSONObject();
	facetValueJson.put("facet_id", pItem.getPropertyValue(BBBAdminConstant.EPH_FACET_ID_PROPERTY_NAME));
	facetValueJson.put("facet_value_id", pItem.getPropertyValue(BBBAdminConstant.FACET_VALUE_ID_PROPERTY_NAME));		
	facetValueJson.put("facet_value_desc", pItem.getPropertyValue(BBBAdminConstant.FACET_VALUE_DESCRIPTION_PROPERTY_NAME));
	
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createSingleEphFacetValueJsonObject() : End");
	}
	return facetValueJson;
	
}




public List<RepositoryItem> fetchChildEphNodes(String pParentNodeId,String pDisplayname) throws RepositoryException{
	List<RepositoryItem> childItems=new ArrayList<RepositoryItem>();
	if (pParentNodeId!=null) {
		RepositoryItem parentEphNode = getAdminRepository().getItem(pParentNodeId, BBBAdminConstant.EPH_NODE_ITEM_DESCRIPTOR_NAME);
		Set<RepositoryItem> childNodeList=(Set<RepositoryItem>) parentEphNode.getPropertyValue(BBBAdminConstant.CHILD_NODES_PROPERTY_NAME);
		for (RepositoryItem childEphNodes : childNodeList) {
					childItems.add(childEphNodes);
			}
		}
	return childItems;
}

public RepositoryItem[] getRootNodesByName(String  pDisplayName) throws RepositoryException{
	
	RepositoryView view=getAdminRepository().getView(BBBAdminConstant.EPH_NODE_ITEM_DESCRIPTOR_NAME);
	RqlStatement rulesStatement = RqlStatement.parseRqlStatement(PARENT_EPH_SEARCH_BY_NAME_QUERY);
	Object[]params = new Object[]{pDisplayName};
	RepositoryItem[] ephFacetItems = rulesStatement.executeQuery(view,params);
	
	return ephFacetItems;
}

public RepositoryItem[] getAllRootNodes() throws RepositoryException{
	
	RepositoryView view=getAdminRepository().getView(BBBAdminConstant.EPH_NODE_ITEM_DESCRIPTOR_NAME);
	RqlStatement rulesStatement = RqlStatement.parseRqlStatement(ALL_EPH_PARENT_NODES_QUERY);
	Object[]params = new Object[]{};
	RepositoryItem[] ephFacetItems = rulesStatement.executeQuery(view,params);
	
	return ephFacetItems;
}


public List<RepositoryItem> searchChildEphNodeByName(String pParentNodeId,String pDisplayname) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchChildEphNode() : Start");
	}
	List<RepositoryItem> childItems=new ArrayList<RepositoryItem>();
	if (pParentNodeId!=null) {
		
		RepositoryItem parentEphNode = getAdminRepository().getItem(pParentNodeId, BBBAdminConstant.EPH_NODE_ITEM_DESCRIPTOR_NAME);
		Set<RepositoryItem> childNodeList=(Set<RepositoryItem>) parentEphNode.getPropertyValue(BBBAdminConstant.CHILD_NODES_PROPERTY_NAME);
		for (RepositoryItem childNode : childNodeList) {
			String displayName	=(String)childNode.getPropertyValue(BBBAdminConstant.DISPLAY_NAME_PROPERTY_NAME);
			
			if (pDisplayname != null) {
				if (displayName != null && displayName.toLowerCase().startsWith(pDisplayname.toLowerCase())) {
					childItems.add(childNode);
				}
			}
		}
	}
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchChildEphNode() : End");
	}	
	return childItems;
	
}

public List<RepositoryItem> searchChildEphNodeById(String pParentNodeId,String pNodeId) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchChildEphNode() : Start");
	}
	List<RepositoryItem> childItems=new ArrayList<RepositoryItem>();
	if (pParentNodeId!=null) {
		
		RepositoryItem parentEphNode = getAdminRepository().getItem(pParentNodeId, BBBAdminConstant.EPH_NODE_ITEM_DESCRIPTOR_NAME);
		Set<RepositoryItem> childNodeList=(Set<RepositoryItem>) parentEphNode.getPropertyValue(BBBAdminConstant.CHILD_NODES_PROPERTY_NAME);
		for (RepositoryItem childNode : childNodeList) {
			String nodeId	=(String)childNode.getRepositoryId();
			
			if (pNodeId != null) {
				if (nodeId != null && nodeId.toLowerCase().startsWith(pNodeId.toLowerCase())) {
					childItems.add(childNode);
				}
			}
		}
	}
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchChildEphNode() : End");
	}	
	return childItems;
	
}

public List<RepositoryItem> searchChildEphNode(String pParentNodeId,String pDisplayname) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchChildEphNode() : Start");
	}
	List<RepositoryItem> childItems=new ArrayList<RepositoryItem>();
	if (pParentNodeId!=null) {
		
		RepositoryItem parentEphNode = getAdminRepository().getItem(pParentNodeId, BBBAdminConstant.EPH_NODE_ITEM_DESCRIPTOR_NAME);
		Set<RepositoryItem> childNodeList=(Set<RepositoryItem>) parentEphNode.getPropertyValue(BBBAdminConstant.CHILD_NODES_PROPERTY_NAME);
		for (RepositoryItem childNode : childNodeList) {
			String displayName	=(String)childNode.getPropertyValue(BBBAdminConstant.DISPLAY_NAME_PROPERTY_NAME);
			
			if (pDisplayname != null) {
				if (displayName != null && displayName.toLowerCase().startsWith(pDisplayname.toLowerCase())) {
					childItems.add(childNode);
				}
			}
		}
	}
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchChildEphNode() : End");
	}	
	return childItems;
	
}

public void reOrderCatEph(String pCategoryId, String pRuleId, int pNewSequenceNo) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : reOrderCatEph() : Start");
	}
	if (pCategoryId!="" && pRuleId!="") {
		MutableRepositoryItem categoryItem = (MutableRepositoryItem) getAdminRepository()
				.getItem(pCategoryId, BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
		List<RepositoryItem> ephRules = (List<RepositoryItem>) categoryItem
				.getPropertyValue(BBBAdminConstant.CATEGORY_EPH_RULES_PROPERTY_NAME);
		RepositoryItem ephFacetItem = getAdminRepository().getItem(pRuleId,
				BBBAdminConstant.EPH_FACET_RULE_ITEM_DESCRIPTOR);
		if (categoryItem != null && ephRules != null) {
			if (ephRules.contains(ephFacetItem)) {
				ephRules.remove(ephFacetItem);
				ephRules.add(pNewSequenceNo, ephFacetItem);
			}
			categoryItem.setPropertyValue(
					BBBAdminConstant.CATEGORY_EPH_RULES_PROPERTY_NAME,
					ephRules);
			getAdminRepository().updateItem(categoryItem);
		}
	}
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : reOrderCatEph() : End");
	}

}

public RepositoryItem updateEphRule(String pRuleId,String pEphId, String pFacetRuleName, String pFacetValuePair,String pLastModifiedUser) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : updateEphRule() : Start");
	}
	MutableRepositoryItem ephRuleItem=getAdminRepository().getItemForUpdate(pRuleId,BBBAdminConstant.EPH_FACET_RULE_ITEM_DESCRIPTOR);
	if (pRuleId!=null) {
	ephRuleItem.setPropertyValue(BBBAdminConstant.EPH_ID_PROPERTY_NAME,pEphId);
	ephRuleItem.setPropertyValue(BBBAdminConstant.FACET_RULE_NAME_PROPERTY_NAME,pFacetRuleName);
	ephRuleItem.setPropertyValue(BBBAdminConstant.FACET_VALUE_LIST_PROPERTY_NAME,pFacetValuePair);
	ephRuleItem.setPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME,pLastModifiedUser);
	getAdminRepository().updateItem(ephRuleItem);
	}
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : updateEphRule() : End");
	}
	
	return ephRuleItem;

}

public void deleteEph(String pCategoryId,String pRuleId) throws RepositoryException, SQLException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : deleteEph() : Start");
	}
	removeEphFromCategories(pCategoryId,pRuleId);
	getAdminRepository().removeItem(pRuleId, BBBAdminConstant.EPH_FACET_RULE_ITEM_DESCRIPTOR);
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : deleteEph() : End");
	}
	
}

public void  removeEphFromCategories(String pCategoryId,String pRuleId) throws RepositoryException ,SQLException{
	  if (isLoggingDebug()) {
			logDebug("BBBAdminManager : removeEphFromCategories() : Start");
		}
		if (pCategoryId!=null  && pRuleId!=null) {
			MutableRepositoryItem categoryItem = getAdminRepository().getItemForUpdate(pCategoryId,BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
			List<RepositoryItem> ephList=(List<RepositoryItem>) categoryItem.getPropertyValue(BBBAdminConstant.CATEGORY_EPH_RULES_PROPERTY_NAME);
			RepositoryItem ephItem = getAdminRepository().getItem(pRuleId, BBBAdminConstant.EPH_FACET_RULE_ITEM_DESCRIPTOR);
			if(ephList.contains(ephItem)){
				ephList.remove(ephItem);
				categoryItem.setPropertyValue(BBBAdminConstant.CATEGORY_EPH_RULES_PROPERTY_NAME, ephList);	
				getAdminRepository().updateItem(categoryItem);
			}
			
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : removeSkuFromCategories() : End");
		}
	}


public void getAllNodesInHierarchy(String pEphId, List<String>pEphNodes) throws RepositoryException{
	
	RepositoryItem ephNode = getAdminRepository().getItem(pEphId, BBBAdminConstant.EPH_NODE_ITEM_DESCRIPTOR_NAME);
	if(ephNode!=null){
	Set<RepositoryItem>childNodes =  (Set<RepositoryItem>) ephNode.getPropertyValue(BBBAdminConstant.CHILD_NODES_PROPERTY_NAME);
	if(childNodes!=null && childNodes.isEmpty()){
		pEphNodes.add(ephNode.getRepositoryId().toString());
	}
	else if(childNodes!=null){
		for(RepositoryItem childNode : childNodes){
			getAllNodesInHierarchy(childNode.getRepositoryId(),pEphNodes);
		}
	}

	}	
}
//JDA Api's
/**
 * This method is used to fetch all Jda department
 * @return
 * @throws RepositoryException
 */
public RepositoryItem[] fetchAllJdaDept() throws RepositoryException{

	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchAllJdaDept() : Start");
	}
	
	RepositoryView view = getProductCatalog().getView(BBBAdminConstant.JDA_DEPT_ITEM_DESCRIPTOR);
	Object params[] = new Object[1];
	RqlStatement statement=RqlStatement.parseRqlStatement(JDA_DEPT_ALL_QUERY);
	RepositoryItem[] items=statement.executeQuery(view,params);
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchAllJdaDept() : End");
	}
	return items;

}


/**
 * This method is used to fetch all Jda Sub-department
 * @param pJdaDept
 * @return
 * @throws RepositoryException
 */
public RepositoryItem[] fetchAllJdaSubDept(String pJdaDept) throws RepositoryException{

	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchAllJdaSubDept() : Start");
	}
	
	RepositoryView view = getProductCatalog().getView(BBBAdminConstant.JDA_SUB_DEPT_ITEM_DESCRIPTOR);
	Object params[] = new Object[1];
	RqlStatement statement=RqlStatement.parseRqlStatement(JDA_SUB_DEPT_ALL_QUERY);
	params[0] = pJdaDept;
	RepositoryItem[] items=statement.executeQuery(view,params);
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchAllJdaSubDept() : End");
	}
	return items;

}



/**
 * This method is used to fetch all Jda class
 * @param pJdaDept
 * @param pJdaSubDept
 * @return
 * @throws RepositoryException
 */
public RepositoryItem[] fetchAllClass(String pJdaDept, String pJdaSubDept) throws RepositoryException{

	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchAllClass() : Start");
	}
	pJdaSubDept = pJdaSubDept.substring((pJdaSubDept.indexOf("_"))+1);
	RepositoryView view = getProductCatalog().getView(BBBAdminConstant.JDA_CLASS_ITEM_DESCRIPTOR);
	Object params[] = new Object[2];
	RqlStatement statement=RqlStatement.parseRqlStatement(JDA_CLASS_ALL_QUERY);
	params[0] = pJdaDept;
	params[1] = pJdaSubDept;
	RepositoryItem[] items=statement.executeQuery(view,params);
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchAllClass() : End");
	}
	return items;

}


/**
 * This method is used to search all Jda dept by id.
 * @param pJdaDeptId
 * @return
 * @throws RepositoryException
 */
public RepositoryItem[] searchJdaDeptById(String pJdaDeptId) throws RepositoryException{

	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchJdaDeptById() : Start");
	}
	
	RepositoryView view = getProductCatalog().getView(BBBAdminConstant.JDA_DEPT_ITEM_DESCRIPTOR);
	Object params[] = new Object[1];
	RqlStatement statement=RqlStatement.parseRqlStatement(JDA_DEPT_BY_ID_SEARCH_QUERY);
	params[0] = pJdaDeptId;
	RepositoryItem[] items=statement.executeQuery(view,params);
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchJdaDeptById() : End");
	}
	return items;

}

/**
 * This method is used to search jda dept by name.
 * @param pJdaDeptName
 * @return
 * @throws RepositoryException
 */
public RepositoryItem[] searchJdaDeptByName(String pJdaDeptName) throws RepositoryException{

	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchJdaDeptByName() : Start");
	}
	
	RepositoryView view = getProductCatalog().getView(BBBAdminConstant.JDA_DEPT_ITEM_DESCRIPTOR);
	Object params[] = new Object[1];
	RqlStatement statement=RqlStatement.parseRqlStatement(JDA_DEPT_BY_NAME_SEARCH_QUERY);
	params[0] = pJdaDeptName;
	RepositoryItem[] items=statement.executeQuery(view,params);
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchJdaDeptByName() : End");
	}
	return items;

}

/**
 * This method is used to search jda subdept by name.
 * @param pJdaDeptId
 * @param pJdaSubDeptName
 * @return
 * @throws RepositoryException
 */
public RepositoryItem[] searchJdaSubDeptByName(String pJdaDeptId,  String pJdaSubDeptName) throws RepositoryException{

	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchJdaSubDeptByName() : Start");
	}
	
	RepositoryView view = getProductCatalog().getView(BBBAdminConstant.JDA_SUB_DEPT_ITEM_DESCRIPTOR);
	Object params[] = new Object[2];
	RqlStatement statement=RqlStatement.parseRqlStatement(JDA_SUBDEPT_BY_NAME_SEARCH_QUERY);
	params[0] = pJdaDeptId;
	params[1] = pJdaSubDeptName;
	RepositoryItem[] items=statement.executeQuery(view,params);
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchJdaSubDeptByName() : End");
	}
	return items;

}

/**
 * This method is used to search jda sub dept by id.
 * @param pJdaDeptId
 * @param pJdaSubDeptId
 * @return
 * @throws RepositoryException
 */
public RepositoryItem[] searchJdaSubDeptById(String pJdaDeptId,  String pJdaSubDeptId) throws RepositoryException{

	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchJdaSubDeptById() : Start");
	}
	
	RepositoryView view = getProductCatalog().getView(BBBAdminConstant.JDA_SUB_DEPT_ITEM_DESCRIPTOR);
	Object params[] = new Object[2];
	RqlStatement statement=RqlStatement.parseRqlStatement(JDA_SUBDEPT_BY_ID_SEARCH_QUERY);
	params[0] = pJdaDeptId;
	params[1] = pJdaSubDeptId;
	RepositoryItem[] items=statement.executeQuery(view,params);
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchJdaSubDeptById() : End");
	}
	return items;

}

/**
 * This method is used to search jda class by id.
 * @param pJdaDeptId
 * @param pJdaSubDeptId
 * @param pJdaClassId
 * @return
 * @throws RepositoryException
 */
public RepositoryItem[] searchJdaClassById(String pJdaDeptId,  String pJdaSubDeptId, String  pJdaClassId) throws RepositoryException{

	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchJdaClassById() : Start");
	}
	pJdaSubDeptId = pJdaSubDeptId.substring((pJdaSubDeptId.indexOf("_"))+1);
	RepositoryView view = getProductCatalog().getView(BBBAdminConstant.JDA_CLASS_ITEM_DESCRIPTOR);
	Object params[] = new Object[3];
	RqlStatement statement=RqlStatement.parseRqlStatement(JDA_CLASS_BY_ID_SEARCH_QUERY);
	params[0] = pJdaDeptId;
	params[1] = pJdaSubDeptId;
	params[2] = pJdaClassId;
	RepositoryItem[] items=statement.executeQuery(view,params);
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchJdaClassById() : End");
	}
	return items;

}

/**
 * This method is used to search jda class based on class name
 * @param pJdaDeptId
 * @param pJdaSubDeptId
 * @param pJdaClassName
 * @return
 * @throws RepositoryException
 */
public RepositoryItem[] searchJdaClassByName(String pJdaDeptId,  String pJdaSubDeptId, String  pJdaClassName) throws RepositoryException{

	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchJdaClassById() : Start");
	}
	pJdaSubDeptId = pJdaSubDeptId.substring((pJdaSubDeptId.indexOf("_"))+1);
	RepositoryView view = getProductCatalog().getView(BBBAdminConstant.JDA_CLASS_ITEM_DESCRIPTOR);
	Object params[] = new Object[3];
	RqlStatement statement = RqlStatement.parseRqlStatement(JDA_CLASS_BY_NAME_SEARCH_QUERY);
	params[0] = pJdaDeptId;
	params[1] = pJdaSubDeptId;
	params[2] = pJdaClassName;
	RepositoryItem[] items=statement.executeQuery(view,params);
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchJdaClassById() : End");
	}
	return items;

}


/**
 * This method returns json object for jda dept
 * @param pJdaDept
 * @return
 * @throws JSONException
 * @throws RepositoryException
 */
public JSONObject createSingleJdaDeptJson(RepositoryItem pJdaDept) throws JSONException, RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createSingleJdaDeptJson() : Start");
	}
	JSONObject jsonJdaDept = new JSONObject();
	String jdaDeptId = (String)pJdaDept.getPropertyValue(BBBAdminConstant.ID_PROPERTY_NAME);
	jsonJdaDept.put("jda_dept_id", jdaDeptId);
	jsonJdaDept.put("descrip", pJdaDept.getPropertyValue(BBBAdminConstant.JDA_DESCRIPTION_PROPERTY_NAME));
	RepositoryItem[] subdeptItems = fetchAllJdaSubDept(jdaDeptId);
	int count=0;
	if(subdeptItems!=null){
		count = subdeptItems.length;
	}
	jsonJdaDept.put("children", count);
	return jsonJdaDept;
}



/**
 * This method returns json object for jda sub dept
 * @param pJdaSubDept
 * @return
 * @throws JSONException
 * @throws RepositoryException
 */
public JSONObject createSingleJdaSubDeptJson(RepositoryItem pJdaSubDept) throws JSONException, RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createSingleJdaDeptJson() : Start");
	}
	JSONObject jsonJdaSubDept = new JSONObject();
	RepositoryItem jdaDeptItem = (RepositoryItem) pJdaSubDept.getPropertyValue(BBBAdminConstant.JDA_DEPT_ID_PROPERTY_NAME);
	String jdaDeptId = (String)jdaDeptItem.getRepositoryId();
	String jdaSubDeptId = (String)pJdaSubDept.getPropertyValue(BBBAdminConstant.ID_PROPERTY_NAME);
	jsonJdaSubDept.put("jda_dept_id", jdaDeptId);
	jsonJdaSubDept.put("jda_sub_dept_id", jdaSubDeptId);
	jsonJdaSubDept.put("descrip", pJdaSubDept.getPropertyValue(BBBAdminConstant.JDA_DESCRIPTION_PROPERTY_NAME));
	RepositoryItem[] jdaClasses = fetchAllClass(jdaDeptId, jdaSubDeptId);
	int count=0;
	if(jdaClasses!=null){
		count = jdaClasses.length;
	}
	jsonJdaSubDept.put("children", count);
	return jsonJdaSubDept;
}


/**
 * This method returns json object for jda sub dept
 * @param pJdaClass
 * @return
 * @throws JSONException
 * @throws RepositoryException
 */
public JSONObject createSingleJdaClassJson(RepositoryItem pJdaClass) throws JSONException, RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createSingleJdaDeptJson() : Start");
	}
	JSONObject jsonJdaSubDept = new JSONObject();
	
	
	String jdaDeptId = (String)pJdaClass.getPropertyValue(BBBAdminConstant.JDA_DEPT_ID_PROPERTY_NAME);
	String jdaSubDeptId = (String)pJdaClass.getPropertyValue(BBBAdminConstant.JDA_SUB_DEPT_ID_PROPERTY_NAME);
	String jdaClass = (String)pJdaClass.getPropertyValue(BBBAdminConstant.JDA_CLASS_PROPERTY_NAME);
	jsonJdaSubDept.put("jda_dept_id", jdaDeptId);
	jsonJdaSubDept.put("jda_sub_dept_id", jdaDeptId+"_"+jdaSubDeptId);
	jsonJdaSubDept.put("jda_class", jdaClass);
	jsonJdaSubDept.put("descrip", pJdaClass.getPropertyValue(BBBAdminConstant.JDA_DESCRIPTION_PROPERTY_NAME));
	jsonJdaSubDept.put("children", 0);
	return jsonJdaSubDept;
}

/**
 * This method is used for fetching all facets belonging to specific jda hierarchy.
 * @param pJdaDeptid
 * @param pJdaSubDeptid
 * @param pJdaClass
 * @return
 * @throws RepositoryException
 */
public List<RepositoryItem> fetchAllJdaFacets(String pJdaDeptid, String pJdaSubDeptid, String pJdaClass) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchAllJdaFacets() : Start");
	}
	RepositoryView view=getAdminRepository().getView(BBBAdminConstant.JDA_FACET_ITEM_DESCRIPTOR_NAME);
	RqlStatement jdaFacetStatement;
	Object[]params;
	if(pJdaClass!=null){
		jdaFacetStatement = RqlStatement.parseRqlStatement(ALL_JDA_FACETS_WITH_JDACLASS_QUERY);
	    params = new Object[]{pJdaDeptid,pJdaSubDeptid,pJdaClass};
	} else {
		jdaFacetStatement = RqlStatement.parseRqlStatement(ALL_JDA_FACETS_WITHOUT_JDACLASS_QUERY);
		params = new Object[]{pJdaDeptid,pJdaSubDeptid};
	}
	
	RepositoryItem[] jdaFacetItems = jdaFacetStatement.executeQuery(view,params);
	List<RepositoryItem> jdaFacets = new<RepositoryItem> ArrayList();
	if(jdaFacetItems!=null && jdaFacetItems.length>0){
		for(RepositoryItem jdaFacetItem:jdaFacetItems ){
			RepositoryItem facetItem = (RepositoryItem)jdaFacetItem.getPropertyValue(BBBAdminConstant.JDA_FACET_ID_PROPERTY_NAME);
			if(facetItem!=null && !jdaFacets.contains(facetItem)){
				jdaFacets.add(facetItem);
			}
		}
	}
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchAllJdaFacets() : End");

}
	return jdaFacets;
}

/**
 * This method is used to search jda  facets by facet id.
 * @param pJdaDeptid
 * @param pJdaSubDeptid
 * @param pJdaClass
 * @param pFacetId
 * @return
 * @throws RepositoryException
 */
public List<RepositoryItem> fetchJdaFacetsByFacetId(String pJdaDeptid, String pJdaSubDeptid, String pJdaClass, String pFacetId) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchEphFacetsById() : Start");
	}
	RepositoryView view=getAdminRepository().getView(BBBAdminConstant.JDA_FACET_ITEM_DESCRIPTOR_NAME);
	RqlStatement jdaFacetStatement;
	Object[]params;
	if(pJdaClass!=null){
		jdaFacetStatement = RqlStatement.parseRqlStatement(JDA_FACET_BY_FACETID_WITH_JDACLASS_QUERY);
	    params = new Object[]{pJdaDeptid,pJdaSubDeptid,pJdaClass,pFacetId};
	} else {
		jdaFacetStatement = RqlStatement.parseRqlStatement(JDA_FACET_BY_FACETID_WITHOUT_JDACLASS_QUERY);
		params = new Object[]{pJdaDeptid,pJdaSubDeptid,pFacetId};
	}
	
	RepositoryItem[] jdaFacetItems = jdaFacetStatement.executeQuery(view,params);
	List<RepositoryItem> jdaFacets = new<RepositoryItem> ArrayList();
	
	if(jdaFacetItems!=null && jdaFacetItems.length>0){
		for(RepositoryItem jdaFacetItem:jdaFacetItems ){
			RepositoryItem facetItem = (RepositoryItem)jdaFacetItem.getPropertyValue(BBBAdminConstant.JDA_FACET_ID_PROPERTY_NAME);
			
			if(facetItem!=null && !jdaFacets.contains(facetItem)){
				jdaFacets.add(facetItem);
				}
		}
	}
	
	
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchEphFacetsById() : End");

}
	return jdaFacets;
}

/**
 * This method is used to search jda facets by facet name
 * @param pJdaDeptid
 * @param pJdaSubDeptid
 * @param pJdaClass
 * @param pFacetName
 * @return
 * @throws RepositoryException
 */
public List<RepositoryItem> fetchJdaFacetsByFacetName(String pJdaDeptid, String pJdaSubDeptid, String pJdaClass, String pFacetName) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchJdaFacetsByFacetName() : Start");
	}
	RepositoryView view=getAdminRepository().getView(BBBAdminConstant.JDA_FACET_ITEM_DESCRIPTOR_NAME);
	RqlStatement jdaFacetStatement;
	Object[]params;
	if(pJdaClass!=null){
		jdaFacetStatement = RqlStatement.parseRqlStatement(ALL_JDA_FACETS_WITH_JDACLASS_QUERY);
	    params = new Object[]{pJdaDeptid,pJdaSubDeptid,pJdaClass};
	} else {
		jdaFacetStatement = RqlStatement.parseRqlStatement(ALL_JDA_FACETS_WITHOUT_JDACLASS_QUERY);
		params = new Object[]{pJdaDeptid,pJdaSubDeptid};
	}
	
	RepositoryItem[] jdaFacetItems = jdaFacetStatement.executeQuery(view,params);
	List<RepositoryItem> jdaFacets = new<RepositoryItem> ArrayList();
	if(jdaFacetItems!=null && jdaFacetItems.length>0){
		for(RepositoryItem jdaFacetItem : jdaFacetItems){
			RepositoryItem facetItem = (RepositoryItem)jdaFacetItem.getPropertyValue(BBBAdminConstant.JDA_FACET_ID_PROPERTY_NAME);
			if(facetItem!=null){
				String facetName = (String)facetItem.getPropertyValue(BBBAdminConstant.DESCRIPTION_PROPERTY_NAME);
				if(facetName!=null && facetName.toLowerCase().startsWith(pFacetName.toLowerCase()) && !jdaFacets.contains(facetItem)){
					jdaFacets.add(facetItem);
				}
			}
		}
	}

	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchJdaFacetsByFacetName() : End");

	}
	return jdaFacets;
}

/**
 * This method is used to fetch jda facet values for particular jda,subdept & class facet
 * @param pJdaDeptid
 * @param pJdaSubDeptid
 * @param pJdaClass
 * @param pFacetId
 * @return
 * @throws RepositoryException
 */
public RepositoryItem[] fetchJdaFacetValueByFacetId(String pJdaDeptid, String pJdaSubDeptid, String pJdaClass, String pFacetId) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchJdaFacetValueByFacetId() : Start");
	}
	RepositoryView view=getAdminRepository().getView(BBBAdminConstant.JDA_FACET_ITEM_DESCRIPTOR_NAME);
	RqlStatement jdaFacetValueStatement;
	Object[]params;
	if(pJdaClass!=null){
		jdaFacetValueStatement = RqlStatement.parseRqlStatement(JDA_FACETVALUE_BY_FACETID_WITH_JDACLASS_QUERY);
	    params = new Object[]{pJdaDeptid,pJdaSubDeptid,pJdaClass,pFacetId};
		
	} else {
		jdaFacetValueStatement = RqlStatement.parseRqlStatement(JDA_FACETVALUE_BY_FACETID_WITHOUT_JDACLASS_QUERY);
		params = new Object[]{pJdaDeptid,pJdaSubDeptid,pFacetId};
	}
	RepositoryItem[] ephFacetItems = jdaFacetValueStatement.executeQuery(view,params);
	
	
	
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchJdaFacetValueByFacetId() : End");

}
	return ephFacetItems;
}

/**
 * This method is used to create json object for jda facet value
 * @param pItem
 * @return
 * @throws JSONException
 * @throws RepositoryException
 */
public JSONObject createSingleJdaFacetValueJsonObject(RepositoryItem pItem) throws JSONException, RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createSingleJdaFacetValueJsonObject() : Start");
	}
	JSONObject facetValueJson= new JSONObject();
	RepositoryItem facetItem = (RepositoryItem)pItem.getPropertyValue(BBBAdminConstant.JDA_FACET_ID_PROPERTY_NAME);
	String facetId =  (String)(facetItem.getPropertyValue(BBBAdminConstant.ID_PROPERTY_NAME));
	facetValueJson.put("facet_id", facetId);
	facetValueJson.put("facet_value_id", pItem.getPropertyValue(BBBAdminConstant.FACET_VALUE_ID_PROPERTY_NAME));		
	facetValueJson.put("facet_value_desc", pItem.getPropertyValue(BBBAdminConstant.FACET_VALUE_DESCRIPTION_PROPERTY_NAME));
	
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createSingleJdaFacetValueJsonObject() : End");
	}
	return facetValueJson;
	
}

/**
 * This method is used to create Jda rule json object.
 * @param pItem
 * @param list_cat_id
 * @return
 * @throws JSONException
 * @throws RepositoryException
 */
public JSONObject createSingleJdaRuleJsonObject(RepositoryItem pItem, String list_cat_id) throws JSONException, RepositoryException{
	JSONObject jsonJdaRule = new JSONObject();
	RepositoryItem[] items;
	String jdaDeptId = (String) pItem.getPropertyValue(BBBAdminConstant.JDA_DEPT_ID_PROPERTY_NAME);
	String jdaSubDeptId = (String) pItem.getPropertyValue(BBBAdminConstant.JDA_SUB_DEPT_ID_PROPERTY_NAME);
	String jdaClass = (String) pItem.getPropertyValue(BBBAdminConstant.JDA_CLASS_PROPERTY_NAME);
	jsonJdaRule.put("rule_id", pItem.getRepositoryId());
	jsonJdaRule.put("list_cat_id", list_cat_id);
	jsonJdaRule.put("jda_dept_id", jdaDeptId);
	RepositoryItem jdaDept = getProductCatalog().getItem(jdaDeptId, BBBAdminConstant.JDA_DEPT_ITEM_DESCRIPTOR);
	if(jdaDept!=null){
	jsonJdaRule.put("jda_dept_descrip", jdaDept.getPropertyValue(BBBAdminConstant.JDA_DESCRIPTION_PROPERTY_NAME));
	}
	jsonJdaRule.put("jda_sub_dept_id", jdaSubDeptId);
	RepositoryItem jdaSubDept = getProductCatalog().getItem(jdaSubDeptId, BBBAdminConstant.JDA_SUB_DEPT_ITEM_DESCRIPTOR);
	if(jdaSubDept!=null){
	jsonJdaRule.put("jda_sub_dept_descrip", jdaSubDept.getPropertyValue(BBBAdminConstant.JDA_DESCRIPTION_PROPERTY_NAME));
	}
	items = fetchAllJdaSubDept(jdaDeptId);
	int jdaSubDeptCount=0;
	if(items!=null){
		jdaSubDeptCount = items.length;
	}
	jsonJdaRule.put("jda_sub_dept_count", jdaSubDeptCount);
	if(jdaClass!=null && !jdaClass.trim().equals("")){
	jsonJdaRule.put("jda_class", jdaClass);
	String tempJdaSubDeptId = jdaSubDeptId.substring((jdaSubDeptId.indexOf("_"))+1);
	RepositoryItem jdaClassItem = getProductCatalog().getItem(jdaDeptId+":"+tempJdaSubDeptId+":"+jdaClass, BBBAdminConstant.JDA_CLASS_ITEM_DESCRIPTOR);
	if(jdaClassItem!=null){
	jsonJdaRule.put("jda_class_descrip", jdaClassItem.getPropertyValue(BBBAdminConstant.JDA_DESCRIPTION_PROPERTY_NAME));
	}
}
	items = fetchAllClass(jdaDeptId, jdaSubDeptId);
	int jdaClassCount=0;
	if(items!=null){
		jdaClassCount = items.length;
	}
	jsonJdaRule.put("jda_class_count", jdaClassCount);
	jsonJdaRule.put("rule_type_cd", 1);
	jsonJdaRule.put("facet_rule_name", pItem.getPropertyValue(BBBAdminConstant.FACET_RULE_NAME_PROPERTY_NAME));
	String facetValuePair = (String)pItem.getPropertyValue(BBBAdminConstant.FACET_VALUE_LIST_PROPERTY_NAME);
	jsonJdaRule.put("facet_value_pair_list", facetValuePair);
	
	if(facetValuePair!=null && !facetValuePair.trim().equals("")){
	String facets = getJdaFacetsDescription(jdaDeptId,jdaSubDeptId,jdaClass,facetValuePair);
	jsonJdaRule.put("facets", facets);
	}
	jsonJdaRule.put("create_user", pItem.getPropertyValue(BBBAdminConstant.CREATED_USER_PROPERTY_NAME));
	jsonJdaRule.put("create_date", pItem.getPropertyValue(BBBAdminConstant.CREATION_DATE_PROPERTY_NAME));
	jsonJdaRule.put("last_mod_user", pItem.getPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME));
	jsonJdaRule.put("last_mod_date", pItem.getPropertyValue(BBBAdminConstant.LAST_MODIFIED_DATE_PROPERTY_NAME));
	
	return jsonJdaRule;
}

/**
 * This method is used to get jda facets description
 * @param pJdaDeptid
 * @param pJdaSubDeptid
 * @param pJdaClass
 * @param pFacetValuePairs
 * @return
 * @throws RepositoryException
 */
public String getJdaFacetsDescription(String pJdaDeptid, String pJdaSubDeptid, String pJdaClass, String pFacetValuePairs) throws RepositoryException{
	String facetValuePairsDesc ="";
	String []facetPairs = pFacetValuePairs.split(",");
	for(String facetPair:facetPairs){
		String []facetIdValue = facetPair.split(":");
		String facetId = facetIdValue[0];
		String facetValueId = facetIdValue[1];
		RepositoryItem facetPairItem = null;
		//if(pJdaClass!=null && !pJdaClass.trim().equals("")){
			facetPairItem = (RepositoryItem)getAdminRepository().getItem(pJdaDeptid+":"+pJdaSubDeptid+":"+facetPair, BBBAdminConstant.JDA_FACET_ITEM_DESCRIPTOR_NAME);
		//}
			/*else {
			RepositoryView view=getAdminRepository().getView(BBBAdminConstant.JDA_FACET_ITEM_DESCRIPTOR_NAME);
			RqlStatement jdaFacetValueStatement = RqlStatement.parseRqlStatement("jdaDeptId equals ?0 and jdaSubDeptId equals ?1 and jdaFacetId equals ?2 and facetValueId equals ?3");
			Object[]params = new Object[]{pJdaDeptid,pJdaSubDeptid,facetId,facetValueId};
			RepositoryItem[] jdaFacetItems = jdaFacetValueStatement.executeQuery(view,params);
			if(jdaFacetItems!=null){
				facetPairItem = jdaFacetItems[0];
			}
		}*/
		if(facetPairItem!=null){ 
		RepositoryItem facetItem = (RepositoryItem)facetPairItem.getPropertyValue(BBBAdminConstant.JDA_FACET_ID_PROPERTY_NAME);
		String facetIdDesc = (String)facetItem.getPropertyValue(BBBAdminConstant.DESCRIPTION_PROPERTY_NAME);
		String facetValueIdDesc = (String)facetPairItem.getPropertyValue(BBBAdminConstant.FACET_VALUE_DESCRIPTION_PROPERTY_NAME);
		facetValuePairsDesc =  facetValuePairsDesc+facetId+"\t"+facetIdDesc+"\r"+facetValueId+"\t"+facetValueIdDesc+"\n";
		}
	}
	
	return facetValuePairsDesc;
}


/*Facet Rules Methods*/

public RepositoryItem createFacetRule(String pCategoryId, String pFacetRuleName, String pFacetValuePair, String pCreatedUser) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createEphRule() : Start");
	}
	
	MutableRepositoryItem facetRuleItem = getAdminRepository().createItem(BBBAdminConstant.FACET_RULE_ITEM_DESCRIPTOR);
	facetRuleItem.setPropertyValue(BBBAdminConstant.FACET_RULE_NAME_PROPERTY_NAME,pFacetRuleName);
	facetRuleItem.setPropertyValue(BBBAdminConstant.FACET_VALUE_LIST_PROPERTY_NAME,pFacetValuePair);
	facetRuleItem.setPropertyValue(BBBAdminConstant.CREATED_USER_PROPERTY_NAME,pCreatedUser);
	facetRuleItem.setPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME,pCreatedUser);
	RepositoryItem ruleItem = getAdminRepository().addItem(facetRuleItem);
	
	
	addFacetRuleToCategory(pCategoryId,ruleItem);
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createEphRule() : End");
	}
	
	return ruleItem;

}
public void addFacetRuleToCategory(String pCategoryId, RepositoryItem facetRuleItem) throws RepositoryException{
	MutableRepositoryItem categoryItem = (MutableRepositoryItem) getAdminRepository().getItem(pCategoryId, BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);

	List <RepositoryItem>facetRules = (List<RepositoryItem>)categoryItem.getPropertyValue(BBBAdminConstant.CATEGORY_FACET_RULES_PROPERTY_NAME);
	if(facetRuleItem!=null && facetRules!=null){
		facetRules.add(facetRuleItem);
	}
	else if(facetRuleItem!=null){
		facetRules = new <RepositoryItem>ArrayList();
		facetRules.add(facetRuleItem);
	}
	categoryItem.setPropertyValue(BBBAdminConstant.CATEGORY_FACET_RULES_PROPERTY_NAME, facetRules);
	getAdminRepository().updateItem(categoryItem);
}



public RepositoryItem updateFacetRule(String pRuleId,String pFacetRuleName, String pFacetValuePairs, String pLastModifiedUser) throws RepositoryException{
	
	MutableRepositoryItem facetRuleItem  = getAdminRepository().getItemForUpdate(pRuleId, BBBAdminConstant.FACET_RULE_ITEM_DESCRIPTOR);
	if (pRuleId!=null){
	facetRuleItem.setPropertyValue(BBBAdminConstant.FACET_RULE_NAME_PROPERTY_NAME, pFacetRuleName);
	facetRuleItem.setPropertyValue(BBBAdminConstant.FACET_VALUE_LIST_PROPERTY_NAME, pFacetValuePairs);
	facetRuleItem.setPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME, pLastModifiedUser);
	
	getAdminRepository().updateItem(facetRuleItem);
	}
	return (RepositoryItem)facetRuleItem;
	
}
public void deleteFacetRules(String pCategoryId,String pRuleId) throws RepositoryException, SQLException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : deleteFacetRules() : Start");
	}
	removeFacetsFromCategories(pCategoryId,pRuleId);
	getAdminRepository().removeItem(pRuleId, BBBAdminConstant.FACET_RULE_ITEM_DESCRIPTOR);
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : deleteFacetRules() : End");
	}
	
}

public void  removeFacetsFromCategories(String pCategoryId,String pRuleId) throws RepositoryException ,SQLException{
	  if (isLoggingDebug()) {
			logDebug("BBBAdminManager : removeFacetsFromCategories() : Start");
		}
		if (pCategoryId!=null  && pRuleId!=null) {
			MutableRepositoryItem categoryItem = getAdminRepository().getItemForUpdate(pCategoryId,BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
			List<RepositoryItem> facetList=(List<RepositoryItem>) categoryItem.getPropertyValue(BBBAdminConstant.CATEGORY_FACET_RULES_PROPERTY_NAME);
			RepositoryItem facetItem = getAdminRepository().getItem(pRuleId, BBBAdminConstant.FACET_RULE_ITEM_DESCRIPTOR);
			if(facetList.contains(facetItem)){
				facetList.remove(facetItem);
				categoryItem.setPropertyValue(BBBAdminConstant.CATEGORY_FACET_RULES_PROPERTY_NAME, facetList);	
				getAdminRepository().updateItem(categoryItem);
			}
			
		}
		if (isLoggingDebug()) {
			logDebug("BBBAdminManager : removeFacetsFromCategories() : End");
		}
	}

public JSONObject createSingleFacetRuleJsonObject(RepositoryItem pItem, String list_cat_id) throws JSONException, RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createSingleFacetRuleJsonObject() : Start");
	}
	JSONObject jsonFacetRule = new JSONObject();
	if(pItem!=null)	{
	jsonFacetRule.put("list_cat_id", list_cat_id);	
	jsonFacetRule.put("rule_id", pItem.getRepositoryId());
	jsonFacetRule.put("facet_rule_name", pItem.getPropertyValue(BBBAdminConstant.FACET_RULE_NAME_PROPERTY_NAME));
	jsonFacetRule.put("facet_value_pair_list", pItem.getPropertyValue(BBBAdminConstant.FACET_VALUE_LIST_PROPERTY_NAME));
	String facetValuePair = (String)pItem.getPropertyValue(BBBAdminConstant.FACET_VALUE_LIST_PROPERTY_NAME);
	if(facetValuePair!=null && !facetValuePair.trim().equals("")){
	String facets = getFacetsDescription(facetValuePair);
	jsonFacetRule.put("facets", facets);
	}
	jsonFacetRule.put("create_user", pItem.getPropertyValue(BBBAdminConstant.CREATED_USER_PROPERTY_NAME));
	jsonFacetRule.put("create_date", pItem.getPropertyValue(BBBAdminConstant.CREATION_DATE_PROPERTY_NAME));
	jsonFacetRule.put("last_mod_user", pItem.getPropertyValue(BBBAdminConstant.LAST_MODIFIED_USER_PROPERTY_NAME));
	jsonFacetRule.put("last_mod_date", pItem.getPropertyValue(BBBAdminConstant.LAST_MODIFIED_DATE_PROPERTY_NAME));
	jsonFacetRule.put("rule_type_code","3" );
	
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createSingleFacetRuleJsonObject() : End");
	}
	

}
	return jsonFacetRule;

}
public void reOrderCatFacets(String pCategoryId, String pRuleId, int pNewSequenceNo) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : reOrderCatFacets() : Start");
	}

	if (pCategoryId!="" && pRuleId!="") {
		MutableRepositoryItem categoryItem = (MutableRepositoryItem) getAdminRepository()
				.getItem(pCategoryId, BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
		List<RepositoryItem> facetRules = (List<RepositoryItem>) categoryItem
				.getPropertyValue(BBBAdminConstant.CATEGORY_FACET_RULES_PROPERTY_NAME);
		RepositoryItem facetItem = getAdminRepository().getItem(pRuleId,
				BBBAdminConstant.FACET_RULE_ITEM_DESCRIPTOR);
		if (categoryItem != null && facetRules != null) {
			if (facetRules.contains(facetItem)) {
				facetRules.remove(facetItem);
				facetRules.add(pNewSequenceNo, facetItem);
			}
			categoryItem.setPropertyValue(
					BBBAdminConstant.CATEGORY_FACET_RULES_PROPERTY_NAME,
					facetRules);
			getAdminRepository().updateItem(categoryItem);
		}
	}
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : reOrderCatFacets() : End");
	}

}

public String getFacetsDescription(String pFacetValuePairs) throws RepositoryException{
	String facetValuePairsDesc ="";
	String []facetPairs = pFacetValuePairs.split(",");
	for(String facetPair:facetPairs){
		String []facetIdValue = facetPair.split(":");
		String facetId = facetIdValue[0];
		String facetValueId = facetIdValue[1];
		RepositoryItem facetPairItem = (RepositoryItem)getAdminRepository().getItem(facetPair, BBBAdminConstant.FACET_VALUE_PAIRS_ITEM_DESCRIPTOR);
		if(facetPairItem!=null){ 
		RepositoryItem facetItem = (RepositoryItem)facetPairItem.getPropertyValue(BBBAdminConstant.FACET_ID_PROPERTY_NAME);
		String facetIdDesc = facetItem.getPropertyValue(BBBAdminConstant.DESCRIPTION_PROPERTY_NAME).toString();
		String facetValueIdDesc = facetPairItem.getPropertyValue(BBBAdminConstant.FACET_VALUE_DESCRIPTION_PROPERTY_NAME).toString();
		facetValuePairsDesc =  facetValuePairsDesc+facetId+"\t"+facetIdDesc+"\r"+facetValueId+"\t"+facetValueIdDesc+"\n";
		}
	}
	
	return facetValuePairsDesc;
}

public List<RepositoryItem> fetchFacetRulesFromCategory(String pCategory) throws RepositoryException{
	  if (isLoggingDebug()) {
			logDebug("BBBAdminManager : fetchFacetRulesFromCategory() : Start");
		}
	List<RepositoryItem> facets = null;

	if (pCategory!=null) {
		
		RepositoryItem categoryItem = getAdminRepository().getItem(pCategory, BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
		facets = (List<RepositoryItem>) categoryItem.getPropertyValue(BBBAdminConstant.CATEGORY_FACET_RULES_PROPERTY_NAME);
	  }
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchFacetRulesFromCategory() : End");
	}
	return facets;
}

/* Facet and facet_value_pair methods*/

public List<RepositoryItem> fetchAllFacets() throws RepositoryException{
	return uniqueFacets;
	}


public List<RepositoryItem> fetchFacetsByFacetName(String pDescription) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchFacetsByFacetName() : Start");
	}
	
	List<RepositoryItem> facets = new<RepositoryItem> ArrayList();
	if(uniqueFacets!=null && uniqueFacets.size()>0){
		for(RepositoryItem facet : uniqueFacets){
			if(facet!=null){  
				if(facet!=null){
					String facetName = (String)facet.getPropertyValue(BBBAdminConstant.DESCRIPTION_PROPERTY_NAME);
					if(facetName!=null && facetName.toLowerCase().startsWith(pDescription.toLowerCase())&& !facets.contains(facet)){
						facets.add(facet);
					}
				}
			}
		}
	}
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchFacetsByFacetName() : End");

	}

	return facets;
}


public List<RepositoryItem> fetchFacetsByFacetId(String pFacetId) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchFacetsByFacetId() : Start");
	}
	List<RepositoryItem> facets = new<RepositoryItem> ArrayList();
	if(uniqueFacets!=null && uniqueFacets.size()>0){
		for (RepositoryItem facetRepoItem : uniqueFacets) {
			if (facetRepoItem!=null && facetRepoItem.getRepositoryId().startsWith(pFacetId) && !facets.contains(facetRepoItem)) {
				facets.add(facetRepoItem);
			}
		}
	}
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchFacetsByFacetId() : End");

	}
	return facets;
}

public List<RepositoryItem> fetchFacetValueByFacetId(String pFacetId) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchFacetValueByFacetId() : Start");
	}
	RepositoryView view=getAdminRepository().getView(BBBAdminConstant.FACET_VALUE_PAIRS_ITEM_DESCRIPTOR);
	RqlStatement rulesStatement = RqlStatement.parseRqlStatement(FACET_VALUE_BY_FACET_ID);
	Object[]params = new Object[]{pFacetId};
	RepositoryItem[] facetValueItems = rulesStatement.executeQuery(view,params);
	List<RepositoryItem> facets = new<RepositoryItem> ArrayList();
	if(facetValueItems!=null && facetValueItems.length>0){
		for (RepositoryItem facetRepoItem : facetValueItems) {
			if (!facets.contains(facetRepoItem)) {
				facets.add(facetRepoItem);
				
			}
		}
	}
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchFacetValueByFacetId() : End");

	}
	return facets;
}
public JSONObject createSingleFacetValueJsonObject(RepositoryItem pItem) throws JSONException, RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createSingleEphFacetValueJsonObject() : Start");
	}
	JSONObject facetValueJson= new JSONObject();
	facetValueJson.put("facet_id", pItem.getPropertyValue(BBBAdminConstant.FACET_ID_PROPERTY_NAME));
	facetValueJson.put("facet_value_id", pItem.getPropertyValue(BBBAdminConstant.FACET_VALUE_ID_PROPERTY_NAME));		
	facetValueJson.put("facet_value_desc", pItem.getPropertyValue(BBBAdminConstant.FACET_VALUE_DESCRIPTION_PROPERTY_NAME));
	
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createSingleEphFacetValueJsonObject() : End");
	}
	return facetValueJson;
	
}
/*search on Facet Rules*/
public RepositoryItem[] searchFacetRulesById(String pRuleId) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchFacetRulesById() : Start");
	}
	RepositoryItem[] rulesItem;
	RepositoryView view = getAdminRepository().getView(BBBAdminConstant.FACET_RULE_ITEM_DESCRIPTOR);
	RqlStatement rulesStatement = RqlStatement.parseRqlStatement(SEARCH_RULE_BY_ID_QUERY);
	Object[]params = new Object[]{pRuleId};
	rulesItem = rulesStatement.executeQuery(view,params);
	
	
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchFacetRulesById() : End");
	}
	return rulesItem;
	
}
public RepositoryItem[] searchFacetRulesByName(String pRuleName) throws RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchFacetRulesByName() : Start");
	}
	RepositoryItem[] rulesItem;
	RepositoryView view = getAdminRepository().getView(BBBAdminConstant.FACET_RULE_ITEM_DESCRIPTOR);
	RqlStatement rulesStatement = RqlStatement.parseRqlStatement(SEARCH_RULES_BY_NAME_QUERY);
	Object[]params = new Object[]{pRuleName};
	rulesItem = rulesStatement.executeQuery(view,params);
	
	
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchFacetRulesByName() : End");
	}
	return rulesItem;
	
}



/**
 * This method is used to search Sku by id.
 * @param pSkuId
 * @return
 * @throws RepositoryException
 */
public RepositoryItem[] searchSkuById(String pSkuId) throws RepositoryException{

	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchSkuById() : Start");
	}
	
	RepositoryView view = getProductCatalog().getView(BBBAdminConstant.SKU_ITEM_DESCRIPTOR_NAME);
	Object params[] = new Object[1];
	RqlStatement statement=RqlStatement.parseRqlStatement(SKU_BY_ID_SEARCH_QUERY);
	params[0] = pSkuId;
	RepositoryItem[] items=statement.executeQuery(view,params);
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchSkuById() : End");
	}
	return items;

}
public RepositoryItem[] searchSkuByDisplayName(String pDisplayName) throws RepositoryException{

	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchSkuByDisplayName() : Start");
	}
	
	RepositoryView view = getProductCatalog().getView(BBBAdminConstant.SKU_ITEM_DESCRIPTOR_NAME);
	Object params[] = new Object[1];
	RqlStatement statement=RqlStatement.parseRqlStatement(SKU_BY_NAME_SEARCH_QUERY);
	params[0] = pDisplayName;
	RepositoryItem[] items=statement.executeQuery(view,params);
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : searchSkuByDisplayName() : End");
	}
	return items;

}
public JSONObject createSingleSkuSearchJsonObject(RepositoryItem skuItem) throws JSONException, RepositoryException{
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createSingleSkuSearchJsonObject() : Start");
	}
	JSONObject skuJson= new JSONObject();
	if(skuItem!=null){
	skuJson.put("sku_id", skuItem.getRepositoryId());
	skuJson.put("display_name", skuItem.getPropertyValue("displayNameDefault"));		
	}
	
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : createSingleSkuSearchJsonObject() : End");
	}
	return skuJson;
	
}
/*public RepositoryItem[] fetchAllSkus() throws RepositoryException{

	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchAllSkus() : Start");
	}
	
	RepositoryView view = getProductCatalog().getView(BBBAdminConstant.SKU_ITEM_DESCRIPTOR_NAME);
	Object params[] = new Object[]{};
	RqlStatement statement=RqlStatement.parseRqlStatement("ALL ORDER BY id,displayNameDefault");
	RepositoryItem[] items=statement.executeQuery(view,params);
	if (isLoggingDebug()) {
		logDebug("BBBAdminManager : fetchAllSkus() : End");
	}
	return items;

}*/
}