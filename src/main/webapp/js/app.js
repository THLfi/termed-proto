'use strict';

var App = angular.module('termed', ['ngResource', 'ngRoute']);

App.factory('SchemeList', function($resource) {
  return $resource('api/schemes');
}).factory('Scheme', function($resource) {
  return $resource('api/schemes/:schemeId');
}).factory('CollectionList', function($resource) {
  return $resource('api/schemes/:schemeId/collections');
}).factory('Collection', function($resource) {
  return $resource('api/schemes/:schemeId/collections/:id');
}).factory('ConceptList', function($resource) {
  return $resource('api/schemes/:schemeId/concepts');
}).factory('Concept', function($resource) {
  return $resource('api/schemes/:schemeId/concepts/:id');
});

App.factory('PropertyUtils', function() {
  return {
    langPriority: function(value) {
      if (value.lang == 'fi') return 0;
      if (value.lang == 'sv') return 1;
      if (value.lang == 'en') return 2;
      return value.lang;
    },
    prefLabelFi: function(value) {
      return value.properties.prefLabel.filter(function(value) {
        return value.lang == 'fi';
      }).map(function(value) {
        return value.value;
      }).join(', ');
    },
    ensurePropertiesFiValue: function(properties, propertyIds) {
      for (var i = 0; i < propertyIds.length; i++) {
        if (!properties[propertyIds[i]]) {
          properties[propertyIds[i]] = [{
            lang: 'fi',
            value: ''
          }];
        }
      }
    }
  }
});

App.controller('SchemeListCtrl', function($scope, $location, SchemeList) {

  $scope.schemes = SchemeList.query();

  $scope.newScheme = function() {
    SchemeList.save({
      properties: {
        prefLabel: [{
          lang: 'fi',
          value: 'Uusi sanasto'
        }]
      }
    }, function(scheme) {
      $location.path('/schemes/' + scheme.id + '/edit');
    });
  }
});

App.controller('SchemeEditCtrl', function($scope, $routeParams, $location,
        Scheme, SchemeList, PropertyUtils) {

  Scheme.get({
    schemeId: $routeParams.schemeId
  }, function(scheme) {
    PropertyUtils.ensurePropertiesFiValue(scheme.properties, ['prefLabel',
        'altLabel']);
    $scope.scheme = scheme;
  });

  $scope.save = function() {
    $scope.scheme.$save(function(scheme) {
      $location.path('/schemes');
    }, function(error) {
      $scope.error = error;
    });
  }

  $scope.remove = function() {
    $scope.scheme.$delete({
      schemeId: $routeParams.schemeId
    }, function() {
      $location.path('/schemes');
    }, function(error) {
      $scope.error = error;
    });
  }

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
      query: query,
      orderBy: 'prefLabel.fi.sortable'
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

App.controller('ConceptTreeCtrl', function($scope, $location, $routeParams,
        Scheme, Concept, ConceptList) {

  $scope.query = ($location.search()).q || "";
  $scope.max = 50;

  $scope.scheme = Scheme.get({
    schemeId: $routeParams.schemeId
  });

  $scope.loadMoreResults = function() {
    $scope.max += 50;
    $scope.searchConcepts(($location.search()).q || "broader:null");
  }

  $scope.searchConcepts = function(query) {
    ConceptList.query({
      schemeId: $routeParams.schemeId,
      query: query,
      max: $scope.max,
      orderBy: 'prefLabel.fi.sortable'
    }, function(concepts) {
      $scope.concepts = concepts;
      $location.search({
        q: $scope.query
      }).replace();
    });
  }

  $scope.loadNarrower = function(concept) {
    ConceptList.query({
      schemeId: $routeParams.schemeId,
      query: "broader.id:" + concept.id,
      max: -1,
      orderBy: 'prefLabel.fi.sortable'
    }, function(concepts) {
      concept.narrower = concepts;
      concept.expanded = true;
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

  $scope.searchConcepts(($location.search()).q || "broader:null");

});

App.controller('ConceptCtrl', function($scope, $routeParams, Concept,
        ConceptList, PropertyUtils) {

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

  $scope.langPriority = PropertyUtils.langPriority;
  $scope.prefLabelFi = PropertyUtils.prefLabelFi;

});

App.controller('ConceptEditCtrl', function($scope, $routeParams, $location,
        Concept, ConceptList, PropertyUtils) {

  Concept.get({
    schemeId: $routeParams.schemeId,
    id: $routeParams.id
  }, function(concept) {
    PropertyUtils.ensurePropertiesFiValue(concept.properties, ['prefLabel',
        'altLabel', 'definition', 'note', 'example', 'hiddenLabel',
        'deprecatedLabel']);
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
    }, function(error) {
      $scope.error = error;
    });
  }

});

App.controller('CollectionCtrl', function($scope, $routeParams, Collection,
        ConceptList, PropertyUtils) {

  Collection.get({
    schemeId: $routeParams.schemeId,
    id: $routeParams.id
  }, function(collection) {
    $scope.collection = collection;
  });

  $scope.langPriority = PropertyUtils.langPriority;
  $scope.prefLabelFi = PropertyUtils.prefLabelFi;

});

App.controller('CollectionEditCtrl', function($scope, $routeParams, $location,
        Collection, CollectionList, PropertyUtils) {

  Collection.get({
    schemeId: $routeParams.schemeId,
    id: $routeParams.id
  }, function(collection) {
    PropertyUtils.ensurePropertiesFiValue(collection.properties, ['prefLabel',
        'altLabel']);
    $scope.collection = collection;
  });

  $scope.save = function() {
    $scope.collection.$save({
      schemeId: $routeParams.schemeId
    }, function(collection) {
      $location.path('/schemes/' + $routeParams.schemeId + '/collections/'
              + collection.id);
    }, function(error) {
      $scope.error = error;
    });
  }

  $scope.remove = function() {
    $scope.collection.$delete({
      schemeId: $routeParams.schemeId,
      id: $routeParams.id
    }, function() {
      $location.path('/schemes/' + $routeParams.schemeId + '/concepts');
    }, function(error) {
      $scope.error = error;
    });
  }
});

App.config(function($routeProvider) {
  $routeProvider.when('/schemes/', {
    templateUrl: 'partials/scheme-list.html',
    controller: 'SchemeListCtrl'
  }).when('/schemes/:schemeId/edit', {
    templateUrl: 'partials/scheme-edit.html',
    controller: 'SchemeEditCtrl'
  }).when('/schemes/:schemeId/collections/:id', {
    templateUrl: 'partials/collection.html',
    controller: 'CollectionCtrl'
  }).when('/schemes/:schemeId/collections/:id/edit', {
    templateUrl: 'partials/collection-edit.html',
    controller: 'CollectionEditCtrl'
  }).when('/schemes/:schemeId/concepts', {
    templateUrl: 'partials/concept-list.html',
    controller: 'ConceptListCtrl',
    reloadOnSearch: false
  }).when('/schemes/:schemeId/tree', {
    templateUrl: 'partials/concept-tree.html',
    controller: 'ConceptTreeCtrl',
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
