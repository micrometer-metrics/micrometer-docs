/*
 * Copyright 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.micrometer.docs.adoc;

import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.asciidoctor.SafeMode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

public class CoalescerMain {

    private static final InternalLogger log = InternalLoggerFactory.getInstance(CoalescerMain.class);

    public static void main(String... args) throws IOException {
        File inputFile = new File(args[0]);
        File outputFile = new File(args[1]);
        log.info("Will do the adoc conversion from root directory [" + inputFile + "] to [" + outputFile + "]");
        if (!inputFile.exists()) {
            log.info("There's no file [" + inputFile + "], skipping adoc conversion");
            return;
        }
        new CoalescerMain().walk(inputFile, outputFile);
    }

    void walk(File input, File output) throws IOException {
        // TODO: This will be done by ./gradlew clean
        // cleanOutputFolder(output);
        try (Stream<Path> walk = Files.walk(input.toPath())) {
            walk.filter(Files::isRegularFile).forEach(path -> {
                Path relative = input.toPath().relativize(path);
                File newFile = new File(output, relative.toString());
                newFile.getParentFile().mkdirs();
                if (path.toString().endsWith(".adoc")) {
                    convert(path.toFile(), newFile);
                }
                else {
                    try {
                        Files.copy(path, newFile.toPath());
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    private void cleanOutputFolder(File output) throws IOException {
        try (Stream<Path> walk = Files.walk(output.toPath())) {
            walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
        output.mkdirs();
    }

    void convert(File input, File output) {
        Asciidoctor asciidoctor = Asciidoctor.Factory.create();
        asciidoctor.javaExtensionRegistry().preprocessor(new CoalescerPreprocessor(output));
        Options options = options(input, output);
        try {
            String fileAsString = new String(Files.readAllBytes(input.toPath()));
            asciidoctor.convert(fileAsString, options);
            log.info("Successfully converted the file from [" + input + "] to [" + output + "]\n");
        }
        catch (IOException ex) {
            throw new IllegalStateException("Failed to convert the file", ex);
        }
    }

    private Options options(File input, File output) {
        Attributes attributes = Attributes.builder()
            .allowUriRead(true)
            .attribute("project-root", output.getParent())
            .build();

        return Options.builder()
            .sourceDir(input.getParentFile())
            .baseDir(input.getParentFile())
            .attributes(attributes)
            .safe(SafeMode.UNSAFE)
            .parseHeaderOnly(true)
            .build();
    }

}
