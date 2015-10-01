CREATE TABLE ${schema}.scheme_configuration (
    id varchar(255) PRIMARY KEY NOT NULL
);

ALTER TABLE ${schema}.scheme ADD COLUMN configuration_id varchar(255);
ALTER TABLE ${schema}.scheme ADD CONSTRAINT scheme_configuration_id_fkey
    FOREIGN KEY (configuration_id) REFERENCES ${schema}.scheme_configuration(id);

CREATE TABLE ${schema}.property_configuration (
    id varchar(255) PRIMARY KEY NOT NULL,
    configuration_id varchar(255) NOT NULL REFERENCES ${schema}.scheme_configuration,
    required boolean,
    repeatable boolean,
    property_id varchar(255) NOT NULL REFERENCES ${schema}.property(id),
    regex varchar(255)
);

CREATE INDEX property_configuration_configuration_id_idx ON
    ${schema}.property_configuration(configuration_id);

CREATE TABLE ${schema}.concept_reference_type_configuration (
    id varchar(255) PRIMARY KEY NOT NULL,
    configuration_id varchar(255) NOT NULL REFERENCES ${schema}.scheme_configuration(id),
    required boolean,
    repeatable boolean,
    concept_reference_type_id varchar(255) NOT NULL REFERENCES ${schema}.concept_reference_type(id),
    primary_hierarchy boolean
);

CREATE INDEX concept_reference_type_configuration_configuration_id_idx ON
    ${schema}.concept_reference_type_configuration(configuration_id);

CREATE TABLE ${schema}.concept_reference_type_configuration_range (
    concept_reference_type_configuration_id varchar(255)
        REFERENCES ${schema}.concept_reference_type_configuration(id),
    scheme_id varchar(255) REFERENCES ${schema}.scheme(id),
    PRIMARY KEY (concept_reference_type_configuration_id, scheme_id)
);

CREATE INDEX concept_reference_type_configuration_range_crtc_id_idx ON
    ${schema}.concept_reference_type_configuration_range(concept_reference_type_configuration_id);
CREATE INDEX concept_reference_type_configuration_range_scheme_id_idx ON
    ${schema}.concept_reference_type_configuration_range(scheme_id);

