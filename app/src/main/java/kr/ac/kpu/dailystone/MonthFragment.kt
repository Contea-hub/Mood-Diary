package kr.ac.kpu.dailystone

import android.app.ActionBar
import android.app.Activity
import android.content.res.Resources
import android.hardware.display.DisplayManager
import android.media.Image
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_month.*
import kotlinx.android.synthetic.main.fragment_month.view.*


class MonthFragment : Fragment() {
    private var mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private lateinit var database: DatabaseReference

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

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        database.child(uid!!).child("diary").child("20").child("07")
            .addValueEventListener(postListener)
        //}
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}

