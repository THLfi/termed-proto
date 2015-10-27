'use strict';

angular.module('termed.resources', ['ngResource'])

.factory('SchemeList', function($resource) {
  return $resource('api/crud/schemes');
})

.factory('Scheme', function($resource) {
  return $resource('api/crud/schemes/:schemeId');
})

.factory('CollectionList', function($resource) {
  return $resource('api/crud/collections');
})

.factory('Collection', function($resource) {
  return $resource('api/crud/collections/:id');
})

.factory('ConceptList', function($resource) {
  return $resource('api/crud/concepts');
})

.factory('Concept', function($resource) {
  return $resource('api/crud/concepts/:id');
})

.factory('ConceptTrees', function($resource) {
  return $resource('api/trees/schemes/:schemeId/:referenceTypeId');
})

.factory('ConceptBroaderPaths', function($resource) {
  return $resource('api/paths/concepts/:id/broader');
})

.factory('ConceptPartOfPaths', function($resource) {
  return $resource('api/paths/concepts/:id/partOf');
})

.factory('PropertyList', function($resource) {
  return $resource('api/crud/properties');
})

.factory('ReferenceTypeList', function($resource) {
  return $resource('api/crud/referenceTypes');
});
