package com.fastcampus.loan.controller;

import com.fastcampus.loan.dto.ApplicationDTO.*;
import com.fastcampus.loan.dto.FileDTO;
import com.fastcampus.loan.dto.ResponseDTO;
import com.fastcampus.loan.service.ApplicationService;
import com.fastcampus.loan.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/applications")
public class ApplicationController extends AbstractController {
    private final ApplicationService applicationService;
    private final FileStorageService fileStorageService;

    @PostMapping
    public ResponseDTO<Response> create(@RequestBody Request request) {
        return ok(applicationService.create(request));
    }

    @GetMapping("/{applicationId}")
    public ResponseDTO<Response> get(@PathVariable Long applicationId) {
        return ok(applicationService.get(applicationId));
    }

    @PutMapping("/{applicationId}")
    public ResponseDTO<Response> update(@PathVariable Long applicationId, @RequestBody Request request) {
        return ok(applicationService.update(applicationId, request));
    }

    @DeleteMapping("/{applicationId}")
    public ResponseDTO<Void> delete(@PathVariable Long applicationId) {
        applicationService.delete(applicationId);
        return ok();
    }

    @PostMapping("/{applicationId}/terms")
    public ResponseDTO<Boolean> acceptTerms(@PathVariable Long applicationId, @RequestBody AcceptTerms request) {
        return ok(applicationService.acceptTerms(applicationId, request));
    }

    @PostMapping("/{applicationId}/files")
    public ResponseDTO<Void> upload(@PathVariable Long applicationId, MultipartFile file) {
        fileStorageService.save(applicationId, file);
        return ok();
    }

    @GetMapping("/{applicationId}/files")
    public ResponseEntity<Resource> download(@PathVariable Long applicationId, @RequestParam(value = "filename") String filename) {
        Resource file = fileStorageService.load(applicationId, filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION
                , "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("/{applicationId}/files/infos")
    public ResponseDTO<List<FileDTO>> getFileInfos(@PathVariable Long applicationId) {
        List<FileDTO> fileInfos = fileStorageService.loadAll(applicationId).map(path -> {
            String fileName = path.getFileName().toString();
            return FileDTO.builder()
                    .name(fileName)
                    .url(MvcUriComponentsBuilder.fromMethodName(ApplicationController.class, "download", fileName).build().toString())
                    .build();
        }).collect(Collectors.toList());

        return ok(fileInfos);
    }

    @DeleteMapping("/{applicationId}/files")
    public ResponseDTO<Void> deleteAll(@PathVariable Long applicationId) {
        fileStorageService.deleteAll(applicationId);
        return ok();
    }

    @PutMapping("/{applicationId}/contract")
    public ResponseDTO<Response> contract(@PathVariable Long applicationId) {
        return ok(applicationService.contract(applicationId));
    }
}
