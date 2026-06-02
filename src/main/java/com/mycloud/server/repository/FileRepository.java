package com.mycloud.server.repository;

import com.mycloud.server.model.File;
import com.mycloud.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByOwner(User owner);

    Optional<File> findByIdAndOwner(long id, User owner);

    // PostgreSQL REGEX: ~  (case-sensitive)
    @Query(
            value = "SELECT * FROM file f " +
                    "WHERE f.owner_id = :ownerId " +
                    "AND f.filename ~ '.*\\([0-9]+\\).*'",
            nativeQuery = true
    )
    List<File> findDuplicateCandidatesByOwner(@Param("ownerId") User owner);


    @Query("SELECT COALESCE(SUM(f.fileSize), 0) FROM File f WHERE f.owner = :owner")
    Long sumSizeByOwner(User owner);

    @Query("SELECT f.fileType, COUNT(f) FROM File f WHERE f.owner = :owner GROUP BY f.fileType")
    List<Object[]> countByFileType(User owner);

    @Query("SELECT f FROM File f WHERE f.owner = :owner ORDER BY f.fileName ASC")
    List<File> findAllByOwnerOrderByFileName(User owner);

    // Sum bytes per file type
    @Query("SELECT f.fileType, SUM(f.fileSize) FROM File f WHERE f.owner = :owner GROUP BY f.fileType")
    List<Object[]> sumBytesByFileType(User owner);
    long countByOwner(User owner);
    @Query("SELECT f FROM File f ORDER BY f.createdAt DESC")
    List<File> findAllFiles();

    @Query("SELECT SUM(f.fileSize) FROM File f")
    Long sumAllSizes();

    @Query("SELECT f.fileType, COUNT(f) FROM File f GROUP BY f.fileType")
    List<Object[]> countAllByFileType();

    @Query("SELECT f.fileType, SUM(f.fileSize) FROM File f GROUP BY f.fileType")
    List<Object[]> sumAllBytesByFileType();
}
