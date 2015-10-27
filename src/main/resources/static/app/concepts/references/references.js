angular.module('termed.concepts.references', ['pascalprecht.translate', 'termed.resources'])

.directive('thlConceptReferences', function($translate, ReferenceTypeList) {
  return {
    restrict: 'E',
    scope: {
      concept: '='
    },
    templateUrl: 'app/concepts/references/references.html',
    controller: function($scope) {
      $scope.lang = $translate.use();

      $scope.refTypes = ReferenceTypeList.query({
        orderBy: 'index.sortable'
      });
    }
  };
})

.directive('thlConceptReferencesEdit', function($translate, ReferenceTypeList) {
  return {
    restrict: 'E',
    scope: {
      concept: '='
    },
    templateUrl: 'app/concepts/references/references-edit.html',
    controller: function($scope) {
      $scope.lang = $translate.use();

      $scope.refTypes = ReferenceTypeList.query({
        orderBy: 'index.sortable'
      });
    }
  };
})

.directive('thlSelectConcept', function($q, $timeout, Concept, ConceptList) {
  return {
    scope: {
      'schemeId': '=',
      'ngModel': "="
    },
    link: function(scope, elem, attrs) {
      elem.select2({
        allowClear: true,
        multiple: !!attrs.multiple,
        query: function(query) {
          ConceptList.query({
            schemeId: scope.schemeId,
            query: query.term
          }, function(results) {
            query.callback({
              results: results
            });
          });
        },
        formatResult: function(result) {
          return result.properties.prefLabel.fi[0];
        },
        formatSelection: function(result) {
          return result.properties.prefLabel.fi[0];
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
              for (var i = 0; i < data.length; i++) {
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
              schemeId: scope.schemeId,
              id: idObject.id
            }, function(result) {
              d.resolve(result);
            });
            return d.promise;
          }

          var promises = [];
          for (var i = 0; i < ngModel.length; i++) {
            promises.push(promiseGet(ngModel[i]));
          }

          // wait for all Resource.gets
          $q.all(promises).then(function(data) {
            elem.select2('data', data)
          });
        } else {
          Concept.get({
            schemeId: scope.schemeId,
            id: ngModel.id
          }, function(resource) {
            elem.select2('data', resource);
          });
        }
      });
    }
  }
});
