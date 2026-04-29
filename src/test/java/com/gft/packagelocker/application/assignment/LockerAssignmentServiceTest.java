package com.gft.packagelocker.application.assignment;

import com.gft.packagelocker.domain.model.Locker;
import com.gft.packagelocker.domain.model.LockerSize;
import com.gft.packagelocker.domain.model.LockerStatus;
import com.gft.packagelocker.domain.model.PackageStatus;
import com.gft.packagelocker.infrastructure.repository.LockerRepository;
import com.gft.packagelocker.infrastructure.repository.PackageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LockerAssignmentServiceTest {

    @Mock
    private LockerRepository lockerRepository;

    @Mock
    private PackageRepository packageRepository;

    @InjectMocks
    private LockerAssignmentService service;

    private UUID lockerSmallId;
    private UUID lockerMediumId;
    private UUID lockerLargeId;

    private Locker lockerS;
    private Locker lockerM;
    private Locker lockerL;

    @BeforeEach
    void setUp() {
        lockerSmallId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        lockerMediumId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        lockerLargeId = UUID.fromString("00000000-0000-0000-0000-000000000003");

        lockerS = new Locker(lockerSmallId, "S-01", LockerSize.S, LockerStatus.AVAILABLE);
        lockerM = new Locker(lockerMediumId, "M-01", LockerSize.M, LockerStatus.AVAILABLE);
        lockerL = new Locker(lockerLargeId, "L-01", LockerSize.L, LockerStatus.AVAILABLE);
    }

    @Test
    void assign_smallPackage_selectsSmallestFittingLocker() {
        when(lockerRepository.findByStatus(LockerStatus.AVAILABLE))
                .thenReturn(List.of(lockerS, lockerM, lockerL));
        when(packageRepository.existsByAssignedLockerIdAndStatusNot(any(), eq(PackageStatus.PICKED_UP)))
                .thenReturn(false);

        LockerAssignmentResult result = service.assign(LockerSize.S);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getLockerId()).isEqualTo(lockerSmallId);
    }

    @Test
    void assign_mediumPackage_skipsTooSmallLockers() {
        when(lockerRepository.findByStatus(LockerStatus.AVAILABLE))
                .thenReturn(List.of(lockerS, lockerM, lockerL));
        when(packageRepository.existsByAssignedLockerIdAndStatusNot(any(), eq(PackageStatus.PICKED_UP)))
                .thenReturn(false);

        LockerAssignmentResult result = service.assign(LockerSize.M);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getLockerId()).isEqualTo(lockerMediumId);
    }

    @Test
    void assign_largePackage_selectsLargeLocker() {
        when(lockerRepository.findByStatus(LockerStatus.AVAILABLE))
                .thenReturn(List.of(lockerS, lockerM, lockerL));
        when(packageRepository.existsByAssignedLockerIdAndStatusNot(any(), eq(PackageStatus.PICKED_UP)))
                .thenReturn(false);

        LockerAssignmentResult result = service.assign(LockerSize.L);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getLockerId()).isEqualTo(lockerLargeId);
    }

    @Test
    void assign_noAvailableLockers_returnsNoAvailableLockerFailure() {
        when(lockerRepository.findByStatus(LockerStatus.AVAILABLE))
                .thenReturn(List.of());

        LockerAssignmentResult result = service.assign(LockerSize.S);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getReason()).isEqualTo("NO_AVAILABLE_LOCKER");
    }

    @Test
    void assign_allLockersTooSmall_returnsNoAvailableLockerFailure() {
        when(lockerRepository.findByStatus(LockerStatus.AVAILABLE))
                .thenReturn(List.of(lockerS, lockerM));
        when(packageRepository.existsByAssignedLockerIdAndStatusNot(any(), eq(PackageStatus.PICKED_UP)))
                .thenReturn(false);

        LockerAssignmentResult result = service.assign(LockerSize.L);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getReason()).isEqualTo("NO_AVAILABLE_LOCKER");
    }

    @Test
    void assign_smallestLockerHasActivePackage_selectsNextFitting() {
        when(lockerRepository.findByStatus(LockerStatus.AVAILABLE))
                .thenReturn(List.of(lockerS, lockerM));
        when(packageRepository.existsByAssignedLockerIdAndStatusNot(eq(lockerSmallId), eq(PackageStatus.PICKED_UP)))
                .thenReturn(true);
        when(packageRepository.existsByAssignedLockerIdAndStatusNot(eq(lockerMediumId), eq(PackageStatus.PICKED_UP)))
                .thenReturn(false);

        LockerAssignmentResult result = service.assign(LockerSize.S);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getLockerId()).isEqualTo(lockerMediumId);
    }

    @Test
    void assign_nullPackageSize_returnsInvalidSizeFailure() {
        LockerAssignmentResult result = service.assign(null);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getReason()).isEqualTo("INVALID_PACKAGE_SIZE");
    }

    @Test
    void assign_deterministic_sameInputAlwaysSameOutput() {
        UUID id1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID id2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
        Locker locker1 = new Locker(id1, "M-01", LockerSize.M, LockerStatus.AVAILABLE);
        Locker locker2 = new Locker(id2, "M-02", LockerSize.M, LockerStatus.AVAILABLE);

        when(lockerRepository.findByStatus(LockerStatus.AVAILABLE))
                .thenReturn(List.of(locker2, locker1));
        when(packageRepository.existsByAssignedLockerIdAndStatusNot(any(), eq(PackageStatus.PICKED_UP)))
                .thenReturn(false);

        LockerAssignmentResult first = service.assign(LockerSize.M);
        LockerAssignmentResult second = service.assign(LockerSize.M);

        assertThat(first.getLockerId()).isEqualTo(second.getLockerId()).isEqualTo(id1);
    }
}
