package com.msd.core.tracking

object Constants {

    // EVENTS

    // MAIN
    const val OPEN_CONFIGURATION_CLICKED_EVENT = "open_configuration_clicked"
    const val EDIT_CONFIGURATION_CLICKED_EVENT = "edit_configuration_clicked"
    const val DELETE_CONFIGURATION_CLICKED_EVENT = "delete_configuration_clicked"
    const val CONFIGURATION_DELETED_EVENT = "configuration_deleted"
    const val ADD_CONFIGURATION_CLICKED_EVENT = "add_configuration_clicked"


    // EDITOR
    const val SMB_CONFIGURATION_CREATED_EVENT = "smb_configuration_created"
    const val SMB_CONFIGURATION_EDITED_EVENT = "smb_configuration_edited"

    // EXPLORER
    const val LIST_FILES_AND_DIRECTORIES_EVENT = "list_files_and_directories"
    const val OPEN_FILE_EVENT = "open_file"
    const val OPEN_LOCAL_FILE_EVENT = "open_local_file"
    const val DOWNLOAD_FILE_EVENT = "download_file"

    // PARAMETER KEYS
    const val FILES_NUMBER = "files_number"
    const val OPEN_TIME = "open_time"
    const val FILE_SIZE = "file_size"
}
