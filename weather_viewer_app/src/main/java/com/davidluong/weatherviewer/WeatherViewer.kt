package com.davidluong.weatherviewer

import android.content.Context
import android.content.SharedPreferences
import android.location.LocationListener
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.FragmentTransaction
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_weather_viewer.*
import kotlinx.android.synthetic.main.toolbar.*
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.Toast
import com.davidluong.weatherviewer.AddCityDialogFragment
import com.davidluong.weatherviewer.ReadLocationTask
import com.davidluong.weatherviewer.SingleForecastFragment
import de.hdodenhof.circleimageview.*
import android.support.v4.widget.SwipeRefreshLayout

class WeatherViewer : AppCompatActivity(), AddCityDialogFragment.DialogFinishedListener {

/*
params are sent to the background thread; progress sent to main thread; result is sent back to the foreground thread; must implement doInBackground( //WebRequests go here ); pausing an activity doesn't pause the task

appNS (app namespace)

app:showAsAction="ifRoom|withText"     //if there's room, show icon otherwise show its text

For material design, tool bars used instead of action bar (still usable)
    To change the bar type, go to 'styles.xml'


https://www.rit.edu/its/resnet/android     //set domain to 'radius.rit.edu'
*/

    companion object {
        val PREFERRED_CITY_NAME_KEY = "preferred_city_name"
        val PREFERRED_CITY_ZIPCODE_KEY = "preferred_city_zipcode"
        val SHARED_PREFERENCES_NAME = "weather_viewer_shared_preferences"
        val LAST_SELECTED_KEY = "last_selected"
        val MY_PERMISSIONS_REQUEST_ACCESS_INTERNET = 1
    }

    private var favoriteCitiesMap = mutableMapOf<String,String>()
    private var lastSelectedCity:String? = null
    var mAdapter: MyAdapter? = null
    var weatherSharedPreferences: SharedPreferences? = null
    var mLayoutManager: LinearLayoutManager? = null
    var mDrawerToggle: ActionBarDrawerToggle? = null

    var refreshing: Boolean = false //to avoid calling the API multiple times


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_viewer)

        //attach the layout to the toolbar object
        setSupportActionBar(toolbar)

        //create HashMap storing city names and zipcodes
        favoriteCitiesMap = HashMap<String,String>()

        weatherSharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)  //keeps shared preferences, JUST to this app

        //let the system know that list of objects is fixed size
        mRecyclerView.setHasFixedSize(true)

/*
        //create our adapter
        mAdapter = MyAdapter(favoriteCitiesMap.keys.toMutableList())

        //set the adapter for the RecyclerView
        mRecyclerView.adapter = mAdapter

        //call the layout manager
        mRecyclerView.layoutManager = mLayoutManager
*/

        //create our adapter
        mAdapter = MyAdapter(favoriteCitiesMap.keys.toMutableList(), applicationContext)

        //creating a layout Manager
        mLayoutManager = LinearLayoutManager(this)

        //call the layout manager (order's switched)
        mRecyclerView.layoutManager = mLayoutManager

        //set the adapter for the RecyclerView
        mRecyclerView.adapter = mAdapter


        //add decorations between items
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST)
        mRecyclerView.addItemDecoration(itemDecoration)


        /**
        * Alternative to the onClick() defined in the 'init{...}' in "MyAdapter.kt" file
        */
        val mGestureDetector = GestureDetector(this@WeatherViewer, object: GestureDetector.SimpleOnGestureListener(){

            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                //return super.onSingleTapConfirmed(e)
                return true
            }

        })
        mRecyclerView.addOnItemTouchListener(object: RecyclerView.OnItemTouchListener {  //right-clicked on 'object' -> generate all 3 options listed
            override fun onTouchEvent(p0: RecyclerView, p1: MotionEvent) {

            }

            override fun onInterceptTouchEvent(recyclerView: RecyclerView, motionEvent: MotionEvent): Boolean {
                val child = recyclerView.findChildViewUnder(motionEvent.x, motionEvent.y)

                /*
                * When tapping a layout, displays the toast, then immediately closes the drawer
                */
                if( child!=null && mGestureDetector.onTouchEvent(motionEvent) ){
                    DrawerLayout.closeDrawers()
                    /*
                    Toast.makeText(this@WeatherViewer,
                            "Adapter position: ${recyclerView.getChildAdapterPosition(child)} Layout Position: ${recyclerView.getChildLayoutPosition(child)}",
                            Toast.LENGTH_SHORT)
                            .show()
                    */

                    val pos = recyclerView.getChildAdapterPosition(child)

                    if( pos > 0 ){   //We don't want to tap on the header
                        val city = mAdapter!!.getCity(recyclerView.getChildAdapterPosition(child))
                        selectForecast(city)
                    }

                    return true
                }

                return false
            }

            override fun onRequestDisallowInterceptTouchEvent(p0: Boolean) {

            }

        })


        //drawer object assigned to the view
        mDrawerToggle = object:ActionBarDrawerToggle(this, DrawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)

                //code here will execute once the drawer is opened (NOTE: this example doesn't do anything)


            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)

                //code here will execute once the drawer is closed (NOTE: this example doesn't do anything)

            }

        } //mDrawerToggle()

        DrawerLayout.addDrawerListener(mDrawerToggle!!)

        //synchronize the state of the drawer indicator with the linked layout
        mDrawerToggle!!.syncState() //builds/fills up the navigation


        refreshing = false

        swipe_refresh_layout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)

        swipe_refresh_layout.setOnRefreshListener{
            refreshing = true

            //do whatever's on the background thread
            loadSelectedForecast()
        }

    } //onCreate()

    override fun onResume() {
        super.onResume()

        if( favoriteCitiesMap.isEmpty() ) {
            loadSavedCities() //load previously saved cities
        }

        if( favoriteCitiesMap.isEmpty() ) {
            //still empty (i.e., no saved cities)
            addSampleCities() //notifies the app, and reloads/redraws
        }

        mAdapter?.notifyDataSetChanged() //refresh the adapter

        loadSelectedForecast()

    } //onResume()


    //display the forecast for the given city
    fun selectForecast(name: String){
        lastSelectedCity = name
        var zipcodeString = favoriteCitiesMap.get(name)

        if( zipcodeString == null ){
            return //don't attempt to load the forecast
        }

        //get the current fragment
        var currentForecastFragment = supportFragmentManager.findFragmentById(R.id.forecast_replacer)

        if( currentForecastFragment == null || !((currentForecastFragment) as SingleForecastFragment).getZipcode().equals(zipcodeString) || refreshing ){

            refreshing = false

            //create one using the zipcode
            currentForecastFragment = SingleForecastFragment.newInstance(zipcodeString, swipe_refresh_layout)
        }

        //display it
        supportFragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.forecast_replacer, currentForecastFragment)
                .commit()

    }


    //load the previously selected forecast
    private fun loadSelectedForecast(){
        if( lastSelectedCity !== null ){

            selectForecast(lastSelectedCity!!)

        } else {
            //get and load the preferred city
            val cityNameString = weatherSharedPreferences!!.getString(PREFERRED_CITY_NAME_KEY, resources.getString(R.string.default_zipcode))
            selectForecast(cityNameString)
        }
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(LAST_SELECTED_KEY, lastSelectedCity)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        lastSelectedCity = savedInstanceState?.getString(LAST_SELECTED_KEY)  //if exists

    }


    private fun loadSavedCities() {
        var citiesMap = weatherSharedPreferences!!.all

        if( citiesMap.size > 0 || citiesMap.isNotEmpty() ) {
            for(cityString in citiesMap.keys) {
                //if not the preferred city
                if( !(cityString.equals(PREFERRED_CITY_NAME_KEY) || cityString.equals(PREFERRED_CITY_ZIPCODE_KEY)) ) {

                    addCity(cityString, citiesMap.get(cityString) as String, false)
                }
            }
        }

    } //loadSavedCities()

    private fun addSampleCities() {
        val sampleCityNamesArray = resources.getStringArray(R.array.default_city_names)
        val sampleCityZipCodesArray = resources.getStringArray(R.array.default_city_zipcodes)

        //for(i in 0..sampleCityNamesArray.size) {
        for(i in 0 until sampleCityNamesArray.size) {
            //set the first sample city as the preferred city by default
            if( i==0 ){
                setPreferred(sampleCityNamesArray[i])
            }

            addCity(sampleCityNamesArray[i], sampleCityZipCodesArray[i], false)
        }

    } //addSampleCities()

    private fun addCity(city: String, zipcode: String, select: Boolean) {
        //favoriteCitiesMap.put(city, zipcode)
        favoriteCitiesMap[city] = zipcode        //CHOOSE THIS over the top option

        //add them to the adapter
        mAdapter?.addCity(city)
        weatherSharedPreferences!!.edit().putString(city, zipcode).apply()

    } //addCity()

    private fun setPreferred(cityNameString: String) {
        //get the zip
        val cityZipCode = favoriteCitiesMap[cityNameString]

        //UPDATE the shared preferences
        weatherSharedPreferences!!.edit()
                .putString(PREFERRED_CITY_NAME_KEY, cityNameString)
                .putString(PREFERRED_CITY_ZIPCODE_KEY, cityZipCode)
                .apply()

    } //setPreferred()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_weather_viewer, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        if( item?.itemId == R.id.add_city_item ){
            showAddCityDialog()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showAddCityDialog(){
        val newAddDialogFragment = AddCityDialogFragment()

        supportFragmentManager
                .beginTransaction()
                .add(newAddDialogFragment, "addCity")
                .commit()
    }

    override fun onDialogFinished(zipcodeString: String, preferred: Boolean) {
        //convert zipcode to city
        getCityNameFromZipCode(zipcodeString, preferred)
    }

    private fun getCityNameFromZipCode(zipcodeString: String, preferred: Boolean){
        //is it already here?
        if( favoriteCitiesMap.containsValue(zipcodeString) ){

            var errorToast = Toast.makeText(this@WeatherViewer, resources.getString(R.string.duplicate_zipcode_error), Toast.LENGTH_LONG)
            errorToast.setGravity(Gravity.CENTER, 0,0)
            errorToast.show()

        } else {
            ReadLocationTask(zipcodeString, this@WeatherViewer, CityNameLocationLoadedListener(zipcodeString, preferred)).execute()
        }

    }


    inner class CityNameLocationLoadedListener(var zipcodeString: String, var preferred: Boolean): ReadLocationTask.LocationLoadedListener {
        override fun onLocationLoaded(cityString: String?, countryString: String?) {
            if( cityString != null ){
                addCity(cityString, zipcodeString, !preferred)

                if( preferred ){
                    setPreferred(cityString)
                }

            } else {
                    var zipcodeToast = Toast.makeText(this@WeatherViewer, resources.getString(R.string.invalid_zipcode_error), Toast.LENGTH_LONG)
                    zipcodeToast.setGravity(Gravity.CENTER, 0,0)
                    zipcodeToast.show()

            } //inner if-else

            mAdapter!!.notifyDataSetChanged()

        } //onLocationLoaded()

    }

}
