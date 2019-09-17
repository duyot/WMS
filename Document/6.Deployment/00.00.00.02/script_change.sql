--Bang ton kho theo ngay de phuc vu chuc nang xuat the kho

  CREATE TABLE "MJR_STOCK_DAILY_REMAIN" 
   (	"ID" NUMBER, 
	"CUST_ID" NUMBER, 
	"GOODS_ID" NUMBER, 
	"GOODS_CODE" VARCHAR2(50 CHAR), 
	"GOODS_NAME" VARCHAR2(100 CHAR), 
	"GOODS_STATE" VARCHAR2(1 CHAR), 
	"STOCK_ID" NUMBER, 
	"AMOUNT" NUMBER(38,4), 
	"GOODS_UNIT_NAME" VARCHAR2(50 CHAR), 
	"REMAIN_DATE" DATE
   );
 
 --Bang tong hop xuat kho theo ngay de phuc vu chuc nang xuat the kho
  CREATE TABLE "MJR_STOCK_DAILY_IMPORT_EXPORT" 
   (	"ID" NUMBER, 
	"CUST_ID" NUMBER, 
	"GOODS_ID" NUMBER, 
	"GOODS_CODE" VARCHAR2(50 CHAR), 
	"GOODS_NAME" VARCHAR2(100 CHAR), 
	"GOODS_STATE" VARCHAR2(1 CHAR), 
	"STOCK_ID" NUMBER, 
	"AMOUNT" NUMBER(38,4), 
	"GOODS_UNIT_NAME" VARCHAR2(50 CHAR), 
	"TRANS_DATE" DATE, 
	"TYPE" VARCHAR2(1 CHAR)
   );
 
 
--Han dung, ghi chu hang trong kho
alter table mjr_stock_goods add column description varchar2(2000);
alter table mjr_stock_goods add column expire_date date;

alter table mjr_stock_goods_serial add column description varchar2(2000);
alter table mjr_stock_goods_serial add column expire_date date;

