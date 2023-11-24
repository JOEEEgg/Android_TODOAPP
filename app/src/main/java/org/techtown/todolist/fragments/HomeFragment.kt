package org.techtown.todolist.fragments

import ToDoAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.techtown.todolist.databinding.FragmentHomeBinding
import org.techtown.todolist.utils.ToDoData

// 인터페이스 호출 : DialogNextBtnClickListener, ToDoAdapterClicksInterface
class HomeFragment : Fragment(), AddTodoPopupFragment.DialogNextBtnClickListener, ToDoAdapter.ToDoAdapterClicksInterface {

    // Firebase 인증을 처리하기 위한 객체
    private lateinit var auth: FirebaseAuth

    // Firebase 데이터베이스에 접근하기 위한 참조 객체, 이 객체를 이용하여 데이터베이스에 데이터를 읽거나 쓸 수 있다.
    private lateinit var databaseReference: DatabaseReference

    // NavController : 화면 간의 이동이나 액션을 처리
    private lateinit var navController: NavController

    // 데이터 바인딩 객체 : 레이아웃과 UI 구성요소 id에 접근
    private lateinit var binding: FragmentHomeBinding

    // 할 일 목록 표시 하기 위한 어댑터
    private lateinit var adapter: ToDoAdapter

    // 할 일 목록 데이터를 담기 위한 리스트
    private lateinit var mList: MutableList<ToDoData>

    // 할 일 팝업 다이얼로그 프래그먼트 : 할 일을 추가/수정하기 위한 프레그먼트, ?는 null이 가능함을 뜻함
    private var popupFragment: AddTodoPopupFragment? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 데이터 바인딩 초기화
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    // onViewCreated : Fragment의 레이아웃을 inflate하고 반환
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view) // 뷰를 초기화 하는 작업

        getDataFromFirebase() // Firebase에서 데이터 가져오는 작업

        registerEvents() // 이벤트 등록 메서드 호출
    }

    // 초기화 메서드
    private fun init(view: View) {
        // NavController 초기화
        navController = Navigation.findNavController(view)

        // Firebase 인증 객체 초기화
        auth = FirebaseAuth.getInstance()

        // Firebase 데이터베이스 참조 객체 초기화
        databaseReference = FirebaseDatabase.getInstance().reference.child("Tasks").child(auth.currentUser?.uid.toString())

        // 리사이클러뷰 설정
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        mList = mutableListOf()
        adapter = ToDoAdapter(mList)
        adapter.setListener(this)
        binding.recyclerView.adapter = adapter
    }

    // 이벤트 등록 메서드
    private fun registerEvents() {
        binding.addTaskBtn.setOnClickListener {
            if (popupFragment != null) {
                childFragmentManager.beginTransaction().remove(popupFragment!!).commit()
            }
            popupFragment = AddTodoPopupFragment()
            popupFragment!!.setListener(this)
            popupFragment!!.show(childFragmentManager, AddTodoPopupFragment.TAG)
        }
    }

    // 할 일을 Firebase DB에 저장하는 메서드
    override fun onSaveTask(todo: String, todoEt: TextInputEditText) {
        databaseReference.push().setValue(todo).addOnCompleteListener {//addOnCompleteListener : Firebase 작업의 성공 또는 실패 여부를 확인
            if (it.isSuccessful) { // it : 작업의 결과를 나타내는 변쉬
                Toast.makeText(context, "Todo saved Successfully !!!", Toast.LENGTH_SHORT).show()
                todoEt.text = null
            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            popupFragment!!.dismiss()
        }
    }

    // 할 일을 Firebase에서 업데이트하는 메서드
    override fun onUpdateTask(toDoData: ToDoData, todoEt: TextInputEditText) {
        val map = HashMap<String, Any>()
        map[toDoData.taskId] = toDoData.task
        databaseReference.updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "글수정 성공", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            popupFragment!!.dismiss()
        }
    }

    // Firebase에서 데이터를 가져오는 메서드
    private fun getDataFromFirebase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for (taskSnapshot in snapshot.children) {
                    val todoTask = taskSnapshot.key?.let {
                        ToDoData(it, taskSnapshot.value.toString())
                    }
                    if (todoTask != null) {
                        mList.add(todoTask)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 할 일 삭제 버튼 클릭 시 호출되는 메서드
    override fun onDeleteTaskBtnClicked(toDoData: ToDoData, position: Int) {
        databaseReference.child(toDoData.taskId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "삭제 성공", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 할 일 수정 버튼 클릭 시 호출되는 메서드
    override fun onEditTaskBtnClicked(toDoData: ToDoData, position: Int) {
        if (popupFragment != null) {
            childFragmentManager.beginTransaction().remove(popupFragment!!).commit()
        }
        popupFragment = AddTodoPopupFragment.newInstance(toDoData.taskId, toDoData.task)
        popupFragment!!.setListener(this)
        popupFragment!!.show(childFragmentManager, AddTodoPopupFragment.TAG)
    }
}
