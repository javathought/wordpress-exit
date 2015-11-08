package io.github.binout.wordpress2html;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class WordPressExitTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void end2endTest() throws Exception {
        File output = testFolder.newFolder();
        // InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("javaonemorething.xml");
        // InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("javathoughts.wordpress.2015-11-07.xml");
        File file = new File(Thread.currentThread().getContextClassLoader().getResource("javathoughts.wordpress.2015-11-07.xml").getFile());

        Globals.from = "javathought.wordpress.com";
        Globals.to = "javathought.github.io";

        WordPressExit.exit(file, output, true, "disqus.xml", System.out::println);

        // assertThat(output.list()).hasSize(38);
        assertThat(output.list()).hasSize(134);
    }
}