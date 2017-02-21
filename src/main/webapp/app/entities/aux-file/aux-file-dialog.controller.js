(function() {
    'use strict';

    angular
        .module('xpacswebApp')
        .controller('AuxFileDialogController', AuxFileDialogController);

    AuxFileDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'AuxFile', 'PatientInfo'];

    function AuxFileDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, AuxFile, PatientInfo) {
        var vm = this;

        vm.auxFile = entity;
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
            if (vm.auxFile.id !== null) {
                AuxFile.update(vm.auxFile, onSaveSuccess, onSaveError);
            } else {
                AuxFile.save(vm.auxFile, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('xpacswebApp:auxFileUpdate', result);
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
