CREATE TABLE ${schema}.concept_broader AS
    (SELECT broader_id AS concept_id, narrower_id AS broader_id
       FROM ${schema}.concept_broader_narrower);

ALTER TABLE ${schema}.concept_broader ADD CONSTRAINT concept_broader_concept_id_fkey
    FOREIGN KEY (concept_id) REFERENCES ${schema}.concept(id);

ALTER TABLE ${schema}.concept_broader ADD CONSTRAINT concept_broader_broader_id_fkey
    FOREIGN KEY (broader_id) REFERENCES ${schema}.concept(id);

CREATE INDEX concept_broader_concept_id_idx ON ${schema}.concept_broader(concept_id);
CREATE INDEX concept_broader_broader_id_idx ON ${schema}.concept_broader(broader_id);

DROP INDEX concept_broader_narrower_broader_id_idx;
DROP INDEX concept_broader_narrower_narrower_id_idx;
DROP TABLE ${schema}.concept_broader_narrower;
