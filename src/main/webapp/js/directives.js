App.directive('thlConceptTree', function($location) {
  return {
    scope: {
      'concept': '='
    },
    link: function(scope, elem, attrs) {
      scope.$watch('concept', function(c) {
        if (c) {
          elem.jstree({
            core: {
              themes: {
                variant: "small"
              },
              data: {
                url: function(node) {
                  var id = node.id == '#' ? c.id : node.li_attr.conceptId
                  return 'api/concepts/' + id + '/trees';
                },
                data: function(node) {
                  return node;
                }
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

App.directive('thlPropertyValues', function() {
  return {
    restrict: 'E',
    scope: {
      values: '=',
      lang: '@'
    },
    templateUrl: 'partials/property-values.html',
    controller: function($scope, PropertyUtils) {
      $scope.langPriority = PropertyUtils.langPriority;
    }
  };
});

App.directive('thlPropertyValuesEdit', function() {
  return {
    restrict: 'E',
    scope: {
      values: '=',
      textArea: '='
    },
    templateUrl: 'partials/property-values-edit.html',
    controller: function($scope, PropertyUtils) {
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
      $scope.langPriority = PropertyUtils.langPriority;
    }
  };
});

App.directive('thlSelectConcept', function($q, $timeout, Concept, ConceptList) {
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