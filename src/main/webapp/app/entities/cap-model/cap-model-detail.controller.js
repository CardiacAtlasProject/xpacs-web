(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('CapModelDetailController', CapModelDetailController);

    CapModelDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'CapModel', 'PatientInfo'];

    function CapModelDetailController($scope, $rootScope, $stateParams, previousState, entity, CapModel, PatientInfo) {
        var vm = this;

        vm.capModel = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('xpacswebApp:capModelUpdate', function(event, result) {
            vm.capModel = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
