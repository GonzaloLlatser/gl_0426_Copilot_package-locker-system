package com.gft.packagelocker.application.delivery;

import com.gft.packagelocker.application.assignment.LockerAssignmentResult;
import com.gft.packagelocker.application.assignment.LockerAssignmentService;
import com.gft.packagelocker.domain.model.Package;
import com.gft.packagelocker.domain.model.PackageStatus;
import com.gft.packagelocker.domain.model.PickupCode;
import com.gft.packagelocker.infrastructure.repository.CustomerRepository;
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
    private final PinGenerator pinGenerator;

    public PackageService(PackageRepository packageRepository,
                          CustomerRepository customerRepository,
                          PickupCodeRepository pickupCodeRepository,
                          LockerAssignmentService lockerAssignmentService,
                          PinGenerator pinGenerator) {
        this.packageRepository = packageRepository;
        this.customerRepository = customerRepository;
        this.pickupCodeRepository = pickupCodeRepository;
        this.lockerAssignmentService = lockerAssignmentService;
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
}
