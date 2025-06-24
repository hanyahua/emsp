package com.volvo.emsp.infrastructure.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SpringDomainEventRepository extends JpaRepository<DomainEventStoreModel, String> {

    List<DomainEventStoreModel> findByStatusOrderByTimestampAsc(String status);
}