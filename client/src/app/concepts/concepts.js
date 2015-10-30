(function (angular) { 'use strict';

angular.module('termed.concepts', ['ngRoute', 'termed.resources', 'termed.concepts.references', 'termed.resources.properties'])

.config(function($routeProvider) {
  $routeProvider

  .when('/schemes/:schemeId/concepts', {
    templateUrl: 'app/concepts/concept-list.html',
    controller: 'ConceptListCtrl',
    reloadOnSearch: false
  })

  .when('/schemes/:schemeId/concepts/:id', {
    templateUrl: 'app/concepts/concept.html',
    controller: 'ConceptCtrl'
  })

  .when('/schemes/:schemeId/concepts/:id/edit', {
    templateUrl: 'app/concepts/concept-edit.html',
    controller: 'ConceptEditCtrl'
  })

  .when('/schemes/:schemeId/all/concepts', {
    templateUrl: 'app/concepts/concept-list-all.html',
    controller: 'ConceptListAllCtrl'
  });
})

.controller('ConceptListCtrl', function($scope, $location, $routeParams, $translate, Scheme, Concept, ConceptList, CollectionList) {

  $scope.lang = $translate.use();

  $scope.query = ($location.search()).q || "";
  $scope.max = 50;

  $scope.scheme = Scheme.get({
    schemeId: $routeParams.schemeId
  });

  $scope.loadMoreResults = function() {
    $scope.max += 50;
    $scope.searchConcepts(($location.search()).q || "");
  };

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
  };

  $scope.newConcept = function() {
    ConceptList.save({
      scheme: $scope.scheme,
      properties: {
        prefLabel: {
          fi: ['Uusi käsite']
        }
      }
    }, function(concept) {
      $location.path('/schemes/' + $routeParams.schemeId + '/concepts/' + concept.id + '/edit');
    }, function(error) {
      $scope.error = error;
    });
  };

  $scope.newCollection = function() {
    CollectionList.save({
      scheme: $scope.scheme,
      properties: {
        prefLabel: {
          fi: ['Uusi käsitekokoelma']
        }
      }
    }, function(collection) {
      $location.path('/schemes/' + $routeParams.schemeId + '/collections/' + collection.id + '/edit');
    }, function(error) {
      $scope.error = error;
    });
  };

  $scope.searchConcepts(($location.search()).q || "");

})

.controller('ConceptCtrl', function($scope, $routeParams, $location, $translate, Concept, ConceptBroaderPaths, ConceptPartOfPaths, ConceptList) {

  $scope.lang = $translate.use();

  Concept.get({
    schemeId: $routeParams.schemeId,
    id: $routeParams.id
  }, function(concept) {
    $scope.concept = concept;
    $scope.broaderPaths = ConceptBroaderPaths.query({
      schemeId: $routeParams.schemeId,
      id: $routeParams.id
    });

    // TODO: refactor
    $scope.instancesPartOfPaths = [];
    if (concept.referrers.type) {
      for (var i = 0; i < concept.referrers.type.length; i++) {
        $scope.instancesPartOfPaths.push(ConceptPartOfPaths.query({
          schemeId: $routeParams.schemeId,
          id: concept.referrers.type[i].id
        }));
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
      $location.path('/schemes/' + $routeParams.schemeId + '/concepts/' + concept.id + '/edit');
    }, function(error) {
      $scope.error = error;
    });
  };

})

.controller('ConceptEditCtrl', function($scope, $routeParams, $location, $translate, Concept) {

  $scope.lang = $translate.use();

  $scope.concept = Concept.get({
    id: $routeParams.id
  });

  $scope.save = function() {
    $scope.concept.$save(function(concept) {
      $location.path('/schemes/' + $routeParams.schemeId + '/concepts/' + concept.id);
    }, function(error) {
      $scope.error = error;
    });
  };

  $scope.remove = function() {
    $scope.concept.$delete({
      id: $routeParams.id
    }, function() {
      $location.path('/schemes/' + $routeParams.schemeId + '/concepts');
    }, function(error) {
      $scope.error = error;
    });
  };

})

.controller('ConceptListAllCtrl', function($scope, $location, $routeParams, $translate, Scheme, ConceptList, ConceptTrees) {

  $scope.lang = $translate.use();

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

  $scope.loadResults();
})

.directive('thlConceptTree', function($location) {
  return {
    scope: {
      'concept': '='
    },
    link: function(scope, elem) {
      scope.$watch('concept', function(c) {
        if (c) {
          elem.jstree({
            core: {
              themes: {
                variant: "small"
              },
              data: {
                url: function(node) {
                  var id = node.id === '#' ? c.id : node.li_attr.conceptId;
                  return 'api/trees/concepts/' + id;
                },
                data: function(node) {
                  return node;
                }
              }
            },
            sort: function(a, b) {
              var aNode = this.get_node(a);
              var bNode = this.get_node(b);

              if (aNode.li_attr.index !== "" && bNode.li_attr.index !== "") {
                return aNode.li_attr.index > bNode.li_attr.index ? 1 : -1;
              } else {
                return aNode.text > bNode.text ? 1 : -1;
              }
            },
            plugins: ["sort"]
          });
        }
      });
      elem.on('activate_node.jstree', function(e, data) {
        scope.$apply(function() {
          $location.path(data.node.a_attr.href);
        });
      });
    }
  };
});

})(window.angular);
