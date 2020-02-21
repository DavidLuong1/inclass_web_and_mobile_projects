// AddCityDialogFragment.kt
// DialogFragment allowing the user to enter a new city's zipcode.

package com.davidluong.weatherviewer

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import com.davidluong.weatherviewer.R
import kotlinx.android.synthetic.main.add_city_dialog.*

class AddCityDialogFragment : DialogFragment(), OnClickListener {

    // listens for results from the AddCityDialog
    interface DialogFinishedListener {
        // called when the AddCityDialog is dismissed
        fun onDialogFinished(zipcodeString: String, preferred: Boolean)
    } // end interface DialogFinishedListener

    // initializes a new DialogFragment
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        // allow the user to exit using the back key
        this.isCancelable = true
    } // end method onCreate

    // inflates the DialogFragment's layout
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              argumentsBundle: Bundle?): View? {
        // inflate the layout defined in add_city_dialog.xml
        val rootView = inflater.inflate(R.layout.add_city_dialog, container,
                false)

        if (argumentsBundle != null)
        // if the arguments Bundle isn't empty
        {
            add_city_edit_text.setText(argumentsBundle.getString(
                    resources.getString(
                            R.string.add_city_dialog_bundle_key)))
        } // end if

        // set the DialogFragment's title
        dialog.setTitle(R.string.add_city_dialog_title)

        // initialize the positive Button
        val okButton = rootView.findViewById(
                R.id.add_city_button) as Button
        okButton.setOnClickListener(this)
        return rootView // return the Fragment's root View
    } // end method onCreateView

    // save this DialogFragment's state
    override fun onSaveInstanceState(argumentsBundle: Bundle) {
        // add the EditText's text to the arguments Bundle
        argumentsBundle.putCharSequence(resources.getString(
                R.string.add_city_dialog_bundle_key),
                add_city_edit_text.text.toString())
        super.onSaveInstanceState(argumentsBundle)
    } // end method onSaveInstanceState

    // called when the Add City Button is clicked
    override fun onClick(clickedView: View) {
        if (clickedView.id == R.id.add_city_button) {
            val listener = activity as DialogFinishedListener
            listener.onDialogFinished(add_city_edit_text.text.toString(),
                    add_city_checkbox.isChecked)
            dismiss() // dismiss the DialogFragment
        } // end if
    } // end method onClick
} // end class AddCityDialogFragment