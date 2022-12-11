package dev.rejfin.todoit.models

import dev.rejfin.todoit.utils.Selectable


data class InvitationModel(
    val id: String = "",
    val groupId: String = "",
    val groupName: String = "",
    val invitationText: String = ""
): Selectable {
    override fun getObjectId(): String {
        return id
    }

    override fun getString(): String {
        return groupName
    }
}
