--
-- Languages
--

INSERT INTO ${schema}.lang (id) VALUES ('fi');
INSERT INTO ${schema}.lang (id) VALUES ('sv');
INSERT INTO ${schema}.lang (id) VALUES ('en');
INSERT INTO ${schema}.lang (id) VALUES ('');


--
-- Properties
--

INSERT INTO ${schema}.property (id, uri) VALUES ('prefLabel','http://www.w3.org/2004/02/skos/core#prefLabel');
INSERT INTO ${schema}.property (id, uri) VALUES ('altLabel','http://www.w3.org/2004/02/skos/core#altLabel');
INSERT INTO ${schema}.property (id, uri) VALUES ('hiddenLabel','http://www.w3.org/2004/02/skos/core#hiddenLabel');
INSERT INTO ${schema}.property (id, uri) VALUES ('definition','http://www.w3.org/2004/02/skos/core#definition');
INSERT INTO ${schema}.property (id, uri) VALUES ('note','http://www.w3.org/2004/02/skos/core#note');
INSERT INTO ${schema}.property (id, uri) VALUES ('changeNote','http://www.w3.org/2004/02/skos/core#changeNote');
INSERT INTO ${schema}.property (id, uri) VALUES ('scopeNote','http://www.w3.org/2004/02/skos/core#scopeNote');
INSERT INTO ${schema}.property (id, uri) VALUES ('example','http://www.w3.org/2004/02/skos/core#example');
INSERT INTO ${schema}.property (id, uri) VALUES ('comment','http://www.w3.org/2000/01/rdf-schema#comment');
INSERT INTO ${schema}.property (id, uri) VALUES ('source','http://purl.org/dc/elements/1.1/source');
INSERT INTO ${schema}.property (id) VALUES ('antonym');
INSERT INTO ${schema}.property (id) VALUES ('index');
INSERT INTO ${schema}.property (id) VALUES ('deprecatedLabel');
INSERT INTO ${schema}.property (id) VALUES ('required');
INSERT INTO ${schema}.property (id) VALUES ('repeatable');
INSERT INTO ${schema}.property (id) VALUES ('classification');

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

INSERT INTO ${schema}.property_properties VALUES ('prefLabel', 'index', '', '00');
INSERT INTO ${schema}.property_properties VALUES ('altLabel', 'index', '', '01');
INSERT INTO ${schema}.property_properties VALUES ('hiddenLabel', 'index', '', '02');
INSERT INTO ${schema}.property_properties VALUES ('deprecatedLabel', 'index', '', '03');
INSERT INTO ${schema}.property_properties VALUES ('comment', 'index', '', '04');
INSERT INTO ${schema}.property_properties VALUES ('definition', 'index', '', '05');
INSERT INTO ${schema}.property_properties VALUES ('example', 'index', '', '06');
INSERT INTO ${schema}.property_properties VALUES ('note', 'index', '', '07');
INSERT INTO ${schema}.property_properties VALUES ('scopeNote', 'index', '', '08');
INSERT INTO ${schema}.property_properties VALUES ('changeNote', 'index', '', '09');
INSERT INTO ${schema}.property_properties VALUES ('antonym', 'index', '', '10');
INSERT INTO ${schema}.property_properties VALUES ('classification', 'index', '', '11');
INSERT INTO ${schema}.property_properties VALUES ('repeatable', 'index', '', '12');
INSERT INTO ${schema}.property_properties VALUES ('required', 'index', '', '13');
INSERT INTO ${schema}.property_properties VALUES ('source', 'index', '', '14');
INSERT INTO ${schema}.property_properties VALUES ('index', 'index', '', '15');


--
-- Reference types
--

INSERT INTO ${schema}.reference_type (id, uri) VALUES ('broader', 'http://www.w3.org/2004/02/skos/core#broader');
INSERT INTO ${schema}.reference_type (id, uri) VALUES ('related', 'http://www.w3.org/2004/02/skos/core#related');
INSERT INTO ${schema}.reference_type (id, uri) VALUES ('type', 'http://www.w3.org/1999/02/22-rdf-syntax-ns#type');
INSERT INTO ${schema}.reference_type (id) VALUES ('partOf');

INSERT INTO ${schema}.reference_type_properties VALUES ('type', 'prefLabel', 'en', 'type');
INSERT INTO ${schema}.reference_type_properties VALUES ('type', 'prefLabel', 'fi', 'tyyppi');
INSERT INTO ${schema}.reference_type_properties VALUES ('type', 'antonym', 'en', 'instance');
INSERT INTO ${schema}.reference_type_properties VALUES ('type', 'antonym', 'fi', 'ilmentymä');
INSERT INTO ${schema}.reference_type_properties VALUES ('broader', 'prefLabel', 'en', 'broader concept');
INSERT INTO ${schema}.reference_type_properties VALUES ('broader', 'prefLabel', 'fi', 'yläkäsite');
INSERT INTO ${schema}.reference_type_properties VALUES ('broader', 'antonym', 'en', 'narrower concept');
INSERT INTO ${schema}.reference_type_properties VALUES ('broader', 'antonym', 'fi', 'alakäsite');
INSERT INTO ${schema}.reference_type_properties VALUES ('partOf', 'prefLabel', 'en', 'part of');
INSERT INTO ${schema}.reference_type_properties VALUES ('partOf', 'prefLabel', 'fi', 'osa kokonaisuutta');
INSERT INTO ${schema}.reference_type_properties VALUES ('partOf', 'antonym', 'en', 'has part');
INSERT INTO ${schema}.reference_type_properties VALUES ('partOf', 'antonym', 'fi', 'kokonaisuden osa');
INSERT INTO ${schema}.reference_type_properties VALUES ('related', 'prefLabel', 'en', 'related concepts');
INSERT INTO ${schema}.reference_type_properties VALUES ('related', 'prefLabel', 'fi', 'assosiatiivinen käsite');
