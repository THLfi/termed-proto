
ALTER TABLE ${schema}.concept_broader_narrower ALTER COLUMN broader_id RENAME TO concept_id;
ALTER INDEX concept_broader_narrower_broader_id_idx RENAME TO concept_broader_concept_id_idx;

ALTER TABLE ${schema}.concept_broader_narrower ALTER COLUMN narrower_id RENAME TO broader_id;
ALTER INDEX concept_broader_narrower_narrower_id_idx RENAME TO concept_broader_broader_id_idx;

ALTER TABLE ${schema}.concept_broader_narrower RENAME TO ${schema}.concept_broader;
