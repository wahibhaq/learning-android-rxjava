package com.learning.rxjava.introtorxtutorials

import android.util.Log
import io.reactivex.functions.Consumer

class DisplayConsumer(val name: String) : Consumer<Any> {

    val TAG = "BaseRx"

    override fun accept(t: Any) {
        Log.i(TAG, name+ " : " + t)
    }
}