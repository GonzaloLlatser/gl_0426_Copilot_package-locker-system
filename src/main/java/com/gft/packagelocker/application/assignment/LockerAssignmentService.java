package com.gft.packagelocker.application.assignment;

import com.gft.packagelocker.domain.model.Locker;
import com.gft.packagelocker.domain.model.LockerSize;
import com.gft.packagelocker.domain.model.LockerStatus;
import com.gft.packagelocker.infrastructure.repository.LockerRepository;
import com.gft.packagelocker.infrastructure.repository.PackageRepository;
import com.gft.packagelocker.domain.model.PackageStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class LockerAssignmentService {

    private final LockerRepository lockerRepository;
    private final PackageRepository packageRepository;

    public LockerAssignmentService(LockerRepository lockerRepository,
                                   PackageRepository packageRepository) {
        this.lockerRepository = lockerRepository;
        this.packageRepository = packageRepository;
    }

    public LockerAssignmentResult assign(LockerSize packageSize) {
        if (packageSize == null) {
            return LockerAssignmentResult.failure("INVALID_PACKAGE_SIZE");
        }

        List<Locker> candidates = lockerRepository.findByStatus(LockerStatus.AVAILABLE)
                .stream()
                .filter(locker -> !hasActivePackage(locker))
                .filter(locker -> locker.getSize().canFit(packageSize))
                .sorted(Comparator.comparing(Locker::getSize).thenComparing(Locker::getId))
                .toList();

        if (candidates.isEmpty()) {
            return LockerAssignmentResult.failure("NO_AVAILABLE_LOCKER");
        }

        return LockerAssignmentResult.success(candidates.get(0).getId());
    }

    private boolean hasActivePackage(Locker locker) {
        return packageRepository.existsByAssignedLockerIdAndStatusNot(
                locker.getId(), PackageStatus.PICKED_UP);
    }
}
