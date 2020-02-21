/*
 * David Luong
 * ISTE-252
 * Project 3: Creating a Hybrid Mobile App
 *
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var app = {
    // Application Constructor
    initialize: function() {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
    },

    // deviceready Event Handler
    //
    // Bind any cordova events here. Common events are:
    // 'pause', 'resume', etc.
    onDeviceReady: function() {
        this.receivedEvent('deviceready');
    },

    // Update DOM on a Received Event
    receivedEvent: function(id) {
        var parentElement = document.getElementById(id);
        var listeningElement = parentElement.querySelector('.listening');
        var receivedElement = parentElement.querySelector('.received');

        listeningElement.setAttribute('style', 'display:none;');
        receivedElement.setAttribute('style', 'display:block;');

        console.log('Received Event: ' + id);

        /* when the receivedEvent is called, then call the geo location function */
        navigator.geolocation.getCurrentPosition(onSuccess, onError, { timeout: 10000 });
    }
};

app.initialize();


function onSuccess(position) {
    var lat=position.coords.latitude;
    var lang=position.coords.longitude;

    //Google Maps
    var myLatlng = new google.maps.LatLng(lat,lang);
    var mapOptions = {zoom: 4,center: myLatlng}
    var map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
    var marker = new google.maps.Marker({position: myLatlng,map: map});
}

function onError(error) {
    alert('code: '+error.code+'\n'+'message: '+error.message+'\n');
}


/**
 *  AJAX Calls
 */
function loadWeatherData(){
    var city   = document.getElementsByClassName('loc');
    var domain = "http://api.openweathermap.org/data/2.5/weather?q=";
    var key    = "6aed9037c181480e720ba28f14573f02";
    var url    = domain + getSelectedCity('address') + "&appid=" + key;

    var xmlhttp=new XMLHttpRequest();
    xmlhttp.onreadystatechange=function(){
        if(xmlhttp.readyState==4 && xmlhttp.status==200){
              var weatherData=JSON.parse(xmlhttp.responseText);
              myFunction(weatherData);
        }
    };
    xmlhttp.open("GET",url,true);
    xmlhttp.send();

    function myFunction( weatherData ){
        var weatherDescription = weatherData.weather[0].description;
        var weatherIcon        = weatherData.weather[0].icon;
        var cityName           = weatherData.name;

/*
    =====================================================================================================
    =    The code below is meant to display the weather icon from open_weather_map_api, and it works.   =
    =      I left this part out because I would have major tweaks to make in my CSS styles.             =
    =====================================================================================================
        //Create a weather-image icon using HTML's DOM
        var image = document.createElement("img");
        image.setAttribute("id", "icon");
        image.style.display = "block";
        image.style.margin = "auto";
        image.src = "http://api.openweathermap.org/img/w/" + weatherIcon + ".png";

        //Insert the weather icon after the corresponding button label, and before the weather text
        document.getElementById("currWeather").appendChild(image);
*/

        var imgURL = "http://api.openweathermap.org/img/w/" + weatherIcon + ".png";
        var image = $("<img id='icon'>");
        image.attr("src", imgURL);
        image.css({'display':'inline-block', 'margin':'auto'});

        document.getElementById("desc").innerHTML = weatherDescription;
        document.getElementById("loc").innerHTML = cityName;

        /* Using a JS Date method to retrieve the current time, instead of doing so
            using the Cordova plugin. */
        var time = new Date();
        document.getElementById("timeInfo").innerHTML = time.toLocaleTimeString();
    }
} //end of loadWeatherData() method


/**
 *  Weather Map & Location Script
 */
function changeLocation(){
    //Get the address info. from the 'value' of the selected item
    var latlng = document.getElementById("address").value;
    var lat = parseFloat(latlng.substring(0,7));
    var lng = parseFloat(latlng.substring(8,15)) * -1;
    console.log(lat);
    console.log(lng);

    //Google Maps
    var myLatlng = new google.maps.LatLng(lat,lng);
    var mapOptions = {zoom: 4,center: myLatlng}
    var map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
    var marker = new google.maps.Marker({
         draggable: true,
         animation: google.maps.Animation.DROP,
         position: myLatlng,
         map: map
    });
}

function getSelectedCity(eleId){
    var dropDown = document.getElementById(eleId);

    if(dropDown.selectedIndex == -1){
        return null;
    }
    return dropDown.options[dropDown.selectedIndex].text;
}
// google.maps.event.addDomListener(window, 'load', onSuccess);
//$(document).bind("projectLoadComplete", initialize);


/**
 *  More jQuery functionalities
 */
//Smooth scrolling
$(window).on('scroll', function(){
   if( $(window).scrollTop() > 40 ){
      $('h1').addClass('inactive');
      $('.inactive').css('opacity','1.0');
   } else{
      $('h1').removeClass('inactive');
      $('h1').css('opacity', '0.8');
   }
});
