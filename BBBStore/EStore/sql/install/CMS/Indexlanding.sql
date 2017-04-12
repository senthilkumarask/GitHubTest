 

CREATE INDEX BBB_LAND_SITE_temp_idx ON BBB_LANDING_SITE(landing_template_id, site_id);
CREATE INDEX BBB_LAND_CATEGORY_temp_idx ON BBB_LANDING_CATEGORY(landing_template_id);
CREATE INDEX BBB_LAND_PBOX_temp_idx ON BBB_LANDING_PROMOBOX(landing_template_id, sequence_num);
CREATE INDEX BBB_LAND_PIMAGE_B_temp_idx ON BBB_LANDING_PIMAGE_BRIDAL(landing_template_id, sequence_num);
CREATE INDEX BBB_LAND_PIMAGE_RC_temp_idx ON BBB_LANDING_PIMAGE_RCAT(landing_template_id, sequence_num);
CREATE INDEX BBB_LAND_PBOX_TMP_F_temp_idx ON BBB_LAND_PBOX_TMP_FIRST(landing_template_id, sequence_num);
CREATE INDEX BBB_LAND_PBOX_TMP_S_temp_idx ON BBB_LAND_PBOX_TMP_SECOND(landing_template_id, sequence_num);
CREATE INDEX BBB_LAND_REGISTRY_temp_idx ON BBB_LANDING_TOPREGISTRY(landing_template_id, sequence_num);
CREATE INDEX BBB_LAND_SHOP_temp_idx ON BBB_LANDING_KEEPSAKE_SHOP(landing_template_id, sequence_num);
CREATE INDEX BBB_LAND_DEALS_temp_idx ON BBB_LANDING_COLLEGE_DEALS(landing_template_id, sequence_num);
CREATE INDEX BBB_LAND_BRANDS_temp_idx ON BBB_LANDING_BRANDS(landing_template_id, sequence_num);
CREATE INDEX BBB_PBOX_TMP_PBOX_FIRST_idx ON BBB_PBOX_TMP_PROMOBOX_FIRST(promoboxtemp_id, sequence_num);
CREATE INDEX BBB_PBOX_TMP_PBOX_SECOND_idx ON BBB_PBOX_TMP_PROMOBOX_SECOND(promoboxtemp_id, sequence_num);
CREATE INDEX BBB_PBOX_TMP_PBOX_THIRD_idx ON BBB_PBOX_TMP_PROMOBOX_THIRD(promoboxtemp_id, sequence_num);

