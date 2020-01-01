package conan.platform;

import com.jetbrains.cidr.CidrTestDataFixture;
import com.jetbrains.cidr.cpp.cmake.CMakeProjectFixture;
import org.jetbrains.annotations.NotNull;

import java.io.File;

class ConanCMakeProjectFixture extends CMakeProjectFixture {

    private String name;
    private String basePath;

    ConanCMakeProjectFixture(@NotNull String name, @NotNull String basePath) {
        super(new CidrTestDataFixture(new File(basePath)));
        this.name = name;
        this.basePath = basePath;
    }
}
