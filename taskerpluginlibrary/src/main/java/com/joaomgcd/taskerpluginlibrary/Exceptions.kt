package com.joaomgcd.taskerpluginlibrary

/**
 * Used to crash app when plugin developer forgets to have an empty constructor in an input class
 */
class NoEmptyConstructorException(inputClassName:String) : RuntimeException("Tasker Input class ${inputClassName} must have empty constructor")