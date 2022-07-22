package dossier.util;

import dossier.dto.ApplicationStatus;
import dossier.dto.ClientApplication;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(value = "dealController", url = "${deal.url}")
public interface FeignClientDeal {
    @RequestMapping(method = RequestMethod.GET, value = "/admin/application/{applicationId}")
    ClientApplication getClientApplication(@PathVariable Long applicationId);

    @RequestMapping(method = RequestMethod.PUT, value = "/admin/application/{applicationId}/status")
    void updateClientApplicationStatus(@PathVariable Long applicationId, ApplicationStatus applicationStatus);
}
