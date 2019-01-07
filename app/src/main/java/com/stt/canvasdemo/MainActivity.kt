package com.stt.canvasdemo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import android.graphics.PaintFlagsDrawFilter




class MainActivity : AppCompatActivity(),View.OnClickListener,View.OnTouchListener {

    lateinit var paint: Paint
    private var baseBitmap: Bitmap? = null
    var iv_canvas: ImageView? = null
    var btn_save: Button? = null
    var btn_resume: Button? = null
    private var canvas: Canvas? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 初始化一个画笔，笔触宽度为5，颜色为红色
        paint = Paint()
        paint.strokeWidth = 9.0f;
        paint.color = Color.RED;
        paint.isAntiAlias = true
        iv_canvas = findViewById(R.id.iv_canvas)
        btn_save = findViewById<Button>(R.id.btn_save)
        btn_resume = findViewById<Button>(R.id.btn_resume)
        btn_save?.setOnClickListener(this)
        btn_resume?.setOnClickListener(this)
        iv_canvas?.setOnTouchListener(this)
        var people = People()
        people.name;
    }
    override fun onClick(v: View?) {
        when(v){
            btn_save -> {
                Toast.makeText(this, "save", Toast.LENGTH_SHORT).show()
                saveBitmap();
            }
            btn_resume->{
                Toast.makeText(this, "resume", Toast.LENGTH_SHORT).show()
                resumeCanvas();
            }
        }
    }

    var startX: Float = 0f
    var startY: Float = 0f
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when(event?.getAction()){
            MotionEvent.ACTION_DOWN -> {
                if (baseBitmap == null) {
                    baseBitmap = Bitmap.createBitmap(iv_canvas!!.getWidth(),
                            iv_canvas!!.getHeight(), Bitmap.Config.ARGB_8888);
                    canvas = Canvas(baseBitmap);
                    canvas?.drawFilter = PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
                    canvas?.drawColor(Color.WHITE);
                }
                // 记录开始触摸的点的坐标
                startX = event.getX();
                startY = event.getY();
            }
            MotionEvent.ACTION_MOVE -> {
                // 记录移动位置的点的坐标
                val stopX = event.x
                val stopY = event.y

                //根据两点坐标，绘制连线
                canvas?.drawLine(startX, startY, stopX, stopY, paint)

                // 更新开始点的位置
                startX = event.x
                startY = event.y

                // 把图片展示到ImageView中
                iv_canvas?.setImageBitmap(baseBitmap)
            }
        }
        return true
    }

    protected fun saveBitmap() {
        try {
            // 保存图片到SD卡上
            val file = File(getExternalStorageDirectory(),System.currentTimeMillis().toString() + ".png")
            val stream = FileOutputStream(file)
            baseBitmap?.compress(CompressFormat.PNG, 100, stream)
            Toast.makeText(this@MainActivity, "保存图片成功", Toast.LENGTH_SHORT).show()

            // Android设备Gallery应用只会在启动的时候扫描系统文件夹
            // 这里模拟一个媒体装载的广播，用于使保存的图片可以在Gallery中查看
            val intent = Intent()
            intent.action = Intent.ACTION_MEDIA_MOUNTED
            intent.data = Uri.fromFile(Environment
                    .getExternalStorageDirectory())
            sendBroadcast(intent)
        } catch (e: Exception) {
            Toast.makeText(this@MainActivity, "保存图片失败", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }

    /**
     * 清除画板
     */
    protected fun resumeCanvas() {
        // 手动清除画板的绘图，重新创建一个画板
        if (baseBitmap != null) {
            baseBitmap = Bitmap.createBitmap(iv_canvas!!.getWidth(),
                    iv_canvas!!.getHeight(), Bitmap.Config.ARGB_8888)
            canvas = Canvas(baseBitmap)
            canvas?.drawColor(Color.WHITE)
            iv_canvas?.setImageBitmap(baseBitmap)
            Toast.makeText(this@MainActivity, "清除画板成功，可以重新开始绘图", Toast.LENGTH_LONG).show()
        }
    }
}
