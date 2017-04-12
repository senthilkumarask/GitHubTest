-- Build Version : PSI8 Release Sprint 0 to 12.1
SET ECHO ON;
SET DEFINE OFF;

UPDATE BBB_PUB.BBB_SKU_ATTRIBUTES_INFO SET ACTION_URL = '/store/static/ltlDeliveryInfo' WHERE SKU_ATTRIBUTE_ID = '12_4';
--Sprint 12.1
UPDATE BBB_PUB.DAS_SEO_TAG SET DISPLAY_NAME='Easy Return Page',TITLE='Easy Returns',DESCRIPTION='Easy Returns' WHERE CONTENT_KEY = 'easyReturns';
UPDATE BBB_PUB.DAS_SEO_TAG SET TITLE='Safety and Recalls',DESCRIPTION='We are committed to providing customers with safe, high-quality products. Visit our Safety and Recalls page for information about product testing, lead content, and importing from China. Recall Information is also posted on our site.' WHERE CONTENT_KEY = 'safetyRecalls';
UPDATE BBB_PUB.DAS_SEO_TAG SET DISPLAY_NAME='Gift Card Policy',title='Gift Card Policy',DESCRIPTION='Give the gift of shopping to a loved one with a Bed Bath & Beyond gift card- Check out bedbathandbeyond.com for everything you need to know about our gift card policy from shipping to balances to refunds- Learn more' WHERE CONTENT_KEY = 'giftCards';
UPDATE BBB_PUB.DAS_SEO_TAG SET TITLE='Shipping ',DESCRIPTION='We currently offer 3 shipping options within the United States: Standard, Expedited and Express. Visit our Shipping and Policies page for all of the details including information about costs, eligibility, tracking your package, gift packaging and more.' WHERE CONTENT_KEY = 'shippingInfo';
-- We are taking  backup of this table before removing data.So no rollback in rollback script
DELETE FROM BBB_PUB.DAS_SEO_TAG A WHERE EXISTS (SELECT * FROM BBB_PUB.BBB_PRODUCT B WHERE B.PRODUCT_ID=A.CONTENT_KEY);

COMMIT;
