package kr.ac.kpu.dailystone

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import kotlin.math.nextUp
import kotlin.math.roundToInt


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
        //dailyGoal(date)

        mainTvDate.text = "${monthformatted}월 ${dayformatted}일"

        mainBtnHappy.setOnClickListener {
            var dialog = DialogDiaryFragment(it.context,date)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
       // val mainTvmgoal:TextView=requireView().findViewById(R.id.mainTvmgoal)

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
        mAuth = FirebaseAuth.getInstance();
        db = Firebase.database.reference
        val mainTvmgoal:TextView= rootView.findViewById(R.id.mainTvmgoal)
        val mainTvdgoal:TextView= rootView.findViewById(R.id.mainTvdgoal)
        val mainPbDgoal : ProgressBar = rootView.findViewById(R.id.mainPbDgoal)
        val mainPbDgoal2 : ProgressBar = rootView.findViewById(R.id.mainPbDgoal2)
        val mainPbMgoal : ProgressBar = rootView.findViewById(R.id.mainPbMgoal)
        val mainPbMgoal2 : ProgressBar = rootView.findViewById(R.id.mainPbMgoal2)
        val mainTvday :TextView= rootView.findViewById(R.id.mainTvday)
        readCount()


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

                    //업데이트를 위해 여기서 호출
                    goalCount(date)
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
                view?.mainPbDay?.progress = value
                view?.mainTvday?.text = "오늘의 행복지수 : $value"
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        db.child(user!!.uid).child("diary").child(year).child(monthformatted)
            .child(dayformatted).addListenerForSingleValueEvent(dayListener)

    }

    private fun goalCount(date:String) {
        var user = FirebaseAuth.getInstance().currentUser
        var sumList = mutableListOf<Int>()
        var gSum: Int = 0
        var total :Int = 0
        var gDate = date.substring(0,4)
        val goalListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in 1..31) {
                    if (i < 10) {
                        if (snapshot.child(gDate + "0$i").child("count").value == null) {
                            sumList.add(i-1, 0)
                        } else {
                            gSum = snapshot.child(gDate+ "0$i").child("count").value.toString().toInt()
                            sumList.add(i-1, gSum.toString().toInt())
                        }
                    } else {//10일 이상
                        if (snapshot.child(gDate + "$i").child("count").value == null) {
                            sumList.add(i-1, 0)
                        } else {
                            gSum = snapshot.child(gDate+ "$i").child("count").value.toString().toInt()
                            sumList.add(i-1, gSum.toString().toInt())
                        }
                    }
                    Log.d("z","합 : ${i-1} : ${sumList[i-1]}")
                }
                total=0
                total = sumList.sum()/(sumList.count()/31)
                Log.d("z","합 : $total, 나눈값 : ${sumList.count()}")
                monthGoal(total,date)
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
        //val mainPbMgoal : ProgressBar = requireView().findViewById(R.id.mainPbMgoal)
        //val mainPbMgoal2 : ProgressBar = requireView().findViewById(R.id.mainPbMgoal2)

        //val mainTvmgoal:TextView= requireView().findViewById(R.id.mainTvmgoal)
        val goalListener = object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                // var goalYear = mainTvDate.text.toString().substring(0, 2)
                //var goalMonth = mainTvDate.text.toString().substring(2, 4)
                var goalYear = date.substring(0,2)
                var goalMonth = date.substring(2,4)
                if (snapshot.child("goal").child(goalYear).child(goalMonth)
                        .child("goal").value == null
                ) {
                    view?.mainPbMgoal?.visibility = View.INVISIBLE
                    view?.mainPbMgoal2?.visibility = View.INVISIBLE
                } else {
                    view?.mainPbMgoal?.visibility = View.VISIBLE
                    view?.mainPbMgoal2?.visibility = View.VISIBLE
                    Maxgoal =
                        snapshot.child("goal").child(goalYear).child(goalMonth)
                            .child("goal").value!!.toString().toInt()
                }
                view?.mainPbMgoal?.max = Maxgoal.toString().toInt()
                view?.mainPbMgoal2?.max = Maxgoal.toString().toInt()
                //tv 설정
                view?.mainTvmgoal?.text = "이 달의 목표치 : ${gSum.toString()}/${Maxgoal.toString()}"
                dailyGoal(Maxgoal - gSum,date)
                if (gSum <= Maxgoal) { //목표달성 안됬을 때
                    view?.mainPbMgoal?.progress = gSum
                    view?.mainPbMgoal2?.progress = 0
                    Log.d("pb", "1 gsum : $gSum, MaxGoal : $Maxgoal")
                }else {//목표값 넘었을 때
                    view?.mainPbMgoal?.progress = Maxgoal
                    if(gSum-Maxgoal>=Maxgoal){ //2배 이상
                        view?.mainPbMgoal2?.progress = Maxgoal
                    }else{
                        view?.mainPbMgoal2?.progress = gSum - Maxgoal
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        db.child(user!!.uid).addValueEventListener(goalListener)
    }
    private fun dailyGoal(dayM: Int, date:String) {
        var user = FirebaseAuth.getInstance().currentUser
        var MaxDay: Int = 1
        var Dgoal: Int = 0
        Log.d("vs", "1 Dgoal : $Dgoal, pr1 : ${view?.mainPbDgoal?.progress}, pr2 : ${view?.mainPbDgoal?.progress}")
        val DgoalListener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("count").child(date).child("count").value == null) {
                    Dgoal=0
                } else {
                    Dgoal =
                        snapshot.child("count").child(date).child("count").value.toString().toInt()
                    Log.d("vs", "2 Dgoal : $Dgoal, pr1 : ${view?.mainPbDgoal?.progress}, pr2 : ${view?.mainPbDgoal2?.progress}")
                }



                var afafday = LocalDate.now()
                var formattererer: DateTimeFormatter = DateTimeFormatter.ofPattern("dd")
                var dadada:Int = afafday.format(formattererer).toInt()


                MaxDay= dayM/(31-dadada)//16쯤 / 남은 일수 = 소수점

                if(dayM>0){//남은 양이 양수일때
                    if(dayM/(31-dadada).toFloat()>=1){ //하루에 한개 이상 써야되는 경우
                        MaxDay= Math.ceil(dayM/(31-dadada).toDouble()).toInt()
                        view?.mainPbDgoal?.max = MaxDay
                        view?.mainPbDgoal2?.max = MaxDay
                        view?.mainPbDgoal?.progress = Dgoal
                        if(Dgoal-MaxDay>MaxDay){ //2바퀴이상
                            view?.mainPbDgoal2?.progress=MaxDay
                        }else{
                            view?.mainPbDgoal2?.progress=Dgoal - MaxDay
                        }


                    }else if(dayM/(31-dadada).toFloat()>=0) { //이하
                        MaxDay = 1
                        view?.mainPbDgoal?.max = 1
                        view?.mainPbDgoal2?.max = 1
                        if(Dgoal==0){
                            view?.mainPbDgoal?.progress = 0
                            view?.mainPbDgoal2?.progress=0
                        }else if(Dgoal==1){
                            view?.mainPbDgoal?.progress = 1
                            view?.mainPbDgoal2?.progress=0
                        }else{
                            view?.mainPbDgoal?.progress = 1
                            view?.mainPbDgoal2?.progress=1
                        }

                    }else{ //예외처리
                        MaxDay=0
                        view?.mainPbDgoal?.max = 1
                        view?.mainPbDgoal2?.max = 1
                        view?.mainPbDgoal?.progress = 1
                        view?.mainPbDgoal2?.progress=1
                    }
                }else if(dayM==0){ //다썼을 때
                    view?.mainPbDgoal?.max = 1
                    view?.mainPbDgoal2?.max = 1

                    view?.mainPbDgoal?.progress = Dgoal
                    view?.mainPbDgoal2?.progress=0

                    if(dayM/(31-dadada).toFloat()>0){
                        MaxDay=1
                    }else{
                        MaxDay=0
                    }
                }else{//남은 일기양이 음수
                    view?.mainPbDgoal?.max = 1
                    view?.mainPbDgoal2?.max = 1
                    view?.mainPbDgoal?.progress = 1
                    view?.mainPbDgoal2?.progress = 1
                    Dgoal=0
                    MaxDay=0
                }
                view?.mainTvdgoal?.text = "오늘의 목표치 : ${Dgoal.toString()}/${MaxDay.toString()}"
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
        mainTvDate.text ="${monthformatted}월 ${dayformatted}일"
        readCount()
        goalCount(date)
        var ft : FragmentTransaction? = fragmentManager?.beginTransaction()
        ft?.detach(this)?.attach(this)?.commit()
        //dailyGoal(date)
    }

    private fun nextDate() {//다음 날짜 조회
        var ld: LocalDate = LocalDate.of(year.toInt(), monthformatted.toInt(), dayformatted.toInt())
        var plusDayNow = ld.plusDays(1)
        var formatted2 = plusDayNow.format(formatter)
        date = formatted2.substring(2, 8)
        year = formatted2.substring(2, 4)
        monthformatted = formatted2.substring(4, 6)
        dayformatted = formatted2.substring(6, 8)
        mainTvDate.text = "${monthformatted}월 ${dayformatted}일"
        readCount()
        goalCount(date)
        var ft : FragmentTransaction? = fragmentManager?.beginTransaction()
        ft?.detach(this)?.attach(this)?.commit()
        //dailyGoal(date)
    }
}