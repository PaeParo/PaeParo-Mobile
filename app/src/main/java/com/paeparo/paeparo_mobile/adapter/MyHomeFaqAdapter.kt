package com.paeparo.paeparo_mobile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.databinding.ItemMyhomeFaqBinding


class MyHomeFaqAdapter:RecyclerView.Adapter<MyHomeFaqAdapter.MyHomeFaqViewHolder>() {
    //제목 to listOf(세부내용)
    val data = mapOf(
        "회원탈퇴를 하고싶어요" to listOf("내 정보 -> 설정 -> 회원탈퇴 클릭 후 회원탈퇴를 진행해주세요."),
    )
    inner class MyHomeFaqViewHolder(private val binding: ItemMyhomeFaqBinding):RecyclerView.ViewHolder(binding.root){
        init {
            binding.faqCardName.setOnClickListener{
                if(binding.layoutDetail01.visibility== View.VISIBLE){
                    binding.layoutDetail01.visibility = View.GONE
                    binding.faqButton.animate().apply {
                        duration =200
                        rotation(0f)
                    }
                }else{
                    binding.layoutDetail01.visibility = View.VISIBLE
                    binding.faqButton.animate().apply {
                        duration = 200
                        rotation(180f)
                    }
                }
            }
        }
        fun bind(position: Int){
            binding.faqName.text = data.keys.elementAt(position)
            data.values.elementAt(position).forEach{
                val view = TextView(binding.root.context).apply {
                    text = it
                    textSize = 20f
                    setPadding(10,10,5,10)
                }
                binding.layoutDetail01.addView(view)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHomeFaqViewHolder {
        val view = ItemMyhomeFaqBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyHomeFaqViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyHomeFaqViewHolder, position: Int) {
        holder.bind(position)
    }
}