(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('CapModelDialogController', CapModelDialogController);

    CapModelDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'CapModel', 'PatientInfo'];

    function CapModelDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, CapModel, PatientInfo) {
        var vm = this;

        vm.capModel = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.patientinfos = PatientInfo.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.capModel.id !== null) {
                CapModel.update(vm.capModel, onSaveSuccess, onSaveError);
            } else {
                CapModel.save(vm.capModel, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('xpacswebApp:capModelUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.creation_date = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
