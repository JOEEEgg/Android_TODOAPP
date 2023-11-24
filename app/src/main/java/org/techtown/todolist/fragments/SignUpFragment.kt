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
import org.techtown.todolist.databinding.ActivityMainBinding
import org.techtown.todolist.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {

    // Firebase 인증을 처리하기 위한 객체
    private lateinit var auth: FirebaseAuth

    // NavController : 화면 간의 이동이나 액션을 처리
    private lateinit var navControl: NavController

    // 데이터 바인딩 객체
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // FragmentSignUpBinding을 사용하여 데이터 바인딩 초기화
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 초기화 메서드 및 이벤트 등록 메서드 호출
        init(view)
        registerEvents()
    }

    // 초기화 메서드
    private fun init(view: View) {
        // NavController 및 FirebaseAuth 초기화
        navControl = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
    }

    // 이벤트 등록 메서드
    private fun registerEvents() {

        // "Sign In" 텍스트를 클릭하면 SignInFragment로 이동
        binding.textViewSignIn.setOnClickListener {
            navControl.navigate(R.id.action_signUpFragment_to_signInFragment)
        }

        // "Next" 버튼을 클릭하면 사용자 회원가입 시도
        binding.nextBtn.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passEt.text.toString().trim()
            val verifyPass = binding.verifyPassEt.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty() && verifyPass.isNotEmpty()) {
                if (pass == verifyPass) {

                    // 회원가입 중이라는 표시를 보여주기 위해 프로그래스 바 표시
                    binding.progressBar.visibility = View.VISIBLE

                    // Firebase에 사용자 생성 요청
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(
                        OnCompleteListener {
                            if (it.isSuccessful) {
                                // 회원가입 성공 시 홈 화면으로 이동
                                Toast.makeText(context, "회원가입 완료", Toast.LENGTH_SHORT).show()
                                navControl.navigate(R.id.action_signUpFragment_to_homeFragment)
                            } else {
                                // 회원가입 실패 시 실패 메시지를 토스트로 표시
                                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                            // 프로그래스 바 감추기
                            binding.progressBar.visibility = View.GONE
                        })
                }
            } else {
                // 이메일 또는 비밀번호가 비어 있으면 경고 메시지를 토스트로 표시
                Toast.makeText(context, "password is wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
