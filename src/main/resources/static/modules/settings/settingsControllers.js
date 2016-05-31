'use strict';

angular.module('settings', [])

.controller('UserSettings', function($scope, $location, SettingsServices) {
	$scope.quantityPerDay = 0;
	$scope.save = function() {
		SettingsServices.save($scope.quantityPerDay, function(response) {
			$location.path('/');
		});
	};
	$scope.getData = function() {
		SettingsServices.get(function(response) {
			$scope.quantityPerDay = response.data;
		});
	};
	$scope.getData();
})
