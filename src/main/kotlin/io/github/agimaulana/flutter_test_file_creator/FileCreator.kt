package io.github.agimaulana.flutter_test_file_creator

import com.intellij.openapi.project.Project
import java.io.File

class FileCreator (private val project: Project) {
    private fun mkdir(dir: File) {
        try {
            dir.mkdirs()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    fun createFile(dir: String, fileName: String): File {
        val basePath = project.basePath
        val baseTestDir = File("$basePath/$dir")
        val testFile = File(baseTestDir, fileName)
        if (!testFile.exists()) {
            try {
                mkdir(baseTestDir)
                testFile.createNewFile()
            } catch (e: Exception) {
                e.printStackTrace()
                throw CreateFileFailedException(e)
            }
            return testFile
        } else {
            throw  FileAlreadyExistException()
        }
    }
}