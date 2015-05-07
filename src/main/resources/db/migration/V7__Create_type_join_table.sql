
CREATE TABLE ${schema}.concept_type (
    concept_id varchar(255) REFERENCES ${schema}.concept(id),
    type_id varchar(255) REFERENCES ${schema}.concept(id)
);

CREATE INDEX concept_type_concept_id ON ${schema}.concept_type(concept_id);
CREATE INDEX concept_type_type_id ON ${schema}.concept_type(type_id);
