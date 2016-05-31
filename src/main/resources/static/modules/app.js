'use strict';

angular
	.module('app', [
        'ngRoute',
        'LocalStorageModule',
        'ui.bootstrap',
        'angularUtils.directives.dirPagination',
        'pascalprecht.translate',
        'authentication',
        'home',
        'user',
        'caloriesRecord',
        'settings'
    ])
    .constant("APP_PATHS", {
    	"HOME": "/",
    	"LOGIN": "/login",
    	"REGISTER": "/user/add"
    })
	.config(
			function($routeProvider, $httpProvider, $locationProvider, $translateProvider, localStorageServiceProvider) {

				$locationProvider.html5Mode(true);
				localStorageServiceProvider.setPrefix('caloriesTracker').setNotify(true, true);

				$routeProvider.when('/', {
					templateUrl : 'modules/home/views/home.html',
					controller : 'home',
					controllerAs : 'controller'
				}).when('/message', {
					templateUrl : 'modules/message/message.html',
					controller : 'message',
					controllerAs : 'controller'
				}).when('/settings', {
					templateUrl : 'modules/settings/views/settings.html',
					controller : 'UserSettings',
					controllerAs : 'controller'
				}).when('/login', {
					templateUrl : 'modules/authentication/views/login.html',
					controller : 'Authentication',
					controllerAs : 'controller'
				}).when('/users', {
					templateUrl : 'modules/user/views/list.html',
					controller : 'Users',
					controllerAs : 'controller'
				}).when('/user/view/:id', {
					templateUrl : 'modules/user/views/view.html',
					controller : 'ViewUser',
					controllerAs : 'controller'
				}).when('/user/edit/:id', {
					templateUrl : 'modules/user/views/edit.html',
					controller : 'EditUser',
					controllerAs : 'controller'
				}).when('/user/add/', {
					templateUrl : 'modules/user/views/add.html',
					controller : 'AddUser',
					controllerAs : 'controller'
				}).when('/user/delete/:id', {
					templateUrl : 'modules/user/views/list.html',
					controller : 'DeleteUser',
					controllerAs : 'controller'
				}).when('/caloriesRecords', {
					templateUrl : 'modules/caloriesrecord/views/list.html',
					controller : 'CaloriesRecords',
				}).when('/caloriesRecord/view/:id', {
					templateUrl : 'modules/caloriesrecord/views/view.html',
					controller : 'ViewCaloriesRecord',
				}).when('/caloriesRecord/edit/:id', {
					templateUrl : 'modules/caloriesrecord/views/edit.html',
					controller : 'EditCaloriesRecord',
				}).when('/caloriesRecord/add/', {
					templateUrl : 'modules/caloriesrecord/views/add.html',
					controller : 'AddCaloriesRecord',
				}).when('/caloriesRecord/delete/:id', {
					templateUrl : 'modules/caloriesrecord/views/list.html',
					controller : 'DeleteCaloriesRecord',
				}).otherwise('/');
				
				$httpProvider.interceptors.push(function ($q, $rootScope, $location, localStorageService, APP_PATHS) {
			        return {
			        	'responseError': function(rejection) {
			        		var status = rejection.status;
			        		var config = rejection.config;
			        		var method = config.method;
			        		var url = config.url;
			        		if (status == 401 || status == 403) {
			        			localStorageService.remove("globals");
			        			$rootScope.globals = {};
			        			$location.path(APP_PATHS.LOGIN);
			        		}
			        		return $q.reject(rejection);
			        	}
			        };
			    });
				
				$translateProvider.translations('en', {
					UserError_PAGE_SIZE_INVALID: 'Invalid page size',
					UserError_USER_CREATION_NO_ID: 'Can not specify an id for a new user',
					UserError_USERNAME_ALREADY_TAKEN: 'Username already taken',
					UserError_PASSWORD_CAN_NOT_BE_EMPTY: 'Password can not be empty',
					UserError_USER_NOT_FOUND: 'User not found',
					UserError_PASSWORD_INVALID_MATCHING: 'Password and it\'s confirmation do not match',
					UserError_USERNAME_CAN_NOT_BE_EMPTY: 'Username can\t be empty',
					UserError_ROLE_UNAUTHORIZED: 'Unauthorized role for user creation',
					UserError_CAN_NOT_DISABLE_CURRENT_USER: 'You can\'t disable yourself',
					UserError_CAN_NOT_CHANGE_CURRENT_USER_ROLES: 'You can\'t change your own roles',
					CaloriesRecordError_UNAUTHORIZED_REQUEST: 'Unauthorized request',
					CaloriesRecordError_PAGE_SIZE_INVALID: 'Invalid page size',
					CaloriesRecordError_DATE_RANGE_INVALID: 'Please use a valid date range',
					CaloriesRecordError_CALORIES_RECORD_NOT_FOUND: 'Calories record not found',
					CaloriesRecordError_CALORIES_RECORD_CREATION_NO_ID: 'Can not specify an id for a new calories record'
			    });
			    $translateProvider.preferredLanguage('en');
			    $translateProvider.useSanitizeValueStrategy('escape');
			})
			.run(['$rootScope', '$location', 'localStorageService', '$http', 'AuthenticationServices', 'APP_PATHS',
			    function ($rootScope, $location, localStorageService, $http, AuthenticationServices, APP_PATHS) {
			        // keep user logged in after page refresh
			        $rootScope.globals = localStorageService.get('globals') || {};
			        if ($rootScope.globals.currentUser) {
			            $http.defaults.headers.common['Authorization'] = 'Bearer ' + $rootScope.globals.currentUser.token;
			        }
			        $rootScope.$on('$locationChangeStart', function (event, next, current) {
			            // redirect to login page if not logged in
			            if (!AuthenticationServices.isAuthenticated() && !AuthenticationServices.pathIsPublic($location.path())) {
			            	$location.path(APP_PATHS.LOGIN);
			            }
			        });
			    }
			])
;
