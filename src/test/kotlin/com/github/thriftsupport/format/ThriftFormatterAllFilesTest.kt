package com.github.thriftsupport.format

import com.github.thriftsupport.lang.ThriftLanguage
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.streams.toList

class ThriftFormatterAllFilesTest : BasePlatformTestCase() {

    fun testFormatAllThriftFiles() {
        val projectRoot = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize()
        val thriftFiles = Files.walk(projectRoot).use { stream ->
            stream
                .filter { Files.isRegularFile(it) && it.toString().endsWith(".thrift", ignoreCase = true) }
                .filter { path -> !isUnder(path, projectRoot.resolve("build")) && !isUnder(path, projectRoot.resolve(".gradle")) }
                .map(Path::toFile)
                .toList()
        }

        assertTrue("No .thrift files found in project", thriftFiles.isNotEmpty())

        val codeStyleManager = CodeStyleManager.getInstance(project)
        val psiFactory = PsiFileFactory.getInstance(project)

        thriftFiles.forEach { file ->
            val content = file.readText(StandardCharsets.UTF_8)
            val formatted = formatContent(content, psiFactory, codeStyleManager)
            val reformatted = formatContent(formatted, psiFactory, codeStyleManager)

            val relativePath = projectRoot.relativize(file.toPath().toAbsolutePath().normalize())
            if (formatted.trimEnd() != reformatted.trimEnd()) {
                writeDebugArtifacts(projectRoot, relativePath, formatted, reformatted)
            }
            assertEquals(
                "Formatter should be idempotent for $relativePath",
                formatted.trimEnd(),
                reformatted.trimEnd()
            )
        }
    }

    private fun formatContent(
        text: String,
        psiFactory: PsiFileFactory,
        codeStyleManager: CodeStyleManager
    ): String {
        val psiFile = psiFactory.createFileFromText("test.thrift", ThriftLanguage, text)
        WriteCommandAction.runWriteCommandAction(project) {
            codeStyleManager.reformat(psiFile)
        }
        PsiDocumentManager.getInstance(project).commitAllDocuments()
        return psiFile.text
    }

    private fun isUnder(path: Path, ancestor: Path): Boolean =
        path.startsWith(ancestor)

    private fun writeDebugArtifacts(root: Path, relativePath: Path, first: String, second: String) {
        val debugDir = root.resolve("build").resolve("thrift-format-debug")
        Files.createDirectories(debugDir)
        val baseName = relativePath.toString().replace("\\", "__").replace("/", "__")
        Files.write(debugDir.resolve("${baseName}.formatted"), first.toByteArray(StandardCharsets.UTF_8))
        Files.write(debugDir.resolve("${baseName}.reformatted"), second.toByteArray(StandardCharsets.UTF_8))
    }
}
