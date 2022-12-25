package com.project.CmsApplication.controller;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.project.CmsApplication.Services.CmsServices;
import com.project.CmsApplication.model.BaseResponse;
import com.project.CmsApplication.model.Resource;
import com.project.CmsApplication.repository.ResourceRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/resource")
public class ResourceController {
    @Autowired
    ResourceRepository resourceRepository;
    @Autowired
    CmsServices cmsServices;

    @Value("${attachment.path.resource}")
    private String attachmentPathResource;

    @Value("${sftp.user.name}")
    private String sftpUser;

    @Value("${sftp.user.password}")
    private String sftpPassword;

    @Value("${sftp.url}")
    private String sftpUrl;
    Logger logger = LoggerFactory.getLogger(ResourceController.class);

    @PostMapping("/getResource")
    public BaseResponse<List<Resource>> getResource(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : getResource - " + input);
        return cmsServices.getResourceList(input);
    }

    @PostMapping("/getResourceAll")
    public BaseResponse<List<Resource>> getResourceAll(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : getResourceAll - " + input);
        return cmsServices.getResourceListAll(input);
    }

    @PostMapping("/addNewResource")
    public BaseResponse<String> addNewResource(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : addNewResource - ");
        return cmsServices.addNewResource(input);
    }

    @PostMapping("/updateResource")
    public BaseResponse<Resource> updateResource(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : updateResource - " + input);
        return cmsServices.updateResource(input);
    }

    @PostMapping("/deleteResource")
    public BaseResponse<Resource> deleteResource(@RequestBody String input) throws Exception, SQLException, ParseException {
        logger.info(new Date().getTime() + " : deleteResource - " + input);
        return cmsServices.deleteResource(input);
    }


    @GetMapping(value = "/downloadResource/{file_name}", produces = "application/octet-stream")
    public ResponseEntity<?> getDetailReportFile(@PathVariable("file_name") String file_name) throws Exception {
        Session session = null;
        ChannelSftp channel = null;
        ByteArrayResource resource = null;
        HttpHeaders headers = new HttpHeaders();
        try {
            session = new JSch().getSession(sftpUser, sftpUrl, 22);
            session.setPassword(sftpPassword);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();

            resource = new ByteArrayResource(IOUtils.toByteArray(channel.get(attachmentPathResource + file_name)));
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            headers.add("Content-Disposition", "attachment;filename=" + file_name);
            headers.add("Access-Control-Expose-Headers", "Content-Disposition");

            return ResponseEntity.ok()
                    .headers(headers).contentLength(resource.contentLength())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);

        } catch (Exception e) {
            return new ResponseEntity<>("BAD_REQUEST", HttpStatus.BAD_REQUEST);
        } finally {
            if (session.isConnected() || session != null) {
                session.disconnect();
            }
            if (channel.isConnected() || channel != null) {
                channel.disconnect();
            }

        }


    }
}
