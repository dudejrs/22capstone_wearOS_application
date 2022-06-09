package com.example.capstone_wearos_applicaition

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.data.StockData
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.roundToInt


class StockListItemAdapter(val context : Context, private var stock_list : List<StockData>, val container_view : View) : RecyclerView.Adapter<StockListItemAdapter.CustomViewHolder>() {


    inner class StockOnClickListener : View.OnClickListener{

        private var isToggled = false

        override fun onClick(p0: View?) {
            isToggled = !(isToggled)

            if (isToggled){
                p0?.findViewById<LinearLayout>(R.id.stockCard1)!!.visibility = View.INVISIBLE
                p0?.findViewById<LinearLayout>(R.id.stockCard2)!!.visibility = View.VISIBLE

            }else{
                p0?.findViewById<LinearLayout>(R.id.stockCard1)!!.visibility = View.VISIBLE
                p0?.findViewById<LinearLayout>(R.id.stockCard2)!!.visibility = View.INVISIBLE
            }
        }
    }
    inner class StockDetailOnClickListener(index : Int) : View.OnClickListener{
        private var index: Int = 0

        init{
            this.index = index
        }

        override fun onClick(p0: View?) {
            val intent = Intent(context, StockDetailsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            intent.putExtra("stockNumber","${stock_list[index].stockNumber}")
            intent.putExtra("stockNumber",stock_list[index].stockNumber)
            Log.d("app:StockList","${stock_list[index].stockNumber}")
            context.startActivity(intent)
        }
    }

    private val custom_format = DecimalFormat("#,###")

    class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val stock_name = view.findViewById<TextView>(R.id.stockName)!!
        val stock_price = view.findViewById<TextView>(R.id.stockPrice)!!
        val stock_gap = view.findViewById<TextView>(R.id.gap)!!
        val stock_ratio = view.findViewById<TextView>(R.id.stockRatio)!!
        val up_down = view.findViewById<ImageView>(R.id.stockUpDown)!!
        val up_down2 = view.findViewById<ImageView>(R.id.stockUpDown2)!!
        val btn = view.findViewById<Button>(R.id.detail_button)!!
        val evaluated_price = view.findViewById<TextView>(R.id.evalutedPrice)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.stock_simple_card,parent,false)
        view.setOnClickListener (StockOnClickListener())
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val gap = stock_list[position].price - stock_list[position].bp
        holder.stock_name.text = stock_list[position].stockName
        holder.stock_price.text = custom_format.format(stock_list[position].price)
        holder.stock_gap.text = custom_format.format(abs(gap))
        holder.stock_ratio.text = "("+((abs(gap).toFloat()/ stock_list[position].bp.toFloat()*100).roundToInt()/100f).toString()+"%)"
        holder.up_down.apply{
            rotation = if (gap >= 0) 180.0f else 0.0f
            isVisible = (gap != 0)
        }
        holder.up_down2.apply{
            rotation = if (gap >= 0) 180.0f else 0.0f
            isVisible = (gap != 0)
        }
        holder.evaluated_price.text = custom_format.format(stock_list[position].price * stock_list[position].holdings)
        holder.btn.setOnClickListener(StockDetailOnClickListener(position))
    }

    override fun getItemCount() = stock_list.size
}