package com.nelsonchineduodo

import android.content.Context
import java.util.HashSet

class  ClassSharedPreferences(val context: Context?){
    private val PREFERENCE_NAME = "us_car_owners_preference"


    private val PREFERENCE_CAR_FILTERS_JSON_DETAILS = "car_filters_json_details"
    private val PREFERENCE_CAR_OWNER_JSON_DETAILS = "car_owner_json_detail"

    private val preference = context?.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)!!


    //set  set Car Filter JSON Details arrays in JSON
    fun setCarFilterJSONDetails(data:String){
        val editor = preference.edit()
        editor.putString(PREFERENCE_CAR_FILTERS_JSON_DETAILS,data)
        editor.apply()
    }
    fun getCarFilterJSONDetails():String?{
        return  preference.getString(PREFERENCE_CAR_FILTERS_JSON_DETAILS,"")
    }

    //set  set Car owner arrays in JSON
    fun setCarOwnerJSONDetails(data:String){
        val editor = preference.edit()
        editor.putString(PREFERENCE_CAR_OWNER_JSON_DETAILS,data)
        editor.apply()
    }
    fun getCarOwnerJSONDetails():String?{
        return  preference.getString(PREFERENCE_CAR_OWNER_JSON_DETAILS,"")
    }

}