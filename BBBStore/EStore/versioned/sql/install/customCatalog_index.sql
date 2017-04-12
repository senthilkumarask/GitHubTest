CREATE INDEX BBB_CATEGORY_category_idx ON BBB_CATEGORY(category_id, asset_version);

CREATE INDEX BBB_PRD_RP_RELN_product_idx ON BBB_PRD_ROLLUP_RELN(product_id, rollup_order, asset_version);

CREATE INDEX BBB_PRODUCT_product_idx ON BBB_PRODUCT(product_id, asset_version);

CREATE INDEX BBB_PRD_PRD_RELN_product_idx ON BBB_PRD_PRD_RELN(product_id, asset_version, sequence_num);

CREATE INDEX BBB_PRD_CL_RP_RLN_prd_rln_idx ON BBB_PRD_COLL_ROLLUP_RELN(product_relan_id, rollup_order, asset_version);

CREATE INDEX BBB_PRD_RP_RELN_product_idx ON BBB_PRD_ROLLUP_RELN(product_id, rollup_order, asset_version);

CREATE INDEX BBB_PRD_MEDIA_product_idx ON BBB_PRD_MEDIA(product_id, asset_version);

CREATE INDEX BBB_PRD_CLR_PTR_prdt_idx ON BBB_PROD_COLOR_PICTURES(product_id, asset_version);

CREATE INDEX BBB_PRODUCT_TABS_prdt_idx ON BBB_PRODUCT_TABS(product_id, asset_version);

CREATE INDEX BBB_ALE_SHIP_MTD_STS_st_idx ON BBB_APLCBLE_SHIP_METHOD_STATES(state_id, asset_version, ship_method_cd);

CREATE INDEX BBB_OTR_MEDIA_STS_med_idx ON BBB_OTHER_MEDIA_SITES(media_id, asset_version, site_id);

CREATE INDEX BBB_SKU_sku_idx ON BBB_SKU(sku_id, asset_version);

CREATE INDEX BBB_SKU_OTHER_MEDIA_sku_idx ON BBB_SKU_OTHER_MEDIA(sku_id, asset_version, sequence_num);

CREATE INDEX BBB_SKU_FREE_SHIPPING_sku_idx ON BBB_SKU_FREE_SHIPPING(sku_id, asset_version, ship_method_cd);

CREATE INDEX BBB_SKU_ELGBLE_SPMEDS_sku_idx ON BBB_SKU_ELIGIBLE_SHIPMETHODS(sku_id, asset_version, ship_method_cd);

CREATE INDEX BBB_SKU_MEDIA_sku_idx ON BBB_SKU_MEDIA(sku_id, asset_version);

CREATE INDEX BBB_SKU_REBATES_sku_idx ON BBB_SKU_REBATES(sku_id, asset_version, rebate_id);

CREATE INDEX BBB_SKU_NONSHIPPABLE_sku_idx ON BBB_SKU_NONSHIPPABLE(sku_id, asset_version, state_id);

CREATE INDEX BBB_SKU_ATR_STE_sku_atr_idx ON BBB_SKU_ATTRIBUTE_SITES(sku_attribute_id, asset_version, site_id);

CREATE INDEX BBB_SKU_ATTRIBUTES_sku_idx ON BBB_SKU_ATTRIBUTES(sku_id, asset_version, priority);