package com.project.CmsApplication.controller;

import com.project.CmsApplication.Services.CmsServices;
import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.Configuration;
import com.project.CmsApplication.model.Users;
import com.project.CmsApplication.repository.ConfigurationRepository;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/utility")
public class UtilityController {

    Logger logger = LoggerFactory.getLogger(UtilityController.class);

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Autowired
    CmsServices cmsServices;

    @Autowired
    ConfigurationRepository configurationRepository;

    @GetMapping("/testService")
    public BaseResponse<String> testService() throws Exception {
        logger.info(new Date().getTime() + " : Test Service");
        BaseResponse baseResponse = new BaseResponse<>();

        baseResponse.setStatus("2000");
        baseResponse.setSuccess(true);
        baseResponse.setMessage("Service on");
        return baseResponse;
    }

    @PostMapping("/addFile")
    public BaseResponse<Map<String, String>> addFile(@RequestBody String input) throws Exception {
        logger.info(new Date().getTime() + " : Add File test");
        BaseResponse baseResponse = new BaseResponse<>();
        JSONObject jsonObject = new JSONObject(input);
        return cmsServices.addFile(jsonObject.optString("file_name"), jsonObject.optString("file_content"), jsonObject.optString("folder"));
    }

    @PostMapping("/uploadFile")
    public BaseResponse<String> uploadFile(@RequestParam MultipartFile obj, @RequestParam String folder) throws Exception {
        logger.info(new Date().getTime() + " : Upload multipart file");
        BaseResponse baseResponse = new BaseResponse<>();
        return cmsServices.uploadFile(obj, folder);
    }

//    @PostMapping("/downloadFile")
//    public ResponseEntity<InputStreamResource> downloadFile(@RequestBody String input) throws Exception {
//        logger.info(new Date().getTime() + " : Upload multipart file");
//        BaseResponse baseResponse = new BaseResponse<>();
//        JSONObject jsonInput = new JSONObject(input);
//        InputStreamResource file = cmsServices.downloadFile(jsonInput.getString("file_name"), jsonInput.getString("folder"));
//        return ResponseEntity.ok()
//                .contentType(MediaType.ALL)
//                .header(HttpHeaders.CONTENT_DISPOSITION)
//                .body(file);
//    }

    @GetMapping("/downloadFile/{folder:.+}/{fileName:.+}")
    public void downloadFIleFromServer(HttpServletRequest request, HttpServletResponse response,
                                       @PathVariable("folder") String folder,
                                       @PathVariable("fileName") String fileName) throws IOException {
        InputStream file = cmsServices.downloadFile(fileName, folder);
        response.setContentType("application/octet-stream");
        response.setContentLength(IOUtils.toByteArray(file).length);
        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + fileName + "\""));
        try {
            FileCopyUtils.copy(file, response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @PostMapping("/getFile")
    public BaseResponse<Map<String, Object>> getFileByName(@RequestBody String input) throws Exception {
        logger.info(new Date().getTime() + " : Get File");
        JSONObject jsonObject = new JSONObject(input);
        return cmsServices.getFile(jsonObject.optString("file_name"), jsonObject.optString("folder"));
    }

    @PostMapping("/getConfig")
    public BaseResponse<List<Configuration>> getConfig() {
        BaseResponse<List<Configuration>> result = new BaseResponse<>();
        result.setData(configurationRepository.findAll());
        result.setStatus("2000");
        result.setSuccess(true);
        result.setMessage("Config listed");
        return result;
    }

    @GetMapping("/getDbCredential")
    public BaseResponse<List<Configuration>> getDbCredential() {
        logger.info("Get database credential");
        return cmsServices.getDbCredential();
    }

    @PostMapping("/addConfig")
    public BaseResponse<List<Configuration>> addConfig(@RequestBody String input) throws Exception {
        logger.info("Add new configuration");

        return cmsServices.addConfig(input);
    }

    @PostMapping("/updateConfig")
    public BaseResponse<List<Configuration>> updateConfig(@RequestBody String input) {
        BaseResponse<List<Configuration>> result = new BaseResponse<>();
        JSONObject jsonInput = null;
        try {
            jsonInput = new JSONObject(input);
        } catch (JSONException e) {
            result.setStatus("0");
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
        if (jsonInput != null && !jsonInput.optString("configuration_value").equals("") && jsonInput.optInt("configuration_id") != 0) {
            configurationRepository.updateConfiguration(jsonInput.optString("configuration_value"), jsonInput.optInt("configuration_id"));
            result.setStatus("2000");
            result.setSuccess(true);
            result.setMessage("Config " + jsonInput.optString("configuration_name") + " updated");
        } else {
            result.setStatus("0");
            result.setSuccess(false);
            result.setMessage("Some field is empty");
        }
        return result;
    }

    @PostMapping("/testParsePPT")
    public BaseResponse testParsePPT(@RequestBody String input) {
        logger.info(new Date().getTime() + " : Parse PPT");
        BaseResponse result = new BaseResponse<>();
//            executor.submit(() -> {
        try {
            JSONObject jsonInput = new JSONObject(input);
            List<String> res = cmsServices.ParseToImage(jsonInput.getString("file_name"));
            result.setData(res);
            result.setStatus("200");
            result.setSuccess(true);
            result.setMessage("PPT file successfully parse to image : " + res.size());
        } catch (IOException e) {
            result.setStatus("500");
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setStatus("500");
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
//            });

        return result;
    }

    @PostMapping("/testGetImageParsed")
    public BaseResponse testGetImageParsed(@RequestBody String input) {
        logger.info(new Date().getTime() + " : get PPT image");
        BaseResponse result = new BaseResponse<>();
//            executor.submit(() -> {
        try {
            JSONObject jsonInput = new JSONObject(input);
            List<String> res = cmsServices.getPresentationImage(jsonInput.getString("file_name").split("\\.")[0]);
            result.setData(res);
            result.setStatus("200");
            result.setSuccess(true);
            result.setMessage("PPT Image parsed list : " + res.size());
        } catch (IOException e) {
            result.setStatus("500");
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setStatus("500");
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
//            });

        return result;
    }

//    @PostMapping("/queryBuilder")
//    public BaseResponse<List<Users>> queryBuilder(@RequestBody String input) throws Exception {
//        logger.info(new Date().getTime() + " : test query");
//        JSONObject jsonObject = new JSONObject(input);
//        return cmsServices.queryBuilder();
//    }
}
