package nekox.box;

import io.objectbox.BoxStore;

import java.io.File;

public class ObjectBox {

    public static BoxStore create(File baseDirectory, String fileName) {

        return MyObjectBox.builder()
                .baseDirectory(baseDirectory)
                .name(fileName)
                .build();

    }

}
