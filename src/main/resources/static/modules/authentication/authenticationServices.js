'use strict';

angular.module('authentication')

.factory('AuthenticationServices',
	['$http', '$rootScope', '$location', 'localStorageService', 'APP_PATHS',
    function($http, $rootScope, $location, localStorageService, APP_PATHS) {
		$rootScope.globals = {};
		
		var userHasAuthority = function(authorityToFind) {
			if ($rootScope.globals.currentUser && $rootScope.globals.currentUser.authorities) {
				var authorities = $rootScope.globals.currentUser.authorities;
				for (var authority in authorities) {
					if (authorities[authority] == authorityToFind) {
						return true;
					}
				}
			}
			return false;
		}
		
		var service = {
			authenticate : function(credentials, callback) {
				$http.post('api/authentication', {
    				username: credentials.username,
    				password: credentials.password
				}).then(function(response) {
					if (response.data.token) {
						service.setAuth(response.data);
					} else {
						service.clearAuth();
					}
					callback && callback($rootScope.globals.currentUser != null);
				}, function() {
					service.clearAuth();
					callback && callback(false);
				});

			},

			setAuth : function(auth) {
	            $rootScope.globals = {
	                currentUser: {
	                    username: auth.username.toLowerCase(),
	                    userId: auth.userId,
	                    token: auth.token,
	                    expiration: auth.expiration,
	                    authorities: auth.authorities
	                }
	            };
	            $http.defaults.headers.common['Authorization'] = 'Bearer ' + auth.token;
	            localStorageService.set("globals", $rootScope.globals);
	        },
	 
	        clearAuth : function() {
	            $rootScope.globals = {};
	            localStorageService.remove("globals");
	            $http.defaults.headers.common.Authorization = 'Bearer ';
	            $location.path(APP_PATHS.LOGIN);
	        },
	        
			isAuthenticated : function() {
				var localStorageData = localStorageService.get('globals');
				if (!!localStorageData && !!localStorageData.currentUser) {
					var expireTime = moment(localStorageData.currentUser.expiration) - moment();
					if (expireTime < 0) {
						localStorageService.remove('globals')
					}
					return expireTime > 0;
				}
				return false;
			},
			
			getLoggedUserId : function() {
				return $rootScope.globals.currentUser.userId;
			},
	    	
	    	getLoggedUserUsername : function() {
	    		return service.isAuthenticated() && $rootScope.globals.currentUser.username;
	    	},
			
			canCrudOtherUsers : function() {
				return userHasAuthority('USER_CRUD_OTHERS');
			},
			
			canCrudCaloriesRecords : function() {
				return userHasAuthority('CALORIES_RECORD_CRUD_OTHERS') || userHasAuthority('CALORIES_RECORD_CRUD_OWN');
			},
			
			canCrudOtherUsersItems : function() {
	    		return userHasAuthority('CALORIES_RECORD_CRUD_OTHERS');
	    	},
			
			pathIsPublic : function(path) {
				return path == APP_PATHS.LOGIN || path.startsWith(APP_PATHS.REGISTER);
			}
		};
		return service;
    }
]);