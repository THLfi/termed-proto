'use strict';

var App = angular.module('termed', ['ngResource', 'ngRoute']);

App.factory('SchemeList', function($resource) {
  return $resource('api/schemes');
}).factory('Scheme', function($resource) {
  return $resource('api/schemes/:schemeId');
}).factory('ConceptList', function($resource) {
  return $resource('api/schemes/:schemeId/concepts');
}).factory('Concept', function($resource) {
  return $resource('api/schemes/:schemeId/concepts/:id');
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

App.controller('SchemeListCtrl', function($scope, $location, SchemeList) {

  $scope.schemes = SchemeList.query();

});

App.controller('ConceptListCtrl', function($scope, $location, $routeParams,
        Scheme, Concept, ConceptList) {

  $scope.query = ($location.search()).q || "";

  $scope.scheme = Scheme.get({
    schemeId: $routeParams.schemeId
  });

  $scope.searchConcepts = function(query) {
    ConceptList.query({
      schemeId: $routeParams.schemeId,
      query: query
    }, function(concepts) {
      $scope.concepts = concepts;
      $location.search({
        q: $scope.query
      }).replace();
    });
  }

  $scope.newConcept = function() {
    var concept = new Concept({
      scheme: $scope.scheme,
      properties: {
        prefLabel: [{
          lang: 'fi',
          value: 'Uusi käsite'
        }]
      }
    });

    concept.$save({
      schemeId: $routeParams.schemeId
    }, function(concept) {
      $location.path('/schemes/' + $routeParams.schemeId + '/concepts/'
              + concept.id + '/edit');
    }, function(error) {
      $scope.error = error;
    });
  }

  $scope.searchConcepts(($location.search()).q || "");

});

App.controller('ConceptEditCtrl', function($scope, $routeParams, $location,
        Concept, ConceptList, Config) {

  function ensureProperties(properties, propertyIds) {
    for (var i = 0; i < propertyIds.length; i++) {
      if (!properties[propertyIds[i]]) {
        var values = []
        properties[propertyIds[i]] = values;
        ensureValue(values);
      }
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
    schemeId: $routeParams.schemeId,
    id: $routeParams.id
  }, function(concept) {
    ensureProperties(concept.properties, ['prefLabel', 'altLabel',
        'definition', 'note', 'example', 'hiddenLabel', 'deprecatedLabel']);
    $scope.concept = concept;
  });

  $scope.save = function() {
    $scope.concept.$save({
      schemeId: $routeParams.schemeId
    }, function(concept) {
      $location.path('/schemes/' + $routeParams.schemeId + '/concepts/'
              + concept.id);
    }, function(error) {
      $scope.error = error;
    });
  }

  $scope.remove = function() {
    $scope.concept.$delete({
      schemeId: $routeParams.schemeId,
      id: $routeParams.id
    }, function() {
      $location.path('/schemes/' + $routeParams.schemeId + '/concepts');
    });
  }

});

App.controller('ConceptCtrl', function($scope, $routeParams, Concept,
        ConceptList, Config) {

  function collectBroader(concept) {
    var broader = [concept];
    function recursiveCollectBroader(concept) {
      if (concept.broader) {
        broader.unshift(concept.broader);
        recursiveCollectBroader(concept.broader);
      }
    }
    recursiveCollectBroader(concept);
    return broader;
  }

  Concept.get({
    schemeId: $routeParams.schemeId,
    id: $routeParams.id
  }, function(concept) {
    $scope.concept = concept;
    $scope.broader = collectBroader(concept);
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
  $routeProvider.when('/schemes/', {
    templateUrl: 'partials/scheme-list.html',
    controller: 'SchemeListCtrl'
  }).when('/schemes/:schemeId/concepts', {
    templateUrl: 'partials/concept-list.html',
    controller: 'ConceptListCtrl',
    reloadOnSearch: false
  }).when('/schemes/:schemeId/concepts/:id', {
    templateUrl: 'partials/concept.html',
    controller: 'ConceptCtrl'
  }).when('/schemes/:schemeId/concepts/:id/edit', {
    templateUrl: 'partials/concept-edit.html',
    controller: 'ConceptEditCtrl'
  }).otherwise({
    redirectTo: '/schemes'
  });
});
