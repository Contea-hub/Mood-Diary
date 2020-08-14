package kr.ac.kpu.dailystone

import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentTransaction
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
    var date = formatted.substring(2, 8)
    private val formatterYear: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy")
    private var yearformatted: String = current.format(formatterYear)
    var year = yearformatted.substring(2, 4)
    private var formatterMonth: DateTimeFormatter = DateTimeFormatter.ofPattern("MM")
    private var monthformatted: String = current.format(formatterMonth)
    private val formatterDay: DateTimeFormatter = DateTimeFormatter.ofPattern("dd")
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
        var yearDisplay = yearformatted + "년"
        tvOutputYear.text = yearDisplay

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


        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rootView.monthGrid.removeAllViews()
                for (i in 1..31) {
                    Log.d("HAN", "check $i : ${snapshot.child("$i").hasChildren()}")
                    if (i < 10) {
                        if (snapshot.child("0$i").hasChildren()) {
                            val gv: GridLayout = rootView.findViewById(R.id.monthGrid)
                            var width = Resources.getSystem().displayMetrics.widthPixels
                            var devicewidth = (width - 128) / 5
                            val iv: ImageView = ImageView(context)
                            val monthCount = snapshot.child("0$i").childrenCount.toInt()
                            for (j in 1 until monthCount + 1) {
                                var sum = 0
                                sum += snapshot.child("0$i").child("$j")
                                    .child("level").value.toString().toInt()
                                var meanSum: Int = (sum / monthCount)
                                if (meanSum in 1..20) {
                                    iv.setImageResource(R.drawable.level_sad)
                                } else if (meanSum in 21..50) {
                                    iv.setImageResource(R.drawable.level_normal)
                                } else if (meanSum in 51..100) {
                                    iv.setImageResource(R.drawable.level_happy)
                                }

                            }
                            val params = LinearLayout.LayoutParams(devicewidth, devicewidth)
                            iv.layoutParams = params
                            iv.setOnClickListener {
                                val mDialog = MonthDetailFragment.newInstance(
                                    yearformatted, monthformatted,
                                    "0$i"
                                )
                                fragmentManager?.let { it1 ->
                                    mDialog.show(
                                        it1,
                                        MonthDetailFragment.TAG
                                    )
                                }
                            }
                            gv.addView(iv)
                        }
                    } else {
                        if (snapshot.child("$i").hasChildren()) {
                            val gv: GridLayout = rootView.findViewById(R.id.monthGrid)
                            var width = Resources.getSystem().displayMetrics.widthPixels
                            var devicewidth = (width - 128) / 5
                            var sum = 0
                            val iv: ImageView = ImageView(context)
                            val monthCount = snapshot.child("$i").childrenCount.toInt()
                            for (j in 1 until monthCount + 1) {

                                sum += snapshot.child("$i").child("$j")
                                    .child("level").value.toString().toInt()
                                var meanSum: Int = (sum / monthCount)
                                Log.d("HAN", "$meanSum")
                                if (meanSum in 1..20) {
                                    iv.setImageResource(R.drawable.level_sad)
                                } else if (meanSum in 21..50) {
                                    iv.setImageResource(R.drawable.level_normal)
                                } else if (meanSum in 51..100) {
                                    iv.setImageResource(R.drawable.level_happy)
                                }
                                Log.d("HAN", "meansum : $meanSum")

                            }

                            val params = LinearLayout.LayoutParams(devicewidth, devicewidth)
                            iv.layoutParams = params
                            iv.setOnClickListener {
                                val mDialog = MonthDetailFragment.newInstance(
                                    yearformatted,
                                    monthformatted,
                                    "$i"
                                )
                                fragmentManager?.let { it1 ->
                                    mDialog.show(
                                        it1,
                                        MonthDetailFragment.TAG
                                    )
                                }

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
        database.child(uid!!).child("diary").child(yearformatted.substring(2, 4))
            .child(monthformatted)
            .addValueEventListener(postListener)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        monthPreviousButton.setOnClickListener {
            if (monthformatted == "01") {
                monthformatted = "12"
                yearformatted = (yearformatted.toInt() - 1).toString()
            } else if (monthformatted.toInt() in 1..10) {
                monthformatted = "0" + (monthformatted.toInt() - 1).toString()
            } else {
                monthformatted = (monthformatted.toInt() - 1).toString()
            }
            var ft: FragmentTransaction? = fragmentManager?.beginTransaction()
            ft?.detach(this)?.attach(this)?.commit()
            Log.d("HAN", "month : $monthformatted")
        }

        monthNextButton.setOnClickListener {
            if (monthformatted == "12") {
                monthformatted = "01"
                yearformatted = (yearformatted.toInt() + 1).toString()
            } else if (monthformatted.toInt() in 1..8) {
                monthformatted = "0" + (monthformatted.toInt() + 1).toString()
            } else {
                monthformatted = (monthformatted.toInt() + 1).toString()
            }
            var ft: FragmentTransaction? = fragmentManager?.beginTransaction()
            ft?.detach(this)?.attach(this)?.commit()
            Log.d("HAN", "month : $monthformatted")
        }
    }
}

