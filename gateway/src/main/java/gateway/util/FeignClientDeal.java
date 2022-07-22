package gateway.util;

import gateway.dto.ClientApplication;
import gateway.dto.FinishRegistrationRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "conveyorDeal",  url = "${deal.url}")
public interface FeignClientDeal {

    @RequestMapping(method = RequestMethod.GET, value = "/admin/application/{applicationId}")
    ClientApplication getClientApplication(@PathVariable Long applicationId);

    @RequestMapping(method = RequestMethod.GET, value = "/admin/application")
    List<ClientApplication> getAllClientApplications();

    @RequestMapping(method = RequestMethod.POST, value = "/document/{applicationId}/send")
     void sendCreateDocReq(@PathVariable Long applicationId);

    @RequestMapping(method = RequestMethod.POST, value = "/document/{applicationId}/sign")
    void sendDocSignReq(@PathVariable Long applicationId);

    @RequestMapping(method = RequestMethod.POST, value = "/document/{applicationId}/code")
    void sendCode(@PathVariable Long applicationId, @RequestBody int code);

    @RequestMapping(method = RequestMethod.PUT, value = "/calculate/{applicationId}")
    void sendFinishReg(@RequestBody FinishRegistrationRequestDTO finishRegistrationRequestDTO, @PathVariable Long applicationId);

    @RequestMapping(method = RequestMethod.POST, value = "/document/{applicationId}/deny")
    void denyApp(@PathVariable Long applicationId);
}
