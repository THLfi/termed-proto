
CREATE INDEX field_parent_id_idx ON termed.concept(parent_id);

CREATE INDEX concept_property_concept_id_idx ON termed.concept_property(concept_id);
