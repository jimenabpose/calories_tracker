'use strict';

angular.module('authentication', [])
.controller('Authentication', function($scope, $route, $location, AuthenticationServices, APP_PATHS) {
	$scope.credentials = {};
	$scope.tab = function(route) {
		return $route.current && route === $route.current.controller;
	};
	$scope.isAuthenticated = function() {
		return AuthenticationServices.isAuthenticated();
	};
	$scope.login = function() {
		AuthenticationServices.authenticate($scope.credentials, function(authenticated) {
			if (authenticated) {
				$scope.error = false;
				$location.path(APP_PATHS.HOME);
			} else {
				$scope.error = true;
			}
		})
	};
	$scope.canCrudOtherUsers = function() {
		return AuthenticationServices.canCrudOtherUsers();
	};
	$scope.canCrudCaloriesRecords = function() {
		return AuthenticationServices.canCrudCaloriesRecords();
	};
	$scope.getLoggedUserUsername = function() {
		return AuthenticationServices.getLoggedUserUsername();
	}
	$scope.logout = AuthenticationServices.clearAuth;
});
