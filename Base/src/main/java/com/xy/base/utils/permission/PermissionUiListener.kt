package com.xy.base.utils.permission

interface PermissionUiListener {
    fun onCreatePermissionDenied():PermissionDialogDenied?
    fun onCreatePermissionReason():PermissionDialogReason?
}