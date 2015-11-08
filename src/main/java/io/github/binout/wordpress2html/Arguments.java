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

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;

import java.io.File;

class Arguments {

    @Parameter(names = "-file", converter = FileConverter.class)
    File file;

    @Parameter(names = "-output", converter = FileConverter.class)
    File output;

    @Parameter(names = "-disqus", description = "convert to disqus")
    String disqus;

    @Parameter(names = "-from", description = "convert from domain")
    String from;

    @Parameter(names = "-to", description = "convert to domain")
    String to;

    @Parameter(names = "-asciidoc", description = "Convert to asciidoc" , arity = 0)
    boolean asciidoc = false;

    public static class FileConverter implements IStringConverter<File> {
        @Override
        public File convert(String value) {
            return new File(value);
        }
    }

}
