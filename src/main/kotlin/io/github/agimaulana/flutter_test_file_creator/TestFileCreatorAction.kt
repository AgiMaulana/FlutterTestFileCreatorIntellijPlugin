package io.github.agimaulana.flutter_test_file_creator

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.ui.Messages

class TestFileCreatorAction : AnAction() {

    /**
     * On Android Studio the update(event) may be called twice with the first event contains
     * null virtual file but the second call the virtual file isn't null.
     *
     * Without suspending the update with Thread.sleep(1000) the presentation may be failed to enable the action.
     */
    override fun update(event: AnActionEvent) {
        super.update(event)
        val virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE)
        val sourceFileHelper = SourceFileHelper.createFrom(event)
        val isLibFile = sourceFileHelper.contentRootPath.startsWith("lib/")
        val isShouldEnable = virtualFile?.extension == "dart" && isLibFile
        event.presentation.isEnabled = isShouldEnable

        println("update - virtualFile: $virtualFile")
        println("update - contentRootPath: ${sourceFileHelper.contentRootPath}")
        println("update - isLibFile: $isLibFile")
        println("update - isShouldEnable: $isShouldEnable")
        println("update - event.presentation.isEnabled: ${event.presentation.isEnabled}")
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project
        val sourceFileHelper = SourceFileHelper.createFrom(event)
        val testFilePath = sourceFileHelper.createTestFilePath()
        val confirmedPath = Messages.showInputDialog(
            "Test file path",
            "Create Flutter Test File",
            null,
            testFilePath,
            null
        )
        if (confirmedPath != null) {
            val fileCreator = FileCreator(project!!)
            val (testDir, fileName) = sourceFileHelper.splitPathAndFileName(confirmedPath, "")
            try {
                val testFile = fileCreator.createFile(testDir, fileName)
                TestFileContentUtils.writeContentToFile(testFile)
                val fileInteraction = FileInteraction(project)
                fileInteraction.refreshWithoutFileWatcher(false)
                fileInteraction.openFile(testFile)
                fileInteraction.goToLine(4)
            } catch (e: FileAlreadyExistException) {
                Messages.showErrorDialog("File already exists", "Error")
            } catch (e: CreateFileFailedException) {
                Messages.showErrorDialog("Failed to create test file", "Error")
            }
        }
    }
}