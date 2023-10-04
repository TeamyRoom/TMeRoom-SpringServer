package org.finalproject.tmeroom.file.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum FileType {
    DOCUMENT("doc", "docx", "hwp", "pdf", "ppt", "psd", "txt", "xls", "xlsx"),
    IMAGE("bmp", "gif", "jpeg", "jpg", "png"),
    AUDIO("flac", "midi", "mp3", "wma", "wav", "rm"),
    VIDEO("flv", "mov", "mp4", "mpg", "mkv", "avi", "swf", "ts"),
    ARCHIVE("zip", "apk", "rar", "7z", "tar"),
    CODE("c", "c++", "cpp", "class", "cxx", "js", "pl", "py", "rb", "java", "h", "hxx", "css", "less", "scss", "sass", "json", "html", "htm", "xml", "toml", "yaml", "yml", "dev", "dsp", "dsw", "vcproj", "vcxproj", "sln", "classpath", "project", "vsix"),
    EXE("bat", "exe", "com", "apk", "dll", "iso", "lnk", "sys", "resx"),
    ETC;

    private final List<String> extensions;

    FileType(String... extensions) {
        this.extensions = Arrays.asList(extensions);
    }

    public List<String> getExtensions() {
        return extensions;
    }

    public static FileType fromExtension(String extension) {
        for (FileType type : FileType.values()) {
            if (type.extensions.contains(extension)) {
                return type;
            }
        }
        return FileType.ETC; // 일치하는 유형을 찾지 못한 경우 기본값 반환
    }
}
