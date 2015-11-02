(function (angular) { 'use strict';

angular.module('termed.translations', ['pascalprecht.translate', 'ngSanitize'])

.config(function($translateProvider) {
  $translateProvider

  .preferredLanguage('fi')

  .useSanitizeValueStrategy('escapeParameters')

  .translations('fi', {
    termed: 'Termieditori',
    allConcepts: 'kaikki käsitteet',
    concepts: 'käsitteet',
    concept: 'käsite',
    source: 'lähde',
    vocab: 'sanasto',
    vocabs: 'sanastot',
    hierarchy: 'hierarkia',
    list: 'lista',
    tree: 'puu',
    example: 'esimerkki',
    description: 'kuvaus',
    searchHelp: 'hakuohje',
    parentHierarchies: 'yläkäsitehierarkiat',
    memberOfCollections: 'kuuluu kokoelmiin',
    searchConcepts: 'etsi käsitteitä',
    searchVocabs: 'etsi sanastoja',
    addNewVocab: 'lisää uusi sanasto',
    addConcept: 'lisää käsite',
    addChildConcept: 'lisää alakäsite',
    addCollection: 'lisää kokoelma',
    showMoreResults: 'näytä lisää hakutuloksia',
    displayAsList: 'esitä listana',
    displayAsTree: 'esitä puuna',
    save: 'tallenna',
    remove: 'poista',
    edit: 'muokkaa',
    download: 'lataa',
    updated: 'muokkattu',
    added: 'lisätty',
    warnSlowAllConceptsView: 'Huom. näkymän avautuminen voi sanaston koosta riippuen kestää useita minuutteja.'
  })

  .translations('en', {
    termed: 'Termed',
    allConcepts: 'all concepts',
    concepts: 'concepts',
    concept: 'concept',
    source: 'source',
    vocab: 'vocabulary',
    vocabs: 'vocabularies',
    hierarchy: 'hierarchy',
    list: 'list',
    tree: 'tree',
    example: 'example',
    description: 'description',
    searchHelp: 'search help',
    parentHierarchies: 'parent hierarchies',
    memberOfCollections: 'member of collections',
    searchConcepts: 'search concepts',
    searchVocabs: 'search vocabularies',
    addNewVocab: 'add new vocabulary',
    addConcept: 'add concept',
    addChildConcept: 'add child concept',
    addCollection: 'add collection',
    showMoreResults: 'show more results',
    displayAsList: 'display as list',
    displayAsTree: 'display as tree',
    save: 'save',
    remove: 'remove',
    edit: 'edit',
    download: 'download',
    updated: 'updated',
    added: 'added',
    warnSlowAllConceptsView: 'Note that opening all concepts view might take several minutes.'
  });

});

})(window.angular);
