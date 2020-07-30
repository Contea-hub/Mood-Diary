package kr.ac.kpu.dailystone

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_main.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    private val formatted: String = current.format(formatter)
    var date = formatted.substring(2,8)
    private val formatterYear: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy")
    private val yearformatted: String = current.format(formatterYear)
    var year = yearformatted.substring(2,4)
    private val formatterMonth : DateTimeFormatter = DateTimeFormatter.ofPattern("MM")
    private val monthformatted: String = current.format(formatterMonth)
    private val formatterDay : DateTimeFormatter = DateTimeFormatter.ofPattern("dd")
    private val dayformatted: String = current.format(formatterDay)




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mAuth = FirebaseAuth.getInstance();
        db = Firebase.database.reference
        //preDate()
        ProgressView() //프로그레스바 출력

        mainBtnHappy.setOnClickListener {
            var dialog = DialogAddFragment(it.context)
            dialog.show()
        }
        mainBtnSad.setOnClickListener {
            var dialog = DialogSadAddFragment(it.context)
            dialog.show()
        }
        /* val args = Bundle()
         args.putString("key", "value")
         val dialogFragment = DialogFragment()
         dialogFragment.setArguments(args)
         fragmentManager?.let { dialogFragment.show(it, "Sample Dialog Fragment") }*/



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


    private fun ProgressView(){
        var user = FirebaseAuth.getInstance().currentUser
        var day:Double = 50.0
        var dayList = mutableListOf<Int>()
        var dcnt:Any = 0

        val daymyRef = db.child(user!!.uid).child("diary").child(year).child(monthformatted).child(dayformatted)

        val dayListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child("count").child(date).child("count").value==null){
                    dcnt=0
                }
                else{
                    dcnt = snapshot.child("count").child(date).child("count").value!!
                }

                var level:Any=0
                for( i in 0 until dcnt.toString().toInt()){
                    level = snapshot.child("diary").child(year).child(monthformatted).child(dayformatted).child(dcnt.toString()).child("level").value!!
                    Log.d("daytest","level : $level")
                    dayList.add(level.toString().toInt())
                }
                day = dayList!!.average()
                Log.d("daytest","day : $day")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        db.child(user!!.uid).addValueEventListener(dayListener)

        mainPbDay.progress = day.toInt()


    }










    /*
   private fun preDate(){//이전 날짜 조회
       var uUid:String = FirebaseAuth.getInstance().currentUser?.uid.toString()
       val dateRef:DatabaseReference = database.getReference(uUid)

       // Read from the database
       /*
       dateRef.addValueEventListener(object : ValueEventListener {
           override fun onDataChange(dataSnapshot: DataSnapshot) {
               val value =dataSnapshot?.value.toString()
               mainTvDate.setText(value)
               Toast.makeText(applicationContext,"Successed to read value.",
                   Toast.LENGTH_LONG).show()
           }

           override fun onCancelled(error: DatabaseError) {
               // Failed to read value
               Toast.makeText(applicationContext, "Failed to read value.",
                   Toast.LENGTH_LONG).show()
           }
       })

        */
   }
   */

}