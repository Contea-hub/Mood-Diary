package kr.ac.kpu.dailystone

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_main.*


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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mAuth = FirebaseAuth.getInstance();
        db = Firebase.database.reference
        //preDate()

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
}