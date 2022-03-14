package io.jenkins.plugins.codeInsights.framework

import hudson.FilePath
import hudson.model.Run
import io.jenkins.plugins.codeInsights.usecase.FileTransferService

class FileTransferServiceImpl(private val workspace: FilePath, run: Run<*, *>) : FileTransferService {
    private val local = FilePath(run.rootDir)

    override fun copyFromWorkspaceToLocal(path: String) {
        workspace.child(path).copyRecursiveTo(local.child(path))
    }

    override fun readFile(path: String): String = workspace.child(path).readToString()
}
