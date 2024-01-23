package com.joaomgcd.taskerpluginlibrary.config

import android.os.Bundle
import net.dinglisch.android.tasker.TaskerPlugin



class HostCapabilities(bundleExtras: Bundle?) {
    val supportsJsonKeys = TaskerPlugin.hostSupportsKeyEncoding(bundleExtras, TaskerPlugin.Encoding.JSON)
    val sendsRelevanVariables = TaskerPlugin.hostSupportsRelevantVariables(bundleExtras)
    val action = HostCapabilitesAction(bundleExtras)
    val condition = HostCapabilitesCondition(bundleExtras)
    val event = HostCapabilitesEvent(bundleExtras)
}

class HostCapabilitesAction(bundleExtras: Bundle?) {
    val canReturnVariables = TaskerPlugin.Setting.hostSupportsVariableReturn(bundleExtras)
    val canReplaceVariables = TaskerPlugin.Setting.hostSupportsOnFireVariableReplacement(bundleExtras)
    val canRunSynchronously = TaskerPlugin.Setting.hostSupportsSynchronousExecution(bundleExtras)
}

class HostCapabilitesCondition(bundleExtras: Bundle?) {
    val canReturnVariables = TaskerPlugin.Condition.hostSupportsVariableReturn(bundleExtras)
}

class HostCapabilitesEvent(bundleExtras: Bundle?) {
    val supportsPassThroughData = TaskerPlugin.Event.hostSupportsRequestQueryDataPassThrough(bundleExtras)
}