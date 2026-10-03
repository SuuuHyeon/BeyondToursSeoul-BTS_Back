package com.beyondtoursseoul.bts.repository;

import com.beyondtoursseoul.bts.domain.Attraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface AttractionRepository extends JpaRepository<Attraction, Long> {

    List<Attraction> findByDetailFetchedFalseAndExternalIdNotNull();

    // [탐색 화면 · 페이지] 카테고리 없음
    @Query("""
        select a, s from Attraction a
        join AttractionLocalScore s on s.id.attractionId = a.id
        where s.id.date = :date
          and s.id.timeSlot = :timeSlot
          and (:minScore is null or s.score >= :minScore)
          and (:maxScore is null or s.score <= :maxScore)
        order by s.score desc nulls last
        """)
    Page<Object[]> findForPage(@Param("date") LocalDate date,
                               @Param("timeSlot") String timeSlot,
                               @Param("minScore") BigDecimal minScore,
                               @Param("maxScore") BigDecimal maxScore,
                               Pageable pageable);

    // [탐색 화면 · 페이지] 카테고리 있음
    @Query("""
        select a, s from Attraction a
        join AttractionLocalScore s on s.id.attractionId = a.id
        where s.id.date = :date
          and s.id.timeSlot = :timeSlot
          and (:minScore is null or s.score >= :minScore)
          and (:maxScore is null or s.score <= :maxScore)
          and (a.cat1 in :codes or a.cat2 in :codes or a.cat3 in :codes)
        order by s.score desc nulls last
        """)
    Page<Object[]> findForPageByCategoryCodes(@Param("date") LocalDate date,
                                              @Param("timeSlot") String timeSlot,
                                              @Param("minScore") BigDecimal minScore,
                                              @Param("maxScore") BigDecimal maxScore,
                                              @Param("codes") Collection<String> codes,
                                              Pageable pageable);

    // [지도 화면 · 전체] 카테고리 없음
    @Query("""
        select a, s from Attraction a
        join AttractionLocalScore s on s.id.attractionId = a.id
        where s.id.date = :date
          and s.id.timeSlot = :timeSlot
          and (:minScore is null or s.score >= :minScore)
          and (:maxScore is null or s.score <= :maxScore)
        order by s.score desc nulls last
        """)
    List<Object[]> findForMap(@Param("date") LocalDate date,
                              @Param("timeSlot") String timeSlot,
                              @Param("minScore") BigDecimal minScore,
                              @Param("maxScore") BigDecimal maxScore);

    // [지도 화면 · 전체] 카테고리 있음
    @Query("""
        select a, s from Attraction a
        join AttractionLocalScore s on s.id.attractionId = a.id
        where s.id.date = :date
          and s.id.timeSlot = :timeSlot
          and (:minScore is null or s.score >= :minScore)
          and (:maxScore is null or s.score <= :maxScore)
          and (a.cat1 in :codes or a.cat2 in :codes or a.cat3 in :codes)
        order by s.score desc nulls last
        """)
    List<Object[]> findForMapByCategoryCodes(@Param("date") LocalDate date,
                                             @Param("timeSlot") String timeSlot,
                                             @Param("minScore") BigDecimal minScore,
                                             @Param("maxScore") BigDecimal maxScore,
                                             @Param("codes") Collection<String> codes);


    /**
     * 기준점에서 가장 가까운 관광지(제외 ID 제외). PostGIS geography 거리(m) 기준.
     */
    @Query(value = """
            SELECT a.id FROM attraction a
            WHERE a.geom IS NOT NULL
              AND a.id NOT IN (:excludeIds)
            ORDER BY ST_Distance(
                a.geom::geography,
                ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography
            )
            LIMIT 1
            """, nativeQuery = true)
    List<Long> findNearestAttractionIdExcluding(
            @Param("lon") double lon,
            @Param("lat") double lat,
            @Param("excludeIds") Collection<Long> excludeIds);

    /**
     * 기준점에서 {@code minMeters} 이상 떨어진 가장 가까운 관광지(제외 ID 제외).
     */
    @Query(value = """
            SELECT a.id FROM attraction a
            WHERE a.geom IS NOT NULL
              AND a.id NOT IN (:excludeIds)
              AND ST_Distance(
                a.geom::geography,
                ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography
              ) >= :minMeters
            ORDER BY ST_Distance(
                a.geom::geography,
                ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography
            )
            LIMIT 1
            """, nativeQuery = true)
    List<Long> findNearestAttractionIdBeyondMetersExcluding(
            @Param("lon") double lon,
            @Param("lat") double lat,
            @Param("minMeters") double minMeters,
            @Param("excludeIds") Collection<Long> excludeIds);
}
