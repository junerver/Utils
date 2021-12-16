package xyz.junerver.utils

import android.app.Application
import androidx.core.content.FileProvider

/**
 * Description:
 *
 * @author Junerver
 * date: 2021/12/16-9:26
 * Email: junerver@gmail.com
 * Version: v1.0
 */
class UtilsFileProvider : FileProvider(){
    override fun onCreate(): Boolean {
       Utils.init(context?.applicationContext as Application)
        return true
    }
}