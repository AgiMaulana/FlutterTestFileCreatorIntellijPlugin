package io.github.agimaulana.flutter_test_file_creator

import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import java.io.File
import kotlin.io.path.Path

open class FileInteraction(protected val project: Project) {
    protected val fileEditorManager = FileEditorManager.getInstance(project)
    protected val virtualFileManager = VirtualFileManager.getInstance()

    fun openFile(testFileRootPath: String) {
        val path = Path(testFileRootPath);
        val virtualFile = VirtualFileManager.getInstance().findFileByNioPath(path)
        println("openFile - path: $testFileRootPath")
        println("openFile - virtualFile: ${virtualFile?.path}")
        if (virtualFile != null) {
            val openFileDescriptor = OpenFileDescriptor(project, virtualFile)
            FileEditorManager.getInstance(project).openTextEditor(openFileDescriptor, true)
        }
    }

    fun openFile(file: File) = openFile(file.path)

    fun refreshWithoutFileWatcher(asynchronous: Boolean) = virtualFileManager.refreshWithoutFileWatcher(asynchronous)

    /**
     * Credit to pradyumna singh
     * https://intellij-support.jetbrains.com/hc/en-us/community/posts/206133429-OpenFile-in-IDEA-and-goto-Line-
     */
    fun goToLine(lineNumber: Int): Boolean {
        val editor = fileEditorManager.selectedTextEditor ?: return false
        val caretModel = editor.caretModel
        val totalLineCount = editor.document.lineCount
        if (lineNumber > totalLineCount) return false

        //Moving caret to line number
        caretModel.moveToLogicalPosition(LogicalPosition(lineNumber - 1, 0))

        //Scroll to the caret
        val scrollingModel = editor.scrollingModel
        scrollingModel.scrollToCaret(ScrollType.CENTER)
        return true
    }
}