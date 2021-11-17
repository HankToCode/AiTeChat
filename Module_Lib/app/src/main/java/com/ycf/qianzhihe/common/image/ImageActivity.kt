package com.ycf.qianzhihe.common.image

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.luck.picture.lib.photoview.PhotoView
import com.ycf.qianzhihe.R
import com.ycf.qianzhihe.app.base.BaseInitActivity
import com.ycf.qianzhihe.app.domain.EaseShowBigImageNewItem
import com.ycf.qianzhihe.app.weight.HackyViewPager
import com.zds.base.ImageLoad.GlideUtils
import com.zds.base.util.FilePathUtils
import kotlinx.android.synthetic.main.activity_img.*
import java.io.File

/**
 * 3.1.1保存图片到相册 3.2.1查看图片 3.2.1.1保存图片
 **/
class ImageActivity : BaseInitActivity(), View.OnClickListener, ViewPager.OnPageChangeListener, View.OnLongClickListener, HintDialog.HintDialogCallBack {

    private lateinit var mHintDialog: HintDialog
    private var mViewPager: ViewPager? = null
    private var imgUrls: ArrayList<EaseShowBigImageNewItem>? = null
    private var index: Int = 0

    override fun getLayoutId(): Int = R.layout.activity_img

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        mViewPager = findViewById<View>(R.id.viewPager) as HackyViewPager
        setUpView()

        mHintDialog = HintDialog(this)
        mHintDialog.setHint("是否保存图片到本地？")
        mHintDialog.setButtonTextLeftRight("否", "是")
        mHintDialog.setHintDialogCallBack(this)
    }

    private fun initView() {
    }

    private fun setUpView() {
        index = intent.getIntExtra(EXTRA_POSITION, 0)
        imgUrls = intent.getParcelableArrayListExtra(EXTRA_IMG)
        tv_postion.text = "${index + 1}/${imgUrls?.size}"
        stSave.setOnClickListener { permissionSaveImage() }

        mViewPager?.let {
            it.adapter = SamplePagerAdapter()
            it.addOnPageChangeListener(this)
            // 设置初始位置
            it.currentItem = index
        }
    }

    private inner class SamplePagerAdapter : PagerAdapter() {
        override fun getCount(): Int {
            return if (imgUrls == null) 0 else imgUrls!!.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val photoView = PhotoView(container.context)
            photoView.setOnClickListener(this@ImageActivity)

            GlideUtils.loadImageViewLoding(imgUrls?.get(position)?.remoteUrl ?: "", photoView)
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)

            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }
    }


    override fun onClick(v: View) {
        finish()
    }

    override fun onLongClick(v: View?): Boolean {
//        mHintDialog.show()
        return true
    }

    override fun hintDialogCallBack(dialog: Dialog, onClick: Int) {
        if (onClick == HintDialog.ONCLICK_RIGHT) {
            permissionSaveImage()
        }
    }


    private fun permissionSaveImage() {
        PermissionUtils.permission(PermissionConstants.STORAGE, PermissionConstants.CAMERA)
                .callback(object : PermissionUtils.SimpleCallback {
                    override fun onGranted() {
                        saveImg()
                    }

                    override fun onDenied() {
                        ToastUtils.showLong("请打开读写手机存储和相机权限")

                    }

                })
                .rationale { shouldRequest -> shouldRequest.again(true) }
                .request()
    }

    private fun saveImg() {
        val dir = FilePathUtils.getSavePhotoFilePath()
        val path = "$dir${EncryptUtils.encryptMD5ToString(imgUrls?.get(index)?.remoteUrl)}.jpg"
        if (FileUtils.createOrExistsDir(dir)) {
            val requestManager = Glide.with(this)
            val requestBuilder = requestManager.downloadOnly()
            requestBuilder.load(imgUrls?.get(index)?.remoteUrl)
            requestBuilder.listener(object : RequestListener<File> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<File>?, isFirstResource: Boolean): Boolean {
                    ToastUtils.showLong("保存失败")
                    return false
                }

                override fun onResourceReady(resource: File?, model: Any?, target: com.bumptech.glide.request.target.Target<File>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    if (resource != null) {
                        val file = File(path)
                        FileUtils.copyFile(resource, file)
//                        MediaStore.Images.Media.insertImage(contentResolver,file.path,file.name,null)
                        //通知相册更新
                        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.absolutePath))
                        intent.data = Uri.fromFile(file)
                        sendBroadcast(intent)

                        ToastUtils.showLong("保存成功")
                    } else {
                        ToastUtils.showLong("保存失败")
                    }
                    return false
                }
            })
            requestBuilder.preload()
        }
    }

    override fun onPageScrollStateChanged(arg0: Int) {

    }

    override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {

    }

    override fun onPageSelected(poisition: Int) {
        tv_postion.text = "${poisition + 1}/${imgUrls?.size}"
        index = poisition
    }

    override fun onDestroy() {
        Glide.get(this).clearMemory()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_POSITION = "position"
        const val EXTRA_IMG = "img"

        fun actionStart(context: Context, position: Int, pics: ArrayList<EaseShowBigImageNewItem>) {
            val intent = Intent(context, ImageActivity::class.java).apply {
                putExtra(EXTRA_POSITION, position)
                putParcelableArrayListExtra(EXTRA_IMG, pics)
            }
            context.startActivity(intent)
        }
    }
}