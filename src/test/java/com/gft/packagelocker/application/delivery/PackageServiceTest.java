package com.gft.packagelocker.application.delivery;

import com.gft.packagelocker.application.assignment.LockerAssignmentResult;
import com.gft.packagelocker.application.assignment.LockerAssignmentService;
import com.gft.packagelocker.domain.model.LockerSize;
import com.gft.packagelocker.domain.model.Package;
import com.gft.packagelocker.domain.model.PackageStatus;
import com.gft.packagelocker.domain.model.PickupCode;
import com.gft.packagelocker.infrastructure.repository.CustomerRepository;
import com.gft.packagelocker.infrastructure.repository.PackageRepository;
import com.gft.packagelocker.infrastructure.repository.PickupCodeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
class PackageServiceTest {

    @Mock
    private PackageRepository packageRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PickupCodeRepository pickupCodeRepository;

    @Mock
    private LockerAssignmentService lockerAssignmentService;

    @Mock
    private PinGenerator pinGenerator;

    @InjectMocks
    private PackageService packageService;

    private final UUID customerId = UUID.randomUUID();
    private final UUID lockerId = UUID.randomUUID();
    private final UUID packageId = UUID.randomUUID();

    @Test
    void createDelivery_happyPath_returnsSuccessWithPinAndLockerId() {
        CreateDeliveryRequest request = new CreateDeliveryRequest(customerId, LockerSize.M);

        Package savedPackage = new Package(packageId, customerId, LockerSize.M, PackageStatus.CREATED, null);

        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(packageRepository.save(any(Package.class))).thenReturn(savedPackage);
        when(lockerAssignmentService.assign(LockerSize.M)).thenReturn(LockerAssignmentResult.success(lockerId));
        when(pinGenerator.generate()).thenReturn("123456");

        CreateDeliveryResponse response = packageService.createDelivery(request);

        assertThat(response.isAssigned()).isTrue();
        assertThat(response.getDeliveryId()).isEqualTo(packageId);
        assertThat(response.getLockerId()).isEqualTo(lockerId);
        assertThat(response.getPin()).isEqualTo("123456");
        assertThat(response.getFailureReason()).isNull();
    }

    @Test
    void createDelivery_customerNotFound_throwsIllegalArgumentException() {
        CreateDeliveryRequest request = new CreateDeliveryRequest(customerId, LockerSize.S);

        when(customerRepository.existsById(customerId)).thenReturn(false);

        assertThatThrownBy(() -> packageService.createDelivery(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(customerId.toString());

        verify(packageRepository, never()).save(any(Package.class));
    }

    @Test
    void createDelivery_noLockerAvailable_returnsFailureResponse() {
        CreateDeliveryRequest request = new CreateDeliveryRequest(customerId, LockerSize.L);

        Package savedPackage = new Package(packageId, customerId, LockerSize.L, PackageStatus.CREATED, null);

        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(packageRepository.save(any(Package.class))).thenReturn(savedPackage);
        when(lockerAssignmentService.assign(LockerSize.L))
                .thenReturn(LockerAssignmentResult.failure("NO_AVAILABLE_LOCKER"));

        CreateDeliveryResponse response = packageService.createDelivery(request);

        assertThat(response.isAssigned()).isFalse();
        assertThat(response.getDeliveryId()).isEqualTo(packageId);
        assertThat(response.getFailureReason()).isEqualTo("NO_AVAILABLE_LOCKER");
        assertThat(response.getPin()).isNull();
        assertThat(response.getLockerId()).isNull();

        verify(pickupCodeRepository, never()).save(any(PickupCode.class));
    }

    @Test
    void createDelivery_assignmentSuccess_savesPackageAsAssignedWithLockerId() {
        CreateDeliveryRequest request = new CreateDeliveryRequest(customerId, LockerSize.S);

        Package savedPackage = new Package(packageId, customerId, LockerSize.S, PackageStatus.CREATED, null);

        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(packageRepository.save(any(Package.class))).thenReturn(savedPackage);
        when(lockerAssignmentService.assign(LockerSize.S)).thenReturn(LockerAssignmentResult.success(lockerId));
        when(pinGenerator.generate()).thenReturn("654321");

        packageService.createDelivery(request);

        ArgumentCaptor<Package> packageCaptor = ArgumentCaptor.forClass(Package.class);
        verify(packageRepository, org.mockito.Mockito.times(2)).save(packageCaptor.capture());
        Package assignedPackage = packageCaptor.getAllValues().get(1);
        assertThat(assignedPackage.getStatus()).isEqualTo(PackageStatus.ASSIGNED);
        assertThat(assignedPackage.getAssignedLockerId()).isEqualTo(lockerId);
    }

    @Test
    void createDelivery_assignmentSuccess_savesPickupCodeWithCorrectPin() {
        CreateDeliveryRequest request = new CreateDeliveryRequest(customerId, LockerSize.M);

        Package savedPackage = new Package(packageId, customerId, LockerSize.M, PackageStatus.CREATED, null);

        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(packageRepository.save(any(Package.class))).thenReturn(savedPackage);
        when(lockerAssignmentService.assign(LockerSize.M)).thenReturn(LockerAssignmentResult.success(lockerId));
        when(pinGenerator.generate()).thenReturn("999888");

        ArgumentCaptor<PickupCode> codeCaptor = ArgumentCaptor.forClass(PickupCode.class);

        packageService.createDelivery(request);

        verify(pickupCodeRepository).save(codeCaptor.capture());
        PickupCode saved = codeCaptor.getValue();
        assertThat(saved.getPin()).isEqualTo("999888");
        assertThat(saved.getDeliveryId()).isEqualTo(packageId);
        assertThat(saved.isActive()).isTrue();
    }
}
