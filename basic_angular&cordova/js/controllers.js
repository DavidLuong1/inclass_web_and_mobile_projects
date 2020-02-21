angular.module('starter.controllers', [])

.controller('AppCtrl', function($scope, $ionicModal, $timeout) {

  // With the new view caching in Ionic, Controllers are only called
  // when they are recreated or on app start, instead of every page change.
  // To listen for when this page is active (for example, to refresh data),
  // listen for the $ionicView.enter event:
  //$scope.$on('$ionicView.enter', function(e) {
  //});

  // Form data for the login modal
  $scope.loginData = {};

  // Create the login modal that we will use later
  $ionicModal.fromTemplateUrl('templates/login.html', {
    scope: $scope
  }).then(function(modal) {
    $scope.modal = modal;
  });

  // Triggered in the login modal to close it
  $scope.closeLogin = function() {
    $scope.modal.hide();
  };

  // Open the login modal
  $scope.login = function() {
    $scope.modal.show();
  };

  // Perform the login action when the user submits the login form
  $scope.doLogin = function() {
    console.log('Doing login', $scope.loginData);

    // Simulate a login delay. Remove this and replace with your login
    // code if using a login system
    $timeout(function() {
      $scope.closeLogin();
    }, 1000);
  };
})

.controller('CoursesCtrl', function($scope, Courses) {

    $scope.courses = Courses.getCourses();

})
.controller('CourseCtrl', function($scope, $stateParams, Courses) {
  $scope.course = Courses.getCourse($stateParams.courseId);

})

.service('Courses', function($filter) {
var courses = [
    { title: 'Web and Mobile I', room: '70-3590',id: 1 },
    { title: 'Web and Mobile II',room: '70-2620', id: 2 },
    { title: 'Mobile Foundations',room: '70-3410', id: 3 },
    { title: 'App Dev I', room: '70-3590',id: 4 },
    { title: 'App Dev II',room: '70-3590', id: 5 },
    { title: 'Elective',room: '70-2690', id: 6 }
  ];
  return {
        getCourses: function(){
                return courses;
          },
          getCourse: function(courseId){
          return $filter('filter') (courses, {id:courseId})[0];
          }
  };
});














