package io.jenkins.plugins.codeInsights;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

@SuppressWarnings("unused")
public class CodeInsightsBuilder extends Builder implements SimpleBuildStep {
    private final String repositoryName;
    private final String srcPath;
    private final String commitId;

    private String checkstyleFilePath;

    @DataBoundConstructor
    public CodeInsightsBuilder(
        @NotNull final String repositoryName,
        @NotNull final String srcPath,
        @NotNull String commitId) {
        this.repositoryName = repositoryName;
        this.srcPath = srcPath;
        this.commitId = commitId;
    }

    @DataBoundSetter
    public void setCheckstyleFilePath(final String checkstyleFilePath) {
        this.checkstyleFilePath = checkstyleFilePath;
    }

    @Override
    public void perform(@NotNull final Run<?, ?> run,
                        @NotNull final FilePath workspace,
                        @NotNull final Launcher launcher,
                        @NotNull final TaskListener listener) throws IOException, InterruptedException {
        final DescriptorImpl descriptor = (DescriptorImpl) super.getDescriptor();
        new KotlinEntryPoint(
            descriptor.bitbucketUrl,
            descriptor.project,
            descriptor.reportKey,
            descriptor.username,
            descriptor.password,
            repositoryName,
            workspace.absolutize().getRemote(),
            srcPath,
            commitId,
            checkstyleFilePath,
            listener.getLogger()
        ).delegate();
    }

    @Symbol("codeInsights")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        private String bitbucketUrl;
        private String project;
        private String reportKey;
        private String username;
        private String password;

        public DescriptorImpl() {
            super.load();
        }

        @Override
        public boolean configure(final StaplerRequest req, final JSONObject json) {
            final JSONObject globalSettings = json.getJSONObject("codeInsights");
            this.bitbucketUrl = globalSettings.getString("bitbucketUrl");
            this.project = globalSettings.getString("project");
            this.reportKey = globalSettings.getString("reportKey");
            this.username = globalSettings.getString("username");
            this.password = globalSettings.getString("password");
            save();
            return true;
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        @NotNull
        public String getDisplayName() {
            return "Call Bitbucket Server Code Insights API";
        }
    }
}