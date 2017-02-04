(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name eureka.cohorts.controller:MainCtrl
     * @description
     * This is the main controller for the cohorts section of the application.
     * @requires cohorts.CohortService
     */

    angular
        .module('eureka.cohorts')
        .controller('cohorts.MainCtrl', MainCtrl);
        
    MainCtrl.$inject = ['CohortService', 'NgTableParams'];
    
    function MainCtrl(CohortService, NgTableParams) {
        var vm = this;
        var copyData = [];
        vm.remove = remove;

        function remove(key) {
            CohortService.removeCohort(key);
            for (var i = 0; i < vm.cohortsList.length; i++) {
                if (vm.cohortsList[i].name === key) {
                    vm.cohortsList.splice(i, 1);
                    break;
                }
            }
        }

        function displayError(msg) {
            vm.errorMsg = msg;
        }

        vm.selected = [];

        vm.filter = {
            options: {
                debounce: 500
            }
        };

        vm.query = {
            filter: '',
            order: 'name',
            limit: 5,
            page: 1
        };

        function success(cohorts) {
            vm.cohortsList = cohorts;
            vm.gridOptions.data = cohorts;
            copyData = cohorts;
             // NG Table
            vm.tableParams = new NgTableParams({}, { dataset: copyData});
        }

        vm.removeFilter = function () {
            vm.filter.show = false;
            vm.query.filter = '';

            if(vm.filter.form.$dirty) {
                vm.filter.form.$setPristine();
            }
        };

        // in the future we may see a few built in alternate headers but in the mean time
        // you can implement your own search header and do something like
        vm.search = function (predicate) {
            vm.filter = predicate;
            vm.deferred = CohortService.getCohorts(vm.query).then(success, displayError);
        };

        vm.onOrderChange = function () {
            return CohortService.getCohorts(vm.query);
        };

        vm.onPaginationChange = function () {
            return CohortService.getCohorts(vm.query);
        };

        // UI-Grid
        vm.gridOptions = {
            enableSorting: true,
            columnDefs: [
                { name:'Name', field: 'name' },
                { name:'Descripton', field: 'description' },
                { name:'Type', field: 'type'},
                { name:'Created', field: 'created_at', enableCellEdit:false},
                { name: ' ',  field: 'edit',
                  cellTemplate: '<a href="${editUrl}" title="Edit">'+
                  '<span class="glyphicon glyphicon-pencil edit-icon" title="Edit"></span></a> '+
                  '<span class="glyphicon glyphicon-remove delete-icon" title="Delete"></span>'
                }
            ],
            data: []
        };
       

        CohortService.getCohorts().then(success, displayError);

     }
})();