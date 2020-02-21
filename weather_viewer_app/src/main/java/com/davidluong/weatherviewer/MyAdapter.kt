package com.davidluong.weatherviewer

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.w3c.dom.Text
import android.widget.Toast
import android.content.Context

//REVIEW: Adapter is where all the data is located. The data is passed to the RecyclerView
class MyAdapter(favoriteCities: MutableList<String>, passedContext: Context): RecyclerView.Adapter<MyAdapter.ViewHolder>() {  //passedContext is needed to display the Toast

    companion object {
        //which view is being worked on
        private val TYPE_HEADER = 0
        private val TYPE_ITEM = 1
    }

    private var mCities = mutableListOf<String>() //stores the titles passed in
    private val name = "Weather Viewer App"
    private val link = "http://openweathermap.org"
    private val circleView = R.drawable.weather_pic

    private var context: Context? = null


    init {
        mCities = favoriteCities
        context = passedContext

    } //init() - initializes the parameters in the class constructor

    inner class ViewHolder(itemView: View, viewType: Int, c: Context):
            RecyclerView.ViewHolder(itemView),
            View.OnClickListener { //populates the view with the data

        var holderId: Int

        //for item/row view
        var textView: TextView? = null
        var imageView: ImageView? = null

        //for header row
        var circleView: ImageView? = null
        var name: TextView? = null
        var link: TextView? = null

        var contxt: Context? = null

        init {
            contxt = c

            //Comment (line 57-58) for the alternative in "WeatherViewer.kt" file to work
             /*
             itemView.isClickable = true
             itemView.setOnClickListener(this)
             */


            //set the appropriate view according to the type
            if(viewType == TYPE_ITEM) {
                //create objects from 'item_row.xml'
//                textView = itemView.findViewById<TextView>(R.id.rowText)
//                imageView = itemView.findViewById<ImageView>(R.id.rowIcon)

                textView = itemView.findViewById(R.id.rowText)
                imageView = itemView.findViewById(R.id.rowIcon)

                //set the holderId
                holderId = TYPE_ITEM

            } else {
                //it's a header view
//                name = itemView.findViewById<TextView>(R.id.name)
//                link = itemView.findViewById<TextView>(R.id.link)

                name = itemView.findViewById(R.id.name)
                link = itemView.findViewById(R.id.link)
                circleView = itemView.findViewById(R.id.circleView)
                holderId = TYPE_HEADER
            }

        } //init()


        override fun onClick(view: View?) {
            Toast.makeText(contxt,
                    "The layout position clicked is: ${layoutPosition} Adapter Position: ${adapterPosition} ",
                    Toast.LENGTH_LONG).show()

        } //onClick()

    } //inner class ViewHolder()

    /*
    * METHODS
    */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {   //decides which view to create, then inflate it
        var vHolder: ViewHolder
        if(viewType == TYPE_ITEM) {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)

            //create the ViewHolder assign the object of type view
            vHolder = ViewHolder(v, viewType, context!!)

        } else {
            //type header
            val v = LayoutInflater.from(parent.context).inflate(R.layout.header, parent, false)

            //create the ViewHolder assign the object of type view
            vHolder = ViewHolder(v, viewType, context!!)
        }

        return vHolder

    } //onCreateViewHolder()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //super.onBindViewHolder(holder, position, payloads)

        if(holder.holderId == TYPE_ITEM) {
            //decrement by 1 because 0 indicates the header view, and the first row of the data list starts at 1
            holder.textView?.text = mCities.get(position-1)

            //using static image (normally would be dynamic)
            holder.imageView?.setImageResource(R.drawable.ic_generic_image)

        } else {
            //being static here, but could be dynamic
            holder.circleView?.setImageResource(circleView)
            holder.name?.text = name
            holder.link?.text = link
        }

    } //onBindViewHolder()

    override fun getItemCount(): Int {
        return mCities.size + 1  // increment by 1 for the header row

    } //getItemCount()

    override fun getItemViewType(position: Int): Int {
        if( isPositionHeader(position) ) {
            return TYPE_HEADER
        } else {
            return TYPE_ITEM
        }

    } //getItemViewType()

    private fun isPositionHeader(position: Int): Boolean {
        return position == TYPE_HEADER

    } //isPositionHeader()

    fun addCity(newCity: String) {
        mCities.add(newCity)
        mCities.sort()

    } //addCity()

    fun getCity(position: Int): String {
        return mCities.get(position-1)
    }

}