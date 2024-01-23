package io.github.subhamtyagi.ocr.tasker

import android.content.Context
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerAction
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.runner.TaskerOutputRename
import com.joaomgcd.taskerpluginlibrary.runner.TaskerOutputRenames
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess
import io.github.subhamtyagi.ocr.R
import io.github.subhamtyagi.ocr.tasker.ChannelManager.channelResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking

object ChannelManager {
    val channelResult = Channel<OCROutput>()
}
const val TAG = "OCRRunner"
class OCRRunner : TaskerPluginRunnerAction<OCRInput, OCROutput>() {

    override val notificationProperties get() = NotificationProperties(iconResId = R.drawable.plugin)
    override fun run(context: Context,input: TaskerInput<OCRInput>): TaskerPluginResult<OCROutput> {
        val imagePathName = input.regular.imagePathName?: ""
        val output = runBlocking {
            BackgroundWork().readText(context, imagePathName)
            channelResult.receive()
        }
        return TaskerPluginResultSucess(output)
    }

    override fun addOutputVariableRenames(context: Context,input: TaskerInput<OCRInput>,renames: TaskerOutputRenames) {
        super.addOutputVariableRenames(context, input, renames)
        renames.add(TaskerOutputRename(OCROutput.VAR_RESULT, input.regular.resultVariableName))
    }

}