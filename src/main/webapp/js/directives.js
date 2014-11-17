
App.directive('thlSelectType', function($timeout, Type, TypeList, TypeEditor) {
  return {
    scope: {
      'ngModel': "="
    },
    link: function(scope, elem, attrs) {
      elem.select2({
        query: function(query) {
          TypeList.query({
            query: query.term
          }, function(results) {
            query.callback({
              results: results
            });
          });
        },
        formatResult: function(result) {
          return result.properties.label.fi;
        },
        formatSelection: function(selection) {
          return selection.properties.label.fi;
        }
      });

      elem.on('change', function(event) {
        scope.$apply(function() {
          if (elem.select2('data')) {
            var field = scope.ngModel;
            var type = elem.select2('data');

            if (type.id !== field.type.id) {
              TypeEditor.instantiate(field, type);
            }
          }
        });
      });

      scope.$watch('ngModel.type.id', function(ngModelTypeId) {
        if (!ngModelTypeId) {
          if (elem.select2('data')) {
            // defer clean to avoid element change inside $watch
            $timeout(function() {
              elem.select2('data', '');
            });
          }
        } else {
          Type.get({
            id: ngModelTypeId,
            cached: true
          }, function(resource) {
            elem.select2('data', resource);
          });
        }
      });
    }
  }
});