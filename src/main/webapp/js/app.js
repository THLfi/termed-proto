'use strict';

var App = angular.module('termed', ['ngResource', 'ngRoute']);

App.factory('ConceptList', function($resource) {
  return $resource('api/concepts');
}).factory('Concept', function($resource) {
  return $resource('api/concepts/:id');
});

App.factory('Config', function() {
  return {
    langPriority: function(value) {
      if (value.lang == 'fi') return 0;
      if (value.lang == 'sv') return 1;
      if (value.lang == 'en') return 2;
      return value.lang;
    }
  }
});

App.controller('ConceptListCtrl', function($scope, $location, ConceptList) {

  $scope.query = ($location.search()).q || "";

  $scope.searchConcepts = function(query) {
    ConceptList.query({
      query: query
    }, function(concepts) {
      $scope.concepts = concepts;
      $location.search({
        q: $scope.query
      }).replace();
    });
  }

  $scope.newConcept = function() {
    ConceptList.save({
      properties: {
        label: {
          fi: "Uusi käsite"
        }
      }
    }, function(concept) {
      $location.path('/concepts/' + concept.id + '/edit');
    });
  }

  $scope.searchConcepts(($location.search()).q || "");

});

App.controller('ConceptEditCtrl', function($scope, $routeParams, $location,
        Concept, ConceptList, Config) {

  function ensureProperty(properties, property) {
    if (!properties[property]) {
      var values = []
      properties[property] = values;
      ensureValue(values);
    }
  }

  function ensureValue(values) {
    if (values.length == 0) {
      values.push({
        lang: 'fi',
        value: ''
      });
    }
  }

  Concept.get({
    id: $routeParams.id
  }, function(concept) {
    ensureProperty(concept.properties, 'prefLabel');
    ensureProperty(concept.properties, 'altLabel');
    ensureProperty(concept.properties, 'definition');
    ensureProperty(concept.properties, 'note');
    ensureProperty(concept.properties, 'example');
    ensureProperty(concept.properties, 'hiddenLabel');
    ensureProperty(concept.properties, 'deprecatedLabel');
    $scope.concept = concept;
  });

  $scope.save = function() {
    $scope.concept.$save(function(concept) {
      $location.path('/concepts/' + concept.id);
    }, function(error) {
      $scope.error = error;
    });
  }

  $scope.remove = function() {
    $scope.concept.$delete({
      id: $routeParams.id
    }, function() {
      $location.path('/');
    });
  }

});

App.controller('ConceptCtrl', function($scope, $routeParams, Concept,
        ConceptList, Config) {

  function collectParents(concept) {
    var parents = [concept];
    function recursiveCollectParents(concept) {
      if (concept.parent) {
        parents.unshift(concept.parent);
        recursiveCollectParents(concept.parent);
      }
    }
    recursiveCollectParents(concept);
    return parents;
  }

  Concept.get({
    id: $routeParams.id
  }, function(concept) {
    $scope.concept = concept;
    $scope.parents = collectParents(concept);
  });

  $scope.langPriority = Config.langPriority;

  $scope.prefLabelFi = function(value) {
    return value.properties.prefLabel.filter(function(value) {
      return value.lang == 'fi';
    }).map(function(value) {
      return value.value;
    }).join(', ');
  }

});

App.config(function($routeProvider) {
  $routeProvider.when('/concepts', {
    templateUrl: 'partials/concept-list.html',
    controller: 'ConceptListCtrl',
    reloadOnSearch: false
  }).when('/concepts/:id', {
    templateUrl: 'partials/concept.html',
    controller: 'ConceptCtrl'
  }).when('/concepts/:id/edit', {
    templateUrl: 'partials/concept-edit.html',
    controller: 'ConceptEditCtrl'
  }).otherwise({
    redirectTo: '/concepts'
  });
});
