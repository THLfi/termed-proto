CREATE TABLE ${schema}.property_properties (
    subject_id varchar(255) NOT NULL REFERENCES ${schema}.property(id),
    property_id varchar(255) NOT NULL REFERENCES ${schema}.property(id),
    lang varchar(2) NOT NULL REFERENCES ${schema}.lang(id),
    value varchar(10000) NOT NULL,
    CONSTRAINT property_properties_unique UNIQUE(subject_id, property_id, lang, value)
);

INSERT INTO ${schema}.property_properties VALUES ('altLabel', 'prefLabel', 'en', 'alternative label');
INSERT INTO ${schema}.property_properties VALUES ('altLabel', 'prefLabel', 'fi', 'vaihtoehtoinen termi');
INSERT INTO ${schema}.property_properties VALUES ('antonym', 'prefLabel', 'en', 'antonym');
INSERT INTO ${schema}.property_properties VALUES ('antonym', 'prefLabel', 'fi', 'antonyymi');
INSERT INTO ${schema}.property_properties VALUES ('changeNote', 'prefLabel', 'en', 'change note');
INSERT INTO ${schema}.property_properties VALUES ('changeNote', 'prefLabel', 'fi', 'muutokset');
INSERT INTO ${schema}.property_properties VALUES ('classification', 'prefLabel', 'en', 'classification');
INSERT INTO ${schema}.property_properties VALUES ('classification', 'prefLabel', 'fi', 'luokitus');
INSERT INTO ${schema}.property_properties VALUES ('comment', 'prefLabel', 'en', 'comment');
INSERT INTO ${schema}.property_properties VALUES ('comment', 'prefLabel', 'fi', 'kommentti');
INSERT INTO ${schema}.property_properties VALUES ('definition', 'prefLabel', 'en', 'definition');
INSERT INTO ${schema}.property_properties VALUES ('definition', 'prefLabel', 'fi', 'määritelmä');
INSERT INTO ${schema}.property_properties VALUES ('deprecatedLabel', 'prefLabel', 'en', 'deprecated label');
INSERT INTO ${schema}.property_properties VALUES ('deprecatedLabel', 'prefLabel', 'fi', 'korvattu termi');
INSERT INTO ${schema}.property_properties VALUES ('example', 'prefLabel', 'en', 'example');
INSERT INTO ${schema}.property_properties VALUES ('example', 'prefLabel', 'fi', 'esimerkki');
INSERT INTO ${schema}.property_properties VALUES ('hiddenLabel', 'prefLabel', 'en', 'hidden label');
INSERT INTO ${schema}.property_properties VALUES ('hiddenLabel', 'prefLabel', 'fi', 'piilotettu termi');
INSERT INTO ${schema}.property_properties VALUES ('index', 'prefLabel', 'en', 'index');
INSERT INTO ${schema}.property_properties VALUES ('index', 'prefLabel', 'fi', 'järjestysnumero');
INSERT INTO ${schema}.property_properties VALUES ('note', 'prefLabel', 'en', 'note');
INSERT INTO ${schema}.property_properties VALUES ('note', 'prefLabel', 'fi', 'huomautus');
INSERT INTO ${schema}.property_properties VALUES ('prefLabel', 'prefLabel', 'en', 'preferred label');
INSERT INTO ${schema}.property_properties VALUES ('prefLabel', 'prefLabel', 'fi', 'ensisijainen termi');
INSERT INTO ${schema}.property_properties VALUES ('repeatable', 'prefLabel', 'en', 'repeatable');
INSERT INTO ${schema}.property_properties VALUES ('repeatable', 'prefLabel', 'fi', 'toistuva');
INSERT INTO ${schema}.property_properties VALUES ('required', 'prefLabel', 'en', 'required');
INSERT INTO ${schema}.property_properties VALUES ('required', 'prefLabel', 'fi', 'pakollinen');
INSERT INTO ${schema}.property_properties VALUES ('scopeNote', 'prefLabel', 'en', 'scope note');
INSERT INTO ${schema}.property_properties VALUES ('scopeNote', 'prefLabel', 'fi', 'käyttöala');
INSERT INTO ${schema}.property_properties VALUES ('source', 'prefLabel', 'en', 'source');
INSERT INTO ${schema}.property_properties VALUES ('source', 'prefLabel', 'fi', 'lähde');

CREATE INDEX property_properties_subject_id_idx ON ${schema}.property_properties(subject_id);

CREATE TABLE ${schema}.concept_reference_type_properties (
    subject_id varchar(255) NOT NULL REFERENCES ${schema}.concept_reference_type(id),
    property_id varchar(255) NOT NULL REFERENCES ${schema}.property(id),
    lang varchar(2) NOT NULL REFERENCES ${schema}.lang(id),
    value varchar(10000) NOT NULL,
    CONSTRAINT concept_reference_type_properties_unique UNIQUE(subject_id, property_id, lang, value)
);

INSERT INTO ${schema}.concept_reference_type_properties VALUES ('type', 'prefLabel', 'en', 'type');
INSERT INTO ${schema}.concept_reference_type_properties VALUES ('type', 'prefLabel', 'fi', 'tyyppi');
INSERT INTO ${schema}.concept_reference_type_properties VALUES ('type', 'antonym', 'en', 'instance');
INSERT INTO ${schema}.concept_reference_type_properties VALUES ('type', 'antonym', 'fi', 'ilmentymä');
INSERT INTO ${schema}.concept_reference_type_properties VALUES ('broader', 'prefLabel', 'en', 'broader concept');
INSERT INTO ${schema}.concept_reference_type_properties VALUES ('broader', 'prefLabel', 'fi', 'yläkäsite');
INSERT INTO ${schema}.concept_reference_type_properties VALUES ('broader', 'antonym', 'en', 'narrower concept');
INSERT INTO ${schema}.concept_reference_type_properties VALUES ('broader', 'antonym', 'fi', 'alakäsite');
INSERT INTO ${schema}.concept_reference_type_properties VALUES ('partOf', 'prefLabel', 'en', 'part of');
INSERT INTO ${schema}.concept_reference_type_properties VALUES ('partOf', 'prefLabel', 'fi', 'osa kokonaisuutta');
INSERT INTO ${schema}.concept_reference_type_properties VALUES ('partOf', 'antonym', 'en', 'has part');
INSERT INTO ${schema}.concept_reference_type_properties VALUES ('partOf', 'antonym', 'fi', 'kokonaisuden osa');
INSERT INTO ${schema}.concept_reference_type_properties VALUES ('related', 'prefLabel', 'en', 'related concepts');
INSERT INTO ${schema}.concept_reference_type_properties VALUES ('related', 'prefLabel', 'fi', 'assosiatiivinen käsite');

CREATE INDEX concept_reference_type_properties_subject_id_idx ON ${schema}.concept_reference_type_properties(subject_id);

