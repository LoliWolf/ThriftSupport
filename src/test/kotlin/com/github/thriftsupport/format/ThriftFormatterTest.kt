package com.github.thriftsupport.format

import com.intellij.formatting.FormattingContext
import com.intellij.formatting.FormattingMode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CommonCodeStyleSettings
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.github.thriftsupport.lang.ThriftLanguage
import java.io.File

class ThriftFormatterTest : BasePlatformTestCase() {

    fun testFormatterIsRegistered() {
        // 写入调试日志
        try {
            val logFile = File("D:\\Projects\\ThriftSupport\\formatter_test_debug.log")
            logFile.appendText("ThriftFormatterTest.testFormatterIsRegistered() started at ${System.currentTimeMillis()}\n")
        } catch (e: Exception) {
            // 忽略日志错误
        }

        // 测试格式化器是否正确注册
        val builder = ThriftFormattingModelBuilder()
        assertNotNull("ThriftFormattingModelBuilder should be instantiable", builder)
        
        try {
            val logFile = File("D:\\Projects\\ThriftSupport\\formatter_test_debug.log")
            logFile.appendText("ThriftFormattingModelBuilder instantiated successfully at ${System.currentTimeMillis()}\n")
        } catch (e: Exception) {
            // 忽略日志错误
        }
    }

    fun testFormatterWithSimpleCode() {
        try {
            val logFile = File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
            logFile.appendText("ThriftFormatterTest: Starting testFormatterWithSimpleCode\n")
            
            // 创建一个简单的 Thrift 文件
            val thriftCode = """
                const string TEST_CONST = "test";
                struct TestStruct {
                    1: string name;
                    2: i32 id;
                }
            """.trimIndent()
            
            val psiFile = myFixture.configureByText("test.thrift", thriftCode)
            logFile.appendText("ThriftFormatterTest: Created PSI file: ${psiFile.name}\n")
            
            // 创建格式化上下文
            val settings = CodeStyleSettings()
            val commonSettings = settings.getCommonSettings(ThriftLanguage)
            val context = FormattingContext.create(
                psiFile,
                TextRange(0, psiFile.textLength),
                settings,
                FormattingMode.REFORMAT
            )
            
            logFile.appendText("ThriftFormatterTest: Created formatting context\n")
            
            // 获取格式化器并创建模型
            val builder = ThriftFormattingModelBuilder()
            val model = builder.createModel(context)
            
            logFile.appendText("ThriftFormatterTest: Created formatting model: ${model != null}\n")
            assertNotNull("Formatting model should not be null", model)
            
        } catch (e: Exception) {
            val logFile = File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
            logFile.appendText("ThriftFormatterTest: Error in testFormatterWithSimpleCode: ${e.message}\n")
            throw e
        }
    }
}