
CREATE INDEX field_broader_id_idx ON ${schema}.concept(broader_id);

CREATE INDEX concept_properties_subject_id_idx ON ${schema}.concept_properties(subject_id);
