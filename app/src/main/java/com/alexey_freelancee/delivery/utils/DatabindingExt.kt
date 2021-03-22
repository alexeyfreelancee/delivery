package com.alexey_freelancee.delivery.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("date")
fun setDate(view: TextView, date:Long?){
    if(date!=null){

        val sdf = SimpleDateFormat("dd.MM HH:ss",Locale.getDefault())
        view.text= sdf.format(Date(date))
    }
}


@BindingAdapter("weight")
fun setDate(view: TextView, weight:Double?){
    if(weight!=null){
        view.text="$weight кг"
    }
}