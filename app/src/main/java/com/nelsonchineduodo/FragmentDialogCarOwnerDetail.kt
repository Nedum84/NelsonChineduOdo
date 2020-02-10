package com.nelsonchineduodo


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_dialog_car_owner_detail.*
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap


class FragmentDialogCarOwnerDetail: DialogFragment(){
    lateinit var thisContext: Activity
    lateinit var carOwnerDetails:CarOwnersClassBinder




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dialog_car_owner_detail, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        thisContext = activity!!

        closeDialogFrag.setOnClickListener {
            dialog!!.dismiss()
        }
        close_dialog.setOnClickListener {
            dialog!!.dismiss()
        }


        initializeData()
    }

    @SuppressLint("SetTextI18n")
    private fun initializeData(){
        carOwnerDetails = Gson().fromJson(ClassSharedPreferences(thisContext).getCarOwnerJSONDetails(), Array<CarOwnersClassBinder>::class.java).asList()[0]

        ownerNameFull.text = "${carOwnerDetails.first_name} ${carOwnerDetails.last_name} (${carOwnerDetails.car_model_year})"
        ownerBioFull.text = "${carOwnerDetails.bio}"
        ownerTitle.text = "${carOwnerDetails.first_name} ${carOwnerDetails.last_name}'s Bio:"

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStyle(STYLE_NO_TITLE, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
        } else {
            setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_NoActionBar)
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.Animation_WindowSlideUpDown
        isCancelable = true
        return dialog
    }
}
