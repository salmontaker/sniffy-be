package com.salmontaker.sniffy.admin.controller;

import com.salmontaker.sniffy.founditem.service.FoundItemBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final FoundItemBatchService foundItemBatchService;

    @PostMapping("/found-items/sync")
    public void doFoundItemBatchService() {
        foundItemBatchService.syncExternalData();
    }
}
