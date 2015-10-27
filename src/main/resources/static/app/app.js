'use strict';

angular.module('termed', ['ngRoute', 'pascalprecht.translate', 'termed.filters', 'termed.translations', 'termed.schemes', 'termed.concepts', 'termed.collections'])

.config(function($routeProvider) {
  $routeProvider.otherwise({
    redirectTo: '/schemes'
  });
})

.config(function($httpProvider) {
  if (!$httpProvider.defaults.headers.get) {
    $httpProvider.defaults.headers.get = {};
  }

  // disable caches for IE
  $httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache';
  $httpProvider.defaults.headers.get['Pragma'] = 'no-cache';
})

.controller('HeaderCtrl', function($scope, $translate) {
  $scope.changeLang = function(langKey) {
    $translate.use(langKey);
  };
})
