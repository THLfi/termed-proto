App.directive('thlConceptValues', function() {
  return {
    restrict: 'E',
    scope: {
      values: '=',
      lang: '@'
    },
    templateUrl: 'partials/concept-values.html',
    controller: function($scope, Config) {
      $scope.langPriority = Config.langPriority;
    }
  };
});

App.directive('thlConceptValuesEdit', function() {
  return {
    restrict: 'E',
    scope: {
      values: '='
    },
    templateUrl: 'partials/concept-values-edit.html',
    controller: function($scope, Config) {
      $scope.addPropertyValue = function(values) {
        values.push({
          lang: 'fi',
          value: ''
        });
      }
      $scope.removePropertyValue = function(values, value) {
        values.splice(values.indexOf(value), 1);
        if (values.length == 0) {
          values.push({
            lang: 'fi',
            value: ''
          });
        }
      }
      $scope.langPriority = Config.langPriority;
    }
  };
});

App.directive('thlSelectConcept', function($q, $timeout, Concept, ConceptList) {
  return {
    scope: {
      'ngModel': "="
    },
    link: function(scope, elem, attrs) {
      elem.select2({
        allowClear: true,
        multiple: !!attrs.multiple,
        query: function(query) {
          ConceptList.query({
            query: query.term
          }, function(results) {
            query.callback({
              results: results
            });
          });
        },
        formatResult: function(result) {
          return result.properties.prefLabel.filter(function(value) {
            return value.lang == 'fi'
          }).map(function(value) {
            return value.value;
          }).join(', ');
        },
        formatSelection: function(result) {
          return result.properties.prefLabel.filter(function(value) {
            return value.lang == 'fi'
          }).map(function(value) {
            return value.value;
          }).join(', ');
        }
      });

      elem.on('change', function(event) {
        scope.$apply(function() {
          if (!elem.select2('data')) {
            scope.ngModel = "";
          } else {
            if (attrs.multiple) {
              var data = elem.select2('data');
              var idObjects = [];
              for ( var i = 0; i < data.length; i++) {
                idObjects.push({
                  id: data[i].id
                });
              }
              scope.ngModel = idObjects;
            } else {
              scope.ngModel = {
                id: elem.select2('data').id
              };
            }
          }
        });
      });

      scope.$watch('ngModel', function(ngModel) {
        if (!ngModel) {
          if (elem.select2('data')) {
            // defer clean to avoid element change inside $watch
            $timeout(function() {
              elem.select2('data', '');
            });
          }
          return;
        }

        if (attrs.multiple) {
          var promiseGet = function(idObject) {
            var d = $q.defer();
            Concept.get({
              id: idObject.id
            }, function(result) {
              d.resolve(result);
            });
            return d.promise;
          }

          var promises = [];
          for ( var i = 0; i < ngModel.length; i++) {
            promises.push(promiseGet(ngModel[i]));
          }

          // wait for all Resource.gets
          $q.all(promises).then(function(data) {
            elem.select2('data', data)
          });
        } else {
          Concept.get({
            id: ngModel.id
          }, function(resource) {
            elem.select2('data', resource);
          });
        }
      });
    }
  }
});