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
package io.github.binout.wordpress2html.writer;

import io.github.binout.wordpress2html.Globals;
import io.github.binout.wordpress2html.Post;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class PostWriter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final File file;
    private final Post post;
    private final Optional<Html2AsciidocConverter> asciidocConverter;

    public PostWriter(File output, Post post, Optional<Html2AsciidocConverter> asciidocConverter) throws IOException {
        this.post = post;
        this.asciidocConverter = asciidocConverter;
        this.file = new File(output, getFilename(this.post) + ".html");
    }

    public File write() throws IOException {
        String htmlContent = getFullHtml();
        Files.copy(new ByteArrayInputStream(htmlContent.getBytes("UTF-8")), file.toPath());
        if (asciidocConverter.isPresent()) {
            File asciidoc = asciidocConverter.get().convert(file);
            addHeader(asciidoc);
        }
        return file;
    }

    private void addHeader(File asciidoc) throws IOException {
        boolean firstTag = true;
        List<String> tags;
        String content = IOUtils.toString(new FileInputStream(asciidoc));
        FileWriter fileWriter = new FileWriter(asciidoc, false);
        fileWriter.append("= ").append(post.getTitle()).append("\n");
        fileWriter.append(":published_at: ").append(post.getDate().format(DATE_TIME_FORMATTER)).append("\n");
        tags = post.getTags();
        if (! tags.isEmpty()) {
            fileWriter.append(":hp-tags: ");
            for (String tag : post.getTags()) {
                if (! firstTag) {
                    fileWriter.append(", ");
                } else {
                    firstTag = false;
                }
                fileWriter.append(tag);
            }
            fileWriter.append("\n");
        }
        content = content.replaceAll(Globals.from, Globals.to);
        content = content.replaceAll("\\[caption .*image:http://.*/(.*)\\[/caption\\]", "image::$1");
        fileWriter.append("\n").append(content);
        fileWriter.close();
    }

    private String getFullHtml() {
        return "<html><head>" +
                "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />" +
                "</head>" +
                "<body>" + post.getHtmlContent().replace("\n", "</p><p>") +
                "</body></html>";
    }

    private static String getFilename(Post p) {
        String name = p.getDate().format(DATE_TIME_FORMATTER) + "-" + p.getTitle();
        System.out.println("[" + name + "]");
        return name.replaceAll("[^a-zA-Z0-9.]+", "-").replaceAll("-$", "");
    }

}
