package com.davidluong.weatherviewer

// SingleForecastFragment.kt
// Displays forecast information for a single city.

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.davidluong.weatherviewer.R
import kotlinx.android.synthetic.main.single_forecast_layout.*

class SingleForecastFragment : Fragment() {

    private var zipcodeString: String? = null // zipcode for this forecast
    private var conditionBitmap: Bitmap? = null
    private var contxt: Context? = null

    // lookup keys for the Fragment's saved state
    private val LOCATION_KEY = "location"
    private val TEMPERATURE_KEY = "temperature"
    private val HUMIDITY_KEY = "humidity"
    private val IMAGE_KEY = "image"

    // used to retrieve zipcode from saved Bundle
    companion object {
        val ZIP_CODE_KEY = "id_key"
        var mRefreshLayout: SwipeRefreshLayout? = null  //was a val

        @JvmStatic
        fun newInstance(zipCodeString: String, refreshLayout: SwipeRefreshLayout?) =
                SingleForecastFragment().apply {
                    arguments = Bundle().apply {
                        putString(ZIP_CODE_KEY, zipCodeString)
                    }

                    mRefreshLayout = refreshLayout //without this, the isRefreshable in the load method at the bottom won't work
                }

        @JvmStatic
        fun newInstance(argumentsBundle: Bundle) {
            //get the zipcode from the given bundle
            val zipCodeString = argumentsBundle.getString(ZIP_CODE_KEY, null)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            zipcodeString = it.getString(ZIP_CODE_KEY)
        }
    }



    override fun onSaveInstanceState(savedInstanceStateBundle: Bundle) {
        super.onSaveInstanceState(savedInstanceStateBundle)

        // store the View's contents into the Bundle
        savedInstanceStateBundle.putString(LOCATION_KEY,
                location.getText().toString())
        savedInstanceStateBundle.putString(TEMPERATURE_KEY,
                temperature.getText().toString())
        savedInstanceStateBundle.putString(HUMIDITY_KEY,
                humidity.getText().toString())
        savedInstanceStateBundle.putParcelable(IMAGE_KEY, conditionBitmap)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.single_forecast_layout, null)
        contxt = rootView.context

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //if there is no saved information
        if (savedInstanceState == null) {
            //hide the forecast and show the loading message
            forecast_layout.visibility = View.GONE
            loading_message.visibility = View.VISIBLE

            //load the forecast in a background thread
            ReadForecastTask(zipcodeString!!, weatherForecastListener,
                    location.context).execute()
        } else {
            // display information in the saved state Bundle using the
            // Fragment's Views
            forecast_image.setImageBitmap(savedInstanceState.getParcelable(IMAGE_KEY))
            location.text = savedInstanceState.getString(LOCATION_KEY)
            temperature.text = savedInstanceState.getString(TEMPERATURE_KEY)
            humidity.text = savedInstanceState.getString(HUMIDITY_KEY)
        }
    }

    //receives weather information from AsyncTask
    val weatherForecastListener = object : ReadForecastTask.ForecastListener {                          //ForecastListener fetches data from OpenWeatherMap API
        override fun onForecastLoaded(image: Bitmap?, temp: String?, humidityString: String?,
                                      cityString: String?,
                                      countryString: String?) {

            //if this fragment was detached while the background process ran
            if (!this@SingleForecastFragment.isAdded()) {
                return  //leave the method
            } else if (image == null) {
                val errorToast = Toast.makeText(context, context?.resources?.getString(R.string.null_data_toast),
                        Toast.LENGTH_LONG)
                errorToast.setGravity(Gravity.CENTER,0,0)
                errorToast.show()
                return  //exit before updating the forecast
            }

            val resources = this@SingleForecastFragment.resources

            // display the loaded information
            location.text = "${cityString} ${zipcodeString} ${countryString}"
            forecast_image.setImageBitmap(image)
            conditionBitmap = image
            temperature.text = "${temp}${0x00B0.toChar()}${resources.getString(R.string.temperature_unit)}"
            humidity.text = "${humidityString}${0x0025.toChar()}"
            loading_message.setVisibility(View.GONE) // hide loading message
            forecast_layout.setVisibility(View.VISIBLE) // show the forecast


            mRefreshLayout?.isRefreshing = false

        } //onForecastLoaded
    } //weatherForecastListener

    //access for the zipcode of this Fragment's forecast information
    fun getZipcode(): String {
        return zipcodeString!!
    }


}
