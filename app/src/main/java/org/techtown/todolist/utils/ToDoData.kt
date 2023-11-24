package org.techtown.todolist.utils

// taskId: 각 할 일 항목의 고유한 식별자로 사용될 문자열입니다.
// task: 실제 할 일 내용을 나타내는 문자열입니다.
data class ToDoData(
    val taskId:String,
    var task: String,
    var completed: Boolean = false // 완료 여부
    )
