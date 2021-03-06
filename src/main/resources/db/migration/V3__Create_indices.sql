
CREATE INDEX concept_broader_narrower_broader_id_idx ON ${schema}.concept_broader_narrower(broader_id);

CREATE INDEX concept_scheme_id_idx ON ${schema}.concept(scheme_id);

CREATE INDEX concept_related_concept_id ON ${schema}.concept_related(concept_id);

CREATE INDEX concept_properties_subject_id_idx ON ${schema}.concept_properties(subject_id);

CREATE INDEX collection_scheme_id_idx ON ${schema}.collection(scheme_id);

CREATE INDEX collection_concept_collection_id_idx ON ${schema}.collection_concept(collection_id);

CREATE INDEX collection_properties_subject_id_idx ON ${schema}.collection_properties(subject_id);

CREATE INDEX scheme_properties_subject_id_idx ON ${schema}.scheme_properties(subject_id);

