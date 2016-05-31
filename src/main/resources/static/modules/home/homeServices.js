'use strict';

angular.module('home')

.factory('HomeServices',
	['$http', 'AuthenticationServices',
    function ($http, AuthenticationServices) {
        var service = {};
        service.getCaloriesForToday = function (callback) {
        	$http.get('/api/userSettings/date', {
    			params: {
    				date: moment().format('YYYY-MM-DD')
    			}
    		}).success(function (response) {
    			callback(response);
    		});
        };
        service.canCrudCaloriesRecords = function () {
        	return AuthenticationServices.canCrudCaloriesRecords();
        };
        return service;
    }
]);
