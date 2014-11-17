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
          console.log("formatResult", result);
          return result.properties ? result.properties.label.fi : result.id;
        },
        formatSelection: function(result) {
          console.log("formatSelection", result);
          return  result.properties ? result.properties.label.fi : result.id;
        }
      });

      elem.on('change', function(event) {
        scope.$apply(function() {
          if (!elem.select2('data')) {
            console.log("clear");

            scope.ngModel = "";
          } else {
            if (attrs.multiple) {
              console.log("set many");

              var data = elem.select2('data');
              var idObjects = [];
              for ( var i = 0; i < data.length; i++) {
                idObjects.push({
                  id: data[i].id
                });
              }
              scope.ngModel = idObjects;
            } else {
              console.log("set one");

              scope.ngModel = {
                id: elem.select2('data').id
              };
            }
          }
        });
      });

      scope.$watch('ngModel', function(ngModel) {
        console.log("model changed: ", ngModel);

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