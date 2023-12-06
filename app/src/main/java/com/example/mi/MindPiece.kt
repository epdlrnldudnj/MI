package com.example.mi

import android.app.Application

class MindPiece : Application() {
    var mindpiece:Int = 0
    fun addpiece(how: Int){
        mindpiece += how
    }
    fun minuspiece(how: Int){
        mindpiece -= how
    }
}