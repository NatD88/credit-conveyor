package dossier.service;

import dossier.dto.ApplicationStatus;
import dossier.dto.ClientApplication;
import dossier.util.FeignClientDeal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeignServiceDeal {
    private final FeignClientDeal feignClientDeal;

    public ClientApplication getClientApplication(Long applicationId) {
        log.info("getClientApplication in FeignServiceDeal started. feignClient will send request");
        return feignClientDeal.getClientApplication(applicationId);
    }

    public void updateClientApplicationStatus(Long applicationId, ApplicationStatus applicationStatus) {
        log.info("updateClientApplicationStatus in FeignServiceDeal started. feignClient will send applicationStatus for updation");
        feignClientDeal.updateClientApplicationStatus(applicationId, applicationStatus);
    }
}
