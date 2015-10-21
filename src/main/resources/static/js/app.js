'use strict';

var App = angular.module('termed', ['ngResource', 'ngRoute']);

App.factory('SchemeList', function($resource) {
  return $resource('api/crud/schemes');
}).factory('Scheme', function($resource) {
  return $resource('api/crud/schemes/:schemeId');
}).factory('CollectionList', function($resource) {
  return $resource('api/crud/collections');
}).factory('Collection', function($resource) {
  return $resource('api/crud/collections/:id');
}).factory('ConceptList', function($resource) {
  return $resource('api/crud/concepts');
}).factory('Concept', function($resource) {
  return $resource('api/crud/concepts/:id');
}).factory('ConceptTrees', function($resource) {
  return $resource('api/trees/schemes/:schemeId/:referenceTypeId');
}).factory('ConceptBroaderPaths', function($resource) {
  return $resource('api/paths/concepts/:id/broader');
}).factory('ConceptPartOfPaths', function($resource) {
  return $resource('api/paths/concepts/:id/partOf');
}).factory('PropertyList', function($resource) {
  return $resource('api/crud/properties');
}).factory('ConceptReferenceTypeList', function($resource) {
  return $resource('api/crud/conceptReferenceTypes');
});

App.controller('SchemeListCtrl', function($scope, $location, SchemeList,
        ConceptList) {

  $scope.query = ($location.search()).q || "";
  $scope.max = 50;

  $scope.loadMoreResults = function() {
    $scope.max += 50;
    $scope.searchConcepts(($location.search()).q || "");
  }

  $scope.searchConcepts = function(query) {
    ConceptList.query({
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

  $scope.schemes = SchemeList.query({
    orderBy: 'prefLabel.fi.sortable'
  });

  $scope.newScheme = function() {
    SchemeList.save({
      properties: {
        prefLabel: {
          fi: ['Uusi sanasto']
        }
      }
    }, function(scheme) {
      $location.path('/schemes/' + scheme.id + '/edit');
    });
  }

  $scope.searchConcepts(($location.search()).q || "");

});

App.controller('SchemeEditCtrl', function($scope, $routeParams, $location,
        Scheme, SchemeList) {

  $scope.scheme = Scheme.get({
    schemeId: $routeParams.schemeId
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

App.controller('ConceptListAllCtrl', function($scope, $location, $routeParams,
        Scheme, ConceptList, ConceptTrees, PropertyList) {

  $scope.scheme = Scheme.get({
    schemeId: $routeParams.schemeId
  });

  $scope.display = {
    type: "list"
  };

  $scope.loadResults = function() {
    if ($scope.display.type === "tree") {
      $scope.rootConcepts = ConceptTrees.query({
        schemeId: $routeParams.schemeId,
        referenceTypeId: 'broader',
        orderBy: 'prefLabel.fi.sortable'
      });
    } else if ($scope.display.type === "list") {
      $scope.rootConcepts = ConceptList.query({
        schemeId: $routeParams.schemeId,
        orderBy: 'prefLabel.fi.sortable',
        max: -1,
        cached: false
      });
    }
  };

  $scope.properties = PropertyList.query({
    orderBy: 'index.sortable'
  });

  $scope.loadResults();

});

App.controller('ConceptListCtrl', function($scope, $location, $routeParams,
        Scheme, Concept, ConceptList, CollectionList) {

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
    $scope.query = query;
    ConceptList.query({
      schemeId: $routeParams.schemeId,
      query: query,
      max: $scope.max,
      orderBy: 'prefLabel.fi.sortable'
    }, function(concepts) {
      $scope.concepts = concepts;
      $location.search({
        q: query
      }).replace();
    });
  }

  $scope.newConcept = function() {
    ConceptList.save({
      scheme: $scope.scheme,
      properties: {
        prefLabel: {
          fi: ['Uusi käsite']
        }
      }
    }, function(concept) {
      $location.path('/schemes/' + $routeParams.schemeId + '/concepts/'
              + concept.id + '/edit');
    }, function(error) {
      $scope.error = error;
    });
  }

  $scope.newCollection = function() {
    CollectionList.save({
      scheme: $scope.scheme,
      properties: {
        prefLabel: {
          fi: ['Uusi käsitekokoelma']
        }
      }
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
        Concept, ConceptBroaderPaths, ConceptPartOfPaths, ConceptList) {

  Concept.get({
    schemeId: $routeParams.schemeId,
    id: $routeParams.id
  }, function(concept) {
    $scope.concept = concept;
    $scope.broaderPaths = ConceptBroaderPaths.query({
      schemeId: $routeParams.schemeId,
      id: $routeParams.id
    });
    $scope.instancesPartOfPaths = [];
    if (concept.referrers.type) {
      for (var i = 0; i < concept.referrers.type.length; i++) {
        $scope.instancesPartOfPaths.push(ConceptPartOfPaths.query({
          schemeId: $routeParams.schemeId,
          id: concept.referrers.type[i].id
        }))
      }
    }
  });

  $scope.newConcept = function() {
    ConceptList.save({
      scheme: $scope.concept.scheme,
      references: {
        broader: [$scope.concept]
      },
      properties: {
        prefLabel: {
          fi: ['Uusi käsite']
        }
      }
    }, function(concept) {
      $location.path('/schemes/' + $routeParams.schemeId + '/concepts/'
              + concept.id + '/edit');
    }, function(error) {
      $scope.error = error;
    });
  }

});

App.controller('ConceptEditCtrl', function($scope, $q, $routeParams, $location,
        Concept) {

  $scope.concept = Concept.get({
    id: $routeParams.id
  });

  $scope.save = function() {
    $scope.concept.$save(function(concept) {
      $location.path('/schemes/' + $routeParams.schemeId + '/concepts/'
              + concept.id);
    }, function(error) {
      $scope.error = error;
    });
  }

  $scope.remove = function() {
    $scope.concept.$delete({
      id: $routeParams.id
    }, function() {
      $location.path('/schemes/' + $routeParams.schemeId + '/concepts');
    }, function(error) {
      $scope.error = error;
    });
  }

});

App.controller('CollectionCtrl', function($scope, $routeParams, Collection) {

  Collection.get({
    id: $routeParams.id
  }, function(collection) {
    $scope.collection = collection;
  });

});

App.controller('CollectionEditCtrl', function($scope, $routeParams, $location,
        Collection, CollectionList, PropertyList) {

  $scope.collection = Collection.get({
    id: $routeParams.id
  });

  $scope.save = function() {
    $scope.collection.$save(function(collection) {
      $location.path('/schemes/' + $routeParams.schemeId + '/collections/'
              + collection.id);
    }, function(error) {
      $scope.error = error;
    });
  }

  $scope.remove = function() {
    $scope.collection.$delete({
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
  }).when('/schemes/:schemeId/all/concepts', {
    templateUrl: 'partials/concept-list-all.html',
    controller: 'ConceptListAllCtrl'
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

App.run(function($rootScope) {
  $rootScope.languages = ['fi', 'sv', 'en'];
  $rootScope.lang = 'fi';
});
