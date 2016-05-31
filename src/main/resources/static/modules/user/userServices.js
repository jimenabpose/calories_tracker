'use strict';

angular.module('user')

.factory('UserServices',
	['$http', 'AuthenticationServices',
    function ($http, AuthenticationSetvices) {
        var service = {};
        service.get = function (pageNumber, usersPerPage, usernameContent, showOnlyEnabledUsers, callback, errorCallback) {
            $http.get('/api/user', {
    			params: {
    				pageNumber: pageNumber,
    				pageSize: usersPerPage,
    				usernameContent: usernameContent,
    				showOnlyEnabledUsers: showOnlyEnabledUsers
    			}
    		}).success(function (response) {
    			callback(response);
    		}).error(function (response) {
    			errorCallback(response);
    		});
        };
        service.getUser = function(id, callback, errorCallback) {
        	$http.get('/api/user/' + id).then(function(response) {
        		callback(response);
        	});
    	};
    	service.editUser = function(user, callback, errorCallback) {
    		$http.put('/api/user/', user)
    		.success(function (response) {callback(response);})
    		.error(function (response) {errorCallback(response);});
    	};
    	service.addUser = function(user, callback, errorCallback) {
    		$http.post('/api/user' + (AuthenticationSetvices.isAuthenticated() ? '' : '/register'), user)
    		.success(function (response) {callback(response);})
    		.error(function (response) {errorCallback(response);});
    	};
        service.deleteUser = function(id, callback, errorCallback) {
    		$http.delete('/api/user/' + id)
    		.success(function (response) {callback(response);})
    		.error(function (response) {errorCallback(response);});
    	};
    	service.getRolesData = function(callback) {
    		$http.get('/api/combos/roles').then(function (response) {
                callback(response);
            });
    	};
    	service.getUserRole = function(callback) {
    		$http.get('/api/combos/userRole').then(function (response) {
                callback(response);
            });
    	};
        return service;
    }
]);
