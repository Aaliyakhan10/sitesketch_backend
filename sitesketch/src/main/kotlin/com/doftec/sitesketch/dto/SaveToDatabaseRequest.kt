package com.doftec.sitesketch.dto

import com.doftec.sitesketch.model.Resume

data class SaveToDatabaseRequest(val code: String,
                                 val resume: Resume
) {

}