
CREATE TABLE ${schema}.concept_reference_type (
    id varchar(255) PRIMARY KEY NOT NULL
);

INSERT INTO ${schema}.concept_reference_type (id) VALUES ('type');
INSERT INTO ${schema}.concept_reference_type (id) VALUES ('broader');
INSERT INTO ${schema}.concept_reference_type (id) VALUES ('related');
INSERT INTO ${schema}.concept_reference_type (id) VALUES ('partOf');

CREATE TABLE ${schema}.concept_references (
    id varchar(255) PRIMARY KEY NOT NULL,
    type_id varchar(255) NOT NULL REFERENCES ${schema}.concept_reference_type(id),
    source_id varchar(255) NOT NULL REFERENCES ${schema}.concept(id),
    target_id varchar(255) NOT NULL REFERENCES ${schema}.concept(id),
);

CREATE INDEX concept_references_source_id_idx ON ${schema}.concept_references(source_id);
CREATE INDEX concept_references_target_id_idx ON ${schema}.concept_references(target_id);
