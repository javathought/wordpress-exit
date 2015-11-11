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
import java.util.Scanner;

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
        content = content.replaceAll("\\[caption .*image:http://.*/(.*)\\]\\[/caption\\]", "image::$1");
        // TODO
        // Add youtube conversion
//        content = content.replaceAll("\\[youtube=http://www.youtube.com/watch?v=(.*)]", "video::$1[youtube]");
//        [youtube=http://www.youtube.com/watch?v=cQ0bgz3tyNk&fs=1&hl=fr_FR]
//        video::KCylB780zSM[youtube]
        content = content.replaceAll("\\[.*code language=\"(.*)\"\\]", "[source,$1]");
        content = content.replaceAll("&lt;", "<");
        content = content.replaceAll("&gt;", ">");
        content = content.replaceAll("&amp;", "&");
        content = content.replaceAll("&quot;", "\"");
        content = content.replaceAll("-{5,}", "-----------------------");



        fileWriter.append("\n").append(content);
        fileWriter.close();
    }

    private String getFullHtml() {

        String content = post.getHtmlContent();
        boolean pre = false;
        StringBuilder builder = new StringBuilder();

        Scanner scanner = new Scanner(content);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            // String line;
            // process the line
            line = line.replaceAll("</?pre>", " ");
            if (line.contains("[code") || line.contains("[sourcecode")) {
                pre = true;
            }

            if (pre) {
                if (line.contains("[/code") || line.contains("[/sourcecode")) {
                    pre = false;
                }
                line = line.replaceAll("<", "&lt;");
                line = line.replaceAll(">", "&gt;");
                line = line.replaceAll("&", "&amp;");
                line = line.replaceAll("(\\[.*code .*\\])", "$1<pre>");
                line = line.replaceAll("(\\[\\/.*code\\])", "</pre>");
                builder.append(line).append("\n");

            } else {
                builder.append(line).append("</p><p>");

            }


        }
        scanner.close();

        if (post.getTitle().startsWith("XML")) {
            System.out.println(builder.toString());
        }

        return "<html><head>" +
                "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />" +
                "</head>" +
                "<body>" + builder.toString() +
                "</body></html>";
    }

    private static String getFilename(Post p) {
        String name = p.getDate().format(DATE_TIME_FORMATTER) + "-" + p.getTitle();
        System.out.println("[" + name + "]");
        return name.replaceAll("[^a-zA-Z0-9.]+", "-").replaceAll("-$", "");
    }

}
