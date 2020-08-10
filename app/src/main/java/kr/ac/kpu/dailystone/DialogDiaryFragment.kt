package kr.ac.kpu.dailystone

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.Resources.getSystem
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.firebase.ui.auth.AuthUI.getApplicationContext
import com.firebase.ui.auth.data.model.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.dialog_diary.*
import java.lang.NumberFormatException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.pow
import kotlin.math.sqrt

@RequiresApi(Build.VERSION_CODES.O)
class DialogDiaryFragment(context: Context,date:String) : Dialog(context) {
    private var mAuth: FirebaseAuth? = null
    val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var db: DatabaseReference
    private var level : Any=""
    private var diary : Any=""
    private var emoticon : Any = 0
    private var diaryColor : Any = 0
    private lateinit var bitmap: Bitmap
    var cnt:Any = 0

    private val current: LocalDate = LocalDate.now()
    private var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    private var formatted: String = current.format(formatter)
    var date = date
    var year = formatted.substring(2,4)
    var monthformatted = formatted.substring(4,6)
    var dayformatted: String = formatted.substring(6,8)

    var user: FirebaseUser?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_diary)
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().currentUser

        db= Firebase.database.reference

        //date 받아오기
        year = date.substring(0,2)
        monthformatted = date.substring(2,4)
        dayformatted = date.substring(4,6)
        Toast.makeText(context, "date $date", Toast.LENGTH_SHORT).show()

        drawIv()
        ddEdHl.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                try {
                    var i: String? = ddEdHl.text.toString()
                    if (i == "") {
                        i = "0"
                    }
                    var str: Int? = i?.let { Integer.parseInt(it) }

                    if ((i!!.toInt() > 100) || (i!!.toInt() < 0)) {
                        Toast.makeText(context, "0부터 100까지의 숫자만 입력해주세요", Toast.LENGTH_SHORT).show()
                        ddEdHl.setText("0")
                    } else {
                        //ddSbar.progress = str!!
                    }
                }catch(e: NumberFormatException){
                    Toast.makeText(context, "숫자만 입력가능합니다", Toast.LENGTH_SHORT).show()
                    return
                }
                ddEdHl.setSelection(ddEdHl.length())

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
        })

        ddBtnYes.setOnClickListener {
            level = ddEdHl.text.toString()
            diary = ddEdDiary.text.toString()
            if(emoticon==0||diaryColor==0||level==""||diary==""){
                Toast.makeText(context,"다시 입력해주세요",Toast.LENGTH_SHORT).show()
            }else{
                onWriteDBPost()
                dismiss()
            }
        }
        ddBtnNo.setOnClickListener {
            dismiss()
        }
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n", "RestrictedApi")
    private fun drawIv(){
        ddIvbackground.isDrawingCacheEnabled = true
        ddIvbackground.buildDrawingCache()

        LinearDraw.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            val width = LinearDraw.width-ddIvicon.width
            val height = LinearDraw.height-ddIvicon.height

            var radius = (motionEvent.getX()-LinearDraw.width/2.toFloat() )*(motionEvent.getX()-LinearDraw.width/2.toFloat() ) +
                    (motionEvent.getY()-LinearDraw.height/2.toFloat() )*(motionEvent.getY()-LinearDraw.height/2.toFloat())

            var act = motionEvent.action
            when (act){
                MotionEvent.ACTION_MOVE, MotionEvent.ACTION_DOWN -> { //누르고 움직였을 때
                    if((width/2)*(width/2) >= radius){//원 안을 눌렀을 때
                        changeIconColor(motionEvent)
                        ddIvicon.x = motionEvent.getX() - ddIvicon.width/2
                        ddIvicon.y = motionEvent.getY() - ddIvicon.height/2

                        if(motionEvent.getY() <= LinearDraw.height/2.toFloat()) {
                            if((width / 12) * (width / 12) >= radius) {
                                emoticon = 0
                                ddIvicon.setImageResource(R.drawable.basic_level)
                            } else if ((width / 6) * (width / 6) >= radius) {
                                emoticon = 1
                                ddIvicon.setImageResource(R.drawable.happy_level1)
                            } else if ((width / 3) * (width / 3) >= radius) {
                                emoticon = 2
                                ddIvicon.setImageResource(R.drawable.happy_level2)
                            } else if ((width / 2) * (width / 2) >= radius) {
                                emoticon = 3
                                ddIvicon.setImageResource(R.drawable.happy_level3)
                            }
                        }else{
                            if((width / 12) * (width / 12) >= radius) {
                                emoticon = 0
                                ddIvicon.setImageResource(R.drawable.basic_level)
                            } else if ((width / 6) * (width / 6) >= radius) {
                                emoticon = 4
                                ddIvicon.setImageResource(R.drawable.sad_level1)
                            } else if ((width / 3) * (width / 3) >= radius) {
                                emoticon = 5
                                ddIvicon.setImageResource(R.drawable.sad_level2)
                            } else if ((width / 2) * (width / 2) >= radius) {
                                emoticon = 6
                                ddIvicon.setImageResource(R.drawable.sad_level3)
                            }
                        }

                    }else{//원 밖에 있을 때
                        val cx = LinearDraw.width/2.toFloat()
                        val cy = LinearDraw.height/2.toFloat()
                        var mx = motionEvent.getX()
                        var my = motionEvent.getY()
                        var middleX =
                            (width / 2) * (height / 2) * (1 / (1 + (my - cy) / (mx - cx) * (my - cy) / (mx - cx)))
                        var realX:Float
                        var realY:Float

                        if(mx >= cx) {
                            realX = sqrt(middleX) + cx
                            realY = (my - cy) / (mx - cx) * (realX - cx) + cy
                        }else {
                            realX = -sqrt(middleX) + cx
                            realY = (my - cy) / (mx - cx) * (realX - cx) + cy
                        }
                        if(motionEvent.getY() <= LinearDraw.height/2.toFloat()) {
                            emoticon=3
                            ddIvicon.setImageResource(R.drawable.happy_level3)
                        }else{
                            emoticon=6
                            ddIvicon.setImageResource(R.drawable.sad_level3)
                        }
                        changeIconColor(motionEvent)
                        ddIvicon.x = realX-ddIvicon.width/2
                        ddIvicon.y = realY-ddIvicon.height/2
                    }
                }
                MotionEvent.ACTION_UP -> { //누른걸 땠을 때
                    ddIvicon.x = LinearDraw.width/2.toFloat() - ddIvicon.width/2
                    ddIvicon.y = LinearDraw.height/2.toFloat() - ddIvicon.height/2
                }
            }
            return@OnTouchListener true
        })
    }

    private fun changeIconColor(motionEvent:MotionEvent){
        bitmap = ddIvbackground.drawingCache
        var r:Int
        var g:Int
        var b:Int
        if(motionEvent.x.toInt()<bitmap.width&&motionEvent.y.toInt()<bitmap.height&&motionEvent.y.toInt()>=0&&motionEvent.x.toInt()>=0){
            val pixel = bitmap.getPixel(motionEvent.x.toInt(),motionEvent.y.toInt())
            r= Color.red(pixel)
            g= Color.green(pixel)
            b= Color.blue(pixel)
            val hex = "#"+Integer.toHexString(pixel)
            ddIvicon.setColorFilter(Color.rgb(r,g,b))
            Log.d("color","r:$r, g:$g, b:$b")
            if(r==255 && g==0 && b==0){
                diaryColor=1
            }else if(r==255&&g==192&&b==0){
                diaryColor=2
            }else if(r==0&&g==176&&b==80){
                diaryColor=3
            }else if(r==0&&g==112&&b==192){
                diaryColor=4
            }else{
                diaryColor=0
            }
        }
    }

    private fun onWriteDBPost() {
        db = Firebase.database.reference
        var user = FirebaseAuth.getInstance().currentUser

        level = ddEdHl.text.toString()
        diary = ddEdDiary.text.toString()

        val postValues: HashMap<String, Any> = HashMap()
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("count").child(date).child("count").value == null) {
                    cnt = 0
                } else {
                    cnt = snapshot.child("count").child(date).child("count").value!!
                }

                val myRefDiary =
                    db.child(user!!.uid).child("diary").child(year).child(monthformatted)
                        .child(dayformatted)
                        .child((cnt.toString().toInt() + 1).toString())
                postValues["level"] = level
                postValues["diary"] = diary
                postValues["emoticon"] = emoticon
                postValues["color"] = diaryColor

                myRefDiary.setValue(postValues)

                val myRefCount = db.child(user!!.uid).child("count").child(date).child("count")
                myRefCount.setValue(cnt.toString().toInt() + 1)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        db.child(user!!.uid).addListenerForSingleValueEvent(postListener)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
class DialogDiaryFragmentModify(context: Context, date:String, val modifyCnt: String) : Dialog(context) {
    private var mAuth: FirebaseAuth? = null
    val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var db: DatabaseReference
    private lateinit var level : Any
    private lateinit var diary : Any
    var cnt:Any = 0

    private val current: LocalDate = LocalDate.now()

    private var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    private var formatted: String = current.format(formatter)
    var date = date
    var year = formatted.substring(2,4)
    var monthformatted = formatted.substring(4,6)
    var dayformatted: String = formatted.substring(6,8)

    var user: FirebaseUser?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_diary)
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().currentUser

        db= Firebase.database.reference

        //date 받아오기
        year = date.substring(0,2)
        monthformatted = date.substring(2,4)
        dayformatted = date.substring(4,6)
        Toast.makeText(context, "date $date", Toast.LENGTH_SHORT).show()

        //touch
        drawIv()


        ddBtnYes.setOnClickListener {
            onWriteDBPost()
            dismiss()
        }
        ddBtnNo.setOnClickListener {
            dismiss()
        }
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n", "RestrictedApi")
    private fun drawIv(){
        var levelX:Int=0
        var levelY:Int=0
        var tempx = ddIvicon.x


        LinearDraw.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            val width = LinearDraw.width
            val height = LinearDraw.height
            val iconx = LinearDraw.width/2.toFloat() - ddIvicon.width/2
            val icony = LinearDraw.height/2.toFloat() - ddIvicon.height/2

            var radius = (motionEvent.getX()-LinearDraw.width/2.toFloat() )*(motionEvent.getX()-LinearDraw.width/2.toFloat() ) +
                    (motionEvent.getY()-LinearDraw.height/2.toFloat() )*(motionEvent.getY()-LinearDraw.height/2.toFloat())

            var act = motionEvent.action
            when (act){
                MotionEvent.ACTION_DOWN -> { //처음 눌렀을 때
                    ddIvicon.x = motionEvent.getX() - ddIvicon.width/2
                    ddIvicon.y = motionEvent.getY() - ddIvicon.height/2
                }
                MotionEvent.ACTION_MOVE -> { //누르고 움직였을 때
                    if((width/2)*(width/2) >= radius){//원 안을 눌렀을 때
                        ddIvicon.x = motionEvent.getX() - ddIvicon.width/2
                        ddIvicon.y = motionEvent.getY() - ddIvicon.height/2

                        if(motionEvent.getY() <= LinearDraw.height/2.toFloat()) {
                            if((width / 12) * (width / 12) >= radius) {
                                ddIvicon.setImageResource(R.drawable.basic_level)
                            } else if ((width / 6) * (width / 6) >= radius) {
                                ddIvicon.setImageResource(R.drawable.happy_level3)
                            } else if ((width / 3) * (width / 3) >= radius) {
                                ddIvicon.setImageResource(R.drawable.happy_level2)
                            } else if ((width / 2) * (width / 2) >= radius) {
                                ddIvicon.setImageResource(R.drawable.happy_level1)
                            }
                            Log.d("position","플러스 ${ddIvicon.x}, ${ddIvicon.y}")
                        }else{if((width / 12) * (width / 12) >= radius) {
                            ddIvicon.setImageResource(R.drawable.dialogicon)
                        } else if ((width / 6) * (width / 6) >= radius) {
                            Log.d("position", "ㅠㅠ3")
                        } else if ((width / 3) * (width / 3) >= radius) {
                            Log.d("position", "ㅠㅠ2")

                        } else if ((width / 2) * (width / 2) >= radius) {
                            Log.d("position", "ㅠㅠ1")

                        }
                            Log.d("position","마이너스 ${ddIvicon.x}, ${ddIvicon.y}")
                        }
                    }else{//원 밖에 있을 때
                        val cx = LinearDraw.width/2.toFloat()
                        val cy = LinearDraw.height/2.toFloat()
                        var mx = motionEvent.getX()
                        var my = motionEvent.getY()
                        var middleX =
                            (width / 2) * (height / 2) * (1 / (1 + (my - cy) / (mx - cx) * (my - cy) / (mx - cx)))
                        if(motionEvent.getX() >= width/2) {
                            var realX = sqrt(middleX) + cx
                            var realY = (my - cy) / (mx - cx) * (realX - cx) + cy
                            ddIvicon.setImageResource(R.drawable.dialogicon)

                            if(motionEvent.getY()>=height/2){ //둘다양수
                                ddIvicon.x = realX-ddIvicon.width/2
                                ddIvicon.y = realY-ddIvicon.height/2
                            }else{//x양y음
                                ddIvicon.x = realX-ddIvicon.width/2
                                ddIvicon.y = realY-ddIvicon.height/2
                            }

                        }else {
                            var realX = -sqrt(middleX) + cx
                            var realY = (my - cy) / (mx - cx) * (realX - cx) + cy
                            ddIvicon.setImageResource(R.drawable.dialogicon)

                            if(motionEvent.getY()>=height/2){//x음y양
                                ddIvicon.x = realX-ddIvicon.width/2
                                ddIvicon.y = realY-ddIvicon.height/2
                            }else{//둘다음수
                                ddIvicon.x = realX-ddIvicon.width/2
                                ddIvicon.y = realY-ddIvicon.height/2
                            }
                        }
                    }
                }
                MotionEvent.ACTION_UP -> { //누른걸 땠을 때
                    ddIvicon.x = LinearDraw.width/2.toFloat() - ddIvicon.width/2
                    ddIvicon.y = LinearDraw.height/2.toFloat() - ddIvicon.height/2
                }

            }
            return@OnTouchListener true
        })



    }


    private fun onWriteDBPost() {
        db = Firebase.database.reference
        var user = FirebaseAuth.getInstance().currentUser
        level = ddEdHl.text.toString()
        diary = ddEdDiary.text.toString()


        val postValues: HashMap<String, Any> = HashMap()
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val myRefDiary =
                    db.child(user!!.uid).child("diary").child(year).child(monthformatted)
                        .child(dayformatted)
                        .child((modifyCnt).toString())
                postValues["level"] = level
                postValues["diary"] = diary
                myRefDiary.setValue(postValues)


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        db.child(user!!.uid).addListenerForSingleValueEvent(postListener)
    }
}