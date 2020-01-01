package conan.platform;

import com.google.common.io.Resources;
import com.jetbrains.cidr.CidrCodeInsightFixture;
import com.jetbrains.cidr.CidrTestCase;
import com.jetbrains.cidr.CidrTestDataFixture;
import com.jetbrains.cidr.cpp.CPPTestDataFixture;
import com.jetbrains.cidr.cpp.cmake.CMakeProjectFixture;
import com.jetbrains.cidr.execution.CidrExecutionFixture;
import com.jetbrains.cidr.execution.debugger.CidrDebuggingFixture;
import conan.commands.Install;
import conan.profiles.CMakeProfile;
import conan.profiles.ConanProfile;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class CMakeFindPackageTest extends CidrTestCase<CMakeProjectFixture, CidrExecutionFixture<CMakeProjectFixture>, CidrDebuggingFixture<CidrExecutionFixture<CMakeProjectFixture>>, CidrCodeInsightFixture> {

    @Override
    protected boolean runInDispatchThread() {
        return false;
    }

    @Nullable
    @Override
    protected CidrTestDataFixture createTestDataFixture() {
        return new CPPTestDataFixture(new File(Resources.getResource(getClass(), "fixture").getFile()));
    }

    @Nullable
    @Override
    protected CMakeProjectFixture createProjectFixture() {
        return new CMakeProjectFixture(myTestDataFixture);
    }

    public void testNothing() throws Exception {
        myProjectFixture.initProject("find-package", null, false);
        myProjectFixture.openProjectWithoutReloadingCMake();
        new Install(getProject(), new CMakeProfile("blah", getTestProjectRoot().toString()), new ConanProfile("default"), false).run_sync(null);
        myProjectFixture.reload();
    }
}
