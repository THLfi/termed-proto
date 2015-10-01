ALTER TABLE ${schema}.property ADD COLUMN uri varchar(255);

UPDATE ${schema}.property
   SET uri = 'http://www.w3.org/2004/02/skos/core#prefLabel'
 WHERE id = 'prefLabel';

UPDATE ${schema}.property
   SET uri = 'http://www.w3.org/2004/02/skos/core#altLabel'
 WHERE id = 'altLabel';

UPDATE ${schema}.property
   SET uri = 'http://www.w3.org/2004/02/skos/core#hiddenLabel'
 WHERE id = 'hiddenLabel';

UPDATE ${schema}.property
   SET uri = 'http://www.w3.org/2004/02/skos/core#note'
 WHERE id = 'note';

UPDATE ${schema}.property
   SET uri = 'http://www.w3.org/2004/02/skos/core#changeNote'
 WHERE id = 'changeNote';

UPDATE ${schema}.property
   SET uri = 'http://www.w3.org/2004/02/skos/core#scopeNote'
 WHERE id = 'scopeNote';

UPDATE ${schema}.property
   SET uri = 'http://www.w3.org/2004/02/skos/core#definition'
 WHERE id = 'definition';

UPDATE ${schema}.property
   SET uri = 'http://www.w3.org/2004/02/skos/core#example'
 WHERE id = 'example';

UPDATE ${schema}.property
   SET uri = 'http://purl.org/dc/elements/1.1/source'
 WHERE id = 'source';

UPDATE ${schema}.property
   SET uri = 'http://www.w3.org/2000/01/rdf-schema#comment'
 WHERE id = 'comment';


ALTER TABLE ${schema}.concept_reference_type ADD COLUMN uri varchar(255);

UPDATE ${schema}.concept_reference_type
   SET uri = 'http://www.w3.org/2004/02/skos/core#broader'
 WHERE id = 'broader';

UPDATE ${schema}.concept_reference_type
   SET uri = 'http://www.w3.org/2004/02/skos/core#related'
 WHERE id = 'related';

UPDATE ${schema}.concept_reference_type
   SET uri = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#type'
 WHERE id = 'type';

