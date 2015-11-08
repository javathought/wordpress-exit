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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Post {

    private final String title;
    private final String htmlContent;
    private final LocalDateTime date;
    private final List<String> tags;
    private final String link;

//    public Post(String title, String htmlContent, LocalDateTime date, Stream<String> tags) {
    public Post(String title, String htmlContent, LocalDateTime date, List<String> tags, String link) {
        this.title = title.replaceAll("-{2,}", "-");
        this.htmlContent = htmlContent;
        this.date = date;
        this.tags = tags;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getLink() {
        return link;
    }

    @Override
    public String toString() {
        return "Post{" +
                "title='" + title + '\'' +
                ", htmlContent='" + htmlContent + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
