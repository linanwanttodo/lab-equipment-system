package com.barezen.repository;

import com.barezen.entity.Equipment;
import com.barezen.entity.EquipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    boolean existsByCode(String code);

    java.util.Optional<Equipment> findByCode(String code);

    /**
     * 多条件可选筛选：实验室 / 类别 / 状态 / 关键字（名称或编号），传 null 表示该条件不参与。
     */
    @Query("""
            select e from Equipment e
            where (:labId is null or e.lab.id = :labId)
              and (:category is null or e.category = :category)
              and (:status is null or e.status = :status)
              and (:keyword is null or e.name like %:keyword% or e.code like %:keyword%)
            order by e.id desc
            """)
    List<Equipment> search(@Param("labId") Long labId,
                           @Param("category") String category,
                           @Param("status") EquipmentStatus status,
                           @Param("keyword") String keyword);

    /** 设备类别去重列表，用于筛选下拉 */
    @Query("select distinct e.category from Equipment e where e.category is not null and e.category <> '' order by e.category")
    List<String> findDistinctCategories();
}
