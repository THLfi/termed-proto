
CREATE INDEX field_broader_id_idx ON termed.concept(broader_id);

CREATE INDEX concept_property_concept_id_idx ON termed.concept_property(concept_id);
