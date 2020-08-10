package kr.ac.kpu.dailystone

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_main.*
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.*
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
    private val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var db: DatabaseReference

    //date
    private val current: LocalDate = LocalDate.now()
    private var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    private var formatted: String = current.format(formatter)
    var date = formatted.substring(2,8)
    var year = formatted.substring(2,4)
    var monthformatted = formatted.substring(4,6)
    var dayformatted: String = formatted.substring(6,8)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mAuth = FirebaseAuth.getInstance();
        db = Firebase.database.reference

        //ProgressView()
        readCount()

        mainTvDate.text = date

        mainBtnHappy.setOnClickListener {

            //var dialog = DialogAddFragment(it.context,date)
            var dialog = DialogDiaryFragment(it.context,date)
            dialog.show()



        }
        mainBtnSad.setOnClickListener {
            var dialog = DialogSadAddFragment(it.context)
            dialog.show()
        }
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
        return inflater.inflate(R.layout.fragment_main,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private fun readCount(){
        var user = FirebaseAuth.getInstance().currentUser
        var dcnt:Any = 0

        val dayListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value==null){
                    dcnt=0
                    mainPbDay.progress = 0
                }
                else{
                    dcnt = snapshot.value!!
                    //일별 평균 level 출력
                    readLevel(dcnt.toString().toInt())
                }
            }
            override fun onCancelled(error: DatabaseError) { }
        }
        db.child(user!!.uid).child("count").child(date).child("count")
            .addValueEventListener(dayListener)
    }

    private fun readLevel(dcnt:Int){
        var user = FirebaseAuth.getInstance().currentUser
        var day:Int = 50
        var dayList = mutableListOf<Int>()
        var value = 0

        val dayListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var level:Any=0
                for( i in 0 until dcnt){
                    level = snapshot.child((i+1).toString()).child("level").value!!
                    if(level !=""){
                        dayList.add(i,level.toString().toInt())
                    }
                }
                day = dayList.average().toInt()
                value = day
                mainPbDay.progress = value
            }
            override fun onCancelled(error: DatabaseError) { }
        }
        db.child(user!!.uid).child("diary").child(year).child(monthformatted)
            .child(dayformatted).addListenerForSingleValueEvent(dayListener)

    }

   private fun preDate(){//이전 날짜 조회

       var ld : LocalDate = LocalDate.of(year.toInt(), monthformatted.toInt(),dayformatted.toInt())
       var minusDayNow = ld.plusDays(-1)
       var formatted2 = minusDayNow.format(formatter)
       date = formatted2.substring(2,8)
       year = formatted2.substring(2,4)
       monthformatted = formatted2.substring(4,6)
       dayformatted = formatted2.substring(6,8)
       mainTvDate.text = date
       readCount()
   }

    private fun nextDate(){//다음 날짜 조회
        var ld : LocalDate = LocalDate.of(year.toInt(), monthformatted.toInt(),dayformatted.toInt())
        var plusDayNow = ld.plusDays(1)
        var formatted2 = plusDayNow.format(formatter)
        date = formatted2.substring(2,8)
        year = formatted2.substring(2,4)
        monthformatted = formatted2.substring(4,6)
        dayformatted = formatted2.substring(6,8)
        mainTvDate.text = date
        readCount()
    }
}