'use strict';

App.controller('LoginCtrl', function($scope, $rootScope, $http, $location,
        authService) {

  function getUserInfo() {
    $http({
      method: 'GET',
      url: 'api/user/info'
    }).success(function(user) {
      $rootScope.user = user;
    });
  }

  $scope.showLogin = function() {
    $('#loginModal').modal('show');
  }

  $scope.login = function() {
    $http({
      method: 'POST',
      url: 'api/user/authenticate',
      data: $.param({
        username: $scope.username,
        password: $scope.password
      }),
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      ignoreAuthModule: true
    }).success(function() {
      authService.loginConfirmed();
      getUserInfo();
    });
  };

  $scope.logout = function() {
    $http({
      method: 'POST',
      url: 'api/user/logout'
    }).success(function(user) {
      getUserInfo();
    });
  }

  getUserInfo();

});

App.directive('showWhenLoginRequired', function($timeout, authService) {
  return {
    link: function(scope, elem, attrs) {
      scope.$on('event:auth-loginRequired', function() {
        $('#loginModal').modal('show');
      });
      scope.$on('event:auth-loginConfirmed', function() {
        $('#loginModal').modal('hide');
      });
    }
  }
});
