CREATE TABLE ${schema}.concept_reference_type (
    id varchar(255) PRIMARY KEY NOT NULL
);

INSERT INTO ${schema}.concept_reference_type (id) VALUES ('broader');
INSERT INTO ${schema}.concept_reference_type (id) VALUES ('type');
INSERT INTO ${schema}.concept_reference_type (id) VALUES ('related');
INSERT INTO ${schema}.concept_reference_type (id) VALUES ('partOf');

-- create new table to handle all concept to concept relations

CREATE TABLE ${schema}.concept_references AS (
    SELECT 'broader' AS type_id, concept_id AS source_id, broader_id AS target_id
      FROM ${schema}.concept_broader
    UNION
    SELECT 'type' AS type_id, concept_id AS source_id, type_id AS target_id
      FROM ${schema}.concept_type
    UNION
    SELECT 'related' AS type_id, concept_id AS source_id, related_id AS target_id
      FROM ${schema}.concept_related
    UNION
    SELECT 'partOf' AS type_id, concept_id AS source_id, part_of_id AS target_id
      FROM ${schema}.concept_part_of
);

ALTER TABLE ${schema}.concept_references ALTER COLUMN type_id SET NOT NULL;
ALTER TABLE ${schema}.concept_references ALTER COLUMN source_id SET NOT NULL;
ALTER TABLE ${schema}.concept_references ALTER COLUMN target_id SET NOT NULL;

ALTER TABLE ${schema}.concept_references ADD CONSTRAINT concept_references_type_id_fkey
    FOREIGN KEY (type_id) REFERENCES ${schema}.concept_reference_type(id);
ALTER TABLE ${schema}.concept_references ADD CONSTRAINT concept_references_source_id_fkey
    FOREIGN KEY (source_id) REFERENCES ${schema}.concept(id);
ALTER TABLE ${schema}.concept_references ADD CONSTRAINT concept_references_target_id_fkey
    FOREIGN KEY (target_id) REFERENCES ${schema}.concept(id);

ALTER TABLE ${schema}.concept_references ADD CONSTRAINT concept_references_pkey
    PRIMARY KEY (type_id, source_id, target_id);

CREATE INDEX concept_references_type_id_idx ON
    ${schema}.concept_references(type_id);
CREATE INDEX concept_references_source_id_idx ON
    ${schema}.concept_references(source_id);
CREATE INDEX concept_references_target_id_idx ON
    ${schema}.concept_references(target_id);

-- drop migrated tables with indices

DROP INDEX concept_broader_concept_id_idx;
DROP INDEX concept_broader_broader_id_idx;
DROP TABLE ${schema}.concept_broader;

DROP INDEX concept_type_concept_id;
DROP INDEX concept_type_type_id;
DROP TABLE ${schema}.concept_type;

DROP INDEX concept_related_concept_id;
DROP TABLE ${schema}.concept_related;

DROP INDEX concept_part_of_concept_id;
DROP INDEX concept_part_of_part_of_id;
DROP TABLE ${schema}.concept_part_of;

