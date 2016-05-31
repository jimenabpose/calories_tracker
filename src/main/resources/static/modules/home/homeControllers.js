'use strict';

angular.module('home', [])
	.controller('home', function($scope, $http, HomeServices) {
		if (HomeServices.canCrudCaloriesRecords()) {
			$scope.canCrudCaloriesRecords = true;
			$scope.statusIsOk = true;
			HomeServices.getCaloriesForToday(function(response) {
				$scope.caloriesPerDay = response.quantityPerDay;
				$scope.caloriesForToday = response.quantityForToday;
				$scope.statusIsOk = response.quantityForToday < response.quantityPerDay ? true : false;
			});
		} else {
			$scope.canCrudCaloriesRecords = false;
		}
	});