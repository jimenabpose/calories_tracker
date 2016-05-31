'use strict';

angular.module('caloriesRecord', [])

.controller('CaloriesRecords', function($scope, $location, $window, CaloriesRecordServices) {
	$scope.caloriesRecordFilter = {};
	$scope.caloriesRecords = {};
	$scope.pagination = {
        current: 1,
        itemsPerPage: 5,
        totalItems: 0
    };
	$scope.datePickerOptions = {
		showWeeks: true
	};
	if (CaloriesRecordServices.canCrudOtherUsersItems()) {
		CaloriesRecordServices.findUsersData(function(response) {
			$scope.usersData = response.data;
		});
	} else {
		$scope.caloriesRecordFilter.user = CaloriesRecordServices.getLoggedUserId();
	}
	$scope.clearDateFrom = function() {
		$scope.caloriesRecordFilter.dateFrom = null;
		$scope.getData();
	};
	$scope.clearDateTo = function() {
		$scope.caloriesRecordFilter.dateTo = null;
		$scope.getData();
	};
	$scope.viewCaloriesRecord = function($routeParams) {
		$location.path('caloriesRecord/view/' + $routeParams.id);
	};
	$scope.editCaloriesRecord = function($routeParams) {
		$location.path('caloriesRecord/edit/' + $routeParams.id);
	};
	$scope.addCaloriesRecord = function() {
		$location.path('caloriesRecord/add/');
	};
	$scope.deleteCaloriesRecord = function($routeParams) {
		if ($window.confirm("Are you sure you want to delete the record '" + $routeParams.text + "'? (This action can't be undone)")) {
			var id = $routeParams.id;
			CaloriesRecordServices.deleteCaloriesRecord(
				id,
				function(response) {
					$scope.getData();
				},
				function(response) {
					$scope.error = response.message;
				}
			);
		}
	};
	$scope.canCrudOtherUsersItems = function() {
		return CaloriesRecordServices.canCrudOtherUsersItems();
	};
	$scope.getData = function() {
		CaloriesRecordServices.get(
			$scope.pagination.current,
			$scope.pagination.itemsPerPage,
			$scope.caloriesRecordFilter.user,
			$scope.caloriesRecordFilter.dateFrom,
			$scope.caloriesRecordFilter.dateTo,
			$scope.caloriesRecordFilter.timeFrom,
			$scope.caloriesRecordFilter.timeTo,
			function(response) {
				$scope.error = null;
				$scope.caloriesRecords = response.content;
				$scope.pagination.totalItems = response.totalElements;
			},
			function(response) {
				$scope.error = response.message;
			}
		);
	};
	$scope.findCaloriesRecords = function() {
		$scope.getData();
	};
	$scope.getData();
})

.controller('ViewCaloriesRecord', function($scope, $routeParams, $location, CaloriesRecordServices) {
	var id = $routeParams.id;
	CaloriesRecordServices.getCaloriesRecord(id,function(response) {
		$scope.caloriesRecord = response.data;
	});
	$scope.editCaloriesRecord = function($routeParams) {
		$location.path('caloriesRecord/edit/' + $routeParams.id);
	};
})

.controller('AddCaloriesRecord', function($scope, $location, CaloriesRecordServices) {
	$scope.caloriesRecord = {
		user: {},
		date: new Date(),
		time: new Date()
	};
	if (CaloriesRecordServices.canCrudOtherUsersItems()) {
		CaloriesRecordServices.findUsersData(function(response) {
			$scope.usersData = response.data;
		});
	} else {
		$scope.caloriesRecord.user.id = CaloriesRecordServices.getLoggedUserId();
	}
	$scope.addCaloriesRecord = function() {
		CaloriesRecordServices.addCaloriesRecord($scope.caloriesRecord,
			function(response) {
				$location.path('caloriesRecords');
			},
			function(response) {
				$scope.error = response.message;
			}
		);
	};
	$scope.canCrudOtherUsersItems = function() {
		return CaloriesRecordServices.canCrudOtherUsersItems();
	};
})

.controller('EditCaloriesRecord', function($scope, $routeParams, $location, CaloriesRecordServices) {
	var id = $routeParams.id;
	$scope.caloriesRecord = {
		user: {}
	};
	CaloriesRecordServices.getCaloriesRecord(id, function(response) {
		$scope.caloriesRecord = response.data;
	});
	if (CaloriesRecordServices.canCrudOtherUsersItems()) {
		CaloriesRecordServices.findUsersData(function(response) {
			$scope.usersData = response.data;
		});
	} else {
		$scope.caloriesRecord.user.id = CaloriesRecordServices.getLoggedUserId();
	}
	$scope.editCaloriesRecord = function() {
		CaloriesRecordServices.editCaloriesRecord($scope.caloriesRecord,
			function(response) {
				$location.path('caloriesRecords');
			},
			function(response) {
				$scope.error = response.message;
			}
		)
	};
	$scope.canCrudOtherUsersItems = function() {
		return CaloriesRecordServices.canCrudOtherUsersItems();
	};
});
