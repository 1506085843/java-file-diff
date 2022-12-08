package diff.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FileCoypUtils {
    public static void copyfile(String jsCssPath) {
        try {
            InputStream inputStream1 = FileCoypUtils.class.getClassLoader().getResourceAsStream("jscss/diff2html.min.css");
            FileUtils.copyInputStreamToFile(inputStream1, new File(jsCssPath + "diff2html.min.css"));

            InputStream inputStream2 = FileCoypUtils.class.getClassLoader().getResourceAsStream("jscss/diff2html-ui.min.js");
            FileUtils.copyInputStreamToFile(inputStream2, new File(jsCssPath + "diff2html-ui.min.js"));

            InputStream inputStream3 = FileCoypUtils.class.getClassLoader().getResourceAsStream("jscss/github.min.css");
            FileUtils.copyInputStreamToFile(inputStream3, new File(jsCssPath + "github.min.css"));
        } catch (IOException e) {
        }

    }
}
