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
}).factory('ConceptListAll', function($resource) {
  return $resource('api/concepts');
}).factory('ConceptList', function($resource) {
  return $resource('api/schemes/:schemeId/concepts');
}).factory('Concept', function($resource) {
  return $resource('api/schemes/:schemeId/concepts/:id');
}).factory('ConceptBroaderPaths', function($resource) {
  return $resource('api/schemes/:schemeId/concepts/:id/broader');
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

App.controller('SchemeListCtrl', function($scope, $location, SchemeList,
        ConceptListAll) {

  $scope.query = ($location.search()).q || "";
  $scope.max = 50;

  $scope.loadMoreResults = function() {
    $scope.max += 50;
    $scope.searchConcepts(($location.search()).q || "");
  }

  $scope.searchConcepts = function(query) {
    ConceptListAll.query({
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

  $scope.searchConcepts(($location.search()).q || "");

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
      $location.path('/schemes/' + $routeParams.schemeId + '/concepts');
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
        Scheme, Concept, ConceptList, Collection) {

  $scope.query = ($location.search()).q || "";
  $scope.max = 50;

  $scope.scheme = Scheme.get({
    schemeId: $routeParams.schemeId
  });

  $scope.loadMoreResults = function() {
    $scope.max += 50;
    $scope.searchConcepts(($location.search()).q || "");
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

  $scope.toggleTree = function() {
    if ($scope.tree) {
      $scope.tree = false;
      $scope.query = "";
    } else {
      $scope.tree = true;
      $scope.query = "*:* -broader.id:[* TO *]";
    }
    $scope.searchConcepts($scope.query);
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

  $scope.newCollection = function() {
    var collection = new Collection({
      scheme: $scope.scheme,
      properties: {
        prefLabel: [{
          lang: 'fi',
          value: 'Uusi käsitekokoelma'
        }]
      }
    });

    collection.$save({
      schemeId: $routeParams.schemeId
    }, function(collection) {
      $location.path('/schemes/' + $routeParams.schemeId + '/collections/'
              + collection.id + '/edit');
    }, function(error) {
      $scope.error = error;
    });
  }

  $scope.searchConcepts(($location.search()).q || "");

});

App.controller('ConceptCtrl', function($scope, $routeParams, $location,
        Concept, ConceptBroaderPaths, ConceptList, PropertyUtils) {

  Concept.get({
    schemeId: $routeParams.schemeId,
    id: $routeParams.id
  }, function(concept) {
    $scope.concept = concept;
    $scope.broaderPaths = ConceptBroaderPaths.query({
      schemeId: $routeParams.schemeId,
      id: $routeParams.id
    });
  });

  $scope.langPriority = PropertyUtils.langPriority;
  $scope.prefLabelFi = PropertyUtils.prefLabelFi;

  $scope.newConcept = function() {
    var concept = new Concept({
      scheme: $scope.concept.scheme,
      broader: [$scope.concept],
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

App.config(function($routeProvider, $httpProvider) {
  $routeProvider.when('/schemes/', {
    templateUrl: 'partials/scheme-list.html',
    controller: 'SchemeListCtrl',
    reloadOnSearch: false
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
  }).when('/schemes/:schemeId/concepts/:id', {
    templateUrl: 'partials/concept.html',
    controller: 'ConceptCtrl'
  }).when('/schemes/:schemeId/concepts/:id/edit', {
    templateUrl: 'partials/concept-edit.html',
    controller: 'ConceptEditCtrl'
  }).otherwise({
    redirectTo: '/schemes'
  });

  if (!$httpProvider.defaults.headers.get) {
    $httpProvider.defaults.headers.get = {};
  }
  // disable caches for IE
  $httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache';
  $httpProvider.defaults.headers.get['Pragma'] = 'no-cache';
});
