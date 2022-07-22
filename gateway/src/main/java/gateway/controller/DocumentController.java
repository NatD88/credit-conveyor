package gateway.controller;

import gateway.service.FeignServiceDealMs;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/document")
@Api(value = "document")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final FeignServiceDealMs feignServiceDealMs;

    @PostMapping("/{applicationId}")
    @ApiOperation(value = "requesting documents sending")
    public void createDocuments(@PathVariable Long applicationId) {
        log.info("applicationId entered createDocuments in DocumentController.  applicationId: {}", applicationId);
        feignServiceDealMs.sendCreateDocReq(applicationId);
    }

    @PostMapping("/{applicationId}/sign")
    @ApiOperation(value = "requesting documents signing")
    public void signRequestDocuments(@PathVariable Long applicationId) {
        log.info("applicationId entered signRequestDocuments in DocumentController.  applicationId: {}", applicationId);
        feignServiceDealMs.sendDocSignReq(applicationId);
    }

    @PostMapping("/{applicationId}/code")
    @ApiOperation(value = "approving documents")
    public void approveDocuments(@PathVariable Long applicationId, @RequestBody int code) {
        log.info("applicationId  ant code entered approveDocumentss in DocumentController.  applicationId: {}", applicationId);
        feignServiceDealMs.sendCodeCheckReq(applicationId, code);
    }
}
