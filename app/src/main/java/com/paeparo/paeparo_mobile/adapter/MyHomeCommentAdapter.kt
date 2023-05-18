package com.paeparo.paeparo_mobile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.TestMyHomeComment
import com.paeparo.paeparo_mobile.databinding.ItemMyhomeCommentBinding
import timber.log.Timber

class MyHomeCommentAdapter(var datas:MutableList<TestMyHomeComment>):RecyclerView.Adapter<MyHomeCommentAdapter.MyHomeCommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHomeCommentViewHolder {
        val binding = ItemMyhomeCommentBinding.inflate(LayoutInflater.from(parent.context),parent,false,)
        return MyHomeCommentViewHolder(binding).also { holder->
            with(binding){
                commentView.setOnClickListener {
                    Timber.d("commentView Click")
                    /*
                    val intent = Intent(it.context, activity)

                    해당 아이템 클릭시 액티비티를 하나 생성
                    intent.putExtra()

                    startActivity(it.context, intent, null)
                    */
                }
            }
        }
    }

    override fun onBindViewHolder(holder: MyHomeCommentViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    class MyHomeCommentViewHolder(private  val binding: ItemMyhomeCommentBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(position: TestMyHomeComment){
            with(binding){
                commentTitle.text=position.commentTitle
                comment.text=position.comment
            }
        }
    }
}