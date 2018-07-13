package com.github.springbootkotlingithubapi.model

enum class Type(val type: String) {
    CLOSED("closed"),
    REOPENED("reopened"),
    SUBSCRIBED("subscribed"),
    UNSUBSCRIBED("unsubscribed"),
    MERGED("merged"),
    REFERENCED("referenced"),
    MENTIONED("mentioned"),
    ASSIGNED("assigned"),
    UNASSIGNED("unassigned"),
    LABELED("labeled"),
    UNLABELED("unlabeled"),
    MILESTONED("milestoned"),
    DEMILESTONED("demilestoned"),
    RENAMED("renamed"),
    LOCKED("locked"),
    UNLOCKED("unlocked"),
    HEAD_REF_DELETED("head_ref_deleted"),
    HEAD_REF_RESTORED("head_ref_restored"),
    CONVERTED_NOTE_TO_ISSUE("converted_note_to_issue"),
    MOVED_COLUMNS_IN_PROJECT("moved_columns_in_project"),
    COMMENT_DELETED("comment_deleted"),
    REVIEW_REQUESTED("review_requested");

    companion object {
        fun of(type: String): Type? = enumValues<Type>()
                .filter { it.type.toUpperCase() == type.toUpperCase() }
                .singleOrNull()
    }
}