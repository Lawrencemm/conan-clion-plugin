package conan.commands;

import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.project.Project;

public class ConfigInstallFromGit extends AsyncConanCommand {
    public ConfigInstallFromGit(Project project, ProcessListener processListener, String location) {
        super(project, null, processListener, "config", "install", "--type git", location);
    }
}
