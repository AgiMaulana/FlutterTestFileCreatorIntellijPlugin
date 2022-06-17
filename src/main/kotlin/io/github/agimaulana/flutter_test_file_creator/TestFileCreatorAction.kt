package io.github.agimaulana.flutter_test_file_creator

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.ui.Messages

class TestFileCreatorAction : AnAction() {

    override fun update(event: AnActionEvent) {
        super.update(event)
        val virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE)
        val sourceFileHelper = SourceFileHelper.createFrom(event)
        val isLibFile = sourceFileHelper.contentRootPath.startsWith("lib/")
        event.presentation.isEnabled = virtualFile?.extension == "dart" && isLibFile
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
            val (testDir, fileName) = sourceFileHelper.splitPathAndFileName(confirmedPath)
            try {
                val testFile = fileCreator.createFile(testDir, fileName)
                TestFileContentUtils.writeContentToFile(testFile);
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