(function() {
    'use strict';
    angular
        .module('xpacswebApp')
        .factory('CapModel', CapModel);

    CapModel.$inject = ['$resource', 'DateUtils'];

    function CapModel ($resource, DateUtils) {
        var resourceUrl =  'api/cap-models/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.creation_date = DateUtils.convertLocalDateFromServer(data.creation_date);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.creation_date = DateUtils.convertLocalDateToServer(copy.creation_date);
                    return angular.toJson(copy);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    var copy = angular.copy(data);
                    copy.creation_date = DateUtils.convertLocalDateToServer(copy.creation_date);
                    return angular.toJson(copy);
                }
            }
        });
    }
})();
