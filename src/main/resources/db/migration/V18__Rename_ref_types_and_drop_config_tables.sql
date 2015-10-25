-- rename ConceptReferenceType to ReferenceType

ALTER TABLE ${schema}.concept_reference_type RENAME TO reference_type;

ALTER TABLE ${schema}.concept_reference_type_properties RENAME TO reference_type_properties;


-- rename collection concept join table

ALTER TABLE ${schema}.collection_concept RENAME TO collection_members;


-- drop configuration tables (was not yet supported anyways, will be added again later)

ALTER TABLE ${schema}.scheme DROP COLUMN configuration_id;

DROP TABLE ${schema}.scheme_configuration CASCADE;
