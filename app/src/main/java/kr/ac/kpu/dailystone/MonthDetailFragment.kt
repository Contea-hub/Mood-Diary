package kr.ac.kpu.dailystone

import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.dialog_diary.*
import kotlinx.android.synthetic.main.fragment_month.view.*
import kotlinx.android.synthetic.main.fragment_month_detail.view.*
import kotlinx.android.synthetic.main.fragment_month_detail.view.tvOutputMonth
import java.lang.IllegalStateException

private const val GETTING_YEAR = "getting_year"
private const val GETTING_MONTH = "getting_month"
private const val GETTING_DAY = "getting_day"
class MonthDetailFragment : DialogFragment() {
    private var mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private lateinit var database: DatabaseReference

    companion object {
        const val TAG = "MonthDetailDialog"
        fun newInstance(gettingYear : String, gettingMonth : String, gettingDay : String) =
            MonthDetailFragment().apply {
                val newYearFormat = gettingYear.substring(2,4)
                arguments = Bundle().apply {
                    putString(GETTING_YEAR, newYearFormat)
                    putString(GETTING_MONTH, gettingMonth)
                    putString(GETTING_DAY, gettingDay)
                }
            }
    }
    private var selYear : String? = null
    private var selMonth : String? = null
    private var selDay : String? = null
    var width = Resources.getSystem().displayMetrics.widthPixels
    var devicewidth = (width - 200) / 5


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selYear = it.getString(GETTING_YEAR)
            selMonth = it.getString(GETTING_MONTH)
            selDay = it.getString(GETTING_DAY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        database = Firebase.database.reference
        var uid = mAuth?.uid
        val rootView = inflater.inflate(R.layout.fragment_month_detail, container, false)


        rootView.tvOutputMonth.text = selYear + "년" + selMonth + "월" + selDay + "일 "
        val postListener = object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                rootView.monthGriddetail.removeAllViews()
                val childCount = snapshot.childrenCount.toInt()
                for (i in 1 until childCount + 1) {
                    Log.d("HAN", "check $i : ${snapshot.child("$i").hasChildren()}")
                    if(i<10){
                        if (snapshot.child("$i").hasChildren()) {
                            val gv: GridLayout = rootView.findViewById(R.id.monthGriddetail)
                            val iv: ImageView = ImageView(context)
                            iv.setImageResource(R.drawable.happy_level1)
                            val params = LinearLayout.LayoutParams(devicewidth, devicewidth)
                            iv.layoutParams = params
                            iv.setOnClickListener {
                                val dateSet = selYear + selMonth + selDay
                                val dialog = DialogDiaryFragmentModify(it.context, dateSet , "$i")
                                dialog.show()
                                dialog.ddEdHl.setText(snapshot.child("$i").child("level").value.toString())
                                dialog.ddEdDiary.setText(snapshot.child("$i").child("diary").value.toString())
                            }
                            gv.addView(iv)
                        }
                    }else {
                        if (snapshot.child("$i").hasChildren()) {
                            val gv: GridLayout = rootView.findViewById(R.id.monthGriddetail)
                            val iv: ImageView = ImageView(context)
                            iv.setImageResource(R.drawable.happy_level1)
                            val params = LinearLayout.LayoutParams(devicewidth, devicewidth)
                            iv.layoutParams = params
                            iv.setOnClickListener {
                                val dateSet = selYear + selMonth + selDay
                                val dialog = DialogDiaryFragmentModify(it.context, dateSet , "$i")
                                dialog.show()
                                dialog.ddEdHl.setText(snapshot.child("$i").child("level").value.toString())
                                dialog.ddEdDiary.setText(snapshot.child("$i").child("diary").value.toString())

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
        database.child(uid!!).child("diary").child(selYear!!).child(selMonth!!).child(selDay!!)
            .addValueEventListener(postListener)



        return rootView
    }

    override fun onResume() {
        super.onResume()

        val params : ViewGroup.LayoutParams? = dialog?.window?.attributes
        params?.width = LinearLayout.LayoutParams.MATCH_PARENT
        params?.height = LinearLayout.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = params as android.view.WindowManager.LayoutParams
    }



}