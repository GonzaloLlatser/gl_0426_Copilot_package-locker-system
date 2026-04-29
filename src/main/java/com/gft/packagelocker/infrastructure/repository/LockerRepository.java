package com.gft.packagelocker.infrastructure.repository;

import com.gft.packagelocker.domain.model.Locker;
import com.gft.packagelocker.domain.model.LockerSize;
import com.gft.packagelocker.domain.model.LockerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LockerRepository extends JpaRepository<Locker, UUID> {

    List<Locker> findByStatus(LockerStatus status);

    List<Locker> findByStatusAndSizeOrderByIdAsc(LockerStatus status, LockerSize size);

    List<Locker> findByStatusOrderBySizeAscIdAsc(LockerStatus status);
}
