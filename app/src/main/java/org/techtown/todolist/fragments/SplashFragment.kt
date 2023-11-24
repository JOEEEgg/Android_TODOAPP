package org.techtown.todolist.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import org.techtown.todolist.R

class SplashFragment : Fragment() {

    // Firebase 인증 객체
    private lateinit var mAuth: FirebaseAuth

    // NavController : 화면 간의 이동이나 액션을 처리
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 스플래시 화면의 레이아웃을 inflate
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 초기화 메서드 호출
        init(view)

        // 현재 로그인 상태 확인
        val isLogin: Boolean = mAuth.currentUser != null

        // 핸들러를 사용하여 지연된 실행
        val handler = Handler(Looper.myLooper()!!)
        handler.postDelayed({
            // 로그인 상태에 따라 다른 화면으로 이동
            if (isLogin)
                navController.navigate(R.id.action_splashFragment_to_homeFragment)
            else
                navController.navigate(R.id.action_splashFragment_to_signInFragment)
        }, 2000) // 2000 밀리초(2초) 후에 실행
    }

    // 초기화 메서드
    private fun init(view: View) {
        // Firebase 인증 객체 및 NavController 초기화
        mAuth = FirebaseAuth.getInstance()
        navController = Navigation.findNavController(view)
    }
}
