package org.finalproject.tmeroom.file.repository;

import org.finalproject.tmeroom.file.data.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
