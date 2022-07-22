package gateway.service;

import gateway.dto.ClientApplication;
import gateway.dto.FinishRegistrationRequestDTO;
import gateway.util.FeignClientDeal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeignServiceDealMs {
    private final FeignClientDeal feignClientDeal;

    public ClientApplication getClientAppById(Long applicationId) {
        log.info("applicationId entered getClientAppById in FeignServiceDealMs. applicationId: {} ", applicationId);
        return feignClientDeal.getClientApplication(applicationId);
    }

    public List<ClientApplication> getAllApps() {
        log.info("getAllApps in FeignServiceDealMs started");
        return feignClientDeal.getAllClientApplications();
    }

    public void sendCreateDocReq(Long applicationId) {
        log.info("applicationId entered sendCreateDocReq in FeignServiceDealMs. applicationId: {} ", applicationId);
        feignClientDeal.sendCreateDocReq(applicationId);
    }

    public void sendDocSignReq(Long applicationId) {
        log.info("applicationId entered sendDocSignReq in FeignServiceDealMs. applicationId: {} ", applicationId);
        feignClientDeal.sendDocSignReq(applicationId);
    }

    public void sendCodeCheckReq(Long applicationId, int code) {
        log.info("applicationId and code entered sendCodeCheckReq in FeignServiceDealMs. applicationId: {} ", applicationId);
        feignClientDeal.sendCode(applicationId, code);
    }

    public void finishReg(FinishRegistrationRequestDTO finishRegistrationRequestDTO, Long applicationId) {
        log.info("applicationId and finishRegistrationRequestDTO entered finishReg in FeignServiceDealMs. applicationId: {} ", applicationId);
        feignClientDeal.sendFinishReg(finishRegistrationRequestDTO, applicationId);
    }

    public void denyApp(Long applicationId) {
        log.info("applicationId entered denyApp in FeignServiceDealMs. applicationId: {} ", applicationId);
        feignClientDeal.denyApp(applicationId);
    }
}
