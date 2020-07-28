package kr.ac.kpu.dailystone

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_userinfo.*


class UserFragment : Fragment() {
    private lateinit var mAuth : FirebaseAuth
    companion object { // 상수 역할

        fun newInstance(): UserFragment {
            return UserFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()

        userBtninfo.setOnClickListener {
            val intent = Intent(context, UserInfoActivity::class.java)
            startActivity(intent)
        }

        userBtnReset.setOnClickListener {

        }

        userBtnLogout.setOnClickListener {      //클릭 시 로그아웃
            signOut()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_userinfo, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private fun signOut(){
        val intent = Intent(context, LoginActivity::class.java)
        FirebaseAuth.getInstance().signOut()
        startActivity(intent)
        activity?.finish()
    }

}