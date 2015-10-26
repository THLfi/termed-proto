--
-- Languages (e.g. "en" or "fi")
--

CREATE TABLE ${schema}.lang (
    id varchar(2) PRIMARY KEY
);


--
-- Properties (e.g. "prefLabel" or "description")
--

CREATE TABLE ${schema}.property (
    id varchar(255) PRIMARY KEY,
    uri varchar(255) UNIQUE
);

CREATE TABLE ${schema}.property_properties (
    subject_id varchar(255) REFERENCES ${schema}.property(id),
    property_id varchar(255) REFERENCES ${schema}.property(id),
    lang varchar(2) REFERENCES ${schema}.lang(id),
    value ${property_value_data_type},
    CONSTRAINT property_properties_pkey PRIMARY KEY(subject_id, property_id, lang, value)
);

CREATE INDEX property_properties_subject_id_idx ON ${schema}.property_properties(subject_id);


--
-- Reference types (e.g. "broader" or "related")
--

CREATE TABLE ${schema}.reference_type (
    id varchar(255) PRIMARY KEY,
    uri varchar(255) UNIQUE
);

CREATE TABLE ${schema}.reference_type_properties (
    subject_id varchar(255) REFERENCES ${schema}.reference_type(id),
    property_id varchar(255) REFERENCES ${schema}.property(id),
    lang varchar(2) REFERENCES ${schema}.lang(id),
    value ${property_value_data_type},
    CONSTRAINT reference_properties_pkey PRIMARY KEY(subject_id, property_id, lang, value)
);

CREATE INDEX reference_type_properties_subject_id_idx ON ${schema}.reference_type_properties(subject_id);


--
-- Schemes (e.g. vocabularies or ontologies like "animals" or "medical terms")
--

CREATE TABLE ${schema}.scheme (
    id varchar(255) PRIMARY KEY,
    uri varchar(255) UNIQUE,
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp
);

CREATE TABLE ${schema}.scheme_properties (
    subject_id varchar(255) REFERENCES ${schema}.scheme(id),
    property_id varchar(255) REFERENCES ${schema}.property(id),
    lang varchar(2) REFERENCES ${schema}.lang(id),
    value ${property_value_data_type},
    CONSTRAINT scheme_properties_pkey PRIMARY KEY(subject_id, property_id, lang, value)
);

CREATE INDEX scheme_properties_subject_id_idx ON ${schema}.scheme_properties(subject_id);

CREATE TABLE ${schema}.property_configuration (
    id varchar(255) PRIMARY KEY,
    scheme_id varchar(255) NOT NULL REFERENCES ${schema}.scheme(id),
    property_id varchar(255) NOT NULL REFERENCES ${schema}.property(id),
    regex varchar(255),
    localized boolean,
    required boolean,
    repeatable boolean
);

CREATE TABLE ${schema}.reference_type_configuration (
    id varchar(255) PRIMARY KEY,
    scheme_id varchar(255) NOT NULL REFERENCES ${schema}.scheme(id),
    reference_type_id varchar(255) NOT NULL REFERENCES ${schema}.reference_type(id),
    primary_hierarchy boolean,
    required boolean,
    repeatable boolean
);

CREATE TABLE ${schema}.reference_type_configuration_range (
    reference_type_configuration_id varchar(255) REFERENCES ${schema}.reference_type_configuration(id),
    scheme_id varchar(255) REFERENCES ${schema}.scheme(id),
    PRIMARY KEY (reference_type_configuration_id, scheme_id)
);


--
-- Scheme concepts (e.g. "cat" or "caffeine")
--

CREATE TABLE ${schema}.concept (
    id varchar(255) PRIMARY KEY,
    uri varchar(255),
    scheme_id varchar(255) NOT NULL REFERENCES ${schema}.scheme(id),
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp,
    CONSTRAINT concept_scheme_id_uri_unique UNIQUE(scheme_id, uri)
);

CREATE TABLE ${schema}.concept_properties (
    subject_id varchar(255) REFERENCES ${schema}.concept(id),
    property_id varchar(255) REFERENCES ${schema}.property(id),
    lang varchar(2) REFERENCES ${schema}.lang(id),
    value ${property_value_data_type},
    CONSTRAINT concept_properties_pkey PRIMARY KEY(subject_id, property_id, lang, value)
);

CREATE INDEX concept_properties_subject_id_idx ON ${schema}.concept_properties(subject_id);

CREATE TABLE ${schema}.concept_references (
    source_id varchar(255) REFERENCES ${schema}.concept(id),
    target_id varchar(255) REFERENCES ${schema}.concept(id),
    type_id varchar(255) REFERENCES ${schema}.reference_type(id),
    CONSTRAINT concept_references_pkey PRIMARY KEY(source_id, target_id, type_id)
);

CREATE INDEX concept_references_source_id_idx ON ${schema}.concept_references(source_id);
CREATE INDEX concept_references_target_id_idx ON ${schema}.concept_references(target_id);


--
-- Scheme concept collections (e.g. "deprecated")
--

CREATE TABLE ${schema}.collection (
    id varchar(255) PRIMARY KEY,
    uri varchar(255),
    scheme_id varchar(255) NOT NULL REFERENCES ${schema}.scheme(id),
    created_by varchar(255),
    created_date timestamp,
    last_modified_by varchar(255),
    last_modified_date timestamp,
    CONSTRAINT collection_scheme_id_uri_unique UNIQUE(scheme_id, uri)
);

CREATE TABLE ${schema}.collection_properties (
    subject_id varchar(255) REFERENCES ${schema}.collection(id),
    property_id varchar(255) REFERENCES ${schema}.property(id),
    lang varchar(2) REFERENCES ${schema}.lang(id),
    value ${property_value_data_type},
    CONSTRAINT collection_properties_pkey PRIMARY KEY(subject_id, property_id, lang, value)
);

CREATE INDEX collection_properties_subject_id_idx ON ${schema}.collection_properties(subject_id);

CREATE TABLE ${schema}.collection_members (
    collection_id varchar(255) REFERENCES ${schema}.collection(id),
    concept_id varchar(255) REFERENCES ${schema}.concept(id),
    CONSTRAINT collection_concept_pkey PRIMARY KEY(collection_id, concept_id)
);

CREATE INDEX collection_members_collection_id_idx ON ${schema}.collection_members(collection_id);
CREATE INDEX collection_members_concept_id_idx ON ${schema}.collection_members(concept_id);
