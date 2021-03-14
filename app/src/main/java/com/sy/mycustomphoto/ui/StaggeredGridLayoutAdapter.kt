package com.sy.mycustomphoto.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.sy.mycustomphoto.R
import kotlinx.android.synthetic.main.layout_recycler_item.view.*

class StaggeredGridLayoutAdapter(
    private val context: Context,
    private val listener: OnclickItemListener,
    data: ArrayList<MyPictureInfo> = arrayListOf()
) :
    RecyclerView.Adapter<StaggeredGridLayoutAdapter.VH>() {
    private var num = 0;
    private var _data: ArrayList<MyPictureInfo> = data


    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgView: ImageView = itemView.mImg
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(this.context).inflate(R.layout.layout_recycler_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return _data.size
    }


    interface OnclickItemListener {
        fun onClick(position: Int, path: String)
        fun onLongClick(position: Int,path: String)
    }


    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.imgView.run {
            setImageBitmap(_data[position].bm)
            setOnLongClickListener {
                listener.onLongClick(position, _data[position].path)
                return@setOnLongClickListener true
            }
            setOnClickListener {
                listener.onClick(position, _data[position].path)
            }
            if (position >= num) {
                num = position
                val animation = AnimationUtils.loadAnimation(context, R.anim.img_show_anim)
                this.animation = animation
                startAnimation(animation)
            }
        }
    }

    fun addItem(img: MyPictureInfo) {
        _data.add(img)
        notifyItemInserted(_data.size)
    }

    fun getPath(current: Int, step: Int): String {
        return _data[current + step].path
    }

}