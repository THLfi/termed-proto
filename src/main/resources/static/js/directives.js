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
                  return 'api/trees/concepts/' + id;
                },
                data: function(node) {
                  return node;
                }
              }
            },
            sort: function(a, b) {
              var a = this.get_node(a);
              var b = this.get_node(b);

              if (a.li_attr.index !== "" && b.li_attr.index !== "") {
                return a.li_attr.index > b.li_attr.index ? 1 : -1;
              } else
                return a.text > b.text ? 1 : -1;
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

App.directive('thlConceptReferences', function($rootScope,
        ReferenceTypeList) {
  return {
    restrict: 'E',
    scope: {
      concept: '='
    },
    templateUrl: 'partials/concept-references.html',
    controller: function($scope) {
      $scope.lang = $rootScope.lang;

      $scope.refTypes = ReferenceTypeList.query({
        orderBy: 'index.sortable'
      });
    }
  };
});

App.directive('thlResourceProperties', function($rootScope, PropertyList) {
  return {
    restrict: 'E',
    scope: {
      resourceProperties: '='
    },
    templateUrl: 'partials/resource-properties.html',
    controller: function($scope) {
      $scope.languages = $rootScope.languages;
      $scope.lang = $rootScope.lang;

      $scope.properties = PropertyList.query({
        orderBy: 'index.sortable'
      });
    }
  };
});

App.directive('thlResourcePropertiesEdit', function($rootScope, PropertyList) {
  return {
    restrict: 'E',
    scope: {
      resourceProperties: '='
    },
    templateUrl: 'partials/resource-properties-edit.html',
    controller: function($scope) {
      $scope.languages = $rootScope.languages;
      $scope.lang = $rootScope.lang;

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
        if (!resourceProperties) { return; }

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