(function (angular) { 'use strict';

angular.module('termed.collections', ['ngRoute', 'termed.resources', 'termed.resources.properties'])

.config(function($routeProvider) {
  $routeProvider

  .when('/schemes/:schemeId/collections/:id', {
    templateUrl: 'app/collections/collection.html',
    controller: 'CollectionCtrl'
  })

  .when('/schemes/:schemeId/collections/:id/edit', {
    templateUrl: 'app/collections/collection-edit.html',
    controller: 'CollectionEditCtrl'
  });
})

.controller('CollectionCtrl', function($scope, $routeParams, Collection) {
  $scope.collection = Collection.get({
    id: $routeParams.id
  });
})

.controller('CollectionEditCtrl', function($scope, $routeParams, $location, Collection, CollectionList, PropertyList) {

  $scope.collection = Collection.get({
    id: $routeParams.id
  });

  $scope.save = function() {
    $scope.collection.$save(function(collection) {
      $location.path('/schemes/' + $routeParams.schemeId + '/collections/' + collection.id);
    }, function(error) {
      $scope.error = error;
    });
  };

  $scope.remove = function() {
    $scope.collection.$delete({
      id: $routeParams.id
    }, function() {
      $location.path('/schemes/' + $routeParams.schemeId + '/concepts');
    }, function(error) {
      $scope.error = error;
    });
  };
});

})(window.angular);
