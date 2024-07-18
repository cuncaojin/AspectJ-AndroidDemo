package tech.xiaoniu.aspect

import android.util.Log
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature

/**
 * 切面类
 */
@Aspect
class MyAspect {
    // 切点
    @Pointcut(POINTCUT)
    fun debugLog() {
    }

    @Before("debugLog()")
    fun beforeDebugLog(joinPoint: JoinPoint) {
        try {
            val className = joinPoint.getThis().javaClass.simpleName
            val methodName = joinPoint.signature.name
            val signature = joinPoint.signature as MethodSignature
            val method = signature.method
            val annotation = method.getAnnotation(DebugLog::class.java)
            if (annotation == null) {
                Log.e(TAG, "beforeDebugLog: annotation == null")
            } else {
                Log.i(TAG, "beforeDebugLog: Method invoked: $className.$methodName")
            }
        } catch (e: Throwable) {
            Log.e(TAG, "beforeDebugLog: failed on error: ", e)
        }
    }

    @After("debugLog()")
    fun afterDebugLog(joinPoint: JoinPoint) {
        try {
            val className = joinPoint.getThis().javaClass.simpleName
            val methodName = joinPoint.signature.name
            val signature = joinPoint.signature as MethodSignature
            val method = signature.method
            val annotation = method.getAnnotation(DebugLog::class.java)
            if (annotation == null) {
                Log.e(TAG, "afterDebugLog: annotation == null")
            } else {
                Log.i(TAG, "afterDebugLog: Method invoked: $className.$methodName")
            }
        } catch (e: Throwable) {
            Log.e(TAG, "afterDebugLog: failed on error: ", e)
        }
    }

    companion object {
        // private val TAG = MyAspect::class.java.simpleName
        private val TAG = "yg"
        const val POINTCUT = "execution(@tech.xiaoniu.aspect.DebugLog * *(..))"
    }
}