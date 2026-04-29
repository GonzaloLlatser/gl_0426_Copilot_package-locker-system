package com.gft.packagelocker.application.delivery;

import com.gft.packagelocker.application.assignment.LockerAssignmentResult;
import com.gft.packagelocker.application.assignment.LockerAssignmentService;
import com.gft.packagelocker.domain.model.LockerStatus;
import com.gft.packagelocker.domain.model.Package;
import com.gft.packagelocker.domain.model.PackageStatus;
import com.gft.packagelocker.domain.model.PickupCode;
import com.gft.packagelocker.infrastructure.repository.CustomerRepository;
import com.gft.packagelocker.infrastructure.repository.LockerRepository;
import com.gft.packagelocker.infrastructure.repository.PackageRepository;
import com.gft.packagelocker.infrastructure.repository.PickupCodeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PackageService {

    private final PackageRepository packageRepository;
    private final CustomerRepository customerRepository;
    private final PickupCodeRepository pickupCodeRepository;
    private final LockerAssignmentService lockerAssignmentService;
    private final LockerRepository lockerRepository;
    private final PinGenerator pinGenerator;

    public PackageService(PackageRepository packageRepository,
                          CustomerRepository customerRepository,
                          PickupCodeRepository pickupCodeRepository,
                          LockerAssignmentService lockerAssignmentService,
                          LockerRepository lockerRepository,
                          PinGenerator pinGenerator) {
        this.packageRepository = packageRepository;
        this.customerRepository = customerRepository;
        this.pickupCodeRepository = pickupCodeRepository;
        this.lockerAssignmentService = lockerAssignmentService;
        this.lockerRepository = lockerRepository;
        this.pinGenerator = pinGenerator;
    }

    @Transactional
    public CreateDeliveryResponse createDelivery(CreateDeliveryRequest request) {
        UUID customerId = request.getCustomerId();
        if (customerId == null || !customerRepository.existsById(customerId)) {
            throw new IllegalArgumentException("Customer not found: " + request.getCustomerId());
        }

        Package pkg = new Package();
        pkg.setCustomerId(customerId);
        pkg.setSize(request.getPackageSize());
        pkg.setStatus(PackageStatus.CREATED);
        pkg = packageRepository.save(pkg);

        LockerAssignmentResult assignment = lockerAssignmentService.assign(request.getPackageSize());

        if (!assignment.isSuccess()) {
            return CreateDeliveryResponse.failure(pkg.getId(), assignment.getReason());
        }

        pkg.setStatus(PackageStatus.ASSIGNED);
        pkg.setAssignedLockerId(assignment.getLockerId());
        packageRepository.save(pkg);

        String pin = pinGenerator.generate();
        PickupCode pickupCode = new PickupCode(null, pkg.getId(), pin, true);
        pickupCodeRepository.save(pickupCode);

        return CreateDeliveryResponse.success(pkg.getId(), assignment.getLockerId(), pin);
    }

    @Transactional
    public Package confirmDeposit(UUID deliveryId) {
        Package pkg = packageRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found: " + deliveryId));

        if (pkg.getStatus() != PackageStatus.ASSIGNED) {
            throw new IllegalStateException("Delivery is not in ASSIGNED state: " + pkg.getStatus());
        }

        pkg.setStatus(PackageStatus.DEPOSITED);

        if (pkg.getAssignedLockerId() != null) {
            lockerRepository.findById(pkg.getAssignedLockerId()).ifPresent(locker -> {
                locker.setStatus(LockerStatus.OCCUPIED);
                lockerRepository.save(locker);
            });
        }

        return packageRepository.save(pkg);
    }

    @Transactional
    public Package pickup(UUID deliveryId, String pin) {
        Package pkg = packageRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found: " + deliveryId));

        if (pkg.getStatus() == PackageStatus.PICKED_UP) {
            throw new IllegalStateException("Package already picked up");
        }
        if (pkg.getStatus() != PackageStatus.DEPOSITED) {
            throw new IllegalStateException("Package is not available for pickup: " + pkg.getStatus());
        }

        PickupCode code = pickupCodeRepository
                .findByDeliveryIdAndPinAndActiveTrue(deliveryId, pin)
                .orElseThrow(() -> new IllegalArgumentException("Invalid PIN"));

        pkg.setStatus(PackageStatus.PICKED_UP);
        packageRepository.save(pkg);

        code.setActive(false);
        pickupCodeRepository.save(code);

        if (pkg.getAssignedLockerId() != null) {
            lockerRepository.findById(pkg.getAssignedLockerId()).ifPresent(locker -> {
                locker.setStatus(LockerStatus.AVAILABLE);
                lockerRepository.save(locker);
            });
        }

        return pkg;
    }

    public Package getDelivery(UUID deliveryId) {
        return packageRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found: " + deliveryId));
    }
}
