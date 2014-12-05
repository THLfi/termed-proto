'use strict';

App.controller('LoginCtrl', function($scope, $http, $location, authService) {
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
    });
  };
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
