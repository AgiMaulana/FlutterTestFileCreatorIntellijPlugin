package io.github.agimaulana.flutter_test_file_creator

import java.io.File

object TestFileContentUtils {
    private const val content = "import 'package:flutter_test/flutter_test.dart';\n" +
        "\n" +
        "void main() {\n" +
        "\n" +
        "}\n"

    fun writeContentToFile(file: File) = file.writeText(content)
}