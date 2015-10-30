(function (angular) { 'use strict';

angular.module('termed.filters', ['pascalprecht.translate'])

.filter('capitalize', function() {
  return function(input) {
    return input.charAt(0).toUpperCase() + input.slice(1).toLowerCase();
  };
});

})(window.angular);
