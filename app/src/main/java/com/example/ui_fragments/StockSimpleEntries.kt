package com.example.ui_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
//import com.google.android.material.card.*
import com.example.capstone_wearos_applicaition.R

class StockSimpleEntries( private val name : String,
                          private val price : Int,
                          private val gap: Int,
                          private val ratio: Float) : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(
            R.layout.stock_simple_card,
            container,
            false
        )

        view.findViewById<TextView>(R.id.stockName).apply{
            text = name
        }
        view.findViewById<TextView>(R.id.stockPrice).apply{
            text = price.toString()
        }
        view.findViewById<TextView>(R.id.gap).apply{
            text = gap.toString()
        }
        view.findViewById<TextView>(R.id.stockRatio).apply{
            text = "($ratio%)"
        }
        view.findViewById<ImageView>(R.id.stockUpDown).apply{
            rotation = if (gap >= 0) 180.0f else 0.0f
            visibility = if (gap != 0) View.INVISIBLE else View.VISIBLE
        }

        return view
    }

}
