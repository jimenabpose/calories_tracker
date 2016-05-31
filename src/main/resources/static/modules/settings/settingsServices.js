'use strict';

angular.module('settings')

.factory('SettingsServices',
	['$http',
    function ($http) {
        var service = {};
        service.get = function (callback) {
            $http.get('/api/userSettings/').then(function (response) {
                callback(response);
            });
        };
        service.save = function(quantityPerDay, callback) {
        	$http.put('/api/userSettings/', quantityPerDay).then(function(response) {
    			callback(response);
    		});
    	};
        return service;
    }
]);
