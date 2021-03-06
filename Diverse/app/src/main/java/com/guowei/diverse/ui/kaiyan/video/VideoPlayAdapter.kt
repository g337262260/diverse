package com.guowei.diverse.ui.kaiyan.video

import ImageLoader
import android.annotation.SuppressLint
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson
import com.guowei.diverse.R
import com.guowei.diverse.app.CommonViewHolder
import com.guowei.diverse.app.inflate
import com.guowei.diverse.databinding.ItemBeanForClientCardBinding
import com.guowei.diverse.databinding.ItemTheEndBinding
import com.guowei.diverse.databinding.ItemVideoSmallCardBinding
import com.guowei.diverse.model.eye.Data
import com.guowei.diverse.model.eye.Item
import com.guowei.diverse.model.eye.item.VideoSmallCard
import com.guowei.diverse.util.TimeUtil
import com.guowei.diverse.util.anim.TiaoZiUtil


/**
 * 文件： VideoPlayAdapter
 * 描述：
 * 作者： YangJunQuan   2018-9-17.
 */
class VideoPlayAdapter(private val mContext: Context) : RecyclerView.Adapter<CommonViewHolder>() {


    //data
    var mHeaderData: Data? = null
    var mRelatedData = ArrayList<Item>()

    //onItemClick
    lateinit var onItemClick: (position: Int) -> Unit


    enum class ItemType(var value: Int) {
        TYPE_HEADER(0),
        TYPE_RELATED(1),
        TYPE_THE_END(2);
    }


    fun setData(headData: Data, relatedData: List<Item>) {
        this.mHeaderData = headData
        this.mRelatedData = relatedData as ArrayList<Item>
        this.notifyDataSetChanged()
    }

    /**
     * ***********************************************    RecyclerView 方法  *************************************************
     */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        val viewHolder =
                when (viewType) {
                    ItemType.TYPE_HEADER.value -> parent.inflate<ItemBeanForClientCardBinding>(R.layout.item_bean_for_client_card)
                    ItemType.TYPE_RELATED.value -> parent.inflate<ItemVideoSmallCardBinding>(R.layout.item_video_small_card)
                    ItemType.TYPE_THE_END.value -> parent.inflate<ItemTheEndBinding>(R.layout.item_the_end)
                    else -> throw Exception("invalid view type")
                }
        return CommonViewHolder(viewHolder.itemView)
    }


    override fun getItemCount(): Int {
        return 1 + mRelatedData.size + 1
    }


    override fun getItemViewType(position: Int): Int {

        return when (position) {
            0 -> ItemType.TYPE_HEADER.value
            in 1..mRelatedData.size -> ItemType.TYPE_RELATED.value
            else -> ItemType.TYPE_THE_END.value
        }
    }

    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {

        when (getItemViewType(position)) {
            ItemType.TYPE_HEADER.value -> initHeaderView(holder, position)
            ItemType.TYPE_RELATED.value -> initRelatedView(holder, position)
            ItemType.TYPE_THE_END.value -> initTheEndView(holder, position)
        }
    }


    /**
     * *********************************************** 以下是初始化各种不同类型ItemView 的渲染方法 ************************************************
     */

    @SuppressLint("SetTextI18n")
    private fun initHeaderView(holder: CommonViewHolder, position: Int) {
        val itemBeanForClientCardBinding = DataBindingUtil.getBinding<ItemBeanForClientCardBinding>(holder.itemView)


        //视频标题、内容
        with(itemBeanForClientCardBinding!!) {
            tvVideoTitle.text = mHeaderData?.title
            tvCategory.text = "#${mHeaderData?.category}"
            tvVideoDescription.text = mHeaderData?.description


            tvCollectionCount.text = mHeaderData?.consumption?.collectionCount.toString()
            tvShare.text = mHeaderData?.consumption?.shareCount.toString()
            tvReply.text = mHeaderData?.consumption?.replyCount.toString()

            tagsContainer.removeAllViews()
            mHeaderData?.tags?.forEach { itemTag ->
                val itemView = LayoutInflater.from(mContext).inflate(R.layout.video_tag, tagsContainer, false)
                val ivTag = itemView.findViewById<ImageView>(R.id.ivTag)
                val tvTag = itemView.findViewById<TextView>(R.id.tvTag)

                tvTag.text = "#" + itemTag.name + "#"
                ImageLoader.loadNetImageWithCornerAndShade(mContext, ivTag, itemTag.headerImage, placeHolderId = R.drawable.corner_4_solid_dark_light)
                tagsContainer.addView(itemView)
            }


            tvAuthor.text = mHeaderData?.author?.name
            tvSlogan.text = mHeaderData?.author?.description
            ImageLoader.loadNetCircleImage(mContext, ivAvatar, mHeaderData?.author?.icon)
        }


        startAnimate(itemBeanForClientCardBinding)

    }

    private fun initRelatedView(holder: CommonViewHolder, position: Int) {
        val itemVideoSmallCardBinding = DataBindingUtil.getBinding<ItemVideoSmallCardBinding>(holder.itemView)


        //init data
        val jsonObject = mRelatedData[position - 1].data
        val videoSmallCard = Gson().fromJson(jsonObject, VideoSmallCard::class.java)

        val videoTitle = videoSmallCard.title                                                       //视频标题
        val videoFeedUrl = videoSmallCard.cover.detail                                              //视频封面Url
        val videoCategory = "#" + videoSmallCard?.category                                           //视频类别
        val videoDuration = TimeUtil.getFormatHMS(videoSmallCard.duration * 1000.toLong())       //视频时长



        with(itemVideoSmallCardBinding!!) {
            //init view
            tvTitle.text = videoTitle
            tvCatogory.text = videoCategory
            tvTitle.setTextColor(mContext.resources.getColor(R.color.white))
            tvCatogory.setTextColor(mContext.resources.getColor(R.color.white))


            tvVideoDuration.text = videoDuration
            ImageLoader.loadNetImageWithCorner(mContext, ivFeed, videoFeedUrl, placeHolderId = R.drawable.corner_4_solid_dark_light)


            //init Event
            holder.itemView.setOnClickListener {
                onItemClick.invoke(position - 1)    // -1 是由于Header的存在
            }
        }


    }


    //没有更多数据，到底部的提示ItemView
    private fun initTheEndView(holder: CommonViewHolder, position: Int) {
        val itemTheEndBinding = DataBindingUtil.getBinding<ItemTheEndBinding>(holder.itemView)!!

        with(itemTheEndBinding) {
            tvEnd.typeface = Typeface.createFromAsset(mContext.assets, "fonts/Lobster-1.4.otf")
            tvEnd.setTextColor(mContext.resources.getColor(R.color.white))
            root.setBackgroundColor(Color.TRANSPARENT)
        }
    }


    /**
     * ****************************************************  动画效果   *************************************************************
     */

    private fun startAnimate(binding: ItemBeanForClientCardBinding) {


        //init Animate
        val translationAnim = TranslateAnimation(0f, 0f, -80f, 0f)
        translationAnim.duration = 400

        with(binding) {
            binding.tvShare.startAnimation(translationAnim)
            binding.tvReply.startAnimation(translationAnim)
            binding.tvPreload.startAnimation(translationAnim)
            binding.tvCollectionCount.startAnimation(translationAnim)

            TiaoZiUtil(tvVideoTitle, mHeaderData?.title, 50)
        }

    }


}
