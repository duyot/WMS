--Bang ton kho theo ngay de phuc vu chuc nang xuat the kho

  CREATE TABLE MJR_STOCK_DAILY_REMAIN 
   (	ID NUMBER, 
	CUST_ID NUMBER, 
	GOODS_ID NUMBER, 
	GOODS_CODE VARCHAR2(50 CHAR), 
	GOODS_NAME VARCHAR2(100 CHAR), 
	GOODS_STATE VARCHAR2(1 CHAR), 
	STOCK_ID NUMBER, 
	AMOUNT NUMBER(38,4), 
	GOODS_UNIT_NAME VARCHAR2(50 CHAR), 
	REMAIN_DATE DATE
   );
 
 --Bang tong hop xuat kho theo ngay de phuc vu chuc nang xuat the kho
  CREATE TABLE MJR_STOCK_DAILY_IMPORT_EXPORT 
   (	ID NUMBER, 
	CUST_ID NUMBER, 
	GOODS_ID NUMBER, 
	GOODS_CODE VARCHAR2(50 CHAR), 
	GOODS_NAME VARCHAR2(100 CHAR), 
	GOODS_STATE VARCHAR2(1 CHAR), 
	STOCK_ID NUMBER, 
	AMOUNT NUMBER(38,4), 
	GOODS_UNIT_NAME VARCHAR2(50 CHAR), 
	TRANS_DATE DATE, 
	TYPE VARCHAR2(1 CHAR)
   );
 
 
--Han dung, ghi chu, ngay san xuat, so chung tu cua hang trong kho
alter table mjr_stock_goods add description varchar2(2000);
alter table mjr_stock_goods add produce_date date;
alter table mjr_stock_goods add expire_date date;
comment on column mjr_stock_goods.description is 'Ghi chu';
comment on column mjr_stock_goods.produce_date is 'Ngay san xuat';
comment on column mjr_stock_goods.expire_date is 'Ngay het han';



alter table mjr_stock_goods_serial add  description varchar2(2000);
alter table mjr_stock_goods_serial add  produce_date date;
alter table mjr_stock_goods_serial add  expire_date date;
comment on column mjr_stock_goods_serial.description is 'Ghi chu';
comment on column mjr_stock_goods_serial.produce_date is 'Ngay san xuat';
comment on column mjr_stock_goods_serial.expire_date is 'Ngay het han';


alter table mjr_stock_trans_detail add description varchar2(2000);
alter table mjr_stock_trans_detail add produce_date date;
alter table mjr_stock_trans_detail add expire_date date;
comment on column mjr_stock_trans_detail.description is 'Ghi chu';
comment on column mjr_stock_trans_detail.produce_date is 'Ngay san xuat';
comment on column mjr_stock_trans_detail.expire_date is 'Ngay het han';



alter table CAT_CUSTOMER add EXPORT_METHOD number(1,0) DEFAULT 0;
comment on column CAT_CUSTOMER.EXPORT_METHOD is 'Phuong thuc xuat kho: 0-Theo ngay nhap, 1-Theo ngay san xuat, 2-Theo han su dung';


alter table mjr_stock_trans add order_id number(19,0);
alter table mjr_stock_trans add order_code varchar2(2000);


REM INSERTING into APP_PARAMS
SET DEFINE OFF;
Insert into APP_PARAMS (ID,CODE,TYPE,NAME,STATUS,PAR_ORDER,ISUNICODE,TEST) values (SEQ_APP_PARAMS.nextval,'1','EXPORT_ORDER_TYPE  ','Xuất bán','1',1,0,null);
Insert into APP_PARAMS (ID,CODE,TYPE,NAME,STATUS,PAR_ORDER,ISUNICODE,TEST) values (SEQ_APP_PARAMS.nextval,'2','EXPORT_ORDER_TYPE  ','Xuất thanh lý','1',2,0,null);


  CREATE TABLE "MJR_ORDER" 
   (	"ID" NUMBER, 
	"CODE" VARCHAR2(50 CHAR), 
	"CUST_ID" NUMBER, 
	"STOCK_ID" NUMBER, 
	"TYPE" NUMBER(1,0), 
  "EXPORT_METHOD" NUMBER(1,0), 
	"STATUS" NUMBER(2,0), 
	"CREATED_DATE" DATE DEFAULT sysdate, 
	"CREATED_USER" VARCHAR2(50 CHAR), 
	"DESCRIPTION" VARCHAR2(2000 CHAR), 
	"PARTNER_ID" NUMBER(10,0), 
	"PARTNER_NAME" NVARCHAR2(2000), 
	"RECEIVE_ID" NUMBER(10,0), 
	"RECEIVE_NAME" NVARCHAR2(2000) 
   ) ;
 

   COMMENT ON COLUMN "WMS_DB"."MJR_ORDER"."TYPE" IS '1.Nhap 2.Xuat';
   COMMENT on column "WMS_DB"."MJR_ORDER"."EXPORT_METHOD" is 'Phuong thuc xuat kho: 0-Theo ngay nhap, 1-Theo ngay san xuat, 2-Theo han su dung';
   COMMENT ON COLUMN "WMS_DB"."MJR_ORDER"."STATUS" IS '1.Chua nhap/xuat 2.Da nhap/xuat';

   
   
  CREATE TABLE "WMS_DB"."MJR_ORDER_DETAIL" 
   (	"ID" NUMBER, 
	"ORDER_ID" NUMBER, 
	"GOODS_ID" NUMBER, 
	"GOODS_CODE" VARCHAR2(50 CHAR), 
	"GOODS_STATE" VARCHAR2(1 CHAR), 
	"IS_SERIAL" VARCHAR2(1 CHAR), 
	"AMOUNT" NUMBER(19,2),  
	"SERIAL" VARCHAR2(20 CHAR),
	"UNIT_NAME" NVARCHAR2(20), 
	"PARTNER_ID" NUMBER(19,0), 	
	"TOTAL_MONEY" NUMBER(19,2), 
	"VOLUME" NUMBER(38,6), 
	"WEIGHT" NUMBER(38,6), 
	"DESCRIPTION" VARCHAR2(2000 BYTE)
   ) ;
 

   COMMENT ON COLUMN "WMS_DB"."MJR_ORDER_DETAIL"."TOTAL_MONEY" IS 'Tong tien duoc lam tron cua moi ma hang hoa';
 
   COMMENT ON COLUMN "WMS_DB"."MJR_ORDER_DETAIL"."VOLUME" IS 'The tich hang hoa';
 
   COMMENT ON COLUMN "WMS_DB"."MJR_ORDER_DETAIL"."WEIGHT" IS 'Trong luong hang hoa';
 
 

---Patched at 2/10/2019---

