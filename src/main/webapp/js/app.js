'use strict';

var App = angular.module('termed', ['ngResource', 'ngRoute']);

App.factory('ConceptList', function($resource) {
  return $resource('api/concepts');
}).factory('Concept', function($resource) {
  return $resource('api/concepts/:id');
});

App.factory('Config', function() {
  var langPriority = {
    fi: 0,
    sv: 1,
    en: 2
  }
  return {
    langPriority: function(value) {
      return langPriority[value.lang]
    }
  }
});

App.controller('ConceptListCtrl', function($scope, $location, ConceptList) {

  $scope.searchConcepts = function(query) {
    ConceptList.query({
      query: query
    }, function(concepts) {
      $scope.concepts = concepts;
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

  $scope.searchConcepts();
});

App.controller('ConceptEditCtrl', function($scope, $routeParams, $location,
        Concept, ConceptList, Config) {

  Concept.get({
    id: $routeParams.id
  }, function(concept) {
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

  $scope.langPriority = Config.langPriority;

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

});

App.config(function($routeProvider) {
  $routeProvider.when('/concepts', {
    templateUrl: 'partials/concept-list.html',
    controller: 'ConceptListCtrl',
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
