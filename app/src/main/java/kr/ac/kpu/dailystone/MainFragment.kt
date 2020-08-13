package kr.ac.kpu.dailystone

import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_main.*
import kr.ac.kpu.dailystone.MonthDetailFragment.Companion.TAG
import org.w3c.dom.Text
import kotlinx.android.synthetic.main.fragment_main.view.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
class MainFragment : Fragment() {
    companion object { // 상수 역할
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private var mAuth: FirebaseAuth? = null //auth
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var db: DatabaseReference

    //date
    private val current: LocalDate = LocalDate.now()
    private var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    private var formatted: String = current.format(formatter)
    var date = formatted.substring(2, 8)
    var year = formatted.substring(2, 4)
    var monthformatted = formatted.substring(4, 6)
    var dayformatted: String = formatted.substring(6, 8)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance();
        db = Firebase.database.reference
        readCount()
        goalCount(date)
        dailyGoal(date)
        mainTvDate.text = date

        mainBtnHappy.setOnClickListener {
            var dialog = DialogDiaryFragment(it.context,date)
            dialog.show()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainBtnPre.setOnClickListener {
            preDate()
        }
        mainBtnNext.setOnClickListener {
            nextDate()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)
        readDayDiary(rootView)

        return rootView
    }

    private fun readDayDiary(rootView:View){
        db = Firebase.database.reference
        var user = FirebaseAuth.getInstance().currentUser
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rootView.mainGrid.removeAllViews()
                val childCount = snapshot.childrenCount.toInt()
                Log.d("test","childCount : $childCount")
                for (i in 1 until childCount + 1) {
                    if (snapshot.child("$i").hasChildren()) {
                        val gv: GridLayout = rootView.findViewById(R.id.mainGrid)
                        var width = Resources.getSystem().displayMetrics.widthPixels
                        var devicewidth = (width - 128) / 5
                        val iv = ImageView(context)

                        matchImageViewColor(iv, snapshot.child("$i").child("color").value.toString())
                        matchImageViewResource(iv, snapshot.child("$i").child("emoticon").value.toString())

                        val params = LinearLayout.LayoutParams(devicewidth, devicewidth)
                        iv.layoutParams = params
                        gv.addView(iv)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        db.child(user!!.uid).child("diary").child(year).child(monthformatted).child(dayformatted)
            .addValueEventListener(postListener)
    }

    private fun matchImageViewColor(view : ImageView, i : String){
        when(i){
            "1" -> {view.setColorFilter(Color.rgb(255,0,0))}
            "2" -> {view.setColorFilter(Color.rgb(255,192,0))}
            "3" -> {view.setColorFilter(Color.rgb(0, 176,80))}
            "4" -> {view.setColorFilter(Color.rgb(0,112,192))}
            "0" -> {view.setColorFilter(Color.rgb(0,112,192))}
        }
    }

    private fun matchImageViewResource(view : ImageView, i : String){
        when(i){
            "0" -> {view.setImageResource(R.drawable.basic_level) }
            "1" -> {view.setImageResource(R.drawable.happy_level1) }
            "2" -> {view.setImageResource(R.drawable.happy_level2) }
            "3" -> {view.setImageResource(R.drawable.happy_level3)}
            "4" -> {view.setImageResource(R.drawable.sad_level1) }
            "5" -> {view.setImageResource(R.drawable.sad_level2)}
            "6" -> {view.setImageResource(R.drawable.sad_level3)}
        }
    }

    private fun readCount() {
        var user = FirebaseAuth.getInstance().currentUser
        var dcnt: Any = 0

        val dayListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == null) {
                    dcnt = 0
                    mainPbDay.progress = 0
                } else {
                    dcnt = snapshot.value!!
                    //일별 평균 level 출력
                    readLevel(dcnt.toString().toInt())
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        db.child(user!!.uid).child("count").child(date).child("count")
            .addValueEventListener(dayListener)
    }

    private fun readLevel(dcnt: Int) {
        var user = FirebaseAuth.getInstance().currentUser
        var day: Int = 50
        var dayList = mutableListOf<Int>()
        var value = 0

        val dayListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var level: Any = 0
                for (i in 0 until dcnt) {
                    if (snapshot.child((i + 1).toString()).child("level").value != null) {
                        level = snapshot.child((i + 1).toString()).child("level").value!!
                        dayList.add(i, level.toString().toInt())
                    }
                }
                day = dayList.average().toInt()
                value = day
                mainPbDay.progress = value
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        db.child(user!!.uid).child("diary").child(year).child(monthformatted)
            .child(dayformatted).addListenerForSingleValueEvent(dayListener)

    }
    private fun goalCount(date:String) {
        var user = FirebaseAuth.getInstance().currentUser
        var gSum: Int = 0
        var gDate = date.substring(0,4)
        val goalListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in 1..31) {
                    if (i < 10) {
                        if (snapshot.child(gDate + "0$i")
                                .child("count").value == null
                        ) {
                        } else {
                            gSum += snapshot.child(gDate+ "0$i")
                                .child("count").value.toString().toInt()
                        }
                    } else {
                        if (snapshot.child(gDate + "$i")
                                .child("count").value == null
                        ) {

                        } else {
                            gSum += snapshot.child(gDate+ "$i")
                                .child("count").value.toString().toInt()
                        }
                    }
                }
                monthGoal(gSum,date)
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        db.child(user!!.uid).child("count")
            .addValueEventListener(goalListener)
    }

    private fun monthGoal(gSum: Int, date : String) {
        var user = FirebaseAuth.getInstance().currentUser
        var Maxgoal: Int = 0
        //val mainTvDate : TextView = requireView().findViewById(R.id.mainTvDate)
        val mainPbMgoal : ProgressBar = requireView().findViewById(R.id.mainPbMgoal)
        val mainPbMgoal2 : ProgressBar = requireView().findViewById(R.id.mainPbMgoal2)
        val goalListener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
               // var goalYear = mainTvDate.text.toString().substring(0, 2)
               //var goalMonth = mainTvDate.text.toString().substring(2, 4)
                var goalYear = date.substring(0,2)
                var goalMonth = date.substring(2,4)

                if (snapshot.child("goal").child(goalYear).child(goalMonth)
                        .child("goal").value == null
                ) {

                    mainPbMgoal.visibility = View.INVISIBLE
                    mainPbMgoal2.visibility = View.INVISIBLE
                } else {
                    mainPbMgoal.visibility = View.VISIBLE
                    mainPbMgoal2.visibility = View.VISIBLE
                    Maxgoal =
                        snapshot.child("goal").child(goalYear).child(goalMonth)
                            .child("goal").value!!.toString().toInt()
                }

                mainPbMgoal.max = Maxgoal.toString().toInt()
                mainPbMgoal2.max = Maxgoal.toString().toInt()
                if (gSum <= Maxgoal) {
                    mainPbMgoal.progress = gSum
                    mainPbMgoal2.progress = 0
                    Log.d("pb", "1 gsum : $gSum, MaxGoal : $Maxgoal")
                } else if ((gSum - Maxgoal) == Maxgoal) {
                        mainPbMgoal.progress = Maxgoal
                        mainPbMgoal2.progress = Maxgoal
                        Log.d(
                            "pb", "3 gs" +
                                    "um : $gSum, MaxGoal : $Maxgoal, pr1 : ${mainPbMgoal.progress} ,pr2 : ${mainPbMgoal2.progress}"
                        )
                    } else {
                    /* val progress2: Drawable = mainPbMgoal.progressDrawable.mutate()
                     progress2.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                     mainPbMgoal.progressDrawable = progress2*/

                    Log.d("pb", "2 gsum : $gSum, MaxGoal : $Maxgoal ,pr1 : ${mainPbMgoal.progress} , pr2 : ${mainPbMgoal2.progress}")
                    if ((gSum - Maxgoal) > Maxgoal) {
                    }
                    else{
                        mainPbMgoal.progress = Maxgoal
                        mainPbMgoal2.progress = gSum - Maxgoal
                        Log.d("pb", "4 gsum : $gSum, MaxGoal : $Maxgoal ,pr1 : ${mainPbMgoal.progress} , pr2 : ${mainPbMgoal2.progress}")
                    }
                }

                Log.d(
                    "Min: ", "snapshot : ${snapshot.child("goal")
                        .child(goalYear).value}"
                )
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        db.child(user!!.uid).addValueEventListener(goalListener)
    }

    private fun dailyGoal(date:String) {
        var user = FirebaseAuth.getInstance().currentUser
        var MaxDay: Int = 1
        var Dgoal: Int = 0
        val mainPbDgoal : ProgressBar = requireView().findViewById(R.id.mainPbDgoal)
        val mainPbDgoal2 : ProgressBar = requireView().findViewById(R.id.mainPbDgoal2)
        mainPbDgoal.max = MaxDay
        Log.d("vs", "1 Dgoal : $Dgoal, pr1 : ${mainPbDgoal.progress}, pr2 : ${mainPbDgoal2.progress}")
        val DgoalListener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.child("count").child(date).child("count").value == null) {

                } else {
                    Dgoal =
                        snapshot.child("count").child(date).child("count").value.toString().toInt()
                    Log.d("vs", "2 Dgoal : $Dgoal, pr1 : ${mainPbDgoal.progress}, pr2 : ${mainPbDgoal2.progress}")
                }
                if (Dgoal <= 1) {
                    mainPbDgoal.progress = Dgoal
                    mainPbDgoal2.progress = 0
                    Log.d("vs", "3 Dgoal : $Dgoal, pr1 : ${mainPbDgoal.progress}, pr2 : ${mainPbDgoal2.progress}")

                } else {
                    mainPbDgoal.progress = MaxDay
                    mainPbDgoal2.progress = Dgoal - MaxDay
                    Log.d("vs", "4 Dgoal : $Dgoal, pr1 : ${mainPbDgoal.progress}, pr2 : ${mainPbDgoal2.progress}")
                }

            }

        }
        db.child(user!!.uid).addValueEventListener(DgoalListener)
    }
    private fun preDate() {//이전 날짜 조회

        var ld: LocalDate = LocalDate.of(year.toInt(), monthformatted.toInt(), dayformatted.toInt())
        var minusDayNow = ld.plusDays(-1)
        var formatted2 = minusDayNow.format(formatter)
        date = formatted2.substring(2, 8)
        year = formatted2.substring(2, 4)
        monthformatted = formatted2.substring(4, 6)
        dayformatted = formatted2.substring(6, 8)
        mainTvDate.text = date
        var ft : FragmentTransaction? = fragmentManager?.beginTransaction()
        ft?.detach(this)?.attach(this)?.commit()
        readCount()
        goalCount(date)
        dailyGoal(date)
    }

    private fun nextDate() {//다음 날짜 조회
        var ld: LocalDate = LocalDate.of(year.toInt(), monthformatted.toInt(), dayformatted.toInt())
        var plusDayNow = ld.plusDays(1)
        var formatted2 = plusDayNow.format(formatter)
        date = formatted2.substring(2, 8)
        year = formatted2.substring(2, 4)
        monthformatted = formatted2.substring(4, 6)
        dayformatted = formatted2.substring(6, 8)
        mainTvDate.text = date
        var ft : FragmentTransaction? = fragmentManager?.beginTransaction()
        ft?.detach(this)?.attach(this)?.commit()
        readCount()
        goalCount(date)
        dailyGoal(date)
    }
}