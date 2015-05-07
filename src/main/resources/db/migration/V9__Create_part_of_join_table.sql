
CREATE TABLE ${schema}.concept_part_of (
    concept_id varchar(255) REFERENCES ${schema}.concept(id),
    part_of_id varchar(255) REFERENCES ${schema}.concept(id)
);

CREATE INDEX concept_part_of_concept_id ON ${schema}.concept_part_of(concept_id);
CREATE INDEX concept_part_of_part_of_id ON ${schema}.concept_part_of(part_of_id);
