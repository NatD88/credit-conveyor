package gateway.controller;

import gateway.dto.ClientApplication;
import gateway.service.FeignServiceDealMs;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Api(value = "admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final FeignServiceDealMs feignServiceDealMs;

    @GetMapping("/application/{applicationId}")
    public ClientApplication getAppById(@PathVariable Long applicationId) {
        log.info("applicationId entered getAppById in AdminController ");
        return feignServiceDealMs.getClientAppById(applicationId);
    }

    @GetMapping("/application")
    public List<ClientApplication> getAllApps() {
        log.info("getAllApps in AdminController started ");
        return feignServiceDealMs.getAllApps();
    }
}
