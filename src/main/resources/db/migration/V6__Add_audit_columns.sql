
ALTER TABLE ${schema}.concept ADD created_by varchar(255);
ALTER TABLE ${schema}.concept ADD last_modified_by varchar(255);
ALTER TABLE ${schema}.concept ADD created_date timestamp;
ALTER TABLE ${schema}.concept ADD last_modified_date timestamp;

UPDATE ${schema}.concept SET created_date = now();
UPDATE ${schema}.concept SET last_modified_date = now();

ALTER TABLE ${schema}.collection ADD created_by varchar(255);
ALTER TABLE ${schema}.collection ADD last_modified_by varchar(255);
ALTER TABLE ${schema}.collection ADD created_date timestamp;
ALTER TABLE ${schema}.collection ADD last_modified_date timestamp;

UPDATE ${schema}.collection SET created_date = now();
UPDATE ${schema}.collection SET last_modified_date = now();

ALTER TABLE ${schema}.scheme ADD created_by varchar(255);
ALTER TABLE ${schema}.scheme ADD last_modified_by varchar(255);
ALTER TABLE ${schema}.scheme ADD created_date timestamp;
ALTER TABLE ${schema}.scheme ADD last_modified_date timestamp;

UPDATE ${schema}.scheme SET created_date = now();
UPDATE ${schema}.scheme SET last_modified_date = now();
