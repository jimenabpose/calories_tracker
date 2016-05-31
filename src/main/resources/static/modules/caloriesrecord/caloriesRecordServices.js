'use strict';

angular.module('caloriesRecord')

.factory('CaloriesRecordServices',
	['$http', 'AuthenticationServices',
    function ($http, AuthenticationServices) {
		var serializeDate = function(date) {
			return moment(date).format('YYYY-MM-DD');
		};
		var serializeTime = function(time) {
			return moment(time).format('HH:mm');
		};
		var unserializeDate = function(date) {
			return moment(date, ['YYYY-MM-DD']).toDate();
		};
		var unserializeTime = function(time) {
			return moment(time, ['HH:mm']).toDate();
		};
		var getCaloriesRecordResponseWithUnserializedFormats = function(response) {
			response.data.date = unserializeDate(response.data.date);
			response.data.time = unserializeTime(response.data.time);
			return response;
		};
		var getCaloriesRecordRequestWithSerializedFormats = function(request) {
			var serializedRequest = JSON.parse(JSON.stringify(request));
			serializedRequest.date = serializeDate(request.date);
			serializedRequest.time = serializeTime(request.time);
			return serializedRequest;
		};
        var service = {};
        service.get = function (pageNumber, itemsPerPage, user, dateFrom, dateTo, timeFrom, timeTo, callback, errorCallback) {
        	$http.get('/api/caloriesRecord', {
    			params: {
    				pageNumber: pageNumber,
    				pageSize: itemsPerPage,
    				user: user,
					dateFrom: dateFrom ? serializeDate(dateFrom) : dateFrom,
					dateTo: dateTo ? serializeDate(dateTo) : dateTo,
					timeFrom: timeFrom ? serializeTime(timeFrom) : timeFrom,
					timeTo: timeTo ? serializeTime(timeTo) : timeTo
    			}
    		}).success(function (response) {
    			callback(response);
    		}).error(function (response) {
    			errorCallback(response)
    		});
        };
        service.getCaloriesRecord = function(id, callback) {
        	$http.get('/api/caloriesRecord/' + id).then(function(response) {
        		callback(getCaloriesRecordResponseWithUnserializedFormats(response));
        	});
    	};
    	service.editCaloriesRecord = function(caloriesRecord, callback, errorCallback) {
    		$http.put('/api/caloriesRecord/', getCaloriesRecordRequestWithSerializedFormats(caloriesRecord)).then(
				function(response) {
					callback(getCaloriesRecordResponseWithUnserializedFormats(response));
				},
				function(response) {
					errorCallback(response);
				}
			);
    	}
    	service.addCaloriesRecord = function(caloriesRecord, callback, errorCallback) {
    		$http.post('/api/caloriesRecord', getCaloriesRecordRequestWithSerializedFormats(caloriesRecord)).then(
				function(response) {
					callback(getCaloriesRecordResponseWithUnserializedFormats(response));
				},
				function(response) {
					errorCallback(response);
				}
			);
    	}
        service.deleteCaloriesRecord = function(id, callback, errorCallback) {
    		$http.delete('/api/caloriesRecord/' + id).then(
				function (response) {
    				callback(response);
	            },
				function(response) {
					errorCallback(response);
				}
	        );
    	};
    	service.canCrudOtherUsersItems = function() {
    		return AuthenticationServices.canCrudOtherUsersItems();
    	};
    	service.getLoggedUserId = function() {
    		return AuthenticationServices.getLoggedUserId();
    	};
    	service.findUsersData = function(callback) {
    		$http.get('/api/combos/users').then(function(response) {
    			callback(response);
    		});
    	}
        return service;
    }
]);
