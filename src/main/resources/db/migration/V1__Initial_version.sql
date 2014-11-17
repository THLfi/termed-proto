
CREATE TABLE termed.concept_scheme (
    id varchar(255) PRIMARY KEY NOT NULL
);

CREATE TABLE termed.concept (
    id varchar(255) PRIMARY KEY NOT NULL,
    scheme_id varchar(255) REFERENCES termed.concept_scheme(id),
    broader_id varchar(255) REFERENCES termed.concept(id)
);

CREATE TABLE termed.concept_related (
    concept_id varchar(255) REFERENCES termed.concept(id),
    related_id varchar(255) REFERENCES termed.concept(id)
);

CREATE TABLE termed.collection (
    id varchar(255) PRIMARY KEY NOT NULL
);

CREATE TABLE termed.collection_member (
    collection_id varchar(255) REFERENCES termed.collection(id),
    concept_id varchar(255) REFERENCES termed.concept(id)
);

CREATE TABLE termed.property (
    id varchar(255) PRIMARY KEY NOT NULL
);

CREATE TABLE termed.lang (
    id varchar(2) PRIMARY KEY NOT NULL
);

CREATE TABLE termed.concept_property (
    concept_id varchar(255) NOT NULL REFERENCES termed.concept(id),
    property_id varchar(255) NOT NULL REFERENCES termed.property(id),
    lang varchar(2) NOT NULL REFERENCES termed.lang(id),
    value varchar(10000) NOT NULL
);
