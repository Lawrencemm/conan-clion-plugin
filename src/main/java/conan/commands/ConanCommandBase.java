package conan.commands;

import com.google.common.collect.Lists;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.jetbrains.cidr.cpp.cmake.CMakeRunner;
import com.jetbrains.cidr.cpp.cmake.CMakeRunnerStep;
import conan.commands.task.AsyncConanTask;
import conan.commands.task.CMakeEnvironmentTask;
import conan.commands.task.SyncConanTask;
import conan.persistency.settings.ConanProjectSettings;
import conan.profiles.ConanProfile;
import conan.utils.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static conan.utils.Utils.CONAN_HOME_ENV;
import static conan.utils.Utils.log;

/**
 * Base class for all Conan commands.
 *
 * Created by Yahav Itzhak on Feb 2018.
 */
public class ConanCommandBase {

    private static final Logger logger = Logger.getInstance(ConanCommandBase.class);

    Project project;
    GeneralCommandLine args;

    protected ConanCommandBase(@Nullable Project project, String... args) {
        this.project = project;

        ConanProjectSettings conanProjectSettings = ConanProjectSettings.getInstance(project);
        String envExePath = System.getenv("CONAN_EXE_PATH");
        if(envExePath != null){
            log(logger, "Conan at: ", envExePath, NotificationType.INFORMATION);
            this.args = new GeneralCommandLine(Lists.asList(envExePath, args));
        }
        else {
            String configPath = conanProjectSettings.getConanPath();
            String conanExe = StringUtil.isEmpty(configPath) ? "conan" : configPath;
            log(logger, "Conan at: ", conanExe, NotificationType.INFORMATION);
            this.args = new GeneralCommandLine(Lists.asList(conanExe, args));
        }
        this.args.setWorkDirectory(project.getBaseDir().getCanonicalPath());
        String conanHome = Utils.getConanHomeEnv();
        if (conanHome != null) {
            this.args.withEnvironment(CONAN_HOME_ENV, conanHome);
        }
    }

    public void addParameter(String arg) {
        args.addParameter(arg);
    }

    public void run_async(ConanProfile conanProfile, @Nullable CMakeRunner.Listener cmakeListener, @Nullable ProcessListener processListener) {
        AsyncConanTask task = new AsyncConanTask(this.project, conanProfile, cmakeListener, processListener, this.args);
        this.run(task);
    }

    public SyncConanTask getSyncTask(@Nullable ProcessListener processListener) {
        if (processListener == null) {
            processListener = new ProcessAdapter() {};
        }
        return new SyncConanTask(this.project, processListener, this.args);
    }

    public void run_sync(@Nullable ProcessListener processListener) {
        this.run(getSyncTask(processListener));
    }

    public void run_cmake_environment(@NotNull CMakeRunnerStep.Parameters parameters) {
        CMakeEnvironmentTask task = new CMakeEnvironmentTask(this.args);
        task.run(parameters);
    }

    public void run(Task task) {
        // The progress manager is only good for foreground threads.
        if (SwingUtilities.isEventDispatchThread()) {
            ProgressManager.getInstance().run(task);
        } else {
            // Run the scan task when the thread is in the foreground.
            SwingUtilities.invokeLater(() -> ProgressManager.getInstance().run(task));
        }
    }

    public GeneralCommandLine getCommandLine() {
        return args;
    }

}
