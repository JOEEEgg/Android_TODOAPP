package org.techtown.todolist.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import org.techtown.todolist.databinding.FragmentAddTodoPopupBinding
import org.techtown.todolist.utils.ToDoData


class AddTodoPopupFragment : DialogFragment() {


    private lateinit var binding: FragmentAddTodoPopupBinding // 데이터 바인딩을 사용하기 위한 바인딩 객체
    private  var listener: DialogNextBtnClickListener? = null // 외부에서 리스너를 설정할 수 있는 변수
    private var toDoData: ToDoData? = null // 다이얼로그에서 사용될 할 일 데이터 객체

    // 외부에서 이 다이얼로그의 리스너를 설정하는 메서드
    fun setListener(listener: HomeFragment) {
        this.listener = listener // this는 AddTodoPopupFragment를 가리킨다.
    }

    // 레이아웃을 인플레이트하여 반환하는 메서드
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // 데이터 바인딩을 사용하여 레이아웃 초기화
        binding = FragmentAddTodoPopupBinding.inflate(inflater, container, false)
        return binding.root
    }

    // onViewCreated : 뷰 생성 후 호출되는 메서드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 인자로 전달된 데이터 확인 후 할 일 데이터 설정
        if (arguments != null) { // arguments : 인자
            toDoData = ToDoData(
                arguments?.getString("taskId").toString(), // ToDoData 클래스에 taskId, task 인자를 toDoData에 설정
                arguments?.getString("task").toString())
            binding.todoEt.setText(toDoData?.task)
        }

        // 이벤트 등록 메서드 호출
        registerEvents()
    }

    // 다이얼로그 내 이벤트 등록 메서드
    private fun registerEvents() {
        // 중복 코드: 인자로 전달된 데이터 확인 후 할 일 데이터 설정
        if (arguments != null) {
            toDoData = ToDoData(
                arguments?.getString("taskId").toString(),
                arguments?.getString("task").toString())
            binding.todoEt.setText(toDoData?.task)
        }

        // 할 일을 저장 또는 업데이트하는 버튼에 대한 클릭 이벤트 처리
        binding.todoNextBtn.setOnClickListener {
            val todoTask = binding.todoEt.text.toString()

            if (todoTask.isNotEmpty()){
                if(toDoData == null) {
                    // 새로운 할 일을 저장하는 메서드 호출
                    listener?.onSaveTask(todoTask, binding.todoEt)
                }else{
                    // 기존 할 일을 업데이트하는 메서드 호출
                    toDoData!!.task = todoTask
                    listener?.onUpdateTask(toDoData!!, binding.todoEt)
                }
            }

        }
        // 다이얼로그를 닫는 버튼에 대한 클릭 이벤트 처리
        binding.todoClose.setOnClickListener {
            dismiss()
        }
    }
    // 다이얼로그 내에서 발생하는 이벤트를 처리하기 위한 인터페이스
    interface DialogNextBtnClickListener{

        // 새로운 할 일 저장
        // todoTask : 새로 저장 될 할일의 내용을 나타내는 문자열
        // todoEt : 할 일을 입력받는
        // TextInputEditText : 사용자가 할 일을 입력하고 저장할 때 이 메서드가 호출
        // toDoData : 업데이트 할 데이터를 나타낸다.
        fun onSaveTask(todoTask : String, todoEt :TextInputEditText)
        fun onUpdateTask(toDoData: ToDoData, todoEt :TextInputEditText)
    }

    // 동반 객체
    companion object {
        // 태그 상수 정의
        // 새로운 인스턴스를 생성하고 초기화하는 정적 팩토리 메서드
        const val TAG = "AddTodoPopupFragment"

        // Fragment 인스턴스를 반환하는 정적 팩토리 메서드
        @JvmStatic
        fun newInstance(taskId: String, task: String) =
            AddTodoPopupFragment().apply {
                arguments = Bundle().apply {
                    putString("taskId", taskId)
                    putString("task", task)
                }
            }
    }
}