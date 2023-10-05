package org.finalproject.tmeroom.file.data.entity;

import lombok.Getter;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Getter
public enum FileType {
    DOCUMENT("doc", "docx", "hwp", "pdf", "ppt", "psd", "txt", "xls", "xlsx"),
    IMAGE("bmp", "gif", "jpeg", "jpg", "png"),
    AUDIO("flac", "midi", "mp3", "wma", "wav", "rm"),
    VIDEO("flv", "mov", "mp4", "mpg", "mkv", "avi", "swf", "ts"),
    ARCHIVE("zip", "apk", "rar", "7z", "tar"),
    CODE("c", "c++", "cpp", "class", "cxx", "js", "pl", "py", "rb", "java", "h", "hxx", "css", "less", "scss", "sass", "json", "html", "htm", "xml", "toml", "yaml", "yml", "dev", "dsp", "dsw", "vcproj", "vcxproj", "sln", "classpath", "project", "vsix"),
    EXE("bat", "exe", "com", "dll", "iso", "lnk", "sys", "resx"),
    ETC;

    private final List<String> extensions;
    private static final Map<String, FileType> stringFileTypeMap =
            Stream.of(values())
                    .flatMap(type -> type.extensions.stream()
                            .map(extension-> new SimpleEntry<>(extension, type)))
                    .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));

    FileType(String... extensions) {
        this.extensions = Arrays.asList(extensions);
    }

    public List<String> getExtensions() {
        return extensions;
    }

    public static FileType fromExtension(String extension) {
        return stringFileTypeMap.getOrDefault(extension, ETC);
    }
}
