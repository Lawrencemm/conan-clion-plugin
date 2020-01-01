package conan.platform;

import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.jetbrains.cidr.CidrCodeInsightFixture;
import com.jetbrains.cidr.CidrTestCase;
import com.jetbrains.cidr.CidrTestDataFixture;
import com.jetbrains.cidr.cpp.CPPTestDataFixture;
import com.jetbrains.cidr.cpp.cmake.CMakeProjectFixture;
import com.jetbrains.cidr.cpp.cmake.CMakeSettings;
import com.jetbrains.cidr.cpp.cmake.workspace.CMakeProfileInfo;
import com.jetbrains.cidr.cpp.cmake.workspace.CMakeWorkspace;
import com.jetbrains.cidr.execution.CidrExecutionFixture;
import com.jetbrains.cidr.execution.debugger.CidrDebuggingFixture;
import conan.persistency.settings.ConanProjectSettings;
import conan.profiles.CMakeProfile;
import conan.profiles.ConanProfile;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

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

        CMakeWorkspace cMakeWorkspace = myProjectFixture.getCMakeWorkspace();
        Application application = ApplicationManager.getApplication();

        // Reloading creates the CMake profiles
        myProjectFixture.reload();

        // Create profile matching for the Conan plugin
        List<CMakeSettings.Profile> cmakeProfiles = cMakeWorkspace.getSettings().getProfiles();
        Collection<CMakeProfileInfo> profileInfos = cMakeWorkspace.getProfileInfos();
        Map<CMakeProfile, ConanProfile> profileMapping = Maps.newHashMap();
        for (CMakeSettings.Profile profile : cmakeProfiles)
        {
            Optional<CMakeProfileInfo> profileInfo = profileInfos.stream().filter(pi -> pi.getProfile() == profile).findFirst();
            assert profileInfo.isPresent();

            CMakeProfile cMakeProfile = new CMakeProfile(profile.getName(), Objects.requireNonNull(profileInfo.get().getGenerationDir()));
            ConanProfile conanProfile = new ConanProfile("default");

            profileMapping.put(cMakeProfile, conanProfile);
        }
        ConanProjectSettings conanProjectSettings = ConanProjectSettings.getInstance(myProjectFixture.getProject());
        conanProjectSettings.setProfileMapping(profileMapping);
        myProjectFixture.reload();
        myProjectFixture.assertErrors();
    }
}
