import android.text.Spannable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.techtown.todolist.databinding.EachTodoItemBinding
import org.techtown.todolist.utils.ToDoData
import com.google.android.material.button.MaterialButton
import org.techtown.todolist.R


class ToDoAdapter(private val list: MutableList<ToDoData>) : RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>() {

    inner class ToDoViewHolder(val binding: EachTodoItemBinding) : RecyclerView.ViewHolder(binding.root)

    private var listener: ToDoAdapterClicksInterface? = null

    fun setListener(listener: ToDoAdapterClicksInterface) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val binding = EachTodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToDoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        bindToDoData(holder, position)
    }

    private fun bindToDoData(holder: ToDoViewHolder, position: Int) {

        with(holder.binding) {
            list.getOrNull(position)?.let { toDoData ->
                todoTask.text = toDoData.task

                completeButton.setOnClickListener {
                    toDoData.completed = !toDoData.completed

                    if (toDoData.completed) {
                        completeButton.setImageResource(R.drawable.checked)

                        // 텍스트에 가운데 선 효과 추가
                        val spannableString = SpannableString(toDoData.task)
                        spannableString.setSpan(
                            StrikethroughSpan(), 0, spannableString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        todoTask.text = spannableString
                    }else {
                        completeButton.setImageResource(R.drawable.unchecked_24)
                        // 텍스트에 가운데 선 효과 제거
                        todoTask.text = toDoData.task
                    }
                }

                // todoTask 클릭 이벤트 처리 부분
                todoTask.setOnClickListener {
                    // completeButton의 클릭 이벤트를 강제로 호출하여 체크 상태를 반전시킴
                    completeButton.performClick()
                }

                deleteTask.setOnClickListener {
                    listener?.onDeleteTaskBtnClicked(toDoData, position)
                }
                editTask.setOnClickListener {
                    listener?.onEditTaskBtnClicked(toDoData, position)
                }
            }
        }
    }

    interface ToDoAdapterClicksInterface {
        fun onDeleteTaskBtnClicked(toDoData: ToDoData, position: Int)
        fun onEditTaskBtnClicked(toDoData: ToDoData, position: Int)
    }
}
