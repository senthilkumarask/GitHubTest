package com.bbb.search.endeca.indexing.accessor;

import atg.commerce.endeca.index.dimension.CategoryNodePropertyAccessor;
import atg.commerce.search.IndexConstants;
import atg.repository.RepositoryItem;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

public class NodeIdPropertyAccessor extends CategoryNodePropertyAccessor implements
		IndexConstants {

	@Override
	protected Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
		// TODO Auto-generated method stub
		Object value = super.getTextOrMetaPropertyValue(pContext, pItem, pPropertyName, pType);
		if (null!=value){
		String parentProducts=(String)value.toString();
		String splitParentProducts[] = parentProducts.split("\\.");
		if(splitParentProducts.length==1){
			String l1Category= splitParentProducts[0].replace("[","");
			return l1Category;
		}
		else if(splitParentProducts.length==2){
			String l1Category= splitParentProducts[1].replace("]","");
			return l1Category;
		}
		  if(splitParentProducts.length==3){
			  String l1Category= splitParentProducts[2].replace("]","");
			return l1Category;
		}
		else{
			return null;
		}
		}else{
			return null;
		}

	}
	
}
