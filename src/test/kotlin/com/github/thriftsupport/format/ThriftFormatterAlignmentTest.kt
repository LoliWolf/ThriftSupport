package com.github.thriftsupport.format

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.github.thriftsupport.lang.ThriftLanguage
import java.io.File

class ThriftFormatterAlignmentTest : BasePlatformTestCase() {

    fun testAlignment() {
        // 读取测试数据
        val testDataDir = File("src/test/resources/testData/formatter")
        val beforeFile = File(testDataDir, "alignment_before.thrift")
        val afterFile = File(testDataDir, "alignment_after.thrift")
        
        assertTrue("Before file should exist", beforeFile.exists())
        assertTrue("After file should exist", afterFile.exists())
        
        val beforeContent = FileUtil.loadFile(beforeFile)
        val expectedContent = FileUtil.loadFile(afterFile)
        
        // 创建 PSI 文件
        val psiFile = PsiFileFactory.getInstance(project)
            .createFileFromText("test.thrift", ThriftLanguage, beforeContent)
        
        // 应用格式化
        WriteCommandAction.runWriteCommandAction(project) {
            val codeStyleManager = CodeStyleManager.getInstance(project)
            codeStyleManager.reformat(psiFile)
        }
        
        // 获取格式化后的内容
        val actualContent = psiFile.text
        
        // 写入调试日志
        val debugFile = File("alignment_test_debug.log")
        debugFile.writeText("""
            === BEFORE FORMATTING ===
            $beforeContent
            
            === AFTER FORMATTING ===
            $actualContent
            
            === EXPECTED ===
            $expectedContent
            
            === COMPARISON ===
            Actual equals expected: ${actualContent.trim() == expectedContent.trim()}
        """.trimIndent())
        
        // 验证结果
        assertEquals("Formatted content should match expected", 
                    expectedContent.trim(), actualContent.trim())
    }
}