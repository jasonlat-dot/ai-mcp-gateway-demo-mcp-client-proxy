package com.jasonlat.mcp.trigger;

import com.jasonlat.mcp.api.IOpenAiApiProxy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author jasonlat
 * 2026-04-18  16:52
 */
@Slf4j
@RestController
@RequestMapping("/v1/")
@CrossOrigin(origins = "*")
public class OpenaiApiProxyController {

    @Resource
    private IOpenAiApiProxy openAiApiProxy;

    @RequestMapping(value = "chat/completions", method = {RequestMethod.POST})
    public Object completions(@RequestBody Object request) {
        log.info("request入参: {}", request);
        Object response = openAiApiProxy.completions(request).blockingGet();
        log.info("response结果: {}", response);
        return response;
    }

}
