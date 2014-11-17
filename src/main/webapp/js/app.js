'use strict';

var App = angular.module('termed', ['ngResource', 'ngRoute']);

App.factory('ConceptList', function($resource) {
  return $resource('api/concepts');
}).factory('Concept', function($resource) {
  return $resource('api/concepts/:id');
});

App.controller('ConceptListCtrl', function($scope, $location, ConceptList) {

  $scope.concepts = ConceptList.query();

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
});

App.controller('ConceptEditCtrl', function($scope, $routeParams, $location,
        Concept, ConceptList) {

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

});

App.controller('ConceptCtrl', function($scope, $routeParams, Concept,
        ConceptList) {

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
