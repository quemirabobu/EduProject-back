package com.bit.eduventure.gps.Repository;



import com.bit.eduventure.gps.Entity.GPS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GPSRepository extends JpaRepository<GPS, Long> {



//    @Query(value = "SELECT *  " +
//            "FROM gps  " +
//            "WHERE carnumber = 2  " +
//            "ORDER BY id DESC  " +
//            "LIMIT 1;", nativeQuery = true)
//    List<GPS> findbylast();


    @Query(value =
            "SELECT g.* FROM (" +
                    "SELECT g1.*, ROW_NUMBER() OVER (PARTITION BY g1.carnumber ORDER BY g1.id DESC) as rn " +
                    "FROM gps g1" +
                    ") g WHERE g.rn = 1",
            nativeQuery = true)
    List<GPS> findLatestForEachCarNumber();


}
