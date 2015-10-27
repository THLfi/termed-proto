'use strict';

angular.module('termed.resources.properties', ['pascalprecht.translate', 'termed.resources'])

.directive('thlResourceProperties', function($translate, PropertyList) {
  return {
    restrict: 'E',
    scope: {
      resourceProperties: '='
    },
    templateUrl: 'app/properties/properties.html',
    controller: function($scope) {
      $scope.lang = $translate.use();

      $scope.properties = PropertyList.query({
        orderBy: 'index.sortable'
      });
    }
  };
})

.directive('thlResourcePropertiesEdit', function($translate, PropertyList) {
  return {
    restrict: 'E',
    scope: {
      resourceProperties: '='
    },
    templateUrl: 'app/properties/properties-edit.html',
    controller: function($scope) {
      $scope.languages = ['fi', 'sv', 'en'];
      $scope.lang = $translate.use();

      $scope.properties = PropertyList.query({
        orderBy: 'index.sortable'
      }, function() {
        ensureProperties();
      });

      $scope.$watch('resourceProperties', function() {
        ensureProperties();
      }, true);

      function ensureProperties() {
        $scope.properties.forEach(function(prop) {
          $scope.languages.forEach(function(lang) {
            ensureProperty($scope.resourceProperties, prop.id, lang);
          });
        });
      }

      function ensureProperty(resourceProperties, propertyId, lang) {
        // not yet loaded
        if (!resourceProperties) {
          return;
        }

        if (!resourceProperties[propertyId]) {
          resourceProperties[propertyId] = {};
        }
        if (!resourceProperties[propertyId][lang]) {
          resourceProperties[propertyId][lang] = [""];
        }

        var values = resourceProperties[propertyId][lang];

        // remove all empty values (not including the last one)
        for (var i = 0; i < values.length - 1; i++) {
          if (values[i] == "") {
            values.splice(i, 1);
            i--;
          }
        }

        // ensure that last value is empty
        if (values.length == 0 || values[values.length - 1] != "") {
          values.push("");
        }
      }
    }
  };
})

.filter('localizeValue', function($translate) {
  return function(propertyValues) {
    if (!propertyValues) {
      return;
    }

    function hasValue(lang) {
      return propertyValues[lang] && propertyValues[lang].length > 0;
    }

    function getValue(lang) {
      return propertyValues[lang].filter(function(input) {
        return !!input
      }).join(', ');
    }

    var lang = $translate.use();

    if (hasValue(lang)) {
      return getValue(lang);
    }

    for ( var lang in propertyValues) {
      if (hasValue(lang)) {
        return getValue(lang);
      }
    }

    return '-';
  };
});
