'use strict';

angular.module('user', [])

.controller('Users', function($scope, $location, UserServices) {
	$scope.userFilter = {};
	$scope.user = {};
	$scope.users = {};
	$scope.error = null;
	$scope.pagination = {
        current: 1,
        usersPerPage: 8,
        totalUsers: 0
    };
	$scope.viewUser = function($routeParams) {
		$location.path('user/view/' + $routeParams.id);
	};
	$scope.editUser = function($routeParams) {
		$location.path('user/edit/' + $routeParams.id);
	};
	$scope.addUser = function() {
		$location.path('user/add/');
	};
	$scope.deleteUser = function($routeParams) {
		var id = $routeParams.id;
		UserServices.deleteUser(id,
			function(response) {
				$scope.getData();
			},
			function (response) {
				$scope.error = response.message;
			}
		);
	};
	$scope.getData = function() {
		UserServices.get(
			$scope.pagination.current,
			$scope.pagination.usersPerPage,
			$scope.userFilter.usernameContent,
			$scope.userFilter.showOnlyEnabledUsers,
			function (response) {
				$scope.users = response.content;
				$scope.pagination.totalUsers = response.totalElements;
				$scope.error = null;
			},
			function (response) {
				$scope.error = response.message;
			}
		);
	};
	$scope.findUsers = function() {
		$scope.getData();
	};
	$scope.getData();
})

.controller('ViewUser', function($scope, $routeParams, $location, UserServices) {
	var id = $routeParams.id;
	UserServices.getUser(id,function(response) {
		$scope.user = response.data;
	});
	$scope.editUser = function($routeParams) {
		$location.path('user/edit/' + $routeParams.id);
	};
})

.controller('AddUser', function($scope, $location, $window, AuthenticationServices, UserServices) {
	$scope.user = {
		enabled: true
	};
	$scope.isAuthenticated = function() {
		return AuthenticationServices.isAuthenticated();
	};
	$scope.passwordIsRequired = function() {
		return true;
	};
	if (AuthenticationServices.canCrudOtherUsers()) {
		UserServices.getRolesData(function(response) {
			$scope.rolesData = response.data;
		});
	} else {
		UserServices.getUserRole(function(response) {
			$scope.user.rolesList = [response.data];
		});
	}
	$scope.addUser = function() {
		UserServices.addUser(
			$scope.user,
			function(response) {
				if ($scope.isAuthenticated()) {
					$location.path('users');
				} else {
					$window.alert("User created successfully");
					$location.path('login');
				}
			},
			function(response) {
				$scope.error = response.message;
			}
		);
	};
})

.controller('EditUser', function($scope, $routeParams, $location, AuthenticationServices, UserServices) {
	var id = $routeParams.id;
	UserServices.getUser(id,function(response) {
		$scope.user = response.data;
	});
	$scope.isAuthenticated = function() {
		return AuthenticationServices.isAuthenticated();
	};
	$scope.passwordIsRequired = function() {
		return false;
	};
	if (AuthenticationServices.canCrudOtherUsers()) {
		UserServices.getRolesData(function(response) {
			$scope.rolesData = response.data;
		});
	} else {
		UserServices.getUserRole(function(response) {
			$scope.user.rolesList = [response.data];
		});
	}
	$scope.editUser = function() {
		UserServices.editUser(
			$scope.user,
			function(response) {
				$location.path('users');
			},
			function(response) {
				$scope.error = response.message;
			}
		)
	};
});
