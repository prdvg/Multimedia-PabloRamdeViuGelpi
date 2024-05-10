object PermissionManager {
    private val grantedPermissions = mutableSetOf<String>()

    fun addGrantedPermission(permission: String) {
        grantedPermissions.add(permission)
    }

    fun isPermissionGranted(permission: String): Boolean {
        return grantedPermissions.contains(permission)
    }
}
