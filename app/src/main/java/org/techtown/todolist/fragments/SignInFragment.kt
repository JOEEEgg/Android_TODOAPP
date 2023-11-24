package org.techtown.todolist.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import org.techtown.todolist.R
import org.techtown.todolist.databinding.FragmentSignInBinding
import org.techtown.todolist.databinding.FragmentSignUpBinding

class SignInFragment : Fragment() {

    // Firebase 인증을 처리하기 위한 객체
    private lateinit var auth: FirebaseAuth

    // NavController : 화면 간의 이동이나 액션을 처리
    private lateinit var navControl: NavController

    // 데이터 바인딩 객체
    private lateinit var binding: FragmentSignInBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // FragmentSignInBinding을 사용하여 데이터 바인딩 초기화
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    // 초기화 메서드
    private fun init(view: View) {
        // NavController 및 FirebaseAuth 초기화
        navControl = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance() // Firebase 인증 객체 초기화
    }

    // onViewCreated : Fragment의 레이아웃을 inflate하고 반환
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 초기화 메서드 및 이벤트 등록 메서드 호출
        init(view)
        registerEvents()
    }

    // 사용자 로그인을 시도하는 메서드
    private fun loginUser(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            // it : 람다 식 내에서 현재 처리 중인 객체나 값을 참조하는 데 사용되는 특별한 식별자, 다시 말해, task에 대한 암시적인 참조
            if (it.isSuccessful) {
                // 로그인이 성공하면 HomeFragment로 이동
                navControl.navigate(R.id.action_signInFragment_to_homeFragment)
            } else {
                // 로그인이 실패하면 실패 메시지를 토스트로 표시
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    //  회원 등록 메서드
    private fun registerEvents() {

        // "Sign Up" 텍스트를 클릭하면 SignUpFragment로 이동
        binding.textViewSignUp.setOnClickListener {
            navControl.navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        // "Next" 버튼을 클릭하면 사용자 로그인 시도
        binding.nextBtn.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passEt.text.toString().trim()

            // 이메일과 비밀번호 필드가 비어 있지 않은 경우 로그인 시도
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                loginUser(email, pass)
            } else {
                // 필수 필드가 비어 있으면 경고 메시지를 토스트로 표시
                Toast.makeText(context, "Empty fields are not allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
