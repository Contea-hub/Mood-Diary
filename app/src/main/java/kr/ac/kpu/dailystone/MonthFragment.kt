package kr.ac.kpu.dailystone

import android.app.ActionBar
import android.app.Activity
import android.content.res.Resources
import android.hardware.display.DisplayManager
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_month.*
import kotlinx.android.synthetic.main.fragment_month.view.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class MonthFragment : Fragment() {
    private var mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private lateinit var database: DatabaseReference
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


    companion object { // 상수 역할

        fun newInstance(): MonthFragment {
            return MonthFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = Firebase.database.reference
        var uid = mAuth?.uid
        var monthDisplay = monthformatted + "월"
        tvOutputMonth.text = monthDisplay
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        database = Firebase.database.reference
        var uid = mAuth?.uid
        val rootView = inflater.inflate(R.layout.fragment_month, container, false)

        Log.d("HAN", "uid check : $uid")
        //for(i in 1..31) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rootView.monthGrid.removeAllViews()
                for (i in 1..31) {
                    Log.d("HAN", "check $i : ${snapshot.child("$i").hasChildren()}")
                    if(i<10){
                        if (snapshot.child("0$i").hasChildren()) {
                            val gv: GridLayout = rootView.findViewById(R.id.monthGrid)
                            var width = Resources.getSystem().displayMetrics.widthPixels
                            var devicewidth = (width - 128) / 5
                            val iv: ImageView = ImageView(context)
                            iv.setImageResource(R.drawable.happy_level1)
                            val params = LinearLayout.LayoutParams(devicewidth, devicewidth)
                            iv.layoutParams = params
                            iv.setOnClickListener {
                                Toast.makeText(context, "clicked $i", Toast.LENGTH_SHORT).show()
                            }
                            gv.addView(iv)
                        }
                    }else {
                        if (snapshot.child("$i").hasChildren()) {
                            val gv: GridLayout = rootView.findViewById(R.id.monthGrid)
                            var width = Resources.getSystem().displayMetrics.widthPixels
                            var devicewidth = (width - 128) / 5
                            val iv: ImageView = ImageView(context)
                            iv.setImageResource(R.drawable.happy_level1)
                            val params = LinearLayout.LayoutParams(devicewidth, devicewidth)
                            iv.layoutParams = params
                            iv.setOnClickListener {
                                Toast.makeText(context, "clicked $i", Toast.LENGTH_SHORT).show()
                            }
                            gv.addView(iv)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        database.child(uid!!).child("diary").child(year).child(monthformatted)
            .addValueEventListener(postListener)
        //}
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}

