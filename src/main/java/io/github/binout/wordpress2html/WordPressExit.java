/*
 * Copyright 2014 Benoît Prioux
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.binout.wordpress2html;

import com.beust.jcommander.JCommander;
import io.github.binout.wordpress2html.extractor.PostExtractor;
import io.github.binout.wordpress2html.writer.Html2AsciidocConverter;
import io.github.binout.wordpress2html.writer.PostWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class WordPressExit {

    public static void exit(File input, File output, boolean asciidoc, String disqusOutput, Consumer<String> logger) throws Exception {
        long begin = System.currentTimeMillis();
        logger.accept("BEGIN");

        logger.accept("Begin extraction of posts...");
        PostExtractor postExtractor = new PostExtractor(new FileInputStream(input));
        List<Post> posts = postExtractor.getPosts();
        logger.accept("Find " + posts.size() + " posts");

        output.mkdirs();
        logger.accept("Begin writing html posts...");

        Optional<Html2AsciidocConverter> asciidocConverter = asciidoc ? Optional.of(new Html2AsciidocConverter()) : Optional.empty();
        posts.stream().forEach(p -> {
            try {
                File file = new PostWriter(output, p, asciidocConverter).write();
                logger.accept("Write " + file.getName());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        postExtractor.replaceForDisqus(input.getAbsolutePath(), disqusOutput);

        logger.accept("END : duration=" + (System.currentTimeMillis() - begin) + " ms");
    }

    public static void main(String[] args) throws Exception {
        Arguments arguments = new Arguments();
        new JCommander(arguments, args);

        Globals.from = arguments.from;
        Globals.to = arguments.to;

        exit(arguments.file, arguments.output, arguments.asciidoc, arguments.disqus, System.out::println);
    }

}
