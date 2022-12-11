package dev.rejfin.todoit.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dev.rejfin.todoit.models.GroupModel
import dev.rejfin.todoit.models.SmallUserModel
import java.util.*

class GroupsViewModel : ViewModel() {
    /** ui related variables */
    val groupList = mutableStateListOf<GroupModel>()
    private val userGroupList = mutableMapOf<String, Map<String,String>>()
    var errorState by mutableStateOf<String?>(null)
    var isLoadingData by mutableStateOf(false)

    /** firebase related variables */
    private val firebaseAuth = Firebase.auth
    private val database = Firebase.database
    private val storage = FirebaseStorage.getInstance()
    private val dbGroupRef = database.getReference("groups")
    private val dbUsersRef = database.getReference("users")
    private val storageRef = storage.getReference("groups")

    /** at start download information about group that user is member of */
    init {
        isLoadingData = true
        dbUsersRef.child(firebaseAuth.uid!!).child("groups").addValueEventListener(
            object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.children.count() == 0){
                        isLoadingData = false
                    }
                    for (groupsSnapshot in snapshot.children) {
                        val group = groupsSnapshot.getValue<Map<String, String>>()!!
                        if(!userGroupList.keys.any { it == group["id"] }){
                            userGroupList[group.values.first()] = group
                        }
                        getGroup(group.values.first())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    errorState = error.message
                    isLoadingData = false
                }
            }
        )
    }

    /** download information about group with passed id */
    fun getGroup(groupId: String){
        dbGroupRef.child(groupId).addListenerForSingleValueEvent(
            object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue<GroupModel>()?.let{model ->
                        if(!groupList.any { it.id == model.id }){
                            groupList.add(model)
                        }
                        isLoadingData = false
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    errorState = error.message
                    isLoadingData = false
                }
            }
        )
    }

    /** function sends image to firebase storage and returns link to it by callback */
    private fun sendImage(image:Uri, groupId: String, callback: (imageUrl:String?) -> Unit){
        val ref = storageRef.child(groupId)
        val uploadTask = ref.putFile(image)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                callback(null)
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(task.result.toString())
            } else {
                callback(null)
            }
        }
    }

    /** function create new group, that user who create is owner */
    fun createNewGroup(name:String, description: String, image: Uri){
        val groupId = UUID.randomUUID().toString()
        sendImage(image, groupId){ imageUrl ->
            dbGroupRef.child(groupId).setValue(
                GroupModel(
                    groupId,
                    name,
                    description,
                    firebaseAuth.uid!! ,
                    imageUrl,
                    hashMapOf(firebaseAuth.uid!! to SmallUserModel(
                        id = firebaseAuth.uid!!,
                        displayName = firebaseAuth.currentUser!!.displayName!!,
                        firebaseAuth.currentUser!!.photoUrl.toString()
                    ))
                )
            )
            val newGroupList = userGroupList
            newGroupList[groupId] = mapOf("id" to groupId)
            dbUsersRef.child(firebaseAuth.uid!!).child("groups").setValue(newGroupList)
        }
    }

    fun clearError(){
        errorState = null
    }
}