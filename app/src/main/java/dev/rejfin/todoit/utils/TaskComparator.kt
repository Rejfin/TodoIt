package dev.rejfin.todoit.utils

import dev.rejfin.todoit.models.TaskModel

class TaskComparator(val userId: String) : Comparator<TaskModel> {
    override fun compare(task1: TaskModel?, task2: TaskModel?): Int {
        return if(task1 != null && task2 != null){
            if(task1.done && !task2.done){ 1 }
            else if(!task1.done && task2.done){ -1 }
            else if(task2.endTimestamp < System.currentTimeMillis()){ -1 }
            else if(task1.endTimestamp < System.currentTimeMillis()){ 1 }
            else if(task1.lockedByUserId != null && task1.lockedByUserId != userId){-1}
            else if(task2.lockedByUserId != null && task2.lockedByUserId != userId){-1}
            else{ 0 }
        }else{
            0
        }
    }
}