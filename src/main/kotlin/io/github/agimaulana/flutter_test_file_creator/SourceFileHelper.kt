package io.github.agimaulana.flutter_test_file_creator

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ex.ProjectEx
import com.intellij.openapi.project.impl.ProjectExImpl
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.project.stateStore

class SourceFileHelper private constructor(private val project: Project?, private val virtualFile: VirtualFile?) {
    val contentRootPath: String
    get() {
        val stateStore = project?.stateStore
        val projectBasePath = stateStore?.projectBasePath?.toString() ?: ""
        return virtualFile?.path
            ?.substringAfter(projectBasePath)
            ?.trimStart { it == '/' } ?: ""
    }

    fun splitPathAndFileName(path: String, fileNameSuffix: String): Pair<String, String> {
        val split = path.split("/")
        val pathWithoutFileName = split.subList(0, split.size - 1).joinToString("/")
        val testPath = pathWithoutFileName.replace("lib", "test")

        val fileName = split.last()
        val fileNameWithoutExtension = fileName.substringBeforeLast(".")
        val extension = fileName.substringAfterLast(".")
        val testFileName = "%s%s.%s".format(fileNameWithoutExtension, fileNameSuffix, extension)

        return Pair(testPath, testFileName)
    }

    fun createTestFilePath(): String {
        val (testPath, testFileName) = splitPathAndFileName(contentRootPath, "_test")
        return "$testPath/$testFileName"
    }

    companion object {
        fun create(project: Project?, virtualFile: VirtualFile?): SourceFileHelper {
            return SourceFileHelper(project, virtualFile)
        }

        fun createFrom(event: AnActionEvent) = create(
            event.project,
            event.getData(PlatformDataKeys.VIRTUAL_FILE)
        )
    }
}