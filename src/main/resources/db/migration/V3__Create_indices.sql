
CREATE INDEX field_parent_id_idx ON termed.concept(parent_id);

CREATE INDEX concept_properties_concept_id_idx ON termed.concept_properties(concept_id);
